package com.hako.dreamproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.HashMap;

import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.NAME;

public class SplashActivity extends AppCompatActivity {
    String email;
    String name;
    String profile;
    FirebaseAuth mAuth;
//    ImageView extraPoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        EmojiManager.install(new GoogleEmojiProvider());

//        extraPoints = findViewById(R.id.extra_points);
//        Glide.with(this).load(R.drawable.packman).into(extraPoints);
        mAuth = FirebaseAuth.getInstance();
        if (!isEmulator()) {

            new Handler().postDelayed(() -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && !AppController.getInstance().getId().equalsIgnoreCase("0")) {
                    email = user.getEmail();
                    name = user.getDisplayName();
                    profile = user.getPhotoUrl() + "";
                    login();
                } else {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }

            }, 5000);
        } else {
            Toast.makeText(getApplicationContext(), "This Device Not Supported", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void login() {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("register_or_login", "1");
                params.put("email", email);
                params.put("profile", profile);
                params.put(NAME, name);
                params.put("refer", "0");
                Log.e("Constants.TAG", params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e(Constant.TAG, s);
                super.onPostExecute(s);
                if (AppController.getInstance().login(s)) {
                    if (AppController.getInstance().getUnderMain().equalsIgnoreCase("1")) {
                        Toast.makeText(getApplicationContext(), "Maintance Mode", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(SplashActivity.this, Maintance.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else if (AppController.getInstance().getStatus().equalsIgnoreCase("1") && AppController.getInstance().getUnderMain().equalsIgnoreCase("0")) {
                        Toast.makeText(getApplicationContext(), "Your Account Was Suspendend", Toast.LENGTH_LONG).show();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                } else {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }


            }
        }
        Login ru = new Login();
        ru.execute();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
////        if (hasFocus) {
////            getWindow().getDecorView().setSystemUiVisibility(
////                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
////                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                            | View.SYSTEM_UI_FLAG_FULLSCREEN
////                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
////        }
//    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }


}
