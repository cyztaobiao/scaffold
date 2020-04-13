package jujube.android.starter.mvvm;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Executor mainThreadHandler = new Executor() {

        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    };

    public static void execIO(Runnable command) {
        IO_EXECUTOR.execute(command);
    }

    public static <T> void execMain(final Task<T> task) {
        IO_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final T res = task.run();
                    mainThreadHandler.execute(new Runnable() {
                        @Override
                        public void run() {
                            task.handle(res);
                        }
                    });
                }catch (final Exception e) {
                    mainThreadHandler.execute(new Runnable() {
                        @Override
                        public void run() {
                            task.onError(e);
                        }
                    });
                }

            }
        });
    }

    public static void execUI(Runnable command) {
        mainThreadHandler.execute(command);
    }

    public interface Task<T> {
        T run() throws Exception;

        void handle(T res);

        void onError(Throwable t);
    }

    public interface Resolver<T> {
        void resolve(T t);
    }

}