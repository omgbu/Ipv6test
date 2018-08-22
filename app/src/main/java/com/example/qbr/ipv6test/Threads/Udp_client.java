package com.example.qbr.ipv6test.Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by qbr on 2018/8/21.
 */

public class Udp_client extends Thread {
    DatagramSocket msocket;
    private String IpAddress;

    public Udp_client(String ipAddress){
        this.IpAddress = ipAddress;
    }

    @Override
    public void run() {
        String mes = "发送成功！！！";
        try {
            msocket = new DatagramSocket();
            InetAddress NEXT_IP = InetAddress.getByName(IpAddress);
            DatagramPacket packet_CAR= new DatagramPacket(mes.getBytes(), mes.getBytes().length, NEXT_IP, 8890);
            msocket.send(packet_CAR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
