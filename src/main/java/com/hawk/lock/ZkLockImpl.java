package com.hawk.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 利用zookeeper在一个节点下不能创建一个相同的节点实现分布式锁
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class ZkLockImpl extends ZkLock {
    private CountDownLatch cdl = null;

    @Override
    protected boolean tryLock() {
        try {
            zkClient.createEphemeral(path);
            return true;
        } catch (ZkException e) {
            return false;
        }
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
        zkClient.subscribeDataChanges(path, iZkDataListener);
        if (zkClient.exists(path)) {
            cdl = new CountDownLatch(1);
            try {
                //阶段存在等待阻塞，只有当之前获取到锁的线程释放了锁,注册的delete事件触发的cdl.countDown() 唤醒阻塞线程竞争锁
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        zkClient.unsubscribeDataChanges(path, iZkDataListener);
    }

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ZkLockImpl();
        lock.lock();
        TimeUnit.MINUTES.sleep(1);
        lock.release();
    }
}
