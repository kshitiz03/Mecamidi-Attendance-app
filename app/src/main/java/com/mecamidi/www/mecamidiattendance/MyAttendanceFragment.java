package com.mecamidi.www.mecamidiattendance;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;

import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.provider.CalendarContract;
import android.content.DialogInterface;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import android.widget.PopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAttendanceFragment extends Fragment {


    public MyAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.nav_myattd);

        View some=inflater.inflate(R.layout.fragment_my_attendance, container, false);
        final Calendar myCalendar = Calendar.getInstance();

        final EditText edittext= (EditText) some.findViewById(R.id.Birthday);
        final EditText edittext1= (EditText) some.findViewById(R.id.Birthday1);

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

        edittext.setOnClickListener(new OnClickListener() {

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
        edittext1.setOnClickListener(new OnClickListener() {

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
        some.findViewById(R.id.show_my).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AttendanceActivity.class));
                //finish();
            }
        });

        some.findViewById(R.id.show_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = edittext.getText().toString();
                String end = edittext1.getText().toString();
                if(start.equals("") || end.equals("")) {
                    Functions.showToast(getContext(),R.string.empty_fields);
                    return;
                }
                new MyAttendanceTask().execute(start,end);
            }
        });

        return some;
    }

    private class MyAttendanceTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected void onPreExecute() {

        }

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
            String id = String.valueOf(getContext().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE).getInt(Functions.ID,-1));
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            keys.add("start"); values.add(start);
            keys.add("end"); values.add(end);
            keys.add("id"); values.add(id);

            URL url = null;
            try {
                url = new URL(Data.URL_MYATT);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return Functions.connectHttp(url,keys,values);
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

}
