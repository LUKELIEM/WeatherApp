package com.scipio.luke.stormy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scipio.luke.stormy.Adapters.HourAdapter;
import com.scipio.luke.stormy.R;
import com.scipio.luke.stormy.weather.Hour;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends Activity {

    private Hour[] mHours;
    private String mLocality;
    private String mCountry;

//    @Bind(R.id.hourlyLocationLabel)
//    TextView mHourlyLocationLabel;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        //This is how you get a Parcelable array from an intent
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        mLocality = intent.getStringExtra(MainActivity.LOCALITY);
        mCountry = intent.getStringExtra(MainActivity.COUNTRY);
//        mHourlyLocationLabel.setText(mLocality+", "+mCountry);

        // specify an adapter, make sure to pass in the Context
        HourAdapter adapter = new HourAdapter(this, mHours);
        mRecyclerView.setAdapter(adapter);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
    }
}
