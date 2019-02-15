package com.OrxtraDev.TitoApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.OrxtraDev.TitoApp.activity.PhoneActivity;
import com.OrxtraDev.TitoApp.model.User;
import com.OrxtraDev.TitoApp.model.passData;
import com.OrxtraDev.TitoApp.util.PermissionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.OrxtraDev.TitoApp.R.id.map;


public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback  , ActivityCompat.OnRequestPermissionsResultCallback ,
        NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener ,
        GoogleApiClient.OnConnectionFailedListener ,  GoogleMap.OnMyLocationClickListener{



    //to init the views
    @BindView(R.id.cate1TV)TextView cate1;
    @BindView(R.id.cate2TV)TextView cate2;
    @BindView(R.id.cate3TV)TextView cate3;
    @BindView(R.id.cate4TV)TextView cate4;
    @BindView(R.id.cate5TV)TextView cate5;

    @BindView(R.id.cate1V)View v1;
    @BindView(R.id.cate2V)View v2;
    @BindView(R.id.cate3V)View v3;
    @BindView(R.id.cate4V)View v4;
    @BindView(R.id.cate5V)View v5;
    @BindView(R.id.locationTV)TextView locationTV;




    //location api
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;



    private static final String TAG = "google";
    private GoogleMap mMap;


    //the left menu
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ImageView menuIV;
    private int mSelectedId;


    //the firebase inital
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference df;
    private DatabaseReference mFirebaseDatabaseReference ;

    //google sign in log out
    //google sign in
    private GoogleApiClient mGoogleApiClient;


    ProgressDialog progressDialog;

    //the flags and the public variables
    String whereToGo;



    double clat,clang;
    double tolat,tolang;


    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    int firstzoom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarTranslucent(true);
        }
        setContentView(R.layout.map_include);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري تحديد الموقع...");
        progressDialog.show();

        firstzoom = 0;

        clang =0;
        clat=0;
        tolang=0;
        tolat=0;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        df = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            Intent i = new Intent(MapsActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }


        df = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                if (user.getPhone()==null){
                    Intent i = new Intent(MapsActivity.this, PhoneActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        df.addValueEventListener(postListener);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        initViews();



        //google sign in logout

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("257412246317-o3gaqqn6ljtmm5voje2k7ik31htu7pb0.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    /**
     * to select the location to where to go the destetation
     */
    @OnClick(R.id.locationTV)void wheretogo(){
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("EG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(MapsActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG,"Picker1:"+e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG,"Picker2:"+e.getMessage());
        }
    }

    //     A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
//                Log.i(TAG, "Place: " + place.getName());
                locationTV.setText(place.getAddress());
                whereToGo = place.getAddress().toString();

                tolat = place.getLatLng().latitude;
                tolang = place.getLatLng().longitude;

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("google", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
//        Log.d("google","gooogle here image  ");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the User will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the User has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
//        float zoomLevel = (float) 14.0;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.2081336, 29.0457663), zoomLevel));

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        enableMyLocation();

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub

                clat =  arg0.getLatitude();
                clang = arg0.getLongitude();
//                mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                float zoomLevel = (float) 15.0;
                if (firstzoom==0) {
                   firstzoom=1;
                   progressDialog.dismiss();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), zoomLevel));
                }


            }
        });


    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(MapsActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
//        float zoomLevel = (float) 14.0;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()), zoomLevel));

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        float zoomLevel = (float) 14.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }
    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    //here we begin to inti the views
    private void initViews(){


        mDrawer = findViewById(R.id.nvView);
        mDrawerLayout = findViewById(R.id.drawer_layout);


        mDrawer.setNavigationItemSelectedListener(this);

        //to get username and image in left menu
        View hView = mDrawer.getHeaderView(0);
        final TextView nav_user =  hView.findViewById(R.id.userNameNav);
        final ImageView nav_image =  hView.findViewById(R.id.image);

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

        menuIV = findViewById(R.id.menuIV);

        menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        selectCateg();
    }

    @SuppressLint("WrongConstant")
    private void itemSelection(int mSelectedId) {

//        IntentDetail data;
        switch (mSelectedId) {


            case R.id.logout_lm:
                mDrawerLayout.closeDrawer(GravityCompat.START);

                //to remove the token from the backend if he is not login
                DatabaseReference ref =    FirebaseDatabase.getInstance().getReference().child("Tokens");
                ref.child(mFirebaseUser.getUid()).child("token").setValue("");

                Intent i = new Intent(MapsActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                mFirebaseAuth.signOut();
                //facebook sign out
                if (LoginManager.getInstance() != null)
                    LoginManager.getInstance().logOut();
                //google log out
                // Google sign out
                if (Auth.CREDENTIALS_API != null)
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
//                                updateUI(null);
                                }
                            });
                break;
