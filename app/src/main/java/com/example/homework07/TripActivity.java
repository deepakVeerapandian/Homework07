package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TripActivity extends AppCompatActivity {

    ImageView imgUser;
    ImageView imgAddTrip;
    private RecyclerView rv_tripItem;
    private RecyclerView.Adapter tripAdapter;
    private RecyclerView.LayoutManager layoutManager;

    final ArrayList<Trips>tripItemArrayList=new ArrayList<Trips>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        setTitle("Trips");

        imgUser = findViewById(R.id.imgViewUserPage);
        rv_tripItem=findViewById(R.id.rv_tripItem);
        imgAddTrip = findViewById(R.id.imgAddTrip);
        rv_tripItem.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rv_tripItem.setLayoutManager(layoutManager);
        FirebaseFirestore db;
        db=FirebaseFirestore.getInstance();

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        imgAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripActivity.this, AddTripActivity.class);
                startActivity(intent);
            }
        });

        db.collection("Trips")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String titleItem= (String) document.getData().get("title");
                            String adminItem= (String)document.getData().get("admin");
                            String coverPhotoItem= (String)document.getData().get("coverPhoto");
                            Trips tripItem=new Trips(titleItem,adminItem,coverPhotoItem);
                            tripItemArrayList.add(tripItem);
                        }
                        tripAdapter=new TripAdapter(tripItemArrayList);
                        rv_tripItem.setAdapter(tripAdapter);

                    } else {
                        Log.d("tag", "Error getting documents: ", task.getException());
                    }
                }
            });
    }
}
