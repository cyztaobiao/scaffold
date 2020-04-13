package jujube.android.starter.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerThrowableAdapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        try {
            bind(holder, position);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void bind(@NonNull VH holder, int position) throws Exception;
}
