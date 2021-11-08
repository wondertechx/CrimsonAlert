package com.android.crimsonalert.activities.onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.crimsonalert.R;
import com.android.crimsonalert.Utils.FirstTimeCheck;
import com.android.crimsonalert.activities.EmergencyContactActivity;
import com.android.crimsonalert.activities.LoginActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Onboarding3 extends AppCompatActivity {
    private ImageView upperShape, lowerShape, Illustration, indicator;
    private TextView title, description;
    private View nextBtn;
    private FirstTimeCheck firstTimeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding3);



        init();
        firstTimeCheck = new FirstTimeCheck(Onboarding3.this);

        YoYo.with(Techniques.SlideInDown).duration(1000).repeat(0).playOn(upperShape);
        YoYo.with(Techniques.SlideInUp).duration(1000).repeat(0).playOn(lowerShape);
        YoYo.with(Techniques.FadeInLeft).duration(1000).repeat(0).playOn(title);
        YoYo.with(Techniques.FadeInRight).duration(1000).repeat(0).playOn(description);
        YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(Illustration);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firstTimeCheck.setFirst(false);
                Intent intent = new Intent(Onboarding3.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void init(){
        upperShape=findViewById(R.id.onboarding3UpperShape);
        lowerShape=findViewById(R.id.onboarding3LowerShape);
        Illustration=findViewById(R.id.onboarding3Illus);
        indicator=findViewById(R.id.onboardingIndicator3);
        title=findViewById(R.id.onboarding3Title);
        description=findViewById(R.id.onboarding3Desc);
        nextBtn=findViewById(R.id.onboardNext3);
    }
}