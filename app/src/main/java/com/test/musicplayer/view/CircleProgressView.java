package com.test.musicplayer.view;



import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.test.musicplayer.R;

public class CircleProgressView extends View{
	  private static final String TAG = "CircleProgressBar";

	    private int mMaxProgress = 100;

	    private int mProgress = 0;

	    private final int mCircleLineStrokeWidth = 8;

	    private boolean isPlay = false;
	    
	    // 画圆所在的距形区域
	    private final RectF mRectF;

	    private final Paint mPaint;

	    private final Context mContext;

	    private Bitmap mInnerRing;
	    
	    private Bitmap mPlayIcon;
	    
	    private Bitmap mPauseIcon;
	    
	    private float mDegree = 0;
	    
	    public CircleProgressView(Context context, AttributeSet attrs) {
	        super(context, attrs);

	        mContext = context;
	        mRectF = new RectF();
	        mPaint = new Paint();
	        
	        mInnerRing = BitmapFactory.decodeResource(getResources(), R.drawable.inner_ring);
	        mPlayIcon = BitmapFactory.decodeResource(getResources(), R.drawable.play);
	        mPauseIcon = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
	        
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);
	        int width = this.getWidth();
	        int height = this.getHeight();

	        if (width != height) {
	            int min = Math.min(width, height);
	            width = min;
	            height = min;
	        }

	        // 设置画笔相关属性
	        mPaint.setAntiAlias(true);
	        mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
	        canvas.drawColor(Color.TRANSPARENT);
	        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
	        mPaint.setStyle(Style.STROKE);
	        // 位置
	        mRectF.left = (mCircleLineStrokeWidth / 2)+10; // 左上角x
	        mRectF.top = (mCircleLineStrokeWidth / 2)+10; // 左上角y
	        mRectF.right = (width - mCircleLineStrokeWidth / 2)-10; // 左下角x
	        mRectF.bottom = (height - mCircleLineStrokeWidth / 2)-10; // 右下角y

	        // 绘制圆圈，进度条背景
	        mPaint.setColor(Color.parseColor("#999999"));
	        canvas.drawArc(mRectF, -90, 360, false, mPaint);
	        mPaint.setColor(Color.parseColor("#ff7f00"));
	        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);
	        
	        mInnerRing = big(mInnerRing, 300, 300);
	        int innerWidth = mInnerRing.getWidth();
	        int innerHeight = mInnerRing.getHeight();
	        
	        int centerX = getWidth()/2;
	        int centerY = getHeight()/2;
	        canvas.save();
	        canvas.translate(centerX,centerY);
	        mDegree = mDegree+4;
	        if (mDegree>=360) 
	        {
				mDegree = 0;
			}
	        canvas.rotate(mDegree);
	        canvas.translate(-centerX, -centerY);
	        canvas.drawBitmap(mInnerRing, getWidth()/2-innerWidth/2,getHeight()/2-innerHeight/2, mPaint);
	        canvas.restore();
	       
	        
	        // 位置
	        mRectF.left = (mCircleLineStrokeWidth / 2)+80; // 左上角x
	        mRectF.top = (mCircleLineStrokeWidth / 2)+80; // 左上角y
	        mRectF.right = (width - mCircleLineStrokeWidth / 2)-80; // 左下角x
	        mRectF.bottom = (height - mCircleLineStrokeWidth / 2)-80; // 右下角y

//	        // 绘制圆圈，进度条背景
	        mPaint.setStrokeWidth(15);
	        mPaint.setColor(Color.parseColor("#8c380d"));
	        canvas.drawArc(mRectF, -90, 360, false, mPaint);
	        
	        if (isPlay) {
	        	mPauseIcon = big(mPauseIcon, 110, 110);
			    int stateWidth = mPauseIcon.getWidth();
			    int stateHeight = mPauseIcon.getHeight();
//			    canvas.drawBitmap(mPauseIcon, getWidth()/2-stateWidth/2,getHeight()/2-stateHeight/2, mPaint);
			    
//			    postInvalidateDelayed(50);
			}else{
				mPlayIcon = big(mPlayIcon, 110, 110);
			    int stateWidth = mPlayIcon.getWidth();
			    int stateHeight = mPlayIcon.getHeight();
//			    canvas.drawBitmap(mPlayIcon, getWidth()/2-stateWidth/2,getHeight()/2-stateHeight/2, mPaint);
			}
	    }

	     public static Bitmap big(Bitmap b,float x,float y)
	     {
	    	 int w=b.getWidth();
	      	int h=b.getHeight();
	      	float sx=(float)x/w;//要强制转换，不转换我的在这总是死掉。
	      	float sy=(float)y/h;
	      	Matrix matrix = new Matrix();
	      	matrix.postScale(sx, sy); // 长和宽放大缩小的比例
	      	Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
	        h, matrix, true);
	      	return resizeBmp;
	     }
	    
	    
	    public int getMaxProgress() {
	        return mMaxProgress;
	    }

	    public void setMaxProgress(int maxProgress) {
	        this.mMaxProgress = maxProgress;
	    }

	    public void setProgress(int progress) {
	        this.mProgress = progress;
	        this.invalidate();
	    }

	    public void setProgressNotInUiThread(int progress) {
	        this.mProgress = progress;
	        this.postInvalidate();
	    }

	    public void setPlay(boolean isPlay){
	    	this.isPlay = isPlay;
	    	this.invalidate();
	    }

	    public boolean getPlay(){
	    	return isPlay;
	    }
}