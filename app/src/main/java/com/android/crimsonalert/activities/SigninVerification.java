package com.android.crimsonalert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.crimsonalert.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.concurrent.TimeUnit;

public class SigninVerification extends AppCompatActivity {
    private EditText otp;
    private TextView otpNumber, resend;
    private View otpBtn;
    private String phoneNumber;
    private String otpId;
    private FirebaseAuth mAuth;
    private MKLoader loader;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_verification);

        init();

        phoneNumber=getIntent().getStringExtra("mobileNo").toString();
        otpNumber.setText(phoneNumber);

        initiateOtp();

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateOtp();
            }
        });
        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(otp.getText().toString())) {
                    otp.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                    YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(otp);
                    Toasty.error(getApplicationContext(), "Please Enter OTP!", Toasty.LENGTH_LONG, true).show();
                }else if (otp.getText().toString().length()!=6) {
                    otp.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                    YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(otp);
                    Toasty.error(getApplicationContext(), "Invalid OTP!", Toasty.LENGTH_LONG, true).show();
                }else{
                    loader.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId,otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }

        });
    }
    private void init(){
        otp = findViewById(R.id.signInOtpEt);
        otpNumber = findViewById(R.id.signInOtpTv);
        resend = findViewById(R.id.signInResendTv);
        otpBtn = findViewById(R.id.signInVerifyBtn);
        loader = findViewById(R.id.signinLoader);
        mAuth=FirebaseAuth.getInstance();
    }
    private void initiateOtp() {

        new CountDownTimer(60000,1000){
            @Override
            public void onTick(long l) {
                resend.setText(""+l/1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText("Resend");
                resend.setEnabled(true);
            }
        }.start();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {


                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                otp.getBackground().setColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP);
                                YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(otp);
                                Toast.makeText(SigninVerification.this, "OTP failed, try again", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                SigninVerification.this.otpId = verificationId;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                mAuth.getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID);
                                Toasty.error(getApplicationContext(),"Please Register first to continue!", Toasty.LENGTH_LONG,true).show();
                                Runnable r = new Runnable() {

                                    @Override
                                    public void run() {

                                        startActivity(new Intent(SigninVerification.this, SignupActivity.class));

                                    }
                                };

                                Handler h = new Handler();

                                h.postDelayed(r, 2000);
                            } else {
                                startActivity(new Intent(SigninVerification.this, SosActivity.class));
                                finish();
                            }

                        } else {
                            Toasty.error(getApplicationContext(),"Invalid OTP!", Toasty.LENGTH_LONG,true).show();
                        }
                    }
                });
    }

}