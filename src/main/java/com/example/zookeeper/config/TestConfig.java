package com.example.zookeeper.config;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.TimeUnit;

/**
 * 作为配置中心,异步获取数据的方式
 * Config：类即为配置项
 * ConfWatcher：核心监听类，监听节点事件、状态、数据
 *
 * 测试方式：另起一个客户端，模拟节点 /APPConf 的创建、修改、删除过程，并通过无限循环打印出节点内容
 */
public class TestConfig {
    static ZooKeeper zk = ZKUtils.getZK();

    public static void main(String[] args) throws InterruptedException {
        ConfWatcher confWatcher = new ConfWatcher();
        confWatcher.setZk(zk);
        Config config = new Config();
        confWatcher.setConfig(config);
        confWatcher.aWait();
        while (true){
            if (config.getConfig() != null && !"".equals(config.getConfig())) {
                System.out.println(config.getConfig());
            }else{
                confWatcher.aWait();
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
