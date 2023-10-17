package com.example.etrainbooking;

import com.example.etrainbooking.TrainController.Schedule;
import com.example.etrainbooking.UserController.User;

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("/api/Schedules")
    Call<List<Schedule>> getSchedules();

    @POST("/api/auth/login")
    Call<JSONObject> login(@Body RequestBody jsonObject);

    @POST("/api/Users/addUsers")
    Call<User> registerUser(@Body User user);

    @GET("/api/User/getUserByNIC")
    Call<User> getUserByNIC(@Query("nic") String nic);

    @PUT("/api/User/updateUser")
    Call<User> updateUser(@Body User user);

}
