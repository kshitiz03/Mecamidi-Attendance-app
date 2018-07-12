package com.mecamidi.www.mecamidiattendance;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment{


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dashb= inflater.inflate(R.layout.fragment_dashboard, container, false);
        SharedPreferences pref =this.getActivity().getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        ((TextView)dashb.findViewById(R.id.name)).setText(pref.getString(Functions.NAME,"default"));
        ((TextView)dashb.findViewById(R.id.contact_no)).setText(pref.getString(Functions.CONTACT,"default"));
        ((TextView)dashb.findViewById(R.id.email_id)).setText(pref.getString(Functions.EMAIL,"default"));
        ((TextView)dashb.findViewById(R.id.emp_code)).setText(pref.getString(Functions.LOGINID,"default"));

        // Inflate the layout for this fragment
        return dashb;
    }


}
