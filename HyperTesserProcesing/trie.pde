class TrieNode<T> {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;
    T Body;
    
    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
}

public class Trie<T> {
    private final TrieNode<T> root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word,T body) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.Body=body;
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
    T get(String word) {
        TrieNode<T> node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.get(ch);
        }
        return node.Body;
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
    
    private static class TrieIterator implements Iterator<String> {
        private final Queue<Pair> queue;

        public TrieIterator(TrieNode root) {
            queue = new LinkedList<>();
            queue.offer(new Pair(root, ""));
        }

        @Override
        public boolean hasNext() {
            while (!queue.isEmpty()) {
                Pair front = queue.peek();
                if (front.node.isEndOfWord) {
                    return true;
                }
                queue.poll();
                for (Map.Entry<Character, TrieNode> entry : front.node.children.entrySet()) {
                    queue.offer(new Pair(entry.getValue(), front.word + entry.getKey()));
                }
            }
            return false;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Pair front = queue.poll();
            for (Map.Entry<Character, TrieNode> entry : front.node.children.entrySet()) {
                queue.offer(new Pair(entry.getValue(), front.word + entry.getKey()));
            }
            return front.word;
        }

        private static class Pair {
            TrieNode node;
            String word;

            Pair(TrieNode node, String word) {
                this.node = node;
                this.word = word;
            }
        }
    }
}
