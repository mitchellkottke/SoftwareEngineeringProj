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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private String flash = "Flash"; //question type will always be Flash
    private String userID = "quinz001"; //for now the test user is quinz001
    private String questionID; //the question

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

    /*------------------REPORT POPUP OPTIONS------------------
        when the report button is pressed, this shows the popup options
    */
    public void reportPopUpOptions(View v)
    {
        PopupMenu reportPop = new PopupMenu(FlashcardsActivity.this, v);
        reportPop.setOnMenuItemClickListener(FlashcardsActivity.this);
        reportPop.inflate(R.menu.report_menu_popup);
        reportPop.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){

        String targetURL = getString(R.string.serverURL) + "/reportQuestion";

        TextView qv = (TextView)findViewById(R.id.qAView); //Question view
        questionID = qv.getText().toString();

        switch (item.getItemId()){
            case R.id.irrelevantButton:
                roText = item.getTitle().toString();

                StringRequest postRequest = new StringRequest(Request.Method.POST, targetURL,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  report = new HashMap<String, String>();
                        report.put("user", userID);
                        report.put("questionID", questionID);
                        report.put("questionType", flash);
                        report.put("reasonForReport", roText);
                        return report;
                    }
                };
                requests.addToRequestQueue(postRequest);

                Toast.makeText(FlashcardsActivity.this,roText + " button selected.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.innapropriateButton:
                roText = item.getTitle().toString();
                Toast.makeText(FlashcardsActivity.this, roText + " Button selected." , Toast.LENGTH_SHORT).show();
                return true;
            case R.id.otherReportButton:
                roText = item.getTitle().toString();
                Toast.makeText(FlashcardsActivity.this, roText + " Button Selected.", Toast.LENGTH_SHORT).show();
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

}//****END OF CLASS****

//EXTRAS
/*
 private String returnQuestionID(){
        String targetURL = getString(R.string.serverURL) + "/getFlash";

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET, targetURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            questionID = response.getString("question");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                });
        requests.addToRequestQueue(sr);

        return questionID;
    }
 */

//                JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, targetURL, null,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                JSONObject report = new JSONObject();
//                                try {
//                                    report.put("user", userID);
//                                    report.put("questionID", questionID);
//                                    report.put("questionType", flash);
//                                    report.put("reasonForReport", roText);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                Log.d("JSON-Report", "user: " + userID + " question: " + questionID +
//                                        " questionType: " + flash + " reasonForReport: " + roText);
//                            }//end of onResponse
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.d("ERROR", error.toString());
//                            }
//                        }
//                );
//                requests.addToRequestQueue(sr);