package com.github.vzakharchenko.dynamic.orm.core.pk;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.querydsl.sql.RelationalPath;

import java.util.UUID;

/**
 *
 */
public abstract class PKGeneratorUUID<NUMBER extends Number> implements PKGenerator<NUMBER> {

    public NUMBER generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str = idOne.toString();
        int uid = str.hashCode();
        String filterStr = String.valueOf(uid);
        str = filterStr.replaceAll("-", "");
        return parseString(str);
    }

    protected abstract NUMBER parseString(String uid);

    @Override
    public NUMBER generateNewValue(OrmQueryFactory ormQueryFactory,
                                   RelationalPath<?> qTable, DMLModel dmlModel) {
        return generateUniqueId();
    }

    @Override
    public String name() {
        return null;
    }
}
