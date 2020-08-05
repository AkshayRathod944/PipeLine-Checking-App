package com.example.mark2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mark2.data.DatabaseHandler;
import com.example.mark2.modal.Reading;
import com.example.mark2.ui.ReadingItemAdapter;

import java.util.List;

public class AllReadingActivity extends AppCompatActivity {

   private TextView textView;
   private RecyclerView recyclerView;
   private ReadingItemAdapter adapter;
    private   String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_all_reading );
        getSupportActionBar ().setTitle ( "All Readings"  );

        recyclerView=findViewById ( R.id.recyclerView );

        username = getSharedPreferences ( "PREFERENCE",MODE_PRIVATE )
                .getString ( "userid","");

        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );


        DatabaseHandler handler=new DatabaseHandler ( AllReadingActivity.this );

     //   Reading reading=new Reading ( 1.11,2.22,"Akshay",0.00,0.01,"Aish","123456789",1 );
     //   handler.addReading ( reading );

       // Reading reading2=new Reading ( 1.11,2.22,"Akshay",0.00,0.01,"Aish1","223456789",0 );
       // handler.addReading ( reading2 );

      //  Reading reading3=new Reading ( 1.11,2.22,"Akshay",0.00,0.01,"Aish2","323456789",1 );
      //  handler.addReading ( reading3 );

        List<Reading> list=handler.getAllReadingByTimeStamp ();

        for (int j=0;j<list.size ();j++) {
          //  textView.append ( list.get ( j ).getLocationTitle ()+"\n" );
        }

        adapter=new ReadingItemAdapter ( AllReadingActivity.this,list, username);
        recyclerView.setAdapter ( adapter );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        Intent i= new Intent ( AllReadingActivity.this,MainActivity.class );
        startActivity ( i );
        finish ();
    }
}