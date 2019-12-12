package com.test.musicplayer.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.test.musicplayer.Constant;
import com.test.musicplayer.bean.MusicBean;
import com.test.musicplayer.impl.EventListener;
import com.test.musicplayer.util.Util;


public class MusicPlayerMgr {
	public static String TAG = "MusicPlayerMgr";
	public static int MUSIC_PLAY  =  0x01;
	public static int MUSIC_PAUSE  =  0x02;
	public static int mCurrentState = MUSIC_PAUSE;
	
	public static Context mContext;
	public static List<MusicBean> mUsingMusicBeans = new ArrayList<MusicBean>();//当前使用的播放列表
	public static List<MusicBean> mSDmusicBeans = new ArrayList<MusicBean>();//当前返回的SD卡文件列表
	private AudioManager mAudioManager;
	private static MediaPlayer mMediaPlayer;
	public static MusicBean mCurrentBean = null;
	public static MusicBean mLastBean = null;
	private static int mCurrentMode = Constant.MODE_LIST;
	
	private boolean needRestart = false;
	//切换歌曲的时间戳
	private static List<Long> mSwitchSongTimeStamp = new ArrayList<Long>();
	
	//SD卡拨出失去音频(或者音频文件被删除)后的重试次数
	static int sdUnmount = 0;
	static boolean isSwitchFast = false;
	
	public MusicPlayerMgr(Context mContext){
		this.mContext = mContext;
		init();
	}
	
