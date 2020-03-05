package com.github.vzakharchenko.dynamic.orm.qModel;


import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;

import javax.annotation.processing.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Generated("com.querydsl.query.sql.codegen.MetaDataSerializer")
public class QUser extends RelationalPathBase<QUser> {

    public static final QUser user = new QUser("USER");
    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final PrimaryKey<QUser> primary1 = createPrimaryKey(id);

    public QUser(String variable) {
        super(QUser.class, forVariable(variable), "", "USER");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(38).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(38).notNull());
    }

}

