package com.ultimabyte.bpoultry.api;

import androidx.annotation.Nullable;

import com.ultimabyte.bpoultry.BPoultry;
import com.ultimabyte.bpoultry.utils.ErrorUtils;

import retrofit2.Response;

public class GenericApiResponse<E> {

    private final ApiRequestType requestType;
    private Response<E> response;
    private final boolean isSuccessful;
    private final boolean isCanceled;
    private String errorMessage;


    GenericApiResponse(ApiRequestType requestType, Response<E> response) {
        this.requestType = requestType;
        this.response = response;
        this.isCanceled = false;
        this.isSuccessful = response.isSuccessful();
    }


    GenericApiResponse(ApiRequestType requestType, Throwable throwable, boolean canceled) {
        this.requestType = requestType;
        this.isSuccessful = false;
        this.isCanceled = canceled;
        this.errorMessage = ErrorUtils.getErrorMessage(BPoultry.shared(), throwable);
    }


    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public Response<E> getResponse() {
        return response;
    }


    public boolean isSuccessful() {
        return isSuccessful;
    }


    public boolean isCanceled() {
        return isCanceled;
    }

    public ApiRequestType getRequestType() {
        return requestType;
    }
}
