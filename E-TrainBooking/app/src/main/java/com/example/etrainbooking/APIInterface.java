package com.example.etrainbooking;

import com.example.etrainbooking.TrainController.Schedule;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("/api/Schedules")
    Call<List<Schedule>> getSchedules();

}
