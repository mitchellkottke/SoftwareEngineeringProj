package com.example.cs4531.interviewapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Once database is up and running, we can set functions to the getQuestion
 * button to retrieve a sample question from the database, and then put it in
 * the textview. The getAnswer should have the same functionality.
 */
public class FlashcardsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener{

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
        getQuestion(tv);
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
        if (id == R.id.nav_myAccount){
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

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET, targetURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                        public void onResponse (JSONObject response){
                        try {
                       // JSONObject flashcard = response.getJSONObject();
                        answerString = response.getString("answer");

//                        Log.d("GET", response.toString());
                        tv.setText(response.getString("question"));

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

    /*------------------REPORT POPUP OPTIONS------------------
        when the report button is pressed, this shows the popup options
     */
    public void reportPopUpOptions(View v)
    {
//        PopupMenu reportMenu = new PopupMenu(FlashcardsActivity.this, v);
//        MenuInflater inflater = reportMenu.getMenuInflater();
//        inflater.inflate(R.menu.report_menu_popup, reportMenu.getMenu());
//        reportMenu.show();
        PopupMenu reportPop = new PopupMenu(FlashcardsActivity.this, v);
        reportPop.setOnMenuItemClickListener(FlashcardsActivity.this);
        reportPop.inflate(R.menu.report_menu_popup);
        reportPop.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.irrelevantButton:
                Toast.makeText(FlashcardsActivity.this, "Irrelevant Button Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.innapropriateButton:
                Toast.makeText(FlashcardsActivity.this, "Inappropriate Button Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.otherReportButton:
                Toast.makeText(FlashcardsActivity.this, "Other Button Selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return  false;
        } //end of switch statement
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

// extra shit
/*
        //@Override
    public boolean onReportOptionsItemSelected(MenuItem item){
        View v = null;
        PopupMenu reportMenu = new PopupMenu(FlashcardsActivity.this, v);
        //item = reportMenu.getMenu().getItem(1);
        String itemTitle;
        //for(int i=0; i<4; i++){
            //item = reportMenu.getMenu().getItem(i);
            //itemTitle = item.getTitle().toString();
            switch (item.getItemId()){
                case R.id.irrelevantButton:
                    Toast.makeText(FlashcardsActivity.this, "Irrelevant Button Selected", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.innapropriateButton:
                    Toast.makeText(FlashcardsActivity.this, "Innapropriate Button Selected", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.otherReportButton:
                    Toast.makeText(FlashcardsActivity.this, "Other Button Selected", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }//end of switch

        //}
    }
     */

//public void reportButtons(View)
    /*
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
        if (id == R.id.nav_myAccount){
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        }
        return false;
    }
     */