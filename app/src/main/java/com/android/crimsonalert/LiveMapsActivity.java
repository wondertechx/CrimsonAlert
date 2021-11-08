package com.android.crimsonalert;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.crimsonalert.models.users;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;


import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LiveMapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private String myLat, myLng;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String UserName;
    private GeoApiContext mGeoApiContext;
    private ImageView back;

    //DirectionsResult result;
    String userid;
    DatabaseReference location, locRef;
    String current_user_id, join_user_id, join_user_email;
    FirebaseAuth auth;
    FirebaseUser user;
    Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = firebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        UserName = getIntent().getStringExtra("name");
        userid = getIntent().getStringExtra("userid");

        location = FirebaseDatabase.getInstance().getReference();

        back = findViewById(R.id.mapArrow);

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        //getintent

        loadLocationforthisuser(userid);
    }

    private void loadLocationforthisuser(final String userid) {

        locRef = firebaseDatabase.getInstance().getReference(userid).child("location");
        DatabaseReference nameRef = firebaseDatabase.getInstance().getReference(userid);
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users nameUser = dataSnapshot.getValue(users.class);
                UserName = nameUser.name;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        locRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final com.android.crimsonalert.models.Location tracking = dataSnapshot.getValue(com.android.crimsonalert.models.Location.class);
                final LatLng friendlocation = new LatLng(Double.parseDouble(tracking.getLatitude()), Double.parseDouble(tracking.getLongitude()));

                //friend
                final Location friend = new Location("");
                friend.setLatitude(Double.parseDouble(tracking.getLatitude()));
                //friend.setLongitude(Double.parseDouble(tracking.getLongitude()));
                friend.setLongitude(Double.parseDouble(tracking.getLongitude()));



                mMap.clear();


                //add friend on map


                //user2 title = new User2();

                DatabaseReference loc = FirebaseDatabase.getInstance().getReference(user.getUid()).child("location");
                loc.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mMap.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            if (dataSnapshot1.getKey().equals("latitude")) {
                                myLat = dataSnapshot1.getValue(String.class);
                            }

                            if (dataSnapshot1.getKey().equals("longitude")) {
                                myLng = dataSnapshot1.getValue(String.class);
                            }


                        }
                        LatLng current = new LatLng(Double.parseDouble(myLat),Double.parseDouble(myLng));
                        mMap.addMarker(new MarkerOptions().position(current).title("My Location"));
                        Location currentUser = new Location("");
                        currentUser.setLatitude(Double.parseDouble(myLat));
                        currentUser.setLongitude(Double.parseDouble(myLng));

                        mMap.addMarker(new MarkerOptions().position(friendlocation).title(UserName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(myLat),Double.parseDouble(myLng)), 12.0f));

                        mMap.addPolyline(new PolylineOptions().add(friendlocation, current).width(7).color(R.color.secondary));



                        //String url = getUrl(friendlocation, current, "driving");
                        //new FetchURL(LiveMapsActivity.this).execute(url,"driving");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //LatLng current = new LatLng(65.8326898, 87.3217877);
                //mMap.addMarker(new MarkerOptions().position(current).title("my location"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
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
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        if(marker.getSnippet().equals("This is you")){
            marker.hideInfoWindow();
        }
        else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(LiveMapsActivity.this);
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            calculateDirections(marker);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private double distance(Location currentUser, Location friend) {
        double theta = currentUser.getLongitude() - friend.getLongitude();
        double dist = Math.sin(deg2rad(currentUser.getLatitude()))
                * Math.sin(deg2rad(currentUser.getLatitude())) * Math.sin(deg2rad(friend.getLatitude())) * Math.cos(deg2rad(currentUser.getLatitude())) * Math.cos(deg2rad(friend.getLatitude()))*Math.cos(deg2rad(theta));

        dist = Math.cos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);

    }

    private double rad2deg(double rad) {
        return (rad * 180/Math.PI);

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI/180.0);
    }
    private void calculateDirections(Marker marker){
        //Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        (Double.parseDouble(myLat)),
                        (Double.parseDouble(myLng))
                )
        );
        //Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
//                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
//                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
//                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
//                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                //Log.d(TAG, "onResult: successfully retrieved directions.");
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                //Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {


                for (DirectionsRoute route : result.routes) {

                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(LiveMapsActivity.this, R.color.grey));
                    polyline.setClickable(true);

                }
            }
        });
    }
}