package com.hawk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class CuratorTest {
    public static void main(String[] args) {
        CuratorFramework client = CuratorUtil.getInstance("192.168.220.98:2181");
        String path = "/curator";
        String data = "hello curator";
        client.start();
        try {
            create(client,path,data);
            System.out.println("============================== created");
            String result = query(client,path);
            System.out.println("============================== result:" + result);
            update(client,path,"upadte !!!!",-1);
            System.out.println("============================== updated, data: " + query(client,path));
            delete(client,path,-1);
            System.out.println("============================== deleted");
            createSync(client,path,data);
            System.out.println("============================== created sync");
            delete(client,path,-1);
            System.out.println("============================== deleted");
            createSyncExcutor(client,path,data);
            System.out.println("============================== created syncExecutor");
            delete(client,path,-1);
            System.out.println("============================== deleted");
            transaction(client,path,data);
            System.out.println("============================== transaction");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void create(CuratorFramework client, String path, String data) {
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }/*finally {
            if (client != null) {
                client.close();
            }
        }*/
    }

    private static String query(CuratorFramework client, String path) {
        try {
            byte[] result = client.getData().forPath(path);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }/*finally {
            if (client != null) {
                client.close();
            }
        }*/
        return null;
    }

    private static void update(CuratorFramework client, String path, String data, Integer version) {
        try {
            Stat stat = client.setData()
                    .withVersion(version)
                    .forPath(path, data.getBytes());
            System.out.println(stat);
        } catch (Exception e) {
            e.printStackTrace();
        }/* finally {
            if (client != null) {
                client.close();
            }
        }*/
    }

    private static void delete(CuratorFramework client, String path, Integer version) {
        try {
            client.delete()
                    .withVersion(version)
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }/* finally {
            if (client != null) {
                client.close();
            }
        }*/
    }

    /**
     * 使用curator内部线程池异步运行
     *
     * @param client
     * @param path
     * @param data
     */
    private static void createSync(CuratorFramework client, String path, String data) {
        //inBackground这个方法就是异步运行的意思
        try {
            client.create().creatingParentsIfNeeded().
                    withMode(CreateMode.EPHEMERAL).
                    inBackground(new BackgroundCallback() {
                        @Override
                        //只有当接到创建成功才会回调
                        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                            System.out.println(event.getName() + ":" + event.getPath());
                        }
                    }).forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            if (client != null) {
                client.close();
            }
        }*/
    }

    /**
     * 使用自定义线程池异步运行
     *
     * @param client
     */
    private static void createSyncExcutor(CuratorFramework client, String path, String data) {
        ExecutorService pools = Executors.newFixedThreadPool(2);
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).
                    inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                            System.out.println(Thread.currentThread().getName() + "==resultCode:" + event.getResultCode() + "==eventType:" + event.getType());
                        }
                    }, pools).
                    forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            if (client != null) {
                client.close();
            }
        }*/
    }

    private static void transaction(CuratorFramework client, String path, String data) {
        try {
            Collection<CuratorTransactionResult> results = client.inTransaction()
                    .create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, data.getBytes())
                    .and()
                    .setData()
                    .forPath(path, (data + "modify data").getBytes())
                    .and()
                    .commit();
            for (CuratorTransactionResult result : results) {
                System.out.println(result.getForPath() + "==" + result.getResultPath() + "==" + result.getResultStat());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
