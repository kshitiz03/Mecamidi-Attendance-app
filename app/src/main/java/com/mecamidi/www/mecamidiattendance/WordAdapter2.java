package com.mecamidi.www.mecamidiattendance;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class WordAdapter2 extends ArrayAdapter<Word2> {

    private Context context;

    public WordAdapter2(Context context, ArrayList<Word2> words2) {
        super(context, 0, words2);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item2, parent, false);
        }

        Word2 currentWord = getItem(position);

        final String desc = currentWord.getDesc();

        TextView miwokTextView = (TextView) listItemView.findViewById(R.id.startd);
        // Get the Miwok translation from the currentWord object and set this text on
        // the Miwok TextView.
        miwokTextView.setText(currentWord.getStartdate());

        // Find the TextView in the list_item.xml layout with the ID default_text_view.
        TextView defaultTextView = (TextView) listItemView.findViewById(R.id.endd);
        // Get the default translation from the currentWord object and set this text on
        // the default TextView.
        defaultTextView.setText(currentWord.getEnddate());

        TextView punchout = (TextView) listItemView.findViewById(R.id.names);
        // Get the default translation from the currentWord object and set this text on
        // the default TextView.
        punchout.setText(currentWord.getName());

        TextView status1 = (TextView) listItemView.findViewById(R.id.type_l);
        // Get the default translation from the currentWord object and set this text on
        // the default TextView.
        status1.setText(currentWord.getType());

        View textContainer = listItemView.findViewById(R.id.text_container2);
        // Find the color that the resource ID maps to
        int color = ContextCompat.getColor(getContext(), R.color.bg_screen1);
        // Set the background color of the text container View
        textContainer.setBackgroundColor(color);

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(desc).setPositiveButton("OK",null);
                builder.create().show();
            }
        });

        Button approve = listItemView.findViewById(R.id.approve);
        Button reject = listItemView.findViewById(R.id.reject);

        return listItemView;
    }
}

