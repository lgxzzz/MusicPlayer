package com.test.musicplayer.impl;

import java.util.List;

import com.test.musicplayer.bean.MusicBean;

public interface EventListener {
	public void onScanFinish(List<MusicBean> musicBeans);
    public void onPlayerStatus(int state, MusicBean bean);
    public void onProgress(int duration,int progress);
}
