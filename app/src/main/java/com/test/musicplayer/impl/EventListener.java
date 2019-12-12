package com.test.musicplayer.impl;

import java.util.List;

import com.test.musicplayer.bean.MusicBean;

public interface EventListener {
	public void onScanFinish();
	public void refreshInfo(MusicBean bean);
	public void onLoadLrcFinish(MusicBean bean);
	public void onClearLrc();
	public void onLooper();
	public void onScanSDFinish(int size);
}
