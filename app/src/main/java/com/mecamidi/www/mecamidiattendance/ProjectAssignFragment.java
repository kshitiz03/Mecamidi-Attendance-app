package com.mecamidi.www.mecamidiattendance;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


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
        View some=inflater.inflate(R.layout.fragment_project_assign, container, false);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(some.getContext(), android.R.layout.simple_list_item_1, memberall);
        AutoCompleteTextView tv = (AutoCompleteTextView) some.findViewById(R.id.assign1);

        tv.setAdapter(adapter);

        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(some.getContext(), android.R.layout.simple_list_item_1, memberall);
        AutoCompleteTextView tv1 = (AutoCompleteTextView) some.findViewById(R.id.assign_proj);

        tv.setAdapter(adapter1);

        return some;
    }

    private static final String[] memberall = new String[] {
            "Kshitiz", "Ashwin", "Aviral", "Parampreet", "Anu", "Ashwin1", "Ashwin2"
    };

}
