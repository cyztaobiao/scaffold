package jujube.android.starter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import jujube.android.starter.recyclerview.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter<T> extends RecyclerThrowableAdapter {

    private boolean includeAll = false;

    public void setIncludeAll(boolean includeAll) {
        this.includeAll = includeAll;
    }

    private int layoutResId;
    private List<T> data;
    private int selectPos;
    private OnItemClickListener<T> itemClickListener;

    public FilterAdapter(int layoutResId) {
        this.data = new ArrayList<>();
        this.layoutResId = layoutResId;
    }

    public void setItemClickListener(OnItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void update(List<T> data) {
        this.data = data;
        if (!data.isEmpty()) {
            select(0);
        }
        notifyDataSetChanged();
    }

    public void select(int selectPos) {
        this.selectPos = selectPos;
        if (itemClickListener != null) {
            if (includeAll && selectPos == 0) {
                itemClickListener.onItemClick(null);
            }else {
                itemClickListener.onItemClick(data.get(includeAll ? selectPos - 1 : selectPos));
            }
        }
        notifyDataSetChanged();
    }

    public void select(T item) {
        select(data.indexOf(item));
        notifyDataSetChanged();
    }

    @Override
    protected void bind(@NonNull ViewHolder holder, final int position) {
        TextView filterTextView = (TextView) holder.itemView;

        if (includeAll && position == 0) {
            filterTextView.setText("全部");
        }else {
            final T item = data.get(includeAll ? position - 1 : position);
            filterTextView.setText(item.toString());
        }

        filterTextView.setSelected(position == selectPos);
        filterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(position);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
    }

    @Override
    public int getItemCount() {
        if (includeAll)
            return data.size() + 1;
        return data.size();
    }
}
