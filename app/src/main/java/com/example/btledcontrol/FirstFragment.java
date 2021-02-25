package com.example.btledcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.UUID;


public class FirstFragment extends Fragment {
    private static final String TAG = "bluetooth1";
    private static final int REQUEST_ENABLE_BT = 1;
    ////////////////////////////////////////////////////// Параметры для сохранения настроек
    private SharedPreferences mSettings;
    private static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_MODE = "mode";
    public static final String APP_PREFERENCES_BRIGHTNESS = "brightness";
    public static final String APP_PREFERENCES_EMPTYBRIGHT = "emptyBright";
    public static final String APP_PREFERENCES_SMOOTH = "smooth";
    public static final String APP_PREFERENCES_RAINBOWSTEP = "rainbowStep";
    public static final String APP_PREFERENCES_SMOOTHFREQ = "smoothFreq";
    public static final String APP_PREFERENCES_MAXCOEFFREQ = "maxCoefFreq";
    public static final String APP_PREFERENCES_LIGHTCOLOR = "lightColor";
    public static final String APP_PREFERENCES_LIGHTSAT = "lightSat";
    public static final String APP_PREFERENCES_COLORSPEED = "colorSpeed";
    public static final String APP_PREFERENCES_RAINBOWPERIOD = "rainbowPeriod";
    public static final String APP_PREFERENCES_RAINBOWSTEP2 = "rainbowSteptwo";
    public static final String APP_PREFERENCES_RUNNINGSPEED = "runningSpeed";
    public static final String APP_PREFERENCES_HUESTEP = "hueStep";
    public static final String APP_PREFERENCES_HUESTART = "hueStart";
    public static final String APP_PREFERENCES_DEBUG = "debug";
    public static final String APP_PREFERENCES_ADRESS = "adress";
    ////////////////////////////////////////////////////////
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private static byte[] settings =  new byte[2];
    private static byte[] eeprom =  new byte[15];
    private static byte[] dimmer =  new byte[5];
    boolean preModeActive = false, debug;
    boolean connectionEstablished = false;
    TextView logTextLeft,logTextRight,logTxt3,logTxt4,textNote,logColor;
    SeekBar seekBarOne,seekBarTwo,seekBarThree,seekBarFour;
    Spinner spinner,spinnerPreModes;
    Button buttonOnOff;
    ImageView imgColor;
    Float chsv;
    int colorRed, colorGreen, colorBlue;
    // Адреса параметров ////////////////////////////////////////////////////////////////////////////
    byte onoff = 1;              //1.x Включение отключение
    byte noiseSet = 2;           //2.x Настройка шумов
    byte mode = 3;               //3.x Режим {eeprom[0]}
    byte preMode = 4;            //4.x Под режим
    byte brightness = 5;         //5.x Яркость горящих светодиодов {eeprom[1]}
    byte emptyBright = 6;        //6.x Яркость негорящих светодиодов {eeprom[2]}
    byte smooth = 7;             //7.x Плавность спектра (SMOOTH, режим 1,2) {eeprom[3]}
    byte rainbowStep = 8;        //8.x Скорость движения (RAINBOW_STEP режим 2) {eeprom[4]}
    byte smoothFreq = 9;         //9.x Плавность включения (SMOOTH_FREQ режим 3,4,5) {eeprom[5]}
    byte maxCoefFreq = 10;       //10.x Чувствительность (MAX_COEF_FREQ режим 3,4,5,8) {eeprom[6]}
    byte lightColor = 11;        //11.x Настройка цвета (LIGHT_COLOR режим 7) {eeprom[7]}
    byte lightSat = 12;          //12.x Натройка насыщенности (LIGHT_SAT режим 7) {eeprom[8]}
    byte colorSpeed = 13;        //13.x Скорость изменения (COLOR_SPEED режим 7) {eeprom[9]}
    byte rainbowPeriod = 14;     //14.x Скорость движения (RAINBOW_PERIOD режим 7) {eeprom[10]}
    byte rainbowStepTwo = 15;    //15.x Шаг цвета (RAINBOW_STEP_2 режим 7) {eeprom[11]}
    byte runningSpeed = 16;      //16.x Скорость движения (RUNNING_SPEED режим 8) {eeprom[12]}
    byte hueStep = 17;           //17.x Шаг изменения цвета (HUE_STEP режим 9) {eeprom[13]}
    byte hueStart = 18;          //18.x Цвет (HUE_START режим 9) {eeprom[14]}
    ////////////////////////////////////////////////////////////////////////////////////////////////



