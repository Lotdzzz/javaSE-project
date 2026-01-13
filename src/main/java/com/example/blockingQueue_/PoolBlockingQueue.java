package com.example.blockingQueue_;

import com.example.enum_.TimeTypeEnum;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PoolBlockingQueue {

    /**
     * 阻塞队列：任务仓库，最多放capacity个任务（有界）
     */
    private final Runnable[] tailQueue;

    /**
     * 指定最大任务数
     */
    private volatile int capacity = 0;

    /**
     * head 头指针
     * 指定队列的排头 以后取任务从这里取
     */
    private int head = 0;

    /**
     * tail 尾指针
     * put放任务时 在这个坐标放
     */
    private int tail = 0;

    /**
     * size 当前大小
     * 队列进出任务时记录当前的任务数
     */
    private int size = 0;

    /**
     * 阻塞队列的锁 防止任务入队列被挤爆发生数据错乱
     * isEmpty --- take队列任务未满时 阻塞的条件
     * isFull --- put队列满时 阻塞的条件
     * isTempEmpty --- 临时工的锁 当仓库没东西时 临时工用这个锁阻塞
     */
    private static final ReentrantLock lock = new ReentrantLock();

    private static final Condition isEmpty = lock.newCondition();

    private static final Condition isFull = lock.newCondition();

    private static final Condition isTempEmpty = lock.newCondition();

    /**
     * 初始化队列
     *
     * @param capacity 创建阻塞队列指定队列容量
     */
    public PoolBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.tailQueue = new Runnable[capacity];
    }

    /**
     * @param runnable 新增任务 需要放入队列
     */
    public void put(Runnable runnable) throws InterruptedException {
        lock.lock(); //获取当前的锁
        try {
            while (size == capacity) { //循环判断队列是否满任务 防止假唤醒
                System.out.println("Queue is full waiting...");
                isFull.await();
            }
            tailQueue[tail] = runnable; //开始向队列中加入任务
            System.out.println("Put " + runnable);
            tail = (tail + 1) % capacity; //尾指针后移 如果到尾部则循环回头部
            size++; //队列任务数增加
            isEmpty.signal(); //唤醒持队列接取任务锁的所有线程 让他们抢着接活
            isTempEmpty.signal(); //如果有临时工的话同时唤醒临时工
        } finally {
            lock.unlock(); //程序执行完释放锁
        }
    }

    /**
     * 接取任务
     *
     * @return 有空闲线程[正式员工]可以接这个活
     */
    public Runnable take() throws InterruptedException {
        lock.lock();
        try {
            while (size == 0) { //循环判断当前是否没有任务 没有则等待任务
                System.out.println("Queue is empty waiting...");
                isEmpty.await();
            }
            Runnable runnable = tailQueue[head]; //如果有任务则从队列头中拿取任务
            head = (head + 1) % capacity; //进行头指针迭代
            size--; //总任务数减少
            isFull.signal(); //唤醒所有持有新增任务锁的线程开始增任务
            return runnable; //返回这个活给线程[员工]哥干活
        } finally {
            lock.unlock();
        }
    }

    /**
     * 临时工接取任务 如果空闲等待的阻塞超时了 临时工直接返回null 解雇本临时工线程
     *
     * @param keepAliveTime 单位时间数量
     * @param unit          时间单位
     * @return 任务
     */
    public Runnable poll(long keepAliveTime, TimeTypeEnum unit) throws InterruptedException {
        lock.lock();
        //获取超市阻塞的时长（纳秒计算）超精确
        long nanos = TimeTypeEnum.toNanos(unit, keepAliveTime);
        try {
            while (size == 0) {
                //判断当前剩余时间纳秒是否到期
                if (nanos <= 0) {
                    System.out.println(Thread.currentThread().getName() + "Temp Worker destroy...");
                    return null;
                }
                System.out.println("Temp : Queue is empty waiting...");
                //使用awaitNanos返回阻塞剩余时间
                nanos = isTempEmpty.awaitNanos(nanos);
            }
            Runnable runnable = tailQueue[head];
            head = (head + 1) % capacity;
            size--;
            isFull.signal();
            return runnable;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断当前仓库是否满仓
     */
    public synchronized boolean queueIsFull() {
        return size == capacity;
    }
}
