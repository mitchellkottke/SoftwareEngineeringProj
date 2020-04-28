package com.example.cs4531.interviewapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;


public class ReportOtherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public RestRequests requests; //our RestRequests class
    private String questionType = "Flash"; //question type will always be Flash
    private String userID = "default";
    private GoogleSignInAccount account;//Google account of current user
    private String item = "Other";

    //FOR TEXT INPUT SUBMISSION - OTHER REPORTED QUESTIONS
    private TextView reportText;
    private EditText otherSubmission;
    private Button submit;
    private String userInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_other_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        mToggle = new ActionBarDrawerToggle(ReportOtherActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        requests = RestRequests.getInstance(getApplicationContext());



        otherSubmission = (EditText) findViewById(R.id.otherInput);
        reportText = (TextView) findViewById(R.id.reportOtherText);
        submit = (Button) findViewById(R.id.submitOtherButton);

                userInput = otherSubmission.getText().toString();
    }


    /**
     * onSubmitButton takes the users text input after selecting the report option, other
     * and sends the userID, questionID and the reason for the report
     * @author quinz001 and kottk05 and thiel433
     *
     */

    public boolean onSubmitButton (View v){

        TextView qv = (TextView)findViewById(R.id.questionView); //Question view
        String targetURL = getString(R.string.serverURL) + "/reportQuestion";
        final String questionStr = qv.getText().toString();
        if(account != null){
            userID = account.getEmail();
            userID = userID.substring(0, 8);
        }else {
            userID = "User Not Found";
        }


        if(userInput != null && questionStr!= null) {
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
                    report.put("question", questionStr);
                    report.put("questionType", questionType);
                    report.put("reasonForReport", userInput);
                    return report;
                }
            };
            requests.addToRequestQueue(postRequest);
            Toast.makeText(ReportOtherActivity.this, reportText + " button selected. Question has been reported.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ReportOtherActivity.this, "Question could not be reported", Toast.LENGTH_SHORT).show();
        }
        return true;
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


}
