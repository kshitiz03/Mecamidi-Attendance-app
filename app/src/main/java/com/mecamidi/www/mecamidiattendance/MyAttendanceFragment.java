package com.mecamidi.www.mecamidiattendance;


import android.app.Activity;
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
import android.widget.PopupWindow;


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

//        CalendarView edit_text= some.findViewById(R.id.calendarView);
//        edit_text.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                showPopup(MyAttendanceFragment.this);
//            }
//        });
        // Inflate the layout for this fragment
        return some;
    }

//    private void showPopup(Fragment context) {
//
//        LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = layoutInflater.inflate(R.layout.fragment_my_attendance, null,false);
//        // Creating the PopupWindow
//        final PopupWindow popupWindow = new PopupWindow(
//                layout,400,400);
//
//        popupWindow.setContentView(layout);
//        popupWindow.setHeight(500);
//        popupWindow.setOutsideTouchable(false);
//        // Clear the default translucent background
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//
//        CalendarView cv = (CalendarView) layout.findViewById(R.id.calendarView);
//        cv.setBackgroundColor(Color.BLUE);
//
//        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month,
//                                            int dayOfMonth) {
//                // TODO Auto-generated method stub
//                popupWindow.dismiss();
//                Log.d("date selected", "date selected " + year + " " + month + " " + dayOfMonth);
//
//            }
//        });
//        popupWindow.showAtLocation(layout, Gravity.TOP,5,170);
//    }

}
