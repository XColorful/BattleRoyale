package xiao.battleroyale.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ClassUtils {

    /**
     * O(1)查找
     * 索引访问
     * 简单快速遍历（内存相近）
     * 基本没有删除操作的情况下无List缺点
     * @param <T>
     */
    public static class ArraySet<T> implements Iterable<T> {

        private final List<T> list;
        private final Set<T> set;

        public ArraySet() {
            this.list = new ArrayList<>();
            this.set = new HashSet<>();
        }

        public boolean add(T element) {
            if (set.add(element)) {
                list.add(element);
                return true;
            }
            return false;
        }

        public boolean remove(T element) {
            if (set.remove(element)) {
                list.remove(element);
                return true;
            }
            return false;
        }

        public boolean contains(T element) {
            return set.contains(element);
        }

        public int size() {
            return list.size();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public void clear() {
            list.clear();
            set.clear();
        }

        public T get(int index) {
            return list.get(index);
        }

        public T set(int index, T element) {
            T oldElement = list.get(index);
            if (oldElement.equals(element)) {
                return oldElement; // 如果新旧元素相同，无需操作
            }

            // 尝试从 Set 中移除旧元素，并添加新元素
            if (set.remove(oldElement)) { // 确保旧元素被移除了
                if (set.add(element)) { // 尝试添加新元素
                    list.set(index, element); // 更新 List
                    return oldElement;
                } else {
                    // 新元素已存在于 Set 中，并且不是 oldElement，这是一个冲突
                    // 回滚 Set，并抛出异常或返回错误，因为无法满足唯一性要求
                    set.add(oldElement); // 将旧元素加回 Set
                    throw new IllegalArgumentException("Element " + element + " already exists in the set, conflicting with replacement.");
                }
            } else {
                // 旧元素不在 Set 中，这表明 list 和 set 已经不同步，是个内部错误
                throw new IllegalStateException("Internal error: old element " + oldElement + " not found in set. Data inconsistency detected.");
            }
        }

        public List<T> asList() {
            return new ArrayList<>(list);
        }

        public Set<T> asSet() {
            return new HashSet<>(set);
        }

        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }

        public boolean removeIf(Predicate<? super T> filter) {
            boolean removed = false;
            Iterator<T> listIterator = list.iterator();
            while (listIterator.hasNext()) {
                T element = listIterator.next();
                if (filter.test(element)) {
                    listIterator.remove(); // 从 list 中高效移除
                    set.remove(element); // 从 set 中移除
                    removed = true;
                }
            }
            return removed;
        }
    }

    /**
     * O(1)查找
     * O(1)入队出队
     * 不需要随机删除时应优于LinkedHashSet
     * @param <T>
     */
    public static class QueueSet<T> {
        private final Set<T> set;
        private final Queue<T> queue;

        public QueueSet() {
            this.set = new HashSet<>();
            this.queue = new ArrayDeque<>(); // 底层是循环数组，扩容不频繁的情况下优于链表
        }

        public boolean add(T element) {
            if (set.add(element)) {
                queue.add(element);
                return true;
            }
            return false;
        }

        public boolean contains(T element) {
            return set.contains(element);
        }

        public T poll() {
            T element = queue.poll();
            if (element != null) {
                set.remove(element);
            }
            return element;
        }

        public T peek() {
            return queue.peek();
        }

        public boolean remove(T element) {
            if (set.remove(element)) {
                // ArrayDeque.remove(Object) 是 O(N)
                return queue.remove(element);
            }
            return false;
        }

        public int size() {
            return queue.size();
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        public void clear() {
            set.clear();
            queue.clear();
        }

        public void removeOldest(int count) {
            int chunksToRemove = Math.min(count, size());
            for (int i = 0; i < chunksToRemove; i++) {
                poll();
            }
        }
    }

    /**
     * O(1)查找
     * O(1)索引访问
     * 几乎不需要修改内容的情况下没有List缺点
     * @param <K> 元素的键类型
     * @param <V> 元素的值类型
     */
    public static class ArrayMap<K, V> implements Iterable<V> {

        private final List<V> list;
        private final Map<K, V> map;
        private final Function<V, K> keyExtractor;

        /**
         * @param keyExtractor 一个函数，用于从值类型V中提取键类型K
         */
        public ArrayMap(Function<V, K> keyExtractor) {
            this.list = new ArrayList<>();
            this.map = new HashMap<>();
            this.keyExtractor = keyExtractor;
        }

        public boolean add(V value) {
            K key = keyExtractor.apply(value);
            if (!map.containsKey(key)) {
                map.put(key, value);
                list.add(value);
                return true;
            }
            return false;
        }

        public V put(K key, V value) {
            V oldValue = map.get(key);
            if (oldValue != null) { // 如果键已存在，更新值
                map.put(key, value);
                // 找到并替换列表中对应的旧值
                int index = list.indexOf(oldValue); // O(N) 操作
                if (index != -1) {
                    list.set(index, value);
                }
            } else { // 如果键不存在，则添加新键值对
                map.put(key, value);
                list.add(value);
            }
            return oldValue;
        }

        public void putAll(Map<? extends K, ? extends V> m) {
            clear(); // 先清空，再添加，确保列表和映射同步，且顺序取决于传入Map的迭代顺序
            for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
                // 调用内部的 put 方法，确保 Map 和 List 的同步更新
                put(entry.getKey(), entry.getValue());
            }
        }

        public V mapGet(K key) {
            return map.get(key);
        }

        public V listGet(int index) {
            return list.get(index);
        }

        public V remove(K key) {
            V value = map.remove(key);
            if (value != null) {
                list.remove(value); // List.remove(Object) 是 O(N)
                return value;
            }
            return null;
        }

        public boolean containsKey(K key) {
            return map.containsKey(key);
        }

        public int size() {
            return list.size();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public void clear() {
            list.clear();
            map.clear();
        }

        /**
         * 返回此 ArrayMap 中所有值的不可修改列表视图
         * 列表的顺序与元素被添加或更新的顺序一致
         */
        public List<V> asList() {
            return Collections.unmodifiableList(list);
        }

        /**
         * 返回此 ArrayMap 中所有键的不可修改 Set 视图
         */
        public Set<K> keySet() {
            return Collections.unmodifiableSet(map.keySet());
        }

        /**
         * 返回一个不可修改的 Map 视图，包含此 ArrayMap 中的所有键值对
         */
        public Map<K, V> asMap() {
            return Collections.unmodifiableMap(map);
        }

        public void sort(Comparator<V> comparator) {
            list.sort(comparator);
        }

        @Override
        public Iterator<V> iterator() {
            return list.iterator();
        }

        public boolean removeIf(Predicate<? super V> filter) {
            boolean removed = false;
            // 为了避免并发修改问题，先收集需要移除的键
            List<K> keysToRemove = new ArrayList<>();
            for (V value : list) {
                if (filter.test(value)) {
                    keysToRemove.add(keyExtractor.apply(value));
                }
            }

            for (K key : keysToRemove) {
                if (remove(key) != null) { // 调用 remove(K key) 确保同步移除
                    removed = true;
                }
            }
            return removed;
        }
    }
}