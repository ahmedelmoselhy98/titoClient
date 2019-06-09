package com.OrxtraDev.TitoApp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.OrxtraDev.TitoApp.LoginActivity;
import com.OrxtraDev.TitoApp.MapsSendOrderActivity;
import com.OrxtraDev.TitoApp.R;
import com.OrxtraDev.TitoApp.activity.PhoneActivity;
import com.OrxtraDev.TitoApp.model.User;
import com.OrxtraDev.TitoApp.model.passData;
import com.OrxtraDev.TitoApp.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.OrxtraDev.TitoApp.R.id.map;


public class MapsFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationClickListener {


    //to init the views
    @BindView(R.id.cate1TV)
    TextView cate1;
    @BindView(R.id.cate2TV)
    TextView cate2;
    @BindView(R.id.cate3TV)
    TextView cate3;
    @BindView(R.id.cate4TV)
    TextView cate4;
    @BindView(R.id.cate5TV)
    TextView cate5;

    @BindView(R.id.cate1V)
    View v1;
    @BindView(R.id.cate2V)
    View v2;
    @BindView(R.id.cate3V)
    View v3;
    @BindView(R.id.cate4V)
    View v4;
    @BindView(R.id.cate5V)
    View v5;
    @BindView(R.id.locationTV)
    TextView locationTV;


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
    private DatabaseReference mFirebaseDatabaseReference;

    //google sign in log out
    //google sign in
    private GoogleApiClient mGoogleApiClient;


    ProgressDialog progressDialog;

    //the flags and the public variables
    String whereToGo;


    double clat, clang;
    double tolat, tolang;


    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_include, container, false);
        ButterKnife.bind(this, view);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setStatusBarTranslucent(true);
//        }


        startLocationUpdates();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.getting_location));
        progressDialog.setCancelable(false);


        firstzoom = 0;

        clang = 0;
        clat = 0;
        tolang = 0;
        tolat = 0;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        df = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            Intent i = new Intent(getActivity(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return null;
        }


        df = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                if (user.getPhone() == null) {
                    Intent i = new Intent(getContext(), PhoneActivity.class);
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
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


        return view;

    }

    /**
     * to select the location to where to go the destetation
     */
    @OnClick(R.id.locationTV)
    void wheretogo() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("EG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Picker1:" + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Picker2:" + e.getMessage());
        }
    }

    //     A place has been received; use requestCode to track the request.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                locationTV.setText(place.getAddress());
                whereToGo = place.getAddress().toString();

                tolat = place.getLatLng().latitude;
                tolang = place.getLatLng().longitude;


                Intent i = new Intent(getActivity(), MapsSendOrderActivity.class);

                passData passData = new passData();

                passData.setText(locationTV.getText().toString());
                passData.setClat(clat);
                passData.setClang(clang);
                passData.setTolat(tolat);
                passData.setTolang(tolang);

                i.putExtra("data", passData);

                startActivity(i);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
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
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
                            getActivity(), R.raw.style_json));

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

        progressDialog.show();

        enableMyLocation();


//        progressDialog.show();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {

//                mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                float zoomLevel = (float) 15.0;

                    clat = arg0.getLatitude();
                    clang = arg0.getLongitude();
                    firstzoom = 2;
                    progressDialog.dismiss();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), zoomLevel));



            }
        });


    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            progressDialog.dismiss();
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
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
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



    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }



    @OnClick(R.id.orderNowBtn)
    void FromAction(View view) {
        Intent i = new Intent(getActivity(), MapsSendOrderActivity.class);
        passData passData = new passData();
        passData.setText(locationTV.getText().toString());
        passData.setClat(clat);
        passData.setClang(clang);
        passData.setTolat(tolat);
        passData.setTolang(tolang);

        i.putExtra("data", passData);

        startActivity(i);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save selected item so it will remains same even after orientation change
        outState.putInt("SELECTED_ID", mSelectedId);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("google", "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    /**
     * here to action the select the specific category
     */
    private void selectCateg() {
        cate1.setOnClickListener(this);
        cate2.setOnClickListener(this);
        cate3.setOnClickListener(this);
        cate4.setOnClickListener(this);
        cate5.setOnClickListener(this);

    }

    //gray text , black
    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.cate1TV) {
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
        } else if (view.getId() == R.id.cate2TV) {


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

        } else if (view.getId() == R.id.cate3TV) {

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

        } else if (view.getId() == R.id.cate4TV) {

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

        } else if (view.getId() == R.id.cate5TV) {


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


    // Trigger new location updates at interval
    protected void startLocationUpdates() {
//        if(!gpsTracker.canGetLocation()) {
//
//            Log.d("google","cannot get location");
//            gpsTracker.showSettingsAlert();
//            return;
//        }
//        Log.d("google","can get location");

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        final LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);


        task.addOnSuccessListener(this.getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                Log.d("google", "can get loaction");
//                locationSettingsResponse.getLocationSettingsStates().isGpsPresent()
                if (!locationSettingsResponse.getLocationSettingsStates().isGpsUsable()){



                }
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                2);

                        Log.d("google","this dialog didnot work exception ");
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.d("google","location exception "+sendEx.getMessage());
                    }
                }
            }
        });



        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
//                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

}


