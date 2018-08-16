package com.mecamidi.www.mecamidiattendance;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment{

    private SharedPreferences pref;
    private TextView prj_text;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View dashb= inflater.inflate(R.layout.fragment_dashboard, container, false);

        pref =this.getActivity().getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        prj_text = dashb.findViewById(R.id.project_name);
        if (pref == null) return dashb;
        ((TextView)dashb.findViewById(R.id.name)).setText(pref.getString(Functions.NAME,"default"));
        ((TextView)dashb.findViewById(R.id.contact_no)).setText(pref.getString(Functions.CONTACT,"default"));
        ((TextView)dashb.findViewById(R.id.email_id)).setText(pref.getString(Functions.EMAIL,"default"));
        ((TextView)dashb.findViewById(R.id.emp_code)).setText(pref.getString(Functions.LOGINID,"default"));
        prj_text.setText(pref.getString(Functions.PROJECTNAME,"NA"));
        final int id = pref.getInt(Functions.ID,-1);
       dashb.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor log = getActivity().getSharedPreferences(Functions.PREF, MODE_PRIVATE).edit();
                log.clear();
                log.apply();
                DatabaseHandler handler = new DatabaseHandler(getContext());
                handler.clearAll();
                Intent lognew = new Intent(getActivity(), MainActivity.class);
                startActivity(lognew);
                getActivity().finish();
            }
        });

       dashb.findViewById(R.id.update_prj).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               new UpdateProjectTask().execute(id);
           }
       });

        // Inflate the layout for this fragment
        return dashb;
    }

    private class UpdateProjectTask extends AsyncTask<Integer,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            Functions.showToast(getContext(),R.string.load);
        }

        @Override
        protected JSONObject doInBackground(Integer... data) {
            int id = data[0];

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
                url = new URL(Data.URL_UPDATE_PROJECT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return Functions.connectHttp(url,new String[]{"id"},new String[]{String.valueOf(id)});
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if(result.has("login") && result.getBoolean("login")) {
                    Functions.showToast(getContext(),result.getString("msg"));
                }
                else {
                    String location = result.getString("location");
                    String prj = result.getString("name");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Functions.LOCATION,location);
                    editor.putString(Functions.PROJECTNAME,prj);
                    editor.apply();
                    prj_text.setText(pref.getString(Functions.PROJECTNAME,"NA"));
                    Functions.showToast(getContext(),result.getString("msg"));
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
