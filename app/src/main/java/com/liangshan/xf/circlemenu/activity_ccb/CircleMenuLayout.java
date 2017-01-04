package com.liangshan.xf.circlemenu.activity_ccb;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangshan.xf.circlemenu.R;

/**
 * Created by Administrator on 2016/9/29 0029.
 */

public class CircleMenuLayout extends ViewGroup
{
    private int mRadius;
    /**
     * 该容器内child item的默认尺寸---圆环上的子控件
     */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    /**
     * 菜单的中心child的默认尺寸---中心的子控件
     */
    private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private static final float RADIO_PADDING_LAYOUT = 1 / 12f;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private float mPadding;
    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 300;
    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;
    /**
     * 如果移动角度达到该值，则屏蔽点击----当View快速旋转的时候不能点击
     */
    private static final int NOCLICK_VALUE = 3;
    /**
     * 菜单项的文本
     */
    private String[] mItemTexts;
    /**setMenuItemIconsAndTexts
     * 菜单项的图标
     */
    private int[] mItemImgs;
    /**
     * 菜单的个数
     */
    private int mMenuItemCount;
    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 0;
    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;
    /**
     * 判断是否正在自动滚动--------手指离开屏幕后，View是否在自己转动
     */
    private boolean isFling;
    //每个View的布局文件-------- 一张图片、一个标题
    private int mMenuItemLayoutId = R.layout.circle_menu_item;

