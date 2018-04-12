package com.han.waterdropview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by han on 2018/4/6.
 * 不重叠的水滴
 * 如果要重叠的 需要再修改代码
 * 点击事件   setOnItemClickListener(OnItemClickListener onItemClickListener) <br/>
 * 设置数据处理类  setDataProcessing(DataProcessing mDataProcessing) <br/>
 * 设置图片 setImageResource(int id) <br/>
 * 设置文字大小 没有实现 默认是由高减去宽的大小 如果要指定文字大小在 DataProcessing 这个接口中修改  <br/>
 * 设置文字颜色 setTextColor(int color) <br/>
 * 设置数据 setData(List<T> data)<br/>
 * 添加数据<br/>
 * 删除数据  removeData(T data) <br/>
 * <p>
 * 删除数据 再添加数据 可能会数据混乱  后期再优化吧 先这样 我先上班了 2018年4月8日 16:53:32<br/>
 * 好像是Index 会乱其他的似乎不会乱  数据不会处理的时候 使用 onItemClickListener 中的数据进行判断<br/>
 * <p>
 * 使用通配符 <T><br/>
 * 类似这样的的使用方法 如果没有指定通配符类型 ,默认是 Object的类型<br/>
 * <p>
 * mview.setOnItemClickListener(new WaterDropView.OnItemClickListener<String>() {
 * <p>
 * public void onItemClickListener(WaterDropView aview, int item, String mdata) {
 * Log.d("han", " item :" + item + "   data: " + mdata);
 * mview.removeData(item);
 * <p>
 * }
 * });<br/>
 * 2018年4月11日 17:02:15
 * 更新动画方式 使用 Animator 动画代替 Animation<br/>
 */

public class WaterDropView<T> extends RelativeLayout {

    private int mImageResource = 0; //图片资源
    private int mTextColor = 0xFFFFFFFF; // 文字默认颜色

    private OnItemClickListener onItemClickListener; //点击事件接口
    private DataProcessing mDataProcessing; //数据处理接口
    private final Handler mHandler = new Handler(); //Handler
    private static final String TAG = "han";

    private List<IRectangle> mList = new ArrayList<>(); //存放View的列表

    private List<T> mDataList = new ArrayList<>();//存放数据的列表
    //默认 图片大小
    private int mWaterWidth = 80;
    private int mWaterHight = 110;
    //随机数
    private Random random;
    //移除动画.和插入动画
    protected LayoutTransition mLayoutTransition;

    public WaterDropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public WaterDropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterDropView(Context context) {
        this(context, null);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        // Pix.init(getContext());//工具初始化
        random = new Random();
        this.setLayoutTransition(getLayoutTransitions());
    }


    public interface OnItemClickListener<T> {
        /**
         * 第几个被点击
         *
         * @param aview 对象
         * @param item  点击的下标
         */
        void onItemClickListener(WaterDropView aview, int item, T mdata);
    }

    //处理数据的接口
    public interface DataProcessing<T> {
        /**
         * 返回图片ID
         *
         * @param imageView
         * @param object    数据源
         * @return
         */
        int getImageId(ImageView imageView, T object, int index);

        /**
         * 返回 Text 文本 数据<br><br/>
         * textView size 是   (height -  width) * 2 / 3
         * textView Color是 0xFFFFFFFF
         *
         * @param textView
         * @param object
         * @return
         */
        String getText(TextView textView, T object, int index);

        /**
         * 动画
         * 这2个 实现一个就好了
         *
         * @param object
         * @param index
         * @return
         */
        Animation getAnimations(T object, int index);


        /**
         * 动画
         * 优先取这个数据 如果这个有数据就不取下面那个了
         *
         * @param object
         * @param index
         * @return
         */
        Animator getAnimator(T object, int index);
    }

    //数据处理接口 基类
    public static class BaseDataProcessing<T> implements DataProcessing<T> {
        protected Random random = new Random();

        @Override
        public int getImageId(ImageView imageView, T object, int index) {
            return 0;
        }

        /**
         * index 会混乱请不要依赖index做操作
         *
         * @param textView
         * @param object   数据
         * @param index    当前第几条数据
         * @return
         */
        @Override
        public String getText(TextView textView, T object, int index) {
            return object.toString();
        }

