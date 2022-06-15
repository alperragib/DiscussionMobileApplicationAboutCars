package com.yazilimmuhendisim.arababam.app.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yazilimmuhendisim.arababam.app.R;
import com.yazilimmuhendisim.arababam.app.fragment.UyelikSozlesmesi;
import com.yazilimmuhendisim.arababam.app.library.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText editTextReg_Username,editTextReg_Email,editTextReg_Password,editTextReg_PasswordAgain,editTextLog_EmailUsername,editTextLog_Password;
    Button buttonLogin,buttonRegister;
    String token;
    TextView textViewLoginError,textViewRegError;
    CheckBox checkBoxUS;
    final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black_a));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
            token = instanceIdResult.getToken();
        });

        CardView cardGoogle = findViewById(R.id.cardview_google);
        CardView cardLogin = findViewById(R.id.cardView_login);
        CardView cardRegister = findViewById(R.id.cardView_register);

        checkBoxUS = findViewById(R.id.checkBoxUyelikSozlesmesi);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        TextView textViewGirisYap = findViewById(R.id.textViewGirisYap);
        TextView textViewKayitOl = findViewById(R.id.textViewKayitOl);
        TextView textViewUyelikSozlesmesi = findViewById(R.id.textViewUyelikSozlesmesi);
        textViewLoginError = findViewById(R.id.textViewLoginError);
        textViewRegError = findViewById(R.id.textViewRegError);

        editTextReg_Username = findViewById(R.id.editTextReg_Username);
        editTextReg_Email = findViewById(R.id.editTextReg_Email);
        editTextReg_Password = findViewById(R.id.editTextReg_Password);
        editTextReg_PasswordAgain = findViewById(R.id.editTextReg_PasswordAgain);
        editTextLog_EmailUsername = findViewById(R.id.editTextLog_EmailUsername);
        editTextLog_Password = findViewById(R.id.editTextLog_Password);

        textViewUyelikSozlesmesi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                UyelikSozlesmesi newFragment = new UyelikSozlesmesi();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();

            }
        });

        textViewKayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxUS.setChecked(false);
                editTextReg_Username.setText("");
                editTextReg_Email.setText("");
                editTextReg_Password.setText("");
                editTextReg_PasswordAgain.setText("");

                editTextReg_Username.setError(null);
                editTextReg_Email.setError(null);
                editTextReg_Password.setError(null);
                editTextReg_PasswordAgain.setError(null);
                checkBoxUS.setError(null);

                cardLogin.setVisibility(View.GONE);
                cardRegister.setVisibility(View.VISIBLE);
            }
        });

        textViewGirisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTextLog_EmailUsername.setText("");
                editTextLog_Password.setText("");

                editTextLog_EmailUsername.setError(null);
                editTextLog_Password.setError(null);

                cardLogin.setVisibility(View.VISIBLE);
                cardRegister.setVisibility(View.GONE);
                checkBoxUS.setChecked(false);

            }
        });

        cardGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCheck();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCheck();
            }
        });
    }

    private void loginCheck(){

        String text = editTextLog_EmailUsername.getText().toString().trim();
        String password = editTextLog_Password.getText().toString().trim();

        if( (text.isEmpty() || text.length()<1) && (password.isEmpty() || password.length()<1)){
            editTextLog_EmailUsername.setError("Bu alan boş bırakılamaz!");
            editTextLog_Password.setError("Bu alan boş bırakılamaz!");
            editTextLog_EmailUsername.setText("");
            editTextLog_Password.setText("");
        }
        else if (text.isEmpty() || text.length()<1)
        {
            editTextLog_EmailUsername.setError("Bu alan boş bırakılamaz!");
            editTextLog_EmailUsername.setText("");
        }
        else if (password.isEmpty() || password.length()<1)
        {
            editTextLog_Password.setError("Bu alan boş bırakılamaz!");
            editTextLog_Password.setText("");
        }
        else if(text.length()<5){
            editTextLog_EmailUsername.setError("Geçersiz giriş yaptınız!");
        }
        else
        {
            hideKeyboard(this);
            if(!TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                loginUser("null",text,password);
            }
            else
            {
                loginUser(text,"null",password);
            }
        }


    }

    private void registerCheck(){
        String email = editTextReg_Email.getText().toString().trim();
        String username = editTextReg_Username.getText().toString().trim();
        String password = editTextReg_Password.getText().toString().trim();
        String password_again = editTextReg_PasswordAgain.getText().toString().trim();

        if (username.isEmpty() || username.length()<1)
        {
            editTextReg_Username.setError("Bu alan boş bırakılamaz!");
        }
        else if(email.isEmpty() || email.length()<1){
            editTextReg_Email.setError("Bu alan boş bırakılamaz!");
        }

        else if (password.isEmpty() || password.length()<1)
        {
            editTextReg_Password.setError("Bu alan boş bırakılamaz!");
        }
        else if (password_again.isEmpty() || password_again.length()<1)
        {
            editTextReg_PasswordAgain.setError("Bu alan boş bırakılamaz!");
        }
        else if (username.length()<6 || username.length()>20)
        {
            editTextReg_Username.setError("En az 6 ve en fazla 20 karakter olabilir.");
        }
        else if (!isValidUsername(username))
        {
            editTextReg_Username.setError("Kullanıcı adı formatı hatalı! Türkçe karakter kullanmayınız ve boşluk kullanmayınız.");
        }
        else if(!(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            editTextReg_Email.setError("Geçersiz email adresi!");
        }

        else if (password.length()<6 || password.length()>20)
        {
            editTextReg_Password.setError("En az 6 ve en fazla 30 karakter olabilir.");
        }
        else if (!password.equals(password_again))
        {
            editTextReg_PasswordAgain.setError("Şifreler eşleşmiyor!");
        }
        else if (!checkBoxUS.isChecked())
        {
            checkBoxUS.setError("Kabul etmeden üye olamazsınız!");
        }
        else
        {
            checkBoxUS.setError(null);
            hideKeyboard(this);
            checkUsername(email,username,password);
        }
    }

    private void loginUser(String username,String email, String password){

        buttonLogin.setText("Lütfen bekleyin....");
        buttonLogin.setClickable(false);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
            token = instanceIdResult.getToken();
        });

        String url = "http://yazilimmuhendisim.com/api/database_arababam/user_login.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("ERR-3")){
                    textViewLoginError.setText("Hatalı giriş yaptınız!");
                    textViewLoginError.setVisibility(View.VISIBLE);
                    buttonLogin.setText("Giriş Yap");
                    buttonLogin.setClickable(true);
                }
                else if(response.startsWith("ERR")){
                    textViewLoginError.setText("Hatalı giriş yaptınız!");
                    textViewLoginError.setVisibility(View.VISIBLE);
                    buttonLogin.setText("Giriş Yap");
                    buttonLogin.setClickable(true);
                }
                else
                {
                    textViewLoginError.setVisibility(View.GONE);
                    try {
                        JSONObject user = new JSONObject(response);
                        String user_id = user.getString("id");
                        String username = user.getString("username");
                        String email = user.getString("email");
                        String token = user.getString("token");
                        String about = user.getString("about");
                        try {
                            about = URLDecoder.decode(
                                    about, "UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }
                        String profile_photo_url = user.getString("profile_photo_url");

                        MySharedPreferences sp = new MySharedPreferences();

                        sp.setSharedPreference(LoginActivity.this,"user_id",user_id);
                        sp.setSharedPreference(LoginActivity.this,"username",username);
                        sp.setSharedPreference(LoginActivity.this,"email",email);
                        sp.setSharedPreference(LoginActivity.this,"token",token);
                        sp.setSharedPreference(LoginActivity.this,"about",about);
                        sp.setSharedPreference(LoginActivity.this,"profile_photo_url",profile_photo_url);

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();

                    } catch (JSONException e) {
                        textViewLoginError.setText("Bir hata meydana geldi!");
                        textViewLoginError.setVisibility(View.VISIBLE);
                        buttonLogin.setText("Giriş Yap");
                        buttonLogin.setClickable(true);
                    }


                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewLoginError.setText("İnternet bağlantınızı kontrol ediniz!");
                textViewLoginError.setVisibility(View.VISIBLE);
                buttonLogin.setText("Giriş Yap");
                buttonLogin.setClickable(true);
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("email",email);
                params.put("password",password);
                params.put("token",token);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }

    private void checkUsername(String email,String username,String password){

        buttonRegister.setText("Lütfen bekleyin....");
        buttonRegister.setClickable(false);

        String url = "http://yazilimmuhendisim.com/api/database_arababam/check_username.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("OK")){
                    checkEmail(email,username,password);
                }
                else
                {
                    editTextReg_Username.setError("Kullanıcı adı zaten kayıtlı!");

                    buttonRegister.setText("Kayıt Ol");
                    buttonRegister.setClickable(true);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewRegError.setText("İnternet bağlantınızı kontrol ediniz!");
                textViewRegError.setVisibility(View.VISIBLE);
                buttonRegister.setText("Kayıt Ol");
                buttonRegister.setClickable(true);
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }

    private void checkEmail(String email,String username,String password){

        String url = "http://yazilimmuhendisim.com/api/database_arababam/check_email.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("OK")){
                    registerUser(email,username,password);
                }
                else
                {
                    editTextReg_Email.setError("Email adresi zaten kayıtlı!");
                    buttonRegister.setText("Kayıt Ol");
                    buttonRegister.setClickable(true);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewRegError.setText("İnternet bağlantınızı kontrol ediniz!");
                textViewRegError.setVisibility(View.VISIBLE);
                buttonRegister.setText("Kayıt Ol");
                buttonRegister.setClickable(true);
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }

    private void registerUser(String email,String username,String password){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
            token = instanceIdResult.getToken();
        });

        String url = "http://yazilimmuhendisim.com/api/database_arababam/user_register.php";
        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.startsWith("ERR")){

                    textViewRegError.setVisibility(View.GONE);
                    try {
                        JSONObject user = new JSONObject(response);
                        String user_id = user.getString("id");
                        String username = user.getString("username");
                        String email = user.getString("email");
                        String token = user.getString("token");
                        String about = user.getString("about");
                        try {
                            about = URLDecoder.decode(
                                    about, "UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }
                        String profile_photo_url = user.getString("profile_photo_url");

                        MySharedPreferences sp = new MySharedPreferences();

                        sp.setSharedPreference(LoginActivity.this,"user_id",user_id);
                        sp.setSharedPreference(LoginActivity.this,"username",username);
                        sp.setSharedPreference(LoginActivity.this,"email",email);
                        sp.setSharedPreference(LoginActivity.this,"token",token);
                        sp.setSharedPreference(LoginActivity.this,"about",about);
                        sp.setSharedPreference(LoginActivity.this,"profile_photo_url",profile_photo_url);

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();

                    } catch (JSONException e) {
                        textViewRegError.setText("Bir hata meydana geldi!");
                        textViewRegError.setVisibility(View.VISIBLE);
                        buttonRegister.setText("Kayıt Ol");
                        buttonRegister.setClickable(true);
                    }
                }
                else
                {
                    textViewRegError.setText("Kayıt oluşturulamadı! Lütfen girdiğiniz bilgileri kontrol ediniz.");
                    textViewRegError.setVisibility(View.VISIBLE);
                    buttonRegister.setText("Kayıt Ol");
                    buttonRegister.setClickable(true);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewRegError.setText("İnternet bağlantınızı kontrol ediniz!");
                textViewRegError.setVisibility(View.VISIBLE);
                buttonRegister.setText("Kayıt Ol");
                buttonRegister.setClickable(true);
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("username",username);
                params.put("password",password);
                params.put("token",token);
                return params;
            }
        };
        istek.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        Volley.newRequestQueue(this).add(istek);
    }
    public static boolean isValidUsername(String username)
    {

        String regex = "^[a-zA-Z0-9._-]{3,}$";

        Pattern p = Pattern.compile(regex);

        if (username == null) {
            return false;
        }

        Matcher m = p.matcher(username);
        return m.matches();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleLogin(account);
            } catch (ApiException e) {
                e.printStackTrace();
                showAlertDialog("arababam.net","Geçerli bir Google hesabınızla giriş yapmanız gerekmektedir.");
            }
        }
    }

    private void googleLogin(GoogleSignInAccount acct) {
        String email = acct.getEmail();
        String userNameHam = email.split("@")[0].toLowerCase();

        if(userNameHam.length()>20){
            userNameHam = userNameHam.substring(0,20);
        }

        String username = userNameHam;

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
            token = instanceIdResult.getToken();
        });

        if (email != null && !email.isEmpty() && email.length() > 1) {

            String url = "http://yazilimmuhendisim.com/api/database_arababam/google_user_login.php";
            StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.startsWith("ERR"))
                    {
                        showAlertDialog("arababam.net","Geçerli bir Google hesabınızla giriş yapmanız gerekmektedir.");
                    }
                    else {

                        try {
                            JSONObject user = new JSONObject(response);
                            String user_id = user.getString("id");
                            String username = user.getString("username");
                            String email = user.getString("email");
                            String token = user.getString("token");
                            String about = user.getString("about");
                            try {
                                about = URLDecoder.decode(
                                        about, "UTF-8");
                            } catch (UnsupportedEncodingException e) {

                            }
                            String profile_photo_url = user.getString("profile_photo_url");

                            MySharedPreferences sp = new MySharedPreferences();

                            sp.setSharedPreference(LoginActivity.this, "user_id", user_id);
                            sp.setSharedPreference(LoginActivity.this, "username", username);
                            sp.setSharedPreference(LoginActivity.this, "email", email);
                            sp.setSharedPreference(LoginActivity.this, "token", token);
                            sp.setSharedPreference(LoginActivity.this, "about", about);
                            sp.setSharedPreference(LoginActivity.this, "profile_photo_url", profile_photo_url);

                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        } catch (JSONException e) {
                            showAlertDialog("arababam.net","Geçerli bir Google hesabınızla giriş yapmanız gerekmektedir.");
                        }


                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showAlertDialog("arababam.net","Geçerli bir Google hesabınızla giriş yapmanız gerekmektedir.");

                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("username", username);
                    params.put("token", token);
                    return params;
                }
            };
            istek.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            istek.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
            Volley.newRequestQueue(this).add(istek);
        }
        else
        {
            showAlertDialog("arababam.net","Geçerli bir Google hesabınızla giriş yapmanız gerekmektedir.");
        }


    }
    void showAlertDialog(String baslik,String icerik){
        AlertDialog.Builder ao = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setText(baslik);
        textView.setPadding(60, 30, 30, 30);
        textView.setTextSize(20F);

        textView.setTextColor(getResources().getColor(R.color.secondaryColor));
        ao.setTitle(baslik);
        ao.setMessage(icerik);
        ao.setCustomTitle(textView);


        final AlertDialog dialog = ao.setNeutralButton("Tamam", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        }).create();

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.secondaryColor));
            }
        });

        dialog.show();
    }

}