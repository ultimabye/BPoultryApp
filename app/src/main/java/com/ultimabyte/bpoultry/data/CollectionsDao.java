package com.ultimabyte.bpoultry.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ultimabyte.bpoultry.Collection;

import java.util.List;

@Dao
public interface CollectionsDao {

    @Query("SELECT * FROM collections ORDER BY created_at DESC")
    LiveData<List<Collection>> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Collection> items);

    @Query("DELETE FROM collections")
    int deleteAll();

}
