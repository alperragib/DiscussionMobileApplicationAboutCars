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

import java.util.List;

public class ImageUrlAdapter extends RecyclerView.Adapter<ImageUrlAdapter.CardViewTasarimTutucu>{

    private Context context;
    private List<String> urlList;
    private boolean closable;

    public ImageUrlAdapter(Context context, List<String> urlList,boolean closable){
        this.context=context;
        this.urlList=urlList;
        this.closable=closable;
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
    public ImageUrlAdapter.CardViewTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_tasarim,parent,false);
        return new CardViewTasarimTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageUrlAdapter.CardViewTasarimTutucu holder, int position) {

        if(!closable){
            holder.imageViewClose.setVisibility(View.GONE);
        }
        else
        {
            holder.imageViewClose.setVisibility(View.VISIBLE);
        }

        holder.imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, urlList.size());
            }
        });

        Picasso.with(context).load(urlList.get(position)).centerCrop().fit()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentImage = new Intent(context.getApplicationContext(), FullImageActivity.class);
                intentImage.putExtra("url",urlList.get(position));
                context.startActivity(intentImage);
            }
        });



    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }
}
