package com.example.du_an1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.du_an1.R;
import com.example.du_an1.model.ThongBao;

import java.util.List;

public class Adapter_thongbao extends BaseAdapter {
    List<ThongBao> list;
    Context context;

    public Adapter_thongbao(List<ThongBao> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.item_thongbao,parent,false);
        TextView massage = convertView.findViewById(R.id.tv_thongbao);

        massage.setText(list.get(position).getNoiDung());
        return convertView;
    }
}
