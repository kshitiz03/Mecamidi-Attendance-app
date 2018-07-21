package com.mecamidi.www.mecamidiattendance;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WordAdapter1 extends ArrayAdapter<Word1> {


    public WordAdapter1(Context context, ArrayList<Word1> words1) {
        super(context, 0, words1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item1, parent, false);
        }

        Word1 currentWord = getItem(position);

        TextView miwokTextView = (TextView) listItemView.findViewById(R.id.date);
        // Get the Miwok translation from the currentWord object and set this text on
        // the Miwok TextView.
        miwokTextView.setText(currentWord.getDate());

        // Find the TextView in the list_item.xml layout with the ID default_text_view.
        TextView defaultTextView = (TextView) listItemView.findViewById(R.id.punchin);
        // Get the default translation from the currentWord object and set this text on
        // the default TextView.
        defaultTextView.setText(currentWord.getPunchin());

        TextView punchout = (TextView) listItemView.findViewById(R.id.punchout);
        // Get the default translation from the currentWord object and set this text on
        // the default TextView.
        punchout.setText(currentWord.getPunchout());

        TextView status1 = (TextView) listItemView.findViewById(R.id.Status1);
        // Get the default translation from the currentWord object and set this text on
        // the default TextView.
        status1.setText(currentWord.getStatus());

        View textContainer = listItemView.findViewById(R.id.text_container1);
        // Find the color that the resource ID maps to
        int color = ContextCompat.getColor(getContext(), R.color.bg_screen1);
        // Set the background color of the text container View
        textContainer.setBackgroundColor(color);

        return listItemView;
    }
}

