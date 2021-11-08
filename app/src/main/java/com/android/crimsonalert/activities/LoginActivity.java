package com.android.crimsonalert.activities;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.android.crimsonalert.R;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {
    private TextView phoneTv, register;
    private CountryCodePicker loginCountryCode;
    private EditText phone;
    private View loginBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(LoginActivity.this, SosActivity.class));

        }


       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
               startActivity(intent);
           }
       });

       loginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (TextUtils.isEmpty(phone.getText().toString())){
                   phoneTv.setTextColor(getResources().getColor(R.color.secondary));
                   phone.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                   YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(phone);
                   Toasty.error(getApplicationContext(),"Please Enter phone number!", Toasty.LENGTH_SHORT,true).show();
               } else if (phone.getText().toString().replace(" ", "").length()!=10){
                   phoneTv.setTextColor(getResources().getColor(R.color.secondary));
                   phone.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                   YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(phone);
                   Toasty.error(getApplicationContext(),"Please Enter Valid phone number!", Toasty.LENGTH_LONG,true).show();
               } else{
                   Intent intent = new Intent(LoginActivity.this, SigninVerification.class);
                   intent.putExtra("mobileNo", loginCountryCode.getFullNumberWithPlus().replace(" ", ""));
                   startActivity(intent);
                   finish();
               }
           }
       });
    }
    private void init(){
        phoneTv = findViewById(R.id.loginTv);
        register = findViewById(R.id.register);
        loginCountryCode = findViewById(R.id.countryPickLogin);
        phone = findViewById(R.id.loginPhoneEt);
        loginCountryCode.registerCarrierNumberEditText(phone);
        loginBtn = findViewById(R.id.signinBtn);
        mAuth = FirebaseAuth.getInstance();
    }
}