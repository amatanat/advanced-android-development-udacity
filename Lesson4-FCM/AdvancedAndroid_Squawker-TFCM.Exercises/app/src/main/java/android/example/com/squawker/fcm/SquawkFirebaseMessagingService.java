package android.example.com.squawker.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkDatabase;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by amatanat.
 */

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {

    private final String KEY_AUTHOR = "author";
    private final String KEY_AUTHOR_KEY = "authorKey";
    private final String KEY_MESSAGE = "message";
    private final String KEY_DATE= "date";

    private static final int NOTIFICATION_MAX_CHARACTERS = 30;
    private static final String TAG = SquawkFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();

        if(data.size() > 0){

            Log.d(TAG,  "Data$$$$$$$$$ " + data);

            saveData(data);
            showNofitication(data);
        }


    }

    private void showNofitication(Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Create the pending intent to launch the activity
        PendingIntent pendingIntent = PendingIntent
                                .getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);

        String author = data.get(KEY_AUTHOR);
        String message = data.get(KEY_MESSAGE);

                        // If the message is longer than the max number of characters we want in our
                                // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"channel_id")
                                .setSmallIcon(R.drawable.ic_duck)
                                .setContentTitle(String.format(getString(R.string.notification_message), author))
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    // save data into database
    private void saveData(final Map<String, String> data) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> insertData = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SquawkContract.COLUMN_AUTHOR, data.get(KEY_AUTHOR));
                contentValues.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get(KEY_AUTHOR_KEY));
                contentValues.put(SquawkContract.COLUMN_MESSAGE, data.get(KEY_MESSAGE));
                contentValues.put(SquawkContract.COLUMN_DATE, data.get(KEY_DATE));
                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, contentValues);
                return null;
            }
        };

        insertData.execute();
    }
}
