package com.hako.dreamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.hako.dreamproject.adapters.chat.MessagesAdapter;
import com.hako.dreamproject.adapters.chat.chatGameAdapter;
import com.hako.dreamproject.model.GameModel;
import com.hako.dreamproject.model.Message;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.UsableFunctions;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.GAMELIST;
import static com.hako.dreamproject.utils.Constant.IMAGE;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.ROTATION;

public class chatActivity extends AppCompatActivity {

    // ImageView
    private ImageView ivBack;
    private ImageView ivMyProfile;
    private ImageView ivFreindProfile;
    private ImageView ivFollowPlayer;
    private  ImageView ivSendMessage;
    private ImageView ivShowEmoji;

    // ConstraintLayout
    private ConstraintLayout cLSelectedGame;
    private ConstraintLayout cLSelectedGameBackground;

    // TextView
    private TextView tvCancelSelectedGame, tvSelctedGameName;
    private TextView tvMyName, tvMyScore;
    private TextView tvFreindName, tvFreindScore;

    // Progress Loading
    private CircularProgressIndicator pbSelectedGameLoading;

    // RecyclerView
    private RecyclerView rvGames;
    private RecyclerView rvMessages;

    // EditText
    private EmojiEditText etMessage;

    // Data
    private Boolean Follow = false;
    private Boolean Emoji = false;
    List<GameModel> gameModelList = new ArrayList<>();

    // TAG
    String TAG_CHAT_ACTIVITY = "chatActivity";

    // popup Emoji
    EmojiPopup popup;

    // Adapters
    private MessagesAdapter messagesAdapter;

    // Firebase
    private FirebaseFirestore db;

    // Message data
    String messageRoomId;
    String myId, myProfileImage, myName, myScore;
    String reciverId, freindProfileImage, freindName, freindScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get from previous activity
        messageRoomId = getIntent().getStringExtra("chatRoomId");
        myScore = getIntent().getStringExtra("myScore");
        reciverId = getIntent().getStringExtra("reciverId");
        freindProfileImage = getIntent().getStringExtra("freindProfile");
        freindName = getIntent().getStringExtra("freindName");
        freindScore = getIntent().getStringExtra("freindScore");


        myId = AppController.getInstance().getUser_unique_id();
        myProfileImage = AppController.getInstance().getProfile();
        myName = AppController.getInstance().getName();

