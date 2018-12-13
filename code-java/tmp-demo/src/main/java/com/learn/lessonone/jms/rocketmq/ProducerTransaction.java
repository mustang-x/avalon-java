package com.learn.lessonone.jms.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/10 0:27
 */
public class ProducerTransaction {

    private static AtomicInteger transactionIndex = new AtomicInteger(1);


    public static void main(String[] args) throws MQClientException {
        /**
         * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ProducerGroupName需要由应用来保证唯一,一类Producer集合的名称，这类Producer通常发送一类消息，
         * 且发送逻辑一致<br>
         * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
         * 因为服务器会回查这个Group下的任意一个Producer
         */
        final TransactionMQProducer producer = new TransactionMQProducer("ProducerTransaction");
        producer.setNamesrvAddr("120.78.66.32:7878");
        producer.setInstanceName("Producer");
        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        producer.start();

        /**
         * 服务器回调Producer，检查本地事务分支成功还是失败
         */
        producer.setTransactionCheckListener(msg -> {
            System.out.println("ProducerTransaction -> [" + new String(msg.getBody()) + "]");
            System.out.println("ProducerTransaction -> [" + msg.toString() + "]");

            int value = transactionIndex.getAndIncrement();
            if (value == 0) {
                throw new RuntimeException("Could not find db");
            } else if ((value % 3) == 0) {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            } else if ((value % 2) == 0) {
                return LocalTransactionState.COMMIT_MESSAGE;
            }
            return LocalTransactionState.UNKNOW;
        });

        /**
         * 下面这段代码表明一个Producer对象可以发送多个topic，多个tag的消息。
         * 注意：send方法是同步调用，只要不抛异常就标识成功。但是发送成功也可会有多种状态，<br>
         * 例如消息写入Master成功，但是Slave不成功，这种情况消息属于成功，但是对于个别应用如果对消息可靠性要求极高，<br>
         * 需要对这种情况做处理。另外，消息可能会存在发送失败的情况，失败重试由应用来处理。
         */
        try {
            for (int i = 0; i < 5; i++) {
                {
                    // key = OrderID001
                    Message message = new Message("TransactionMsgTest1",
                            "TagA",
                            "OrderID001",
                            ("Hello Moto A").getBytes());

                    TransactionSendResult result = producer.sendMessageInTransaction(message, new LocalTransactionExecuter() {
                        @Override
                        public LocalTransactionState executeLocalTransactionBranch(Message message, Object arg) {
                            System.out.println("executeLocalTransactionBranch-msg -> [" + new String(message.getBody()) + "]");
                            System.out.println("executeLocalTransactionBranch-msg -> [" + message.toString() + "]");
                            System.out.println("executeLocalTransactionBranch-arg -> [" + arg + "]");
                            return LocalTransactionState.UNKNOW;
                        }
                    }, "$$$");

                    System.out.println("=====================================================================");
                    System.out.println("result -> [" + result + "]");
                }
                // ==========================================================================================
                {
                    // key = OrderID001
                    Message message = new Message("TransactionMsgTest2",
                            "TagB",
                            "OrderID4",
                            ("Hello Moto B").getBytes());

                    TransactionSendResult result = producer.sendMessageInTransaction(message, new LocalTransactionExecuter() {
                        @Override
                        public LocalTransactionState executeLocalTransactionBranch(Message message, Object arg) {
                            System.out.println("executeLocalTransactionBranch-msg -> [" + new String(message.getBody()) + "]");
                            System.out.println("executeLocalTransactionBranch-msg -> [" + message.toString() + "]");
                            System.out.println("executeLocalTransactionBranch-arg -> [" + arg + "]");
                            return LocalTransactionState.COMMIT_MESSAGE;
                        }
                    }, "$$$");

                    System.out.println("=====================================================================");
                    System.out.println("result -> [" + result + "]");
                }
                // ==========================================================================================
                {
                    // key = OrderID001
                    Message message = new Message("TransactionMsgTest3",
                            "TagC",
                            "OrderID8",
                            ("Hello Moto C").getBytes());

                    TransactionSendResult result = producer.sendMessageInTransaction(message, new LocalTransactionExecuter() {
                        @Override
                        public LocalTransactionState executeLocalTransactionBranch(Message message, Object arg) {
                            System.out.println("executeLocalTransactionBranch-msg -> [" + new String(message.getBody()) + "]");
                            System.out.println("executeLocalTransactionBranch-msg -> [" + message.toString() + "]");
                            System.out.println("executeLocalTransactionBranch-arg -> [" + arg + "]");
                            return LocalTransactionState.COMMIT_MESSAGE;
                        }
                    }, "$$$");

                    System.out.println("=====================================================================");
                    System.out.println("result -> [" + result + "]");
                }

                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        /**todo 这里不理解
         * 退出时，要调用shutdown来清理资源，关闭网络链接，从MetaQ服务器上注销自己
         * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
         */
        // producer.shutdown();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                producer.shutdown();
            }
        }));
        System.exit(0);
    } // 执行本地事务，由客户端回调

}