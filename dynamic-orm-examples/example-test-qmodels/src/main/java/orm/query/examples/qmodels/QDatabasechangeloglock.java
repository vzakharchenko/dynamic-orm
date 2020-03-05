package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QDatabasechangeloglock is a Querydsl query type for QDatabasechangeloglock
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDatabasechangeloglock extends com.querydsl.sql.RelationalPathBase<QDatabasechangeloglock> {

    private static final long serialVersionUID = -1358411871;

    public static final QDatabasechangeloglock databasechangeloglock = new QDatabasechangeloglock("DATABASECHANGELOGLOCK");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath locked = createBoolean("locked");

    public final StringPath lockedby = createString("lockedby");

    public final DateTimePath<java.sql.Timestamp> lockgranted = createDateTime("lockgranted", java.sql.Timestamp.class);

    public final com.querydsl.sql.PrimaryKey<QDatabasechangeloglock> databasechangeloglockPk = createPrimaryKey(id);

    public QDatabasechangeloglock(String variable) {
        super(QDatabasechangeloglock.class, forVariable(variable), "PUBLIC", "DATABASECHANGELOGLOCK");
        addMetadata();
    }

    public QDatabasechangeloglock(String variable, String schema, String table) {
        super(QDatabasechangeloglock.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDatabasechangeloglock(String variable, String schema) {
        super(QDatabasechangeloglock.class, forVariable(variable), schema, "DATABASECHANGELOGLOCK");
        addMetadata();
    }

    public QDatabasechangeloglock(Path<? extends QDatabasechangeloglock> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "DATABASECHANGELOGLOCK");
        addMetadata();
    }

    public QDatabasechangeloglock(PathMetadata metadata) {
        super(QDatabasechangeloglock.class, metadata, "PUBLIC", "DATABASECHANGELOGLOCK");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(locked, ColumnMetadata.named("LOCKED").withIndex(2).ofType(Types.BOOLEAN).withSize(0).notNull());
        addMetadata(lockedby, ColumnMetadata.named("LOCKEDBY").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lockgranted, ColumnMetadata.named("LOCKGRANTED").withIndex(3).ofType(Types.TIMESTAMP).withSize(26));
    }

}

