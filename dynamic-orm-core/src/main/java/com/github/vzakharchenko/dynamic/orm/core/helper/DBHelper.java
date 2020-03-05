package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLQuery;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.github.vzakharchenko.dynamic.orm.core.exception.IsNotActiveTransaction;
import com.github.vzakharchenko.dynamic.orm.core.exception.NoActionExeption;
import com.github.vzakharchenko.dynamic.orm.core.exception.NoRowExeption;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 21:44
 */
public abstract class DBHelper {


    public static void invokeExceptionIfNoAction(long rowsAffected, long rowsExpected) {
        if (rowsAffected == 0) {
            throw new NoRowExeption();
        }
        if (!Objects.equals(rowsExpected, rowsAffected)) {
            throw new NoActionExeption(rowsAffected, rowsExpected);
        }
    }

    public static void invokeExceptionIfNoAction(long rowsAffected) {
        if (rowsAffected == 0) {
            throw new NoRowExeption();
        }
    }

    public static void transactionCheck() {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IsNotActiveTransaction();
        }

    }

    public static SQLQuery<?> castProjectionQueryToSqlQuery(SQLCommonQuery<?> sqlQuery) {
        if (!(sqlQuery instanceof SQLQuery)) {
            throw new UnsupportedOperationException("unSupport query type " + sqlQuery);
        }
        return (SQLQuery) sqlQuery;
    }


}
