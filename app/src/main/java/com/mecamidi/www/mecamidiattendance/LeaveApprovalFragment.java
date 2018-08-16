package com.mecamidi.www.mecamidiattendance;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class LeaveApprovalFragment extends Fragment {

    LeaveApprovalFragment.WordAdapter2 adapter;
    ArrayList<Word2> words2;

    public LeaveApprovalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences pref = getContext().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE);
        int admin = pref.getInt(Functions.ADMIN,-1);
        if(admin == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Error!!");
            builder.setMessage("You are not an admin");
            builder.setPositiveButton("OK",null);
            builder.create().show();
            return null;
        }
        final View some=inflater.inflate(R.layout.fragment_leave_approval, container, false);

        getActivity().setTitle(R.string.nav_la);
        words2 = new ArrayList<>();
//        for(int i=0; i<10; i++) {
//
//            words2.add(new Word2("Kshitiz", "15/06/18", "23/06/18", "SL","I just don't want to come",1));
//        }
        adapter = new LeaveApprovalFragment.WordAdapter2(getContext(), words2);
        ListView listView = some.findViewById(R.id.list2);

        listView.setAdapter(adapter);
        new LeaveApproveTask().execute(admin);
//        // Inflate the layout for this fragment
        return some;
    }


    public class LeaveApproveTask extends AsyncTask<Integer,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            Functions.showToast(getContext(),R.string.load);
        }

        @Override
        protected JSONObject doInBackground(Integer... data) {

            if(!Functions.isNetworkAvailable(getContext())) {
                try {
                    JSONObject j = new JSONObject();
                    j.put("msg","No Internet Connection");
                    j.put("login",false);
                    return j;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("tag","here");
                }
            }

            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            int admin = data[0];
            keys.add("admin"); values.add(String.valueOf(admin));
            String apprej = "";
            if(data.length > 1) {
                keys.add("leave_id"); values.add(String.valueOf(data[1]));
                int r = data[2];
                if(r == 0) apprej = "r";
                else apprej = "a";
            }
            keys.add("apprej"); values.add(apprej);
            URL url = null;
            try {
                url = new URL(Data.URL_APPROVE);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return Functions.connectHttp(url,keys,values);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.e("tag",result.toString());
            try {
                if(result.has("msg"))
                    Functions.showToast(getContext(), result.getString("msg"));
                JSONArray a = result.getJSONArray("leave");
                updateAdapter(a);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAdapter(JSONArray array) throws JSONException {
        words2.clear();
        for(int i=0;i<array.length();i++) {
            JSONObject current = array.getJSONObject(i);
            Word2 w = new Word2(current.getString("name"),current.getString("start"),current.getString("end"),current.getString("type"),current.getString("reason"),current.getInt("leave_id"));
            words2.add(w);
            adapter.notifyDataSetChanged();
        }

    }


    private class WordAdapter2 extends ArrayAdapter<Word2> {

        private Context context;

        public WordAdapter2(Context context, ArrayList<Word2> words2) {
            super(context, 0, words2);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item2, parent, false);
            }

            Word2 currentWord = getItem(position);

            final String desc = currentWord.getDesc();

            SharedPreferences pref = getContext().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE);
            final int admin = pref.getInt(Functions.ADMIN,-1);
            TextView miwokTextView = (TextView) listItemView.findViewById(R.id.startd);
            // Get the Miwok translation from the currentWord object and set this text on
            // the Miwok TextView.
            miwokTextView.setText(currentWord.getStartdate());

            // Find the TextView in the list_item.xml layout with the ID default_text_view.
            TextView defaultTextView = (TextView) listItemView.findViewById(R.id.endd);
            // Get the default translation from the currentWord object and set this text on
            // the default TextView.
            defaultTextView.setText(currentWord.getEnddate());

            TextView punchout = (TextView) listItemView.findViewById(R.id.names);
            // Get the default translation from the currentWord object and set this text on
            // the default TextView.
            punchout.setText(currentWord.getName());

            TextView status1 = (TextView) listItemView.findViewById(R.id.type_l);
            // Get the default translation from the currentWord object and set this text on
            // the default TextView.
            status1.setText(currentWord.getType());

            View textContainer = listItemView.findViewById(R.id.text_container2);
            // Find the color that the resource ID maps to
            int color = ContextCompat.getColor(getContext(), R.color.bg_screen1);
            // Set the background color of the text container View
            textContainer.setBackgroundColor(color);

            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(desc);
                    if(desc.equals("")) builder.setMessage(R.string.no_desc);
                    builder.setPositiveButton("OK",null);
                    builder.create().show();
                }
            });
            final int leave_id = currentWord.getLeaveId();

            Button approve = listItemView.findViewById(R.id.approve);
            Button reject = listItemView.findViewById(R.id.reject);
            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LeaveApprovalFragment.LeaveApproveTask().execute(admin,leave_id,1);
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LeaveApproveTask().execute(admin,leave_id,0);
                }
            });
            return listItemView;
        }
    }



}
