package com.obd2.dgt.ui.ListAdapter.MainList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;

import java.util.ArrayList;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder>{
    private ArrayList<MainListItem> mainList;
    private LayoutInflater mInflater;
    Context mContext;
    ItemClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout menu_item_layout;
        public ImageView menu_img;
        public TextView menu_text;
        public ImageView menu_next_btn;
        public ViewHolder(View v) {
            super(v);
            menu_item_layout = v.findViewById(R.id.menu_item_layout);
            menu_img = v.findViewById(R.id.menu_img);
            menu_text = v.findViewById(R.id.menu_text);
            menu_next_btn = v.findViewById(R.id.menu_next_btn);
        }
    }
    public MainListAdapter(Context context, ArrayList<MainListItem> mainList, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mainList = mainList;
        this.mContext = context;
        mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_main_menu, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MainListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.menu_img.setImageResource(mainList.get(position).resId);
        holder.menu_text.setText(mainList.get(position).text);
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
