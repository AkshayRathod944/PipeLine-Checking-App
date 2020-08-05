package com.example.mark2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark2.data.DatabaseHandler;
import com.example.mark2.modal.MyLocation;
import com.example.mark2.ui.RadarView;
import com.example.mark2.util.AppBroadcast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private List<MyLocation> dbLocationArrayList = new ArrayList<> ();
    private ArrayList<Location> locationsArrayList = new ArrayList<> ();
    private float distance, nearestDistance;
    private Location nearestSpot;
    private boolean isNearAnyLocation = false;
    private TextView nearestLocationTextView, currentLocation, distanceFrom;
    private DatabaseHandler db = new DatabaseHandler ( MainActivity.this );

    //Location variables
    private static final int ALL_PERMISSIONS_RESULT = 1111;

    private GoogleApiClient client;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<> ();
    private ArrayList<String> permissionsRejected = new ArrayList<> ();
    private TextView locationTextView;
    private LocationRequest locationRequest;

    public static final long UPDATE_INTERVAL = 5000;
    public static final long FASTEST_INTERVAL = 5000;
     private LocationManager locationManager ;
     private Context context;
     private  boolean GpsStatus ;




    //animation variables
    RadarView mRadarView = null;
    private Boolean isFirstRun;


    AppBroadcast appBroadcastReciver =new AppBroadcast (MainActivity.this);

    //  protected static final String TAG = "LocationOnOff";
   // private GoogleApiClient googleApiClient;
   // final static int REQUEST_LOCATION = 199;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        nearestLocationTextView = findViewById ( R.id.Nearest_location_TextView );
        currentLocation = findViewById ( R.id.current_Location );
        distanceFrom = findViewById ( R.id.distance );
        mRadarView = (RadarView) findViewById(R.id.radarView);

        //   animation
        if (mRadarView != null) mRadarView.stopAnimation();
        //------this------//
        //--------------checkin location
        context = getApplicationContext();
        CheckGpsStatus();
        //-----this-------//

        //when app run first time
        firstRun ();
        //-------this------//

        Button email = findViewById ( R.id.Email );
        email.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent ( MainActivity.this,EmailActivity.class );
                startActivity ( intent );
                finish ();
            }
        } );
        //get all locations from location db table
        dbLocationArrayList = db.getAllLocations ();
        for (int i = 0; i < dbLocationArrayList.size (); i++) {
            Location location = new Location ( dbLocationArrayList.get ( i ).getLocationName () );
            location.setLatitude ( dbLocationArrayList.get ( i ).getLatitude () );
            location.setLongitude ( dbLocationArrayList.get ( i ).getLongitude () );
            // nearestLocationTextView.append ( "lon "+location.getLongitude ()+"\nlat "+location.getLatitude () );
            locationsArrayList.add ( location );
        }
        //--------this-------//

        Button fab2 = findViewById ( R.id.locations_updater );
        fab2.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent ( MainActivity.this, UpdateLocationActivity.class );
                startActivity ( intent );
                finish ();
            }
        } );

        Button fab1 = findViewById ( R.id.all_readings );
        fab1.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent ( MainActivity.this, AllReadingActivity.class );
                startActivity ( intent );
                finish ();
            }
        } );

        Button ReadingButton = findViewById ( R.id.reading_button );
        ReadingButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                //   -----animation stops here
                if (mRadarView != null) mRadarView.startAnimation();

                if (isNearAnyLocation) {
                    nearestLocationTextView.setText ( "lon " + nearestSpot.getLongitude () + "\nlat " + nearestSpot.getLatitude () );
                    distanceFrom.setText ( String.valueOf ( nearestDistance ) );

                    Intent intent = new Intent ( MainActivity.this, NewReadingActivity.class );
                    intent.putExtra ( "latitude", nearestSpot.getLatitude () );
                    intent.putExtra ( "longitude", nearestSpot.getLongitude () );
                    intent.putExtra ( "provider", nearestSpot.getProvider () );
                    startActivity ( intent );
                } else {
                    nearestLocationTextView.setText ( "Sorry!!! You are Away from reading Spot\n distance is " );
                    distanceFrom.setText ( String.valueOf ( nearestDistance ) );
                    new AlertDialog.Builder ( MainActivity.this )
                            .setMessage ( " Sorry You can't fill Readings\n You are not near any Targeted spot" )
                            .setPositiveButton ( "ok", null ).create ().show ();
                }
            }
        } );

        //google location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient ( MainActivity.this );
        //let's add permissions we need to request location of the users
        permissions.add ( Manifest.permission.ACCESS_FINE_LOCATION );
        permissions.add ( Manifest.permission.ACCESS_COARSE_LOCATION );

        permissionsToRequest = permissionsToRequest ( permissions );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size () > 0) {
                requestPermissions ( permissionsToRequest.toArray (
                        new String[permissionsToRequest.size ()] ),
                        ALL_PERMISSIONS_RESULT
                );
            }
        }

        client = new GoogleApiClient.Builder ( this )
                .addApi ( LocationServices.API )
                .addOnConnectionFailedListener ( this )
                .addConnectionCallbacks ( this )
                .build ();

        //     Nroadcast reciver
        IntentFilter filter = new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION );
        registerReceiver ( appBroadcastReciver,filter );
    }

    private void firstRun() {

        // Go and update the information of firstRunActivity to Store the userName in SharePreferance
        isFirstRun = getSharedPreferences ( "PREFERENCE", MODE_PRIVATE )
                .getBoolean ( "isFirstRun", true );

        if (isFirstRun) {
            startActivity ( new Intent ( MainActivity.this, UserNameActivity.class ) );
            Toast.makeText ( MainActivity.this, "First Run", Toast.LENGTH_LONG ).show ();
            finish ();
        }

        getSharedPreferences ( "PREFERENCE", MODE_PRIVATE ).edit ()
                .putBoolean ( "isFirstRun", false ).apply ();
        //-----up to this------//
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<> ();

        for (String perm : wantedPermissions) {
            if (!hasPermission ( perm )) {
                result.add ( perm );
            }
        }

        return result;
    }


    private void checkPlayServicess() {
        int errorCode = GoogleApiAvailability.getInstance ()
                .isGooglePlayServicesAvailable ( this );

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance ()
                    .getErrorDialog ( this, errorCode, errorCode, new DialogInterface.OnCancelListener () {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText ( MainActivity.this, "No services",
                                    Toast.LENGTH_LONG )
                                    .show ();
                            finish ();
                        }
                    } );
            errorDialog.show ();
        } else {
            //    Toast.makeText(MainActivity.this, "All is good", Toast.LENGTH_LONG)
            //            .show();
        }
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission ( perm ) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    @Override
    protected void onStart() {
        super.onStart ();
        if (client != null) {
            client.connect ();
        }
    }

    @Override
    protected void onStop() {
        super.onStop ();
        client.disconnect ();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume ();
        checkPlayServicess ();
    }

    @Override
    protected void onPause() {
        super.onPause ();

        if (client != null && client.isConnected ()) {
            LocationServices.getFusedLocationProviderClient ( this )
                    .removeLocationUpdates ( new LocationCallback () {
                    } );
            client.disconnect ();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission ( this,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission ( this,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation ()
                .addOnSuccessListener ( this, new OnSuccessListener<Location> () {
                    @Override
                    public void onSuccess(Location location) {
                        //Get last know location. But it could be null
                        if (location != null) {
                            currentLocation.setText ( "lon " + location.getLongitude () + "\nlat " + location.getLatitude () );
                        }
                    }
                } );
        startLocationUpdates ();
    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText ( MainActivity.this, "Connection Suspended", Toast.LENGTH_LONG ).show ();
        new AlertDialog.Builder ( MainActivity.this )
                .setMessage ( " Connection Suspended" )
                .setPositiveButton ( "ok", null ).create ().show ();

    }

    private void startLocationUpdates() {

        locationRequest = LocationRequest.create ();
        locationRequest.setPriority ( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval ( UPDATE_INTERVAL );
        locationRequest.setFastestInterval ( FASTEST_INTERVAL );
        if (ActivityCompat.checkSelfPermission ( this,
                Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission ( this,
                Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText ( MainActivity.this,
                    "You need to enable permission to display location!",
                    Toast.LENGTH_LONG )
                    .show ();
        }
        LocationServices.getFusedLocationProviderClient ( MainActivity.this )
                .requestLocationUpdates ( locationRequest, new LocationCallback () {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult ( locationResult );
                        if (locationResult != null) {
                            Location location = locationResult.getLastLocation ();
                            currentLocation.setText ( "lon " + location.getLongitude () + "\nlat " + location.getLatitude () );
                            nearestSpot ( location );
                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability ( locationAvailability );
                    }
                }, null );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission ( perm )) {

                        permissionsRejected.add ( perm );

                    }
                }
                if (permissionsRejected.size () > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale ( permissionsRejected.get ( 0 ) )) {
                            new AlertDialog.Builder ( MainActivity.this )
                                    .setMessage ( "This Permissions are Mandatory to continue" )
                                    .setPositiveButton ( "ok", new DialogInterface.OnClickListener () {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                requestPermissions ( permissionsRejected.toArray (
                                                        new String[permissionsRejected.size ()] ), ALL_PERMISSIONS_RESULT );
                                            }
                                        }
                                    } ).setNegativeButton ( "cancel", null ).create ()
                                    .show ();
                        }
                    }
                } else {
                    if (client != null) {
                        client.connect ();
                    }
                }
                break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText ( MainActivity.this, "Connection Failed", Toast.LENGTH_LONG ).show ();

        new AlertDialog.Builder ( MainActivity.this )
                .setMessage ( " Connection Failed" )
                .setPositiveButton ( "ok", null ).create ().show ();

    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLocation.setText ( "lon " + location.getLongitude () + "\nlat " + location.getLatitude () );
            nearestSpot ( location );
            Toast.makeText ( MainActivity.this, "changing", Toast.LENGTH_LONG ).show ();
        }
    }

    private void nearestSpot(Location lastLocation) {
        for (int i = 0; i < locationsArrayList.size (); i++) {

            distance = locationsArrayList.get ( i ).distanceTo ( lastLocation );

            if (distance < 20.0) {
                nearestDistance = distance;
                isNearAnyLocation = true;
                nearestSpot = locationsArrayList.get ( i );
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        unregisterReceiver(appBroadcastReciver);

    }

    public void CheckGpsStatus(){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(GpsStatus == true) {
            Toast.makeText ( MainActivity.this,"Gps Connected",Toast.LENGTH_LONG ).show ();
        } else {
            new AlertDialog.Builder ( MainActivity.this )
                    .setMessage ( " Sorry Gps is Disconnectef\n press connect to continue" )
                    .setPositiveButton ( "connect", new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                         Intent intent1 = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent1);
                        }
                    } ).setNegativeButton ( "cancel",null )
                    .create ()
                    .show ();
            Toast.makeText ( MainActivity.this,"Gps DisConnected",Toast.LENGTH_LONG ).show ();
        }
    }
}

