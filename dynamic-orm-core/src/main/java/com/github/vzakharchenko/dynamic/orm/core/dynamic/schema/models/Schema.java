package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models;

import java.io.Serializable;
import java.util.List;

public class Schema implements Serializable {
    private String version = "1.0.1";
    private List<SchemaTable> tables;
    private List<SchemaSequence> sequences;
    private List<SchemaView> views;

    public List<SchemaTable> getTables() {
        return tables;
    }

    public void setTables(List<SchemaTable> tables) {
        this.tables = tables;
    }

    public List<SchemaSequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<SchemaSequence> sequences) {
        this.sequences = sequences;
    }

    public List<SchemaView> getViews() {
        return views;
    }

    public void setViews(List<SchemaView> views) {
        this.views = views;
    }

    public String getVersion() {
        return version;
    }
}
