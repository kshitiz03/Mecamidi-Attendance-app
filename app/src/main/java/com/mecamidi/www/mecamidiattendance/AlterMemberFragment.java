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
public class AlterMemberFragment extends Fragment {


    public AlterMemberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.nav_altm);
        View some=inflater.inflate(R.layout.fragment_alter_member, container, false);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(some.getContext(), android.R.layout.simple_list_item_1, memberall);
        AutoCompleteTextView tv = (AutoCompleteTextView) some.findViewById(R.id.members);

        tv.setAdapter(adapter);
        // Inflate the layout for this fragment
        return some;
    }


    private static final String[] memberall = new String[] {
            "Kshitiz", "Ashwin", "Aviral", "Parampreet", "Anu", "Ashwin1", "Ashwin2"
    };


}
