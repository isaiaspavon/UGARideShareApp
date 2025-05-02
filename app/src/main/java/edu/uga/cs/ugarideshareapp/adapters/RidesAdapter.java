package edu.uga.cs.ugarideshareapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.models.Ride;

/**
 * RidesAdapter is a flexible RecyclerView adapter for displaying ride items.
 * It supports three interaction modes:
 * - MODE_ACCEPT: Accepting a ride (rider or driver)
 * - MODE_EDIT_DELETE: Editing or deleting a posted ride
 * - MODE_CONFIRM: Confirming that a ride took place
 */
public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RideViewHolder> {

    public static final int MODE_ACCEPT = 0;
    public static final int MODE_EDIT_DELETE = 1;

    public static final int MODE_CONFIRM = 2;

    private List<Ride> rideList;
    private OnRideClickListener listener;
    private int mode;

    /**
     * Interface to handle ride interactions like accept, edit, or delete.
     */
    public interface OnRideClickListener {
        void onRideClick(Ride ride);
        void onEditRideClick(Ride ride);
        void onDeleteRideClick(Ride ride);
    } // onRideClickListener

    /**
     * Constructs a new RidesAdapter.
     *
     * @param rideList list of rides to display
     * @param listener callback for ride actions
     * @param mode     the interaction mode (accept/edit/confirm)
     */
    public RidesAdapter(List<Ride> rideList, OnRideClickListener listener, int mode) {
        this.rideList = rideList;
        this.listener = listener;
        this.mode = mode;
    } // RidesAdpater

    /**
     * Inflates the ride item layout for each item.
     *
     * @param parent   parent ViewGroup
     * @param viewType type of view (not used)
     * @return a new RideViewHolder
     */
    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    } // onCreateViewHolder

    /**
     * Binds ride data to the view and configures button visibility and behavior
     * based on the current mode (accept/edit/delete/confirm).
     *
     * @param holder   view holder to bind
     * @param position item position
     */
    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        holder.textRideInfo.setText(ride.getFromLocation() + " → " + ride.getToLocation() + "\n" + ride.getDateTime());

        if (mode == MODE_ACCEPT) {
            holder.buttonAccept.setVisibility(View.VISIBLE);
            holder.buttonEdit.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);

            holder.buttonAccept.setText("Accept Ride");
            holder.buttonAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRideClick(ride);
                } // if
            });

        } else if (mode == MODE_EDIT_DELETE) {
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonEdit.setVisibility(View.VISIBLE);
            holder.buttonDelete.setVisibility(View.VISIBLE);

            holder.buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditRideClick(ride);
                } // if
            });

            holder.buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteRideClick(ride);
                } // if
            });

        } else if (mode == MODE_CONFIRM) { // <<< ADD THIS NEW BLOCK
            holder.buttonAccept.setVisibility(View.VISIBLE);
            holder.buttonEdit.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);

            holder.buttonAccept.setText("Confirm Ride");
            holder.buttonAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRideClick(ride);
                } // if
            });
        } // if
    } // onBindViewHolder

    /**
     * Returns the number of ride items in the list.
     *
     * @return item count
     */
    @Override
    public int getItemCount() {
        return rideList.size();
    } // getItemCount

    /**
     * ViewHolder class that represents a single ride item.
     */
    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView textRideInfo;
        Button buttonAccept;
        Button buttonEdit;
        Button buttonDelete;


        public RideViewHolder(@NonNull View itemView) {
            super(itemView);

            textRideInfo = itemView.findViewById(R.id.textRideInfo);
            buttonAccept = itemView.findViewById(R.id.buttonAction);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        } // RideViewHolder
    } // RideViewHolder
} // RidesAdapter
