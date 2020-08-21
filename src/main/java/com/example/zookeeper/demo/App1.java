package com.example.zookeeper.demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App1 {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        //建立zookeeper连接,并开启监听
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 3000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                String path = watchedEvent.getPath();
                Event.KeeperState state = watchedEvent.getState();
                Event.EventType type = watchedEvent.getType();
                System.out.println("路径：" + path);
                System.out.println("类型" + type.getIntValue());
                System.out.println("状态" + state.getIntValue());
            }
        });
        //创建节点
        String s = zooKeeper.create("/test",
                "dddd".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        // 异步回调
        // 和事件监听一样，也只回调一次结束
        zooKeeper.getData("/test",
                new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        System.out.println("事件触发：" + watchedEvent.toString());
                    }
                },
                new AsyncCallback.DataCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                        if(data!=null) {
                            System.out.println(new String(data));
                        }
                    }
                }
                , new Stat());
        TimeUnit.SECONDS.sleep(3000);
    }

}
