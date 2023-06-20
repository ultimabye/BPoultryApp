package com.ultimabyte.bpoultry;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ultimabyte.bpoultry.api.ApiRequestHandler;
import com.ultimabyte.bpoultry.api.ApiRequestType;
import com.ultimabyte.bpoultry.api.GenericApiResponse;
import com.ultimabyte.bpoultry.api.RestService;
import com.ultimabyte.bpoultry.data.BPoultryDB;
import com.ultimabyte.bpoultry.data.BPoultryRepo;
import com.ultimabyte.bpoultry.databinding.ActivityCollectionsListBinding;
import com.ultimabyte.bpoultry.utils.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CollectionsListActivity extends BaseActivity {

    private static final String TAG = CollectionsListActivity.class.getSimpleName();

    private ActivityCollectionsListBinding mBinding;

    private CollectionsAdapter mAdapter;
    private CollectionsViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityCollectionsListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.toolbar);

        mAdapter = new CollectionsAdapter(new ArrayList<>());
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setAdapter(mAdapter);

        mBinding.fab.setOnClickListener((View.OnClickListener) view -> {
            startActivity(new Intent(CollectionsListActivity.this, SubmitCollectionActivity.class));
        });
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CollectionsViewModel.class);
        //get all messages and observe for changes.
        subscribeUi(mViewModel.getCollections());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collections, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void logout() {
        BPoultry.shared().async(() -> {
            BPoultry.shared().getRepository().clearAllData();
            AppSettings.clearAppSettings(CollectionsListActivity.this);
            BPoultry.shared().onMain(() -> {
                Intent login = new Intent(CollectionsListActivity.this, LoginActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
            });
        });
    }


    private void subscribeUi(LiveData<List<Collection>> liveData) {
        final boolean[] forceRefresh = {true};
        // Update the list when the data changes
        liveData.observe(this, items -> {
            boolean needsRefresh = forceRefresh[0];
            if (items != null) {
                mBinding.setIsLoading(false);
                //update adapter.
                mAdapter.setItems(items);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
            Logger.d(TAG, "needsRefresh=" + needsRefresh);
            if (needsRefresh) {
                forceRefresh[0] = false;
                //submit a pending runnable to be executed when it becomes visible.
                runOnResume(() -> fetchCollections(forceRefresh));
            } else {
                //hide loading indicator.
                mBinding.setIsLoading(false);
            }
        });
    }


    private void fetchCollections(boolean[] forceRefresh) {
        mBinding.setIsLoading(true);
        //load page.
        //enqueue api call.
        ApiRequestHandler<CollectionListResponse> apiHandler = new ApiRequestHandler<>();
        apiHandler.sendRequest(ApiRequestType.fetchCollections,
                RestService.getAPI(this).collections());
    }


    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onApiResponse(GenericApiResponse<CollectionListResponse> response) {
        if (response.getRequestType() == ApiRequestType.fetchCollections) {
            //hide loading indicator.
            mBinding.setIsLoading(false);
            if (response.isSuccessful()) {
                //get messages response.
                CollectionListResponse collectionListResponse = response.getResponse().body();
                List<Collection> itemResponse = collectionListResponse.collection;
                if (itemResponse != null) {
                    //save messages to database.
                    BPoultryDB database = BPoultry.shared().getDatabase();
                    BPoultryRepo.shared().saveCollections(itemResponse);
                }
            } else {
                //handle error.
                handleFailedResponse(response, false, response.getRequestType());
            }
        }
    }
}