package com.example.tp_linklightsimulator;

import android.util.Log;
import android.widget.Toast;

import java.io.Console;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DiscoveryListener implements Runnable {
    public boolean running = true;
    public DatagramSocket socket = null;
    public DiscoveryListener() throws UnknownHostException, SocketException {
        this.socket = new DatagramSocket(9999, InetAddress.getByName("0.0.0.0"));
        this.socket.setBroadcast(true);
    }
    public String decrypt(byte[] input) {
        byte firstKey = (byte) 0xAB;
        byte key = firstKey;
        byte nextKey;
        String output = "";
        for(int i = 0; i < input.length; i++)
        {
            nextKey = input[i];
            input[i] = (byte) (input[i] ^ key);
            key = nextKey;
        }
        return new String(input);
    }
    @Override
    public void run()
    {
        System.out.println("Now listening");
        while(!Thread.currentThread().isInterrupted())
        {
            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                System.out.println(this.decrypt(packet.getData()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void shutdown()
    {
        this.running = false;
    }
}
