package com.coderpig.drysister.imgloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.os.Handler;  //这是正确的包

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.util.logging.Handler;  这个是错误的Handler包

public class PictureLoader {
    //声明三个变量 图片，图片url，字节数组
    //ImageView继承View组件主要用于显示图片，任何drawable对象都可以用imageview来显示
    private ImageView loadImg;
    private String imgUrl;
    private byte[] picByte;
    //Handler方法已过时，Handler（）会报错，需要在括号里输入内容
    //handler的作用1:传递消息；2：子线程通知主线程更新UI
    //Handler的初始化有两种方式，一种是没有参数的，一种是传递一个Looper对象。如果是在主线程中调用该构造方法，
    //使用的就是MainLooper。在该Handler的handleMessage方法中是可以更新UI的。
    Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    //处理发送来的消息 ↓ handMessage处理消息的方法,通常是用于被重写! 接受消息msg
        public void handleMessage(Message msg){
        super.handleMessage(msg);
        //判断消息是否来自于子线程，这个就是一个标志，handle接受多个message的时候，进行区分给与对应操作的 1 2 3 4
        if(msg.what == 0x123){
            if(picByte != null){
                //Bitmap位图包括像素以及长、宽、颜色等描述信息。
                // BitmapFactory提供了四类方法：decodeFile、decodeResource、decodeStream、decodeByteArray
                // decodeByteArray从字节序列里读取，decodeFile从文件中读取，decodeResource从资源中读取
                // decodeByteArray(byte[] data,  int offset,  int length,  Options opts)
                // 1：byte[] data：是要进行decode的资源数据；2： int offset：decode的位移量，一般为0
                // 3：int length：decode的数据长度一般为data数组的长度；4： Options opts：设置显示图片的参数，压缩，比例等
                Bitmap bitmap = BitmapFactory.decodeByteArray(picByte,0,picByte.length);
                //ImageView的setImageBitmap()是设置imageView组件的图片显示
                //实际上是setImageDrawable接口的封装，支持直接略过Bitmap对象进行组件图片的设置
                loadImg.setImageBitmap(bitmap);
            }
        }
    }
    };
    //自定义方法 传入图片和url连接，主要被调用的方法
    public void load(ImageView loadImg,String imgUrl) {
        this.loadImg = loadImg;
        this.imgUrl = imgUrl;
        //Drawable 是Android 中图像显示的常用方法。
        //Drawable是指可在屏幕上绘制的图形，已经通过getDrawable(int)等API检索或者应用到具有
        // android:drawable 和 android:icon 等属性的其他XML 资源的图形。
        Drawable drawable = loadImg.getDrawable();
        //instanceof来测试它所指向的对象是否是BitmapDrawable类的一个实例
        if(drawable != null && drawable instanceof BitmapDrawable){
            //图片资源的类型转换 Drawable → Bitmap
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            // isRecycled() 如果此位图已被回收，则返回 true。
            if(bitmap !=null && !bitmap.isRecycled()){
                //释放与此位图关联的本机对象，并清除对像素数据的引用。
                bitmap.recycle();
            }
        }
        //start（）方法来启动线程，真正实现了多线程运行
        new Thread(runnable).start();
    }
    //Runnable接口应该由其实例旨在由线程执行的任何类实现。该类必须定义一个名为 的无参数方法run。
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                //用GET方式获取连接，响应时间为10000内
                URL url = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                //判断响应值，是否成功
                if(conn.getResponseCode() == 200){
                    //获取响应的输入流
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024]; // 定义一个字节数组,相当于缓存
                    int length = -1; //得到实际读取到的字节数 读到最后返回-1
                    // 循环读取
                    while ((length = in.read(bytes)) !=-1 ){
                        out.write(bytes,0,length);
                    }
                    //将out读取到的转为字节数组赋值给picByte
                    picByte = out.toByteArray();
                    in.close();
                    out.close();
                    //子线程给处理发出信息
                    handler.sendEmptyMessage(0x123);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };


}

