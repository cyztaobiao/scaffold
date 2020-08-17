package jujube.android.starter.adapter.multilevel;

import java.util.List;

public class Node {

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
