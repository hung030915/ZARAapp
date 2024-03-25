package com.example.du_an1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.du_an1.R;

import java.util.List;

public class Adapter_diachi extends BaseAdapter {
    List<String> list;
    Context context;

    public Adapter_diachi(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_list_hang,parent,false);
        if (list!=null){
            TextView ten = view.findViewById(R.id.tv_ten_hang);
            ten.setText(list.get(position));
        }

        return view;
    }
}
