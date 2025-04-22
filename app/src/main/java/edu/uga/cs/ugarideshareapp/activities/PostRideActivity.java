package edu.uga.cs.ugarideshareapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides");

                String from = editTextFrom.getText().toString();
                String to = editTextTo.getText().toString();
                String dateTime = editTextDateTime.getText().toString();

                // isOffer = true for offer, false for request
                Ride ride = new Ride(from, to, dateTime, true);

                // Push ride to database
                ridesRef.push().setValue(ride)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(PostRideActivity.this, "Ride posted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to main
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(PostRideActivity.this, "Failed to post ride.", Toast.LENGTH_SHORT).show();
                        });

            }
        });
    }
}
