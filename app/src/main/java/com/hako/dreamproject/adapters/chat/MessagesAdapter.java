package com.hako.dreamproject.adapters.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hako.dreamproject.R;
import com.hako.dreamproject.model.Message;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.myViewHolder> {

    private Context context;
    private ArrayList<Message> messages;
    private String myUserid;

    public MessagesAdapter(Context context, ArrayList<Message> messages, String myUserid) {
        this.context = context;
        this.messages = messages;
        this.myUserid = myUserid;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_message_card, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout clMyMessage;
        private ImageView ivMyProfile;
        private TextView tvMyMessage;

        private ConstraintLayout clPlayerMessage;
        private ImageView ivPlayerProfile;
        private TextView tvPlayerMessage;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            clMyMessage = itemView.findViewById(R.id.cL_myMessageCard_myMessage);
            ivMyProfile = itemView.findViewById(R.id.iv_myMessage_profile);
            tvMyMessage = itemView.findViewById(R.id.tv_myMessage_message);

            clPlayerMessage = itemView.findViewById(R.id.cL_myMessageCard_playerMessage);
            ivPlayerProfile = itemView.findViewById(R.id.iv_myMessage_playerProfile);
            tvPlayerMessage = itemView.findViewById(R.id.tv_myMessage_playerMessage);
        }
        void bind(Message message){
            if(message.getSenderId().equals(myUserid)){// My Message
                clMyMessage.setVisibility(View.VISIBLE);
                clPlayerMessage.setVisibility(View.GONE);
                tvMyMessage.setText(message.getMessage().trim());
            }else {
                clPlayerMessage.setVisibility(View.VISIBLE);
                clMyMessage.setVisibility(View.GONE);
                tvPlayerMessage.setText(message.getMessage().trim());
            }
        }
    }

    public void addMessage(Message message){
        messages.add(message);
        notifyDataSetChanged();
    }

    public void addMessageList(ArrayList<Message> messagesArrayList){
        messages.removeAll(messages);
        messages.addAll(messagesArrayList);
        notifyDataSetChanged();
    }
}
