/*
 * 创建日期 2005-7-13
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package nc.jdbc.framework;

import nc.bs.dao.DAOException;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.mapping.IMappingMeta;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.SuperVO;

import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.List;

/**
 * @author hey
 *         <p/>
 *         对象持久化管理器抽象类
 */
abstract public class PersistenceManager {
	protected int maxRows = 100000;

	/**
	 * 释放资源
	 */

	abstract public void release();

	/**
	 * 得到JdbcSession
	 * 
	 * @return 返回JdbcSession
	 */
	abstract public JdbcSession getJdbcSession();

	/**
	 * 把一个值对象插入到数据库中
	 * 
	 * @param vo
	 *            值对象
	 * @throws DbException
	 *             如果插入过程中发生错误则抛出异常
	 */
	abstract public String insert(final SuperVO vo) throws DbException;

	/**
	 * 把一个值对象集合插入到数据库中
	 * 
	 * @param vos
	 *            值对象集合
	 * @throws DbException
	 *             如果插入过程中发生错误则抛出异常
	 */
	abstract public String[] insert(final List vos) throws DbException;

	/**
	 * 把一个值对象数组插入到数据库中
	 * 
	 * @param vo
	 *            值对象数组
	 * @throws DAOException
	 *             如果插入过程中发生错误则抛出异常
	 */
	abstract public String[] insert(final SuperVO vo[]) throws DbException;

	/**
	 * 更新一个在数据库中已经存在值对象
	 * 
	 * @param vo
	 *            值对象
	 * @throws DAOException
	 *             如果插入过程中发生错误则抛出异常
	 */
	abstract public int update(final SuperVO vo) throws DbException;

	abstract public int update(final List vos) throws DbException;

	abstract public int update(final SuperVO vo[]) throws DbException;

	abstract public int update(final SuperVO[] vo, String[] fieldNames)
			throws DbException;

	/**
	 * @since5.5 进行update更新增加条件的处理
	 * @param vo
	 * @param fieldNames
	 * @param whereClause
	 * @param param
	 * @return
	 * @throws DbException
	 */
	abstract public int update(final SuperVO[] vo, String[] fieldNames,
			String whereClause, SQLParameter param) throws DbException;

	/**
	 * 删除一个在数据库中已经存在值对象
	 * 
	 * @param vo
	 * @throws DbException
	 */

	abstract public int delete(final SuperVO vo) throws DbException;

	abstract public int delete(final SuperVO vo[]) throws DbException;

	abstract public int delete(final List vos) throws DbException;

	abstract public int deleteByPK(Class className, String pk)
			throws DbException;

	abstract public int deleteByPKs(Class className, String[] pks)
			throws DbException;

	abstract public int deleteByClause(Class className, String wherestr)
			throws DbException;

	abstract public int deleteByClause(Class className, String wherestr,
			SQLParameter params) throws DbException;

	abstract public Collection retrieveByCorp(Class c, String pkCorp,
			String[] selectedFields) throws DbException;

	abstract public Collection retrieveByCorp(Class c, String pkCorp)
			throws DbException;

	abstract public Object retrieveByPK(Class className, String pk)
			throws DbException;

	abstract public Object retrieveByPK(Class className, String pk,
			String[] selectedFields) throws DbException;

	/**
	 * 根据VO的字段值条件查询数据
	 * 
	 * @param vo
	 * @return
	 * @throws DbException
	 */

	abstract public Collection retrieve(SuperVO vo, boolean isAnd)
			throws DbException;

	abstract public Collection retrieve(SuperVO vo, boolean isAnd,
			String[] fields) throws DbException;

	abstract public Object retrieve(SuperVO vo, boolean isAnd, String[] fields,
			ResultSetProcessor processor) throws DbException;

	/**
	 * 查询VO对应表所有数据
	 * 
	 * @param className
	 * @return
	 * @throws DbException
	 */

	abstract public Collection retrieveAll(Class className) throws DbException;

