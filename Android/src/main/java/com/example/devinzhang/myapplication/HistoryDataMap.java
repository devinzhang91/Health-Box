package com.example.devinzhang.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by root on 15-4-13.
 */
public class HistoryDataMap extends SurfaceView implements SurfaceHolder.Callback {

    public static int DataLenght = 500;
    private static int oldX, oldY;
    private boolean isFirst = true;
    private static SurfaceHolder holder;
    static int[] dataInt = new int[DataLenght]; // data
    private static int dataHead = 0;
    private static int mapH;
    private static int mapW;

    private static Canvas c = null;
    private static Paint p = new Paint(); //创建画笔
    private static Paint pQ = new Paint(); //创建赛贝尔画笔
    private static Path path = new Path();

    public HistoryDataMap(Context context, AttributeSet set) {
        super(context, set);
        holder = this.getHolder();
        holder.addCallback(this);
        pQ.setColor(Color.RED); // 画笔颜色
        pQ.setStrokeWidth(2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //设置长宽
        mapH = (int) (MainActivity.SCREEN_HEIGHT * 0.3);
        mapW = (int) (MainActivity.SCREEN_WIDTH);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    public void drawData() {

        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。

        c.drawColor(Color.BLACK);//设置画布背景颜色
        p.setColor(Color.GREEN);

        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        p.setPathEffect(effects);
        for (int i = 0; i < 6; i++) {
            if (i == 4) {
                p.setStrokeWidth(3);
                c.drawLine(1, (int) (mapH / 5.0 * i), mapW - 1, (int) (mapH / 10.0 * i), p);
            } else {
                p.setStrokeWidth(1);
                c.drawLine(1, (int) (mapH / 5.0 * i), mapW - 1, (int) (mapH / 10.0 * i), p);
            }

        }
        p.setStrokeWidth(1);
        for (int i = 0; i < 11; i++) {
            c.drawLine((int) (mapW / 10.0 * i), 1,
                    (int) (mapW / 10.0 * i), mapH - 1, p);
        }

        oldX = 0;
        oldY = (int) (mapH * 3 / 4 - dataInt[dataHead] * 0.300 + 100);
        for (int j = dataHead+1; j < DataLenght; j++) {
            c.drawLine(oldX, oldY, (int) (mapW/(float)DataLenght * (j-dataHead)), (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100), p);
            oldX = (int) (int) (mapW/(float)DataLenght * (j-dataHead));
            oldY = (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100);
        }
        for (int j = 0; j < dataHead; j++) {
            c.drawLine(oldX, oldY, (int) (mapW/(float)DataLenght * (j+DataLenght-dataHead)), (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100), p);
            oldX = (int) (int) (mapW/(float)DataLenght * (j+DataLenght-dataHead));
            oldY = (int) (mapH * 3 / 4 - dataInt[j] * 0.300 + 100);
        }

        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。

    }

    public void paseData(String strData) {
        try {
            String [] stringArr = strData.split(",");
            for(int i=0; i<stringArr.length-1; i++) {
                dataInt[i] = Integer.parseInt(stringArr[i]);
            }
            dataHead = Integer.parseInt(stringArr[stringArr.length-1]);
        } catch (Exception e){
            Log.e("paseData: ", "Error!");
        }
        drawData();
    }

}
