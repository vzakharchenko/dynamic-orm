package orm.query.examples.qmodels;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QRolePermission is a Querydsl query type for QRolePermission
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRolePermission extends com.querydsl.sql.RelationalPathBase<QRolePermission> {

    private static final long serialVersionUID = -896295672;

    public static final QRolePermission rolePermission = new QRolePermission("ROLE_PERMISSION");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> permissionId = createNumber("permissionId", Integer.class);

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final com.querydsl.sql.PrimaryKey<QRolePermission> rolePermissionPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QPermission> rolePermission1Fkey = createForeignKey(permissionId, "ID");

    public final com.querydsl.sql.ForeignKey<QRole> rolePermission2Fkey = createForeignKey(roleId, "ID");

    public QRolePermission(String variable) {
        super(QRolePermission.class, forVariable(variable), "PUBLIC", "ROLE_PERMISSION");
        addMetadata();
    }

    public QRolePermission(String variable, String schema, String table) {
        super(QRolePermission.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRolePermission(String variable, String schema) {
        super(QRolePermission.class, forVariable(variable), schema, "ROLE_PERMISSION");
        addMetadata();
    }

    public QRolePermission(Path<? extends QRolePermission> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "ROLE_PERMISSION");
        addMetadata();
    }

    public QRolePermission(PathMetadata metadata) {
        super(QRolePermission.class, metadata, "PUBLIC", "ROLE_PERMISSION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(permissionId, ColumnMetadata.named("PERMISSION_ID").withIndex(2).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(roleId, ColumnMetadata.named("ROLE_ID").withIndex(3).ofType(Types.INTEGER).withSize(32).notNull());
    }

}

