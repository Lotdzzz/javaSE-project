package com.example;

import com.example.blockingQueue_.PoolBlockingQueue;
import com.example.enum_.TimeTypeEnum;
import com.example.threadPoolExecute.ThreadPoolExecute;

import java.util.Scanner;

/**
 * 测试程序
 */
public class ThreadPoolApplication {

    /**
     * 创建自定义线程池
     */
    public static ThreadPoolExecute threadPoolExecute = new ThreadPoolExecute(
            5,                  // 核心线程数（正式工）：n个
            10,                 // 最大线程数（总人数）：n正式+m临时
            20,                  // 临时工空闲s单位就辞退
            TimeTypeEnum.SECOND,   // 时间单位：单位
            new PoolBlockingQueue<>(10) //自定义阻塞队列
    );

    public static void main(String[] args) {
//        test1(threadPoolExecute);
//        test2(threadPoolExecute);
//        test3(new PoolBlockingQueue<>(10));
        test4();
    }

    /**
     * 多个线程池并发
     */
    public static void test4() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                ThreadPoolExecute threadPoolExecute = new ThreadPoolExecute(
                        5,
                        10,
                        20,
                        TimeTypeEnum.SECOND,
                        new PoolBlockingQueue<>(10));

                new Thread(() -> {
                    for (int i1 = 0; i1 < 20; i1++) {
                        threadPoolExecute.execute(() -> {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(Thread.currentThread().getName() + "已完工");
                        });
                    }
                }).start();
            }).start();
        }
    }

    /**
     * 阻塞队列测试
     *
     * @param poolBlockingQueue 阻塞队列
     */
    public static void test3(PoolBlockingQueue<Runnable> poolBlockingQueue) {
        //新增任务
        new Thread(() -> {
            for (int i = 0; i < 13; i++) {
                try {
                    poolBlockingQueue.put(() -> {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(Thread.currentThread().getName() + "----" + "完工...");
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        //执行任务
        new Thread(() -> {
            while (true) {
                try {
                    poolBlockingQueue.take().run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    poolBlockingQueue.take().run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    poolBlockingQueue.take().run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * 手动添加任务测试 输入1会添加一条任务在队列内 每次任务执行时间为10秒
     *
     * @param threadPoolExecute 线程池
     */
    public static void test1(ThreadPoolExecute threadPoolExecute) {
        new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                if (scanner.next().equals("1")) {
                    threadPoolExecute.execute(() -> {
                        System.out.println(Thread.currentThread().getName() + "----" + "在干活中...");
                        try {
                            Thread.sleep(1000000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 添加指定数量的任务数 每个任务工作时间为10秒
     *
     * @param threadPoolExecute 线程池
     */
    public static void test2(ThreadPoolExecute threadPoolExecute) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            int number = scanner.nextInt();
            for (int i = 0; i < number; i++) {
                threadPoolExecute.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "----" + "在干活中...");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }).start();
    }
}
