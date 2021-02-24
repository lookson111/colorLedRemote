package com.example.btledcontrol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

public class VibrateService extends Service {
    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);

        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        //Указываем длительность вибрации в миллисекундах,
        //в нашем примере будет вибро-сигнал длительностью в 2 секунды
        vibrator.vibrate(100);

        Toast.makeText(getApplicationContext(), "Вибрация активирована", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
