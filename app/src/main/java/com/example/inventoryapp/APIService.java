package com.example.inventoryapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface APIService {
    @GET("items")
    Call<List<InventoryItem>> getAllItems();

    //@GET("items/{upc}")
    //Call<InventoryItem> getItem(@Path("upc") String upc);

    @POST("items")
    Call<Void> addItem(@Body InventoryItem item);

    @PUT("items/{upc}")
    Call<Void> updateItem(@Path("upc") String upc, @Body InventoryItem item);

    @DELETE("items/{upc}")
    Call<Void> deleteItem(@Path("upc") String upc);
}
