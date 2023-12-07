package com.obd2.dgt.ui.ListAdapter.TroubleLis;

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
import com.obd2.dgt.ui.InfoActivity.MessageActivity;
import com.obd2.dgt.ui.MainListActivity.DiagnosisActivity;

import java.util.ArrayList;

public class PTroubleAdapter extends RecyclerView.Adapter<PTroubleAdapter.ViewHolder> {
    private ArrayList<PTroubleItem> itemList;
    private LayoutInflater mInflater;
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_trouble_code;
        public TextView item_trouble_desc;
        public ImageView trouble_delete_btn;

        public ViewHolder(View v) {
            super(v);
            item_trouble_code = v.findViewById(R.id.item_trouble_code);
            item_trouble_desc = v.findViewById(R.id.item_trouble_desc);
            trouble_delete_btn = v.findViewById(R.id.trouble_delete_btn);
        }
    }

    public PTroubleAdapter(Context context, ArrayList<PTroubleItem> itemList) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.mContext = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<PTroubleItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PTroubleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_trouble, parent, false);
        return new PTroubleAdapter.ViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "MissingPermission"})
    @Override
    public void onBindViewHolder(@NonNull PTroubleAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.item_trouble_code.setText(itemList.get(position).code_num);
        holder.item_trouble_desc.setText(itemList.get(position).code_desc);

        holder.trouble_delete_btn.setOnClickListener(view -> {
            DiagnosisActivity.getInstance().onPastTroubleDeleteClick(itemList.get(position).cid);
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

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
