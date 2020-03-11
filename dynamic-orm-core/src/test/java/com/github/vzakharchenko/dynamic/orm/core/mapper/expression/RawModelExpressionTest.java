package com.github.vzakharchenko.dynamic.orm.core.mapper.expression;

import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersionAnnotation;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertNotNull;

public class RawModelExpressionTest {
    @Test
    public void testRawModelExpression(){
        RawModelExpression rawModelExpression = RawModelExpression.createFromTables(Arrays.asList(QTesttable.testtable,
                QTestTableVersionAnnotation.qTestTableVersionAnnotation));
        assertNotNull(rawModelExpression);
    }
}
