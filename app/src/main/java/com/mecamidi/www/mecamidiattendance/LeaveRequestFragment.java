package com.mecamidi.www.mecamidiattendance;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveRequestFragment extends Fragment {

    private int leave_type = 0;

    public LeaveRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.nav_levreq);
        final View some = inflater.inflate(R.layout.fragment_leave_request, container, false);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(some.getContext(), R.layout.spinner_textview, memberall);
        Spinner tv = some.findViewById(R.id.leave);

        tv.setAdapter(adapter);
        tv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                leave_type = (int)l;
//                Functions.showToast(getContext(),String.valueOf(l));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        some.findViewById(R.id.Leave_stat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),LeaveView.class));
            }
        });

        final Calendar myCalendar = Calendar.getInstance();
        final EditText edittext= some.findViewById(R.id.start);
        final EditText edittext1= some.findViewById(R.id.end);

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
                dt.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
                dt.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dt.show();
            }
        });

        some.findViewById(R.id.show_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String start = edittext.getText().toString();
                String end = edittext1.getText().toString();
                if(end.equals("") || start.equals("")) {
                    Functions.showToast(getContext(),"Some fields are empty");
                    return;
                }
                new RequestLeaveTask().execute(start,end);
            }
        });


        return some;
    }

    private static final String[] memberall = new String[] {
            "CL(Casual Leave)", "SL(Sick Leave)", "EL(Earned Leave)", "CTO(Compensatory Off)", "SRT(Short Leave)", "WO(Week Off)", "HO(Holiday)", "OD(Office Duty)"
    };

    private class RequestLeaveTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            Functions.showToast(getContext(),"Loading");
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
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            keys.add("start"); values.add(start);
            keys.add("end"); values.add(end);
            keys.add("leave_type"); values.add(String.valueOf(leave_type+1));
            String id = String.valueOf(getContext().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE).getInt(Functions.ID,-1));
            keys.add("id"); values.add(id);
            URL url = null;
            try {
                url = new URL(Data.URL_LEAVE);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return Functions.connectHttp(url,keys,values);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.e("tag",result.toString());
            try {
                Functions.showToast(getContext(),result.getString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
