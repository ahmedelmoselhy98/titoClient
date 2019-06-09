package com.OrxtraDev.TitoApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.OrxtraDev.TitoApp.adapter.CategAdapter;
import com.OrxtraDev.TitoApp.model.CategLocal;
import com.OrxtraDev.TitoApp.model.CategoryModel;
import com.OrxtraDev.TitoApp.model.OrderModel;
import com.OrxtraDev.TitoApp.model.passData;
import com.OrxtraDev.TitoApp.view.BottomDialog;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.OrxtraDev.TitoApp.R.id.map;


public class MapsSendOrderActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, RoutingListener {


    //init the views
    @BindView(R.id.whereTV)
    TextView whereTV;
    @BindView(R.id.startTV)
    TextView startTV;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.categoriesSP)
    Spinner categoriesSP;
    @BindView(R.id.categTV)
    TextView categoryTV;
    @BindView(R.id.categIV)
    ImageView categoryIV;
    @BindView(R.id.estimatedPriceTV)TextView estimatedPriceTV;
    @BindView(R.id.expectedTV)TextView expectedTV;


    private static final String TAG = "test_tag";
    private GoogleMap mMap;

    //inti the view
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;

    private FrameLayout expand_category_frame;
    private ImageView expand_image;
    private boolean expand = false;
    private Button viewProfileBT, requestPersonBT;

    //for the User detail and the category visibility
    View categ_detail, marker_user_detail;


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

    String whereToGo;
    String categId = "-L_wEOtpCYemDnlzh7Ys";


    //init the firebase
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference df;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    int firstzoom;

    double clat, clang;
    double tolat, tolang;


    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    //the route
    private List<Polyline> polylines;

    private static final int[] COLORS = new int[]{R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.primary_dark_material_light};

    ProgressDialog progressDialog;
    ArrayList<CategoryModel> categoryModels = new ArrayList<>();


    //todo here to calculate the time and the distance
    double routeDuration =0,routeDistance=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setStatusBarTranslucent(true);
//        }
        setContentView(R.layout.map_include_detail);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);


        startLocationUpdates();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.setCancelable(false);


        firstzoom = 1;
        clang = 0;
        clat = 0;
        tolang = 0;
        tolat = 0;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        passData passData = (passData) getIntent().getSerializableExtra("data");

//        Log.d("google",whereToGo);
        if (passData != null) {
            if (passData.getTolang() != 0) {

                whereTV.setText(passData.getText());

                clang = passData.getClang();
                clat = passData.getClat();
                tolang = passData.getTolang();
                tolat = passData.getTolat();
                LatLng start = new LatLng(passData.getClat(), passData.getClang());
                LatLng waypoint = new LatLng(passData.getClat(), passData.getClang());
                LatLng end = new LatLng(passData.getTolat(), passData.getTolang());

                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(this)
                        .waypoints(start, waypoint, end)
                        .key("AIzaSyDCRPQbPfAo9dAWVKIOIQ3jKYHLf68oF_g")
                        .build();
                routing.execute();

            }
        }
