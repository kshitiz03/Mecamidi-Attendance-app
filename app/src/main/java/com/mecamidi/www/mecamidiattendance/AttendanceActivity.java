package com.mecamidi.www.mecamidiattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {

    public static final String EXTRA_DATA = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        final ArrayList<Word1> words1 = new ArrayList<>();

        WordAdapter1 adapter = new WordAdapter1(AttendanceActivity.this, words1);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list1);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);

        String s = getIntent().getStringExtra(EXTRA_DATA);
        try {
            JSONObject json = new JSONObject(s);
            JSONArray array = json.getJSONArray("attendance");
            for(int i=0;i<array.length();i++) {

                JSONObject current = array.getJSONObject(i);
                String date = current.getString("date");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:MM", Locale.US);
                Timestamp sts = Timestamp.valueOf(current.getString("intime"));
                String in = sdf.format(sts);
                Timestamp ets = Timestamp.valueOf(current.getString("outtime"));
                String out = sdf.format(ets);
                String mark = current.getString("mark");
                Word1 word = new Word1(date,in,out,mark.toUpperCase());
                words1.add(word);

            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
