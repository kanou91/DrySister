package com.coderpig.drysister.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.coderpig.drysister.R;
import com.coderpig.drysister.bean.entity.Sister;
import com.coderpig.drysister.imgloader.PictureLoader;
import com.coderpig.drysister.network.SisterApi;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button refreshBtn;
    private Button showBtn;
    private ImageView showImg;

//    private ArrayList<String> urls;
    private ArrayList<Sister> data;
    private int curPos = 0;
    private int page = 1;
    private PictureLoader loader;
    private SisterApi sisterApi;
    private SisterTask sisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sisterApi = new SisterApi();
        loader = new PictureLoader(); //创造一个PictureLoader（）对象
        initData();
        initUI();
    }
    private void initData(){
          data = new ArrayList<>();
//        urls = new ArrayList<>();
//        urls.add("https://ae01.alicdn.com/kf/Ue16c54cac6574a06a0c1afdad979b007W.jpg");
//        urls.add("https://ae01.alicdn.com/kf/Uec00959acd9c4d0aa900d5fb8ea481931.jpg");
//        urls.add("https://ae01.alicdn.com/kf/Uef43b2afdd2e4480aab896c8fad7e5f1c.jpg");
//        urls.add("https://ae01.alicdn.com/kf/U892b3e7fb9b84ce3ade783d3396fc371A.jpg");
//        urls.add("https://ae01.alicdn.com/kf/U54ae3ae4e9ac4572a436c11a9cfa4927E.jpg");
    }
    private void initUI() {
        showBtn = (Button) findViewById(R.id.btn_show);
        showImg = (ImageView) findViewById(R.id.img_show);
        refreshBtn = (Button) findViewById(R.id.btn_refresh);

        showBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_show:
              if(data !=null && !data.isEmpty()){
                  if(curPos>9){
                      curPos = 0;
                  }
                  loader.load(showImg,data.get(curPos).getUrl()); //调用加载图片
                  curPos++; //循环
              }
                break;
            case R.id.btn_refresh:
                sisterTask = new SisterTask();
                sisterTask.execute();
                curPos = 0;
                break;
        }
    }
    private class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>>{
        public SisterTask() { }
        @Override
        protected ArrayList<Sister> doInBackground(Void...params){
            return sisterApi.fetchSister(10,page);
        }
        @Override
        protected void onPostExecute(ArrayList<Sister> sisters){
            super.onPostExecute(sisters);
            data.clear();
            data.addAll(sisters);
            page++;
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        sisterTask.cancel(true);
    }
}