package com.obd2.dgt.ui.CustomSpinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.obd2.dgt.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private int selectedPosition = -1;

    public CustomSpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @SuppressLint("ResourceAsColor")
    private View customView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        //textView.setBackgroundColor(R.color.transparent);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        textView.setTextSize(22);
        textView.setText(getItem(position));

        // 선택된 항목의 글자 색상 변경
        if (position == selectedPosition) {
            textView.setTextColor(Color.WHITE); // 원하는 색상으로 변경
        } else {
            textView.setTextColor(Color.BLACK); // 선택되지 않은 항목의 색상
            textView.setAlpha((float) 0.5);
        }

        return view;
    }
}
