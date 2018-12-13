### JVM

#### Java虚拟机概述和基本概念

​	系统虚拟机(Visual Box VM)和程序虚拟机(HotSpot)

​	桥接：同一个网段、DNS，不同网卡使用的通信协议不一样的

​	hostonly：虚拟机间、虚拟机和本地能ping通，虚拟机不能访问外网

​	NAT(vmnet8)：共享主机的IP地址，网络地址转换；子网IP：同网段；网关/DHCP设置；IPV4设置同网关

​	

Hot Sport VM

堆分配参数：

- -Xmn：设置新生代大小，设置一个比较大的新生代会减少老年代的大小，这个参数对系统性能以及GC行为又很大影响，新生代大小一般会设置整个堆空间的1/3到1/4左右
  - 不同的堆分布情况，对系统执行会产生一定的影响，基本策略：尽可能将对象预留在新生代，减少老年代的GC次数
  - -XX:SurvivorRatio：Eden:s0:s1

- -XX:+HeapDumpOnOutofMemoryError -XX:HeapDumpPath=c:/error.dump
- -XX:MaxPermSize/-XX:PermSize：默认64M；方法区和堆一样，线程共享，用于保存系统的类信息，如果系统运行时生产大量的类，就需要设置一个享对何时的方法区，以免出现永久去内存溢出问题



#### 内存结构

线程共享区：

- **方法去区**：运行时常量，静态常量，加载的类信息，编译后的代码等
  - 类的版本 | 字段 | 方法 | 接口
  - 方法区和永久代
  - 
- **堆**：存放对象实例 | 垃圾收集器管理的主要区域
  - 新生代：
    - Eden | 
    - Survivor0 | 
    - Survivor1
  - 老年代：



线程独占区：

- **虚拟机栈**：
  - 虚拟机栈描述的是Java方法执行的动态内存模型
  - 栈帧：
    - 每个方法执行，都会创建一个栈帧，伴随着方法从创建到执行完成；用于存储局部变量表，操作数栈，动态链接，方法出口等
  - 局部变量表
    - 存放编译期可知的各种基本数据类型，引用类型，returenAddress类型
  - 大小：StackOverflowError| OutofMemoryError
- **本地方法栈**：
  - HotSports不区分本地方法栈和虚拟机栈；执行native方法服务；
- **程序计数器**：
  - 程序计数器是一块比较小的内存空间，可以看做当前线程所执行的字节码的行号指示器；
  - 程序计数器处于线程独占区
  - 线程执行的是Java方法，记录的是正在执行的虚拟机字节码指令的地址；执行的是native方法，值为undefined
  - 此区域是唯一一个在Java虚拟机规范中没有规定任务OOM的区域



#### 垃圾回收机制

可达性分析算法：

- 虚拟机栈：变量表
- 方法区中常量
- 方法去的类属性
- 本地方法

算法：

- 标记-清除算法：空间碎片影响效率；
- 复制算法：from / to 
- 标记-压缩：老年代；即在标记清除基础上把存活的对象压缩到内存一端
- 分代收集



#### 性能监控工具





------

### 堆内存

Java中的堆是JVM所管理的最大的一款内存空间，主要用于存放各种类的实例对象；在Java中，堆被划分成两个不同的区域：新生代(Young)、老年代(Old)；其中新生代又划分三个区域：Eden、From Survivor、ToSurvivor。这样划分的目的为了使JVM能够更好的管理内存中的对象，包括内存的分配以及回收。

新生代：Young Generation，主要用来存放新生的对象。 老年代：Old Generation或者称为Tenured Generation，主要存放应用程序声明周期长的内存对象。

> **永久代**：(方法区，不属于Java堆，另外一个别名为"非堆Non-Heap"，但是一般查看PrintGCDetails都会带上PermGen)指内存的永久保存区域， 主要存放Class和Meta的信息，Class在被Load的时候被放入PermGen space区域。它与存放Instance的Heap区域不同，GC(Garbage Collection)不会再主程序运行期对PermGen space进行清理，所以如果你的应用会加载很多Class的话，就很有可能出现PermGen space错误。

堆大小 = 新生代 + 老年代。参数：-Xms -Xmx 默认**新生代(Young)与老年代(Old)的比例的值为1:2**，可以通过参数**-XX:NewRatio**来指定，即：新生代(Young)=1/3的堆空间大小；老年代(Old)=2/3的堆空间大小。其中新生代(Young)被细分为Eden、From、To；默认Eden : From : to = 8：1：1(可以通过参数**-XX:SurvivorRatio**来指定)，即：Eden=8/10的新生区空间大小，From=To=1/10的新生代空间大小。
JVM每次只会使用Eden和其中一块Survivor区域来为对象服务，所以无论什么时候总有一款Survivor区域是空闲着的

### 回收方法区

**方法区(或者HotSpot虚拟机中的永久代(PermGen)),Java虚拟机规范中确实说过可以不要求虚拟机在方法区实现垃圾收集，而且方法区中进行垃圾收集的"性价比"一般比较低**；
在堆中，尤其新生代，常规应用进行一次垃圾收集一般可以回收70%-95%的空间，而永久代的垃圾收集率远低于此；
永久代的垃圾回收：**无用的类**(JDK1.7及其之后的版本已经将字符串常量池从永久代中移除)

