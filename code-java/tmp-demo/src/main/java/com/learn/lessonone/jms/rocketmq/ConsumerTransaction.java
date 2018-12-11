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
public class ConsumerTransaction {

    public static final Map<MessageQueue, Long> offseTable = new HashMap<>();

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {

        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("ConsumerTransaction");
        consumer.setNamesrvAddr("120.78.66.32:7878");
        consumer.setInstanceName("Consumer");
        consumer.start();


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