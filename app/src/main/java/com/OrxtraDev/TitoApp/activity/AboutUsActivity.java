package com.OrxtraDev.TitoApp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.OrxtraDev.TitoApp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.shareParent)void shareAction(){
        String message = "https://play.google.com/store/apps/details?id=com.OrxtraDev.TitoApp";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "https://play.google.com/store/apps/details?id=com.OrxtraDev.TitoApp"));

    }
}
