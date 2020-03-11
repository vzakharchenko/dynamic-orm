package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.core.exception.NoActionExeption;
import com.github.vzakharchenko.dynamic.orm.core.exception.NoRowExeption;
import com.querydsl.sql.SQLCommonQuery;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.AssertJUnit.assertEquals;

public class DBHelperTest {

    @Test(expectedExceptions = NoActionExeption.class)
    public void testDBHelper1() {
        DBHelper.invokeExceptionIfNoAction(1, 2);
    }

    @Test(expectedExceptions = NoRowExeption.class)
    public void testDBHelper2() {
        DBHelper.invokeExceptionIfNoAction(0);
    }

    @Test
    public void testDBHelper3() {
        DBHelper.invokeExceptionIfNoAction(1);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testDBHelper4() {
        SQLCommonQuery sqlCommonQuery = mock(SQLCommonQuery.class);
        DBHelper.castProjectionQueryToSqlQuery(sqlCommonQuery);
    }

    @Test()
    public void testDBHelper5() {
        try {
            DBHelper.invokeExceptionIfNoAction(1, 2);
        } catch (NoActionExeption actionExeption) {
            assertEquals(actionExeption.getRowsExpected(), 2);
            assertEquals(actionExeption.getRowsAffected(), 1);
        }
    }
}
