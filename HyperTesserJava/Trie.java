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
    private final TrieNode<T> root;

    public Trie(T rootvalue) {
        root = new TrieNode<>(rootvalue);
    }

    public void insert(String word,T value) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode<>(value));
            node = node.children.get(ch);
        }
        node.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return false;
            }
            node = node.children.get(ch);
        }
        return node.isEndOfWord;
    }
    public T getbody(String word) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.get(ch);
        }
        return node.body;
    }

    public boolean startsWith(String prefix) {
        TrieNode<T> node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return false;
            }
            node = node.children.get(ch);
        }
        return true;
    }

    @Override
    public Iterator<String> iterator() {
        return new TrieIterator<>(root);
    }

    private static class TrieIterator<T> implements Iterator<String> {
        private final Queue<Pair<T>> queue;

        public TrieIterator(TrieNode<T> root) {
            queue = new LinkedList<>();
            queue.offer(new Pair<>(root, ""));
        }

        @Override
        public boolean hasNext() {
            while (!queue.isEmpty()) {
                Pair<T> front = queue.peek();
                if (front.node.isEndOfWord) {
                    return true;
                }
                queue.poll();
                for (Map.Entry<Character, TrieNode<T>> entry : front.node.children.entrySet()) {
                    queue.offer(new Pair<>(entry.getValue(), front.word + entry.getKey()));
                }
            }
            return false;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Pair<T> front = queue.poll();
            for (Map.Entry<Character, TrieNode<T>> entry : front.node.children.entrySet()) {
                queue.offer(new Pair<>(entry.getValue(), front.word + entry.getKey()));
            }
            return front.word;
        }

        private static class Pair<T> {
            TrieNode<T> node;
            String word;

            Pair(TrieNode<T> node, String word) {
                this.node = node;
                this.word = word;
            }
        }
    }
}
