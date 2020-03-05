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
public class QTestTableVersion extends RelationalPathBase<QTestTableVersion> {

    public static final QTestTableVersion qTestTableVersion = new QTestTableVersion("TEST_TABLE_VERSION");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public final PrimaryKey<QTestTableVersion> idPk = createPrimaryKey(id);

    public QTestTableVersion(String variable) {
        super(QTestTableVersion.class, forVariable(variable), "", "TEST_TABLE_VERSION");
        addMetadata();
    }

    public QTestTableVersion(String variable, String schema, String table) {
        super(QTestTableVersion.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestTableVersion(Path<? extends QTestTableVersion> path) {
        super(path.getType(), path.getMetadata(), "", "TEST_TABLE_VERSION");
        addMetadata();
    }

    public QTestTableVersion(PathMetadata metadata) {
        super(QTestTableVersion.class, metadata, "", "TEST_TABLE_VERSION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
    }

}

