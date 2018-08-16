package com.mecamidi.www.mecamidiattendance;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectAssignFragment extends Fragment {

    ArrayList<NameProject> emp;
    ArrayList<NameProject> prj;
    ArrayAdapter<NameProject> empAdapter;
    ArrayAdapter<NameProject> prjAdapter;
    private int empid = 0;
    private int prjid = 0;

    public ProjectAssignFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.nav_proja);

        emp = new ArrayList<>();
        prj = new ArrayList<>();

//        SharedPreferences pref = getContext().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE);
//        int admin = pref.getInt(Functions.ADMIN,-1);
//        if(admin == 0) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setTitle("Error!!");
//            builder.setMessage("You are not an admin");
//            builder.setPositiveButton("OK",null);
//            builder.create().show();
//            return null;
//        }

        View some=inflater.inflate(R.layout.fragment_project_assign, container, false);
        empAdapter = new ArrayAdapter<>(some.getContext(), android.R.layout.simple_list_item_1,emp);
        final AutoCompleteTextView tv = some.findViewById(R.id.assign1);
        tv.setAdapter(empAdapter);
        tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                if(obj instanceof NameProject) {
                    NameProject np = (NameProject)obj;
                    empid = np.getId();
                }
            }
        });

        prjAdapter = new ArrayAdapter<>(some.getContext(), android.R.layout.simple_list_item_1, prj);
        final AutoCompleteTextView tv1 =some.findViewById(R.id.assign_proj);
        tv1.setAdapter(prjAdapter);
        tv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                if(obj instanceof NameProject) {
                    NameProject np = (NameProject)obj;
                    prjid = np.getId();
                }
            }
        });
        new NameProjectTask().execute();
        some.findViewById(R.id.add_mem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = tv.getText().toString();
                String p = tv1.getText().toString();
                if(e.equals("") || p.equals("")) {
                    Functions.showToast(getContext(),R.string.empty_fields);
                    return;
                }
                Log.e("tag",String.valueOf(prjid) +" "+String.valueOf(empid));
                new NameProjectTask().execute(prjid,empid);
            }
        });
        return some;
    }


    private class NameProjectTask extends AsyncTask<Integer,Void,JSONObject> {

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

            URL url = null;
            try {
                url = new URL(Data.URL_PROJECT);
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }

            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            if(data.length > 0) {
                keys.add("prjid"); values.add(String.valueOf(data[0]));
                keys.add("empid"); values.add(String.valueOf(data[1]));
            }
            return Functions.connectHttp(url,keys,values);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.e("tag",result.toString());
            try {
                if (result.has("msg"))
                    Functions.showToast(getContext(), result.getString("msg"));
                updateEmployeeAdapter(result.getJSONArray("emp"));
                updateProjectAdapter(result.getJSONArray("prj"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEmployeeAdapter(JSONArray a) throws JSONException {

        int l = a.length();
        for(int i=0;i<l;i++) {
            JSONObject current = a.getJSONObject(i);
            NameProject np = new NameProject(current.getString("name"),current.getInt("id"));
            emp.add(np);
        }
        empAdapter.notifyDataSetChanged();
    }

    private void updateProjectAdapter(JSONArray a) throws JSONException {

        int l = a.length();
        for(int i=0;i<l;i++) {
            JSONObject current = a.getJSONObject(i);
            NameProject np = new NameProject(current.getString("name"),current.getInt("id"));
            prj.add(np);
        }
        prjAdapter.notifyDataSetChanged();
    }
}
