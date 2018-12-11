package com.learn.lessonone.jvm;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/5 12:55
 */
public class JVMDemo {

    public static void main(String[] args) {

        // -XX:+PrintGCDetails  -XX:UseSerialGC -XX:+PrintGC -Xms5m -Xmx20m
        // Missing +/- setting for VM option 'UseSerialGC' Java8没有这个配置了

        System.out.println("maxMemory -> " + Runtime.getRuntime().maxMemory());
        System.out.println("freeMemory -> " + Runtime.getRuntime().freeMemory());
        System.out.println("totalMemory -> " + Runtime.getRuntime().totalMemory());
        System.out.println("==============================================");

        byte[] b1 = new byte[1 * 1024 * 1024];

        System.out.println("maxMemory -> " + Runtime.getRuntime().maxMemory());
        System.out.println("freeMemory -> " + Runtime.getRuntime().freeMemory());
        System.out.println("totalMemory -> " + Runtime.getRuntime().totalMemory());
        System.out.println("==============================================");

        byte[] b2 = new byte[4 * 1024 * 1024];
        System.out.println("maxMemory -> " + Runtime.getRuntime().maxMemory());
        System.out.println("freeMemory -> " + Runtime.getRuntime().freeMemory());
        System.out.println("totalMemory -> " + Runtime.getRuntime().totalMemory());
        System.out.println("==============================================");


        int a = 0x00000000ff980000;
        int b = 0x00000000ffc80000;
        System.out.println("Last =>  " + (b - a) / 1024);

    }

}