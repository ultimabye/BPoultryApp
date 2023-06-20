package com.ultimabyte.bpoultry;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CollectionListResponse {

    @SerializedName("data")
    public List<Collection> collection;
}
