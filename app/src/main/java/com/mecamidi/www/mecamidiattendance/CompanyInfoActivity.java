package com.mecamidi.www.mecamidiattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CompanyInfoActivity extends AppCompatActivity implements View.OnClickListener {

    Button edit;
    EditText ann;
    View.OnClickListener doneListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Company Information");
        setSupportActionBar(toolbar);

        edit = findViewById(R.id.edit_ann);
        ann = findViewById(R.id.ann);
        ann.setEnabled(false);

        SharedPreferences pref = getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        if(pref.getInt(Functions.ADMIN,-1) != 2) {
            edit.setVisibility(View.INVISIBLE);
        }

        new AnnTask().execute();

        doneListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = ann.getText().toString();
                ann.setEnabled(false);
                edit.setText(getResources().getString(R.string.edit));
                new AnnTask().execute(a);
                edit.setOnClickListener(CompanyInfoActivity.this);
            }
        };

//        final View.OnClickListener editListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ann.setEnabled(true);
//                edit.setText(getResources().getString(R.string.done));
//                edit.setOnClickListener(doneListener);
//            }
//        };

        edit.setOnClickListener(this);

        final ArrayList<Word> words2 = new ArrayList<Word>();

        words2.add(new Word("Sanjeev Kapoor", "9818455737","sanjeev.kapoor@mecamidihpp.com","Managing Director"));
        words2.add(new Word("Amarjeet Singh", "9818455727","amarjeet.singh@mecamidihpp.com","Director Projects"));

        words2.add(new Word("Anagh Garg", "9873575550","anahg.garg@mecamidihpp.com","Whole time director"));

        words2.add(new Word("Virendra  Garg", "9825329589","virendra.garg@mcamidihpp.com","Sr. Vice President"));

        words2.add(new Word("Anu Agarwal", "9899291417","anu.agarwal@mecamidihpp.com","Manager-HR"));

        words2.add(new Word("Parampreet Singh", "9999063320","parampreet.singh@mecamidihpp.com",""));

        words2.add(new Word("Ramgopal Jalindra", "9873574816","ramgopal.jalindra@mecamidihpp.com",""));

        words2.add(new Word("Amit Kumar Vashisth", "9958699133","amit.vashisth@mecamidihpp.com",""));
        words2.add(new Word("Hemchand Harbola", "9958699134","hemchand.harbola@mecamidihpp.com",""));
        words2.add(new Word("Jagdeep Singh ", "9958699146","jagdeep.singh@mecamidihpp.com",""));

        words2.add(new Word("Sanjay Kumar", "8802010504","sanjay.kumar@mecamidihpp.com",""));


        Word_Adapter_Emp adapter = new Word_Adapter_Emp(CompanyInfoActivity.this, words2);


        ListView listView = (ListView) findViewById(R.id.list4);

        listView.setAdapter(adapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CompanyInfoActivity.this,DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private class AnnTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            Functions.showToast(CompanyInfoActivity.this,R.string.load);
        }

        @Override
        protected JSONObject doInBackground(String... data) {
            if(!Functions.isNetworkAvailable(CompanyInfoActivity.this)) {
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
                url = new URL(Data.URL_ANN);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if(data.length > 0) {
                return Functions.connectHttp(url,new String[]{"ann"},new String[]{data[0]});
            }
            else {
                return Functions.connectHttp(url,new String[]{},new String[]{});
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if (result.has("login") && !result.getBoolean("login")) {
                    Functions.showToast(CompanyInfoActivity.this,result.getString("msg"));
                    if(result.has("error")) {
                        Log.e("tag",result.getString("error"));
                    }
                }
                else {
                    if(result.has("msg"))
                        Functions.showToast(CompanyInfoActivity.this,result.getString("msg"));
                    ann.setText(result.getString("ann"));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        ann.setEnabled(true);
        edit.setText(getResources().getString(R.string.done));
        edit.setOnClickListener(doneListener);
    }
}
