package com.hako.dreamproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Date;
import java.util.HashMap;

import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;


public class LoginActivity extends AppCompatActivity {
    Button loginWithGoogle;
    EditText refer;
    String code;
    String name;
    String email;
    ImageView packman;
    String profile;
    FirebaseAuth mAuth;
    String phone;
    TextView goodmoring;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginWithGoogle = findViewById(R.id.login_with_google);
        refer = findViewById(R.id.refer);
        packman = findViewById(R.id.img_hori);
        goodmoring = findViewById(R.id.goodmorning);
        String greeting = null;
        Date dt = new Date();
        int hours = dt.getHours();
        if (hours >= 1 && hours <= 12) {
            greeting = "Good Morning";
        } else if (hours >= 12 && hours <= 16) {
            greeting = "Good Afternoon";
        } else if (hours >= 16 && hours <= 21) {
            greeting = "Good Evening";
        } else if (hours >= 21 && hours <= 24) {
            greeting = "Good Night";
        }
        goodmoring.setText("Hi' " + greeting);
        Glide.with(this).load(R.drawable.packman).into(packman);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                //.requestIdToken("735929854041-kig5f76te5rhaab3q9qe1m6h4m2vf7vt.apps.googleusercontent.com")
                .requestEmail()
                .build();
        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginWithGoogle.setOnClickListener(view -> {
            if (refer.length() == 0) {
                code = "0";
            } else {
                code = refer.getText().toString();
            }
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.e("TAG", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        name = user.getDisplayName();
                        email = user.getEmail();
                        profile = user.getPhotoUrl().toString();
                        phone = "0";
                        login();
                    } else {
                        Log.e("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
                params.put("name", name);
                params.put("refer", code);
                Log.e("Constants.TAG", params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(ERROR, FALSE);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e("TAG", s);
                super.onPostExecute(s);
                if (AppController.getInstance().login(s)) {
                    if (AppController.getInstance().getUnderMain().equalsIgnoreCase("1")) {
                        Toast.makeText(getApplicationContext(), "Maintance Mode", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this, Maintance.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else if (AppController.getInstance().getStatus().equalsIgnoreCase("1") && AppController.getInstance().getUnderMain().equalsIgnoreCase("0")) {
                        Toast.makeText(getApplicationContext(), "Your Account Was Suspendend", Toast.LENGTH_LONG).show();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                } else {
                    Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
        Login ru = new Login();
        ru.execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}

