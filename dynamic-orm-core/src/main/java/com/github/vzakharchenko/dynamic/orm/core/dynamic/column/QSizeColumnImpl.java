package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

public class QSizeColumnImpl extends QDefaultColumn implements QSizeColumn {

    private Integer columnSize;

    public QSizeColumnImpl(String name) {
        super(name);
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }

    @Override
    public Integer size() {
        return columnSize;
    }
}
