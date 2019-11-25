package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FriendsDetailsActivity extends AppCompatActivity {

    TextView tv_usernamevalue_friendsDetails;
    TextView tv_firstNamevalue_friendDetails;
    TextView tv_lastnamevalue_friendDetails;
    TextView tv_genderValue_friendsDetails;
    ImageView iv_friendsImage_friendsDetails;
    ImageView iv_cancel_friendsDetails;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_details);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        tv_usernamevalue_friendsDetails=findViewById(R.id. tv_usernamevalue_friendsDetails);
        tv_firstNamevalue_friendDetails=findViewById(R.id.tv_firstNamevalue_friendDetails);
        tv_lastnamevalue_friendDetails=findViewById(R.id.tv_lastnamevalue_friendDetails);
        tv_genderValue_friendsDetails=findViewById(R.id.tv_genderValue_friendsDetails);
        iv_cancel_friendsDetails=findViewById(R.id.iv_cancel_friendsDetails);
        iv_friendsImage_friendsDetails=findViewById(R.id.iv_friendsImage_friendsDetails);
        if(getIntent().getExtras()!=null)
        {
            final String userName=getIntent().getExtras().getString("userName");
            String firstName=getIntent().getExtras().getString("firstName");
            String lastName=getIntent().getExtras().getString("lastName");
            String gender=getIntent().getExtras().getString("gender");
            String Image=getIntent().getExtras().getString("Image");

            StorageReference listRef = storage.getReference().child("images/"+userName);

            listRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                   Picasso.get().load(uri.toString()).into(iv_friendsImage_friendsDetails);

                    //Handle whatever you're going to do with the URL here
                }
            });

            tv_usernamevalue_friendsDetails.setText(userName);
            tv_firstNamevalue_friendDetails.setText(firstName);
            tv_lastnamevalue_friendDetails.setText(lastName);
            tv_genderValue_friendsDetails.setText(gender);
            setTitle("Profile : "+userName);

        }
        iv_cancel_friendsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
