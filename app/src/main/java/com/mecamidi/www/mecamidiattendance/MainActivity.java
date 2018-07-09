package com.mecamidi.www.mecamidiattendance;


<<<<<<< HEAD
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
=======
import android.content.Intent;
>>>>>>> lets see
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
<<<<<<< HEAD
import android.widget.EditText;
import android.widget.TextView;

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
=======
>>>>>>> lets see

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
<<<<<<< HEAD

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = ((EditText)findViewById(R.id.username)).getText().toString();
                String pass = ((EditText)findViewById(R.id.password)).getText().toString();
                if(code.isEmpty() || pass.isEmpty()) {
                    TextView error = findViewById(R.id.error_field);
                    error.setText(getResources().getString(R.string.empty_fields));
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
        private TextView error;

        @Override
        protected void onPreExecute() {
            error = findViewById(R.id.error_field);
            error.setText(getResources().getString(R.string.load));
        }

        @Override
        protected JSONObject doInBackground(String... loginData) {

            String code = loginData[0];
            String pass = loginData[1];

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
                e.printStackTrace();
                String json = String.format("{\"msg\":\"Error in connecting to server\",\"error\":\"%s\",\"login\":\"%b\"}",e.getMessage(),false);
                try {
                    return new JSONObject(json);
                } catch (JSONException e1) {
                    e1.printStackTrace();
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
                    return new JSONObject(result.toString());
                }

            }
            catch (Exception e ) {
                e.printStackTrace();
                String json = String.format("{\"msg\":\"Error in connecting to server\",\"error\":\"%s\",\"login\":\"%b\"}",e.getMessage(),false);
                try {
                    return new JSONObject(json);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            error.setText(" ");
            try {
                if(result.has("msg")) error.setText(result.getString("msg"));
                else error.setText(getResources().getString(R.string.json_error));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
=======
        //startActivity(new Intent(MainActivity.this, WelcomeActivity.class));


>>>>>>> lets see
    }
}