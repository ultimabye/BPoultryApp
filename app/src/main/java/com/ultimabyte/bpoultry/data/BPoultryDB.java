package com.ultimabyte.bpoultry.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.ultimabyte.bpoultry.Collection;
import com.ultimabyte.bpoultry.data.converter.DateConverter;

@SuppressWarnings("WeakerAccess")
@Database(
        entities = {
                Shop.class,
                Collection.class
        },
        version = 1)

@TypeConverters({
        DateConverter.class
})
public abstract class BPoultryDB extends RoomDatabase {


    private static final String TAG = BPoultryDB.class.getSimpleName();

    private static BPoultryDB sInstance;

    @VisibleForTesting
    private static final String DATABASE_NAME = "clearstream-db";

    public abstract ShopsDao shopsDao();

    public abstract CollectionsDao collectionsDao();


    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static BPoultryDB shared(final Context context) {
        synchronized (BPoultryDB.class) {
            if (sInstance == null) {
                sInstance = buildDatabase(context.getApplicationContext());
                sInstance.updateDatabaseCreated(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new shared of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static BPoultryDB buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, BPoultryDB.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        // notify that the database was created and it's ready to be used
                        BPoultryDB.shared(appContext).setDatabaseCreated();
                    }
                })
                .build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
