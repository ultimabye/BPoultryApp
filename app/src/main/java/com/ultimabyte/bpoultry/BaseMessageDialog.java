package com.ultimabyte.bpoultry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.ultimabyte.bpoultry.databinding.DialogSingleMessageBinding;

/**
 * Base dialog to display a simple message with a title and buttons to dismiss it.
 * <p>
 * Author: qijaz221@gmail.com
 */

public abstract class BaseMessageDialog extends NoTitleDialogFragment
        implements View.OnClickListener {

    protected DialogSingleMessageBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.dialog_single_message,
                container,
                false);

        mBinding.setTitle(getDialogTitle());
        mBinding.setMessage(getDialogMessage());

        mBinding.setNegativeAction(getNegativeActionText());
        mBinding.setPositiveAction(getPositiveActionText());

        mBinding.positiveButton.setOnClickListener(this);
        mBinding.negativeButton.setOnClickListener(this);

        mBinding.setCancelable(isCancelable());

        return mBinding.getRoot();
    }


    protected String getNegativeActionText() {
        return requireContext().getString(R.string.cancel);
    }


    protected String getPositiveActionText() {
        return requireContext().getString(R.string.okay);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.negative_button) {
            dismissAllowingStateLoss();
        } else if (view.getId() == R.id.positive_button) {
            onPositiveButtonClicked(view);
        }
    }

    protected abstract String getDialogMessage();

    protected abstract String getDialogTitle();

    @SuppressWarnings("UnusedParameters")
    protected abstract void onPositiveButtonClicked(View view);
}
