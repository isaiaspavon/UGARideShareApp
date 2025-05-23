/*
Unused class for now. Focusing on RideDetails to handle point logic
 */

package edu.uga.cs.ugarideshareapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.models.Ride;
import edu.uga.cs.ugarideshareapp.models.User;

/**
 * ConfirmRideActivity allows a user (either a driver or a rider) to confirm that a scheduled ride took place.
 * Once both the driver and rider confirm the ride, the ride is marked as completed and ride points are transferred:
 *  - Driver receives 50 points
 *  - Rider loses 50 points (if they have enough)
 */
public class ConfirmRideActivity extends AppCompatActivity {

    private TextView textViewFrom, textViewTo, textViewDateTime, textViewType, textViewStatus;
    private Button buttonConfirmRide;
    private DatabaseReference ridesRef, usersRef;
    private String rideId;
    private Ride currentRide;
    private String currentUserUid;

    /**
     * Initializes the ConfirmRideActivity and loads the ride details.
     * Sets up the confirmation logic for the current user.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ride);

        textViewFrom = findViewById(R.id.textViewFromConfirm);
        textViewTo = findViewById(R.id.textViewToConfirm);
        textViewDateTime = findViewById(R.id.textViewDateTimeConfirm);
        textViewType = findViewById(R.id.textViewTypeConfirm);
        textViewStatus = findViewById(R.id.textViewStatusConfirm);
        buttonConfirmRide = findViewById(R.id.buttonConfirmRide);

        ridesRef = FirebaseDatabase.getInstance().getReference("rides");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rideId = getIntent().getStringExtra("rideId");

        loadRideDetails();

        buttonConfirmRide.setOnClickListener(v -> confirmRide());
    } // onCreate

    /**
     * Loads ride details from Firebase and displays them on the screen.
     * Disables the confirm button if the ride is already marked completed.
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
                    textViewStatus.setText("Status: " + currentRide.getStatus());

                    if ("completed".equals(currentRide.getStatus())) {
                        buttonConfirmRide.setEnabled(false);
                        buttonConfirmRide.setText("Ride Completed");
                    } // if
                } // if
            } // onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmRideActivity.this, "Failed to load ride.", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });
    } // loadRideDetails

    /**
     * Confirms that the current user (driver or rider) participated in the ride.
     * If both parties confirm, the ride is marked as completed and points are transferred.
     */
    private void confirmRide() {
        if (currentRide == null || rideId == null) return;

        boolean isDriver = currentUserUid.equals(currentRide.getDriverUid());
        boolean isRider = currentUserUid.equals(currentRide.getRiderUid());

        if (isDriver) {
            ridesRef.child(rideId).child("confirmDriver").setValue(true);
        } else if (isRider) {
            ridesRef.child(rideId).child("confirmRider").setValue(true);
        } else {
            Toast.makeText(this, "You are not part of this ride.", Toast.LENGTH_SHORT).show();
            return;
        } // if

        Toast.makeText(this, "Confirmation recorded.", Toast.LENGTH_SHORT).show();

        // Immediately disable the button after clicking
        buttonConfirmRide.setEnabled(false);
        buttonConfirmRide.setText("Ride Confirmed");

        // Check if both confirmed
        ridesRef.child(rideId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Ride updatedRide = snapshot.getValue(Ride.class);
                if (updatedRide != null && updatedRide.isConfirmDriver() && updatedRide.isConfirmRider()) {
                    completeRide(updatedRide);
                } // if
            } // onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    } // confirmRide

    /**
     * Transfers points between the rider and driver and marks the ride as completed.
     * Driver receives 50 points; rider is deducted 50 points if they have enough.
     * If not, the ride cannot be completed.
     *
     * @param ride The Ride object that is being confirmed as completed.
     */
    private void completeRide(Ride ride) {
        if (ride.getDriverUid() != null && ride.getRiderUid() != null) {
            // Award driver points
            usersRef.child(ride.getDriverUid()).child("points").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer points = currentData.getValue(Integer.class);
                    if (points == null) {
                        currentData.setValue(150); // In case driver was missing points
                    } else {
                        currentData.setValue(points + 50);
                    } // if
                    return Transaction.success(currentData);
                } // doTransaction

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) { }
            });

            // Subtract rider points
            usersRef.child(ride.getRiderUid()).child("points").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer points = currentData.getValue(Integer.class);
                    if (points == null) {
                        currentData.setValue(0); // Rider had no points (should not happen)
                    } else if (points >= 50) {
                        currentData.setValue(points - 50);
                    } else {
                        // Not enough points
                        return Transaction.abort(); // Stop transaction
                    } // if
                    return Transaction.success(currentData);
                } // doTransaction

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                    if (!committed) {
                        Toast.makeText(ConfirmRideActivity.this, "Rider does not have enough points!", Toast.LENGTH_SHORT).show();
                    } // if
                } // onComplete
            });
        } // if

        // Mark ride as completed
        ridesRef.child(rideId).child("status").setValue("completed");

        Toast.makeText(ConfirmRideActivity.this, "Ride completed! Points transferred.", Toast.LENGTH_SHORT).show();
    } // completeRide

} // ConfirmRideActivity
