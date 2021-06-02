package com.hako.dreamproject.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hako.dreamproject.ActivityTracker;
import com.hako.dreamproject.CoinMarket;
import com.hako.dreamproject.LoginActivity;
import com.hako.dreamproject.R;
import com.hako.dreamproject.model.checkModel;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.LOADING;
import static com.hako.dreamproject.utils.Constant.MESSAGE;
import static com.hako.dreamproject.utils.Constant.TAG;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;

public class RewardFragment extends Fragment {
    View rootView;
    List<checkModel> checkModelList = new ArrayList<>();
    CheckAdapter checkAdapter;
    RecyclerView rvChecking;
    SwipeRefreshLayout swipetoRefresh;
    LinearLayout loading;
    LinearLayout noItem;
    NestedScrollView scrollView;
    FirebaseAuth mAuth;
    String userid;
    String token;
    TextView claim;
    TextView playGame;
    TextView play3Game;
    TextView fiftenMinutes;
    int total = 0;
    int days = 0;
    TextView hone;
    TextView htwo;
    TextView hthree;
    TextView hfour;
    TextView totalComplete;
    TextView points;
    LinearLayout pointsClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.reward_fragment, container, false);
        rvChecking = rootView.findViewById(R.id.rvcheckin);
        swipetoRefresh = rootView.findViewById(R.id.pulltorefresh);
        noItem = rootView.findViewById(R.id.noitem);
        loading = rootView.findViewById(R.id.loading);
        scrollView = rootView.findViewById(R.id.scrollview);
        claim = rootView.findViewById(R.id.claim);
        play3Game = rootView.findViewById(R.id.playThree);
        playGame = rootView.findViewById(R.id.launchGame);
        fiftenMinutes = rootView.findViewById(R.id.playfifteen);
        hfour = rootView.findViewById(R.id.hfour);
        hone = rootView.findViewById(R.id.hone);
        points = rootView.findViewById(R.id.points);
        pointsClick = rootView.findViewById(R.id.pointsClick);
        hthree = rootView.findViewById(R.id.hthree);
        htwo = rootView.findViewById(R.id.htwo);
        totalComplete = rootView.findViewById(R.id.totalComplete);
        scrollView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        swipetoRefresh.setOnRefreshListener(() -> {
            swipetoRefresh.setRefreshing(true);
            getPlayerData();
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            if (user != null) {
                userid = AppController.getInstance().getId();
                token = AppController.getInstance().getToken();
                points.setText(numberCalculation(Integer.parseInt(AppController.getInstance().getCoins())));

            } else {
                userid = "none";
                token = "none";
                points.setText("0");
                claim.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pointsClick.setOnClickListener(view -> {
            if (AppController.getInstance().getId().equalsIgnoreCase("0")) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), ActivityTracker.class);
                startActivity(intent);
            }
        });
        claim.setOnClickListener(view ->
                goForDaily()
        );
        getPlayerData();
        mylay();
        playGame.setOnClickListener(view ->
                loadFragment(new HomeFragment())
        );
        play3Game.setOnClickListener(view ->
                loadFragment(new HomeFragment())
        );
        fiftenMinutes.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CoinMarket.class);
            startActivity(intent);
        }
        );
        return rootView;
    }

    @SuppressLint("DefaultLocale")
    private String numberCalculation(long number) {
        if (number < 1000)
            return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f %c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    public void getPlayerData() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("dailycheck", API);
                params.put(USERID, userid);
                params.put(TOKEN, token);
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, LOADING);
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
                        checkModelList.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            checkModel ff = new checkModel();
                            ff.setDate(dataobj.getString("days"));
                            ff.setPoints(dataobj.getString("points"));
                            ff.setJoined(dataobj.getString("joined"));
                            checkModelList.add(ff);
                        }
                        checkAdapter = new CheckAdapter(getContext(), checkModelList);
                        rvChecking.setItemAnimator(new DefaultItemAnimator());
                        rvChecking.setHasFixedSize(true);
                        rvChecking.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        rvChecking.setAdapter(checkAdapter);
                        rvChecking.setVisibility(View.VISIBLE);
                    } else {
                        rvChecking.setVisibility(View.GONE);
                        noItem.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(ERROR, e.getMessage());
                }
                swipetoRefresh.setRefreshing(false);
                loading.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                noItem.setVisibility(View.GONE);
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

    public void mylay() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("myplay", API);
                params.put(USERID, userid);
                params.put(TOKEN, token);
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, LOADING);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        total = obj.getInt("total");
                        days = obj.getInt("day");
                        if (total < 101) {
                            totalComplete.setText(total + "/100");
                        } else {
                            totalComplete.setText("100/100");
                        }
                        if (days < 30) {
                            totalComplete.setText(total + "/100");
                        } else {
                            totalComplete.setText("30/30");
                        }
                        try{
                            if (total == 0) {
                                hone.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);

                            } else if (total > 0 && total > 15) {
                                hone.getBackground().setColorFilter(getContext().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                htwo.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);

                            } else if (total > 15 && total > 51) {
                                hone.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                htwo.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                hthree.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                            } else if (total > 50 && total > 100) {
                                hone.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                htwo.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                hthree.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                hfour.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

    public void goForDaily() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("claim_daily", API);
                params.put("userid", userid);
                params.put("token", token);
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
                        Toast.makeText(getContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
                        getPlayerData();
                    } else {
                        Toast.makeText(getContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(ERROR, e.getMessage());
                }

            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

    public class CheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        List<checkModel> data = Collections.emptyList();

        // create constructor to innitilize context and data sent from MainActivity
        public CheckAdapter(Context context, List<checkModel> data) {
            this.context = context;
            this.data = data;
        }

        // Inflate the layout when viewholder created
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_dailycheckin, parent, false);
            return new MyHolder(view);
        }

        // Bind data
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // Get current position of item in recyclerview to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final checkModel current = data.get(position);
            myHolder.day.setText("Day " + current.getDate());
            myHolder.points.setText("+" + current.getPoints());
            if (position == (getItemCount() - 1)) {
                myHolder.view.setVisibility(View.GONE);
            }
            if (current.getJoined().equalsIgnoreCase("1")) {
                myHolder.points.setText("✔");
            }
        }

        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView day;
            TextView points;
            View view;
            public MyHolder(View itemView) {
                super(itemView);
                day = itemView.findViewById(R.id.day);
                points = itemView.findViewById(R.id.points);
                view = itemView.findViewById(R.id.viewtwo);
            }

        }
    }


}
