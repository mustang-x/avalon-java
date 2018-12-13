0、说明

各位开发同事遇到现实服务器实例报OOM错误时 尽量按以下步骤保留现场信息 方便后续定位

1、获取进程号

jps -v|grep 关键字

2、创建事故现网文件夹

mkdir {pid}

3、查看磁盘空间是否打满

df -h >{pid}/df_h.txt 

4、查看内存空间是否打满

free -m >{pid}/free_m.txt 

5、查看io

iostat -d -k 1 10 >{pid}/io.txt 

6、查看cpu等其它信息（兜底包含其它信息）

top -M -n 2 -d 3 >{pid}/top.txt 查看top

7、查看是否一直fullgc

jstat -gcutil {pid} 1000 1000 > {pid}/jstat_gcutil.txt 

8、得到存活内存分布图

jmap -histo:live {pid} >{pid}/jmap_histo.txt 

9、dump当前堆栈到对应文件

jmap -dump:live,file={pid}/jmap_dum {pid} 

10、堆栈快照

jstack {pid} >{pid}/jstack_1.txt 一次堆栈快照 备用

jstack {pid} >{pid}/jstack_2.txt 两次堆栈快照 备用

11、重启

12、2-10 完整脚本

复制至文本编辑工具替换{pid}占位符为对应java程序pid即可一次执行（通用脚本已分发, ls ~/.recodOOM.sh 可以确认脚本）

`mkdir {pid};`

`df -h >{pid}/df_h.txt`

`free -m >{pid}/free_m.txt`

`iostat -d -k 1 10 >{pid}/io.txt`

`jstat -gcutil {pid} > {pid}/jstat_gcutil.txt`

`jmap -histo:live {pid} >{pid}/jmap_histo.txt`

`jmap -dump:live,file={pid}/jmap_dum {pid} ;`

`jstack {pid} >{pid}/jstack_1.txt;`

`jstack {pid} >{pid}/jstack_2.txt;`

`top -M -n 1 >{pid}/top.txt;`