        @Override
        public Animation getAnimations(T object, int index) {
            return getAnimations(Math.abs(random.nextInt() % 300));
        }

        @Override
        public Animator getAnimator(T object, int index) {
            return getAnimators(Math.abs(random.nextInt() % 300));
        }

        private Animator getAnimators(long time) {
            Interpolator interpolator;
            //interpolator  = new CycleInterpolator(1);//动画循环播放特定的次数，速率改变沿着正弦曲线
            //interpolator = new LinearInterpolator();//线性运动
            interpolator = new AccelerateDecelerateInterpolator();//开始和结束的时候比较慢


            ObjectAnimator animation = ObjectAnimator.ofFloat(null, "translationY", 8, -8);//new TranslateAnimation(0, 0, 8, -8);
            animation.setDuration(1500);//播放时长
            animation.setStartDelay(time);

            animation.setRepeatCount(ValueAnimator.INFINITE);//循环播放
            animation.setRepeatMode(ValueAnimator.REVERSE);//播放后再倒着播放一次
            //animation.setStartOffset(time);//啥时候开始播放
            animation.setInterpolator(interpolator);
            return animation;
        }


        private Animation getAnimations(long time) {
            Interpolator interpolator;
            //interpolator  = new CycleInterpolator(1);//动画循环播放特定的次数，速率改变沿着正弦曲线
            //interpolator = new LinearInterpolator();//线性运动
            interpolator = new AccelerateDecelerateInterpolator();//开始和结束的时候比较慢

            Animation animation = new TranslateAnimation(0, 0, 8, -8);
            animation.setDuration(1500);//播放时长
            animation.setRepeatCount(ValueAnimator.INFINITE);//循环播放
            animation.setRepeatMode(Animation.REVERSE);//播放后再倒着播放一次
            animation.setStartOffset(time);//啥时候开始播放
            animation.setInterpolator(interpolator);
            return animation;
        }
    }


    //添加移除 和新加动画
    {
        mLayoutTransition = new LayoutTransition();


        AnimatorSet animatorRing = new AnimatorSet();
        animatorRing.playTogether(ObjectAnimator.ofFloat(null, "Alpha", 0.0f, 1.0f));


        AnimatorSet animatorDis = new AnimatorSet();
        animatorDis.playTogether(ObjectAnimator.ofFloat(null, "Alpha", 1.0f, 0.0f));

        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, animatorRing);//View出现的时候
        mLayoutTransition.setDuration(LayoutTransition.APPEARING, 500);
        mLayoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);//源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！

        mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, animatorDis); //View 消失的时候
        mLayoutTransition.setDuration(LayoutTransition.DISAPPEARING, 500);

//        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING,   ); //当View出现的时候其他View的动画
//        mLayoutTransition.setDuration(200);

