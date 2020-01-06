package com.example.tp_linklightsimulator;

import android.util.Log;

import java.io.Console;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DiscoveryListener implements Runnable {
    public boolean running = true;
    @Override
    public void run()
    {
        this.running = true;
        Log.println(Log.INFO,"","gogo");
        while(!Thread.currentThread().isInterrupted())
        {
            System.out.println("Yoooo");
            byte[] buffer = new byte[2048];
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(9999, InetAddress.getByName("0.0.0.0"));
                DatagramPacket p = new DatagramPacket(buffer, buffer.length);
                System.out.println("Now listening");
                socket.receive(p);
                System.out.println(p.getData().toString());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
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
