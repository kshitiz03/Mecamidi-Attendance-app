package com.mecamidi.www.mecamidiattendance;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Word_Adapter_Emp extends ArrayAdapter<Word> {

private Context context;
    public Word_Adapter_Emp(Context context, ArrayList<Word> words2) {
        super(context, 0, words2);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.my_employees_list, parent, false);
        }

        final Word currentWord = getItem(position);

        TextView miwokTextView = (TextView) listItemView.findViewById(R.id.name_emp);

        miwokTextView.setText(currentWord.getName());

        TextView defaultTextView = (TextView) listItemView.findViewById(R.id.contact_emp);

        defaultTextView.setText(currentWord.getContact());

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("E-mail - ");
        stringBuilder.append("\n");
        stringBuilder.append(currentWord.getEmail());
        if(currentWord.getDesg()!="")
        {
            stringBuilder.append("\n\n");

            stringBuilder.append("Designation - ");
            stringBuilder.append("\n");

            stringBuilder.append(currentWord.getDesg());
        }
        final String finalString = stringBuilder.toString();

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(finalString).setPositiveButton("OK",null);
                builder.create().show();
            }
        });

        View textContainer = listItemView.findViewById(R.id.text_container3);
        int color = ContextCompat.getColor(getContext(), R.color.bg_screen1);
        textContainer.setBackgroundColor(color);

        return listItemView;
    }
}

