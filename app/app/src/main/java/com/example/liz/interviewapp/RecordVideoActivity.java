package com.example.liz.interviewapp;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;

public class RecordVideoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    VideoView myVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        myVideoView = findViewById(R.id.videoView);
    }


    public void recordVideo(View myView){
        Intent myVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(myVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(myVideoIntent, REQUEST_CODE);
        }
    }

    
    protected void onActivityResult(int requestCode, int resultCode, Intent myVideoIntent) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri videoUri = myVideoIntent.getData();
            myVideoView.setVideoURI(videoUri);
            myVideoView.start();
        }
    }
}
