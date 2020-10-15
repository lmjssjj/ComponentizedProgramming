package com.lmjssjj.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LiveDataBus {

    private static LiveDataBus mLiveDataBus = new LiveDataBus();

    private Map<String, BusMutableLiveData<Object>> mLiveDatas = new HashMap<>();

    private LiveDataBus() {
    }

    public static LiveDataBus getInstance() {
        return mLiveDataBus;
    }

    public synchronized <T> BusMutableLiveData<T> with(String key, Class<T> type) {
        if (!mLiveDatas.containsKey(key)) {
            mLiveDatas.put(key, new BusMutableLiveData<Object>());
        }
        return (BusMutableLiveData<T>) mLiveDatas.get(key);
    }

    public class BusMutableLiveData<T> extends MutableLiveData<T> {
        private boolean isViscosity = false;

        public void observe(@NonNull LifecycleOwner owner, boolean isViscosity, @NonNull Observer<? super T> observer) {
            this.isViscosity = isViscosity;
            observe(owner, observer);
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            try {

                if (isViscosity) {
                    hook(observer);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * hook技术的实现方法―拦截onChange方法的执行
         */
        private void hook(Observer<? super T> observer) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            //获取到LiveData的类对象
            Class<LiveData> liveDataClass = LiveData.class;
            //根据类对象获取到mvVersion的反射对象
            Field mVersionField = liveDataClass.getDeclaredField("mVersion");
            //打开权限
            mVersionField.setAccessible(true);
            //获取到mObserversField的放射对象
            Field mObserversField = liveDataClass.getDeclaredField("mObservers");
            mObserversField.setAccessible(true);
            //获取对象值
            Object mObservers = mObserversField.get(this);
            //获取到mObservers 这个map的get方法
            Method get = mObservers.getClass().getDeclaredMethod("get", Object.class);
            //打开权限
            get.setAccessible(true);
            //执行get方法
            Object invokeEntry = get.invoke(mObservers, observer);
            //定义一个空对象lifecycleBoundlobserver
            Object observerWrapper = null;
            if (invokeEntry != null && invokeEntry instanceof Map.Entry) {
                observerWrapper = ((Map.Entry) invokeEntry).getValue();
            }

            if (observerWrapper == null) {
                throw new NullPointerException("observerWrapper不能为空");
            }
            Class clazz = observerWrapper.getClass();
            //得到bbserverwrapper的类对象
            Class<?> aclass = observerWrapper.getClass().getSuperclass();
            //获致uLastVersior的发射对象
            Field mLastVersionField = aclass.getDeclaredField("mLastVersion");
            //打开权限
            mLastVersionField.setAccessible(true);
            //获取到mVersion的值
            Object o = mVersionField.get(this);
            //把它的值赋值给LastVersion
            mLastVersionField.set(observerWrapper, o);

        }
    }
}
