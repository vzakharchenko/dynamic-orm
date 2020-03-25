package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.IndexData;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Path;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Column;
import liquibase.structure.core.ForeignKey;
import liquibase.structure.core.Index;
import liquibase.structure.core.Relation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.TableFactory.*;

public final class LiquibaseHelper {
    private LiquibaseHelper() {
    }

    public static Relation getRelation(DatabaseObject databaseObject) {
        return Optional.ofNullable(getSimpleRelation(databaseObject)).or(() ->
                Optional.ofNullable(getForeignRelation(databaseObject)).or(()
                        -> Optional.ofNullable(getIndexRelation(databaseObject)).or(
                        Optional::empty))).orElse(null);
    }

    private static Relation getForeignRelation(DatabaseObject databaseObject) {
        return databaseObject.getAttribute("foreignKeyTable", Relation.class);
    }

    private static Relation getIndexRelation(DatabaseObject databaseObject) {
        return databaseObject.getAttribute("table", Relation.class);
    }

    private static Relation getSimpleRelation(DatabaseObject databaseObject) {
        return databaseObject.getAttribute("relation", Relation.class);
    }


    public static boolean isDeletedTableOrigin(DatabaseObject databaseObject, Relation tableOrigin) {
        if (databaseObject instanceof ForeignKey) {
            List<com.querydsl.sql.ForeignKey<?>> foreignKeyList =
                    tableOrigin.getAttribute(DELETED_FOREIGN_KEYS, List.class);
            return isDeletedForeignKeys((ForeignKey) databaseObject, foreignKeyList);
        }
        if (databaseObject instanceof Index) {
            List<IndexData> indexList =
                    tableOrigin.getAttribute(DELETED_INDICES, List.class);
            return isDeletedIndex((Index) databaseObject, indexList);
        }
        return isDeleted(databaseObject, tableOrigin
                .getAttribute(DELETED_STRING_OBJECTS, List.class));
    }

    private static boolean isDeletedForeignKeys(ForeignKey foreignKey,
                                                List<com.querydsl.sql.ForeignKey<?>> foreignKeys) {
        Set<String> columns = foreignKey.getForeignKeyColumns().stream().map(Column::getName)
                .collect(Collectors.toSet());
        return CollectionUtils.isNotEmpty(foreignKeys) &&
                foreignKeys.stream().anyMatch(foreignKey0 ->
                        CollectionUtils.isEqualCollection(columns,
                                foreignKey0.getLocalColumns().stream()
                                        .map((Function<Path<?>, String>)
                                                ModelHelper::getColumnRealName)
                                        .collect(Collectors.toSet())));
    }

    private static boolean isDeleted(DatabaseObject databaseObject, List<String> removedColumns) {
        if (CollectionUtils.isNotEmpty(removedColumns)) {
            return removedColumns.stream().anyMatch(s ->
                    removedColumns.contains(StringUtils.upperCase(
                            databaseObject.getAttribute(
                                    "name", String.class))));
        }
        return false;
    }


    private static boolean isDeletedIndex(Index index, List<IndexData> indexList) {
        Set<String> columns = index.getColumns().stream().map(Column::getName)
                .collect(Collectors.toSet());
        return CollectionUtils.isNotEmpty(indexList) &&
                indexList.stream().anyMatch(index0 ->
                        CollectionUtils.isEqualCollection(columns,
                                index0.getColumns().stream()
                                        .map(ModelHelper::getColumnRealName)
                                        .collect(Collectors.toSet())));
    }
}
