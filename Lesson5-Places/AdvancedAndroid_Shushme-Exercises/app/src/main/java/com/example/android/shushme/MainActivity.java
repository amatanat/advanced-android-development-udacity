package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest;
import android.Manifest.permission;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.example.android.shushme.provider.PlaceContract;
import com.example.android.shushme.provider.PlaceContract.PlaceEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 100;

    // placepicket intent builder start intent request code
    private final int PLACE_PICKER = 200;

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private CheckBox mLocationPermissionCheckbox;
    private GoogleApiClient googleApiClient;
    private Geofencing mGeofencing;
    private boolean mIsEnabled;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);


        // Initialize the switch state and Handle enable/disable switch change
        Switch onOffSwitch = (Switch) findViewById(R.id.enable_switch);
        mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;
                editor.commit();
                if (isChecked) mGeofencing.registerAllGeofences();
                else mGeofencing.unregisterAllGeofences();
            }

        });

        //  (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
        googleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Places.GEO_DATA_API)
            .addApi(LocationServices.API)
            .enableAutoManage(this,this)
            .build();

        mGeofencing = new Geofencing(this, googleApiClient);
    }

    //  (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        refreshPlacesData();
        Log.i(TAG, "Google APi Client: connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Google APi Client: connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Google APi Client: connection failed");
    }


    //  (7) Override onResume and inside it initialize the location permissions checkbox

    @Override
    protected void onResume() {
        super.onResume();

        mLocationPermissionCheckbox = (CheckBox) findViewById(R.id.checkBox);
        // check if locaiton permission is granted or not
        if (ContextCompat.checkSelfPermission(this,
            permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){

            // permission isn't granted
            mLocationPermissionCheckbox.setChecked(false);
        } else {
            // permission is granted
            mLocationPermissionCheckbox.setChecked(true);

        }
    }

    public void addPlaceButton(View view){
        if (ContextCompat.checkSelfPermission(this,
            permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // permission is not granted

            Toast.makeText(MainActivity.this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
        }

        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, "Google Play Services Repairable Exception");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, "Google Play Services Not Available Exception");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER && resultCode == RESULT_OK){
                Place places = PlacePicker.getPlace(this,data);
            if (places == null){
                Log.i(TAG, "No place is selected");
                return;
            }

            // extract place data from Place
//            String placeName = places.getName().toString();
//            String placeAddress = places.getAddress().toString();
            String placeId = places.getId();

            // save placeId in DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(PlaceEntry.COLUMN_PLACE_ID, placeId);
            getContentResolver().insert(PlaceEntry.CONTENT_URI, contentValues);
        }

        refreshPlacesData();
    }

    //  (8) Implement onLocationPermissionClicked to handle the CheckBox click event

    public void onLocationPermissionClicked(View view){
        ActivityCompat.requestPermissions(this,
            new String[]{permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_LOCATION);
    }

    public void refreshPlacesData(){

        Uri uri = PlaceEntry.CONTENT_URI;
        // get all data with the given uri
        Cursor data = getContentResolver().query(
            uri,
            null,
            null,
            null,
            null
        );

        // if there is no data in given uri in db => return
        if (data == null || data.getCount() == 0) return;

        List<String> guide = new ArrayList<>();

        // iterate through the data
        while (data.moveToNext()){

            // get 'PLACE ID' column index and get 'ID' string in this cloumn and add it to Arraylist
            guide.add(data.getString(data.getColumnIndexOrThrow(PlaceEntry.COLUMN_PLACE_ID)));

            // get the places corresponding to the ID from GeoDataApi
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(
                googleApiClient, guide.toArray(new String[guide.size()]));

            // to retrieve the server's result setup the callback on PendingResult
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                // receive place in this method
                public void onResult(@NonNull PlaceBuffer places) {
                    // update adatper data
                    mAdapter.swapPlaces(places);
                    mGeofencing.updateGeofencesList(places);
                    if (mIsEnabled) mGeofencing.registerAllGeofences();
                }
            });

        }
    }

}
