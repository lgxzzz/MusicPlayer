package com.test.musicplayer.service;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.musicplayer.Constant;
import com.test.musicplayer.MainActivity;
import com.test.musicplayer.R;
import com.test.musicplayer.bean.MusicBean;
import com.test.musicplayer.util.Util;
import com.test.musicplayer.view.BottomCirTraDialog;
import com.test.musicplayer.view.CircleProgressView;
import com.test.musicplayer.view.MarqueeTextView;

import java.util.ArrayList;
import java.util.List;

public class MusicViewMgr implements View.OnClickListener {

    public MainActivity mContext;
    public ViewGroup mViewGroup;
    private ImageView mMode;
    private ImageView mPre;
    private ImageView mNext;
    private ImageView mPlay;
    private TextView mSinger;
    private MarqueeTextView mSong;
    private TextView mProgress;
    private TextView mTimes;
    private CircleProgressView mCircleProgressView;
    private Button mListBtn;
    private BottomCirTraDialog mBottomCirTraDialog;
    private List<MusicBean> musicBeans = new ArrayList<>();

    public MusicViewMgr(Context context){
        this.mContext = (MainActivity) context;

        mViewGroup  = (ViewGroup) this.mContext.findViewById(R.id.music_info);
        mMode  = (ImageView) mViewGroup.findViewById(R.id.music_info_mode);
        mPre  = (ImageView) mViewGroup.findViewById(R.id.music_info_pre);
        mNext  = (ImageView) mViewGroup.findViewById(R.id.music_info_next);
        mPlay  = (ImageView) mViewGroup.findViewById(R.id.music_info_play);
        mSinger  = (TextView) mViewGroup.findViewById(R.id.music_info_singer);
        mSong  = (MarqueeTextView) mViewGroup.findViewById(R.id.music_info_song);
        mProgress  = (TextView) mViewGroup.findViewById(R.id.music_info_progress);
        mTimes  = (TextView) mViewGroup.findViewById(R.id.music_info_times);
        mCircleProgressView = (CircleProgressView) mViewGroup.findViewById(R.id.music_info_cirle);
        mListBtn = (Button) mViewGroup.findViewById(R.id.music_list_btn);

        mMode.setOnClickListener(this);
        mPre.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mSinger.setOnClickListener(this);
        mSong.setOnClickListener(this);
        mProgress.setOnClickListener(this);
        mTimes.setOnClickListener(this);
        mMode.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mListBtn.setOnClickListener(this);
        mCircleProgressView.setOnClickListener(this);


        switch (Util.getMode(mContext)) {
            case Constant.MODE_LIST:
                mMode.setImageResource(R.drawable.mode_list);
                break;
            case Constant.MODE_RANDOM:
                mMode.setImageResource(R.drawable.mode_random);
                break;
            case Constant.MODE_SINGLE:
                mMode.setImageResource(R.drawable.mode_single);
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.music_info_mode:
                Util.saveMode(MusicContext.getCurrentMode()+1,mContext);
                MusicContext.getMusicPlayerMgr().switchMode(MusicContext.getCurrentMode()+1);
                refreshMode();


                break;
            case R.id.music_info_pre:
                MusicContext.getMusicPlayerMgr().pre();
                break;
                case R.id.music_list_btn:
                    mBottomCirTraDialog = new BottomCirTraDialog(mContext, R.style.MyDialogTheme, new BottomCirTraDialog.SelectDialogListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MusicBean bean = musicBeans.get(position);
                            MusicContext.getMusicPlayerMgr().play(bean);
                        }
                    },musicBeans);
                    mBottomCirTraDialog.show();
                    break;
            case R.id.music_info_cirle:
                case R.id.music_info_play:
                if (MusicContext.isPlaying())
                {
                    MusicContext.getMusicPlayerMgr().pasuse();
                    mCircleProgressView.setPlay(false);
                }else{
                    MusicContext.getMusicPlayerMgr().play();
                }
                break;
            case R.id.music_info_next:
                MusicContext.getMusicPlayerMgr().next();
                break;
            default:
                break;
        }
    }

    public void onScanFinish(List<MusicBean> musicBeans) {
        this.musicBeans = musicBeans;

    }

    public void onPlayerStatus(int state, MusicBean bean) {
        if (bean!=null){
            mSinger.setText(bean.getSinger());
            mSong.setText(bean.getSong());
            mCircleProgressView.setPlay(state==Constant.MUSIC_PLAY?true:false);
            mPlay.setBackgroundResource(state==Constant.MUSIC_PLAY?R.drawable.pause:R.drawable.play);
        }
    }

    public void onProgress(int progress,int duration) {
        mTimes.setText(convertIntToString(progress));
        mProgress.setText(convertIntToString(duration));
        mCircleProgressView.setMaxProgress(duration);
        mCircleProgressView.setProgress(progress);
    }

    public String convertIntToString(int time){
        if (time == -1) {
            return "0:00";
        }
        String str = "";
        int min = (time/1000)/60;
        int second = (time/1000)%60;
        str = min+":"+(second>=10?second:"0"+second);
        return str;
    }

    public void refreshMode(){
        switch (MusicContext.getCurrentMode()) {
            case Constant.MODE_LIST:
                mMode.setImageResource(R.drawable.mode_list);
                break;
            case Constant.MODE_RANDOM:
                mMode.setImageResource(R.drawable.mode_random);
                break;
            case Constant.MODE_SINGLE:
                mMode.setImageResource(R.drawable.mode_single);
                break;
            default:
                break;
        }
    }
}
