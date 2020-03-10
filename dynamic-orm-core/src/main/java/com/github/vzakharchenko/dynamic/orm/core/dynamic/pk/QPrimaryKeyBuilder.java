package com.github.vzakharchenko.dynamic.orm.core.dynamic.pk;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.querydsl.core.types.Path;

public interface QPrimaryKeyBuilder {

    QPrimaryKeyBuilder addPrimaryKey(Path path);

    QPrimaryKeyBuilder addPrimaryKey(String columnName);

    QPrimaryKeyBuilder addPrimaryKeyGenerator(PKGenerator<?> pkGenerator);

    QTableBuilder finish();
}
