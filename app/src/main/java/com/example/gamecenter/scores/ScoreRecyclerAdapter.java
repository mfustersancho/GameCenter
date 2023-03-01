package com.example.gamecenter.scores;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamecenter.R;
import com.example.gamecenter.db.model.DBScore;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ScoreRecyclerAdapter extends RecyclerView.Adapter<ScoreRecyclerAdapter.ViewHolder> {

    private ArrayList<DBScore> mLocalDataset;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView score;
        private final TextView time;
        private final TextView title;
        private final ImageView icon;

        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.score_row_date);
            score = view.findViewById(R.id.score_row_score);
            time = view.findViewById(R.id.score_row_time);
            title = view.findViewById(R.id.score_row_title);
            icon = view.findViewById(R.id.score_row_icon);
        }

        public TextView getDate() {
            return date;
        }

        public TextView getScore() {
            return score;
        }

        public TextView getTime() {
            return time;
        }

        public TextView getTitle() {
            return title;
        }

        public ImageView getIcon() {
            return icon;
        }
    }

    public ScoreRecyclerAdapter(Context context) {
        mContext = context;
        mLocalDataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getDate().setText(mLocalDataset.get(position).date);
        holder.getScore().setText(String.valueOf(mLocalDataset.get(position).score));
        holder.getTime().setText(mLocalDataset.get(position).time);
        holder.getTitle().setText(mLocalDataset.get(position).title);

        Field[] fields = R.array.class.getFields();
        for(Field field: fields) {
            if(field.getName().startsWith("game_")) {
                try {
                    int stringArrayId = field.getInt(null);
                    String[] stringArray = mContext.getResources().getStringArray(stringArrayId);
                    if(mLocalDataset.get(position).title.equals(stringArray[0])) {
                        String imageResource = stringArray[2];
                        if(imageResource != null) {
                            Drawable icon = mContext.getDrawable( mContext.getResources()
                                    .getIdentifier(imageResource, "drawable", "com.example.gamecenter"));
                            holder.getIcon().setImageDrawable(icon);
                        }
                        break;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mLocalDataset.size();
    }

    public void updateDataset(ArrayList<DBScore> newDataset) {
        mLocalDataset.clear();
        mLocalDataset.addAll(newDataset);
        this.notifyDataSetChanged();
    }
}
