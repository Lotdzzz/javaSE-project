package com.example.threadPoolExecute;

import com.example.blockingQueue_.PoolBlockingQueue;
import com.example.enum_.TimeTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ThreadPoolExecute {
    /**
     * corePoolSize
     * 核心线程数 → 工厂的「正式工人数」
     */
    private final int corePoolSize;

    /**
     * maximumPoolSize
     * 最大线程数 → 工厂「正式工+临时工」的总人数上限
     */
    private final int maximumPoolSize;

    /**
     * keepAliveTime
     * 空闲超时时间 → 临时工没事干，多久后被辞退
     */
    private final long keepAliveTime;

    /**
     * isShutdown
     * 标记线程池是否被关闭
     * false 运行中
     * true 被关闭
     * volatile 标记可见性
     */
    private volatile Boolean isShutdown;

    /**
     * unit
     * 时间单位 → 配合参数3，比如：秒、毫秒
     */
    private final TimeTypeEnum unit;

    /**
     * workQueue
     * 阻塞队列 → 工厂的「任务仓库」
     */
    private final PoolBlockingQueue<Runnable> workQueue;

    /**
     * 记录正式员工
     */
    private final List<Thread> formalThreads;

    /**
     * 记录临时工
     */
    private final List<Thread> temporaryThreads;

    /**
     * 线程池全局锁
     * 保证execute执行三步走的原子性
     */
    private final Object LOCK = new Object();

    public ThreadPoolExecute(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeTypeEnum unit, PoolBlockingQueue<Runnable> workQueue) {
        log.info("{} ThreadPool Init...", Thread.currentThread().getName());
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        this.workQueue = workQueue;
        this.formalThreads = new ArrayList<>();
        this.temporaryThreads = new ArrayList<>();
        this.isShutdown = false;

        //线程池创建 常驻线程开始阻塞等待唤醒
        startFactoryPoolByFormal();
    }

    /**
     * 向线程池传入任务
     * execute执行 判断关闭池-招募临时工-新增任务 三个步骤
     * 加锁保证原子性
     *
     * @param runnable 需要干的活
     */
    public void execute(Runnable runnable) {
        if (isShutdown)
            throw new RuntimeException("线程池已关闭，无法使用。");

        if (runnable == null)
            throw new NullPointerException("任务为空，无法载入。");

        synchronized (LOCK) {
            if (workQueue.queueIsFull() && (formalThreads.size() + temporaryThreads.size()) < maximumPoolSize) {
                //一次创建一个临时工
                startFactoryPoolByTemporary(runnable);
                return;
            }

            //当临时工已不能再添加时 此时满仓 那这个任务就会被抛弃
            if (workQueue.queueIsFull()
                    && (formalThreads.size() + temporaryThreads.size()) == maximumPoolSize) {
                throw new RuntimeException("任务被抛弃");
            }

            try {
                //向队列新增任务
                this.workQueue.put(runnable);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    /**
     * 开始招募正式员工[常驻线程]开始抢活干
     */
    private void startFactoryPoolByFormal() {
        for (int i = 0; i < corePoolSize; i++) {
            FormalWorker formalWorker = new FormalWorker(null);
            formalThreads.add(formalWorker);
            formalWorker.start();
        }
    }

    /**
     * 当满仓且正式员工都在忙时招募临时工
     * 当满仓时一次只创建一个 不够再要
     */
    private void startFactoryPoolByTemporary(Runnable runnable) {
        TemporaryWorker temporaryWorker = new TemporaryWorker(runnable);
        temporaryThreads.add(temporaryWorker);
        temporaryWorker.start();
    }

    /**
     * 销毁该线程池 让它再起不能
     */
    public void shutdown() {
        isShutdown = true;
        //销毁常驻线程
        formalThreads.forEach(Thread::interrupt);
        //销毁临时线程
        temporaryThreads.forEach(Thread::interrupt);
        log.info("{} ThreadPool Shutdown...", Thread.currentThread().getName());
    }

    /**
     * 内部类表示正式员工 只有类内才能用workQueue的take因此创建内部类
     */
    private class FormalWorker extends Thread {

        private Runnable task;

        public FormalWorker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                //指定的线程数死循环抢任务 直到线程池被销毁
                while (!isShutdown) {
                    if (task != null) {
                        task.run();
                        task = null;
                        continue;
                    }
                    workQueue.take().run();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 内部类表示临时工
     * 临时工机制：当正式员工都在忙 仓库满仓时出生 但正式员工都在忙 仓库没满不创建
     * poll方法超时阻塞 当临时工长期接不到活就会被解雇为null
     */
    private class TemporaryWorker extends Thread {

        private Runnable task;

        public TemporaryWorker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                while (!isShutdown) {
                    if (task != null) {
                        task.run();
                        task = null;
                        continue;
                    }
                    Runnable poll = workQueue.poll(keepAliveTime, unit);
                    if (poll != null) {
                        poll.run();
                    } else {
                        temporaryThreads.remove(this);
                        return;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