//            case R.id.profile_lm:
//                mDrawerLayout.closeDrawer(GravityCompat.START);
//                startActivity(new Intent(TreeActivity.this, ProfileActivity.class));
//                break;
        }

    }

    /**
     * go to detail activity
     * @param view the view from the xml
     */
    public void FromAction(View view) {
        Intent i = new Intent(MapsActivity.this,MapsSendOrderActivity.class);

        passData passData = new passData();

        passData.setText(locationTV.getText().toString());
        passData.setClat(clat);
        passData.setClang(clang);
        passData.setTolat(tolat);
        passData.setTolang(tolang);

        i.putExtra("data",passData);

        startActivity(i);
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param ,menuItem The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(false);
        mSelectedId = menuItem.getItemId();
        itemSelection(mSelectedId);
        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //save selected item so it will remains same even after orientation change
        outState.putInt("SELECTED_ID", mSelectedId);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("google", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    /**
     * here to action the select the specific category
     */
    private void selectCateg(){
        cate1.setOnClickListener(this);
        cate2.setOnClickListener(this);
        cate3.setOnClickListener(this);
        cate4.setOnClickListener(this);
        cate5.setOnClickListener(this);

    }

    //gray text , black
    @Override
    public void onClick(View view) {


        if (view.getId()==R.id.cate1TV){
            cate1.setTextColor(getResources().getColor(R.color.black));

            cate2.setTextColor(getResources().getColor(R.color.gray_text));
            cate3.setTextColor(getResources().getColor(R.color.gray_text));
            cate4.setTextColor(getResources().getColor(R.color.gray_text));
            cate5.setTextColor(getResources().getColor(R.color.gray_text));


            //selected
            v1.setVisibility(View.VISIBLE);

            v2.setVisibility(View.GONE);
            v3.setVisibility(View.GONE);
            v4.setVisibility(View.GONE);
            v5.setVisibility(View.GONE);
        }else  if (view.getId()==R.id.cate2TV){


            cate2.setTextColor(getResources().getColor(R.color.black));

            cate1.setTextColor(getResources().getColor(R.color.gray_text));
            cate3.setTextColor(getResources().getColor(R.color.gray_text));
            cate4.setTextColor(getResources().getColor(R.color.gray_text));
            cate5.setTextColor(getResources().getColor(R.color.gray_text));

            //selected
            v2.setVisibility(View.VISIBLE);

            v1.setVisibility(View.GONE);
            v3.setVisibility(View.GONE);
            v4.setVisibility(View.GONE);
            v5.setVisibility(View.GONE);

        }else  if (view.getId()==R.id.cate3TV){


            cate3.setTextColor(getResources().getColor(R.color.black));

            cate2.setTextColor(getResources().getColor(R.color.gray_text));
            cate1.setTextColor(getResources().getColor(R.color.gray_text));
            cate4.setTextColor(getResources().getColor(R.color.gray_text));
            cate5.setTextColor(getResources().getColor(R.color.gray_text));

            //selected
            v3.setVisibility(View.VISIBLE);

            v2.setVisibility(View.GONE);
            v1.setVisibility(View.GONE);
            v4.setVisibility(View.GONE);
            v5.setVisibility(View.GONE);

        }else  if (view.getId()==R.id.cate4TV){

            cate4.setTextColor(getResources().getColor(R.color.black));

            cate2.setTextColor(getResources().getColor(R.color.gray_text));
            cate3.setTextColor(getResources().getColor(R.color.gray_text));
            cate1.setTextColor(getResources().getColor(R.color.gray_text));
            cate5.setTextColor(getResources().getColor(R.color.gray_text));


            //selected
            v4.setVisibility(View.VISIBLE);

            v2.setVisibility(View.GONE);
            v3.setVisibility(View.GONE);
            v1.setVisibility(View.GONE);
            v5.setVisibility(View.GONE);

        }else  if (view.getId()==R.id.cate5TV){


            cate5.setTextColor(getResources().getColor(R.color.black));

            cate2.setTextColor(getResources().getColor(R.color.gray_text));
            cate3.setTextColor(getResources().getColor(R.color.gray_text));
            cate4.setTextColor(getResources().getColor(R.color.gray_text));
            cate1.setTextColor(getResources().getColor(R.color.gray_text));

            //selected
            v5.setVisibility(View.VISIBLE);

            v2.setVisibility(View.GONE);
            v3.setVisibility(View.GONE);
            v4.setVisibility(View.GONE);
            v1.setVisibility(View.GONE);
        }
    }



}


