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
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
    //显示和隐藏menu时手指滑动的速度
   private static final int SNAP_VELOCITY=200;
    private int screenWidth;
    private int leftEdge;
    private int rightEdge=0;
    private int menuPadding;
    private View menu;
    private View content;
    private LinearLayout.LayoutParams menuParams;
    private float xDown;
    private float xMove;
    private float xUp;
    private boolean menuVisivble;
    //计算手指滑动速度
    private VelocityTracker mvelocityTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        content.setOnTouchListener(this);
    }

    private void initValues() {
        content=findViewById(R.id.ll_content);
        menu=findViewById(R.id.ll_menu);
        WindowManager manager= (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics=new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        screenWidth=metrics.widthPixels;
        menuParams= (LinearLayout.LayoutParams) menu.getLayoutParams();
        menuParams.width=screenWidth-menuPadding;
        leftEdge=menuParams.width;
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
                xMove=event.getRawX();
                int distanceX= (int) (xMove-xDown);
                if(menuVisivble){
                    menuParams.leftMargin=distanceX;
                }else{
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
                if(wantshowmenu()){
                   if(shouldScrolltomenu()){
                       scrolltomenu();
                   }
                }
                break;
        }
        return false;
    }

    private void scrolltomenu() {

    }

    private boolean shouldScrolltomenu() {
        return xUp-xDown>screenWidth/2||getScrollVelocity()>SNAP_VELOCITY;
    }

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
            return null;
        }
    }
}
