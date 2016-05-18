package com.slidemenu.sampleslidemenu;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
    //显示和隐藏menu时手指滑动的速度
   private static final int SNAP_VELOCITY=200;
    //屏幕宽度
    private int screenWidth;
    //初始化时menu的leftmargin的值
    private int leftEdge;
    //menu完全显示时leftmargin的值
    private int rightEdge=0;
    //menu完全显示时距离右侧屏幕边缘的距离
    private int menuPadding=180;
    private View menu;
    private View content;
    private LinearLayout.LayoutParams menuParams;
    private float xDown;
    private float xMove;
    private float xUp;
    //menu是否是显示的状态
    private boolean menuVisivble;
    //计算手指滑动速度
    private VelocityTracker mvelocityTracker;
    private Button btMenu,btContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        content.setOnTouchListener(this);
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"点击menu",Toast.LENGTH_SHORT).show();
            }
        });
        btContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!menuVisivble) {
                    scrolltomenu();
                }else{
                    scrolltocontent();
                }
                Toast.makeText(MainActivity.this,"点击Content",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initValues() {
        btContent= (Button) findViewById(R.id.bt_content);
        btMenu= (Button) findViewById(R.id.bt_menu);
        content=findViewById(R.id.ll_content);
        menu=findViewById(R.id.ll_menu);
        WindowManager manager= (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics=new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        screenWidth=metrics.widthPixels;
        menuPadding=screenWidth/3;
        menuParams= (LinearLayout.LayoutParams) menu.getLayoutParams();
        //menu的宽度
        menuParams.width=screenWidth-menuPadding;
        leftEdge=-menuParams.width;
        //初始化时menu的leftmargin
        menuParams.leftMargin=leftEdge;
        content.getLayoutParams().width=screenWidth;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVerpctyTracker(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下时x轴坐标
                xDown=event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动时x轴坐标
                xMove=event.getRawX();
                int distanceX= (int) (xMove-xDown);
                if(menuVisivble){
                    //menu显示时
                    menuParams.leftMargin=distanceX;
                }else{
                    //menu不显示时
                    menuParams.leftMargin=leftEdge+distanceX;
                }
                if(menuParams.leftMargin<leftEdge){
                    menuParams.leftMargin=leftEdge;
                }else if(menuParams.leftMargin>rightEdge){
                    menuParams.leftMargin=rightEdge;
                }
                menu.setLayoutParams(menuParams);
                break;
            case MotionEvent.ACTION_UP:
                xUp=event.getRawX();
                //判断要显示menu
                if(wantshowmenu()){
                    //根据手势滑动判断是否允许显示menu
                   if(shouldScrolltomenu()){
                       scrolltomenu();
                   }else{
                       scrolltocontent();
                   }
                    //判断要显示content
                }else if(wanttoshowcontent()){
                    //根据手势滑动判断是否允许显示content
                   if(shouldScrolltocontent()){
                       scrolltocontent();
                   }else{
                       scrolltomenu();
                   }
                }
                recycleVelocityTracker();
                break;
        }
        return true;
    }
   //回收VelocityTracker
    private void recycleVelocityTracker() {
        mvelocityTracker.recycle();
        mvelocityTracker=null;
    }

    private boolean shouldScrolltocontent() {
        return xDown-xUp+menuPadding>screenWidth/2||getScrollVelocity()>SNAP_VELOCITY;
    }

    private boolean wanttoshowcontent() {
        return xUp-xDown<0&&menuVisivble;
    }

    private void scrolltocontent() {
        new ScrollTask().execute(-30);
    }

    private void scrolltomenu() {
          new ScrollTask().execute(30);
    }

    private boolean shouldScrolltomenu() {
        return xUp-xDown>screenWidth/2||getScrollVelocity()>SNAP_VELOCITY;
    }

    /**
     * 获取在content的x轴上滑动的速度
     * @return 每秒钟滑动距离的像素值
     */
    private int getScrollVelocity() {
        mvelocityTracker.computeCurrentVelocity(1000);
        int velocity= (int) mvelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    private boolean wantshowmenu() {
        return xUp-xDown>0&&!menuVisivble;
    }

    private void createVerpctyTracker(MotionEvent event) {
        if(mvelocityTracker==null){
            mvelocityTracker=VelocityTracker.obtain();
        }
        mvelocityTracker.addMovement(event);
    }

    class ScrollTask extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... params) {
            int leftMargin=menuParams.leftMargin;
            while (true) {
                //根据传入速度循环改变leftmargin
                leftMargin=leftMargin+params[0];
                if (leftMargin > rightEdge) {
                    leftMargin = rightEdge;
                    break;
                }
                if(leftMargin<leftEdge){
                    leftMargin=leftEdge;
                    break;
                }
                publishProgress(leftMargin);
                sleep(20);
            }
            if(params[0]>0){
                menuVisivble=true;
            }else{
                menuVisivble=false;
            }
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            menuParams.leftMargin=values[0];
            menu.setLayoutParams(menuParams);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            menuParams.leftMargin=integer;
            menu.setLayoutParams(menuParams);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
