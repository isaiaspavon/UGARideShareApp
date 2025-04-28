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

public class RidesRequestListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RidesAdapter adapter;
    private List<Ride> requestList = new ArrayList<>();
    private DatabaseReference ridesRef;
    private String currentUserUid;

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
            }

            @Override
            public void onEditRideClick(Ride ride) {
                // not needed here
            }

            @Override
            public void onDeleteRideClick(Ride ride) {
                // not needed here
            }
        }, RidesAdapter.MODE_ACCEPT); // <- IMPORTANT: setting mode to accept

        recyclerView.setAdapter(adapter);

        ridesRef = FirebaseDatabase.getInstance().getReference("rides");

        loadRequestsFromFirebase();
    }

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
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RidesRequestListActivity.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRequestClicked(Ride ride) {
        Intent intent = new Intent(this, RideDetailsActivity.class);
        intent.putExtra("rideId", ride.getId());
        startActivity(intent);
    }
}
