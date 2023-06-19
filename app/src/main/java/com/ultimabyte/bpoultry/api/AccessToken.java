package com.ultimabyte.bpoultry.api;

import com.google.gson.annotations.SerializedName;

/**
 * Model class to receive authToken response from API.
 * Author: qijaz221@gmail.com
 */

public class AccessToken {

    @SerializedName("access_token")
    private final String access_token;

    @SerializedName("token_type")
    private String token_type;

    @SerializedName("expires_in")
    private final Integer expires_in;

    @SerializedName("refresh_token")
    private final String refresh_token;


    //constructor
    public AccessToken(String access_token, String token_type,
                       String refresh_token, int expires_in) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
    }


    //getters & setters
    public String getAccessToken() {
        return access_token;
    }

    public String getTokenType() {
        // OAuth requires uppercase Authorization HTTP header value for token type
        if (!Character.isUpperCase(token_type.charAt(0))) {
            token_type = Character.toString(token_type.charAt(0)).toUpperCase() + token_type.substring(1);
        }

        return token_type;
    }


    @SuppressWarnings("UnusedDeclaration")
    public int getExpiry() {
        return expires_in;
    }

    public String getRefreshToken() {
        return refresh_token;
    }
}
