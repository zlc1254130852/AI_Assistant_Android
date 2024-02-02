package com.example.AI_Assistant;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAudioPermission();
        checkNeedPermissions();

        findViewById(R.id.btn_chat).setOnClickListener(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    private void getAudioPermission(){
       int flag = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
       if (flag != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
       }

    }

    @Override
    public void onClick(View v) {

            if(v.getId()==R.id.btn_chat){
            Intent it = new Intent(this, ChatActivity.class);
            startActivity(it);
        }
    }

    private void checkNeedPermissions(){
        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }
}