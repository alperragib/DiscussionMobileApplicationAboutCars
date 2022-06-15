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
import android.widget.LinearLayout;
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
import com.yazilimmuhendisim.arababam.app.activity.SoruYanitActivity;
import com.yazilimmuhendisim.arababam.app.activity.YanitEditActivity;
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

public class MyYanitAdapter extends RecyclerView.Adapter<MyYanitAdapter.CardViewTasarimTutucu>{

    private Context context;
    private List<Yanit> yanitList;
    private boolean anotherProfile;

    public MyYanitAdapter(Context context, List<Yanit> yanitList,boolean anotherProfile){
        this.context=context;
        this.yanitList=yanitList;
        this.anotherProfile=anotherProfile;
    }

    public class CardViewTasarimTutucu extends RecyclerView.ViewHolder{
        public TextView icerik,like_size,date;
        public ImageView popup;
        public LinearLayout en_iyi_yanit;
        public CardView cardViewYanit;
        public CardViewTasarimTutucu(@NonNull View itemView) {
            super(itemView);
            icerik = itemView.findViewById(R.id.textViewMyYanitIcerik);
            like_size = itemView.findViewById(R.id.textViewMyYanitLikeSize);
            en_iyi_yanit = itemView.findViewById(R.id.linearLayoutwMyYanitEnIyiYanit);
            popup = itemView.findViewById(R.id.imageViewMyYanitPopup);
            cardViewYanit = itemView.findViewById(R.id.cardViewYanit);
            date = itemView.findViewById(R.id.textViewMyYanitDate);
        }
    }

    @NonNull
    @Override
    public MyYanitAdapter.CardViewTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_yanit_tasarim,parent,false);
        return new CardViewTasarimTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyYanitAdapter.CardViewTasarimTutucu holder, int position) {

        if(anotherProfile){
            holder.popup.setVisibility(View.GONE);
        }else
        {
            holder.popup.setVisibility(View.VISIBLE);
        }

        final Yanit yanit = yanitList.get(position);
        holder.icerik.setText(yanit.getIcerik());
        holder.like_size.setText(String.valueOf(yanit.getLike_size()));
        holder.date.setText(yanit.getDate());

        if(yanit.getEn_iyi_yanit()==1){
            holder.en_iyi_yanit.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.en_iyi_yanit.setVisibility(View.GONE);
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
                                Intent intentEdit = new Intent(context, YanitEditActivity.class);
                                intentEdit.putExtra("Yanit", (Serializable) yanit);
                                context.startActivity(intentEdit);
                                return true;

                            case R.id.popup_delete:
                                AlertDialog.Builder ao = new AlertDialog.Builder(context);

                                ao.setTitle("arababam.net");
                                ao.setMessage("Yanıtınızı silmek istediğinize emin misiniz?");

                                ao.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                ao.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        yanitDelete(yanit.getId(),position);
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

        holder.cardViewYanit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySharedPreferences sp = new MySharedPreferences();
                String user_id = sp.getSharedPreference(context,"user_id","0");
                goSoruDetail(user_id,yanit.getSoru_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return yanitList.size();
    }

    private void yanitDelete(int yanit_id,int position) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/yanit_sil.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("OK")){
                    yanitList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, yanitList.size());
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
