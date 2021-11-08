package com.android.crimsonalert.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.android.crimsonalert.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class LocationService extends Service {
public LocationService(){}

    String latitude, longitude;
    FirebaseAuth auth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestLocationUpdates();
       // buildNotification();
    }

    private void buildNotification() {

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))

                .setOngoing(true)
                .setContentIntent(broadcastIntent);

        startForeground(1, builder.build());
    }

    private void requestLocationUpdates() {
        LocationRequest request = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(1000);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    auth = FirebaseAuth.getInstance();
                    // user = auth.getCurrentUser();

                    if(auth.getUid() != null) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(auth.getUid());
                        Location location = locationResult.getLastLocation();
                        double lat = locationResult.getLastLocation().getLatitude();
                        double lng = locationResult.getLastLocation().getLongitude();

                        if (location != null) {


//Save the location data to the database//


                            com.android.crimsonalert.models.Location loc = new com.android.crimsonalert.models.Location(latitude,longitude);
                            loc.setLatitude(String.valueOf(locationResult.getLastLocation().getLatitude()));
                            loc.setLongitude(String.valueOf(locationResult.getLastLocation().getLongitude()));
                            //loc.getLocation().setLatitude(String.valueOf(locationResult.getLastLocation().getLatitude()));
                            //loc.getLocation().setLongitude(String.valueOf(locationResult.getLastLocation().getLongitude()));
                            ref.child("location").setValue(loc);
                            updateSharedPref(loc);


                        }
                    }
                }



            }, null);
        }
    
    }

    private void updateSharedPref(com.android.crimsonalert.models.Location loc) {
        SharedPreferences sharedPref = getSharedPreferences("LOGGED_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loc);
        editor.putString("current_user", json);
        editor.apply();
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
