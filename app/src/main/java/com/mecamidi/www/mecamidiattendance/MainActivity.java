package com.mecamidi.www.mecamidiattendance;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},Data.REQUEST_CODE);

        SharedPreferences pref = getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        if(pref.contains(Functions.LOGINID)) {
            Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
            Log.e("tag",pref.getString(Functions.LOGINID,""));
            startActivity(intent);

            finish();
        }
        File attDir = new File(Environment.getExternalStorageDirectory().getPath().concat("/MHPP Attendance/"));
        attDir.mkdir();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //startActivity(new Intent(MainActivity.this, WelcomeActivity.class));

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignupActivity.class));
                finish();
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = ((EditText)findViewById(R.id.username)).getText().toString();
                String pass = ((EditText)findViewById(R.id.password)).getText().toString();
                if(code.isEmpty() || pass.isEmpty()) {
                    Functions.showToast(MainActivity.this,R.string.empty_fields);
//                    TextView error = findViewById(R.id.error_field);
//                    error.setText(getResources().getString(R.string.empty_fields));
                    return;
                }
                login(code,pass);
            }
        });
    }

    private void login(String code,String pass) {

        new LoginAsyncTask().execute(code,pass);

    }


    private class LoginAsyncTask extends AsyncTask<String,Void,JSONObject> {

        private String loginUrl = Data.URL_LOGIN;
        private URL url;
        private HttpURLConnection connection;

        @Override
        protected void onPreExecute() {
            Functions.showToast(MainActivity.this,R.string.load);
        }

        @Override
        protected JSONObject doInBackground(String... loginData) {

            String code = loginData[0];
            String pass = loginData[1];

            if(!Functions.isNetworkAvailable(MainActivity.this)) {
                try {
                    JSONObject j = new JSONObject();
                    j.put("msg","No Internet Connection");
                    j.put("login",false);
                    return j;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("tag","here");
                }
            }

            try {
                url = new URL(loginUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return Functions.connectHttp(url,new String[]{"empcode","emppass"},new String[]{code,pass});
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try {
                if(result.getBoolean("login")) {
                    if(result.has("data")) {
                        JSONObject data = result.getJSONObject("data");
                        Functions.addToPreferences(MainActivity.this,data);
                        JSONArray team = result.getJSONArray("team");
                        DatabaseHandler handler = new DatabaseHandler(MainActivity.this);
                        handler.addMember(team);
                        Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
                else {
                    Functions.showToast(MainActivity.this, result.getString("msg"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}