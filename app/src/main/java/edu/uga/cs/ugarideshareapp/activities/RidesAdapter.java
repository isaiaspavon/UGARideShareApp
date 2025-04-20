package edu.uga.cs.ugarideshareapp.activities;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RideViewHolder> {

    private List<Ride> rides;

    public RidesAdapter(List<Ride> rides) {
        this.rides = rides;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.text1.setText(ride.getFromLocation() + " → " + ride.getToLocation());
        holder.text2.setText(ride.getDateTime());
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    class RideViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
