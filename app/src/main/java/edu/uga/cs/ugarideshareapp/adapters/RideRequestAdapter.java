package edu.uga.cs.ugarideshareapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.ugarideshareapp.models.Ride;
import edu.uga.cs.ugarideshareapp.R;

public class RideRequestAdapter extends RecyclerView.Adapter<RideRequestAdapter.RequestViewHolder> {

    private List<Ride> requests;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(Ride ride);
    }

    public RideRequestAdapter(List<Ride> requests, OnRequestClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Ride ride = requests.get(position);
        holder.textRideInfo.setText(ride.getFromLocation() + " → " + ride.getToLocation() + "\n" + ride.getDateTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestClick(ride);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textRideInfo;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textRideInfo = itemView.findViewById(R.id.textRideInfo);
        }
    }
}
