package com.ultimabyte.bpoultry;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ultimabyte.bpoultry.api.ApiRequestType;
import com.ultimabyte.bpoultry.api.GenericApiResponse;
import com.ultimabyte.bpoultry.utils.ErrorUtils;
import com.ultimabyte.bpoultry.utils.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    public volatile static boolean sErrorDialogIsShown;

    private ArrayList<Runnable> mPendingRunnableList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }


    protected void runOnResume(Runnable runnable) {
        if (!isDestroyed()) {
            //submit runnable to main thread's queue.
            BPoultry.shared().onMain(runnable);
            return;
        }

        if (mPendingRunnableList == null) {
            mPendingRunnableList = new ArrayList<>();
        }
        mPendingRunnableList.add(runnable);
    }


    @SuppressWarnings("SameParameterValue")
    protected void handleFailedResponse(GenericApiResponse<?> response,
                                        boolean showToast,
                                        ApiRequestType requestType) {
        if (response.isCanceled()) {
            Logger.d(this.getClass().getSimpleName(),
                    "Canceled=" + requestType + " Skip handling error.");
            return;
        }
        //execute this on main thread.
        BPoultry.shared().onMain(() -> {
            //check if we are already showing login screen, if so don't do anything.
            if (BPoultry.shared().isShowingLoginScreen()) {
                Logger.d(this.getClass().getSimpleName(),
                        "LoginActivity is already visible, skip...");
                return;
            }


            //check if we have api response.
            if (response.getResponse() != null) {
                //get error code.
                int responseCode = response.getResponse().code();
                //get error object.
                String errorBody = ErrorUtils.getErrorBody(response.getResponse(),
                        this.getClass().getSimpleName());
                //handle invalid account / user status.
                if (ErrorUtils.handleFailedApiResponse(this, responseCode,
                        errorBody, this.getClass().getSimpleName())) {
                    return;
                }
                //unexpected error, display error message.
                String errorMessage = ErrorUtils.getErrorMessageSafely(errorBody,
                        this.getClass().getSimpleName());
                displayErrorView(errorMessage, showToast, requestType);
            } else {
                //display error message.
                if (response.getErrorMessage() != null) {
                    displayErrorView(response.getErrorMessage(), showToast, requestType);
                } else {
                    displayErrorView(getString(R.string.unknown_error), showToast, requestType);
                }
            }
        });
    }


    @CallSuper
    protected void displayErrorView(String errorMessage,
                                    boolean showToast,
                                    ApiRequestType requestType) {
        if (showToast) {
            showShortToast(String.format(getString(R.string.error), errorMessage));
        } else {
            showAlertDialogWith(errorMessage);
        }
    }


    private void showAlertDialogWith(String msg) {

        if (BaseActivity.sErrorDialogIsShown) {
            Logger.d(this.getClass().getSimpleName(), "ErrorDialog sErrorDialogIsShown=" + BaseActivity.sErrorDialogIsShown);
            return;
        }
        Logger.d(this.getClass().getSimpleName(), "ErrorDialog showing error dialog, msg=" + msg);
        BaseActivity.sErrorDialogIsShown = true;
        ErrorDialog.newInstance(getString(R.string.error_dialog_title), msg)
                .show(getSupportFragmentManager(), ErrorDialog.class.getSimpleName());
    }


    protected void showShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
