package com.mecamidi.www.mecamidiattendance;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlterMemberFragment extends Fragment {

    DatabaseHandler handler;
    ArrayAdapter<String> adapter;

    public AlterMemberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.nav_altm);
        final View some=inflater.inflate(R.layout.fragment_alter_member, container, false);

        handler = new DatabaseHandler(getContext());

        ArrayList<String> memberall = handler.getMembers();

        adapter =
                new ArrayAdapter<>(some.getContext(), android.R.layout.simple_list_item_1, memberall);
        AutoCompleteTextView tv = some.findViewById(R.id.members);

        some.findViewById(R.id.add_mem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)some.findViewById(R.id.add_name)).getText().toString();
                String aadharid = ((EditText)some.findViewById(R.id.add_id)).getText().toString();
                if(name.equals("") || aadharid.equals("")) {
                    Functions.showToast(getContext(),"Some Fields Are Empty");
                    return;
                }
                if(aadharid.length()!=12) {
                    Functions.showToast(getContext(),"Invalid Aadhar ID");
                    return;
                }
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                Date date = new Date(ts.getTime());
                Member member = new Member(name,aadharid,date);
                new AddMemberTask().execute(member);
            }
        });

        some.findViewById(R.id.rem_emp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((AutoCompleteTextView)some.findViewById(R.id.members)).getText().toString();
                if(name.equals("")) {
                    Functions.showToast(getContext(),R.string.empty_fields);
                }
                new RemoveMemberTask().execute(name);
            }
        });

        tv.setAdapter(adapter);
        // Inflate the layout for this fragment
        return some;
    }

    private class AddMemberTask extends AsyncTask<Member,Void,JSONObject> {

        Member member;

        @Override
        protected JSONObject doInBackground(Member... members) {
            member = members[0];
            int id = getContext().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE).getInt(Functions.ID,-1);
            try {
                if(!Functions.isNetworkAvailable(getContext())) {
                    JSONObject j = new JSONObject();
                    j.put("add",false);
                    j.put("msg","No internet Connection");
                    return j;
                }
                return handler.addMember(member,id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if(result.has("add") && result.getBoolean("add")) {
                    Functions.showToast(getContext(), "Member Added Successfully");
                    adapter.add(member.getName());
                    adapter.notifyDataSetChanged();
                }
                else Functions.showToast(getContext(),result.getString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RemoveMemberTask extends AsyncTask<String,Void,JSONObject> {

        String name;

        @Override
        protected JSONObject doInBackground(String... members) {
            name = members[0];
            try {

                if(!Functions.isNetworkAvailable(getContext())) {
                    JSONObject j = new JSONObject();
                    j.put("add",false);
                    j.put("msg","No internet Connection");
                    return j;
                }
                return handler.removeMember(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if(result.has("remove") && result.getBoolean("remove")) {
                    Functions.showToast(getContext(), name + " removed");
                    adapter.remove(name);
                    adapter.notifyDataSetChanged();
                }
                else
                    Functions.showToast(getContext(),result.getString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
