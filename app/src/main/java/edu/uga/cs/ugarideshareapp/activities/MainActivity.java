package edu.uga.cs.ugarideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.ugarideshareapp.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonPostRideOffer;
    private Button buttonPostRideRequest;
    private Button buttonViewRides;
    private TextView textViewPoints;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPostRideOffer = findViewById(R.id.buttonPostRideOffer);
        buttonPostRideRequest = findViewById(R.id.buttonPostRideRequest);
        buttonViewRides = findViewById(R.id.buttonViewRides);
        textViewPoints = findViewById(R.id.textViewPoints); // Ensure the ID matches your XML layout

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        buttonPostRideOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostRideActivity.class);
                startActivity(intent);
            }
        });

        Button buttonViewRequests = findViewById(R.id.buttonViewRequests);

        buttonViewRequests.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RidesRequestListActivity.class);
            startActivity(intent);
        });

        buttonPostRideRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostRideRequestActivity.class);
                startActivity(intent);
            }
        });

        buttonViewRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RidesListActivity.class);
                startActivity(intent);
            }
        });

        // Load the user's points when the activity starts
        loadUserPoints();
    }

    private void loadUserPoints() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            // Get reference to the user's points in the database
            mDatabase.child("users").child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Long points = dataSnapshot.getValue(Long.class);
                        if (points != null) {
                            // Display points in the UI
                            textViewPoints.setText("Points: " + points);
                        } else {
                            // Points field is missing
                            textViewPoints.setText("Points not available");
                        }
                    } else {
                        // User document doesn't exist
                        Toast.makeText(MainActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(MainActivity.this, "Failed to load points: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
