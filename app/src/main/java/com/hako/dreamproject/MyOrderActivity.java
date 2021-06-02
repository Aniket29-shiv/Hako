package com.hako.dreamproject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.hako.dreamproject.model.OtherMarketModel;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
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
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;

public class MyOrderActivity extends AppCompatActivity {
    List<OtherMarketModel> otherMarketModels = new ArrayList<>();
    OtherAdapter otherAdapter;
    RecyclerView rvMyorder;
    LinearLayout loading;
    NestedScrollView scrollView;
    LinearLayout noitem;
    TextView close;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_activity);
        rvMyorder = findViewById(R.id.rvmyorder);
        loading = findViewById(R.id.loading);
        scrollView = findViewById(R.id.scrollable);
        noitem = findViewById(R.id.noitem);
        close = findViewById(R.id.close);
        close.setOnClickListener(view ->
            onBackPressed()
        );
        getOther();
    }

    public void getOther() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("myorder", API);
                params.put(USERID, AppController.getInstance().getId());
                params.put(TOKEN, AppController.getInstance().getToken());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(Constant.TAG,s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray(DATA);
                        otherMarketModels.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            OtherMarketModel ff = new OtherMarketModel();
                            ff.setId(dataobj.getString("id"));
                            ff.setTitle(dataobj.getString("title"));
                            ff.setDescription(dataobj.getString("description"));
                            ff.setIcon(dataobj.getString("icon"));
                            ff.setAmount(dataobj.getString("amount"));
                            ff.setStatus(dataobj.getString("order_status"));
                            otherMarketModels.add(ff);
                        }
                        otherAdapter = new OtherAdapter(getApplicationContext(), otherMarketModels);
                        rvMyorder.setItemAnimator(new DefaultItemAnimator());
                        rvMyorder.setHasFixedSize(true);
                        rvMyorder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        rvMyorder.setAdapter(new AlphaInAnimationAdapter(otherAdapter));
                    } else {
                        noitem.setVisibility(View.VISIBLE);

                    }
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
            View view = inflater.inflate(R.layout.myorderitem, parent, false);
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
            try {
                Glide.with(context).load(current.getIcon()).placeholder(R.drawable.other_placeholder).error(R.drawable.other_placeholder).into(myHolder.icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("status",current.getStatus());
            if (current.getStatus().equalsIgnoreCase("1")) {
                myHolder.status.setText("Processing");
            } else if (current.getStatus().equalsIgnoreCase("2")) {
                myHolder.status.setText("Complete");
            } else if (current.getStatus().equalsIgnoreCase("0")) {
                myHolder.status.setText("Reject");
            }else{
                myHolder.status.setText("Pending");
            }
            myHolder.title.setText(current.getTitle());
            myHolder.description.setText(current.getDescription());

        }

        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {
            TextView title;
            CircleImageView icon;
            TextView description;
            TextView status;

            public MyHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                icon = itemView.findViewById(R.id.icon);
                description = itemView.findViewById(R.id.description);
                status = itemView.findViewById(R.id.status);

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
