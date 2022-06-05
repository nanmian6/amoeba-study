package com.nanmian.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class AckListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        //1、获取消息的id
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {

            //2、获取消息
            System.out.println("message:"+new String(message.getBody()));

            //3、进行业务处理
            System.out.println("=====进行业务处理====");

//            模拟出现异常
            int  i = 5/0;

            //4、进行消息签收 当监听器设置为手动签收时需要开启
            // 设置为true的时候则代表签收该消费者所有未签收的消息,但如果中途发生异常，所有消息都会丢失，因此推荐设置为false
            channel.basicAck(deliveryTag, false);
            System.out.println("=====消息已签收=====");

        } catch (Exception e) {

            //拒绝签收
             /*
            第三个参数：requeue：重回队列。如果设置为true，则消息重新回到queue，broker会重新发送该消息给消费端
             */
            channel.basicNack(deliveryTag, false, false);
            System.out.println("=====消息已回退=====");

        }
    }

}
