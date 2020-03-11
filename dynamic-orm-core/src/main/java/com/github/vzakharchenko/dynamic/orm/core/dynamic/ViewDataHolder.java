package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import java.io.Serializable;

public class ViewDataHolder implements Serializable {
    private ViewModel viewModel;
    private QDynamicTable dynamicTable;

    public ViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public QDynamicTable getDynamicTable() {
        return dynamicTable;
    }

    public void setDynamicTable(QDynamicTable dynamicTable) {
        this.dynamicTable = dynamicTable;
    }
}
