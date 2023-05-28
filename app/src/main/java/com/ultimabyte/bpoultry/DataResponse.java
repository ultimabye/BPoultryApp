package com.ultimabyte.bpoultry;

import com.google.gson.annotations.SerializedName;

public class DataResponse {
    @SerializedName("data")
    private String data;

    // Getter and setter methods for the 'data' field
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
