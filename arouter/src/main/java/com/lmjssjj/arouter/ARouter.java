package com.lmjssjj.arouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

import static java.lang.Class.forName;

public class ARouter {

    private Context mContext;
    private static ARouter aRouter = new ARouter();

    private ARouter() {
    }

    public static ARouter getInstance() {
        return aRouter;
    }

    public void init(Context context) {
        mContext = context;
        //执行生成的工具类中的方法―将Activity的类对象加入到路由麦中
        List<String> classNames = getclassName("com.lmjssjj.utils");
        for (String className : classNames) {
            try {
                Class<?> utilclass = Class.forName(className);//判断是否是工Router的子类
                if (IARouter.class.isAssignableFrom(utilclass)) {
                    IARouter iRouter = (IARouter) utilclass.newInstance();
                    iRouter.putActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Map<String, Class<? extends Activity>> mRouterMap = new HashMap<>();

    public void putActivity(String key, Class<? extends Activity> className) {
        if (mRouterMap.containsKey(key)) {
        } else {
            mRouterMap.put(key, className);
        }
    }

    private List<String> getclassName(String packageName) {
//创建一个class对象的集合
        List<String> classList = new ArrayList<>();
        try {
            // 把当前应有的apk存储路径给dexFile
            DexFile df = new DexFile(mContext.getPackageCodePath());
            Enumeration<String> entries = df.entries();
            while (entries.hasMoreElements()) {
                String className = (String) entries.nextElement();
                if (className.contains(packageName)) {
                    classList.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }


    public void jumpActvity(String key, Bundle bundle) {
        if (mRouterMap.containsKey(key)) {
            Intent intent = new Intent(mContext, mRouterMap.get(key));
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }else{
            Toast.makeText(mContext,"wu",Toast.LENGTH_SHORT).show();
        }
    }
    public void jumpActvity(Context context,String key, Bundle bundle) {
        if (mRouterMap.containsKey(key)) {
            Intent intent = new Intent(context, mRouterMap.get(key));
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Toast.makeText(context,"wu",Toast.LENGTH_SHORT).show();
        }
    }


}
