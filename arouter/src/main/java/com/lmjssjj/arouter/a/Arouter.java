package com.lmjssjj.arouter.a;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lmjssjj.arouter.IARouter;

import org.w3c.dom.Element;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * 中间人
 */
public class Arouter {
    private static Arouter arouter = new Arouter();
    //上下文
    private Context context;

    //装载了所有的Activity的类对象  路由表
    private HashMap<String, Class<? extends Activity>> map;

    private Arouter() {
        map = new HashMap<>();
    }

    public static Arouter getInstance() {
        return arouter;
    }

    public void init(Context context) {
        this.context = context;
        //执行所有生成文件的里面的putActivity的方法
        //找到这些类
        List<String> className = getClassName("com.lmjssjj.arouter");
        for (String aClass : className) {
            try {
                Class<?> utilClass = Class.forName(aClass);
                //判断是否是IRouter的子类
                if (IARouter.class.isAssignableFrom(utilClass)) {
                    IARouter iRouter = (IARouter) utilClass.newInstance();
                    iRouter.putActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 将类对象添加进路由表的方法
     *
     * @param key
     * @param clazz
     */
    public void addActivity(String key, Class<? extends Activity> clazz) {
        if (key != null && clazz != null && !map.containsKey(key)) {
            map.put(key, clazz);
        }
    }

    /**
     * 跳转窗体的方法
     *
     * @param key
     * @param bundle
     */
    public void jumpActivity(String key, Bundle bundle) {
        Class<? extends Activity> activityClass = map.get(key);
        if (activityClass != null) {
            Intent intent = new Intent(context, activityClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        }
    }


    /**
     * 通过包名获取这个包下面的所有的类名
     *
     * @param packageName
     * @return
     */
    private List<String> getClassName(String packageName) {
        //创建一个class对象的集合
        List<String> classList = new ArrayList<>();
        try {
            //把当前应有的apk存储路径给dexFile
            ArrayList<DexFile> multiDex = getMultiDex();
            for (DexFile dex : multiDex) {
                Enumeration<String> entries = dex.entries();
                while (entries.hasMoreElements()) {
                    String className = (String) entries.nextElement();
                    if (className.contains(packageName)) {
                        classList.add(className);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }


    public ArrayList<DexFile> getMultiDex()
    {
        BaseDexClassLoader dexLoader = (BaseDexClassLoader) getClassLoader();
        Field f = getField("pathList", getClassByAddressName("dalvik.system.BaseDexClassLoader"));
        Object pathList = getObjectFromField(f, dexLoader);
        Field f2 = getField("dexElements", getClassByAddressName("dalvik.system.DexPathList"));
        Object[] list = getObjectFromField(f2, pathList);
        Field f3 = getField("dexFile", getClassByAddressName("dalvik.system.DexPathList$Element"));

        ArrayList<DexFile> res = new ArrayList<>();
        for(int i = 0; i < list.length; i++)
        {
            DexFile d = getObjectFromField(f3, list[i]);
            res.add(d);
        }

        return res;
    }

    private Field getField(String className, Class<?> clazz) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(className);
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return field;
    }

    public ClassLoader getClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }


    public Class<?> getClassByAddressName(String classAddressName)
    {
        Class mClass = null;
        try
        {
            mClass = Class.forName(classAddressName);
        } catch(Exception e)
        {
        }
        return mClass;
    }


    public <T extends Object> T getObjectFromField(Field field, Object arg)
    {
        try
        {
            field.setAccessible(true);
            return (T) field.get(arg);
        } catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