	public void init(){
		mSwitchSongTimeStamp.clear();
		sdUnmount = 0;
		isSwitchFast = false;	
		
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.requestAudioFocus(new OnAudioFocusChangeListener() {
			
			@Override
			public void onAudioFocusChange(int focusChange) {
				// TODO Auto-generated method stub
				switch (focusChange) {
				 //重新获取焦点
                case AudioManager.AUDIOFOCUS_GAIN:
                    //判断是否需要重新播放音乐
                    if (needRestart) {
                    	play();
                        needRestart = false;
                    }
                    break;
                //暂时失去焦点
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //暂时失去焦点，暂停播放音乐（将needRestart设置为true）
                    if (isPlaying()) {
                        needRestart = true;
                        pasuse();
                    }
                    break;
                //时期焦点
                case AudioManager.AUDIOFOCUS_LOSS:
                    //暂停播放音乐，不再继续播放
                    pasuse();
                    break;
				default:
					break;
				}
			}
		},AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
		
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mLastBean = mCurrentBean;
				Log.e(TAG, "onCompletion");
				mSwitchSongTimeStamp.add(System.currentTimeMillis());
				if (!isSwitchFast&&isSwitchFast()) 
				{
					Log.e(TAG, "isSwitchFast!");
					return;
				}
				switch (mCurrentMode) {
				case Constant.MODE_LIST:
					next();
					break;
				case Constant.MODE_RANDOM:
					if (mUsingMusicBeans.size()>0) {
						Random random = new Random();
						int index = random.nextInt(mUsingMusicBeans.size());
						playNow(index);
					}
					break;
				case Constant.MODE_SINGLE:
					setCurrent(mLastBean);
					play(mLastBean);
					break;
				default:
					break;
				}
			}
		});
	}

	EventListener mListener;
	public void setEventListener(EventListener listener){
		this.mListener = listener;
	}

	public void setData(List<MusicBean> mUsingMusicBeans){
		Log.e(TAG, "setData:"+mUsingMusicBeans.size());
		this.mUsingMusicBeans = mUsingMusicBeans;
	}
	
	public void setDefault(List<MusicBean> mUsingMusicBeans){
		Log.e(TAG, "setDefault:"+mUsingMusicBeans.size());
		this.mUsingMusicBeans = mUsingMusicBeans;
		mListener.onScanFinish();
	}
	
	public void setSDData(List<MusicBean> mSDmusicBeans){
		this.mSDmusicBeans = mSDmusicBeans;
		if (mUsingMusicBeans.size()==0) {
			Log.e(TAG, "set sd data to using!");
			mUsingMusicBeans = mSDmusicBeans;
		}
	}
	
	
	public void setCurrent(final MusicBean bean){
		Log.e(TAG, "setCurrent");
		if(bean==null)
		{
			Log.e(TAG, "setCurrent bean is null!");
			return;
		}else if (!new File(bean.getPath()).exists()) {
			Log.e(TAG, "setCurrent music file is not exist");
			return;
		}
		mLastBean = mCurrentBean;
		mCurrentBean = bean;
   	    try {
   	    	mCurrentState = MUSIC_PAUSE;
   	    	mMediaPlayer.stop();
	    	mMediaPlayer.reset();
			File file = new File(bean.getPath()); 
			FileInputStream fis = new FileInputStream(file); 
			mMediaPlayer.setDataSource(fis.getFD()); 
	    	mMediaPlayer.prepare();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public static boolean isPlaying(){
//		Log.e(TAG, "isPlaying:"+ (mCurrentState == MUSIC_PLAY?true:false));
		return mCurrentState == MUSIC_PLAY?true:false; 
	}
	
	public void play(){
		Log.e(TAG, "play");
		if (!mMediaPlayer.isPlaying()) 
		{
			boolean flag = false;
			if (mCurrentBean!=null) {
				flag = true;
			}else{
				//当前没有播放歌曲时取列表里一项
				if (mUsingMusicBeans.size()!=0) 
				{
					mCurrentBean = mUsingMusicBeans.get(0);
				}
				if (mCurrentBean==null) 
				{
					flag = false;
				}else
				{
					flag =true;
				}
			}
			Log.e(TAG, mCurrentBean==null?"mCurrentBean is null":"mCurrentBean is not null");
			if (flag) {
				Log.e(TAG, "play mCurrentBean:"+" "+mCurrentBean.getPath());
				mCurrentState = MUSIC_PLAY;
				Log.e(TAG, "play:"+ (mCurrentState == MUSIC_PLAY?true:false));
				mMediaPlayer.start();
				mListener.refreshInfo(mCurrentBean);
				mSwitchSongTimeStamp.clear();
				saveState();
			}
		}
	}
	
	public void play(MusicBean bean){
		if (bean!=null) 
		{
			if (mCurrentBean == null) 
			{
				setCurrent(bean);
				play();
			}else if(!bean.getPath().equals(mCurrentBean.getPath()))
			{
				setCurrent(bean);
				play();
			}else{
				if (isPlaying()) 
				{
					pasuse();
				}else{
					play();
				}
			}
		}
	}
	
	public void reset(){
		if(mCurrentBean!=null){
			String path=mCurrentBean.getPath();
			if(Util.isSDFile(path)){
//				Toast.makeText(mContext, "内置", 0).show();
			}else{
				mCurrentState = MUSIC_PAUSE;
				mMediaPlayer.reset();
//				mCurrentBean.setSong("");
				mCurrentBean=null;
			}
		}
	}
	
	public void pasuse(){
		if (mMediaPlayer.isPlaying()) {
			mCurrentState = MUSIC_PAUSE;
			Log.e(TAG, "pasuse:"+ (mCurrentState == MUSIC_PLAY?true:false));
			mMediaPlayer.pause();
		}
	}
	
	public void next(){
		if (mUsingMusicBeans==null|mUsingMusicBeans.size()==0) {
			Log.e(TAG, "next size is 0!");
			return;
		}
		int mCurrentIndex = getCurrentIndex();
		mLastBean = mCurrentBean;
		switch (mCurrentMode) {
		case Constant.MODE_LIST:
		case Constant.MODE_SINGLE:
			if (mCurrentIndex == mUsingMusicBeans.size()-1) 
			{
				mCurrentIndex = 0;
			}else{
				mCurrentIndex++;
			}
			playNow(mCurrentIndex);
			break;
		case Constant.MODE_RANDOM:
			Random random = new Random();
			mCurrentIndex = random.nextInt(mUsingMusicBeans.size());
			playNow(mCurrentIndex);
			break;
		default:
			break;
		}
		
	}
	
	public void pre(){
		if (mUsingMusicBeans==null|mUsingMusicBeans.size()==0) {
			Log.e(TAG, "pre size is 0!");
			return;
		}
		int mCurrentIndex = getCurrentIndex();
		mLastBean = mCurrentBean;
		switch (mCurrentMode) {
		case Constant.MODE_LIST:
		case Constant.MODE_SINGLE:
			if (mCurrentIndex == 0) 
			{
				mCurrentIndex = mUsingMusicBeans.size()-1;
			}else{
				mCurrentIndex--;
			}
			playNow(mCurrentIndex);
			break;
		case Constant.MODE_RANDOM:
			Random random = new Random();
			mCurrentIndex = random.nextInt(mUsingMusicBeans.size());
			playNow(mCurrentIndex);
			break;
		default:
			break;
		}
		
	}
	
	public int getCurrentIndex(){
		int mCurrentIndex = 0;
		for (int i = 0; i < mUsingMusicBeans.size(); i++) {
			MusicBean mb = mUsingMusicBeans.get(i);
			if (mCurrentBean==null) {
				return mCurrentIndex;
			}
			if (mb.getPath().equals(mCurrentBean.getPath())) {
				mCurrentIndex = i;
			}
		}
		return mCurrentIndex;
	}
	
	public void seekTo(int progress){
		mMediaPlayer.seekTo(progress);
	}
	
	public void switchMode(int mode){
		if (mode > Constant.MODE_RANDOM) {
			mode = Constant.MODE_SINGLE;
		}
		mCurrentMode = mode;
//		Log.e("lgx", "mCurrentMode:"+mCurrentMode);
	}
	
	public void playNow(int index){
		setCurrent(mUsingMusicBeans.get(index));
		play();
	}
	
	public static MusicBean getCurrentMusicBean(){
		if (mCurrentBean!=null) {
			mCurrentBean.setProgress(mMediaPlayer.getCurrentPosition());
			mCurrentBean.setDuration(mMediaPlayer.getDuration());
		}
		return mCurrentBean;
	}
	
	//检测是否一秒内切换了很多歌曲
	public static boolean isSwitchFast(){
		if (mSwitchSongTimeStamp.size()>1) {
			long temp = mSwitchSongTimeStamp.get(0);
			for (int i = 1; i < mSwitchSongTimeStamp.size(); i++) {
					long temp1 = mSwitchSongTimeStamp.get(i);
					Log.e(TAG, "abs:"+Math.abs(temp-temp1));
					if (Math.abs(temp-temp1)<1000) 
					{
						sdUnmount++;
						Log.e(TAG, "count:"+sdUnmount);
						if (sdUnmount>=2) 
						{
							isSwitchFast = true;
							return true;
						}
					}
				}
		}
		return false;
	}
	
	public static int getCurrentMode(){
		return mCurrentMode;
	}
	
	public void saveState(){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("imelocalmusic", Context.MODE_PRIVATE); //私有数据
		Editor editor = sharedPreferences.edit();//获取编辑器
		if (mCurrentBean!=null) {
			editor.putString("musicbean", Util.convertMusicBeanToString(mCurrentBean));
			editor.commit();//提交修改
			Log.e(TAG, "saveState:"+ Util.convertMusicBeanToString(mCurrentBean));
		}
	}
}
