package io.nlopez.smartlocation.location;

/**
 * Basic abstraction for key value storage.
 */
public interface Store<T> {
    T get(String key);
    void put(String key, T value);
    void remove(String key);
}