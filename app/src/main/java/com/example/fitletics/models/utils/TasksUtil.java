package com.example.fitletics.models.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TasksUtil {
    public static String TAG = "TasksUtil";

    private static volatile TasksUtil sInstance;

    private volatile ExecutorService mDefaultExecutor;

    public static TasksUtil getInstance() {
        if (sInstance == null) sInstance = new TasksUtil();
        return sInstance;
    }

    public static void releaseInstance() {
        if (sInstance != null) {
            sInstance.release();
            sInstance = null;
        }
    }

    private void release() {
        this.mDefaultExecutor.shutdown();
        this.mDefaultExecutor = null;
    }

    private TasksUtil() {
        this.mDefaultExecutor = getExecutor();
    }

    public ExecutorService getExecutor() {
        if (mDefaultExecutor == null || mDefaultExecutor.isShutdown() || mDefaultExecutor.isTerminated()) {
            // Create a new ThreadPoolExecutor with 2 threads for each processor on the
            // device and a 60 second keep-alive time.
            int numCores = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    numCores * 2,
                    numCores * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>()
            );
            mDefaultExecutor = executor;
        }
        return mDefaultExecutor;
    }

    public static <TResult> Task<TResult> call(@NonNull Callable<TResult> callable) {
        return Tasks.call(getInstance().getExecutor(), callable);
    }
}
