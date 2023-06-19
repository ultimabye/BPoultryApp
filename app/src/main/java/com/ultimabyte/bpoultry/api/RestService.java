package com.ultimabyte.bpoultry.api;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ultimabyte.bpoultry.AppSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * {@link Retrofit} service generator class.
 * <p>
 * Author: qijaz221@gmail.com
 */
@SuppressWarnings("WeakerAccess")
public class RestService {

    /**
     * ClearStream base url.
     */
    //private static final String API_BASE_URL_PRODUCTION = "https://api.getclearstream.com/v1/";
    public static final String API_BASE_URL_DEBUG = "http://10.0.2.2:8000/api/v1/";


    //public static final String API_BASE_URL = API_BASE_URL_PRODUCTION;
    public static final String API_BASE_URL = API_BASE_URL_DEBUG;

    //set this to true to intercept API calls and add desired response using FakeInterceptor.java
    private static final boolean ALLOW_FAKE_INTERCEPTOR = false;


    /**
     * OkHttp builder.
     */
    private static List<OkHttpClient> httpClients;

    /**
     * Retrofit builder
     */
    private static Retrofit.Builder builder;


    /**
     * @param serviceClass retrofit interface.
     * @return service class API.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public synchronized static <S> S createService(Class<S> serviceClass) {
        //create a new OkHttp builder
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        /*Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();*/

        Gson gson = new GsonBuilder()
                //.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                .create();

        //create a new Retrofit builder, set baseUrl and add GsonConverter.
        builder = new Retrofit.Builder()
                .baseUrl(RestService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));


        //create logging interceptor.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        //set log level.
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //add logging interceptor to httpClient
        httpClientBuilder.addInterceptor(logging);

        //create httpClient from httpBuilder
        OkHttpClient httpClient = httpClientBuilder.build();
        if (httpClients == null) {
            httpClients = new ArrayList<>();
        }
        httpClients.add(httpClient);

        //set http client to retrofit builder
        builder.client(httpClient);

        //create retrofit client from retrofit builder.
        Retrofit retrofit = builder.build();

        //create retrofit service with given class and return it.
        return retrofit.create(serviceClass);
    }


    /**
     * @param serviceClass retrofit interface.
     * @param accessToken  oAuth access token. {@link AccessToken}
     * @param context      context
     * @return service class API.
     */
    public synchronized static <S> S createService(Class<S> serviceClass,
                                                   AccessToken accessToken,
                                                   final Context context,
                                                   @Nullable Integer timeout) {
        //create a new OkHttp builder
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (timeout != null) {
            httpClientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
            httpClientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
            httpClientBuilder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        }


        Gson gson = new GsonBuilder()
                //.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                .create();

        //create a new Retrofit builder, set baseUrl and add GsonConverter.
        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));


        //check if accessToken is present.
        if (accessToken != null) {
            //add authentication interceptor to httpClient, this will take care of automatic auth token refresh.
            httpClientBuilder.addInterceptor(new AuthenticationInterceptor(context, accessToken));
        }
        //disable http logging for now, comment in the code below to enable it.
        //create logging interceptor.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        //set log level.
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //add logging interceptor to httpClient
        httpClientBuilder.addInterceptor(logging);

        //set cache location on httpClient
        httpClientBuilder.cache(CacheProvider.provide());

        //create httpClient from httpBuilder
        OkHttpClient httpClient = httpClientBuilder.build();
        if (httpClients == null) {
            httpClients = new ArrayList<>();
        }
        httpClients.add(httpClient);

        //set http client to retrofit builder
        builder.client(httpClient);

        //create retrofit client from retrofit builder.
        Retrofit retrofit = builder.build();

        //create retrofit service with given class and return it.
        return retrofit.create(serviceClass);
    }


    //static reference of ClearStreamApi, will use the same shared through out the app.
    private static BPoultryApi sBPoultryApi;

    //need different timeout for charts related api requests.
    private static BPoultryApi sClearstreamChartsApi;


    /**
     * Static method to get ClearStreamApi shared.
     *
     * @param context context
     * @return {@link BPoultryApi} shared
     */
    public synchronized static BPoultryApi getAPI(Context context) {
        //check if ClearStreamApi shared is null.
        if (sBPoultryApi == null) {
            //create the shared.
            sBPoultryApi = RestService.createService(BPoultryApi.class,
                    AppSettings.getAccessToken(context),
                    context.getApplicationContext(),
                    null);
        }

        // return ClearStreamApi shared.
        return sBPoultryApi;
    }


    /**
     * Static method to get ClearStreamApi shared.
     *
     * @param context context
     * @return {@link BPoultryApi} shared
     */
    public synchronized static BPoultryApi getChartsAPI(Context context) {
        //check if ClearStreamApi shared is null.
        if (sClearstreamChartsApi == null) {
            //create the shared.
            sClearstreamChartsApi = RestService.createService(BPoultryApi.class,
                    AppSettings.getAccessToken(context),
                    context.getApplicationContext(),
                    60000);
        }

        // return ClearStreamApi shared.
        return sClearstreamChartsApi;
    }


    //static member for authentication service.
    private static Authentication authenticationService;


    /**
     * Static method to get authentication service shared.
     *
     * @return {@link Authentication} shared
     */
    public static Authentication getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = createService(Authentication.class);
        }

        return authenticationService;
    }


    /**
     * Forces API implementation to be re-created through {@link #createService(Class, AccessToken, Context, Integer)}
     */
    public synchronized static void invalidate() {
        if (httpClients != null) {
            for (OkHttpClient client : httpClients) {
                client.dispatcher().cancelAll();
            }
            httpClients.clear();
        }
        sBPoultryApi = null;
        sClearstreamChartsApi = null;
    }
}
