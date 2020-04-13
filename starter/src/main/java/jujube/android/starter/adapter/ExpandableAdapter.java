package jujube.android.starter.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandableAdapter extends RecyclerThrowableAdapter {

    public static class Node {

        private Object target;
        private List<Node> children;

        private Status status;

        public Node(Object target, List<Node> children) {
            this.target = target;
            this.children = children;
        }

        public Object getTarget() {
            return target;
        }

        public List<Node> getChildren() {
            return children;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public boolean hasChildren() {
            return children != null;
        }

    }

    public static class Status {

        public boolean isShown;
        public boolean isExpand;
        public int level;

    }

    protected int pLayoutResId;
    protected int cLayoutResId;

    private List<Node> allNodes;
    private List<Node> visibleNodes;

    protected boolean defaultExpand;

    protected void defaultExpand() {
        this.defaultExpand = false;
    }

    public ExpandableAdapter(@LayoutRes int pLayoutResId, @LayoutRes int cLayoutResId) {
        this.pLayoutResId = pLayoutResId;
        this.cLayoutResId = cLayoutResId;
        allNodes = new ArrayList<>();
        visibleNodes = new ArrayList<>();
    }

    public void update(List<Node> roots) {
        allNodes = new ArrayList<>();
        for (Node node : roots) {
            addNode(allNodes, node, 0);
        }
        visibleNodes = filterVisible();
        notifyDataSetChanged();
    }

    private void addNode(List<Node> list, Node node, int level) {
        list.add(node);
        Status status = new Status();
        status.level = level;
        status.isExpand = defaultExpand;
        status.isShown = level == 0;
        node.setStatus(status);
        if (node.hasChildren()) {
            for (Node child: node.getChildren()) {
                addNode(list, child, level + 1);
            }
        }
    }

    private List<Node> filterVisible() {
        List<Node> visibleNodes = new ArrayList<>();
        for (Node node : allNodes) {
            Status status = node.getStatus();
            if (status.isShown) {
                visibleNodes.add(node);
            }
        }
        return visibleNodes;
    }

    public void expandOrCollapse(int pos) {
        Node node = visibleNodes.get(pos);
        node.getStatus().isExpand = !node.getStatus().isExpand;
        if (node.hasChildren()) {
            for (Node child : node.getChildren()) {
                Status status = child.getStatus();
                status.isShown = !status.isShown;
                if (status.isExpand) {
                    status.isExpand = false;
                }
                collapse(child);
            }
        }
        visibleNodes = filterVisible();
        notifyDataSetChanged();
    }

    private void collapse(Node node) {
        if (node.hasChildren()) {
            for (Node child : node.getChildren()) {
                Status status = child.getStatus();
                status.isShown = false;
                status.isExpand = false;
                collapse(child);
            }
        }
    }

    @Override
    protected void bind(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == 0)
            bindParent(holder, position);
        else
            bindChild(holder, position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(pLayoutResId, parent, false));
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(cLayoutResId, parent, false));
    }

    @Override
    public int getItemCount() {
        return visibleNodes.size();
    }

    @Override
    public int getItemViewType(int position) {
        Node node = visibleNodes.get(position);
        return node.hasChildren() ? 0 : 1;
    }

    public Node getItem(int position) {
        return visibleNodes.get(position);
    }

    public abstract void bindParent(@NonNull ViewHolder holder, int position);

    public abstract void bindChild(@NonNull ViewHolder holder, int position);
}
