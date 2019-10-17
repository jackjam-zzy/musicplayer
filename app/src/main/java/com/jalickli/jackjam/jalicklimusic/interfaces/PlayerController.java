package com.jalickli.jackjam.jalicklimusic.interfaces;

import java.io.IOException;

/*
 * @Author: 郑智源
 * @Description:提供操作播放器的接口
 **/
public interface PlayerController {

    //播放状态
    public static final int PLAYER_STATE_PLAY = 1;
    public static final int PLAYER_STATE_PAUSE = 2;
    public static final int PLAYER_STATE_PRE = 3;
    public static final int PLAYER_STATE_NEXT = 4;
    public static final int PLAYER_STATE_STOP = 5;

    void playOrPause() throws IOException;

    void pre();

    void next();

    void stop();

    /*
    * 设置播放进度
    *
    * @param seek 播放进度
    * */
    void seekTo(int seek);

    /*
    * 把UI的接口控制交给本接口
    * */
    void registerViewController(ViewController viewController);

    /*
    * 取消接口的注册
    * */
    void unRegisterViewController();
}