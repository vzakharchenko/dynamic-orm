
package orm.query.examples.models;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;


/**
 * /*
 * /*   autoGenerate by class com.github.vzakharchenko.dynamic.orm.generator.GenerateModelFactory
 * *<!---->/
 * 
 */
@QueryDslModel(qTableClass = orm.query.examples.qmodels.QRolePermission.class, tableName = "ROLE_PERMISSION")
public class RolePermission
    implements DMLModel
{

    private Integer id;
    private Integer permissionId;
    private Integer roleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

}
