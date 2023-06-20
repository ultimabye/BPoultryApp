package com.ultimabyte.bpoultry;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.ultimabyte.bpoultry.data.BPoultryRepo;
import com.ultimabyte.bpoultry.data.Shop;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class CollectionsViewModel extends AndroidViewModel {


    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<Collection>> mObservableItems;

    public CollectionsViewModel(@NonNull Application application) {
        super(application);
        mObservableItems = new MediatorLiveData<>();
        BPoultryRepo repository = ((BPoultry) application).getRepository();
        LiveData<List<Collection>> items = repository.getCollections();
        // observe the changes of the products from the database and forward them
        mObservableItems.addSource(items, mObservableItems::setValue);
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<Collection>> getCollections() {
        return mObservableItems;
    }
}
