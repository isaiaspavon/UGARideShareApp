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

public class PostRideActivity extends AppCompatActivity {

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
                // Get a reference to the rides node in the Firebase Realtime Database
                DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides");

                // Get user input
                String from = editTextFrom.getText().toString();
                String to = editTextTo.getText().toString();
                String dateTime = editTextDateTime.getText().toString();

                // Get the current authenticated user's UID
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Create a new Ride object for the ride offer
                Ride ride = new Ride(from, to, dateTime, true, userId);

                // Push ride to the database
                ridesRef.push().setValue(ride)
                        .addOnSuccessListener(aVoid -> {
                            // Show success message and close the activity
                            Toast.makeText(PostRideActivity.this, "Ride posted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to main activity
                        })
                        .addOnFailureListener(e -> {
                            // Show failure message
                            Toast.makeText(PostRideActivity.this, "Failed to post ride.", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
