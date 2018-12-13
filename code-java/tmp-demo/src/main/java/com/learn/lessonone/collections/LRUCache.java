package com.learn.lessonone.collections;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: LinkedHashMap 简单实现LRU算法
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/6 20:07
 */
public class LRUCache<K, V> extends LinkedHashMap<K,V> {

    // 负载因子
    private static final int MAX_ENTRIES = 3;

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_ENTRIES;
    }

    LRUCache() {
        super(MAX_ENTRIES, 0.75f, true);
    }

    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>();
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        System.out.println(cache.keySet());
        System.out.println(cache.get(1));
        System.out.println(cache.keySet()); // 调用到头节点
        String key = cache.put(4, "d"); // 删除尾节点 removeEldestEntry() 返回true
        System.out.println(key);
        System.out.println(cache.keySet());
    }
}