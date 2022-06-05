package com.nanmian.zookeeper.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author nanmian
 * @Description:
 * @date 2022/3/15 18:17
 */
@Slf4j
public class ZookeeperClientTest {

    private static final String ZK_ADDRESS = "8.130.49.51:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private static ZooKeeper zooKeeper;
    private static final String ZK_NODE = "/zk‐node";

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        //countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。
        /*是通过一个计数器来实现的，计数器的初始值是线程的数量。每当一个线程执行完毕后，计数器的值就-1，
          当计数器的值为0时，表示所有线程都执行完毕，然后在闭锁上等待的线程就可以恢复工作了*/
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        //创建zookeeper是一个异步的过程，zookeeper客户端会创建两个守护线程：sendThread和eventThread，当没有业务线程在运行时，守护线程会自动退出，

        zooKeeper = new ZooKeeper(ZK_ADDRESS, SESSION_TIMEOUT, new Watcher() {  //watcher用来检测是否连接
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.None && event.getState() == Event.KeeperState.SyncConnected)
                    log.info("连接已建立");
                countDownLatch.countDown();//让主线程继续执行,将count值减1
            }
        });

        log.info("连接中....");
        countDownLatch.await(); //让主线程等待,调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行


        MyConfig config = new MyConfig();
        config.setKey("anyKey");
        config.setName("anyName");

        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writeValueAsBytes(config);

        //创建一个所有权限的持久化节点
        String s = zooKeeper.create("/myConfig1", bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);


        Watcher watcher = new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType() == Event.EventType.NodeDataChanged && watchedEvent.getPath() != null && watchedEvent.getPath().equals("/myConfig"))
                    log.info("PATH:{} 发生了数据变化", watchedEvent.getPath());

                //拿取数据成功时又注册监听  实现循环监听功能
                byte[] data = zooKeeper.getData("/myConfig", this, null);
                //格式化数据
                MyConfig newConfig = mapper.readValue(new String(data), MyConfig.class);

                log.info("数据发生变化:{}", newConfig);
            }
        };

        //拿取数据并注册监听
        byte[] data = zooKeeper.getData("/myConfig", watcher, null);

        MyConfig oldConfig = mapper.readValue(new String(data), MyConfig.class);
        log.info("原始数据:{}", oldConfig);

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

}
