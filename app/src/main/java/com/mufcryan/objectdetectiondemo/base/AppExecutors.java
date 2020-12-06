/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mufcryan.objectdetectiondemo.base;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Global executor pools for the whole app.
 */
public class AppExecutors {
    final static int CPU_COUNT = Runtime.getRuntime().availableProcessors() + 1;

    private static final ThreadFactory sDbThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "App#p-db#" + mCount.getAndIncrement());
        }
    };

    private static final ThreadFactory sDiskThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "App#p-disk#" + mCount.getAndIncrement());
        }
    };

    private static final ThreadFactory sNetworkThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "App#p-net#" + mCount.getAndIncrement());
        }
    };

    private static AppExecutors instance = new AppExecutors(Executors.newSingleThreadExecutor(sDbThreadFactory),
            Executors.newFixedThreadPool(CPU_COUNT, sDiskThreadFactory),
            Executors.newFixedThreadPool(CPU_COUNT, sNetworkThreadFactory),
            new MainThreadExecutor());

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (AppExecutors.class) {
                if (instance == null) {
                    instance = new AppExecutors(Executors.newSingleThreadExecutor(sDbThreadFactory),
                            Executors.newFixedThreadPool(CPU_COUNT, sDiskThreadFactory),
                            Executors.newFixedThreadPool(CPU_COUNT, sNetworkThreadFactory),
                            new MainThreadExecutor());
                }
            }
        }
        return instance;
    }

    private ExecutorService mDBIO;
    private ExecutorService mDiskIO;
    private ExecutorService mNetworkIO;
    private Executor mMainThread;
    // scheduled task
    private ScheduledExecutorService scheduledService;

    private AppExecutors() { }

    private AppExecutors(ExecutorService dbIO, ExecutorService diskIO, ExecutorService networkIO, Executor mainThread) {
        this.mDBIO = dbIO;
        this.mDiskIO = diskIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
        this.scheduledService  = Executors.newScheduledThreadPool(CPU_COUNT);
    }

    public ExecutorService dbIO() {
        return mDBIO;
    }

    public void restartDbIO() {
        mDBIO.shutdownNow();
        mDBIO = Executors.newSingleThreadExecutor(sDbThreadFactory);
    }

    public ExecutorService diskIO() {
        return mDiskIO;
    }

    public ExecutorService networkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    public ScheduledExecutorService getScheduledService(){
        return scheduledService;
    }
}
