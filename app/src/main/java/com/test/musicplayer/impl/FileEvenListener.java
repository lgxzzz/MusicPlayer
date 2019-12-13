package com.test.musicplayer.impl;

import java.util.List;

import com.test.musicplayer.bean.MusicBean;

public interface FileEvenListener {
	public void onScanFileDetailFinish(List<MusicBean> musicBeans);
}
