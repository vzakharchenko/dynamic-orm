package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.Schema;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileSchemaLoader implements SchemaLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final File file;

    protected FileSchemaLoader(File file) {
        this.file = file;
    }

    @Override
    public Schema load() {
        try {
            return objectMapper.readValue(
                    FileUtils.readFileToByteArray(file), Schema.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
