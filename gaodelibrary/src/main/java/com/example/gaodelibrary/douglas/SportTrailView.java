package com.example.gaodelibrary.douglas;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.example.gaodelibrary.ScreenUtil;

import java.util.List;

/**
 * 自定义轨迹动画
 * Created by BLiYing on 2018/5/21.
 */

/**
 * 作者：莫比乌丝环丶
 * 链接：https://www.jianshu.com/p/4fd67921b743
 * 來源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */

public class SportTrailView extends View {

    private Paint mStartPaint;
    /**
     * 起点
     */
    private Point mStartPoint;
    /**
     * 起点bitmap
     */
    private Bitmap mStartBitmap;
    /**
     * 轨迹
     */
    private Paint mLinePaint;
    /**
     * 小亮球
     */
    private Paint mLightBallPaint;
    /**
     * 小两球的bitmap  UI切图
     */
    private Bitmap mLightBallBitmap;
    /**
     * 起点rect 如果为空时不绘制小亮球
     */
    private Rect mStartRect;
    /**
     * 屏幕宽度
     */
    private int mWidth;
    /**
     * 屏幕高度
     */
    private int mHeight;
    /**
     * 轨迹path
     */
    private Path mLinePath;
    /**
     * 保存每一次刷新界面轨迹的重点坐标
     */
    private float[] mCurrentPosition = new float[2];

    public final static int colortext = Color.rgb(124, 252, 0);//绿色
    //    public final static int colortext = Color.rgb(76, 184, 246);//主题蓝色
    public final static int strokewidth = 15;

    public SportTrailView(Context context) {
        this(context, null);
    }

    public SportTrailView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SportTrailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化画笔，path
     */
    private void initPaint() {
        mLinePaint = new Paint();
        mLinePaint.setColor(colortext);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(strokewidth);
//        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setAntiAlias(true);

        mLightBallPaint = new Paint();
        mLightBallPaint.setAntiAlias(true);
        mLightBallPaint.setFilterBitmap(true);

        mStartPaint = new Paint();
        mStartPaint.setAntiAlias(true);
        mStartPaint.setFilterBitmap(true);

        mLinePath = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制轨迹
        canvas.drawPath(mLinePath, mLinePaint);
        //绘制引导亮球
        if (mLightBallBitmap != null && mStartRect != null) {
            int width = mLightBallBitmap.getWidth();
            int height = mLightBallBitmap.getHeight();
            RectF rect = new RectF();
            rect.left = mCurrentPosition[0] - width;
            rect.right = mCurrentPosition[0] + width;
            rect.top = mCurrentPosition[1] - height;
            rect.bottom = mCurrentPosition[1] + height;
            canvas.drawBitmap(mLightBallBitmap, null, rect, mLightBallPaint);
        }
        //绘制起点
        if (mStartBitmap != null && mStartPoint != null) {
            if (mStartRect == null) {
                int width = mStartBitmap.getWidth()/2;
                int height = mStartBitmap.getHeight();
                mStartRect = new Rect();
                mStartRect.left = mStartPoint.x - width;
                mStartRect.right = mStartPoint.x + width;
                mStartRect.top = mStartPoint.y - height;
                mStartRect.bottom = mStartPoint.y;


            }
            canvas.drawBitmap(mStartBitmap, null, mStartRect, mStartPaint);
        }
    }

    /**
     * 绘制运动轨迹
     *
     * @param mPositions      道格拉斯算法抽稀过后对应的点坐标
     * @param startPointResId 起点图片的资源id
     * @param lightBall       小亮球的资源id
     * @param listener        轨迹绘制完成的监听
     */
    public void drawSportLine(final List<Point> mPositions, @DrawableRes int startPointResId, @DrawableRes int lightBall, final OnTrailChangeListener listener) {
        if (mPositions.size() <= 1) {
            listener.onFinish();
            return;
        }
        //用于
        Path path = new Path();
        for (int i = 0; i < mPositions.size(); i++) {
            if (i == 0) {
                path.moveTo(mPositions.get(i).x, mPositions.get(i).y);
            } else {
                path.lineTo(mPositions.get(i).x, mPositions.get(i).y);
            }
        }

        final PathMeasure pathMeasure = new PathMeasure(path, false);
        //轨迹的长度
        final float length = pathMeasure.getLength();
        if (length < ScreenUtil.dp2px(getContext(), 16)) {
            listener.onFinish();
            return;
        }
        //动态图中展示的亮色小球（UI切图）
        mLightBallBitmap = BitmapFactory.decodeResource(getResources(), lightBall);
        //起点
        mStartPoint = new Point(mPositions.get(0).x, mPositions.get(0).y);
//        mStartBitmap = BitmapFactory.decodeResource(getResources(), startPointResId);
        mStartBitmap = BitmapDescriptorFactory.fromResource(startPointResId).getBitmap();
        ValueAnimator animator = ValueAnimator.ofFloat(0, length);
        animator.setDuration(3000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                pathMeasure.getPosTan(value, mCurrentPosition, null);
                if (value == 0) {
                    //如果当前的运动轨迹长度为0，设置path的起点
                    mLinePath.moveTo(mPositions.get(0).x, mPositions.get(0).y);
                }
                //pathMeasure.getSegment（）方法用于保存当前path路径，
                //下次绘制时从上一次终点位置处绘制，不会从开始的位置开始绘制。
                pathMeasure.getSegment(0, value, mLinePath, true);
                invalidate();
                //如果当前的长度等于pathMeasure测量的长度，则表示绘制完毕，
                if (value == length && listener != null) {
                    listener.onFinish();
                }
            }
        });
        animator.start();
    }

    /**
     * 清空自定义view
     */
    public void clearEverything() {
        mLinePath = new Path();
        if (mLightBallBitmap != null) {
            mLightBallBitmap.recycle();
            mLightBallBitmap = null;

        }
        if (mStartBitmap != null) {
            mStartBitmap.recycle();
            mStartBitmap = null;

        }
        invalidate();

    }

    /**
     * 轨迹绘制完成监听
     */
    public interface OnTrailChangeListener {
        void onFinish();
    }


}
