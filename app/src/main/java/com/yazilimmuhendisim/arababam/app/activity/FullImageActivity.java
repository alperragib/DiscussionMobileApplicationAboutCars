package com.yazilimmuhendisim.arababam.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.yazilimmuhendisim.arababam.app.R;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(FullImageActivity.this,R.color.black_a));

        PhotoView imageView = findViewById(R.id.photoViewFullImage);
        ImageView buttonBack = findViewById(R.id.imageViewFullImageBack);

        String bitmap = getIntent().getStringExtra("bitmap");
        String url = getIntent().getStringExtra("url");

        if(bitmap == null && url != null){
            Picasso.with(FullImageActivity.this).load(url)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(imageView);



        }else if (url == null && bitmap!=null){
            imageView.setImageBitmap(BitmapFactory.decodeFile(bitmap));
        }


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}