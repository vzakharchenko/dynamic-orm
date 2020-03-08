package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserrole is a Querydsl query type for QUserrole
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QUserrole extends com.querydsl.sql.RelationalPathBase<QUserrole> {

    private static final long serialVersionUID = 701479716;

    public static final QUserrole userrole = new QUserrole("USERROLE");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final com.querydsl.sql.PrimaryKey<QUserrole> userrolePkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QRole> userroleRoleIdFkey = createForeignKey(roleId, "ID");

    public final com.querydsl.sql.ForeignKey<QUserdata> userroleUserIdFkey = createForeignKey(userId, "ID");

    public QUserrole(String variable) {
        super(QUserrole.class, forVariable(variable), "PUBLIC", "USERROLE");
        addMetadata();
    }

    public QUserrole(String variable, String schema, String table) {
        super(QUserrole.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserrole(String variable, String schema) {
        super(QUserrole.class, forVariable(variable), schema, "USERROLE");
        addMetadata();
    }

    public QUserrole(Path<? extends QUserrole> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "USERROLE");
        addMetadata();
    }

    public QUserrole(PathMetadata metadata) {
        super(QUserrole.class, metadata, "PUBLIC", "USERROLE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(roleId, ColumnMetadata.named("ROLE_ID").withIndex(3).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(userId, ColumnMetadata.named("USER_ID").withIndex(2).ofType(Types.INTEGER).withSize(32).notNull());
    }

}

