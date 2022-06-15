package com.yazilimmuhendisim.arababam.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Picasso;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.adapter.ImageFileAdapter;
import com.yazilimmuhendisim.arababam.app.adapter.ImageUrlAdapter;
import com.yazilimmuhendisim.arababam.app.adapter.YanitAdapter;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Soru;
import com.yazilimmuhendisim.arababam.app.model.User;
import com.yazilimmuhendisim.arababam.app.model.Yanit;
import com.yazilimmuhendisim.arababam.app.network.ApiConfigYanitImageUpload;
import com.yazilimmuhendisim.arababam.app.network.AppConfig;
import com.yazilimmuhendisim.arababam.app.network.ServerResponse;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoruYanitActivity extends AppCompatActivity {

    private Soru soruGelen;
    private EditText editTextYanitIcerik;
    private TextView textViewYanitEkleError,textViewLikeSize,textViewDate,textViewBaslik,textViewIcerik,textViewUsername,textViewYanitSize,textViewYanitMarkaModel,textViewYanitlar;
    private ArrayList<File> imageList;
    private ImageFileAdapter imageFileAdapter;
    private ImageView imageViewLike;
    private int user_id,soru_id=0;
    private ProgressDialog progressDialog;
    private Button buttonYanitEkle;
    private RecyclerView recyclerViewImage,recyclerViewYanitlar,recyclerViewYanitEkleImages;
    private CircleImageView circleImageViewProfil;
    private LinearLayout linearLayoutLike,linearLayoutProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soru_yanit);

        progressDialog = new ProgressDialog(SoruYanitActivity.this);
        progressDialog.setMessage("Yanıtınız yayınlanıyor lütfen bekleyiniz...");
        progressDialog.setCancelable(false);


        Soru soruIntent = (Soru) getIntent().getSerializableExtra("Soru");
        soru_id = getIntent().getIntExtra("soru_id",0);

        MySharedPreferences sp = new MySharedPreferences();
        user_id = Integer.parseInt(sp.getSharedPreference(this,"user_id","0"));

        recyclerViewImage = findViewById(R.id.recylerViewSoruYanitSoruImages);
        recyclerViewYanitlar = findViewById(R.id.recylerViewSoruYanitYanitlar);
        recyclerViewYanitEkleImages = findViewById(R.id.recylerViewSoruYanitEkleImage);
        textViewDate = findViewById(R.id.textViewSoruYanitDate);
        textViewBaslik = findViewById(R.id.textViewSoruYanitBaslik);
        textViewIcerik = findViewById(R.id.textViewSoruYanitIcerik);
        textViewUsername = findViewById(R.id.textViewSoruYanitUsername);
        textViewLikeSize = findViewById(R.id.textViewSoruYanitLikeSize);
        textViewYanitSize = findViewById(R.id.textViewSoruYanitYanitSize);
        textViewYanitMarkaModel = findViewById(R.id.textViewSoruYanitMarkaModel);
        textViewYanitEkleError = findViewById(R.id.textViewSoruYanitError);
        circleImageViewProfil = findViewById(R.id.circleImageViewSoruYanitProfile);
        ImageView imageViewBack = findViewById(R.id.imageViewSoruYanitBack);
        imageViewLike = findViewById(R.id.imageViewSoruYanitLike);
        editTextYanitIcerik = findViewById(R.id.editTextSoruYanitİcerik);
        CardView cardViewImageSec = findViewById(R.id.cardViewSoruYanitImageSec);
        buttonYanitEkle = findViewById(R.id.buttonSoruYanitYayinla);
        linearLayoutLike = findViewById(R.id.linearLayoutSoruYanitLike);
        linearLayoutProfile = findViewById(R.id.linearLayoutSoruYanitProfile);
        textViewYanitlar = findViewById(R.id.textViewSoruYanitYanitlar);

        if(soruIntent!=null && soruIntent.getBaslik() != null && soruIntent.getBaslik().length()>0){
            soruGelen = soruIntent;
            tasarimYerlestir();
        }
        else if (soru_id != 0)
        {
            getSoruInfo(user_id,soru_id);
        }
        else
        {
            Toast.makeText(SoruYanitActivity.this,"Soru bulunamadı! 1",Toast.LENGTH_SHORT).show();

            Intent mainIntent2 = new Intent(SoruYanitActivity.this,MainActivity.class);
            startActivity(mainIntent2);
            finish();
        }

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonYanitEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYanitEkle();
            }
        });

        cardViewImageSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });


    }

    private void getSoruInfo(int user_id, int soru_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_soru_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(SoruYanitActivity.this,"Soru bulunamadı!",Toast.LENGTH_SHORT).show();

                    Intent mainIntent2 = new Intent(SoruYanitActivity.this,MainActivity.class);
                    startActivity(mainIntent2);
                    finish();
                }
                else
                {
                    try {
                        JSONObject soru = new JSONObject(response);

                        int soru_id = Integer.parseInt(soru.getString("id"));
                        int user_id = Integer.parseInt(soru.getString("user_id"));
                        int marka_id = Integer.parseInt(soru.getString("marka_id"));
                        int model_id = Integer.parseInt(soru.getString("model_id"));
                        String baslik = soru.getString("baslik");
                        String icerik = soru.getString("icerik");
                        try {
                            baslik = URLDecoder.decode(
                                    baslik, "UTF-8");
                            icerik = URLDecoder.decode(
                                    icerik, "UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }
                        String marka = soru.getString("marka");
                        String model = soru.getString("model");
                        int like = Integer.parseInt(soru.getString("like"));
                        int like_count = Integer.parseInt(soru.getString("like_count"));
                        String username = soru.getString("username");
                        String user_profile_photo_url = soru.getString("user_profile_photo_url");
                        String date = soru.getString("created_time");
                        ArrayList<String> imagesList = new ArrayList<>();

                        JSONArray jsonArray = soru.getJSONArray("images");

                        for(int a=0; a<jsonArray.length();a++){
                            imagesList.add(jsonArray.getString(a));
                        }

                        JSONArray yanitlar = soru.getJSONArray("yanitlar");

                        ArrayList<Yanit> yanitList = new ArrayList<>();

                        for(int j=0; j<yanitlar.length(); j++){
                            JSONObject yanit = (JSONObject) yanitlar.get(j);

                            int yanit_id = Integer.parseInt(yanit.getString("id"));
                            int yanit_user_id = Integer.parseInt(yanit.getString("user_id"));
                            int yanit_soru_id = Integer.parseInt(yanit.getString("soru_id"));
                            String yanit_icerik = yanit.getString("icerik");
                            try {
                                yanit_icerik = URLDecoder.decode(
                                        yanit_icerik, "UTF-8");
                            } catch (UnsupportedEncodingException e) {

                            }
                            int yanit_like = Integer.parseInt(yanit.getString("like"));
                            int yanit_like_count = Integer.parseInt(yanit.getString("like_count"));
                            int yanit_en_iyi = Integer.parseInt(yanit.getString("en_iyi_yanit"));
                            String yanit_date = yanit.getString("created_time");
                            String yanit_username = yanit.getString("username");
                            String yanit_user_profile_photo_url = yanit.getString("user_profile_photo_url");

                            ArrayList <String> yanit_imagesList = new ArrayList<>();

                            JSONArray jsonArray2 = yanit.getJSONArray("images");

                            for(int b=0; b<jsonArray2.length();b++){
                                yanit_imagesList.add(jsonArray2.getString(b));
                            }

                            yanitList.add(new Yanit(yanit_id,yanit_user_id,yanit_soru_id,yanit_like,yanit_like_count,yanit_en_iyi,yanit_icerik,yanit_username,createdTimeToDate(yanit_date),yanit_user_profile_photo_url,yanit_imagesList));
                        }

                        Soru soruGelenVolley = new Soru(soru_id,like,like_count,user_id,marka,model,baslik,icerik,username,createdTimeToDate(date),user_profile_photo_url,imagesList,yanitList);
                        soruGelen = soruGelenVolley;
                        tasarimYerlestir();

                    } catch (JSONException e) {
                        Toast.makeText(SoruYanitActivity.this,"Soru bulunamadı!",Toast.LENGTH_SHORT).show();

                        Intent mainIntent3 = new Intent(SoruYanitActivity.this,MainActivity.class);
                        startActivity(mainIntent3);
                        finish();
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();

                Intent mainIntent = new Intent(SoruYanitActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id",String.valueOf(user_id));
                params.put("soru_id", String.valueOf(soru_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);

    }

    private void tasarimYerlestir(){
        textViewDate.setText(soruGelen.getDate());
        textViewBaslik.setText(soruGelen.getBaslik());
        textViewIcerik.setText(soruGelen.getIcerik());
        textViewUsername.setText(soruGelen.getUsername());
        textViewYanitMarkaModel.setText(soruGelen.getMarka()+"/"+soruGelen.getModel());
        textViewLikeSize.setText(String.valueOf(soruGelen.getLike_size()));
        textViewYanitSize.setText(String.valueOf(soruGelen.getYanitlarList().size()));

        imageList = new ArrayList<>();

        recyclerViewYanitEkleImages.setHasFixedSize(true);
        recyclerViewYanitEkleImages.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        imageFileAdapter = new ImageFileAdapter(this, imageList);
        recyclerViewYanitEkleImages.setAdapter(imageFileAdapter);

        if(soruGelen.getYanitlarList()==null || soruGelen.getYanitlarList().size()<1){
            textViewYanitlar.setVisibility(View.GONE);
        }
        else
        {
            textViewYanitlar.setVisibility(View.VISIBLE);

            recyclerViewYanitlar.setHasFixedSize(true);
            recyclerViewYanitlar.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

            YanitAdapter yanitAdapter = new YanitAdapter(this,this, soruGelen.getYanitlarList(),soruGelen.getUser_id());
            recyclerViewYanitlar.setAdapter(yanitAdapter);
        }

        if(soruGelen.getImagesList() == null || soruGelen.getImagesList().size()<1){
            recyclerViewImage.setVisibility(View.GONE);
        }
        else
        {
            recyclerViewImage.setVisibility(View.VISIBLE);
            recyclerViewImage.setHasFixedSize(true);
            recyclerViewImage.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

            ImageUrlAdapter imageUrlAdapter = new ImageUrlAdapter(this, soruGelen.getImagesList(),false);
            recyclerViewImage.setAdapter(imageUrlAdapter);
        }
        if(soruGelen.getUser_profile_photo_url() != null && soruGelen.getUser_profile_photo_url().length()>1){
            Picasso.with(this).load(soruGelen.getUser_profile_photo_url()).fit()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(circleImageViewProfil);
        }


        if(soruGelen.getLike()==1){
            imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }
        else
        {
            imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_bos));
        }

        linearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soruLike(user_id,soruGelen.getId());
            }
        });

        linearLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user_id != soruGelen.getUser_id()){
                    getUserProfileInfo(soruGelen.getUser_id());
                }

            }
        });

    }

    private void checkYanitEkle() {
        textViewYanitEkleError.setVisibility(View.GONE);
        String icerik = editTextYanitIcerik.getText().toString().trim();


        if(icerik.isEmpty() || icerik.length()<1){
            editTextYanitIcerik.setError("Bu alan boş bırakılamaz!");
        }
        else if (icerik.length()<10 || icerik.length()>500)
        {
            editTextYanitIcerik.setError("En az 10 ve en fazla 500 karakter olabilir.");
        }
        else
        {
            hideKeyboard(this);
            yanitYayinla(user_id,soruGelen.getId(),icerik);
        }
    }

    private void getUserProfileInfo(int user_id){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        JSONObject user = new JSONObject(response);

                        int user_id = Integer.parseInt(user.getString("id"));
                        int totel_yanit_size = Integer.parseInt(user.getString("totel_yanit_size"));
                        int totel_sorular_like = Integer.parseInt(user.getString("totel_sorular_like"));
                        int totel_yanitlar_like = Integer.parseInt(user.getString("totel_yanitlar_like"));
                        int totel_en_iyi_yanit_size = Integer.parseInt(user.getString("totel_en_iyi_yanit_size"));
                        String username = user.getString("username");
                        String about = user.getString("about");
                        try {
                            about = URLDecoder.decode(
                                    about, "UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }
                        String profile_photo_url = user.getString("profile_photo_url");

                        JSONArray sorular = user.getJSONArray("sorular");
                        ArrayList<Soru> soruList = new ArrayList<>();

                        for(int j=0; j<sorular.length(); j++){

                            JSONObject soru = (JSONObject) sorular.get(j);

                            int soru_id = Integer.parseInt(soru.getString("id"));
                            int soru_user_id = Integer.parseInt(soru.getString("user_id"));
                            String soru_baslik = soru.getString("baslik");
                            String soru_icerik = soru.getString("icerik");
                            try {
                                soru_baslik = URLDecoder.decode(
                                        soru_baslik, "UTF-8");
                                soru_icerik = URLDecoder.decode(
                                        soru_icerik, "UTF-8");
                            } catch (UnsupportedEncodingException e) {

                            }
                            int soru_like_count = Integer.parseInt(soru.getString("like_count"));
                            String soru_date = soru.getString("created_time");
                            String soru_marka = soru.getString("marka");
                            String soru_model = soru.getString("model");

                            JSONArray soru_yanitlar = soru.getJSONArray("yanitlar");
                            ArrayList<Yanit> soru_yanitlarList = new ArrayList<>();

                            for(int i=0; i<soru_yanitlar.length(); i++) {
                                JSONObject soru_yanit = (JSONObject) soru_yanitlar.get(i);

                                int yanit_id = Integer.parseInt(soru_yanit.getString("id"));
                                int yanit_user_id = Integer.parseInt(soru_yanit.getString("user_id"));
                                int yanit_soru_id = Integer.parseInt(soru_yanit.getString("soru_id"));
                                int yanit_like_count = Integer.parseInt(soru_yanit.getString("like_count"));
                                int yanit_en_iyi = Integer.parseInt(soru_yanit.getString("en_iyi_yanit"));
                                String yanit_icerik = soru_yanit.getString("icerik");
                                try {
                                    yanit_icerik = URLDecoder.decode(
                                            yanit_icerik, "UTF-8");
                                } catch (UnsupportedEncodingException e) {

                                }
                                String yanit_date = soru_yanit.getString("created_time");

                                soru_yanitlarList.add(new Yanit(yanit_id,yanit_user_id,yanit_soru_id,0,yanit_like_count,yanit_en_iyi,yanit_icerik,username,createdTimeToDate(yanit_date),profile_photo_url,null));
                            }
                            soruList.add(new Soru(soru_id,0,soru_like_count,soru_user_id,soru_marka,soru_model,soru_baslik,soru_icerik,username,createdTimeToDate(soru_date),profile_photo_url,null,soru_yanitlarList));
                        }

                        JSONArray yanitlar = user.getJSONArray("yanitlar");
                        ArrayList<Yanit> yanitList = new ArrayList<>();

                        for(int k=0; k<yanitlar.length(); k++){

                            JSONObject yanit = (JSONObject) yanitlar.get(k);

                            int yanit_id = Integer.parseInt(yanit.getString("id"));
                            int yanit_user_id = Integer.parseInt(yanit.getString("user_id"));
                            int yanit_soru_id = Integer.parseInt(yanit.getString("soru_id"));
                            int yanit_like_count = Integer.parseInt(yanit.getString("like_count"));
                            int yanit_en_iyi = Integer.parseInt(yanit.getString("en_iyi_yanit"));
                            String yanit_icerik = yanit.getString("icerik");
                            try {
                                yanit_icerik = URLDecoder.decode(
                                        yanit_icerik, "UTF-8");
                            } catch (UnsupportedEncodingException e) {

                            }
                            String yanit_date = yanit.getString("created_time");

                            yanitList.add(new Yanit(yanit_id,yanit_user_id,yanit_soru_id,0,yanit_like_count,yanit_en_iyi,yanit_icerik,username,createdTimeToDate(yanit_date),profile_photo_url,null));
                        }

                        User anotherUser = new User(user_id,totel_yanit_size,totel_sorular_like+totel_yanitlar_like,totel_en_iyi_yanit_size,username,about,profile_photo_url,soruList,yanitList);
                        Intent intentAnotherProfile = new Intent(SoruYanitActivity.this,AnotherProfileActivity.class);
                        intentAnotherProfile.putExtra("User", (Serializable) anotherUser);
                        startActivity(intentAnotherProfile);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }

    private void yanitYayinla(int user_id, int soru_id,String icerik){
            buttonYanitEkle.setClickable(false);

            String url = "http://yazilimmuhendisim.com/api/database_arababam/yanit_yayinla.php";
            StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    editTextYanitIcerik.setText("");
                    if(response.trim().startsWith("ERR")){
                        Toast.makeText(SoruYanitActivity.this,"Yanıtınız yayınlanırken bir hata meydana geldi!",Toast.LENGTH_SHORT).show();
                        buttonYanitEkle.setClickable(true);
                    }
                    else
                    {
                        if(imageList.size()>0){
                            uploadImage(response.trim());
                        }else
                        {
                            refleshSoruDetail(String.valueOf(user_id),soruGelen.getId());
                            buttonYanitEkle.setClickable(true);
                        }

                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                    buttonYanitEkle.setClickable(true);
                }
            }){
                protected Map<String,String> getParams() throws AuthFailureError {
                    String icerik2="";
                    try {
                        icerik2 = URLEncoder.encode(icerik,
                                "UTF-8");
                    } catch (UnsupportedEncodingException e) {

                    }
                    Map<String,String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(user_id));
                    params.put("soru_id", String.valueOf(soru_id));
                    params.put("icerik", icerik2);
                    return params;
                }
            };
            istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
            Volley.newRequestQueue(this).add(istek);
    }

    private void uploadImage(String yanit_id) {
        progressDialog.show();
            for(int i=0; i<imageList.size();i++){

                Map<String, RequestBody> map = new HashMap<>();
                File file = imageList.get(i);

                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                map.put("file\"; filename=\"" + yanit_id +".jpg"+ "\"", requestBody);
                ApiConfigYanitImageUpload getResponse = AppConfig.getRetrofit().create(ApiConfigYanitImageUpload.class);
                Call<ServerResponse> call = getResponse.upload(yanit_id, map);
                int finalI = i;
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                        if (!response.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                            buttonYanitEkle.setClickable(true);
                        }else
                        {
                            if(finalI == (imageList.size()-1)){
                                progressDialog.dismiss();
                                buttonYanitEkle.setClickable(true);
                                refleshSoruDetail(String.valueOf(user_id),soruGelen.getId());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                        buttonYanitEkle.setClickable(true);
                    }
                });
            }
        }

    private void refleshSoruDetail(String user_id,int soru_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_soru_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        JSONObject soru = new JSONObject(response);

                        int soru_id = Integer.parseInt(soru.getString("id"));
                        int user_id = Integer.parseInt(soru.getString("user_id"));
                        int marka_id = Integer.parseInt(soru.getString("marka_id"));
                        int model_id = Integer.parseInt(soru.getString("model_id"));
                        String baslik = soru.getString("baslik");
                        String icerik = soru.getString("icerik");
                        try {
                            baslik = URLDecoder.decode(
                                    baslik, "UTF-8");
                            icerik = URLDecoder.decode(
                                    icerik, "UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }
                        String marka = soru.getString("marka");
                        String model = soru.getString("model");
                        int like = Integer.parseInt(soru.getString("like"));
                        int like_count = Integer.parseInt(soru.getString("like_count"));
                        String username = soru.getString("username");
                        String user_profile_photo_url = soru.getString("user_profile_photo_url");
                        String date = soru.getString("created_time");
                        ArrayList<String> imagesList = new ArrayList<>();

                        JSONArray jsonArray = soru.getJSONArray("images");

                        for(int a=0; a<jsonArray.length();a++){
                            imagesList.add(jsonArray.getString(a));
                        }

                        JSONArray yanitlar = soru.getJSONArray("yanitlar");

                        ArrayList<Yanit> yanitList = new ArrayList<>();

                        for(int j=0; j<yanitlar.length(); j++){
                            JSONObject yanit = (JSONObject) yanitlar.get(j);

                            int yanit_id = Integer.parseInt(yanit.getString("id"));
                            int yanit_user_id = Integer.parseInt(yanit.getString("user_id"));
                            int yanit_soru_id = Integer.parseInt(yanit.getString("soru_id"));
                            String yanit_icerik = yanit.getString("icerik");
                            try {
                                yanit_icerik = URLDecoder.decode(
                                        yanit_icerik, "UTF-8");
                            } catch (UnsupportedEncodingException e) {

                            }
                            int yanit_like = Integer.parseInt(yanit.getString("like"));
                            int yanit_like_count = Integer.parseInt(yanit.getString("like_count"));
                            int yanit_en_iyi = Integer.parseInt(yanit.getString("en_iyi_yanit"));
                            String yanit_date = yanit.getString("created_time");
                            String yanit_username = yanit.getString("username");
                            String yanit_user_profile_photo_url = yanit.getString("user_profile_photo_url");

                            ArrayList <String> yanit_imagesList = new ArrayList<>();

                            JSONArray jsonArray2 = yanit.getJSONArray("images");

                            for(int b=0; b<jsonArray2.length();b++){
                                yanit_imagesList.add(jsonArray2.getString(b));
                            }

                            yanitList.add(new Yanit(yanit_id,yanit_user_id,yanit_soru_id,yanit_like,yanit_like_count,yanit_en_iyi,yanit_icerik,yanit_username,createdTimeToDate(yanit_date),yanit_user_profile_photo_url,yanit_imagesList));
                        }

                        Soru soruNew = new Soru(soru_id,like,like_count,user_id,marka,model,baslik,icerik,username,createdTimeToDate(date),user_profile_photo_url,imagesList,yanitList);
                        Intent intentSoruYanit = getIntent();
                        intentSoruYanit.putExtra("Soru", (Serializable) soruNew);
                        finish();
                        startActivity(intentSoruYanit);
                        overridePendingTransition(0, 0);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id",user_id);
                params.put("soru_id", String.valueOf(soru_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == 1024) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        try {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            assert cursor != null;
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String mediaPath = cursor.getString(columnIndex);
                            cursor.close();

                            File file = new File(mediaPath);

                            imageList.add(file);
                            imageFileAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            if(imageList.size()<5){
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1024);
            }else
            {
                textViewYanitEkleError.setText("Maksimum 5 tane görsel yükleyebilirsiniz.");
                textViewYanitEkleError.setVisibility(View.VISIBLE);
            }


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2342);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==2342){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if(imageList.size()<5){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1024);
                }else
                {
                    textViewYanitEkleError.setText("Maksimum 5 tane görsel yükleyebilirsiniz.");
                    textViewYanitEkleError.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(getIntent().getStringExtra("back") != null && getIntent().getStringExtra("back").equals("0")){
            Intent intentMain = new Intent(SoruYanitActivity.this,MainActivity.class);
            startActivity(intentMain);
        }
        finish();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void soruLike(int user_id, int soru_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/soru_begen.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("OK"))
                {
                    if(soruGelen.getLike()==1){
                        soruGelen.setLike(0);
                        imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_bos));
                        soruGelen.setLike_size((soruGelen.getLike_size()-1));
                        textViewLikeSize.setText(String.valueOf(soruGelen.getLike_size()));

                    }
                    else
                    {
                        soruGelen.setLike(1);
                        imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                        soruGelen.setLike_size((soruGelen.getLike_size()+1));
                        textViewLikeSize.setText(String.valueOf(soruGelen.getLike_size()));
                    }
                }
                else
                {
                    Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SoruYanitActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("soru_id", String.valueOf(soru_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);

    }

    private String createdTimeToDate(String createdTime){

        String datetime[] = createdTime.split(" ");

        String date[] = datetime[0].split("-");
        String time[] = datetime[1].split(":");

        String aylar[] = {"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};

        int g_gun = Integer.parseInt(date[2].trim());
        int g_ay = Integer.parseInt(date[1].trim());
        int g_yil = Integer.parseInt(date[0].trim());

        Date currentTime = Calendar.getInstance().getTime();
        int gun = currentTime.getDate();
        int ay = currentTime.getMonth()+1;
        int yil = currentTime.getYear()+1900;

        if(yil==g_yil && ay==g_ay && gun==g_gun){
            return "Bugün "+time[0]+":"+time[1];
        }
        else if (yil==g_yil && ay==g_ay && (gun-1)==g_gun)
        {
            return "Dün "+time[0]+":"+time[1];
        }
        else
        {
            return g_gun + " " +aylar[(g_ay-1)] + " "+ g_yil;
        }

    }
}