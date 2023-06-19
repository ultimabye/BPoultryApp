package com.ultimabyte.bpoultry.api;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ultimabyte.bpoultry.AppSettings;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * An interceptor that refreshes authentication token when required.
 * Author: qijaz221@gmail.com
 */

public class AuthenticationInterceptor implements Interceptor {

    private final Context mContext;
    private AccessToken mAccessToken;


    AuthenticationInterceptor(Context context, AccessToken accessToken) {
        mContext = context.getApplicationContext();
        mAccessToken = accessToken;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        //Log.d(TAG, "Incoming request..." + request.toString());

        //Build new request
        Request.Builder builder = request.newBuilder();
        builder.header("Accept", "application/json"); //if necessary, say to consume JSON
        builder.header("Content-type", "application/json");

        String token = mAccessToken.getAccessToken(); //save token of this request for future
        setAuthHeader(builder, token); //write current token to request

        request = builder.build(); //overwrite old request
        Response response = chain.proceed(request); //perform request, here original request will be executed

        if (response.code() == HTTP_UNAUTHORIZED) { //if unauthorized
            //Log.d(TAG, "HTTP_UNAUTHORIZED");
            synchronized (this) { //perform all 401 in sync blocks, to avoid multiply token updates
                String currentToken = mAccessToken.getAccessToken(); //get currently stored token

                if (currentToken != null && currentToken.equals(token)) { //compare current token with token that was stored before, if it was not updated - do update
                    //Log.d(TAG, "Trying to refresh token.");
                    int code = refreshToken(); //refresh token
                    if (code != HTTP_OK) { //if refresh token failed for some reason
                        return response;//if token refresh failed - show error to user
                    }
                    //Log.d(TAG, "AccessToken refreshed success, code=" + code);
                }

                if (mAccessToken.getAccessToken() != null) { //retry requires new auth token,
                    setAuthHeader(builder, mAccessToken.getAccessToken()); //set auth token to updated
                    request = builder.build();
                    //Log.d(TAG, "Proceed with updated token.");
                    return chain.proceed(request); //repeat request with new token
                }
            }
        }

        //Log.d(TAG, "Proceed with the original request");
        return response;
    }

    private void setAuthHeader(Request.Builder builder, String token) {
        if (token != null) //Add Auth token to each request if authorized
            builder.header("Authorization", mAccessToken.getTokenType() + " " + mAccessToken.getAccessToken());
        //Log.d(TAG, "Authorization " + mAccessToken.getTokenType() + " " + mAccessToken.getAccessToken());
    }


    private int refreshToken() throws IOException {
        //Refresh token, synchronously, save it, and return result code
        Authentication tokenClient = RestService.createService(Authentication.class);
        Call<AccessToken> call = tokenClient.refreshAccessToken(mAccessToken.getRefreshToken(),
                Authentication.GRANT_TYPE_REFRESH_TOKEN);
        retrofit2.Response<AccessToken> tokenResponse = call.execute();
        if (tokenResponse.code() == HTTP_OK) {
            AccessToken newToken = tokenResponse.body();
            if (newToken != null) {
                mAccessToken = newToken;
                AppSettings.saveAccessToken(mContext, newToken);
            }
        }
        return tokenResponse.code();
    }

}
