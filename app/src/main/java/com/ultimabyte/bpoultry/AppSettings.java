package com.ultimabyte.bpoultry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.ultimabyte.bpoultry.api.AccessToken;

import java.util.Date;

/**
 * Class to save/access App Setting to {@link SharedPreferences}
 * Author: qijaz221@gmail.com
 */

public class AppSettings {

    /**
     * String constants to use as keys with {@link SharedPreferences}
     */
    private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    private static final String KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN";
    private static final String KEY_TOKEN_TYPE = "KEY_TOKEN_TYPE";
    private static final String KEY_TOKEN_EXPIRY = "KEY_TOKEN_EXPIRY";
    private static final String KEY_EMAIL = "KEY_EMAIL";

    static final String KEY_USER_ID = "KEY_USER_ID";


    /**
     * Saves {@link AccessToken} to {@link SharedPreferences}
     *
     * @param context to access {@link SharedPreferences}
     * @param token   to save.
     */
    @SuppressLint("ApplySharedPref")
    public static void saveAccessToken(Context context, AccessToken token) {
        if (token != null) {
            //get private sharedPreferences
            SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);


            //put all token attributes using appropriate keys and commit. IMPORTANT: use commit instead of 'apply' here.
            /*comment this out to test token refresh*/
            prefs.edit().putString(KEY_ACCESS_TOKEN, token.getAccessToken()).commit();

            //comment below line in and logout/login again to test token refresh. This will cause 401 error, resulting on token refresh.
            //prefs.edit().putString(KEY_ACCESS_TOKEN, "fakeAuthToken").commit();

            prefs.edit().putString(KEY_REFRESH_TOKEN, token.getRefreshToken()).commit();
            prefs.edit().putString(KEY_TOKEN_TYPE, token.getTokenType()).commit();
        }
    }


    /**
     * Get {@link AccessToken}, if previously saved to {@link SharedPreferences}, null otherwise.
     *
     * @param context to access {@link SharedPreferences}
     * @return {@link AccessToken} object.
     */
    @Nullable
    public static AccessToken getAccessToken(Context context) {
        //get private sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        //read all attribute values.
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        String refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null);
        String tokenType = prefs.getString(KEY_TOKEN_TYPE, null);
        int expiry = prefs.getInt(KEY_TOKEN_EXPIRY, 0);


        if (token != null && token.length() > 0 && refreshToken != null && refreshToken.length() > 0 && tokenType != null && tokenType.length() > 0) {
            //if all three of Access Token attributes exist, create and return Access Token
            return new AccessToken(token, tokenType, refreshToken, expiry);
        }

        //return null if any of the attribute are missing.
        return null;
    }


    /**
     * Clears saved {@link AccessToken}. Use this when user logs out.
     *
     * @param context to access {@link SharedPreferences}.
     */
    @SuppressLint("ApplySharedPref")
    public static void clearAccessToken(Context context) {
        //get reference to private sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        //put all attributes as null and commit. IMPORTANT: use commit instead of 'apply' here.
        prefs.edit().putString(KEY_ACCESS_TOKEN, null).commit();
        prefs.edit().putString(KEY_REFRESH_TOKEN, null).commit();
        prefs.edit().putString(KEY_TOKEN_TYPE, null).commit();
    }


    /**
     * Saves time when the data on particular screen was last updated.
     *
     * @param context {@link Context} to use.
     * @param key     the key, this should be unique to identify the screen.
     * @param time    the time.
     */
    @SuppressLint("ApplySharedPref")
    public static void saveUpdateTime(Context context, String key, long time) {
        SharedPreferences preferences = getPrivateSharedPrefs(context);
        preferences.edit().putLong(key, time).commit();
    }


    /**
     * Returns the time when a particular screen was last updated.
     *
     * @param context {@link Context} to use.
     * @param key     the key, this should be unique to identify the screen.
     * @return time when the given screen was last updated.
     */
    public static long getUpdateTime(Context context, String key) {
        SharedPreferences preferences = getPrivateSharedPrefs(context);
        return preferences.getLong(key, 0L);
    }


    public static void saveUserEmail(Context context, String email) {
        SharedPreferences sharedPreferences = getPrivateSharedPrefs(context);
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = getPrivateSharedPrefs(context);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }


    public static void saveUserId(Context context, String email) {
        SharedPreferences sharedPreferences = getPrivateSharedPrefs(context);
        sharedPreferences.edit().putString(KEY_USER_ID, email).apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = getPrivateSharedPrefs(context);
        return sharedPreferences.getString(KEY_USER_ID, null);
    }


    public static void clearAppSettings(Context context) {
        //clear access token.
        clearAccessToken(context);
    }


    private static SharedPreferences getPrivateSharedPrefs(Context context) {
        //get reference to private sharedPreferences
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        //return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
