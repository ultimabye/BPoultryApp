package com.ultimabyte.bpoultry.utils;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.ultimabyte.bpoultry.AppSettings;
import com.ultimabyte.bpoultry.BPoultry;
import com.ultimabyte.bpoultry.LoginActivity;
import com.ultimabyte.bpoultry.R;
import com.ultimabyte.bpoultry.api.RestService;
import com.ultimabyte.bpoultry.data.BPoultryRepo;

import org.json.JSONObject;

import java.net.UnknownHostException;

import retrofit2.Response;

/**
 * A convenience class to extract error message from {@link Exception} or {@link Throwable}
 * Author: qijaz221@gmail.com
 */

public class ErrorUtils {

    /**
     * Extracts error message from an {@link Exception} object.
     *
     * @param e {@link Exception} object.
     * @return error message.
     */
    public static String getErrorMessage(@NonNull Exception e) {
        if (e.getMessage() != null) {
            //if original exception has an error message return it.
            return e.getMessage();
        } else {
            //if exception does not have an error message, return a default error message.
            return "Unknown error.";
        }
    }


    /**
     * Extracts error message from an {@link Throwable} object.
     *
     * @param t {@link Throwable} object.
     * @return error message.
     */
    public static String getErrorMessage(@NonNull Context context, @NonNull Throwable t) {
        if (t instanceof UnknownHostException) {
            return context.getString(R.string.no_network_error);
        }
        if (t.getMessage() != null) {
            //if original throwable has an error message return it.
            return t.getMessage();
        } else {
            //if throwable does not have an error message, return a default error message.
            return "Connection failed.";
        }
    }


    public static synchronized boolean handleFailedApiResponse(Context context,
                                                               int responseCode,
                                                               String errorBody,
                                                               String tag) {

        if (!isAuthorizationValid(context, responseCode, tag)) {
            Logger.d(tag, "Authorization failed.");
            return true;
        }

        Logger.d(tag, "Error body=" + errorBody);
        String errorMessage = getErrorMessageSafely(errorBody, tag);
        String errorType = getErrorType(errorBody);
        return false;
    }


    private synchronized static boolean isAuthorizationValid(Context context,
                                                             int responseCode,
                                                             String tag) {
        if (responseCode == HTTP_UNAUTHORIZED) {
            if (BPoultry.shared().isShowingLoginScreen()) {
                Logger.d(tag, "LoginActivity is already visible, skip...");
                return false;
            }

            BPoultry.shared().setShowingLoginScreen(true);

            //Clean cache
            BPoultryRepo.shared().clearAllData();
            //clear access token from sharedPreferences
            AppSettings.clearAccessToken(context);
            RestService.invalidate();

            String errorMessage = context.getString(R.string.expired_login_msg);
            Intent login = new Intent(context, LoginActivity.class);
            login.putExtra(LoginActivity.KEY_STARTUP_ERROR, errorMessage);
            context.startActivity(login);
            if (context instanceof Activity) {
                Activity hostActivity = (Activity) context;
                hostActivity.finish();
            }
            return false;
        }
        return true;
    }


    @Nullable
    private static String getErrorType(String errorBody) {
        if (errorBody == null) {
            return null;
        }
        try {
            JSONObject rootObject = new JSONObject(errorBody);
            JSONObject errorObject = rootObject.getJSONObject("error");
            return errorObject.getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getErrorMessageSafely(String errorBody, String tag) {
        String errorMessage = null;
        //Logger.d("getErrorMessageSafely", "errorBody=" + errorBody);
        if (errorBody != null && !errorBody.isEmpty()) {
            try {
                JSONObject rootObject = new JSONObject(errorBody);
                JSONObject errorObject = rootObject.getJSONObject("error");
                //JSONObject errorObject = rootObject.getJSONObject("error");
                errorMessage = errorObject.getString("message");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Logger.d(tag, "getErrorMessageSafely errorMessage=" + errorMessage);

        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Unknown error, please try again!";
        }

        return errorMessage;
    }


    @Nullable
    public static String getErrorBody(Response<?> response, String tag) {
        if (response == null || response.errorBody() == null) {
            Logger.d(tag, "getErrorBody response or errorBody is null.");
            return null;
        }
        try {
            //Logger.d(tag, "getErrorBody errorBody=" + errorBody);
            return response.errorBody().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
