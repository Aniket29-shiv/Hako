package com.hako.dreamproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hako.dreamproject.model.Online;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ENTRY;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.GAMEICON;
import static com.hako.dreamproject.utils.Constant.GAMEURL;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.PIC;
import static com.hako.dreamproject.utils.Constant.PlAYER;
import static com.hako.dreamproject.utils.Constant.ROOMID;
import static com.hako.dreamproject.utils.Constant.ROTATION;
import static com.hako.dreamproject.utils.Constant.SCORE;
import static com.hako.dreamproject.utils.Constant.SEARCH;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.TRUE;
import static com.hako.dreamproject.utils.Constant.USERID;

public class MainActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    String myUserid;
    String secondUserid;
    String score = "0";
    long time = System.currentTimeMillis();
    WebView webView;
    String gameId;
    String currrentPageUrl;
    String points = "0";
    String rotation = "0";
    String playerId;
    String name;
    String pic;
    String icon;
    String entryFee;
    String roomid;
    TextView secondScore;
    boolean check = true;
    CircleImageView userpic;
    LinearLayout loading;
    FrameLayout frameTwo;
    ImageView gameicon;
    TextView gameName;
    String gname;

    String TAG_MAIN_ACTIVITY = "mainActivity";

    JSONObject chatObject = new JSONObject();

    @SuppressLint({"ObsoleteSdkInt", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        secondScore = findViewById(R.id.score);
        userpic = findViewById(R.id.pic);
        gameicon = findViewById(R.id.gameIcon);
        gameName = findViewById(R.id.gameName);
        webView = findViewById(R.id.webview);
        loading = findViewById(R.id.frameOne);
        frameTwo = findViewById(R.id.frametwo);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data = extras.getString(DATA);
            try {
                JSONObject extra = new JSONObject(data);
                chatObject = extra.getJSONObject("Data");
                currrentPageUrl = extra.getString(GAMEURL);
                Log.d(TAG_MAIN_ACTIVITY, "url: " + currrentPageUrl);
                Log.d(TAG_MAIN_ACTIVITY, "chatRoomId: " + chatObject.getString("chatRoomId"));
                myUserid = AppController.getInstance().getId();
                gameId = extra.getString(ROOMID);
                rotation = extra.getString(ROTATION);
                secondUserid = extra.getString(PlAYER);
                playerId = secondUserid;
                name = extra.getString(NAME);
                gname = extra.getString("gname");
                pic = extra.getString(PIC);
                icon = extra.getString(GAMEICON);
                entryFee = extra.getString(ENTRY);
                Glide.with(getApplicationContext()).load(pic).into(gameicon);
                gameName.setText(name);
                roomid = gameId;
                Glide.with(getApplicationContext()).load(pic)
                        .placeholder(R.drawable.profile_holder)
                        .error(R.drawable.profile_holder)
                        .into(userpic);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG_MAIN_ACTIVITY, "In Catch: " +  e.getMessage());
            }
            if (rotation.matches("1")) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//Set Landscape
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Set Portrait
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }

            try{
                webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
                webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
                webView.getSettings().setAllowFileAccess(true);
                webView.getSettings().setAppCacheEnabled(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webView.loadUrl(currrentPageUrl);
                ChromeClient webViewClient = new ChromeClient(this);
                webView.setWebChromeClient(webViewClient);
                WebViewClientImpl webViewClients = new WebViewClientImpl(this);
                webView.setWebViewClient(webViewClients);
            }
            catch (Exception e){
                Log.e(TAG_MAIN_ACTIVITY, "In Extras != null msg: " + e.getMessage());
            }
        }
        new Handler().postDelayed(() -> {
            loading.setVisibility(View.GONE);
            frameTwo.setVisibility(View.VISIBLE);

            databaseReference.child(secondUserid)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Online user = dataSnapshot.getValue(Online.class);
                    try {
                        if (user != null) {
                            Log.e(TAG_MAIN_ACTIVITY, "In Handler Online: " + user.getOnline() + ", Time " + user.getTime() + ", Score " + user.getScore() + ", status " + user.getStatus());
                            secondScore.setText(user.getScore());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG_MAIN_ACTIVITY, "In Database Catch Failed to read value: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG_MAIN_ACTIVITY, "In Databse onCanclled Failed to read value.", error.toException());
                }
            });
        }, 5000);

        /////// GET SECOND USER /////////////////////////////

    }

    public class ChromeClient extends WebChromeClient {
        Activity activity = null;

        public ChromeClient(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage msg) {
            if (msg.message().contains("wpwala")) {
                String data = msg.message().substring(msg.message().lastIndexOf("=") + 1);
                points = data;
                Online user = new Online(TRUE, time + "", points, "start");
                databaseReference.child(myUserid).setValue(user);

            }
            else if (msg.message().contains("overdata")) {
                String data = msg.message().substring(msg.message().lastIndexOf("=") + 1);
                points = data;
                if (check) {
                    addScore();
                    check = false;
                }
            }
            Log.d(TAG_MAIN_ACTIVITY, "In chromeCliend onConsoleMessage: " + msg.message());
            return true;
        }
    }
    public class WebViewClientImpl extends WebViewClient {
        Activity activity = null;

        public WebViewClientImpl(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Online user = new Online(FALSE, time + "", points, "paused");
        databaseReference.child(myUserid).setValue(user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Online user = new Online(TRUE, time + "", points, "start");
        databaseReference.child(myUserid).setValue(user);
    }

    public void openDialog() {
        JSONObject json = new JSONObject();
        try {
            Online user = new Online(FALSE, time + "", points, "paused");
            databaseReference.child(myUserid).setValue(user);
            json.put("icon", icon);
            json.put(SCORE, points);
            json.put(PlAYER, playerId);
            json.put(ENTRY, entryFee);
            json.put(PIC, pic);
            json.put(NAME, name);
            json.put("gname", gname);
            json.put(ROOMID, roomid);
            json.put(ROTATION, rotation);
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra(DATA, json.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.d(TAG_MAIN_ACTIVITY, "In openDialog catch: " + e.getMessage()
                    + " onjectsRequireNonNull: " + Objects.requireNonNull(e.getMessage()));
        }
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

    public void addScore() {
        class logIn extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("update_score", API);
                params.put(TOKEN, AppController.getInstance().getToken());
                params.put(USERID, AppController.getInstance().getId());
                params.put("s_id", gameId);
                params.put(SEARCH, points);
                Log.d(TAG_MAIN_ACTIVITY, "In addSocre doInBackground params: " + params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject js = new JSONObject(s);
                    if (js.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        openDialog();
                    } else {
                        addScore();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        logIn ru = new logIn();
        ru.execute();
    }

    @Override
    public void onBackPressed() {
        try {
            String chatRoomId = chatObject.getString("chatRoomId");
            String myScore = chatObject.getString("myScore");
            String friendId = chatObject.getString("friendId");
            String friendScore = chatObject.getString("friendScore");
            String friendProfile = chatObject.getString("friendProfile");
            String friendName = chatObject.getString("friendName");
            Log.d(TAG_MAIN_ACTIVITY, "In onBackPressed chatRoomId: " + chatRoomId);
            goToChatActivity(chatRoomId, myScore,
                                friendId, friendScore, friendProfile, friendName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToChatActivity(String chatRoomId, String myScore,
                                  String freindId, String freindScore, String freindProfile, String freindName){
        Intent intent = new Intent(this, chatActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("reciverId", freindId);
        intent.putExtra("myScore", myScore);
        intent.putExtra("freindProfile", freindProfile);
        intent.putExtra("freindName", freindName);
        startActivity(intent);
        finish();
    }

    //https://hoko.orsoot.com/games/sheepfight/index.html?playerusername=Mudssir Ahmed&playeravatarurl=https://lh3.googleusercontent.com/a/AATXAJxFKzBaAPUoIHNg_trmD6JP9jHCqQjXeUhgO0U1=s96-c&playerid=1&roomid=7
    //https://hoko.orsoot.com/games/sheepfight/index.html?playerusername=Muzammil Ahmed&playeravatarurl=https://lh3.googleusercontent.com/a-/AOh14GjV7gKst1gduxyoKm4b6zaVFsZMqqywdIadOYYK=s96-c&playerid=1&roomid=7
}