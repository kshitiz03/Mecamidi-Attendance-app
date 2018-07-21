package com.mecamidi.www.mecamidiattendance;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MarkTeamAttendanceFragment extends Fragment {

    DatabaseHandler handler;
    ArrayAdapter<String> adapter;
    ArrayList<String> memberall;

    public MarkTeamAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.nav_markta);
        // Inflate the layout for this fragment
        View some = inflater.inflate(R.layout.fragment_mark_team_attendance, container, false);
        handler = new DatabaseHandler(getContext());
        memberall = handler.getMembers();
        adapter =
                new ArrayAdapter<String>(some.getContext(), android.R.layout.simple_list_item_1, memberall);
        final AutoCompleteTextView tv = some.findViewById(R.id.teammark);

        tv.setAdapter(adapter);

        some.findViewById(R.id.attendance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = tv.getText().toString();
                new MarkTeamTask().execute(name);
            }
        });

        return some;
    }

    private class MarkTeamTask extends AsyncTask<String,Void,JSONObject> {


        private URL url;
        private int id;
        private Timestamp ts;
        private String d,t;

        @Override
        protected void onPreExecute() {
            Functions.showToast(getContext(),R.string.load);
            SharedPreferences pref = getActivity().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE);
            id = pref.getInt(Functions.ID,-1);
            ts = new Timestamp(System.currentTimeMillis());
        }

        @Override
        protected JSONObject doInBackground(String... data) {


            Date date = new Date(ts.getTime());
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm",Locale.US);
            t = ft.format(date);
            d = f.format(date);

            try {
                url = new URL(Data.URL_TEAMATT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String name = data[0];
            try {
                return handler.markTeam(url,name,d,ts.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if (result.has("msg")) {
                    Functions.showToast(getContext(), result.getString("msg"));
                    ((TextView)getActivity().findViewById(R.id.time)).setText(t);
                    ((TextView)getActivity().findViewById(R.id.date)).setText(d);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
