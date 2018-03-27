package com.example.liz.interviewapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.TextView;
import java.io.File;

/**
 * Once database is up and running, we can set functions to the getQuestion
 * button to retrieve a sample question from the database, and then put it in
 * the textview. The getAnswer should have the same functionality.
 */
public class FlashcardsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.nav_drawer);
        mToggle = new ActionBarDrawerToggle(FlashcardsActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tv = (TextView)findViewById(R.id.qAView);
        tv.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_recordVideo){
            Intent intent = new Intent(this, RecordVideoActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_flashcards){
            Intent intent = new Intent(this, FlashcardsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_resources){
            Intent intent = new Intent(this, ResourcesActivity.class);
            startActivity(intent);
        }
        return false;
    }

    public void getQuestion(View v)
    {
        TextView tv = (TextView)findViewById(R.id.qAView);
        tv.setText("");
        tv.setText("Name three types of fragmentation?\n");
    }

    public void getAnswer(View v)
    {
        TextView tv = (TextView)findViewById(R.id.qAView);
        tv.setText("");
        tv.append("External, Internal, and Data fragmentation");
    }



}
