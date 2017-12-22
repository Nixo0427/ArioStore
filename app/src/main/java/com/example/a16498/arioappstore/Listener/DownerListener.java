package com.example.a16498.arioappstore.Listener;

/**
 * Created by 16498 on 2017/12/20.
 */

public interface DownerListener {

    //下载进度

    void onProgerss(int progress);

    //下载成功

    void onSuccess();

    //下载失败

    void onFailed();

    //暂停

    void onPaused();

    //取消

    void onCanceled();



}
