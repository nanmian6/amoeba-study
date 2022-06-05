package com.nanmian;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-producer.xml")
public class ProducerTest {

    //1.注入 RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testHelloWorld(){
        //2.发送消息

        rabbitTemplate.convertAndSend("spring_queue","hello world spring....");
    }


    /**
     * 发送fanout消息
     */
    @Test
    public void testFanout(){
        //2.发送消息

        rabbitTemplate.convertAndSend("spring_fanout_exchange","","spring fanout....");
    }

    /**
     *  发送direct消息
     */
    @Test
    public void testDirect(){
        //2.发送消息

        rabbitTemplate.convertAndSend("spring_direct_exchange","info","spring Direct....");
    }

    /**
     * 发送topic消息
     */
    @Test
    public void testTopics(){
        //2.发送消息

        rabbitTemplate.convertAndSend("spring_topic_exchange","nanmian.haha","spring topic....");
    }

    /**
     * 测试confirm模式
     */
    @Test
    public void testConfirm() {

        //定义回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 相关配置信息
             * @param ack   exchange交换机 是否成功收到了消息。true 成功，false代表失败
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm方法被执行了....");

                //ack 为  true表示 消息已经到达交换机
                if (ack) {
                    //接收成功
                    System.out.println("接收成功消息" + cause);
                } else {
                    //接收失败
                    System.out.println("接收失败消息" + cause);
                    //做一些处理，让消息再次发送。
                }
            }
        });

        //进行消息发送
        rabbitTemplate.convertAndSend("test_exchange_confirm","confirm","message Confirm...");

        //进行睡眠操作 否则无法监听
        try {
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试return模式
     */
    @Test
    public void testReturn() {

        //设置交换机处理失败消息的模式   为true的时候，消息达到不了 队列时，会将消息重新返回给生产者
        rabbitTemplate.setMandatory(true);

        //定义回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             *
             * @param message   消息对象
             * @param replyCode 错误码
             * @param replyText 错误信息
             * @param exchange  交换机
             * @param routingKey 路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("return 执行了....");

                System.out.println("message:"+message);
                System.out.println("replyCode:"+replyCode);
                System.out.println("replyText:"+replyText);
                System.out.println("exchange:"+exchange);
                System.out.println("routingKey:"+routingKey);

                //处理
            }
        });
        //进行消息发送   //故意写错routingKey 执行回调方法
        rabbitTemplate.convertAndSend("test_exchange_confirm","confirma","message return...");

        //进行睡眠操作
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
      * 测试消费端限流
      */
    @Test
    public void  testQos(){

        for (int i = 0; i < 10; i++) {
            // 发送消息
            rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm", "message confirm....");
        }

    }

    /**
     * 测试TTl特性
     */
    @Test
    public void testTTL(){

        rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.haha", "message ttl....");

    }

    /**
     * 发送测试死信DLX消息：
     *  1. 过期时间
     *  2. 长度限制
     *  3. 消息拒收
     */
    @Test
    public void testDlx(){
        //1. 测试过期时间，死信消息
//        rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.hehe","死信消息测试1，开始...");

//        //2. 测试长度限制后，消息死信
//        for (int i = 0; i < 20; i++) {
//            rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.hehe","死信消息测试2，开始...");
//        }

        //3. 测试消息拒收
        rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.baiqi","死信消息测试3，开始...");

    }

    /**
     * 测试延时消息
     */
    @Test
    public  void testDelay() throws InterruptedException {
        //1.发送订单消息。 将来是在订单系统中，下单成功后，发送消息
        rabbitTemplate.convertAndSend("order_exchange","order.msg","订单信息：id=1,time=2020年12月...");


        //2.打印倒计时10秒
        for (int i = 10; i > 0 ; i--) {
            System.out.println(i+"...");
            Thread.sleep(1000);
        }

    }

}
