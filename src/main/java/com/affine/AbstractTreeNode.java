package com.affine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jore.Assert;

public abstract class AbstractTreeNode<T> {
    private List<T> children = new ArrayList<T>();
    private T parent;

    public AbstractTreeNode() {
    }

    @SuppressWarnings("unchecked")
    public boolean addChild(T child) {
        ((AbstractTreeNode<T>) child).setParent((T) this);
        return children.add(child);
    }

    public List<T> children() {
        return Collections.unmodifiableList(children);
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public T getChildAt(int index) {
        return children.get(index);
    }

    public int getChildCount() {
        return children.size();
    }

    public int getIndex(T node) {
        return children.indexOf(node);
    }

    public T getLastChild() {
        return children.size() > 0 ? children.get(children.size() - 1) : null;
    }

    public T getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public boolean isRoot() {
        return parent == null;
    }

    @SuppressWarnings("unchecked")
    public boolean isSibling(T node) {
        Assert.notNull("Parent node must not be null for sibling check.", getParent());
        return getParent().equals(((AbstractTreeNode<T>) node).getParent());
    }

    public Iterator<T> iterator() {
        return children.iterator();
    }

    public void removeChild(T objectToDelete) {
        children.remove(objectToDelete);
    }

    public void setParent(T parent) {
        this.parent = parent;
    }
}
