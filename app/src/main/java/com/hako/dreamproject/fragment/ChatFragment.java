package com.hako.dreamproject.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hako.dreamproject.R;
import com.hako.dreamproject.adapters.chat.GroupChatAdapter;
import com.hako.dreamproject.chatActivity;
import com.hako.dreamproject.model.chatRoom;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.UsableFunctions;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class ChatFragment extends Fragment{

    // Root View
    private View root;

    // RecyclerView
    private RecyclerView rvGroupChats;

    // Firebase
    FirebaseFirestore db;

    // String
    String TAG_CHAT_FRAGMENT = "chatFragment";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chat, container, false);

        initViews();
        if(UsableFunctions.checkLoggedInOrNot()){
            try{
                getChatRoomsFromFirebase(AppController.getInstance().getUser_unique_id());
            }catch (Exception e){
                getChatRoomsFromFirebase("56341");
            }
        }

        return root;
    }

    private void initViews(){
        //firebase
        db = FirebaseFirestore.getInstance();

        // RecyclerView
        rvGroupChats = root.findViewById(R.id.rv_chat_groupChat);
    }
    private void getChatRoomsFromFirebase(String myId){
        Log.d(TAG_CHAT_FRAGMENT, "myId: " + myId);
        CollectionReference colRef = db.collection("USERS").document(myId)
                .collection("chatRooms");

        colRef.orderBy("chatRoomId", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<chatRoom> chatRooms = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                chatRooms.add(document.toObject(chatRoom.class));
                            }
                            setUpRecyclerView(chatRooms);
                        }
                    }
                });
    }
    private void setUpRecyclerView(ArrayList<chatRoom> chatRooms){

        GroupChatAdapter adapter = new GroupChatAdapter(requireActivity(), chatRooms, new GroupChatAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(chatRoom chatRoom) {
                Intent intent = new Intent(getActivity(), chatActivity.class);
                intent.putExtra("chatRoomId", chatRoom.getChatRoomId());
                intent.putExtra("myScore", chatRoom.getMyScore());
                intent.putExtra("freindScore", chatRoom.getFriendScore());
                intent.putExtra("freindProfile",chatRoom.getFriendProfile());
                intent.putExtra("freindName", chatRoom.getFriendName());
                intent.putExtra("reciverId", chatRoom.getFriendId());
                startActivity(intent);
            }
        });
        rvGroupChats.setAdapter(new AlphaInAnimationAdapter(adapter));
        rvGroupChats.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvGroupChats.setHasFixedSize(true);
        rvGroupChats.setItemAnimator(new DefaultItemAnimator());
    }

}