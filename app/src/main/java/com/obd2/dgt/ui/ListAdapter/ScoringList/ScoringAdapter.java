package com.obd2.dgt.ui.ListAdapter.ScoringList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;

import java.util.ArrayList;

public class ScoringAdapter extends RecyclerView.Adapter<ScoringAdapter.ViewHolder>{
    private ArrayList<ScoringItem> itemList;
    private LayoutInflater mInflater;
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView scoring_index_text;
        public TextView scoring_content_text;
        public ViewHolder(View v) {
            super(v);
            scoring_index_text = v.findViewById(R.id.scoring_index_text);
            scoring_content_text = v.findViewById(R.id.scoring_content_text);
        }
    }
    public ScoringAdapter(Context context, ArrayList<ScoringItem> mainList) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = mainList;
        this.mContext = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData (ArrayList<ScoringItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScoringAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_scoring, parent, false);
        return new ScoringAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ScoringAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.scoring_index_text.setText(itemList.get(position).sIndex);
        holder.scoring_content_text.setText(itemList.get(position).content);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
