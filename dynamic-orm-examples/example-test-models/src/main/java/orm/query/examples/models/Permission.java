
package orm.query.examples.models;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;


/**
 * /*
 * /*   autoGenerate by class com.github.vzakharchenko.dynamic.orm.generator.GenerateModelFactory
 * *<!---->/
 * 
 */
@QueryDslModel(qTableClass = orm.query.examples.qmodels.QPermission.class, tableName = "PERMISSION")
public class Permission
    implements DMLModel
{

    private Integer id;
    private String permissionName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

}
