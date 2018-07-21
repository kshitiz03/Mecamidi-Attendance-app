package com.mecamidi.www.mecamidiattendance;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

public class CheckPunchOutService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        SharedPreferences pref = getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(!pref.getBoolean(Functions.PUNCH,false)) {
            return false;
        }

        editor.putBoolean(Functions.PUNCH,false);
        editor.apply();
        Intent intent = new Intent(this,DashboardActivity.class);
        PendingIntent pit = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,Data.CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("Error").setContentText("You forgot to punch out today").setAutoCancel(true).setContentIntent(pit);
        NotificationManagerCompat compat = NotificationManagerCompat.from(this);
        compat.notify(0,builder.build());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
