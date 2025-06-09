package com.example.poll_system.infrastructure.persistence.jpa.cache;

import java.util.Optional;

public interface CacheStore<K, V> {
    Optional<V> get(K key);

    void put(K key, V value);

    void evict(K key);
}
