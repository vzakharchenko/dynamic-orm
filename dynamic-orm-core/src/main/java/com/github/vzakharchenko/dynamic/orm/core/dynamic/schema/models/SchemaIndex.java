package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models;

import java.io.Serializable;
import java.util.List;

public class SchemaIndex implements Serializable {
    private List<String> columns;

    private Boolean uniq;

    private Boolean clustered;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Boolean getUniq() {
        return uniq;
    }

    public void setUniq(Boolean uniq) {
        this.uniq = uniq;
    }

    public Boolean isClustered() {
        return clustered;
    }

    public void setClustered(Boolean clustered) {
        this.clustered = clustered;
    }
}
