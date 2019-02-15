package com.OrxtraDev.TitoApp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.OrxtraDev.TitoApp.activity.SignUpWithEmail;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.OrxtraDev.TitoApp.activity.PhoneActivity;
import com.OrxtraDev.TitoApp.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {


    //this is just the TAG
    private static String TAG = "google";


    //init the views

    LoginButton loginButton;
    ImageView facebookBtn;

    @BindView(R.id.emailET)
    EditText emailET;

    @BindView(R.id.passwordET)
    EditText passwordET;


    ProgressDialog progressDialog;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    // [END declare_auth]

    private CallbackManager mCallbackManager;

    //google sign in
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;


    //to now if the data of User is exist
    // Firebase instance variables
    private DatabaseReference df;

    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //init the views
        facebookBtn =  findViewById(R.id.facebookBtn);
        loading=  findViewById(R.id.loading);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("انتظر...");

        //google sign in
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("257412246317-o3gaqqn6ljtmm5voje2k7ik31htu7pb0.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // [END initialize_auth]

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton =  findViewById(R.id.login_button);

        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                loading.setVisibility(View.VISIBLE);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]
    }




    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG,"here in the google sign in ");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(TAG,"here in the google sign in not sucess  "+data.getDataString());
                Toast.makeText(LoginActivity.this,"Couldn't get the Data",Toast.LENGTH_LONG).show();
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }else{

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    // [END on_activity_result]
    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the User.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the User.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]




    @SuppressLint("WrongConstant")
    private void updateUI(final FirebaseUser user) {
        loading.setVisibility(View.GONE);
        if (user != null) {
//            Log.d(TAG, "this is the User image  " + String.valueOf(User.getPhotoUrl()));


            df = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    if (u!=null){

                        DatabaseReference ref =    FirebaseDatabase.getInstance().getReference().child("Tokens");

                        String token = FirebaseInstanceId.getInstance().getToken();
                        ref.child(user.getUid()).child("token").setValue(token);


                        Intent i = new Intent(LoginActivity.this, MapsActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else {
                        String user_id = user.getUid();
                        User newUser =new User();
                        newUser.setId(user_id);
                        newUser.setEmail(user.getEmail());
                        newUser.setName(user.getDisplayName());


                        //add the new user
                        mFirebaseDatabaseReference.child("users")
                                .child(user_id).setValue(newUser);



                        //add the token for the new user
                        DatabaseReference ref =    FirebaseDatabase.getInstance().getReference().child("Tokens");
                        String token = FirebaseInstanceId.getInstance().getToken();
                        ref.child(user_id).child("token").setValue(token);


                        //start the phone activity
                        Intent i = new Intent(LoginActivity.this, PhoneActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            df.addListenerForSingleValueEvent(postListener);

        }
    }

    public void facebookLogin(View view) {
        loginButton.performClick();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void googleSignIn(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        loading.setVisibility(View.VISIBLE);
    }



//    public void singUpMail(View view) {
//        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
//    }


    @OnClick(R.id.buttonSignIn)void buttonSignIn(){

        if (!TextUtils.isEmpty(emailET.getText().toString())&&!TextUtils.isEmpty(passwordET.getText().toString()))
        {

            progressDialog.show();
        mAuth.signInWithEmailAndPassword(emailET.getText().toString(),passwordET.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            progressDialog.dismiss();
                            Intent i = new Intent(LoginActivity.this, MapsActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                    .FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        }else {
                            progressDialog.dismiss();
                            Log.w(TAG, "LoginWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "حدث خطاء حاول مرة اخرى.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        }else Toast.makeText(this, "املاء جميع البيانات", Toast.LENGTH_SHORT).show();

    }


    @OnClick(R.id.newAccountButtin)void newAccountButtin(){

        Intent i = new Intent(LoginActivity.this, SignUpWithEmail.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
    public void termsAction(View view) {
        String url = "http://dods.io/Terms";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
