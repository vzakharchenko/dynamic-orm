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
public class QTestTableVersionAnnotation extends RelationalPathBase<QTestTableVersionAnnotation> {

    public static final QTestTableVersionAnnotation qTestTableVersionAnnotation = new QTestTableVersionAnnotation("TEST_TABLE_VERSION_ANNOTATION");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public final PrimaryKey<QTestTableVersionAnnotation> idPk = createPrimaryKey(id);

    public QTestTableVersionAnnotation(String variable) {
        super(QTestTableVersionAnnotation.class, forVariable(variable), "", "TEST_TABLE_VERSION_ANNOTATION");
        addMetadata();
    }

    public QTestTableVersionAnnotation(String variable, String schema, String table) {
        super(QTestTableVersionAnnotation.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestTableVersionAnnotation(Path<? extends QTestTableVersionAnnotation> path) {
        super(path.getType(), path.getMetadata(), "", "TEST_TABLE_VERSION_ANNOTATION");
        addMetadata();
    }

    public QTestTableVersionAnnotation(PathMetadata metadata) {
        super(QTestTableVersionAnnotation.class, metadata, "", "TEST_TABLE_VERSION_ANNOTATION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.INTEGER).withSize(38).notNull());
    }

}

