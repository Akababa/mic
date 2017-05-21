package com.example.m9pang.mic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.nio.ByteBuffer;

public class Debug extends AppCompatActivity implements Parcelable{
    private static final String LOG_TAG = "Debug";
    //settings
    int audioSource = MediaRecorder.AudioSource.MIC;
    int inputHz;int outputHz; /* in Hz*/
    int audioEncoding;
    int bufferSize;
    int numFrames;
    int rblock=AudioRecord.READ_NON_BLOCKING,wblock=AudioTrack.WRITE_NON_BLOCKING;
    boolean useArray=false;
    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private boolean playBack;
    AudioRecord recorder;
    AudioTrack player;

    public Debug(){};
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_debug);
        setSettings(findViewById(R.id.change));
    }

    public void tog(View view){
        ToggleButton on=(ToggleButton)view;
        playBack=on.isChecked();
        if(!playBack){
            stopService(new Intent(this,LoopbackService.class));
            //recorder.release();player.release();
            return;
        }
        startService(new Intent(this, LoopbackService.class).putExtra("info",this));
/*
        recorder = new AudioRecord.Builder()
                .setAudioSource(audioSource)
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(audioEncoding)
                        .setSampleRate(inputHz)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build())
                .build();
        player = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA) // so user can control volume separately
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(audioEncoding)
                        .setSampleRate(inputHz)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .build();
        player.setPlaybackRate(outputHz);
        final int bufferSize=recorder.getBufferSizeInFrames();
        final int sizef;
        switch (audioEncoding) {
            case AudioFormat.ENCODING_PCM_8BIT:
                sizef = 1;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                sizef = 2;
                break;
            case AudioFormat.ENCODING_PCM_FLOAT:
                sizef = 4;
                break;
            default: sizef=0;
        }
        recorder.setPositionNotificationPeriod(numFrames);
        recorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
            //byte[] datab = new byte[numFrames];
            //short[] datas = new short[numFrames];
            //float[] dataf = new float[numFrames];
            ByteBuffer data = ByteBuffer.allocateDirect(bufferSize * sizef);
            @Override
            public void onPeriodicNotification(AudioRecord recorder){
                int read=0;
                /*if(useArray) {
                    switch (audioEncoding) {
                        case AudioFormat.ENCODING_PCM_8BIT:
                            read = recorder.read(datab, 0, numFrames, rblock);
                            if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                player.write(datab, 0, read, wblock);
                            break;
                        case AudioFormat.ENCODING_PCM_16BIT:
                            read = recorder.read(datas, 0, numFrames, rblock);
                            if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                player.write(datas, 0, read, wblock);
                            break;
                        case AudioFormat.ENCODING_PCM_FLOAT:
                            read = recorder.read(dataf, 0, numFrames, rblock);
                            if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                player.write(dataf, 0, read, wblock);
                            break;
                    }
                } else {
                    read = recorder.read(data, numFrames * sizef, rblock);
                    if (read != AudioRecord.ERROR_INVALID_OPERATION)
                        player.write(data, read, wblock);
                    data.clear();
                }
            //}
            public void onMarkerReached(AudioRecord recorder){}
        });

        recorder.startRecording();
        player.play();*/

/*
        final AudioRecord recorder = new AudioRecord.Builder()
                .setAudioSource(audioSource)
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(audioEncoding)
                        .setSampleRate(inputHz)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build())
                .build();
        final AudioTrack player = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA) // so user can control volume separately
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(audioEncoding)
                        .setSampleRate(inputHz)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .build();
        player.setPlaybackRate(outputHz);
        bufferSize=recorder.getBufferSizeInFrames();

        recorder.startRecording();
        player.play();
        new Thread(new Runnable(){
            @Override
            public void run() {
                int read=0;
                //data=ByteBuffer.allocateDirect(bufferSize*2);
                if(useArray) {
                    switch (audioEncoding) {
                        case AudioFormat.ENCODING_PCM_8BIT:
                            byte[] data = new byte[numFrames];
                            while (playBack) {
                                read = recorder.read(data, 0, numFrames, rblock);
                                if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                    player.write(data, 0, read, wblock);
                            }
                            break;
                        case AudioFormat.ENCODING_PCM_16BIT:
                            short[] datas = new short[numFrames];
                            while (playBack) {
                                read = recorder.read(datas, 0, numFrames, rblock);
                                if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                    player.write(datas, 0, read, wblock);
                            }
                            break;
                        case AudioFormat.ENCODING_PCM_FLOAT:
                            float[] dataf = new float[numFrames];
                            while (playBack) {
                                read = recorder.read(dataf, 0, numFrames, rblock);
                                if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                    player.write(dataf, 0, read, wblock);
                            }
                            break;

                    }
                }else {
                    int sizef = 1;
                    switch (audioEncoding) {
                        case AudioFormat.ENCODING_PCM_8BIT:
                            sizef = 1;
                            break;
                        case AudioFormat.ENCODING_PCM_16BIT:
                            sizef = 2;
                            break;
                        case AudioFormat.ENCODING_PCM_FLOAT:
                            sizef = 4;
                            break;
                    }
                    ByteBuffer data = ByteBuffer.allocateDirect(bufferSize * sizef);
                    while (playBack) {
                        read = recorder.read(data, numFrames * sizef, rblock);
                        if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                            player.write(data, read, wblock);
                        }
                        data.clear();
                    }
                }
                recorder.release();
                player.release();
            }
        }).start();*/
    }

    public void setSettings(View view){
        boolean temp=playBack;playBack=false;
        EditText ed=(EditText)findViewById(R.id.editText3);
        numFrames=Integer.parseInt(ed.getText().toString());
        CheckBox ch=(CheckBox)findViewById(R.id.checkBox);
        rblock=ch.isChecked()?AudioRecord.READ_BLOCKING:AudioRecord.READ_NON_BLOCKING;
        CheckBox ch2=(CheckBox)findViewById(R.id.checkBox2);
        wblock=ch2.isChecked()?AudioTrack.WRITE_BLOCKING:AudioTrack.WRITE_NON_BLOCKING;
        CheckBox ch3=(CheckBox)findViewById(R.id.useArray);
        useArray=ch3.isChecked();
        EditText ih=(EditText)findViewById(R.id.inputHz);
        inputHz=Integer.parseInt(ih.getText().toString());
        EditText oh=(EditText)findViewById(R.id.outputHz);
        outputHz=Integer.parseInt(oh.getText().toString());
        RadioGroup rg=(RadioGroup)findViewById(R.id.typedd);
        switch(rg.getCheckedRadioButtonId()){
            case R.id.s8: audioEncoding=AudioFormat.ENCODING_PCM_8BIT;break;
            case R.id.s16: audioEncoding=AudioFormat.ENCODING_PCM_16BIT;break;
            case R.id.s32: audioEncoding=AudioFormat.ENCODING_PCM_FLOAT;break;
        }
        bufferSize =  AudioRecord.getMinBufferSize(inputHz, AudioFormat.CHANNEL_IN_MONO, audioEncoding);
        TextView tv=(TextView)findViewById(R.id.textView);
        tv.setText("bufferSize: "+bufferSize+" bytes");
        EditText as=(EditText)findViewById(R.id.audioSource);
        audioSource=Integer.parseInt(as.getText().toString());

        if(temp) tog(findViewById(R.id.on));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,LoopbackService.class));
    }

    protected Debug(Parcel in) {
        audioSource = in.readInt();
        inputHz = in.readInt();
        outputHz = in.readInt();
        audioEncoding = in.readInt();
        bufferSize = in.readInt();
        numFrames = in.readInt();
        rblock = in.readInt();
        useArray = in.readByte() != 0x00;
        permissionToRecordAccepted = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(audioSource);
        dest.writeInt(inputHz);
        dest.writeInt(outputHz);
        dest.writeInt(audioEncoding);
        dest.writeInt(bufferSize);
        dest.writeInt(numFrames);
        dest.writeInt(rblock);
        dest.writeByte((byte) (useArray ? 0x01 : 0x00));
        dest.writeByte((byte) (permissionToRecordAccepted ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Debug> CREATOR = new Parcelable.Creator<Debug>() {
        @Override
        public Debug createFromParcel(Parcel in) {
            return new Debug(in);
        }

        @Override
        public Debug[] newArray(int size) {
            return new Debug[size];
        }
    };
}
