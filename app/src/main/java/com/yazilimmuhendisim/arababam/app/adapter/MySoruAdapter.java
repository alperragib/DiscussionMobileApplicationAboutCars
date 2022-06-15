package com.yazilimmuhendisim.arababam.app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.activity.AnotherProfileActivity;
import com.yazilimmuhendisim.arababam.app.activity.SoruEditActivity;
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

public class MySoruAdapter extends RecyclerView.Adapter<MySoruAdapter.CardViewTasarimTutucu>{

    private Context context;
    private List<Soru> soruList;
    private boolean anotherProfile;

    public MySoruAdapter(Context context, List<Soru> soruList,boolean anotherProfile){
        this.context=context;
        this.soruList=soruList;
        this.anotherProfile=anotherProfile;
    }

    public class CardViewTasarimTutucu extends RecyclerView.ViewHolder{
        public TextView baslik,icerik,yanit_size,like_size,marka_model,date;
        public ImageView imageView_succ,popup;
        public CardView cardView;
        public CardViewTasarimTutucu(@NonNull View itemView) {
            super(itemView);
            baslik = itemView.findViewById(R.id.textViewMySoruBaslik);
            icerik = itemView.findViewById(R.id.textViewMySoruIcerik);
            yanit_size = itemView.findViewById(R.id.textViewMySoruYanitSize);
            like_size = itemView.findViewById(R.id.textViewMySoruLikeSize);
            imageView_succ = itemView.findViewById(R.id.imageViewMySoruEnIyiYanit);
            cardView = itemView.findViewById(R.id.cardViewMySoru);
            popup = itemView.findViewById(R.id.imageViewMySoruPopup);
            marka_model = itemView.findViewById(R.id.textViewMySoruMarkaModel);
            date = itemView.findViewById(R.id.textViewMySoruDate);

        }
    }

    @NonNull
    @Override
    public MySoruAdapter.CardViewTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_soru_tasarim,parent,false);
        return new CardViewTasarimTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MySoruAdapter.CardViewTasarimTutucu holder, int position) {

        if(anotherProfile){
            holder.popup.setVisibility(View.GONE);
        }else
        {
            holder.popup.setVisibility(View.VISIBLE);
        }

        final Soru soru = soruList.get(position);
        holder.baslik.setText(soru.getBaslik());
        holder.icerik.setText(soru.getIcerik());
        holder.yanit_size.setText(String.valueOf(soru.getYanitlarList().size()));
        holder.like_size.setText(String.valueOf(soru.getLike_size()));
        holder.marka_model.setText(soru.getMarka()+"/"+soru.getModel());
        holder.date.setText(soru.getDate());

        boolean en_iyi = false;

        for(int i=0;i<soru.getYanitlarList().size();i++){
            if(soru.getYanitlarList().get(i).getEn_iyi_yanit()==1){
                en_iyi=true;
                break;
            }
        }

        if(en_iyi){
            holder.imageView_succ.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imageView_succ.setVisibility(View.GONE);
        }


        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.popup);
                popupMenu.getMenuInflater().inflate(R.menu.my_soru_yanit_popup_menu,popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.popup_edit:
                                Intent intentEdit = new Intent(context, SoruEditActivity.class);
                                intentEdit.putExtra("Soru", (Serializable) soru);
                                context.startActivity(intentEdit);
                                return true;
                                
                            case R.id.popup_delete:
                                AlertDialog.Builder ao = new AlertDialog.Builder(context);

                                ao.setTitle("arababam.net");
                                ao.setMessage("Sorunuzu silmek istediğinize emin misiniz?");

                                ao.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                ao.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        soruDelete(soru.getId(),position);
                                    }
                                });

                                ao.create().show();
                                return true;
                            default:return false;
                        }
                    }
                });
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySharedPreferences sp = new MySharedPreferences();
                String user_id = sp.getSharedPreference(context,"user_id","0");
                goSoruDetail(user_id,soru.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return soruList.size();
    }

    private void soruDelete(int soru_id,int position) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/soru_sil.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("OK")){
                    soruList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, soruList.size());
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
                params.put("soru_id", String.valueOf(soru_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(context).add(istek);

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
