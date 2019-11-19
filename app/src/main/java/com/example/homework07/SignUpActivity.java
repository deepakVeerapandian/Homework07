package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText et_firstName;
    EditText et_lastName;
    EditText et_userName;
    EditText et_password;
    Button btn_cancel;
    Button btn_signUp;
    RadioGroup rg_gender;

    String gender = "";
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        db = FirebaseFirestore.getInstance();
        et_userName = findViewById(R.id.et_username_signup);
        et_password = findViewById(R.id.et_password_signup);
        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_signUp = findViewById(R.id.btn_sign_signup);
        rg_gender = findViewById(R.id.radioGroup);

        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioBtbMale:
                        gender = "Male";
                        break;
                    case R.id.radioBtnFemale:
                        gender = "Female";
                        break;
                }
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(SignUpActivity.this, "Select a gender", Toast.LENGTH_SHORT).show();
                    errorFlag = 1;
                }
                if(errorFlag == 0)
                {

                    User user = new User(1, userName, password, firstName, lastName, "",  gender);

                    Map<String , Object> movieMap = user.toHashMap();
                    db.collection("User").document(userName)
                            .set(movieMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("user", userName +" added successfully");
                                        Intent intent = new Intent(SignUpActivity.this, TripActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Log.d("user", task.getException().toString());
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Enter correct details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
