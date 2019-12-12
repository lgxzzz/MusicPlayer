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
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;

	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	public static Map<String,Integer> mDirPaths = new HashMap<String,Integer>();

	/**
	 * 扫描拿到所有的音乐文件夹
	 */
	public List<MusicFolder> mAllFolders = new ArrayList<MusicFolder>();
	public static List<MusicBean> mAllMusicBeans = new ArrayList<MusicBean>();
	
	/**
	 * 扫描到的外置路径文件夹
	 * */
	public List<MusicFolder> mSDFolders = new ArrayList<MusicFolder>();
	public static List<MusicBean> mSDMusicBeans = new ArrayList<MusicBean>();
	
	
	/**
	 * 当前的文件夹路径，退出时保存状态
	 * */
	public String mCurrentFolerPath = Constant.ALL_FOLDERS_LIST;
	
	public int mCurrentMode = Constant.FILE_LIST;
	
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
				mCurrentMode =  Constant.FILE_LIST;
				
				//加载SD卡文件
				File p = new File("/storage/");
				scanFileList(p);
				
				getMusics();
//				//读取上次保存的文件路径
//				loadSave();
				// 通知扫描音乐完成
				mListener.onScanFileParentFinish(mAllMusicBeans);
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
	            if (isSDFilePath(key)) 
	            {
					musicFloder.setCount(getSDfileSize(key));
				}
	            mAllFolders.add(musicFloder);
	        }
		// 扫描完成，辅助的HashSet也就可以释放内存了
		mDirPaths.clear();
		
	}

	public synchronized void onFolderSelect(final int position){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mCurrentMode == Constant.FILE_LIST) 
				{
					   	Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
						mAllMusicBeans.clear();
						mImgDir = new File(mAllFolders.get(position).getDir());
						String mPath = mImgDir.getAbsolutePath();
						//如果是外置SD卡路径
						if (isSDFilePath(mPath))  
						{
							for (int i = 0; i < mSDMusicBeans.size(); i++) 
							{
								MusicBean bean = mSDMusicBeans.get(i);
								String t_path = bean.getPath();
								//根据后缀长度判断是否当前文件
								 String[] file_folder = t_path.replace(mPath, "").split("/");
				                 if (file_folder.length>=3) 
				                 {
									continue;
				                 }
				                 int index = t_path.lastIndexOf("/");
				                 String parent = t_path.substring(0, index);
				                 if (parent.equals(mPath)) 
				                 {
				                	mAllMusicBeans.add(bean);
				                 }
							}
						}else
						{
							   //不是SD路径则检索数据库
						        Cursor cursor=mContext.getContentResolver().query(uri,
						                new String[]{
						                        MediaStore.Audio.Media.TITLE,
						                        MediaStore.Audio.Media.ARTIST,
						                        MediaStore.Audio.Media.DATA},"_data"+" like '%"+mPath+"%'",null,null);
						        while (cursor.moveToNext()) 
						        {
						                  String song =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
						                  String singer =    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
						                  String path =   cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
						                  //排除2级目录下文件
						                  // 如父路径/storage/udisk1
						                  // /storage/udisk1/小幸运-田馥甄.aac  正确
						                  // /storage/udisk1/音乐/蔡依林 - 说爱你.mp3  过滤
						                  String[] file_folder = path.replace(mPath, "").split("/");
						                  if (file_folder.length>=3) 
						                  {
											continue;
						                  }
						                  if (new File(path).exists()) 
						                  {
						                	   mAllMusicBeans.add(new MusicBean(song, singer, path));
						                  }
						        }
						}
					     
					mListener.onScanFileDetailFinish(mAllMusicBeans);
					mCurrentMode =  Constant.FILE_DETAIL;
					mCurrentFolerPath = mPath;
				}
			}
		}).start();
	}
	
	public void onBackToFolder(){
		if (mCurrentMode != Constant.FILE_LIST) 
		{
			mCurrentMode = Constant.FILE_LIST;
			mListener.onScanFileDetailFinish(mAllMusicBeans);
			mCurrentFolerPath = Constant.ALL_FOLDERS_LIST;
		}
	}
	
	public void onOpenAllFolder(){
			mAllMusicBeans.clear();
			Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			        Cursor cursor=mContext.getContentResolver().query(uri,
			                new String[]{
			                        MediaStore.Audio.Media.TITLE,
			                        MediaStore.Audio.Media.ARTIST,
			                        MediaStore.Audio.Media.DATA},null,null,null);
			        while (cursor.moveToNext()) {
			                  String song =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			                  String singer =    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			                  String path =   cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			                  if (new File(path).exists()) 
			                  {
			                	   mAllMusicBeans.add(new MusicBean(song, singer, path));
			                  }
			        }
			mListener.onScanFileDetailFinish(mAllMusicBeans);
			mCurrentMode =  Constant.FILE_OPEN_ALL;
			mCurrentFolerPath = Constant.ALL_FOLDERS_MUSICS;
	}
	
	public void onRefreshFolder(){
		getMusics();
		mListener.onScanFileDetailFinish(mAllMusicBeans);
		mCurrentMode =  Constant.FILE_LIST;
		mCurrentFolerPath = Constant.ALL_FOLDERS_LIST;
	}
	
	
	public void onLooperScanFile(){
		//更新SD文件多媒体信息
		onRefreshSDFile();
	}
	
	public synchronized void onRefreshSDFile(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				List<MusicBean> all_beans = new ArrayList<MusicBean>();
				Cursor cursor=mContext.getContentResolver().query(uri,
		                new String[]{
		                        MediaStore.Audio.Media.TITLE,
		                        MediaStore.Audio.Media.ARTIST,
		                        MediaStore.Audio.Media.DATA},"_data"+" like '%"+"/storage"+"%'",null,null);
				while (cursor!=null&&cursor.moveToNext()) 
		        {
		                  String song =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		                  String singer =    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		                  String path =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		                  all_beans.add(new MusicBean(song, singer, path));
		        }
				List<MusicBean> t_beans = new ArrayList<MusicBean>();
//				Log.e("lgx", "mSDMusicBeans size:"+mSDMusicBeans.size());
				for (int j = 0; j < mSDMusicBeans.size(); j++) {
					MusicBean bean = mSDMusicBeans.get(j);
					String t_path = bean.getPath();
					String t_song = bean.getSong();
					String t_singer = bean.getSinger();
					for (int i = 0; i < all_beans.size(); i++) 
					{
						MusicBean a_bean = all_beans.get(i);
						if (a_bean.getPath().equals(t_path)) 
						{
							bean.setSong(a_bean.getSong());
							bean.setSinger(a_bean.getSinger());
						}
					}
					t_beans.add(bean);
				}
//				Log.e("lgx", "t_beans size:"+t_beans.size());
				mSDMusicBeans = t_beans;
			}
		}).start();
	
	}
	
	public void startToScanSD(){
		mSDMusicBeans.clear();
		mDirPaths.clear();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				File p = new File("/storage/");
				scanFileList(p);
				// 扫描完成，辅助的HashSet也就可以释放内存了
	    		if (mCurrentMode == Constant.FILE_LIST) 
	    		{
	    			mListener.onScanSDFileFinish(mSDMusicBeans);
	    		}
			}
		}).start();
	}
	
	public void onClearSD(){
		mSDMusicBeans.clear();
		mDirPaths.clear();
	}
	
    /** 
     * 扫描Sdcard（外部存储）下所有文件 
     */  
    public void scanFileList(final File p) {  
        File[] listFile = p.listFiles();  
        if (listFile==null) {
			return;
		}
        int length = listFile.length;  
        if (listFile != null) 
        {  
            for (int i = 0; i < length; i++) 
            {  
                File file = listFile[i];  
                String path = file.getAbsolutePath();
//                Log.e("lgx","path:"+ file.getAbsolutePath());
                if (path.contains("/sdcard0")) {
					continue;
				}
                if (file.isDirectory()) {  
                    scanFileList(file);  
                } 
                else 
                {  
                    //file://music/p/xx.mp3  
                	   String fileName = file.getName();  
                    if (file.getName().endsWith(".mp3")||file.getName().endsWith(".aac")) 
                    {  
//                       String fileName = file.getName();
//                       Log.e("lgx", "music:"+fileName);
                       mSDMusicBeans.add(new MusicBean(fileName, "未知歌手", path));
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
                }  
            }
   		 for (Entry<String, Integer> entry : mDirPaths.entrySet())
	        {
	            String key = entry.getKey();
	            int value = entry.getValue();
	            MusicFolder musicFloder = new MusicFolder();
	            musicFloder.setDir(key);
	            musicFloder.setCount(value);
	            if (!isAllFilePath(key)) 
	            {
	            	mAllFolders.add(musicFloder);
				}else{
					removeAllFilePath(key); 
					mAllFolders.add(musicFloder);
				}
	            removeSDFilePath(key);
				mSDFolders.add(musicFloder);
	        }
        }

    } 
	
    public boolean isSDFilePath(String path){
    	for (int i = 0; i < mSDFolders.size(); i++) {
			MusicFolder folder = mSDFolders.get(i);
			if (path.equals(folder.getDir())) 
			{
				return true;
			}
		}
    	return false;
    }
    
    public void removeAllFilePath(String path){
    	for (int i = 0; i < mAllFolders.size(); i++) {
			MusicFolder folder = mAllFolders.get(i);
			String dir = folder.getDir();
			if (dir==null) {
				continue;
			}
			if (path.equals(folder.getDir())) 
			{
				mAllFolders.remove(folder);
			}
		}
    }
    
    public void removeSDFilePath(String path){
    	for (int i = 0; i < mSDFolders.size(); i++) {
			MusicFolder folder = mSDFolders.get(i);
			String dir = folder.getDir();
			if (dir==null) {
				continue;
			}
			if (path.equals(folder.getDir())) 
			{
				mSDFolders.remove(folder);
			}
		}
    }
    
    public int getSDfileSize(String path){
    	for (int i = 0; i < mSDFolders.size(); i++) {
			MusicFolder folder = mSDFolders.get(i);
			String dir = folder.getDir();
			if (dir==null) {
				continue;
			}
			if (path.equals(folder.getDir())) 
			{
				return folder.getCount();
			}
		}
    	return 0;
    }
    
    public boolean isAllFilePath(String path){
    	for (int i = 0; i < mAllFolders.size(); i++) {
			MusicFolder folder = mAllFolders.get(i);
			String dir = folder.getDir();
			if (dir==null) {
				continue;
			}
			if (path.equals(folder.getDir())) 
			{
				return true;
			}
		}
    	return false;
    }
    
    
	public MusicBean ChooseMusicBean(int index){
		MusicBean bean = mAllMusicBeans.get(index);
		return bean;
	}
	
	public void loadSave(){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("imelocalmusic", Context.MODE_PRIVATE); //私有数据
		mCurrentFolerPath  = sharedPreferences.getString("folder", Constant.ALL_FOLDERS_LIST);
		if (mCurrentFolerPath.equals(Constant.ALL_FOLDERS_LIST)) 
		{
			onBackToFolder();
		}else if(mCurrentFolerPath.equals(Constant.ALL_FOLDERS_MUSICS)){
			onOpenAllFolder();
		}else if (!mCurrentFolerPath.equals(Constant.ALL_FOLDERS_LIST)&&mAllFolders.size()>0) 
		{
			for (int i = 0; i < mAllFolders.size(); i++) {
				MusicFolder folder = mAllFolders.get(i);
				mImgDir = new File(folder.getDir());
				String mPath = mImgDir.getAbsolutePath();
				if (mPath.equals(mCurrentFolerPath)) {
					onFolderSelect(i);
				}
			}
		}
	}
	
	public void saveState(){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("imelocalmusic", Context.MODE_PRIVATE); //私有数据
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString("folder", mCurrentFolerPath);
		editor.commit();//提交修改
	}
	
}
