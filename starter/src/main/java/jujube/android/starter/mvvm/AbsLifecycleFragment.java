package jujube.android.starter.mvvm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class AbsLifecycleFragment<T extends AbsViewModel> extends Fragment {

    protected View mRootView;

    protected T mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerComponent();
        if (mRootView == null) {
            mViewModel = createViewModel();
            mRootView = inflater.inflate(provideLayoutId(), container, false);
            initView();
            dataObserve();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterComponent();
    }

    protected abstract int provideLayoutId();

    protected abstract void registerComponent();

    protected abstract void unregisterComponent();

    protected abstract void initView();

    protected abstract void dataObserve();

    protected abstract T createViewModel();


}
