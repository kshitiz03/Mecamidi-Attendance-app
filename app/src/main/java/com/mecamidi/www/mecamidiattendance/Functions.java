package com.mecamidi.www.mecamidiattendance;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Functions {

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

}
