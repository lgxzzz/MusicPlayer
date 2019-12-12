package com.test.musicplayer.adapter;

public class MusicFolder
{
	/**
	 * 音乐的文件夹路径
	 */
	private String dir;


	/**
	 * 文件夹的名称
	 */
	private String name;

	/**
	 * 音乐的数量
	 */
	private int count;

	/**
	 * 
	 * */
	
	
	public String getDir()
	{
		return dir;
	}

	public void setDir(String dir)
	{
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}


	public String getName()
	{
		return name;
	}
	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	

}
