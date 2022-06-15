package com.yazilimmuhendisim.arababam.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.activity.FullImageActivity;

import java.io.File;
import java.util.List;

public class ImageFileAdapter extends RecyclerView.Adapter<ImageFileAdapter.CardViewTasarimTutucu>{

    private Context context;
    private List<File> fileList;

    public ImageFileAdapter(Context context, List<File> fileList){
        this.context=context;
        this.fileList=fileList;
    }

    public class CardViewTasarimTutucu extends RecyclerView.ViewHolder{
        public ImageView imageView,imageViewClose;
        public CardView cardView;
        public CardViewTasarimTutucu(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewImageTasarim);
            imageViewClose = itemView.findViewById(R.id.imageViewImageRemove);
            cardView = itemView.findViewById(R.id.cardViewImageTasarim);

        }
    }

    @NonNull
    @Override
    public ImageFileAdapter.CardViewTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_tasarim,parent,false);
        return new CardViewTasarimTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFileAdapter.CardViewTasarimTutucu holder, int position) {

        if(fileList.get(position).exists())
        {
            Picasso.with(context).load(fileList.get(position)).centerCrop().fit().into(holder.imageView);
        }

        holder.imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, fileList.size());
            }
        });



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentImage = new Intent(context.getApplicationContext(), FullImageActivity.class);
                intentImage.putExtra("bitmap",fileList.get(position).getAbsolutePath());
                context.startActivity(intentImage);
            }
        });



    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
