package com.hako.dreamproject.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.R;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.ServiceWrapper;
import com.hako.dreamproject.utils.Token_Res;
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
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;

public class BuyNowActivity extends AppCompatActivity {
    String amount;
    String points;
    String icon;
    CircleImageView buyicon;
    TextView txtAmount;
    TextView txtMessage;
    TextView paytm;
    String txnTokenString;
    String orderIdString;
    ProgressDialog progressDialog;
    TextView upi;
    TextView close;
    Random rand;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buynow_dialog);
        buyicon = findViewById(R.id.icon);
        txtAmount = findViewById(R.id.amount);
        txtMessage = findViewById(R.id.message);
        paytm = findViewById(R.id.paytm);
        upi = findViewById(R.id.upi);
        close = findViewById(R.id.close);
        close.setOnClickListener(view -> onBackPressed());
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
                points = extra.getString("points");
                icon = extra.getString("icon");
                txtMessage.setText(points + " Points Purchase");
                txtAmount.setText(RS + amount);
                Glide.with(getApplicationContext()).load(icon).into(buyicon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        paytm.setOnClickListener(view -> {
            boolean isAppInstalled = appInstalledOrNot("net.one97.paytm");
            if (isAppInstalled) {
                progressDialog.setMessage("please wait.");
                progressDialog.show();
                getToken(amount);
            } else {
                Toast.makeText(getApplicationContext(), "PAYTM INSTALL FIRST", Toast.LENGTH_LONG).show();
            }
        });
        upi.setOnClickListener(view ->
            Toast.makeText(getApplicationContext(),"Comming soon..",Toast.LENGTH_LONG).show()
        );
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
                        if (response.body().getBody().getTxnToken() != "") {
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
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it
        //String host = "https://securegw.paytm.in/";
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
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(Constant.TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            Log.e(Constant.TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(Constant.TAG, " data response - " + data.getStringExtra("response"));
            try {
                JSONObject js = new JSONObject(data.getStringExtra("response"));
                if (js.getString("STATUS").equalsIgnoreCase("TXN_SUCCESS")) {
                    addMoney();
                    Toast.makeText(BuyNowActivity.this, js.getString("STATUS"), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BuyNowActivity.this, js.getString("STATUS"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(Constant.TAG, " payment failed");
            Toast.makeText(BuyNowActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void addMoney() {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("addmoney", API);
                params.put("type", "recharge");
                params.put("name", "Points Purchase");
                params.put("points", points);
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
                    if(obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        Toast.makeText(getApplicationContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
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
