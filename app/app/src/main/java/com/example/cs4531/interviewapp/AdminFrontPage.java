package com.example.cs4531.interviewapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminFrontPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public RestRequests requests; //our RestRequests class

    private ArrayAdapter<String> mListViewAdapter;

    private String question;
    private String type;
    private String reasonReport;
    private String user;
    private int dbItems;

    private RecyclerView mRecycleView;

    //NEW
    //private RecyclerView.Adapter mAdapter; NEW CHANGED TO THE ONE BELOW
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    JSONObject properties;

    Button deleteReport;
    Button deleteQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_front_page);

        mDrawerLayout= (DrawerLayout) findViewById(R.id.nav_drawer);
        mToggle = new ActionBarDrawerToggle(AdminFrontPage.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        requests = RestRequests.getInstance(getApplicationContext());

        final ArrayList<ExampleItem> exampleList = new ArrayList<>();

        String targetURL = getString(R.string.serverURL) + "/getReported";
        JsonArrayRequest sr = new JsonArrayRequest(Request.Method.GET, targetURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse (JSONArray response){
                        try {

                            for (int i=0; i<response.length(); i++){
                                JSONObject jo = response.getJSONObject(i);

                                question = jo.getString("questionID");
                                type = jo.getString("questionType");
                                reasonReport = jo.getString("reasonForReport");
                                user = jo.getString("user");

                                exampleList.add(new ExampleItem(question,
                                        type,
                                        "Reason for Report: " + reasonReport,
                                        "Reported By: " + user));
                            }//end of for

                        } catch (JSONException e) {
                            Log.d("ERROR", "IDK SOME ERROR");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JSON CALL ERROR", error.toString());
                        //tv.setText(error.toString());
                    }
                });
        requests.addToRequestQueue(sr);


        mRecycleView = findViewById(R.id.recyclerView);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(AdminFrontPage.this);
        mAdapter = new ExampleAdapter(exampleList);

        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);

        //NEW
        deleteReport = findViewById(R.id.deleteReport);
        deleteReport.setText("Delete Report");

        deleteQuestion = findViewById(R.id.deleteQuestion);
        deleteQuestion.setText("Delete Question");

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(AdminFrontPage.this, position + " button has been clicked", Toast.LENGTH_SHORT).show();
                ExampleItem currentItem = exampleList.get(position);
                Toast.makeText(AdminFrontPage.this, currentItem.getmQuestion()+ " has been clicked", Toast.LENGTH_SHORT).show();
                question = currentItem.getmQuestion();
                type = currentItem.getmType();

            }
        });

        deleteReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReport(question, type);
            }
        });
    }


    /**
     * deleteReport deletes the question from the
     * reportedQuestions collections db
     * @author quinz001
     * @param mQuestion the question string
     * @param mType the type of question
     */
    public void deleteReport(String mQuestion, String mType){
        question = mQuestion;
        type = mType;

        String targetURL = getString(R.string.serverURL) + "/deleteReport";
        StringRequest postRequest = new StringRequest(Request.Method.POST, targetURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(AdminFrontPage.this, question + " has been deleted from database.", Toast.LENGTH_SHORT).show();
                        Log.d("DELETE BUTTON SENT", response);
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
                report.put("question", question);
                report.put("questionType", type);
                return report;
            }
        };
        requests.addToRequestQueue(postRequest);
    }

    /**
     * deleteQuestion deletes the question from either the
     * flashQuestions or technicalQuestions collection from the db
     * @param mQuestion the question string
     * @param mType the question type
     */
    public void deleteQuestion(String mQuestion, String mType){
        question = mQuestion;
        type = mType;

        String targetURL = getString(R.string.serverURL) + "/deleteQuestion";
        StringRequest postRequest = new StringRequest(Request.Method.POST, targetURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(AdminFrontPage.this, "DELETE BUTTON CLICKED", Toast.LENGTH_LONG).show();
                        Log.d("DELETE BUTTON SENT", response);
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
                report.put("question", question);
                report.put("questionType", type);
                return report;
            }
        };
        requests.addToRequestQueue(postRequest);
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
        //For Admin Page
        if (id == R.id.nav_admin){
            Intent intent = new Intent(this, AdminLogInPage.class);
            startActivity(intent);
        }
        return false;
    }

    public void createExampleList() {

    }

}

// OLD GET METHOD FOR REPORTED QUESTIONS
//        String targetURL = getString(R.string.serverURL) + "/getReported";
//        JsonArrayRequest sr = new JsonArrayRequest(Request.Method.GET, targetURL, null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse (JSONArray response){
//                        try {
//
//                            for (int i=0; i<response.length(); i++){
//                                JSONObject jo = response.getJSONObject(i);
//                                exampleList.add(new ExampleItem("Question: " + jo.getString("questionID"),
//                                        "Type: " + jo.getString("questionType"),
//                                        "Reason for Report: " + jo.getString("reasonForReport"),
//                                        "Reported By: " + jo.getString("user")));
//                            }
//
//                        } catch (JSONException e) {
//                            Log.d("ERROR", "IDK SOME ERROR");
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("JSON CALL ERROR", error.toString());
//                        //tv.setText(error.toString());
//                    }
//                });
//        requests.addToRequestQueue(sr);






