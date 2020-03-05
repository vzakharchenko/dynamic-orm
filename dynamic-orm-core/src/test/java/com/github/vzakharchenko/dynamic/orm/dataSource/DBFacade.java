package com.github.vzakharchenko.dynamic.orm.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 */
public abstract class DBFacade {
    public static final int MAX_CONNS = 50;
    private static final Logger LOGGER = LoggerFactory.getLogger(DBFacade.class);
    private static final Map<Connection, StackTrace> CONNECTIONS =
            new ConcurrentReferenceHashMap<>();

    public static void freeDBConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                LOGGER.warn("Could not free connection", e);
            }
            unregisterConnection(conn);
        }
    }


    /**
     * Returns the number of connections in the dynamic pool.
     */
    protected static int getDBConnBrokerSize() {
        return CONNECTIONS.size();
    }


    /**
     * Register DB connection object for logging and monitoring
     */
    public static Connection registerConnection(Connection conn) throws SQLException {
        try {
            if (conn == null || conn.isClosed()) {
                throw new SQLException("getDBConnection error");
            }
            conn.setAutoCommit(false);
            int numOfConns = getDBConnectionsInUse();
            if (numOfConns >= (MAX_CONNS - 5)) {
                LOGGER.warn("Facade.getDBConnection number of connections used: " + numOfConns);
                LOGGER.warn("Facade.getDBConnection pool size: " + getDBConnBrokerSize());
                LOGGER.warn("Facade.getDBConnection max conns available: " + MAX_CONNS);
            }
            CONNECTIONS.put(conn, new StackTrace("New connection from " +
                    Thread.currentThread().getName(),
                    System.currentTimeMillis(), new Exception().getStackTrace()));
            LOGGER.debug("Open. Number of connections open right now = " + CONNECTIONS.size());
            if (CONNECTIONS.size() > MAX_CONNS >> 1) {
                for (Connection connection : CONNECTIONS.keySet()) {
                    if (connection.isClosed()) {
                        CONNECTIONS.remove(connection);
                    }
                }
            }
            return conn;

        } catch (SQLException e) {
            printUsedConnections();
            throw e;
        }
    }

    private static void unregisterConnection(Connection conn) {
        CONNECTIONS.remove(conn);
        LOGGER.debug("Close. Number of connections open right now = " + CONNECTIONS.size());
    }


    public static void printUsedConnections() {
        StringBuilder buf = new StringBuilder("Open DB connections:\n");
        for (Map.Entry<Connection, StackTrace> entry : CONNECTIONS.entrySet()) {
            try {
                if (entry.getKey().isClosed()) {
                    continue;
                }
            } catch (SQLException ignore) {
                continue;
            }
            buf.append(entry.getValue().toString()).append('\n');
        }
        LOGGER.info(buf.toString());
    }

    /**
     * Obtain DB connection pool object in use total Creation date: (3/5/00 10:12:43 PM)
     */
    public static int getDBConnectionsInUse() {
        int dbConnectionsInUse = 0;
        for (Map.Entry<Connection, StackTrace> entry : CONNECTIONS.entrySet()) {
            try {
                if (entry.getKey().isClosed()) {
                    continue;
                }
            } catch (SQLException ignore) {
                continue;
            }
            dbConnectionsInUse++;
        }
        return dbConnectionsInUse;
    }


}
