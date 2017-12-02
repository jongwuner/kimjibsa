package com.example.kimjipsa;

import com.example.kimjipsa.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class Loading extends Activity {
   Handler handler = new Handler();
    int value = 50;
    int add = 1;
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    value = value + add;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(value);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) { }

                }
            }
        });
        t.start();
        
        handler.postDelayed(new Runnable() {
           @Override
           public void run(){
           Intent i = new Intent(Loading.this, NoteList.class);
           startActivity(i);
   
           finish();
      }
       },SPLASH_TIME_OUT);
    }
}