package com.mecamidi.www.mecamidiattendance;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class PunchInOutFragment extends Fragment implements View.OnClickListener {

    private boolean punchedIn = false;
    private Button punchIn;
    private Button punchOut;
    private String loc;

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
            change(punchIn,false);
            change(punchOut,true);
        }
        else {
            change(punchOut,false);
            change(punchIn,true);
        }
        punchIn.setOnClickListener(this); punchOut.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        Location loca = accessLocation();
        loc = String.format("%s&%s",loca.getLatitude(),loca.getLongitude());
//        loc = loca.toString();
        Toast.makeText(getContext(),loc,Toast.LENGTH_LONG).show();

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

    private Location accessLocation() {
        Location location;
        Log.v("Location","Accessed Location");
        LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new GpsLocationListener();
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        else {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.v("Location","Location Send");
            return location;
        }
    }

    private class GpsLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
//            String loc = String.format("Latitude: %s Longitude: %s",lat,lon);
//            Toast toast = Toast.makeText(getContext(),loc,Toast.LENGTH_LONG);
//            toast.show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast toast = Toast.makeText(getContext(),"GPS Enabled",Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast toast = Toast.makeText(getContext(),"GPS Disabled",Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


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
                    change(punchIn,!punchIn.isEnabled());
                    change(punchOut,!punchOut.isEnabled());
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

    private void change(Button btn,boolean enable) {
        if(enable) {
            btn.setEnabled(true);
            btn.setBackground(getResources().getDrawable(R.drawable.ripple_enable));
            btn.setTextColor(getResources().getColor(R.color.bg_screen1));
        }
        else {
            btn.setEnabled(false);
            btn.setBackground(getResources().getDrawable(R.drawable.ripple_disable));
            btn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

}
