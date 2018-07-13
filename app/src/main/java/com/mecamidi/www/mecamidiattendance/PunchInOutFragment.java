package com.mecamidi.www.mecamidiattendance;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;


/**
 * A simple {@link Fragment} subclass.
 */
public class PunchInOutFragment extends Fragment {


    public PunchInOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.nav_punch);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_punch_in_out, container, false);
    }

}
