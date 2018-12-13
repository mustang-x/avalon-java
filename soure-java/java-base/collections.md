### HashMap

- put()

```
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab;  // table 数组
        Node<K,V> p; // node 节点1
        int n, i;
        /**
        * table 是全局变量table数组
        */
        // 判断map容器是否初始化过 并把tab.length赋值给变量n
        if ((tab = table) == null || (n = tab.length) == 0) 
            n = (tab = resize()).length; // 如果没有就调用resize()方法进行初始化
        // 取得tab.length长度-1 & hash 下标的数组的node并赋予p(节点1)，如果是null 新节点放入改下标的数组；    
        if ((p = tab[i = (n - 1) & hash]) == null) 
            tab[i] = newNode(hash, key, value, null); // 直接放入新node
        else {
            Node<K,V> e; // node 节点2
            K k; // key
            // key一样 如果拿出来的哈希值与现在的哈希值一致并且equals一致 即key是一样的
            if (p.hash == hash && 
                ((k = p.key) == key || (key != null && key.equals(k)))) 
                e = p; // 从数组拿出来的node赋值给e value直接替换
            // 属于TreeNode 判断拿出来的node是否属于TreeNode
            else if (p instanceof TreeNode) 
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            // key不一致并且也不是TreeNode(即：即使是链表也没有超过8个碰撞的)    
            else {
                // for循环 一次拿出链表里面的node
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null); // 放入新node
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash); // 转红黑树
                        break;
                    }
                    // key是一致， 
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) // 
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount; // 快速失败机制
        if (++size > threshold)
            resize(); // 扩容
        afterNodeInsertion(evict);
        return null;
    }
```

------

- key 可以使用任何对象作为key，只要遵守equals()和hashCode()定义的规则；并且当对象插入Map中之后将不会改变；
- get和put原理，equals()和hashCode()的作用 通过对key的hashCode()进行hashing，并计算下标(n-1 & hash)，从而获得buckets的位置。如果产生碰撞，则利用key.queal()方法去链表或者树种查找对应的节点。
- String，Integer这样的wrapper类比较适合作为key，因为重写了equals()和hashCode()方法，不可变性是必要的；减少碰撞几率，提高性能；h & (length-1)这种取模用位运算的方式比较快，这个需要数组的大小永远是2的N次方来保证其有效性。