package com.example.mark2.data;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.mark2.R;
import com.example.mark2.modal.MyLocation;
import com.example.mark2.modal.Reading;
import com.example.mark2.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super ( context, Utils.DATABASE_NAME, null, Utils.DATABASE_VERSION );

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE="CREATE TABLE "+Utils.TABLE_NAME+"("+
                Utils.KEY_ID+" INTEGER PRIMARY KEY,"+
                Utils.KEY_LOCATION_NAME+" TEXT,"+
                Utils.KEY_LATITUDE+ " REAL NOT NULL," +
                Utils.KEY_LONGITUDE + " REAL NOT NULL"+")";

        String CREATE_READING_TABLE="CREATE TABLE "+Utils.READING_TABLE_NAME+"("+
                Utils.READING_KEY_ID+" INTEGER PRIMARY KEY,"+
                Utils.READING_KEY_CURRENT+" REAL NOT NULL,"+
                Utils.READING_KEY_VOLTAGE+ " REAL NOT NULL," +
                Utils.READING_KEY_USERNAME+ " TEXT NOT NULL,"+
                Utils.READING_KEY_LATITUDE+ " REAL NOT NULL," +
                Utils.READING_KEY_LONGITUDE+ " REAL NOT NULL," +
                Utils.READING_KEY_LOCATION_TITLE+ " TEXT NOT NULL," +
                Utils.READING_KEY_TIMESTAMP+ " TEXT NOT NULL," +
                Utils.READING_KEY_STATUS+ " INT NOT NULL"+")";

        db.execSQL ( CREATE_READING_TABLE );

        db.execSQL ( CREATE_TABLE );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String DROP_TABLE=String.valueOf ( R.string.drop_db);
        db.execSQL ( DROP_TABLE, new String[]{Utils.TABLE_NAME} );
        //creating again db
        onCreate ( db );

    }

    //adding data to database
    public void addLocation(MyLocation myLocation){

        SQLiteDatabase db=this.getWritableDatabase ();
        //creating contentValue pair to store location
        ContentValues values = new ContentValues (  );
        values.put ( Utils.KEY_LOCATION_NAME,myLocation.getLocationName () );
        values.put ( Utils.KEY_LATITUDE,myLocation.getLatitude () );
        values.put ( Utils.KEY_LONGITUDE,myLocation.getLongitude () );



        //adding to table
        db.insert ( Utils.TABLE_NAME,null,values );
        db.close ();


    }

    //get all Location
    public List<MyLocation> getAllLocations(){

        SQLiteDatabase db = this.getReadableDatabase ();

        List<MyLocation> locationList = new ArrayList<> (  );

        String allLocations="SELECT * FROM "+Utils.TABLE_NAME;

        Cursor cursor=db.rawQuery ( allLocations,null );

        //looping through all locations

        if (cursor.moveToFirst ()){

            do {
                //extracting contact from db
                MyLocation location = new MyLocation (  );
                location.setId ( Integer.parseInt ( cursor.getString ( 0 ) ) );
                location.setLocationName (  cursor.getString ( 1 ));
                location.setLatitude ( cursor.getDouble (2 ) );
                location.setLongitude ( cursor.getDouble (3 ) );
                //adding all contact to contact list
                locationList.add ( location );

            }while (cursor.moveToNext ());
        }

        return locationList;
    }

    //delete all
    public void deleteAll(){
        SQLiteDatabase db=this.getWritableDatabase ();
        db.execSQL ( "DELETE FROM "+Utils.TABLE_NAME );
        db.close ();
    }

    //Get contacts count
    public int getCount() {
        String countQuery = "SELECT * FROM " + Utils.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();

    }
    //Delete single contact
    public void deleteLocation(MyLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Utils.TABLE_NAME, Utils.KEY_ID + "=?",
                new String[]{String.valueOf(location.getId())});
        db.close();
    }

    public void addReading(Reading reading){
        SQLiteDatabase db= this.getWritableDatabase ();

        ContentValues values = new ContentValues (  );
        values.put ( Utils.READING_KEY_CURRENT,reading.getCurrent () );
        values.put ( Utils.READING_KEY_VOLTAGE,reading.getVoltage () );
        values.put ( Utils.READING_KEY_USERNAME,reading.getUserName () );
        values.put ( Utils.READING_KEY_LATITUDE,reading.getLatitude () );
        values.put ( Utils.READING_KEY_LONGITUDE,reading.getLongitude () );
        values.put ( Utils.READING_KEY_LOCATION_TITLE,reading.getLocationTitle () );
        values.put ( Utils.READING_KEY_TIMESTAMP,reading.getTimeStamp () );
        values.put ( Utils.READING_KEY_STATUS,reading.getStatus () );

        db.insert ( Utils.READING_TABLE_NAME,null,values );

        Log.d ("db","item added");
        db.close ();
    }

    public List<Reading> getAllReadingByTimeStamp(){
        List<Reading> readingList = new ArrayList<> (  );
        SQLiteDatabase db = this.getReadableDatabase ();

        String query = "SELECT * FROM "+Utils.READING_TABLE_NAME+" ORDER BY "+Utils.READING_KEY_TIMESTAMP+" DESC";

        Cursor cursor=db.rawQuery ( query,null );

        if (cursor.moveToFirst ()){
            do{
                Reading reading=new Reading (  );
                reading.setId (Integer.parseInt ( cursor.getString ( 0 ) ));
                reading.setCurrent ( Double.parseDouble ( cursor.getString ( 1 ) ) );
                reading.setVoltage ( Double.parseDouble ( cursor.getString ( 2 ) ) );
                reading.setUserName (cursor.getString ( 3 ));
                reading.setLatitude ( Double.parseDouble ( cursor.getString ( 4 ) ) );
                reading.setLongitude ( Double.parseDouble ( cursor.getString ( 5 ) ) );
                reading.setLocationTitle ( cursor.getString ( 6 ) );
                reading.setTimeStamp (  ( cursor.getString ( 7 ) ) );
                reading.setStatus ( Integer.parseInt ( cursor.getString ( 8 ) ) );

                readingList.add ( reading );

            }while (cursor.moveToNext ());


        }
        return readingList;
    }

    //Get contacts count
    public int getReadingCount() {
        String countQuery = "SELECT * FROM " + Utils.READING_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public boolean updateReadingStatus(int id,int status){
        SQLiteDatabase db=this.getWritableDatabase ();
        ContentValues contentValues = new ContentValues (  );
        contentValues.put ( Utils.READING_KEY_STATUS,status );
        db.update ( Utils.READING_TABLE_NAME,contentValues,Utils.READING_KEY_ID+"="+id,null );
        db.close ();
        return true;
    }

    public List<Reading> getUnSyncedReadings() {
        List<Reading> readingList = new ArrayList<> ();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + Utils.READING_TABLE_NAME + " WHERE " + Utils.READING_KEY_STATUS + " = 0;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst ()) {
            do { Reading reading=new Reading (  );
                reading.setId (Integer.parseInt ( cursor.getString ( 0 ) ));
                reading.setCurrent ( Double.parseDouble ( cursor.getString ( 1 ) ) );
                reading.setVoltage ( Double.parseDouble ( cursor.getString ( 2 ) ) );
                reading.setUserName (cursor.getString ( 3 ));
                reading.setLatitude ( Double.parseDouble ( cursor.getString ( 4 ) ) );
                reading.setLongitude ( Double.parseDouble ( cursor.getString ( 5 ) ) );
                reading.setLocationTitle ( cursor.getString ( 6 ) );
                reading.setTimeStamp ( ( cursor.getString ( 7 ) ) );
                reading.setStatus ( Integer.parseInt ( cursor.getString ( 8 ) ) );

                //add contact objects to our list
                readingList.add ( reading );
            } while (cursor.moveToNext ());
        }
        return readingList;
    }


}


