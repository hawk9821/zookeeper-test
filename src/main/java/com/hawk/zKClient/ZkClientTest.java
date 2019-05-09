package com.hawk.zKClient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangdonghao
 * @date 2019/5/8
 */
public class ZkClientTest {
    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient("192.168.220.98:2181", 5000);
        zkClient.createPersistent("/zkClient", true);
        zkClient.subscribeDataChanges("/zkClient", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("=================================节点修改了");
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("=================================节点删除了");
            }
        });
        zkClient.writeData("/zkClient", "hello zkClient");
        System.out.println("=================================  " +zkClient.readData("/zkClient"));
        zkClient.delete("/zkClient");
        //监听事件还未执行主线程已经结束，故sleep 1s
        TimeUnit.SECONDS.sleep(1);
        zkClient.close();
    }
}
