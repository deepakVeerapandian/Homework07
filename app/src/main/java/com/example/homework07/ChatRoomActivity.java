package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ChatRoomActivity extends AppCompatActivity{
    RecyclerView chatRecylerView;
    ImageView iv_sendButton;
    ImageView iv_openGallery;
    EditText et_messages;

    FirebaseFirestore db;
    private RecyclerView rv_chat;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager layoutManager;
    String tripTitle = "";
    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayList<ChatObject> chatsFromDB;
    public static final int PICK_IMAGE = 1;
    private Uri filePath;
//    Boolean isImageSent = false;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        if(getIntent().getExtras()!=null){
            tripTitle = getIntent().getExtras().getString("tripTitle");
            setTitle("Chat Room : "+tripTitle);
        }

        chatRecylerView = findViewById(R.id.recylerViewChat);
        iv_sendButton = findViewById(R.id.imgViewSend_chat);
        iv_openGallery = findViewById(R.id.imgViewGallery_chat);
        et_messages = findViewById(R.id.editTextTypeMessage_chat);
        rv_chat = findViewById(R.id.recylerViewChat);

        layoutManager = new LinearLayoutManager(this);
        rv_chat.setLayoutManager(layoutManager);

        chatsFromDB = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final DocumentReference docRef = db.collection("Chats").document(tripTitle);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("trips", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("trips", "Current data: " + snapshot.getData());
                    Chats chat = new Chats(snapshot.getData());
                    chatsFromDB.clear();

                    for(int i = 0; i<chat.chatList.size(); i++){
                        Map<String,String> map = (Map<String, String>) chat.chatList.get(i);
                        chatsFromDB.add(new ChatObject(map));
                    }
                    chatAdapter = new ChatAdapter(chatsFromDB);
                    rv_chat.setAdapter(chatAdapter);
                    rv_chat.scrollToPosition(chatsFromDB.size()-1);
                } else {
                    Log.d("trips", "Current data: null");
                }
            }
        });

        iv_sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isImageSent = false;
                sendMessage("0");
//                String message = et_messages.getText().toString();
//                String msgID = UUID.randomUUID().toString();
//                String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
//                String currentTime = formatDateTimeFromDate(DATE_FORMAT, Calendar.getInstance().getTime());
//
//                chatsFromDB.add(new ChatObject(MainActivity.loggedInUserName, tripTitle, msgID, message, currentTime));
//                Chats chat = new Chats(chatsFromDB);
//                db.collection("Chats").document(tripTitle)
//                        .set(chat.ToHashMap())
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    Log.d("chats",  "chat added successfully");
////                                    chatAdapter = new ChatAdapter(chatsFromDB);
////                                    rv_chat.setAdapter(chatAdapter);
//                                    et_messages.setText("");
//                                }
//                                else{
//                                    Log.d("trip", task.getException().toString());
//                                }
//                            }
//                        });
            }
        });

        iv_openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    public static String formatDateTimeFromDate(String mDateFormat, Date date) {
        if (date == null) {
            return null;
        }
        return DateFormat.format(mDateFormat, date).toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                isImageSent = true;
                sendMessage("1");
//                uploadImage();
//                iv_displayPhoto.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //uploading the image to firebase storage with progress bar display
    private void uploadImage(String msgID) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Sending Image...");
            progressDialog.show();

            imageUrl = "chats/"+ msgID;
            StorageReference ref = storageReference.child(imageUrl);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
//                            DocumentReference docRef = db.collection("Chats").document(tripTitle);
//                            docRef.update("imgUrl", "chats/"+ )
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.d("user", "Image URL stored in DB!");
////                                            Intent intent = new Intent(UserActivity.this, TripActivity.class);
////                                            startActivity(intent);
////                                            finish();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w("user", "Error updating document", e);
//                                        }
//                                    });
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

    public void sendMessage(String isImageSent){
        String message = et_messages.getText().toString();
        String msgID = UUID.randomUUID().toString();
        String DATE_FORMAT = "MM/dd HH:mm";
//        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        String currentTime = formatDateTimeFromDate(DATE_FORMAT, Calendar.getInstance().getTime());

        if(isImageSent.equals("0")){
            chatsFromDB.add(new ChatObject(MainActivity.loggedInUserName, tripTitle, msgID, message, "0", currentTime));
            Chats chat = new Chats(chatsFromDB);
            db.collection("Chats").document(tripTitle)
                    .set(chat.ToHashMap())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("chats",  "chat added successfully");
//                                    chatAdapter = new ChatAdapter(chatsFromDB);
//                                    rv_chat.setAdapter(chatAdapter);
                                et_messages.setText("");
                            }
                            else{
                                Log.d("trip", task.getException().toString());
                            }
                        }
                    });
        }
        else{
            uploadImage(msgID);
            chatsFromDB.add(new ChatObject(MainActivity.loggedInUserName, tripTitle, msgID, imageUrl, "1", currentTime));
            Chats chat = new Chats(chatsFromDB);
            db.collection("Chats").document(tripTitle)
                    .set(chat.ToHashMap())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("chats",  "chat added successfully");
//                                    chatAdapter = new ChatAdapter(chatsFromDB);
//                                    rv_chat.setAdapter(chatAdapter);
                                et_messages.setText("");
                            }
                            else{
                                Log.d("trip", task.getException().toString());
                            }
                        }
                    });
        }

    }
}
