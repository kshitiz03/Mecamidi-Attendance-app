package com.mecamidi.www.mecamidiattendance;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SendDataService extends JobService {

    public SendDataService() {
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
            boolean punchedIn = Boolean.parseBoolean(param.getExtras().get(Functions.PUNCH).toString());
            DatabaseHandler handler = new DatabaseHandler(SendDataService.this);
            try {
                return handler.sendData(punchedIn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if(result.has("done") && result.getBoolean("done")) {
                    if(result.has("msg")) {
                        Functions.showToast(SendDataService.this,result.getString("msg"));
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
