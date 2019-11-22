package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddTripActivity extends AppCompatActivity {
    EditText et_title;
    EditText et_latitude;
    EditText et_longitude;
    ImageView iv_coverPhoto;
    ImageView iv_cancelBtn;
    Button btn_addTrip;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        setTitle("Add new trip");
        db=FirebaseFirestore.getInstance();

        et_title=findViewById(R.id.et_title_addTrip);
        et_latitude=findViewById(R.id.et_latitude_addTrip);
        et_longitude=findViewById(R.id.et_longitude_addTrip);
        iv_coverPhoto=findViewById(R.id.iv_coverPhoto_addTrip);
        btn_addTrip=findViewById(R.id.btn_addtripDetails_addTrip);
        iv_cancelBtn = findViewById(R.id.imgCancel_addTrip);

        iv_cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTripActivity.this, TripActivity.class);
                startActivity(intent);
            }
        });

        btn_addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            et_title.setError(null);
            et_latitude.setError(null);
            et_longitude.setError(null);

            final String  title=et_title.getText().toString();
            String latitude=et_latitude.getText().toString();
            String longitude=et_longitude.getText().toString();
            String coverPhoto= "";
            ArrayList<String> mem=new ArrayList<String>();
            int errorFlag=0;
            if(title.equals("")){
                et_title.setError("Enter a valid title");
                errorFlag=1;
            }
            if (latitude.equals("")) {
                et_latitude.setError("Enter a valid latitude ");
                errorFlag=1;
            }
            if(longitude.equals("")){
                et_longitude.setError("Enter a valid longitude");
                errorFlag=1;
            }
            if(errorFlag == 0)      //deleting the existing user and creating a new user
            {
                Trips trips=new Trips(title, latitude, longitude, coverPhoto, mem, MainActivity.loggedInUserName);
                Map<String,Object> trip=trips.tripsToHashMap();
                db.collection("Trips").document(title)
                        .set(trip)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("Trips", title +" added successfully");
                                    Intent intent = new Intent(AddTripActivity.this, TripActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Log.d("trip", task.getException().toString());
                                }
                            }
                        });
            }
            else{
                Toast.makeText(AddTripActivity.this, "Enter correct details", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
}
