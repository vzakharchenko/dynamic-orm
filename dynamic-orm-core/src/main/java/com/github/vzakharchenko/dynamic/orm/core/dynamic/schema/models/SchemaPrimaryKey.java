package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models;

import java.io.Serializable;

public class SchemaPrimaryKey implements Serializable {
    private String column;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
