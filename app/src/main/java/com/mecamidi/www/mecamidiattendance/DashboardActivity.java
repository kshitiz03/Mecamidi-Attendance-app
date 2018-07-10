package com.mecamidi.www.mecamidiattendance;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences pref = getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        if(pref.contains(Functions.NAME)) {
            ((TextView)findViewById(R.id.text)).setText(pref.getString(Functions.NAME,"default"));
        }
    }
}
