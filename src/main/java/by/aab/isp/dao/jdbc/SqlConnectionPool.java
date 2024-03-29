package by.aab.isp.dao.jdbc;

import by.aab.isp.dao.DaoException;

import java.sql.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

//TODO: add force close connection by timeout feature
public class SqlConnectionPool implements DataSource {
    
    private final String url;
    private final String user;
    private final String password;
    private final SqlDialect dialect;
    private final AtomicInteger connectionsCount = new AtomicInteger(0);
    private final PooledConnection[] connections;
    private final BlockingQueue<PooledConnection> pool;

    private static final Pattern SCHEMA_PATTERN = Pattern.compile("^jdbc:([a-z]+):");

    public SqlConnectionPool(String url, String user, String password, int poolSize) {
        this.url = url;
        this.user = user;
        this.password = password;
        String schema = SCHEMA_PATTERN.matcher(url)
                .results()
                .map(result -> result.group(1))
                .findFirst()
                .orElseThrow();
        dialect = SqlDialect.valueOf(schema.toUpperCase());
        connections = new PooledConnection[poolSize];
        pool = new ArrayBlockingQueue<>(poolSize);
        init();
    }

    void init() {
        try {
            Class.forName(dialect.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        }        
    }
    
    @Override
    public void close() {
        pool.clear();
        Arrays.stream(connections)
                .filter(Objects::nonNull)
                .forEach(PooledConnection::doClose);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            PooledConnection result = pool.poll();
            if (result != null) {
                result.isInPool.set(false);
                return result;
            }
            for (int count = connectionsCount.get(); count < connections.length; count = connectionsCount.get()) {
                if (connectionsCount.compareAndSet(count, count + 1)) {
                    result = new PooledConnection(DriverManager.getConnection(url, user, password));
                    connections[count] = result;
                    return result;
                }
            }
            result = pool.take();
            result.isInPool.set(false);
            return result; 
        } catch (InterruptedException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public SqlDialect getDialect() {
        return dialect;
    }

    private class PooledConnection implements Connection {
        
        private final AtomicBoolean isInPool = new AtomicBoolean(false);
        private final Connection connection;
        
        PooledConnection(Connection connection) {
            this.connection = connection;
        }

        public <T> T unwrap(Class<T> iface) throws SQLException {
            return connection.unwrap(iface);
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return connection.isWrapperFor(iface);
        }

        public Statement createStatement() throws SQLException {
            return connection.createStatement();
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return connection.prepareStatement(sql);
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            return connection.prepareCall(sql);
        }

        public String nativeSQL(String sql) throws SQLException {
            return connection.nativeSQL(sql);
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            connection.setAutoCommit(autoCommit);
        }

        public boolean getAutoCommit() throws SQLException {
            return connection.getAutoCommit();
        }

        public void commit() throws SQLException {
            connection.commit();
        }

        public void rollback() throws SQLException {
            connection.rollback();
        }

        public void close() throws SQLException {
            try {
                if (isInPool.compareAndSet(false, true)) {
                    pool.put(this);
                }
            } catch (InterruptedException e) {
                throw new DaoException(e);
            }
        }

        private void doClose() {
            try {
                connection.close();
            } catch (SQLException ignore) {
            }
        }

        public boolean isClosed() throws SQLException {
            return connection.isClosed();
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            return connection.getMetaData();
        }

        public void setReadOnly(boolean readOnly) throws SQLException {
            connection.setReadOnly(readOnly);
        }

        public boolean isReadOnly() throws SQLException {
            return connection.isReadOnly();
        }

        public void setCatalog(String catalog) throws SQLException {
            connection.setCatalog(catalog);
        }

        public String getCatalog() throws SQLException {
            return connection.getCatalog();
        }

        public void setTransactionIsolation(int level) throws SQLException {
            connection.setTransactionIsolation(level);
        }

        public int getTransactionIsolation() throws SQLException {
            return connection.getTransactionIsolation();
        }

        public SQLWarning getWarnings() throws SQLException {
            return connection.getWarnings();
        }

        public void clearWarnings() throws SQLException {
            connection.clearWarnings();
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return connection.createStatement(resultSetType, resultSetConcurrency);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return connection.getTypeMap();
        }

        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            connection.setTypeMap(map);
        }

        public void setHoldability(int holdability) throws SQLException {
            connection.setHoldability(holdability);
        }

        public int getHoldability() throws SQLException {
            return connection.getHoldability();
        }

        public Savepoint setSavepoint() throws SQLException {
            return connection.setSavepoint();
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            return connection.setSavepoint(name);
        }

        public void rollback(Savepoint savepoint) throws SQLException {
            connection.rollback(savepoint);
        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            connection.releaseSavepoint(savepoint);
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return connection.prepareStatement(sql, autoGeneratedKeys);
        }

        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return connection.prepareStatement(sql, columnIndexes);
        }

        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return connection.prepareStatement(sql, columnNames);
        }

        public Clob createClob() throws SQLException {
            return connection.createClob();
        }

        public Blob createBlob() throws SQLException {
            return connection.createBlob();
        }

        public NClob createNClob() throws SQLException {
            return connection.createNClob();
        }

        public SQLXML createSQLXML() throws SQLException {
            return connection.createSQLXML();
        }

        public boolean isValid(int timeout) throws SQLException {
            return connection.isValid(timeout);
        }

        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            connection.setClientInfo(name, value);
        }

        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            connection.setClientInfo(properties);
        }

        public String getClientInfo(String name) throws SQLException {
            return connection.getClientInfo(name);
        }

        public Properties getClientInfo() throws SQLException {
            return connection.getClientInfo();
        }

        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return connection.createArrayOf(typeName, elements);
        }

        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return connection.createStruct(typeName, attributes);
        }

        public void setSchema(String schema) throws SQLException {
            connection.setSchema(schema);
        }

        public String getSchema() throws SQLException {
            return connection.getSchema();
        }

        public void abort(Executor executor) throws SQLException {
            connection.abort(executor);
        }

        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            connection.setNetworkTimeout(executor, milliseconds);
        }

        public int getNetworkTimeout() throws SQLException {
            return connection.getNetworkTimeout();
        }

        public void beginRequest() throws SQLException {
            connection.beginRequest();
        }

        public void endRequest() throws SQLException {
            connection.endRequest();
        }

        public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout)
                throws SQLException {
            return connection.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
        }

        public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
            return connection.setShardingKeyIfValid(shardingKey, timeout);
        }

        public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
            connection.setShardingKey(shardingKey, superShardingKey);
        }

        public void setShardingKey(ShardingKey shardingKey) throws SQLException {
            connection.setShardingKey(shardingKey);
        }
        
    }

}
