package com.learn.lessonone.deadlock;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/5 22:49
 */
public class DeadLockTest {

    private static Object lock1 = new Object();
    private static Object lock2 = new Object();
    public static void main(String[] args) {
        new Thread(new MyThread1(lock1, lock2)).start();
        new Thread(new MyThread2(lock1, lock2)).start();
    }
}

class MyThread1 implements Runnable {
    private Object lock1;
    private Object lock2;
    public MyThread1(Object lock1, Object lock2) {
        this.lock1 = lock1;
        this.lock2 = lock2;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock1) {
                System.out.println("using lock1");
                synchronized (lock2) {
                    System.out.println("using lock2");
                }
            }
        }
    }
}

class MyThread2 implements Runnable {
    private Object lock1;
    private Object lock2;
    public MyThread2(Object lock1, Object lock2) {
        this.lock1 = lock1;
        this.lock2 = lock2;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock2) {
                System.out.println("using lock2");
                synchronized (lock1) {
                    System.out.println("using lock1");
                }
            }
        }
    }
}