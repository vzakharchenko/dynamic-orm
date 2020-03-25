package com.github.vzakharchenko.dynamic.orm.core.dynamic;

public interface QSequenceBuilder {
    QSequenceBuilder initialValue(Long initialValue);

    QSequenceBuilder increment(Long incrementValue);

    QSequenceBuilder min(Long minValue);

    QSequenceBuilder max(Long maxValue);

    QDynamicTableFactory addSequence();
}
