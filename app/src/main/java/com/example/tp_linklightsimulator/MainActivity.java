package com.example.tp_linklightsimulator;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
        final TextView inputName = findViewById(R.id.deviceName);
        button.setText("STOP");
        Device.run();
        Device.setName(inputName.getText().toString());
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
        final Context ctx = this;
        inputName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Title");

// Set up the input
                final EditText input = new EditText(ctx);
                input.setText(inputName.getText().toString());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Device.name = input.getText().toString();
                        inputName.setText(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
