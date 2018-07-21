package com.mecamidi.www.mecamidiattendance;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class LeaveView extends AppCompatActivity {

    WordAdapter adapter;
    ArrayList<Word> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_view);
        words = new ArrayList<Word>();
//        for(int i=0; i<20; i++) {
//            words.add(new Word("18/06/18", "10/07/18", "Approved"));
//            words.add(new Word("18/06/18", "10/07/18", "Pending"));
//        }

        adapter = new WordAdapter(LeaveView.this, words);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);
        new ShowLeaveTask().execute();

    }

    private class ShowLeaveTask extends AsyncTask<Void,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            Functions.showToast(LeaveView.this,R.string.load);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            if(!Functions.isNetworkAvailable(LeaveView.this)) {
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

            String id = String.valueOf(getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE).getInt(Functions.ID,-1));

            URL url = null;
            try {
                url = new URL(Data.URL_SHOW_LEAVE);
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return Functions.connectHttp(url,new String[]{"id"},new String[]{id});
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.e("tag",result.toString());
            try {
                if(result.has("login") && !result.getBoolean("login")) {
                    Functions.showToast(LeaveView.this,result.getString("msg"));
                }
                else {
                    updateAdapter(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAdapter(JSONObject result) throws JSONException {
        JSONArray array = result.getJSONArray("leave");
        for(int i=0;i<array.length();i++) {
            JSONObject current = array.getJSONObject(i);
            String start = current.getString("start_date");
            String end = current.getString("end_date");
            String status = current.getString("status");
            int id = current.getInt("leave_id");
            switch (status) {
                case "a":
                    status = "Approved";
                    break;
                case "p":
                    status = "Pending";
                    break;
                case "r":
                    status = "Rejected";
                    break;
            }
            Word w = new Word(start,end,status,id);
            words.add(w);
            adapter.notifyDataSetChanged();
        }
    }
}
