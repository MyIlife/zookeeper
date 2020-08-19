package com.example.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 事件监听、异步结果回调
 */
public class ConfWatcher implements Watcher, AsyncCallback.DataCallback,AsyncCallback.StatCallback {
    ZooKeeper zk;
    Config config;
    CountDownLatch countDownLatch = new CountDownLatch(1);
    public void aWait() throws InterruptedException {
        zk.exists("/APPConf", this, this, "");
        countDownLatch.await();
    }
    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/APPConf",this,this,"");
                break;
            case NodeDeleted:
                config.setConfig("");
                break;
            case NodeDataChanged:
                zk.getData("/APPConf",this,this,"");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
      if(data!=null){
          config.setConfig(new String(data));
          countDownLatch.countDown();
      }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if(stat!=null){
            System.err.println("stat："+stat.toString());
            zk.getData("/APPConf",this,this,"");
        }
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }
}
