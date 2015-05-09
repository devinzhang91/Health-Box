package com.example.devinzhang.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PullToFlashAdapter extends BaseAdapter {
    ArrayList<HashMap<String, Object>> ls;
    Context mContext;
    LinearLayout linearLayout = null;
    LayoutInflater inflater;
    TextView tex;
    final int VIEW_TYPE = 3;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    final int TYPE_3 = 2;
    private String strChart = "";

    public PullToFlashAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        ls = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        return ls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0)
            return TYPE_1;
        else if (p == 1)
            return TYPE_2;
        else
            return TYPE_3;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            inflater = LayoutInflater.from(mContext);
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.map_view, parent, false);
                    holder1 = new ViewHolder1();
                    holder1.datamap = (DrawMap) convertView.findViewById(R.id.view);
                    convertView.setTag(holder1);
                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, (int) (MainActivity.SCREEN_HEIGHT * 0.5));
                    convertView.setLayoutParams(lp);
                    break;
                case TYPE_2:
                    convertView = inflater.inflate(R.layout.fragment_motion,
                            parent, false);
                    holder2 = new ViewHolder2();
                    holder2.sw_motion = (Switch) convertView.findViewById(R.id.motion_switch);
                    holder2.tv_state = (TextView) convertView.findViewById(R.id.textView_state);
                    holder2.tv_grid = (TextView) convertView.findViewById(R.id.textView_grid);
                    holder2.tv_walk = (TextView) convertView.findViewById(R.id.textView_walk);
                    holder2.tv_run = (TextView) convertView.findViewById(R.id.textView_run);
                    convertView.setTag(holder2);
                    break;
                default:
                    break;
            }

        } else {
            switch (type) {
                case TYPE_1:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }
        // 设置资源
        switch (type) {
            case TYPE_1:
                final ViewHolder1 finalHolder = holder1;
                break;
            case TYPE_2:
                holder2.sw_motion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {

                        } else {
                        }
                    }
                });
                break;

        }

        return convertView;
    }

    public class ViewHolder1 {
        DrawMap datamap;
    }

    public class ViewHolder2 {
        Switch sw_motion;
        TextView tv_state;
        TextView tv_grid;
        TextView tv_walk;
        TextView tv_run;
    }


}

