package com.example.devinzhang.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
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
        ViewHolder3 holder3 = null;
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
                    convertView = inflater.inflate(R.layout.file_progressbar_view,
                            parent, false);
                    holder2 = new ViewHolder2();
                    holder2.tv_progress = (TextView) convertView
                            .findViewById(R.id.textView_progress);
                    holder2.pb_file = (ProgressBar) convertView
                            .findViewById(R.id.progressBar_file);
                    convertView.setTag(holder2);
                    break;
                case TYPE_3:
                    convertView = inflater.inflate(R.layout.speed_control_view,
                            parent, false);
                    holder3 = new ViewHolder3();
                    holder3.tv_speed = (TextView) convertView
                            .findViewById(R.id.textView_speed);
                    holder3.sb_speed = (SeekBar) convertView
                            .findViewById(R.id.seekBar_speed);
                    convertView.setTag(holder3);
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
                case TYPE_3:
                    holder3 = (ViewHolder3) convertView.getTag();
                    break;
            }
        }
        // 设置资源
        switch (type) {
            case TYPE_1:
                final ViewHolder1 finalHolder = holder1;
                break;
            case TYPE_2:
                holder2.pb_file.setProgress(0);
                holder2.tv_progress.setText(holder2.pb_file.getProgress() + "%");
                break;
            case TYPE_3:
                holder3.sb_speed.setProgress(0);
                holder3.tv_speed.setText(holder3.sb_speed.getProgress() + "%");
                final ViewHolder3 finalHolder1 = holder3;
                holder3.sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        finalHolder1.tv_speed.setText(finalHolder1.sb_speed.getProgress() + "%");
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
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
        TextView tv_progress;
        ProgressBar pb_file;
    }

    public class ViewHolder3 {
        TextView tv_speed;
        SeekBar sb_speed;
    }
}

