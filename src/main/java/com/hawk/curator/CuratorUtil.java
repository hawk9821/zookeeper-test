package com.hawk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * 单例类
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class CuratorUtil {
    private static String connectStr = null;
    private static TestingServer server = null;

    static {
        try {
            server = new TestingServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CuratorInstance {
        private static CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectStr)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    }

    public static CuratorFramework getInstance(String connect){
        connectStr = connect;
        return CuratorInstance.client;
    }
}

