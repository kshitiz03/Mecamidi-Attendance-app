package com.mecamidi.www.mecamidiattendance;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment{

    private final int PICK_IMAGE = 1;
    private final int PERMISSION_IMAGE = 1;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View dashb= inflater.inflate(R.layout.fragment_dashboard, container, false);
        CardView cardView = dashb.findViewById(R.id.view2);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        cardView.setCardElevation(0);
        SharedPreferences pref =this.getActivity().getSharedPreferences(Functions.PREF,MODE_PRIVATE);
        if (pref == null) return dashb;
        ((TextView)dashb.findViewById(R.id.name)).setText(pref.getString(Functions.NAME,"default"));
        ((TextView)dashb.findViewById(R.id.contact_no)).setText(pref.getString(Functions.CONTACT,"default"));
        ((TextView)dashb.findViewById(R.id.email_id)).setText(pref.getString(Functions.EMAIL,"default"));
        ((TextView)dashb.findViewById(R.id.emp_code)).setText(pref.getString(Functions.LOGINID,"default"));
        Gson gson = new Gson();
        String json = pref.getString(Functions.IMAGE,"");
        Bitmap bitmap = gson.fromJson(json,Bitmap.class);
        if(json.equals(""))
            ((ImageView)dashb.findViewById(R.id.imageView1)).setImageDrawable(getResources().getDrawable(R.drawable.photo));
        else
            ((ImageView)dashb.findViewById(R.id.imageView1)).setImageBitmap(bitmap);

        dashb.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }
                else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_IMAGE);
                }
            }
        });

        // Inflate the layout for this fragment
        return dashb;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_IMAGE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Log.e("Dashboard","Loading Intent");
        startActivityForResult(Intent.createChooser(intent, "Pick Image"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Dashboard","in Activity Result");
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data!=null && data.getData() != null) {

            try {
                Log.e("Dashboard","Loading bitmap");
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                bitmap = Bitmap.createScaledBitmap(bitmap,50,109,true);
                // Log.d(TAG, String.valueOf(bitmap));
                ImageView imageView = getActivity().findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);
                SharedPreferences pref = getActivity().getSharedPreferences(Functions.PREF,MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(bitmap);
                editor.putString(Functions.IMAGE,json);
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
