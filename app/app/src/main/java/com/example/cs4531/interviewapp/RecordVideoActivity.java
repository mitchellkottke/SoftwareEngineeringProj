package com.example.cs4531.interviewapp;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;


public class RecordVideoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMenuItemClickListener {

    private Button recordView, playView;
    private VideoView viewOfVideo;
    private TextView questionView;
    private int ACTIVITY_START_CAMERA_APP = 0;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public RestRequests requests; //our RestRequests class

    private String roText; //report option text
    private String questionType = "Technical/Interview"; //question type will always be Recorded Video
    private String userID = "default";//for now the test user is quinz001
    private String questionID; //the question
    private GoogleSignInAccount account;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        recordView = findViewById(R.id.recordButton);
        playView =  findViewById(R.id.playbackButton);
        viewOfVideo = findViewById(R.id.videoView);
        questionView = findViewById(R.id.questionView);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.nav_drawer);
        mToggle = new ActionBarDrawerToggle(RecordVideoActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        requests = RestRequests.getInstance(getApplicationContext());
                recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent playBackIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(playBackIntent, ACTIVITY_START_CAMERA_APP);
            }
        });

                playView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOfVideo.start();
            }
        });
        getQuestion(questionView);
        account = intent.getParcelableExtra("account");

        mAuth = FirebaseAuth.getInstance();
    }

    public void getQuestion(View v)
    {
        String targetURL = getString(R.string.serverURL) + "/getTechnical";
        StringRequest sr = new StringRequest(Request.Method.GET, targetURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GET", response.toString());
                        questionView.setText(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                        questionView.setText(error.toString());
                    }
                });
        requests.addToRequestQueue(sr);
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
     * onMenuItemClick takes what the user clicked as their option for the reported question
     * and sends the userID, questionID, questionType, and the reason for the report
     * @author quinz001
     * @param item the menu item
     */
    @Override
    public boolean onMenuItemClick(MenuItem item){
        final MenuItem i = item;
        TextView qv = (TextView)findViewById(R.id.qAView); //Question view
        final String questionStr = qv.getText().toString();
        StringRequest req = new StringRequest(Request.Method.POST, "http://akka.d.umn.edu:1234/getQuestionID",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Log.d("Question ID found: ", response);
                        reportQuestion(i, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        reportQuestion(i, null);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> report = new HashMap<String, String>();
                report.put("question", questionStr);
                report.put("type", "Flash");
                return report;
            }
        };
        requests.addToRequestQueue(req);
        return true;
    }

    /**
     * Helper to onMenuItemClick, does the actual reporting
     * @author kottk055
     * @param item menu item chosen
     * @param questionID id of question to report
     */
    private void reportQuestion(MenuItem item, String questionID){
        String targetURL = getString(R.string.serverURL) + "/reportQuestion";
        this.questionID = questionID;
        this.questionID = this.questionID.substring(1);
        this.questionID = this.questionID.substring(0, questionID.length()-2);//Trim "" off of id
        final String id = this.questionID;

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
        if(roText != null && questionID != null) {
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
                    report.put("questionID", id);
                    report.put("questionType", questionType);
                    report.put("reasonForReport", roText);
                    return report;
                }
            };
            requests.addToRequestQueue(postRequest);
            Toast.makeText(RecordVideoActivity.this, roText + " button selected. Question has been reported.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(RecordVideoActivity.this, "Question could not be reported", Toast.LENGTH_SHORT).show();
        }
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

    //displays toast saying whether video was saved or not
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            viewOfVideo.setVideoURI(videoUri);
            Toast.makeText(getApplicationContext(), "Video has been saved", Toast.LENGTH_LONG).show();
        }

       if(resultCode != RESULT_OK) {
           Toast.makeText(getApplicationContext(), "ERROR video not saved", Toast.LENGTH_LONG).show();
       }
    }

    public void showPopUp (View view)
    {
        PopupMenu popupmenu = new PopupMenu (RecordVideoActivity.this, view);
        popupmenu.setOnMenuItemClickListener(RecordVideoActivity.this);
        popupmenu.inflate(R.menu.report_menu_popup);
        popupmenu.show();
    }

}
