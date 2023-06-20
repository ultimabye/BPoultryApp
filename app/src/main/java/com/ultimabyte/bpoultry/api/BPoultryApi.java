package com.ultimabyte.bpoultry.api;


import com.ultimabyte.bpoultry.Collection;
import com.ultimabyte.bpoultry.CollectionListResponse;
import com.ultimabyte.bpoultry.data.Shop;
import com.ultimabyte.bpoultry.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * A {@link retrofit2.Retrofit} interface containing all ClearStream API methods.
 * <p>
 * Author: qijaz221@gmail.com
 */
@SuppressWarnings("UnusedDeclaration")
public interface BPoultryApi {

    @GET("user")
    Call<User> user();


    @GET("driver/{id}/shops/")
    Call<List<Shop>> shops(@Path("id") String userId);


    @GET("collection/today")
    Call<CollectionListResponse> collections();


    @POST("collection")
    Call<Collection> submitCollection(@Query("shop_id") int shopId,
                                      @Query("driver_id") String driverId,
                                      @Query("collection_amount") int weight);
}

