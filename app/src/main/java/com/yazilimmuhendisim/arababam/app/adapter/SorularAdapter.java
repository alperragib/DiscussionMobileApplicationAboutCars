package com.yazilimmuhendisim.arababam.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.activity.SoruYanitActivity;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Soru;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SorularAdapter extends RecyclerView.Adapter<SorularAdapter.CardViewTasarimTutucu>{

    private Context context;
    private List<Soru> soruList;
    private InterstitialAd mInterstitialAd;

    public SorularAdapter(Context context, List<Soru> soruList){
        this.context=context;
        this.soruList=soruList;
    }

    public class CardViewTasarimTutucu extends RecyclerView.ViewHolder{
        public TextView baslik,icerik,yanit_size,username,date,marka_model;
        public CircleImageView imageViewProfile;
        public ImageView imageViewEnIyiYanit;
        public CardView cardViewSoru;

        public CardViewTasarimTutucu(@NonNull View itemView) {
            super(itemView);
            baslik = itemView.findViewById(R.id.textViewSorularBaslik);
            icerik = itemView.findViewById(R.id.textViewSorularIcerik);
            yanit_size = itemView.findViewById(R.id.textViewSorularYanitSize);
            username = itemView.findViewById(R.id.textViewSorularUsername);
            date = itemView.findViewById(R.id.textViewSorularDate);
            imageViewProfile = itemView.findViewById(R.id.circleImageViewSorularProfile);
            imageViewEnIyiYanit = itemView.findViewById(R.id.imageViewSorularEnIyiYanit);
            cardViewSoru = itemView.findViewById(R.id.cardViewSorular);
            marka_model = itemView.findViewById(R.id.textViewSorularMarkaModel);

            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {}
            });

            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId("ca-app-pub-3332967002509193/6028638956");
            mInterstitialAd.setAdListener(new AdListener()
            {
                @Override
                public void onAdLoaded()
                {

                }
                @Override
                public void onAdFailedToLoad(int errorCode)
                {

                }
                @Override
                public void onAdClosed()
                {

                }
            });
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

        }
    }

    @NonNull
    @Override
    public SorularAdapter.CardViewTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.soru_tasarim,parent,false);
        return new CardViewTasarimTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SorularAdapter.CardViewTasarimTutucu holder, int position) {
        MySharedPreferences sp = new MySharedPreferences();
        String user_id = sp.getSharedPreference(context,"user_id","0");

        final Soru soru = soruList.get(position);
        holder.baslik.setText(soru.getBaslik());
        holder.icerik.setText(soru.getIcerik());
        holder.yanit_size.setText(String.valueOf(soru.getYanitlarList().size()));
        holder.username.setText(soru.getUsername());
        holder.date.setText(soru.getDate());
        holder.marka_model.setText(soru.getMarka()+"/"+soru.getModel());

        if(soru.getUser_profile_photo_url()!=null && soru.getUser_profile_photo_url().startsWith("http")){
            Picasso.with(context).load(soru.getUser_profile_photo_url()).fit()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(holder.imageViewProfile);
        }

        boolean en_iyi = false;

        for(int i=0;i<soru.getYanitlarList().size();i++){
            if(soru.getYanitlarList().get(i).getEn_iyi_yanit()==1){
                en_iyi=true;
                break;
            }
        }

        if(en_iyi){
            holder.imageViewEnIyiYanit.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imageViewEnIyiYanit.setVisibility(View.GONE);
        }

        holder.cardViewSoru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSoruDetail(user_id,soru.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return soruList.size();
    }

    private void goSoruDetail(String user_id,int soru_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_soru_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(context,"Soru bulunamadı!",Toast.LENGTH_SHORT).show();
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
                            context.startActivity(intentSoruYanit);

                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        else
                        {
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }

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
