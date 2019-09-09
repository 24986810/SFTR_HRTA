package nc.jdbc.framework;

import nc.bs.logging.Logger;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.exception.ExceptionFactory;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.jdbc.framework.util.DBUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: 贺扬 Date: 2005-1-14 Time: 16:29:51 数据库访问对象， 提供一个统一简单灵活的数据访问API,简化数据访问操作
 */
public final class JdbcSession {
    private Connection conn = null;

    private int maxRows = 100000;

    private int dbType = 0;

    private int timeoutInSec = 0;

    private int fetchSize = 0;

    private PreparedStatement prepStatement = null;

    private Statement statement = null;

    private String lastSQL = null;

    private Batch batch = null;

    private DatabaseMetaData dbmd = null;

    private final int BATCH_SIZE = Integer.valueOf(System.getProperty("nc.maxBatch", "600"));

    private int batchIndex = 0;

    private int batchRows = 0;

    /**
     * 构造有参数JdbcSession对象
     * 
     * @param con
     *            数据库连接
     */
    public JdbcSession(Connection con) {

        dbType = DBUtil.getDbType(con);
        this.conn = con;
    }

    /**
     * 构造默认JdbcSession该JdbcSession会默认从当前访问的DataSource得到连接
     */
    public JdbcSession() throws DbException {
        try {
            Connection con = ConnectionFactory.getConnection();
            dbType = DBUtil.getDbType(con);
            // dbType = DataSourceCenter.getInstance().getDatabaseType();
            this.conn = con;
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }

    }

    /**
     * 构造JdbcSession，该JdbcSession会从指定的DataSource中得到连接
     * 
     * @param dataSourceName
     *            数据源名称
     * @throws DbException
     *             如果访问数据源出错则抛出DbException
     */
    public JdbcSession(String dataSourceName) throws DbException {
        try {
            Connection con = ConnectionFactory.getConnection(dataSourceName);
            dbType = DataSourceCenter.getInstance().getDatabaseType(dataSourceName);

            this.conn = con;
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }

    }

    /**
     * 设置是否自动添加版本(ts)信息
     * 
     * @param isAddTimeStamp
     */
    public void setAddTimeStamp(boolean isAddTimeStamp) {
        if (conn instanceof CrossDBConnection)
            ((CrossDBConnection) conn).setAddTimeStamp(isAddTimeStamp);
    }

    /**
     * 是否进行SQL翻译
     * 
     * @param isTranslator参数
     */
    public void setSQLTranslator(boolean isTranslator) {

        if (conn instanceof CrossDBConnection)
            ((CrossDBConnection) conn).enableSQLTranslator(isTranslator);
    }

    /**
     * 设置自动提交
     * 
     * @param autoCommit参数
     */
    void setAutoCommit(boolean autoCommit) throws DbException {
        try {
            conn.setAutoCommit(autoCommit);

        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }
    }

    /**
     * 得到当前连接的FetchSize大小
     * 
     * @return int 返回 FetchSize
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * 设置当前连接的FetchSize大小
     * 
     * @param fetchSize参数
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * 设置当前连接的事务级别
     * 
     * @param level参数
     */
    void setTransactionIsolation(int level) throws DbException {
        try {
            conn.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }
    }

