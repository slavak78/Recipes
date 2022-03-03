package ru.slava.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.firebase.messaging.FirebaseMessaging;

public class SActivity extends AppCompatActivity {
    String APP_PREFERENCES_PHONE = "phone";
    String APP_PREFERENCES_TOK = "tok";
    boolean isReady = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    SecureSharedPreferences mSettings = new SecureSharedPreferences(SActivity.this);
                    SecureSharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREFERENCES_TOK, token);
                    editor.apply();
                });

        Runnable doit = () -> isReady = true;
        Handler mHandler = new Handler(Looper.getMainLooper());
       mHandler.postDelayed(doit, 2000);

       final View content = findViewById(android.R.id.content);
       content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
           @Override
           public boolean onPreDraw() {
               if(isReady) {
                   content.getViewTreeObserver().removeOnPreDrawListener(this);
                   Intent intent;
                   SecureSharedPreferences mSettings = new SecureSharedPreferences(SActivity.this);
                   //       SecureSharedPreferences.Editor editor = mSettings.edit();
                   //     editor.putString(APP_PREFERENCES_PHONE, "79003808848");
                   if(mSettings.contains(APP_PREFERENCES_PHONE)) {
                       intent = new Intent(SActivity.this, GlavActivity.class);
                   } else {
                       intent = new Intent(SActivity.this, MainActivity.class);
                   }
                   startActivity(intent);
                   finish();
                   return true;
               } else {
                   return false;
               }
           }
       });
    }
 }




