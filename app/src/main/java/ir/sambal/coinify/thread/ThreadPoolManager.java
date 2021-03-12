package ir.sambal.coinify.thread;

import android.os.Process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolManager {

    private static ThreadPoolManager instance = null;

    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private final ExecutorService executorService;
    private final BlockingQueue<Runnable> taskQueue;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        instance = new ThreadPoolManager();
    }

    private ThreadPoolManager() {
        taskQueue = new LinkedBlockingQueue<Runnable>();

        //executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE, new BackgroundThreadFactory());
        executorService = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES * 2,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                taskQueue,
                new BackgroundThreadFactory()
        );
    }

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    // Add a runnable to the queue, which will be executed by the next available thread in the pool
    public void addRunnable(Runnable runnable){
        executorService.execute(runnable);
    }

    /** A ThreadFactory implementation which create new threads for the thread pool.
     *  The threads created is set to background priority, so it does not compete with the UI thread.
     */
    private static class BackgroundThreadFactory implements ThreadFactory {
        private static final int sTag = 1;

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("CustomThread" + sTag);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            return thread;
        }
    }
}