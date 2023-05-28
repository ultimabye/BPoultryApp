package com.ultimabyte.bpoultry;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApiService {
    @FormUrlEncoded
    @POST("/oauth/token")
    Call<AuthTokenResponse> login(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );

    @GET("api/data/{userId}")
    Call<DataResponse> getData(
            @Header("Authorization") String authorization,
            @Path("userId") String userId
    );
}

