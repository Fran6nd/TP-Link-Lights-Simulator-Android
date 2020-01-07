package com.example.tp_linklightsimulator;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class DiscoveryListener {
    public boolean running = true;
    public static DatagramSocket socket = null;
    public static  Thread listener;
    public static  String json_status;
    public static String name;
    public static boolean init(Context ctx, String name) {
        DiscoveryListener.name = name;
        try {
            DiscoveryListener.socket = new DatagramSocket(9999, InetAddress.getByName("0.0.0.0"));
            DiscoveryListener.socket.setBroadcast(true);
            socket.setSoTimeout(10);
            Resources res = ctx.getResources();
            InputStream in_s = res.openRawResource(R.raw.lb100_status);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            json_status = new String((b));
            System.out.println(json_status);
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
    public static void setName(String name)
    {
        DiscoveryListener.name = name;
    }
    public static void run()
    {
        Log.d("goog", "now listening...");
        listener  = new Thread(new Runnable() {
            @Override
            public void run() {
                    loop();

            }
        });
        listener.start();
    }
    public static void stop(){
        try {
            listener.interrupt();
            listener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void loop(){
        System.out.println("Now listening");
        while(!Thread.currentThread().isInterrupted())
        {
            byte[] buf = new byte[512];
            Arrays.fill( buf, (byte) 0 );
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                System.out.println(tplink.decrypt(packet.getData()));
                byte[] msg = tplink.encrypt(String.format(json_status, DiscoveryListener.name));
                socket.send(new DatagramPacket(msg,msg.length, packet.getAddress(), packet.getPort() ));

            }
            catch (SocketTimeoutException exception) {
                // Normal.
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
