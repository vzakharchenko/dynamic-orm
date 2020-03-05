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
public class QTestTableSequence extends RelationalPathBase<QTestTableSequence> {

    public static final QTestTableSequence Q_TEST_TABLE_SEQUENCE = new QTestTableSequence("TEST_TABLE_SEQUENCE");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> test2 = createNumber("test2", Integer.class);

    public final PrimaryKey<QTestTableSequence> idPk = createPrimaryKey(id);

    public QTestTableSequence(String variable) {
        super(QTestTableSequence.class, forVariable(variable), "", "TEST_TABLE_SEQUENCE");
        addMetadata();
    }

    public QTestTableSequence(String variable, String schema, String table) {
        super(QTestTableSequence.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestTableSequence(Path<? extends QTestTableSequence> path) {
        super(path.getType(), path.getMetadata(), "", "TEST_TABLE_SEQUENCE");
        addMetadata();
    }

    public QTestTableSequence(PathMetadata metadata) {
        super(QTestTableSequence.class, metadata, "", "TEST_TABLE_SEQUENCE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(test2, ColumnMetadata.named("TEST2").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
    }

}

