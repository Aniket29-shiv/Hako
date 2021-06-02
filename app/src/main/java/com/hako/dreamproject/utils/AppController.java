package com.hako.dreamproject.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.hako.dreamproject.R;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.MESSAGE;
import static com.hako.dreamproject.utils.Constant.TRUE;


public class AppController extends Application {

    String coins;
    String name;
    String email;
    String profile;
    String id;
    String status;
    String token;
    String refer;
    String midids;
    String upiid;
    String update;
    String user_unique_id;

    public String getUser_unique_id() {
        return user_unique_id;
    }
    public void setUser_unique_id(String user_unique_id) {
        this.user_unique_id = user_unique_id;
    }

    public String getMidids() {
        return midids;
    }

    public void setMidids(String midids) {
        this.midids = midids;
    }
    public String getUnderMain() {
        return underMain;
    }

    public void setUnderMain(String underMain) {
        this.underMain = underMain;
    }

    String underMain;
    private Activity Activity;

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }



    public String getUpiid() {
        return upiid;
    }
    public void setUpiid(String upiid) {
        this.upiid = upiid;
    }

    public String getRefer() {
        return refer;
    }
    public void setRefer(String refer) {
        this.refer = refer;
    }

    public void setType(String type) {
        sharedPref.edit().putString("TYPE", type).apply();
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String token) {
        this.status = token;
    }

    private SharedPreferences sharedPref;
    private static AppController mInstance;

    public String getCoins() {
        return this.coins;
    }
    public void setCoins(String coins) {
       this.coins = coins;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return this.profile;
    }
    public void setProfile(String profile) {

        this.profile = profile;
    }

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public static boolean hostAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("www.google.com", 80), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static synchronized AppController getInstance() {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPref = this.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);
        this.readData();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


    }
    public void readData() {
        this.setId(sharedPref.getString(getString(R.string.settings_account_id), "0"));
        this.setToken(sharedPref.getString(getString(R.string.token), "0"));

    }
    public void logout(AppCompatActivity activity) {

        AppController.getInstance().removeData();
        AppController.getInstance().readData();
    }
    public void removeData() {
        sharedPref.edit().putString(getString(R.string.settings_account_id), "0").apply();

    }

    public Boolean login(String authObj) {
        try {
            JSONObject js = new JSONObject(authObj);

            if(js.getString(ERROR).equalsIgnoreCase(FALSE)){
                JSONObject jsonObject = js.getJSONObject("data");
                JSONObject setting = js.getJSONObject("settings");
                this.setId(jsonObject.getString("userid"));
                this.setCoins(jsonObject.getString("points"));
                this.setName(jsonObject.getString("name"));
                this.setEmail(jsonObject.getString("email"));
                this.setProfile(jsonObject.getString("profile"));
                this.setStatus(jsonObject.getString("status"));
                this.setToken(jsonObject.getString("token"));
                this.setRefer(jsonObject.getString("refer"));
                this.setUpdate(setting.getString("version"));
                this.setUpiid(setting.getString("apk"));
                this.setUnderMain(setting.getString("mode"));
                this.setMidids(setting.getString("mid"));
                this.setUser_unique_id(jsonObject.getString("user_unique_id"));
                this.saveData();
                return true;
            }else if(js.getString(ERROR).equalsIgnoreCase(TRUE)){
               Toast.makeText(mInstance,js.getString(MESSAGE),Toast.LENGTH_LONG).show();
                return false;
            }else{
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error",e.getMessage());
           /* Intent i = new Intent(mInstance, NoInternet.class);
            startActivity(i);
            (Activity).mInstance.finish();*/
           Toast.makeText(mInstance,e.getMessage(),Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public void saveData() {
        sharedPref.edit().putString(getString(R.string.settings_account_id), this.getId()).apply();
        sharedPref.edit().putString(getString(R.string.token), this.getToken()).apply();
    }
}
