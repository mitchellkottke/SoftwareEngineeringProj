package com.example.liz.interviewapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * @author ghutch
     * @param view
     * This function transitions to RecordVideoActivity
     */
    public void switchToVideo(View view) {
        Intent myIntent = new Intent(this, RecordVideoActivity.class);
        startActivity(myIntent);

    }
}