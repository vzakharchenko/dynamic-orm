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


/**
 * QSCountryCode is a Querydsl query type for QSCountryCode
 */
@Generated("com.querydsl.query.sql.codegen.MetaDataSerializer")
public class QTestTableStatus extends RelationalPathBase<QTestTableStatus> {

    public static final QTestTableStatus qTestTableStatus = new QTestTableStatus("TEST_TABLE_STATUS");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);
    public final NumberPath<Integer> status = createNumber("status", Integer.class);
    public final NumberPath<Integer> testColumn = createNumber("testColumn", Integer.class);

    public final PrimaryKey<QTestTableStatus> idPk = createPrimaryKey(id);

    public QTestTableStatus(String variable) {
        super(QTestTableStatus.class, forVariable(variable), "", "TEST_TABLE_STATUS");
        addMetadata();
    }

    public QTestTableStatus(String variable, String schema, String table) {
        super(QTestTableStatus.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestTableStatus(Path<? extends QTestTableStatus> path) {
        super(path.getType(), path.getMetadata(), "", "TEST_TABLE_STATUS");
        addMetadata();
    }

    public QTestTableStatus(PathMetadata metadata) {
        super(QTestTableStatus.class, metadata, "", "TEST_TABLE_STATUS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(3).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(testColumn, ColumnMetadata.named("TEST_COLUMN").withIndex(4).ofType(Types.INTEGER).withSize(38).notNull());
    }

}

