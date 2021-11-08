package com.android.crimsonalert.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.crimsonalert.R;
import com.android.crimsonalert.models.Location;
import com.android.crimsonalert.models.users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;
import static com.android.crimsonalert.activities.SosActivity.SHARED_PREFS;
import static com.android.crimsonalert.activities.SosActivity.TEXT;

public class SendSms {

    Context c;
    String latitiude, longitude, name, helperPhone, helperName;

    public SendSms(){}

    public SendSms(Context c) {
        this.c = c;
    }

    public void prepareDistressAlert() {

        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        name = sharedPreferences.getString(TEXT, "");


        final Location locationModel;
        //final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        SharedPreferences sp = c.getSharedPreferences("LOGGED_USER", MODE_PRIVATE);
        String currentUser = sp.getString("current_user", null);

        if (currentUser != null) {


            Gson gson = new Gson();
            locationModel = gson.fromJson(currentUser, Location.class);  // get user's location

            // send distress alerts to all emergency contacts
            ArrayList<users> emergencyContactList;
            gson = new Gson();
            String jsonArrayList = sp.getString("contact_list", null);
            Type type = new TypeToken<ArrayList<users>>() {
            }.getType();
            if (jsonArrayList != null) {
                emergencyContactList = gson.fromJson(jsonArrayList, type);
                for (users contact : emergencyContactList) {
                    send(locationModel, contact);
                }
            }
        }

    }

    public void send(Location lat, users helpingContact) {



        latitiude = lat.getLatitude();
        longitude = lat.getLongitude();
        helperPhone = helpingContact.getPhone();
        helperName = helpingContact.getName();


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(helperPhone, null, "Hey "+helperName +", "+name +" has triggered SOS alert from Crimson Alert app, Last known location: https://www.google.com/maps/search/?api=1&query="+latitiude+","+longitude, null, null);
            Toast.makeText(c, "Message sent to "+helperName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            e.printStackTrace();
          Toast.makeText(c, "sms failed, try again", Toast.LENGTH_LONG).show();
        }


       // String distressedLat = disstressedUser.getLatitude();
      //  String distressedLon = disstressedUser.getLongitude();










    }
}
