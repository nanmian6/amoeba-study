package com.nanmian.rabbitmqspringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitmqSpringbootConsumerApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void receiveTest(){
        Object o = rabbitTemplate.receiveAndConvert("boot_queue");
        String s = o.toString();
        System.out.println(s);
    }

}
