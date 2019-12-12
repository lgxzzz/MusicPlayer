package com.test.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    MediaMsgBinder mMediaMsgBinder = new MediaMsgBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMediaMsgBinder;
    }


    //多媒体消息binder
    public class MediaMsgBinder extends Binder{
        public void pre(){};
        public void next(){};
        public void play(){};
        public void pause(){};
        public void switchMode(int mode){};
    }

}
