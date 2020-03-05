package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.change;

import liquibase.structure.core.Column;

/**
 * Created by vzakharchenko on 20.10.14.
 */
public class ChangeMissedColumn implements ChangeMissedDatabaseObject<Column> {

    @Override
    public Column change(Column column) {
        Column changeColumn = copyColumn(column);
        changeColumn.setName(column.getName());
        changeColumn.setNullable(Boolean.TRUE);
        changeColumn.setDefaultValue(null);
        return changeColumn;
    }

    private Column copyColumn(Column column) {
        Column changeColumn = new Column();
        for (String attribute : column.getAttributes()) {
            changeColumn.setAttribute(attribute, column.getAttribute(attribute, Object.class));
        }
        return changeColumn;
    }
}
