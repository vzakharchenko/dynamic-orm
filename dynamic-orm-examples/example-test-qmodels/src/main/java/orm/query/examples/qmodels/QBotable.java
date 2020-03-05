package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBotable is a Querydsl query type for QBotable
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QBotable extends com.querydsl.sql.RelationalPathBase<QBotable> {

    private static final long serialVersionUID = -1562373410;

    public static final QBotable botable = new QBotable("BOTABLE");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> stage = createNumber("stage", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final DateTimePath<java.sql.Timestamp> version = createDateTime("version", java.sql.Timestamp.class);

    public final com.querydsl.sql.PrimaryKey<QBotable> botablePkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QUserdata> botableUserIdFkey = createForeignKey(userId, "ID");

    public QBotable(String variable) {
        super(QBotable.class, forVariable(variable), "PUBLIC", "BOTABLE");
        addMetadata();
    }

    public QBotable(String variable, String schema, String table) {
        super(QBotable.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBotable(String variable, String schema) {
        super(QBotable.class, forVariable(variable), schema, "BOTABLE");
        addMetadata();
    }

    public QBotable(Path<? extends QBotable> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "BOTABLE");
        addMetadata();
    }

    public QBotable(PathMetadata metadata) {
        super(QBotable.class, metadata, "PUBLIC", "BOTABLE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(200).notNull());
        addMetadata(stage, ColumnMetadata.named("STAGE").withIndex(5).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(userId, ColumnMetadata.named("USER_ID").withIndex(3).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(4).ofType(Types.TIMESTAMP).withSize(26).notNull());
    }

}

