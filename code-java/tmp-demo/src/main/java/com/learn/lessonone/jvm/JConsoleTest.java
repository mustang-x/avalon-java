package com.learn.lessonone.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description:C:\Program Files\Java\jdk1.8.0_144\bin\jconsole.exe
 * <p>
 * @see /soure-java/pics/1544522773.jpg
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/5 13:19
 */
public class JConsoleTest {

    byte[] bytes = new byte[1 * 1024];

    public static void main(String[] args) {
        List<Object> objects = new ArrayList<>();
        new Thread(() -> {
            while (true) {
                objects.add(new JConsoleTest());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        // fullList(1000);

    }

    private static void fullList(int num) {
        List<JConsoleTest> jConsoleTests = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jConsoleTests.add(new JConsoleTest());
        }
    }

}