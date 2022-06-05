package com.nanmian.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * @author nanmian
 * @Description:
 * @date 2022/4/20 17:24
 */
public class DirectListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        //打印消息
        System.out.println(new String(message.getBody()));
    }

}
