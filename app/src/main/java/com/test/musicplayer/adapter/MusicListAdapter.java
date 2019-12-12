package com.test.musicplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import com.test.musicplayer.bean.MusicBean;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MusicListAdapter extends BaseAdapter{
	Context mContext;
	List<MusicBean> musicBeans = new ArrayList<MusicBean>();
	ListView mListView;
	
	public MusicListAdapter(Context mContext,List<MusicBean> musicBeans){
		this.mContext = mContext;
		this.musicBeans = musicBeans;
	}
	
	public void setListview(ListView mListView){
		this.mListView = mListView;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musicBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return musicBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MusicBean bean = musicBeans.get(position);
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
//			convertView = LayoutInflater.from(mContext).inflate(R.layout.music_item, null);
//			holder.Singer = (TextView) convertView.findViewById(R.id.music_item_singer);
//			holder.Song = (TextView) convertView.findViewById(R.id.music_item_song);
//			holder.index = (TextView) convertView.findViewById(R.id.music_item_index);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag(); 
		}
		holder.Singer.setText(bean.getSinger());
		holder.Song.setText(bean.getSong());
		holder.index.setText(position+1+"");
		if (bean.isChoose()) {
			holder.Singer.setTextColor(Color.GRAY);
			holder.Song.setTextColor(Color.WHITE);
			holder.index.setTextColor(Color.WHITE);
		}else{
			holder.Singer.setTextColor(Color.parseColor("#666666"));
			holder.Song.setTextColor(Color.parseColor("#cc6600"));
			holder.index.setTextColor(Color.parseColor("#cc6600"));
		}
		return convertView;
	}

	static class ViewHolder{
		TextView Singer;
		TextView Song;
		TextView index;
	}
	
	public void choose(int index){
		for (int i = 0; i < musicBeans.size(); i++) {
			MusicBean bean = musicBeans.get(i);
			if (index == i) {
				bean.setChoose(true);
			}else{
				bean.setChoose(false);
			}
		}
		notifyDataSetChanged();
	}
	
	//局部刷新
	public void choose(MusicBean bean,boolean flag){
//		if (bean!=null&&musicBeans.size()>0) {
//			int index = 0;
//			for (int i = 0; i < musicBeans.size(); i++) {
//				MusicBean tmp = musicBeans.get(i);
//				if (tmp.getPath().equals(bean.getPath())) {
//					tmp.setChoose(true);
//					index = i;
//				}else{
//					tmp.setChoose(false);
//				}
//			}
//			int visiblePosition = mListView.getFirstVisiblePosition();
//			int visibleCount = mListView.getLastVisiblePosition();
//			for (int i = 0; i <=visibleCount-visiblePosition; i++)
//			{
//				View view = mListView.getChildAt(i);
//				if (view!=null) {
//					TextView Singer = (TextView) view.findViewById(R.id.music_item_singer);
//					TextView Song = (TextView) view.findViewById(R.id.music_item_song);
//					TextView Index = (TextView) view.findViewById(R.id.music_item_index);
//					if (Singer!=null) {
//
//						Singer.setTextColor(Color.parseColor("#666666"));
//						Song.setTextColor(Color.parseColor("#cc6600"));
//						Index.setTextColor(Color.parseColor("#cc6600"));
//
//					}
//				}
//			}
//
//			View view = mListView.getChildAt(index - visiblePosition);
//			if (view!=null) {
//				TextView Singer = (TextView) view.findViewById(R.id.music_item_singer);
//				TextView Song = (TextView) view.findViewById(R.id.music_item_song);
//				TextView Index = (TextView) view.findViewById(R.id.music_item_index);
//				if (Singer!=null) {
//					Singer.setTextColor(Color.GRAY);
//					Song.setTextColor(Color.WHITE);
//					Index.setTextColor(Color.WHITE);
//				}
//			}
//			else
//			{
//				if (flag) {
//					mListView.setSelection(index);
//				}
//
//			}
//		}
	}
	
}
