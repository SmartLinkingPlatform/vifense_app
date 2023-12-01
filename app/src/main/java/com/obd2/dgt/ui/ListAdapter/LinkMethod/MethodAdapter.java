package com.obd2.dgt.ui.ListAdapter.LinkMethod;

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

public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.ViewHolder>{
    private ArrayList<MethodItem> itemList;
    private LayoutInflater mInflater;
    Context mContext;
    MethodAdapter.ItemClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView item_text;
        public ImageView item_btn;
        public ViewHolder(View v) {
            super(v);
            item_text = v.findViewById(R.id.item_link_method_text);
            item_btn = v.findViewById(R.id.item_link_method_check_btn);
        }
    }
    public MethodAdapter(Context context, ArrayList<MethodItem> itemList, MethodAdapter.ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.mContext = context;
        mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MethodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_link_method, parent, false);
        return new MethodAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MethodAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(itemList.get(position).selected) {
            holder.item_btn.setImageResource(R.drawable.check_on);
        } else {
            holder.item_btn.setImageResource(R.drawable.check_off);
        }
        holder.item_text.setText(itemList.get(position).text);
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
