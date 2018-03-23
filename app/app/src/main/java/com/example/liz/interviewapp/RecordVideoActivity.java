package com.example.liz.interviewapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;

public class RecordVideoActivity extends Activity {


    private static final int REQUEST_CODE = 1;
    private Button recordView, playView;
    private VideoView viewOfVideo;
    private int ACTIVITY_START_CAMERA_APP = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        recordView = (Button) findViewById(R.id.recordButton);
        playView = (Button) findViewById(R.id.playbackButton);
        viewOfVideo = (VideoView) findViewById(R.id.videoView);


        //setting listeners for buttons
        recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //"Standard Intent action that can be sent to have the camera
                // application capture a video and return it"
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
    }

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
}