package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class UserActivity extends AppCompatActivity {
    EditText et_firstName;
    EditText et_lastName;
    EditText et_userName;
    EditText et_password;
    Button btn_save;
    RadioGroup rg_gender;
    RadioButton rbtn_male;
    RadioButton rbtn_female;
    ImageView iv_cancel;
    ImageView iv_logout;
    ImageView iv_displayPhoto;

    String gender = "";
    FirebaseFirestore db;
    public static final int PICK_IMAGE = 1;
    String imageURL = "";
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("User Profile");

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        et_userName = findViewById(R.id.et_UserName_user);
        et_password = findViewById(R.id.et_password_user);
        et_firstName = findViewById(R.id.et_firstName_user);
        et_lastName = findViewById(R.id.et_lastName_user);
        btn_save = findViewById(R.id.btnSaveUser);
        rg_gender = findViewById(R.id.radioGroup_user);
        rbtn_female = findViewById(R.id.radioBtnFemale_user);
        rbtn_male = findViewById(R.id.radioBtbMale_user);
        iv_cancel = findViewById(R.id.imgViewCancel_user);
        iv_logout = findViewById(R.id.imgViewLogout_user);
        iv_displayPhoto = findViewById(R.id.imgViewAvatar_user);

        //Fetching the user details and displaying in page
        DocumentReference docRef = db.collection("User").document(MainActivity.loggedInUserName);
        docRef.get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    final User user = new User((Map<String, Object>) documentSnapshot.getData());

                    et_userName.setText(user.userName);
                    et_firstName.setText(user.firstName);
                    et_lastName.setText(user.lastName);
                    et_password.setText(user.password);
                    if(user.gender.equals("Male")) {
                        rbtn_male.setChecked(true);
                        gender = "Male";
                    }
                    else {
                        rbtn_female.setChecked(true);
                        gender = "Female";
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
                                        if(x[2].equals(MainActivity.loggedInUserName)){
                                            imageURL = task.getResult().toString();
                                            Picasso.get().load(imageURL).into(iv_displayPhoto);
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

                }
                else{
                    Log.d("user", task.getException().toString());
                }
                }
            });

        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioBtbMale_user:
                        gender = "Male";
                        break;
                    case R.id.radioBtnFemale_user:
                        gender = "Female";
                        break;
                }
            }
        });

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, TripActivity.class);
                startActivity(intent);
            }
        });

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                MainActivity.loggedInUserName = "";
                startActivity(intent);
            }
        });

        iv_displayPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String firstName = et_firstName.getText().toString();
            String lastName = et_lastName.getText().toString();
            final String userName = et_userName.getText().toString();
            String password = et_password.getText().toString();
            int errorFlag=0;
            if(firstName.equals("")){
                et_firstName.setError("Enter a valid FirstName");
                errorFlag=1;
            }
            if (lastName.equals("")) {
                et_lastName.setError("Enter a valid LastName ");
                errorFlag=1;
            }
            if(userName.equals("")){
                et_userName.setError("Enter a valid Email");
                errorFlag=1;
            }
            if(password.equals(""))
            {
                et_password.setError("Enter a valid password");
                errorFlag=1;
            }
            if(gender == ""){
                Toast.makeText(UserActivity.this, "Select a gender", Toast.LENGTH_SHORT).show();
                errorFlag = 1;
            }
            if(errorFlag == 0)      //deleting the existing user and creating a new user
            {
                final User user = new User(1, userName, password, firstName, lastName, "",  gender);
                db.collection("User").document(MainActivity.loggedInUserName)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        Map<String , Object> movieMap = user.toHashMap();
                        db.collection("User").document(userName)
                            .set(movieMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        uploadImage();
                                        Log.d("user", userName +" added successfully");
                                    }
                                    else{
                                        Log.d("user", task.getException().toString());
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("user", "Error deleting document", e);
                        }
                    });
            }
            else{
                Toast.makeText(UserActivity.this, "Enter correct details", Toast.LENGTH_SHORT).show();
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
                iv_displayPhoto.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //uploading the image to firebase storage with progress bar display
    private void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Image Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ MainActivity.loggedInUserName);
            ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    DocumentReference docRef = db.collection("User").document(MainActivity.loggedInUserName);
                    docRef.update("imgUrl", "images/"+ MainActivity.loggedInUserName+".jpeg")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("user", "Image URL stored in DB!");
                                Intent intent = new Intent(UserActivity.this, TripActivity.class);
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
