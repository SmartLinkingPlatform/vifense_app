package com.obd2.dgt.ui.ListAdapter.DrivingList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;

import java.util.ArrayList;

public class DrivingAdapter extends RecyclerView.Adapter<DrivingAdapter.ViewHolder>{
    private ArrayList<DrivingItem> drivingList;
    private LayoutInflater mInflater;
    Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public FrameLayout record_date_layout;
        public TextView record_date_text;
        public TextView record_sub_mileage_text;
        public TextView record_sub_driving_time;
        public TextView record_sub_region_start;
        public TextView record_sub_region_end;
        public ViewHolder(View v) {
            super(v);
            record_date_layout = v.findViewById(R.id.record_date_layout);
            record_date_text = v.findViewById(R.id.record_date_text);
            record_sub_mileage_text = v.findViewById(R.id.record_sub_mileage_text);
            record_sub_driving_time = v.findViewById(R.id.record_sub_driving_time);
            record_sub_region_start = v.findViewById(R.id.record_sub_region_start);
            record_sub_region_end = v.findViewById(R.id.record_sub_region_end);
        }
    }
    public DrivingAdapter(Context context, ArrayList<DrivingItem> drivingList) {
        this.mInflater = LayoutInflater.from(context);
        this.drivingList = drivingList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public DrivingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_record, parent, false);
        return new DrivingAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull DrivingAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String date = drivingList.get(position).date;
        if (date.isEmpty()) {
            holder.record_date_layout.setVisibility(View.GONE);
        } else {
            holder.record_date_layout.setVisibility(View.VISIBLE);
            String show_date = date.substring(0, 4) + mContext.getString(R.string.unit_year);
            show_date += date.substring(4, 6) + mContext.getString(R.string.unit_month);
            show_date += date.substring(6, 8) + mContext.getString(R.string.unit_day);
            holder.record_date_text.setText(show_date);
        }

        String[] detail = drivingList.get(position).detail;
        String distance = detail[5] + mContext.getString(R.string.unit_4);
        String start_time = detail[0];
        String end_time = detail[1];
        String driving_time = detail[2];
        String show_time = start_time + "~" + end_time + "(" + driving_time + ")";
        String start_place = detail[3];
        if (start_place.isEmpty() || start_place.equals("null")) {
            start_place = mContext.getString(R.string.no_start_place);
        }
        String end_place = detail[4];
        if (end_place.isEmpty() || end_place.equals("null")) {
            end_place = mContext.getString(R.string.no_end_place);
        }

        holder.record_sub_mileage_text.setText(distance);
        holder.record_sub_driving_time.setText(show_time);
        holder.record_sub_region_start.setText(start_place);
        holder.record_sub_region_end.setText(end_place);
    }

    @Override
    public int getItemCount() {
        return drivingList.size();
    }
}
