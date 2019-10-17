package com.jalickli.jackjam.jalicklimusic.interfaces;

public interface ViewController {

    /*
    * 播放状态改变的通知
    * */
    void onPlayerStateChang(int state);

    /*
    * 播放进度的改变
    * */
    void onSeekchange(int seek);

}