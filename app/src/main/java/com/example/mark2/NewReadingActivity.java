package com.example.mark2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mark2.controller.MySingleton;
import com.example.mark2.data.DatabaseHandler;
import com.example.mark2.modal.Reading;
import com.example.mark2.util.TimeConverter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewReadingActivity extends AppCompatActivity {

    //layout parameters
    private TextView locationNameTextView,locationCoOrdnate,userNameTextView,timeStamp;
    private EditText currentEditText,voltageEditText;
    private Button saveButton;
    public final static String READING_POST_URL ="https://script.google.com/macros/s/AKfycbwWfeHw4n6kHLz8EyV8S22fH_g7U1tCHEqPmEQrUZc7I-QiLywF/exec?action=addItem";

    //database
    private DatabaseHandler db = new DatabaseHandler ( NewReadingActivity.this );
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;
    private   String username,currentValue,voltageValue;
    private double current,voltage;
    private  Reading reading;
    private  double latitude;
    private  double longitude;
    private String locationName;
    private  long timeInMilli;

    private String timeString;
    private String dbTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_new_reading );
        getSupportActionBar ().setTitle ( "New Reading" );

        locationNameTextView=findViewById ( R.id.locationNameSave );
        locationCoOrdnate=findViewById ( R.id.locationCoordnateSave );
        userNameTextView=findViewById ( R.id.userNameSave );
        timeStamp=findViewById ( R.id.timeStampNew );
        currentEditText=(EditText) findViewById ( R.id.edittextCurrent );
        voltageEditText=(EditText) findViewById ( R.id.editTextVoltage );
        saveButton=findViewById ( R.id.SaveReading );


        Intent replyIntent=getIntent ();
        latitude = replyIntent.getDoubleExtra ( "latitude",0 );
        longitude = replyIntent.getDoubleExtra ( "longitude",0 );
        locationName = replyIntent.getStringExtra ( "provider" );

        username = getSharedPreferences ( "UserName",MODE_PRIVATE ).getString ( "userid","");

        timeInMilli = Calendar.getInstance ().getTimeInMillis ();
        dbTime = String.valueOf ( timeInMilli );
        timeString= TimeConverter.getDate ( timeInMilli,"dd-MM-yyyy HH:mm:ss" );

        userNameTextView.setText (  username);
        locationCoOrdnate.setText ( "latitude "+  latitude +" longitude "+ longitude );
        locationNameTextView.setText ( locationName );
        timeStamp.setText ( timeString );




        saveButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                currentValue = currentEditText.getText ().toString ();
                voltageValue = voltageEditText.getText ().toString ();

                if(currentValue.isEmpty ()&&voltageValue.isEmpty ()){
                    new AlertDialog.Builder ( NewReadingActivity.this )
                            .setMessage ( "Enter the Value's" )
                            .setPositiveButton ( "ok", null ).create ().show ();
                }else if(currentValue.isEmpty ()){
                    new AlertDialog.Builder ( NewReadingActivity.this )
                            .setMessage ( "Enter the Value of current" )
                            .setPositiveButton ( "ok", null ).create ().show ();
                }else if (voltageValue.isEmpty ()){
                    new AlertDialog.Builder ( NewReadingActivity.this )
                            .setMessage ( "Enter the Value of voltage" )
                            .setPositiveButton ( "ok", null ).create ().show ();
                } else {
                    current = Double.parseDouble ( currentValue );
                    voltage = Double.parseDouble ( voltageValue );
                    saveOnServer ();
                }
                //Toast.makeText ( NewReadingActivity.this,currentValue+" "+voltageValue,Toast.LENGTH_LONG ).show ();
            }
        } );

    }

    private void addReadingToLocal(int status){

        reading = new Reading ( current, voltage, username, latitude, longitude, locationName, dbTime, status );
        db.addReading ( reading );
    }

    private void saveOnServer(){

        final ProgressDialog loading = ProgressDialog.show(NewReadingActivity.this,"Adding Item","Please wait");
        final String currentToServer = String.valueOf ( current );
        final String voltageToServer = String.valueOf ( voltage );
        final String locationNameToServer = locationName;
        final String latitudeToServer = String.valueOf ( latitude );
        final String longitudeToServer = String.valueOf ( longitude );
        final String userNameToServer = username;
        final String timeStampToServer = timeString;

        StringRequest stringRequest=new StringRequest (
                Request.Method.POST, READING_POST_URL,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        addReadingToLocal ( NAME_SYNCED_WITH_SERVER );
                        loading.dismiss();
                        Toast.makeText(NewReadingActivity.this,response,Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                addReadingToLocal ( NAME_NOT_SYNCED_WITH_SERVER );
                loading.dismiss ();
                Toast.makeText(NewReadingActivity.this,error.toString (),Toast.LENGTH_LONG).show();

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

        MySingleton.getInstance ( getApplicationContext () ).addToRequestQueue ( stringRequest );


    }



}