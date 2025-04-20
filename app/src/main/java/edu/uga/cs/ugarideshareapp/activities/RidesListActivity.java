package edu.uga.cs.ugarideshareapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.ugarideshareapp.R;
import edu.uga.cs.ugarideshareapp.activities.Ride; // Make sure you import your Ride class
import edu.uga.cs.ugarideshareapp.activities.RidesAdapter; // Import your adapter if needed

public class RidesListActivity extends AppCompatActivity {

    private RecyclerView ridesRecyclerView;
    private List<Ride> rideList;
    private RidesAdapter ridesAdapter;

    private DatabaseReference ridesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_list);

        ridesRecyclerView = findViewById(R.id.ridesRecyclerView);
        ridesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rideList = new ArrayList<>();
        ridesAdapter = new RidesAdapter(rideList);
        ridesRecyclerView.setAdapter(ridesAdapter);

        // Firebase reference
        ridesRef = FirebaseDatabase.getInstance().getReference("rides");

        // Load rides from Firebase
        loadRidesFromFirebase();
    }

    private void loadRidesFromFirebase() {
        ridesRef.orderByChild("dateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                rideList.clear();
                for (DataSnapshot rideSnapshot : snapshot.getChildren()) {
                    Ride ride = rideSnapshot.getValue(Ride.class);
                    if (ride != null) {
                        rideList.add(ride);
                    }
                }
                ridesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RidesListActivity.this, "Failed to load rides.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
