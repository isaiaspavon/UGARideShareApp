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

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RideViewHolder> {

    public static final int MODE_ACCEPT = 0;
    public static final int MODE_EDIT_DELETE = 1;

    public static final int MODE_CONFIRM = 2;

    private List<Ride> rideList;
    private OnRideClickListener listener;
    private int mode;

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
        void onEditRideClick(Ride ride);
        void onDeleteRideClick(Ride ride);
    }

    public RidesAdapter(List<Ride> rideList, OnRideClickListener listener, int mode) {
        this.rideList = rideList;
        this.listener = listener;
        this.mode = mode;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

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
                }
            });

        } else if (mode == MODE_EDIT_DELETE) {
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonEdit.setVisibility(View.VISIBLE);
            holder.buttonDelete.setVisibility(View.VISIBLE);

            holder.buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditRideClick(ride);
                }
            });

            holder.buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteRideClick(ride);
                }
            });

        } else if (mode == MODE_CONFIRM) { // <<< ADD THIS NEW BLOCK
            holder.buttonAccept.setVisibility(View.VISIBLE);
            holder.buttonEdit.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);

            holder.buttonAccept.setText("Confirm Ride");
            holder.buttonAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRideClick(ride);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return rideList.size();
    }

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
        }
    }
}
