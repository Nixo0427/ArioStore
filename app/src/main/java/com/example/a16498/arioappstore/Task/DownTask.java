package com.example.a16498.arioappstore.Task;

import android.os.AsyncTask;
import android.os.Environment;

import com.example.a16498.arioappstore.Listener.DownerListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 16498 on 2017/12/20.
 */

public class DownTask extends AsyncTask<String,Integer,Integer> {


    public static final int TYPE_SUCCESS = 0;

    public static final int TYPE_FAIL = 1;

    public static final int TYPE_PAUSE = 2;

    public static final int TYPE_CANCELED = 3;

    private DownerListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;




    public DownTask(DownerListener listener){
        this.listener = listener;
    }


    /**
     * doInBackGround
     * 进行后台的耗时操作，这里主要是下载任务。
     * @param strings
     * @return
     */

    @Override
    protected Integer doInBackground(String... strings) {

        InputStream inputStream = null;
        RandomAccessFile saveFile = null;
        File file ;

        //记录已下载的文件长度
        long downloadLength = 0;
        String downloadUrl = strings[0];

        //获取文件名，把URL从后往前数到第一个/符号停止 就是文件的名字了
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));

        //通过Environment.getExternalStoragePublicDirectory方法获取到下载文件夹的路径
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        //在指定的路径创建文件
        file = new File(directory+fileName);

        //判断文件是否存在，是->文件大小就是该文件的长度
        if (file.exists()){
            downloadLength = file.length();
        }

        //构建网络请求方法对象，将url传入进去，该方法会返回文件长度

        //拿到了长度，接下来的判断就好办了。

        try {

            long contentLength = 0;
            //新建请求客户端OkHttpClient。
            OkHttpClient client = new OkHttpClient();
            //创建并且封装请求体，将URL封装进去。
            Request request = new Request.Builder()
                    .url(downloadUrl)
                    .build();
            //创建返回表单，将客户端调用newCall方法把请求放进去，使用的是串行方法。
            Response response = client.newCall(request).execute();
            //对返回表单进行判断，如果返回表单不等于空并且返回的值为200~300就说明请求成功了
            //将返回表单的body部分的长度
            //否则返回0
            if(response != null && response.isSuccessful()){
                contentLength = response.body().contentLength();
                response.body().close();
            }


            //对下载结果进行判断，对于绝对的下载来说只有两种结果，
            // 一个是下载成功，一个是失败，如果是0就说明是变了，如果等于文件的长度了就说明成功了。
            if (contentLength == 0) {
                return TYPE_FAIL;
            } else if (contentLength == downloadLength) {
                return TYPE_SUCCESS;
            }

            //接受方法传过来的返回表单。
            Request request2 = new Request.Builder()
                    .addHeader("RANGE","bytes=" + downloadLength + "-")
                    .url(downloadUrl)
                    .build();
             response = client.newCall(request2).execute();
            //判断表单是否为空，如果不是空就将表单的表单体转为字节流与原来的文件进行对接。
            if(response != null){
                inputStream = response.body().byteStream();
                saveFile = new RandomAccessFile(file,"rw");
                saveFile.seek(downloadLength);
                byte[] b = new byte[1024];
                //之前下载的总体大小
                int toatal = 0 ;
                //每次存入长度
                int len ;

                //每次往len里传入1024个字节当len没读到最后一个空间，也就是没读到-1的时候继续循环
                while ((len = inputStream.read(b)) != -1 ){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSE;
                    }else{
                        toatal += len;
                        saveFile.write(b,0,len);
                        //计算下载百分比。（下载的大小X100/文件的大小）
                        int progress = (int) ((toatal + downloadLength) * 100 / contentLength);
                        //把下载进度传递给显示进度的方法。
                        publishProgress(progress);
                    }
                }
                //把返回表单关掉 并返回成功
                response.body().close();
                return TYPE_SUCCESS;
            }


        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
            if(inputStream != null){
                inputStream.close();
                }
            if(saveFile != null){
                saveFile.close();
            }
            if (isCanceled&&file != null){
                file.delete();
            }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }


        return TYPE_FAIL;


    }

    /**
     * onProgressUpdate
     * 在这里进行实时的进度更新
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if(progress > lastProgress){
            listener.onProgerss(progress);
            lastProgress = progress;
        }

    }


    /**
     * onPostExecute
     * 在这里进行结果的展示
     * @param integer
     */
    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAIL:
                listener.onFailed();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            case TYPE_PAUSE:
                listener.onPaused();
                break;
                default:
                    break;
        }
    }

    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }
}
