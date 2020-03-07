package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import java.util.ArrayList;
import java.util.List;

public class CacheStorageImpl implements CacheStorage {
    private List<QDynamicTable> dynamicTables = new ArrayList<>();

    @Override
    public List<QDynamicTable> getDynamicTables() {
        return dynamicTables;
    }

    public void setDynamicTables(List<QDynamicTable> dynamicTables) {
        this.dynamicTables = dynamicTables;
    }
}
