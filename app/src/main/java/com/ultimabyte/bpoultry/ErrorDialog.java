package com.ultimabyte.bpoultry;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * A dialog to display error message.
 * Author: qijaz221@gmail.com
 */

public class ErrorDialog extends BaseMessageDialog {


    private static final String KEY_ERROR_MESSAGE = "KEY_ERROR_MESSAGE";
    private static final String KEY_ERROR_TITLE = "KEY_ERROR_TITLE";

    private DialogDismissListener mListener;

    public static ErrorDialog newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString(KEY_ERROR_MESSAGE, message);
        args.putString(KEY_ERROR_TITLE, title);
        ErrorDialog fragment = new ErrorDialog();
        fragment.setCancelOnTouchOutSide(false);
        fragment.setCancelable(false);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof DialogDismissListener) {
            mListener = (DialogDismissListener) getParentFragment();
        } else if (getActivity() instanceof DialogDismissListener) {
            mListener = (DialogDismissListener) getActivity();
        }
    }


    @Override
    protected String getPositiveActionText() {
        return getString(R.string.okay);
    }

    @Override
    protected String getDialogMessage() {
        return requireArguments().getString(KEY_ERROR_MESSAGE);
    }

    @Override
    protected String getDialogTitle() {
        return requireArguments().getString(KEY_ERROR_TITLE);
    }

    @Override
    protected void onPositiveButtonClicked(View view) {
        BaseActivity.sErrorDialogIsShown = false;
        if (mListener != null) {
            mListener.onDialogDismissed(this);
        }
        dismissAllowingStateLoss();
    }
}
