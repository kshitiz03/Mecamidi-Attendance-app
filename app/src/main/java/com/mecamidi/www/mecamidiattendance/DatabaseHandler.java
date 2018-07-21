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
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mecamidihpp";
    private final String TABLE_NAME ="punch";
    private final String WORKERS = "workers";
    private Context context;
    private URL url;
    private String TEAM = "team";

    public DatabaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "create table punch (date date,intime timestamp,inlocation varchar(50),outtime timestamp, outlocation varchar(50), id integer)";
        db.execSQL(query);
        query = "create table workers (name varchar(50),aadharid varchar(12), date_of_joining varchar(20))";
        db.execSQL(query);
        query = "create table team (aadharid varchar(12),date date,time timestamp)";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if(oldVersion<20) {
//            db.execSQL("delete from punch");
//        }
    }

    private JSONObject addData(boolean punchedIn,@NonNull ContentValues values) throws JSONException {

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
        JSONObject j = new JSONObject();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        while(cursor.moveToNext()) {
            keys.add("punched_in"); values.add(String.valueOf(punchedIn));
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
            j =  Functions.connectHttp(url,keys,values);
            keys.clear(); values.clear();
        }
        cursor.close();
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

    public JSONObject addMember(Member member,int id) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase read = this.getReadableDatabase();

        Cursor cursor = read.query(WORKERS,new String[]{"aadharid"},"aadharid = ?",new String[]{member.getAadharid()},null,null,null);

        if(cursor.getCount() == 1) {
            JSONObject j = new JSONObject();
            j.put("msg","Already Present");
            j.put("add",false);
            return j;
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put("name",member.getName());
        cv.put("aadharid",member.getAadharid());
        Date date = member.getDateOfJoining();
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String doj = d.format(date);
        cv.put("date_of_joining",doj);
        db.insert(WORKERS,null,cv);
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        keys.add("name"); values.add(member.getName());
        keys.add("aadharid"); values.add(member.getAadharid());
        keys.add("doj"); values.add(doj);
        keys.add("id"); values.add(String.valueOf(id));
        try {
            JSONObject j = Functions.connectHttp(new URL(Data.URL_ADD_MEMBER),keys,values);
            j.put("add",true);

            return j;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            cursor.close();
        }
        return null;
    }

    public JSONObject removeMember(String name) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase read = this.getReadableDatabase();

        Cursor cursor = read.query(WORKERS,new String[]{"aadharid"},"name = ?",new String[]{name},null,null,null);

        if(cursor.getCount() == 0) {
            JSONObject j = new JSONObject();
            j.put("msg","No one present");
            j.put("remove",false);
            return j;
        }

        db.delete(WORKERS,"name = ?",new String[]{name});

        if(cursor.moveToFirst()) {
            try {
                JSONObject j = Functions.connectHttp(new URL(Data.URL_ADD_MEMBER),new String[]{"aadharid","delete"},new String[]{cursor.getString(0),"1"});
//                Functions.showToast(context,j.getString("msg"));
                j.put("remove",true);

                return j;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            finally {
                cursor.close();
            }
        }
        return null;
    }

    public ArrayList<String> getMembers() {
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(WORKERS,new String[]{"name"},null,null,null,null,null);
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0));
        }
        cursor.close();
        return result;
    }

    public JSONObject markTeam(URL url,String name,String date,String time) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(WORKERS,new String[]{"aadharid"},"name = ?",new String[]{name},null,null,null);
        if(cursor.getCount() == 0) {
            JSONObject j = new JSONObject();
            j.put("msg", "No one present");
            j.put("remove", false);
            return j;
        }
        String aadharid = "";
        if(cursor.moveToFirst()) {
            aadharid = cursor.getString(0);
        }
        db.close();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("aadharid",aadharid);
        cv.put("date",date);
        cv.put("time",time);
        db.insert(TEAM,null,cv);

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(2,new ComponentName(context,MarkTeamService.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true).build());

        db.close();
        JSONObject json = new JSONObject();
        json.put("msg","Data added successfully");
        return json;
    }

    public JSONObject markTeam(URL url) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TEAM,null,null,null,null,null,null);
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        JSONObject j = new JSONObject();
        while(cursor.moveToNext()) {
            String aadharid = cursor.getString(0);
            String date = cursor.getString(1);
            String time = cursor.getString(2);
            keys.add("time");values.add(time);
            keys.add("date");values.add(date);
            keys.add("aadharid"); values.add(aadharid);
            keys.add("mark"); values.add("p");
            j = Functions.connectHttp(url,keys,values);
            Log.e("tag",j.toString());
            keys.clear(); values.clear();
        }
        if(j.has("marked") && j.getBoolean("marked")) {
            db = this.getWritableDatabase();
            db.execSQL("delete from team");
        }
        db.close();
        cursor.close();
        j.put("done",true);
        return j;
    }
}
