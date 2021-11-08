package com.android.crimsonalert.activities.onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.crimsonalert.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Onboarding2 extends AppCompatActivity {
    private ImageView upperShape, lowerShape, Illustration, indicator;
    private TextView title, description;
    private View nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding2);

        init();

        YoYo.with(Techniques.SlideInDown).duration(1000).repeat(0).playOn(upperShape);
        YoYo.with(Techniques.SlideInUp).duration(1000).repeat(0).playOn(lowerShape);
        YoYo.with(Techniques.FadeInLeft).duration(1000).repeat(0).playOn(title);
        YoYo.with(Techniques.FadeInRight).duration(1000).repeat(0).playOn(description);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Onboarding2.this, Onboarding3.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        upperShape=findViewById(R.id.onboarding2UpperShape);
        lowerShape=findViewById(R.id.onboarding2LowerShape);
        Illustration=findViewById(R.id.onboarding2Illus);
        indicator=findViewById(R.id.onboarding2Indicator);
        title=findViewById(R.id.onboarding2Title);
        description=findViewById(R.id.onboarding2Desc);
        nextBtn=findViewById(R.id.onboardNext2);
    }
}