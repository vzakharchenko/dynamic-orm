package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

import com.querydsl.core.types.Path;

public class QCustomColumnImpl
        extends QSizeColumnImpl implements QCustomColumn {

    private Integer dd;

    private Path<?> column;

    private String jdbc;

    public void setColumn(Path<?> column) {
        this.column = column;
    }

    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }

    public QCustomColumnImpl(String name) {
        super(name);
    }

    public void setDecimalDigits(Integer d) {
        this.dd = d;
    }

    @Override
    public Path<?> customColumn() {
        return column;
    }

    @Override
    public String jdbcType() {
        return jdbc;
    }

    @Override
    public Integer decimalDigits() {
        return dd;
    }
}
