package com.example.tp_linklightsimulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {
    public boolean running = true;

    public void listen() throws IOException {
        System.out.println("Hello, World");
        DatagramSocket socket = new DatagramSocket(9999, InetAddress.getByName("0.0.0.0"));
        socket.setBroadcast(true);
        System.out.println("Listen on " + socket.getLocalAddress() + " from " + socket.getInetAddress() + " port " + socket.getBroadcast());
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (this.running) {
            String msg = "{\"system\":{\"get_sysinfo\":{\"err_code\":0,\"sw_ver\":\"1.0.8 Build 151101 Rel.24452\",\"hw_ver\":\"1.0\",\"type\":\"smartplug\",\"model\":\"HS130(EU)\",\"mac\":\"50:C7:BF:00:C4:D0\",\"deviceId\":\"8006BE9B2C1A6114DBFA0632B02D566D170BC38A\",\"hwId\":\"22603EA5E716DEAEA6642A30BE87AFCA\",\"fwId\":\"BFF24826FBC561803E49379DBE74FD71\",\"oemId\":\"812A90EB2FCF306A993FAD8748024B07\",\"alias\":\"mio\",\"dev_name\":\"Wi-Fi Smart Plug\",\"icon_hash\":\"\",\"relay_state\":0,\"on_time\":0,\"active_mode\":\"schedule\",\"feature\":\"TIM\",\"updating\":0,\"rssi\":-52,\"led_off\":0,\"latitude\":0,\"longitude\":0}},\"emeter\":{\"err_code\":-1,\"err_msg\":\"module not support\"}}";
            byte[] msgByte = tplink.encrypt(msg);
            System.out.println("Waiting for data");
            socket.receive(packet);
            System.out.println(tplink.decrypt(packet.getData()));
            socket.send(new DatagramPacket(msgByte,msgByte.length, packet.getAddress(), packet.getPort() ));
            System.out.println("Data received");
        }
    }
    public Thread dl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("started");
        Log.d("fdsf", "gfdfg");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.start_stop);
        final Button button_menu = findViewById(R.id.button);
        button.setText("START");
        System.out.println("started");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("fdfdf", "ggfdgfg");
                if(button.getText() == "START")
                {
                    dl = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                listen();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dl.start();
                }
                else{
                    running = false;
                    dl.interrupt();
                }
                button.setText(button.getText() == "START" ? "STOP" : "START");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.deviceselector, menu);
        return true;
    }

    // Event Handling for individual deviceSelector
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_save:
                Toast.makeText(this, "Save selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_search:
                Toast.makeText(this, "Search selected:", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete:
                Toast.makeText(this, "Delete selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}

     /*   button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                //button.setText(button.getText() == "START" ? "STOP" : "START");
            }
        });*/
