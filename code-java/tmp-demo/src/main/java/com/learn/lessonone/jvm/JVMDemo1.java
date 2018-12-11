package com.learn.lessonone.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/5 18:29
 */
public class JVMDemo1 {
    static byte[] b = null;

    public static void main(String[] args) {

        /*
        * -XX:+PrintGCDetails -XX:+PrintGC -Xms5m -Xmx20m -Xmn4m -XX:SurvivorRatio=2
        *
        * -Xmn：设置新生代大小，设置一个比较大的新生代会减少老年代的大小，这个参数对系统性能以及GC行为又很大影响，
        * 生代大小一般会设置整个堆空间的1/3到1/4左右
        * 不同的堆分布情况，对系统执行会产生一定的影响，基本策略：尽可能将对象预留在新生代，减少老年代的GC次数
        * -XX:SurvivorRatio：Eden:s0:s1
        *
        * */
        List<byte[]> lst = new ArrayList();
        for (int i = 0; i < 10; i++) {
            b = new byte[1 * 1024 * 1024];
            lst.add(b);
        }
    }

}