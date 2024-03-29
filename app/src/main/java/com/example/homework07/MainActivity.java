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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    FirebaseFirestore db;
    private  EditText et_userName;
    private EditText et_password;
    private Button btn_login;
    private Button btn_signUp;
    public static String loggedInUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=FirebaseFirestore.getInstance();
        et_userName = findViewById(R.id.et_userName);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btnLogin);
        btn_signUp = findViewById(R.id.btnSignUp);
        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        mAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,101);



            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flagError=0;
                final String userName=et_userName.getText().toString();
                final String password=et_password.getText().toString();
                if(password.equals(""))
                {
                    flagError=1;
                    et_password.setError("Enter password");
                }
                if(userName.equals(""))
                {
                    flagError=1;
                    et_userName.setError("Enter UserName");
                }
                if(flagError==0)
                {
                    DocumentReference docRef = db.collection("User").document(userName);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String passwordDb= (String) document.getData().get("password");
                                if(password.equals(passwordDb))
                                {
                                    loggedInUserName = userName;
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    Intent  i=new Intent(MainActivity.this,TripActivity.class);
                                    startActivity(i);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("demo", "get failed with ", task.getException());
                        }
                        }
                    });
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Enter correct details ", Toast.LENGTH_LONG).show();
                }
            }
        });






//            User user  =  new User(account.getGivenName(),account.getFamilyName(),account.getEmail(),account.getPhotoUrl().toString(),"");

        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this,this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();


//        signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setOnClickListener(this);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("demo", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("demo", "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            loggedInUserName=user.getEmail();
                            DocumentReference docRef = db.collection("User").document(user.getEmail());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Intent intent = new Intent(MainActivity.this, TripActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            User u=new User(1,user.getEmail(),user.getPhoneNumber(),user.getDisplayName(),user.getDisplayName(),"","Male");
                                            Map<String , Object> userMap = u.toHashMap();
                                            db.collection("User").document(user.getEmail())
                                                    .set(userMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                            else{
                                                                Log.d("user", task.getException().toString());
                                                            }
                                                        }
                                                    });
                                            Toast.makeText(MainActivity.this, "User created with google sign in", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Log.d("demo", "get failed with ", task.getException());
                                    }
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("demo", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });


    }


//    @Override
//    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.sign_in_button:
//                signIn();
//                break;
//        }
//    }

//    private void signIn(){
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d("AUTH_GOOGLE", "firebaseAuthWithGoogle:" + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("SUCESS", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                        } else {
//
//                        }
//
//                        // ...
//                    }
//                });
//    }



//    private void handleSignInResult(Task<GoogleSignInAccount> result){




//            final GoogleSignInAccount account = result.getSignInAccount();
//            loggedInUserName = account.getEmail();

//            DocumentReference docRef = db.collection("User").document(account.getEmail());
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Intent intent = new Intent(MainActivity.this, TripActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                    else
//                    {
//                        User user=new User(1,account.getEmail(),account.getIdToken(),account.getGivenName(),account.getDisplayName()," ","Male");
//                        Map<String , Object> userMap = user.toHashMap();
//                        db.collection("User").document(account.getEmail())
//                            .set(userMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        Intent intent = new Intent(MainActivity.this, TripActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                    else{
//                                        Log.d("user", task.getException().toString());
//                                    }
//                                }
//                            });
//                        Toast.makeText(MainActivity.this, "User created with google sign in", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Log.d("demo", "get failed with ", task.getException());
//                }
//                }
//            });
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
}
