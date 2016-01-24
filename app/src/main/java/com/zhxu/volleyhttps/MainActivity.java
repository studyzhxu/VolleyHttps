package com.zhxu.volleyhttps;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.zhxu.volleyhttps.net.DataRequester;

public class MainActivity extends Activity {

    private ImageView iv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
        requestImage();

    }

    public void requestImage(){
        DataRequester.withHttp(this)

                .setUrl("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg")
                .setMethod(DataRequester.Method.GET)
                .setImageView(iv)
                .setDafaultImage(R.mipmap.ic_launcher)
                .setFailImage(R.mipmap.ic_launcher)
                .requestImage();
    }


}
