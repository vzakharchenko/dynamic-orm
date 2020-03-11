package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import java.io.File;

public final class SchemaUtils {
    private SchemaUtils() {
    }

    public static SchemaSaver getFileSaver(File file) {
        return new FileSchemaSaver(file);
    }


    public static SchemaLoader getFileLoader(File file) {
        return new FileSchemaLoader(file);
    }


}
