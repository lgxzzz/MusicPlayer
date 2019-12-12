package com.test.musicplayer.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.test.musicplayer.Constant;
import com.test.musicplayer.bean.MusicBean;


public class Util {
	public static MusicBean convertStringToMusicBean(String str){
		try {
			JSONObject object = new JSONObject(str);
			MusicBean bean  =new MusicBean();
			if (object.opt("Song")!=null) {
				String Song = object.getString("Song");
				bean.setSong(Song);
			}
			if(object.opt("Path")!=null){
				String Path = object.getString("Path");
				bean.setPath(Path);
			}
			if(object.opt("Singer")!=null){
				String Singer = object.getString("Singer");
				bean.setSinger(Singer);
			}
			if(object.opt("Progress")!=null){
				int Progress = object.getInt("Progress");
				bean.setProgress(Progress);
			}
			return bean;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static String convertMusicBeanToString(MusicBean bean){
		JSONObject object = new JSONObject();
		try {
			object.put("Song",bean.Song);
			object.put("Path",bean.Path);
			object.put("Singer",bean.Singer);
			object.put("Singer",bean.Singer);
			object.put("Progress",bean.Progress);
			return object.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isSDFile(String path){
		if (path.contains("/emulated/")) {
			return  true;
		}
		return false;
	}
	
	
	//保存mode
	public static void saveMode(int mode,Context mContext){
		if (mode > Constant.MODE_RANDOM) {
			mode = Constant.MODE_SINGLE;
		}
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("imelocalmusic", Context.MODE_PRIVATE); //私有数据
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putInt("mode", mode);
		editor.commit();//提交修改
//		Log.e("lgx", "saveMode:"+mode);
	}
	
	//加载mode
	public static int getMode(Context mContext)
	{
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("imelocalmusic", Context.MODE_PRIVATE); //私有数据
		int mode = sharedPreferences.getInt("mode", Constant.MODE_LIST);
		return mode;
	}
}
