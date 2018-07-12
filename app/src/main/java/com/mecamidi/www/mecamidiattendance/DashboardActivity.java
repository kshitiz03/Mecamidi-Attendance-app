package com.mecamidi.www.mecamidiattendance;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences pref = getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        ((TextView)findViewById(R.id.name)).setText(pref.getString(Functions.NAME,"default"));
        ((TextView)findViewById(R.id.contact_no)).setText(pref.getString(Functions.CONTACT,"default"));
        ((TextView)findViewById(R.id.email_id)).setText(pref.getString(Functions.EMAIL,"default"));
        ((TextView)findViewById(R.id.emp_code)).setText(pref.getString(Functions.LOGINID,"default"));
    }
}