	/**
	 * 根据条件查询VO对应表数据
	 * 
	 * @param className
	 * @param condition
	 * @return
	 * @throws DbException
	 */
	abstract public Collection retrieveByClause(Class className,
			String condition, String[] fields) throws DbException;

	abstract public Collection retrieveByClause(Class className,
			String condition) throws DbException;

	abstract public int deleteByPKs(IMappingMeta meta, String[] pks)
			throws DbException;

	/**
	 * 得到数据库类型
	 * 
	 * @return
	 */

	abstract public int getDBType();

	/**
	 * 是否自动添加时间戳
	 * 
	 * @param isAddTimeStamp
	 */
	abstract public void setAddTimeStamp(boolean isAddTimeStamp);

	/**
	 * 是否进行SQL翻译
	 * 
	 * @param isTranslator
	 */
	abstract public void setSQLTranslator(boolean isTranslator);

	/**
	 * 把一个值对象数组插入到数据库中
	 * 
	 * @param vo
	 *            值对象数据
	 * @throws DAOException
	 *             如果插入过程中发生错误则抛出异常
	 */
	public abstract String[] insertObject(final Object vo[], IMappingMeta meta)
			throws DbException;

	public abstract String[] insertWithPK(final List vos) throws DbException;

	/**
	 * 把一个值对象插入到数据库中
	 * 
	 * @param vo
	 *            值对象
	 * @throws DbException
	 *             如果插入过程中发生错误则抛出异常
	 */
	public abstract String insertWithPK(final SuperVO vo) throws DbException;

	public abstract String[] insertWithPK(final SuperVO vos[])
			throws DbException;

	public abstract String insertObjectWithPK(final Object vo, IMappingMeta meta)
			throws DbException;

	public abstract String[] insertObjectWithPK(final Object vo[],
			IMappingMeta meta) throws DbException;

	/**
	 * 把一个值对象根据MappingMeta的信息插入到数据库中
	 * 
	 * @param vo
	 *            值对象
	 * @param meta
	 *            数据映射信息
	 * @return
	 * @throws DbException
	 *             如果插入数据出错则抛出DbException
	 */
	public abstract String insertObject(final Object vo, IMappingMeta meta)
			throws DbException;

	/**
	 * 把一个值对象数组根据MappingMeta的信息插入到数据库中
	 * 
	 * @param vos
	 *            值对象数组
	 * @param meta
	 *            数据映射信息
	 * @return 返回更新行数
	 * @throws DbException
	 *             如果更新数据出错则抛出DbException
	 */
	public abstract int updateObject(final Object[] vos, IMappingMeta meta)
			throws DbException;

	/**
	 * 把一个值对象数组根据MappingMeta的信息更新到数据库中
	 * 
	 * @param vo
	 *            值对象
	 * @param meta
	 *            数据映射信息
	 * @return 返回更新行数
	 * @throws DbException
	 *             如果插更新数据出错则抛出DbException
	 */
	public abstract int updateObject(final Object vo, IMappingMeta meta)
			throws DbException;

	public abstract int updateObject(final Object vo, IMappingMeta meta,
			String whereClause) throws DbException;

	public abstract int updateObject(final Object[] vo, IMappingMeta meta,
			String whereClause) throws DbException;

	/**
	 * @since 5.5增加条件的更新处理
	 * @param vo
	 * @param meta
	 * @param whereClause
	 * @param param
	 * @return
	 * @throws DbException
	 */
	public abstract int updateObject(final Object[] vo, IMappingMeta meta,
			String whereClause, SQLParameter param) throws DbException;

	/**
	 * 根据MappingMeta信息删除值对象数组对应的数据
	 * 
	 * @param vos
	 *            值对象数组
	 * @param meta
	 *            数据映射信息
	 * @throws DbException
	 *             如果删除数据出错则抛出DbException
	 */
	public abstract void deleteObject(final Object vos[], IMappingMeta meta)
			throws DbException;

