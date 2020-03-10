package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.SequanceModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.ViewModel;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.SequanceFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.TableFactory;
import liquibase.structure.core.Sequence;
import liquibase.structure.core.Table;
import liquibase.structure.core.View;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LiquibaseHolder {
    private final Map<String, QDynamicTable> qDynamicTables;
    private final Map<String, SequanceModel> sequanceModels;
    private final Map<String, ViewModel> viewModels;

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
}
