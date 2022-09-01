package org.example.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CuratorTest {

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

    // ============================create================================================

    /**
     * create node (ephemeral, persistent, sequence, data)
     * 1. basic create
     */
    @Test
    public void testCreate1() throws Exception {
        // basic create
        // if not set data, data will default to be current ip
        String path = client.create().forPath("/app1");
        System.out.println(path);
    }


    /**
     * 2. create node with data
     * @throws Exception
     */
    @Test
    public void testCreate2() throws Exception {
        // create node with data
        String path = client.create().forPath("/app2", "hello".getBytes());
        System.out.println(path);
    }

    /**
     * 3. set node type
     */
    @Test
    public void testCreate3() throws Exception {
        // set node type
        // default: persistent
        // ephemeral
        String path = client.create().withMode(CreateMode.EPHEMERAL).forPath("/app3", "hello".getBytes());
        System.out.println(path);
    }

    /**
     * 4.  create multiple node in different level
     */
    @Test
    public void testCreate4() throws Exception {
        // create multiple level root
//        String path = client.create().forPath("/app4");
//        System.out.println(path);
//        // if app4 not exist, will throw exception
//        path = client.create().forPath("/app4/p1");
//        System.out.println(path);
        // if parent not exist, will auto create
        String path = client.create().creatingParentsIfNeeded().forPath("/app4/p1");
        System.out.println(path);
    }

    // ============================get================================================

    /**
     * get node data:
     * 1. get data
     * @throws Exception
     */
    @Test
    public void testGet1() throws Exception {
        // 1. get data
        byte[] data = client.getData().forPath("/app1");
        System.out.println(new String(data));
    }

    /**
     * 2. get children node     ls
     * @throws Exception
     */
    @Test
    public void testGet2() throws Exception {
        // 2. get children node (ls)
        List<String> path = client.getChildren().forPath("/");
        System.out.println(path); // app1, app2, app4
        path = client.getChildren().forPath("/app4");
        System.out.println(path); // p1
    }

    /**
     * 3. get node status   ls -s
     * @throws Exception
     */
    @Test
    public void testGet3() throws Exception {
        // 3. get status
        Stat status = new Stat();
        System.out.println(status);
        client.getData().storingStatIn(status).forPath("/app1");
        System.out.println(status);
    }

    // ============================set================================================

    /**
     * 1. set data
     * @throws Exception
     */
    @Test
    public void testSet() throws Exception {
        // set data
        client.setData().forPath("/app1", "itcast".getBytes());
    }

    /**
     * 2. set data according to version
     * @throws Exception
     */
    @Test
    public void testSetForVersion() throws Exception {
        // set data with version
        Stat status = new Stat();
        client.getData().storingStatIn(status).forPath("/app1");
        int version = status.getVersion(); // get version

        client.setData().withVersion(version).forPath("app1", "hellollo".getBytes());
    }

    // ============================delete================================================

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {

    }

    /**
     * close
     */
    @After
    public void close() {
        if (client != null) client.close();
    }


    /**
     * just curious of sorting order using anonymous
     */
    @Test
    public void sortList() {
                                        // abc, acd, abcde, abd
        List<String> list = Arrays.asList("cba", "dca", "edcba", "dba");

        list.sort((s1, s2) -> {
            int len1 = s1.length() - 1;
            int len2 = s2.length() - 1;

            char a = s1.charAt(len1);
            char b = s2.charAt(len2);

            while (a == b && len1 >= 0 && len2 >= 0) {
                a = s1.charAt(len1);
                b = s2.charAt(len2);
                len1--;
                len2--;
            }
            if (len1 == 0) return 1; // shorter string comes first
            if (len2 == 0) return -1; // longer string comes last
            return a - b;
        });
        // cba, edcba, dca, dba
        System.out.println(list);
    }
}
