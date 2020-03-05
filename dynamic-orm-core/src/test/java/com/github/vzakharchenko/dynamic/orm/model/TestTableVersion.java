package com.github.vzakharchenko.dynamic.orm.model;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SequanceName;
import com.github.vzakharchenko.dynamic.orm.core.pk.PrimaryKeyGenerators;
import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersion;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 20:47
 */
@QueryDslModel(qTableClass = QTestTableVersion.class, tableName = "TEST_TABLE_VERSION", primaryKeyGenerator = PrimaryKeyGenerators.SEQUENCE)
@SequanceName("TEST_SEQUENCE")
public class TestTableVersion implements DMLModel {

    private Integer id;

    private Integer version;

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
}
