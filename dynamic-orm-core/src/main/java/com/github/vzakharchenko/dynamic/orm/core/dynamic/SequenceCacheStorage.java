package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import java.util.ArrayList;
import java.util.List;

public class SequenceCacheStorage implements CacheStorage<SequanceModel> {
    private List<SequanceModel> sequenceModels = new ArrayList<>();

    @Override
    public List<SequanceModel> getDynamicObjects() {
        return sequenceModels;
    }

    public void setSequenceModels(List<SequanceModel> sequenceModels) {
        this.sequenceModels = sequenceModels;
    }
}
