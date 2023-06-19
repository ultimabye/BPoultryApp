package com.ultimabyte.bpoultry.api;

import androidx.annotation.NonNull;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;


/**
 * An Interceptor to enable short term cache
 * <p>
 */
@SuppressWarnings("UnusedDeclaration")
public class CacheInterceptor implements Interceptor {

    private static final String CACHE_CONTROL = "Cache-Control";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        //create a cache control for 2 minutes.
        CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(2, TimeUnit.MINUTES)
                .build();

        return response.newBuilder().header(CACHE_CONTROL, cacheControl.toString()).build();
    }
}
