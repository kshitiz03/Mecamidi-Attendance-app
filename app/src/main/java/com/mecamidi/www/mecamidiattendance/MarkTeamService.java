package com.mecamidi.www.mecamidiattendance;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MarkTeamService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new MarkTeamTask().execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private class MarkTeamTask extends AsyncTask<JobParameters,Void,JSONObject> {

        JobParameters param;

        @Override
        protected JSONObject doInBackground(JobParameters... voids) {
            param = voids[0];
            DatabaseHandler handler = new DatabaseHandler(MarkTeamService.this);
            try {
                return handler.markTeam(new URL(Data.URL_TEAMATT));
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if(result.has("done") && result.getBoolean("done")) {
                    if(result.has("msg")) {
                        Functions.showToast(MarkTeamService.this,result.getString("msg"));
                    }
                    jobFinished(param,false);
                }
                else jobFinished(param,true);
            } catch (JSONException e) {
                e.printStackTrace();
                jobFinished(param,true);
            }
        }
    }
}
