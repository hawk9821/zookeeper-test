package com.hawk.lock;

/**
 * @author zhangdonghao
 * @date 2019/5/9
 */
public interface Lock {
    /**
     * 获取锁
     */
    void lock();

    /**
     * 释放锁
     */
    void release();
}
