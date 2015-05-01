package com.example.devinzhang.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PullToFlashFragment extends Fragment {
    public BluetoothReceiver receiver;
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice device;
    public final String lockName = "HC-05";
    public String message = "000001";
    public ArrayAdapter<String> adapter;
    public List<String> devices = new ArrayList<String>();
    public List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    private RefreshableView refreshableView;
    private ListView listView;
    private ListListener listListener;
    private String chartString = "";
    private HttpThreadPost myHttpThreadPost;
    private Handler handler;
    public PullToFlashFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);
        System.out.println("PullToFlashFragment--->onCreateView");

        listView = (ListView) rootView.findViewById(R.id.list_view);
        refreshableView = (RefreshableView) rootView.findViewById(R.id.refreshable_view);

        devices.add("devices list");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.enable()) {
            Toast.makeText(getActivity(), "open buletooth success", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "open buletooth fail", Toast.LENGTH_LONG).show();
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        receiver = new BluetoothReceiver();
        getActivity().registerReceiver(receiver, filter);
        //bluetoothAdapter.startDiscovery();
        showDevices();
        //listView.setBackgroundResource(R.mipmap.background0);

        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    if(MainActivity.isConnect) {
                        //MainActivity.client.chatOUT("check file !");
                        try {
                            String strData = "";
                            int[] tempData = new int[DrawMap.DataLenght];
                            tempData = DrawMap.dataInt; //获取当前值
                            int tempHeader = DrawMap.dataHead;
                            for(int i=0; i<DrawMap.DataLenght; i++) {
                                strData = strData + String.valueOf(tempData[i]) + ',';
                            } strData = strData + String.valueOf(tempHeader);
                            List<NameValuePair> params = new ArrayList<>();
                            params.add(new BasicNameValuePair("time", String.valueOf(System.currentTimeMillis())));
                            params.add(new BasicNameValuePair("content", strData));
                            myHttpThreadPost = new HttpThreadPost(MainActivity.URL + MainActivity.UPLOAD, params, HttpThreadPost.UPLOAD, handler);
                            myHttpThreadPost.start();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "获取数据出错", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        bluetoothAdapter.startDiscovery();
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);

        /************************* device 列表 **************************/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                if(MainActivity.isConnect) {

                } else {
                    if (arg2 != 0) {
                        device = deviceList.get(arg2 - 1);
                        try {
                            bluetoothAdapter.cancelDiscovery();
                            listListener.onConnentPullToFlashFragment(bluetoothAdapter, device, msgHandler);
                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                }
            }
        });


        /************************** msg接收 *************************/
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = null;
                switch (msg.what) {
                    case HttpThreadPost.UPLOAD:
                        System.out.println("UPLOAD Done");
                        result = (String) msg.obj;
                        Toast.makeText(getActivity(), "上传完成", Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            }
        };

        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        System.out.println("PullToFlashFragment--->onAttach:"+activity.getLocalClassName());
        listListener = (ListListener) activity;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //callback
        System.out.println("PullToFlashFragment--->onActivityCreated:");
    }

    @Override
    public void onDestroy() {
        System.out.println("Activity--->onDestroy");
        bluetoothAdapter.cancelDiscovery();
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    public interface ListListener {
        public void onConnentPullToFlashFragment(BluetoothAdapter btAdapter, BluetoothDevice btDevice, Handler btHandler);
    }

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAG", "onReceive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (isLock(device)) {
                    //Toast.makeText(MainActivity.this, "isLock", Toast.LENGTH_LONG).show();
                    devices.add(device.getName() + " : " + device.getAddress());
                    Log.e("TAG", "isLock");
                } else {
                    devices.add(device.getName());
                    Log.e("TAG", "device.getName()");
                }
                deviceList.add(device);
            }
            showDevices();
        }
    }

    private boolean isLock(BluetoothDevice device) {
        boolean isLockName = (device.getName()).equals(lockName);
        boolean isSingleDevice = devices.indexOf(device.getName()) == -1;
        return isLockName && isSingleDevice;
    }

    public void showDevices() {
        Log.e("TAG", "showDevices");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.device_item_layout, devices);
        listView.setAdapter(arrayAdapter);
    }

    public Handler msgHandler = new Handler() {
        public short data;

        @Override
        public void handleMessage(Message msg) {
            Message msgDrawMap = null;
            switch (msg.what) {
                case MainActivity.DATA:
                    chartString += (String)msg.obj;
                    //Log.e("PullToFlashFragment", "input: " + chartString);
                    if(MainActivity.isConnect){
                        try {
                            data = (short) (Integer.valueOf((String) msg.obj, 16) & 0xffff);
                            msgDrawMap = msgHandler.obtainMessage();
                            msgDrawMap.what =  MainActivity.DATA;
                            msgDrawMap.obj = data;
                            DrawMap.dataHandler.sendMessage(msgDrawMap);
                        } catch (Exception e) {
                            Log.e("DataPaseError:", e.toString());
                        }
                    }

                    break;
                case MainActivity.STATE:
                    if(msg.obj.equals(MyBluetooth.CONNECT_SUCCESS)) {
                        //开辟内存空间
                        ArrayList<HashMap<String, Object>> watchViewItem = new ArrayList<>();
                        watchViewItem.add(0, null);
                        //watchViewItem.add(1, null);
                        //watchViewItem.add(2, null);
                        PullToFlashAdapter pullToFlashAdapter = new PullToFlashAdapter(getActivity(), watchViewItem);
                        listView.setAdapter(pullToFlashAdapter);
                    }
                    break;
                case MainActivity.MESSAGE:
                    break;
            }
        }
    };
}
