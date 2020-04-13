package jujube.android.starter.mvvm;

import androidx.lifecycle.ViewModel;

public abstract class AbsViewModel<T extends BaseRepository> extends ViewModel {


    protected T mRepository;

    public AbsViewModel(T repository) {
        this.mRepository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mRepository != null) {
            mRepository.unDisposable();
        }
    }

}
