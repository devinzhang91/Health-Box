package com.example.devinzhang.myapplication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyBluetooth {
    public static final int CONNECT_FAILED = 0; // 连接失败
    public static final int CONNECT_SUCCESS = 1; // 连接成功
    public static final int READ_FAILED = 2; // 读取失败
    public static final int WRITE_FAILED = 3; // 写入失败
    public static final int DATA = 4; // 数据
    public static final int MESSAGE = 5; // 信息

    private BluetoothDevice device;
    private BluetoothSocket socket;
    public boolean isConnect;
    Handler handler;
    static public UUID MY_UUID = null;
    public UUID MY_UUID2 = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static public String message = "";
    String data = "";

    public MyBluetooth(){

    }

    public UUID getMY_UUID() {
        return MY_UUID;
    }

    public void setMY_UUID(UUID mY_UUID) {
        MY_UUID = mY_UUID;
    }

    public MyBluetooth(BluetoothAdapter mBluetoothAdapter, BluetoothDevice mDevice, Handler mHandler) {
        // TODO Auto-generated constructor stub
        this.device = mDevice;
        this.handler = mHandler;
    }

    public boolean disconnect() {
        try {
            socket.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void connect(final String message) {
        Thread threadConnect = new Thread(new Runnable() {

            public void run() {
                BluetoothSocket tmp = null;
                Method method;

                try {
                    method = device.getClass().getMethod("createRfcommSocket",
                            new Class[] { int.class });
                    tmp = (BluetoothSocket) method.invoke(device, 1);
                    Log.d("TAG", device.getName()+" : "+device.getAddress());
                } catch (Exception e) {
                    setState(CONNECT_FAILED);
                    Log.e("TAG", "fail creat: " + e.toString());
                }
                socket = tmp;
                try {
                    socket.connect();
                    isConnect = true;
                    Log.d("TAG", "socket connected");
                    setState(CONNECT_SUCCESS);
                } catch (Exception e) {
                    setState(CONNECT_FAILED);
                    isConnect = false;
                    Log.e("TAG", "fail connect: " + e.toString());
                }
            }
        });
        threadConnect.start();
    }

    public void chatOUT(String msg) {
        if (isConnect) {
            Log.e("TAG", "chatOUT...");
            try {
                OutputStream outStream = socket.getOutputStream();
                Log.d("DATA", "output: " + msg.length());
                //outStream.write(getHexBytes(msg));
                outStream.write(msg.getBytes());
            } catch (IOException e) {
                setState(WRITE_FAILED);
                Log.e("TAG", "fail send: " + e.toString());
            }
//			}
        }
    }

    public void chatIN() {
        Thread threadChat = new Thread(new Runnable() {
            public void run() {
                if (isConnect) {
                    Log.e("TAG", "chatIN...");
                    try {
                        BufferedInputStream inputStream =new BufferedInputStream(socket.getInputStream());
                        while (true) {
                            try {
                                char buff = (char) inputStream.read();
//                                Message msg = handler.obtainMessage();
//                                msg.what = DATA;
//                                msg.obj = buff;
//                                handler.sendMessage(msg);

                                if(buff =='P'){
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA;
                                    msg.obj = data;
                                    handler.sendMessage(msg);
                                    data = "";
                                } else if(buff =='Q'){
                                    Message msg = handler.obtainMessage();
                                    msg.what = MESSAGE;
                                    msg.obj = data;
                                    handler.sendMessage(msg);
                                    data = "";
                                } else {
                                    data += buff;
                                }
                                //Log.e("DATA", "input: " + data);

                            } catch (IOException e) {
                                setState(READ_FAILED);
                                Log.e("TAG", "fail get: " + e.toString());
                                break;
                            }
                        }
                    } catch (IOException e) {
                        setState(WRITE_FAILED);
                        Log.e("TAG", e.toString());
                    }
                }

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Log.e("TAG", "fail close: " + e.toString());
                    }
                }

            }

        });
        threadChat.start();
    }

    private void setState(int STATE) {
        // TODO Auto-generated method stub
        Message msg = handler.obtainMessage();
        msg.what = STATE;
        handler.sendMessage(msg);
    }
}
