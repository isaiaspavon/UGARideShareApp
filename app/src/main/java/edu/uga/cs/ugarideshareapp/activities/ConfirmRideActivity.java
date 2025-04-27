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

public class ConfirmRideActivity extends AppCompatActivity {

    private TextView textViewFrom, textViewTo, textViewDateTime, textViewType, textViewStatus;
    private Button buttonConfirmRide;
    private DatabaseReference ridesRef, usersRef;
    private String rideId;
    private Ride currentRide;
    private String currentUserUid;

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
    }

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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmRideActivity.this, "Failed to load ride.", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        }

        Toast.makeText(this, "Confirmation recorded.", Toast.LENGTH_SHORT).show();

        // Check if both confirmed
        ridesRef.child(rideId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Ride updatedRide = snapshot.getValue(Ride.class);
                if (updatedRide != null && updatedRide.isConfirmDriver() && updatedRide.isConfirmRider()) {
                    completeRide(updatedRide);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        finish(); // Close activity
    }

    private void completeRide(Ride ride) {
        // Award points
        if (ride.getDriverUid() != null && ride.getRiderUid() != null) {
            usersRef.child(ride.getDriverUid()).child("points").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer points = currentData.getValue(Integer.class);
                    if (points == null) {
                        currentData.setValue(150); // In case driver was missing points field
                    } else {
                        currentData.setValue(points + 50);
                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) { }
            });

            usersRef.child(ride.getRiderUid()).child("points").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer points = currentData.getValue(Integer.class);
                    if (points == null) {
                        currentData.setValue(50); // Just in case
                    } else {
                        currentData.setValue(points - 50);
                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) { }
            });
        }

        // Set ride as completed
        ridesRef.child(rideId).child("status").setValue("completed");
    }
}
