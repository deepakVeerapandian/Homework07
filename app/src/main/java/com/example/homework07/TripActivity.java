package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class TripActivity extends AppCompatActivity {

    ImageView imgUser;
    ImageView imgAddTrip;
    private RecyclerView rv_tripItem;
    private RecyclerView.Adapter tripAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView btn_view_Users;
    FirebaseStorage storage;
    StorageReference storageReference;
//    public static int flagEdit=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        setTitle("Trips");

        final ArrayList<Trips>tripItemArrayList=new ArrayList<Trips>();

        imgUser = findViewById(R.id.imgViewUserPage);
        rv_tripItem=findViewById(R.id.rv_tripItem);
        imgAddTrip = findViewById(R.id.imgAddTrip);
        btn_view_Users=findViewById(R.id.btn_viewUsers);
        rv_tripItem.setHasFixedSize(true);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        layoutManager = new LinearLayoutManager(this);
        rv_tripItem.setLayoutManager(layoutManager);
        FirebaseFirestore db;
        db=FirebaseFirestore.getInstance();


        btn_view_Users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(TripActivity.this, ViewUsersActivity.class);
                startActivity(i);
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                flagEdit=1;
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
                                final String titleItem= (String) document.getData().get("title");
                                final String adminItem= (String)document.getData().get("admin");
                                final String coverPhotoItem= (String)document.getData().get("coverPhoto");
                                StorageReference listRef = storage.getReference().child("images/"+titleItem);

                                listRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.e("Tuts+", "uri: " + uri.toString());
                                        Trips t=new Trips(titleItem,adminItem,coverPhotoItem,uri.toString());
                                        tripItemArrayList.add(t);
                                        tripAdapter.notifyDataSetChanged();
                                    }
                                });
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