//            whereTV.setText(whereToGo);


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //firebase intial
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        df = FirebaseDatabase.getInstance().getReference();

        polylines = new ArrayList<>();

        initViews();

        mFirebaseDatabaseReference.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.exists()) {
                            CategoryModel model = snapshot.getValue(CategoryModel.class);
                            categoryModels.add(model);
                        }
                    }

                    String[] catNames = new String[categoryModels.size()];
                    for (int i = 0; i < catNames.length; i++) {
                        catNames[i] = categoryModels.get(i).getName();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item, catNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categoriesSP.setAdapter(adapter);

                    categoriesSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            categId = categoryModels.get(position).getId();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    protected void setStatusBarTranslucent(boolean makeTranslucent) {
//        if (makeTranslucent) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        } else {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the User will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the User has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
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


        //custom marker
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


//        progressDialog.show();
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {

//                mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                float zoomLevel = (float) 15.0;


                if (firstzoom == 1) {
                    clat = arg0.getLatitude();
                    clang = arg0.getLongitude();
                    firstzoom = 2;
                    progressDialog.dismiss();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), zoomLevel));
                }


            }
        });


        //to action the all marker
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (categ_detail.getVisibility() == View.VISIBLE) {
//                    categ_detail.setVisibility(View.GONE);
//                    marker_user_detail.setVisibility(View.VISIBLE);
//                } else {
//                    categ_detail.setVisibility(View.VISIBLE);
//                    marker_user_detail.setVisibility(View.GONE);
//                }
//                return true;
//            }
//        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        float zoomLevel = (float) 14.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), zoomLevel));

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        clat = location.getLatitude();
        clang = location.getLongitude();
        float zoomLevel = (float) 14.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
    }


    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        @SuppressLint("InflateParams") View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.markerIV);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    @OnClick(R.id.whereTV)
    void whereToGo() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("EG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(MapsSendOrderActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Picker1:" + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Picker2:" + e.getMessage());
        }
    }

    @OnClick(R.id.expectedTV)void expectedAction(){
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("EG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(MapsSendOrderActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Picker1:" + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Picker2:" + e.getMessage());
        }
    }

    @OnClick(R.id.startTV)
    void startAction() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("EG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(MapsSendOrderActivity.this);
            startActivityForResult(intent, 33);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Picker1:" + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Picker2:" + e.getMessage());
        }
    }


    //     A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
//                Log.i(TAG, "Place: " + place.getName());
                whereTV.setText(place.getAddress());
                whereToGo = place.getAddress().toString();
                tolat = place.getLatLng().latitude;
                tolang = place.getLatLng().longitude;

                Log.d("google", "" + tolat + "  " + tolang + " " + clang + " " + clat);

                LatLng start = new LatLng(clat, clang);
                LatLng waypoint = new LatLng(tolat, tolang);
                LatLng end = new LatLng(tolat, tolang);

                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(this)
                        .waypoints(start, waypoint, end)
                        .key("AIzaSyDCRPQbPfAo9dAWVKIOIQ3jKYHLf68oF_g")
                        .build();
                routing.execute();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("google", status.getStatusMessage());

            }
//            else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
        } else if (requestCode == 33) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
//                Log.i(TAG, "Place: " + place.getName());
                startTV.setText(place.getAddress());
//                whereToGo = place.getAddress().toString();
                clat = place.getLatLng().latitude;
                clang = place.getLatLng().longitude;


                Log.d("google", "" + tolat + "  " + tolang + " " + clang + " " + clat);

                LatLng start = new LatLng(clat, clang);
                LatLng waypoint = new LatLng(tolat, tolang);
                LatLng end = new LatLng(tolat, tolang);

                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(this)
                        .waypoints(start, waypoint, end)
                        .key("AIzaSyDCRPQbPfAo9dAWVKIOIQ3jKYHLf68oF_g")
                        .build();
                routing.execute();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("google", status.getStatusMessage());

            }
//            else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
        }
//        Log.d("google","gooogle here image  ");
    }

    //here we begin to inti the views
    private void initViews() {


    }

    public void DriverAction(View view) {

//        if (tolang==0||tolat==0){
//            Toast.makeText(MapsSendOrderActivity.this,"please select where to go first ",Toast.LENGTH_LONG).show();
//            return;
//        }
        OrderModel order = new OrderModel();

        order.setUserId(mFirebaseUser.getUid());
        order.setId(random());
        order.setStatus("0");

//        Toast.makeText(this, "Lat:"+clat, Toast.LENGTH_SHORT).show();
        order.setCatag(categId);
        order.setLat("" + clat);
        order.setLang("" + clang);
        order.setEndTime("");
        order.setDriverId("");
        order.setStartTime("");

        order.setTime(System.currentTimeMillis() + "");
        order.setToLang("" + tolang);
        order.setToLat("" + tolat);


        order.setTripDistance("");
        order.setTripPrice("");
        order.setTripTime("");


        mFirebaseDatabaseReference.child("Orders")
                .child(order.getId()).setValue(order);


        Intent i = new Intent(MapsSendOrderActivity.this, OrderAcceptActivity.class);

        i.putExtra("order", order.getId());

        startActivity(i);
        finish();


    }


    /**
     * to get ids for the firebase
     *
     * @return random string
     */
    protected String random() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }

    public void CancelAction(View view) {
        finish();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.d("google", "failed          " + e.getMessage() + e.toString());
    }

    @Override
    public void onRoutingStart() {
        Log.d("google", "start");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i1) {

//        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(clat, clang));
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

//        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.

        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);



