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

/**
 * MainActivity serves as the home screen of the UGA Ride Share App.
 * It displays the user's current ride-point balance and provides navigation to:
 * - Post ride offers or requests
 * - View available rides
 * - View personal offers
 * - View ride requests
 * - Logout
 */
public class MainActivity extends AppCompatActivity {

    private Button buttonPostRideOffer;
    private Button buttonPostRideRequest;
    private Button buttonViewRides;
    private TextView textViewPoints;
    private Button buttonMyOffers;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    /**
     * Initializes the main screen, sets up button listeners for navigation,
     * and loads the user's ride-point balance from Firebase.
     *
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPostRideOffer = findViewById(R.id.buttonPostRideOffer);
        buttonPostRideRequest = findViewById(R.id.buttonPostRideRequest);
        buttonViewRides = findViewById(R.id.buttonViewRides);
        textViewPoints = findViewById(R.id.textViewPoints);
        buttonMyOffers = findViewById(R.id.buttonViewMyOffers);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        buttonPostRideOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostRideActivity.class);
                startActivity(intent);
            } // onClick
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
            } // onclick
        });

        buttonViewRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RidesListActivity.class);
                startActivity(intent);
            } // onClick
        });

        buttonMyOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyRideOffersActivity.class);
                startActivity(intent);
            } // onClick
        });

        // Load the user's points when the activity starts
        loadUserPoints();

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    } // onCreate

    /**
     * Loads the current user's ride-point balance from Firebase
     * and updates the UI in real-time using a value event listener.
     */
    private void loadUserPoints() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            // Get reference to the user's points in the database
            DatabaseReference userPointsRef = mDatabase.child("users").child(userId).child("points");

            // Add a real-time listener to update points in the UI automatically
            userPointsRef.addValueEventListener(new ValueEventListener() {
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
                        } // if
                    } else {
                        // User document doesn't exist
                        Toast.makeText(MainActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    } // if
                } // onDataChange

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(MainActivity.this, "Failed to load points: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } // onCancelled
            });
        } // if
    } // loadUserPoints

    /**
     * Attempts to remove the Firebase value listener when the activity is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            mDatabase.child("users").child(userId).child("points").removeEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // No-op
                } // onDataChange

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // No-op
                } // onCancelled
            });
        } // if
    } // onStop
} // MainActivity
