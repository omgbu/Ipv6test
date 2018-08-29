package com.example.qbr.ipv6test.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.example.qbr.ipv6test.R;
import com.example.qbr.ipv6test.Utils.ExeCommand;

/**
 * Created by qbr on 2018/8/21.
 */

public class CommandActivity extends Activity {
    TextView content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);
        content = (TextView) findViewById(R.id.content);
        Intent intent = getIntent();
        String address= intent.getStringExtra("add");
        String ip = null;
        switch (address){
            case "对方IP地址":
                ip= intent.getStringExtra("Ip_server");
                break;
            case "Cernet-BUPT":
                ip= "2001:da8:215:6a01::b198";
                break;
        }
        content.append("qbr:\\>  ping6 -c 10 "+ ip + "\n");
        new Thread(new comThread(mMessageHandler, ip)).start();

    }
    final Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            content.append(msg.obj.toString());
        }
    };
    class comThread extends Thread{
        String ip;
        private Handler handler;
        Message mes;
        public comThread(Handler handler, String ip){
            this.ip = ip;
            this.handler = handler;
        }

        @Override
        public void run() {
            String cmd3 = "ping6 -c 10 " + ip;
            String str3 = new ExeCommand().run(cmd3, 1000000).getResult();
            mes = handler.obtainMessage();
            mes.obj = str3;
            handler.sendMessage(mes);
            Log.i("auto", "hh"+str3);
        }
    }
}
