package com.nanmian.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class QosListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

          //获取到的消息
        System.out.println(new String(message.getBody()));

        Thread.sleep(1000);

        //处理业务逻辑

        //进行消息的签收 设置为true的时候则代表签收该消费者所有未签收的消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
