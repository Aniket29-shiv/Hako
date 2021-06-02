package com.hako.dreamproject.utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UsableFunctions {

    public static boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static Dialog showTimeOutDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.time_out_dialog);
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_background));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.show();
        return dialog;
    }

    public static String getMessageId() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss__");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        return dateTime + sb.toString();
    }

    public static String getMessageRoomId() {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss__");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        return dateTime + sb.toString();
    }

    public static String getInvitationId() {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss__");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        return  dateTime + "InvitationId_" + sb.toString();
    }

    public static String getGameRoomId() {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 8; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return "gameRoomId_" + sb.toString();
    }

    public static Boolean checkLoggedInOrNot(){
        if(AppController.getInstance().getId().equalsIgnoreCase("0")){
            return false;
        }else{
            return true;
        }
    }
}
