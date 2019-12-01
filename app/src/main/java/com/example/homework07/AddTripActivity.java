package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.homework07.UserActivity.PICK_IMAGE;


public class AddTripActivity extends AppCompatActivity {
    EditText et_title;
    EditText et_latitude;
    EditText et_longitude;
    ImageView iv_coverPhoto;
    ImageView iv_cancelBtn;
    Button btn_addTrip;
    private Uri filePath;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        setTitle("Add new trip");
        db=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        et_title=findViewById(R.id.et_title_addTrip);
        et_latitude=findViewById(R.id.et_latitude_addTrip);
        et_longitude=findViewById(R.id.et_longitude_addTrip);
        iv_coverPhoto=findViewById(R.id.iv_coverPhoto_addTrip);
        btn_addTrip=findViewById(R.id.btn_addtripDetails_addTrip);
        iv_cancelBtn = findViewById(R.id.imgCancel_addTrip);

        iv_coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

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
            mem.add(MainActivity.loggedInUserName);
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
            if(filePath==null)
            {
                Toast.makeText(getApplicationContext(), "Select an Image before adding a trip", Toast.LENGTH_SHORT).show();
                errorFlag=1;

            }
            if(errorFlag == 0)
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
                                    uploadImage(title);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv_coverPhoto.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private void uploadImage(final String title) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Image Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+title);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            DocumentReference docRef = db.collection("Trips").document(title);
                            docRef.update("coverPhoto", "images/"+title+"")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("user", "Image URL stored in DB!");
                                            Intent intent = new Intent(AddTripActivity.this, TripActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("user", "Error updating document", e);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
