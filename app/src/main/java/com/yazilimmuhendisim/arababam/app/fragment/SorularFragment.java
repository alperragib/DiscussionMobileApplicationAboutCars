package com.yazilimmuhendisim.arababam.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
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
import com.yazilimmuhendisim.arababam.app.adapter.SorularAdapter;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;
import com.yazilimmuhendisim.arababam.app.model.Marka;
import com.yazilimmuhendisim.arababam.app.model.Model;
import com.yazilimmuhendisim.arababam.app.model.Soru;
import com.yazilimmuhendisim.arababam.app.model.Yanit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SorularFragment extends Fragment implements SearchView.OnQueryTextListener,AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    LinearLayout linearLayoutFilter,linearLayoutNotFound;
    Button buttonFilterUygula,buttonNotFoundTekrarDene;
    TextView textViewNotFound;
    MenuItem itemFilter;
    RecyclerView recyclerView_sorular;
    SorularAdapter sorularAdapter;
    ArrayList<Soru> soruList;
    ArrayList<Marka> markaList;
    Spinner spinnerMarka,spinnerModel,spinnerSirala;
    CheckBox checkBoxKayitliTut;
    MySharedPreferences sp;
    boolean kayitli = false;
    int user_id, marka_id, model_id, siralama;
    String search = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sorular, container, false);

        toolbar = root.findViewById(R.id.toolbarSorular);
        recyclerView_sorular = root.findViewById(R.id.recylerViewSorular);
        linearLayoutFilter = root.findViewById(R.id.linearLayoutFilter);
        linearLayoutNotFound = root.findViewById(R.id.linearLayoutSorularNotFound);
        buttonNotFoundTekrarDene = root.findViewById(R.id.buttonSorularNotFoundTekrardene);
        textViewNotFound = root.findViewById(R.id.textViewSorularNotFound);
        spinnerMarka = root.findViewById(R.id.spinnerSorularMarka);
        spinnerModel = root.findViewById(R.id.spinnerSorularModel);
        spinnerSirala = root.findViewById(R.id.spinnerSorularSirala);
        buttonFilterUygula = root.findViewById(R.id.buttonSorularFilterUygula);
        checkBoxKayitliTut = root.findViewById(R.id.checkboxSorularKayitliTut);
        spinnerMarka.setOnItemSelectedListener(this);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        sp = new MySharedPreferences();

        buttonFilterUygula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutFilter.setVisibility(View.GONE);
                itemFilter.setIcon(getResources().getDrawable(R.drawable.ic_filter));

                filtreUygula();
            }
        });

        soruList = new ArrayList<>();

        recyclerView_sorular.setHasFixedSize(true);
        recyclerView_sorular.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        getMarkaModel();

        buttonNotFoundTekrarDene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search != null && search.length()>0){
                    searchSorular(user_id,search);
                }
                else
                {
                    getSorular(user_id,marka_id,model_id,siralama);
                }

            }
        });

        ArrayList<String> marka = new ArrayList<>();
        marka.add("Marka Seç");

        for(int i=0; i<markaList.size();i++){
            marka.add(markaList.get(i).getMarka());
        }

        String[] sirala = { "En Yeni", "En Eski", "Beğeni Sayısı"};

        ArrayAdapter aa1 = new ArrayAdapter(getContext(),R.layout.spinner_item,marka);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarka.setAdapter(aa1);

        ArrayAdapter aa3 = new ArrayAdapter(getContext(),R.layout.spinner_item,sirala);
        aa3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSirala.setAdapter(aa3);

        int sp_marka_id = Integer.parseInt(sp.getSharedPreference(getContext(),"marka_id","-1"));
        int sp_model_id = Integer.parseInt(sp.getSharedPreference(getContext(),"model_id","-1"));
        int sp_siralama = Integer.parseInt(sp.getSharedPreference(getContext(),"siralama","1"));
        user_id = Integer.parseInt(sp.getSharedPreference(root.getContext(),"user_id","0"));
        getSorular(user_id,sp_marka_id,sp_model_id,sp_siralama);

        if(sp_marka_id != -1 || sp_siralama != 1){
            kayitli=true;
            checkBoxKayitliTut.setChecked(true);
        }

        if(sp_siralama!=1){
            spinnerSirala.setSelection(sp_siralama-1);
        }

        if(sp_marka_id != -1){

            int markaPosition = 0;
            for(int a=0;a<markaList.size();a++){
                if(markaList.get(a).getId() == sp_marka_id){
                    markaPosition = a;
                }
            }
            spinnerMarka.setSelection(markaPosition+1);

            ArrayList<String> model = new ArrayList<>();
            model.add("Tüm Modeller");

            ArrayList<Model> modelList = markaList.get(markaPosition).getModelList();

            for(int j=0; j<modelList.size();j++){
                model.add(modelList.get(j).getModel());
            }

            ArrayAdapter aa24 = new ArrayAdapter(getContext(),R.layout.spinner_item,model);
            aa24.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerModel.setAdapter(aa24);

            int modelPosition = -1;
            for(int b=0;b<modelList.size();b++){
                if(modelList.get(b).getId() == sp_model_id){
                    modelPosition = b;
                }
            }
            spinnerModel.setSelection(modelPosition+1);
            spinnerModel.setVisibility(View.VISIBLE);


        }

        return root;

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch(parent.getId()) {
            case R.id.spinnerSorularMarka:

                if(position==0){
                    spinnerModel.setVisibility(View.GONE);
                }
                else
                {
                    if(!kayitli){
                        ArrayList<String> model = new ArrayList<>();
                        model.add("Tüm Modeller");

                        ArrayList<Model> modelList = markaList.get(position-1).getModelList();

                        for(int j=0; j<modelList.size();j++){
                            model.add(modelList.get(j).getModel());
                        }

                        ArrayAdapter aa2 = new ArrayAdapter(getContext(),R.layout.spinner_item,model);
                        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerModel.setAdapter(aa2);
                        spinnerModel.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        kayitli=false;
                    }

                }

                break;
            case R.id.spinnerSorularModel:

                break;

            case R.id.spinnerSorularSirala:

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.sorular_toolbar_menu,menu);


        MenuItem item = menu.findItem(R.id.sorularToolbarMenuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Yaşadığınız problemi arayın...");
        searchView.setOnQueryTextListener(this);


        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getSorular(user_id,marka_id,model_id,siralama);
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.sorularToolbarMenuFilter:

                itemFilter = item;

                if(linearLayoutFilter.getVisibility()==View.GONE)
                {
                    linearLayoutFilter.setVisibility(View.VISIBLE);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_close));
                }
                else
                {
                    linearLayoutFilter.setVisibility(View.GONE);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_filter));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query.trim().length()>0){
            searchSorular(user_id,query.trim());
            return true;
        }
        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void getSorular(int user_id, int marka_id, int model_id, int siralama){

        this.user_id = user_id;
        this.marka_id = marka_id;
        this.model_id = model_id;
        this.siralama = siralama;
        this.search = null;

        soruList = new ArrayList<>();

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_sorular.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("ERR-3")){
                    textViewNotFound.setText("Soru bulunamadı!");
                    buttonNotFoundTekrarDene.setVisibility(View.GONE);
                    linearLayoutNotFound.setVisibility(View.VISIBLE);
                    recyclerView_sorular.setVisibility(View.GONE);
                }
                else if(response.trim().startsWith("ERR")){
                    textViewNotFound.setText("İnternet bağlantısı bulunamadı!");
                    buttonNotFoundTekrarDene.setVisibility(View.VISIBLE);
                    linearLayoutNotFound.setVisibility(View.VISIBLE);
                    recyclerView_sorular.setVisibility(View.GONE);
                }
                else
                {
                    try {

                        JSONArray sorular = new JSONArray(response);

                        for(int i=0; i<sorular.length(); i++){
                            JSONObject soru = (JSONObject) sorular.get(i);

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
                            ArrayList <String> imagesList = new ArrayList<>();

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

                            soruList.add(new Soru(soru_id,like,like_count,user_id,marka,model,baslik,icerik,username,createdTimeToDate(date),user_profile_photo_url,imagesList,yanitList));
                        }

                        linearLayoutNotFound.setVisibility(View.GONE);
                        recyclerView_sorular.setVisibility(View.VISIBLE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                sorularAdapter = new SorularAdapter(getContext(), soruList);
                recyclerView_sorular.setAdapter(sorularAdapter);


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewNotFound.setText("İnternet bağlantısı bulunamadı!");
                buttonNotFoundTekrarDene.setVisibility(View.VISIBLE);
                linearLayoutNotFound.setVisibility(View.VISIBLE);
                recyclerView_sorular.setVisibility(View.GONE);

            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("marka_id", String.valueOf(marka_id));
                params.put("model_id", String.valueOf(model_id));
                params.put("siralama", String.valueOf(siralama));
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(getContext()).add(istek);



    }

    private void searchSorular(int user_id, String search) {

        this.user_id = user_id;
        this.search = search;

        soruList = new ArrayList<>();

        String url = "http://yazilimmuhendisim.com/api/database_arababam/get_sorular_search.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("ERR-3")){
                    textViewNotFound.setText("Soru bulunamadı!");
                    buttonNotFoundTekrarDene.setVisibility(View.GONE);
                    linearLayoutNotFound.setVisibility(View.VISIBLE);
                    recyclerView_sorular.setVisibility(View.GONE);
                }
                else if(response.trim().startsWith("ERR")){
                    textViewNotFound.setText("İnternet bağlantısı bulunamadı!");
                    buttonNotFoundTekrarDene.setVisibility(View.VISIBLE);
                    linearLayoutNotFound.setVisibility(View.VISIBLE);
                    recyclerView_sorular.setVisibility(View.GONE);
                }
                else
                {
                    try {

                        JSONArray sorular = new JSONArray(response);

                        for(int i=0; i<sorular.length(); i++){
                            JSONObject soru = (JSONObject) sorular.get(i);

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
                            ArrayList <String> imagesList = new ArrayList<>();

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

                            soruList.add(new Soru(soru_id,like,like_count,user_id,marka,model,baslik,icerik,username,createdTimeToDate(date),user_profile_photo_url,imagesList,yanitList));
                        }

                        linearLayoutNotFound.setVisibility(View.GONE);
                        recyclerView_sorular.setVisibility(View.VISIBLE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                sorularAdapter = new SorularAdapter(getContext(), soruList);
                recyclerView_sorular.setAdapter(sorularAdapter);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewNotFound.setText("İnternet bağlantısı bulunamadı!");
                buttonNotFoundTekrarDene.setVisibility(View.VISIBLE);
                linearLayoutNotFound.setVisibility(View.VISIBLE);
                recyclerView_sorular.setVisibility(View.GONE);

            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("search", search);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(getContext()).add(istek);



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

    private void filtreUygula() {

        int marka_id=-1,model_id=-1,siralama=1;
        if(spinnerMarka.getSelectedItemPosition()>0){
            Marka marka = markaList.get(spinnerMarka.getSelectedItemPosition()-1);
           marka_id = marka.getId();

           if(spinnerModel.getSelectedItemPosition()>0){
               Model model = marka.getModelList().get(spinnerModel.getSelectedItemPosition()-1);
                model_id = model.getId();
           }
        }

        siralama = (spinnerSirala.getSelectedItemPosition()+1);

        if(checkBoxKayitliTut.isChecked())
        {
            sp.setSharedPreference(getContext(),"marka_id",String.valueOf(marka_id));
            sp.setSharedPreference(getContext(),"model_id",String.valueOf(model_id));
            sp.setSharedPreference(getContext(),"siralama",String.valueOf(siralama));
        }
        else
        {
            sp.removeSharedPreference(getContext(),"marka_id");
            sp.removeSharedPreference(getContext(),"model_id");
            sp.removeSharedPreference(getContext(),"siralama");
        }

        String user_id = sp.getSharedPreference(getContext(),"user_id","0");
        getSorular(Integer.parseInt(user_id),marka_id,model_id,siralama);
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