package com.hawk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class CuratorClientTest {
    private static String connectStr = "192.168.220.98:2181";

    public static void main(String[] args) {
        try {
            //用于模拟zookeeper服务
            TestingServer testingServer = new TestingServer();
            //构造器创建连接
            //重连机制 ExponentialBackoffRetry(1000,3)   1000毫秒3次
            CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(testingServer.getConnectString(), 5000, 5000, new ExponentialBackoffRetry(1000, 3));
            //启动连接
            curatorClient.start();
            //构建模式创建连接
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(testingServer.getConnectString())
                    .connectionTimeoutMs(5000)
                    .sessionTimeoutMs(5000)
                    .retryPolicy(new ExponentialBackoffRetry(1000,3)).build();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
