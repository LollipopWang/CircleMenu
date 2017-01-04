package com.liangshan.xf.circlemenu.activity_my;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.liangshan.xf.circlemenu.R;

public class CircleActivity extends AppCompatActivity {

    private int indexImage=0,//点击“图片展示“按钮的次数
                indexFlash=0;//点击“动画展示“按钮的次数

    private Button bt1,bt2,bt3;

    private MyCircleMenuLayout id_menulayout3D,id_menulayoutImage,id_menulayoutFlash;

    private String[] mItemTexts3D = new String[] { "仓栅式半挂车", "低平板半挂车", "集装箱半挂车","栏板半挂车", "平板式半挂车", "气罐车", "厢式半挂车", "自卸式半挂车"};
    private String[] mItemTextsImage = new String[] { "Image安全中 ", "Image特色服", "Image投资理","Image转账汇", "Image我的账", "Image信用" , "Image特色服", "Image投资理"};
    private String[] mItemTextsFlash = new String[] { "Flash安全中 ", "Flash特色服", "Flash投资理","Flash转账汇", "Flash我的账", "Flash信用", "Flash特色服", "Flash投资理" };

    private int[] mItemImgs = new int[] { R.mipmap.png11,
                                          R.mipmap.png22,
                                          R.mipmap.png33,
                                          R.mipmap.png44,
                                          R.mipmap.png55,
                                          R.mipmap.png66,
                                          R.mipmap.png77,
                                          R.mipmap.png88};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //自已切换布局文件看效果
        setContentView(R.layout.activity_main02);

        bt1= (Button) findViewById(R.id.bt1);
        bt2= (Button) findViewById(R.id.bt2);
        bt3= (Button) findViewById(R.id.bt3);

        //初始化组件
        id_menulayout3D = (MyCircleMenuLayout) findViewById(R.id.id_menulayout3D);
        id_menulayoutImage = (MyCircleMenuLayout) findViewById(R.id.id_menulayoutImage);
        id_menulayoutFlash = (MyCircleMenuLayout) findViewById(R.id.id_menulayoutFlash);
        //setMenuItemIconsAndTexts去设置文本和图片就行~~~
        id_menulayout3D.setMenuItemIconsAndTexts(mItemImgs, mItemTexts3D);
        thread3D();

        //设置按钮的点击事件
        onClickListener();

        //给圆形菜单设置监听事件---------已经在自定义组件CircleMenuLayout中定义了监听事件的接口
        id_menulayout3D.setOnMenuItemClickListener(new MyCircleMenuLayout.OnMenuItemClickListener()
        {

            @Override
            public void itemClick(View view, int pos)
            {                                 //点击圆环上的小View,吐司该View中的文本内容，int pos即该View的Position
                Toast.makeText(CircleActivity.this, mItemTexts3D[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemCenterClick(View view)
            {                                //点击中间圆形图标，吐司"you can do something just like ccb "
                Toast.makeText(CircleActivity.this, "you can do something just like ccb ", Toast.LENGTH_SHORT).show();
            }
        });

        id_menulayoutImage.setOnMenuItemClickListener(new MyCircleMenuLayout.OnMenuItemClickListener()
        {

            @Override
            public void itemClick(View view, int pos)
            {                                 //点击圆环上的小View,吐司该View中的文本内容，int pos即该View的Position
                Toast.makeText(CircleActivity.this, mItemTextsImage[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemCenterClick(View view)
            {                                //点击中间圆形图标，吐司"you can do something just like ccb "
                Toast.makeText(CircleActivity.this, "you can do something just like ccb ", Toast.LENGTH_SHORT).show();
            }
        });

        id_menulayoutFlash.setOnMenuItemClickListener(new MyCircleMenuLayout.OnMenuItemClickListener()
        {

            @Override
            public void itemClick(View view, int pos)
            {                                 //点击圆环上的小View,吐司该View中的文本内容，int pos即该View的Position
                Toast.makeText(CircleActivity.this, mItemTextsFlash[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemCenterClick(View view)
            {                                //点击中间圆形图标，吐司"you can do something just like ccb "
                Toast.makeText(CircleActivity.this, "you can do something just like ccb ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void onClickListener() {
       //点击“3D展示”按钮
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_menulayout3D.setVisibility(View.VISIBLE);
                id_menulayoutImage.setVisibility(View.GONE);
                id_menulayoutFlash.setVisibility(View.GONE);
                thread3D();
            }
        });
        //点击“图片展示”按钮
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indexImage==0){
                    id_menulayout3D.setVisibility(View.GONE);
                    id_menulayoutImage.setVisibility(View.VISIBLE);
                    id_menulayoutFlash.setVisibility(View.GONE);
                    id_menulayoutImage.setMenuItemIconsAndTexts(mItemImgs, mItemTextsImage);
                }else {
                    id_menulayout3D.setVisibility(View.GONE);
                    id_menulayoutImage.setVisibility(View.VISIBLE);
                    id_menulayoutFlash.setVisibility(View.GONE);
                }
                threadImage();
                indexImage++;
            }
        });
        //点击“动画展示”按钮
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indexFlash==0){
                    id_menulayout3D.setVisibility(View.GONE);
                    id_menulayoutImage.setVisibility(View.GONE);
                    id_menulayoutFlash.setVisibility(View.VISIBLE);
                    id_menulayoutFlash.setMenuItemIconsAndTexts(mItemImgs, mItemTextsFlash);
                }else{
                    id_menulayout3D.setVisibility(View.GONE);
                    id_menulayoutImage.setVisibility(View.GONE);
                    id_menulayoutFlash.setVisibility(View.VISIBLE);
                }
                threadFlash();
                indexFlash++;
            }
        });

    }

    private void thread3D() {
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(200);
                    id_menulayout3D.circle(360);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void threadImage() {
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(200);
                    id_menulayoutImage.circle(360);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void threadFlash() {
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(200);
                    id_menulayoutFlash.circle(360);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
