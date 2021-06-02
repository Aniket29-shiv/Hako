package com.hako.dreamproject.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hako.dreamproject.BrowserActivity;
import com.hako.dreamproject.LoginActivity;
import com.hako.dreamproject.R;
import com.hako.dreamproject.chatActivity;
import com.hako.dreamproject.dialog.showInviteDialog;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.UsableFunctions;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.GAMELIST;
import static com.hako.dreamproject.utils.Constant.HELPDESK;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.PRIVACY;
import static com.hako.dreamproject.utils.Constant.TAG;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERAGREEMENT;
import static com.hako.dreamproject.utils.Constant.USERID;

public class ProfileFragment extends Fragment {
    View rootView;

    CircleImageView profile;

    LinearLayout agreements;
    LinearLayout privacy;
    LinearLayout helpDesk;

    // TextView
    TextView name;
    TextView email;
    TextView tvMyId;
    TextView totalPlayed;
    TextView todayPlayed;
    TextView refercode;

    // LinearLayout
    LinearLayout lL_inviteFreind;

    // Firebase
    FirebaseFirestore db;

    String TAG_INVITE_FRIEND = "inviteFriend";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        setViews();
        setOnClickAnimation();
        setOnClickListeners();

        if(AppController.getInstance().getId().equalsIgnoreCase("0")){
            name.setText("Login");
            email.setText("Explore All Features");
        }else{
            name.setText(AppController.getInstance().getName());
            email.setText(AppController.getInstance().getEmail());
            tvMyId.setText("ID: " + AppController.getInstance().getUser_unique_id());
            Glide.with(getContext()).load(AppController.getInstance().getProfile()).into(profile);
            stats();
        }

        return rootView;
    }
    private void setViews(){
        // TextView
        name = rootView.findViewById(R.id.name);
        email = rootView.findViewById(R.id.email);
        tvMyId = rootView.findViewById(R.id.tv_profileFrag_myId);
        totalPlayed = rootView.findViewById(R.id.totalPlayed);
        todayPlayed = rootView.findViewById(R.id.today);
        refercode = rootView.findViewById(R.id.refer);

        // ImageView
        profile = rootView.findViewById(R.id.profile);
        agreements = rootView.findViewById(R.id.agrements);
        privacy = rootView.findViewById(R.id.privacy);
        helpDesk = rootView.findViewById(R.id.help);

        // LinearLayout
        lL_inviteFreind = rootView.findViewById(R.id.lL_profile_inviteFreinds);

        // Firebase
        db = FirebaseFirestore.getInstance();
    }
    private void setOnClickAnimation(){
        ClickShrinkEffectKt.applyClickShrink(lL_inviteFreind);
        ClickShrinkEffectKt.applyClickShrink(refercode);
    }
    private void setOnClickListeners(){
        lL_inviteFreind.setOnClickListener(view -> {
            if(UsableFunctions.checkLoggedInOrNot()){
                Log.d(TAG_INVITE_FRIEND, "myId: " + AppController.getInstance().getId());
                showPoPForInviteFriends();
            }else{
                Toast.makeText(requireContext(), "Please Login First", Toast.LENGTH_SHORT).show();
                gotoLoginActivity();
            }
        });
        refercode.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", AppController.getInstance().getRefer());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(),"Code Copy :"+AppController.getInstance().getRefer(),Toast.LENGTH_LONG).show();
        });
        agreements.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra("url",USERAGREEMENT);
            intent.putExtra(NAME,"User Agreements");
            startActivity(intent);
        });
        privacy.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra("url",PRIVACY);
            intent.putExtra(NAME,"Privacy Policy");
            startActivity(intent);
        });
        helpDesk.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra("url",HELPDESK);
            intent.putExtra(NAME,"HelpDesk");
            startActivity(intent);
        });
    }

    // ClickFunctions
    private void showPoPForInviteFriends(){
        showInviteDialog inviteDialog = new showInviteDialog();
        Dialog dialog = inviteDialog.showDialog(new Dialog(requireContext()), requireContext());
        EditText etInviteId = dialog.findViewById(R.id.et_inviteDialog_playerId);
        dialog.findViewById(R.id.btn_inviteDialog_invite).setOnClickListener( view -> {
            String friendId = etInviteId.getText().toString().trim();
            if(!friendId.isEmpty()){
                setInvitationFriend(friendId, dialog);
            }
        });
    }
    private void setInvitationFriend(String friendId, Dialog dialog){
        String invitationId = UsableFunctions.getInvitationId();
        String myId = AppController.getInstance().getUser_unique_id();
        String chatRoomId = UsableFunctions.getMessageRoomId();
        Log.d(TAG_INVITE_FRIEND, "friendId: " + friendId + " InvitationId: " + invitationId + "myId: " + myId);

        HashMap<String, String> inviteMap = new HashMap<>();
        inviteMap.put("senderId", myId);
        inviteMap.put("senderName", AppController.getInstance().getName());
        inviteMap.put("senderImage", AppController.getInstance().getProfile());
        inviteMap.put("chatRoomId", chatRoomId);
        inviteMap.put("receiverId", friendId);

        db.collection("INVITATION")
                .document(invitationId)
                .set(inviteMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Invitation Sent", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(requireContext(), chatActivity.class);
                        intent.putExtra("chatRoomId", chatRoomId);
                        intent.putExtra("reciverId", friendId);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_INVITE_FRIEND, "Failed due to: " + e.getMessage());
                        Toast.makeText(requireContext(), "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void invite(String myProfile, String myId, String chatRoomId, String reciverId, String checkUser, String senderName, String invitationId){
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("senderImage", myProfile);
                params.put("senderId", myId);
                params.put("chatRoomId", chatRoomId);
                params.put("receiverId", reciverId);
                params.put("checkuser", checkUser);
                params.put("senderName", senderName);
                params.put("invitationId", invitationId);

                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e("loading", "loading..");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray("data");
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

//    private void addChatRoomInUser(String chatRoomId, String myId, String freindId){
//        DocumentReference docRef = db.collection("USERS").document(myId)
//                .collection("chatRooms").document(chatRoomId);
//        HashMap<String, String> chatRoom = new HashMap<>();
//        chatRoom.put("chatRoomId", chatRoomId);
//        chatRoom.put("freindId", freindId);
//        docRef.set(chatRoom);
//    }


    private void gotoLoginActivity(){
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    public void stats() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("stats", API);
                params.put(USERID, AppController.getInstance().getId());
                params.put(TOKEN, AppController.getInstance().getToken());
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
                        totalPlayed.setText(obj.getString("total"));
                        todayPlayed.setText(obj.getString("today"));
                        refercode.setText(AppController.getInstance().getRefer());
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

}
