package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.RawModel;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.UpdateModelBuilder;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Path;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.vzakharchenko.dynamic.orm.core.RawModelBuilderImpl.SIZE;
import static com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper.getValueFromModelByColumn;

public final class PrimaryKeyHelper {
    private PrimaryKeyHelper() {
    }

    public static Path getPrimaryKeyColumn(RelationalPath qTable) {
        if (!hasPrimaryKey(qTable)) {
            throw new IllegalStateException(qTable + " does not have Primary Key");
        }
        if (hasCompositePrimaryKey(qTable)) {
            throw new IllegalStateException(qTable + " has Composite key. Please use getPrimaryKeyColumns" +
                    " instead of getPrimaryKeyColumn");
        }
        PrimaryKey<?> primaryKey = getPrimaryKey(qTable);
        return primaryKey.getLocalColumns().get(0);
    }

    public static List<? extends Path<?>> getPrimaryKeyColumns(RelationalPath qTable) {
        if (!hasPrimaryKey(qTable)) {
            throw new IllegalStateException(" Primary Key does not exist on table " +
                    qTable.getTableName());
        }
        PrimaryKey<?> primaryKey = getPrimaryKey(qTable);
        return primaryKey.getLocalColumns();
    }

    public static PrimaryKey<?> getPrimaryKey(RelationalPath qTable) {
        if (qTable == null) {
            return null;
        }
        return (PrimaryKey<?>) qTable.getPrimaryKey();
    }

    public static CompositeKey validateCompositeKey(CompositeKey compositeKey,
                                                    RelationalPath<?> qTable) {
        Assert.notNull(qTable.getPrimaryKey(),
                qTable + " does not have Primary Key");
        Assert.isTrue(CollectionUtils.isEqualCollection(
                compositeKey.getCompositeMap().keySet(),
                Sets.newHashSet(qTable.getPrimaryKey().getLocalColumns())),
                qTable + " contains another Primary Key.");
        return compositeKey;
    }

    public static CompositeKey getCompositeKey(Serializable value, RelationalPath<?> qTable) {
        if (value instanceof CompositeKey) {
            return validateCompositeKey((CompositeKey) value, qTable);
        } else {
            return getOnePrimaryKey(qTable, value);
        }
    }


    public static CompositeKey getCompositeKey(RelationalPath qTable,
                                               Map<Path<?>, ?> setMap) {
        List<? extends Path<?>> columns = getPrimaryKeyColumns(qTable);
        CompositeKeyBuilder builder = CompositeKeyBuilder.create(qTable);
        columns.forEach(new Consumer<Path<?>>() {
            @Override
            public void accept(Path<?> column) {
                builder.addPrimaryKey(column, (Serializable) setMap.get(column));
            }
        });
        return builder.build();
    }

    public static CompositeKey getPrimaryKeyValues(RawModel rawModel, RelationalPath<?> qTable) {
        List<? extends Path<?>> columns = getPrimaryKeyColumns(qTable);
        CompositeKeyBuilder compositeKeyBuilder = CompositeKeyBuilder.create(qTable);
        for (Path<?> column : columns) {
            Object columnValue = rawModel.getColumnValue(column);
            compositeKeyBuilder.addPrimaryKey(column, (Serializable) columnValue);
        }
        return compositeKeyBuilder.build();
    }

    public static List<CompositeKey> getPrimaryKeyValues(List<RawModel> rawModels,
                                                         RelationalPath<?> qTable) {
        return rawModels.stream().map(rawModel -> getPrimaryKeyValues(rawModel, qTable))
                .collect(Collectors.toList());
    }

    public static CompositeKey getOnePrimaryKeyValues(DMLModel model, RelationalPath<?> qTable) {
        List<? extends Path<?>> primaryKeyColumns = getPrimaryKeyColumns(qTable);
        Assert.isTrue(primaryKeyColumns.size() == 1,
                "Composite Key does not supported." +
                        " Please use getPrimaryKeyValues instead of getOnePrimaryKeyValues");
        Object value = getValueFromModelByColumn(model, primaryKeyColumns.get(0));
        return value != null ? getCompositeKey((Serializable) value, qTable) : null;
    }

    public static CompositeKey getPrimaryKeyValues(DMLModel model, RelationalPath<?> qTable) {
        CompositeKeyBuilder compositeKeyBuilder = CompositeKeyBuilder.create(qTable);
        List<? extends Path<?>> columns = getPrimaryKeyColumns(qTable);
        columns.forEach((Consumer<Path<?>>) column -> {
            Object value = getValueFromModelByColumn(model, column);
            if (value == null) {
                throw new IllegalArgumentException("Primary key " +
                        column + " value is null");
            }
            compositeKeyBuilder.addPrimaryKey(column, (Serializable) value);
        });
        return compositeKeyBuilder.build();
    }

    public static boolean hasPrimaryKey(RelationalPath<?> qTable) {
        return getPrimaryKey(qTable) != null;
    }

    public static boolean hasCompositePrimaryKey(RelationalPath<?> qTable) {
        return hasPrimaryKey(qTable) && getPrimaryKeyColumns(qTable).size() > 1;
    }

    public static boolean isPrimaryKeyValueNull(RelationalPath<?> qTable, DMLModel model) {
        return hasPrimaryKey(qTable) && getPrimaryKeyColumns(qTable)
                .stream().anyMatch((Predicate<Path<?>>)
                        path -> getValueFromModelByColumn(model, path) == null);
    }


    public static void updateModelBuilder(
            UpdateModelBuilder updateModelBuilder,
            RelationalPath<?> qTable,
            Serializable serializable) {
        if (serializable instanceof CompositeKey) {
            CompositeKey compositeKey = (CompositeKey) serializable;
            updateModelBuilder.set(compositeKey.getCompositeMap());
        } else {
            List<? extends Path<?>> columns = getPrimaryKeyColumns(qTable);
            if (columns.size() > SIZE) {
                throw new IllegalArgumentException("Please use CompositeKeyBuilder " +
                        "for composite Private Key");
            }
            Path<?> path = columns.get(0);
            CompositeKey compositeKey = CompositeKeyBuilder
                    .create(qTable).addPrimaryKey(path, serializable).build();
            updateModelBuilder(updateModelBuilder, qTable, compositeKey);
        }
    }

    public static CompositeKey getOnePrimaryKey(
            RelationalPath qTable,
            Serializable value) {
        List<? extends Path<?>> primaryKeyColumns = getPrimaryKeyColumns(qTable);
        Assert.isTrue(!primaryKeyColumns.isEmpty(),
                qTable + " does not have Primary Key");
        Assert.isTrue(primaryKeyColumns.size() == 1,
                qTable + " has composite Primary key");
        Path<?> column = primaryKeyColumns.get(0);
        return CompositeKeyBuilder
                .create(qTable).addPrimaryKey(column, value).build();
    }

    public static List<CompositeKey> getCompositeKeys(List<? extends Serializable> keys,
                                                      RelationalPath<?> qTable) {
        return keys.stream().map((Function<Serializable, CompositeKey>)
                key -> getCompositeKey(key, qTable)).collect(Collectors.toList());
    }

    public static boolean isOneOfPrimaryKey(Path column) {
        RelationalPath<?> qTable = ModelHelper.getQTable(column);
        return getPrimaryKeyColumns(qTable).contains(column);
    }
}
