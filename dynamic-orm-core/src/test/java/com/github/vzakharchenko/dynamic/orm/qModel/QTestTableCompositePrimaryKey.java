package com.github.vzakharchenko.dynamic.orm.qModel;


import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
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
public class QTestTableCompositePrimaryKey extends RelationalPathBase<QTestTableCompositePrimaryKey> {

    public static final QTestTableCompositePrimaryKey qTestTableCompositePrimaryKey = new QTestTableCompositePrimaryKey("TEST_COMPOSITE_PK_TABLE");

    public final NumberPath<Integer> id1 = createNumber("id1", Integer.class);
    public final StringPath id2 = createString("id2");

    public final NumberPath<Integer> version = createNumber("version", Integer.class);
    public final NumberPath<Integer> status = createNumber("status", Integer.class);
    public final NumberPath<Integer> testColumn = createNumber("testColumn", Integer.class);

    public final PrimaryKey<QTestTableCompositePrimaryKey> idPk = createPrimaryKey(id1,id2);

    public QTestTableCompositePrimaryKey(String variable) {
        super(QTestTableCompositePrimaryKey.class, forVariable(variable), "", "TEST_COMPOSITE_PK_TABLE");
        addMetadata();
    }

    public QTestTableCompositePrimaryKey(String variable, String schema, String table) {
        super(QTestTableCompositePrimaryKey.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestTableCompositePrimaryKey(Path<? extends QTestTableCompositePrimaryKey> path) {
        super(path.getType(), path.getMetadata(), "", "TEST_COMPOSITE_PK_TABLE");
        addMetadata();
    }

    public QTestTableCompositePrimaryKey(PathMetadata metadata) {
        super(QTestTableCompositePrimaryKey.class, metadata, "", "TEST_COMPOSITE_PK_TABLE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id1, ColumnMetadata.named("ID1").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(id2, ColumnMetadata.named("ID2").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(3).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(testColumn, ColumnMetadata.named("TEST_COLUMN").withIndex(4).ofType(Types.INTEGER).withSize(38).notNull());
    }

}

