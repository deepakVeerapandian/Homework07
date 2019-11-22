package com.example.homework07;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.tri=tri;
            tv_adminSegment=itemView.findViewById(R.id.tv_adminSegment);
            tv_tripsegment=itemView.findViewById(R.id.tv_tripsegment);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i=new Intent(itemView.getContext(),TripDetailsActivity.class);
                    i.putExtra("tripTitle",tri.title);
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }

}
