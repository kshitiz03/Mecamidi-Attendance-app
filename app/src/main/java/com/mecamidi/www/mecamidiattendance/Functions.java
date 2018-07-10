package com.mecamidi.www.mecamidiattendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
        if(json.getString("admin").equals("0")) editor.putBoolean(ADMIN,false);
        else editor.putBoolean(ADMIN,true);
        editor.putString(DOB,json.getString("dob"));
        editor.putString(DESIGNATION,json.getString("designation"));
        editor.apply();

    }

}
