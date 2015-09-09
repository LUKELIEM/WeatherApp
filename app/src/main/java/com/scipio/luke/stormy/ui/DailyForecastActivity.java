package com.scipio.luke.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scipio.luke.stormy.Adapters.DayAdapter;
import com.scipio.luke.stormy.R;
import com.scipio.luke.stormy.weather.Day;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;
    private String mLocality;
    private String mCountry;

    @Bind(R.id.dailyLocationLabel)
    TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);
/*
        String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                daysOfTheWeek);

        setListAdapter(adapter);

        */
        Intent intent = getIntent();

        //This is how you get a Parcelable array from an intent
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length,Day[].class);

        mLocality = intent.getStringExtra(MainActivity.LOCALITY);
        mCountry = intent.getStringExtra(MainActivity.COUNTRY);
        mLocationLabel.setText(mLocality+", "+mCountry);

        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);
    }


    // ListViews have a listener that acts much like the OnClickListener of a button.
    // l	The ListView where the click happened
    // v	The view that was clicked within the ListView
    // position	The position of the view in the list
    // d	The row id of the item that was clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfWeek = mDays[position].getDayOfTheWeek();
        String maxTemperature = mDays[position].getTemperatureMax()+"";
        String summary = mDays[position].getSummary();

        String message = String.format("On %s, temperature will reach a high of %s and it will be %s",
        dayOfWeek,
        maxTemperature,
        summary);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
