package com.mecamidi.www.mecamidiattendance;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveRequestFragment extends Fragment {


    public LeaveRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.nav_levreq);
        final Calendar myCalendar = Calendar.getInstance();
        View some = inflater.inflate(R.layout.fragment_leave_request, container, false);
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


        return some;
    }

}
