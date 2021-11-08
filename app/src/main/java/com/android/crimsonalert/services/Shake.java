package com.android.crimsonalert.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


import com.android.crimsonalert.Utils.SendSms;
import com.android.crimsonalert.Utils.ShakeDetector;

import es.dmoral.toasty.Toasty;

public class Shake extends Service {

    private SensorManager mSensorManager;
    private ShakeDetector mShakeDetector;


    public Shake() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();

        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (count == 3) {
                    SendSms sendSms = new SendSms();
                    sendSms.prepareDistressAlert();
                }

            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy");
        //mSensorManager.unregisterListener(mShakeDetector);
        super.onDestroy();
    }
    public int onStartCommand (Intent intent,int flags, int startId){
        return START_STICKY;
    }
}
