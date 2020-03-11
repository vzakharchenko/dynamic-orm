package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

public class QDefaultColumn implements QColumn {

    private final String name;
    private Boolean nullable0 = Boolean.TRUE;
    private Boolean pk;

    public QDefaultColumn(String name) {
        this.name = name;
    }

    public void setNullable(Boolean nullable) {
        this.nullable0 = nullable;
    }

    public void setIsPrimaryKey(Boolean privateKey) {
        this.pk = privateKey;
    }

    @Override
    public String columnName() {
        return name;
    }

    @Override
    public Boolean notNull() {
        return !nullable0;
    }

    @Override
    public Boolean isPrimaryKey() {
        return pk;
    }
}
