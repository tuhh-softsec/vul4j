/*
 * J.A.D.E. Java(TM) Addition to Default Environment.
 * Latest release available at http://jade.dautelle.com/
 * This class is public domain (not copyrighted).
 */
package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p> This class represents a <code>Map</code> collection with real-time 
 *     behavior. Unless the map's size exceeds its current capacity, 
 *     no dynamic memory allocation is ever performed and response time is 
 *     <b>extremely fast</b> and <b>consistent</b>.</p>
 *  
 * <p> Our <a href="http://jade.dautelle.com/doc/benchmark.txt">benchmark</a> 
 *     indicates that {@link FastMap#put FastMap.put(key, value)} is up to 
 *     <b>5x faster</b> than <code>java.util.HashMap.put(key, value)</code>.
 *     This difference is mostly due to the cost of the <code>Map.Entry</code>
 *     allocations that {@link FastMap} avoids by recycling its entries 
 *     (see note below).</p>
 * 
 * <p> {@link FastMap} has a predictable iteration order, which is the order
 *     in which keys were inserted into the map (similar to 
 *     <code>java.util.LinkedHashMap</code> collection class).</p>
 * 
 * <p> Applications may change the resizing policy  of {@link FastMap} 
 *     by overriding the {@link #sizeChanged} method. For example, to improve
 *     predictability, automatic resizing can be disabled.</p>  
 *
 * <p> This implementation is not synchronized. Multiple threads accessing
 *     or modifying the collection must be synchronized externally.</p>
 * 
 * <p> <b>Note:</b> To avoid dynamic memory allocations, {@link FastMap}
 *     maintains an internal pool of <code>Map.Entry</code> objects. The size
 *     of the pool is determined by the map's capacity. When an entry is
 *     removed from the map, it is automatically restored to the pool.</p>
 * 
 * <p><i> This class is <b>public domain</b> (not copyrighted).</i></p>
 *  
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.3, October 31 2003
 */
public class FastMap implements Map, Cloneable, Serializable {

    /**
     * Holds the map's hash table.
     */
    private transient EntryImpl[] _entries;

    /**
     * Holds the map's current capacity.
     */
    private transient int _capacity;

    /**
     * Holds the hash code mask.
     */
    private transient int _mask;

    /**
     * Holds the first pool entry (linked list).
     */
    private transient EntryImpl _poolFirst;

    /**
     * Holds the first map entry (linked list).
     */
    private transient EntryImpl _mapFirst;

    /**
     * Holds the last map entry (linked list).
     */
    private transient EntryImpl _mapLast;

    /**
     * Holds the current size.
     */
    private transient int _size;

    /**
     * Creates a {@link FastMap} with a capacity of <code>256</code> entries.
     */
    public FastMap() {
        initialize(256);
    }
    
    /**
     * Creates a {@link FastMap}, copy of the specified <code>Map</code>.
     * If the specified map is not an instance of {@link FastMap}, the 
     * newly created map has a capacity set to the specified map's size.
     * The copy has the same order as the original, regardless of the original 
     * map's implementation:<pre>
     *     TreeMap dictionary = ...;
     *     FastMap dictionaryLookup = new FastMap(dictionary);
     * </pre>
     * 
     * @param  map the map whose mappings are to be placed in this map.
     */
    public FastMap(Map map) {
        int capacity = (map instanceof FastMap) ? 
            ((FastMap)map).capacity() : map.size();
        initialize(capacity);
        putAll(map); 
    }

    /**
     * Creates a {@link FastMap} with the specified capacity. Unless the 
     * capacity is exceeded, operations on this map do not allocate entries.
     * For optimum performance, the capacity should be of the same order 
     * of magnitude or larger than the expected map's size.
     * 
     * @param  capacity the number of buckets in the hash table; it also 
     *         defines the number of pre-allocated entries.
     */
    public FastMap(int capacity) {
        initialize(capacity);
    }

    /**
     * Returns the number of key-value mappings in this {@link FastMap}. 
     *
     * @return this map's size.
     */
    public int size() {
        return _size;
    }

    /**
     * Returns the capacity of this {@link FastMap}. The capacity defines
     * the number of buckets in the hash table, as well as the maximum number
     * of entries the map may contain without allocating memory.
     *
     * @return this map's capacity.
     */
    public int capacity() {
        return _capacity;
    }

    /**
     * Indicates if this {@link FastMap} contains no key-value mappings.
     *
     * @return <code>true</code> if this map contains no key-value mappings;
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return _size == 0;
    }

    /**
     * Indicates if this {@link FastMap} contains a mapping for the specified
     * key.
     *
     * @param   key the key whose presence in this map is to be tested.
     * @return <code>true</code> if this map contains a mapping for the
     *         specified key; <code>false</code> otherwise.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public boolean containsKey(Object key) {
        EntryImpl entry = _entries[keyHash(key) & _mask];
        while (entry != null) {
            if (key.equals(entry._key) ) {
                return true;
            }
            entry = entry._next;
        }
        return false;  
    }

    /**
     * Indicates if this {@link FastMap} maps one or more keys to the
     * specified value.
     *
     * @param  value the value whose presence in this map is to be tested.
     * @return <code>true</code> if this map maps one or more keys to the
     *         specified value.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public boolean containsValue(Object value) {
        EntryImpl entry = _mapFirst;
        while (entry != null) {
            if (value.equals(entry._value) ) {
                return true;
            }
            entry = entry._after;
        }
        return false;  
    }

    /**
     * Returns the value to which this {@link FastMap} maps the specified key. 
     *
     * @param  key the key whose associated value is to be returned.
     * @return the value to which this map maps the specified key,
     *         or <code>null</code> if there is no mapping for the key.
     * @throws NullPointerException if key is <code>null</code>.
     */
    public Object get(Object key) {
        EntryImpl entry = _entries[keyHash(key) & _mask];
        while (entry != null) {
            if (key.equals(entry._key) ) {
                return entry._value;
            }
            entry = entry._next;
        }
        return null;  
    }
    
    /**
     * Returns the entry with the specified key. 
     * 
     * @param key the key whose associated entry is to be returned.
     * @return the entry for the specified key or <code>null</code> if none.
     */
    public Map.Entry getEntry(Object key) {
        EntryImpl entry = _entries[keyHash(key) & _mask];
        while (entry != null) {
            if (key.equals(entry._key)) {
                return entry;
            }
            entry = entry._next;
        }
        return null;  
    }

    /**
     * Associates the specified value with the specified key in this 
     * {@link FastMap}. If the {@link FastMap} previously contained a mapping
     * for this key, the old value is replaced.
     *
     * @param  key the key with which the specified value is to be associated.
     * @param  value the value to be associated with the specified key.
     * @return the previous value associated with specified key,
     *         or <code>null</code> if there was no mapping for key.
     *         A <code>null</code> return can also indicate that the map 
     *         previously associated <code>null</code> with the specified key.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public Object put(Object key, Object value) {
        EntryImpl entry = _entries[keyHash(key) & _mask];
        while (entry != null) {
            if (key.equals(entry._key) ) {
                Object prevValue = entry._value; 
                entry._value = value;
                return prevValue;
            }
            entry = entry._next;
        }
        // No previous mapping.
        addEntry(key, value);
        return null;
    }

    /**
     * Copies all of the mappings from the specified map to this 
     * {@link FastMap}.
     *
     * @param  map the mappings to be stored in this map.
     * @throws NullPointerException the specified map is <code>null</code>, or
     *         the specified map contains <code>null</code> keys.
     */
    public void putAll(Map map) {
        for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            addEntry(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the mapping for this key from this {@link FastMap} if present.
     *
     * @param  key the key whose mapping is to be removed from the map.
     * @return previous value associated with specified key,
     *         or <code>null</code> if there was no mapping for key.
     *         A <code>null</code> return can also indicate that the map 
     *         previously associated <code>null</code> with the specified key.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public Object remove(Object key) {
        EntryImpl entry = _entries[keyHash(key) & _mask];
        while (entry != null) {
            if (key.equals(entry._key) ) {
                Object prevValue = entry._value; 
                removeEntry(entry);
                return prevValue;
            }
            entry = entry._next;
        }
        return null;
    }

    /**
     * Removes all mappings from this {@link FastMap}.
     */
    public void clear() {
        // Clears all keys, values and buckets linked lists.
        for (EntryImpl entry = _mapFirst; entry != null; entry = entry._after) {
            entry._key = null;
            entry._value = null;
            entry._before = null;
            entry._next = null;
            if (entry._previous == null) { // First in bucket.
                _entries[entry._index] = null;
            } else {
                entry._previous = null;
            }
        }
        
        // Recycles all entries.
        if (_mapLast != null) {
            _mapLast._after = _poolFirst; // Connects to pool.
            _poolFirst = _mapFirst;
            _mapFirst = null;
            _mapLast = null;
            _size = 0;
            sizeChanged();
        }
    }

    /**
     * Changes the current capacity of this {@link FastMap}. If the capacity
     * is increased, new entries are allocated and added to the pool. 
     * If the capacity is decreased, entries from the pool are deallocated 
     * (and are eventually garbage collected). The capacity also determined 
     * the number of buckets for the hash table.
     * 
     * @param newCapacity the new capacity of this map.
     */
    public void setCapacity(int newCapacity) {
        if (newCapacity > _capacity) { // Capacity increases.
            for (int i = _capacity; i < newCapacity; i++) {
                EntryImpl entry = new EntryImpl();
                entry._after = _poolFirst;
                _poolFirst = entry;
            }
        } else if (newCapacity < _capacity) { // Capacity decreases.
            for (    int i = newCapacity; 
                     (i < _capacity) && (_poolFirst != null); i++) {
                // Disconnects the entry for gc to do its work.
                EntryImpl entry = _poolFirst;
                _poolFirst = entry._after;
                entry._after = null; // All pointers are now null!
            }
        }
        // Find a power of 2 >= capacity
        int tableLength = 16;
        while (tableLength < newCapacity) {
            tableLength <<= 1;
        }
        // Checks if the hash table has to be re-sized.
        if (_entries.length != tableLength) {
            _entries = new EntryImpl[tableLength];
            _mask = tableLength - 1;
            
            // Repopulates the hash table.
            EntryImpl entry = _mapFirst;
            while (entry != null) {
                int index = keyHash(entry._key) & _mask;
                entry._index = index;
        
                // Connects to bucket.
                entry._previous = null; // Resets previous.
                EntryImpl next = _entries[index];
                entry._next = next;
                if (next != null) {
                    next._previous = entry;
                }
                _entries[index] = entry;
                
                entry = entry._after;
            }
        }
        _capacity = newCapacity;
    }

    /**
     * Returns a shallow copy of this {@link FastMap}. The keys and
     * the values themselves are not cloned.
     *
     * @return a shallow copy of this map.
     */
    public Object clone() {
        try {
            FastMap clone = (FastMap) super.clone();
            clone.initialize(_capacity);
            clone.putAll(this);
            return clone;
        } catch (CloneNotSupportedException e) { 
            // Should not happen, since we are Cloneable.
            throw new InternalError();
        }
    }

    /**
     * Compares the specified object with this {@link FastMap} for equality. 
     * Returns <code>true</code> if the given object is also a map and the two
     * maps represent the same mappings (regardless of collection iteration
     * order).
     *
     * @param obj the object to be compared for equality with this map.
     * @return <code>true</code> if the specified object is equal to this map;
     *         <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Map) {
            Map that = (Map) obj;
            if (this.size() == that.size()) {
                EntryImpl entry = _mapFirst;
                while (entry != null) {
                    if (!that.entrySet().contains(entry)) {
                        return false;
                    }
                    entry = entry._after;
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code value for this {@link FastMap}.
     *
     * @return the hash code value for this map.
     */
    public int hashCode() {
        int code = 0;
        EntryImpl entry = _mapFirst;
        while (entry != null) {
            code += entry.hashCode();
            entry = entry._after;
        }
        return code;
    }

    /**
     * Returns a <code>String</code> representation of this {@link FastMap}.
     *
     * @return <code>this.entrySet().toString();</code>
     */
    public String toString() {
        return entrySet().toString();
    }

    /**
     * Returns a collection view of the values contained in this 
     * {@link FastMap}.  The collection is backed by the map, so changes to
     * the map are reflected in the collection, and vice-versa. 
     * The collection supports element removal, which removes the corresponding
     * mapping from this map, via the
     * <code>Iterator.remove</code>, <code>Collection.remove</code>,
     * <code>removeAll</code>, <code>retainAll</code>, 
     * and <code>clear</code> operations. It does not support the 
     * <code>add</code> or <code>addAll</code> operations.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection values() {
        return _values;
    }
    private transient Values _values;
    private class Values extends AbstractCollection {
        public Iterator iterator() {
            return new Iterator() {
                EntryImpl after = _mapFirst;
                EntryImpl before;
                public void remove() {
                    removeEntry(before);
                }
                public boolean hasNext() {
                    return after != null;
                }
                public Object next() {
                    before = after;
                    after = after._after;
                    return before._value;
                }
            };
        }
        public int size() {
            return _size;
        }
        public boolean contains(Object o) {
            return containsValue(o);
        }
        public void clear() {
            FastMap.this.clear();
        }
    }

    /**
     * Returns a collection view of the mappings contained in this
     * {@link FastMap}. Each element in the returned collection is a
     * <code>Map.Entry</code>.  The collection is backed by the map,
     * so changes to the map are reflected in the collection, and vice-versa. 
     * The collection supports element removal, which removes the corresponding
     * mapping from this map, via the
     * <code>Iterator.remove</code>, <code>Collection.remove</code>,
     * <code>removeAll</code>, <code>retainAll</code>, 
     * and <code>clear</code> operations. It does not support the 
     * <code>add</code> or <code>addAll</code> operations.
     *
     * @return a collection view of the mappings contained in this map.
     */
    public Set entrySet() {
        return _entrySet;
    }
    private transient EntrySet _entrySet;
    private class EntrySet extends AbstractSet {
        public Iterator iterator() {
            return new Iterator() {
                EntryImpl after = _mapFirst;
                EntryImpl before;
                public void remove() {
                    removeEntry(before);
                }
                public boolean hasNext() {
                    return after != null;
                }
                public Object next() {
                    before = after;
                    after = after._after;
                    return before;
                }
            };
        }
        public int size() {
            return _size;
        }
        public boolean contains(Object obj) { // Optimization.
            if (obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) obj;
                Map.Entry mapEntry = getEntry(entry.getKey());
                return entry.equals(mapEntry);
            } else {
                return false;
            } 
        }
        public boolean remove(Object obj) { // Optimization.
            if (obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)obj;
                EntryImpl mapEntry = (EntryImpl) getEntry(entry.getKey());
                if      ((mapEntry != null) && 
                        (entry.getValue()).equals(mapEntry._value)) {
                    removeEntry(mapEntry);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Returns a set view of the keys contained in this {@link FastMap}. 
     * The set is backed by the map, so changes to the map are reflected 
     * in the set, and vice-versa.  The set supports element removal,
     * which removes the corresponding mapping from this map, via the
     * <code>Iterator.remove</code>, <code>Collection.remove</code>,
     * <code>removeAll</code>, <code>retainAll</code>, 
     * and <code>clear</code> operations. It does not support the 
     * <code>add</code> or <code>addAll</code> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    public Set keySet() {
        return _keySet;
    }
    private transient KeySet _keySet;
    private class KeySet extends AbstractSet {
        public Iterator iterator() {
            return new Iterator() {
                EntryImpl after = _mapFirst;
                EntryImpl before;
                public void remove() {
                    removeEntry(before);
                }
                public boolean hasNext() {
                    return after != null;
                }
                public Object next() {
                    before = after;
                    after = after._after;
                    return before._key;
                }
            };
        }
        public int size() {
            return _size;
        }
        public boolean contains(Object obj) { // Optimization.
            return FastMap.this.containsKey(obj);
        }
        public boolean remove(Object obj) { // Optimization.
            return FastMap.this.remove(obj) != null;
        }
        public void clear() { // Optimization.
            FastMap.this.clear();
        }
    }
    
    /**
     * This methods is being called when the size of this {@link FastMap} 
     * has changed. The default behavior is to double the map's capacity 
     * when the map's size reaches the current map's capacity.
     * Sub-class may override this method to implement custom resizing 
     * policies or to disable automatic resizing. For example:<pre>
     *     Map fixedCapacityMap = new FastMap(256) { 
     *           protected sizeChanged() {
     *               // Do nothing, automatic resizing disabled.
     *           }
     *     };</pre>
     * @see #setCapacity
     */
    protected void sizeChanged() {
        if (size() > capacity()) {
            setCapacity(capacity() * 2);
        }
    }
    
    /**
     * Returns the hash code for the specified key. The formula being used 
     * is identical to the formula used by <code>java.util.HashMap</code>
     * (ensures similar behavior for ill-conditioned hashcode keys).
     * 
     * @param key the key to calculate the hashcode for.
     * @return the hash code for the specified key.
     */
    private static int keyHash(Object key) {
        // From HashMap.hash(Object) function.
        int hashCode = key.hashCode();
        hashCode += ~(hashCode << 9);
        hashCode ^=  (hashCode >>> 14);
        hashCode +=  (hashCode << 4);
        hashCode ^=  (hashCode >>> 10);
        return hashCode;
    }
        
    /**
     * Adds a new entry for the specified key and value.
     * @param key the entry's key.
     * @param value the entry's value.
     */
    private void addEntry(Object key, Object value) {
        EntryImpl entry = _poolFirst;
        if (entry != null) {
            _poolFirst = entry._after;
            entry._after = null;
        } else { // Pool empty.
            entry = new EntryImpl();
        }
        
        // Setup entry paramters.
        entry._key = key;
        entry._value = value;
        int index = keyHash(key) & _mask;
        entry._index = index;
        
        // Connects to bucket.
        EntryImpl next = _entries[index];
        entry._next = next;
        if (next != null) {
            next._previous = entry;
        }
        _entries[index] = entry;
        
        // Connects to collection.
        if (_mapLast != null) {
            entry._before = _mapLast;
            _mapLast._after = entry;
        } else {
            _mapFirst = entry;
        }
        _mapLast = entry;
        
        // Updates size.
        _size++;
        sizeChanged();
    }

    /**
     * Removes the specified entry from the map.
     * 
     * @param entry the entry to be removed.
     */
    private void removeEntry(EntryImpl entry) {
        
        // Removes from bucket.
        EntryImpl previous = entry._previous;
        EntryImpl next = entry._next;
        if (previous != null) {
            previous._next = next;
            entry._previous = null;
        } else { // First in bucket.
            _entries[entry._index] = next;
        }
        if (next != null) { 
            next._previous = previous;
            entry._next = null;
        } // Else do nothing, no last pointer.
        
        // Removes from collection.
        EntryImpl before = entry._before;
        EntryImpl after = entry._after;
        if (before != null) { 
            before._after = after;
            entry._before = null;
        } else { // First in collection.
            _mapFirst = after;
        }
        if (after != null) { 
            after._before = before;
        } else { // Last in collection.
            _mapLast = before;
        }

        // Clears value and key.
        entry._key = null;
        entry._value = null;

        // Recycles.
        entry._after = _poolFirst;
        _poolFirst = entry;
        
        // Updates size.
        _size--;
        sizeChanged();
    }

    /**
     * Initializes this instance for the specified capacity.
     * Once initialized, operations on this map should not create new objects 
     * (unless the map's size exceeds the specified capacity). 
     *  
     * @param capacity the initial capacity.
     */
    private void initialize(int capacity) {
        // Find a power of 2 >= capacity
        int tableLength = 16;
        while (tableLength < capacity) {
            tableLength <<= 1;
        }
        // Allocates hash table.
        _entries = new EntryImpl[tableLength];
        _mask = tableLength - 1;
        _capacity = capacity;
        _size = 0;
        // Allocates views.
        _values = new Values();
        _entrySet = new EntrySet();
        _keySet = new KeySet();
        // Resets pointers.
        _poolFirst = null;
        _mapFirst = null;
        _mapLast = null;
        // Allocates entries.
        for (int i=0; i < capacity; i++) {
           EntryImpl entry = new EntryImpl();
           entry._after = _poolFirst;
           _poolFirst = entry;
        }
    }
    
    /**
     * Requires special handling during de-serialization process.
     *
     * @param  stream the object input stream.
     * @throws IOException if an I/O error occurs.
     * @throws ClassNotFoundException if the class for the object de-serialized
     *         is not found.
     */
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        int capacity = stream.readInt();
        initialize(capacity);
        int size = stream.readInt();
        for (int i=0; i < size; i++) {
            Object key = stream.readObject();
            Object value = stream.readObject();
            addEntry(key, value);
        }
    }
    
    /**
     * Requires special handling during serialization process.
     *
     * @param  stream the object output stream.
     * @throws IOException if an I/O error occurs.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeInt(_capacity);
        stream.writeInt(_size);
        int count = 0;
        EntryImpl entry = _mapFirst;
        while (entry != null) {
            stream.writeObject(entry._key);
            stream.writeObject(entry._value);
            count++;
            entry = entry._after;
        }
        if (count != _size) {
            throw new IOException("FastMap Corrupted");
        }
    }
    
    /**
     * This class represents a {@link FastMap} entry.
     */
    private static final class EntryImpl implements Map.Entry {

        /**
         * Holds the entry key (null when in pool).
         */
        private Object _key;
        
        /**
         * Holds the entry value (null when in pool).
         */
        private Object _value;

        /**
         * Holds the bucket index (undefined when in pool). 
         */
        private int _index;

        /**
         * Holds the previous entry in the same bucket (null when in pool).
         */
        private EntryImpl _previous;

        /**
         * Holds the next entry in the same bucket (null when in pool).
         */
        private EntryImpl _next;

        /**
         * Holds the entry added before this entry (null when in pool).
         */
        private EntryImpl _before;

        /**
         * Holds the entry added after this entry 
         * or the next available entry when in pool.
         */
        private EntryImpl _after;

        /**
         * Returns the key for this entry.
         * 
         * @return the entry's key.
         */
        public Object getKey() {
            return _key;
        }

        /**
         * Returns the value for this entry.
         * 
         * @return the entry's value.
         */
        public Object getValue() {
            return _value;
         }

        /**
         * Sets the value for this entry.
         * 
         * @param value the new value.
         * @return the previous value.
         */
        public Object setValue(Object value) {            
            Object old = _value;
            _value = value;
            return old;
        } 

        /**
         * Indicates if this entry is considered equals to the specified 
         * entry.
         * 
         * @param that the object to test for equality.
         * @return <code>true<code> if both entry are considered equal;
         *         <code>false<code> otherwise.
         */
        public boolean equals(Object that) {
            if (that instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) that;
                return (_key.equals(entry.getKey())) &&
                    ((_value != null) ? 
                        _value.equals(entry.getValue()) : 
                        (entry.getValue() == null));
            } else {
                return false;
            }
        }

        /**
         * Returns the hash code for this entry.
         * 
         * @return this entry's hash code.
         */
        public int hashCode() {
            return _key.hashCode() ^ ((_value != null) ? _value.hashCode() : 0);
        } 

        /**
         * Returns the text representation of this entry.
         * 
         * @return this entry's textual representation.
         */
        public String toString() {
            return _key + "=" + _value;
        }
    }
}