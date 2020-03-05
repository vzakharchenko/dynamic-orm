package com.github.vzakharchenko.dynamic.orm.dataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 *
 */
public class OrmDataSource extends DelegatingDataSource {

    public OrmDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    private static Connection createProxy(Connection connection) {
        Connection proxy = (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionAdapterHandler(connection));
        return proxy;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = getTargetDataSource().getConnection();
            conn = createProxy(conn);
            return DBFacade.registerConnection(conn);
        } catch (SQLException ex) {
            DBFacade.printUsedConnections();
            throw ex;
        }
    }

    private static class ConnectionAdapterHandler implements InvocationHandler {

        private Connection connection;

        ConnectionAdapterHandler(Connection connection) {
            this.connection = connection;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            if (StringUtils.equalsIgnoreCase(method.getName(), "close")) {
                DBFacade.freeDBConnection(connection);
                return null;

            } else if (method.getDeclaringClass().equals(Objects.class)) {
                return method.invoke(this, args);
            } else {
                return method.invoke(connection, args);
            }
        }
    }

}
