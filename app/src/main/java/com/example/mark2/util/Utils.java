package com.example.mark2.util;

public class Utils {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="myLocation_db";
    public static final String TABLE_NAME="myLocations";

    //table contain
    public static final String KEY_ID="id";
    public static final String KEY_LOCATION_NAME="location_name";
    public static final String KEY_LATITUDE="Latitude";
    public static final String KEY_LONGITUDE="longitude";


    //for reading table

    public static final int READING_DATABASE_VERSION=1;
    public static final String READING_DATABASE_NAME="READINGS_db";
    public static final String READING_TABLE_NAME="reading_table";

    //Reading table contants

    public static final String READING_KEY_ID="id";
    public static final String READING_KEY_CURRENT="CURRENT";
    public static final String READING_KEY_VOLTAGE="VOLTAGE";
    public static final String READING_KEY_USERNAME="USERNAME";
    public static final String READING_KEY_LATITUDE="lat";
    public static final String READING_KEY_LONGITUDE="lon";
    public static final String READING_KEY_LOCATION_TITLE="loc_title";
    public static final String READING_KEY_TIMESTAMP="timestamp";
    public static final String READING_KEY_STATUS="status";
}
