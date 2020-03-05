package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPermission is a Querydsl query type for QPermission
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPermission extends com.querydsl.sql.RelationalPathBase<QPermission> {

    private static final long serialVersionUID = 1114662258;

    public static final QPermission permission = new QPermission("PERMISSION");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath permissionName = createString("permissionName");

    public final com.querydsl.sql.PrimaryKey<QPermission> permissionPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QRolePermission> _rolePermission1Fkey = createInvForeignKey(id, "PERMISSION_ID");

    public QPermission(String variable) {
        super(QPermission.class, forVariable(variable), "PUBLIC", "PERMISSION");
        addMetadata();
    }

    public QPermission(String variable, String schema, String table) {
        super(QPermission.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPermission(String variable, String schema) {
        super(QPermission.class, forVariable(variable), schema, "PERMISSION");
        addMetadata();
    }

    public QPermission(Path<? extends QPermission> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PERMISSION");
        addMetadata();
    }

    public QPermission(PathMetadata metadata) {
        super(QPermission.class, metadata, "PUBLIC", "PERMISSION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(permissionName, ColumnMetadata.named("PERMISSION_NAME").withIndex(2).ofType(Types.VARCHAR).withSize(200).notNull());
    }

}

