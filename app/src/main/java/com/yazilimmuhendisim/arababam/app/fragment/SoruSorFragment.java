package com.yazilimmuhendisim.arababam.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.activity.SoruYanitActivity;
import com.yazilimmuhendisim.arababam.app.adapter.ImageFileAdapter;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Marka;
import com.yazilimmuhendisim.arababam.app.model.Model;
import com.yazilimmuhendisim.arababam.app.model.Soru;
import com.yazilimmuhendisim.arababam.app.model.Yanit;
import com.yazilimmuhendisim.arababam.app.network.ApiConfigSoruImageUpload;
import com.yazilimmuhendisim.arababam.app.network.ApiConfigYanitImageUpload;
import com.yazilimmuhendisim.arababam.app.network.AppConfig;
import com.yazilimmuhendisim.arababam.app.network.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

import static android.app.Activity.RESULT_CANCELED;

public class SoruSorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText editTextBaslik,editTextIcerik;
    private Spinner spinnerMarka,spinnerModel;
    private TextView textViewError;
    private ArrayList<File> imageList;
    private ImageFileAdapter imageFileAdapter;
    private Button buttonYayinla;
    private ProgressDialog progressDialog;
    private ArrayList<Marka> markaList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_sorusor, container, false);

        editTextBaslik = root.findViewById(R.id.editTextSoruSorBaslik);
        editTextIcerik = root.findViewById(R.id.editTextSoruSorİcerik);
        spinnerMarka = root.findViewById(R.id.spinnerSoruSorMarka);
        spinnerModel = root.findViewById(R.id.spinnerSoruSorModel);
        CardView cardViewImage = root.findViewById(R.id.cardViewSoruSorImage);
        RecyclerView recyclerViewImage = root.findViewById(R.id.recylerViewSoruSorImage);
        buttonYayinla = root.findViewById(R.id.buttonSoruSorYayinla);
        textViewError = root.findViewById(R.id.textViewSoruSorError);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = root.findViewById(R.id.bannerSoruSor);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Yanıtınız yayınlanıyor lütfen bekleyiniz...");
        progressDialog.setCancelable(false);

        imageList = new ArrayList<>();

        recyclerViewImage.setHasFixedSize(true);
        recyclerViewImage.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        imageFileAdapter = new ImageFileAdapter(root.getContext(), imageList);
        recyclerViewImage.setAdapter(imageFileAdapter);

        getMarkaModel();

        ArrayList<String> marka = new ArrayList<>();
        marka.add("Marka Seç");

        for(int i=0; i<markaList.size();i++){
            marka.add(markaList.get(i).getMarka());
        }

        ArrayAdapter aa1 = new ArrayAdapter(getContext(),R.layout.spinner_item,marka);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarka.setAdapter(aa1);

        spinnerMarka.setOnItemSelectedListener(this);

        buttonYayinla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYayinla();
            }
        });

        cardViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        return root;
    }

    private void checkYayinla()
    {
        MySharedPreferences sp = new MySharedPreferences();
        int user_id = Integer.parseInt(sp.getSharedPreference(getContext(),"user_id","0"));

        int marka_id=0,model_id=0;
        if(spinnerMarka.getSelectedItemPosition()>0){
            Marka marka = markaList.get(spinnerMarka.getSelectedItemPosition()-1);
            marka_id = marka.getId();

            if(spinnerModel.getSelectedItemPosition()>0){
                Model model = marka.getModelList().get(spinnerModel.getSelectedItemPosition()-1);
                model_id = model.getId();
            }
        }

        textViewError.setVisibility(View.GONE);
        String baslik = editTextBaslik.getText().toString().trim();
        String icerik = editTextIcerik.getText().toString().trim();

        if (baslik.isEmpty() || baslik.length()<1)
        {
            editTextBaslik.setError("Bu alan boş bırakılamaz!");
        }
        else if(icerik.isEmpty() || icerik.length()<1){
            editTextIcerik.setError("Bu alan boş bırakılamaz!");
        }
        else if (baslik.length()<10 || baslik.length()>150)
        {
            editTextBaslik.setError("En az 10 ve en fazla 150 karakter olabilir.");
        }
        else if (icerik.length()<20 || icerik.length()>1000)
        {
            editTextIcerik.setError("En az 20 ve en fazla 1000 karakter olabilir.");
        }
        else if (spinnerMarka.getSelectedItemPosition()==0)
        {
            textViewError.setText("Lütfen marka seçin.");
            textViewError.setVisibility(View.VISIBLE);
        }
        else if (spinnerModel.getSelectedItemPosition()==0)
        {
            textViewError.setText("Lütfen model seçin.");
            textViewError.setVisibility(View.VISIBLE);
        }
        else
        {
            hideKeyboard(getActivity());
            soruYayinla(user_id,marka_id,model_id,baslik,icerik);
        }
    }

    private void soruYayinla(int user_id, int marka_id, int model_id, String baslik, String icerik){
        buttonYayinla.setClickable(false);

        String url = "http://yazilimmuhendisim.com/api/database_arababam/soru_yayinla.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                editTextBaslik.setText("");
                editTextIcerik.setText("");

                if(response.trim().startsWith("ERR")){
                    textViewError.setText("Soru yayınlanırken bir hata meydana geldi!");
                    textViewError.setVisibility(View.VISIBLE);
                    buttonYayinla.setClickable(true);
                }
                else
                {
                    if(imageList.size()>0){
                        uploadImage(user_id,response.trim());
                    }else
                    {
                        goSoruDetail(user_id,Integer.parseInt(response.trim()));
                        //buttonYayinla.setClickable(true);
                    }

                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewError.setText("İnternet bağlantısı bulunamadı!");
                textViewError.setVisibility(View.VISIBLE);
                buttonYayinla.setClickable(true);
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {
                String icerik2="",baslik2="";
                try {
                    baslik2 = URLEncoder.encode(baslik,
                            "UTF-8");
                    icerik2 = URLEncoder.encode(icerik,
                            "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("marka_id", String.valueOf(marka_id));
                params.put("model_id", String.valueOf(model_id));
                params.put("baslik", baslik2);
                params.put("icerik", icerik2);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(getContext()).add(istek);
    }

    private void uploadImage(int user_id,String soru_id) {
        progressDialog.show();
        for(int i=0; i<imageList.size();i++){

            Map<String, RequestBody> map = new HashMap<>();
            File file = imageList.get(i);

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            map.put("file\"; filename=\"" + soru_id +".jpg"+ "\"", requestBody);
            ApiConfigSoruImageUpload getResponse = AppConfig.getRetrofit().create(ApiConfigSoruImageUpload.class);
            Call<ServerResponse> call = getResponse.upload(soru_id, map);
            int finalI = i;
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                    if (!response.isSuccessful()){
                        progressDialog.dismiss();
                        textViewError.setText("İnternet bağlantısı bulunamadı!");
                        textViewError.setVisibility(View.VISIBLE);
                        buttonYayinla.setClickable(true);
                    }else
                    {
                        if(finalI == (imageList.size()-1)){
                            progressDialog.dismiss();
                            buttonYayinla.setClickable(true);
                            goSoruDetail(user_id,Integer.parseInt(soru_id));
                        }
                    }

                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    textViewError.setText("İnternet bağlantısı bulunamadı!");
                    textViewError.setVisibility(View.VISIBLE);
                    buttonYayinla.setClickable(true);
                }
            });
        }
    }

    private void goSoruDetail(int user_id,int soru_id) {

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_soru_info.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().startsWith("ERR")){
                    Toast.makeText(getContext(),"İnternet bağlantısı bulunamadı!",Toast.LENGTH_SHORT).show();
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
                        Intent intentSoruYanit = new Intent(getActivity(),SoruYanitActivity.class);
                        intentSoruYanit.putExtra("Soru", (Serializable) soruNew);
                        intentSoruYanit.putExtra("back", "0");
                        startActivity(intentSoruYanit);
                        getActivity().finish();

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
                params.put("user_id", String.valueOf(user_id));
                params.put("soru_id", String.valueOf(soru_id));
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

                            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            assert cursor != null;
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String mediaPath = cursor.getString(columnIndex);
                            cursor.close();

                            File file = new File(mediaPath);

                            imageList.add(file);
                            imageFileAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch(parent.getId()) {
            case R.id.spinnerSoruSorMarka:

                if(position==0){
                    spinnerModel.setVisibility(View.GONE);
                }
                else
                {
                        ArrayList<String> model = new ArrayList<>();
                        model.add("Model Seç");

                        ArrayList<Model> modelList = markaList.get(position-1).getModelList();

                        for(int j=0; j<modelList.size();j++){
                            model.add(modelList.get(j).getModel());
                        }

                        ArrayAdapter aa2 = new ArrayAdapter(getContext(),R.layout.spinner_item,model);
                        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerModel.setAdapter(aa2);
                        spinnerModel.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.spinnerSoruSorModel:

                break;
        }

    }

    private void getMarkaModel(){

        markaList = new ArrayList<>();

        try {
            JSONArray markalar = new JSONArray(dahiliOku("marka_model.txt"));

            for(int i=0; i<markalar.length(); i++){

                JSONObject marka = (JSONObject) markalar.get(i);

                int marka_id = Integer.parseInt(marka.getString("id"));
                String marka_ismi = marka.getString("marka");
                String logo_image_url = marka.getString("logo_image_url");

                JSONArray modeller = marka.getJSONArray("modeller");

                ArrayList<Model> modelList = new ArrayList<>();

                for(int a=0; a<modeller.length();a++){
                    JSONObject model = (JSONObject) modeller.get(a);
                    int model_id = Integer.parseInt(model.getString("id"));
                    String model_ismi = model.getString("model");

                    modelList.add(new Model(model_id,model_ismi));
                }

                markaList.add(new Marka(marka_id,marka_ismi,logo_image_url,modelList));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String dahiliOku(String fileName){
        try {
            FileInputStream fis = getContext().openFileInput(fileName);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            if(imageList.size()<5){
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1024);
            }else
            {
                textViewError.setText("Maksimum 5 tane görsel yükleyebilirsiniz.");
                textViewError.setVisibility(View.VISIBLE);
            }


        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2342);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==2342){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if(imageList.size()<5){
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

