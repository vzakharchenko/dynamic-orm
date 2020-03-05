
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
@QueryDslModel(qTableClass = orm.query.examples.qmodels.QBotable.class, tableName = "BOTABLE")
public class Botable
    implements DMLModel
{

    private Integer id;
    private String name;
    private Integer stage;
    private Integer userId;
    private Timestamp version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getVersion() {
        if (version!= null) {
            return ((Timestamp) version.clone());
        } else {
            return null;
        }
    }

    public void setVersion(Timestamp version) {
        if (version!= null) {
            this.version = ((Timestamp) version.clone());
        } else {
            this.version = null;
        }
    }

}
