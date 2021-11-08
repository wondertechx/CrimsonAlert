package com.android.crimsonalert.activities;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crimsonalert.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hbb20.CountryCodePicker;

public class SignupActivity extends AppCompatActivity {
    private EditText Name, Phone;
    private View signup;
    private CountryCodePicker countryPick;
    private TextView nameTv, phoneTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

       init();



       signup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (TextUtils.isEmpty(Name.getText().toString())){
                   nameTv.setTextColor(getResources().getColor(R.color.secondary));
                   Name.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                   YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(Name);

               } else if (TextUtils.isEmpty(Phone.getText().toString())){
                   phoneTv.setTextColor(getResources().getColor(R.color.secondary));
                   Phone.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                   YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(Phone);

               } else if
                   (Phone.getText().toString().replace(" ", "").length()!=10){
                   phoneTv.setTextColor(getResources().getColor(R.color.secondary));
                   Phone.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                   YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(Phone);

                   Toasty.error(getApplicationContext(),"Please Enter Valid phone number!", Toasty.LENGTH_LONG,true).show();
               } else {
                   Intent intent = new Intent(SignupActivity.this, OtpVerificationActivity.class);
                   intent.putExtra("mobile", countryPick.getFullNumberWithPlus().replace(" ", ""));
                   intent.putExtra("name", Name.getText().toString());
                   startActivity(intent);
                   finish();
               }
           }
       });
    }
    private void init(){
        Name = findViewById(R.id.signupName);
        Phone = findViewById(R.id.signupPhone);
        signup = findViewById(R.id.signupBtn);
        countryPick = findViewById(R.id.countryPick);
        countryPick.registerCarrierNumberEditText(Phone);
        nameTv = findViewById(R.id.textView16);
        phoneTv = findViewById(R.id.textView17);
    }
}