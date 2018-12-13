### Zookeeper的存储结构

- PERSISTENT：持久化节点，创建这个节点的客户端在与Zookeeper服务的连接断开后，这个节点也不会被删除(除非用API强制删除)；
- PERSISITENT_SEQUENTIAL：持久化顺序编号节点；当客户端请求创建这个节点A后，Zookeeper会根据parent-zonde的zxid状态，为这个A节点编写一个全目录唯一的编号(这个的编号只会一直增长)；当客户端与Zookeeper服务的连接断开后，这个节点也不会被删除；
- EPHEMERAL：临时znode节点；创建这个节点的客户端在与Zookeeper服务的连接断开后，这个节点就会被删除；
- EPHEMERAL_SEQUENTIAL：临时顺序编号znode节点；当客户端请求创建这个节点A后，Zookeeper会根据parent-znode的zxid状态，为这个A节点编写一个全目录唯一的编号(这个编号只会一直增长)，当创建这个及诶单的客户端与Zookeeper服务的连接断开后，这个节点被删除；

#### znode

- 每个znode默认能够存储1M的数据；
- 可使用zkCli命令，登录到Zookeeper上，并通过ls、create、sync等命令操作这些znode节点；
- znode出了名称、数据意外，还有一条属性：zxid；这套zid与时间戳对应，记录zid不同的状态；

**znode结构**

- zxid：时间戳，每次修改znode都会生成一个新zxid；如果zxid1小于zxid2，那么zxid1在zxid2之前发生；
- version：对节点的每次修改将使得节点的版本号增加1；
- data：每一个znode默认能够存储1M的数据；对于data的修改都会引起以上两者的变化；
- tick：租约协议的具体体现；如果当前节点时"临时节点"；在tick时间周期内没有收到新的客户租约，则视为无效；