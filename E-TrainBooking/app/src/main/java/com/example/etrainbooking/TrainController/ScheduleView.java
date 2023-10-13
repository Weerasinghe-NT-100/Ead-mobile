package com.example.etrainbooking.TrainController;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.etrainbooking.APIClient;
import com.example.etrainbooking.APIInterface;
import com.example.etrainbooking.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private APIInterface apiInterface;
    private ScheduleAdapter scheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiInterface = APIClient.getClient().create(APIInterface.class);
        scheduleAdapter = new ScheduleAdapter();

        recyclerView.setAdapter(scheduleAdapter);

        // Fetch schedules from the API
        Call<List<Schedule>> call = apiInterface.getSchedules();
        call.enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                if (response.isSuccessful()) {
                    List<Schedule> schedules = response.body();
                    scheduleAdapter.setSchedules(schedules);
                } else {
                    // Handle error response
                    Toast.makeText(ScheduleView.this, "Error fetching schedules", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                Log.e("API_CALL", "API call failed");
                Log.e("API_CALL_URL", call.request().url().toString());
                Log.e("API_CALL_ERROR", t.getMessage());
                // Handle network or other errors
                Toast.makeText(ScheduleView.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
