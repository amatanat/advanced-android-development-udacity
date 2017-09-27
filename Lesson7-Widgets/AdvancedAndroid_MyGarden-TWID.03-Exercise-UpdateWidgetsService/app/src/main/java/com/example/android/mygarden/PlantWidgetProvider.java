package com.example.android.mygarden;

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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.MainActivity;
import com.example.android.mygarden.ui.PlantDetailActivity;

import java.nio.BufferUnderflowException;

import static com.example.android.mygarden.PlantWateringService.startActionUpdatePlantWidgets;
import static com.example.android.mygarden.ui.PlantDetailActivity.EXTRA_PLANT_ID;

public class PlantWidgetProvider extends AppWidgetProvider {



    // DONE (1): Modify updateAppWidget method to take an image recourse and call
    // setImageViewResource to update the widget’s image
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int imgRecourse, int appWidgetId, long plantId,
                                boolean hideWaterDropButton) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews remoteViews;

        if (width < 300){
            remoteViews = getSinglePlantRemoteView(context, imgRecourse, plantId, hideWaterDropButton);
        } else{
            remoteViews = getGradenGridRemoteViews(context);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private static RemoteViews getGradenGridRemoteViews(Context context){
        return null;
    }

    private static RemoteViews getSinglePlantRemoteView(Context context,
                                                        int imgRecourse,
                                                        long plantId,
                                                        boolean hideWaterDropButton){
        Intent intent;

        if (plantId == PlantContract.INVALID_PLANT_ID){

            // Create an Intent to launch MainActivity when clicked
            intent = new Intent(context, MainActivity.class);

        } else {
            // create an intent to launcht PlantDetailActivity
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(EXTRA_PLANT_ID, plantId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);

        // Add the wateringservice click handler
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        wateringIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);

        // update widget's image
        views.setImageViewResource(R.id.widget_plant_image, imgRecourse);

        // set plant id as the text of textview
        views.setTextViewText(R.id.widget_plant_id, String.valueOf(plantId));

        // hide water drop image
        if (hideWaterDropButton)
            views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.widget_water_button,View.VISIBLE);

        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // DONE (2): Move the updateAppWidget loop to a new method called updatePlantWidgets and pass through the image recourse
        // There may be multiple widgets active, so update all of them
        // DONE (4): Call startActionUpdatePlantWidgets in onUpdate as well as in AddPlantActivity and PlantDetailActivity (add and delete plants)
        startActionUpdatePlantWidgets(context);
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int[] appWidgetIds, int imgRecourse, long plantId,
                                          boolean hideWaterDropButton){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager,imgRecourse,appWidgetId,plantId,hideWaterDropButton);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        startActionUpdatePlantWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

}
