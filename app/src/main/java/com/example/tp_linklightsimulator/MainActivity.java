package com.example.tp_linklightsimulator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public boolean running = true;
    public Thread dl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView lamp = findViewById(R.id.imageView);
        Device.init(this, "gogo441", lamp);
        final Button button = findViewById(R.id.start_stop);
        final EditText inputName = findViewById(R.id.deviceName);
        button.setText("START");
        System.out.println("started");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button.getText() == "START") {
                    Device.run();
                    Device.setName(inputName.getText().toString());
                } else {
                    Device.stop();
                }
                button.setText(button.getText() == "START" ? "STOP" : "START");
            }
        });
        inputName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.equals("")) {
                    Device.setName(inputName.getText().toString());
                }
            }


            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {
                Device.setName(inputName.getText().toString());

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
        switch (menuItem.getItemId()) {
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
