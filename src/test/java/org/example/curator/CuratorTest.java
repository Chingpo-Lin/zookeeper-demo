package org.example.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CuratorTest {

    /**
     * build connect
     */
    @Test
    public void testConnect() {
        // first method to build connection

        // 1. connect String        zk server address and port
        // 2. sessionTimeoutMs      session timeout in ms
        // 3. connectionTimeoutMs   connection timeout
        // 4. retry policy          retry policy to use
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
                60 * 1000, 15 * 1000, retryPolicy);
        // start connect
        client.start();
        // second method to build connection
//        CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
//                .sessionTimeoutMs(60 * 1000)
//                .connectionTimeoutMs(15 * 1000)
//                .retryPolicy(retryPolicy)
////                .namespace("itheima") // all operation is set under itheima
//                .build();
//        // start connect
//        client.start();
    }

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
