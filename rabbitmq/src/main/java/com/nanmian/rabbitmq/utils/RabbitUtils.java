package com.nanmian.rabbitmq.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.*;


public class RabbitUtils {
    private static ConnectionFactory connectionFactory = new ConnectionFactory();
    static {
        connectionFactory.setHost("47.102.210.107");
        connectionFactory.setPort(5672);//5672是RabbitMQ的默认端口号
        connectionFactory.setUsername("{admin}");
        connectionFactory.setPassword("{123456}");
        connectionFactory.setVirtualHost("nanmian");
    }
    public static Connection getConnection(){
        Connection conn = null;

        try {
            conn = connectionFactory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return conn;

    }
}
