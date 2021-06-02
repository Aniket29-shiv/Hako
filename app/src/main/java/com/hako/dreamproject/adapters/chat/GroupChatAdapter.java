package com.hako.dreamproject.adapters.chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.R;
import com.hako.dreamproject.model.chatRoom;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import java.util.ArrayList;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.myViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(chatRoom chatRoom);
    }

    private Activity activity;
    private ArrayList<chatRoom> chatRooms;
    private final OnItemClickListener listener;

    public GroupChatAdapter(Activity activity, ArrayList<chatRoom> chatRooms, OnItemClickListener listener) {
        this.activity = activity;
        this.chatRooms = chatRooms;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        chatRoom current = chatRooms.get(position);
        holder.bind(current.getFriendName(), current.getFriendProfile());
        holder.itemView.setOnClickListener( view -> {
            listener.onItemClick(current);
        });
        ClickShrinkEffectKt.applyClickShrink(holder.itemView);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(activity).inflate(R.layout.chat_card, parent, false);
        return new myViewHolder(holder);
    }


    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGroupChatImage;
        TextView tvChatTitle;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGroupChatImage = itemView.findViewById(R.id.iv_charCard_groupChatImage);
            tvChatTitle = itemView.findViewById(R.id.tv_chatCard_chatTitle);
        }
        public void bind(String title, String friendProfile){
            Glide.with(activity).load(friendProfile).circleCrop().into(ivGroupChatImage);
            tvChatTitle.setText(title);
        }
    }
}
