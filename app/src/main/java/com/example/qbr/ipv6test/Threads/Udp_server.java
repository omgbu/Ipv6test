package com.example.qbr.ipv6test.Threads;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by qbr on 2018/8/21.
 */

public class Udp_server extends Thread {
    private Handler handler;
    Message mes;
    DatagramSocket socket;
    DatagramPacket packet;
    public Udp_server(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        try {
            socket=new DatagramSocket(8890);
            socket.setSoTimeout(60000);
            byte[] data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            String reply = new String(data, 0, packet.getLength());
            mes = handler.obtainMessage();
            mes.obj = reply;
            handler.sendMessage(mes);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stack = sw.toString();
            mes = handler.obtainMessage();
            mes.obj = stack;
            handler.sendMessage(mes);
            e.printStackTrace();
        }
    }
}
