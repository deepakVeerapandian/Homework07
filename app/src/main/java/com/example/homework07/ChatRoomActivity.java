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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    ArrayList<ChatObject> chatList;

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

        chatList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

//        final DocumentReference docRef = db.collection("Trips").document(tripTitle);
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("trips", "Listen failed.", e);
//                    return;
//                }
//
//                if (snapshot != null && snapshot.exists()) {
//                    Log.d("trips", "Current data: " + snapshot.getData());
//                    Chats chat = new Chats(snapshot.getData());
//                    chatList.add(chat);
//                } else {
//                    Log.d("trips", "Current data: null");
//                }
//            }
//        });

        iv_sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_messages.getText().toString();
                String msgID = UUID.randomUUID().toString();
                Date currentTime = Calendar.getInstance().getTime();

//                DocumentReference docRef = db.collection("Trips").document(userName);
//                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//
//                            }
//                        } else {
//                            Log.d("demo", "get failed with ", task.getException());
//                        }
//                    }
//                });

                chatList.add(new ChatObject(MainActivity.loggedInUserName, tripTitle, msgID, message, currentTime));
                Chats chat = new Chats(chatList);
                db.collection("Chats").document(tripTitle)
                        .set(chat.ToHashMap())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("chats",  "chat added successfully");
                                    chatAdapter = new ChatAdapter(chatList);
                                    rv_chat.setAdapter(chatAdapter);
                                    et_messages.setText("");
                                }
                                else{
                                    Log.d("trip", task.getException().toString());
                                }
                            }
                        });
            }
        });
    }
}
