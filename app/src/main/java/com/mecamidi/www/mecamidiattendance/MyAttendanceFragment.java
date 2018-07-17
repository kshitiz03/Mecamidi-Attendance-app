package com.mecamidi.www.mecamidiattendance;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

import java.util.Calendar;
import android.widget.PopupWindow;

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


        return some;
    }

}
