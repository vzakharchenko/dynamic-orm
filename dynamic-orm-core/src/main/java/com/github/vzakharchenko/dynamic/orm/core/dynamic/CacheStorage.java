package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import java.io.Serializable;
import java.util.List;

public interface CacheStorage<T extends Serializable> extends Serializable {
    List<T> getDynamicObjects();
}
