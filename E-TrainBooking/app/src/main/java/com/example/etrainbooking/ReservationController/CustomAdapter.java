package com.example.etrainbooking.ReservationController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.etrainbooking.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Reservation> reservations;

    public CustomAdapter(Context context, ArrayList<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Reservation reservation = reservations.get(position);

        holder.booking_id_txt.setText(String.valueOf(reservation.getTid()));
        holder.txt_train.setText(reservation.getTrain());
        holder.txt_start.setText("Start: "+ reservation.getStart());
        holder.txt_destination.setText("Destination: "+ reservation.getDestination());
        holder.txt_date.setText(reservation.getDate());

        // Set an item click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click here, e.g., open details activity
                Intent intent = new Intent(context, ResViewDetails.class);
                intent.putExtra("id", String.valueOf(reservation.getTid()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView booking_id_txt, txt_train, txt_start, txt_destination, txt_date;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            booking_id_txt = itemView.findViewById(R.id.booking_id_txt);
            txt_train = itemView.findViewById(R.id.txt_train);
            txt_start = itemView.findViewById(R.id.txt_start);
            txt_destination = itemView.findViewById(R.id.txt_destination);
            txt_date = itemView.findViewById(R.id.txt_date);
        }
    }
}
