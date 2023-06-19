package com.ultimabyte.bpoultry.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * A separate retrofit interface just for authentication.
 * Author: qijaz221@gmail.com
 */
public interface Authentication {

    /**
     * fields to use during initial authentication request and subsequent token refresh calls.
     */
    String GRANT_TYPE_PASSWORD = "password";
    String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";


    /**
     * @param username  username or email of the user when logging in the first time.
     * @param password  password of the user.
     * @param grantType grantType param required by server, should be {@link #GRANT_TYPE_PASSWORD}.
     * @return {@link AccessToken}
     */
    @FormUrlEncoded
    @POST("login")
    Call<AccessToken> getNewAccessToken(
            @Field("username") String username,
            @Field("password") String password);


    /**
     * @param refreshToken the actual refresh token.
     * @param grantType    grantType param required by server, should be  {@link #GRANT_TYPE_REFRESH_TOKEN}.
     * @return {@link AccessToken}
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> refreshAccessToken(
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType);

}
