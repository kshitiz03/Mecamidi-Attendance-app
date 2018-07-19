package com.mecamidi.www.mecamidiattendance;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.PersistableBundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "mecamidihpp";
    private final String TABLE_NAME ="punch";
    private Context context;
    private URL url;

    public DatabaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "create table punch (date date,intime timestamp,inlocation varchar(50),outtime timestamp, outlocation varchar(50), id integer)";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<20) {
            db.execSQL("delete from punch");
        }
    }

    private JSONObject addData(boolean punchedIn, ContentValues values) throws JSONException {

        SQLiteDatabase db = this.getWritableDatabase();
        values.remove("punched_in");
        if(punchedIn) {
            SharedPreferences p = context.getSharedPreferences(Functions.PREF,Context.MODE_PRIVATE);
            String id = String.valueOf(p.getInt(Functions.ID,-1));
            String d = values.getAsString("date");
            db.update(TABLE_NAME,values,"id = ? and date = ?",new String[]{id,d});
        }
        else {
            db.insert(TABLE_NAME,null,values);
        }

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(Functions.PUNCH,String.valueOf(punchedIn));

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(1,new ComponentName(context,SendDataService.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true).setExtras(bundle).build());
        db.close();
        JSONObject json = new JSONObject();
        json.put("msg","Data added successfully");
        return json;
    }

    public JSONObject addData(URL url,boolean punchedIn, ArrayList<String> keys, ArrayList<String> values) throws JSONException {
        this.url = url;
        ContentValues cv = new ContentValues();
        for (int i=0;i<keys.size();i++) {
            cv.put(keys.get(i),values.get(i));
        }
        return addData(punchedIn,cv);
//        return sendData(url,punchedIn);

    }

    public JSONObject sendData(boolean punchedIn) throws JSONException {

        try {
            url = new URL(Data.URL_PUNCH);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        SharedPreferences pref = context.getSharedPreferences(Functions.PREF,Context.MODE_PRIVATE);
//        boolean punchedIn = pref.getBoolean(Functions.PUNCH,false);

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        keys.add("punched_in"); values.add(String.valueOf(punchedIn));
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            String date = cursor.getString(0);
            String intime = cursor.getString(1);
            String inlocation = cursor.getString(2);
            String outtime = cursor.getString(3);
            String outlocation = cursor.getString(4);
            int id = cursor.getInt(5);
            keys.add("date"); values.add(date);
            keys.add("intime"); values.add(intime);
            keys.add("inlocation"); values.add(inlocation);
            keys.add("id"); values.add(String.valueOf(id));
            if(outlocation != null && outtime != null) {
                keys.add("outlocation");
                values.add(outlocation);
                keys.add("outtime");
                values.add(outtime);
            }
        }
        cursor.close();
        JSONObject j =  Functions.connectHttp(url,keys,values);
        if(j.has("marked") && j.getBoolean("marked")) {
            db = this.getWritableDatabase();
            db.execSQL("delete from punch");
        }
        db.close();
        j.put("done",true);
        return j;
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from punch");
        db.close();
    }
}
