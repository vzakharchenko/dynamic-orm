package com.github.vzakharchenko.dynamic.orm;

import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import org.testng.annotations.Test;
import com.github.vzakharchenko.dynamic.orm.qModel.QUser;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by vassio on 6/5/15.
 */
public class UserQueryOrm extends OracleTestQueryOrm {

    @Test(enabled = false)
    public void test() throws SQLException {

        SQLTemplates dialect = new OracleTemplates(); // SQL-dialect
        SQLQuery query = new SQLQuery(dataSource.getConnection(), dialect);
        SQLQuery clause = (SQLQuery) query.from(QUser.user)
                .select(QUser.user.name)
                .where(QUser.user.id.eq(1000));
        List<String> names = clause
                .fetch();
    }
}
