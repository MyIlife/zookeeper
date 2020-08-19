package com.example.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static ZooKeeper zk;
    private static final String ADDRESSES = "localhost:2181,localhost:2182,localhost:2183";
    private static final  int SESSION_TIMEOUT = 3000;
    private static UtilsWatcher utilsWatcher = new UtilsWatcher();
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static ZooKeeper getZK(){
        if(zk!=null){
            return zk;
        }
        try {
            zk = new ZooKeeper(ADDRESSES,SESSION_TIMEOUT, utilsWatcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
        utilsWatcher.setCountDownLatch(countDownLatch);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }
}
