import org.jetbrains.annotations.NotNull;

import java.util.*;

class TrieNode<T> {
    Map<Character, TrieNode<T>> children;
    boolean isEndOfWord;
    T body;

    public TrieNode(T ibody) {
        children = new HashMap<>();
        isEndOfWord = false;
        body = ibody;
    }
}

public class Trie<T> implements Iterable<String> {
    final TrieNode<T> root;

    public Trie(T rootvalue) {
        root = new TrieNode<>(rootvalue);
    }

    public void insert(@NotNull String word, T value) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode<>(value));
            node = node.children.get(ch);
        }
        node.isEndOfWord = true;
    }

    public boolean search(@NotNull String word) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return false;
            }
            node = node.children.get(ch);
        }
        return node.isEndOfWord;
    }
    public T begsearch(@NotNull String word) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            if (node.isEndOfWord){return node.body;}
            if (!node.children.containsKey(ch)) {
                return root.body;
            }
            node = node.children.get(ch);
        }
        return root.body;
    }
    public T getbody(@NotNull String word) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.get(ch);
        }
        return node.body;
    }

    public boolean startsWith(@NotNull String prefix) {
        TrieNode<T> node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return false;
            }
            node = node.children.get(ch);
        }
        return true;
    }

    public @NotNull Iterator<String> iterator() {
        return new TrieIterator<>(root);
    }

    private static class TrieIterator<T> implements Iterator<String> {
        private final Queue<TPair<T>> queue;

        public TrieIterator(TrieNode<T> root) {
            queue = new LinkedList<>();
            queue.offer(new TPair<>(root, ""));
        }

        @Override
        public boolean hasNext() {
            while (!queue.isEmpty()) {
                TPair<T> front = queue.peek();
                if (front.node.isEndOfWord) {
                    return true;
                }
                queue.poll();
                for (Map.Entry<Character, TrieNode<T>> entry : front.node.children.entrySet()) {
                    queue.offer(new TPair<>(entry.getValue(), front.word + entry.getKey()));
                }
            }
            return false;
        }

        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            TPair<T> front = queue.poll();
            assert front != null;
            for (Map.Entry<Character, TrieNode<T>> entry : front.node.children.entrySet()) {
                queue.offer(new TPair<>(entry.getValue(), front.word + entry.getKey()));
            }
            return front.word;
        }

        record TPair<T>(TrieNode<T> node,String word){}
    }
}
