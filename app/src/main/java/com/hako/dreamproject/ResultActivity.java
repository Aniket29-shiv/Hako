package com.hako.dreamproject;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ENTRY;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.PIC;
import static com.hako.dreamproject.utils.Constant.PlAYER;
import static com.hako.dreamproject.utils.Constant.ROOMID;
import static com.hako.dreamproject.utils.Constant.ROTATION;
import static com.hako.dreamproject.utils.Constant.SCORE;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.TRUE;
import static com.hako.dreamproject.utils.Constant.USERID;

public class ResultActivity extends AppCompatActivity {

    public static final String TAG = "example_dialog";
    String logo;
    TextView close;
    Button replay;
    CircleImageView icon;
    TextView desision;
    TextView myPoint;
    TextView playerScore;
    TextView earn;
    String playerId;
    String name;
    String roomid;
    CircleImageView playerPic;
    String userId;
    TextView played;
    String score;
    ProgressBar progressBar;
    TextView team;
    String entryFee;
    CircleImageView myPic;
    String profile;
    String playerPics;
    String rotation;
    TextView share;
    DatabaseReference databaseReference;
    long time = System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        myPic = findViewById(R.id.mypic);
        userId = AppController.getInstance().getId();
        profile = AppController.getInstance().getProfile();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        desision = findViewById(R.id.dicision);
        replay = findViewById(R.id.play_again);
        close = findViewById(R.id.close);
        myPoint = findViewById(R.id.myPoint);
        played = findViewById(R.id.playing);
        playerScore = findViewById(R.id.playerScore);
        icon = findViewById(R.id.logo);
        team = findViewById(R.id.team);
        progressBar = findViewById(R.id.progress);
        playerPic = findViewById(R.id.playerPic);
        earn = findViewById(R.id.earn);
        share = findViewById(R.id.share);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data = extras.getString(DATA);
            try {
                JSONObject extra = new JSONObject(data);
                playerId = extra.getString(PlAYER);
                rotation = extra.getString(ROTATION);
                name = extra.getString(NAME);
                Glide.with(getApplicationContext()).load(extra.getString("icon"))
                        .placeholder(R.drawable.profile_holder)
                        .error(R.drawable.profile_holder)
                        .into(icon);
                playerPics = extra.getString(PIC);
                Glide.with(getApplicationContext()).load(playerPics)
                        .placeholder(R.drawable.profile_holder)
                        .error(R.drawable.profile_holder)
                        .into(playerPic);
                Glide.with(getApplicationContext()).load(profile)
                        .placeholder(R.drawable.profile_holder)
                        .error(R.drawable.profile_holder)
                        .into(myPic);
                score = extra.getString(SCORE);
                myPoint.setText(score);
                roomid = extra.getString(ROOMID);
                logo = extra.getString("icon");
                entryFee = extra.getString(ENTRY);
                Online user = new Online(FALSE, time + "", score, "paused");
                databaseReference.child(AppController.getInstance().getId()).setValue(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (rotation.equalsIgnoreCase("0")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        close.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        replay.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        Glide.with(getApplicationContext())
                .load(logo)
                .into(icon);
        /////// GET SECOND USER /////////////////////////////
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(playerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Online user = dataSnapshot.getValue(Online.class);
                if (user != null) {
                    Log.e(Constant.TAG, "Online: " + user.getOnline() + ", Time " + user.getTime() + ", Score " + user.getScore() + ", status " + user.getStatus());
                    if (user.getOnline().equalsIgnoreCase(FALSE)) {
                        finalUpdate();
                        if (Integer.parseInt(score) > Integer.parseInt(user.getScore())) {
                            desision.setText("YOU WIN");
                            desision.setVisibility(View.VISIBLE);
                        } else {
                            desision.setText("YOU LOSE");
                            desision.setVisibility(View.VISIBLE);
                        }
                        played.setVisibility(View.GONE);
                        playerScore.setText(user.getScore());
                        progressBar.setVisibility(View.GONE);
                    } else {
                        played.setText("Oponet Steel Playing...");
                        playerScore.setText(user.getScore());
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("TAG", "Failed to read value.", error.toException());
            }
        });
    }


    public void finalUpdate() {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("final_update", API);
                params.put(TOKEN, AppController.getInstance().getToken());
                params.put(USERID, userId);
                params.put("s_id", roomid);
                params.put(SCORE, score);
                params.put(NAME, name);
                Log.e(TAG, params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e(Constant.TAG, s);
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(TRUE)) {
                        finalUpdate();
                    }
                } catch (Exception e) {
                    Log.e(Constant.TAG, e.getMessage());
                    finalUpdate();
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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
