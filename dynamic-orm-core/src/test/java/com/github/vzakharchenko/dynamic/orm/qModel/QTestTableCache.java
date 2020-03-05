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
public class QTestTableCache extends RelationalPathBase<QTestTableCache> {

    public static final QTestTableCache testTableCache = new QTestTableCache("TEST_TABLE_CACHE");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> test2 = createNumber("test2", Integer.class);

    public final PrimaryKey<QTestTableCache> idPk = createPrimaryKey(id);

    public QTestTableCache(String variable) {
        super(QTestTableCache.class, forVariable(variable), "", "TEST_TABLE_CACHE");
        addMetadata();
    }

    public QTestTableCache(String variable, String schema, String table) {
        super(QTestTableCache.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestTableCache(Path<? extends QTestTableCache> path) {
        super(path.getType(), path.getMetadata(), "", "TEST_TABLE_CACHE");
        addMetadata();
    }

    public QTestTableCache(PathMetadata metadata) {
        super(QTestTableCache.class, metadata, "", "TEST_TABLE_CACHE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(test2, ColumnMetadata.named("TEST2").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
    }

}