//            Log.d("google","this is the distance and duration  "+ route.get(i).getDistanceText()+ route.get(i).getDurationText());


//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }


        Log.d("google", "this is the distance and duration00000000  " + route.get(0).getDistanceValue() + "       " + route.get(0).getDurationValue());
//        route.get(0).getDistanceText();
        Log.d("google", "this is the distance and duration   " + (route.get(0).getDistanceValue() / 1000.0) + "       " + (route.get(0).getDurationValue() / 60));
        Log.d("google", "this is the distance and duration00000000  " + route.get(0).getDistanceText() + "       " + route.get(0).getDurationText());


        routeDuration = (route.get(0).getDurationValue() / 60);
        routeDistance = (route.get(0).getDistanceValue() / 1000.0);
        getEstimatedPrice();





        LatLng from_Latlng=new LatLng(clat,clang);
        LatLng to_Latlong=new LatLng(tolat,tolang);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(from_Latlng);
        builder.include(to_Latlong);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(route.get(0).getLatLgnBounds(), 10), 2000, null);



        // Start marker


        MarkerOptions marker1 = new MarkerOptions()
                .position(new LatLng(clat, clang))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher)));

        mMap.addMarker(marker1);


        MarkerOptions marker2 = new MarkerOptions()
                .position(new LatLng(tolat, tolang))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.descccc)));

        mMap.addMarker(marker2);



    }

    @Override
    public void onRoutingCancelled() {
        Log.d("google", "canceled ");
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                Log.d("google", "can get loaction");
//                locationSettingsResponse.getLocationSettingsStates().isGpsPresent()
//                if (!locationSettingsResponse.getLocationSettingsStates().isGpsUsable()){
//
//
//
//                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsSendOrderActivity.this,
                                2);

                        Log.d("google", "this dialog didnot work exception ");
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.d("google", "location exception " + sendEx.getMessage());
                    }
                }
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
//                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }



    BottomDialog bottomDialog;

    @OnClick(R.id.selectcategCard)
    void selectcategoryAction() {
        selectcateg();
    }

    private void selectcateg() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.post_detail, null);

        final ArrayList<CategLocal> data = new ArrayList<>();

        data.add(new CategLocal(R.drawable.car0, "تيتو", "تيتو", "-L_wEOtpCYemDnlzh7Ys"));
        data.add(new CategLocal(R.drawable.car1, "تيتو بلس", "تيتو بلس", "-LwEW_Y_YFhvEJgcccC"));


        final CategAdapter adapter = new CategAdapter(data, MapsSendOrderActivity.this, categId);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MapsSendOrderActivity.this, LinearLayoutManager.VERTICAL, false);

        RecyclerView recyclerView = customView.findViewById(R.id.categRV);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);


        bottomDialog = new BottomDialog.Builder(MapsSendOrderActivity.this)
                .setCustomView(customView)
                .show();


    }

    //todo here the event bus


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CategLocal event) {
        bottomDialog.dismiss();


        categoryTV.setText(event.getTitle());
        categoryIV.setImageResource(event.getImage());
        categId = event.getId();


        getEstimatedPrice();


    }


    private void getEstimatedPrice(){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Category").child(categId);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CategoryModel model = dataSnapshot.getValue(CategoryModel.class);

                if (model != null) {
                    //todo here to calculate the price

                    if (routeDistance!=0&&routeDuration!=0){
                        int price = (int) ((routeDistance*model.getKiloPrice())+(routeDuration*model.getMinutePrice())
                                + model.getOpenPrice());


                        if (price<model.getMinimumPrice()){
                            price = (int) model.getMinimumPrice();
                        }


                        expectedTV.setVisibility(View.GONE);
                        estimatedPriceTV.setText(price+" - "+(price+5)+" EGP");


                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        df.addValueEventListener(postListener);
    }


}


