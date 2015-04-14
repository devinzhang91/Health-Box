package com.example.root.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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


public class MainActivity extends FragmentActivity implements PullToFlashFragment.ListListener, TerminalFragment.TerminalListener {

    public static final int MSGDATA = 0;
    public static final int STATE = 1;
    public static MyBluetooth client = null;

    public static boolean isConnect = false;

    public int SCREEN_WIDTH, SCREEN_HEIGHT;
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
    private PlaceholderFragment fragmentSecond;
    private PlaceholderFragment fragmentThird;
    private TerminalFragment terminalFragment;
    private BluetoothAdapter bluetoothAdapter;
    private Handler pullToFlashHandler;
    private Handler terminalHandler;
    private String strChat = "";

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
    }

    private void setViewPager() {
        //初始化数据
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        listData = new ArrayList<>();
        pull2FlashFragment = new PullToFlashFragment();
        fragmentSecond = new PlaceholderFragment();
        fragmentThird = new PlaceholderFragment();
        terminalFragment = new TerminalFragment();
        //三个布局加入列表
        listData.add(pull2FlashFragment);
        listData.add(terminalFragment);
        listData.add(fragmentThird);
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
        viewPager.setOffscreenPageLimit(3);

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
                        layout1.setBackgroundResource(R.mipmap.bac1);
                        layout2.setBackgroundResource(R.mipmap.bac2);
                        layout3.setBackgroundResource(R.mipmap.bac2);
                        break;
                    case 1:
                        //图片切换
                        image1.setImageDrawable(getResources().getDrawable(R.mipmap.lcd));
                        image2.setImageDrawable(getResources().getDrawable(R.mipmap.msgs));
                        image3.setImageDrawable(getResources().getDrawable(R.mipmap.mobile));
                        //背景加深
                        layout1.setBackgroundResource(R.mipmap.bac2);
                        layout2.setBackgroundResource(R.mipmap.bac1);
                        layout3.setBackgroundResource(R.mipmap.bac2);
                        break;
                    case 2:
                        //图片切换
                        image1.setImageDrawable(getResources().getDrawable(R.mipmap.lcd));
                        image2.setImageDrawable(getResources().getDrawable(R.mipmap.msg));
                        image3.setImageDrawable(getResources().getDrawable(R.mipmap.mobiles));
                        //背景加深
                        layout1.setBackgroundResource(R.mipmap.bac2);
                        layout2.setBackgroundResource(R.mipmap.bac2);
                        layout3.setBackgroundResource(R.mipmap.bac1);
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
                    //xiao xi zhongzhuan
                    msgPullToFlash = handler.obtainMessage();
                    msgPullToFlash.what = MSGDATA;
                    msgPullToFlash.obj = msg.obj;
                    pullToFlashHandler.sendMessage(msgPullToFlash);
                    msgTerminal = handler.obtainMessage();
                    msgTerminal.what = MSGDATA;
                    msgTerminal.obj = msg.obj;
                    terminalHandler.sendMessage(msgTerminal);

                    strChat += (String)msg.obj ;
                    chartText.setText(strChat);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
                                    System.exit(0);
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
    protected void onDestroy() {
        System.out.println("Activity--->onDestroy");
        super.onDestroy();
    }

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
}
