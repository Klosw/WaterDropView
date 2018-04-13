package com.han.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.han.waterdropview.R;
import com.han.waterdropview.WaterDropView;

import java.util.ArrayList;

/**
 * 测试 Activity
 * <p>
 * Created by han on 2018-04-13.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "han";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaterDropView dropView = (WaterDropView) findViewById(R.id.mWaterDropView);
        // dropView.setData(new ArrayList());
        for (int i = 0; i < 10; i++) {
            dropView.addData("x " + i);
        }
        dropView.setTextColor(0xFFFFFF00);
        dropView.setOverlapping(false);
        //处理数据
        dropView.setDataProcessing(new WaterDropView.BaseDataProcessing() {
            /**
             * 处理图片
             * @param imageView
             * @param object
             * @param index
             * @return
             */
            @Override
            public int getImageId(ImageView imageView, Object object, int index) {
                return R.mipmap.pic_water;
            }

            /**
             * 处理文字数据
             * @param textView
             * @param object   数据
             * @param index    当前第几条数据
             * @return
             */
            @Override
            public String getText(TextView textView, Object object, int index) {
                // textView.setTextColor(0xFF000000);
                return super.getText(textView, object, index) + " c";
            }
        });
        //点击事件
        dropView.setOnItemClickListener(new WaterDropView.OnItemClickListener() {
            @Override
            public void onItemClickListener(WaterDropView aview, int item, Object mdata) {
                aview.removeData(mdata);
            }
        });
    }

}
