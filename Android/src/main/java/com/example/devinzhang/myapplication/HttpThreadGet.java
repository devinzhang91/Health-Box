package com.example.devinzhang.myapplication;

import java.util.List;
import org.apache.http.NameValuePair;
import android.os.Handler;
import android.os.Message;

public class HttpThreadGet extends Thread {
    public static final int HISTORY = 0; // 历史记录查询

    List<NameValuePair> params;
    String httpUrl;
    int type;
    Handler handler;

    HttpThreadGet(String url, int type, Handler handler) {
        this.type = type;
        this.httpUrl = url;
        this.handler = handler;
    }

    public void run() {
        HttpRunner jsonParser = new HttpRunner();
        String json = jsonParser.makeHttpGET(httpUrl);
        Message msg = new Message();
        switch (type) {
            case HISTORY: // 历史记录查询
                msg.what = HISTORY;
                break;
            default:
                break;
        }
        msg.obj = json;
        handler.sendMessage(msg);
    }
}
