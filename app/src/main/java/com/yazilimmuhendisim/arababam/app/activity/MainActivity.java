package com.yazilimmuhendisim.arababam.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.adapter.BildirimAdapter;
import com.yazilimmuhendisim.arababam.app.fragment.SoruSorFragment;
import com.yazilimmuhendisim.arababam.app.fragment.SorularFragment;
import com.yazilimmuhendisim.arababam.app.fragment.NotificationsFragment;
import com.yazilimmuhendisim.arababam.app.fragment.ProfileFragment;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Bildirim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    Fragment active;
    public static ArrayList<Bildirim> bildirimList;
    MySharedPreferences sp;
    BadgeDrawable badgeDrawable;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black_a));

        navView = findViewById(R.id.nav_view);

        sp = new MySharedPreferences();

        badgeDrawable = navView.getOrCreateBadge(R.id.navigation_notifications);
        badgeDrawable.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        badgeDrawable.setVisible(false);

        String user_id = sp.getSharedPreference(MainActivity.this,"user_id","0");
        getUserNotification(user_id);

        Fragment fragment1 = new SorularFragment();
        Fragment fragment2 = new SoruSorFragment();
        Fragment fragment3 = new NotificationsFragment();
        Fragment fragment4 = new ProfileFragment();

        FragmentManager fm = getSupportFragmentManager();

        active = fragment1;

        fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4).addToBackStack("tag").commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3).addToBackStack("tag").commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2).addToBackStack("tag").commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").addToBackStack("tag").commit();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_sorular:
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black_a));
                        return true;

                    case R.id.navigation_sorusor:
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black_a));
                        return true;

                    case R.id.navigation_notifications:

                        fm.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black_a));

                        getUserNotification(user_id);
                        setUserNotificationOkundu(user_id);
                        return true;

                    case R.id.navigation_profile:
                        fm.beginTransaction().hide(active).show(fragment4).commit();
                        active = fragment4;

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.primaryColor));
                        return true;
                }
                return false;
            }
        });

        MySharedPreferences sp = new MySharedPreferences();
        String username = sp.getSharedPreference(this,"username","null");
        String email = sp.getSharedPreference(this,"email","null");

        if(username.equals("null") || email.equals("null")){
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ao = new AlertDialog.Builder(MainActivity.this);
        ao.setTitle("arababam.net");
        ao.setMessage("Uygulamadan çıkmak istediğinize emin misiniz?");

        ao.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        ao.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finishAffinity();
                System.exit(0);
            }
        });

        ao.create().show();
    }

    private void getUserNotification(String user_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_notification.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){

                }
                else
                {
                    bildirimList = new ArrayList<>();
                    try {
                        JSONArray notifications = new JSONArray(response);

                        int okunmayan_size = 0;

                        for(int i=0; i<notifications.length(); i++){
                            JSONObject notification = (JSONObject) notifications.get(i);
                            int notification_id = Integer.parseInt(notification.getString("id"));
                            int soru_id = Integer.parseInt(notification.getString("soru_id"));
                            int okundu = Integer.parseInt(notification.getString("okundu"));
                            String baslik = notification.getString("title");
                            String icerik = notification.getString("content");
                            String date = notification.getString("created_time");

                            if(okundu == 0){
                                okunmayan_size++;
                            }
                            bildirimList.add(new Bildirim(notification_id,soru_id,okundu,baslik,icerik,createdTimeToDate(date)));
                        }

                        if(okunmayan_size>0)
                        {
                            badgeDrawable.setVisible(true);
                            badgeDrawable.setNumber(okunmayan_size);
                        }
                        else
                        {
                            badgeDrawable.setVisible(false);
                        }

                        if(bildirimList != null && bildirimList.size()>0){
                            NotificationsFragment.linearLayoutNotFound.setVisibility(View.GONE);
                            NotificationsFragment.recyclerView.setVisibility(View.VISIBLE);
                            BildirimAdapter bildirimAdapter = new BildirimAdapter(MainActivity.this, bildirimList);
                            NotificationsFragment.recyclerView.setAdapter(bildirimAdapter);
                        }
                        else
                        {
                            NotificationsFragment.recyclerView.setVisibility(View.GONE);
                            NotificationsFragment.linearLayoutNotFound.setVisibility(View.VISIBLE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        Volley.newRequestQueue(this).add(istek);

    }

    private void setUserNotificationOkundu(String user_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/set_notification_okundu.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("OK")){
                    badgeDrawable.setVisible(false);
                    badgeDrawable.clearNumber();
                    navView.removeBadge(R.id.navigation_notifications);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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