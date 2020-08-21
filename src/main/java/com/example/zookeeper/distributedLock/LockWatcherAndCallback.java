package com.example.zookeeper.distributedLock;

import lombok.SneakyThrows;
import org.apache.zookeeper.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

public class LockWatcherAndCallback implements Watcher, AsyncCallback.StringCallback, AsyncCallback.ChildrenCallback {
    public static String LOCK_PATH = "/lock";
    public static String CHILD_LOCK_PATH = LOCK_PATH + "/c";
    CountDownLatch c = new CountDownLatch(1);
    private ZooKeeper zk;
    private Thread thread;
    private String lockName;
    public LockWatcherAndCallback(ZooKeeper zk,Thread thread) {
        this.zk = zk;
        this.thread = thread;
    }

    /**
     * Watcher
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                //如果有节点被删除，说明已经释放锁了，此时调用获取子节点，可以使得下一个节点获得锁
                zk.getChildren(LOCK_PATH,false,this,"");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    /**
     * StringCallback
     * @param rc
     * @param path
     * @param ctx
     * @param name
     */
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        //创建临时序列化节点之后的回调
        lockName = name.split("/")[2];
        zk.getChildren(LOCK_PATH,false,this,"");
    }

    public void tryLock() {
        zk.create(CHILD_LOCK_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "");
        LockSupport.park();
    }

    public void unlock() {
        try {
            String delete = LOCK_PATH+"/"+lockName;
            zk.delete(delete,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * ChildrenCallback
     * @param rc
     * @param path
     * @param ctx
     * @param children
     */
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children) {
        Collections.sort(children);
        int i = children.indexOf(lockName);
        if(i <1){
            //表示轮到自己了
            LockSupport.unpark(thread);
        }else{
            // 没有轮到自己，监听前一个节点是否删除
            try {
                String prePath = LOCK_PATH + "/" + children.get(i - 1);
                zk.exists(prePath,this);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
