package jujube.android.demos.hook;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jujube.android.demos.R;

/**
 * 通过反射增加自定义函数到sqlite 关键在于接口实例化参考{@link CallbackInstance}
 */

public class SqliteHookActivity extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/android-demos/app.db", null);

        try {
            Class<?> clazz = database.getClass();
            Class<?> callback = Class.forName("android.database.sqlite.SQLiteDatabase$CustomFunction");

            FunHandler handler = new FunHandler();
            Object callbackObj = Proxy.newProxyInstance(getClassLoader(), new Class[]{callback}, handler);

            Method method = clazz.getDeclaredMethod("addCustomFunction", new Class[]{String.class, int.class, callback});
            method.invoke(database, new Object[]{"fun", 0, callbackObj});
        }catch (Exception e) {
            e.printStackTrace();
        }

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                try {
                    database.rawQuery("select fun()", new String[]{}).close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static class FunHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.e("TAG", method.getName() + " doing callback...");
            for (Object arg : args)
                Log.e("TAG", arg.toString());
            return method.toString();
        }
    }

}