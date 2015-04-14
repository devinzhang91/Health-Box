package com.example.root.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 15-4-4.
 */
public class TerminalFragment extends Fragment {

    private TerminalListener terminalListener;
    private String chartString = "";

    public TerminalFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.terminal_view, container, false);
        System.out.println("TerminalFragment--->onCreateView");
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
                case MainActivity.MSGDATA:
                    chartString += (String)msg.obj;
                    Log.e("TerminalFragment", "input: " + chartString);
                    break;
                case MainActivity.STATE:
                    if(msg.obj.equals(MyBluetooth.CONNECT_SUCCESS)) {

                    }
                    break;
            }
        }
    };
}
