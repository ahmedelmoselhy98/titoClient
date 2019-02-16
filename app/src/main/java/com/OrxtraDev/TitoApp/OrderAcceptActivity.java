package com.OrxtraDev.TitoApp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.OrxtraDev.TitoApp.model.RatingOrderModel;
import com.bumptech.glide.Glide;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.OrxtraDev.TitoApp.model.DriverModel;
import com.OrxtraDev.TitoApp.model.OrderModel;
import com.OrxtraDev.TitoApp.util.PermissionUtils;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.OrxtraDev.TitoApp.R.id.map;
import static com.OrxtraDev.TitoApp.R.id.ratingBar;


public class OrderAcceptActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, RatingDialogListener {


    //init the views

    @BindView(R.id.driverIV)
    ImageView driverIV;
    @BindView(R.id.driverNameTV)
    TextView driverNameTV;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.driverParent)
    LinearLayout driverParent;
    @BindView(R.id.endTrip)
    LinearLayout endTrip;
    @BindView(R.id.callBtn)
    Button callBtn;
    @BindView(R.id.endTripBtn)
    Button endTripBtn;


    @BindView(R.id.tripPrice)
    TextView tripPrice;
    @BindView(R.id.tripTime)
    TextView tripTime;
    @BindView(R.id.tripDistance)
    TextView tripDistance;


    @BindView(R.id.ratingBar)
    Button orderRating;




    private static final String TAG = "google";
    private GoogleMap mMap;


    //init the firebase
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference df, df1;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    private RatingOrderModel ratingOrderModel;

    OrderModel order;

    int firstzoom;


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


    private String orderId;
    private String orderStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarTranslucent(true);
        }

        setContentView(R.layout.map_include_from);
        ButterKnife.bind(this);

        firstzoom = 1;
        orderId = getIntent().getStringExtra("order");

        if (orderId == null) {
            return;
        }


        driverParent.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        orderStatus = "";


        ratingOrderModel =  new RatingOrderModel();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        //firebase intial
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();


        initViews();

        getOrderData();
    }


    /**
     * here to get the data from the firebase for the order and listen if any driver is accept it or if the driver is refused
     */
    private void getOrderData() {


        df = FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                loading.setVisibility(View.GONE);


                order = dataSnapshot.getValue(OrderModel.class);

                if (order == null)
                    return;

                Log.d("hazem", "here in the order " + order.getStatus());


                if (order.getStatus().equals(orderStatus)) {
                    return;
                }


                if (orderStatus.equals("1")) {
                    if (order.getStatus().equals("0"))
                        return;
                }

                if (orderStatus.equals("2")) {
                    if (order.getStatus().equals("0"))
                        return;
                    if (order.getStatus().equals("1"))
                        return;
                }

                orderStatus = order.getStatus();


                if (orderStatus.equals("0")) {
//                    orderParent.setVisibility(View.VISIBLE);
//                    canceledParent.setVisibility(View.GONE);
                } else if (orderStatus.equals("1")) {

                    loading.setVisibility(View.GONE);
                    driverParent.setVisibility(View.VISIBLE);

//                        startParent.setVisibility(View.VISIBLE);

                    //show the user location on the map
//                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(order.getLat()),
//                                Double.parseDouble(order.getLang())));
//
//                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
//
//                        mMap.moveCamera(center);
//
//
//                        MarkerOptions marker1 = new MarkerOptions()
//                                .position(new LatLng(Double.parseDouble(order.getLat()),
//                                        Double.parseDouble(order.getLang())))
//                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(userImageString)));
//
//                        mMap.addMarker(marker1);


                } else if (orderStatus.equals("2")) {

                    driverParent.setVisibility(View.GONE);
                    Toast.makeText(OrderAcceptActivity.this,"Trip is starting",Toast.LENGTH_LONG).show();


//                        orderParent.setVisibility(View.GONE);
//                        startParent.setVisibility(View.GONE);
//                        endParent.setVisibility(View.VISIBLE);


//                    show the user location on the map
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(order.getToLat()),
                                Double.parseDouble(order.getToLang())));

                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                        mMap.moveCamera(center);


                        MarkerOptions marker1 = new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(order.getToLat()),
                                        Double.parseDouble(order.getToLang())))
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.lock_outline)));

                        mMap.addMarker(marker1);

                    double clat,clang,tolat,tolang;
                    clat = Double.parseDouble(order.getLat());
                        clang = Double.parseDouble(order.getLang());

                        tolat = Double.parseDouble(order.getToLat());
                        tolang = Double.parseDouble(order.getToLang());

                        LatLng start = new LatLng(clat, clang);
                        LatLng waypoint= new LatLng(tolat, tolang);
                        LatLng end = new LatLng(tolat, tolang);

                        Routing routing = new Routing.Builder()
                                .travelMode(Routing.TravelMode.DRIVING)
                                .withListener(new RoutingListener() {
                                    @Override
                                    public void onRoutingFailure(RouteException e) {
                                        Log.e(TAG,"RouteError"+e.getMessage());
                                    }

                                    @Override
                                    public void onRoutingStart() {

                                    }

                                    @Override
                                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

                                    }

                                    @Override
                                    public void onRoutingCancelled() {

                                    }
                                })
                                .waypoints(start, waypoint, end)
                                .key("AIzaSyBVHgr4vi1pi5DRuQj8TjJxBPZ67DP4LSg")
                                .build();
                        routing.execute();

                }else if (orderStatus.equals("4")){


                    tripPrice.setText("سعر الرحلة: "+order.getTripPrice()+" جنية ");
                    tripTime.setText("وقت الرحلة: "+order.getTripTime()+" دقيقة ");
                    tripDistance.setText("المسافة المقطوعة: "+order.getTripDistance()+" كيلومتر ");
                    endTrip.setVisibility(View.VISIBLE);

                    orderRating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showRatingDialog();
                        }
                    });


                }


                getUserData(order.getDriverId());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        df.addValueEventListener(postListener);
    }


    @OnClick(R.id.callBtn)
    void cancel() {
        finish();
    }

    /**
     * here to get the data of the user who created the order
     *
     * @param userId the user id of who created the order
     */
    private void getUserData(final String userId) {
        df1 = FirebaseDatabase.getInstance().getReference().child("Drivers").child(userId);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DriverModel driver = dataSnapshot.getValue(DriverModel.class);

                if (driver == null)
                    return;

                driverNameTV.setText(driver.getFname() + " " + driver.getLname());

                if (!OrderAcceptActivity.this.isDestroyed())
                    Glide.with(OrderAcceptActivity.this).load(driver.getProfileImage()).into(driverIV);


                checkPermissions();

                callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_CALL);

                        intent.setData(Uri.parse("tel:" + driver.getPhone()));
                        if (ActivityCompat.checkSelfPermission(OrderAcceptActivity.this, android.Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        df1.addValueEventListener(postListener);

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
        enableMyLocation();

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {

//                mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                float zoomLevel = (float) 15.0;
                if (firstzoom == 1) {
                    firstzoom = 2;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), zoomLevel));
                }


            }
        });

    }

    /* Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(OrderAcceptActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
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
        float zoomLevel = (float) 14.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), zoomLevel));

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        float zoomLevel = (float) 14.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
    }


    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        TextView markerImageView = customMarkerView.findViewById(R.id.ivHostImage);
//        markerImageView.setImageResource(resId);
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


    //here we begin to inti the views
    private void initViews() {

    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(OrderAcceptActivity.this,
                    android.Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(OrderAcceptActivity.this,
                        android.Manifest.permission.CALL_PHONE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(OrderAcceptActivity.this,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            3);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("ارسال")
                .setNegativeButtonText("الفاء")
                .setNoteDescriptions(Arrays.asList("سيئ جدا", "سيئ", "مقبول", "جيد جدا", "ممتاز !!!"))
                .setDefaultRating(1)
                .setTitle("تقييم هذة الرحلة")
//                .setDescription("Please select some stars and give your feedback")
                .setStarColor(R.color.colorPrimaryDark)
                .setNoteDescriptionTextColor(R.color.black)
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.black)
                .setCommentTextColor(R.color.black)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
//                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(OrderAcceptActivity.this)

                .show();
    }

    @OnClick(R.id.endTripBtn)void endTripBtn(){
        finish();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {


        ratingOrderModel.setFeedback(s);
        ratingOrderModel.setRate(i);
        ratingOrderModel.setDriverId(order.getDriverId());
        ratingOrderModel.setUserId(order.getUserId());
        ratingOrderModel.setOrderId(order.getId());


        final ProgressDialog p =  new ProgressDialog(OrderAcceptActivity.this);
        p.setMessage("تحميل...");
        p.show();
        p.setCancelable(false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Ratings");
        String id = databaseReference.push().getKey();
        ratingOrderModel.setId(id);
        databaseReference.child(id).setValue(ratingOrderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    p.dismiss();
                    finish();
                    Toast.makeText(OrderAcceptActivity.this, "تم بنجاح", Toast.LENGTH_SHORT).show();
                }else {
                    p.dismiss();
                    Toast.makeText(OrderAcceptActivity.this, "فشل", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }
}


