package edu.uga.cs.ugarideshareapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.models.Ride;

public class PostRideRequestActivity extends AppCompatActivity {

    private EditText editTextFrom, editTextTo, editTextDateTime;
    private Button buttonPostRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ride);

        editTextFrom = findViewById(R.id.editTextFrom);
        editTextTo = findViewById(R.id.editTextTo);
        editTextDateTime = findViewById(R.id.editTextDateTime);
        buttonPostRide = findViewById(R.id.buttonPostRide);

        buttonPostRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get reference to the rides node in Firebase Realtime Database
                DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides");

                // Get the input values
                String from = editTextFrom.getText().toString();
                String to = editTextTo.getText().toString();
                String dateTime = editTextDateTime.getText().toString();

                // Get the current authenticated user's UID
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Create a new Ride object for the ride request (isOffer = false)
                Ride ride = new Ride(from, to, dateTime, false, userId);

                // Push ride request to the database
                ridesRef.push().setValue(ride)
                        .addOnSuccessListener(aVoid -> {
                            // Show success message and finish the activity
                            Toast.makeText(PostRideRequestActivity.this, "Ride request posted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to main activity
                        })
                        .addOnFailureListener(e -> {
                            // Show failure message
                            Toast.makeText(PostRideRequestActivity.this, "Failed to post ride request.", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
