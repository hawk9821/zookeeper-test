package com.hawk.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdonghao
 * @date 2019/5/8
 */
public class ACLTest {
    private static CountDownLatch cdl = new CountDownLatch(1);

    private static String connectStr = "192.168.220.98:2181";

    public static void main(String[] args) {
        String node = "/acl";
        ZooKeeper client = null;
        try {
            client = new ZooKeeper(connectStr, 5000, new WatcherTest(cdl));
            ZooKeeper client1 = new ZooKeeper(connectStr, 5000, new WatcherTest(cdl));

            cdl.await();
            //ZK的节点有5种操作权限：
            //CREATE、READ、WRITE、DELETE、ADMIN 也就是 增、删、改、查、管理权限
            //Create 允许对子节点Create操作
            //Read 允许对本节点GetChildren和GetData操作
            //Write 允许对本节点SetData操作
            //Delete 允许对子节点Delete操作
            //Admin 允许对本节点setAcl操作
            //ALL 拥有全部权限

            //身份的认证有4种方式：
            //world：默认方式，相当于全世界都能访问
            //auth：代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户)
            //digest：即用户名:密码这种方式认证，这也是业务系统中最常用的
            //ip：使用Ip地址认证
            List<ACL> acls = new ArrayList<>();
            ACL acl1 = new ACL(ZooDefs.Perms.READ, new Id("digest", DigestAuthenticationProvider.generateDigest("hawk:123")));
            ACL acl2 = new ACL(ZooDefs.Perms.DELETE, new Id("digest", DigestAuthenticationProvider.generateDigest("hawk:123")));
            ACL acl3 = new ACL(ZooDefs.Perms.ADMIN, new Id("digest", DigestAuthenticationProvider.generateDigest("hawk:123")));
            ACL acl4 = new ACL(ZooDefs.Perms.WRITE, new Id("digest", DigestAuthenticationProvider.generateDigest("hawk:123")));
            acls.add(acl1);
            acls.add(acl2);
            acls.add(acl3);
            acls.add(acl4);

            String path = client.create(node, "hello acl ".getBytes(), acls, CreateMode.PERSISTENT);
            System.out.println("新增/acl节点，存储数据为： hello acl");
            //权限校验
            System.out.println("==============权限校验==============");
            client1.addAuthInfo("digest", "hawk:123".getBytes());
            System.out.println("==============修改数据==============");
            client1.setData(path, "hello acl !!!".getBytes(), -1);
            Stat stat = new Stat();
            byte[] data = client1.getData(path,false,stat);
            System.out.println("修改后节点数据为：" + new String(data));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }finally {
//            try {
//                client.delete(node,-1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (KeeperException e) {
//                e.printStackTrace();
//            }
        }
    }
}
