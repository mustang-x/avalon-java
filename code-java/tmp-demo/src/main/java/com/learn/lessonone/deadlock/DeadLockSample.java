package com.learn.lessonone.deadlock;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/7 13:06
 */
public class DeadLockSample extends Thread {

    private String first;
    private String second;

    public DeadLockSample(String name, String first, String second) {
        super(name);
        this.first = first;
        this.second = second;
    }

    @Override
    public void run() {
        synchronized (first) {
            System.out.println(this.getName() + " obtained: " + first);
            try {
                TimeUnit.SECONDS.sleep(1);

                synchronized (second) {
                    System.out.println(this.getName() + " obtained: " + second);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";

        DeadLockSample thread1 = new DeadLockSample("Thread1", lockA, lockB);
        DeadLockSample thread2 = new DeadLockSample("Thread2", lockA, lockB);
        thread1.start();
        thread2.start();


    }
}