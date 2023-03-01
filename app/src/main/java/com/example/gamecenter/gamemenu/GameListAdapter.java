package com.example.gamecenter.gamemenu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamecenter.R;

import java.util.List;

public final class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder>{
    private List<GameInfo> mGameList;
    private long userId;
    public static class GameListViewHolder extends RecyclerView.ViewHolder {
        public ImageView gameIcon;
        public TextView gameTitle;
        public TextView gameDescription;

        public GameListViewHolder(@NonNull View itemView) {
            super(itemView);
            gameIcon = itemView.findViewById(R.id.game_icon);
            gameTitle = itemView.findViewById(R.id.game_title);
            gameDescription = itemView.findViewById(R.id.game_description);
        }
    }

    public GameListAdapter(List<GameInfo> data, long userId) {
        mGameList = data;
        this.userId = userId;
    }

    @NonNull
    @Override
    public GameListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_item, parent, false);
        GameListViewHolder gameListViewHolder = new GameListViewHolder(v);
        return gameListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameListViewHolder holder, int position) {
        GameInfo gameInfo = mGameList.get(position);
        holder.gameIcon.setImageDrawable(gameInfo.icon);
        holder.gameTitle.setText(gameInfo.title);
        holder.gameDescription.setText(gameInfo.description);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), gameInfo.activity);
            try {
                intent.putExtra((String)gameInfo.activity.getField("INTENT_USER_ID").get(null), userId);
                v.getContext().startActivity(intent);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGameList.size();
    }
}
