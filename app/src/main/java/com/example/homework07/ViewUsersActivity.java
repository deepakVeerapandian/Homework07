package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewUsersActivity extends AppCompatActivity {

    public ArrayAdapter<User> adapter;
    private EditText et_searchUsers_viewUsers;
    private ImageView iv_cancel_viewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        final ListView lv_viewUser;
        final FirebaseFirestore db;
        db=FirebaseFirestore.getInstance();

        lv_viewUser=findViewById(R.id.lv_userView);
        iv_cancel_viewUsers=findViewById(R.id.iv_cancel_viewUsers);
        et_searchUsers_viewUsers=findViewById(R.id.et_searchUser_viewUser);
        et_searchUsers_viewUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {
                final String searchedName=et_searchUsers_viewUsers.getText().toString();
                final ArrayList<User> UserList=new ArrayList<User>();
                db.collection("User")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User u=new User(document.getData());
                                        if(u.userName.contains(searchedName))
                                        {
                                            if(!u.userName.equals(MainActivity.loggedInUserName))
                                            {
                                                UserList.add(u);
                                            }

                                        }
                                    }
                                    adapter=new ArrayAdapter<User>(getBaseContext(),android.R.layout.simple_list_item_1,UserList);
                                    lv_viewUser.setAdapter(adapter);
                                } else {
                                    Log.d("demo", "Error getting documents: ", task.getException());
                                }
                            }
                        });


            }
        });




        if(getIntent()!=null)
        {
            final ArrayList<User> UserList=new ArrayList<User>();
            db.collection("User")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    User u=new User(document.getData());
                                    if(!u.userName.equals(MainActivity.loggedInUserName))
                                    {
                                        UserList.add(u);
                                    }
                                }
                                adapter=new ArrayAdapter<User>(getBaseContext(),android.R.layout.simple_list_item_1,UserList);
                                lv_viewUser.setAdapter(adapter);
                            } else {
                                Log.d("demo", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            lv_viewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    User i=adapter.getItem(position);
                    Intent intent=new Intent(ViewUsersActivity.this,FriendsDetailsActivity.class);
                    String firstName=i.firstName.toString();
                    String lastName=i.lastName.toString();
                    String userName=i.userName.toString();
                    String Image=i.imgUrl.toString();
                    String gender=i.gender.toString();
                    intent.putExtra("firstName",firstName);
                    intent.putExtra("lastName",lastName);
                    intent.putExtra("userName",userName);
                    intent.putExtra("Image",Image);
                    intent.putExtra("gender",gender);
                    startActivity(intent);
                }
            });
            iv_cancel_viewUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
