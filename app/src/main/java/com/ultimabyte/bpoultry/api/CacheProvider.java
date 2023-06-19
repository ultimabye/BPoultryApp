package com.ultimabyte.bpoultry.api;


import com.ultimabyte.bpoultry.BPoultry;

import java.io.File;

import okhttp3.Cache;


/**
 * Helper class to provide {@link Cache} to {@link okhttp3.OkHttpClient}
 * Author: qijaz221@gmail.com
 */
public class CacheProvider {


    /**
     * Create cache in app's cache directory.
     *
     * @return {@link Cache}
     */
    public static Cache provide() {
        Cache cache = null;
        try {
            cache = new Cache(new File(BPoultry.shared().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }
}
