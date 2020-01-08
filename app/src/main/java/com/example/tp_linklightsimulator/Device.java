package com.example.tp_linklightsimulator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class Device {
    public boolean running = true;
    public static DatagramSocket socket = null;
    public static Thread listener;
    public static String json_status;
    public static String name;
    public static int state = 1;
    public static String deviceId;
    public static ImageView view;
    public static Context ctx;

    public static boolean init(Context ctx, String name, ImageView lamp) {
        Device.name = name;

        view = lamp;
        Device.ctx = ctx;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device.set_state(state == 1 ? 0 : 1);
            }
        });
        try {
            Device.socket = new DatagramSocket(9999, InetAddress.getByName("0.0.0.0"));
            Device.socket.setBroadcast(true);
            socket.setSoTimeout(10);
            Resources res = ctx.getResources();
            InputStream in_s = res.openRawResource(R.raw.lb100_status);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            json_status = new String((b));
            System.out.println(json_status);
            view.setImageResource(R.drawable.light_on);
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

    public static void setName(String name) {
        Device.name = name;
    }

    public static void run() {
        listener = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();

            }
        });
        listener.start();
    }

    public static void stop() {
        try {
            listener.interrupt();
            listener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generateString() {
        String deviceId = "";
        WifiManager manager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
        }
    public static void sendInfos(DatagramPacket packet) throws IOException {
        byte[] msg = tplink.encrypt(String.format(json_status,Device.deviceId, Device.name, Device.state));

            socket.send(new DatagramPacket(msg,msg.length, packet.getAddress(), packet.getPort() ));

    }
    public static void set_state(int state)
    {
        deviceId = generateString();
        Device.state = state;
        if(Device.state == 1){
            view.setImageResource(R.drawable.light_on);
        }
        else{
            view.setImageResource(R.drawable.light_off);
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
                String json = tplink.decrypt(packet.getData());
                JSONObject obj = new JSONObject(json);
                JSONObject instruct = obj.getJSONObject("system");
                if(instruct.has("get_sysinfo"))
                {
                    System.out.println("Get infos");
                }
                else if (instruct.has("set_relay_state"))
                {
                    set_state(instruct.getJSONObject("set_relay_state").getInt("state"));
                }
                String instruction;
                System.out.println(json);
                Device.sendInfos(packet);


            }
            catch (SocketTimeoutException exception) {
                // Normal.
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
