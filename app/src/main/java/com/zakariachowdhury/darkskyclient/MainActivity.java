package com.zakariachowdhury.darkskyclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.zakariachowdhury.darkskyclient.event.ErrorEvent;
import com.zakariachowdhury.darkskyclient.event.WeatherEvent;
import com.zakariachowdhury.darkskyclient.model.Currently;
import com.zakariachowdhury.darkskyclient.service.WeatherServiceProvider;
import com.zakariachowdhury.darkskyclient.util.WeatherIconUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    @BindView(R.id.temperatureContainer)
    LinearLayout temperatureContainer;

    @BindView(R.id.temperatureTextView)
    TextView temperatureTextView;

    @BindView(R.id.summaryTextView)
    TextView summaryTextView;

    @BindView(R.id.iconImageView)
    ImageView iconImageView;

    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initGoogleApiClient();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WeatherEvent weatherEvent) {
        Currently currently = weatherEvent.getWeather().getCurrently();
        temperatureTextView.setText(String.valueOf(Math.round(currently.getTemperature())));
        summaryTextView.setText(currently.getSummary());
        temperatureContainer.setVisibility(View.VISIBLE);

        String icon = currently.getIcon();
        if (WeatherIconUtil.ICONS.get(icon) != null) {
            iconImageView.setImageResource(WeatherIconUtil.ICONS.get(icon));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ErrorEvent errorEvent) {
        Toast.makeText(this, errorEvent.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void requestCurrentWeather() {
        getDeviceLocation();

        if (mLocationPermissionGranted && mLastKnownLocation != null) {
            double lat = mLastKnownLocation.getLatitude();
            double lng = mLastKnownLocation.getLongitude();

            WeatherServiceProvider weatherServiceProvider = new WeatherServiceProvider(this);
            weatherServiceProvider.getWeather(lat, lng);
        } else {
            Toast.makeText(this, R.string.error_google_map_api, Toast.LENGTH_LONG).show();
        }
    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        requestCurrentWeather();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.error_google_map_api, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestCurrentWeather();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, R.string.error_google_map_api, Toast.LENGTH_LONG).show();
    }
}
