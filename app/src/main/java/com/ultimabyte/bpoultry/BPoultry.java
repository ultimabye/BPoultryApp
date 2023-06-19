package com.ultimabyte.bpoultry;

import android.app.Application;

import com.ultimabyte.bpoultry.data.BPoultryDB;
import com.ultimabyte.bpoultry.data.BPoultryRepo;
import com.ultimabyte.bpoultry.utils.AppExecutors;

public class BPoultry extends Application {

    private static BPoultry shared;

    private AppExecutors mAppExecutors;
    private boolean isShowingLoginScreen;


    @Override
    public void onCreate() {
        super.onCreate();
        shared = this;
        mAppExecutors = new AppExecutors();
    }

    public static BPoultry shared() {
        return shared;
    }


    public BPoultryDB getDatabase() {
        return BPoultryDB.shared(this);
    }


    public BPoultryRepo getRepository() {
        return BPoultryRepo.shared();
    }


    public void async(Runnable runnable) {
        mAppExecutors.worker().execute(runnable);
    }

    public void onMain(Runnable runnable) {
        mAppExecutors.mainThread().execute(runnable);
    }


    public void onMainDelayed(Runnable runnable, long millis) {
        mAppExecutors.mainThread().executeDelayed(runnable, millis);
    }


    public boolean isShowingLoginScreen() {
        return isShowingLoginScreen;
    }


    public void setShowingLoginScreen(boolean isShowingLoginScreen) {
        this.isShowingLoginScreen = isShowingLoginScreen;
    }
}
