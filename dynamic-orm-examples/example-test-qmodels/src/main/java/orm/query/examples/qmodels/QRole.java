package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QRole is a Querydsl query type for QRole
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRole extends com.querydsl.sql.RelationalPathBase<QRole> {

    private static final long serialVersionUID = -682797479;

    public static final QRole role = new QRole("ROLE");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QRole> rolePkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QRolePermission> _rolePermission2Fkey = createInvForeignKey(id, "ROLE_ID");

    public final com.querydsl.sql.ForeignKey<QUserrole> _userroleRoleIdFkey = createInvForeignKey(id, "ROLE_ID");

    public QRole(String variable) {
        super(QRole.class, forVariable(variable), "PUBLIC", "ROLE");
        addMetadata();
    }

    public QRole(String variable, String schema, String table) {
        super(QRole.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRole(String variable, String schema) {
        super(QRole.class, forVariable(variable), schema, "ROLE");
        addMetadata();
    }

    public QRole(Path<? extends QRole> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "ROLE");
        addMetadata();
    }

    public QRole(PathMetadata metadata) {
        super(QRole.class, metadata, "PUBLIC", "ROLE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(200).notNull());
    }

}

