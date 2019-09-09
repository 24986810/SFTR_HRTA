package nc.bs.dao;

import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.IVOPersistence;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.mapping.IMappingMeta;
import nc.jdbc.framework.mapping.MappingMeta;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.BeanMappingListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.jdbc.framework.util.DBUtil;
import nc.vo.pub.SqlSupportVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.UpdateResultVO;

import java.lang.reflect.Array;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hey <p/> 数据库访问帮助类封装了常用的持久层访问操作
 */

final public class BaseDAO implements IVOPersistence, IUAPQueryBS {
	private String dataSource = null;

	int maxRows = 100000;

	/**
	 * 默认构造函数，将使用当前数据源
	 */
	public BaseDAO() {
	}

	/**
	 * 有参构造函数，将使用指定数据源
	 * 
	 * @param dataSource
	 *            数据源名称
	 */
	public BaseDAO(String dataSource) {
		super();
		this.dataSource = dataSource;
	}

	/**
	 * 根据SQL 执行数据库查询,并返回ResultSetProcessor处理后的对象 （非 Javadoc）
	 * 
	 * @param sql
	 *            查询的SQL
	 * @param processor
	 *            结果集处理器
	 */
	public Object executeQuery(String sql, ResultSetProcessor processor) throws DAOException {
		PersistenceManager manager = null;
		Object value = null;
		try {
			manager = createPersistenceManager(dataSource);
			JdbcSession session = manager.getJdbcSession();
			value = session.executeQuery(sql, processor);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return value;
	}

	/**
	 * 根据指定SQL 执行有参数的数据库查询,并返回ResultSetProcessor处理后的对象
	 * 
	 * @param sql
	 *            查询的SQL
	 * @param parameter
	 *            查询参数
	 * @param processor
	 *            结果集处理器
	 */
	public Object executeQuery(String sql, SQLParameter parameter, ResultSetProcessor processor) throws DAOException {
		PersistenceManager manager = null;
		Object value = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			JdbcSession session = manager.getJdbcSession();
			value = session.executeQuery(sql, parameter, processor);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return value;
	}

	/**
	 * 根据指定SQL 执行有参数的数据库更新操作
	 * 
	 * @param sql
	 *            更新的sql
	 * @param parameter
	 *            更新参数
	 * @return
	 * @throws DAOException
	 *             更新发生错误抛出DAOException
	 */
	public int executeUpdate(String sql, SQLParameter parameter) throws DAOException {
		PersistenceManager manager = null;
		int value;
		try {
			manager = createPersistenceManager(dataSource);
			JdbcSession session = manager.getJdbcSession();
			value = session.executeUpdate(sql, parameter);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return value;
	}

	/**
	 * 根据指定SQL 执行无参数的数据库更新操作
	 * 
	 * @param sql
	 *            更新的sql
	 * @return
	 * @throws DAOException
	 *             更新发生错误抛出DAOException
	 */
	public int executeUpdate(String sql) throws DAOException {
		PersistenceManager manager = null;
		int value;
		try {
			manager = createPersistenceManager(dataSource);
			JdbcSession session = manager.getJdbcSession();
			value = session.executeUpdate(sql);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return value;
	}

	/**
	 * 根据VO类名查询该VO对应表的所有数据
	 * 
	 * @param className
	 *            SuperVo类名
	 * 
	 * @return
	 * @throws DAOException
	 *             发生错误抛出DAOException
	 */
	public Collection retrieveAll(Class className) throws DAOException {
		PersistenceManager manager = null;
		Collection values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieveAll(className);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	/**
	 * 根据VO类名和where条件返回vo集合
	 * 
	 * @param className
	 *            Vo类名称
	 * @param condition
	 *            查询条件
	 * @return 返回Vo集合
	 * @throws DAOException
	 *             发生错误抛出DAOException
	 */
	public Collection retrieveByClause(Class className, String condition) throws DAOException {
		PersistenceManager manager = null;
		Collection values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieveByClause(className, condition);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	public Collection retrieveByClause(Class className, String condition, SQLParameter params) throws DAOException {
		return retrieveByClause(className, condition, (String[]) null, params);
	}

	/**
	 * 根据条件和排序返回Vo集合
	 * 
	 * @param className
	 *            VO类名
	 * @param condition
	 *            查询条件
	 * @param orderBy
	 *            排序条件
	 * @return 返回Vo集合
	 * @throws DAOException
	 *             发生错误抛出DAOException
	 */
	public Collection retrieveByClause(Class className, String condition, String orderBy) throws DAOException {
		return retrieveByClause(className, appendOrderBy(condition, orderBy), (String[]) null, null);
	}

	public Collection retrieveByClause(Class className, String condition, String orderBy, SQLParameter params)
			throws DAOException {
		return retrieveByClause(className, appendOrderBy(condition, orderBy), (String[]) null, params);
	}

	/**
	 * 根据VO类名和where条件返回指定列的vo集合
	 * 
	 * @param className
	 *            类名称
	 * @param condition
	 *            查询条件
	 * @param fields
	 *            指定的字段名
	 * 
	 */
	public Collection retrieveByClause(Class className, String condition, String[] fields) throws DAOException {
		return retrieveByClause(className, condition, fields, null);
	}

	public Collection retrieveByClause(Class className, String condition, String[] fields, SQLParameter params)
			throws DAOException {
		PersistenceManager manager = null;
		Collection values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieveByClause(className, condition, fields, params);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	/**
	 * 根据VO类名和where条件及排序条件返回指定列的vo集合
	 * 
	 * @param className
	 *            类名
	 * @param condition
	 *            查询条件
	 * @param orderBy
	 *            排序条件
	 * @param fields
	 *            指定列
	 * @return
	 * @throws DAOException
	 */
	public Collection retrieveByClause(Class className, String condition, String orderBy, String[] fields)
			throws DAOException {

		return retrieveByClause(className, appendOrderBy(condition, orderBy), fields);
	}

	public Collection retrieveByClause(Class className, String condition, String orderBy, String[] fields,
			SQLParameter params) throws DAOException {
		return retrieveByClause(className, appendOrderBy(condition, orderBy), fields, params);
	}

	private String appendOrderBy(String condition, String orderBy) {
		StringBuffer clause = new StringBuffer();
		if (condition != null)
			clause.append(condition);
		if (orderBy != null && condition == null)
			clause.append("ORDER BY ").append(orderBy);
		if (orderBy != null && condition != null) {
			clause.append(" ORDER BY ").append(orderBy);
		}

		return clause.toString();
	}

	/**
	 * 通过where条件查询所有满足条件的vo数组。 支持多表 创建日期：(2002-6-12)
	 * 
	 * @param c
	 *            Class
	 * @param strWhere
	 *            String
	 * @return SuperVO[]
	 * @throws Exception
	 *             异常说明。
	 */
	@SuppressWarnings("unchecked")
	public Object[] retrieveByClause(Class c, SqlSupportVO[] sqlvos, String fromStr, String strWhere, String strOrderBy)
			throws DAOException {
		if (sqlvos == null || sqlvos.length == 0)
			throw new NullPointerException("Sqlvos is null;");
		if (fromStr == null)
			throw new NullPointerException("fromStr is null;");
		String[][] fields = new String[2][sqlvos.length];
		MappingMeta meta = new MappingMeta("", "");
		for (int i = 0; i < sqlvos.length; i++) {
			fields[0][i] = sqlvos[i].getSqlSelectField();
			fields[1][i] = sqlvos[i].getVoAttributeName();
			meta.addMapping(sqlvos[i].getVoAttributeName(), sqlvos[i].getSqlSelectField());
		}
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(this.dataSource);
			JdbcSession session = manager.getJdbcSession();
			StringBuffer sql = new StringBuffer("SELECT ");
			for (int i = 0; i < fields[0].length; i++) {
				sql.append(fields[0][i]);
				if (i != fields[0].length - 1)
					sql.append(",");
			}
			sql.append(" FROM ").append(fromStr);

			// create where sentence
			if (strWhere != null && strWhere.trim().length() != 0) {
				sql.append(" WHERE ").append(strWhere);
			}
			// create order by sentence
			if (strOrderBy != null && strOrderBy.trim().length() != 0) {
				sql.append(" ORDER BY ").append(strOrderBy);
			}
			BaseProcessor processor = new BeanMappingListProcessor(c, meta);
			List result = (List) session.executeQuery(sql.toString(), processor);
			return result.toArray((Object[]) Array.newInstance(c, 0));
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());

		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据VO中的属性值作为匹配条件从数据库中查询该VO对应的表的数据
	 * 
	 * @param vo
	 *            要查询的VO对象
	 * @param isAnd
	 *            指定匹配条件的逻辑（true代表&&,flase代表||）
	 * @return
	 * @throws DAOException
	 *             如果查询出错抛出DAOException
	 */
	public Collection retrieve(SuperVO vo, boolean isAnd) throws DAOException {
		PersistenceManager manager = null;
		Collection values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieve(vo, isAnd);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

		return values;
	}

	/**
	 * 根据指定VO的值以及逻辑条件返回指定字段的VO集合
	 * 
	 * @param vo
	 *            条件VO
	 * @param isAnd
	 *            逻辑条件，true代表与运算false代表或运算
	 * 
	 * 
	 */
	public Collection retrieve(SuperVO vo, boolean isAnd, String[] fields) throws DAOException {
		PersistenceManager manager = null;
		Collection values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieve(vo, isAnd, fields);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	/**
	 * 根据公司名和指定字段返回VO集合
	 * 
	 * @param className
	 *            VO类名
	 * @param pkCorp
	 *            公司主键
	 * @param selectedFields
	 *            查询字段
	 * 
	 */
	public Collection retrieveByCorp(Class className, String pkCorp, String[] selectedFields) throws DAOException {
		PersistenceManager manager = null;
		Collection values = null;
		try {
			manager = createPersistenceManager(dataSource);

			values = manager.retrieveByCorp(className, pkCorp, selectedFields);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	/**
	 * 根据公司PK返回指定VO集合
	 * 
	 * @param className
	 *            VO名称
	 * @param 公司PK
	 * 
	 */
	public Collection retrieveByCorp(Class className, String pkCorp) throws DAOException {
		PersistenceManager manager = null;
		Collection values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieveByCorp(className, pkCorp);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	/**
	 * 根据PK查询指定VO
	 * 
	 * @param VO类名
	 * @param pk
	 *            主键
	 * 
	 */
	public Object retrieveByPK(Class className, String pk) throws DAOException {
		PersistenceManager manager = null;
		Object values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieveByPK(className, pk);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	/**
	 * 根据主键返回指定列的VO对象
	 */
	public Object retrieveByPK(Class className, String pk, String[] selectedFields) throws DAOException {
		PersistenceManager manager = null;
		Object values = null;
		try {
			manager = createPersistenceManager(dataSource);
			values = manager.retrieveByPK(className, pk, selectedFields);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return values;
	}

	/**
	 * 插入一个VO对象，如果该VO的主键值非空则插入VO的原有主键
	 * 
	 * @param vo
	 * @return
	 * @throws DAOException
	 */
	public String insertVOWithPK(SuperVO vo) throws DAOException {
		PersistenceManager manager = null;
		String pk = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			pk = manager.insertWithPK(vo);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return pk;
	}

	/**
	 * 插入一个VO对象
	 * 
	 * @param vo
	 *            SuperVO对象
	 * 
	 */
	public String insertVO(SuperVO vo) throws DAOException {
		PersistenceManager manager = null;
		String pk = null;
		try {
			manager = createPersistenceManager(dataSource);
			pk = manager.insert(vo);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return pk;

	}

	/**
	 * 插入一个VO数组如果该VO的主键值非空则插入VO的原有主键
	 * 
	 * @param vo
	 * @return
	 * @throws DAOException
	 */
	public String[] insertVOArrayWithPK(SuperVO[] vo) throws DAOException {
		PersistenceManager manager = null;
		String pk[] = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			pk = manager.insertWithPK(vo);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return pk;
	}

	/**
	 * 插入VO数组
	 * 
	 * @param vo
	 *            VO数组
	 */
	public String[] insertVOArray(SuperVO[] vo) throws DAOException {
		PersistenceManager manager = null;
		String pk[] = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			pk = manager.insert(vo);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return pk;
	}

	/**
	 * 插入VO集合
	 * 
	 * @param vos
	 *            VO集合
	 */
	public String[] insertVOList(List vos) throws DAOException {
		PersistenceManager manager = null;
		String pk[] = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			pk = manager.insert(vos);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return pk;
	}

	/**
	 * 根据IMappingMeta插入一个VO对象，该VO的主键值非空则插入VO的原有主键
	 * 
	 * @param vo
	 *            VO对象
	 * @param meta
	 *            IMappingMeta
	 * @return
	 * @throws DAOException
	 */
	public String insertObjectWithPK(Object vo, IMappingMeta meta) throws DAOException {

		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			return manager.insertObjectWithPK(vo, meta);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据IMappingMeta插入一个VO对象
	 * 
	 * @param vo
	 *            VO对象
	 * @param meta
	 *            IMappingMeta
	 */
	public String insertObject(Object vo, IMappingMeta meta) throws DAOException {

		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			return manager.insertObject(vo, meta);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据IMappingMeta插入VO对象集合，该VO的主键值非空则插入VO的原有主键
	 * 
	 * @param vo
	 *            VO对象集合
	 * @param meta
	 *            IMappingMeta
	 * @return
	 * @throws DAOException
	 */
	public String[] insertObjectWithPK(Object[] vo, IMappingMeta meta) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			;
			return manager.insertObjectWithPK(vo, meta);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据IMappingMeta插入VO对象集合
	 * 
	 * @param vo
	 *            VO对象集合
	 * @param meta
	 *            IMappingMeta
	 * @return
	 * @throws DAOException
	 */
	public String[] insertObject(Object[] vo, IMappingMeta meta) throws DAOException {

		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			return manager.insertObject(vo, meta);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据VO对象更新数据库
	 * 
	 * @param vo
	 *            VO对象
	 */
	public int updateVO(SuperVO vo) throws DAOException {
		return updateVOArray(new SuperVO[] { vo });
	}

	/**
	 * 根据VO对象中指定列更新数据库
	 * 
	 * @param vos
	 *            VO对象
	 * @param fieldNames
	 *            指定列
	 * @throws DAOException
	 */
	public void updateVO(SuperVO vo, String[] fieldNames) throws DAOException {
		updateVOArray(new SuperVO[] { vo }, fieldNames);
	}

	/**
	 * 根据VO对象数组更新数据库
	 * 
	 * @param vo
	 *            VO对象
	 */
	public int updateVOArray(SuperVO[] vos) throws DAOException {
		return updateVOArray(vos, null);
	}

	/**
	 * 根据VO对象数组中指定列更新数据库
	 * 
	 * @param vos
	 *            VO对象
	 * @param fieldNames
	 *            指定列
	 */
	public int updateVOArray(SuperVO[] vos, String[] fieldNames) throws DAOException {
		return updateVOArray(vos, fieldNames, null, null);

	}

	/**
	 * 根据VO对象集合更新数据库
	 * 
	 * @paramvos VO对象集合
	 */
	public void updateVOList(List vos) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.update(vos);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据VO对象和MappingMeta信息更新数据库
	 * 
	 * @param vo
	 *            VO对象
	 * @param meta
	 *            MappingMeta信息
	 */
	public int updateObject(Object vo, IMappingMeta meta) throws DAOException {
		return updateObject(vo, meta, null);
	}

	public int updateObject(Object[] vos, IMappingMeta meta) throws DAOException {
		return updateObject(vos, meta, null);
	}

	/**
	 * 在数据库中删除一组VO对象。
	 * 
	 * @param SuperVO[]
	 *            vos
	 * @throws Exception
	 *             异常说明。
	 */
	public UpdateResultVO execUpdateByVoState(SuperVO[] vos, String[] selectedFields) throws DAOException {
		ArrayList<SuperVO> listInsert = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listUpdate = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listDelete = new ArrayList<SuperVO>();
		for (int i = 0; i < vos.length; i++) {
			int status = vos[i].getStatus();
			if (status == nc.vo.pub.VOStatus.NEW)
				listInsert.add(vos[i]);
			else if (status == nc.vo.pub.VOStatus.UPDATED)
				listUpdate.add(vos[i]);
			else if (status == nc.vo.pub.VOStatus.DELETED)
				listDelete.add(vos[i]);
		}
		UpdateResultVO rsVO = new UpdateResultVO();
		if (listInsert.size() > 0) {
			rsVO.setPks(insertVOArray((SuperVO[]) listInsert.toArray(new SuperVO[listInsert.size()])));
		}

		if (listUpdate.size() > 0) {
			updateVOArray((SuperVO[]) listUpdate.toArray(new SuperVO[listUpdate.size()]), selectedFields);
		}
		if (listDelete.size() > 0) {
			deleteVOArray((SuperVO[]) listDelete.toArray(new SuperVO[listDelete.size()]));
		}
		return rsVO;
	}

	/**
	 * 在数据库中删除一组VO对象。
	 * 
	 * @param SuperVO[]
	 *            vos
	 * @throws Exception
	 *             异常说明。
	 */
	public UpdateResultVO execUpdateByVoState(SuperVO[] vos) throws DAOException {
		return execUpdateByVoState(vos, null);
	}

	/**
	 * 在数据库中删除一个VO对象。
	 * 
	 * @param vo
	 *            VO对象
	 * @throws DAOException
	 *             如果删除出错抛出DAOException
	 */
	public void deleteVO(SuperVO vo) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.delete(vo);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 在数据库中删除一组VO对象。
	 * 
	 * @param vos
	 *            VO数组对象
	 * @throws DAOException
	 *             如果删除出错抛出DAOException
	 */
	public void deleteVOArray(SuperVO[] vos) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.delete(vos);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 在数据库中根据类名和PK数组删除一组VO对象集合
	 * 
	 * @param className
	 *            要删除的VO类名
	 * @param pks
	 *            PK数组
	 * @throws DAOException
	 *             如果删除出错抛出DAOException
	 */
	public void deleteByPKs(Class className, String[] pks) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.deleteByPKs(className, pks);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 在数据库中根据类名和条件删除数据
	 * 
	 * @param className
	 *            VO类名
	 * @param wherestr
	 *            条件
	 * @throws DAOException
	 *             如果删除出错抛出DAOException
	 */
	public void deleteByClause(Class className, String wherestr) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.deleteByClause(className, wherestr);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	public void deleteByClause(Class className, String wherestr, SQLParameter params) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.deleteByClause(className, wherestr, params);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 在数据库中根据类名和PK删除一个VO对象集合
	 * 
	 * @param className
	 *            VO类名
	 * @param pk
	 *            PK值
	 * @throws DAOException
	 *             如果删除出错抛出DAOException
	 */
	public void deleteByPK(Class className, String pk) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.deleteByPK(className, pk);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 在数据库中删除一组VO对象集合
	 * 
	 * @param vos
	 *            VO对象集合
	 * @throws DAOException
	 *             如果删除出错抛出DAOException
	 */
	public void deleteVOList(List vos) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.delete(vos);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据VO中的属性值和IMappingMeta删除指定数据
	 * 
	 * @param vo
	 *            vo对象
	 * @param meta
	 *            IMappingMeta
	 * 
	 */
	public void deleteObject(Object vo, IMappingMeta meta) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.deleteObject(vo, meta);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据VO数组中的属性值和IMappingMeta删除指定数据
	 * 
	 * @param vo
	 *            vo数组
	 * @param meta
	 *            IMappingMeta
	 */
	public void deleteObject(Object[] vos, IMappingMeta meta) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			manager.deleteObject(vos, meta);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据IMappingMeta返回VO数组集合
	 * 
	 * @param vo
	 *            vo对象
	 * @param meta
	 *            IMappingMeta
	 */
	public Collection retrieve(Object vo, IMappingMeta meta) throws DAOException {

		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieve(vo, meta);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据IMappingMeta返回指定Class的VO对象集合
	 * 
	 * @param className
	 *            类名称
	 * @param meta
	 *            IMappingMeta
	 */
	public Collection retrieveAll(Class className, IMappingMeta meta) throws DAOException {

		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveAll(className, meta);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据IMappingMeta和查询条件返回指定Class的VO对象集合
	 * 
	 * @param className
	 *            类名称
	 * @param condition
	 *            查询条件
	 * @param meta
	 *            IMappingMeta
	 */
	public Collection retrieveByClause(Class className, IMappingMeta meta, String condition) throws DAOException {

		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveByClause(className, meta, condition);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	public Collection retrieveByClause(Class className, IMappingMeta meta, String condition, SQLParameter params)
			throws DAOException {
		return retrieveByClause(className, meta, condition, (String[]) null, params);
	}

	public Collection retrieveByClause(Class className, IMappingMeta meta, String condition, String[] fields,
			SQLParameter params) throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveByClause(className, meta, condition, fields, params);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据条件和 IMappingMeta查询返回指定列的VO集合
	 * 
	 * @param className
	 *            类名
	 * @param meta
	 *            IMappingMeta
	 * @param condition
	 *            指定条件
	 * @param fields
	 *            查询列
	 */
	public Collection retrieveByClause(Class className, IMappingMeta meta, String condition, String[] fields)
			throws DAOException {

		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveByClause(className, meta, condition, fields);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据IMappingMeta和传入条件删除数据
	 * 
	 * @param meta
	 * @param wherestr
	 * @return
	 * @throws DAOException
	 */
	public int deleteByClause(IMappingMeta meta, String wherestr) throws DAOException {
		return deleteByClause(meta, wherestr, null);
	}

	public int deleteByClause(IMappingMeta meta, String wherestr, SQLParameter params) throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			return manager.deleteByClause(meta, wherestr, params);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据PK和IMappingMeta删除指定数据
	 * 
	 * @param meta
	 *            IMappingMeta
	 * @param pk
	 *            主键
	 * @return
	 * @throws DAOException
	 */
	public int deleteByPK(IMappingMeta meta, String pk) throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.deleteByPK(meta, pk);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据IMappingMeta和公司pk返回指定列的VO集合
	 * 
	 * @param c
	 *            VO类名称
	 * @param meta
	 *            IMappingMeta
	 * @param pkCorp
	 *            公司PK
	 * @param selectedFields
	 *            指定字段数组
	 * @return
	 * @throws DAOException
	 */
	public Collection retrieveByCorp(Class c, IMappingMeta meta, String pkCorp, String[] selectedFields)
			throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveByCorp(c, meta, pkCorp, selectedFields);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据公司PK和IMappingMeta返回对应VO集合
	 * 
	 * @param c
	 *            VO类
	 * @param meta
	 *            IMappingMeta
	 * @param pkCorp
	 *            公司PK
	 * @return
	 * @throws DAOException
	 */
	public Collection retrieveByCorp(Class c, IMappingMeta meta, String pkCorp) throws DAOException {

		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveByCorp(c, meta, pkCorp);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据IMappingMeta和pk返回指定字段对应的VO对象
	 * 
	 * @param className
	 *            VO类名
	 * @param meta
	 *            IMappingMeta
	 * @param pk
	 *            主键
	 * @return
	 * @throws DAOException
	 */
	public Object retrieveByPK(Class className, IMappingMeta meta, String pk, String[] selectedFields)
			throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveByPK(className, meta, pk, selectedFields);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据IMappingMeta和pk返回对应的VO对象
	 * 
	 * @param className
	 *            VO类名
	 * @param meta
	 *            IMappingMeta
	 * @param pk
	 *            主键
	 * @return
	 * @throws DAOException
	 */
	public Object retrieveByPK(Class className, IMappingMeta meta, String pk) throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.retrieveByPK(className, meta, pk);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 根据 IMappingMeta和条件更新VO对象对应的数据库
	 * 
	 * @param vos
	 *            VO对象
	 * @param meta
	 *            IMappingMeta
	 * @param whereClause
	 *            条件语句
	 * @return
	 * @throws DAOException
	 */
	public int updateObject(Object vo, IMappingMeta meta, String whereClause) throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.updateObject(vo, meta, whereClause);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	/**
	 * 根据 IMappingMeta和条件更新VO对象数组对应的数据库
	 * 
	 * @param vos
	 *            VO对象
	 * @param meta
	 *            IMappingMeta
	 * @param whereClause
	 *            条件语句
	 * @return
	 * @throws DAOException
	 */
	public int updateObject(Object[] vos, IMappingMeta meta, String whereClause) throws DAOException {
		return updateObject(vos, meta, whereClause, null);
	}

	/**
	 * 根据 IMappingMeta和条件更新VO对象数组对应的数据库
	 * 
	 * @param vos
	 *            VO对象
	 * @param meta
	 *            IMappingMeta
	 * @param whereClause
	 *            条件语句
	 * @return
	 * @throws DAOException
	 */
	public int updateObject(Object[] vos, IMappingMeta meta, String whereClause, SQLParameter param)
			throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.updateObject(vos, meta, whereClause, param);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

	/**
	 * 获得数据源类型
	 * 
	 * @param key
	 *            nc.vo.pub.oid.OID
	 * @throws java.sql.SQLException
	 *             异常说明。
	 */
	public int getDBType() {

		return DataSourceCenter.getInstance().getDatabaseType(dataSource);

	}

	/**
	 * 返回数据库相关的表名
	 * 
	 * @param dbType
	 *            int
	 * @param tableName
	 *            java.lang.String
	 * @return java.lang.String
	 * @since ：V1.00
	 */
	protected String getTableName(int dbType, String tableName) {
		String strTn = null;
		switch (dbType) {
		case DBConsts.SQLSERVER:
			strTn = tableName;
			break;
		case DBConsts.ORACLE:
		case DBConsts.DB2:
			// ORACLE需将表名大写
			strTn = tableName.toUpperCase();
			break;
		}
		return strTn;
	}

	/**
	 * 判断数据表是否存在
	 * 
	 * @param tableName
	 *            数据表名称
	 * @return
	 * @throws DAOException
	 *             出错抛出DAOException
	 */
	public boolean isTableExisted(String tableName) throws DAOException {
		if (tableName == null)
			throw new NullPointerException("TableName is null!");
		PersistenceManager manager = null;
		ResultSet rs = null;
		try {
			manager = createPersistenceManager(dataSource);
			int dbType = manager.getDBType();
			DatabaseMetaData dbmd = manager.getMetaData();

			// 获得表名信息
			rs = dbmd.getTables(manager.getCatalog(), manager.getSchema(), getTableName(dbType, tableName),
					new String[] { "TABLE" });
			while (rs.next()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			DBUtil.closeRs(rs);
			if (manager != null)
				manager.release();
		}
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	/**
	 * Modified by cch , add this method 根据PK和IMappingMeta删除指定数据
	 * 
	 * @param meta
	 *            IMappingMeta
	 * @param pk
	 *            主键
	 * @return
	 * @throws DAOException
	 */
	public int deleteByPKs(IMappingMeta meta, String[] pks) throws DAOException {
		PersistenceManager manager = null;

		try {
			manager = createPersistenceManager(dataSource);
			return manager.deleteByPKs(meta, pks);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

	}

	private PersistenceManager createPersistenceManager(String ds) throws DbException {
		PersistenceManager manager = PersistenceManager.getInstance(ds);
		manager.setMaxRows(maxRows);
		return manager;
	}

	public int updateVOArray(final SuperVO[] vos, String[] fieldNames, String whereClause, SQLParameter param)
			throws DAOException {
		PersistenceManager manager = null;
		try {
			manager = createPersistenceManager(dataSource);
			return manager.update(vos, fieldNames, whereClause, param);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
	}

}
