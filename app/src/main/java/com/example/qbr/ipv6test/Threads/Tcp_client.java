package com.example.qbr.ipv6test.Threads;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by qbr on 2018/7/12.
 */

public class Tcp_client extends Thread {
    Handler mhandler;
    Message mes;
    private int mTimeout = 10000;
    private String IpAddress;

    public Tcp_client(Handler handler, String ipAddress){
        this.mhandler=handler;
        this.IpAddress = ipAddress;
    }

    @Override
    public void run() {
        super.run();
        try {
            Socket socket = new Socket( );
            InetAddress inetAddress = InetAddress.getByName(IpAddress);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, 8899);
            socket.connect(socketAddress, mTimeout);
            OutputStream mOutStream = socket.getOutputStream();
            // 2、获取输出流，向服务器端发送信息
            // 向本机的52000端口发出客户请求
            mOutStream.write("连接成功了！！！".getBytes());
            mOutStream.flush();

        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stack = sw.toString();
            mes = mhandler.obtainMessage();
            mes.obj = stack;
            mhandler.sendMessage(mes);
            Log.e("save_log", stack);
            e.printStackTrace();
        }
    }
}
