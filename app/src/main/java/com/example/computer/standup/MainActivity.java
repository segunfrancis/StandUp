package com.example.computer.standup;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        ToggleButton alarmButton = findViewById(R.id.alarmToggle);
        final Button nextAlarmButton = findViewById(R.id.next_alarm);

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        // The flag determines what happens if a PendingIntent whose intent matches the
        // intent you are trying to create already exists. The NO_CREATE flag returns null
        // unless a PendingIntent with a matching Intent exists.
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        alarmButton.setChecked(alarmUp);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String toastMessage;
                if (isChecked) {
                    long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                triggerTime, repeatInterval, notifyPendingIntent);
                    }
                    toastMessage = getString(R.string.alarm_on_toast_message);
                } else {
                    // Cancel notification if the alarm is turned off
                    mNotificationManager.cancelAll();

                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntent);
                    }
                    toastMessage = getString(R.string.alarm_off_toast_message);
                }
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        /*nextAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (alarmManager != null) {
                        AlarmManager.AlarmClockInfo clockInfo = alarmManager.getNextAlarmClock();
                        long nextAlarmTime = clockInfo.getTriggerTime();
                        SimpleDateFormat df = new SimpleDateFormat();
                        String properTime = df.format(nextAlarmTime);
                        Toast.makeText(MainActivity.this, properTime, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(MainActivity.this, "No Alarm Set!", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        createNotificationChannel();
    }

    public void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Stand up notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.notification_channel_description));
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
