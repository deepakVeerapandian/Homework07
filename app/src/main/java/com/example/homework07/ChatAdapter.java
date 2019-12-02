package com.example.homework07;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    ArrayList<ChatObject> tdata;
    FirebaseStorage storage;


    public ChatAdapter(ArrayList<ChatObject> tdata) {
        this.tdata = tdata;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        ChatAdapter.ViewHolder viewholder=new ChatAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, int position) {
        final ChatObject chat = tdata.get(position);

        holder.tv_messages.setText(chat.message);
        holder.tv_msgDetails.setText(chat.sender +", "+chat.sentTime);
        holder.iv_picture.setVisibility(View.INVISIBLE);


        if(chat.isImageSent.equals("1")){
            holder.iv_picture.setVisibility(View.VISIBLE);
            holder.tv_messages.setVisibility(View.INVISIBLE);
            storage = FirebaseStorage.getInstance();
            StorageReference listRef = storage.getReference().child(chat.message);

            listRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.e("chat image+", "uri: " + uri.toString());
                    Picasso.get().load(uri.toString()).into(holder.iv_picture);
                }
            })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo...", "Image chat view failue");
                    }
                });
        }

        Resources res = holder.itemView.getContext().getResources();
        int color = res.getColor(R.color.listColor);
        if(chat.sender.equals(MainActivity.loggedInUserName)) {
            holder.tv_messages.setGravity(Gravity.RIGHT);
            holder.tv_msgDetails.setGravity(Gravity.RIGHT);
//            holder.tv_messages.setBackgroundColor(color);
//            holder.tv_messages.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        holder.chat = chat;

    }

    @Override
    public int getItemCount() {
        return tdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_messages;
        TextView tv_msgDetails;
        ChatObject chat;
        ImageView iv_picture;
        ConstraintLayout constraintLayout;
        ArrayList<ChatObject> chatsFromDB=new ArrayList<>();

        LinearLayout linearLayout;
//        ImageView imgMsg;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.chat = chat;
            tv_messages=itemView.findViewById(R.id.tv_message_chat);
            tv_msgDetails=itemView.findViewById(R.id.tv_msgDetails);
            iv_picture=itemView.findViewById(R.id.imgChat_chat);
            final FirebaseFirestore db;
            db=FirebaseFirestore.getInstance();
            constraintLayout = itemView.findViewById(R.id.layout_listItem);
//            linearLayout=itemView.findViewById(R.id.linear_layout_chat);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String tripid=chat.tripId;
                    final String msgId=chat.messagedId;
                    if(MainActivity.loggedInUserName.equals(chat.sender))
                    {
                        DocumentReference docRef = db.collection("Chats").document(tripid);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot snapshot= task.getResult();
                                    if (snapshot.exists()) {
                                        if (snapshot != null && snapshot.exists()) {
//                                            Log.d("trips", "Current data: " + snapshot.getData());
                                            Chats chat = new Chats(snapshot.getData());
                                            chatsFromDB.clear();

                                            for(int i = 0; i<chat.chatList.size(); i++){
                                                Map<String,String> map = (Map<String, String>) chat.chatList.get(i);
                                                if(map.get("messagedId").equals(msgId))
                                                {
//                                                    chat.chatList.remove(i);
                                                }
                                                else
                                                {
                                                    chatsFromDB.add(new ChatObject(map));
                                                }


                                            }

                                            db.collection("Chats").document(tripid)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Chats chat = new Chats(chatsFromDB);
                                                            db.collection("Chats").document(tripid)
                                                                    .set(chat.ToHashMap())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                Log.d("chats",  "chat added successfully");


                                                                            }
                                                                            else{
                                                                                Log.d("trip", task.getException().toString());
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("demo", "Error deleting document", e);
                                                        }
                                                    });


                                        }

                                    }
                                    else {
                                        Log.d("trips", "Current data: null");
                                    }
                                }


                            }
                        });




                    }

                    return false;
                }
            });


        }
    }
}
