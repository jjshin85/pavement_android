package com.pnpc.pavement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView serviceButton;
    boolean serviceStarted;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int LOCATION_REQUEST = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState != null){
            serviceStarted = savedInstanceState.getBoolean("serviceStarted");
        }

        //Checking SDK. If above 6.0, checks location permission. If not granted, requests it.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(hasLocationPermission() == false) {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            }
        }

        serviceButton = (ImageView) findViewById(R.id.service_button);

        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, PavementService.class);
                if (serviceStarted == false) {
                    startService(serviceIntent);
                    serviceStarted = true;
                    setServiceButtonImage();
                    serviceButton.setColorFilter(Color.BLUE, Mode.SRC_ATOP);

                    Log.i("PavementService", "startService called");
                } else {
                    stopService(serviceIntent);
                    serviceStarted = false;
                    serviceButton.setColorFilter(null);
                    setServiceButtonImage();
                    Log.i("PavementService", "stopService called");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Drawable statsIcon = getResources().getDrawable(R.drawable.stats_tab);
        statsIcon.setColorFilter(null);
        Drawable pavementIcon = getResources().getDrawable(R.drawable.pavement_tab);
        pavementIcon.setColorFilter(Color.BLUE, Mode.SRC_ATOP);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_stats) {
            Intent intent = new Intent(MainActivity.this, RecalibrateActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST){
            if(hasLocationPermission()){
            }
        }
    }
    private boolean hasLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
        }
        else{
            return true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(serviceStarted == true){
            outState.putBoolean("serviceStarted", serviceStarted);
        }
        Log.i("onSavedInstanceState", "outstate: serviceStarted " + serviceStarted);
        super.onSaveInstanceState(outState);
    }

    public void setServiceButtonImage(){
        if(serviceStarted == false){
            serviceButton.setImageResource(R.drawable.toggle);
        }
        else{
            serviceButton.setImageResource(R.drawable.stop_3);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        serviceStarted = savedInstanceState.getBoolean("serviceStarted");
        if(serviceStarted == true){
            setServiceButtonImage();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}
