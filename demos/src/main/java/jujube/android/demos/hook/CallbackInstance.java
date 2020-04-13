package jujube.android.demos.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 接口实例化
 */

public class CallbackInstance {

    public static void main(String[] args) {
        try {
            Class<?> mClass = Class.forName("jujube.android.demos.hook.CallbackInstance");
            Class<?> mCallback = Class.forName("jujube.android.demos.hook.CallbackInstance$Callback");
            Handler mHandler = new Handler();
            Object mObj = Proxy.newProxyInstance(CallbackInstance.class.getClassLoader(), new Class[]{mCallback}, mHandler);
            Method mMethod = mClass.getDeclaredMethod("mainMethod", String[].class, mCallback);
            mMethod.invoke(mClass.newInstance(), new String[]{"1", "2", "3"}, mObj);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException 
                | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    public static class Handler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("doing callback...");
            String[] items = (String[]) args[0];
            for (Object item : items)
                System.out.println(item);
            return null;
        }
    }

    public interface Callback {
        void doCallback(String[] args);
    }

    public void mainMethod(String[] args, Callback callback) {
        System.out.println("doing main...");
        callback.doCallback(args);
    }
}
