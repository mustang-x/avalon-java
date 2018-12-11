package com.learn.lessonone.jms.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/9 22:47
 */
public class Producer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("hello_producer");

        producer.setNamesrvAddr("120.78.66.32:7878");
        producer.start();
        try {
            for (int i = 0; i < 500; i++) {
                Message message = new Message("rocketMQTest",
                        "TagA",
                        ("Hello Avalon" + i + 1).getBytes());

                SendResult send = producer.send(message);
                System.out.println("Producer发送消息 -> [" + message + "]  result=[" + send + "]");
            }
        } catch (RemotingException e) {
            System.out.println(e);
        } catch (MQBrokerException e) {
            System.out.println(e);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        producer.shutdown();
    }

}