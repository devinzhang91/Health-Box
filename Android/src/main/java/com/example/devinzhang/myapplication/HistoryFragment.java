package com.example.devinzhang.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DevinZhang on 2015/4/27 0027.
 */
public class HistoryFragment extends Fragment {
    private RefreshableView refreshableView;
    private ListView listView;
    private Handler handler;
    private HttpThreadGet myHttpThreadGet;

    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    public HistoryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        System.out.println("HistoryFragment--->onCreateView");

        listView = (ListView) rootView.findViewById(R.id.list_view);
        refreshableView = (RefreshableView) rootView.findViewById(R.id.refreshable_view);

        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    myHttpThreadGet = new HttpThreadGet(MainActivity.URL + MainActivity.HISTORY, HttpThreadGet.HISTORY, handler);
                    myHttpThreadGet.start();
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
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                try {

                } catch (Exception e) {
                    Log.e("TAG", e.toString());
                }
            }
        });

        /************************** msg接收 *************************/
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = null;
                switch (msg.what) {
                    case HttpThreadGet.HISTORY :
                        System.out.println("HISTORY Done");
                        result = (String) msg.obj;
                        list = parseJSONString(result);
                        HistoryAdapter adapter = new HistoryAdapter(getActivity(), list);
                        listView.setAdapter(adapter);
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
        System.out.println("HistoryFragment--->onAttach:"+activity.getLocalClassName());
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("HistoryFragment--->onActivityCreated:");
    }


    @Override
    public void onDestroy() {
        System.out.println("HistoryFragment--->onDestroy");
        super.onDestroy();
    }

    private List<Map<String, Object>> parseJSONString(String JSONString) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        try {
            JSONArray jsonObjs = new JSONArray(JSONString);
            for (int i = 0; i < jsonObjs.length(); i++) {
                Map<String, Object> tempMap = new HashMap<>();
                // 直接把JSON字符串转化为一个JSONObject对象
                JSONObject jsonObj = new JSONObject(jsonObjs.getJSONObject(i).toString());
                // 第1个键值对
                tempMap.put("time", jsonObj.getString("time"));
                // 第2个键值对
                tempMap.put("map", paseData(jsonObj.getString("content")));   //解析数据，生成bitmap
                Log.d("JSON : ","正在解析:" + jsonObj.getString("time"));
                tempList.add(tempMap);
            }
            Log.d("JSON : ","解析完成");
            Toast.makeText(getActivity(), "数据已获取", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "数据解析出错", Toast.LENGTH_LONG).show();
            Log.e("JSON : ","解析出错 "+ e.toString());
        }
        return tempList;
    }

    //以下代码搬运DrawMap，修改直接生成bitmap，优化内存
    private int DataLenght = 500;
    private int oldX, oldY;
    int[] dataInt = new int[DataLenght]; // data
    private int dataHead = 0;
    private int mapH = (int) (MainActivity.SCREEN_HEIGHT * 0.3);
    private int mapW = (int) (MainActivity.SCREEN_WIDTH);

    private Canvas c = null;
    private Paint p = new Paint(); //创建画笔
    private Paint pQ = new Paint(); //创建赛贝尔画笔

    public Bitmap paseData(String strData) {
        try {
            String [] stringArr = strData.split(",");
            for(int i=0; i<stringArr.length-1; i++) {
                dataInt[i] = Integer.parseInt(stringArr[i]);
            }
            dataHead = Integer.parseInt(stringArr[stringArr.length-1]);
        } catch (Exception e){
            Log.e("paseData: ", "Error!");
        }
        return drawData();
    }

    public Bitmap drawData() {

        Bitmap tmpBitmap = Bitmap.createBitmap(mapW, mapH, Bitmap.Config.ARGB_8888);

        c = new Canvas(tmpBitmap);
        c.drawColor(Color.BLACK);//设置画布背景颜色
        p.setColor(Color.GREEN);

        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        p.setPathEffect(effects);
        for (int i = 0; i < 6; i++) {
            if (i == 4) {
                p.setStrokeWidth(3);
                c.drawLine(1, (int) (mapH / 5.0 * i), mapW - 1, (int) (mapH / 5.0 * i), p);
            } else {
                p.setStrokeWidth(1);
                c.drawLine(1, (int) (mapH / 5.0 * i), mapW - 1, (int) (mapH / 5.0 * i), p);
            }

        }
        p.setStrokeWidth(1);
        for (int i = 0; i < 11; i++) {
            c.drawLine((int) (mapW / 10.0 * i), 1,
                    (int) (mapW / 10.0 * i), mapH - 1, p);
        }

        pQ.setColor(Color.RED); // 画笔颜色
        pQ.setStrokeWidth(2);
        oldX = 0;
        oldY = (int) (mapH * 4 / 5 - dataInt[dataHead] * 0.150 + 50 );
        for (int j = dataHead+1; j < DataLenght; j++) {
            c.drawLine(oldX, oldY, (int) (mapW/(float)DataLenght * (j-dataHead)), (int) (mapH * 4 / 5 - dataInt[j] * 0.150 + 50 ), pQ);
            oldX = (int) (int) (mapW/(float)DataLenght * (j-dataHead));
            oldY = (int) (mapH * 4 / 5 - dataInt[j] * 0.150 + 50 );
        }
        for (int j = 0; j < dataHead; j++) {
            c.drawLine(oldX, oldY, (int) (mapW/(float)DataLenght * (j+DataLenght-dataHead)), (int) (mapH * 4 / 5 - dataInt[j] * 0.150 + 50 ), pQ);
            oldX = (int) (int) (mapW/(float)DataLenght * (j+DataLenght-dataHead));
            oldY = (int) (mapH * 4 / 5 - dataInt[j] * 0.150 + 50 );
        }
        return tmpBitmap;
    }

}
