package com.hako.dreamproject.notification;

/**
 * Created by Bhavna on 9/29/2017.
 */

import android.content.Intent;
import android.util.Log;

import com.hako.dreamproject.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private MyNotificationManager notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        System.out.println("responce***"+remoteMessage.getData().toString());
        Log.e(TAG, "response --- : " + remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
                System.out.println("responce***-"+json.getString("title"));
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //fireBase cloud messaging
    private void sendPushNotification(JSONObject json) {
        try {
            //getting the json data

            JSONObject data = json.getJSONObject("data");

            //parsing json data

            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");

            System.out.println("title**"+title+".."+message+".."+imageUrl);
            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);

            //if there is no image
            if (imageUrl.equals("null")) {
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
