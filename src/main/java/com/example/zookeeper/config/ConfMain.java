package com.example.zookeeper.config;
import com.example.zookeeper.ZKUtils;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.TimeUnit;

/**
 * 作为配置中心,异步获取数据的方式
 * Config：类即为配置项
 * ConfWatcher：核心监听类，监听节点事件、状态、数据
 *
 * 测试方式：另起一个客户端，模拟节点 /APPConf 的创建、修改、删除过程，并通过无限循环打印出节点内容
 */
public class ConfMain {
    static ZooKeeper zk = ZKUtils.getZK();

    public static void main(String[] args) throws InterruptedException {
        ConfWatcher confWatcher = new ConfWatcher();
        confWatcher.setZk(zk);
        ConfigBean config = new ConfigBean();
        confWatcher.setConfig(config);
        confWatcher.aWait();
        while (true){
            if (config.getInfo() != null && !"".equals(config.getInfo())) {
                System.out.println(config.getInfo());
            }else{
                confWatcher.aWait();
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
