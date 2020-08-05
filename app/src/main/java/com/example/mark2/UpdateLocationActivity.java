package com.example.mark2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mark2.controller.MySingleton;
import com.example.mark2.data.DatabaseHandler;
import com.example.mark2.data.ListAsyncResponse;
import com.example.mark2.modal.MyLocation;
import com.example.mark2.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateLocationActivity extends AppCompatActivity {

    private TextView textView;
    private int i = 0;
    private Button button, nextButton;
    private ArrayList<MyLocation> locationArrayList = new ArrayList<> ();
    private List<MyLocation> dbArrayList = new ArrayList<> ();
    private ProgressDialog progressDialog;
    private DatabaseHandler db = new DatabaseHandler ( UpdateLocationActivity.this);

    private String url2 = "https://script.google.com/macros/s/AKfycbynbZwA7wX-VbqgW7HCPPoqm_cZJv3mBVSFRrhUk4Xm8-ag8N_o/exec?action=getLocation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_update_location );
        getSupportActionBar ().setTitle ( "Update Location" );

        textView = findViewById ( R.id.textView );
        button = findViewById ( R.id.button_save );
        nextButton = findViewById ( R.id.gonext );

        dbArrayList = db.getAllLocations ();
        for (int i = 0; i < dbArrayList.size (); i++) {
            MyLocation myLocation = dbArrayList.get ( i );
            String name = myLocation.getLocationName ();
            double lat = myLocation.getLatitude ();
            double lon = myLocation.getLongitude ();

            //   Toast.makeText ( UpdateLocationActivity.this,"Database Updated",Toast.LENGTH_LONG ).show ();
            textView.append ( name + "  " + String.valueOf ( lat ) + "  " + String.valueOf ( lon ) + "\n" );
        }

        button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    progressDialog = new ProgressDialog ( UpdateLocationActivity.this );
                    progressDialog.setTitle ( "Update DB" );
                    progressDialog.setMessage ( "fetching Data Please Wait!!!" );
                    progressDialog.show ();
                    deleteAll ();
                    update ( new ListAsyncResponse () {
                        @Override
                        public void processFinished(ArrayList<MyLocation> locationArrayList) {
                            for (int i = 0; i < locationArrayList.size (); i++) {
                                MyLocation location1 = new MyLocation ();
                                location1 = locationArrayList.get ( i );
                                db.addLocation ( location1 );
                            }
                            i = 1;
                            Toast.makeText ( UpdateLocationActivity.this, "Database Updated", Toast.LENGTH_LONG ).show ();
                        }
                    } );
                } else {
                    new AlertDialog.Builder ( UpdateLocationActivity.this )
                            .setMessage ( " Sorry You can'tupdate again\n data is already updated" )
                            .setPositiveButton ( "ok", null ).create ().show ();
                }
            }
        }
        );


        nextButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent ( UpdateLocationActivity.this,MainActivity.class );
                startActivity ( intent );
                finish ();
            }
        } );
    }

    public void deleteAll(){
        dbArrayList=db.getAllLocations ();

        for (int i=0;i<dbArrayList.size ();i++){
            MyLocation myLocation=dbArrayList.get ( i );
            String name=myLocation.getLocationName (  );
            double lat=myLocation.getLatitude (  );
            double lon=myLocation.getLongitude (  );

            //   Toast.makeText ( UpdateLocationActivity.this,"Database Updated",Toast.LENGTH_LONG ).show ();
            //  textView.append ( name+"  "+String.valueOf (lat  )+"  "+String.valueOf ( lon )+"\n" );
            //deleting items
            db.deleteLocation ( myLocation );
        }
    }


    private void update(final ListAsyncResponse callBack) {

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest ( Request.Method.GET, url2, null, new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject response) {

                progressDialog.dismiss ();
                try {
                    JSONArray jsonArray=response.getJSONArray ( "items" );

                    for (int i=0;i<jsonArray.length ();i++){

                        JSONObject item = jsonArray.getJSONObject (i );
                        String name=item.getString ( "name" );
                        double lat=item.getDouble ( "lat" );
                        double lon=item.getDouble ( "lon" );

                        MyLocation location=new MyLocation ( name,lat,lon );
                        locationArrayList.add ( location );

                        // textView.append ( name+"  "+String.valueOf (lat  )+"  "+String.valueOf ( lon )+"\n" );
                    }


                } catch (JSONException e) {
                    e.printStackTrace ();

                }
                if (null != callBack) {
                    callBack.processFinished(locationArrayList);
                }

            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss ();
                Toast.makeText ( UpdateLocationActivity.this,"Database not Updated\nplease chek your internet connection",Toast.LENGTH_LONG ).show ();

            }
        } );
        MySingleton.getInstance ( getApplicationContext () ).addToRequestQueue ( jsonObjectRequest );

    }

}