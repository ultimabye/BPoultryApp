package com.ultimabyte.bpoultry;

import com.google.gson.annotations.SerializedName;

public class AuthTokenResponse {
    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getters and setters
}
