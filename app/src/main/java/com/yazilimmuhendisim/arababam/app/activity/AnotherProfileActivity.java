package com.yazilimmuhendisim.arababam.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.adapter.MySoruAdapter;
import com.yazilimmuhendisim.arababam.app.adapter.MyYanitAdapter;
import com.yazilimmuhendisim.arababam.app.model.User;


import de.hdodenhof.circleimageview.CircleImageView;

public class AnotherProfileActivity extends AppCompatActivity {

    private User user;
    private LinearLayout linearLayoutNotFound;
    private TextView textViewNotFound;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_profile);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(AnotherProfileActivity.this,R.color.primaryColor));

        user = (User) getIntent().getSerializableExtra("User");

        if(user==null){
            finish();
        }

        recyclerView = findViewById(R.id.recylerViewAnotherProfile);
        linearLayoutNotFound = findViewById(R.id.linearLayoutAnotherProfileNotFound);
        textViewNotFound = findViewById(R.id.textViewAnotherProfileNotFound);
        ImageView imageViewBack = findViewById(R.id.imageViewAnotherProfileBack);
        TextView textViewSorular = findViewById(R.id.textViewAnotherProfileSorular);
        TextView textViewYanitlar = findViewById(R.id.textViewAnotherProfileYanitlar);
        TextView textViewYanitSize = findViewById(R.id.textViewAnotherProfileYanitSize);
        TextView textViewUsername = findViewById(R.id.textViewAnotherProfileUsername);
        TextView textViewAbout = findViewById(R.id.textViewAnotherProfileAbout);
        TextView textViewLikeSize = findViewById(R.id.textViewAnotherProfileLikeSize);
        TextView textViewEnIyiYanitSize = findViewById(R.id.textViewAnotherProfileEnIyiYanitSize);
        CircleImageView profilePhoto = findViewById(R.id.circleImageViewAnotherProfilePhoto);

        if(user.getUser_profile_photo_url() != null && user.getUser_profile_photo_url().startsWith("http")){
            Picasso.with(this).load(user.getUser_profile_photo_url())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(profilePhoto);
        }

        textViewUsername.setText(user.getUsername());

        if(user.getAbout()!=null && user.getAbout().length()>0){
            textViewAbout.setVisibility(View.VISIBLE);
        }else
        {
            textViewAbout.setVisibility(View.GONE);
        }

        textViewAbout.setText(user.getAbout());
        textViewYanitSize.setText(String.valueOf(user.getYanit_size()));
        textViewLikeSize.setText(String.valueOf(user.getLike_size()));
        textViewEnIyiYanitSize.setText(String.valueOf(user.getEn_iyi_yanit_size()));


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        MySoruAdapter soruAdapter = new MySoruAdapter(this, user.getSoruList(),true);
        MyYanitAdapter yanitAdapter = new MyYanitAdapter(this, user.getYanitList(),true);
        recyclerView.setAdapter(soruAdapter);

        if(user.getSoruList().size()<1){
            textViewNotFound.setText("Soru bulunamadı!");
            linearLayoutNotFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }


        textViewSorular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getSoruList().size()<1){
                    textViewNotFound.setText("Soru bulunamadı!");
                    linearLayoutNotFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    linearLayoutNotFound.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    recyclerView.setAdapter(soruAdapter);
                    recyclerView.setHasFixedSize(true);
                }
                textViewYanitlar.setBackgroundColor(getResources().getColor(R.color.white));
                textViewYanitlar.setTextColor(getResources().getColor(R.color.secondaryColor));

                textViewSorular.setBackgroundColor(getResources().getColor(R.color.black_a));
                textViewSorular.setTextColor(getResources().getColor(R.color.white));

                textViewSorular.setClickable(false);
                textViewYanitlar.setClickable(true);
            }
        });

        textViewYanitlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getYanitList().size()<1){
                    textViewNotFound.setText("Yanıt bulunamadı!");
                    linearLayoutNotFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else
                {
                    linearLayoutNotFound.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    recyclerView.setAdapter(yanitAdapter);
                    recyclerView.setHasFixedSize(true);
                }
                textViewSorular.setBackgroundColor(getResources().getColor(R.color.white));
                textViewSorular.setTextColor(getResources().getColor(R.color.secondaryColor));

                textViewYanitlar.setBackgroundColor(getResources().getColor(R.color.black_a));
                textViewYanitlar.setTextColor(getResources().getColor(R.color.white));

                textViewYanitlar.setClickable(false);
                textViewSorular.setClickable(true);


            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}