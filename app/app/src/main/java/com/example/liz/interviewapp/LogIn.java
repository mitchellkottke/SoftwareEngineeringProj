package com.example.liz.interviewapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class LogIn extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private SignInButton signIn;
    private Button signOut;
    private TextView accountInfo;
    GoogleSignInOptions signInOptions;
    private static final int REQ_CODE = 9001;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        signIn = (SignInButton)findViewById(R.id.googleLogIn);
        signIn.setOnClickListener(this);
        signOut = (Button)findViewById(R.id.googleLogOut);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        accountInfo = (TextView)findViewById(R.id.accountInfo);
        accountInfo.setText(" ");

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.googleLogIn)
        {
            signIn();
        }

        else {
            signOut(view);
        }
    }


    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    protected void signOut(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }

    public void updateUI(boolean isLogin) {
        if(isLogin) {
            accountInfo.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.GONE);
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
        else{
            accountInfo.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
            Intent logInIntent = new Intent(this, LogIn.class);
            startActivity(logInIntent);
        }

    }

    //this is where we can display name, prof pic, etc
    //go to 27:06 on tutorial
    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String email = account.getEmail();
            accountInfo.setText("Logged in as:" + email);
            updateUI(true);
        }
        else {
            accountInfo.setText(" ");
            updateUI(false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            GoogleSignInAccount account = result.getSignInAccount();
//            String email = account.getEmail();
//            accountInfo.setText(email);
            handleResult(result);

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}