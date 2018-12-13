package com.learn.lessonone.jms.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/10 0:28
 */
public class    ConsumerTransaction {

    public static final Map<MessageQueue, Long> offseTable = new HashMap<>();

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {

        /**
         * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ConsumerGroupName需要由应用来保证唯一 ,最好使用服务的包名区分同一服务,一类Consumer集合的名称，
         * 这类Consumer通常消费一类消息，且消费逻辑一致
         * PullConsumer：Consumer的一种，应用通常主动调用Consumer的拉取消息方法从Broker拉消息，主动权由应用控制
         */
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("ConsumerTransaction");
        consumer.setNamesrvAddr("120.78.66.32:7878");
        consumer.setInstanceName("Consumer");
        consumer.start();

        // 拉取订阅主题的队列，默认队列大小是4
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("TransactionMsgTest1");
        for (MessageQueue mq : mqs) {
            System.out.println("Consumer from the queue : [" + mq + "]");
            SINGLE_MQ: while (true) {
                PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);

                List<MessageExt> lstMsg = pullResult.getMsgFoundList();
                if (lstMsg != null && lstMsg.size() > 0) {
                    for (MessageExt msg : lstMsg) {
                        System.out.println("msg : [" + new String(msg.getBody()) + "]");
                    }
                }
                System.out.println("next : [ " + pullResult.getNextBeginOffset() + "]");
                putMessageQueueOffset(mq, pullResult.getNextBeginOffset());

                switch (pullResult.getPullStatus()) {
                    case FOUND: break;
                    case NO_MATCHED_MSG: break;
                    case NO_NEW_MSG:
                        break SINGLE_MQ;
                    case OFFSET_ILLEGAL:break ;
                    default:break ;

                }
            }
        }

        consumer.shutdown();
    }

    private static void putMessageQueueOffset(MessageQueue mq, Long offset) {
        offseTable.put(mq, offset);
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offseTable.get(mq);
        if (offset != null) {
            System.out.println("offset : [" + offset + "]");
            return offset;
        }
        return 0;
    }

}