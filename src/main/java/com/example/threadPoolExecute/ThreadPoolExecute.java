package com.example.threadPoolExecute;

import com.example.blockingQueue_.PoolBlockingQueue;
import com.example.enum_.TimeTypeEnum;

public class ThreadPoolExecute {
    /**
     * corePoolSize
     * 核心线程数 → 工厂的「正式工人数」
     */
    private volatile int corePoolSize;

    /**
     * maximumPoolSize
     * 最大线程数 → 工厂「正式工+临时工」的总人数上限
     */
    private volatile int maximumPoolSize;

    /**
     * keepAliveTime
     * 空闲超时时间 → 临时工没事干，多久后被辞退
     */
    private volatile long keepAliveTime;

    /**
     * isShutdown
     * 标记线程池是否被关闭
     * false 运行中
     * true 被关闭
     */
    private volatile Boolean isShutdown = false;

    /**
     * unit
     * 时间单位 → 配合参数3，比如：秒、毫秒
     */
    private volatile TimeTypeEnum unit;

    /**
     * workQueue
     * 阻塞队列 → 工厂的「任务仓库」
     */
    private volatile PoolBlockingQueue workQueue;

    public ThreadPoolExecute(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeTypeEnum unit, PoolBlockingQueue workQueue) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        this.workQueue = workQueue;

        //开始抢活干 初始化时没有任务则会阻塞
        startFactoryPool();
    }

    /**
     * 向线程池传入任务
     * @param runnable 需要干的活
     */
    public void execute(Runnable runnable) {
        if (isShutdown)
            throw new RuntimeException("线程池已关闭，无法使用。");

        try {
            //向队列新增任务
            this.workQueue.put(runnable);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始招募正式员工[常驻线程]开始抢活干
     */
    private void startFactoryPool() {
        for (int i = 0; i < corePoolSize; i++)
            new FormalWorker().start();
    }

    /**
     * 销毁该线程池 让它再起不能
     */
    private void shutdown() {
        isShutdown = true;
    }

    /**
     * 内部类表示正式员工 只有类内才能用workQueue的take因此创建内部类
     */
    private class FormalWorker extends Thread {
        @Override
        public void run() {
            try {
                //指定的线程数死循环抢任务 直到线程池被销毁
                while (!isShutdown) workQueue.take().run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
