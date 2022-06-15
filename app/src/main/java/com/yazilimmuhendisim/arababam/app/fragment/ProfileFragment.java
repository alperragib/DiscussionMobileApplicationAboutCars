package com.yazilimmuhendisim.arababam.app.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.yazilimmuhendisim.arababam.app.activity.FullImageActivity;
import com.yazilimmuhendisim.arababam.app.activity.LoginActivity;
import com.yazilimmuhendisim.arababam.app.adapter.MySoruAdapter;
import com.yazilimmuhendisim.arababam.app.adapter.MyYanitAdapter;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Soru;
import com.yazilimmuhendisim.arababam.app.model.User;
import com.yazilimmuhendisim.arababam.app.model.Yanit;
import com.yazilimmuhendisim.arababam.app.network.ApiConfigProfileImageUpload;
import com.yazilimmuhendisim.arababam.app.network.AppConfig;
import com.yazilimmuhendisim.arababam.app.network.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

import static android.app.Activity.RESULT_CANCELED;

public class ProfileFragment extends Fragment {

    private User userNew;
    private LinearLayout linearLayoutNotFound;
    private TextView textViewNotFound,textViewYanitSize,textViewLikeSize,textViewEnIyiYanitSize,textViewAbout,textViewUsername,textViewSorular,textViewYanitlar;
    private RecyclerView recyclerView;
    private MySoruAdapter soruAdapter;
    private MyYanitAdapter yanitAdapter;
    private CircleImageView imageViewProfilePhoto;
    private ProgressDialog progressDialog;
    private int user_id;
    private MySharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = root.findViewById(R.id.recylerViewProfile);
        ImageView imageViewExit = root.findViewById(R.id.imageViewProfileExitAccount);
        textViewSorular = root.findViewById(R.id.textViewProfileSorularim);
        textViewYanitlar = root.findViewById(R.id.textViewProfileYanitlarim);
        textViewAbout = root.findViewById(R.id.textViewProfileAbout);
        textViewUsername = root.findViewById(R.id.textViewProfileUsername);
        textViewYanitSize = root.findViewById(R.id.textViewProfileYanitSize);
        textViewNotFound = root.findViewById(R.id.textViewProfileNotFound);
        linearLayoutNotFound = root.findViewById(R.id.linearLayoutProfileNotFound);
        textViewLikeSize = root.findViewById(R.id.textViewProfileLikeSize);
        textViewEnIyiYanitSize = root.findViewById(R.id.textViewProfileEnIyiYanitSize);
        imageViewProfilePhoto = root.findViewById(R.id.circleImageViewProfilePhoto);

        sp = new MySharedPreferences();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Profil fotoğrafınız güncelleniyor lütfen bekleyiniz...");
        progressDialog.setCancelable(false);

        MySharedPreferences sp = new MySharedPreferences();
        user_id = Integer.parseInt(sp.getSharedPreference(getContext(),"user_id","0"));

