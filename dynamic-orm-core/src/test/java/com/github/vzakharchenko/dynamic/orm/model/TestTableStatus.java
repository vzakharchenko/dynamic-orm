package com.github.vzakharchenko.dynamic.orm.model;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SequanceName;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.annotations.Version;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableStatus;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 20:47
 */
@QueryDslModel(qTableClass = QTestTableStatus.class, tableName = "TEST_TABLE_STATUS", primaryKeyGenerator = PrimaryKeyGenerators.SEQUENCE)
@SequanceName("TEST_SEQUENCE")
public class TestTableStatus implements DMLModel {

    private Integer id;
    @Version
    private Integer version;

    @SoftDelete(defaultStatus = "0", deletedStatus = "-1")
    private Integer status;

    private Integer testColumn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTestColumn() {
        return testColumn;
    }

    public void setTestColumn(Integer testColumn) {
        this.testColumn = testColumn;
    }
}