- 该类所有的实例都已经被回收，也就是Java堆中不存在该类的任何实例；
- 加载该类的ClassLoader已经被回收；
- 该类对应的Java.lang.Class对象没有任务地方被引用，无法在任何地方通过反射访问该类的方法。
  虚拟机可以对满足上述3个条件的无用类进行回收，这里说的**仅仅是可以**，并不和对象一样，不使用了就必然会回收。是否对类进行回收，HotSpot虚拟机提供了参数：**-Xnoclassgc**(关闭Class的垃圾回收功能，就是虚拟机加载的类，即便是不适用，没有实例也不会回收。)进行控制。

> 在大量使用反射、动态代理、CGlib等ByteCode框架、动态生成JSP以及OSGI这类频繁自定义ClassLoader的场景都需要虚拟机具备类卸载的 功能，以保证永久代不会溢出。

### GC

Java中的堆也是GC垃圾回收的主要区域：

- Minor GC：新生代 复制算法 在经历过一次Minor GC后，如果对象还存活并且能被**另外一块Survivor区域容纳**，对象年龄+1，以后对象在Survivor区每熬过一次Minor GC 年龄+1，达到默认的15(参数：**-XX:MaxTenuringThreshold**设置)，对象就进入老年代；
  如果**比较大的对象**(需要分配一块比较大的连续内存空间)直接进入老年代(参数：**-XX:PretenureSizeThreshold**设置)；**目的是为了避免在Eden区及两个Survivor区之间发生大量的内存复制**；

**条件：**

- 虚拟机先检查**老年代最大可用的连续空间是否大于新生代所有的对象空间**，成立则确保安全 Minor GC
- 不成立；查看HandlerPromotionFailure设置值是否允许担保失败。如果允许就检查**老年代最大可用的连续空间是否大于历次晋升到老年大对象的平均大小**，大于进行 Minor GC 虽然有风险；如果小于，或者设置了不允许冒险；进行Full GC；
- Full GC(Major GC)：老年代 标记-清楚或标记-清楚—整理

**Stop The World**： **当虚拟机完成两次标记后，便确认了可以回收的对象，但是垃圾回收是与当前程序任务线程是并发执行的。这里就会出现问题：当GC线程标记好了一个对象的时候，此时我们程序的线程又将对象重新加入“关系网”中**，执行第二次标记的时候，**该对象没有重写finalize()方法，JVM只会执行一次**；垃圾回收的时候就不会回收该对象；

虚拟机的解决方法就是在一些特定指令位置设置一些“安全点”，当程序运行到这些安全点的时候就会**暂停所有当前运行的线程(Stop The World)**,暂停后再找到GC Roots进行关系的组件，进而执行标记和清楚；

特定的指令位置：

- 循环的末尾；
- 方法临返回前/调用方法的call指令后；
- 可能抛异常的位置

找到“GC Roots”也是要花很长的时间，然而这里又有新的解决方法，就是通过采用一个OopMap的数据结构来记录系统中存活的“GC Roots”，**在类加载完成的时候，虚拟机就把对象内什么偏移量上是什么类型的数据计算出来保存在OopMap，通过解释OopMap就可以找到堆中的对象，这些对象就是GC Roots。而不需要一个一个的去判断某个内存位置的值是不是引用**。这种方式也叫准确式GC。

> Full GC == Major GC 指的是对老年代/永久代的stop the world的GC； Full GC的次数 = 老年代的GC时 stop the world 的次数； Full GC的时间 = 老年代的GC时 stop the world 的总时间 CMS 不等于Full GC，我们可以看到CMS分为多个阶段，只有stop the world 的阶段被计算到Full GC的次数和时间，而和业务线程并发的GC的次数和时间则不被任务Full GC Full GC本身不会先进行Minor GC，我们可以配置，让Full GC之前进行一次Minor GC,因为老年代很多对象都会引用到新生代的对象，先进行一次Minor GC可以提高老年代GC的速度。比如：老年代使用CMS时，设置CMSScavengeBeforeRemark优化，让CMS remark之前进行一次Minor GC；

### 参数

- 堆区：
  -Xms | -Xmx 通常分别为内存的1/64 | 1/4；一般参数值配置相同，目的是为了能够在Java垃圾回收机制清理完堆区不需要重新分隔堆区的大小浪费资源；
- 非堆区：
  -XX:PermSize 初始内存分配大小； -XX:MaxPermSize: 分配内存的最大上限；

### GC Roots：

- 栈帧中的引用对象；
- 静态属性引起的对象；
- 常量引起的对象；
- 本地方法栈中JNI引用的对象；

标记：引用计数 | 可达性分析

### finalize流程：

当对象变成(GC Roots)不可达时，GC会判断该对象是否覆盖了finalize方法，若未覆盖，则直接将其回收。否则，若对象未执行过finalize方法，将其放入F-Queue队列，**由一低优先级线程执行该队列中对象的finalize方法**。执行finalize方法完毕后，GC会再次判断该对象是否可达，若不可达，则进行回收，否则，对象“复活”。

```
public class GC {

    public static GC SAVE_HOOK = null;

    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK = new GC();
        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(500); // 如果去掉这个睡眠的位置 可以观察 finalize()方法是个优先级比较低的线程 
        if (SAVE_HOOK != null) { //此时对象应该处于(reachable, finalized)状态
            System.out.println("Yes , I am still alive");
        } else {
            System.out.println("No , I am dead");
        }
        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(500);
        if (SAVE_HOOK != null) {
            System.out.println("Yes , I am still alive");
        } else {
            System.out.println("No , I am dead");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("execute method finalize()");
        SAVE_HOOK = this; // 重新复活对象
    }
}
```