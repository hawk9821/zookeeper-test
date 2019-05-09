package com.hawk.lock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 利用zookeeper顺序临时节点实现分布式锁
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class ZkSeqLockImpl extends ZkLock {
    private String currentNode;
    private String beforeNode;
    private CountDownLatch cdl;

    public ZkSeqLockImpl() {
        if (!this.zkClient.exists(path)) {
            //创建lock根节点
            this.zkClient.createPersistent(path);
        }
    }

    @Override
    protected boolean tryLock() {
        if (currentNode == null || currentNode.length() <= 0) {
            currentNode = this.zkClient.createEphemeralSequential(path + "/", "lock");
        }
        List<String> children = this.zkClient.getChildren(path);
        Collections.sort(children);
        //判断当前用户创建的临时节点的名称是否和/lock节点下的最新子节点相等，如果相等就代表获得锁
        if (currentNode.equals(path + "/" + children.get(0))) {
            return true;
        } else {
            //如果不能获取锁，那么必须要获取到当前节点前一个的节点，要注册对前一个节点的事件监听
            int i = Collections.binarySearch(children, currentNode.substring(6));
            beforeNode = path + "/" + children.get(i - 1);
        }
        return false;
    }

    @Override
    protected void waitForLock() {
        IZkDataListener iZkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println(Thread.currentThread().getName() + "=== 释放锁");
                if (cdl != null) {
                    cdl.countDown();
                }
            }
        };
        zkClient.subscribeDataChanges(beforeNode, iZkDataListener);
        if (zkClient.exists(beforeNode)) {
            cdl = new CountDownLatch(1);
            try {
                //阶段存在等待阻塞，只有当之前获取到锁的线程释放了锁,注册的delete事件触发的cdl.countDown() 唤醒阻塞线程竞争锁
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        zkClient.unsubscribeDataChanges(beforeNode, iZkDataListener);
    }

    public static void main(String[] args) {
        List<String> t = new ArrayList<>();
        t.add("001");
        t.add("003");
        t.add("006");
        t.add("005");
        t.add("002");
        t.add("004");
        Collections.sort(t);
        int a = Collections.binarySearch(t, "/lock/004".substring(6));
        System.out.println(t.get(a));
    }
}



