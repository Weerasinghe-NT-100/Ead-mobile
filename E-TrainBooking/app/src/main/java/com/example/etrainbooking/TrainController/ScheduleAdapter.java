package com.example.etrainbooking.TrainController;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.etrainbooking.R;
import com.example.etrainbooking.ReservationController.ResViewDetails;
import com.example.etrainbooking.ReservationController.ReservationAdd;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> schedules;
    static Context context;

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        if (schedules != null) {
            Schedule schedule = schedules.get(position);
            holder.bind(schedule);
        }
    }

    @Override
    public int getItemCount() {
        return schedules != null ? schedules.size() : 0;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private TextView startTextView;
        private TextView endTextView;
        private TextView arrivalTextView;
        private TextView destinationTextView;
        private TextView dateTextView;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            startTextView = itemView.findViewById(R.id.scheduleStartTextView);
            endTextView = itemView.findViewById(R.id.scheduleEndTextView);
            arrivalTextView = itemView.findViewById(R.id.txt_arrival);
            destinationTextView = itemView.findViewById(R.id.txt_departure);
            dateTextView = itemView.findViewById(R.id.txt_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click here, e.g., open details activity
                    Intent intent = new Intent(context, ReservationAdd.class);
                    context.startActivity(intent);
                }
            });

        }

        void bind(Schedule schedule) {
            startTextView.setText("Start: " + schedule.getStart());
            endTextView.setText("Destination: " + schedule.getEnd());
            arrivalTextView.setText("Arrival Time: " + schedule.getArrivalTime());
            destinationTextView.setText("Departure Time: " + schedule.getDepartureTime());
            dateTextView.setText("Date: " + schedule.getDate());
        }
    }
}
