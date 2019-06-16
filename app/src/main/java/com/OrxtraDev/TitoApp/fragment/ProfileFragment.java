package com.OrxtraDev.TitoApp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.OrxtraDev.TitoApp.LoginActivity;
import com.OrxtraDev.TitoApp.R;
import com.OrxtraDev.TitoApp.activity.ChangeLanguage;
import com.OrxtraDev.TitoApp.activity.WalletActivity;
import com.OrxtraDev.TitoApp.model.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileFragment extends Fragment {


    //the firebase inital
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference df;
    private DatabaseReference mFirebaseDatabaseReference;

    //google sign in log out
    //google sign in
    private GoogleApiClient mGoogleApiClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        df = FirebaseDatabase.getInstance().getReference();


        final TextView nav_user = view.findViewById(R.id.userNameNav);
        final ImageView nav_image = view.findViewById(R.id.image);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null)
                    return;

                nav_user.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);


        return view;
    }


    @OnClick(R.id.payment)
    void payment() {

        startActivity(new Intent(getActivity(), WalletActivity.class));


    }

    @OnClick(R.id.settings)
    void settings() {

    }
    @OnClick(R.id.language)
    void language() {
        startActivity(new Intent(getActivity(), ChangeLanguage.class));
    }

    @OnClick(R.id.joindirver)
    void joindirver() {
        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.TitoApp.driver");
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.TitoApp.driver"));
            startActivity(intent);
        }
    }

    @OnClick(R.id.logout)
    void logout() {
//to remove the token from the backend if he is not login
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Tokens");
        ref.child(mFirebaseUser.getUid()).child("token").setValue("");

        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        mFirebaseAuth.signOut();
        //facebook sign out
        if (LoginManager.getInstance() != null)
            LoginManager.getInstance().logOut();

    }

}
