package com.example.homework07;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    ArrayList<ChatObject> tdata;

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
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatObject chat = tdata.get(position);
        holder.tv_messages.setText(chat.message);
        holder.tv_msgDetails.setText(chat.sender +","+chat.sentTime);

        holder.iv_picture.setVisibility(View.INVISIBLE);

        Resources res = holder.itemView.getContext().getResources();
        int color = res.getColor(R.color.listColor);
        if(chat.sender.equals(MainActivity.loggedInUserName)) {
            holder.tv_messages.setGravity(Gravity.RIGHT);
            holder.tv_msgDetails.setGravity(Gravity.RIGHT);
            holder.tv_messages.setBackgroundColor(color);
        }

//        Picasso.get().load(chat.).into(holder.iv_coverPhotosegment);



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

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.chat = chat;
            tv_messages=itemView.findViewById(R.id.tv_message_chat);
            tv_msgDetails=itemView.findViewById(R.id.tv_msgDetails);
            iv_picture=itemView.findViewById(R.id.imgChat_chat);

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