	/**
	 * 根据MappingMeta信息删除值对象对应的数据
	 * 
	 * @param vo
	 *            值对象数
	 * @param meta
	 *            数据映射信息
	 * @throws DbException
	 *             如果删除数据出错则抛出DbException
	 */
	public abstract void deleteObject(final Object vo, IMappingMeta meta)
			throws DbException;

	/**
	 * 根据值对象的属性值为条件查询对应数据
	 * 
	 * @param vo
	 *            值对象
	 * @param meta
	 *            数据映射信息
	 * @return
	 * @throws DbException
	 *             如果查询出错则抛出DbException
	 */
	public abstract Collection retrieve(Object vo, IMappingMeta meta)
			throws DbException;

	/**
	 * 根据数据映射信息查询对应的所有数据
	 * 
	 * @param className
	 *            对象名
	 * @param meta
	 *            数据映射信息
	 * @return
	 * @throws DbException
	 *             如果查询出错则抛出DbException
	 */
	public abstract Collection retrieveAll(Class className, IMappingMeta meta)
			throws DbException;

	public abstract Collection retrieveByClause(Class className,
			IMappingMeta meta, String condition, String[] fields)
			throws DbException;

	public abstract Collection retrieveByClause(Class className,
			IMappingMeta meta, String condition) throws DbException;

	public abstract Collection retrieveByClause(Class className,
			String condition, String[] fields, SQLParameter parameters)
			throws DbException;

	public abstract Collection retrieveByClause(Class className,
			IMappingMeta meta, String condition, String[] fields,
			SQLParameter params) throws DbException;

	/**
	 * 得到当前数据库的DatabaseMetaData
	 * 
	 * @return DatabaseMetaData
	 */
	public abstract DatabaseMetaData getMetaData();

	/**
	 * 得到当前数据库的Catalog
	 * 
	 * @return Catalog名称
	 */
	public abstract String getCatalog();

	/**
	 * 得到当前数据库的Schema
	 * 
	 * @return Schema名称
	 */
	public abstract String getSchema();

	/**
	 * 设置当前操作为只读
	 * 
	 * @param isReadOnly
	 *            是否只读
	 * @throws DbException
	 *             如果设置出错则抛出DbException
	 */
	public abstract void setReadOnly(boolean isReadOnly) throws DbException;

	/**
	 * 
	 */
	public abstract int deleteByPK(IMappingMeta meta, String pk)
			throws DbException;

	public abstract int deleteByClause(IMappingMeta meta, String wherestr)
			throws DbException;

	public abstract int deleteByClause(IMappingMeta meta, String wherestr,
			SQLParameter params) throws DbException;

	public abstract Collection retrieveByCorp(Class c, IMappingMeta meta,
			String pkCorp) throws DbException;

	public abstract Collection retrieveByCorp(Class c, IMappingMeta meta,
			String pkCorp, String[] selectedFields) throws DbException;

	public abstract Object retrieveByPK(Class className, IMappingMeta meta,
			String pk) throws DbException;

	public abstract Object retrieveByPK(Class className, IMappingMeta meta,
			String pk, String[] selectedFields) throws DbException;

	/**
	 * 根据默认数据源得到PersistenceManager实例
	 * 
	 * @return
	 * @throws DbException
	 *             如果出错则抛出DbException
	 */
	static public PersistenceManager getInstance() throws DbException {
		return new JdbcPersistenceManager();
	}

	/**
	 * 根据传入的数据源参数得到PersistenceManager实例
	 * 
	 * @param dataSourceName
	 *            数据源名称
	 * @return
	 * @throws DbException
	 *             如果出错则抛出DbException
	 */
	static public PersistenceManager getInstance(String dataSourceName)
			throws DbException {
		return new JdbcPersistenceManager(dataSourceName);

	}

	/**
	 * 根据传入的JdbcSession参数得到PersistenceManager实例
	 * 
	 * @param session
	 *            JdbcSession参数
	 * @return
	 * @throws DbException
	 *             如果出错则抛出DbException
	 */
	static public PersistenceManager getInstance(JdbcSession session) {
		return new JdbcPersistenceManager(session);

	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

}