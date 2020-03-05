
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
@QueryDslModel(qTableClass = orm.query.examples.qmodels.QDatabasechangelog.class, tableName = "DATABASECHANGELOG")
public class Databasechangelog
    implements DMLModel
{

    private String author;
    private String comments;
    private String contexts;
    private Timestamp dateexecuted;
    private String description;
    private String exectype;
    private String filename;
    private String id;
    private String labels;
    private String liquibase;
    private String md5sum;
    private Integer orderexecuted;
    private String tag;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getContexts() {
        return contexts;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public Timestamp getDateexecuted() {
        if (dateexecuted!= null) {
            return ((Timestamp) dateexecuted.clone());
        } else {
            return null;
        }
    }

    public void setDateexecuted(Timestamp dateexecuted) {
        if (dateexecuted!= null) {
            this.dateexecuted = ((Timestamp) dateexecuted.clone());
        } else {
            this.dateexecuted = null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExectype() {
        return exectype;
    }

    public void setExectype(String exectype) {
        this.exectype = exectype;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getLiquibase() {
        return liquibase;
    }

    public void setLiquibase(String liquibase) {
        this.liquibase = liquibase;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public Integer getOrderexecuted() {
        return orderexecuted;
    }

    public void setOrderexecuted(Integer orderexecuted) {
        this.orderexecuted = orderexecuted;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
