package com.OrxtraDev.TitoApp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.OrxtraDev.TitoApp.R;
import com.OrxtraDev.TitoApp.util.SharedPrefDueDate;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeLanguage extends AppCompatActivity {

    SharedPrefDueDate pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        ButterKnife.bind(this);
        pref = new SharedPrefDueDate(this);

    }


    @OnClick(R.id.englishBtn)void englishBtn(){
        pref.setLang("en");
        locale();
    }

    @OnClick(R.id.arabicBtn)void arabicBtn(){
        pref.setLang("ar");
        locale();
    }

    private void locale(){
        String lang = pref.getLang();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        Intent intent = new Intent(this,Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @OnClick(R.id.backIV)
    void backIV() {
        finish();
    }

}