//        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, );//当View消失的时候其他View的动画
//        mLayoutTransition.setDuration(200);

        mLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        mLayoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);//源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！
    }


    //  画图片
    private void drawView() {
        if (mWaterWidth == 0) {
            mWaterWidth = 80;
            mWaterHight = 80 + 20;
        }
        Log.d(TAG, "drawView: " + getWidth() + "  H :" + getHeight());

        int width = getWidth(); //当前控件宽度
        int height = getHeight();//当前控件高度
        if (width == 0) {//保证当前获取到宽度
            Log.w(TAG, "drawView: getWidth() == 0 !");
            mHandler.removeCallbacks(runDrawView);
            mHandler.postDelayed(runDrawView, 100);
            return;
        }
        //下面代码需要重新 构思
        int listsize = mDataList.size();
        int P = width * height;//面积
        int P2 = (mWaterWidth * mWaterHight) * listsize;//绘制图片的面积   //4/10 是测试出来的范围 不重叠的范围
        boolean isShow = (width - mWaterWidth * 3 / 2) < mWaterWidth && (height - mWaterHight * 3 / 2) < mWaterHight;//宽度必须大于 图片宽度 高度也是一样的
        if (P2 > (P * 4 / 10) || isShow) {//小于全部面积的2/3 说明可以放下这么多图片 当前区域太小无法显示完全 ,所以不显示 ,否则容易出bug
            int i = (P * 4 / 10) / (mWaterWidth * mWaterHight);
            Log.w(TAG, "drawView:  The current area can't fit so many pictures ! number " + listsize + "   Estimated to store : " + i);
            Toast.makeText(getContext(), "The current area can't fit so many pictures!", Toast.LENGTH_SHORT).show();
            // listsize = (P * 2 / 3) / (mWaterWidth * mWaterHight);// 无法修改
            // Log.w(TAG, "drawView:  The current area can't fit so many pictures  ! Change number " + listsize);
            return;
        }
        int i = 0;
        do {
            if (mList.size() < listsize) {//如果当前列表少于数据就创建新的
                int x = Math.abs(random.nextInt()) % (width - mWaterWidth * 3 / 2);
                int y = Math.abs(random.nextInt()) % (height - mWaterHight * 3 / 2);

                Log.d(TAG, "drawView: X " + x + "  Y :" + y + " i :" + i);

                IRectangle rectangle = new IRectangle();
                rectangle.setBounds(x, y, mWaterWidth, mWaterHight);

                if (!intersectsAll(rectangle)) {
                    addWaterView(rectangle);
                } else {
                    rectangle = null;
                }
                i++;
                if (i >= 1000) {//数据不能超过 1000
                    Toast.makeText(getContext(), "Excessive number of random numbers prevents deadlock from jumping out of the loop!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                return;
            }
        } while (true);
    }


    //添加View
    private void addWaterView(IRectangle bounds) {
        if (mDataProcessing == null) {
            mDataProcessing = getDefaultDataProcessing();
            Log.w(TAG, "addWaterView: mDataProcessing is null ,You need to implement a DataProcessing !");
        }
        ImageTextView imageTextView = new ImageTextView(getContext());
        LayoutParams lp = new LayoutParams(bounds.width, bounds.height);
        lp.topMargin = bounds.y;
        lp.leftMargin = bounds.x;
        imageTextView.setLayoutParams(lp);
        int index = mList.size();
        T data = mDataList.get(mList.size());

        int id = mDataProcessing.getImageId(imageTextView.getImageView(), data, index);
        if (id == 0) {//如果回调中没有获取到 id 那么使用全局ID
            if (mImageResource != 0) {
                imageTextView.setImageResource(mImageResource);
            } else {
                imageTextView.setImageColor(0xFF4488FF);
            }
        } else {//获取到id就设置ID
            imageTextView.setImageResource(id);
        }
        imageTextView.setTextColor(mTextColor);
        imageTextView.setTextSize((bounds.height - bounds.width) * 2 / 3);
        imageTextView.setText(mDataProcessing.getText(imageTextView.getTextView(), data, index));


        Animator animator = mDataProcessing.getAnimator(data, index);
        if (animator != null) {
            animator.setTarget(imageTextView);
            imageTextView.setTag(animator);
            animator.start();
        } else {
            Animation loadAnim = mDataProcessing.getAnimations(data, index);// getAnimations((bounds.x * bounds.y) % 300);//设置开始时间
            if (loadAnim != null) {
                imageTextView.startAnimation(loadAnim);
            }
        }
        imageTextView.setOnClickListener(mClick);
        this.addView(imageTextView);//往控件中添加 View
        bounds.mView = imageTextView;//添加View
        bounds.mData = mDataList.get(mList.size());//添加数据
        mList.add(bounds);
    }


    //点击事件
    final OnClickListener mClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int index = getIndex(v);
                onItemClickListener.onItemClickListener(WaterDropView.this, index, mDataList.get(index));
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        drawView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeAllViews();
    }

    private final Runnable runDrawView = new Runnable() {
        @Override
        public void run() {
            drawView();
        }
    };

    //改变数据了
    public void setData(List<T> data) {
        mDataList.clear();
        mDataList.addAll(data);
        this.removeAllViews();
        this.drawView();
    }

    //移除id
    public void removeData(T data) {
        int index = getIndexObj(data);
        removeData(index);
    }

    //添加数据
    public void addData(T data) {
        mDataList.add(data);
        drawView();
    }

    //移除下标
    public void removeData(int index) {
        if (mList.size() == 0) {//还没有添加View数据的时候
            Log.w(TAG, "removeData: ViewList is size == 0 ! mDataList: " + mDataList.size());
            if (mDataList.size() != 0) {//添加了数据 _ 删除下标
                mDataList.remove(index);
            }
            return;
        }
        //保证数据源 和 VIEW是同步的 本身是可以做一个列表的
        IRectangle mIRe = mList.get(index);
        mDataList.remove(mIRe.mData);
        Object object = mIRe.mView.getTag();
        if (object instanceof Animator) {
            Animator animator = (Animator) object;
            animator.cancel();//停止
        }
        mIRe.mView.clearAnimation();
        this.removeView(mIRe.mView);
        mList.remove(mIRe);
        mIRe.clear();
    }

    //通过View获取下标
    private int getIndex(View v) {
        for (int i = 0; i < mList.size(); i++) {
            IRectangle mRec = mList.get(i);
            if (mRec.mView == v) {
                return i;
            }
        }
        return 0;
    }

    //通过View数据获取下标
    private int getIndexListObj(T data) {
        for (int i = 0; i < mList.size(); i++) {
            IRectangle mRec = mList.get(i);
            if (mRec.mData == data) {
                return i;
            }
        }
        return 0;
    }

    //通过数据获取下标
    private int getIndexObj(T data) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i) == data) {
                return i;
            }
        }
        return 0;
    }

    //比较全部大小
    private boolean intersectsAll(IRectangle bounds) {
        boolean intersect = false;
        for (IRectangle lis : mList) {
            if (bounds.intersects(lis)) {
                return true;
            }
        }
        return intersect;
    }


    public void setImageResource(int resid) {
        mImageResource = resid;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    @NonNull
    private DataProcessing getDefaultDataProcessing() {
        return new BaseDataProcessing();
    }

    //处理数据的接口
    public void setDataProcessing(DataProcessing mDataProcessing) {
        this.mDataProcessing = mDataProcessing;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //添加View移除动画
    protected LayoutTransition getLayoutTransitions() {
        return mLayoutTransition;
    }

}

/////////////////////////////////////////////////////////////////**********************************///////////////////////////////////////////////////

/**
 * 文字在下 图片在上 图片大小是控件大小
 */
class ImageTextView extends LinearLayout {
    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private ImageView mImageView;
    private TextView mTextView;

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);
        mImageView = new ImageView(getContext(), attrs, defStyleAttr);
        mTextView = new TextView(getContext(), attrs, defStyleAttr);
        mTextView.setSingleLine();
        mTextView.setGravity(Gravity.CENTER);
        mImageView.setLayoutParams(generateDefaultLayoutParams());
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        this.addView(mImageView);
        this.addView(mTextView);
    }

    public void setImageResource(int resid) {
        mImageView.setImageResource(resid);
    }

    public void setImageColor(int color) {
        mImageView.setImageDrawable(new ColorDrawable(color));
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setTextSize(int size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setTextSize(int unit, int size) {
        mTextView.setTextSize(unit, size);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mImageView.getWidth() == 0 && mImageView.getWidth() != getWidth()) {
            int width = getWidth();
            mImageView.setLayoutParams(new LayoutParams(width, width));
            mTextView.setLayoutParams(new LayoutParams(width, LayoutParams.WRAP_CONTENT));
        }
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context) {
        this(context, null);
    }
}
////////////////////////////////////////////////////////////////***********************************///////////////////////////////////////////////////

/**
 * Created by han on 2018/4/8.
 */
//数据存放的 Class 和数据比较的class
class IRectangle implements Cloneable {
    public int x;
    public int y;
    public int width;
    public int height;
    public View mView;//View
    public Object mData;//数据

    public void clear() {
        mView = null;
        mData = null;
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }


    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    void grow(int w, int h) {
        this.x -= w;
        this.y -= h;
        this.width += 2 * w;
        this.height += 2 * h;
    }

    public boolean intersects(IRectangle r) {
        int tw = this.width;
        int th = this.height;
        int rw = r.width;
        int rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.x;
        int ty = this.y;
        int rx = r.x;
        int ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    public int getCenterX() {
        return (this.x + this.width) / 2;
    }

    public int getCenterY() {
        return (this.y + this.height) / 2;
    }

    public IRectangle clone() {
        IRectangle iRectangle = new IRectangle();
        iRectangle.setBounds(this.x, this.y, this.width, this.height);
        return iRectangle;
    }
}
