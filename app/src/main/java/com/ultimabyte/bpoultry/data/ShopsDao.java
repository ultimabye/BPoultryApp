package com.ultimabyte.bpoultry.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface ShopsDao {

    @Query("SELECT * FROM shops ORDER BY name DESC")
    LiveData<List<Shop>> loadAllShops();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Shop> items);

    @Query("DELETE FROM shops")
    int deleteAll();

}
