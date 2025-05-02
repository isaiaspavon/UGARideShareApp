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

/**
 * MyRideOffersActivity displays a list of all ride offers or requests posted by the current user.
 * Users can choose to edit or delete their own rides using this screen.
 * Firebase Realtime Database is used to retrieve and manage the user's rides.
 */
public class MyRideOffersActivity extends AppCompatActivity implements RidesAdapter.OnRideClickListener {
    private RecyclerView recyclerView;
    private RidesAdapter ridesAdapter;
    private List<Ride> allRides = new ArrayList<>();

    /**
     * Initializes the layout, sets up the RecyclerView, and loads the current user's ride offers.
     *
     * @param savedInstanceState the previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ride_offers);

        recyclerView = findViewById(R.id.recyclerViewMyRideOffers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchRides();

    } // onCreate

    /**
     * Fetches all rides from Firebase that belong to the current user.
     * Filters results based on the authenticated user's UID.
     * Populates the RecyclerView using a custom RidesAdapter in edit/delete mode.
     */
    private void fetchRides() {
        DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        } // if
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
                    } // if
                } // for

                ridesAdapter = new RidesAdapter(allRides, MyRideOffersActivity.this, RidesAdapter.MODE_EDIT_DELETE);
                recyclerView.setAdapter(ridesAdapter);
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyRideOffersActivity.this, "Failed to load rides.", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });
    } // fetchRides

    /**
     * Unused in this context. Required by OnRideClickListener.
     *
     * @param ride the clicked ride
     */
    @Override
    public void onRideClick(Ride ride) {
        // Handle the accept ride logic here
    } // onRideClick

    /**
     * Opens EditRideActivity to allow the user to modify the selected ride.
     *
     * @param ride the ride selected for editing
     */
    @Override
    public void onEditRideClick(Ride ride) {
        Toast.makeText(this, "Editing ride: " + ride.getId(), Toast.LENGTH_SHORT).show();
        // Launch an activity or dialog to edit the ride
        Intent intent = new Intent(this, EditRideActivity.class);
        intent.putExtra("rideId", ride.getId());  // Pass ride ID to the edit screen
        startActivity(intent);
    } // onEditRideClick

    /**
     * Deletes the selected ride from Firebase and refreshes the list.
     *
     * @param ride the ride selected for deletion
     */
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
    } // onDeleteRideClick
} // MyRideOffersActivity