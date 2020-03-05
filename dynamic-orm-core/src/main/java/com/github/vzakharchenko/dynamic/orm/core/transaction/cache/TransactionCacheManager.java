package com.github.vzakharchenko.dynamic.orm.core.transaction.cache;

import java.util.Collection;

/**
 * Created by vzakharchenko on 04.12.14.
 */
public interface TransactionCacheManager {
    /**
     * Return the cache associated with the given name.
     *
     * @param name the cache identifier (must not be {@code null})
     * @return the associated cache, or {@code null} if none found
     */
    TransactionalCache getTransactionalCache(String name);

    /**
     * Return a collection of the cache names known by this manager.
     *
     * @return the names of all caches known by the cache manager
     */
    Collection<String> getCacheNames();
}
