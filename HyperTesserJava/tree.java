import org.jetbrains.annotations.NotNull;

import java.util.*;

class TreeNode<T> {
    private final T value;
    private TreeNode<T> parent;
    private final List<TreeNode<T>> children;

    public TreeNode(T value) {
        this.value = value;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public T getValue() {
        return value;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void addChild(@NotNull TreeNode<T> child) {
        child.parent = this;
        children.add(child);
    }

    public boolean removeChild(TreeNode<T> child) {
        return children.remove(child);
    }

    public void removeSubtree() {
        if (parent != null) {
            parent.removeChild(this);
            parent = null;
        }
        children.clear();
    }
}

class Tree<T> implements Iterable<TreeNode<T>> {
    final TreeNode<T> root;

    public Tree(T rootvalue) {
        this.root = new TreeNode<>(rootvalue);
    }

    public boolean removeNode(@NotNull TreeNode<T> node) {
        if (node.getParent() != null) {
            node.removeSubtree();
            return true;
        }
        else {return false;}
    }

    public Iterator<TreeNode<T>> iterator() {
        return new TreeIterator<>(root);
    }

    private static class TreeIterator<T> implements Iterator<TreeNode<T>> {
        private final Queue<TreeNode<T>> queue;

        public TreeIterator(TreeNode<T> root) {
            this.queue = new LinkedList<>();
            this.queue.add(root);
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public @NotNull TreeNode<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            TreeNode<T> current = queue.poll();
            queue.addAll(current.getChildren());
            return current;
        }
    }
}