package com.mecamidi.www.mecamidiattendance;

import android.app.IntentService;
import android.content.Intent;

public class ProjectService extends IntentService {

    public ProjectService() {
        super("ProjectService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Functions.showToast(this,"ABC");
    }
}
