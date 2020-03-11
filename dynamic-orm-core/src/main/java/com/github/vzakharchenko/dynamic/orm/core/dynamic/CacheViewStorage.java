package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import java.util.ArrayList;
import java.util.List;

public class CacheViewStorage implements CacheStorage<ViewDataHolder> {
    private List<ViewDataHolder> views = new ArrayList<>();

    @Override
    public List<ViewDataHolder> getDynamicObjects() {
        return views;
    }

    public void setViews(List<ViewDataHolder> views) {
        this.views = views;
    }
}
