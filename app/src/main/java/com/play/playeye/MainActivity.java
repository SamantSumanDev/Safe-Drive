package com.play.playeye;


import static com.play.playeye.Condition.FACE_NOT_FOUND;
import static com.play.playeye.Condition.USER_EYES_CLOSED;
import static com.play.playeye.Condition.USER_EYES_OPEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    LinearLayout background, drive_bg, stop_alarm, restartAlarm;

    ConstraintLayout actionbar_bg;

    boolean flag = false;
    CameraSource cameraSource;
    MediaPlayer m;

    private Vibrator vibrator;

    ImageView drive_img_status;
    TextView drive_txt_status;

    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m.pause();

                MainActivity.this.finish();

            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Permission not granted!\n Grant permission and restart app", Toast.LENGTH_SHORT).show();
        } else {
            init();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        m = MediaPlayer.create(this, R.raw.squad);
        getSupportActionBar().hide();

    }

    private void init() {
        background = findViewById(R.id.background);

        drive_img_status = findViewById(R.id.drive_img_status);
        drive_txt_status = findViewById(R.id.drive_txt_status);
        actionbar_bg = findViewById(R.id.actionbar_bg);
        drive_bg = findViewById(R.id.drive_bg);
        stop_alarm = findViewById(R.id.stop_alarm);
        restartAlarm = findViewById(R.id.restartAlarm);

        flag = true;
        initCameraSource();

    }


    private void initCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerDaemon(MainActivity.this)).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            cameraSource.start();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                cameraSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        m.pause();

        if (cameraSource != null) {
            cameraSource.stop();
        }
        setBackgroundWhite();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m.pause();

        if (cameraSource != null) {
            cameraSource.release();
        }

    }


    public void updateMainView(Condition condition) {

        switch (condition) {
            case USER_EYES_OPEN:
                setBackgroundGray();
                drive_img_status.setImageResource(R.drawable.drive_car);
                drive_txt_status.setText("Online");
                stop_alarm.setBackgroundColor(getResources().getColor(R.color.white));

                break;

            case USER_EYES_CLOSED:
                setBackgroundGray();
                drive_img_status.setImageResource(R.drawable.drive_car);
                drive_txt_status.setText("Online");
                stop_alarm.setBackgroundColor(getResources().getColor(R.color.white));

                break;

            case FACE_NOT_FOUND:
                setBackgroundRed();
                drive_img_status.setImageResource(R.drawable.drive_sleep);
                drive_txt_status.setText("Offline");

                m.start();
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(2000);


                break;
            default:
                setBackgroundWhite();
                drive_img_status.setImageResource(R.drawable.face_scan);
                drive_txt_status.setText("Look At Me");
        }


        stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop_alarm.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                m.pause();
                if (cameraSource != null) {
                    cameraSource.stop();
                }
                setBackgroundWhite();
                vibrator.cancel();
            }
        });

    }

    private void setBackgroundWhite() {
        if (background != null)
            for (long i = 0; i < 1528; i++) {
                System.out.println(i);
                if (1527 == i) {

                    background.setBackgroundColor(getResources().getColor(R.color.white));
                    actionbar_bg.setBackgroundColor(getResources().getColor(R.color.white));
                    drive_bg.setBackgroundColor(getResources().getColor(R.color.white));

                }
            }


    }


    private void setBackgroundGray() {
        if (background != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    background.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                    actionbar_bg.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    drive_bg.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                }
            });
        }
    }


    private void setBackgroundRed() {
        if (background != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    background.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

                    actionbar_bg.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    drive_bg.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));


                }
            });
        }
    }
}