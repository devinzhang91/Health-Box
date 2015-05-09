package com.example.devinzhang.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MotionFragment extends Fragment {
    private MotionListener motionListener;
    Switch sw_motion;
    TextView tv_state;
    TextView tv_grid;
    TextView tv_walk;
    TextView tv_run;

    Intent serviceIntent;
    private MotionServer.ControlBinder binder;

    public MotionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_motion, container, false);
        System.out.println("MotionFragment--->onCreateView");
        sw_motion = (Switch) rootView.findViewById(R.id.motion_switch);
        tv_state = (TextView) rootView.findViewById(R.id.textView_state);
        tv_grid = (TextView) rootView.findViewById(R.id.textView_grid);
        tv_walk = (TextView) rootView.findViewById(R.id.textView_walk);
        tv_run = (TextView) rootView.findViewById(R.id.textView_run);

        sw_motion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    if(!isOPen(getActivity())) {
                        turnGPSOn();
                    } else {
                        //开启和绑定服务
                        serviceIntent = new Intent(getActivity(), MotionServer.class);
                        getActivity().startService(serviceIntent);
                        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                        //注册广播滤波器
                        IntentFilter actionFilter = new IntentFilter(MotionServer.ACTION);
                        getActivity().registerReceiver(myBroadcastReceive, actionFilter);
                    }
                } else {
                    //停止和注销服务
                    getActivity().stopService(serviceIntent);
                    getActivity().unbindService(serviceConnection);
                    //注销广播
                    getActivity().unregisterReceiver(myBroadcastReceive);
                }
            }
        });

        return rootView;
    }


    //连接后台服务模块
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MotionServer.ControlBinder) service;
            //binder.start();
            Log.v("MainActivity", "onServiceConnected");
        }
        public void onServiceDisconnected(ComponentName name) {
            Log.v("MainActivity", "onServiceDisconnected");
        }
    };

    //APP内广播接收者
    private BroadcastReceiver myBroadcastReceive = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            Log.i("MainActivity", "Receive event");
        }

    };

    public void turnGPSOn()
    {
        Toast.makeText(getActivity(), "请开启位置服务", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,0);
    }

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("MotionFragment--->onDestroy");
//        //停止和注销服务
//        getActivity().stopService(serviceIntent);
//        getActivity().unbindService(serviceConnection);
//        //注销广播
//        getActivity().unregisterReceiver(myBroadcastReceive);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        System.out.println("MotionFragment--->onAttach:"+activity.getLocalClassName());
        motionListener = (MotionListener) activity;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //callback
        System.out.println("MotionFragment--->onActivityCreated:");
    }

    public interface MotionListener {
        public void onConnentMotionFragment();
    }
}
