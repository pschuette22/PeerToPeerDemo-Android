package com.zeppatech.p2pdemo.p2p.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by PSchuette on 8/30/16.
 */
public class ThreadManager {

    // Max two background threads running at a time
    private static ExecutorService executor = Executors.newFixedThreadPool(3);

    /**
     * Tell the thread manager to execute a thread
     * @param runnable
     */
    public static void execute(Runnable runnable){
        executor.execute(runnable);
    }

    /**
     * Kill all the running threads regardless of their current state
     */
    public static void killRunners(){
        // Bang Bang
        executor.shutdownNow();
    }

}
