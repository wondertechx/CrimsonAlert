package com.android.crimsonalert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;



import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.provider.Settings;
import android.view.MenuItem;

import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.android.crimsonalert.R;
import com.android.crimsonalert.Utils.SendSms;
import com.android.crimsonalert.models.users;
import com.android.crimsonalert.services.LocationService;
import com.android.crimsonalert.services.Shake;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Objects;

import static com.android.crimsonalert.activities.EmergencyCallActivity.CALL_PREFS;



public class SosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String phoneNumber = "phone";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private ImageView SosButton, star1,star2,star3,star4;
    private TextView sosText;
    private TextView sosAlert;
    private String name;
    private Button cancel;
    private String callNumber;
    private String widgetString;


    private FirebaseDatabase firebaseDatabase;
    Gson gson;
    private SwipeButton swipeButton;

    CountDownTimer sosTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.menuham);
        SosButton = findViewById(R.id.sosButton);
        star1 = findViewById(R.id.starShape);
        star2 = findViewById(R.id.starShape2);
        star3 = findViewById(R.id.starShape3);
        star4 = findViewById(R.id.starShape4);
        sosAlert = findViewById(R.id.sosAlertText);
        sosText=findViewById(R.id.sosText);
        swipeButton = findViewById(R.id.swipeCall);
        cancel = findViewById(R.id.cancelText);

        firebaseAuth = FirebaseAuth.getInstance();


        widgetString = getIntent().getStringExtra("widget");

      if (Objects.equals(widgetString, "fromWidget")){
          prepareSms();
      }

        SharedPreferences sharedPreferences = getSharedPreferences(CALL_PREFS, MODE_PRIVATE);
        callNumber = sharedPreferences.getString(phoneNumber, "");


        isGPSOn();
        permissions();

        startService(new Intent(getApplicationContext(), LocationService.class));
        startService(new Intent(getApplicationContext(), Shake.class));



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users user = snapshot.getValue(users.class);

                name = user.getName();

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT, name);
                editor.apply();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cancel.setOnClickListener(view -> {
            sosTimer.cancel();
            sosAlert.setVisibility(View.INVISIBLE);
            sosText.setText("SOS");
            cancel.setVisibility(View.INVISIBLE);

            Toast.makeText(SosActivity.this, "SOS cancelled", Toast.LENGTH_SHORT).show();

        });



        swipeButton.setOnStateChangeListener(active -> {

            if (callNumber.equals("")) {
                Toast.makeText(SosActivity.this, "Please Save Emergency Call Number first",Toast.LENGTH_LONG).show();

            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + callNumber));
                startActivity(intent);
                Toast.makeText(SosActivity.this, "Calling...", Toast.LENGTH_SHORT).show();
            }
        });



        SosButton.setOnClickListener(view -> {

           prepareSms();
        });


        setSupportActionBar(toolbar);



        firebaseAuth = FirebaseAuth.getInstance();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.emergency_contact);
    }

    private void prepareSms() {
        SharedPreferences sp = getSharedPreferences("LOGGED_USER", MODE_PRIVATE);
        String currentUser = sp.getString("current_user", null);

        ArrayList<users> emergencyContactList;
        gson = new Gson();
        String jsonArrayList = sp.getString("contact_list", null);
        Type type = new TypeToken<ArrayList<users>>() {
        }.getType();

        emergencyContactList = gson.fromJson(jsonArrayList, type);

        if(emergencyContactList.size()==0){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Add Emergency Contact");
            alertDialogBuilder.setMessage("Please Add Emergency contacts first");
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SosActivity.this, EmergencyContactActivity.class);
                            startActivity(intent);

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {

            cancel.setVisibility(View.VISIBLE);
            sosAlert.setVisibility(View.VISIBLE);
            countDown();

            rotateView();
            //YoYo.with(Techniques.RotateIn).duration(3000).repeat(0).playOn(SosButton);

        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.emergency_contact: {
                Intent intent = new Intent(SosActivity.this, EmergencyContactActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.call:{
                Intent intent = new Intent(SosActivity.this, EmergencyCallActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.Profile: {
                Intent intent = new Intent(SosActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.join: {
                Intent intent = new Intent(SosActivity.this, JoinActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.liveLocation: {
                Intent intent = new Intent(SosActivity.this, MyCircleActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.manage: {
                Intent intent = new Intent(SosActivity.this, ManageCircleActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.logout: {
               Logout();
               break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void Logout(){

        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SosActivity.this, LoginActivity.class));
    }

    private void countDown() {

        sosTimer = new CountDownTimer(4000,1000){
            @Override
            public void onTick(long l) {
                sosText.setText(""+l/1000);

            }

            @Override
            public void onFinish() {
                sosAlert.setVisibility(View.INVISIBLE);
                sosText.setText("SOS");
                cancel.setVisibility(View.INVISIBLE);

                SendSms sendSms = new SendSms(getApplicationContext());
                sendSms.prepareDistressAlert();
               // Toasty.success(SosActivity.this, "Alert successfully sent", Toasty.LENGTH_SHORT, true).show();

            }
        }.start();


}

    private void permissions() {
        int Permission_All = 1;
        String[] Permissions = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CALL_PHONE,

                android.Manifest.permission.INTERNET,
              //  Manifest.permission.RECORD_AUDIO,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.FOREGROUND_SERVICE};
               // Manifest.permission.CAMERA,
             //   Manifest.permission.WRITE_EXTERNAL_STORAGE

        if(!hasPermissions(this, Permissions)){
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);

        }
    }

    public static boolean hasPermissions(Context context, String... permissions){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }

    private void rotateView(){
        //animate the views when the sos button is clicked

        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        SosButton.startAnimation(rotate);
        YoYo.with(Techniques.RotateIn).duration(3000).repeat(0).playOn(star1);
        YoYo.with(Techniques.RotateIn).duration(3000).repeat(0).playOn(star2);
        YoYo.with(Techniques.RotateIn).duration(3000).repeat(0).playOn(star3);
        YoYo.with(Techniques.RotateIn).duration(3000).repeat(0).playOn(star4);
    }
    //check if gps is on or not

    private void isGPSOn() {
        boolean check = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            check = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        if (!check) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Turn on location");
            alertDialogBuilder.setMessage("This app require location to be turned on to function, Please enable the location in the settings to continue");
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

    }
}