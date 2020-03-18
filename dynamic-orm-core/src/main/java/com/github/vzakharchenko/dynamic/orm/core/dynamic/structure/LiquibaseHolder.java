package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.SequanceModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.ViewModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.SequanceFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.TableFactory;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Relation;
import liquibase.structure.core.Sequence;
import liquibase.structure.core.Table;
import liquibase.structure.core.View;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class LiquibaseHolder {
    private final Map<String, QDynamicTable> qDynamicTables;
    private final Map<String, SequanceModel> sequanceModels;
    private final Map<String, ViewModel> viewModels;
    private final Set<String> removedTables = new HashSet<>();
    private final Set<String> removedSequences = new HashSet<>();

    private LiquibaseHolder(Map<String, QDynamicTable> qDynamicTables,
                            Map<String, SequanceModel> sequanceModels,
                            Map<String, ViewModel> viewModels) {
        this.qDynamicTables = qDynamicTables;
        this.sequanceModels = sequanceModels;
        this.viewModels = viewModels;
    }

    public static LiquibaseHolder create(Map<String, QDynamicTable> qDynamicTables,
                                         Map<String, SequanceModel> sequanceModels,
                                         Map<String, ViewModel> viewModels) {
        return new LiquibaseHolder(qDynamicTables, sequanceModels, viewModels);
    }

    public List<Table> getTables() {
        return qDynamicTables.values().stream()
                .map(TableFactory::createTable)
                .collect(Collectors.toList());
    }

    public List<Sequence> getSequencies() {
        return sequanceModels.values()
                .stream()
                .map(SequanceFactory::createSequance)
                .collect(Collectors.toList());
    }

    public List<View> getViews() {
        return viewModels.values().stream().map(viewModel -> {
            View view = new View(
                    "",
                    "",
                    viewModel.getName());
            view.setDefinition(viewModel.getSql());
            view.setContainsFullDefinition(true);
            return view;
        }).collect(Collectors.toList());
    }

    public void addRemovedSequences(Set<String> sequences) {
        removedSequences.addAll(sequences);
    }

    public void addRemovedTables(Set<String> tables) {
        removedTables.addAll(tables);
    }

    public boolean isDeletedObject(DatabaseObject databaseObject) {
        if (databaseObject instanceof Relation) {
            return isDeletedRelation((Relation) databaseObject);
        } else if (databaseObject instanceof Sequence) {
            return isDeletedSequence((Sequence) databaseObject);
        }
        return false;
    }

    public boolean isDeletedSequence(Sequence sequence) {
        return removedSequences.stream().anyMatch(sequenceName ->
                StringUtils.equalsIgnoreCase(sequenceName, sequence.getName()));
    }

    public boolean isDeletedRelation(Relation relation) {
        return removedTables.stream().anyMatch(tableName ->
                StringUtils.equalsIgnoreCase(tableName, relation.getName()));
    }

}
