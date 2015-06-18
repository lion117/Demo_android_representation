package com.example.ishow;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher;
import android.widget.ImageView;
import android.view.View;
import android.os.Handler;


public class Main_view extends Activity implements ViewSwitcher.ViewFactory {
    /**
     * Called when the activity is first created.
     */
    private ImageSwitcher _img_switcher;
    private float _touch_down_x, _touch_up_x;
    private int  _img_index=0;
    private int  _img_counts=10;
    private int  _img_switch_time = 4000;
    private boolean _is_loop = false;

    private int[] _image_array = {R.drawable.beauty_1,R.drawable.beauty_2, R.drawable.beauty_3,R.drawable.beauty_4,R.drawable.beauty_5,R.drawable.beauty_6,R.drawable.beauty_7,R.drawable.beauty_8,R.drawable.beauty_9,R.drawable.beauty_0 };

    Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            super.handleMessage(msg);
        }
    };

    Runnable _runable = new Runnable() {
        @Override
        public void run() {
            if (_is_loop){
                moveLeft();
            }
            Message i_message = new Message();
            _handler.postDelayed(this, _img_switch_time);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initMusicPlayer();
        /////////////////////////
       initImageSwitcher();
        _handler.postDelayed(_runable, _img_switch_time);
        _is_loop= true;

    }

    public void initMusicPlayer(){

        Intent intent = new Intent();
        intent.setClass(this, Backgound_music.class);
        //启动Service，然后绑定该Service，这样我们可以在同时销毁该Activity，看看歌曲是否还在播放
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void initImageSwitcher(){
        _img_switcher = (ImageSwitcher)findViewById(R.id.imageSwitcher);
        _img_switcher.setFactory((ViewSwitcher.ViewFactory) this);
        _img_switcher.setImageResource(_image_array[_img_index]);
        // set long click resopne
//        _img_switcher.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (_is_loop){
//                    _is_loop= false;
//
//                } else {
//                    _is_loop = true;
//                }
//                return  true;
//            }
//        });

        _img_switcher.setOnTouchListener(new View.OnTouchListener()
        {

            public  boolean onTouch(View v, MotionEvent event)
            {

                if (event.getAction() == MotionEvent.ACTION_DOWN)

                {
                    _touch_down_x = event.getX();
                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    _touch_up_x = event.getX();
                    float touch_distance = _touch_down_x - _touch_up_x;

                    if (touch_distance > 200  )//左滑
                    {
                        moveLeft();
                    }
                    else if(touch_distance < -200  )  // go right
                    {
                        moveRight();
                    }
                    else if (touch_distance< 30 && touch_distance > -30){
                        if (_is_loop) {
                            _is_loop = false;

                        } else {
                            _is_loop = true;
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public  void moveLeft(){

        _img_switcher.setInAnimation(getApplicationContext(), R.animator.ani_in);
        _img_switcher.setOutAnimation(getApplicationContext(), R.animator.ani_out);

        if (_img_index >0 )
        {
            _img_index--;
            _img_switcher.setImageResource(_image_array[_img_index]);
        }
        else {

            _img_index = _img_counts-1;
            _img_switcher.setImageResource(_image_array[_img_index]);

        }
    }

    public void moveRight(){
        _img_switcher.setInAnimation(getApplicationContext(), R.animator.ani_left_out);
        _img_switcher.setOutAnimation(getApplicationContext(), R.animator.ani_left_in);

        if (_img_index < _img_counts-2)
        {
            _img_index++;
            _img_switcher.setImageResource(_image_array[_img_index]);
        }
        else {
            _img_index = 0;
            _img_switcher.setImageResource(_image_array[_img_index]);
        }
    }

    public   ImageView iv = null;

    public View makeView() {
        // 将所有图片通过ImageView来显示
        iv = new ImageView(this);

        iv.setBackgroundColor(0xFF0000E0);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP  );
        iv.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return iv;
    }


    private Backgound_music audioService;

    //使用ServiceConnection来监听Service状态的变化
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            audioService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //这里我们实例化audioService,通过binder来实现
            audioService = ((Backgound_music.AudioBinder)binder).getService();

        }
    };


}
