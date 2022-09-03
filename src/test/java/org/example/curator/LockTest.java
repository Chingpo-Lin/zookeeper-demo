package org.example.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LockTest {

    public static void main(String[] args) {
        TicketShop ticketShop = new TicketShop();

        // create clients
        Thread t1 = new Thread(ticketShop, "custom1");
        Thread t2 = new Thread(ticketShop, "custom2");

        t1.start();
        t2.start();
    }
}
