package com.example.homework07;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TripDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        if(getIntent().getExtras()!=null)
        {
            String title = getIntent().getExtras().getString("tripTitle");
            setTitle(title);
        }
    }
}
