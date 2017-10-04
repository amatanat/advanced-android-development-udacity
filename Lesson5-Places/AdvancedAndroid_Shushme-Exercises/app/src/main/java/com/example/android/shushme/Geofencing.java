package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amatanat on 04.10.17.
 */

public class Geofencing implements ResultCallback<Status> {

  private static final String TAG = Geofencing.class.getName();

  private static final float GEOFENCE_RADIUS = 50; // 50 meters
  private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours

  private Context mContext;
  private GoogleApiClient mGoogleApiClient;

  private PendingIntent mGeofencePendingIntent;
  private List<Geofence> mGeofenceList;

  public Geofencing(Context context, GoogleApiClient googleApiClient) {
    mContext = context;
    mGoogleApiClient = googleApiClient;
    mGeofencePendingIntent = null;
    mGeofenceList = new ArrayList<>();
  }

  public void registerAllGeofences(){
    if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()
        || mGeofenceList == null || mGeofenceList.size() == 0) return;

    try{
      LocationServices.GeofencingApi.addGeofences(
          mGoogleApiClient,
          getGeofencingRequest(),
          getGeofencePendingIntent()
      ).setResultCallback(this);
    }catch (SecurityException securityException){
        Log.e(TAG, securityException.getMessage());
    }
  }

  public void unregisterAllGeofences(){
    if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) return;

    try{
      LocationServices.GeofencingApi.removeGeofences(
          mGoogleApiClient,
          getGeofencePendingIntent()
      ).setResultCallback(this);
    }catch (SecurityException securityException){
      Log.e(TAG, securityException.getMessage());
    }
  }

  public void updateGeofencesList(PlaceBuffer places){
    mGeofenceList = new ArrayList<>();
    if (places == null || places.getCount() == 0) return;

    for (Place place: places ){
      String placeUID = place.getId();
      double latitude = place.getLatLng().latitude;
      double longitute = place.getLatLng().longitude;
      Geofence geofence = new Geofence.Builder()
          .setRequestId(placeUID)
          .setExpirationDuration(GEOFENCE_TIMEOUT)
          .setCircularRegion(latitude, longitute, GEOFENCE_RADIUS)
          .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
          .build();
      mGeofenceList.add(geofence);
    }
  }

  private GeofencingRequest getGeofencingRequest(){
    GeofencingRequest.Builder geofencingRequest = new GeofencingRequest.Builder();
    geofencingRequest.addGeofences(mGeofenceList);
    geofencingRequest.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    return geofencingRequest.build();
  }

  private PendingIntent getGeofencePendingIntent(){

    // reuse pending intent if we have it
    if (mGeofencePendingIntent != null) return mGeofencePendingIntent;

    // create a new pending intent
    Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
    mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return mGeofencePendingIntent;

  }

  @Override
  public void onResult(@NonNull Status status) {
    Log.e(TAG, status.getStatus().toString());
  }
}
