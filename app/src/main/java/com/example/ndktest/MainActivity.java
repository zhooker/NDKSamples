package com.example.ndktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ndktest.ndk.NDKHelper;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLayout("点击", "传递字符串到JNI,转化并返回．", new ClickListener() {
            @Override
            public String getResult() {
                return NDKHelper.stringFromJNI("stringFromJNI");
            }
        });

        addLayout("点击", "JNI 访问、修改数组", new ClickListener() {
            @Override
            public String getResult() {
                int[] arr = new int[10];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = i;
                }

                StringBuffer buffer = new StringBuffer();
                buffer.append("before :");
                for (int i = 0; i < arr.length; i++) {
                    buffer.append(arr[i] + " ");
                }

                buffer.append("\n" + NDKHelper.sumArray(arr) +"\n");

                buffer.append("after    :");
                for (int i = 0; i < arr.length; i++) {
                    buffer.append(arr[i] + " ");
                }
                return buffer.toString();
            }
        });
    }

    private void addLayout(String name, String info, final ClickListener listner) {
        LinearLayout llyt = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_main_item, null);
        TextView infotv = (TextView) llyt.findViewById(R.id.info);
        infotv.setText(info);

        final TextView tv = (TextView) llyt.findViewById(R.id.result);
        Button btn = (Button) llyt.findViewById(R.id.btn);
        btn.setText(name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = listner.getResult();
                tv.setText(result);
            }
        });
        LinearLayout container = (LinearLayout) findViewById(R.id.activity_main);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 12);
        container.addView(llyt, params);
    }

    private interface ClickListener {
        String getResult();
    }
}
