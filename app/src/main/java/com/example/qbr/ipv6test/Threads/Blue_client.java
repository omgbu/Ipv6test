package com.example.qbr.ipv6test.Threads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import org.json.JSONObject;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by qbr on 2018/8/21.
 */

public class Blue_client extends Thread {
    private BluetoothDevice device;
    private final String tag = "B_client";
    private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private String MY_IP;
    private String provider;
    public Blue_client(BluetoothDevice device, String MY_IP, String provider) {
        this.device = device;
        this.MY_IP = MY_IP;
        this.provider = provider;
    }

    @Override
    public void run() {
        BluetoothSocket socket;
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
//            socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            Log.d(tag, "连接服务端...");
            socket.connect();
            Log.d(tag, "连接建立.");
            // 读数据
            readDataFromServer(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readDataFromServer(BluetoothSocket socket) {
        try {
            OutputStream os = socket.getOutputStream();
            if (os != null) {
                // 需要发送的信息
                JSONObject jsonObjSend;
                jsonObjSend = new JSONObject();
                jsonObjSend.put("IP", MY_IP);
                jsonObjSend.put("Provider", provider);
                os.write(jsonObjSend.toString().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

