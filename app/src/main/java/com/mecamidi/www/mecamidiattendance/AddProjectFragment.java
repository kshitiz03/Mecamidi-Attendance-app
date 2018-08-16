package com.mecamidi.www.mecamidiattendance;


import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.core.util.Function;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddProjectFragment extends Fragment {

    private final int PLACE_PICKER = 1;
    EditText projectLocation;
    Place place;

    public AddProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_project, container, false);
        getActivity().setTitle(R.string.add_prj);

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

        projectLocation = view.findViewById(R.id.location);
        projectLocation.setEnabled(false);

        view.findViewById(R.id.prj_loc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()),PLACE_PICKER);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        view.findViewById(R.id.prj_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)view.findViewById(R.id.name)).getText().toString();
                if(name.equals("")) {
                    Functions.showToast(getContext(),R.string.empty_fields);
                    return;
                }

                if(Functions.isNetworkAvailable(getContext())) {
                    if(place == null) {
                        Functions.showToast(getContext(),R.string.empty_fields);
                        return;
                    }
                    String address = place.getAddress().toString();
                    String location = String.format("%s&%s",place.getLatLng().latitude,place.getLatLng().longitude);
                    new AddProjectTask().execute(name,address,location);
                }
                else {
                    Location l = Functions.accessLocation(getContext());
                    String loc = String.format("%s&%s",l.getLatitude(),l.getLongitude());
                    SharedPreferences pref = getContext().getSharedPreferences(Functions.PREF,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Functions.PROJECT,"");
                    editor.putString(Functions.PRJLOCATION,loc);
                    editor.putString(Functions.PRJNAME,name);
                    editor.apply();
                    JobScheduler jobScheduler = (JobScheduler)getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    jobScheduler.schedule(new JobInfo.Builder(10,new ComponentName(getContext(),SendProjectService.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true).build());
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER) {
            if(resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getContext(),data);
                if(place != null)
                    projectLocation.setText(place.getAddress());
            }
        }
    }

    private class AddProjectTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            Functions.showToast(getContext(),R.string.load);
        }

        @Override
        protected JSONObject doInBackground(String... data) {
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
            String name = data[0];
            String address = data[1];
            String location = data[2];

            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            keys.add("name"); values.add(name);
            keys.add("address"); values.add(address);
            keys.add("location"); values.add(location);

            URL url = null;
            try {
                url = new URL(Data.URL_ADD_PROJECT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return Functions.connectHttp(url,keys,values);
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try {
                if(result.has("msg")) {
                    if(result.getString("error").equals("")) {
                        Functions.showToast(getContext(),result.getString("msg"));
                    }
                    else Log.e("tag",result.getString("msg"));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
