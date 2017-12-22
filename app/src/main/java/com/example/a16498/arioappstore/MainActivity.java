package com.example.a16498.arioappstore;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.a16498.arioappstore.Service.DownLoadService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{




    private DownLoadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownLoadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };






    Button bilibili_pause , bilibili_start , bilibili_cancel;
    Button QQ_pause , QQ_start , QQ_cancel;
    Button we_pause , we_start , we_cancel;
    Button zhi_pause , zhi_start , zhi_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bilibili_cancel = findViewById(R.id.Bili_cancel);
        bilibili_start = findViewById(R.id.Bili_start);
        bilibili_pause = findViewById(R.id.Bili_pause);
        bilibili_pause.setOnClickListener(this);
        bilibili_start.setOnClickListener(this);
        bilibili_cancel.setOnClickListener(this);

        QQ_cancel = findViewById(R.id.QQ_cancel);
        QQ_start = findViewById(R.id.QQ_start);
        QQ_pause = findViewById(R.id.QQ_pause);
        QQ_pause.setOnClickListener(this);
        QQ_start.setOnClickListener(this);
        QQ_cancel.setOnClickListener(this);

        we_start = findViewById(R.id.we_start);
        we_pause = findViewById(R.id.we_pause);
        we_cancel = findViewById(R.id.we_cancel);
        we_start.setOnClickListener(this);
        we_pause.setOnClickListener(this);
        we_cancel.setOnClickListener(this);



        zhi_cancel = findViewById(R.id.zhi_cancel);
        zhi_start = findViewById(R.id.zhi_start);
        zhi_pause = findViewById(R.id.zhi_pause);
        zhi_pause.setOnClickListener(this);
        zhi_start.setOnClickListener(this);
        zhi_cancel.setOnClickListener(this);

        //其他

        Intent intent = new Intent(this,DownLoadService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onClick(View view) {
        if(downloadBinder == null){
            return;
        }
        switch (view.getId()){
            case R.id.Bili_start:
                String bili = "https://dl.hdslb.com/mobile/latest/iBiliPlayer-bili.apk";
                downloadBinder.startDownload(bili);
                break;
            case R.id.Bili_pause:
                downloadBinder.pauseDownload();
                break;
            case R.id.Bili_cancel:
                downloadBinder.cancelDownload();
                break;
            case R.id.QQ_start:
                String qq = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
                downloadBinder.startDownload(qq);
                break;
            case R.id.QQ_pause:
                downloadBinder.pauseDownload();
                break;
            case R.id.QQ_cancel:
                downloadBinder.cancelDownload();
                break;
            case R.id.we_start:
                String we = "http://dldir1.qq.com/weixin/android/weixin6523android1180.apk";
                downloadBinder.startDownload(we);
                break;
            case R.id.we_pause:
                downloadBinder.pauseDownload();
                break;
            case R.id.we_cancel:
                downloadBinder.cancelDownload();
                break;


            case R.id.zhi_start:
                String zhi = "https://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk";
                downloadBinder.startDownload(zhi);
                break;
            case R.id.zhi_pause:
                downloadBinder.pauseDownload();
                break;
            case R.id.zhi_cancel:
                downloadBinder.cancelDownload();
                break;



                default:
                    break;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "你没有足够的权限使用小喵喔！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
                default:
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
