package com.example.zookeeper.distributedLock;

import com.example.zookeeper.ZKUtils;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * 分布式锁
 * 1.zookeeper 临时节点的序列化能力
 * 2.zookeeper 顺序处理请求的能力
 * 3.zookeeper 节点事件监听的能力
 * 步骤：模拟10个客户端请求获取锁，如果获得锁就执行相应的业务逻辑，如果没有得到锁，就阻塞等待。
 * 获取锁必须按照申请的顺序，前一个客户端释放锁才能继续获取锁
 */
public class LockMain {
    public static void main(String[] args) {
        ZooKeeper zk = ZKUtils.getZK();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                LockWatcherAndCallback l = new LockWatcherAndCallback(zk,Thread.currentThread());
                l.tryLock();
                // do something
                System.out.println("============="+Thread.currentThread().getName()+"：我开始工作了");
                l.unlock();
            }).start();
        }
        LockSupport.park();
    }
}
