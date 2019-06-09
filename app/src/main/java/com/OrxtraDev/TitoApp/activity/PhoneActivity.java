package com.OrxtraDev.TitoApp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.OrxtraDev.TitoApp.MapsActivity;
import com.OrxtraDev.TitoApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneActivity extends AppCompatActivity {

    //init the views
    @BindView(R.id.phoneET)EditText phoneET;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

    }

    /**
     * add action the phone  button
     */
    @OnClick(R.id.phoneBtn)void addPhone(){


        if (phoneET.getText().toString().isEmpty()){
            Toast.makeText(PhoneActivity.this, "please enter your phone number", Toast.LENGTH_LONG).show();
            return;
        }

        mFirebaseDatabaseReference.child("users")
                .child(mFirebaseUser.getUid()).child("phone").setValue(phoneET.getText().toString());

        Intent i = new Intent(PhoneActivity.this, MapsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


}
