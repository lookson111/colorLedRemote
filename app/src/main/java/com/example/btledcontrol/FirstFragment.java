package com.example.btledcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class FirstFragment extends Fragment {
    private static final String TAG = "bluetooth1";

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private static byte[] settings =  new byte[2];
    private static byte[] dimmer =  new byte[5];
    boolean preModeActive = false;
    TextView logTextLeft;
    TextView logTextRight;


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
    public void request(){
        settings[0] = 10;
        sendDataByte(settings);

        settings = readDataByte();
        logTextRight.setText(String.valueOf(settings[0] + "." + settings[1]));
        //    }
        //}
    }

    public void updateText()
    {
        logTextLeft.setText(String.valueOf(settings[0] + "." + settings[1]));
        sendDataByte(settings);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        String[] data = {"Обычный", "Радуга спектр", "5 полос", "3 полосы", "Выделение частот", "Огонь", "Радуга", "Бегущие частоты", "Анализатор спектра"};
        String[] preModesOne = {"Все частоты", "Только низкие", "Только средние", "Только высокие"};
        String[] preModesThree = {"Постоянный", "Плавная смена цвета", "Радуга"};

        //Создаём экземпляры диммеров и текстбоксов
        final SeekBar seekBarOne = view.findViewById(R.id.seekParOne);
        final SeekBar seekBarTwo = view.findViewById(R.id.seekParTwo);
        final SeekBar seekBarThree = view.findViewById(R.id.seekParThree);
        final SeekBar seekBarFour = view.findViewById(R.id.seekParFour);
        TextView textView = view.findViewById(R.id.textViewConsole);
        TextView textView2 = view.findViewById(R.id.textViewConsole2);
        TextView textSeekBarOne = view.findViewById(R.id.textSeekOne);
        TextView textSeekBarTwo = view.findViewById(R.id.textSeekTwo);
        TextView textSeekBarThree = view.findViewById(R.id.textSeekThree);
        TextView textSeekBarFour = view.findViewById(R.id.textSeekFour);

        logTextLeft = textView;
        logTextRight = textView2;
        //Изменение первого диммера
        seekBarOne.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0]= dimmer[1];
                settings[1]= (byte)progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText();
            }
        });
        //Изменение второго диммера
        seekBarTwo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0]= dimmer[2];
                settings[1]= (byte)progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText();
            }
        });
        //Изменение третьего диммера
        seekBarThree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0]= dimmer[3];
                settings[1]= (byte)progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText();
            }
        });
        //Изменение четвёртого диммера
        seekBarFour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings[0]= dimmer[4];
                settings[1]= (byte)progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText();
            }
        });

        // адаптер для списка режимов и под-режимов
        ArrayAdapter<String> adapterModes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, data);
        ArrayAdapter<String> adapterPreModesOne = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, preModesOne);
        ArrayAdapter<String> adapterPreModesThree = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, preModesThree);
        adapterModes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPreModesOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPreModesThree.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Выпадающий список основных режимов
        Spinner spinner = view.findViewById(R.id.listmode);
        //Выпадающий список под-режимов
        Spinner spinnerPreModes = view.findViewById(R.id.listPreMode);
        spinner.setAdapter(adapterModes);
        // устанавливаем обработчик нажатия списка режимов
        // Адреса параметров
        byte onoff = 1;              //1.x Включение отключение
        byte noiseSet = 2;           //2.x Настройка шумов
        byte mode = 3;               //3.x Режим
        byte preMode = 4;            //4.x Под режим
        byte brightness = 5;         //5.x Яркость горящих светодиодов
        byte emptyBright = 6;        //6.x Яркость негорящих светодиодов
        byte smooth = 7;             //7.x Плавность спектра (SMOOTH, режим 1,2)
        byte rainbowStep = 8;        //8.x Скорость движения (RAINBOW_STEP режим 2)
        byte smoothFreq = 9;         //9.x Плавность включения (SMOOTH_FREQ режим 3,4,5)
        byte maxCoefFreq = 10;       //10.x Чувствительность (MAX_COEF_FREQ режим 3,4,5,8)
        byte lightColor = 11;        //11.x Настройка цвета (LIGHT_COLOR режим 7)
        byte lightSat = 12;          //12.x Натройка насыщенности (LIGHT_SAT режим 7)
        byte colorSpeed = 13;        //13.x Скорость изменения (COLOR_SPEED режим 7)
        byte rainbowPeriod = 14;     //14.x Скорость движения (RAINBOW_PERIOD режим 7)
        byte rainbowStepTwo = 15;    //15.x Шаг цвета (RAINBOW_STEP_2 режим 7)
        byte runningSpeed = 16;      //16.x Скорость движения (RUNNING_SPEED режим 8)
        byte hueStep = 17;           //17.x Шаг изменения цвета (HUE_STEP режим 9)
        byte hueStart = 18;          //18.x Цвет (HUE_START режим 9)

        dimmer[1] = brightness;
        dimmer[2] = emptyBright;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                //String item = (String)parent.getItemAtPosition(position);
                settings[0] = mode;
                settings[1]= (byte)position;
                preModeActive = false;
                switch (position){
                    case 0: //Обычный
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(false);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность спектра");
                        spinnerPreModes.setEnabled(false);
                        dimmer[3] = smooth;
                        break;
                    case 1: //Радуга спектр
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(true);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность спектра");
                        textSeekBarFour.setText("Скорость движения радуги");
                        spinnerPreModes.setEnabled(false);
                        dimmer[3] = smooth;
                        dimmer[4] = rainbowStep;
                        break;
                    case 2: //5 полос
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(true);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность включения");
                        textSeekBarFour.setText("Чувствительность");
                        spinnerPreModes.setEnabled(false);
                        dimmer[3] = smoothFreq;
                        dimmer[4] = maxCoefFreq;
                        break;
                    case 3: //3 полосы
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(true);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность анимации");
                        textSeekBarFour.setText("Чувствительность");
                        spinnerPreModes.setEnabled(false);
                        dimmer[3] = smoothFreq;
                        dimmer[4] = maxCoefFreq;
                        break;
                    case 4: //Выделение частот
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(true);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Плавность анимации");
                        textSeekBarFour.setText("Чувствительность");
                        spinnerPreModes.setEnabled(true);
                        spinnerPreModes.setAdapter(adapterPreModesOne);
                        dimmer[3] = smoothFreq;
                        dimmer[4] = maxCoefFreq;
                        break;
                    case 5: //Огонь
                        seekBarOne.setEnabled(false);
                        seekBarTwo.setEnabled(false);
                        seekBarThree.setEnabled(false);
                        seekBarFour.setEnabled(false);
                        spinnerPreModes.setEnabled(false);
                        break;
                    case 6: //Радуга
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(true);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Настройка цвета");
                        textSeekBarFour.setText("Настройка насыщенности");
                        spinnerPreModes.setEnabled(true);
                        spinnerPreModes.setAdapter(adapterPreModesThree);
                        dimmer[3] = lightColor;
                        dimmer[4] = lightSat;
                        break;
                    case 7: //Бегущие частоты
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(true);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Скорость");
                        textSeekBarFour.setText("Чувствительность");
                        spinnerPreModes.setEnabled(true);
                        spinnerPreModes.setAdapter(adapterPreModesOne);
                        dimmer[3] = runningSpeed;
                        dimmer[4] = maxCoefFreq;

                        break;
                    case 8: //Анализатор спектра
                        seekBarOne.setEnabled(true);
                        seekBarTwo.setEnabled(true);
                        seekBarThree.setEnabled(true);
                        seekBarFour.setEnabled(true);
                        textSeekBarOne.setText("Яркость горящих светодиодов");
                        textSeekBarTwo.setText("Яркость негорящих светодиодов");
                        textSeekBarThree.setText("Шаг изменения цвета");
                        textSeekBarFour.setText("Цвет");
                        spinnerPreModes.setEnabled(false);
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

        view.findViewById(R.id.btn0).setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                settings[0] = noiseSet;
                settings[1] = 1;
                updateText();
                Toast.makeText(v.getContext(), "Настройка шумов", Toast.LENGTH_SHORT).show();
            }
        });


        view.findViewById(R.id.btnStar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                settings[0] = onoff;
                settings[1] = 1;
                updateText();
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btnRef).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //onPause();
                //onResume();
                request();
                Toast.makeText(v.getContext(), "Подключение к устройству", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //Log.d(TAG, "...onResume - попытка соединения...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        //Log.d(TAG, "...Соединяемся...");
        try {
            btSocket.connect();
            //Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        //Log.d(TAG, "...Создание Socket...");

        try {
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
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
}