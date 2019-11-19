package com.example.homework07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TripActivity extends AppCompatActivity {

    ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        setTitle("Trip details");

        imgUser = findViewById(R.id.imgViewUserPage);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }
}
