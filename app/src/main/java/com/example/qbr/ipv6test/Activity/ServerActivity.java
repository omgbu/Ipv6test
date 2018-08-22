package com.example.qbr.ipv6test.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.qbr.ipv6test.R;
import com.example.qbr.ipv6test.Threads.Tcp_server;
import com.example.qbr.ipv6test.Threads.Udp_server;

/**
 * Created by qbr on 2018/8/20.
 */

public class ServerActivity extends Activity{
    TextView textView_s,textView_mes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        textView_s = (TextView)findViewById(R.id.textView_s);
        textView_mes = (TextView)findViewById(R.id.Mes);
        final Handler mMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                textView_mes.setText(msg.obj.toString());
            }
        };
        Intent intent = getIntent();
        String tran= intent.getStringExtra("tran");
        switch (tran){
            case "TCP":
                Tcp_server t = new Tcp_server(mMessageHandler);
                t.start();
                break;
            case "UDP":
                Udp_server t1 = new Udp_server(mMessageHandler);
                t1.start();
                break;
        }

    }
}
