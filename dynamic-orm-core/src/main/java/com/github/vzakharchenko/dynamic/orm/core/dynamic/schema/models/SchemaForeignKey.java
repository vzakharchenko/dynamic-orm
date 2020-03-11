package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models;

import java.io.Serializable;
import java.util.List;

public class SchemaForeignKey implements Serializable {
    private List<String> localColumns;
    private List<String> remoteColumns;
    private boolean dynamicRemote;
    private String table;

    public List<String> getLocalColumns() {
        return localColumns;
    }

    public void setLocalColumns(List<String> localColumns) {
        this.localColumns = localColumns;
    }

    public List<String> getRemoteColumns() {
        return remoteColumns;
    }

    public void setRemoteColumns(List<String> remoteColumns) {
        this.remoteColumns = remoteColumns;
    }

    public boolean isDynamicRemote() {
        return dynamicRemote;
    }

    public void setDynamicRemote(boolean dynamicRemote) {
        this.dynamicRemote = dynamicRemote;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
