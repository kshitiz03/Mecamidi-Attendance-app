package com.mecamidi.www.mecamidiattendance;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeamAttendanceFragment extends Fragment {

    String file = "";
    DatabaseHandler handler;

    public TeamAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.nav_teamattd);
        // Inflate the layout for this fragment
        final View some=inflater.inflate(R.layout.fragment_team_attendance, container, false);

        Button export = some.findViewById(R.id.export);
//        Button ll = some.findViewById(R.id.export_layout);

        file = Environment.getExternalStorageDirectory().getPath().concat("/MHPP Attendance/");

        SharedPreferences pref = getActivity().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE);
        if(pref.getInt(Functions.ADMIN,-1)==0) {
            export.setVisibility(View.INVISIBLE);
        }

        handler = new DatabaseHandler(getContext());
        ArrayList<String> memberall = handler.getMembers();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(some.getContext(), android.R.layout.simple_list_item_1, memberall);
        AutoCompleteTextView tv = (AutoCompleteTextView) some.findViewById(R.id.teamattd);

        tv.setAdapter(adapter);

        final Calendar myCalendar = Calendar.getInstance();

        final EditText edittext= (EditText) some.findViewById(R.id.Birthday2);
        final EditText edittext1= (EditText) some.findViewById(R.id.Birthday3);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Functions.updateLabel(edittext, myCalendar);
            }

        };

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Functions.updateLabel(edittext1, myCalendar);
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dt= new DatePickerDialog(getContext(),R.style.AppBaseTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dt.getDatePicker().setMaxDate(System.currentTimeMillis());
                dt.show();
            }
        });
        edittext1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dt= new DatePickerDialog(getContext(),R.style.AppBaseTheme, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dt.getDatePicker().setMaxDate(System.currentTimeMillis());
                dt.show();
            }
        });
        some.findViewById(R.id.show_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)some.findViewById(R.id.teamattd)).getText().toString();
                String start = edittext.getText().toString();
                String end = edittext1.getText().toString();
                if(start.equals("") || end.equals("") || name.equals("")) {
                    Functions.showToast(getContext(),R.string.empty_fields);
                    return;
                }
                String aadhar = "";


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                try {
                    Date e = sdf.parse(end);
                    Date s = sdf.parse(start);
                    if (s.after(e)) {
                        Functions.showToast(getContext(),"End date cannot be before start date");
                        return;
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                try {
                    JSONObject j = handler.returnAadhar(name);
                    if(j.getBoolean("found")) aadhar = j.getString("msg");
                    else {
                        Functions.showToast(getContext(),j.getString("msg"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                new ShowTeamAttendanceTask().execute(start,end,aadhar);
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = ((EditText)some.findViewById(R.id.Birthday2)).getText().toString();
                String end = ((EditText)some.findViewById(R.id.Birthday3)).getText().toString();
                if(end.equals("") || start.equals("")) {
                    Functions.showToast(getContext(),R.string.empty_fields);
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                try {
                    Date e = sdf.parse(end);
                    Date s = sdf.parse(start);
                    if (s.after(e)) {
                        Functions.showToast(getContext(),"End date cannot be before start date");
                        return;
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                new ExportAttendanceTask().execute(start,end);
            }
        });

//        some.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                File att = getLatestFileFromDir(file);
//                Log.e("file",att.getPath());
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Uri uri = FileProvider.getUriForFile(getContext(),BuildConfig.APPLICATION_ID+".provider",att);
//                intent.setDataAndType(uri,"application/vnd.ms-excel");
//                startActivity(intent);
//            }
//        });

        return some;
    }
//
//    private File getLatestFileFromDir(String dirPath){
//        File dir = new File(dirPath);
//        File[] files = dir.listFiles();
//        if (files == null || files.length == 0) {
//            return null;
//        }
//
//        File lastModifiedFile = files[0];
//        for (int i = 1; i < files.length; i++) {
//            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
//                lastModifiedFile = files[i];
//            }
//        }
//        return lastModifiedFile;
//    }

    private class ShowTeamAttendanceTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... data) {

            if(!Functions.isNetworkAvailable(getContext())) {
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

            String start = data[0];
            String end = data[1];
            String aadharid = data[2];
            URL url = null;
            try {
                url = new URL(Data.URL_SHOW_TEAM);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return Functions.connectHttp(url,new String[]{"start","end","aadharid"},new String[]{start,end,aadharid});
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            Log.e("tag",s.toString());
            try {
                if(s.has("login") && !s.getBoolean("login")) {
                    Functions.showToast(getContext(),s.getString("msg"));
                }
                else {
                    Intent intent = new Intent(getContext(),AttendanceActivity.class);
                    intent.putExtra(AttendanceActivity.EXTRA_DATA,s.toString());
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class ExportAttendanceTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            Functions.showToast(getContext(),R.string.load);
        }

        @Override
        protected String doInBackground(String... data) {
            String start = data[0];
            String end = data[1];
            try {
                return HttpDownloadUtility.downloadFile(Data.URL_EXPORT,new String[]{start,end});
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}
