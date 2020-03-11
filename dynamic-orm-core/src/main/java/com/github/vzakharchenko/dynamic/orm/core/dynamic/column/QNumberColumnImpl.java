package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

public class QNumberColumnImpl
        extends QSizeColumnImpl implements QNumberColumn {

    private Integer dd;

    private Class<? extends Number> tclass;

    public QNumberColumnImpl(String name) {
        super(name);
    }

    public void setDecimalDigits(Integer d) {
        this.dd = d;
    }

    public void setNumberClass(Class<? extends Number> nimberclass) {
        this.tclass = nimberclass;
    }

    @Override
    public Class<? extends Number> numberClass() {
        return tclass;
    }

    @Override
    public Integer decimalDigits() {
        return dd;
    }
}
