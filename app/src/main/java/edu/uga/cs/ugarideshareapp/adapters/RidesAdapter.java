package edu.uga.cs.ugarideshareapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.ugarideshareapp.models.Ride;
import edu.uga.cs.ugarideshareapp.R;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RideViewHolder> {

    private List<Ride> rides;
    private OnRideClickListener listener;
    private boolean showingAcceptedRides = false; // NEW

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
    }

    public RidesAdapter(List<Ride> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    public void setShowingAcceptedRides(boolean value) {
        this.showingAcceptedRides = value;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.textRideInfo.setText(ride.getFromLocation() + " → " + ride.getToLocation() + "\n" + ride.getDateTime());

        if (showingAcceptedRides) {
            holder.buttonAction.setText("Confirm Ride");
        } else {
            holder.buttonAction.setText("Accept Ride");
        }

        holder.buttonAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRideClick(ride);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView textRideInfo;
        Button buttonAction;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            textRideInfo = itemView.findViewById(R.id.textRideInfo);
            buttonAction = itemView.findViewById(R.id.buttonAction);
        }
    }
}
