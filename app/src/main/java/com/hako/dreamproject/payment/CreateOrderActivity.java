package com.hako.dreamproject.payment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.MyOrderActivity;
import com.hako.dreamproject.R;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.ServiceWrapper;
import com.hako.dreamproject.utils.Token_Res;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.MESSAGE;
import static com.hako.dreamproject.utils.Constant.RS;
import static com.hako.dreamproject.utils.Constant.RULES;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;

public class CreateOrderActivity extends AppCompatActivity {
    String amount;
    String id;
    String description;
    String background;
    String icon;
    String title;
    String txnTokenString;
    String orderIdString;
    ProgressDialog progressDialog;
    Random rand;
    ImageView imgBackground;
    CircleImageView imgIcon;
    TextView txtTitle;
    TextView txtDescription;
    TextView txtBuyNow;
    String myids;
    WebView webView;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        txtBuyNow = findViewById(R.id.buynow);
        webView = findViewById(R.id.webview);
        imgBackground = findViewById(R.id.background);
        imgIcon = findViewById(R.id.icon);
        txtTitle = findViewById(R.id.title);
        txtDescription = findViewById(R.id.description);
        progressDialog = new ProgressDialog(this);
        Bundle extras = getIntent().getExtras();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String date = df.format(c.getTime());
        rand = new Random();
        int min = 1000;
        int max = 9999;
        int randomNum = rand.nextInt((max - min) + 1) + min;
        orderIdString = date + String.valueOf(randomNum);
        if (extras != null) {
            String data = extras.getString("data");
            try {
                JSONObject extra = new JSONObject(data);
                amount = extra.getString("amount");
                title = extra.getString("title");
                description = extra.getString("description");
                icon = extra.getString("icon");
                id = extra.getString("id");
                background = extra.getString("background");
                txtTitle.setText(title);
                txtDescription.setText(description);
                txtBuyNow.setText(RS + amount + " Buy Now");
                Glide.with(getApplicationContext()).load(icon).into(imgIcon);
                Glide.with(getApplicationContext()).load(background).into(imgBackground);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        webView.setWebViewClient(webViewClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(RULES);
        txtBuyNow.setOnClickListener(view -> {
            boolean isAppInstalled = appInstalledOrNot("net.one97.paytm");
            if (isAppInstalled) {

                dialog();
            } else {
                Toast.makeText(getApplicationContext(), "PAYTM INSTALL FIRST", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void dialog() {
        View view = LayoutInflater.from(CreateOrderActivity.this).inflate(R.layout.playerid_edittext, null);
        final EditText playerids = (EditText) view.findViewById(R.id.playerid);
        TextView ok = view.findViewById(R.id.ok_button);
        TextView cancle = view.findViewById(R.id.cancel_button);
        ok.setOnClickListener(view1 -> {
            if (playerids.length() == 0) {
                Toast.makeText(getApplicationContext(), "Enter Playerid", Toast.LENGTH_LONG).show();
            } else {
                myids = playerids.getText().toString();
                progressDialog.setMessage("please wait.");
                progressDialog.show();
                getToken(amount);
            }
        });
        cancle.setOnClickListener(view1 ->
            bottomSheetDialog.dismiss()
        );

        bottomSheetDialog = new BottomSheetDialog(CreateOrderActivity.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
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

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.fillInStackTrace();
        }
        return false;
    }

    private void getToken(String amount) {
        ServiceWrapper serviceWrapper = new ServiceWrapper(null);
        Call<Token_Res> call = serviceWrapper.getTokenCall("12345", AppController.getInstance().getMidids(), orderIdString, amount);
        call.enqueue(new Callback<Token_Res>() {
            @Override
            public void onResponse(Call<Token_Res> call, Response<Token_Res> response) {

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        if (!response.body().getBody().getTxnToken().equalsIgnoreCase("")) {
                            Log.e("TAG", " transaction token : " + response.body().getBody().getTxnToken());
                            startPaytmPayment(response.body().getBody().getTxnToken(), amount);

                        } else {
                            Log.e("TAG", " Token status false");
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAG", " error in Token Res " + e.toString());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Token_Res> call, Throwable t) {
                Log.e("TAG", " response error " + t.toString());
                progressDialog.dismiss();
            }
        });
    }

    public void startPaytmPayment(String token, String amount) {
        txnTokenString = token;
        /*// for test mode use it*/
       String host = "https://securegw-stage.paytm.in/";
        // for production mode use it*/
       // String host = "https://securegw.paytm.in/";
        String callBackUrl = host + "theia/paytmCallback?ORDER_ID=" + orderIdString;
        Log.e("TAG", " callback URL " + callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdString, AppController.getInstance().getMidids(), txnTokenString, amount, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(Constant.TAG, "Response (onTransactionResponse) : " + bundle.toString());
            }

            @Override
            public void networkNotAvailable() {
                Log.e(Constant.TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(Constant.TAG, " onErrorProcess " + s);
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(Constant.TAG, "Clientauth " + s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(Constant.TAG, " UI error " + s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(Constant.TAG, " error loading web " + s + "--" + s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(Constant.TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(Constant.TAG, " transaction cancel " + s);
            }
        });
        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, 227);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(Constant.TAG, " result code " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 227 && data != null) {

            Log.e(Constant.TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(Constant.TAG, " data response - " + data.getStringExtra("response"));
            try {
                JSONObject js = new JSONObject(data.getStringExtra("response"));
                if (js.getString("STATUS").equalsIgnoreCase("TXN_SUCCESS")) {
                    addMoney();
                }
                Toast.makeText(CreateOrderActivity.this, js.getString("STATUS"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(Constant.TAG, " payment failed");
            Toast.makeText(CreateOrderActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void addMoney() {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("create_order", API);
                params.put("id", id);
                params.put("player", myids);
                params.put(USERID, AppController.getInstance().getId());
                params.put(TOKEN, AppController.getInstance().getToken());
                Log.e(Constant.TAG, params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Please Wait..");
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e("TAG", s);
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        Toast.makeText(getApplicationContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Ohh ! Network Issue", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
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
