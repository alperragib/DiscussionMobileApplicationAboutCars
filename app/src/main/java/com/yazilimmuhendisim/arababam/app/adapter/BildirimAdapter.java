package com.yazilimmuhendisim.arababam.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.activity.SoruYanitActivity;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Bildirim;
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

public class BildirimAdapter extends RecyclerView.Adapter<BildirimAdapter.CardViewTasarimTutucu>{

    private Context context;
    private List<Bildirim> bildirimList;

    public BildirimAdapter(Context context, List<Bildirim> bildirimList){
        this.context=context;
        this.bildirimList=bildirimList;
    }

    public class CardViewTasarimTutucu extends RecyclerView.ViewHolder{
        public TextView title,content,date;
        public CardView cardView;

        public CardViewTasarimTutucu(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewBildirimTitle);
            content = itemView.findViewById(R.id.textViewBildirimContent);
            date = itemView.findViewById(R.id.textViewBildirimDate);
            cardView = itemView.findViewById(R.id.cardViewBildirim);
        }
    }

    @NonNull
    @Override
    public BildirimAdapter.CardViewTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bildirim_tasarim,parent,false);
        return new CardViewTasarimTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BildirimAdapter.CardViewTasarimTutucu holder, int position) {
        MySharedPreferences sp = new MySharedPreferences();
        String user_id = sp.getSharedPreference(context,"user_id","0");

        final Bildirim bildirim = bildirimList.get(position);
        holder.date.setText(bildirim.getDate());
        holder.title.setText(bildirim.getTitle());
        holder.content.setText(bildirim.getContent());

        if(bildirim.getOkundu() == 0)
        {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }
        else
        {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.grey2));
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSoruDetail(user_id,bildirim.getSoru_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return bildirimList.size();
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
