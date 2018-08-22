package com.example.qbr.ipv6test.Threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.InputStream;
import java.util.UUID;


/**
 * Created by qbr on 2018/8/21.
 */

public class Blue_server extends Thread {
    private BluetoothServerSocket serverSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private final String tag = "b_server";
    private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private Handler handler;
    private Message mes;
    public Blue_server(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(tag, UUID.fromString(MY_UUID));
//            serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(tag, UUID.fromString(MY_UUID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(tag, "等待客户连接...");
        try {
            BluetoothSocket socket = serverSocket.accept();
            boolean run = true;
            while (run){
                if (socket.isConnected()) {
                    Log.d(tag, "已建立与客户连接.");
                    // 写数据
                    RecDataToClient(socket);
                    break;
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    // 蓝牙服务端发送简单的一个字符串
    private void RecDataToClient(BluetoothSocket socket) {
        byte[] buffer = new byte[1024];
        try {
            InputStream is = socket.getInputStream();
            int cnt = is.read(buffer);
            is.close();
            String s = new String(buffer, 0, cnt);
            mes = handler.obtainMessage();
            mes.obj = s;
            handler.sendMessage(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
