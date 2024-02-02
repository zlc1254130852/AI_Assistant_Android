package com.example.AI_Assistant;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class ChatAsrUtil {

    private Context context;

    private MediaRecorder mr = null;

    private MediaPlayer mediaPlayer;

    public ChatAsrUtil(Context context){
        this.context=context;

        mediaPlayer = new MediaPlayer();

    }


    public void onClick(int actionType){
        if(actionType== MotionEvent.ACTION_DOWN){

            if(mr == null){

                File soundFile = new File(ContextCompat.getExternalFilesDirs(context,Environment.DIRECTORY_DCIM)[0].getAbsolutePath()+"/openai.mp3");
                if(!soundFile.exists()){
                    try {
                        soundFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                mr = new MediaRecorder();
                mr.setAudioSource(MediaRecorder.AudioSource.MIC);
                mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mr.setOutputFile(soundFile.getAbsolutePath());
                Log.d("fileoutput",soundFile.getAbsolutePath());
                try {
                    mr.prepare();
                    mr.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (actionType== MotionEvent.ACTION_UP){

            if(mr != null){
                mr.stop();
                mr.release();
                mr = null;

            }
        }

    }

    public void destroy(){

    }

}
