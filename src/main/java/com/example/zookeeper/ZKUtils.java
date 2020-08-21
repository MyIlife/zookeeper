package com.example.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static ZooKeeper zk;
    private static final String ADDRESSES = "localhost:2181";
    private static final  int SESSION_TIMEOUT = 3000;
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static ZooKeeper getZK(){
        if(zk!=null){
            return zk;
        }
        try {
            zk = new ZooKeeper(ADDRESSES, SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    switch (event.getState()) {
                        case Unknown:
                            break;
                        case Disconnected:
                            break;
                        case NoSyncConnected:
                            break;
                        case SyncConnected:
                            // 连接成功之后方可继续使用
                            countDownLatch.countDown();
                            break;
                        case AuthFailed:
                            break;
                        case ConnectedReadOnly:
                            break;
                        case SaslAuthenticated:
                            break;
                        case Expired:
                            break;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 等待连接成功
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("==================== zookeeper 连接成功！");
        return zk;
    }
}
