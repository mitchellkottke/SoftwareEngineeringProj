package com.example.cs4531.interviewapp;

import android.content.Intent;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    private String roText; //report option text
    private String questionType = "Flash"; //question type will always be Flash
    private String userID = "default";//for now the test user is quinz001
    private String questionID; //the question
    private GoogleSignInAccount account;//Google account of current user

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
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
        account = intent.getParcelableExtra("account");

        //ALSO NEW
        mAuth = FirebaseAuth.getInstance();
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
        //For Admin Page
        if (id == R.id.nav_admin){
            Intent intent = new Intent(this, AdminLogInPage.class);
            intent.putExtra("account", account);
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

                            // Log.d("GET", response.toString());
                                tv.setText(response.getString("question"));
                            } catch (JSONException e) {
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

    /**
     * reportPopUpOptions shows the options available when a user wants to report a question
     * @author quinz001
     * @param v the view
     */
    public void reportPopUpOptions(View v)
    {
        PopupMenu reportPop = new PopupMenu(FlashcardsActivity.this, v);
        reportPop.setOnMenuItemClickListener(FlashcardsActivity.this);
        reportPop.inflate(R.menu.report_menu_popup);
        reportPop.show();
    }

    /**
     * onMenuItemClick takes what the user clicked as their option for the reported question
     * and sends the userID, questionID, questionType, and the reason for the report
     * @author quinz001 and kottk055
     * @param item the menu item
     */
    @Override
    public boolean onMenuItemClick(MenuItem item){
        TextView qv = (TextView)findViewById(R.id.qAView); //Question view
        String targetURL = getString(R.string.serverURL) + "/reportQuestion";
        final String questionStr = qv.getText().toString();
        if(account != null){
            userID = account.getEmail();
            userID = userID.substring(0, 8);
        }else {
            userID = "User Not Found";
        }
        try {
            roText = item.getTitle().toString();
        }catch(Exception e){
            roText = null;
        }
        if(roText != null && questionStr!= null) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, targetURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("REPORT BUTTON SENT", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> report = new HashMap<String, String>();
                report.put("user", userID);
                report.put("questionID", questionStr);
                report.put("questionType", questionType);
                report.put("reasonForReport", roText);
                return report;
            }
        };
        requests.addToRequestQueue(postRequest);
        Toast.makeText(FlashcardsActivity.this, roText + " button selected. Question has been reported.", Toast.LENGTH_SHORT).show();
    }
        else{
        Toast.makeText(FlashcardsActivity.this, "Question could not be reported", Toast.LENGTH_SHORT).show();
    }
        return true;
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

}//****END OF CLASS****