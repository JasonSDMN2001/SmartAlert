package com.unipi.boidanis.smartalert;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Restarter extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "This works unfortunately", Toast.LENGTH_SHORT).show();
        if ((intent.getAction().equals(Intent.ACTION_SCREEN_ON) ||
                intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION) ||
                intent.getAction().matches(LocationManager.KEY_PROVIDER_ENABLED))&&context!=null) {
            Toast.makeText(context, "broadcast!", Toast.LENGTH_SHORT).show();
            context.startService(new Intent(context,MyService.class));
                /*alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                intent = new Intent(getApplicationContext(),MyService.class);
                pendingIntent = PendingIntent.getService(getApplicationContext(),1234,intent,
                        PendingIntent.FLAG_IMMUTABLE);
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()
                //+(5000),pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000,pendingIntent);*/

        }
    }

}