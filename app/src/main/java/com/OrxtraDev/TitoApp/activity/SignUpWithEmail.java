package com.OrxtraDev.TitoApp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.OrxtraDev.TitoApp.LoginActivity;
import com.OrxtraDev.TitoApp.MapsActivity;
import com.OrxtraDev.TitoApp.R;
import com.OrxtraDev.TitoApp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpWithEmail extends AppCompatActivity {



    @BindView(R.id.emailET)
    EditText emailET;

    @BindView(R.id.passwordET)
    EditText passwordET;

    @BindView(R.id.nameET)
    EditText nameET;

    @BindView(R.id.phoneET)
    EditText phoneET;

    ProgressDialog progressDialog;

    private static String TAG = "google";


    User user;

    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_email);
        ButterKnife.bind(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("انتظر...");
        mAuth = FirebaseAuth.getInstance();
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        user =new User();

    }


    @OnClick(R.id.buttonSignUp)void  buttonSignUp(){

        user.setEmail(emailET.getText().toString());
        user.setPhone(phoneET.getText().toString());
        user.setName(nameET.getText().toString());

        if (!TextUtils.isEmpty(emailET.getText().toString())&&!TextUtils.isEmpty(passwordET.getText().toString())) {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String userID = mAuth.getCurrentUser().getUid();

                                DatabaseReference refToken = FirebaseDatabase.getInstance().getReference().child("Tokens");
                                String token = FirebaseInstanceId.getInstance().getToken();
                                refToken.child(userID).child("token").setValue(token);
                                user.setToken(token);
                                user.setId(userID);
                                DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("users");
                                refUser.child(userID).setValue(user);

                                progressDialog.dismiss();
                                Intent i = new Intent(SignUpWithEmail.this, MapsActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                        .FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            } else {

                                progressDialog.dismiss();
                                Log.w(TAG, "SignUpWithEmail:failure", task.getException());
                                Toast.makeText(SignUpWithEmail.this, "حدث خطاء حاول مرة اخرى.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }else Toast.makeText(this, "املاء جميع البيانات", Toast.LENGTH_SHORT).show();
    }

//    Intent i = new Intent(SignUpWithEmail.this, MapsActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
//            .FLAG_ACTIVITY_CLEAR_TASK);
//    startActivity(i);

}
