package com.obd2.dgt.ui.ListAdapter.LinkDevice;

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

public class PairedAdapter extends RecyclerView.Adapter<PairedAdapter.ViewHolder>{
    private ArrayList<PairedItem> itemList;
    private LayoutInflater mInflater;
    Context mContext;
    PairedAdapter.ItemClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView item_name_text;
        public TextView item_mac_text;
        public ImageView item_btn;
        public ViewHolder(View v) {
            super(v);
            item_name_text = v.findViewById(R.id.item_link_device_name);
            item_mac_text = v.findViewById(R.id.item_link_device_mac);
            item_btn = v.findViewById(R.id.item_link_device_btn);
        }
    }
    public PairedAdapter(Context context, ArrayList<PairedItem> itemList, PairedAdapter.ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.mContext = context;
        mClickListener = itemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData (ArrayList<PairedItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PairedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_link_device, parent, false);
        return new PairedAdapter.ViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "MissingPermission"})
    @Override
    public void onBindViewHolder(@NonNull PairedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(itemList.get(position).selected) {
            holder.item_btn.setImageResource(R.drawable.link_on);
        } else {
            holder.item_btn.setImageResource(R.drawable.link_off);
        }
        holder.item_name_text.setText(itemList.get(position).device.getName());
        holder.item_mac_text.setText(itemList.get(position).device.getAddress());
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
        return itemList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }
}