    // SPP UUID сервиса
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-адрес Bluetooth модуля
    private static String address = "20:14:05:09:20:37";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    //Метод запрашивает поток данных с ардуино и записывает в массив
    private void request(){
        settings[0] = 1;
        settings[1] = 2;
        sendDataByte(settings);

        eeprom = readDataByte();
        logTextRight.setText(String.valueOf(eeprom[0] + "." + eeprom[1] + "." + eeprom[2] + "." + eeprom[3] + "." + eeprom[4] + "." + eeprom[5] + "." + eeprom[6] + "." + eeprom[7] + "." + eeprom[8] + "." + eeprom[9] + "." + eeprom[10] + "." + eeprom[11] + "." + eeprom[12] + "." + eeprom[13] + "." + eeprom[14]));
        //    }
        //}
    }

    //Метод обновляет текст и отправляет параметр на ардуино
    private void updateText()
    {
        logTextLeft.setText(String.valueOf(settings[0] + "." + settings[1]));
        logTextRight.setText(String.valueOf(eeprom[0] + "." + eeprom[1] + "." + eeprom[2] + "." + eeprom[3] + "." + eeprom[4] + "." + eeprom[5] + "." + eeprom[6] + "." + eeprom[7] + "." + eeprom[8] + "." + eeprom[9] + "." + eeprom[10] + "." + eeprom[11] + "." + eeprom[12] + "." + eeprom[13] + "." + eeprom[14]));
        if (connectionEstablished) sendDataByte(settings);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        mSettings = this.getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        String[] data = {"Обычный", "Радуга спектр", "5 полос", "3 полосы", "Выделение частот", "Огонь", "Радуга", "Бегущие частоты", "Анализатор спектра"};
        String[] preModesOne = {"Все частоты", "Только низкие", "Только средние", "Только высокие"};
        String[] preModesThree = {"Постоянный", "Плавная смена цвета", "Радуга"};

        //Создаём экземпляры диммеров и текстбоксов
        seekBarOne = view.findViewById(R.id.seekParOne);
        seekBarTwo = view.findViewById(R.id.seekParTwo);
        seekBarThree = view.findViewById(R.id.seekParThree);
        seekBarFour = view.findViewById(R.id.seekParFour);
        TextView textSeekBarOne = view.findViewById(R.id.textSeekOne);
        TextView textSeekBarTwo = view.findViewById(R.id.textSeekTwo);
        TextView textSeekBarThree = view.findViewById(R.id.textSeekThree);
        TextView textSeekBarFour = view.findViewById(R.id.textSeekFour);
        logTextLeft = view.findViewById(R.id.textViewConsole);
        logTextRight = view.findViewById(R.id.textViewConsole2);
        logTxt3 = view.findViewById(R.id.txt3);
        logTxt4 = view.findViewById(R.id.txt4);
        logColor = view.findViewById(R.id.txtColorConsole);
        textNote = view.findViewById(R.id.textNotice);
        buttonOnOff = view.findViewById(R.id.btnStar);
        imgColor = view.findViewById(R.id.imageColor);

        // адаптер для списка режимов и под-режимов
        ArrayAdapter<String> adapterModes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, data);
        ArrayAdapter<String> adapterPreModesOne = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, preModesOne);
        ArrayAdapter<String> adapterPreModesThree = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, preModesThree);
        adapterModes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPreModesOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPreModesThree.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Выпадающий список основных режимов
        spinner = view.findViewById(R.id.listmode);
        //Выпадающий список под-режимов
        spinnerPreModes = view.findViewById(R.id.listPreMode);
        spinner.setAdapter(adapterModes);
        // устанавливаем обработчик нажатия списка режимов

        dimmer[1] = brightness;
        dimmer[2] = emptyBright;


        //Изменение первого диммера
        seekBarOne.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0]= dimmer[1];
                settings[1] = (byte)progress;
                eeprom[1] = (byte)progress; //Кидаем в массив для последующего сохранения

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarOne.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                updateText();
            }
        });
        //Изменение второго диммера
        seekBarTwo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0]= dimmer[2];
                settings[1]= (byte)progress;
                eeprom[2] = (byte)progress; //Кидаем в массив для последующего сохранения
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarTwo.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                updateText();
            }
        });
        //Изменение третьего диммера
        seekBarThree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0]= dimmer[3];
                settings[1]= (byte)progress;
                if (dimmer[3] == smooth) eeprom[3] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[3] == smoothFreq) eeprom[5] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[3] == lightColor) {
                    eeprom[7] = (byte)progress; //Кидаем в массив для последующего сохранения
                    previewColorCHSV(progress);
                }
                if (dimmer[3] == lightSat) eeprom[8] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[3] == rainbowPeriod) eeprom[10] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[3] == runningSpeed) eeprom[12] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[3] == hueStep) eeprom[13] = (byte)progress; //Кидаем в массив для последующего сохранения
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarThree.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                updateText();
            }
        });
        //Изменение четвёртого диммера
        seekBarFour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0] = dimmer[4];
                settings[1] = (byte)progress;
                if (dimmer[4] == rainbowStep) eeprom[4] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[4] == maxCoefFreq) eeprom[6] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[4] == lightSat) eeprom[8] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[4] == colorSpeed) eeprom[9] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[4] == rainbowStepTwo) eeprom[11] = (byte)progress; //Кидаем в массив для последующего сохранения
                if (dimmer[4] == hueStart) eeprom[14] = (byte)progress; //Кидаем в массив для последующего сохранения
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarFour.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                updateText();
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                //String item = (String)parent.getItemAtPosition(position);
                settings[0] = mode;
                settings[1]= (byte)position;
                preModeActive = false;
                eeprom[0] = (byte)position; //Кидаем в массив для последующего сохранения
                imgColor.setVisibility(View.INVISIBLE); //Убираем крожок с цветом
                switch (position){
                    case 0: //Обычный
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(false);
                            spinnerPreModes.setEnabled(false);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность спектра");
                        dimmer[3] = smooth;
                        break;
                    case 1: //Радуга спектр
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(true);
                            spinnerPreModes.setEnabled(false);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность спектра");
                        textSeekBarFour.setText("Скорость движения радуги");
                        dimmer[3] = smooth;
                        dimmer[4] = rainbowStep;
                        break;
                    case 2: //5 полос
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(true);
                            spinnerPreModes.setEnabled(false);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность включения");
                        textSeekBarFour.setText("Чувствительность");
                        dimmer[3] = smoothFreq;
                        dimmer[4] = maxCoefFreq;
                        break;
                    case 3: //3 полосы
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(true);
                            spinnerPreModes.setEnabled(false);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность анимации");
                        textSeekBarFour.setText("Чувствительность");
                        dimmer[3] = smoothFreq;
                        dimmer[4] = maxCoefFreq;
                        break;
                    case 4: //Выделение частот
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(true);
                            spinnerPreModes.setEnabled(true);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность анимации");
                        textSeekBarFour.setText("Чувствительность");
                        spinnerPreModes.setAdapter(adapterPreModesOne);
                        dimmer[3] = smoothFreq;
                        dimmer[4] = maxCoefFreq;
                        break;
                    case 5: //Огонь
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(false);
                            seekBarTwo.setEnabled(false);
                            seekBarThree.setEnabled(false);
                            seekBarFour.setEnabled(false);
                            spinnerPreModes.setEnabled(false);
                        }
                        break;
                    case 6: //Радуга
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(true);
                            spinnerPreModes.setEnabled(true);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Настройка цвета");
                        textSeekBarFour.setText("Настройка насыщенности");
                        spinnerPreModes.setAdapter(adapterPreModesThree);
                        dimmer[3] = lightColor;
                        dimmer[4] = lightSat;
                        break;
                    case 7: //Бегущие частоты
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(true);
                            spinnerPreModes.setEnabled(true);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Скорость");
                        textSeekBarFour.setText("Чувствительность");
                        spinnerPreModes.setAdapter(adapterPreModesOne);
                        dimmer[3] = runningSpeed;
                        dimmer[4] = maxCoefFreq;

                        break;
                    case 8: //Анализатор спектра
                        if (connectionEstablished || debug) {
                            seekBarOne.setEnabled(true);
                            seekBarTwo.setEnabled(true);
                            seekBarThree.setEnabled(true);
                            seekBarFour.setEnabled(true);
                            spinnerPreModes.setEnabled(false);
                        }
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Шаг изменения цвета");
                        textSeekBarFour.setText("Цвет");
                        dimmer[3] = hueStep;
                        dimmer[4] = hueStart;
                        break;
                }
                spinnerPreModes.setSelection(0);
                if (seekBarOne.isEnabled() == false) textSeekBarOne.setText("Параметр отключён");
                if (seekBarTwo.isEnabled() == false) textSeekBarTwo.setText("Параметр отключён");
                if (seekBarThree.isEnabled() == false) textSeekBarThree.setText("Параметр отключён");
                if (seekBarFour.isEnabled() == false) textSeekBarFour.setText("Параметр отключён");
                updateText();
                updateView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // устанавливаем обработчик нажатия списка под-режимов
        spinnerPreModes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                //String item = (String)parent.getItemAtPosition(position);
                settings[0] = preMode;
                settings[1]= (byte)position;
                imgColor.setVisibility(View.INVISIBLE); //Убираем кружок с цветом
                if (position != 0) preModeActive = true;
                switch (spinner.getSelectedItemPosition()){
                    case 6:
                        switch (position) {
                            case 0:
                                textSeekBarThree.setText("Настройка цвета");
                                textSeekBarFour.setText("Настройка насыщенности");
                                dimmer[3] = lightColor;
                                dimmer[4] = lightSat;
                                break;
                            case 1:
                                textSeekBarThree.setText("Настройка насыщенности");
                                textSeekBarFour.setText("Скорость изменения");
                                dimmer[3] = lightSat;
                                dimmer[4] = colorSpeed;
                                break;
                            case 2:
                                textSeekBarThree.setText("Скорость движения");
                                textSeekBarFour.setText("Шаг цвета");
                                dimmer[3] = rainbowPeriod;
                                dimmer[4] = rainbowStepTwo;
                                break;
                        }
                     break;
                }
                if (preModeActive) updateText();

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        buttonOnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonOnOff.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                settings[0] = onoff;
                settings[1] = 1;
                updateText();
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Инициализация меню
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }
    //Реакция на события в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_noise) {
            setNoiseLevel();
            return true;
        }
        if (id == R.id.action_reset) {
            resetConnection();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Получаем настройки из файла
    private void getSettings() {
        //Получаем число из настроек
        if (mSettings.contains(APP_PREFERENCES_MODE))        { eeprom[0] = (byte)mSettings.getInt(APP_PREFERENCES_MODE,0); }
        if (mSettings.contains(APP_PREFERENCES_BRIGHTNESS))  { eeprom[1] = (byte)mSettings.getInt(APP_PREFERENCES_BRIGHTNESS,0); }
        if (mSettings.contains(APP_PREFERENCES_EMPTYBRIGHT)) { eeprom[2] = (byte)mSettings.getInt(APP_PREFERENCES_EMPTYBRIGHT,0); }
        if (mSettings.contains(APP_PREFERENCES_SMOOTH))      { eeprom[3] = (byte)mSettings.getInt(APP_PREFERENCES_SMOOTH,0); }
        if (mSettings.contains(APP_PREFERENCES_RAINBOWSTEP)) { eeprom[4] = (byte)mSettings.getInt(APP_PREFERENCES_RAINBOWSTEP,0); }
        if (mSettings.contains(APP_PREFERENCES_SMOOTHFREQ))  { eeprom[5] = (byte)mSettings.getInt(APP_PREFERENCES_SMOOTHFREQ,0); }
        if (mSettings.contains(APP_PREFERENCES_MAXCOEFFREQ)) { eeprom[6] = (byte)mSettings.getInt(APP_PREFERENCES_MAXCOEFFREQ,0); }
        if (mSettings.contains(APP_PREFERENCES_LIGHTCOLOR))  { eeprom[7] = (byte)mSettings.getInt(APP_PREFERENCES_LIGHTCOLOR,0); }
        if (mSettings.contains(APP_PREFERENCES_LIGHTSAT))    { eeprom[8] = (byte)mSettings.getInt(APP_PREFERENCES_LIGHTSAT,0); }
        if (mSettings.contains(APP_PREFERENCES_COLORSPEED))  { eeprom[9] = (byte)mSettings.getInt(APP_PREFERENCES_COLORSPEED,0); }
        if (mSettings.contains(APP_PREFERENCES_RAINBOWPERIOD)) { eeprom[10] = (byte)mSettings.getInt(APP_PREFERENCES_RAINBOWPERIOD,0); }
        if (mSettings.contains(APP_PREFERENCES_RAINBOWSTEP2)) { eeprom[11] = (byte)mSettings.getInt(APP_PREFERENCES_RAINBOWSTEP2,0); }
        if (mSettings.contains(APP_PREFERENCES_RUNNINGSPEED)) { eeprom[12] = (byte)mSettings.getInt(APP_PREFERENCES_RUNNINGSPEED,0); }
        if (mSettings.contains(APP_PREFERENCES_HUESTEP))     { eeprom[13] = (byte)mSettings.getInt(APP_PREFERENCES_HUESTEP,0); }
        if (mSettings.contains(APP_PREFERENCES_HUESTART))    { eeprom[14] = (byte)mSettings.getInt(APP_PREFERENCES_HUESTART,0); }
        if (mSettings.contains(APP_PREFERENCES_DEBUG))        { debug = mSettings.getBoolean(APP_PREFERENCES_DEBUG,false); }
        if (mSettings.contains(APP_PREFERENCES_ADRESS))       { address = mSettings.getString(APP_PREFERENCES_ADRESS,"20:14:05:09:20:37"); }
        spinner.setSelection(eeprom[0]);
        updateView();
        }

    //Обновляем значения на экране
    private void updateView(){
        if (debug) {
            logTextLeft.setVisibility(View.VISIBLE);
            logTextRight.setVisibility(View.VISIBLE);
            logTxt3.setVisibility(View.VISIBLE);
            logTxt4.setVisibility(View.VISIBLE);
            logColor.setVisibility(View.VISIBLE);
        } else {
            logTextLeft.setVisibility(View.INVISIBLE);
            logTextRight.setVisibility(View.INVISIBLE);
            logTxt3.setVisibility(View.INVISIBLE);
            logTxt4.setVisibility(View.INVISIBLE);
            logColor.setVisibility(View.INVISIBLE);
        }
        int progressThree = 0, progressFour = 0;
        if (eeprom[0] == 0) progressThree = eeprom[3]; //Если режим нулевой, достаём третье значение
        if (eeprom[0] == 1) { progressThree = eeprom[3]; progressFour = eeprom[4]; }
        if (eeprom[0] == 2) { progressThree = eeprom[5]; progressFour = eeprom[6]; }
        if (eeprom[0] == 3) { progressThree = eeprom[5]; progressFour = eeprom[6]; }
        if (eeprom[0] == 4) { progressThree = eeprom[5]; progressFour = eeprom[6]; }
        if (eeprom[0] == 6) { progressThree = eeprom[7]; progressFour = eeprom[8]; }
        if (eeprom[0] == 7) { progressThree = eeprom[12]; progressFour = eeprom[6]; }
        if (eeprom[0] == 8) { progressThree = eeprom[13]; progressFour = eeprom[14]; }
        seekBarOne.setProgress(eeprom[1]);
        seekBarTwo.setProgress(eeprom[2]);
        if (eeprom[0] != 5) seekBarThree.setProgress(progressThree); //Защита от изменений, есди режим пятый
        if (eeprom[0] != 0 && eeprom[0] != 5) seekBarFour.setProgress(progressFour);
        logTextRight.setText(String.valueOf(eeprom[0] + "." + eeprom[1] + "." + eeprom[2] + "." + eeprom[3] + "." + eeprom[4] + "." + eeprom[5] + "." + eeprom[6] + "." + eeprom[7] + "." + eeprom[8] + "." + eeprom[9] + "." + eeprom[10] + "." + eeprom[11] + "." + eeprom[12] + "." + eeprom[13] + "." + eeprom[14]));

    }

    //Метод активации настройки шумов
    public void setNoiseLevel() {
        settings[0] = noiseSet;
        settings[1] = 1; //При передаче единицы, метод активируется
        if (connectionEstablished) {
            Toast.makeText(getActivity(), "Настройка шумов", Toast.LENGTH_SHORT).show();
            updateText();
        } else  {
            Toast.makeText(getActivity(), "Нет соединения.", Toast.LENGTH_SHORT).show();
        }
    }

    //Метод сброса подключения и попытки соединения
    public void resetConnection() {
        if (connectionEstablished) onPause(); else onResume();
        //request();
        Toast.makeText(getContext(), "Попытка подключения", Toast.LENGTH_SHORT).show();
    }

    private void disableUI(){
        textNote.setVisibility(View.VISIBLE);
        spinner.setEnabled(false);
        spinnerPreModes.setEnabled(false);
        seekBarOne.setEnabled(false);
        seekBarTwo.setEnabled(false);
        seekBarThree.setEnabled(false);
        seekBarFour.setEnabled(false);
        buttonOnOff.setEnabled(false);
    }

    private void enableUI(){
        textNote.setVisibility(View.INVISIBLE);
        spinner.setEnabled(true);
        spinnerPreModes.setEnabled(true);
        seekBarOne.setEnabled(true);
        seekBarTwo.setEnabled(true);
        seekBarThree.setEnabled(true);
        seekBarFour.setEnabled(true);
        buttonOnOff.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        Toast.makeText(getActivity(), "Восстанавливаем соединение", Toast.LENGTH_SHORT).show();
        getSettings();
        //Log.d(TAG, "...onResume - попытка соединения...");
        // Set up a pointer to the remote node using it's address.
        if (debug == false) disableUI();
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) {
            errorExit("Критическая ошибка", "В методе onResume() не удалось открыть сокет: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        //Log.d(TAG, "...Соединяемся...");
        try {
            btSocket.connect();
            //Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
            Toast.makeText(getActivity(), "Соединение установлено", Toast.LENGTH_SHORT).show();
            enableUI();
            connectionEstablished = true;
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Критическая ошибка", "В методе onResume() не удалось закрыть сокет во  время установки соединения" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        //Log.d(TAG, "...Создание Socket...");

        try {
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
            errorExit("Критическая ошибка", "В методе onResume() не удалось создать выходной поток:" + e.getMessage() + ".");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        disableUI();
        //Log.d(TAG, "...In onPause()...");

        //if (outStream != null) {
        //   try {
        //        outStream.flush();
        //    } catch (IOException e) {
        //        errorExit("Критическая ошибка", "В методе onPause() не удалось сбросить буфер потока: " + e.getMessage() + ".");
         //   }
        //}

        //Сохранение данных
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_MODE, eeprom[0]);
        editor.putInt(APP_PREFERENCES_BRIGHTNESS, eeprom[1]);
        editor.putInt(APP_PREFERENCES_EMPTYBRIGHT, eeprom[2]);
        editor.putInt(APP_PREFERENCES_SMOOTH, eeprom[3]);
        editor.putInt(APP_PREFERENCES_RAINBOWSTEP, eeprom[4]);
        editor.putInt(APP_PREFERENCES_SMOOTHFREQ, eeprom[5]);
        editor.putInt(APP_PREFERENCES_MAXCOEFFREQ, eeprom[6]);
        editor.putInt(APP_PREFERENCES_LIGHTCOLOR, eeprom[7]);
        editor.putInt(APP_PREFERENCES_LIGHTSAT, eeprom[8]);
        editor.putInt(APP_PREFERENCES_COLORSPEED, eeprom[9]);
        editor.putInt(APP_PREFERENCES_RAINBOWPERIOD, eeprom[10]);
        editor.putInt(APP_PREFERENCES_RAINBOWSTEP2, eeprom[11]);
        editor.putInt(APP_PREFERENCES_RUNNINGSPEED, eeprom[12]);
        editor.putInt(APP_PREFERENCES_HUESTEP, eeprom[13]);
        editor.putInt(APP_PREFERENCES_HUESTART, eeprom[14]);
        editor.apply();

        try     {
            btSocket.close();
            connectionEstablished = false;
        } catch (IOException e2) {
            errorExit("Критическая ошибка", "В методе onPause() не удалось закрыть сокет." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth не поддерживается");
        } else {
            if (btAdapter.isEnabled()) {
                //Log.d(TAG, "...Bluetooth включен...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        //finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        //Log.d(TAG, "...Посылаем данные: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "При отправке данных возникла ошибка: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nВ переменной address у вас прописан 00:00:00:00:00:00, вам необходимо прописать реальный MAC-адрес Bluetooth модуля";
            msg = msg +  ".\n\nПроверьте поддержку SPP UUID: " + MY_UUID.toString() + " на Bluetooth модуле, к которому вы подключаетесь.\n\n";

            errorExit("Fatal Error", msg);
        }
    }

    private void sendDataByte(byte[] msgBuffer) {
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "При отправке данных возникла ошибка: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nВ переменной address у вас прописан 00:00:00:00:00:00, вам необходимо прописать реальный MAC-адрес Bluetooth модуля";
            msg = msg +  ".\n\nПроверьте поддержку SPP UUID: " + MY_UUID.toString() + " на Bluetooth модуле, к которому вы подключаетесь.\n\n";

            errorExit("Критическая ошибка", msg);
        }
    }

    private byte[] readDataByte() {
        byte[] dataread = new byte[32];
        try {
            //byte[] dataread = new byte[8];
            int i = 0;
            int count = inStream.available();
            while (inStream.available() <= 7) {
                count++;
                if (count > 1000000)
                    break;
            }
            count = inStream.available();
            while (inStream.available() > 0) {
                dataread[i] = (byte)inStream.read();
                i++;
                if (i >= 32 )
                    break;
            }
            return dataread;
        } catch (IOException e) {
            String msg = "При получении данных возникла ошибка: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nВ переменной address у вас прописан 00:00:00:00:00:00, вам необходимо прописать реальный MAC-адрес Bluetooth модуля";
            msg = msg +  ".\n\nПроверьте поддержку SPP UUID: " + MY_UUID.toString() + " на Bluetooth модуле, к которому вы подключаетесь.\n\n";
            errorExit("Критическая ошибка", msg);
        }
        return dataread;
    }

    //Изменяет цвет элемента в соответствии с палитрой CHSV
    private void previewColorCHSV(int progress){
        imgColor.setVisibility(View.VISIBLE);
        chsv = map(progress, 0,100,0,255,false);
        colorRed = Math.round(chsvRed(chsv));
        colorGreen = Math.round(chsvGreen(chsv));
        colorBlue = Math.round(chsvBlue(chsv));
        String rs = Integer.toHexString(colorRed);
        String gs = Integer.toHexString(colorGreen);
        String bs = Integer.toHexString(colorBlue);
        if (rs.length() == 1) rs = "0" + rs;
        if (gs.length() == 1) gs = "0" + gs;
        if (bs.length() == 1) bs = "0" + bs;
        String col = "#" + rs + gs + bs;
        imgColor.setColorFilter(Color.parseColor(col));
        logColor.setText(progress + " chsv(" + Math.round(chsv) + ") RGB(" + " " + colorRed + ", " + colorGreen + ", " + colorBlue + " )");
    }
    //Метод конвертирует диапазон значений из одного в другой. Например число 50 в диапазоне (0-100) равно 127 в диапазоне (0-255)
    //Если нужно перевернуть значение есть флаг true. Нпример за 30% нужно получить 70%
    private static float map(float val, float in_min, float in_max, float out_min, float out_max, boolean invert) {

//        float value = (val - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
//        if (invert) {
//            if (out_min < 0) value = value * (-1); else value = (value - out_max) * (-1);
//        }
        float percentINrange = Math.abs(in_max - in_min) / 100;
        float percentOUTrange = Math.abs(out_max - out_min) / 100;

        float value = ((val - in_min) / percentINrange) * percentOUTrange;
        if (invert) value = out_max - value; else value = out_min + value;
        return constrain(value, out_min, out_max);
    }
    //Метод проверяет находится ли значение в указанном диапазоне
    private static float constrain(float value, float out_min, float out_max) {
        if (value > out_min && value < out_max) return value;
        if (value < out_min) return out_min;
        if (value > out_max) return out_max;
        return value;
    }
    //Метод возвращает значение зелёного цвета в соответствии с палитрой HSV от 0-255, принимая от 0 до 255
    private static float chsvGreen(float value) {
        if (value <= 96) return map(value, 0, 96, 0,255, false);
        if (value > 96 && value <= 128) return map(value, 97, 128, 169,255, true);
        if (value > 128 && value <= 160) return map(value, 129, 160, 0,169, true);
        return 0;
    }
    //Метод возвращает значение красного цвета в соответствии с палитрой HSV от 0-255, принимая от 0 до 255
    private static float chsvRed(float value) {
        if (value <= 32) return map(value, 0, 32, 169,255, true);
        if (value > 32 && value <= 64) return 169;
        if (value > 64 && value <= 96) return map(value, 65, 96, 0,169, true);
        if (value >= 160 && value <=255) return map(value, 160, 255, 0,255, false);
        return 0;
    }
    //Метод возвращает значение синего цвета в соответствии с палитрой HSV от 0-255, принимая от 0 до 255
    private static float chsvBlue(float value) {
        if (value >= 96 && value <= 128) return map(value, 96, 128, 0,76, false);
        if (value > 128 && value <= 160) return map(value, 129, 160, 76,255, false);
        if (value > 160 && value <= 255) return map(value, 160, 255, 0,255, true);
        return 0;
    }
}