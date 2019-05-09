package com.hawk.lock;

import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class OrderService implements Runnable{
    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
    private static int count = 100;
    private OrderNumFactory factory = new OrderNumFactory();
    private static CountDownLatch cdl = new CountDownLatch(count);
//    private Lock lock = new ZkLockImpl();
    private Lock lock = new ZkSeqLockImpl();
    @Override
    public void run() {
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取锁
        lock.lock();
        //执行业务
        createOrderNum();
        //释放锁
        lock.release();
    }
    public void createOrderNum(){
        String orderNum = factory.createOrderNum();
        logger.info(Thread.currentThread().getName() + " 创建了订单号 : [" + orderNum + "]");
    }

    public static void main(String[] args) {
        for (int i = 0; i < count; i++) {
            new Thread(new OrderService()).start();
            cdl.countDown();
        }
    }
}
