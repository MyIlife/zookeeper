package com.example.zookeeper.demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        //建立zookeeper连接,并开启监听
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183", 3000, new Watcher() {
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
        System.out.println(s);
        //获取节点
        //定义监听器 如果是new Watcher 则表示该节点修改、删除操作时触发监听自定义监听器,即数据发生变化时的监听，如果是true表示默认使用new Zookeeper定义的监听器，false表示不使用监听器
        byte[] data = zooKeeper.getData("/test",
                new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        System.out.println("事件触发："+watchedEvent.toString());
                    }
                }

                , new Stat());
        System.out.println(new String(data));
        zooKeeper.delete("/test",0);
        TimeUnit.SECONDS.sleep(3000);
    }

}
