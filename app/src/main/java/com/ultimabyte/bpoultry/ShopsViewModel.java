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
public class ShopsViewModel extends AndroidViewModel {


    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<Shop>> mObservableItems;

    public ShopsViewModel(@NonNull Application application) {
        super(application);
        mObservableItems = new MediatorLiveData<>();
        BPoultryRepo repository = ((BPoultry) application).getRepository();
        LiveData<List<Shop>> shops = repository.getShops();
        // observe the changes of the products from the database and forward them
        mObservableItems.addSource(shops, mObservableItems::setValue);
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<Shop>> getMessages() {
        return mObservableItems;
    }
}
