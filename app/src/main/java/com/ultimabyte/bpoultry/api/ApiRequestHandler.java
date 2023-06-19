package com.ultimabyte.bpoultry.api;

import androidx.annotation.NonNull;

import com.ultimabyte.bpoultry.utils.Logger;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRequestHandler<E> {

    private static final String TAG = ApiRequestHandler.class.getSimpleName();


    public void sendRequest(ApiRequestType requestType, Call<E> apiCall) {
        //enqueue the call passing in next page.
        apiCall.enqueue(new Callback<E>() {
            @Override
            public void onResponse(@NonNull Call<E> call, @NonNull Response<E> response) {
                GenericApiResponse<E> csResponse = new GenericApiResponse<>(requestType, response);
                Logger.d(TAG, "isSuccess=" + response.isSuccessful() + " request=" + requestType);
                EventBus.getDefault().post(csResponse);
            }

            @Override
            public void onFailure(@NonNull Call<E> call, @NonNull Throwable t) {
                Logger.w(TAG, "Api call failed, request=" + requestType + " error=" + t);
                GenericApiResponse<E> csResponse = new GenericApiResponse<>(
                        requestType, t, call.isCanceled());
                EventBus.getDefault().post(csResponse);
            }
        });
    }
}
