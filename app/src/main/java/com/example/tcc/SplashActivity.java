package com.example.tcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity  extends AppCompatActivity {

    private static int SPLASH_SCREEN= 3000;

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;



    @Override
    protected void onCreate(Bundle savedInstanceState){
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_splash);

     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

     topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
     bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anima);

     image = findViewById(R.id.imageView4);
     logo = findViewById(R.id.textView16);
     slogan = findViewById(R.id.textView17);

     image.setAnimation(topAnim);
     logo.setAnimation(bottomAnim);
     slogan.setAnimation(bottomAnim);

     new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
             Intent intent = new Intent(SplashActivity.this, OnBoarding.class);
             startActivity(intent);
             finish();

         }
     }, SPLASH_SCREEN);



    }
}
