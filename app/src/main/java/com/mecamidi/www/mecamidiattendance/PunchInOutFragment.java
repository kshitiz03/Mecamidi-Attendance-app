package com.mecamidi.www.mecamidiattendance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PunchInOutFragment extends Fragment {

    private boolean punchedIn = false;
    private Button punchIn;
    private Button punchOut;
    private String loc;
    LocationManager mLocationManager;

    public PunchInOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.nav_punch);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_punch_in_out, container, false);


        SharedPreferences pref = getContext().getSharedPreferences(Functions.PREF,Context.MODE_PRIVATE);
        punchedIn = pref.getBoolean(Functions.PUNCH,false);

        punchOut = view.findViewById(R.id.punchout);
        punchIn = view.findViewById(R.id.punchin);
        if (punchedIn) {
            Functions.change(getContext(),punchIn,false);
            Functions.change(getContext(),punchOut,true);
        }
        else {
            Functions.change(getContext(),punchOut,false);
            Functions.change(getContext(),punchIn,true);
        }
        punchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
                scheduleJob();
            }
        });
        punchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });
        return view;
    }


    public void onButtonClick() {

        Location loca = Functions.accessLocation(getContext());
        if(loca == null) {
            Functions.showToast(getContext(),"Your location cannot be determined. Ensure that location is enabled in settings");
            return;
        }
        loc = String.format("%s&%s",loca.getLatitude(),loca.getLongitude());
//        loc = loca.toString();
//        Toast.makeText(getContext(),loc,Toast.LENGTH_LONG).show();
        if(!Functions.checkLocation(getActivity(),loc)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wrong Location!!");
            builder.setMessage("You are in wrong location. You cannot punch in/out until you are near your site location or maybe your location in not updated. Click on Update Project on Dashboard");
            builder.setPositiveButton("OK",null);
            builder.create().show();
            return;
        }
        else {
            Intent intent = new Intent(getContext(),DashboardActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getContext(),0,intent,0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),Data.CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("Location").setContentText("Your location in correct.").setAutoCancel(true).setContentIntent(pi);
            NotificationManagerCompat compat = NotificationManagerCompat.from(getContext());
            compat.notify(0,builder.build());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are You Sure?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new MarkAttendanceAsyncTask().execute(loc);
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



//    private Location getLastKnownLocation() {
//        mLocationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
//        List<String> providers = mLocationManager.getProviders(true);
//        Location bestLocation = null;
//        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return null;
//        } else {
//            for (String provider : providers) {
//                Location l = mLocationManager.getLastKnownLocation(provider);
//                if (l == null) {
//                    continue;
//                }
//                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                    // Found best last known location: %s", l);
//                    bestLocation = l;
//                }
//            }
//            return bestLocation;
//        }
//    }

    private class MarkAttendanceAsyncTask extends AsyncTask<String,Void,JSONObject> {

        private URL url;
        private int id;
        private Timestamp ts;
        private String d,t;
        @Override
        protected void onPreExecute() {
            Functions.showToast(getContext(),R.string.load);
            SharedPreferences pref = getActivity().getSharedPreferences(Functions.PREF, Context.MODE_PRIVATE);
            id = pref.getInt(Functions.ID,-1);
            ts = new Timestamp(System.currentTimeMillis());
        }

        @Override
        protected JSONObject doInBackground(String ... data) {

            String location = data[0];

            try {
                url = new URL(Data.URL_PUNCH);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            keys.add("id");values.add(String.valueOf(id));
            keys.add("punched_in"); values.add(String.valueOf(punchedIn));

            Date date = new Date(ts.getTime());
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm",Locale.US);
            t = ft.format(date);
            d = f.format(date);
            keys.add("date"); values.add(d);

            if(punchedIn) {
                keys.add("outtime"); values.add(ts.toString());
                keys.add("outlocation"); values.add(location);
            }
            else {
                keys.add("intime"); values.add(ts.toString());
                keys.add("inlocation"); values.add(location);
            }
            DatabaseHandler handler = new DatabaseHandler(getContext());
            try {
                return handler.addData(url,punchedIn,keys,values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            return Functions.connectHttp(url,keys,values);
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.e("tag",result.toString());
            if (result.has("msg")) {
                try {
                    Functions.showToast(getContext(),result.getString("msg"));
                    Functions.change(getContext(),punchIn,!punchIn.isEnabled());
                    Functions.change(getContext(),punchOut,!punchOut.isEnabled());
                    punchedIn = !punchedIn;
                    SharedPreferences.Editor editor = getContext().getSharedPreferences(Functions.PREF,Context.MODE_PRIVATE).edit();
                    editor.putBoolean(Functions.PUNCH,punchedIn);
                    editor.apply();
                    ((TextView)getActivity().findViewById(R.id.date)).setText(d);
                    ((TextView)getActivity().findViewById(R.id.time)).setText(t);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void scheduleJob() {

        JobScheduler scheduler = (JobScheduler)getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(new JobInfo.Builder(Data.HOUR_18_JOB,new ComponentName(getContext(),CheckPunchOutService.class)).setPersisted(true).setMinimumLatency(64800000).build());
    }

}
