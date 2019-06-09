package com.OrxtraDev.TitoApp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.OrxtraDev.TitoApp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class WalletActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
    }


    /**
     * press the back button
     */
    @OnClick(R.id.back_image)
    void back__() {
        finish();
    }
}
