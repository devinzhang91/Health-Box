package com.example.devinzhang.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class MotionServer extends Service {
    public static final String ACTION = "com.example.devinzhang.myapplication.MotionServer";
    public static String GPSFLAG = "GPS";
    public static String MOTIONFLAG = "Motion";

    private LocationManager lm;
    private static final String TAG="GpsActivity";

    private double longitude = 0;
    private double latitude = 0;
    private double altitude = 0;

    private ControlBinder binder = new ControlBinder();
    boolean mAllowRebind; // indicates whether onRebind should be used

    /****************** 定时器 ******************/
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            //Log.i(TAG, "running");
            Intent intent = new Intent();  //Itent就是我们要发送的内容
            intent.putExtra("Longitude", longitude);
            intent.putExtra("Latitude", latitude);
            intent.putExtra("Altitude", altitude);
            intent.setAction(GPSFLAG);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
            sendBroadcast(intent);   //发送广播
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();

    }

    //位置监听
    private LocationListener locationListener=new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            Log.i(TAG, location.getLongitude()+","+location.getLatitude());
            Toast.makeText(getApplication(), location.getLongitude()+","+location.getLatitude(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "海拔："+location.getAltitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            //Location location=lm.getLastKnownLocation(provider);
            //updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            //updateView(null);
        }


    };

    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //Log.i(TAG, "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus=lm.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
//                    System.out.println("搜索到："+count+"颗卫星");
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    //timer.schedule(task, 1000, 1000); // 1s后执行task,经过1s再次执行
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    break;
            }
        };
    };

    private void updateView(Location location){
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        altitude = location.getAltitude();
    }

    private Criteria getCriteria(){
        Criteria criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(true);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    //经纬度计算距离
    private static final double EARTH_RADIUS = 6378.137;//地球半径
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        lm.removeUpdates(locationListener);
        lm.removeGpsStatusListener(listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        Location location= lm.getLastKnownLocation(bestProvider);
        lm.addGpsStatusListener(listener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 0, locationListener);
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.i(TAG, "onUnbind");
        lm.removeUpdates(locationListener);
        lm.removeGpsStatusListener(listener);
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.i(TAG, "onRebind");
    }


    public class ControlBinder extends Binder {

        public void start() {
            timer.schedule(task, 1000, 1000); // 1s后执行task,经过1s再次执行
        }
        public void stop() {
            timer.cancel();
        }
        public void reset() {
        }
    }
}
