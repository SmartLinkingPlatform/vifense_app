package com.obd2.dgt.ui.ListAdapter.CarList;

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
import com.obd2.dgt.ui.InfoActivity.MyInfoActivity;

import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private ArrayList<CarItem> itemList;
    private LayoutInflater mInflater;
    Context mContext;
    CarAdapter.ItemClickListener mClickListener;
    String selected_id = "";

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView car_image;
        public ImageView car_info_mod_btn;
        public TextView car_manufacturer;
        public TextView car_model;
        public TextView car_number;
        public TextView car_cyear;
        public TextView car_fuel;
        public TextView car_gas;
        public ViewHolder(View v) {
            super(v);
            car_image = v.findViewById(R.id.car_image);
            car_info_mod_btn = v.findViewById(R.id.car_info_mod_btn);
            car_manufacturer = v.findViewById(R.id.car_manufacturer);
            car_model = v.findViewById(R.id.car_model);
            car_number = v.findViewById(R.id.car_number);
            car_cyear = v.findViewById(R.id.car_cyear);
            car_fuel = v.findViewById(R.id.car_fuel);
            car_gas = v.findViewById(R.id.car_gas);
        }
    }
    public CarAdapter(Context context, ArrayList<CarItem> mainList, CarAdapter.ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = mainList;
        this.mContext = context;
        mClickListener = itemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData (ArrayList<CarItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_car, parent, false);
        return new CarAdapter.ViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CarAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.car_image.setImageResource(R.drawable.car_default);
        holder.car_manufacturer.setText(itemList.get(position).manufacturer);
        holder.car_model.setText(itemList.get(position).model);
        holder.car_number.setText(itemList.get(position).number);
        holder.car_cyear.setText(itemList.get(position).cYear);
        holder.car_fuel.setText(itemList.get(position).fuel);
        holder.car_gas.setText(itemList.get(position).gas);
        holder.car_info_mod_btn.setImageResource(R.drawable.pen_state2);
        holder.car_info_mod_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                MyInfoActivity.getInstance().onCarModifyClick(Integer.parseInt(itemList.get(position).id));
            }
        });

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
