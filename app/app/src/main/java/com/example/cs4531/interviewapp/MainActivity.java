package com.example.cs4531.interviewapp;


import
        android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private LogIn logIn;
    GoogleSignInAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.nav_drawer);
        mToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        account = intent.getParcelableExtra("account");
    }

    /**
     * @author smatthys
     * @param item
     * This function allows the menu toggle button and other menu buttons
     * properly function when clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * @author smatthys
     * @param item
     * This function takes a boolean value to transition between different activities.
     * It holds all the logic necessary for the navigation side bar.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
        if (id == R.id.nav_recordVideo){
            Intent intent = new Intent(this, RecordVideoActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
        if (id == R.id.nav_flashcards){
            Intent intent = new Intent(this, FlashcardsActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
        if (id == R.id.nav_resources){
            Intent intent = new Intent(this, ResourcesActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
        if (id == R.id.nav_myAccount){
            Intent intent = new Intent(this, LogIn.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
        //NEW ADMIN NAV ITEM
        if (id == R.id.nav_admin){
            Intent intent = new Intent(this, AdminPage.class);
            startActivity(intent);
        }
        return false;
    }

    /**
     * @author ghutch
     * @param myView
     * This function transitions to RecordVideoActivity
     */
    public void switchToVideo(View myView) {
        Intent myIntent = new Intent(this, RecordVideoActivity.class);
        myIntent.putExtra("account", account);
        startActivity(myIntent);
    }

    /**
     * @author ghutch
     * @param myView
     * This function transitions to FlashcardsActivity
     */
    public void switchToFlashcards(View myView) {
        Intent myIntent = new Intent(this, FlashcardsActivity.class);
        myIntent.putExtra("account", account);
        startActivity(myIntent);
    }

    /**
     * @author heather quinzon
     * @param myView
     * This function transitions to the AdminPage
     */
    public void switchToAdmin(View myView) {
        Intent myIntent = new Intent(this, AdminPage.class);
        startActivity(myIntent);
    }

}