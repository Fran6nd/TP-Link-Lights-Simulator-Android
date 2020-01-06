package com.example.tp_linklightsimulator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    public Thread dl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("started");
        Log.d("fdsf", "gfdfg");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.start_stop);
        button.setText("START");
        System.out.println("started");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("fdfdf", "ggfdgfg");
                if(button.getText() == "START")
                {
                    dl= new Thread(new DiscoveryListener());
                    dl.start();
                }
                else{
                    dl.interrupt();
                }
                button.setText(button.getText() == "START" ? "STOP" : "START");
            }
        });
    }
}

     /*   button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                //button.setText(button.getText() == "START" ? "STOP" : "START");
            }
        });*/
