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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

/**
 * Once database is up and running, we can set functions to the getQuestion
 * button to retrieve a sample question from the database, and then put it in
 * the textview. The getAnswer should have the same functionality.
 */
public class FlashcardsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public RestRequests requests; //our RestRequests class
    public String answerString; //the answer response

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
        TextView tv = (TextView)findViewById(R.id.qAView); //Question view
        TextView answerView = (TextView)findViewById(R.id.answerView);
        requests = RestRequests.getInstance(getApplicationContext());
        tv.setText("");
        final Button answerButton = (Button)findViewById(R.id.getAnswer);
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
        if (id == R.id.nav_logout){
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        }
        return false;
    }

    /**
     * getQuestion changes the name of the buttons, textViews, and updates the view with a new flashcard question.
     * @author Jaron
     * @param v the view
     */
    public void getQuestion(View v)
    {
        String targetURL = getString(R.string.serverURL) + "/getFlash";
        final TextView tv = (TextView)findViewById(R.id.qAView);
        TextView answerView = (TextView) findViewById(R.id.answerView);
        answerView.setText(""); //Resets the answer field to default on click
        Button getQuestionButton = (Button)findViewById(R.id.get_question);
        getQuestionButton.setText(R.string.new_Question);
        Button answerButton = (Button)findViewById(R.id.getAnswer);
            if(answerButton.getText().toString() == getString(R.string.hide_Answer)) {
                hideAnswer(v);
            }

        JsonArrayRequest sr = new JsonArrayRequest(Request.Method.GET, targetURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                        public void onResponse (JSONArray response){
                        try {
                        JSONObject flashcard = response.getJSONObject(0);
                        answerString = flashcard.getString("answer");

//                        Log.d("GET", response.toString());
                        tv.setText(flashcard.getString("question"));

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                        }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                        tv.setText(error.toString());
                    }
                });
        requests.addToRequestQueue(sr);
    }

    public void showAnswer(View v)
    {
        TextView answerView = (TextView)findViewById(R.id.answerView);
        answerView.setText(answerString);

        Button answerButton = (Button)findViewById(R.id.getAnswer);
        answerButton.setText(R.string.hide_Answer); //change button to hide Answer
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                hideAnswer(v);
            }
        });
    }

    public void hideAnswer(View v)
    {
        TextView answerView = (TextView)findViewById(R.id.answerView);
        answerView.setText("");

        Button answerButton = (Button)findViewById(R.id.getAnswer);
        answerButton.setText(R.string.Get_Answer); //change button to hide Answer
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                showAnswer(v);
            }
        });
    }

}
