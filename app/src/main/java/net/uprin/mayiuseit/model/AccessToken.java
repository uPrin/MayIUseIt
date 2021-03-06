package net.uprin.mayiuseit.model;

import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;

/**
 * Created by uPrin on 2017. 12. 1..
 */

public class AccessToken {

    @SerializedName("ACCESS_TOKEN")
    String accessToken;
    @SerializedName("REFRESH_TOKEN")
    String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
