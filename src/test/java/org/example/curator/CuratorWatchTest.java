package org.example.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CuratorWatchTest {

    private CuratorFramework client;
    /**
     * build connect
     */
    @Before
    public void testConnect() {
        // first method to build connection

        // 1. connect String        zk server address and port
        // 2. sessionTimeoutMs      session timeout in ms
        // 3. connectionTimeoutMs   connection timeout
        // 4. retry policy          retry policy to use
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
//        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
//                60 * 1000, 15 * 1000, retryPolicy);
//        // start connect
//        client.start();
        // second method to build connection
        client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy)
                .namespace("itheima") // all operation is set under itheima
                .build();
        // start connect
        client.start();
    }

    // ============================watcher===============================================

    /**
     * node cache
     * 1. create NodeCache object
     * 2. listener
     * 3. start listen
     */
    @Test
    public void testNodeCache() throws Exception {
        // 1. create NodeCache object
        final NodeCache nodeCache = new NodeCache(client, "/app1");
        // 2. listener
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("node change");

                // get latest data after node change
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });
        // 3. start listen, if set to true, then load cache when start listen
        nodeCache.start(true);

        while (true) {

        }
    }

    /**
     * path children cache (listen all children node not include itself)
     * @throws Exception
     */
    @Test
    public void testPathChildrenCache() throws Exception {
        // 1. initialize listen object
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/app1", true);

        // 2. bind listener
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("child node changes");
                System.out.println(pathChildrenCacheEvent);
                // listen child node data change, and get change data
                // get type
                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();

                // see if type is update
                if (type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    byte[] data = pathChildrenCacheEvent.getData().getData();
                    System.out.println(new String(data));
                }
            }
        });

        // 3. start
        pathChildrenCache.start();

        while (true) {

        }
    }

    /**
     * path children cache (listen all children node)
     * @throws Exception
     */
    @Test
    public void testTreeCache() throws Exception {
        // 1. create
        TreeCache treeCache = new TreeCache(client, "/");

        // 2. register listener
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println("node changes");
                System.out.println(treeCacheEvent);
            }
        });

        // 3. start
        treeCache.start();
    }


    /**
     * close
     */
    @After
    public void close() {
        if (client != null) client.close();
    }
}
