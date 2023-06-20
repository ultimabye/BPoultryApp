package com.ultimabyte.bpoultry.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;


import com.ultimabyte.bpoultry.BPoultry;
import com.ultimabyte.bpoultry.Collection;

import java.util.ArrayList;
import java.util.List;


public class BPoultryRepo {

    private static final String TAG = BPoultryRepo.class.getSimpleName();

    private static BPoultryRepo sInstance;

    private final BPoultryDB mDatabase;
    private MediatorLiveData<List<Shop>> mObservableShops;
    private MediatorLiveData<List<Collection>> mObservableCollections;


    private BPoultryRepo(final BPoultryDB database) {
        mDatabase = database;
    }


    public static BPoultryRepo shared() {
        synchronized (BPoultryRepo.class) {
            if (sInstance == null) {
                sInstance = new BPoultryRepo(BPoultry.shared().getDatabase());
            }
        }
        return sInstance;
    }


    /**
     * Get the list of messages from the database and get notified when the data changes.
     */
    public LiveData<List<Shop>> getShops() {
        if (mObservableShops == null) {
            loadShops();
        }
        return mObservableShops;
    }


    private void loadShops() {
        mObservableShops = new MediatorLiveData<>();
        mObservableShops.addSource(mDatabase.shopsDao().loadAllShops(),
                productEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableShops.postValue(productEntities);
                    }
                });
    }


    public void saveShops(List<Shop> items) {
        if (items != null && items.size() > 0) {
            mDatabase.runInTransaction(() -> {
                mDatabase.shopsDao().insertAll(items);
            });
        } else {
            mObservableShops.postValue(new ArrayList<>());
        }
    }


    public LiveData<List<Collection>> getCollections() {
        if (mObservableCollections == null) {
            loadCollections();
        }
        return mObservableCollections;
    }


    private void loadCollections() {
        mObservableCollections = new MediatorLiveData<>();
        mObservableCollections.addSource(mDatabase.collectionsDao().loadAll(),
                productEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableCollections.postValue(productEntities);
                    }
                });
    }


    public void saveCollections(List<Collection> items) {
        if (items != null && items.size() > 0) {
            mDatabase.runInTransaction(() -> {
                mDatabase.collectionsDao().insertAll(items);
            });
        } else {
            mObservableCollections.postValue(new ArrayList<>());
        }
    }


    public void clearAllData() {
        BPoultry.shared().async(() -> {
            try {
                mDatabase.shopsDao().deleteAll();
                mDatabase.collectionsDao().deleteAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
