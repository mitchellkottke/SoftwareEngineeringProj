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

    private Button recordView, playView;
    private VideoView viewOfVideo;
    private int ACTIVITY_START_CAMERA_APP = 0;

    //private static final int REQUEST_CODE = 1;
    //VideoView myVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        //initiaizing buttons and videoView
        recordView = (Button) findViewById(R.id.recordButton);
        playView = (Button) findViewById(R.id.playbackButton);
        viewOfVideo = (VideoView) findViewById(R.id.videoView);
        //myVideoView = findViewById(R.id.videoView);

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
//    @Override
//    public boolean onCreateOptionsMenu(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }

//        return super.onOptionsItemSelected(item);
//    }


    //in order to open the built in camera app
//    public void recordVideo(View view){
//        Intent myIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//
//        File videoFile = getFile() ;
//        Uri newUri = Uri.fromFile(videoFile);
//
//        myIntent.putExtra(MediaStore.EXTRA_OUTPUT, newUri);
//        myIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        startActivityForResult(myIntent, REQUEST_CODE);
//        if(myIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(myIntent, REQUEST_CODE);
//        }
//    }
//    @Override
//   protected void onActivityResult(int requestCode, int resultCode, Intent myIntent) {
//       if (requestCode==RESULT_OK) {
//           Toast.makeText(getApplicationContext(), "Video has been saved", Toast.LENGTH_LONG).show();
//       }
//       else {
//           Toast.makeText(getApplicationContext(), "ERROR video not saved", Toast.LENGTH_LONG).show();
//       }
//
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            Uri videoUri = myIntent.getData();
//            myVideoView.setVideoURI(videoUri);
//        }
//    }
//
//
//   public File getFile() { //to create a folder and video file
//
//        File myFolder = new File("sdcard/myfolder");
//
//        if(!myFolder.exists()){ //if folder doesnt exit
//            myFolder.mkdir(); //create folder
//        }
//
//        File myVideoFile = new File(myFolder, "myVideo.mp4");
//        return myVideoFile;
//    }
//
//}
