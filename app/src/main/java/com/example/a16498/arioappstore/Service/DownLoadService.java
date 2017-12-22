package com.example.a16498.arioappstore.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.webkit.DownloadListener;
import android.widget.Toast;

import com.example.a16498.arioappstore.Listener.DownerListener;
import com.example.a16498.arioappstore.MainActivity;
import com.example.a16498.arioappstore.R;
import com.example.a16498.arioappstore.Task.DownTask;

import java.io.File;

public class DownLoadService extends Service {

    private DownTask downTask;

    private String downloadUrl;

    //实例化接口,实现抽象方法
    private DownerListener downerListener = new DownerListener() {
        @Override
        public void onProgerss(int progress) {
            getNotifincationManager().notify(1,getNotifincation("小喵正在努力下载中..",progress));
        }

        @Override
        public void onSuccess() {
            downTask = null;
            stopForeground(true);
            getNotifincationManager().notify(1,getNotifincation("小喵放大招！下完啦！",-1));
            Toast.makeText(DownLoadService.this, "小喵下完了，快夸夸小喵！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downTask = null;
            stopForeground(true);
            getNotifincationManager().notify(1,getNotifincation("下载失败了,这绝对不是喵喵的错喔~",-1));
            Toast.makeText(DownLoadService.this, "下载失败了,这绝对不是喵喵的错喔~", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downTask = null ;
            Toast.makeText(DownLoadService.this, "你快跑！小喵帮你暂停啦，撑不了多久！", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCanceled() {
            downTask = null ;
            stopForeground(true);
            Toast.makeText(DownLoadService.this, "取消了喵！", Toast.LENGTH_SHORT).show();

        }
    };

    public DownloadBinder mBinder = new DownloadBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    //绑定Activity的类
    public class DownloadBinder extends Binder{

        public  void startDownload(String url){
            if(downTask == null){
                downloadUrl = url;
                downTask = new DownTask(downerListener);
                downTask.execute(downloadUrl);
                startForeground(1,getNotifincation("正在下载喵,清稍等喵！...",0));
                Toast.makeText(DownLoadService.this, "开始下载啦喵！OvO/", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload(){
            if(downTask != null){
                downTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if(downTask != null){
                downTask.cancelDownload();
            }else{
                if(downloadUrl != null){
                    //友好性提示 是否取消，取消将删除文件。
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory+fileName);
                    if(file.exists()){
                        file.delete();
                    }
                    getNotifincationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownLoadService.this, "小喵为你取消下载了喵", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private NotificationManager getNotifincationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotifincation(String title , int Progress ){
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if(Progress >= 0){
            builder.setContentText("小喵努力为您下载了"+Progress+"%"+"喵！");
        }
        return builder.build();
    }
}