    /**
     * 提交当前连接的事务
     */
    void commitTrans() throws DbException {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }
    }

    /**
     * 回滚当前连接的事务
     */
    void rollbackTrans() throws DbException {
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }
    }

    /**
     * 设置当前连接的只读
     * 
     * @param readOnly参数
     */
    public void setReadOnly(boolean readOnly) throws DbException {
        try {
            conn.setReadOnly(readOnly);
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }
    }

    /**
     * 当前连接的是否只读
     * 
     * @return 返回是否只读
     */
    public boolean isReadOnly() throws DbException {
        try {
            return conn.isReadOnly();
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        }
    }

    /**
     * 设置执行最大行数
     * 
     * @param maxRows
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * 得到执行最大行数
     * 
     * @return
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * 取消查询
     */
    public void cancelQuery() throws DbException {
        try {
            if (prepStatement != null)
                prepStatement.cancel();
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage());
        }
    }

    /**
     * 执行有参数查询
     * 
     * @param sql
     *            查询SQL语句
     * @param parameter
     *            查询参数
     * @param processor
     *            结果集处理对象
     * @return 查询对象
     */
    public Object executeQuery(String sql, SQLParameter parameter, ResultSetProcessor processor) throws DbException {
        // if (!isSelectStatement(sql))
        // throw new IllegalArgumentException(sql + "--不是合法的查询语句");
        Object result = null;
        ResultSet rs = null;

        try {
            if ((!sql.equalsIgnoreCase(lastSQL)) || (prepStatement == null)) {
                if (prepStatement != null) {
                    closeStmt(prepStatement);
                }
                prepStatement = conn.prepareStatement(sql);
                lastSQL = sql;
            }
            prepStatement.clearParameters();
            if (parameter != null) {
                DBUtil.setStatementParameter(prepStatement, parameter);
            }
            if (timeoutInSec > 0)
                prepStatement.setQueryTimeout(timeoutInSec);

            prepStatement.setMaxRows(maxRows > 0 ? maxRows : 0);
            if (fetchSize > 0)
                prepStatement.setFetchSize(fetchSize);
            rs = prepStatement.executeQuery();
            result = processor.handleResultSet(rs);
        }

        catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        } catch (NullPointerException e) {
            SQLException e1 = new SQLException("db connection has interrupted!");
            throw ExceptionFactory.getException(dbType, e1.getMessage(), e1);
        } finally {
            closeRs(rs);
        }
        return result;
    }

    /**
     * 执行无参数查询
     * 
     * @param sql
     *            查询SQL语句
     * @param processor
     *            结果集处理对象
     * @return 查询结果对象
     */
    public Object executeQuery(String sql, ResultSetProcessor processor) throws DbException {
        Object result = null;
        ResultSet rs = null;
        try {
            if (statement == null)
                statement = conn.createStatement();
            if (timeoutInSec > 0)
                statement.setQueryTimeout(timeoutInSec);

            statement.setMaxRows(maxRows > 0 ? maxRows : 0);

            if (fetchSize > 0)
                statement.setFetchSize(fetchSize);
            rs = statement.executeQuery(sql);
            result = processor.handleResultSet(rs);
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        } catch (NullPointerException e) {
            Logger.error("nullpoint exception", e);
            SQLException e1 = new SQLException("NullPointException cause query error");
            throw ExceptionFactory.getException(dbType, e1.getMessage(), e1);
        } finally {
            closeRs(rs);
        }
        return result;
    }

    /**
     * 执行有更新操作
     * 
     * @param sql
     *            预编译SQL语句
     * @param parameter
     *            参数对象
     * @return 变化行数
     */
    public int executeUpdate(String sql, SQLParameter parameter) throws DbException {
        int updateRows;
        try {
            if ((!sql.equalsIgnoreCase(lastSQL)) || (prepStatement == null)) {
                if (prepStatement != null) {
                    closeStmt(prepStatement);
                }
                prepStatement = conn.prepareStatement(sql);
                lastSQL = sql;
            }

            prepStatement.clearParameters();
            if (parameter != null) {
                DBUtil.setStatementParameter(prepStatement, parameter);
            }
            updateRows = prepStatement.executeUpdate();
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        } catch (NullPointerException e) {
            SQLException e1 = new SQLException("db connection has interrupted!");
            throw ExceptionFactory.getException(dbType, e1.getMessage(), e1);
        }
        return updateRows;
    }

    /**
     * 执行无更新操作
     * 
     * @param sql
     *            更新SQL语句
     * @return 更新行数
     */
    public int executeUpdate(String sql) throws DbException {
        // return executeUpdate(sql,null);
        int updateRows = 0;

        try {
            if (statement == null)
                statement = conn.createStatement();
            updateRows = statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        } catch (NullPointerException e) {
            SQLException e1 = new SQLException("db connection has interrupted!");
            throw ExceptionFactory.getException(dbType, e1.getMessage(), e1);
        }
        return updateRows;
    }

    /**
     * 添加有参数批量查询
     * 
     * @param sql
     * @param parameters
     */
    public void addBatch(String sql, SQLParameter parameters) throws DbException {
        try {
            batchIndex++;
            // 如果是第一次执行
            if (batch == null)
                batch = new Batch();
            batch.addBatch(sql, parameters);
            if (batchIndex % BATCH_SIZE == 0)
                batchRows = batchRows + executeBatch();
        } catch (SQLException e) {
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        } catch (NullPointerException e) {
            SQLException e1 = new SQLException("db connection has interrupted!");
            throw ExceptionFactory.getException(dbType, e1.getMessage(), e1);
        }

    }

    /**
     * 添加无参数批量查询
     * 
     * @param sql
     */
    public void addBatch(String sql) throws DbException {
        addBatch(sql, null);
    }

    /**
     * 执行批量更新
     * 
     * @return
     */
    public int executeBatch() throws DbException {
        try {

            int rows = 0;
            if (batch != null) {
                rows = batchRows + batch.executeBatch();
            }
            batchRows = 0;
            batchIndex = 0;
            return rows;
        } catch (SQLException e) {
            Logger.error("execute batch exception", e.getNextException());
            throw ExceptionFactory.getException(dbType, e.getMessage(), e);
        } catch (NullPointerException e) {
            SQLException e1 = new SQLException("db connection has interrupted!");
            throw ExceptionFactory.getException(dbType, e1.getMessage(), e1);
        } finally {
            if (batch != null) {
                batch.cleanupBatch();
                batch = null;
            }
        }
    }

    /**
     * 关闭数据库连接
     */
    public void closeAll() {
        closeStmt(statement);
        closeStmt(prepStatement);
        closeConnection(conn);
    }

    /**
     * 得到当前数据库的MetaData
     * 
     * @return 返回当前数据库的MetaData
     * @throws SQLException
     */
    public DatabaseMetaData getMetaData() {
        if (dbmd == null)
            try {
                dbmd = conn.getMetaData();
            } catch (SQLException e) {
                Logger.error("get metadata error", e);
            }
        return dbmd;
    }

    /**
     * 创建事物处理类
     * 
     * @return JdbcTransaction
     */
    public JdbcTransaction createTransaction() {
        return new JdbcTransaction(this);
    }

    /**
     * 私有Batch类
     */
    private class Batch {
        // private int size;
        private Map<String, PreparedStatement> statementCache = new HashMap<String, PreparedStatement>();

        private Statement batchStatement = null;

        // private int rowCount = 0;
        boolean canBatched = true;

        public Batch() {
            // this.size = 0;
            // canBatched = isSupportBatch();
        }

        public void addBatch(String sql, SQLParameter parameters) throws SQLException {
            if (parameters == null) {
                if (batchStatement == null) {
                    batchStatement = conn.createStatement();
                    batchStatement.setMaxRows(maxRows > 0 ? maxRows : 0);
                }
                batchStatement.addBatch(sql);
                return;
            }
            PreparedStatement ps = (PreparedStatement) statementCache.get(sql);
            if (ps == null) {// 如果缓存中没有该PreparedStatement
                ps = conn.prepareStatement(sql);
                ps.setMaxRows(maxRows > 0 ? maxRows : 0);
                statementCache.put(sql, ps);
            }
            DBUtil.setStatementParameter(ps, parameters);
            // if (canBatched) { //如果数据库支持批量更新
            ps.addBatch();
            // } else {//如果不支持批量更新
            // int updateRow = ps.executeUpdate();
            // rowCount += updateRow;
            // }
            // size++;
        }

        public int executeBatch() throws SQLException {
            // if (!canBatched)//如果不支持批量更新
            // return rowCount;
            // 如果支持批量更新
            int totalRowCount = 0;
            for (Iterator iterator = statementCache.values().iterator(); iterator.hasNext();) {
                PreparedStatement ps = (PreparedStatement) iterator.next();
                int[] rowCounts = ps.executeBatch();
                for (int j = 0; j < rowCounts.length; j++) {
                    if (rowCounts[j] == Statement.SUCCESS_NO_INFO) {
                        // do nothing
                    } else if (rowCounts[j] == Statement.EXECUTE_FAILED) {
                        // throw new SQLException("批量执行第 " + j + "条语句出错！");
                    } else {
                        totalRowCount += rowCounts[j];
                    }
                }
            }
            if (batchStatement != null) {
                int[] rowCounts = batchStatement.executeBatch();
                for (int j = 0; j < rowCounts.length; j++) {
                    if (rowCounts[j] == Statement.SUCCESS_NO_INFO) {
                        // do nothing
                    } else if (rowCounts[j] == Statement.EXECUTE_FAILED) {
                        // throw new SQLException("批量执行第 " + j + "条语句出错！");
                    } else {
                        totalRowCount += rowCounts[j];
                    }
                }
            }
            return totalRowCount;
        }

        /**
         * 清理批量查询
         */
        public void cleanupBatch() throws DbException {
            for (Iterator iterator = statementCache.values().iterator(); iterator.hasNext();) {
                Statement ps = (PreparedStatement) iterator.next();
                closeStmt(ps);
            }
            statementCache.clear();
            closeStmt(batchStatement);
            // size = 0;
        }
    }

    /**
     * 返回数据库连接
     * 
     * @return 返回 conn。
     */
    public Connection getConnection() {

        return conn;
    }

    /**
     * @return 返回 dbType。
     */
    public int getDbType() {
        return dbType;
    }

    private void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
        }
    }

    private void closeStmt(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
        }
    }

    private void closeRs(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
        }
    }
    // private boolean isSelectStatement(String sql) {
    // StringBuffer sb = new StringBuffer(sql.trim());
    // String s = (sb.substring(0, 6));
    // return (s.equalsIgnoreCase("SELECT"));
    // }

    // private boolean isSupportBatch() throws SQLException {
    // return getMetaData().supportsBatchUpdates();
    // }
}
