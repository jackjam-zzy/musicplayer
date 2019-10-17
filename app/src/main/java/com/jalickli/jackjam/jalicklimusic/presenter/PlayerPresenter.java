package com.jalickli.jackjam.jalicklimusic.presenter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import com.jalickli.jackjam.jalicklimusic.R;
import com.jalickli.jackjam.jalicklimusic.interfaces.PlayerController;
import com.jalickli.jackjam.jalicklimusic.interfaces.ViewController;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerPresenter extends Binder implements PlayerController {

    private static final String TAG ="PlayerPresenter" ;
    private ViewController mViewController;
    private MediaPlayer mMediaPlayer;
    private int mCurrentState = PLAYER_STATE_STOP;

    private Timer mTimer;
    private SeekTimeTask mSeekTimeTask;

    @Override
    public void playOrPause() throws IOException {
        Log.d(TAG,"playOrPause。。。。。。。。。。。。。。。。");
        if(mCurrentState == PLAYER_STATE_STOP) {
            //创建播放器
            initPlayer();
            //设置数据源
            mMediaPlayer.setDataSource("/mnt/sdcard/帝女花.mp3");
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            //播放后要改变状态
            mCurrentState = PLAYER_STATE_PLAY;
            startTimer();
        }else if(mCurrentState == PLAYER_STATE_PLAY){
            //如果当前状态是播放的，那么我们就暂停
            if(mMediaPlayer!= null){
                mMediaPlayer.pause();
                mCurrentState = PLAYER_STATE_PAUSE;
                stopTimer();
            }
        }else if(mCurrentState == PLAYER_STATE_PAUSE) {
            //如果当前状态是暂停，那么我们就继续播放
            if(mMediaPlayer!= null){
                mMediaPlayer.start();
                mCurrentState = PLAYER_STATE_PLAY;
                startTimer();
            }
        }
        if(mViewController !=null){
            //通知UI改变状态，更新界面
            mViewController.onPlayerStateChang(mCurrentState);
        }
    }
    /*
    * 初始化播放器
    * */
    private void initPlayer(){
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }


    @Override
    public void pre() {
        Log.d(TAG,"pre。。。。。。。。。。。。。。。。");
    }

    @Override
    public void next() {
        Log.d(TAG,"next。。。。。。。。。。。。。。。。");
    }

    @Override
    public void stop() {
        Log.d(TAG,"stop。。。。。。。。。。。。。。。。");
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mCurrentState = PLAYER_STATE_STOP;
            stopTimer();
            if(mViewController !=null){
                //通知UI改变状态，更新界面
                mViewController.onPlayerStateChang(mCurrentState);
            }

            mMediaPlayer.release();//释放资源
            mMediaPlayer = null;
        }
    }

    @Override
    public void seekTo(int seek) {
        Log.d(TAG,"seekTo。。。" + seek);
        //0~100之间
        //需要做一个转换，得到的seek其实是一个百分比
        if(mMediaPlayer != null) {
            int tarSeek = (int) (seek * 1.0f / 100 * mMediaPlayer.getDuration());//获取文件总长度
            mMediaPlayer.seekTo(tarSeek);
        }
    }
    /*
    * 开启一个timertask
    * */
    private void startTimer(){
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mSeekTimeTask == null) {
            mSeekTimeTask = new SeekTimeTask();
        }
        mTimer.schedule(mSeekTimeTask,0,500);
    }
    /*
    * 关闭一个timertask
    * */
    private void stopTimer(){
        if (mSeekTimeTask != null) {
            mSeekTimeTask.cancel();
            mSeekTimeTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class SeekTimeTask extends TimerTask{
        @Override
        public void run() {
            if (mMediaPlayer != null && mViewController!= null) {
                //获取当前播放进度
                int currentPosition = mMediaPlayer.getCurrentPosition();
                Log.d(TAG, "当前播放进度" + currentPosition);
                int curPosition = (int) (currentPosition*1.0f/mMediaPlayer.getDuration()*100);
                mViewController.onSeekchange(curPosition);
            }
        }
    }




    @Override
    public void registerViewController(ViewController viewController) {
        this.mViewController = viewController;
    }

    @Override
    public void unRegisterViewController() {
        mViewController = null;
    }
}