package com.example.ndktest;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ndktest.ndk.NDKHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initNativeDirectory(getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLayout("传递字符串到JNI,转化并返回．", new ClickListener() {
            @Override
            public String getResult() {
                return NDKHelper.stringFromJNI("stringFromJNI");
            }
        });

        addLayout("JNI 访问、修改数组", new ClickListener() {
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

                buffer.append("\n" + NDKHelper.sumArray(arr) + "\n");

                buffer.append("after    :");
                for (int i = 0; i < arr.length; i++) {
                    buffer.append(arr[i] + " ");
                }
                return buffer.toString();
            }
        });

        addLayout("C++ 访问 Java 实例方法和静态方法", new ClickListener() {
            @Override
            public String getResult() {
                return NDKHelper.callJavaInstaceMethod() + "\n" +
                        NDKHelper.callJavaStaticMethod();
            }
        });

        addLayout("C++ 访问 Java 实例变量和静态变量", new ClickListener() {
            @Override
            public String getResult() {
                ClassField obj = new ClassField();
                obj.setNum(10);
                obj.setStr("Hello");

                // 本地代码访问和修改ClassField为中的静态属性num
                // 不能修改static final 的值
                NDKHelper.accessInstanceField(obj);
                NDKHelper.accessStaticField();
                return obj.getNum() + "\nafter modified : " + obj.getStr();
            }
        });

        addLayout("JNI 调用构造方法和父类实例方法。", new ClickListener() {
            @Override
            public String getResult() {
                return NDKHelper.callSuperInstanceMethod();
            }
        });

        addLayout("JNI 打开第三方动态库。", new ClickListener() {
            @Override
            public String getResult() {
                final String libraryName = "";
                final String path = "/data/data/com.example.ndktest/" + libraryName;
                try {
                    InputStream is = getAssets().open(libraryName);
                    File target = new File(path);
                    if (!target.exists())
                        target.createNewFile();
                    FileOutputStream fos = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                        // buffer字节
                        fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                    }
                    fos.flush();// 刷新缓冲区
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    Log.e("zhuangsj", "JNI 打开第三方动态库  " + e);
                    e.printStackTrace();
                }
                return "result:" + NDKHelper.openSharedLibrary("libnative-hello.so");
            }
        });

        addLayout("JNI 调用Handler传送数据。", new ClickListener() {
            @Override
            public String getResult() {
                NDKHelper.callHandlerFromJava(new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("zhuangsj", "JNI -> 调用Handler传送数据 :  what=" + msg.what +",arg1=" + msg.arg1 + ",arg2=" + msg.arg2);
                    }
                });

                return "000";
            }
        });
    }

    private void addLayout(String info, final ClickListener listner) {
        LinearLayout llyt = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_main_item, null);

        final TextView tv = (TextView) llyt.findViewById(R.id.result);
        tv.setVisibility(View.GONE);
        Button btn = (Button) llyt.findViewById(R.id.btn);
        btn.setText(info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = listner.getResult();
                tv.setVisibility(View.VISIBLE);
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

    public static void initNativeDirectory(Application application) {
        Log.e("zhuangsj", "initNativeDirectory  ");
        if (hasDexClassLoader()) {
            try {
                createNewNativeDir2(application);
            } catch (Exception e) {
                Log.e("zhuangsj", "initNativeDirectory2  " + e);
                e.printStackTrace();
            }
        }

        try {
            createNewNativeDir3(application);
        } catch (Exception e) {
            Log.e("zhuangsj", "initNativeDirectory3  " + e);
            e.printStackTrace();
        }
    }

    private static void createNewNativeDir(Context context) throws Exception {
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        //获取当前类的属性
        Object nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
        ((Field) nativeLibraryDirectories).setAccessible(true);
        //获取 DEXPATHList中的属性值
        File[] files1 = (File[]) ((Field) nativeLibraryDirectories).get(pathList);
        Object filesss = Array.newInstance(File.class, files1.length + 1);
        //添加自定义.so路径
        Array.set(filesss, 0, new File("/storage/emulated/0/"));
        //将系统自己的追加上
        for (int i = 1; i < files1.length + 1; i++) {
            Array.set(filesss, i, files1[i - 1]);
        }
        ((Field) nativeLibraryDirectories).set(pathList, filesss);
    }

    private static void createNewNativeDir2(Context context) throws Exception {
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        //获取当前类的属性
        Object nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
        ((Field) nativeLibraryDirectories).setAccessible(true);
        //获取 DEXPATHList中的属性值
        List files1 = (List) ((Field) nativeLibraryDirectories).get(pathList);
        files1.add(0, new File("/data/data/com.example.ndktest"));
        ((Field) nativeLibraryDirectories).set(pathList, files1);
    }

    private static void createNewNativeDir3(Context context) throws Exception {
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        //获取当前类的属性
        Object nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
        ((Field) nativeLibraryDirectories).setAccessible(true);

        Log.e("zhuangsj", "createNewNativeDir3  : " + ((Field) nativeLibraryDirectories).get(pathList));
    }

    private static Object getPathList(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(obj, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getField(Object obj, Class cls, String str) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    /**
     * 仅对4.0以上做支持
     *
     * @return
     */
    private static boolean hasDexClassLoader() {
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
            return true;
        } catch (ClassNotFoundException var1) {
            Log.e("zhuangsj", "hasDexClassLoader  " + var1);
            return false;
        }
    }
}
