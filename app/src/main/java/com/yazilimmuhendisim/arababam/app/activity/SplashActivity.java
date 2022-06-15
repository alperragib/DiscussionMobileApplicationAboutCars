package com.yazilimmuhendisim.arababam.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    MySharedPreferences sp;
    TextView textView_durum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView_durum = findViewById(R.id.textView_splash_durum);

        sp = new MySharedPreferences();

        String user_id = sp.getSharedPreference(this,"user_id","0");
        checkUser(user_id);

    }

    private void checkUser(String user_id){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                userAgrement();
                getMarkaModel();
                if(response.trim().equals("ERR-3")){

                    sp.removeSharedPreference(SplashActivity.this,"user_id");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent loginIntent = new Intent(SplashActivity.this,LoginActivity.class);
                            startActivity(loginIntent);
                            finish();
                        }
                    }, 2000);
                }
                else if(response.trim().startsWith("ERR")){
                    textView_durum.setText("İnternet bağlantısı bulunamadı...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkUser(user_id);
                        }
                    }, 2000);
                }
                else{

                    String okunan = dahiliOku("marka_model.txt");
                    if(okunan != null && !okunan.isEmpty() && okunan.length()>1){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }, 1000);
                    }else
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkUser(user_id);
                            }
                        }, 2000);
                    }

                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                textView_durum.setText("İnternet bağlantısı bulunamadı...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkUser(user_id);
                    }
                }, 2000);
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

    private void userAgrement(){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_agreement.php";
        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dahiliSil("user_agr.txt");
                dahiliYaz(response,"user_agr.txt");
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }

    private void getMarkaModel(){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_car_marka_model.php";
        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(!response.startsWith("ERR")){
                    dahiliSil("marka_model.txt");
                    dahiliYaz(response,"marka_model.txt");
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }

    public void dahiliYaz(String json,String fileName){
        try {
            FileOutputStream fos = openFileOutput(fileName,MODE_PRIVATE);
            OutputStreamWriter yazici = new OutputStreamWriter(fos);
            yazici.write(json);
            yazici.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dahiliSil(String fileName){

        try {
            File yol = getFilesDir();
            File file = new File(yol,fileName);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public String dahiliOku(String fileName){
        try {
            FileInputStream fis = this.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader okuyucu = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String satir = "";

            while ((satir = okuyucu.readLine()) != null){
                sb.append(satir+"\n");
            }
            okuyucu.close();
            return sb.toString();

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}