package com.example.fallstudie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class StartBildschirmActivity extends AppCompatActivity {

    public static int Start = 4000;
    Animation animation;
    private ImageView appIcon;
    private TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startbildschirm);

        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        //Variablen initialisieren
        appIcon = findViewById(R.id.geldbeutel);
        appName = findViewById(R.id.nameApp);
        appIcon.setAnimation(animation);
        appName.setAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartBildschirmActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },Start);
    }
}