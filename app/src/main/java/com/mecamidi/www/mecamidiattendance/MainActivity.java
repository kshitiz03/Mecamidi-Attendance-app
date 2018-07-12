package com.mecamidi.www.mecamidiattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        if(pref.contains(Functions.LOGINID)) {
            Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //startActivity(new Intent(MainActivity.this, WelcomeActivity.class));

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignupActivity.class));
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

            try {

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("empcode",code).appendQueryParameter("emppass",pass);
                String query = builder.build().getEncodedQuery();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(query);
                writer.flush();
                writer.close();

            } catch (IOException e) {
                Log.e("tag","error here in IO");
                e.printStackTrace();
                try {
                    JSONObject error = new JSONObject();
                    error.put("msg","Error in Connecting to Server");
                    error.put("error",e.getMessage());
                    error.put("login",false);
                    return error;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Log.e("tag","error here in IO in json");
                }
            }

            try {

                int response_code = connection.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    JSONObject json =  new JSONObject(result.toString());
                    json.put("code",code);
                    return json;
                }

            }
            catch (Exception e ) {
                e.printStackTrace();
                Log.e("tag","error here in exception");
                try {
                    JSONObject error = new JSONObject();
                    error.put("msg","Error in Connecting to Server");
                    error.put("error",e.getMessage());
                    error.put("login",false);
                    return error;
                } catch (JSONException e1) {
                    Log.e("tag","error here in exception json");
                    e1.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try {

                if(result.getBoolean("login")) {
                    if(result.has("data")) {
                        JSONObject data = result.getJSONObject("data");
                        Functions.addToPreferences(MainActivity.this,data);
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