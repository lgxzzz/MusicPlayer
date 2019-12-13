package com.test.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.test.musicplayer.bean.MusicBean;
import com.test.musicplayer.service.MusicService;
import com.test.musicplayer.service.MusicViewMgr;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    MusicService.MediaMsgBinder mMediaMsgBinder;

    MusicViewMgr mMusicViewMgr;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }else{
            startMusicBackGroudService();

            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                startMusicBackGroudService();

                init();
            } else {
                // User refused to grant permission.
            }
        }
    }

    //初始化
    public void init(){
        mMusicViewMgr = new MusicViewMgr(this);
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
            mMediaMsgBinder.getService().setmMsgListner(new MusicService.MsgListner() {
                @Override
                public void onScanFinish(List<MusicBean> musicBeans) {
                    mMusicViewMgr.onScanFinish(musicBeans);
                }

                @Override
                public void onPlayerStatus(int state, MusicBean bean) {
                    mMusicViewMgr.onPlayerStatus(state,bean);
                }

                @Override
                public void onProgress(int progress,int duration){
                    mMusicViewMgr.onProgress(progress,duration);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
