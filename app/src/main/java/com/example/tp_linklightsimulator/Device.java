package com.example.tp_linklightsimulator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.Settings;
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

/*
 *
 *API: https://www.briandorey.com/post/tp-link-lb130-smart-wi-fi-led-bulb-python-control
 *
 *
 *
 *
 *
 *
 *
 * */
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
    public static int brightness = 255;
    public static int hue = 50;
    public static int saturation = 50;

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
            InputStream in_s = res.openRawResource(R.raw.lb130_status);

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
        return  Settings.Secure.getString(ctx.getContentResolver(),  Settings.Secure.ANDROID_ID);
    }

    public static void sendInfos(DatagramPacket packet) throws IOException {
        byte[] msg = tplink.encrypt(String.format(json_status, Device.name, Device.deviceId, Device.state));

        socket.send(new DatagramPacket(msg, msg.length, packet.getAddress(), packet.getPort()));

    }

    public static void sendString(DatagramPacket packet, String s) throws IOException {
        byte[] msg = tplink.encrypt(s);

        socket.send(new DatagramPacket(msg, msg.length, packet.getAddress(), packet.getPort()));

    }

    public static void set_state(int state) {
        deviceId = generateString();
        Device.state = state;
        float hsv[] = {(float)hue, ((float)saturation)/256,((float) brightness)/256};
        /*
        * hue 0-360
        * saturation 0-1 per default
        * brightness 0-1 per default*/
        int color =  Color.HSVToColor(hsv);
        int ORANGE = 0xFFFFBF00;
        final Bitmap back = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.light_off).copy(Bitmap.Config.ARGB_8888, true);
        // Decoding the image two resource into a Bitmap
        Bitmap imageTwo = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.light_contour);
        if (state == 1) {
            //imageTwo = setColor(imageTwo, (color & 0x00FFFFFF ) | (((byte)brightness)<<8*3));
            imageTwo = setColor(imageTwo, color);
            Canvas canvas = new Canvas(back);
            canvas.drawBitmap(imageTwo, 0, 0, new Paint());

        } else {
        }

        // Here we construct the canvas with the specified bitmap to draw onto

        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(back);
            }
        });

    }
    public static void setBrightness(int b){
        Device.brightness = b;
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
    public static Bitmap setColor(Bitmap src, int targetColor) {
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
            pixels[x] = ((pixels[x] & 0xFF000000) != 0) ? targetColor : pixels[x];
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
            System.out.println(tplink.decrypt(packet.getData()));
            try {
                socket.receive(packet);
                String json = tplink.decrypt(packet.getData());
                System.out.println(json);
                JSONObject obj = new JSONObject(json);
                if(obj.has("system")) {
                    JSONObject instruct = obj.getJSONObject("system");
                    if (instruct.has("get_sysinfo")) {
                        Device.sendInfos(packet);
                    } else if (instruct.has("set_relay_state")) {
                        set_state(instruct.getJSONObject("set_relay_state").getInt("state"));
                        Device.sendInfos(packet);
                    }
                }
                else if(obj.has("smartlife.iot.smartbulb.lightingservice")){
                    if(obj.getJSONObject("smartlife.iot.smartbulb.lightingservice").has("transition_light_state")) {
                        JSONObject instruct = obj.getJSONObject("smartlife.iot.smartbulb.lightingservice").getJSONObject("transition_light_state");
                        if(instruct.has("brightness"))
                        {
                            brightness = (instruct.getInt("brightness")) ;
                        }
                        if(instruct.has("saturation"))
                        {
                            saturation = (instruct.getInt("saturation"));
                        }
                        if(instruct.has("hue"))
                        {
                            hue  = (instruct.getInt("hue"));
                        }

                        set_state(instruct.getInt("on_off"));
                        String str =" {\"smartlife.iot.smartbulb.lightingservice\":{\"transition_light_state\":{\"on_off\":%d,\"mode\":\"normal\",\"hue\":%d,\"saturation\":%d,\"color_temp\":0,\"brightness\":%d,\"err_code\":0}}} " ;
                        str = String.format(str, state, hue, saturation, brightness);
                        sendString(packet, str);

                    }

                }



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
