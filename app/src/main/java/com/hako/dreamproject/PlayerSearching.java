package com.hako.dreamproject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.dialog.TimeOutDialog;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.UsableFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ENTRY;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.GAMEICON;
import static com.hako.dreamproject.utils.Constant.GAMEURL;
import static com.hako.dreamproject.utils.Constant.IMAGE;
import static com.hako.dreamproject.utils.Constant.MESSAGE;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.PIC;
import static com.hako.dreamproject.utils.Constant.PlAYER;
import static com.hako.dreamproject.utils.Constant.ROOMID;
import static com.hako.dreamproject.utils.Constant.ROTATION;
import static com.hako.dreamproject.utils.Constant.SEARCH;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;


public class PlayerSearching extends AppCompatActivity {

    String roomId;
    String gameId;
    String entryFee;
    String url;
    String newRoomid = "0";
    String player2;
    Handler handler;
    int delay = 3000;
    int i;
    Runnable myRunnable;
    boolean status = false;
    CircleImageView myPic;
    CircleImageView playerPic;
    TextView myName;
    TextView playerName;
    TextView timeLeft;
    TextView close;
    String playerPicData;
    String playerNameData;
    String icon;
    String rotation;
    String gamename;
    JSONObject json = new JSONObject();
    boolean loading = true;
    int t = 0;
    TextView entry1;
    TextView entry2;
    AlertDialog builder;

    // CountDownTimer
    CountDownTimer cT, imageCountDownTimer;

    // Timer Task
    TimerTask timerTask;

    // ImageSearch Boolean
    Boolean imageSearch = true;

