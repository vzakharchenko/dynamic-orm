package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.SequanceModel;
import liquibase.structure.core.Sequence;

public abstract class SequanceFactory {
    public static Sequence createSequance(SequanceModel sequanceModel) {
        Sequence sequence = new Sequence("", "", sequanceModel.getName());
        sequence.setStartValue(sequanceModel.getInitial());
        sequence.setIncrementBy(sequanceModel.getIncrement());
        sequence.setMinValue(sequanceModel.getMin());
        sequence.setMaxValue(sequanceModel.getMax());
        return sequence;
    }
}
