package com.test.musicplayer.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.test.musicplayer.Constant;
import com.test.musicplayer.adapter.MusicFolder;
import com.test.musicplayer.bean.MusicBean;
import com.test.musicplayer.impl.FileEvenListener;


public class MusicFileMgr {
	public static Context mContext;
	static FileEvenListener mListener;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	public static Map<String,Integer> mDirPaths = new HashMap<String,Integer>();

	/**
	 * 扫描拿到所有的音乐文件夹
	 */
	public List<MusicFolder> mAllFolders = new ArrayList<MusicFolder>();
	public static List<MusicBean> mAllMusicBeans = new ArrayList<MusicBean>();
	
	 Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	
	public MusicFileMgr(Context mContext){
		this.mContext = mContext;
	}
	
	public void setFileEventListener(FileEvenListener listener){
		this.mListener = listener;
		browseToRoot();
	}
	
	public void browseToRoot(){
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				getMusics();
				// 通知扫描音乐完成
				mListener.onScanFileDetailFinish(mAllMusicBeans);
			}
		}).start();
	}
	
	/**
	 * 利用ContentProvider扫描
	 */
	private void getMusics()
	{
		mAllFolders.clear();
		mAllMusicBeans.clear();
		String firstImage = null;
	        Cursor mCursor=mContext.getContentResolver().query(uri,
	                new String[]{
	                        MediaStore.Audio.Media.TITLE,
	                        MediaStore.Audio.Media.ARTIST,
	                        MediaStore.Audio.Media.DATA},null,null,null);
	    if (mCursor== null) 
	    {
			return;
		}
		Log.e("MusicFileMgr", mCursor.getCount() + "");
		while (mCursor.moveToNext())
		{
			
			  String song =  mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
              String singer =    mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
              String path =   mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
              mAllMusicBeans.add(new MusicBean(song, singer, path));

			Log.e("TAG", path);
			// 拿到第一个媒体的路径
			if (firstImage == null)
				firstImage = path;
			// 获取该媒体的父路径名
			File parentFile = new File(path).getParentFile();
			if (parentFile == null)
				continue;
			String dirPath = parentFile.getAbsolutePath();
			// 利用一个HashSet防止多次扫描同一个文件夹
			if (!mDirPaths.containsKey(dirPath))
			{
				mDirPaths.put(dirPath, 1);
			} else
			{
				if (mDirPaths.size()!=0) {
					int size = mDirPaths.get(dirPath);
					mDirPaths.put(dirPath, ++size);
				}
			}
		}
		mCursor.close();
		 for (Entry<String, Integer> entry : mDirPaths.entrySet())
	        {
	            String key = entry.getKey();
	            int value = entry.getValue();
	            MusicFolder musicFloder = new MusicFolder();
	            musicFloder.setDir(key);
	            musicFloder.setCount(value);
	            mAllFolders.add(musicFloder);
	        }
		// 扫描完成，辅助的HashSet也就可以释放内存了
		mDirPaths.clear();
		
	}

}
