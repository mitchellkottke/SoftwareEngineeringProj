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

    public void switchToVideo(View view) {
        Intent myIntent = new Intent(this, RecordVideoActivity.class);
        startActivity(myIntent);

    }
}
/* example comment for git */
//Hello - Kirsi
//liz's comment
//Sydney's Comment
//Gaven's comment
//Test comment
//Jaron's comment