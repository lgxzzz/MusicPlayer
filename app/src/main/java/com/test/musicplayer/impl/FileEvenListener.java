package com.test.musicplayer.impl;

import java.util.List;

import com.test.musicplayer.bean.MusicBean;

public interface FileEvenListener {
	public void onScanFileParentFinish(List<MusicBean> default_musicBeans);
	public void onScanFileDetailFinish(List<MusicBean> musicBeans);
	public void onLooperScanFileFinish(List<MusicBean> musicBeans, boolean needToRefresh);
	public void onScanSDFileFinish(List<MusicBean> musicBeans);
}
