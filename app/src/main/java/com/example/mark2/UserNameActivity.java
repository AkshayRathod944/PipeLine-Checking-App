package com.example.mark2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserNameActivity extends AppCompatActivity {

    private EditText userName,emailText;
    private Button NextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_user_name );
      //  getActionBar ().setTitle ( "Register" );

        NextButton =findViewById ( R.id.Next_Button );
        userName =findViewById ( R.id.UserName_EditText );
        emailText =findViewById ( R.id.email_EditText );


        NextButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                String username=userName.getText ().toString ().trim ();
                String email=emailText.getText ().toString ().trim ();
                if (username.isEmpty ()&&email.isEmpty ()){
                    new AlertDialog.Builder ( UserNameActivity.this )
                            .setMessage ( "Please Enter User Name and Email" )
                            .setPositiveButton ( "ok",null ).create ().show ();
                }else if (username.isEmpty ()) {
                    new AlertDialog.Builder ( UserNameActivity.this )
                            .setMessage ( "Please Enter UserName" )
                            .setPositiveButton ( "ok",null ).create ().show ();
                }else if (email.isEmpty ()) {
                    new AlertDialog.Builder ( UserNameActivity.this )
                            .setMessage ( "Please Enter E-Mail" )
                            .setPositiveButton ( "ok",null ).create ().show ();
                }else {
                    SharedPreferences sharedPreferences = getSharedPreferences ( "UserName", MODE_PRIVATE );
                    SharedPreferences.Editor editor = sharedPreferences.edit ();
                    editor.putString ( "userid", username );
                    editor.putString ( "email-id", email );
                    editor.apply ();


                    Intent intent = new Intent ( UserNameActivity.this, UpdateLocationActivity.class );
                    startActivity ( intent );
                    finish ();
                }


            }
        } );


    }
}