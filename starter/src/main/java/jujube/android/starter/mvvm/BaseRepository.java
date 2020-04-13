package jujube.android.starter.mvvm;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseRepository {

    private Disposable mHttpDisposable;
    private CompositeDisposable mCompositeDisposable;

    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    public void unDisposable() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }

    public void cancelHttpSubscribe() {
        if (mHttpDisposable != null && !mHttpDisposable.isDisposed()) {
            mHttpDisposable.dispose();
        }
    }

    public void httpSubscribe(Disposable disposable) {
        //新加的disposable已经在工作了，此时存在两个disposable
        cancelHttpSubscribe();
        if (disposable != null) {
            mHttpDisposable = disposable;
            addDisposable(disposable);
        }
    }
}