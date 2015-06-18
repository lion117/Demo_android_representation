package com.example.ishow;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by LEO on 2015/6/8.
 */
public class Backgound_music extends Service implements MediaPlayer.OnCompletionListener{

    MediaPlayer player;

    private final IBinder binder = new AudioBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void onCreate(){
        super.onCreate();
        //我们从raw文件夹中获取一个应用自带的mp3文件
        player = MediaPlayer.create(this, R.raw.love);
        player.setOnCompletionListener(this);
        player.setLooping(true);
    }

    public void onCompletion(MediaPlayer player) {
        // TODO Auto-generated method stub
//        stopSelf();//结束了，则结束Service

    }

    public int onStartCommand(Intent intent, int flags, int startId){
        if(!player.isPlaying()){
            player.start();
        }
        return START_STICKY;
    }
    public void onDestroy(){
        //super.onDestroy();
        if(player.isPlaying()){
            player.stop();
        }
        player.release();
    }


    //为了和Activity交互，我们需要定义一个Binder对象
    class AudioBinder extends Binder {

        //返回Service对象
        Backgound_music getService(){
            return Backgound_music.this;
        }
    }
}


