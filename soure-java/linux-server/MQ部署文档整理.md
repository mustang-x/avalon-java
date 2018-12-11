# RocketMQ

## Linux部署

### NameServer

- 创建文件并安装	

  ```htm
  # mkdir -p usr/local/rocket/logs && mkdir -p usr/local/rocket/data
  # wget 'http://mirrors.hust.edu.cn/apache/rocketmq/4.2.0/rocketmq-all-4.2.0-bin-release.zip'
  ```

- 编辑**runserver.sh**和**runbroker.sh**启动脚本：

  ```:arrow_lower_right:
  [root@izwz9ddfevb04g2s824qqlz bin]# ls  *.sh
  cachedog.sh    cleancache.v1.sh  play.sh       runserver.sh  startfsrv.sh
  cleancache.sh  os.sh             runbroker.sh  setcache.sh   tools.sh
  ```

  修改里面的 **JVM Configuration**：可根据相应的情况修改

  ```:arrow_lower_right:
  JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn84m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=64m"
  ```

  修改 **nameserver**监听端口[默认：9876]：在 `conf`文件下添加文件：**namesrv.properties** | **namesrv.txt**

  ```:arrow_lower_right:
  # vim conf/namesrv.properties
  ==================================================
  # NameServer修改默认监听端口[9876]:为7878
  listenPort=7878
  ```

- 启动 **nameserver**： -c 指定配置文件

  ```:arrow_right:
  第二个指定日志文件
  =========================================================
  # nohup  ./mqnamesrv -c ../conf/namesrv.properties & 
  # nohup ./mqnamesrv -c ../conf/namesrv.properties >/usr/local/rocketmq/logs/mqnamesrv.log 2>&1 &
  ```

  - 日志记录文件修改：详情看logback_*.xml

    ```html
    # mkdir /usr/local/rocketmq/logs
    # cd /usr/local/rocketmq/conf && sed -i 's#${user.home}#/usr/local/rocketmq#g' *.xml
    ```

- 检查：

  ```:arrow_right:
  [root@izwz9ddfevb04g2s824qqlz logs]# jps
  13089 NamesrvStartup
  32146 QuorumPeerMain
  13211 Jps
  ==================================
  vim ../logs/mqnamesrv.log
  load config properties file OK, ../conf/namesrv.properties
  The Name Server boot success. serializeType=JSON
  
  ```



### Broker

- 查看配置文件

  ```html
  [root@izwz9ddfevb04g2s824qqlz conf]# ll
  total 48
  drwxr-xr-x 2 root root  4096 Dec  9 15:44 2m-2s-async
  drwxr-xr-x 2 root root  4096 Sep 19  2017 2m-2s-sync
  drwxr-xr-x 2 root root  4096 Sep 19  2017 2m-noslave
  -rw-r--r-- 1 root root   949 Sep 19  2017 broker.conf
  -rw-r--r-- 1 root root 14978 Dec 13  2017 logback_broker.xml
  -rw-r--r-- 1 root root  3690 Dec 13  2017 logback_filtersrv.xml
  -rw-r--r-- 1 root root  3692 Dec 13  2017 logback_namesrv.xml
  -rw-r--r-- 1 root root  3761 Dec 13  2017 logback_tools.xml
  -rw-r--r-- 1 root root    67 Dec  9 16:23 namesrv.properties
  ```

