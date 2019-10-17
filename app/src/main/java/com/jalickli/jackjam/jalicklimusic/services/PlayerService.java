package com.jalickli.jackjam.jalicklimusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jalickli.jackjam.jalicklimusic.presenter.PlayerPresenter;

public class PlayerService extends Service {

    private PlayerPresenter playerPresenter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return playerPresenter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(playerPresenter ==null){
            playerPresenter = new PlayerPresenter();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerPresenter=null;
    }
}