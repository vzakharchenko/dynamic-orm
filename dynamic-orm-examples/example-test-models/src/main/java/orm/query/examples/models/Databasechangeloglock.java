
package orm.query.examples.models;

import java.sql.Timestamp;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;


/**
 * /*
 * /*   autoGenerate by class com.github.vzakharchenko.dynamic.orm.generator.GenerateModelFactory
 * *<!---->/
 * 
 */
@QueryDslModel(qTableClass = orm.query.examples.qmodels.QDatabasechangeloglock.class, tableName = "DATABASECHANGELOGLOCK")
public class Databasechangeloglock
    implements DMLModel
{

    private Integer id;
    private Boolean locked;
    private String lockedby;
    private Timestamp lockgranted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getLockedby() {
        return lockedby;
    }

    public void setLockedby(String lockedby) {
        this.lockedby = lockedby;
    }

    public Timestamp getLockgranted() {
        if (lockgranted!= null) {
            return ((Timestamp) lockgranted.clone());
        } else {
            return null;
        }
    }

    public void setLockgranted(Timestamp lockgranted) {
        if (lockgranted!= null) {
            this.lockgranted = ((Timestamp) lockgranted.clone());
        } else {
            this.lockgranted = null;
        }
    }

}
