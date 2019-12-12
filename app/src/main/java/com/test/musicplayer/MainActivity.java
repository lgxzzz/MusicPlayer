package com.test.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.test.musicplayer.service.MusicService;

public class MainActivity extends AppCompatActivity {
    MusicService.MediaMsgBinder mMediaMsgBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMusicBackGroudService();
    }

    //绑定后台服务
    public void startMusicBackGroudService(){
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent,connection, Service.BIND_AUTO_CREATE);

    }

    //解绑后台服务
    public void stopMusicBackGroudService(){
        Intent intent = new Intent(this, MusicService.class);
        unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMediaMsgBinder = (MusicService.MediaMsgBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
