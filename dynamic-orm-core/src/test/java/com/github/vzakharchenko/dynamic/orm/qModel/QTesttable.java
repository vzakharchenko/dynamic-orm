package com.github.vzakharchenko.dynamic.orm.qModel;


import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;

import javax.annotation.processing.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Generated("com.querydsl.query.sql.codegen.MetaDataSerializer")
public class QTesttable extends RelationalPathBase<QTesttable> {

    public static final QTesttable testtable = new QTesttable("TESTTABLE");
    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> test2 = createNumber("test2", Integer.class);

    public final PrimaryKey<QTesttable> primary1 = createPrimaryKey(id);

    public QTesttable(String variable) {
        super(QTesttable.class, forVariable(variable), "", "TESTTABLE");
        addMetadata();
    }

    public QTesttable(String variable, String schema, String table) {
        super(QTesttable.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTesttable(Path<? extends QTesttable> path) {
        super(path.getType(), path.getMetadata(), "", "TESTTABLE");
        addMetadata();
    }

    public QTesttable(PathMetadata metadata) {
        super(QTesttable.class, metadata, "", "TESTTABLE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(test2, ColumnMetadata.named("TEST2").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
    }

}

