package com.OrxtraDev.TitoApp.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.OrxtraDev.TitoApp.MainActivity;
import com.OrxtraDev.TitoApp.OrderAcceptActivity;
import com.OrxtraDev.TitoApp.R;
import com.OrxtraDev.TitoApp.util.SharedPrefDueDate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


public class Splash extends AppCompatActivity {

    SharedPrefDueDate pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        pref =  new SharedPrefDueDate(this);







        if (!pref.getOrderId().isEmpty()){
            Intent intent = new Intent(Splash.this, OrderAcceptActivity.class);
            intent.putExtra("order", pref.getOrderId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        if (pref.getLang() != null && !pref.getLang().isEmpty()){
            String lang = pref.getLang();
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }


        Thread thread = new Thread(){
            @Override
            public void run() {
                try {

                    sleep(2000);
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();

                    //Toast.makeText(getContext(),"waked",Toast.LENGTH_SHORT).show();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        thread.start();
    }
}
