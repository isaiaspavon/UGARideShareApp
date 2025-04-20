package edu.uga.cs.ugarideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import edu.uga.cs.ugarideshareapp.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonPostRideOffer;
    private Button buttonPostRideRequest;
    private Button buttonViewRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPostRideOffer = findViewById(R.id.buttonPostRideOffer);
        buttonPostRideRequest = findViewById(R.id.buttonPostRideRequest);
        buttonViewRides = findViewById(R.id.buttonViewRides);

        buttonPostRideOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostRideActivity.class);
                startActivity(intent);
            }
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
    }
}
