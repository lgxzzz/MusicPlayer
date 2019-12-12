package com.test.musicplayer.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.musicplayer.bean.MusicBean;
import com.test.musicplayer.impl.EventListener;
import com.test.musicplayer.impl.FileEvenListener;
import com.test.musicplayer.util.Util;

public class MusicContext {
	public static String TAG = "MusicContext";
	public static Context mContext;
	private EventListener mListener;
	private static MusicPlayerMgr mPlayerMgr;
	private static MusicFileMgr mFileMgr;
	private static boolean isInit = false;
	private MusicBean mSaveMusicBean;
	public MusicContext(Context mContext){
		this.mContext = mContext;
	}
	
	public void init(){
		mPlayerMgr =new MusicPlayerMgr(mContext);
		mFileMgr = new MusicFileMgr(mContext);
		mPlayerMgr.setEventListener(mListener);
		mPlayerMgr.switchMode(Util.getMode(mContext));
		mSaveMusicBean = getSaveMusicBean();
		mFileMgr.setFileEventListener(new FileEvenListener() {
			
			@Override
			public void onScanFileDetailFinish(List<MusicBean> musicBeans) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onScanFileDetailFinish");
				mPlayerMgr.setData(musicBeans);
				mListener.onScanFinish();
			}

			@Override
			public void onScanFileParentFinish(List<MusicBean> musicBeans) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onScanFileParentFinish");
				mPlayerMgr.setDefault(musicBeans);
				mListener.onScanFinish();
				isInit = true;
				//初始化完成后加载上一次状态
				mHandler.sendEmptyMessageDelayed(LOAD_SAVE, 500);
			}

			@Override
			public void onLooperScanFileFinish(List<MusicBean> musicBeans,
					boolean needToRefresh) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onLooperScanFileFinish");
				mListener.onLooper();
				if (needToRefresh) {
					mPlayerMgr.setData(musicBeans);
					mListener.onScanFinish();
				}
			}

			@Override
			public void onScanSDFileFinish(List<MusicBean> musicBeans) {
				// TODO Auto-generated method stub
				mPlayerMgr.setSDData(musicBeans);
				mListener.onScanSDFinish(musicBeans.size());
			}
		});
	}
	
	public boolean isInit(){
		return isInit;
	}
	
	public void setEventListener(EventListener listener){
		this.mListener = listener;
		init();
	}
	
	public static MusicFileMgr getMusicFileMgr(){
		return mFileMgr;
	}
	
	public static MusicPlayerMgr getMusicPlayerMgr(){
		return mPlayerMgr;
	}
	
	public static List<MusicBean> getMusicListBeans(){
		return mPlayerMgr.mUsingMusicBeans;
	}
	
	public static MusicBean getCurrentMusicBean(){
		return mPlayerMgr.getCurrentMusicBean();
	}
	
	public static boolean isPlaying(){
		return mPlayerMgr.isPlaying();
	}
	
	public static int getCurrentMode(){
		return mPlayerMgr.getCurrentMode();
	}
	
	public void play(){
		mPlayerMgr.play();
	}
	
	public void play(MusicBean bean){
		mPlayerMgr.play(bean);
	}
	
	public void pasuse(){
		mPlayerMgr.pasuse();
	}
	
	public void next(){
		mPlayerMgr.next();
	}
	
	public void pre(){
		mPlayerMgr.pre();
	}
	
	public void seekTo(int progress){
		mPlayerMgr.seekTo(progress);
	}
	
	public void switchMode(int mode){
		mPlayerMgr.switchMode(mode);
	}
		
	//保存当前播放状态
	public void saveCurrentState(){
		mFileMgr.saveState();
		mPlayerMgr.saveState();
	}
	
	public void waitToPlay(){
		while (!isInit) {
			try {
				Log.e(TAG, "Wait:"+isInit);
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				play();
			}
		}, 1000);
	}
	
	public void waitToPlay(final MusicBean bean){
		while (!isInit) {
			try {
				Log.e(TAG, "Wait:"+isInit);
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				play(bean);
			}
		}, 1000);
		
	}
	
	/*---------------------------------------------加载缓存---------------------------------------------------------*/
	public static final int LOAD_SAVE = 1;
	public static final int WAIT_SD_FILE = 2;
	int waitTimeOut = 6;
	private Handler mHandler = new Handler(new Handler.Callback() 
	{
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case LOAD_SAVE:
				loadSaveMusic();
				break;
			case WAIT_SD_FILE:
				if (waitTimeOut>0) 
				{
					waitTimeOut--;
					if (new File(mSaveMusicBean.getPath()).exists()) 
					{
						mPlayerMgr.setCurrent(mSaveMusicBean);
						mPlayerMgr.seekTo(mSaveMusicBean.getProgress());
						mPlayerMgr.play();
						mListener.onScanFinish();
					}else
					{
						mHandler.sendEmptyMessageDelayed(WAIT_SD_FILE, 2000);
					}
				}else
				{
					waitTimeOut = 6;
					Log.e(TAG, "load sd file timeOut !");
				}
				break;
			default:
				break;
			}
			return false;
		}
	});
	
	//获取保存的musicbean
	public MusicBean getSaveMusicBean(){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("imelocalmusic", Context.MODE_PRIVATE); //私有数据
		String musicbean = sharedPreferences.getString("musicbean", "");
		if (musicbean.length()>0) 
		{
			MusicBean bean = Util.convertStringToMusicBean(musicbean);
			return bean;
		} 
		return null;
	}
	
	//加载之前保存的状态
	public void loadSaveMusic(){
		Log.e(TAG, "loadSave:"+mSaveMusicBean);
			if (mSaveMusicBean!=null) 
			{
				if (Util.isSDFile(mSaveMusicBean.getPath())) 
				{
					Log.e(TAG, "loadSave sd music file");
					if (new File(mSaveMusicBean.getPath()).exists()) 
					{
						mPlayerMgr.setCurrent(mSaveMusicBean);
						mPlayerMgr.seekTo(mSaveMusicBean.getProgress());
						mPlayerMgr.play();
						mListener.onScanFinish();
					}else
					{
						mHandler.sendEmptyMessageDelayed(WAIT_SD_FILE, 2000);
					}
				}else
				{
					Log.e(TAG, "loadSave other music file");
					mPlayerMgr.setCurrent(mSaveMusicBean);
					mPlayerMgr.seekTo(mSaveMusicBean.getProgress());
					mPlayerMgr.play();
					mListener.onScanFinish(); 
				}
			}else
			{
				play();
			}
	}

}
