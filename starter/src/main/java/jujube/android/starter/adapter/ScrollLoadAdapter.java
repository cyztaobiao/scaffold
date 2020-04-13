package jujube.android.starter.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class ScrollLoadAdapter<T> extends RecyclerThrowableAdapter {

    protected static final int TYPE_NORMAL = 0;     // 第一种ViewType，正常的item
    protected static final int TYPE_FOOTER = 1;       // 第二种ViewType，底部的提示View

    protected List<T> data; // 数据源
    private boolean hasMore;   // 变量，是否有更多数据
    private boolean hideTips; // 变量，是否隐藏了底部的提示

    public interface OnScrollListener {
        void onScrollToLoad(int lastItemPosition);
    }

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public ScrollLoadAdapter(RecyclerView recyclerView) {
        this(recyclerView, new ArrayList<T>(), false);
    }

    public ScrollLoadAdapter(final RecyclerView recyclerView, List<T> data, boolean hasMore) {
        // 初始化变量
        this.data = data;
        this.hasMore = hasMore;
        this.hideTips = false;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int lastVisibleItem = -1;
            private LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isHideTips() && lastVisibleItem + 1 == getItemCount()) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onScrollListener != null)
                                    onScrollListener.onScrollToLoad(getRealLastPosition());
                            }
                        });
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }
    
    // 获取条目数量，之所以要加1是因为增加了一条footView
    @Override
    public int getItemCount() {
        return data.size() + 1;
    }
    
    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return data.size();
    }


    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    protected abstract void onBindNormalViewHolder(@NonNull ViewHolder holder, int position);

    protected abstract void onLoadingMore(ViewHolder holder);

    protected abstract void onLoadNoMore(ViewHolder holder);

    @Override
    protected void bind(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            if (hasMore) {
                onLoadingMore(holder);
                hideTips = false;
            }else {
                onLoadNoMore(holder);
                hideTips = true;
            }
        }else {
            onBindNormalViewHolder(holder, position);
        }
    }

    // 暴露接口，改变fadeTips的方法
    public boolean isHideTips() {
        return hideTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void reset() {
        data = new ArrayList<>();
        notifyDataSetChanged();
    }
    
    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<T> newData, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newData != null) {
            data.addAll(newData);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return this.data;
    }

}