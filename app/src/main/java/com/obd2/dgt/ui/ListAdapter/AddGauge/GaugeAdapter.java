package com.obd2.dgt.ui.ListAdapter.AddGauge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;

import java.util.ArrayList;

public class GaugeAdapter extends RecyclerView.Adapter<GaugeAdapter.ViewHolder>{
    private ArrayList<GaugeItem> mainList;
    private LayoutInflater mInflater;
    Context mContext;
    GaugeAdapter.ItemClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView add_gauge_check;
        public TextView add_gauge_text;
        public ViewHolder(View v) {
            super(v);
            add_gauge_check = v.findViewById(R.id.add_gauge_check);
            add_gauge_text = v.findViewById(R.id.add_gauge_text);
        }
    }
    public GaugeAdapter(Context context, ArrayList<GaugeItem> mainList, GaugeAdapter.ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mainList = mainList;
        this.mContext = context;
        mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public GaugeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_add_gauge, parent, false);
        return new GaugeAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull GaugeAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(mainList.get(position).selected) {
            holder.add_gauge_check.setImageResource(R.drawable.check_selected);
        } else {
            holder.add_gauge_check.setImageResource(R.drawable.check_normal);
        }
        holder.add_gauge_text.setText(mainList.get(position).text);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }
}
