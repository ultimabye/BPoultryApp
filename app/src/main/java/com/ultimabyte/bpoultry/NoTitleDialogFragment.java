package com.ultimabyte.bpoultry;


import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

/**
 * Abstract dialog fragment which all others dialogs in the app should extend from.
 * <p>
 * Author: qijaz221@gmail.com
 */
public abstract class NoTitleDialogFragment extends DialogFragment {


    /**
     * Boolean flag to set if dialog should disappear when user clicks somewhere outside the dialog.
     * Default behaviour is to dismiss it, use {@link #setCancelOnTouchOutSide(boolean)} to change it's behaviour.
     */
    private boolean mCancelOnTouchOutSide;


    /**
     * No arg constructor to initialize {@link #mCancelOnTouchOutSide}
     */
    public NoTitleDialogFragment() {
        mCancelOnTouchOutSide = true;
    }


    //setter
    public void setCancelOnTouchOutSide(boolean cancelOnTouchOutSide) {
        mCancelOnTouchOutSide = cancelOnTouchOutSide;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        //set dismiss behaviour
        dialog.setCanceledOnTouchOutside(mCancelOnTouchOutSide);

        // request a window without the title
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }


    @Override
    public void onResume() {
        if (getDialog() != null) {
            //set height and width
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                window.setAttributes(params);
            }
        }
        super.onResume();
    }


    /**
     * Convenience method to display a toast with {@link Toast#LENGTH_SHORT}
     *
     * @param text the messages to display in toast.
     */
    protected void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }


    /**
     * Convenience method to display a {@link Snackbar} with {@link Snackbar#LENGTH_SHORT}
     *
     * @param text the messages to display in snack bar.
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void showSnackBar(String text) {
        if (getView() != null) {
            Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).show();
        }
    }


    /**
     * Convenience method to display a {@link Snackbar} with {@link Snackbar#LENGTH_SHORT}
     * with a clickable action.
     *
     * @param text   the messages to display in snack bar.
     * @param action clickable text to display as Action.
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void showClickableSnackBar(String text, String action, View.OnClickListener listener) {
        if (getView() != null) {
            Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).setAction(action, listener).show();
        }
    }


    /**
     * Just a workaround to avoid IllegalStateException because the hosting activity/fragment has already performed
     * onSaveInstanceState.
     *
     * @param manager The FragmentManager this fragment will be added to.
     * @param tag     The tag for this fragment, as per
     */
    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
