package com.example.liz.interviewapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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
public class FlashcardsActivity extends AppCompatActivity {

    private Button home, getAnswer, getQuestion;
    private TextView qAView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);
    }

    public void switchToHome(View myView) {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }

    public void getQuestion(View v)
    {
        TextView tv = (TextView)findViewById(R.id.qAView);
        tv.setText("Name three types of fragmentation?\n");
    }

    public void getAnswer(View v)
    {
        TextView tv = (TextView)findViewById(R.id.qAView);
        tv.append("External, Internal, and Data fragmentation");
    }



}
