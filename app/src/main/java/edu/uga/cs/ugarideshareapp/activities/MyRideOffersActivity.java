package edu.uga.cs.ugarideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.adapters.RidesAdapter;
import edu.uga.cs.ugarideshareapp.models.Ride;

public class MyRideOffersActivity extends AppCompatActivity implements RidesAdapter.OnRideClickListener {
    private RecyclerView recyclerView;
    private RidesAdapter ridesAdapter;
    private List<Ride> allRides = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ride_offers);

        recyclerView = findViewById(R.id.recyclerViewMyRideOffers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchRides();

    }

    private void fetchRides() {
        DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = currentUser.getUid();

        ridesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allRides.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ride ride = snapshot.getValue(Ride.class);
                    if (ride != null && currentUserId.equals(ride.getUserId())) { // Only add user's rides
                        ride.setId(snapshot.getKey()); // Set the ride ID
                        allRides.add(ride);
                    }
                }

                ridesAdapter = new RidesAdapter(allRides, MyRideOffersActivity.this, RidesAdapter.MODE_EDIT_DELETE);
                recyclerView.setAdapter(ridesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyRideOffersActivity.this, "Failed to load rides.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public void onRideClick(Ride ride) {
        // Handle the accept ride logic here
    }

    @Override
    public void onEditRideClick(Ride ride) {
        Toast.makeText(this, "Editing ride: " + ride.getId(), Toast.LENGTH_SHORT).show();
        // Launch an activity or dialog to edit the ride
        Intent intent = new Intent(this, EditRideActivity.class);
        intent.putExtra("rideId", ride.getId());  // Pass ride ID to the edit screen
        startActivity(intent);
    }

    @Override
    public void onDeleteRideClick(Ride ride) {
        // Confirm and delete the ride from Firebase
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("rides").child(ride.getId());
        rideRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ride deleted successfully", Toast.LENGTH_SHORT).show();
                    // Refresh the list
                    fetchRides();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete ride", Toast.LENGTH_SHORT).show();
                });
    }
}

