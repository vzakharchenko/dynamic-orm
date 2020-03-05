package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QDatabasechangelog is a Querydsl query type for QDatabasechangelog
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDatabasechangelog extends com.querydsl.sql.RelationalPathBase<QDatabasechangelog> {

    private static final long serialVersionUID = -1867174538;

    public static final QDatabasechangelog databasechangelog = new QDatabasechangelog("DATABASECHANGELOG");

    public final StringPath author = createString("author");

    public final StringPath comments = createString("comments");

    public final StringPath contexts = createString("contexts");

    public final DateTimePath<java.sql.Timestamp> dateexecuted = createDateTime("dateexecuted", java.sql.Timestamp.class);

    public final StringPath description = createString("description");

    public final StringPath exectype = createString("exectype");

    public final StringPath filename = createString("filename");

    public final StringPath id = createString("id");

    public final StringPath labels = createString("labels");

    public final StringPath liquibase = createString("liquibase");

    public final StringPath md5sum = createString("md5sum");

    public final NumberPath<Integer> orderexecuted = createNumber("orderexecuted", Integer.class);

    public final StringPath tag = createString("tag");

    public QDatabasechangelog(String variable) {
        super(QDatabasechangelog.class, forVariable(variable), "PUBLIC", "DATABASECHANGELOG");
        addMetadata();
    }

    public QDatabasechangelog(String variable, String schema, String table) {
        super(QDatabasechangelog.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDatabasechangelog(String variable, String schema) {
        super(QDatabasechangelog.class, forVariable(variable), schema, "DATABASECHANGELOG");
        addMetadata();
    }

    public QDatabasechangelog(Path<? extends QDatabasechangelog> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "DATABASECHANGELOG");
        addMetadata();
    }

    public QDatabasechangelog(PathMetadata metadata) {
        super(QDatabasechangelog.class, metadata, "PUBLIC", "DATABASECHANGELOG");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(author, ColumnMetadata.named("AUTHOR").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(comments, ColumnMetadata.named("COMMENTS").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(contexts, ColumnMetadata.named("CONTEXTS").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(dateexecuted, ColumnMetadata.named("DATEEXECUTED").withIndex(4).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(description, ColumnMetadata.named("DESCRIPTION").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(exectype, ColumnMetadata.named("EXECTYPE").withIndex(6).ofType(Types.VARCHAR).withSize(10).notNull());
        addMetadata(filename, ColumnMetadata.named("FILENAME").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(labels, ColumnMetadata.named("LABELS").withIndex(13).ofType(Types.VARCHAR).withSize(255));
        addMetadata(liquibase, ColumnMetadata.named("LIQUIBASE").withIndex(11).ofType(Types.VARCHAR).withSize(20));
        addMetadata(md5sum, ColumnMetadata.named("MD5SUM").withIndex(7).ofType(Types.VARCHAR).withSize(35));
        addMetadata(orderexecuted, ColumnMetadata.named("ORDEREXECUTED").withIndex(5).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(tag, ColumnMetadata.named("TAG").withIndex(10).ofType(Types.VARCHAR).withSize(255));
    }

}

