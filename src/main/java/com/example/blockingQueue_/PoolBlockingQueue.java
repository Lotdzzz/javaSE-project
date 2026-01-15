package com.example.blockingQueue_;

import com.example.enum_.TimeTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("unchecked ")
@Slf4j
public class PoolBlockingQueue<T> {

    /**
     * 阻塞队列：任务仓库，最多放capacity个任务（有界）
     */
    private final Object[] tailQueue;

    /**
     * 指定最大任务数
     */
    private final int capacity;

    /**
     * head 头指针
     * 指定队列的排头 以后取任务从这里取
     */
    private int head;

    /**
     * tail 尾指针
     * put放任务时 在这个坐标放
     */
    private int tail;

    /**
     * size 当前大小
     * 队列进出任务时记录当前的任务数
     */
    private int size;

    /**
     * 阻塞队列的锁 防止任务入队列被挤爆发生数据错乱
     * takeLock --- take队列任务未满时 阻塞的条件
     * putLock --- put队列满时 阻塞的条件
     * ps：不能加static 不然多个队列使用一把锁！
     */
    private final ReentrantLock lock;

    private final Condition putLock;

    private final Condition takeLock;

    /**
     * 初始化队列
     *
     * @param capacity 创建阻塞队列指定队列容量
     */
    public PoolBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.tailQueue = new Object[capacity];
        this.lock = new ReentrantLock();
        this.putLock = lock.newCondition();
        this.takeLock = lock.newCondition();
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    /**
     * @param t 新增任务 需要放入队列
     */
    public void put(T t) throws InterruptedException {
        lock.lock(); //获取当前的锁
        try {
            while (queueIsFull()) { //循环判断队列是否满任务 防止假唤醒
                log.info("{}: queue is full", Thread.currentThread().getName()); //只要临时工和拒绝策略存在 这个应该执行不到
                putLock.await(); //恒饥饿状态
            }
            tailQueue[tail] = t; //开始向队列中加入任务
            tail = (tail + 1) % capacity; //尾指针后移 如果到尾部则循环回头部
            size++; //队列任务数增加
            takeLock.signal(); //唤醒持队列接取任务锁的所有线程 让他们抢着接活
        } finally {
            lock.unlock(); //程序执行完释放锁
        }
    }

    /**
     * 接取任务
     *
     * @return 有空闲线程[正式员工]可以接这个活
     */
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queueIsEmpty()) { //循环判断当前是否没有任务 没有则等待任务
                log.info("{} queue is empty", Thread.currentThread().getName());
                takeLock.await();
            }
            T t = (T) tailQueue[head]; //如果有任务则从队列头中拿取任务
            head = (head + 1) % capacity; //进行头指针迭代
            size--; //总任务数减少
            putLock.signal(); //唤醒所有持有新增任务锁的线程开始增任务
            return t; //返回这个活给线程[员工]哥干活
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
    public T poll(long keepAliveTime, TimeTypeEnum unit) throws InterruptedException {
        lock.lock();
        //获取超市阻塞的时长（纳秒计算）超精确
        long nanos = TimeTypeEnum.toNanos(unit, keepAliveTime);
        try {
            while (queueIsEmpty()) {
                //判断当前剩余时间纳秒是否到期
                if (nanos <= 0) {
                    log.info("{} Temp thread is destroy", Thread.currentThread().getName());
                    return null;
                }
                //使用awaitNanos返回阻塞剩余时间
                log.info("{} Temp thread is empty", Thread.currentThread().getName());
                nanos = takeLock.awaitNanos(nanos);
            }
            T t = (T) tailQueue[head];
            head = (head + 1) % capacity;
            size--;
            putLock.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断当前仓库是否满仓
     */
    public boolean queueIsFull() {
        return size == capacity;
    }

    /**
     * 判断当前仓库是否空仓
     */
    public boolean queueIsEmpty() {
        return size == 0;
    }
}
