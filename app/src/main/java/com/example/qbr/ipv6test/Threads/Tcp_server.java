package com.example.qbr.ipv6test.Threads;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by qbr on 2018/7/12.
 */

public class Tcp_server extends Thread {
    Handler mhandler;
    Message mes;

    public Tcp_server(Handler handler){
        this.mhandler=handler;
    }
    InputStream inputStream;
    int bytes = -1;
    byte[] buffer = new byte[1024];
    ServerSocket server=null;
    Socket socket=null;
    @Override
    public void run() {
        super.run();
        try {
            server=new ServerSocket(8899);
            socket=server.accept();
            inputStream = socket.getInputStream();
            bytes = inputStream.read(buffer);
            String s = new String(buffer, 0, bytes);
            mes = mhandler.obtainMessage();
            mes.obj = s;
            mhandler.sendMessage(mes);
//            socket = new DatagramSocket(5209);
//            request = new DatagramPacket(new byte[1024], 1024);
//            socket.receive(request);

        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stack = sw.toString();
            Log.e("save_log", stack);
            e.printStackTrace();
        }

    }

    public void close(){
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
