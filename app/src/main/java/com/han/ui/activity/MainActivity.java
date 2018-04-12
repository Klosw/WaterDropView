package com.han.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.han.waterdropview.R;
import com.han.waterdropview.WaterDropView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaterDropView im = (WaterDropView) findViewById(R.id.mWaterDropView);
        // im.setData(new ArrayList());
        for (int i = 0; i < 10; i++) {
            im.addData("x " + i);
        }
        im.setDataProcessing(new WaterDropView.BaseDataProcessing() {
            @Override
            public int getImageId(ImageView imageView, Object object, int index) {
                return R.mipmap.pic_water;
            }

            @Override
            public String getText(TextView textView, Object object, int index) {
                textView.setTextColor(0xFF000000);
                return super.getText(textView, object, index) + " c";
            }
        });
        try {
            Class mclass = Class.forName("com.han.waterdropview.IRectangle");
            Object object = mclass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
