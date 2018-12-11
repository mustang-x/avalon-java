package com.learn.lessonone.juc.thread;

import java.util.concurrent.*;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/7 14:47
 */
public class FutrueTest {

    //(1)线程池单个线程，线程池队列元素个数为1
    private final static ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            1,
            1,
            1L,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.
                    // DiscardPolicy() // 会第三个任务阻塞住，因为包装的FutureTask 的status=NEW
                    // DiscardOldestPolicy() // 会第二个任务阻塞住，因为包装的FutureTask 的status=NEW  把第二个任务poll出去了
                    AbortPolicy() // 线程里面抛异常
            );

    public static void main(String[] args) throws Exception {

        //(2)添加任务one
        Future futureOne = executorService.submit(() -> {

            System.out.println("start Runnable one");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        //(3)添加任务two
        Future futureTwo = executorService.submit((Callable<Object>) () -> {
            System.out.println("start Callable two");
            return "success";
        });

        //(4)添加任务three
        Future futureThree = null;
        try {
            futureThree = executorService.submit(new Runnable() {

                @Override
                public void run() {
                    System.out.println("start Runnable three");
                }
            });
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }


        System.out.println("task one " + futureOne.get());//(5)等待任务one执行完毕
        System.out.println("task two " + futureTwo.get());//(6)等待任务two执行完毕
        System.out.println("task three " + (futureThree == null ? null : futureThree.get()));// (7)等待任务three执行完毕
        try {
            System.out.println("task three " + (futureThree == null ? null : futureThree.get()));// (7)等待任务three执行完毕
        } catch (InterruptedException e) {
            System.out.println("InterruptedException -> " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            System.out.println(e.getLocalizedMessage());
        }


        executorService.shutdown();//(8)关闭线程池，阻塞直到所有任务执行完毕
    }

}