package edu.uga.cs.ugarideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.models.Ride;

/**
 * RideDetailsActivity shows detailed information about a selected ride.
 * Depending on whether the ride is a request or an offer, the current user can accept it.
 * Points are transferred between driver and rider upon acceptance, and the ride is marked as accepted.
 */
public class RideDetailsActivity extends AppCompatActivity {

    private TextView textViewFrom, textViewTo, textViewDateTime, textViewType;
    private Button buttonAcceptRide;
    private DatabaseReference ridesRef;
    private String rideId;
    private Ride currentRide;

    /**
     * Initializes UI elements, loads ride data, and sets up the ride acceptance button.
     *
     * @param savedInstanceState previously saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        textViewFrom = findViewById(R.id.textViewFrom);
        textViewTo = findViewById(R.id.textViewTo);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        textViewType = findViewById(R.id.textViewType);
        buttonAcceptRide = findViewById(R.id.buttonAcceptRide);

        ridesRef = FirebaseDatabase.getInstance().getReference("rides");
        rideId = getIntent().getStringExtra("rideId");

        loadRideDetails();

        buttonAcceptRide.setOnClickListener(v -> acceptRide());
    } // onCreate

    /**
     * Loads the selected ride's details from Firebase and displays them.
     * Disables the accept button if the ride is already taken.
     */
    private void loadRideDetails() {
        ridesRef.child(rideId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentRide = snapshot.getValue(Ride.class);
                if (currentRide != null) {
                    textViewFrom.setText("From: " + currentRide.getFromLocation());
                    textViewTo.setText("To: " + currentRide.getToLocation());
                    textViewDateTime.setText("Date/Time: " + currentRide.getDateTime());
                    textViewType.setText(currentRide.isOffer() ? "Ride Offer" : "Ride Request");

                    if (!"available".equals(currentRide.getStatus())) {
                        buttonAcceptRide.setEnabled(false);
                        buttonAcceptRide.setText("Already Accepted");
                    } // if
                } // if
            } // onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RideDetailsActivity.this, "Failed to load ride details.", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });
    } // loadRideDetails

    /**
     * Accepts the selected ride and handles points transfer between users.
     * - If it's an offer, the current user becomes the rider and pays 50 points.
     * - If it's a request, the current user becomes the driver and earns 50 points.
     * - The ride is marked as accepted and removed from the database.
     */
    private void acceptRide() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (currentRide == null || rideId == null) return;

        DatabaseReference rideRef = ridesRef.child(rideId);

        if (currentRide.isOffer()) {
            // Rider accepting a ride offer
            rideRef.child("riderUid").setValue(currentUid);

            // Deduct 50 points from the rider (acceptor)
            DatabaseReference riderRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUid)
                    .child("points");

            riderRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer points = currentData.getValue(Integer.class);
                    if (points == null) {
                        currentData.setValue(0);
                    } else {
                        currentData.setValue(points - 50);
                    } // if
                    return Transaction.success(currentData);
                } // doTransaction

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                    // No action needed
                } // onComplete
            });

            // Award 50 points to the driver (who posted the offer)
            if (currentRide.getDriverUid() != null) {
                DatabaseReference driverRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(currentRide.getDriverUid())
                        .child("points");

                driverRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Integer points = currentData.getValue(Integer.class);
                        if (points == null) {
                            currentData.setValue(50);
                        } else {
                            currentData.setValue(points + 50);
                        } // if
                        return Transaction.success(currentData);
                    } // doTransaction

                    @Override
                    public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                        // No action needed
                    } // onComplete
                });
            } // if
        } else {
            // Driver accepting a ride request
            rideRef.child("driverUid").setValue(currentUid);

            // Deduct 50 points from the rider (request creator)
            if (currentRide.getRiderUid() != null) {
                DatabaseReference riderUserRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(currentRide.getRiderUid())
                        .child("points");

                riderUserRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Integer points = currentData.getValue(Integer.class);
                        if (points == null) {
                            currentData.setValue(0);
                        } else {
                            currentData.setValue(points - 50);
                        } // if
                        return Transaction.success(currentData);
                    } // doTransaction

                    @Override
                    public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                        // No action needed
                    } // onComplete
                });
            } // if

            // Award 50 points to the driver (acceptor)
            DatabaseReference driverRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUid)
                    .child("points");

            driverRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer points = currentData.getValue(Integer.class);
                    if (points == null) {
                        currentData.setValue(50);
                    } else {
                        currentData.setValue(points + 50);
                    } // if
                    return Transaction.success(currentData);
                } // doTransaction

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                    // No action needed
                } // onComplete
            });
        } // if

        rideRef.child("status").setValue("accepted");

        // Optionally, delete the ride after acceptance
        rideRef.removeValue();

        Toast.makeText(RideDetailsActivity.this, "Ride accepted and points updated!", Toast.LENGTH_SHORT).show();
        buttonAcceptRide.setEnabled(false);
        buttonAcceptRide.setText("Accepted");
    } // acceptRide

} // RideDetailsActivity