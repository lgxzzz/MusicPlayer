package com.test.musicplayer.bean;

import android.widget.ImageView;

public class MusicBean {
	
	public String Singer;
	public String Album;
	public String Song;
	public String Path;
	public ImageView Pic;
	public int Progress = -1;
	public int Duration = -1;
	public boolean isChoose = false;
	
	public MusicBean(){
		
	}
	
	public MusicBean(String Song,String Singer,String Path){
		this.Song = Song;
		this.Singer = Singer;
		this.Path = Path;
	}
	

	public boolean isChoose() {
		return isChoose;
	}


	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}


	public int getProgress() {
		return Progress;
	}


	public void setProgress(int progress) {
		Progress = progress;
	}


	public int getDuration() {
		return Duration;
	}


	public void setDuration(int duration) {
		Duration = duration;
	}


	public String getSinger() {
		return Singer;
	}
	public void setSinger(String singer) {
		Singer = singer;
	}
	public String getAlbum() {
		return Album;
	}
	public void setAlbum(String album) {
		Album = album;
	}
	public String getSong() {
		return Song;
	}
	public void setSong(String song) {
		Song = song;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}
	public ImageView getPic() {
		return Pic;
	}
	public void setPic(ImageView pic) {
		Pic = pic;
	}
	
	
}
