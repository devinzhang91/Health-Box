package com.example.devinzhang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by root on 15-4-4.
 */
public class TerminalFragment extends Fragment {

    private TerminalListener terminalListener;
    private String chartString = "";
    private TextView chartText;
    private ScrollView scrollView;

    public TerminalFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_terminal, container, false);
        System.out.println("TerminalFragment--->onCreateView");

        chartText = (TextView) rootView.findViewById(R.id.textView_chart_string);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        System.out.println("TerminalFragment--->onAttach:"+activity.getLocalClassName());
        terminalListener = (TerminalListener) activity;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //callback
        System.out.println("TerminalFragment--->onActivityCreated:");
        terminalListener.onConnentTerminalFragment(msgHandler);
    }

    public interface TerminalListener {
        public void onConnentTerminalFragment( Handler btHandler);
    }

    public Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.DATA:
                    chartString += (String)msg.obj;
                    //Log.e("TerminalFragment", "input: " + chartString);
                    //终端窗口显示消息
                    chartText.setText(MainActivity.strChat);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                case MainActivity.STATE:
                    if(msg.obj.equals(MyBluetooth.CONNECT_SUCCESS)) {

                    }
                    break;
                case MainActivity.MESSAGE:
                    break;
            }
        }
    };
}
