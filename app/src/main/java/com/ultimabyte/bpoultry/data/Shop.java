package com.ultimabyte.bpoultry.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "shops")
public class Shop {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public int id;


    @ColumnInfo(name = "name")
    @SerializedName("name")
    public String name;


}
