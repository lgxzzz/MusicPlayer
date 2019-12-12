package com.test.musicplayer.impl;


import com.test.musicplayer.bean.MusicBean;

public interface ImeLocalMusicStateListener {
	public void onPlayerStatus(int state, MusicBean bean);
	public boolean isPlay();
}