    String TAG_PLAYER_SEARCHING = "playerSearching";

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playersearching);
        init();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data = extras.getString("data");
            try {
                JSONObject extra = new JSONObject(data);
                gameId = extra.getString("id");
                entryFee = extra.getString("entry_fee");
                entry2.setText(entryFee);
                entry1.setText(entryFee);
                url = extra.getString("url");
                icon = extra.getString(IMAGE);
                rotation = extra.getString(ROTATION);
                gamename = extra.getString(NAME);
                if (rotation.equalsIgnoreCase("0")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                roomId = "GAMEID_" + gameId + "_NAME_" + gamename + "_ENTRY_" + entryFee;
                setOnline("1", "1");
                Glide.with(getApplicationContext()).load(AppController.getInstance().getProfile())
                        .placeholder(R.drawable.profile_holder)
                        .error(R.drawable.profile_holder)
                        .into(myPic);
                myName.setText(AppController.getInstance().getName());
            } catch (JSONException e) {
                Log.e(ERROR, Objects.requireNonNull(e.getMessage()));
            }
        }


    }

    public void init() {
        myName = findViewById(R.id.my_name);
        myPic = findViewById(R.id.my_pic);
        playerPic = findViewById(R.id.player_pic);
        playerName = findViewById(R.id.player_name);
        timeLeft = findViewById(R.id.timeLeft);
        close = findViewById(R.id.close);
        entry1 = findViewById(R.id.entry1);
        entry2 = findViewById(R.id.entry2);
        close.setOnClickListener(v -> finish());


        cT = new CountDownTimer(100000, 1000) {
            public void onTick(long millisUntilFinished) {
                String v = String.format("%02d", millisUntilFinished / 60000);
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                timeLeft.setText("Matching: " + v + ":" + String.format("%02d", va));
            }

            public void onFinish() {
                imageSearch = false;
                timeLeft.setText("Plyayer Not Found!");
//                new AlertDialog.Builder(PlayerSearching.this)
//                        .setMessage("Time Out Please Try Again")
//                        .setCancelable(false)
//                        .setPositiveButton("ok", (dialog, which) -> finish()).show();
                Dialog dialog = UsableFunctions.showTimeOutDialog(PlayerSearching.this);
                dialog.findViewById(R.id.iv_timeOutDialog_close).setOnClickListener( view -> {
                    dialog.dismiss();
                    finish();
                });
            }
        };
        cT.start();

    }

    public static int count=0;
    int intArray[]  = {R.drawable.face_1,
            R.drawable.face_2,
            R.drawable.face_3,
            R.drawable.face_4,
            R.drawable.face_5,
            R.drawable.face_6,
            R.drawable.face_7,
            R.drawable.face_8,
            R.drawable.face_9,
            R.drawable.face_10,
            R.drawable.face_11,
            R.drawable.face_12,};


    public void setOnline(final String online, final String played) {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("set_online_game", API);
                params.put(TOKEN, AppController.getInstance().getToken());
                params.put(USERID, AppController.getInstance().getId());
                params.put("id", gameId);
                params.put("online", online);
                params.put("played", played);
                Log.e(Constant.TAG, params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e("TAG", s);
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        if (online.equalsIgnoreCase("1")) {
                            search();
                        } else {
                            onCancelled();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(ERROR, e.getMessage());
                }
            }
        }
        Login ru = new Login();
        ru.execute();
    }

    public void search() {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                String myID = AppController.getInstance().getUser_unique_id();
                String token = AppController.getInstance().getToken();
                Log.d(TAG_PLAYER_SEARCHING, "userId: "+ myID + " token: " + token + " roomID: " + roomId);
                params.put(TOKEN, token);
                params.put(USERID, myID);
                params.put(SEARCH, roomId);
                params.put(ENTRY, entryFee);
                params.put("searching", API);

                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    Log.d(TAG_PLAYER_SEARCHING, " res: " + obj);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        String player1 = obj.getString("player1");
                        player2 = obj.getString("player2");
                        newRoomid = obj.getString(ROOMID);

                        if(player1.equals(AppController.getInstance().getUser_unique_id())){
                            url += "&playerid=" + "2&roomid=";
                        }else{
                            url += "&playerid=" + "1&roomid=";
                        }

                        Log.d(TAG_PLAYER_SEARCHING, "chatRoomId: " + obj.getJSONObject("chatData").getString("chatRoomId"));
                        Log.d(TAG_PLAYER_SEARCHING, "chatRoomId: " + obj.toString());

                        playerNameData = obj.getString(NAME);
                        playerPicData = obj.getString(PIC);
                        loading = false;
                        Glide.with(getApplicationContext()).load(playerPicData)
                                .placeholder(R.drawable.profile_holder)
                                .error(R.drawable.profile_holder)
                                .into(playerPic);
                        playerName.setText(playerNameData);
                        playerName.setVisibility(View.VISIBLE);
                        handler = new Handler();
                        myRunnable = () -> {
                            try {
                                json.put(PlAYER, player2);
                                json.put(NAME, playerNameData);
                                json.put(PIC, playerPicData);
                                json.put(GAMEURL, url + newRoomid);
                                json.put("gname", gamename);
                                json.put(GAMEICON, icon);
                                json.put(ENTRY, entryFee);
                                json.put(ROOMID, newRoomid);
                                json.put(ROTATION, rotation);
                                json.put("Data", obj.getJSONObject("chatData"));
                                join(gameId, entryFee, gamename);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        };
                        handler.postDelayed(myRunnable, 3000);
                    } else {
                        if (!status) {
                            if(imageSearch){
                                playerPic.setImageResource(intArray[count]);
                                count+=1;
                                if(count > 11){
                                    count = 0;
                                }
                            }

                            search();
                        }else
                        if (obj.getInt(ROOMID) > 0) {
                            newRoomid = obj.getString(ROOMID);
                        }
                    }

                } catch (Exception e) {
                    if (!status) {
                        handler = new Handler();
                        myRunnable = PlayerSearching.this::search;
                        handler.postDelayed(myRunnable, delay);
                    }
                    Log.e(ERROR, e.getMessage());
                }
            }
        }
        Login ru = new Login();
        ru.execute();
    }

    public void join(final String id, final String entryFee, final String name) {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("join", API);
                params.put(TOKEN, AppController.getInstance().getToken());
                params.put(USERID, AppController.getInstance().getId());
                params.put("id", id);
                params.put(ENTRY, entryFee);
                params.put(NAME, name);
                Log.e("TAG", params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e(Constant.TAG, s);
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        int parse = Integer.parseInt(AppController.getInstance().getCoins()) - Integer.parseInt(entryFee);
                        AppController.getInstance().setCoins(parse + "");
                        Intent intent;
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(DATA, json.toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
                    }
                    cT.cancel();
                } catch (Exception e) {
                    Log.e(ERROR, e.getMessage());
                }
            }

        }
        Login ru = new Login();
        ru.execute();
    }

    public void flag() {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("notfound", API);
                params.put(TOKEN, AppController.getInstance().getToken());
                params.put(USERID, AppController.getInstance().getId());
                params.put("s_id", newRoomid);
                Log.e(Constant.TAG, params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e(Constant.TAG, s);
                super.onPostExecute(s);
            }
        }
        Login ru = new Login();
        ru.execute();
    }

    @Override
    protected void onPause() {
        setOnline("-1", "0");
        if (Integer.parseInt(newRoomid) > 0) {
            flag();
        }
        status = true;
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(Constant.TAG, "Stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(Constant.TAG, "Distroy");
        super.onDestroy();
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

