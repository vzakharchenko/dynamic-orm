package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models;

import java.io.Serializable;

public class SchemaSoftDelete implements Serializable {
    private String column;
    private Serializable deletedValue;
    private Serializable defaultValue;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Serializable getDeletedValue() {
        return deletedValue;
    }

    public void setDeletedValue(Serializable deletedValue) {
        this.deletedValue = deletedValue;
    }

    public Serializable getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Serializable defaultValue) {
        this.defaultValue = defaultValue;
    }
}
