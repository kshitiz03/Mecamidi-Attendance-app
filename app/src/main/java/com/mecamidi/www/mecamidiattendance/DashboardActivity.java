package com.mecamidi.www.mecamidiattendance;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar dash= findViewById(R.id.toolbar);
        setSupportActionBar(dash);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,dash,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new DashboardFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame,fragment);
        ft.commit();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        switch (id) {
            case R.id.nav_punch:
                fragment = new PunchInOutFragment();
                break;

            case R.id.nav_myattd:
                fragment = new MyAttendanceFragment();
                break;

            case R.id.nav_markta:
                fragment = new MarkTeamAttendanceFragment();
                break;

            case R.id.nav_teamattd:
                fragment = new TeamAttendanceFragment();
                break;

            case R.id.nav_levreq:
                fragment = new LeaveRequestFragment();
                break;

            case R.id.nav_coninf:
                intent = new Intent(this,CompanyInfoActivity.class);
                break;

            default:
                fragment = new DashboardFragment();
        }

        if(fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }
        else {
            startActivity(intent);
        }

        DrawerLayout layout = findViewById(R.id.drawer_layout);
        layout.closeDrawer(Gravity.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout layout = findViewById(R.id.drawer_layout);
        if(layout.isDrawerOpen(Gravity.START)) {
            layout.closeDrawer(Gravity.START);
        }
        else {
            super.onBackPressed();
        }
    }
}
