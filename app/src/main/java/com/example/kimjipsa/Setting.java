package com.example.kimjipsa;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Setting extends Activity {
    CheckBox cb1, cb2;
    SeekBar sb;
    Button timeBtn;
    EditText editText;
    Gloval gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        cb1 = (CheckBox)findViewById(R.id.onbox);
        cb2 = (CheckBox)findViewById(R.id.offbox);
        sb = (SeekBar)findViewById(R.id.seekBar1);
        timeBtn = (Button)findViewById(R.id.beaconTimeCycleButton);
        editText=(EditText)findViewById(R.id.beaconTimeCycle);
        final AudioManager vibe = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                vibe.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }
        });
        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                vibe.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        });

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sb.setMax(nMax);
        sb.setProgress(nCurrentVolumn);

        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress, 0);
            }
        });


        //*****************************시간 확인 이부분에 기능 추가*******************************
         timeBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                gv.setCycleTime(editText.getText().toString());
            }
        });





    }
}