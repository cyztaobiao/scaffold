package jujube.android.starter.mvvm;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;

public class ViewModelFactory<R extends BaseRepository> extends ViewModelProvider.NewInstanceFactory {

    private R mRepository;

    public ViewModelFactory(R repository) {
        this.mRepository = repository;
    }

    public static <T extends BaseRepository> ViewModelFactory<T> factory(T repository) {
        return new ViewModelFactory<>(repository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(mRepository.getClass()).newInstance(mRepository);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return super.create(modelClass);
    }
}
