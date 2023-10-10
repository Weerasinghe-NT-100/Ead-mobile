package com.example.etrainbooking;



import com.example.etrainbooking.ReservationController.Reservation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;



public interface APIInterface {


    @POST("/api/Shed")
    Call<Reservation> createUser(@Body Reservation shed);

//    @GET("/api/Shed")
//    Call<UserList> doGetShedList(@Query("page") String page);


}
