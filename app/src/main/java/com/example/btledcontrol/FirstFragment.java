package com.example.btledcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class FirstFragment extends Fragment {
    private static final String TAG = "bluetooth1";

    Button btnOn, btnOff;

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*btnOn = (Button) view.findViewById(R.id.btn0).setOnClickListener();
        btnOff = (Button) view.findViewById(R.id.btnDown);
        btnOff = (Button) view.findViewById(R.id.btnDown);
        btnOff = (Button) view.findViewById(R.id.btnDown);
        btnOff = (Button) view.findViewById(R.id.btnDown);
        btnOff = (Button) view.findViewById(R.id.btnDown);
        btnOff = (Button) view.findViewById(R.id.btnDown);
        btnOff = (Button) view.findViewById(R.id.btnDown);*/

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        view.findViewById(R.id.btn0).setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                sendData("0");
                Toast.makeText(v.getContext(), "Включаем LED", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("1");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("2");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("3");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("4");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("5");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("6");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("7");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("8");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("9");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnDown).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("A");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnUp).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("B");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("C");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnRight).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("D");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("E");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnStar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("F");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnHash).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("G");
                Toast.makeText(v.getContext(), "Выключаем LED", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnRef).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPause();
                onResume();
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
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nВ переменной address у вас прописан 00:00:00:00:00:00, вам необходимо прописать реальный MAC-адрес Bluetooth модуля";
            msg = msg +  ".\n\nПроверьте поддержку SPP UUID: " + MY_UUID.toString() + " на Bluetooth модуле, к которому вы подключаетесь.\n\n";

            errorExit("Fatal Error", msg);
        }
    }
}