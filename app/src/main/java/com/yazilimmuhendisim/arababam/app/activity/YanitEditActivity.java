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
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.adapter.ImageUrlAdapter;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Soru;
import com.yazilimmuhendisim.arababam.app.model.Yanit;
import com.yazilimmuhendisim.arababam.app.network.ApiConfigYanitEditImageUpload;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YanitEditActivity extends AppCompatActivity {

    private EditText editTextIcerik;
    private TextView textViewError;
    private ArrayList<String> imageUrlList;
    private ImageUrlAdapter imageUrlAdapter;
    private Yanit yanit;
    private ProgressDialog progressDialog;
    private Button buttonEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yanit_edit);

        yanit = (Yanit) getIntent().getSerializableExtra("Yanit");

        if(yanit==null){
            finish();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Görsel ekleniyor lütfen bekleyiniz...");
        progressDialog.setCancelable(false);

        editTextIcerik = findViewById(R.id.editTextYanitEditİcerik);
        textViewError = findViewById(R.id.textViewYanitEditError);
        CardView cardViewImageSec = findViewById(R.id.cardViewYanitEditImage);
        ImageView imageViewBack = findViewById(R.id.imageViewYanitEditBack);
        RecyclerView recyclerViewImage = findViewById(R.id.recylerViewYanitEditImage);
        buttonEdit = findViewById(R.id.buttonYanitEditGüncelle);

        editTextIcerik.setText(yanit.getIcerik());

        imageUrlList = new ArrayList<>();

        if(yanit.getImagesList()!=null && yanit.getImagesList().size()>0){
            imageUrlList.addAll(yanit.getImagesList());
        }

        recyclerViewImage.setHasFixedSize(true);
        recyclerViewImage.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        imageUrlAdapter = new ImageUrlAdapter(this, imageUrlList,true);
        recyclerViewImage.setAdapter(imageUrlAdapter);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cardViewImageSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUrlList.size()<5){
                    requestStoragePermission();
                }
                else {
                    Toast.makeText(YanitEditActivity.this,"Bir soruda maksimum 5 adet görsel yükleyebilirsin!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEdit();
            }
        });


    }

    private void checkEdit() {
        textViewError.setVisibility(View.GONE);
        String icerik = editTextIcerik.getText().toString().trim();

        if(icerik.isEmpty() || icerik.length()<1){
            editTextIcerik.setError("Bu alan boş bırakılamaz!");
        }
        else if (icerik.length()<10 || icerik.length()>500)
        {
            editTextIcerik.setError("En az 10 ve en fazla 500 karakter olabilir.");
        }
        else
        {
            hideKeyboard(this);
            yanitGuncelle(yanit.getId(),icerik,imageUrlList.toString(),yanit.getSoru_id());
        }
    }

    private void yanitGuncelle(int yanit_id, String icerik, String image_list, int soru_id){

        buttonEdit.setClickable(false);

        MySharedPreferences sp = new MySharedPreferences();
        int user_id = Integer.parseInt(sp.getSharedPreference(this,"user_id","0"));

        String url = "http://yazilimmuhendisim.com/api/database_arababam/yanit_guncelle.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("OK")){
                    goSoruDetail(user_id,soru_id);
                }
                else
                {
                    textViewError.setText("İnternet bağlantısı bulunamadı!");
                    textViewError.setVisibility(View.VISIBLE);
                    buttonEdit.setClickable(true);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewError.setText("İnternet bağlantısı bulunamadı!");
                textViewError.setVisibility(View.VISIBLE);
                buttonEdit.setClickable(true);
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
                params.put("yanit_id", String.valueOf(yanit_id));
                params.put("icerik", icerik2);
                params.put("image_list", image_list);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }

    private void uploadImage(File file) {
        progressDialog.show();

        String yanit_id = String.valueOf(yanit.getId());

        Map<String, RequestBody> map = new HashMap<>();

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        map.put("file\"; filename=\"" + yanit_id +".jpg"+ "\"", requestBody);
        ApiConfigYanitEditImageUpload getResponse = AppConfig.getRetrofit().create(ApiConfigYanitEditImageUpload.class);
        Call<ServerResponse> call = getResponse.upload(yanit_id, map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                if (!response.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(YanitEditActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }else
                {
                    progressDialog.dismiss();
                    imageUrlList.add(response.body().getMessage().trim());
                    imageUrlAdapter.notifyItemInserted(imageUrlList.size() - 1);
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(YanitEditActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goSoruDetail(int user_id,int soru_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_soru_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(YanitEditActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
                        Intent intentSoruYanit = new Intent(YanitEditActivity.this,SoruYanitActivity.class);
                        intentSoruYanit.putExtra("Soru", (Serializable) soruNew);
                        startActivity(intentSoruYanit);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(YanitEditActivity.this,"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
                            uploadImage(file);


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

            if(imageUrlList.size()<5){
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1024);
            }else
            {
                textViewError.setText("Maksimum 5 tane görsel yükleyebilirsiniz.");
                textViewError.setVisibility(View.VISIBLE);
            }


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2342);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==2342){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if(imageUrlList.size()<5){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1024);
                }else
                {
                    textViewError.setText("Maksimum 5 tane görsel yükleyebilirsiniz.");
                    textViewError.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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