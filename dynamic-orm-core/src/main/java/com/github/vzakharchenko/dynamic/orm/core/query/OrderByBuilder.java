package com.github.vzakharchenko.dynamic.orm.core.query;

public interface OrderByBuilder {
    UnionBuilder desc();
    UnionBuilder asc();
}
