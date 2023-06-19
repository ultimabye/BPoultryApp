package com.ultimabyte.bpoultry.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AppExecutors {

    private final Executor mWorker;
    private final MainThreadExecutor mMainThread;


    private AppExecutors(Executor diskIO, MainThreadExecutor mainThread) {
        this.mWorker = diskIO;
        this.mMainThread = mainThread;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
    }

    public Executor worker() {
        return mWorker;
    }


    public MainThreadExecutor mainThread() {
        return mMainThread;
    }

    public static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }


        public void executeDelayed(@NonNull Runnable command, long millis) {
            mainThreadHandler.postDelayed(command, millis);
        }
    }
}