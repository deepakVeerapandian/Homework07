package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TripDetailsActivity extends AppCompatActivity {
    EditText et_title;
    EditText et_latitude;
    EditText et_longitude;
    ImageView iv_coverPhoto;
    ImageView iv_cancelBtn;
    ImageView iv_members;
    Button btn_saveTrip;
    Button btn_deleteTrip;
    Button btn_joinTrip;
    Button btn_chat;
    TextView tv_admin;

    FirebaseFirestore db;
    String tripTitle = "";
    String tripAdmin = "";
    FirebaseStorage storage;
    ArrayList<String> memebers;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        db=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        memebers = new ArrayList<>();

        et_title=findViewById(R.id.et_title_tripDetails);
        et_latitude=findViewById(R.id.et_latitude_tripDetails);
        et_longitude=findViewById(R.id.et_longitude_tripDetails);
        iv_coverPhoto=findViewById(R.id.iv_coverPhoto_tripDetails);
        iv_members=findViewById(R.id.imgMembers_tripDetails);
        btn_saveTrip=findViewById(R.id.btn_save_tripDetails);
        btn_deleteTrip=findViewById(R.id.btnDelete_tripDetails);
        btn_chat=findViewById(R.id.btnChat_tripDetails);
        btn_joinTrip=findViewById(R.id.btnJoin_tripDetails);
        iv_cancelBtn = findViewById(R.id.imgCancel_tripDetails);
        tv_admin = findViewById(R.id.txtValueAdmin_tripDetails);

        if(getIntent().getExtras()!=null)
        {
            tripTitle = getIntent().getExtras().getString("tripTitle");
            tripAdmin = getIntent().getExtras().getString("tripAdmin");
            setTitle(tripTitle);
            tv_admin.setText(tripAdmin);
        }

        iv_cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripDetailsActivity.this, TripActivity.class);
                startActivity(intent);
            }
        });

        if(!MainActivity.loggedInUserName.equals(tripAdmin)) {
            btn_saveTrip.setVisibility(View.INVISIBLE);
            btn_deleteTrip.setVisibility(View.INVISIBLE);
            btn_chat.setVisibility(View.INVISIBLE);
            et_latitude.setKeyListener(null);
            et_longitude.setKeyListener(null);
            et_title.setKeyListener(null);
        }
        else{
            btn_joinTrip.setVisibility(View.INVISIBLE);
        }

        DocumentReference docRef = db.collection("Trips").document(tripTitle);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Trips trips = new Trips( document.getData());
                        et_title.setText(trips.title);
                        et_latitude.setText(trips.latitude);
                        et_longitude.setText(trips.longitude);
                        memebers.addAll(trips.members);
                        if(memebers.contains(MainActivity.loggedInUserName)){
                            btn_chat.setVisibility(View.VISIBLE);
                            btn_joinTrip.setVisibility(View.INVISIBLE);
                        }

                        StorageReference listRef = storage.getReference().child("images/");
                        listRef.listAll()
                                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        for (final StorageReference item : listResult.getItems()) {
                                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if(task.isSuccessful()){
                                                        String imagePath = item.getPath();
                                                        String [] x = imagePath.split("/");
                                                        if(x[2].equals(tripTitle)){
                                                            String imageURL = task.getResult().toString();
                                                            Picasso.get().load(imageURL).into(iv_coverPhoto);
                                                        }
                                                    }
                                                };
                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("demo...", "Image list view failue");
                                    }
                                });

                        Log.d("trips", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("trips", "No such document");
                    }
                } else {
                    Log.d("trips", "get failed with ", task.getException());
                }
            }
        });


        btn_saveTrip.setOnClickListener(new View.OnClickListener() {
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
                    final Map<String,Object> trip=trips.tripsToHashMap();

                    db.collection("Trips").document(tripTitle)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("Trips").document(title)
                                            .set(trip)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Log.d("Trips", title +" added successfully");
                                                        Intent intent = new Intent(TripDetailsActivity.this, TripActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else{
                                                        Log.d("trip", task.getException().toString());
                                                    }
                                                }
                                            });
                                }
                            });

                }
                else{
                    Toast.makeText(TripDetailsActivity.this, "Enter correct details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] membersArray = memebers.toArray(new String[memebers.size()]);
//                Arrays.toString(membersArray);
                builder = new AlertDialog.Builder(TripDetailsActivity.this);
                builder.setTitle("Trip Members")
                        .setCancelable(true)
                        .setItems(membersArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog membersList = builder.create();
                membersList.show();
            }
        });

        btn_joinTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = db.collection("Trips").document(tripTitle);
                memebers.add(MainActivity.loggedInUserName);
                docRef.update("members", memebers)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Trip", "Member added");
                                Toast.makeText(TripDetailsActivity.this, "You have joined the trip", Toast.LENGTH_SHORT).show();
                                btn_chat.setVisibility(View.VISIBLE);
                                btn_joinTrip.setVisibility(View.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Trip", "Error updating document", e);
                            }
                        });
            }
        });

        btn_deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Trips").document(tripTitle)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Trips", tripTitle +" deleted successfully");
                                Toast.makeText(TripDetailsActivity.this, "Trip deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TripDetailsActivity.this, TripActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripDetailsActivity.this, ChatRoomActivity.class);
                intent.putExtra("tripTitle", tripTitle);
                startActivity(intent);
                finish();
            }
        });
    }
}
