package com.obd2.dgt.ui.ListAdapter.MessageList;

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
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
import com.obd2.dgt.ui.ListAdapter.LinkDevice.PairedItem;


import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<MessageItem> itemList;
    private LayoutInflater mInflater;
    Context mContext;
    MessageAdapter.ItemClickListener mClickListener;
    String selected_id = "";

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView msg_close_btn;
        public TextView msg_send_time;
        public TextView msg_send_user;
        public TextView msg_send_title;
        public TextView msg_send_content;
        public ViewHolder(View v) {
            super(v);
            msg_close_btn = v.findViewById(R.id.msg_close_btn);
            msg_send_time = v.findViewById(R.id.msg_send_time);
            msg_send_user = v.findViewById(R.id.msg_send_user);
            msg_send_title = v.findViewById(R.id.msg_send_title);
            msg_send_content = v.findViewById(R.id.msg_send_content);
        }
    }
    public MessageAdapter(Context context, ArrayList<MessageItem> mainList, MessageAdapter.ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = mainList;
        this.mContext = context;
        mClickListener = itemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData (ArrayList<MessageItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_add_gauge, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        selected_id = "";
        holder.msg_send_time.setText(itemList.get(position).msg_time);
        holder.msg_send_user.setText(itemList.get(position).msg_user);
        holder.msg_send_title.setText(itemList.get(position).msg_title);
        holder.msg_send_content.setText(itemList.get(position).msg_content);
        holder.msg_close_btn.setOnClickListener(view -> onMessageDeleteClick());
        if(itemList.get(position).selected) {
            holder.msg_send_content.setVisibility(View.VISIBLE);
            selected_id = itemList.get(position).id;
        } else {
            holder.msg_send_content.setVisibility(View.GONE);
        }
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
    public void onMessageDeleteClick() {
        if (!selected_id.isEmpty()) {
            MessageInfoTable.deleteMessageInfoTable(selected_id);
        }
    }
}
