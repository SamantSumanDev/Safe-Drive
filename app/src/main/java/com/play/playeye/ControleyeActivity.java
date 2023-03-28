package com.play.playeye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class ControleyeActivity extends AppCompatActivity {

    CardView start_driving;
    LinearLayout settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controleye);


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());


            }
        });
        start_driving = findViewById(R.id.start_driving);

        start_driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                start_driving.startAnimation(rotate);
                Intent intent = new Intent(ControleyeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}