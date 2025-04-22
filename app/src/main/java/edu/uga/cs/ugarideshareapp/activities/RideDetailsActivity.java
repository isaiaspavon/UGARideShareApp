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

        if (currentRide.isOffer()) {
            rideRef.child("riderUid").setValue(currentUid);
        } else {
            rideRef.child("driverUid").setValue(currentUid);
        }

        rideRef.child("status").setValue("accepted");
        Toast.makeText(this, "Ride accepted!", Toast.LENGTH_SHORT).show();
        buttonAcceptRide.setEnabled(false);
        buttonAcceptRide.setText("Accepted");
    }
}
