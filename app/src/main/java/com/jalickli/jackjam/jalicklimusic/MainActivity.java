package com.jalickli.jackjam.jalicklimusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.jalickli.jackjam.jalicklimusic.interfaces.PlayerController;
import com.jalickli.jackjam.jalicklimusic.interfaces.ViewController;
import com.jalickli.jackjam.jalicklimusic.services.PlayerService;

import java.io.IOException;

import static com.jalickli.jackjam.jalicklimusic.interfaces.PlayerController.PLAYER_STATE_NEXT;
import static com.jalickli.jackjam.jalicklimusic.interfaces.PlayerController.PLAYER_STATE_PAUSE;
import static com.jalickli.jackjam.jalicklimusic.interfaces.PlayerController.PLAYER_STATE_PLAY;
import static com.jalickli.jackjam.jalicklimusic.interfaces.PlayerController.PLAYER_STATE_PRE;
import static com.jalickli.jackjam.jalicklimusic.interfaces.PlayerController.PLAYER_STATE_STOP;

public class MainActivity extends AppCompatActivity {

    private SeekBar mSeekBar;
    private Button mPre;
    private Button mPlayOrPause;
    private Button mNext;
    private Button mStop;

    private PlayerConnection mPlayerConnection;
    private PlayerController mPlayerController;

    private boolean isUserTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        /*****************************************************************************/
        initService();  //开启服务
        initBindService();  //绑定服务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayerConnection != null){
            //释放资源
            mPlayerController.unRegisterViewController();
            unbindService(mPlayerConnection);
        }
    }

    /*
        * 初始化各控件
        * */
    private void initView(){
        mSeekBar = (SeekBar) this.findViewById(R.id.seekBar);
        mPre = (Button) this.findViewById(R.id.btn_pre);
        mPlayOrPause = (Button) this.findViewById(R.id.btn_play_or_pause);
        mNext = (Button) this.findViewById(R.id.btn_next);
        mStop = (Button) this.findViewById(R.id.btn_stop);
    }

    /*
    * 初始化控件的事件
    * */
    private void initEvent(){
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条发生改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //我的手已经触摸上去拖动
                isUserTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动
                isUserTouch = false;
                int touchProgress = seekBar.getProgress();
                if(mPlayerController != null){
                    mPlayerController.seekTo(touchProgress);
                }
            }
        });
        mPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上一首
                if(mPlayerController != null){
                    mPlayerController.pre();
                }
            }
        });
        mPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放或者暂停
                if(mPlayerController != null){
                    try {
                        mPlayerController.playOrPause();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下一首
                if(mPlayerController != null){
                    mPlayerController.next();
                }
            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止播放
                if(mPlayerController != null){
                    mPlayerController.stop();
                }
            }
        });
    }

    /******************************************开启服务头部********************************************************/
    private void initService(){
        startService(new Intent(this,PlayerService.class));
    }
    /******************************************开始服务尾部********************************************************/


    /******************************************绑定服务头部********************************************************/
    private void initBindService(){
        Intent intent = new Intent(this, PlayerService.class);
        if(mPlayerConnection == null){
            mPlayerConnection = new PlayerConnection();
        }
        bindService(intent,mPlayerConnection,BIND_AUTO_CREATE);
    }

    //内部类
    private class PlayerConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //系统调用这个来传送在service的onBind()中返回的IBinder
            mPlayerController = (PlayerController) service;
            mPlayerController.registerViewController(mViewController);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Android系统在同service的连接意外丢失时调用这个．比如当service崩溃了或被强杀了．当客户端解除绑定时，这个方法不会被调用．
            mPlayerController = null;
        }
    }
    /********************************************绑定服务尾部******************************************************/

    private ViewController mViewController = new ViewController() {
        @Override
        public void onPlayerStateChang(int state) {
            //我们要根据播放状态来修改UI
            switch (state) {
                case PLAYER_STATE_PLAY :  //播放中的时候，我们要修改按钮显示成暂停
                    mPlayOrPause.setText("暂停");
                    break;
                case PLAYER_STATE_NEXT:
                    break;
                case PLAYER_STATE_PAUSE:
                    mPlayOrPause.setText("播放");
                    break;
                case PLAYER_STATE_PRE:
                    break;
                case PLAYER_STATE_STOP:
                    mPlayOrPause.setText("播放");
                    break;
            }
        }

        @Override
        public void onSeekchange(final int seek) {
            //改变播放进度，有一个条件，当用户的手触摸到进度条的时候，就不更新
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isUserTouch){
                        mSeekBar.setProgress(seek);
                    }
                }
            });
        }
    };

}
