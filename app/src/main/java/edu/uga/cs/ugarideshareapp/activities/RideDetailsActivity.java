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

public class RideDetailsActivity extends AppCompatActivity {

    private TextView textViewFrom, textViewTo, textViewDateTime, textViewType;
    private Button buttonAcceptRide;
    private DatabaseReference ridesRef;
    private String rideId;
    private Ride currentRide;

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

                    if (!"available".equals(currentRide.getStatus())) {
                        buttonAcceptRide.setEnabled(false);
                        buttonAcceptRide.setText("Already Accepted");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RideDetailsActivity.this, "Failed to load ride details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptRide() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (currentRide == null || rideId == null) return;

        DatabaseReference rideRef = ridesRef.child(rideId);

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
                    currentData.setValue(50);
                    points = 50;
                }

                if (points < 50) {
                    return Transaction.abort();
                }

                currentData.setValue(points - 50);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (committed) {
                    // Points deducted successfully
                    if (currentRide.isOffer()) {
                        // Rider accepting a driver offer
                        rideRef.child("riderUid").setValue(currentUid);

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
                                    }
                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                                    // No action needed
                                }
                            });
                        }

                    } else {
                        // Driver accepting a ride request
                        rideRef.child("driverUid").setValue(currentUid);

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
                                        currentData.setValue(50);
                                    } else {
                                        currentData.setValue(points + 50);
                                    }
                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                                    // No action needed
                                }
                            });
                        }
                    }

                    rideRef.child("status").setValue("accepted");

                    // Remove the ride from the database after accepting
                    rideRef.removeValue();

                    // Optionally remove from "myRideOffers" if needed
                    DatabaseReference myRideOffersRef = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(currentUid)
                            .child("myRideOffers");

                    myRideOffersRef.child(rideId).removeValue();

                    Toast.makeText(RideDetailsActivity.this, "Ride accepted and points updated!", Toast.LENGTH_SHORT).show();
                    buttonAcceptRide.setEnabled(false);
                    buttonAcceptRide.setText("Accepted");

                } else {
                    Toast.makeText(RideDetailsActivity.this, "Not enough points to accept ride.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
