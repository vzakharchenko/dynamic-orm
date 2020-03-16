package com.github.vzakharchenko.dynamic.orm.model;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.annotations.Version;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableCompositePrimaryKey;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 20:47
 */
@QueryDslModel(qTableClass = QTestTableCompositePrimaryKey.class,
        tableName = "TEST_COMPOSITE_PK_TABLE")
public class TestTableCompositePrimaryKey implements DMLModel {

    private Integer id1;

    private String id2;
    @Version
    private Integer version;

    @SoftDelete(defaultStatus = "0", deletedStatus = "-1")
    private Integer status;

    private Integer testColumn;

    public Integer getId1() {
        return id1;
    }

    public void setId1(Integer id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
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
