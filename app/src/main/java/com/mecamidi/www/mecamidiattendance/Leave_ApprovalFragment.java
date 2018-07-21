package com.mecamidi.www.mecamidiattendance;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;



public class Leave_ApprovalFragment extends Fragment {


    public Leave_ApprovalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View some=inflater.inflate(R.layout.fragment_leave__approval, container, false);
        final View list1 = inflater.inflate(R.layout.list_item2, container, false);
        final View list2 = inflater.inflate(R.layout.fragment_leave_request, container, false);;

        getActivity().setTitle(R.string.nav_la);
        final ArrayList<Word2> words2 = new ArrayList<Word2>();
        for(int i=0; i<20; i++) {

            words2.add(new Word2("Kshitiz", "15/06/18", "23/06/18", "SL"));
        }
        WordAdapter2 adapter = new WordAdapter2(getActivity(), words2);


        ListView listView = (ListView) some.findViewById(R.id.list2);

        listView.setAdapter(adapter);
        // Inflate the layout for this fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Word2 word = words2.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(some.getContext());
                EditText des= list2.findViewById(R.id.description);
                String mess = des.getText().toString();
                builder.setMessage("SFSGEGGATG");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });


        list1.findViewById(R.id.approve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are You Sure You Want To Approve?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
list1.findViewById(R.id.reject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are You Sure You Want To Reject?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
                return some;
    }

}
