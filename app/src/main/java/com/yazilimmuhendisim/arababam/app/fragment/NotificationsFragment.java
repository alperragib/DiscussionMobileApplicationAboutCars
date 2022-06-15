package com.yazilimmuhendisim.arababam.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.activity.MainActivity;
import com.yazilimmuhendisim.arababam.app.adapter.BildirimAdapter;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Bildirim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.yazilimmuhendisim.arababam.app.activity.MainActivity.bildirimList;

public class NotificationsFragment extends Fragment {

    public static LinearLayout linearLayoutNotFound;
    public static RecyclerView recyclerView;
    String user_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        Button buttonTekrarDene = root.findViewById(R.id.buttonNotificationNotFoundTekrardene);
        linearLayoutNotFound = root.findViewById(R.id.linearLayoutNotificationNotFound);
        recyclerView = root.findViewById(R.id.recylerViewNotification);

        MySharedPreferences sp = new MySharedPreferences();
        user_id = sp.getSharedPreference(getContext(),"user_id","0");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));


        buttonTekrarDene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserNotification(user_id);
            }
        });



        return root;
    }

    private void getUserNotification(String user_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_notification.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(getContext(),"Henüz hiç bildiriminiz yok!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    bildirimList = new ArrayList<>();
                    try {
                        JSONArray notifications = new JSONArray(response);

                        for(int i=0; i<notifications.length(); i++){
                            JSONObject notification = (JSONObject) notifications.get(i);
                            int notification_id = Integer.parseInt(notification.getString("id"));
                            int soru_id = Integer.parseInt(notification.getString("soru_id"));
                            int okundu = Integer.parseInt(notification.getString("okundu"));
                            String baslik = notification.getString("title");
                            String icerik = notification.getString("content");
                            String date = notification.getString("created_time");

                            bildirimList.add(new Bildirim(notification_id,soru_id,okundu,baslik,icerik,createdTimeToDate(date)));
                        }

                        if(bildirimList != null && bildirimList.size()>0){
                            linearLayoutNotFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            BildirimAdapter bildirimAdapter = new BildirimAdapter(getContext(), bildirimList);
                            recyclerView.setAdapter(bildirimAdapter);
                        }
                        else
                        {
                            recyclerView.setVisibility(View.GONE);
                            linearLayoutNotFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id",user_id);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(getContext()).add(istek);

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