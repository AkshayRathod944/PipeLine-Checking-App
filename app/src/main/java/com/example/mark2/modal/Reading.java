package com.example.mark2.modal;


public class Reading {

    private int id;
    private double current;
    private double voltage;
    private String userName;
    private double latitude;
    private double longitude;
    private String locationTitle;
    private String timeStamp;
    private int status;

    public Reading() {

    }

    public Reading(double current, double voltage, String userName, double latitude, double longitude, String locationTitle, String timeStamp, int status) {
        this.current = current;
        this.voltage = voltage;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationTitle = locationTitle;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reading{" +
                "id=" + id +
                ", current=" + current +
                ", voltage=" + voltage +
                ", userName='" + userName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", locationTitle='" + locationTitle + '\'' +
                ", timeStamp=" + timeStamp +
                ", status=" + status +
                '}';
    }
}

