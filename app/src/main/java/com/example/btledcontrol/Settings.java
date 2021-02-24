package com.example.btledcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private SharedPreferences mSettings;
    private static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_DEBUG = "debug";
    public static final String APP_PREFERENCES_ADRESS = "adress";
    boolean debug;
    public static String adress;
    private CheckBox debugCheckBox;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        debugCheckBox = (CheckBox)findViewById(R.id.checkBoxDebug);
        txt = this.findViewById(R.id.editText);
        mSettings = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        if (mSettings.contains(APP_PREFERENCES_DEBUG))        { debug = mSettings.getBoolean(APP_PREFERENCES_DEBUG,false); }
        if (mSettings.contains(APP_PREFERENCES_ADRESS))       {
            adress = mSettings.getString(APP_PREFERENCES_ADRESS,"20:14:05:09:20:37");
        } else {
            adress = "20:14:05:09:20:37";
        }
        txt.setText(adress);

        if (debug) {
            debugCheckBox.setChecked(true);
            debugCheckBox.setText("Режим отладки включён");
        } else {
            debugCheckBox.setChecked(false);
            debugCheckBox.setText("Режим отладки выключен");
        }

        debugCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    debugCheckBox.setText("Режим отладки включён");
                    debug = true; }
                else {
                    debugCheckBox.setText("Режим отладки выключен");
                    debug = false;
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Сохранение данных
                Toast.makeText(this, "Сохраняем и подключаемся, подождите...", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putBoolean(APP_PREFERENCES_DEBUG, debug);
                if (txt.getText().length() != 0) editor.putString(APP_PREFERENCES_ADRESS, txt.getText().toString());
                editor.apply();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}