package com.mecamidi.www.mecamidiattendance;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class Functions {

    public static final String PREF = "LoginPref";
    public static final String LOGINID = "codeKey";
    public static final String NAME = "nameKey";
    public static final String CONTACT = "contactKey";
    public static final String EMAIL = "emailKey";
    public static final String ADDRESS = "addressKey";
    public static final String ADMIN = "adminKey";
    public static final String DOB = "dobKey";
    public static final String DESIGNATION = "designationKey";
    public static final String IMAGE="imagekey";
    public static final String ID = "idKey";
    public static final String LOCATION = "locationKey";
    public static final String PUNCH = "punchKey";
    public static final String PROJECT = "projectKey";
    public static final String PRJLOCATION = "prjlocationKey";
    public static final String PRJNAME = "prjnameKey";
    public static final String PROJECTNAME = "projectnameKey";

    public static Location accessLocation(Context context) {
        Location location;
        Log.v("Location","Accessed Location");
        LocationManager manager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
//        LocationListener listener = new GpsLocationListener();
        if(ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        else {
//            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location == null) {
                return null;
            }
            Log.v("Location","Location Send");
            Log.e("Location",location.toString());
            return location;
        }
    }

    public static void showToast(Context context,int id) {

//        Snackbar snackbar = Snackbar.make(findViewById(R.id.top_layout),id,Snackbar.LENGTH_SHORT);
//        snackbar.show();
        Toast toast = Toast.makeText(context,id,Toast.LENGTH_SHORT);
        toast.show();

    }

    public static void showToast(Context context,CharSequence seq) {

//        Snackbar snackbar = Snackbar.make(findViewById(R.id.top_layout),seq,Snackbar.LENGTH_SHORT);
//        snackbar.show();
        Toast toast = Toast.makeText(context,seq,Toast.LENGTH_LONG);
        toast.show();

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void addToPreferences(Context context,JSONObject json) throws JSONException {

        SharedPreferences pref = context.getSharedPreferences(PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(NAME,json.getString("name"));
        editor.putString(CONTACT,json.getString("contact"));
        editor.putString(EMAIL,json.getString("email"));
        editor.putString(LOGINID,json.getString("code"));
        editor.putString(ADDRESS,json.getString("address"));
        editor.putInt(ADMIN,json.getInt("admin"));
        editor.putString(DOB,json.getString("dob"));
        editor.putString(DESIGNATION,json.getString("designation"));
        editor.putInt(ID,json.getInt("id"));
//        editor.putString(LOCATION,json.getString("location"));
        editor.apply();

    }
    public static void updateLabel(EditText edit, Calendar myCalendar) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edit.setText(sdf.format(myCalendar.getTime()));
    }



    public static JSONObject connectHttp(URL url,String[] keys,String[] values) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            if(keys.length > 0) {
                Uri.Builder builder = new Uri.Builder();
                for (int i=0;i<keys.length;i++) {
                    builder.appendQueryParameter(keys[i],values[i]);
                }
                String query = builder.build().getEncodedQuery();
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(query);
                writer.flush();
            }

            int response_code = connection.getResponseCode();
            if(response_code == HttpURLConnection.HTTP_OK) {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder result = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.e("tag",result.toString());
                return new JSONObject(result.toString());
            }

        }
        catch (IOException e) {
            Log.e("tag","error here in IO");
            e.printStackTrace();
            try {
                JSONObject error = new JSONObject();
                error.put("msg","Error in Connecting to Server");
                error.put("error",e.getMessage());
                error.put("login",false);
                error.put("signup",false);
                return error;
            } catch (JSONException e1) {
                e1.printStackTrace();
                Log.e("tag","error here in IO in json");
            }
        }
        catch (JSONException e ) {
            e.printStackTrace();
            Log.e("tag","error here in exception");
            try {
                JSONObject error = new JSONObject();
                error.put("msg","Error in Connecting to Server");
                error.put("error",e.getMessage());
                error.put("login",false);
                error.put("signup",false);
                return error;
            } catch (JSONException e1) {
                Log.e("tag","error here in exception json");
                e1.printStackTrace();
            }
        }
        finally {
            if(connection != null) connection.disconnect();
            try {
                if(reader != null)
                    reader.close();
                if(writer !=null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject connectHttp(URL url, ArrayList<String> k, ArrayList<String> v) {

        String keys[] = new String[k.size()];
        String values[] = new String[k.size()];
        for(int i=0;i<k.size();i++) {
            keys[i] = k.get(i);
//            Log.v("keys",keys[i]);
            values[i] = v.get(i);
//            Log.v("keys",values[i]);
        }
        return connectHttp(url,keys, values);
    }

    public static boolean checkLocation(Context context,String currentLocation) {
        SharedPreferences pref = context.getSharedPreferences(PREF,Context.MODE_PRIVATE);
        String loc[] = pref.getString(LOCATION,"default").split("&");
        String loc2[] = currentLocation.split("&");
        if(loc[0].equals("default")) {
            return false;
        }
        double lat1 = Double.parseDouble(loc[0]);
        double long1 = Double.parseDouble(loc[1]);
        double lat2 = Double.parseDouble(loc2[0]);
        double long2 = Double.parseDouble(loc2[1]);
        float res[] = new float[1];
        Location.distanceBetween(lat1,long1,lat2,long2,res);
        Log.e("distance",String.valueOf(res[0]));
        return (res[0] < 1500);
    }

    public static void change(Context context,Button btn, boolean enable) {
        if(enable) {
            btn.setEnabled(true);
            btn.setBackground(context.getResources().getDrawable(R.drawable.ripple_enable));
            btn.setTextColor(context.getResources().getColor(R.color.bg_screen1));
        }
        else {
            btn.setEnabled(false);
            btn.setBackground(context.getResources().getDrawable(R.drawable.ripple_disable));
            btn.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

}
