package com.example.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.locks.LockSupport;

/**
 * 事件监听、异步结果回调
 */
public class ConfWatcher implements Watcher, AsyncCallback.DataCallback,AsyncCallback.StatCallback {
    ZooKeeper zk;
    ConfigBean config;
    Thread main;
    public static String PATH = "/APPConf";
    /**
     * 等待节点的创建
     * exists会开启一个线程监听，一旦节点被创建的话触发节点创建事件，进而触发异步获取节点事件
     * @throws InterruptedException
     */
    public void aWait() throws InterruptedException {
        zk.exists(PATH, this, this, "");
        main = Thread.currentThread();
        LockSupport.park();
    }

    /**
     * 能保证节点每次操作都有后续监听靠的就是这个方法
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                System.out.println("==================== 节点创建成功！");
                zk.getData(PATH,this,this,"");
                break;
            case NodeDeleted:
                System.out.println("==================== 节点被删除！");
                config.setInfo("");
                break;
            case NodeDataChanged:
                System.out.println("==================== 节点数据改变！");
                zk.getData(PATH,this,this,"");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    /**
     * 异步数据回调，将得到的数据存在config中
     * @param rc
     * @param path
     * @param ctx
     * @param data
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
      if(data!=null){
          config.setInfo(new String(data));
          if(main.getState().equals(Thread.State.WAITING)){
              LockSupport.unpark(main);
          }
      }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if(stat!=null){
            System.err.println("stat："+stat.toString());
            zk.getData(PATH,this,this,"");
        }
    }

    public ConfigBean getConfig() {
        return config;
    }

    public void setConfig(ConfigBean config) {
        this.config = config;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }
}
