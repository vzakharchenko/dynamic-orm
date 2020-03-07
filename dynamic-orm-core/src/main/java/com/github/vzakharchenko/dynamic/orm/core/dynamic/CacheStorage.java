package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import java.io.Serializable;
import java.util.List;

public interface CacheStorage extends Serializable {
    List<QDynamicTable> getDynamicTables();
}
