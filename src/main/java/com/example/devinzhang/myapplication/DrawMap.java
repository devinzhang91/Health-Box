package com.example.devinzhang.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 15-4-13.
 */
public class DrawMap extends SurfaceView implements SurfaceHolder.Callback ,Runnable{

    public static int DataLenght = 500;
    private static int oldX, oldY;
    private boolean isFirst = true;
    private static SurfaceHolder holder;
    //public static MyDataQueue dataQueue = new MyDataQueue(DataLenght);
    static int[] dataInt = new int[DataLenght]; // data
    private static int dataHead = 0;
    private static int mapH;
    private static int mapW;

    private static Canvas c = null;
    private static Paint p = new Paint(); //创建画笔
    private static Paint pQ = new Paint(); //创建赛贝尔画笔
    private static Path path = new Path();
    private Thread thread; // SurfaceView通常需要自己单独的线程来播放动画

    public DrawMap(Context context, AttributeSet set) {
        super(context, set);
        holder = this.getHolder();
        holder.addCallback(this);
        pQ.setColor(Color.RED); // 画笔颜色
        pQ.setStrokeWidth(2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //设置长宽
        mapH = (int) (MainActivity.SCREEN_HEIGHT * 0.5);
        mapW = (int) (MainActivity.SCREEN_WIDTH);
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    public static void drawData() {

        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。

        c.drawColor(Color.BLACK);//设置画布背景颜色
        p.setColor(Color.GREEN);

        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        p.setPathEffect(effects);
        for (int i = 0; i < 10; i++) {
            if (i == 8) {
                p.setStrokeWidth(3);
                c.drawLine(1, (int) (mapH / 10.0 * i), mapW - 1, (int) (mapH / 10.0 * i), p);
            } else {
                p.setStrokeWidth(1);
                c.drawLine(1, (int) (mapH / 10.0 * i), mapW - 1, (int) (mapH / 10.0 * i), p);
            }

        }
        p.setStrokeWidth(1);
        for (int i = 0; i < 10; i++) {
            c.drawLine((int) (mapW / 10.0 * i), 1,
                    (int) (mapW / 10.0 * i), mapH - 1, p);
        }

        oldX = 0;
        oldY = (int) (mapH * 3 / 4 - dataInt[dataHead] * 0.300 + 100);
        for (int j = dataHead+1; j < DataLenght; j++) {
            c.drawLine(oldX, oldY, (int) (mapW/(float)DataLenght * (j-dataHead)), (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100), pQ);
            oldX = (int) (int) (mapW/(float)DataLenght * (j-dataHead));
            oldY = (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100);
        }
        for (int j = 0; j < dataHead; j++) {
            c.drawLine(oldX, oldY, (int) (mapW/(float)DataLenght * (j+DataLenght-dataHead)), (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100), pQ);
            oldX = (int) (int) (mapW/(float)DataLenght * (j+DataLenght-dataHead));
            oldY = (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100);
        }

        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。

    }

    public static void addData(int data0) {
        dataHead = ++dataHead % DataLenght;
        dataInt[dataHead] = data0;
    }

    public static Handler dataHandler = new Handler() {
        public int data;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.DATA:
                    try {
                        int data0 = (short)msg.obj;
                        //Log.d("Data:", String.valueOf(data0));
                        addData(data0);
                    } catch (Exception e) {
                        Log.e("DataPaseError:", e.toString());
                    }
                    //Log.e("PullToFlashFragment", "input: " + chartString);
                    if (MainActivity.isConnect) {
                    }
                    break;
            }
        }
    };

    @Override
    public void run() {
        while(true) {
            drawData();
            Log.d("Thred:", "running");
            try {
                Thread.sleep(50); // 这个就相当于帧频了，数值越小画面就越流畅
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