    //有参构造方法
    public CircleMenuLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
    }

    /**
     * 设置布局的宽高，并策量menu item宽高----圆形菜单的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int resWidth = 0;
        int resHeight = 0;

        /**
         * 根据传入的参数，分别获取测量模式和测量值------获取圆形菜单的宽width和高height
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /**
         * 如果宽或者高的测量模式是不精确的值-----即圆形菜单的布局文件里面没有明确指定该控件的宽高
         */
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY)
        {
//*******************因为本菜单设置了背景图，所以resWidth和resHeight是背景图的宽和高*********************

            // 主要设置为背景图的宽度
            resWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽的默认值;如果设置了背景图片，则设置为背景图的宽度
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;
            // 主要设置为背景图的高度
            resHeight = getSuggestedMinimumHeight();
            // 如果未设置背景图片，则设置为屏幕高的默认值;如果设置了背景图片，则设置为背景图的高度
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else
        {
            // 如果宽或者高的测量模式是都设置为精确值，则直接取小值；-----  宽度=高度
            resWidth = resHeight = Math.min(width, height);
        }
        //将圆形菜单的宽和高传入方法setMeasuredDimension
        setMeasuredDimension(resWidth, resHeight);

        // 获得半径-----getMeasuredWidth()：圆形菜单的实际宽度，getMeasuredHeight()：圆形菜单的实际高度---取两者的最大值
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        // menu item数量---容器中子控件的个数
        final int count = getChildCount();
        // menu item尺寸----半径*容器内child item的默认尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // menu item测量模式---是精确值
        int childMode = MeasureSpec.EXACTLY;//EXACTLY(完全)，父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
                                                                                    //即给定的边界有多大，子控件就有多大
        // 迭代测量
        for (int i = 0; i < count; i++)
        {
            final View child = getChildAt(i);//获取一个布局中的View

            if (child.getVisibility() == GONE)//如果你所获取的View是隐藏的，那么去获取下一个View
            {
                continue;//continue语句是结束本次for循环而去执行下一次循环
            }

            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;

            if (child.getId() == R.id.id_circle_menu_item_center)//如果获取的View是中心位置的View
            {                                                    //半径*中心child的默认尺寸                        //中心child的测量模式
                makeMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION), childMode);
            } else  //如果获取的View是圆环上的子View
            {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        //mPadding是容器的内边距：无视padding属性
        mPadding = RADIO_PADDING_LAYOUT * mRadius;

    }

    /**
     * MenuItem的点击事件接口-----定义圆形菜单的监听事件接口
     *
     * @author zhy
     *
     */
    public interface OnMenuItemClickListener
    {
        void itemClick(View view, int pos);

        void itemCenterClick(View view);
    }

    /**
     * MenuItem的点击事件接口-----已经定义了监听事件的接口，现在是声明这个监听事件接口的对象
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;

    /**
     * 设置MenuItem的点击事件接口---  一个供外部类使用的一个入口，相当于构造函数，但是传入的是外部已经实例化的对象
     *
     * @param mOnMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener)
    {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    /**
     * 设置menu item的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int layoutRadius = mRadius;

        // Laying out the child views：定制子控件的个数
        final int childCount = getChildCount();

        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);

        // 根据menu item的个数，计算角度---每个圆环上的子控件所占的角度
        float angleDelay = 360 / (getChildCount() - 1);

        // 遍历去设置menuitem的位置
        for (int i = 0; i < childCount; i++)
        {
            final View child = getChildAt(i);

            if (child.getId() == R.id.id_circle_menu_item_center)
            {
                continue;
            }

            if (child.getVisibility() == GONE)
            {
                continue;
            }
            //mStartAngle是布局时的开始角度：除360，余数是mStartAngle
            mStartAngle %= 360;

            // 计算，中心点到menu item中心的距离：屏幕中心-圆形菜单中心-边距
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;

//*************************************************************************************************************************
            // tmp cosa 即menu item中心点的横坐标                                                                          //*
            left = layoutRadius / 2 + (int) Math.round(tmp * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);  //*
            // tmp sina 即menu item的纵坐标                                                                               //*
            top = layoutRadius / 2 + (int) Math.round(tmp * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);   //*
//*************************************************************************************************************************

            child.layout(left, top, left + cWidth, top + cWidth);
            // 叠加尺寸
            mStartAngle += angleDelay;
        }

        // 找到中心的view，
        View cView = findViewById(R.id.id_circle_menu_item_center);
        if (cView != null)//如果中心子控件存在，那就设置onclick事件
        {
            cView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if (mOnMenuItemClickListener != null)
                    {
                        mOnMenuItemClickListener.itemCenterClick(v);
                    }
                }
            });
            // 设置center item位置
            int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = cl + cView.getMeasuredWidth();
            cView.layout(cl, cl, cr, cr);
        }

    }

    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;

    /**
     * 自动滚动的Runnable----------在本类中自定义了内部类：AutoFlingRunnable
     */
    private AutoFlingRunnable mFlingRunnable;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)//触碰事件
    {
        float x = event.getX();//获得事件发生时,触摸的中间区域在屏幕的X轴的位置
        float y = event.getY();//获得事件发生时,触摸的中间区域在屏幕的X轴的位置

        // Log.e("TAG", "x = " + x + " , y = " + y);

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN://表示用户开始触摸.

                mLastX = x;//上一次的x坐标mLastX为用户开始触摸时，触摸的中间区域在屏幕的X轴的位置
                mLastY = y;//上一次的y坐标mLastY为用户开始触摸时，触摸的中间区域在屏幕的y轴的位置
                mDownTime = System.currentTimeMillis();//手指按下到抬起时使用的时间
                mTmpAngle = 0;//手指按下到抬起时，圆形菜单旋转的角度

                // 如果当前已经在快速滚动，那么意味着除快速滚动的回调方法正在被调用
                if (isFling)
                {
                    // 移除快速滚动的回调
                    removeCallbacks(mFlingRunnable);
                    isFling = false;//取消快速滚动
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE://表示用户在移动(手指或者其他)

                /**
                 * 获得开始的角度---getAngle()是在本类中自定义的获取角度的方法
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前的角度---getAngle()是在本类中自定义的获取角度的方法
                 */
                float end = getAngle(x, y);

                // Log.e("TAG", "start = " + start + " , end =" + end);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                //----getQuadrant是在本类中自定义的获取象限的方法
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4)
                {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                } else
                // 二、三象限，角度值是负值
                {
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                }
                // 重新布局
                requestLayout();

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP://表示用户抬起了手指

                // 计算，每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000 / (System.currentTimeMillis() - mDownTime);

                // Log.e("TAG", anglePrMillionSecond + " , mTmpAngel = " +
                // mTmpAngle);

                // 如果达到该值认为是快速移动
                if (Math.abs(anglePerSecond) > mFlingableValue && !isFling)
                {
                    // post一个任务，去自动滚动
                    post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));

                    return true;
                }

                // 如果当前旋转角度超过NOCLICK_VALUE，那么就屏蔽点击
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE)
                {
                    return true;
                }

                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 主要为了action_down（用户开始触摸）时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch)
    {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y)
    {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0)
        {
            return tmpY >= 0 ? 4 : 1;
        } else
        {
            return tmpY >= 0 ? 3 : 2;
        }

    }

    /**
     * 设置菜单条目的图标和文本
     *
     * 拿到两个数据（图片张数和文字个数），算出菜单的个数；然后去遍历，
     * 根据我们预设的R.layout.circle_menu_item，把值设上就可以
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts)
    {
        mItemImgs = resIds;
        mItemTexts = texts;

        // 参数检查
        if (resIds == null && texts == null)
        {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }

        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? texts.length : resIds.length;

        if (resIds != null && texts != null)
        {
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }

        addMenuItems();

    }

    /**
     * 设置MenuItem的布局文件，必须在setMenuItemIconsAndTexts之前调用
     *
     * @param mMenuItemLayoutId
     */
    public void setMenuItemLayoutId(int mMenuItemLayoutId)
    {
        this.mMenuItemLayoutId = mMenuItemLayoutId;
    }

    /**
     * 添加菜单项
     */
    private void addMenuItems()
    {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        /**
         * 根据用户设置的参数，初始化view
         */
        for (int i = 0; i < mMenuItemCount; i++)
        {
            final int j = i;
            View view = mInflater.inflate(mMenuItemLayoutId, this, false);
            ImageView iv = (ImageView) view.findViewById(R.id.id_circle_menu_item_image);
            TextView tv = (TextView) view.findViewById(R.id.id_circle_menu_item_text);

            if (iv != null)//如果有图片资源
            {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(mItemImgs[i]);//设置图片
                iv.setOnClickListener(new OnClickListener()//给图片设置监听事件
                {
                    @Override
                    public void onClick(View v)
                    {

                        if (mOnMenuItemClickListener != null)//监听事件有接口
                        {
                            mOnMenuItemClickListener.itemClick(v, j);/*
                                                                      *v--View的位置
                                                                      * j--组建的position
                                                                      */
                        }
                    }
                });
            }
            if (tv != null)//如果文字有资源
            {
                tv.setVisibility(View.VISIBLE);
                tv.setText(mItemTexts[i]);//设置文字
            }

            // 添加view到容器中---将已经布局完成的布局文件添加到容器中
            addView(view);//系统自带的方法
        }
    }

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param mFlingableValue
     */
    public void setFlingableValue(int mFlingableValue)
    {
        this.mFlingableValue = mFlingableValue;
    }

    /**
     * 设置内边距的比例
     *
     * @param mPadding
     */
    public void setPadding(float mPadding)
    {
        this.mPadding = mPadding;
    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    private int getDefaultWidth()
    {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /**
     * 自动滚动的任务
     *
     * @author zhy
     *
     */
    private class AutoFlingRunnable implements Runnable
    {

        private float angelPerSecond;
                               //velocity:速度
        public AutoFlingRunnable(float velocity)
        {
            this.angelPerSecond = velocity;
        }

        public void run()
        {
            // 如果小于20,则停止
            if ((int) Math.abs(angelPerSecond) < 20)
            {
                isFling = false;
                return;
            }
            isFling = true;
            // 不断改变开始时的角度mStartAngle，让其滚动，/30--除30---为了避免滚动太快
            mStartAngle += (angelPerSecond / 30);
            // 逐渐减小这个值---------速度逐渐减小---滚动越来越慢
            angelPerSecond /= 1.0666F;
            postDelayed(this, 30);
            // 重新布局
            requestLayout();//系统自带的方法
        }
    }

}

