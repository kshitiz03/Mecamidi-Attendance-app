package com.mecamidi.www.mecamidiattendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;

public class MyEmployeesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_employees);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayList<Word> words2 = new ArrayList<Word>();
        for(int i=0; i<20; i++) {

            words2.add(new Word("Kshitiz", "8910510005"));
        }
        Word_Adapter_Emp adapter = new Word_Adapter_Emp(MyEmployeesActivity.this, words2);


        ListView listView = (ListView) findViewById(R.id.list3);

        listView.setAdapter(adapter);
    }
}