        getUserProfileInfo(user_id,false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));


        if(userNew == null){
            textViewNotFound.setText("Soru bulunamadı!");
            linearLayoutNotFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        textViewSorular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userNew != null){
                    if(userNew.getSoruList().size()<1){
                        textViewNotFound.setText("Soru bulunamadı!");
                        linearLayoutNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else {
                        linearLayoutNotFound.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        recyclerView.setAdapter(soruAdapter);
                        recyclerView.setHasFixedSize(true);
                    }
                    textViewYanitlar.setBackgroundColor(getResources().getColor(R.color.white));
                    textViewYanitlar.setTextColor(getResources().getColor(R.color.secondaryColor));

                    textViewSorular.setBackgroundColor(getResources().getColor(R.color.black_a));
                    textViewSorular.setTextColor(getResources().getColor(R.color.white));

                    textViewSorular.setClickable(false);
                    textViewYanitlar.setClickable(true);
                }

            }
        });

        textViewYanitlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userNew != null){
                    if(userNew.getYanitList().size()<1){
                        textViewNotFound.setText("Yanıt bulunamadı!");
                        linearLayoutNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else
                    {
                        linearLayoutNotFound.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        recyclerView.setAdapter(yanitAdapter);
                        recyclerView.setHasFixedSize(true);
                    }
                    textViewSorular.setBackgroundColor(getResources().getColor(R.color.white));
                    textViewSorular.setTextColor(getResources().getColor(R.color.secondaryColor));

                    textViewYanitlar.setBackgroundColor(getResources().getColor(R.color.black_a));
                    textViewYanitlar.setTextColor(getResources().getColor(R.color.white));

                    textViewYanitlar.setClickable(false);
                    textViewSorular.setClickable(true);
                }

            }
        });

        textViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileAboutAlertDialog(v);
            }
        });

        imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageAlertDialog(v);
            }
        });

        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ao = new AlertDialog.Builder(getContext());

                ao.setTitle("arababam.net");
                ao.setMessage("Hesabınızdan çıkış yapmak istediğinize emin misiniz?");

                ao.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                ao.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sp.removeSharedPreference(root.getContext(),"user_id");
                        sp.removeSharedPreference(root.getContext(),"username");
                        sp.removeSharedPreference(root.getContext(),"email");
                        sp.removeSharedPreference(root.getContext(),"token");
                        sp.removeSharedPreference(root.getContext(),"about");
                        sp.removeSharedPreference(root.getContext(),"profile_photo_url");

                        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(loginIntent);

                        if(getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                });

                ao.create().show();



            }
        });

        return root;
    }

    public void showEditProfileAboutAlertDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_user_about_edit, null);

        EditText editText = customLayout.findViewById(R.id.editTextDialogUserAboutEdit);
        TextView textViewIptal = customLayout.findViewById(R.id.textViewDialogUserAboutEditIptal);
        TextView textViewGuncelle = customLayout.findViewById(R.id.textViewDialogUserAboutEditGuncelle);

        if(userNew.getAbout()!=null && userNew.getAbout().length()>0){
            editText.setText(userNew.getAbout());
        }

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        textViewGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String about = editText.getText().toString().trim();

                if (about.length()>150)
                {
                    editText.setError("En fazla 150 karakter olabilir.");
                }
                else
                {
                    updateUserAbout(user_id,about,dialog);
                }

            }
        });

        textViewIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateUserAbout(int user_id, String about,AlertDialog dialog) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/update_user_about.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("OK")){

                    if(about != null && about.length()>0){
                        sp.setSharedPreference(getContext(),"about",about);
                        userNew.setAbout(about);
                        textViewAbout.setText(about);
                    }else
                    {
                        sp.removeSharedPreference(getContext(),"about");
                        userNew.setAbout(null);
                        textViewAbout.setText("Hakkında bir şeyler ekleyebilirsin. Bunu diğer kullanıcılar görebilir. Eklemek için buraya tıkla.");
                    }

                    dialog.dismiss();
                }
                else
                {
                    Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {
                String about2="";
                try {
                    about2 = URLEncoder.encode(about,
                            "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("about", about2);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(getContext()).add(istek);
    }

    public void showProfileImageAlertDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_profile_photo, null);

        RelativeLayout goruntule = customLayout.findViewById(R.id.relativeLayoutDialogProfilePhotoGoruntule);
        RelativeLayout yenisec = customLayout.findViewById(R.id.relativeLayoutDialogProfilePhotoYeniSec);
        RelativeLayout kaldir = customLayout.findViewById(R.id.relativeLayoutDialogProfilePhotoKaldir);

        TextView textView_goruntule = customLayout.findViewById(R.id.textViewDialogProfilePhotoGoruntule);
        TextView textView_kaldir = customLayout.findViewById(R.id.textViewDialogProfilePhotoKaldir);

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        if(userNew != null && userNew.getUser_profile_photo_url() != null && userNew.getUser_profile_photo_url().startsWith("http")){

            goruntule.setClickable(true);
            goruntule.setBackgroundColor(getResources().getColor(R.color.white));
            goruntule.setBackgroundResource(outValue.resourceId);
            textView_goruntule.setTextColor(getResources().getColor(R.color.black_a));

            kaldir.setClickable(true);
            kaldir.setBackgroundColor(getResources().getColor(R.color.white));
            kaldir.setBackgroundResource(outValue.resourceId);
            textView_kaldir.setTextColor(getResources().getColor(R.color.black_a));
        }

        builder.setView(customLayout);

        builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();

        goruntule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userNew != null && userNew.getUser_profile_photo_url() != null && userNew.getUser_profile_photo_url().startsWith("http")){
                    dialog.dismiss();
                    Intent intentImage = new Intent(getContext().getApplicationContext(), FullImageActivity.class);
                    intentImage.putExtra("url",userNew.getUser_profile_photo_url());
                    getContext().startActivity(intentImage);
                }
            }
        });

        yenisec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestStoragePermission();
            }
        });

        kaldir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userNew != null && userNew.getUser_profile_photo_url() != null && userNew.getUser_profile_photo_url().startsWith("http")){
                    dialog.dismiss();
                    profilImageRemove(user_id);
                }

            }
        });

        dialog.show();
    }

    private void profilImageRemove(int user_id){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/profile_image_remove.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("OK")){
                    imageViewProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_person));
                    sp.setSharedPreference(getContext(),"profile_photo_url",null);
                    userNew.setUser_profile_photo_url(null);
                }
                else
                {
                    Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
                params.put("user_id", String.valueOf(user_id));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(getContext()).add(istek);
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

                            Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            assert cursor != null;
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String mediaPath = cursor.getString(columnIndex);
                            cursor.close();

                            File file = new File(mediaPath);
                            uploadImage(String.valueOf(user_id),file);


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(String user_id,File file) {
        progressDialog.show();

            Map<String, RequestBody> map = new HashMap<>();

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            map.put("file\"; filename=\"" + user_id +".jpg"+ "\"", requestBody);
            ApiConfigProfileImageUpload getResponse = AppConfig.getRetrofit().create(ApiConfigProfileImageUpload.class);
            Call<ServerResponse> call = getResponse.upload(user_id, map);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                    if (!response.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();

                        Log.e("ALPERBABA",response.toString());
                    }else
                    {
                        progressDialog.dismiss();
                        Picasso.with(getContext()).load(response.body().getMessage().trim())
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(imageViewProfilePhoto);
                        sp.setSharedPreference(getContext(),"profile_photo_url",response.body().getMessage().trim());
                        userNew.setUser_profile_photo_url(response.body().getMessage().trim());
                    }

                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                    Log.e("ALPERBABA",call.toString()+"\n"+t.getMessage());
                    Log.e("ALPERBABA",t.toString());
                }
            });
    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1024);

        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2342);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==2342){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1024);
            }

        }
    }

    private void getUserProfileInfo(int user_id, boolean refresh){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_user_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){

                    if(refresh){
                        Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        MySharedPreferences sp = new MySharedPreferences();
                        String about = sp.getSharedPreference(getContext(),"about",null);
                        String profile_photo_url = sp.getSharedPreference(getContext(),"profile_photo_url",null);

                        textViewUsername.setText(sp.getSharedPreference(getContext(),"username","username"));

                        if(about != null && about.length()>0){
                            textViewAbout.setText(about);
                        }

                        if(profile_photo_url != null && profile_photo_url.startsWith("http")){
                            Picasso.with(getContext()).load(profile_photo_url)
                                    .placeholder(R.drawable.ic_person)
                                    .error(R.drawable.ic_person)
                                    .into(imageViewProfilePhoto);
                        }
                    }

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

                            JSONArray soru_images = soru.getJSONArray("images");
                            ArrayList<String> soru_images_list = new ArrayList<>();

                            for(int s=0; s<soru_images.length(); s++) {
                                soru_images_list.add(soru_images.get(s).toString());
                            }

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

                                JSONArray yanit_images = soru_yanit.getJSONArray("images");
                                ArrayList<String> yanit_images_list = new ArrayList<>();

                                for(int y=0; y<yanit_images.length(); y++) {
                                    yanit_images_list.add(yanit_images.get(y).toString());
                                }

                                soru_yanitlarList.add(new Yanit(yanit_id,yanit_user_id,yanit_soru_id,0,yanit_like_count,yanit_en_iyi,yanit_icerik,username,createdTimeToDate(yanit_date),profile_photo_url,yanit_images_list));
                            }
                            soruList.add(new Soru(soru_id,0,soru_like_count,soru_user_id,soru_marka,soru_model,soru_baslik,soru_icerik,username,createdTimeToDate(soru_date),profile_photo_url,soru_images_list,soru_yanitlarList));
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

                            JSONArray yanit_images2 = yanit.getJSONArray("images");
                            ArrayList<String> yanit_images_list2 = new ArrayList<>();

                            for(int l=0; l<yanit_images2.length(); l++) {
                                yanit_images_list2.add(yanit_images2.get(l).toString());
                            }

                            yanitList.add(new Yanit(yanit_id,yanit_user_id,yanit_soru_id,0,yanit_like_count,yanit_en_iyi,yanit_icerik,username,createdTimeToDate(yanit_date),profile_photo_url,yanit_images_list2));
                        }

                        userNew = new User(user_id,totel_yanit_size,totel_sorular_like+totel_yanitlar_like,totel_en_iyi_yanit_size,username,about,profile_photo_url,soruList,yanitList);
                        soruAdapter = new MySoruAdapter(getContext(), userNew.getSoruList(),false);
                        yanitAdapter = new MyYanitAdapter(getContext(), userNew.getYanitList(),false);
                        recyclerView.setAdapter(soruAdapter);

                        if(userNew.getSoruList().size()<1){
                            textViewNotFound.setText("Soru bulunamadı!");
                            linearLayoutNotFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        else
                        {
                            linearLayoutNotFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        if(userNew.getUser_profile_photo_url() != null && userNew.getUser_profile_photo_url().startsWith("http")){
                            Picasso.with(getContext()).load(userNew.getUser_profile_photo_url())
                                    .placeholder(R.drawable.ic_person)
                                    .error(R.drawable.ic_person)
                                    .into(imageViewProfilePhoto);
                        }

                        textViewUsername.setText(userNew.getUsername());

                        if(userNew.getAbout() != null && userNew.getAbout().length()>0){
                            textViewAbout.setText(userNew.getAbout());
                        }

                        textViewYanitSize.setText(String.valueOf(userNew.getYanit_size()));
                        textViewLikeSize.setText(String.valueOf(userNew.getLike_size()));
                        textViewEnIyiYanitSize.setText(String.valueOf(userNew.getEn_iyi_yanit_size()));

                        sp.setSharedPreference(getContext(),"username",userNew.getUsername());
                        sp.setSharedPreference(getContext(),"about",userNew.getAbout());
                        sp.setSharedPreference(getContext(),"profile_photo_url",userNew.getUser_profile_photo_url());

                        textViewYanitlar.setBackgroundColor(getResources().getColor(R.color.white));
                        textViewYanitlar.setTextColor(getResources().getColor(R.color.secondaryColor));

                        textViewSorular.setBackgroundColor(getResources().getColor(R.color.black_a));
                        textViewSorular.setTextColor(getResources().getColor(R.color.white));

                        textViewSorular.setClickable(false);
                        textViewYanitlar.setClickable(true);

                    } catch (JSONException e) {
                        if(refresh){
                            Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            MySharedPreferences sp = new MySharedPreferences();
                            String about = sp.getSharedPreference(getContext(),"about",null);
                            String profile_photo_url = sp.getSharedPreference(getContext(),"profile_photo_url",null);

                            textViewUsername.setText(sp.getSharedPreference(getContext(),"username","username"));

                            if(about != null && about.length()>0){
                                textViewAbout.setText(about);
                            }

                            if(profile_photo_url != null && profile_photo_url.startsWith("http")){
                                Picasso.with(getContext()).load(profile_photo_url)
                                        .placeholder(R.drawable.ic_person)
                                        .error(R.drawable.ic_person)
                                        .into(imageViewProfilePhoto);
                            }
                        }
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(refresh){
                    Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    MySharedPreferences sp = new MySharedPreferences();
                    String about = sp.getSharedPreference(getContext(),"about",null);
                    String profile_photo_url = sp.getSharedPreference(getContext(),"profile_photo_url",null);

                    textViewUsername.setText(sp.getSharedPreference(getContext(),"username","username"));

                    if(about != null && about.length()>0){
                        textViewAbout.setText(about);
                    }

                    if(profile_photo_url != null && profile_photo_url.startsWith("http")){
                        Picasso.with(getContext()).load(profile_photo_url)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(imageViewProfilePhoto);
                    }
                }

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
        Volley.newRequestQueue(getContext()).add(istek);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserProfileInfo(user_id,true);
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