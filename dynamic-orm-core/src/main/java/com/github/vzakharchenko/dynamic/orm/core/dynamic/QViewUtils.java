package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QNumberColumnImpl;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumnImpl;
import com.querydsl.core.types.*;
import liquibase.database.Database;

import java.util.Date;
import java.util.List;

public final class QViewUtils {

    private QViewUtils() {
    }

    public static QDynamicTable transform(Database database, ViewModel viewModel) {
        QDynamicTable qDynamicTable = new QDynamicTable(viewModel.getName());
        List<Expression<?>> columns = viewModel.getExpressions();
        for (Expression<?> column : columns) {
            transform(qDynamicTable, database, column);
        }
        return qDynamicTable;
    }

    private static void transform(QDynamicTable qDynamicTable, Database database,
                                  Expression<?> column) {
        if (column instanceof Path) {
            transformPath(qDynamicTable, database, (Path) column);
        } else if (column instanceof Operation) {
            transformOperation(qDynamicTable, database, (Operation) column);
        } else {
            throw new IllegalStateException("Unsupported type " + column);
        }
    }

    private static void transformOperation(QDynamicTable qDynamicTable, Database database,
                                           Operation column) {
        Path path = getPathFromOperation(column);
        if (path == null) {
            throw new IllegalStateException("Operation " + column + " does not support");
        }
        transformPath(qDynamicTable, database, path);
    }

    private static void transformPath(QDynamicTable qDynamicTable, Database database,
                                      Path column) {
        if (Number.class.isAssignableFrom(column.getType())) {
            transformNumber(qDynamicTable, database, column);
        } else if (String.class.isAssignableFrom(column.getType())) {
            transformString(qDynamicTable, database, column);
        } else if (Date.class.isAssignableFrom(column.getType())) {
            transformDate(qDynamicTable, database, column);
        } else if (column.getType().isArray()) {
            transformBlob(qDynamicTable, database, column);
        } else if (Boolean.class.isAssignableFrom(column.getType())) {
            transformBoolean(qDynamicTable, database, column);
        } else {
            throw new IllegalStateException("unsupported operation");
        }
    }

    private static void transformNumber(QDynamicTable qDynamicTable, Database database,
                                        Path column) {
        PathMetadata metadata = column.getMetadata();
        QNumberColumnImpl numberColumn = new QNumberColumnImpl(metadata.getName());
        numberColumn.setNumberClass(Number.class);
        qDynamicTable.createNumberColumn(database, numberColumn);
    }

    private static void transformString(QDynamicTable qDynamicTable, Database database,
                                        Path column) {
        QSizeColumn sizeColumn = createSizeColumn(column.getMetadata());
        qDynamicTable.createStringColumn(database, sizeColumn);
    }

    private static void transformDate(QDynamicTable qDynamicTable, Database database,
                                      Path column) {
        QSizeColumn sizeColumn = createSizeColumn(column.getMetadata());
        qDynamicTable.createDateTimeColumn(database, sizeColumn);
    }

    private static void transformBlob(QDynamicTable qDynamicTable, Database database,
                                      Path column) {
        QSizeColumn sizeColumn = createSizeColumn(column.getMetadata());
        qDynamicTable.createBlobColumn(database, sizeColumn);
    }

    private static void transformBoolean(QDynamicTable qDynamicTable, Database database,
                                         Path column) {
        QSizeColumn sizeColumn = createSizeColumn(column.getMetadata());
        qDynamicTable.createBooleanColumn(database, sizeColumn);
    }

    private static QSizeColumn createSizeColumn(PathMetadata metadata) {
        return new QSizeColumnImpl(metadata.getName());
    }

    private static Path getPathFromOperation(Operation operation) {
        List<Expression<?>> expressions = operation.getArgs();
        for (Expression exp : expressions) {
            if (exp instanceof Path) {
                Path column = (Path) exp;
                if (column.getMetadata().getPathType() == PathType.PROPERTY) {
                    return column;
                }
            }
        }
        return null;
    }
}
