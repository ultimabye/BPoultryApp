package com.ultimabyte.bpoultry;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ultimabyte.bpoultry.api.ApiRequestHandler;
import com.ultimabyte.bpoultry.api.ApiRequestType;
import com.ultimabyte.bpoultry.api.GenericApiResponse;
import com.ultimabyte.bpoultry.api.RestService;
import com.ultimabyte.bpoultry.data.BPoultryDB;
import com.ultimabyte.bpoultry.data.BPoultryRepo;
import com.ultimabyte.bpoultry.data.Shop;
import com.ultimabyte.bpoultry.databinding.ActivitySubmitCollectionBinding;
import com.ultimabyte.bpoultry.utils.ErrorUtils;
import com.ultimabyte.bpoultry.utils.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SubmitCollectionActivity extends BaseActivity {

    private static final String TAG = SubmitCollectionActivity.class.getSimpleName();


    private ActivitySubmitCollectionBinding mBinding;

    private ShopsViewModel mViewModel;

    private ShopsAdapter mAdapter;

    private Shop mSelectedShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_collection);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_submit_collection);
        mAdapter = new ShopsAdapter(this, new ArrayList<>());
        mBinding.spinnerOptions.setAdapter(mAdapter);
        mBinding.spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Shop shop = mAdapter.getItem(position);
                mSelectedShop = shop;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mBinding.buttonSubmit.setOnClickListener(v -> {
            if (mBinding.editTextNumber.getText().length() == 0) {
                showShortToast("Please enter a weight.");
                return;
            }


            if (mSelectedShop == null) {
                showShortToast("Please select a shop.");
                return;
            }

            try {
                int weight = Integer.parseInt(mBinding.editTextNumber.getText().toString());
                submitCollection(mSelectedShop.id, AppSettings.getUserId(SubmitCollectionActivity.this), weight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ShopsViewModel.class);
        //get all messages and observe for changes.
        subscribeUi(mViewModel.getMessages());
    }


    private void subscribeUi(LiveData<List<Shop>> liveData) {
        final boolean[] forceRefresh = {true};
        // Update the list when the data changes
        liveData.observe(this, items -> {
            boolean needsRefresh = forceRefresh[0];
            if (items != null) {
                mBinding.setIsLoading(false);
                //update adapter.
                mAdapter.setShops(items);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
            Logger.d(TAG, "needsRefresh=" + needsRefresh);
            if (needsRefresh) {
                forceRefresh[0] = false;
                //submit a pending runnable to be executed when it becomes visible.
                runOnResume(() -> fetchShops(forceRefresh));
            } else {
                //hide loading indicator.
                mBinding.setIsLoading(false);
            }
        });
    }


    private void fetchShops(boolean[] forceRefresh) {
        mBinding.setIsLoading(true);
        //load page.
        //enqueue api call.
        ApiRequestHandler<List<Shop>> apiHandler = new ApiRequestHandler<>();
        apiHandler.sendRequest(ApiRequestType.fetchShops,
                RestService.getAPI(this).shops(AppSettings.getUserId(this)));
    }


    private void submitCollection(int shopId, String driverId, int weight) {
        mBinding.setIsLoading(true);
        //load page.
        //enqueue api call.
        ApiRequestHandler<Collection> apiHandler = new ApiRequestHandler<>();
        apiHandler.sendRequest(ApiRequestType.submitCollection,
                RestService.getAPI(this).submitCollection(shopId, driverId, weight));
    }


    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onApiResponse(GenericApiResponse<List<?>> response) {
        if (response.getRequestType() == ApiRequestType.fetchShops) {
            //hide loading indicator.
            mBinding.setIsLoading(false);
            if (response.isSuccessful()) {
                //get messages response.
                List<Shop> shopsResponse = (List<Shop>) response.getResponse().body();
                if (shopsResponse != null) {
                    //save messages to database.
                    BPoultryDB database = BPoultry.shared().getDatabase();
                    BPoultryRepo.shared().saveShops(shopsResponse);
                }
            } else {
                //handle error.
                handleFailedResponse(response, false, response.getRequestType());
            }
        } else if (response.getRequestType() == ApiRequestType.submitCollection) {
            //hide loading indicator.
            mBinding.setIsLoading(false);
            if (response.isSuccessful()) {
                //get messages response.
                Collection cResponse = (Collection) response.getResponse().body();
                if (cResponse != null) {
                    BPoultry.shared().onMain(new Runnable() {
                        @Override
                        public void run() {
                            //save messages to database.
                            ErrorDialog.newInstance("Success", "Collection submitted successfully.")
                                    .show(getSupportFragmentManager(), ErrorDialog.class.getSimpleName());
                            mBinding.editTextNumber.setText("");
                        }
                    });
                }
            } else {
                //handle error.
                handleFailedResponse(response, false, response.getRequestType());
            }
        }
    }
}
