package com.github.vzakharchenko.dynamic.orm.core.query.cache;

import com.github.vzakharchenko.dynamic.orm.core.cache.CachedColumn;
import com.github.vzakharchenko.dynamic.orm.core.transaction.cache.TransactionalCache;
import com.querydsl.core.types.Path;

public final class CacheFindAllByColumn<TYPE> {
    private final TransactionalCache transactionCache;
    private final CachedColumn cachedColumn;
    private final Path<TYPE> column;

    protected CacheFindAllByColumn(TransactionalCache transactionCache,
                                CachedColumn cachedColumn,
                                Path<TYPE> column) {
        this.transactionCache = transactionCache;
        this.cachedColumn = cachedColumn;
        this.column = column;
    }

    public TransactionalCache getTransactionCache() {
        return transactionCache;
    }

    public CachedColumn getCachedColumn() {
        return cachedColumn;
    }

    public Path<TYPE> getColumn() {
        return column;
    }
}
