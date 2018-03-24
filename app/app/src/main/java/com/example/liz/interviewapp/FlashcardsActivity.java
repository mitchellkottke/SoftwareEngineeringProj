package com.example.liz.interviewapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Once database is up and running, we can set functions to the getQuestion
 * button to retrieve a sample question from the database, and then put it in
 * the textview. The getAnswer should have the same functionality.
 */
public class FlashcardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);
    }
}
