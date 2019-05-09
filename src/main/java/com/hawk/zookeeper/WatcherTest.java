package com.hawk.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdonghao
 * @date 2019/5/8
 */
public class WatcherTest implements Watcher {
    private CountDownLatch cdl;

    public WatcherTest(CountDownLatch cdl) {
        this.cdl = cdl;
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        //如果client连接到服务端以后，如果会话状态变成了connected就会触发
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType()
                    && watchedEvent.getPath() == null) {
                System.out.println("zookeeper会话创建成功!");
                cdl.countDown();
            } else if (Event.EventType.NodeCreated == watchedEvent.getType()) {
                System.out.println("触发了节点创建事件 " + watchedEvent.getPath());
            } else if (Event.EventType.NodeDataChanged == watchedEvent.getType()) {
                System.out.println("触发了节点变更事件 " + watchedEvent.getPath());
            } else if (Event.EventType.NodeDeleted == watchedEvent.getType()) {
                System.out.println("触发了节点删除事件 " + watchedEvent.getPath());
            } else if (Event.EventType.NodeChildrenChanged == watchedEvent.getType()) {
                System.out.println("触发了子节点变更事件 " + watchedEvent.getPath());
            }
        }
    }
}
