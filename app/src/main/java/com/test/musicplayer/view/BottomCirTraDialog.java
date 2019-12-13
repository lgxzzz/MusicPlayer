package com.test.musicplayer.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.test.musicplayer.R;
import com.test.musicplayer.bean.MusicBean;

import java.util.List;

import androidx.annotation.NonNull;

public class BottomCirTraDialog  extends Dialog implements AdapterView.OnItemClickListener{
    List<MusicBean> musicBeans;
    private SelectDialogListener mListener;
    private Activity mActivity;
    private boolean mUseCustomColor = false;
    private int mFirstItemColor;
    private int mOtherItemColor;
    private Button mBtnCancel;

    public BottomCirTraDialog(@NonNull Context context) {
        super(context);
    }

    public BottomCirTraDialog(Activity activity, int theme, SelectDialogListener listener, List<MusicBean> musicBeans) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.musicBeans = musicBeans;
        //点击Dialog外部消失
        setCanceledOnTouchOutside(true);
    }


    public interface SelectDialogListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.music_list, null);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = getWindow();

        //设置动画
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();

        //保证按钮水平满屏
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        //设置显示位置
        onWindowAttributesChanged(attributes);

        initView();
    }

    private void initView() {
        DialogAdapter dialogAdapter = new DialogAdapter(musicBeans);
        ListView listView = findViewById(R.id.music_listview);
        listView.setOnItemClickListener(this);
        listView.setAdapter(dialogAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onItemClick(parent, view, position, id);
        dismiss();
    }


    private class DialogAdapter extends BaseAdapter {
        List<MusicBean> musicBeans;
        private Viewholder viewholder;
        private LayoutInflater layoutInflater;

        public DialogAdapter(List<MusicBean> musicBeans) {
            this.musicBeans = musicBeans;
            this.layoutInflater = mActivity.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return musicBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return musicBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                viewholder = new Viewholder();
                convertView = layoutInflater.inflate(R.layout.music_list_item, null);
                viewholder.music_name = (TextView) convertView.findViewById(R.id.music_name);
                convertView.setTag(viewholder);
            } else {
                viewholder = (Viewholder) convertView.getTag();
            }
            viewholder.music_name.setText(musicBeans.get(position).getSong());

            return convertView;
        }

    }

    public static class Viewholder {
        public TextView music_name;
    }


    /**
     * 取消事件监听接口
     *
     */
    private SelectDialogCancelListener mCancelListener;

    public interface SelectDialogCancelListener {
        void onCancelClick(View v);
    }
}
