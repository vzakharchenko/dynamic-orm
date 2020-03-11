package com.github.vzakharchenko.dynamic.orm.core.dynamic.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.Schema;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileSchemaSaver implements SchemaSaver {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final File file;

    protected FileSchemaSaver(File file) {
        this.file = file;
    }

    @Override
    public void save(Schema schema) {

        try {
            byte[] value = objectMapper.writeValueAsBytes(schema);
            FileUtils.writeByteArrayToFile(file, value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
