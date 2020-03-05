package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserdata is a Querydsl query type for QUserdata
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QUserdata extends com.querydsl.sql.RelationalPathBase<QUserdata> {

    private static final long serialVersionUID = 701049432;

    public static final QUserdata userdata = new QUserdata("USERDATA");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final com.querydsl.sql.PrimaryKey<QUserdata> userdataPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QBotable> _botableUserIdFkey = createInvForeignKey(id, "USER_ID");

    public final com.querydsl.sql.ForeignKey<QUserrole> _userroleUserIdFkey = createInvForeignKey(id, "USER_ID");

    public QUserdata(String variable) {
        super(QUserdata.class, forVariable(variable), "PUBLIC", "USERDATA");
        addMetadata();
    }

    public QUserdata(String variable, String schema, String table) {
        super(QUserdata.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserdata(String variable, String schema) {
        super(QUserdata.class, forVariable(variable), schema, "USERDATA");
        addMetadata();
    }

    public QUserdata(Path<? extends QUserdata> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "USERDATA");
        addMetadata();
    }

    public QUserdata(PathMetadata metadata) {
        super(QUserdata.class, metadata, "PUBLIC", "USERDATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(200).notNull());
        addMetadata(password, ColumnMetadata.named("PASSWORD").withIndex(3).ofType(Types.VARCHAR).withSize(200).notNull());
    }

}

