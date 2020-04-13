package jujube.android.starter.mvvm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class AbsLifecycleActivity<T extends AbsViewModel> extends AppCompatActivity {

    protected T mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityConfig();

        mViewModel = createViewModel();
        initView();
        dataObserve();
    }

    protected void initView() {
        setContentView(provideLayoutId());
    }

    protected abstract void activityConfig();

    protected abstract int provideLayoutId();

    protected abstract void dataObserve();

    protected abstract T createViewModel();
}
