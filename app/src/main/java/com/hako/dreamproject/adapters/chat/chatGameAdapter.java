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

import com.bumptech.glide.Glide;
import com.hako.dreamproject.R;
import com.hako.dreamproject.model.GameModel;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import java.util.Collections;
import java.util.List;

public class chatGameAdapter extends RecyclerView.Adapter<chatGameAdapter.MyHolder> {

    public interface OnItemClickListener {
        void onItemClick(String id, String names, String image, String url, String rotation);
    }

    Context context;
    LayoutInflater inflater;
    private final OnItemClickListener listener;
    List<GameModel> data = Collections.emptyList();


    public chatGameAdapter(Context context, List<GameModel> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_game, parent, false);
        return new chatGameAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        GameModel current = data.get(position);
        ClickShrinkEffectKt.applyClickShrink(holder.itemView);
        holder.bind(current);
        holder.itemView.setOnClickListener( view ->{
            listener.onItemClick(current.getId(), current.getName(), current.getImage(), current.getUrl(), current.getRotation());
        });
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        ConstraintLayout holders;

        public MyHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_chatGameCard_gameName);
            image = itemView.findViewById(R.id.iv_chatGameCard_gameImage);
            holders = itemView.findViewById(R.id.lL_chatGameCard_game);
        }

        public void bind(GameModel game){
            name.setText(game.getName());

            switch (game.getName()){
                case "Sheep Fight": image.setBackground(context.getDrawable(R.drawable.sheep_fight)); break;
                case "HEXA GONA":    image.setBackground(context.getDrawable(R.drawable.hexa_gona)); break;
                case "BULL FIGHT":    image.setBackground(context.getDrawable(R.drawable.bull_fight)); break;
                case "Bubble Shooter":    image.setBackground(context.getDrawable(R.drawable.bubble_shooter)); break;
                default:  image.setBackground(context.getDrawable(R.drawable.test_game)); break;
            }
        }
    }



}
