package edu.uga.cs.ugarideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.adapters.RidesAdapter;
import edu.uga.cs.ugarideshareapp.models.Ride;

public class RidesListActivity extends AppCompatActivity implements RidesAdapter.OnRideClickListener {

    private RecyclerView ridesRecyclerView;
    private List<Ride> rideList;
    private RidesAdapter ridesAdapter;
    private DatabaseReference ridesRef;
    private String currentUserUid;

    private boolean showingAcceptedRides = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_list);

        ridesRecyclerView = findViewById(R.id.ridesRecyclerView);
        ridesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //buttonToggleView = findViewById(R.id.buttonToggleView);

        rideList = new ArrayList<>();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ridesRef = FirebaseDatabase.getInstance().getReference("rides");

        setupAdapter();
        loadRidesFromFirebase();

    }

    private void setupAdapter() {
        int mode = showingAcceptedRides ? RidesAdapter.MODE_ACCEPT : RidesAdapter.MODE_ACCEPT;
        ridesAdapter = new RidesAdapter(rideList, this, mode);
        ridesRecyclerView.setAdapter(ridesAdapter);
    }

    private void loadRidesFromFirebase() {
        ridesRef.orderByChild("dateTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                rideList.clear();
                for (DataSnapshot rideSnapshot : snapshot.getChildren()) {
                    Ride ride = rideSnapshot.getValue(Ride.class);
                    if (ride != null && ride.isOffer() && "available".equals(ride.getStatus())) {
                        ride.setId(rideSnapshot.getKey());

                        if (showingAcceptedRides) {
                            if ("accepted".equals(ride.getStatus()) &&
                                    (currentUserUid.equals(ride.getDriverUid()) || currentUserUid.equals(ride.getRiderUid()))) {
                                rideList.add(ride);
                            }
                        } else {
                            if ("available".equals(ride.getStatus())) {
                                rideList.add(ride);
                            }
                        }
                    }
                }
                ridesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RidesListActivity.this, "Failed to load rides.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRideClick(Ride ride) {
        if (showingAcceptedRides) {
            // User clicks Confirm on accepted ride
            Intent intent = new Intent(RidesListActivity.this, ConfirmRideActivity.class);
            intent.putExtra("rideId", ride.getId());
            startActivity(intent);
        } else {
            // User clicks Accept on available ride
            Intent intent = new Intent(RidesListActivity.this, RideDetailsActivity.class);
            intent.putExtra("rideId", ride.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onEditRideClick(Ride ride) {
        // Not needed in this screen
    }

    @Override
    public void onDeleteRideClick(Ride ride) {
        // Not needed in this screen
    }
}
