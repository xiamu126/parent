package com.sybd.znld.environment;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZookeeperTest {
    public static void main(String[] args) throws Exception {
        //由于连接zk需要时间，所以这里使用countDownLatch
        final var countDownLatch = new CountDownLatch(1);
        var client = new ZooKeeper("192.168.11.101:2181", 3000, event -> {
            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())){
                System.out.println("连接成功" + event);
                countDownLatch.countDown();
            }
        });
        if (ZooKeeper.States.CONNECTING.equals(client.getState())){
            System.out.println("连接中");
            countDownLatch.await();
        }

        var stat = new Stat();
        client.getData("/", event -> {
            if(Watcher.Event.EventType.NodeDataChanged.equals(event.getType())){
                System.out.println("数据改变了");
            }
        }, stat);

        System.in.read();
    }
}
