package com.example.qbr.ipv6test.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.qbr.ipv6test.R;
import com.example.qbr.ipv6test.Threads.Tcp_client;
import com.example.qbr.ipv6test.Threads.Udp_client;

/**
 * Created by qbr on 2018/8/20.
 */

public class ClientActivity extends Activity {
    TextView textView_c,textView_mes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        textView_c = (TextView) findViewById(R.id.c);
        textView_mes = (TextView) findViewById(R.id.Mes);
        final Handler mMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                textView_mes.setText("异常报错为："+msg.obj.toString());

            }
        };
        Intent intent = getIntent();
        String ip= intent.getStringExtra("Ip_server");
        String tran= intent.getStringExtra("tran");
        switch (tran){
            case "TCP":
                Tcp_client t = new Tcp_client(mMessageHandler,ip);
                t.start();
                break;
            case "UDP":
                Udp_client t1 = new Udp_client(ip);
                t1.start();
                break;
        }
    }
}
