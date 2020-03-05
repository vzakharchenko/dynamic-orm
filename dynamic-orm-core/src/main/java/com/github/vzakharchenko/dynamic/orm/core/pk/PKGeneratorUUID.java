package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.querydsl.sql.RelationalPath;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;

import java.util.UUID;

/**
 *
 */
public abstract class PKGeneratorUUID<NUMBER extends Number> implements PKGenerator<NUMBER> {

    public NUMBER generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str = "" + idOne;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        return parseString(str);
    }

    protected abstract NUMBER parseString(String uid);

    @Override
    public NUMBER generateNewValue(OrmQueryFactory ormQueryFactory,
                                   RelationalPath<?> qTable, DMLModel dmlModel) {
        return generateUniqueId();
    }

}