- 完整配置文件属性

  默认端口：问号的不清楚期待完善

  - 9876 （nameserver 端口） **修改**
  - 10909（主要是fastRemotingServer服务使用）  **？？**
  - 10911（Broker 对外服务的监听端口）    **修改**
  - 10912 (Master 和Slave同步的数据的端口)    **？？**

  修改： **namesrvAddr=120.78.66.32:7878** & **listenPort=7879**

  修改：/etc/hosts  这次没用到这里

  ```html
  192.168.12.132 rocketmq-nameserver1
  192.168.12.132 rocketmq-master1
  
  192.168.12.133 rocketmq-nameserver2
  192.168.12.133 rocketmq-master2
  ```

  ===================================================================



  ```:arrow_upper_right:
  #所属集群名字
  brokerClusterName=rocketmq-cluster
  #broker名字，注意此处不同的配置文件填写的不一样，如果是broker-a.properties 这里就写broker-a,broker-b.properties 这里就写broker-b,以此类推
  brokerName=broker-a
  #0 表示 Master， >0 表示 Slave
  brokerId=0
  #nameServer地址，分号分割
  #namesrvAddr=rocketmq-nameserver1:9876;rocketmq-nameserver2:9876
  namesrvAddr=120.78.66.32:7878
  #添加ip 否则报错：connect to <172.17.0.1:10909> failed
  brokerIP1 =120.78.66.32
  #在发送消息时，自动创建服务器不存在的topic，默认创建的队列数
  defaultTopicQueueNums=4
  #是否允许 Broker 自动创建Topic，建议线下开启，线上关闭
  autoCreateTopicEnable=true
  #是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
  autoCreateSubscriptionGroup=true
  #Broker 对外服务的监听端口
  listenPort=7879
  #删除文件时间点，默认凌晨 0点
  deleteWhen=00
  #文件保留时间，默认 48 小时
  fileReservedTime=120
  #commitLog每个文件的大小默认1G
  mapedFileSizeCommitLog=1073741824
  #ConsumeQueue每个文件默认存30W条，根据业务情况调整
  mapedFileSizeConsumeQueue=300000
  #destroyMapedFileIntervalForcibly=120000
  #redeleteHangedFileInterval=120000
  #检测物理文件磁盘空间
  diskMaxUsedSpaceRatio=88
  #存储路径
  storePathRootDir=/usr/local/rocketmq/data
  #commitLog 存储路径
  storePathCommitLog=/usr/local/rocketmq/data/commitlog
  #消费队列存储路径存储路径
  storePathConsumeQueue=/usr/local/rocketmq/data/consumequeue
  #消息索引存储路径
  storePathIndex=/usr/local/rocketmq/data/index
  #checkpoint 文件存储路径
  storeCheckpoint=/usr/local/rocketmq/data/checkpoint
  #abort 文件存储路径
  abortFile=/usr/local/rocketmq/data/abort
  #限制的消息大小
  maxMessageSize=65536
  #flushCommitLogLeastPages=4
  #flushConsumeQueueLeastPages=2
  #flushCommitLogThoroughInterval=10000
  #flushConsumeQueueThoroughInterval=60000
  #Broker 的角色
  #- ASYNC_MASTER 异步复制Master
  #- SYNC_MASTER 同步双写Master
  #- SLAVE
  brokerRole=ASYNC_MASTER
  #刷盘方式
  #- ASYNC_FLUSH 异步刷盘
  #- SYNC_FLUSH 同步刷盘
  flushDiskType=ASYNC_FLUSH
  #checkTransactionMessageEnable=false
  #发消息线程池数量
  #sendMessageThreadPoolNums=128
  #拉消息线程池数量
  #pullMessageThreadPoolNums=128
  ```

- 启动borker

  ```html
  # nohup sh /usr/local/rocketmq/bin/mqbroker autoCreateTopicEnable=true -c /usr/local/rocketmq/conf/2m-noslave/broker-a.properties >/dev/null 2>&1 &
  ```

- 检查

  ```html
  [root@izwz9ddfevb04g2s824qqlz conf]# jps
  13089 NamesrvStartup
  32146 QuorumPeerMain
  15304 BrokerStartup
  15338 Jps
  =========================================================
  # vim /usr/local/rocketmq/logs/rocketmqlogs/broker.log
  The broker[broker-a, 172.17.0.1:7879] boot success. serializeType=JSON and name server is 120.78.66.32:7878
  
  ```

- 测试发送接收：

  ```html
  # export NAMESRV_ADDR=120.78.66.32:7878
  
  # sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
  
  # sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
  ```

- 关闭： mqshutdown namesrv | broker



### RocketMQ-console

- 下载： git clone https://github.com/apache/rocketmq-externals.git

- 修改里面的properties文件:

  ```html
  # vim rocketmq-console/src/main/resources/application.properties
  ================================================================
  server.contextPath=
  server.port=8080
  #spring.application.index=true
  spring.application.name=rocketmq-console
  spring.http.encoding.charset=UTF-8
  spring.http.encoding.enabled=true
  spring.http.encoding.force=true
  logging.config=classpath:logback.xml
  #if this value is empty,use env value rocketmq.config.namesrvAddr  NAMESRV_ADDR | now, you can set it in ops page.default localhost:9876
  rocketmq.config.namesrvAddr=120.78.66.32:7878
  #if you use rocketmq version < 3.5.8, rocketmq.config.isVIPChannel should be false.default true
  rocketmq.config.isVIPChannel=
  #rocketmq-console's data path:dashboard/monitor
  rocketmq.config.dataPath=/tmp/rocketmq-console/data
  #set it false if you don't want use dashboard.default true
  rocketmq.config.enableDashBoardCollect=true
  ```

- 项目目录：mvn clean package -Dmaven.test.skip=true

  ```html
  [INFO] Building jar: /usr/local/rocketmq/app/rocketmq-externals/rocketmq-console/target/rocketmq-console-ng-1.0.0-sources.jar
  [INFO] ------------------------------------------------------------------------
  [INFO] BUILD SUCCESS
  [INFO] ------------------------------------------------------------------------
  [INFO] Total time:  11:18 min
  [INFO] Finished at: 2018-12-09T18:58:36+08:00
  [INFO] ------------------------------------------------------------------------
  ```

- 启动项目：

  ```html
  # nohup java -jar rocketmq-console-ng-1.0.0.jar >/usr/local/rocketmq/logs/rocketmq-app.log 2>&1 &
  ```

- 访问：http://120.78.66.32:8080/#/