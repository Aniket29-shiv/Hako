package com.hako.dreamproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.model.OtherMarketModel;
import com.hako.dreamproject.model.PointsModel;
import com.hako.dreamproject.payment.BuyNowActivity;
import com.hako.dreamproject.payment.CreateOrderActivity;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.RS;

public class CoinMarket extends AppCompatActivity {
    List<OtherMarketModel> otherMarketModels = new ArrayList<>();
    List<PointsModel> pointsModels = new ArrayList<>();
    RecyclerView rvBanner;
    RecyclerView rvPoints;
    OtherAdapter otherAdapter;
    PointsAdapter pointsAdapter;
    SwipeRefreshLayout refreshLayout;
    LinearLayout noItem;
    LinearLayout loading;
    ImageView myorder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_market);
        rvBanner = findViewById(R.id.rvBanner);
        rvPoints = findViewById(R.id.rvPoints);
        refreshLayout = findViewById(R.id.pulltorefresh);
        noItem = findViewById(R.id.noitem);
        myorder = findViewById(R.id.myorder);
        myorder.setOnClickListener(view -> {
            if (AppController.getInstance().getId().equalsIgnoreCase("0")) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else{
                Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                startActivity(intent);
            }
        });
        loading = findViewById(R.id.loading);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            getOther();
        });
        getOther();
    }
    public void getOther() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("banner", API);
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray(DATA);
                        JSONArray data = obj.getJSONArray("datas");
                        otherMarketModels.clear();
                        pointsModels.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            OtherMarketModel ff = new OtherMarketModel();
                            ff.setId(dataobj.getString("id"));
                            ff.setTitle(dataobj.getString("title"));
                            ff.setDescription(dataobj.getString("description"));
                            ff.setIcon(dataobj.getString("icon"));
                            ff.setAmount(dataobj.getString("amount"));
                            ff.setBackground(dataobj.getString("background"));
                            otherMarketModels.add(ff);
                        }
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataobj = data.getJSONObject(i);
                            PointsModel ff = new PointsModel();
                            ff.setId(dataobj.getString("id"));
                            ff.setAmount(dataobj.getString("amount"));
                            ff.setIcon(dataobj.getString("icon"));
                            ff.setPoints(dataobj.getString("points"));
                            pointsModels.add(ff);
                        }
                        otherAdapter = new OtherAdapter(getApplicationContext(), otherMarketModels);
                        pointsAdapter = new PointsAdapter(getApplicationContext(), pointsModels);
                        rvBanner.setItemAnimator(new DefaultItemAnimator());
                        rvBanner.setHasFixedSize(true);
                        rvBanner.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        rvBanner.setAdapter(new AlphaInAnimationAdapter(otherAdapter));
                        rvPoints.setItemAnimator(new DefaultItemAnimator());
                        rvPoints.setHasFixedSize(true);
                        rvPoints.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                        rvPoints.setAdapter(new AlphaInAnimationAdapter(pointsAdapter));
                        SnapHelper helper = new LinearSnapHelper();
                        helper.attachToRecyclerView(rvBanner);
                        rvBanner.setOnFlingListener(null);
                    } else {
                       //rvGame.setVisibility(View.GONE);
                        noItem.setVisibility(View.VISIBLE);

                    }
                    refreshLayout.setRefreshing(false);
                    loading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }
    public class OtherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        List<OtherMarketModel> data = Collections.emptyList();

        // create constructor to innitilize context and data sent from MainActivity
        public OtherAdapter(Context context, List<OtherMarketModel> data) {
            this.context = context;
            this.data = data;
        }

        // Inflate the layout when viewholder created
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.banneritem, parent, false);
            return new MyHolder(view);
        }

        // Bind data
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // Get current position of item in recyclerview to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final OtherMarketModel current = data.get(position);
           ClickShrinkEffectKt.applyClickShrink(myHolder.itemView);
           try{
               Glide.with(context).load(current.getIcon()).placeholder(R.drawable.other_placeholder).error(R.drawable.other_placeholder).into(myHolder.icon);
               Glide.with(context).load(current.getBackground()).placeholder(R.drawable.other_placeholder).error(R.drawable.other_placeholder).into(myHolder.background);
           }catch (Exception e){
               e.printStackTrace();
           }
           myHolder.amount.setText(RS+current.getAmount());
           myHolder.title.setText(current.getTitle());
           myHolder.description.setText(current.getDescription());
           myHolder.itemView.setOnClickListener(view -> {
               JSONObject js = new JSONObject();
               try {
                   js.put("id",current.getId());
                   js.put("amount",current.getAmount());
                   js.put("description",current.getDescription());
                   js.put("icon",current.getIcon());
                   js.put("title",current.getTitle());
                   js.put("background",current.getBackground());
                   Intent intent = new Intent(context, CreateOrderActivity.class);
                   intent.putExtra("data",js.toString());
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           });

        }

        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView background;
            CircleImageView icon;
            TextView description;
            TextView amount;
            public MyHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                background = itemView.findViewById(R.id.iv_itemMore_gameImage);
                icon = itemView.findViewById(R.id.icon);
                description = itemView.findViewById(R.id.description);
                amount = itemView.findViewById(R.id.amount);
            }
        }

    }
    public class PointsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        List<PointsModel> data = Collections.emptyList();
        // create constructor to innitilize context and data sent from MainActivity
        public PointsAdapter(Context context, List<PointsModel> data) {
            this.context = context;
            this.data = data;
        }

        // Inflate the layout when viewholder created
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.pointsitem, parent, false);
            return new MyHolder(view);
        }
        // Bind data
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // Get current position of item in recyclerview to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final PointsModel current = data.get(position);
            ClickShrinkEffectKt.applyClickShrink(myHolder.itemView);
            try{
                Glide.with(context).load(current.getIcon()).placeholder(R.drawable.extra_placeholder).error(R.drawable.extra_placeholder).into(myHolder.icon);

            }catch (Exception e){
                e.printStackTrace();
            }
            myHolder.amount.setText(RS+current.getAmount());
            myHolder.points.setText(current.getPoints()+" Points");
            myHolder.itemView.setOnClickListener(view -> {
                JSONObject js = new JSONObject();
                try {
                    js.put("amount",current.getAmount());
                    js.put("points",current.getPoints());
                    js.put("icon",current.getIcon());
                    Intent intent = new Intent(context, BuyNowActivity.class);
                    intent.putExtra("data",js.toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }
        class MyHolder extends RecyclerView.ViewHolder {
            TextView points;
            ImageView icon;
            TextView amount;
            public MyHolder(View itemView) {
                super(itemView);
                points = itemView.findViewById(R.id.points);
                icon = itemView.findViewById(R.id.icon);
                amount = itemView.findViewById(R.id.amount);
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
