package com.hawk.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdonghao
 * @date 2019/5/8
 */
public class ZookeeperTest {
    private static CountDownLatch cdl = new CountDownLatch(1);

    private static String connectStr = "192.168.220.98:2181";

    public static void main(String[] args) {
        try {
            ZooKeeper client = new ZooKeeper(connectStr, 5000, new WatcherTest(cdl));
            cdl.await();
            System.out.println("client.getState() = " + client.getState());
            nodeCreate(client, "/hawk");
            //注册修改
            nodeChange(client, "/hawk", "hello zookeeper");
            //不注册修改
//            nodeChangeNoRegistry(client, "/hawk", "hello hawk");
            nodeDelete(client, "/hawk");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void nodeCreate(ZooKeeper client, String node) {
        try {
            //注册事件. 如果是true就代表着/zoo注册一个watcherDemo的事件
            client.exists(node, true);
            client.create(node, node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void nodeChange(ZooKeeper client, String node, String data) {
        //-1代表不管什么版本我都要修改
        try {
            //注册事件,true代表注册事件
            Stat stat = new Stat();
            byte[] data2 = client.getData(node, true, stat);
            System.out.println(new String(data2));
            System.out.println(stat);
            client.setData(node, data.getBytes(), -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void nodeChangeNoRegistry(ZooKeeper client, String node, String data) {
        try {
            client.setData(node, data.getBytes(), -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void nodeDelete(ZooKeeper client, String node) {
        try {
            client.exists(node, true);
            client.delete(node, -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
