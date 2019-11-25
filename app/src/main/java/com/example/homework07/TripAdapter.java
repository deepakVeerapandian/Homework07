package com.example.homework07;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder>{
    ArrayList<Trips> tdata;

    public TripAdapter(ArrayList<Trips> tdata) {
        this.tdata = tdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tripitem, parent, false);
        ViewHolder viewholder=new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trips ti=tdata.get(position);
        holder.tv_tripsegment.setText(ti.title);
        holder.tv_adminSegment.setText(ti.createdBy);
        Picasso.get().load(ti.imgUrl).into(holder.iv_coverPhotosegment);

        Resources res = holder.itemView.getContext().getResources();
        int color = res.getColor(R.color.listColor);
        if(position%2 == 0)
            holder.parentLayout.setBackgroundColor(color);

        holder.tri=ti;
    }

    @Override
    public int getItemCount() {
        return tdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_tripsegment;
        TextView tv_adminSegment;
        Trips tri;
        ImageView iv_coverPhotosegment;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.tri=tri;
            tv_adminSegment=itemView.findViewById(R.id.tv_adminSegment);
            tv_tripsegment=itemView.findViewById(R.id.tv_tripsegment);
            iv_coverPhotosegment=itemView.findViewById(R.id.iv_coverPhotosegment);
            parentLayout=itemView.findViewById(R.id.layout_listItem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i=new Intent(itemView.getContext(),TripDetailsActivity.class);
                    i.putExtra("tripTitle",tri.title);
                    i.putExtra("tripAdmin",tri.createdBy);
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }

}
