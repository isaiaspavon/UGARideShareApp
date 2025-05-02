package edu.uga.cs.ugarideshareapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.models.Ride;

/**
 * EditRideActivity allows users to edit details of an existing ride (offer or request)
 * that has not yet been accepted. The user can update the date, time, origin, and destination.
 * Changes are saved back to Firebase Realtime Database.
 */
public class EditRideActivity extends AppCompatActivity {

    private EditText editFromLocation, editToLocation, editDateTime;
    private Button saveButton;
    private Ride ride;
    private String rideId;

    /**
     * Initializes the UI, fetches the current ride data, and sets up the save button.
     *
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);

        // Initialize UI components
        editFromLocation = findViewById(R.id.editFromLocation);
        editToLocation = findViewById(R.id.editToLocation);
        editDateTime = findViewById(R.id.editDateTime);
        saveButton = findViewById(R.id.saveButton);

        // Get the ride data passed from the previous activity
        rideId = getIntent().getStringExtra("rideId");
        fetchRideData(rideId);

        // Set up the save button to update the ride
        saveButton.setOnClickListener(v -> updateRide());
    } // onCreate

    /**
     * Retrieves the ride data from Firebase and populates the input fields with current values.
     *
     * @param rideId The ID of the ride to edit
     */
    private void fetchRideData(String rideId) {
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("rides").child(rideId);
        rideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ride = dataSnapshot.getValue(Ride.class);
                if (ride != null) {
                    // Populate the fields with current ride data
                    editFromLocation.setText(ride.getFromLocation());
                    editToLocation.setText(ride.getToLocation());
                    editDateTime.setText(ride.getDateTime());
                } // if
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Toast.makeText(EditRideActivity.this, "Error loading ride data", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });
    } // fetchRideData

    /**
     * Updates the ride data in Firebase with the values entered by the user.
     * Shows a success or failure Toast based on the result.
     */
    private void updateRide() {
        // Get the updated ride details from the UI
        String updatedFromLocation = editFromLocation.getText().toString();
        String updatedToLocation = editToLocation.getText().toString();
        String updatedDateTime = editDateTime.getText().toString();

        if (!updatedFromLocation.isEmpty() && !updatedToLocation.isEmpty() && !updatedDateTime.isEmpty()) {
            ride.setFromLocation(updatedFromLocation);
            ride.setToLocation(updatedToLocation);
            ride.setDateTime(updatedDateTime);

            // Update the ride in Firebase
            DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("rides").child(rideId);
            rideRef.setValue(ride)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditRideActivity.this, "Ride updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity and return to the previous screen
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditRideActivity.this, "Failed to update ride", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } // if
    } // updateRide
} // EditRideActivity
