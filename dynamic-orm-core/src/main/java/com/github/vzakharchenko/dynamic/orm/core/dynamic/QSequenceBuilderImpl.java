package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Locale;

public class QSequenceBuilderImpl implements QSequenceBuilder {

    private final QDynamicBuilderContext dynamicBuilderContext;

    private final SequanceModel sequanceModel;

    protected QSequenceBuilderImpl(String name,
                                   QDynamicBuilderContext dynamicBuilderContext
    ) {
        this.sequanceModel = new SequanceModel(name);
        this.dynamicBuilderContext = dynamicBuilderContext;
    }

    @Override
    public QSequenceBuilder initialValue(Long initialValue) {
        sequanceModel.setInitial(BigInteger.valueOf(initialValue));
        return this;
    }

    @Override
    public QSequenceBuilder increment(Long incrementValue) {
        sequanceModel.setIncrement(BigInteger.valueOf(incrementValue));
        return this;
    }

    @Override
    public QSequenceBuilder min(Long minValue) {
        sequanceModel.setMin(BigInteger.valueOf(minValue));
        return this;
    }

    @Override
    public QSequenceBuilder max(Long maxValue) {
        sequanceModel.setMax(BigInteger.valueOf(maxValue));
        return this;
    }

    @Override
    public QDynamicTableFactory finish() {
        dynamicBuilderContext.getContextSequances().put(
                StringUtils.upperCase(sequanceModel.getName(), Locale.US),
                sequanceModel);
        return dynamicBuilderContext;
    }
}
