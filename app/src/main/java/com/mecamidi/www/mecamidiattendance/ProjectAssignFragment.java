package com.mecamidi.www.mecamidiattendance;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectAssignFragment extends Fragment {


    public ProjectAssignFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.nav_proja);

        return inflater.inflate(R.layout.fragment_project_assign, container, false);
    }

}
