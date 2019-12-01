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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
//
//            app:layout_constraintRight_toRightOf="@+id/check_answer1"
//            app:layout_constraintTop_toTopOf="@+id/check_answer1"

//            ConstraintSet constraintSet = new ConstraintSet();
//            constraintSet.clone(holder.constraintLayout);
////            constraintSet.connect(holder.iv_picture,ConstraintSet.RIGHT,R.id.check_answer1,ConstraintSet.RIGHT,0);
//            constraintSet.connect(holder.iv_picture, ConstraintSet.TOP, holder.tv_msgDetails, ConstraintSet.TOP,0);
//            constraintSet.applyTo(holder.constraintLayout);
        }

        Resources res = holder.itemView.getContext().getResources();
        int color = res.getColor(R.color.listColor);
        if(chat.sender.equals(MainActivity.loggedInUserName)) {
            holder.tv_messages.setGravity(Gravity.RIGHT);
            holder.tv_msgDetails.setGravity(Gravity.RIGHT);
            holder.tv_messages.setBackgroundColor(color);
//            holder.tv_messages.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        holder.chat = chat;

        ////////////////////////////////////

//        holder.linearLayout.removeAllViews();
//        holder.linearLayout.setGravity(Gravity.RIGHT);
//
//        holder.iv_picture = new ImageView(holder.itemView.getContext());
//        holder.tv_messages = new TextView(holder.itemView.getContext());
//        holder.tv_msgDetails = new TextView(holder.itemView.getContext());
//
//        holder.tv_messages.setText(chat.message);
//        holder.tv_msgDetails.setText(chat.sender +","+chat.sentTime);
//        holder.iv_picture.setVisibility(View.INVISIBLE);
//
////        holder.tv_messages.setId(1);
////        holder.tv_msgDetails.setId(2);
////        holder.iv_picture.setId(3);
//
//
//
//        if(chat.isImageSent.equals("1")){
//            holder.iv_picture.setVisibility(View.VISIBLE);
//            holder.tv_messages.setVisibility(View.INVISIBLE);
//            storage = FirebaseStorage.getInstance();
//            StorageReference listRef = storage.getReference().child(chat.message);
//
//            listRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Log.e("chat image+", "uri: " + uri.toString());
//                    holder.linearLayout.addView(holder.iv_picture);
//                    Picasso.get().load(uri.toString()).into(holder.iv_picture);
//                    holder.iv_picture.setMaxWidth(50);
//                    holder.iv_picture.setMaxHeight(50);
//
////                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                }
//            })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("demo...", "Image chat view failure");
//                    }
//                });
//        }
//        else{
//            holder.linearLayout.addView(holder.tv_messages);
//
////            holder.linearLayout.addView(holder.tv_msgDetails);
//        }
//        holder.linearLayout.addView(holder.tv_msgDetails);

//        holder.chat = chat;
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

        LinearLayout linearLayout;
//        ImageView imgMsg;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.chat = chat;
            tv_messages=itemView.findViewById(R.id.tv_message_chat);
            tv_msgDetails=itemView.findViewById(R.id.tv_msgDetails);
            iv_picture=itemView.findViewById(R.id.imgChat_chat);
            constraintLayout = itemView.findViewById(R.id.layout_listItem);
//            linearLayout=itemView.findViewById(R.id.linear_layout_chat);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
//                    Intent i=new Intent(itemView.getContext(),TripDetailsActivity.class);
//                    i.putExtra("tripTitle",tri.title);
//                    i.putExtra("tripAdmin",tri.createdBy);
//                    itemView.getContext().startActivity(i);
                }
            });
        }
    }
}
