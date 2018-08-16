package com.mecamidi.www.mecamidiattendance;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SendProjectService extends JobService {

    public SendProjectService() {
    }


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new SendAttendanceTask().execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private class SendAttendanceTask extends AsyncTask<JobParameters,Void,JSONObject> {

        JobParameters param = null;

        @Override
        protected JSONObject doInBackground(JobParameters... jobParameters) {
            param = jobParameters[0];
            SharedPreferences pref = getSharedPreferences(Functions.PREF,MODE_PRIVATE);
            if(pref.contains(Functions.PRJLOCATION)) {
                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> values = new ArrayList<>();

                keys.add("name"); values.add(pref.getString(Functions.PRJNAME,""));
                keys.add("address"); values.add(pref.getString(Functions.PROJECT,""));
                keys.add("location"); values.add(pref.getString(Functions.PRJLOCATION,""));
                URL url = null;
                try {
                    url = new URL(Data.URL_ADD_PROJECT);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                return Functions.connectHttp(url,keys,values);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if(result == null) {
                jobFinished(param,false);
            }
            try {
                    if(result.has("msg")) {
                        Functions.showToast(SendProjectService.this,result.getString("msg"));
                    }
                    jobFinished(param,false);
            } catch (JSONException e) {
                e.printStackTrace();
                jobFinished(param,true);
            }
        }
    }
}
