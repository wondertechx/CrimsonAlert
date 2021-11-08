package com.android.crimsonalert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crimsonalert.R;
import com.android.crimsonalert.models.users;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class OtpVerificationActivity extends AppCompatActivity {
    private EditText otp;
    private TextView otpNumber, resend;
    private View otpBtn;
    private String phoneNumber, Name;
    private String otpId;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private MKLoader loader;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        init();

        phoneNumber=getIntent().getStringExtra("mobile").toString();
        Name = getIntent().getStringExtra("name").toString();

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
                    YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(otp);
                    Toasty.error(getApplicationContext(), "Please Enter OTP!", Toasty.LENGTH_LONG, true).show();
                }else if (otp.getText().toString().length()!=6) {
                    Toasty.error(getApplicationContext(), "Invalid OTP!", Toasty.LENGTH_LONG, true).show();
                    YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(otp);
                }else{
                    loader.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId,otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }

        });


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
                                Toast.makeText(OtpVerificationActivity.this, "OTP failed, try again", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                    OtpVerificationActivity.this.otpId = verificationId;
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
                            if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                sendUserData();
                                startActivity(new Intent(OtpVerificationActivity.this, SosActivity.class));
                                finish();
                            }
                             else{
                                Toasty.error(getApplicationContext(),"Phone number already exists, please sign in instead", Toasty.LENGTH_LONG,true).show();
                                startActivity(new Intent(OtpVerificationActivity.this, LoginActivity.class));
                                finish();
                            }

                        } else {
                            Toasty.error(getApplicationContext(),"Invalid OTP!", Toasty.LENGTH_LONG,true).show();
                            YoYo.with(Techniques.Shake).duration(600).repeat(2).playOn(otp);
                        }
                    }
                });
    }

    private void sendUserData(){
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef =firebaseDatabase.getReference(user.getUid());

        Random r =new Random();
        int n = 100000 + r.nextInt(900000);
        String Code = String.valueOf(n);

        users user = new users(Name,phoneNumber,userId,Code);
        userRef.setValue(user);

    }

    private void init(){
        otp = findViewById(R.id.otpVerficiationET);
        otpNumber = findViewById(R.id.otpNumberTv);
        resend = findViewById(R.id.otpResend);
        otpBtn = findViewById(R.id.verifyOtp);
        loader = findViewById(R.id.Loader);
        mAuth=FirebaseAuth.getInstance();
    }
}