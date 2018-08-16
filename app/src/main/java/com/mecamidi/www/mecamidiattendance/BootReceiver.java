package com.mecamidi.www.mecamidiattendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context,ProjectService.class);
        context.startActivity(in);
    }
}
