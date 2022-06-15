package com.yazilimmuhendisim.arababam.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.activity.AnotherProfileActivity;
import com.yazilimmuhendisim.arababam.app.activity.LoginActivity;
import com.yazilimmuhendisim.arababam.app.activity.SoruYanitActivity;
import com.yazilimmuhendisim.arababam.app.activity.SplashActivity;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Soru;
import com.yazilimmuhendisim.arababam.app.model.User;
import com.yazilimmuhendisim.arababam.app.model.Yanit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class YanitAdapter extends RecyclerView.Adapter<YanitAdapter.CardViewTasarimTutucu>{

    private Context context;
    private Activity activity;
    private List<Yanit> yanitList;
    private int soru_user_id;

    public YanitAdapter(Context context, Activity activity, List<Yanit> yanitList,int soru_user_id){
        this.context=context;
        this.activity=activity;
        this.yanitList=yanitList;
        this.soru_user_id=soru_user_id;
    }

    public class CardViewTasarimTutucu extends RecyclerView.ViewHolder{
        public TextView icerik,like_size,username,date;
        public CircleImageView imageViewProfile;
        public ImageView imageViewLike,imageViewEnIyiYanit;
        public LinearLayout enIyiYanit,like_button,linearprofile,linearEnIyiYanit;
        public RecyclerView recyclerView;
        public RelativeLayout relativeLayout;

        public CardViewTasarimTutucu(@NonNull View itemView) {
            super(itemView);
            icerik = itemView.findViewById(R.id.textViewYanitIcerik);
            username = itemView.findViewById(R.id.textViewYanitUsername);
            date = itemView.findViewById(R.id.textViewYanitDate);
            like_size = itemView.findViewById(R.id.textViewYanitLikeSize);
            imageViewProfile = itemView.findViewById(R.id.circleImageViewYanitProfile);
            imageViewLike = itemView.findViewById(R.id.imageViewYanitLike);
            enIyiYanit = itemView.findViewById(R.id.linearLayoutYanitEnIyiYanit);
            recyclerView = itemView.findViewById(R.id.recylerViewYanitImages);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutYanitCizgi);
            like_button = itemView.findViewById(R.id.linearLayoutYanitLike);
            linearprofile = itemView.findViewById(R.id.linearLayotYanitProfile);
            linearEnIyiYanit = itemView.findViewById(R.id.linearLayoutYanitEnIyi);
            imageViewEnIyiYanit = itemView.findViewById(R.id.imageViewYanitEnIyi);
        }
    }

    @NonNull
    @Override
    public YanitAdapter.CardViewTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.yanit_tasarim,parent,false);
        return new CardViewTasarimTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YanitAdapter.CardViewTasarimTutucu holder, int position) {

        final Yanit yanit = yanitList.get(position);

        MySharedPreferences sp = new MySharedPreferences();
        int user_id = Integer.parseInt(sp.getSharedPreference(context,"user_id","0"));

        if(soru_user_id==user_id && user_id!=yanit.getUser_id()){

            holder.linearEnIyiYanit.setVisibility(View.VISIBLE);

            if(yanit.getEn_iyi_yanit()==1)
            {
                holder.imageViewEnIyiYanit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_box));
            }
            else
            {
                holder.imageViewEnIyiYanit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_box_outline));
            }

        }
        else
        {
            holder.linearEnIyiYanit.setVisibility(View.GONE);
        }

        holder.linearEnIyiYanit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ao = new AlertDialog.Builder(context);

                ao.setTitle("arababam.net");

                if(yanit.getEn_iyi_yanit()==1){
                    ao.setMessage("Bu yanıtı en iyi yanıt seçiminden kaldırmak istediğinize emin misiniz?");
                }
                else
                {
                    ao.setMessage("Bu yanıtı sorunuz için en iyi yanıt olarak seçmek istediğinize emin misiniz?");
                }



                ao.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                ao.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setEnIyiYanit(yanit.getId(),yanit.getSoru_id());

                    }
                });

                ao.create().show();
            }
        });

        if(yanitList.size() == (position+1))
        {
            holder.relativeLayout.setVisibility(View.GONE);
        }

        holder.icerik.setText(yanit.getIcerik());
        holder.like_size.setText(String.valueOf(yanit.getLike_size()));
        holder.date.setText(yanit.getDate());
        holder.username.setText(yanit.getUsername());

        if(yanit.getUser_profile_photo_url()!=null && yanit.getUser_profile_photo_url().length()>1){
            Picasso.with(context).load(yanit.getUser_profile_photo_url()).fit()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(holder.imageViewProfile);
        }

        if(yanit.getEn_iyi_yanit()==1){
            holder.enIyiYanit.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.enIyiYanit.setVisibility(View.GONE);
        }

        if(yanit.getLike()==1){
            holder.imageViewLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite));
        }
        else
        {
            holder.imageViewLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_bos));
        }

        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                yanitLike(holder, yanit, user_id, yanit.getId());
            }
        });

        if(yanit.getImagesList() == null || yanit.getImagesList().size()<1){
            holder.recyclerView.setVisibility(View.GONE);
        }
        else
        {
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

            ImageUrlAdapter imageUrlAdapter = new ImageUrlAdapter(context, yanit.getImagesList(),false);
            holder.recyclerView.setAdapter(imageUrlAdapter);
        }

        holder.linearprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user_id != yanit.getUser_id()) {
                    getUserProfileInfo(yanit.getUser_id());
                }

            }
        });

        String test = yanit.getIcerik();
        SpannableString spannable = new SpannableString(test);
        final Matcher matcher = Pattern.compile("@\\s*(\\w+)").matcher(test);
        while (matcher.find()) {
            final String username = matcher.group(1);

            String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_id_from_username.php";
            StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(!response.trim().startsWith("ERR"))
                    {
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View textView) {
                                getUserProfileInfo(Integer.parseInt(response.trim()));
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                ds.setColor(context.getResources().getColor(R.color.secondaryColor));
                            }
                        };
                        int cityIndex = test.indexOf(username) - 1;
                        spannable.setSpan(clickableSpan, cityIndex, cityIndex + username.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }){
                protected Map<String,String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<>();
                    params.put("username", username);
                    return params;
                }
            };
            istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
            Volley.newRequestQueue(context).add(istek);

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.icerik.setText(spannable);
                holder.icerik.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }, 2500);


    }

    private void setEnIyiYanit(int yanit_id,int soru_id) {

        MySharedPreferences sp = new MySharedPreferences();
        String user_id =sp.getSharedPreference(context,"user_id","0");

        String url = "http://yazilimmuhendisim.com/api/database_arababam/set_en_iyi_yanit.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("OK")){
                    goSoruDetail(user_id,soru_id);
                }
                else
                {
                    Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("yanit_id", String.valueOf(yanit_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(context).add(istek);
    }

    @Override
    public int getItemCount() {
        return yanitList.size();
    }

    private void goSoruDetail(String user_id,int soru_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_soru_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
                        Intent intentSoruYanit = new Intent(context, SoruYanitActivity.class);
                        intentSoruYanit.putExtra("Soru", (Serializable) soruNew);
                        activity.finish();
                        context.startActivity(intentSoruYanit);
                        activity.overridePendingTransition(0, 0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(context).add(istek);

    }


    private void getUserProfileInfo(int user_id){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
                        Intent intentAnotherProfile = new Intent(context,AnotherProfileActivity.class);
                        intentAnotherProfile.putExtra("User", (Serializable) anotherUser);
                        context.startActivity(intentAnotherProfile);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(context).add(istek);
    }

    private void yanitLike(YanitAdapter.CardViewTasarimTutucu holder, Yanit yanit, int user_id, int yanit_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/yanit_begen.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("OK"))
                {
                    if(yanit.getLike()==1){
                        yanit.setLike(0);
                        holder.imageViewLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_bos));
                        yanit.setLike_size((yanit.getLike_size()-1));
                        holder.like_size.setText(String.valueOf(yanit.getLike_size()));
                    }
                    else
                    {
                        yanit.setLike(1);
                        holder.imageViewLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite));
                        yanit.setLike_size((yanit.getLike_size()+1));
                        holder.like_size.setText(String.valueOf(yanit.getLike_size()));
                    }
                }
                else
                {
                    Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("yanit_id", String.valueOf(yanit_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(context).add(istek);

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
