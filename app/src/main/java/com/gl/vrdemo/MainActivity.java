package com.gl.vrdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private VrPanoramaView vr_panorama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //控件初始化
        vr_panorama = (VrPanoramaView) findViewById(R.id.vr_panorama);
        //切换VR的模式
        vr_panorama.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_STEREO);
        //隐藏信息按钮
        vr_panorama.setInfoButtonEnabled(false);
        //隐藏全屏显示
        vr_panorama.setFullscreenButtonEnabled(false);
        //对VR进行监听
        vr_panorama.setEventListener(new Myevent());
        //使用自定义的AsyncTask,播放VR效果
        new vrasty().execute();

    }
    //创建一个异步，由于VR资源数据量大,获取需要时间,故把加载图片放到子线程中进行,
    // 主线程来显示图片,故可以使用一个异步线程AsyncTask或EventBus来处理.
    private class vrasty extends AsyncTask<String,Void,Bitmap>{
        //该方法在子线程运行,从本地文件中把资源加载到内存中.
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                //转成字节输入流
                InputStream open = getAssets().open("andes.jpg");
                //把字节输入流转换成Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(open);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        //该方法在主线程运行
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //创建bVrPanoramaView.Options,去决定显示VR是普通效果,还是立体效果
            VrPanoramaView.Options options = new VrPanoramaView.Options();

            //TYPE_STEREO_OVER_UNDER立体效果:图片的上半部分放在左眼显示,下半部分放在右眼显示     TYPE_MONO:普通效果
            options.inputType=VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

            //使用VR控件对象,显示效果  参数:1.Bitmap对象
            // 2.VrPanoramaView.Options对象,决定显示的效果
            vr_panorama.loadImageFromBitmap(bitmap,options);
        }
    }

    //失去焦点的时候调用
    @Override
    protected void onPause() {
        super.onPause();
        vr_panorama.pauseRendering();
    }
    //再次获取焦点的时候调用
    @Override
    protected void onResume() {
        super.onResume();
        vr_panorama.resumeRendering();
    }
    //销毁的时候调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vr_panorama.shutdown();

    }
    //继承VrPanoramaEventListener，重写成功和失败的方法
    private class Myevent extends VrPanoramaEventListener {
        //成功
        @Override
        public void onLoadSuccess() {
            super.onLoadSuccess();
            Toast.makeText(MainActivity.this, "恭喜你加载成功", Toast.LENGTH_SHORT).show();
        }
        //失败
        @Override
        public void onLoadError(String errorMessage) {
            super.onLoadError(errorMessage);
            Toast.makeText(MainActivity.this, "对不起加载失败", Toast.LENGTH_SHORT).show();
        }
    }
}
