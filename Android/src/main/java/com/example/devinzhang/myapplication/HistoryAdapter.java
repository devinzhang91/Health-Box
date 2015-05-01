package com.example.devinzhang.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class HistoryAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<Map<String, Object>> mData;

    public HistoryAdapter(Context context, List<Map<String, Object>> mListMap){
        this.mInflater = LayoutInflater.from(context);
        this.mData = mListMap;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder=new ViewHolder();
            convertView = mInflater.inflate(R.layout.history_data_layout, null);
            holder.dataMap = (ImageView)convertView.findViewById(R.id.dataMap);
            holder.dataTime = (TextView)convertView.findViewById(R.id.dataTime);
            //holder.dataRate = (TextView)convertView.findViewById(R.id.dataRate);
            convertView.setTag(holder);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, (int) (MainActivity.SCREEN_HEIGHT * 0.35));
            convertView.setLayoutParams(lp);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        //holder.dataMap.setBackgroundResource((Integer)mData.get(position).get("map"));
        holder.dataMap.setImageBitmap((Bitmap)mData.get(position).get("map"));
        holder.dataTime.setText((String)mData.get(position).get("time"));
        //holder.dataRate.setText((String) mData.get(position).get("rate"));

        return convertView;
    }



    public final class ViewHolder{
        public ImageView dataMap;
        public TextView dataTime;
        public TextView dataRate;
    }


}
