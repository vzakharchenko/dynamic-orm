package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.DynamicTableHelper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.dml.DynamicModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;

public final class PrimaryKeyGeneratorHelper {
    private PrimaryKeyGeneratorHelper() {
    }

    public static PKGenerator<?> getPrimaryKeyGeneratorFromModel(
            Class<? extends DMLModel> dmlModel) {
        return AnnotationHelper.getAnnotationHolder(dmlModel).getPkGenerator();
    }

    public static PKGenerator<?> getPrimaryKeyGeneratorFromModel(DMLModel dmlModel) {
        if (dmlModel.isDynamicModel()) {
            DynamicModel dynamicModel = (DynamicModel) dmlModel;
            return DynamicTableHelper.getPkGenerator(dynamicModel.getQTable());
        }
        if (ModelHelper.hasQTableInModel(dmlModel.getClass())) {
            return getPrimaryKeyGeneratorFromModel(dmlModel.getClass());
        }
        return null;
    }
}
