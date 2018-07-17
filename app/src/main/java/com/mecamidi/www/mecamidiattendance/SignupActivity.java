package com.mecamidi.www.mecamidiattendance;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    private boolean email = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.signup_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = true;
                String emp = ((EditText)findViewById(R.id.user_id)).getText().toString();
                String empppass = ((EditText)findViewById(R.id.password)).getText().toString();
                String confpass = ((EditText)findViewById(R.id.con_password)).getText().toString();
                signup(emp,empppass,confpass);
            }
        });

        findViewById(R.id.signup_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = false;
                String emp = ((EditText)findViewById(R.id.user_id)).getText().toString();
                String empppass = ((EditText)findViewById(R.id.password)).getText().toString();
                String confpass = ((EditText)findViewById(R.id.con_password)).getText().toString();
                signup(emp,empppass,confpass);
            }
        });
    }

    private void signup(String emp,String empppass,String confpass) {

        if(emp.isEmpty() || empppass.isEmpty() || confpass.isEmpty()) {
            Functions.showToast(this,R.string.empty_fields);
            return;
        }
        new SignupAsyncTask().execute(emp,empppass,confpass);
    }

    private class SignupAsyncTask extends AsyncTask<String,Void,JSONObject> {

        private String signupUrl = Data.URL_SIGNUP;
        private URL url;
        private HttpURLConnection connection;

        @Override
        protected void onPreExecute() {
            Functions.showToast(SignupActivity.this,getResources().getString(R.string.load));
        }

        @Override
        protected JSONObject doInBackground(String... signupData) {

            String emp = signupData[0];
            String empppass = signupData[1];
            String confpass = signupData[2];

            if(!Functions.isNetworkAvailable(SignupActivity.this)) {

                try {
                    JSONObject j = new JSONObject();
                    j.put("msg","No Internet Connection");
                    j.put("signup",false);
                    return j;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("tag","here");
                }

            }

            try {
                url = new URL(signupUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if(email) return Functions.connectHttp(url,new String[]{"emppass","confpass","empemail"},new String[]{empppass,confpass,emp});
            else return Functions.connectHttp(url,new String[]{"emppass","confpass","empcode"},new String[]{empppass,confpass,emp});
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try {

                if(result.getBoolean("signup")) {
                    if (result.has("data")) {
                        JSONObject data = result.getJSONObject("data");
                        Functions.addToPreferences(SignupActivity.this, data);
                        Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
                else {
                    Functions.showToast(SignupActivity.this, result.getString("msg"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
