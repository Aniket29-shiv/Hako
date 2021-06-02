package com.hako.dreamproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hako.dreamproject.model.TrackerModel;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;

public class ActivityTracker extends AppCompatActivity {
    List<TrackerModel> trackerModels = new ArrayList<>();
    TrackerAdapter trackerAdapter;
    LinearLayout noitem;
    LinearLayout progress;
    TextView close;
    RecyclerView rvTracker;
    TextView myPoints;
    ImageView withdraw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_activity);
        rvTracker = findViewById(R.id.rvTracker);
        noitem = findViewById(R.id.noitem);
        progress = findViewById(R.id.loading);
        close = findViewById(R.id.close);
        myPoints = findViewById(R.id.myPoints);
        withdraw = findViewById(R.id.withdraw);
        withdraw.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(),"Withdraw",Toast.LENGTH_LONG).show();
        });
        close.setOnClickListener(view ->
                onBackPressed()
        );
        myPoints.setText(AppController.getInstance().getCoins());
        getData();
    }

    public void getData() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("tracker", API);
                params.put("userid", AppController.getInstance().getId());
                params.put("token", AppController.getInstance().getToken());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e("loading", "loading..");
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(ERROR, s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        trackerModels.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            TrackerModel ff = new TrackerModel();
                            ff.setName(dataobj.getString("name"));
                            ff.setPoints(dataobj.getString("points"));
                            ff.setDate(dataobj.getString("date"));
                            ff.setType(dataobj.getString("type"));
                            trackerModels.add(ff);
                        }
                        trackerAdapter = new TrackerAdapter(ActivityTracker.this, trackerModels);
                        rvTracker.setItemAnimator(new DefaultItemAnimator());
                        rvTracker.setHasFixedSize(true);
                        rvTracker.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        rvTracker.setAdapter(trackerAdapter);
                        rvTracker.setVisibility(View.VISIBLE);
                        rvTracker.setAdapter(new AlphaInAnimationAdapter(trackerAdapter));
                    } else {
                        noitem.setVisibility(View.VISIBLE);
                    }
                    progress.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(ERROR, e.getMessage());
                }

            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

    public class TrackerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        List<TrackerModel> data = Collections.emptyList();

        // create constructor to innitilize context and data sent from MainActivity
        public TrackerAdapter(Context context, List<TrackerModel> data) {
            this.context = context;
            this.data = data;
        }

        // Inflate the layout when viewholder created
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.tracker_item, parent, false);
            return new MyHolder(view);
        }

        // Bind data
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // Get current position of item in recyclerview to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final TrackerModel current = data.get(position);
            if (current.getType().equalsIgnoreCase("play")) {
                myHolder.points.setText("-" + current.getPoints());
                myHolder.points.setTextColor(context.getResources().getColor(R.color.colorError));
            } else {
                myHolder.points.setText("+" + current.getPoints());
                myHolder.points.setTextColor(context.getResources().getColor(R.color.green));
            }
            myHolder.name.setText(current.getName());
            myHolder.date.setText(current.getDate());
        }

        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView points;
            TextView date;

            public MyHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                points = itemView.findViewById(R.id.points);
                date = itemView.findViewById(R.id.date);
            }

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
}
