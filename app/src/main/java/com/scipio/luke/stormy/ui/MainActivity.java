package com.scipio.luke.stormy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.scipio.luke.stormy.R;
import com.scipio.luke.stormy.weather.Current;
import com.scipio.luke.stormy.weather.Day;
import com.scipio.luke.stormy.weather.Forecast;
import com.scipio.luke.stormy.weather.Hour;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    public static final String LOCALITY = "LOCALITY";
    public static final String COUNTRY = "COUNTRY";

    private Forecast mForecast;

    @Bind(R.id.timeLabel)
    TextView mTimeLabel;
    @Bind(R.id.dailyLocationLabel)
    TextView mLocationLabel;
    @Bind(R.id.temperatureLabel)
    TextView mTemperatureValue;
    @Bind(R.id.humidityValue)
    TextView mHumidityValue;
    @Bind(R.id.precipValue)
    TextView mPrecipValue;
    @Bind(R.id.summaryLabel)
    TextView mSummaryLabel;
    @Bind(R.id.iconImageView)
    ImageView mIconImageView;
    @Bind(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    private double latitude;
    private double longitude;

    private String mLocality;
    private String mCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create the Google API Client and uses the addApi() method to request
        // the LocationServices API.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //set default to the Atlantic Ocean west of Africa
        longitude = 0;
        latitude = 0;

        mProgressBar.setVisibility(View.INVISIBLE);
        Log.d(TAG, "Main UI Code Running!");

//        new AsyncTaskTest(MainActivity.this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

        if (isNetworkAvailable())  // Check to see if there is network access
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

                // Use Geocoder class to convert lat-long into locality and country names
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mLocality = addresses.get(0).getLocality();
                if (mLocality == null)   //This addresses the problem that outside of the USA, city name can be located in Sub-Admin-Area field
                {
                    mLocality = addresses.get(0).getSubAdminArea();
                }

                mCountry = addresses.get(0).getCountryCode();

                if (addresses.size() > 0) {
                    Toast.makeText(this, "You are in "+mLocality + ", " + mCountry, Toast.LENGTH_LONG).show();
                }

                // get forecast from forecast.io API after the device's last know location is known

                mRefreshImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getForecast(latitude, longitude);
                    }
                });

                getForecast(latitude, longitude);

            } else {
                Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
            }
        } else {   // If network not available, notify user via Toast
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "Network not Available!");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    private void getForecast(double latitude, double longitude) {
        // Setting up the URL for activity_forecast.io
        String apiKey = "31541a244520ae1a3a93b88e12b4ab82";

        String forecastURL = "https://api.forecast.io/forecast/" + apiKey + "/"
                + latitude + "," + longitude;

        toggleRefresh();

        // The following code generates an asynchronous call to activity_forecast.io to return a JSON object
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(forecastURL)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(TAG, "Callback Failure!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleRefresh();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleRefresh();
                    }
                });

                try {
                    String jsonData = response.body().string();
                    Log.v(TAG, response.body().string());
                    if (response.isSuccessful()) {
                        mForecast = parseForecastDetail(jsonData);  //get current weather info
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });
                    } else {
                        //An alert dialog pops up if there is an error accessing activity_forecast.io
                        alertUserAboutError();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                } catch (JSONException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureValue.setText((int) mForecast.getCurrent().getTemperature() + "");
        mPrecipValue.setText((int) mForecast.getCurrent().getPrecipChance() + "%");
        mHumidityValue.setText(mForecast.getCurrent().getHumidity() + "%");
        mTimeLabel.setText("At " + mForecast.getCurrent().getFormattedTime() + " it is");
        mSummaryLabel.setText(mForecast.getCurrent().getSummary());
        mLocationLabel.setText(mForecast.getCurrent().getLocation());

        Drawable icon = getResources().getDrawable(mForecast.getCurrent().getIconId());
        mIconImageView.setImageDrawable(icon);
    }

    private Forecast parseForecastDetail(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        return forecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG, "From JSON:" + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current weather = new Current();
        weather.setIcon(currently.getString("icon"));
        weather.setTime(currently.getLong("time"));
        weather.setTemperature(currently.getDouble("temperature"));
        weather.setHumidity(currently.getDouble("humidity"));
        weather.setPrecipChance(currently.getDouble("precipProbability"));
        weather.setSummary(currently.getString("summary"));
        weather.setTimeZone(timeZone);
        weather.setLocation(mLocality + ", " + mCountry);

        Log.d(TAG, weather.getFormattedTime());

        return weather;
    }


    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hourlyForecast = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);

            Hour hour = new Hour();

            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTimeZone(timeZone);

            hourlyForecast[i] = hour;
            Log.i(TAG, "Forecast for Hour " + i + ":" + hourlyForecast[i].getSummary());
        }
        return hourlyForecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] dailyForecast = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);

            Day day = new Day();

            day.setIcon(data.getJSONObject(i).getString("icon"));
            day.setTime(data.getJSONObject(i).getLong("time"));
            day.setTemperatureMax(data.getJSONObject(i).getDouble("temperatureMax"));
            day.setSummary(data.getJSONObject(i).getString("summary"));
            day.setTimeZone(timeZone);

            dailyForecast[i] = day;
            Log.i(TAG, "Forecast for Day " + i + ":" + dailyForecast[i].getSummary());
        }
        return dailyForecast;
    }


    // This procedure set up a dialog to notify the user about error accessing activity_forecast.io
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    // This procedure checks to see if network exists and is available
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();

        //return true only if network exists and is available
        if ((info != null) && info.isConnected()) {
            return true;
        } else return false;
    }

    @OnClick (R.id.dailyButton)
    public void StartDailyActivity(View view){
        Intent intent = new Intent(this, DailyForecastActivity.class);

        intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast()); // Daily Forecast is written to Parcel

        intent.putExtra(LOCALITY, mLocality);
        intent.putExtra(COUNTRY, mCountry);
        startActivity(intent);
    }


    @OnClick (R.id.hourlyButton)
    public void StartHourlyActivity(View view){
        Intent intent = new Intent(this, HourlyForecastActivity.class);

        intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast()); // Hourly Forecast is written to Parcel

        intent.putExtra(LOCALITY, mLocality);
        intent.putExtra(COUNTRY, mCountry);
        startActivity(intent);
    }
}
