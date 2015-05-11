package com.example.devinzhang.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements PullToFlashFragment.ListListener, TerminalFragment.TerminalListener, MotionFragment.MotionListener {

    public static final int STATE = 0;
    public static final int DATA = 1;
    public static final int MESSAGE = 2;

    //public static final String URL = "http://192.168.1.114/";
    public static final String URL = "http://211.83.105.94/blog/";
    public static final String UPLOAD = "test.php";
    public static final String HISTORY = "show.php";
    public static MyBluetooth client = null;

    public static boolean isConnect = false;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private RefreshableView refreshableView;
    private ListView listView;
    private ArrayList<Fragment> listData;
    private ViewPager viewPager;
    private FragmentPagerAdapter fpAdapter;
    //底部图标
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;

    private PullToFlashFragment pull2FlashFragment;
    //private PlaceholderFragment fragmentThird;
    private HistoryFragment historyFragment;
    private TerminalFragment terminalFragment;
    private MotionFragment motionFragment;
    private BluetoothAdapter bluetoothAdapter;
    private Handler pullToFlashHandler;
    private Handler terminalHandler;
    public static String strChat = "";
    public static MyStringQueue stringQueue = new MyStringQueue(30);   // 队列有30个元素

    // TerminalFragment widget
    private EditText et_TerminalSend;
    private Button btn_TerminalSend;
    private TextView chartText;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Activity--->onCreate");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

        setViewPager();

        CustomFAB fab = (CustomFAB) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "FAB", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setViewPager() {
        //初始化数据
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        listData = new ArrayList<>();
        pull2FlashFragment = new PullToFlashFragment();
        historyFragment = new HistoryFragment();
        motionFragment = new MotionFragment();
        //fragmentThird = new PlaceholderFragment();
        terminalFragment = new TerminalFragment();
        //三个布局加入列表
        listData.add(pull2FlashFragment);
        listData.add(historyFragment);
        listData.add(motionFragment);
        listData.add(terminalFragment);
        //ViewPager相当于一组件容器 实现页面切换
        fpAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return listData.size();
            }
            @Override
            public Fragment getItem(int arg0) {
                return listData.get(arg0);
            }
        };
        //设置适配器
        viewPager.setAdapter(fpAdapter);
        viewPager.setOffscreenPageLimit(4);

        //初始化图标
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        layout1 = (LinearLayout) findViewById(R.id.bottomLayout1);
        layout2 = (LinearLayout) findViewById(R.id.bottomLayout2);
        layout3 = (LinearLayout) findViewById(R.id.bottomLayout3);
        //滑屏变换图标
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        //图片切换
                        image1.setImageDrawable(getResources().getDrawable(R.mipmap.lcds));
                        image2.setImageDrawable(getResources().getDrawable(R.mipmap.msg));
                        image3.setImageDrawable(getResources().getDrawable(R.mipmap.mobile));
                        //背景加深
                        layout1.setBackgroundResource(R.color.tab1);
                        layout2.setBackgroundResource(R.color.tab0);
                        layout3.setBackgroundResource(R.color.tab0);
                        break;
                    case 1:
                        //图片切换
                        image1.setImageDrawable(getResources().getDrawable(R.mipmap.lcd));
                        image2.setImageDrawable(getResources().getDrawable(R.mipmap.msgs));
                        image3.setImageDrawable(getResources().getDrawable(R.mipmap.mobile));
                        //背景加深
                        layout1.setBackgroundResource(R.color.tab0);
                        layout2.setBackgroundResource(R.color.tab1);
                        layout3.setBackgroundResource(R.color.tab0);
                        break;
                    case 2:
                        //图片切换
                        image1.setImageDrawable(getResources().getDrawable(R.mipmap.lcd));
                        image2.setImageDrawable(getResources().getDrawable(R.mipmap.msg));
                        image3.setImageDrawable(getResources().getDrawable(R.mipmap.mobiles));
                        //背景加深
                        layout1.setBackgroundResource(R.color.tab0);
                        layout2.setBackgroundResource(R.color.tab0);
                        layout3.setBackgroundResource(R.color.tab1);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Handler handler = new Handler() {
        Message msgPullToFlash, msgTerminal;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyBluetooth.CONNECT_FAILED:
                    // 重复链接
                    Toast.makeText(MainActivity.this, "CONNECT FAILED",
                            Toast.LENGTH_LONG).show();
                    break;
                case MyBluetooth.CONNECT_SUCCESS:
                    Toast.makeText(MainActivity.this, "CONNECT SUCCESS",
                            Toast.LENGTH_LONG).show();
                    msgPullToFlash = handler.obtainMessage();
                    msgPullToFlash.what = STATE;
                    msgPullToFlash.obj = MyBluetooth.CONNECT_SUCCESS;
                    pullToFlashHandler.sendMessage(msgPullToFlash);
                    // 开启对话
                    client.chatIN();
                    isConnect = true;
                    break;
                case MyBluetooth.READ_FAILED:
                    Toast.makeText(MainActivity.this, "READ FAILED",
                            Toast.LENGTH_LONG).show();
                    isConnect = false;
                    break;
                case MyBluetooth.WRITE_FAILED:
                    Toast.makeText(MainActivity.this, "WRITE FAILED",
                            Toast.LENGTH_LONG).show();
                    break;
                case MyBluetooth.DATA:
                    //消息中转
                    msgPullToFlash = handler.obtainMessage();
                    msgPullToFlash.what = DATA;
                    msgPullToFlash.obj = msg.obj;
                    pullToFlashHandler.sendMessage(msgPullToFlash);
                    msgTerminal = handler.obtainMessage();
                    msgTerminal.what = DATA;
                    msgTerminal.obj = msg.obj;
                    terminalHandler.sendMessage(msgTerminal);

                    stringQueue.insert((String)msg.obj);    //对话消息加入队列
                    strChat = stringQueue.getAll('\n');
                    break;
                case MyBluetooth.MESSAGE:
                    msgPullToFlash = handler.obtainMessage();
                    msgPullToFlash.what = MESSAGE;
                    msgPullToFlash.obj = msg.obj;
                    pullToFlashHandler.sendMessage(msgPullToFlash);
                    msgTerminal = handler.obtainMessage();
                    msgTerminal.what = MESSAGE;
                    msgTerminal.obj = msg.obj;
                    terminalHandler.sendMessage(msgTerminal);

                    stringQueue.insert((String)msg.obj);    //对话消息加入队列
                    strChat = stringQueue.getAll('\n');
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Dialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("exit")
                    .setMessage("Sure to exit ?")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (client != null) {
                                        client.disconnect();
                                        isConnect = false;
                                    }
                                    //System.exit(0);
                                    finish();
                                }
                            })
                    .setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).create();
            alertDialog.show();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Activity--->onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregisterReceiver(myBroadcastReceive);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Activity--->onDestroy");
    }


    /****************** 蓝牙广播接受 ******************/
    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 得到广播中得到的数据，并显示出来
            String message = intent.getStringExtra("data");
        }

    }

    //回调接口
    @Override
    public void onConnentPullToFlashFragment(BluetoothAdapter btAdapter, BluetoothDevice btDevice, Handler btHandler) {
        client = new MyBluetooth(bluetoothAdapter, btDevice, handler);
        client.connect("connected");
        pullToFlashHandler = btHandler;

    }

    @Override
    public void onConnentTerminalFragment(Handler btHandler) {
        terminalHandler = btHandler;
        et_TerminalSend = (EditText) findViewById(R.id.editText_chart_string);
        btn_TerminalSend = (Button) findViewById(R.id.button_send);
        chartText = (TextView) findViewById(R.id.textView_chart_string);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        btn_TerminalSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String strSend = et_TerminalSend.getText().toString();
                strChat += strSend + "\n";
                chartText.setText(strChat);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                Log.d("TEST", "TerminalSend onClick: "+ strSend);
                client.chatOUT(strSend);
                et_TerminalSend.setText("");
            }
        });
    }

    @Override
    public void onConnentMotionFragment() {

    }

}
