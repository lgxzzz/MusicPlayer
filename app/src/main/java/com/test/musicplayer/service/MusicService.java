package com.test.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.test.musicplayer.bean.MusicBean;
import com.test.musicplayer.impl.EventListener;

import java.util.List;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    MediaMsgBinder mMediaMsgBinder = new MediaMsgBinder();
    //内容管理
    MusicContext mMusicContext;
    //状态回调
    MsgListner mMsgListner;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMediaMsgBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mMusicContext = new MusicContext(this);

    }

    //多媒体消息binder
    public class MediaMsgBinder extends Binder{
        public void pre(){
            mMusicContext.pre();
        };
        public void next(){
            mMusicContext.next();
        };
        public void play(){
            mMusicContext.play();
        };
        public void pause(){
            mMusicContext.pasuse();
        };
        public void switchMode(int mode){
            mMusicContext.switchMode(mode);
        };

        public MusicService getService(){
            return MusicService.this;
        }
    }

    public void setmMsgListner(MsgListner mMsgListner){
        this.mMsgListner = mMsgListner;
        mMusicContext.setEventListener(new EventListener() {
            //扫描文件
            @Override
            public void onScanFinish(List<MusicBean> musicBeans) {
                MusicService.this.mMsgListner.onScanFinish(musicBeans);
            }
            //播放状态
            @Override
            public void onPlayerStatus(int state, MusicBean bean) {
                MusicService.this.mMsgListner.onPlayerStatus(state,bean);
            }
            //更新进度
            @Override
            public void onProgress(int progress,int duration){
                MusicService.this.mMsgListner.onProgress( progress, duration);
            }
        });
    }

    public interface MsgListner {
        void onScanFinish(List<MusicBean> musicBeans);
        void onPlayerStatus(int state, MusicBean bean);
        void onProgress(int progress,int duration);
    }
}