        setViews();
        setOnClickListners();
        clickShrinkEffect();
        setDataOnViews();
        getGamesData();
        setMessagesIntoRecyclerView();
        getMessageData(messageRoomId);

    }
    private void setViews(){
        //ImageView
        ivBack = findViewById(R.id.iv_chatActivity_backPressed);
        ivMyProfile = findViewById(R.id.iv_chatActivity_myProfile);
        ivFreindProfile = findViewById(R.id.iv_chatActivity_freindProfile);
        ivFollowPlayer = findViewById(R.id.iv_chatActivity_followPlayer);
        ivSendMessage = findViewById(R.id.iv_chatActivity_sendMessage);
        ivShowEmoji = findViewById(R.id.iv_chatActivity_showEmoji);

        // TextView
        tvSelctedGameName = findViewById(R.id.tv_chatActivity_selectedGame);
        tvCancelSelectedGame = findViewById(R.id.tv_chatActivity_cancelSelectedGame);
        tvMyName = findViewById(R.id.tv_chatActivity_myName);
        tvMyScore = findViewById(R.id.tv_chatActivity_myScore);
        tvFreindName = findViewById(R.id.tv_chatActivity_freindName);
        tvFreindScore = findViewById(R.id.tv_chatActivity_freindScore);

        // Constraintlayout
        cLSelectedGame = findViewById(R.id.cL_chatActivity_selectGame);
        cLSelectedGameBackground = findViewById(R.id.cL_chatActicity_selectedGameBackground);

        //RecyclerView
        rvGames = findViewById(R.id.rv_chatActivity_games);
        rvMessages = findViewById(R.id.rv_chatActivity_messages);

        // CircularProgressIndicator
        pbSelectedGameLoading = findViewById(R.id.pb_chatActivity_selectedGameLoading);

        // EditText
        etMessage = findViewById(R.id.et_chatActivity_message);

        // emoji popup
        popup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.cL_chatActivity_rootView)).build(etMessage);

        // Firebase
        db = FirebaseFirestore.getInstance();
    }
    private void setOnClickListners(){
        ivBack.setOnClickListener( view -> {
            onBackPressed();
        });
        ivFollowPlayer.setOnClickListener( view -> {
            handleFollow();
        });
        tvCancelSelectedGame.setOnClickListener( view -> {
            rvGames.setVisibility(View.VISIBLE);
            cLSelectedGame.setVisibility(View.GONE);
        });
        ivSendMessage.setOnClickListener( view -> {
            handleSendMessage(etMessage.getText().toString().trim(), messageRoomId);
        });
        ivShowEmoji.setOnClickListener( view -> {
            handleShowEmoji();
        });
    }
    private void clickShrinkEffect(){
        ClickShrinkEffectKt.applyClickShrink(ivBack);
        ClickShrinkEffectKt.applyClickShrink(ivFollowPlayer);
        ClickShrinkEffectKt.applyClickShrink(ivSendMessage);
        ClickShrinkEffectKt.applyClickShrink(ivShowEmoji);
        ClickShrinkEffectKt.applyClickShrink(tvCancelSelectedGame);
    }
    private void setDataOnViews(){
        Glide.with(this).load(myProfileImage).circleCrop().placeholder(getDrawable(R.drawable.profile_holder)).into(ivMyProfile);
        Glide.with(this).load(freindProfileImage).circleCrop().placeholder(getDrawable(R.drawable.profile_holder)).into(ivFreindProfile);
        tvMyName.setText(myName);
        tvFreindName.setText(freindName);
        tvMyScore.setText(myScore);
        tvFreindScore.setText(freindScore);
    }
    public void getGamesData() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put(GAMELIST, API);
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
                    String TAG_GAME = "gamesInChatActivity";
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        String playerUserName = "";
                        String playerAvatarUrl = "";
                        String roomId = "";
                        String playerId = "1";

                        if(UsableFunctions.checkLoggedInOrNot()){
                            playerUserName = AppController.getInstance().getName();
                            playerAvatarUrl = AppController.getInstance().getProfile();
                        }

                        gameModelList.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            roomId = UsableFunctions.getGameRoomId();
                            String name[] = playerUserName.split(" ");
                            if(name.length > 1){
                                playerUserName = name[0];
                            }
                            String url = dataobj.getString("url") + "?playerusername=" + playerUserName
                                    + "&playeravatarurl=" + playerAvatarUrl;

                            Log.d(TAG_CHAT_ACTIVITY, "data: " + url);

                            GameModel game = new GameModel(
                                    dataobj.getString("gameid"),
                                    dataobj.getString("name"),
                                    dataobj.getString("playing"),
                                    dataobj.getString("image"),
                                    dataobj.getString("type"),
                                    url,
                                    dataobj.getString("rotation"),
                                    roomId
                            );
                            gameModelList.add(game);
                        }
                        setGameListOnRecyclerView(gameModelList);
                    } else {
                        rvGames.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }
    private void setGameListOnRecyclerView(List<GameModel> gameModelList){
        chatGameAdapter adapter = new chatGameAdapter(this, gameModelList, new chatGameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String name, String image, String url, String rotation) {
//                startGame(id, names, image, url, rotation);
                rvGames.setVisibility(View.GONE);
                cLSelectedGame.setVisibility(View.VISIBLE);
                tvSelctedGameName.setText(name);
                switch (name){
                    case "Sheep Fight": cLSelectedGameBackground.setBackground(getDrawable(R.drawable.sheep_fight_dark)); break;
                    case "Bubble Shooter":    cLSelectedGameBackground.setBackground(getDrawable(R.drawable.bubble_shooter_dark)); break;
                    default:  cLSelectedGameBackground.setBackground(getDrawable(R.drawable.test_game)); break;
                }

                final int[] progress = {100};
                pbSelectedGameLoading.setProgressCompat(progress[0], false);
                CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        progress[0] -= 3;
                        pbSelectedGameLoading.setProgressCompat(progress[0], true);
                        Log.d(TAG_CHAT_ACTIVITY, "Progress: " + progress[0]);
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG_CHAT_ACTIVITY, "Progress: Finished");
                    }
                }.start();
            }
        });
        rvGames.setItemAnimator(new DefaultItemAnimator());
        rvGames.setHasFixedSize(true);
        rvGames.setLayoutManager(new LinearLayoutManager(chatActivity.this, LinearLayout.HORIZONTAL, false));
        rvGames.setAdapter(adapter);
        rvGames.setVisibility(View.VISIBLE);
        rvGames.setAdapter(new ScaleInAnimationAdapter(new AlphaInAnimationAdapter(adapter)));
        adapter.notifyDataSetChanged();
    }
    private void getMessageData(String chatRoomId){
        ArrayList<Message> messages = new ArrayList<>();
        CollectionReference colRef = db.collection("MESSAGES").document(chatRoomId).collection("Message");

        colRef.orderBy("messageId", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG_CHAT_ACTIVITY, "Listen failed.", e);
                            return;
                        }
                        ArrayList<Message> firebaseMessages = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            firebaseMessages.add(doc.toObject(Message.class));
                        }
                        messagesAdapter.addMessageList(firebaseMessages);
                    }
                });
    }
    private void setMessagesIntoRecyclerView(){
        ArrayList<Message> messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messages, myId);
        rvMessages.setAdapter(messagesAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(chatActivity.this, LinearLayout.VERTICAL, false));

        rvMessages.setItemAnimator(new DefaultItemAnimator());
        rvMessages.setHasFixedSize(true);
        rvMessages.setAdapter(new ScaleInAnimationAdapter(new AlphaInAnimationAdapter(messagesAdapter)));
        messagesAdapter.notifyDataSetChanged();
    }

    //Handle Follow
    private void handleFollow(){
        Follow = Follow == true ? false : true;
        if(Follow){
            ivFollowPlayer.setBackground(this.getDrawable(R.drawable.followed));
        }
        else {
            ivFollowPlayer.setBackground(this.getDrawable(R.drawable.follow_player));
        }
    }
    // Start Game
    private void startGame(String id, String names, String image, String url, String rotation){
        int fee = 20;
        if (AppController.getInstance().getId().equalsIgnoreCase("0")) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            this.finish();
            this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            int mypoint = Integer.parseInt(AppController.getInstance().getCoins());
//            int fee = Integer.parseInt(total + "");
            if (mypoint >= fee) {
                JSONObject js = new JSONObject();
                try {
                    js.put("id", id);
                    js.put("entry_fee", fee);
                    js.put("url", url + "");
                    js.put(IMAGE, image);
                    js.put(ROTATION, rotation);
                    js.put(NAME, names);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(this, PlayerSearching.class);
                i.putExtra(DATA, js.toString());
                startActivity(i);
                this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            } else {
                Toast.makeText(this, "Not Enough Coins", Toast.LENGTH_LONG).show();            }
        }
    }
    // Handle Send Message
    private void handleSendMessage(String message, String msgRoomId){
        if(!message.isEmpty() && message != ""){
            String msgId = UsableFunctions.getMessageId();
            Message messageObj = new Message(message, msgId, reciverId, myId);
            messagesAdapter.addMessage(messageObj);
            etMessage.setText("");
            rvMessages.scrollToPosition(messagesAdapter.getItemCount() - 1);

            sendMessage(msgRoomId, messageObj);
        }
    }
    private void sendMessage(String msgRoomId, Message messageObj){
        db.collection("MESSAGES")
                .document(msgRoomId)
                .collection("Message")
                .document()
                .set(messageObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG_CHAT_ACTIVITY, "message sends");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG_CHAT_ACTIVITY,"err: " + e.getMessage());
            }
        });
    }
    //HandleShowEmoji
    private void handleShowEmoji(){
        Emoji = Emoji == true ? false : true;
        if(Emoji == true){
            ivShowEmoji.setBackground(this.getDrawable(R.drawable.emoji_opened));
            openEmojiView();
        }
        else {
            ivShowEmoji.setBackground(this.getDrawable(R.drawable.emoji_open));
            closeEmojiView();
        }
    }
    private void openEmojiView(){
        popup.toggle();
    }
    private void closeEmojiView(){
        if(popup.isShowing()){
            popup.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}