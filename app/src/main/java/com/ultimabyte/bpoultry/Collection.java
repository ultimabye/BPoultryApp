package com.ultimabyte.bpoultry;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.ultimabyte.bpoultry.data.Shop;

@Entity(tableName = "collections")
public class Collection {

    @PrimaryKey
    @ColumnInfo(name = "c_id")
    @SerializedName("id")
    public int id;


    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    public String createdAt;


    @ColumnInfo(name = "collection_amount")
    @SerializedName("collection_amount")
    public int collectionAmount;


    @SerializedName("shop")
    @Embedded
    public Shop shop;
}
