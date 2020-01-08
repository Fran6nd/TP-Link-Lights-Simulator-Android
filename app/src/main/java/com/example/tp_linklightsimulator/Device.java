package com.example.tp_linklightsimulator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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

public class Device {
    public static DatagramSocket socket = null;
    public static Thread listener;
    public static String json_status;
    public static String name;
    public static int state = 1;
    public static String deviceId;
    public static ImageView view;
    public static AppCompatActivity ctx;
    public boolean running = true;

    public static boolean init(AppCompatActivity ctx, String name, ImageView lamp) {
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
            set_state(0);
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
        WifiManager manager = (WifiManager) ((Context) ctx).getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    public static void sendInfos(DatagramPacket packet) throws IOException {
        byte[] msg = tplink.encrypt(String.format(json_status, Device.deviceId, Device.name, Device.state));

        socket.send(new DatagramPacket(msg, msg.length, packet.getAddress(), packet.getPort()));

    }

    public static void set_state(int state) {
        deviceId = generateString();
        Device.state = state;

        int ORANGE = 0xFFFFBF00;
        final Bitmap back = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.light_off).copy(Bitmap.Config.ARGB_8888, true);
        // Decoding the image two resource into a Bitmap
        Bitmap imageTwo = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.light_contour);
        if (state == 1) {
            imageTwo = replaceColor(imageTwo, Color.WHITE, ORANGE);
        } else {
            imageTwo = replaceColor(imageTwo, ORANGE, Color.WHITE);
        }

        // Here we construct the canvas with the specified bitmap to draw onto
        Canvas canvas = new Canvas(back);
        canvas.drawBitmap(imageTwo, 0, 0, new Paint());
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(back);
            }
        });

    }

    public static Bitmap replaceColor(Bitmap src, int fromColor, int targetColor) {
        if (src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int x = 0; x < pixels.length; ++x) {
            pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];
        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }


    public static void loop() {
        System.out.println("Now listening");
        while (!Thread.currentThread().isInterrupted()) {
            byte[] buf = new byte[512];
            Arrays.fill(buf, (byte) 0);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String json = tplink.decrypt(packet.getData());
                JSONObject obj = new JSONObject(json);
                JSONObject instruct = obj.getJSONObject("system");
                if (instruct.has("get_sysinfo")) {
                    System.out.println("Get infos");
                } else if (instruct.has("set_relay_state")) {
                    set_state(instruct.getJSONObject("set_relay_state").getInt("state"));
                }
                String instruction;
                System.out.println(json);
                Device.sendInfos(packet);


            } catch (SocketTimeoutException exception) {
                // Normal.
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
