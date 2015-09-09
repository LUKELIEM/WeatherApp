package com.scipio.luke.stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Luke on 7/28/2015.
 */
public class Day implements Parcelable{
    private String mIcon;
    private String mSummary;
    private String mTimeZone;
    private long mTime;
    private double mTemperatureMax;

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperatureMax() {
        return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public int getIconId(){
        return Forecast.getIconID(mIcon);
    }

    public String getDayOfTheWeek(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");

        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        Date dateTime = new Date(getTime()*1000);

        return formatter.format(dateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Write data into the Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mTemperatureMax);
        dest.writeString(mIcon);
        dest.writeString(mTimeZone);
    }

    // Read data out of the Parcel
    private Day(Parcel in){
        mTime = in.readLong();
        mSummary = in.readString();
        mTemperatureMax = in.readDouble();
        mIcon = in.readString();
        mTimeZone = in.readString();
    }

    public Day() {}

    //Implemented based on example provided in Android documentation
    public static final Creator<Day> CREATOR = new Creator<Day>(){

        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
