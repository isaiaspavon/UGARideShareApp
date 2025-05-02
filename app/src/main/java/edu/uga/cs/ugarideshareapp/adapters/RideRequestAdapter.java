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

/**
 * RideRequestAdapter handles displaying ride request items in a RecyclerView.
 * Each item shows the ride's origin, destination, and date/time.
 * Clicking on an item triggers a callback via OnRequestClickListener.
 */
public class RideRequestAdapter extends RecyclerView.Adapter<RideRequestAdapter.RequestViewHolder> {

    private List<Ride> requests;
    private OnRequestClickListener listener;

    /**
     * Interface for handling item click events.
     */
    public interface OnRequestClickListener {
        void onRequestClick(Ride ride);
    } // OnRequestClickListener

    /**
     * Constructor for RideRequestAdapter.
     *
     * @param requests list of ride requests to display
     * @param listener listener for handling ride request clicks
     */
    public RideRequestAdapter(List<Ride> requests, OnRequestClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    } // RideRequestAdapter

    /**
     * Inflates the view for each ride request item.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type
     * @return a new RequestViewHolder instance
     */
    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RequestViewHolder(view);
    } // onCreateViewHolder

    /**
     * Binds ride request data to the view holder.
     *
     * @param holder   the RequestViewHolder
     * @param position the current position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Ride ride = requests.get(position);
        holder.textRideInfo.setText(ride.getFromLocation() + " → " + ride.getToLocation() + "\n" + ride.getDateTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestClick(ride);
            } // if
        });
    } // onBindViewHolder

    /**
     * Returns the total number of ride request items.
     *
     * @return the size of the request list
     */
    @Override
    public int getItemCount() {
        return requests.size();
    } // getItemCount

    /**
     * ViewHolder class for holding each ride request item.
     */
    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textRideInfo;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textRideInfo = itemView.findViewById(R.id.textRideInfo);
        } // RequestViewHolder
    } // RequestViewHolder
} // RideRequestAdpater