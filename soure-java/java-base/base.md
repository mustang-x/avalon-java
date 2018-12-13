# Java-引用

在**java.lang.ref**的包下定义了java的4个级别的引用：强，软，弱，虚  

- 强引用(Final Reference)  
    特点：  
    - 强引用可以直接访问目标对象；  
    - 强引用锁指定的对象在任何时候都不会被系统回收。JVM宁抛出OOM异常也不会回收强引用对象；  
    - 强引用可能导致内存泄露；  

强引用在代码中普遍存在的，类似Object obj = new Object()；只要强引用还存在，垃圾收集器不会回收掉被引用对象

- 软引用(Soft Reference)： 定义一下还有用但并非必须的对象    
  在系统将要发生内存溢出异常之前，将会把这些对象列进回收范围之中进行二次回收；回收后内存依旧不足才会抛出内存溢出异常；如果内存充足，则垃圾回收器不会回收该对象  

```
package tomax.loo.lesson.basecore;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @program: base-core
 * @description:
 * @author: Tomax
 * @create: 2018-10-11 10:46
 **/
public class SoftRefTest {

    private static ReferenceQueue<MyObjcet> queue = new ReferenceQueue<>();

    private static class MyObjcet {

        @Override
        public String toString() {
            return "MyObject";
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            System.out.println("MyObjcet`s finalize called");
        }
    }

    public static class ChechkRefQueue implements Runnable {

        Reference<MyObjcet> obj = null;

        @Override
        public void run() {
            try {
                obj = (Reference<MyObjcet>) queue.remove();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (obj != null) {
                System.out.println("删除的引用为[" + obj + "] 但是Object for Reference is [" + obj.get() + "]");
            }
        }
    }
    /**
     * 然后使用SoftReference 构造这个MyObject对象的软引用softRef，并注册到softQueue引用队列
     * 当softRef被回收时，会被加入softQueue队列。设置obj = null,删除这个强引用，因此，系统内
     * 对MyObject对象的引用只剩下软引用。显示调用GC，通过软引用的get()方法，取得MyObject对象的
     * 引用，发现对象并未被回收，说明GC在内存充足的情况下，不会回收软引用对象。
     * 接着，申请一块大的堆空间，模拟系统堆内存使用紧张，从而产生新一轮的GC，在这次GC后，SoftRef.get()
     * 返回null，说明在系统内存紧张的情况下，软引用会被回收；软引用被回收时，会被加入注册的引用队列。
     * 软引用主要用于内存敏感的告诉缓存
     */
    public static void main(String[] args) {
        MyObjcet obj = new MyObjcet(); // 对象赋值给obj变量，构造强引用

        Reference<MyObjcet> ref =
                //new SoftReference<>(obj, queue); //软引用
                new WeakReference<>(obj, queue); // 弱引用

        System.out.println("创建引用为：" + ref);
        new Thread(new ChechkRefQueue()).start();

        obj = null; // 删除强引用
        System.out.println("Before GC: Soft Get = " + ref.get());
        System.gc();
        System.out.println("After GC: Soft Get = " + ref.get());
        System.out.println("分配大块内存");
        byte[] b = new byte[5 * 1204 * 575]; // 调整这里的堆空间的大小，会抛出OOM异常
        System.out.println("After new byte[]: Soft Get = " + ref.get());
        System.gc();

    }
}

```
运行参数： - Xmx5M (若看GC日志添加参数 -XX:+PrintGCDetails)
​    After GC: Soft Get = MyObject
​    分配大块内存
​    MyObjcet`s finalize called
​    After new byte[]: Soft Get = null
​    Object for SoftReference is null


- 弱引用(Weak Reference): 用来描述非必须对象，强度比软引用更弱一些；   
  被弱引用关联的对象只能生存到下一次垃圾收集发送之前。当垃圾收集器工作时，无论当前内存是否足够，都会回收掉只被弱引用关联的对象；  


> **软引用、弱引用**都非常适合来保存那些可有可无的缓存数据。如果这么做，当系统内存不足时，这些缓存数据会被回收；不会导致内存溢出  
> 当内存资源充足时，这些缓存数据又可以用存在相当长的时间，从而起加速系统作用。  

**JVM对某一个对象至多只执行一次被重写的finalize方法**

- 虚引用(Phantom Reference)：虚引用也称幽灵引用或幻影引用，它是最弱的一种引用关系。
  一个持有虚引用的对象和没有引用几乎是一样的，随时都有可能被垃圾回收器回收。它的作用在于跟踪垃圾回收过程
  虚引用中get方法的返回的永远是null；