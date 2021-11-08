package com.android.crimsonalert.activities.onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.crimsonalert.R;
import com.android.crimsonalert.Utils.FirstTimeCheck;
import com.android.crimsonalert.activities.LoginActivity;
import com.android.crimsonalert.activities.SignupActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Onboarding1 extends AppCompatActivity {
    private ImageView upperShape, lowerShape, Illustration, indicator;
    private TextView title, description;
    private View nextBtn;
    private FirstTimeCheck firstTimeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstTimeCheck = new FirstTimeCheck(this);
        if (!firstTimeCheck.Check()){
            firstTimeCheck.setFirst(false);
            Intent intent = new Intent(Onboarding1.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_onboarding1);

        init();

        YoYo.with(Techniques.SlideInDown).duration(2000).repeat(0).playOn(upperShape);
        YoYo.with(Techniques.SlideInUp).duration(2000).repeat(0).playOn(lowerShape);
        YoYo.with(Techniques.FadeInLeft).duration(2000).repeat(0).playOn(title);
        YoYo.with(Techniques.FadeInRight).duration(2000).repeat(0).playOn(description);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Onboarding1.this, Onboarding2.class);
                startActivity(intent);
            }
        });
    }
    private void init(){
        upperShape=findViewById(R.id.onboarding1UpperShape);
        lowerShape=findViewById(R.id.onboarding1LowerShape);
        Illustration=findViewById(R.id.onboarding1Illus);
        indicator=findViewById(R.id.onboarding1Indicator);
        title=findViewById(R.id.onboarding1Title);
        description=findViewById(R.id.onboarding1Desc);
        nextBtn=findViewById(R.id.onboardNext);
    }
}