package com.hawk.lock;

import org.I0Itec.zkclient.ZkClient;

/**
 * @author zhangdonghao
 * @date 2019/5/9
 */
public abstract class ZkLock implements Lock {
    private static String connectStr = "192.168.220.98:2181";
    protected static String path = "/lock";
    protected ZkClient zkClient = new ZkClient(connectStr);

    /**
     * 在高并发下永远只有一个线程能够获取到锁
     * 获取不到锁的线程等待
     *
     */
    @Override
    public void lock() {
        if (tryLock()){
            System.out.println("线程 [" + Thread.currentThread().getName() + "] 获取到锁");
        }else {
            //阻塞未获得到锁的线程，一旦锁释放，继续tryLock()
            waitForLock();
            tryLock();
        }
    }


    @Override
    public void release() {
        zkClient.close();
    }

    protected abstract boolean tryLock();

    protected abstract void waitForLock();
}
