package com.github.vzakharchenko.dynamic.orm.structure.exception;

import org.testng.annotations.Test;

public class ExceptionTest {
    @Test
    public void testExceptions() {
        DBException dbException = new DBException(new RuntimeException());
        DropAllException dropAllException = new DropAllException(new RuntimeException());
        EmptyPathToChangeSets emptyPathToChangeSets = new EmptyPathToChangeSets();
        UploadException uploadException = new UploadException(new RuntimeException());

    }
}
