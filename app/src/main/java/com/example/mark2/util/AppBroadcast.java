package com.example.mark2.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mark2.NewReadingActivity;
import com.example.mark2.controller.MySingleton;
import com.example.mark2.data.DatabaseHandler;
import com.example.mark2.modal.Reading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppBroadcast extends BroadcastReceiver {

    private Context context;
    private List<Reading> unSyncReadings = new ArrayList<> (  );
    private double current;
    private double voltage;
    private String locationName;
    private double latitude;
    private double longitude;
    private String username;
    private String timeStamp;
    private String timeString;
    private int id;
    DatabaseHandler db;

    public AppBroadcast(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseHandler ( context.getApplicationContext () );

        if ( ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
            );
            if (noConnectivity) {
              //  Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            } else {
                unSyncReadings=db.getUnSyncedReadings ();

                for (int i =0;i<unSyncReadings.size ();i++) {
                    id = unSyncReadings.get ( i ).getId ();
                    current = unSyncReadings.get ( i ).getCurrent ();
                    voltage = unSyncReadings.get ( i ).getVoltage ();
                    locationName = unSyncReadings.get ( i ).getLocationTitle ();
                    latitude = unSyncReadings.get ( i ).getLatitude ();
                    longitude = unSyncReadings.get ( i ).getLongitude ();
                    username = unSyncReadings.get ( i ).getUserName ();
                    timeStamp = unSyncReadings.get ( i ).getTimeStamp ();
                    long time =Long.parseLong ( timeStamp );
                    timeString = TimeConverter.getDate ( time,"dd-MM-yyyy HH:mm:ss" );
                    sendToServer ();
                }

             //   Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendToServer(){

        final String currentToServer = String.valueOf ( current );
        final String voltageToServer = String.valueOf ( voltage );
        final String locationNameToServer = locationName;
        final String latitudeToServer = String.valueOf ( latitude );
        final String longitudeToServer = String.valueOf ( longitude );
        final String userNameToServer = username;
        final String timeStampToServer = timeString;

        StringRequest stringRequest=new StringRequest (
                Request.Method.POST, NewReadingActivity.READING_POST_URL,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {


                        db.updateReadingStatus ( id,NewReadingActivity.NAME_SYNCED_WITH_SERVER );
                    }
                }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                db.updateReadingStatus ( id,NewReadingActivity.NAME_NOT_SYNCED_WITH_SERVER );
            }
        }

        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<> ();

                //here we pass params
                parmas.put("action","addItem");
                parmas.put("current",currentToServer);
                parmas.put("voltage",voltageToServer);
                parmas.put("locationName",locationNameToServer);
                parmas.put("latitude",latitudeToServer);
                parmas.put("longitude",longitudeToServer);
                parmas.put("username",userNameToServer);
                parmas.put("timestamp",timeStampToServer);
                return parmas;
            }
        };
        // int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
        // RetryPolicy retryPolicy = new DefaultRetryPolicy (socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //  stringRequest.setRetryPolicy(retryPolicy);

        MySingleton.getInstance ( context.getApplicationContext () ).addToRequestQueue ( stringRequest );


    }
}
