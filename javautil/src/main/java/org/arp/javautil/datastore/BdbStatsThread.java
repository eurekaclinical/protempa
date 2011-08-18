package org.arp.javautil.datastore;

import com.sleepycat.je.Environment;
import com.sleepycat.je.StatsConfig;

final class BdbStatsThread implements Runnable {

    private final Environment env;

    BdbStatsThread(Environment env) {
        this.env = env;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(env.getStats(new StatsConfig()));
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
