package edu.uga.cs.ugarideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

/**
 * RidesRequestListActivity displays a list of ride requests (as opposed to ride offers).
 * Drivers can browse these requests and accept a ride if they are available.
 */
public class RidesRequestListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RidesAdapter adapter;
    private List<Ride> requestList = new ArrayList<>();
    private DatabaseReference ridesRef;
    private String currentUserUid;

    /**
     * Initializes the RecyclerView and sets up Firebase listeners to load ride requests.
     *
     * @param savedInstanceState previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_request_list);

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter = new RidesAdapter(requestList, new RidesAdapter.OnRideClickListener() {
            @Override
            public void onRideClick(Ride ride) {
                onRequestClicked(ride); // Handle accept ride click
            } // onRideClick

            @Override
            public void onEditRideClick(Ride ride) {
                // not needed here
            } // onEditRideClick

            @Override
            public void onDeleteRideClick(Ride ride) {
                // not needed here
            } // onDeleteRideClick
        }, RidesAdapter.MODE_ACCEPT); // <- IMPORTANT: setting mode to accept

        recyclerView.setAdapter(adapter);

        ridesRef = FirebaseDatabase.getInstance().getReference("rides");

        loadRequestsFromFirebase();
    } // onCreate

    /**
     * Loads all available ride requests (not offers) from Firebase.
     * Filters only "available" requests and adds them to the list.
     */
    private void loadRequestsFromFirebase() {
        ridesRef.orderByChild("dateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot rideSnapshot : snapshot.getChildren()) {
                    Ride ride = rideSnapshot.getValue(Ride.class);
                    if (ride != null && !ride.isOffer() && "available".equals(ride.getStatus())) {
                        ride.setId(rideSnapshot.getKey());
                        requestList.add(ride);
                    } // if
                } // for
                adapter.notifyDataSetChanged();
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RidesRequestListActivity.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });
    } // loadRequestsFromFirebase

    /**
     * Called when a user taps on a ride request. Navigates to RideDetailsActivity for acceptance.
     *
     * @param ride the ride request clicked
     */
    private void onRequestClicked(Ride ride) {
        Intent intent = new Intent(this, RideDetailsActivity.class);
        intent.putExtra("rideId", ride.getId());
        startActivity(intent);
    } // onRequestClicked
} // RidesRequestListActivity