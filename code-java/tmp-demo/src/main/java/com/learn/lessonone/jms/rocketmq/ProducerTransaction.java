package com.learn.lessonone.jms.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/10 0:27
 */
public class ProducerTransaction {

    public static void main(String[] args) throws MQClientException {

        final TransactionMQProducer producer = new TransactionMQProducer("ProducerTransaction");
        producer.setNamesrvAddr("120.78.66.32:7878");
        producer.setInstanceName("Producer");

        producer.start();

        /**
         * 服务器回调Producer，检查本地事务分支成功还是失败
         */
        producer.setTransactionCheckListener(new TransactionCheckListener() {
            @Override
            public LocalTransactionState checkLocalTransactionState(MessageExt messageExt) {
                System.out.println("ProducerTransaction -> [" + new String(messageExt.getBody()) + "]");
                System.out.println("ProducerTransaction -> [" + messageExt.toString() + "]");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

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