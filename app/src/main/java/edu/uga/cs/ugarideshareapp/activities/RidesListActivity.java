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

public class RidesListActivity extends AppCompatActivity {

    private RecyclerView ridesRecyclerView;
    private List<Ride> rideList;
    private RidesAdapter ridesAdapter;
    private DatabaseReference ridesRef;
    private String currentUserUid;

    private boolean showingAcceptedRides = false; // NEW

    private Button buttonToggleView; // NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_list);

        ridesRecyclerView = findViewById(R.id.ridesRecyclerView);
        ridesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonToggleView = findViewById(R.id.buttonToggleView); // Make sure to add this button in your layout

        rideList = new ArrayList<>();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ridesAdapter = new RidesAdapter(rideList, ride -> {
            if (showingAcceptedRides) {
                // If in accepted rides view, go to confirm ride
                Intent intent = new Intent(RidesListActivity.this, ConfirmRideActivity.class);
                intent.putExtra("rideId", ride.getId());
                startActivity(intent);
            } else {
                // Otherwise go to ride details to accept ride
                Intent intent = new Intent(RidesListActivity.this, RideDetailsActivity.class);
                intent.putExtra("rideId", ride.getId());
                startActivity(intent);
            }
        });
        ridesRecyclerView.setAdapter(ridesAdapter);

        ridesRef = FirebaseDatabase.getInstance().getReference("rides");

        loadRidesFromFirebase();

        buttonToggleView.setOnClickListener(v -> {
            showingAcceptedRides = !showingAcceptedRides;
            loadRidesFromFirebase();
            buttonToggleView.setText(showingAcceptedRides ? "View Available Rides" : "View Accepted Rides");
        });
    }

    private void loadRidesFromFirebase() {
        ridesRef.orderByChild("dateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                rideList.clear();
                for (DataSnapshot rideSnapshot : snapshot.getChildren()) {
                    Ride ride = rideSnapshot.getValue(Ride.class);
                    if (ride != null) {
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
                ridesAdapter.setShowingAcceptedRides(showingAcceptedRides);
                ridesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RidesListActivity.this, "Failed to load rides.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
