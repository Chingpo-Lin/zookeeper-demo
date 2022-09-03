package org.example.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class TicketShop implements Runnable {

    private int tickets = 10; // database ticket

    private InterProcessMutex lock;

    public TicketShop() {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy)
                .build();
        // start connect
        client.start();

        lock = new InterProcessMutex(client, "/lock");
    }

    @Override
    public void run() {
        while (true) {
            // get lock here
            try {
                lock.acquire(3, TimeUnit.SECONDS);

                if (tickets > 0) {
                    System.out.println(Thread.currentThread() + ":" + tickets);
                    Thread.sleep(300);
                    tickets--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    // release lock
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
