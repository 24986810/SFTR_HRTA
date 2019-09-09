package nc.impl.hi.hi_301;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.dao.DAOException;
import nc.bs.hr.utils.setdict.HRResHiDictHelper;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.mw.sqltrans.TempTable;
import nc.hr.utils.PubEnv;
import nc.itf.hi.HIDelegator;
import nc.itf.hr.comp.IParValue;
import nc.itf.hr.pub.HROperatorSQLHelper;
import nc.itf.hr.pub.PubDelegator;
import nc.jdbc.framework.crossdb.CrossDBPreparedStatement;
import nc.jdbc.framework.crossdb.CrossDBResultSet;
import nc.jdbc.framework.crossdb.CrossDBResultSetMetaData;
import nc.vo.bd.b04.DeptdocVO;
import nc.vo.hi.hi_301.CtrlDeptVO;
import nc.vo.hi.hi_301.GeneralVO;
import nc.vo.hi.hi_306.DocApplyHVO;
import nc.vo.hr.formulaset.BusinessFuncParser_sql;
import nc.vo.hr.tools.pub.CommonVO;
import nc.vo.hr.tools.pub.CommonVOProcessor;
import nc.vo.hr.validate.IDValidateUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.rs.MemoryResultSet;

/**
 * 新人员采集、维护的DMO。 创建日期：(2004-5-9 19:20:29)
 * 
 * @author：Administrator
 */

public class PsnInfDMO extends nc.bs.pub.DataManageObject {
	
	//	数据库类型
	public final static int DB2 = 0;
	public final static int ORACLE = 1;
	public final static int SQLSERVER = 2;
	public final static int SYBASE = 3;
	public final static int UNKOWNDATABASE = -1;
	
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2002-12-13 15:50:53)
	 * @return int
	 */
	private int getDataBaseType() {
		try {
			String dpn = getConnection().getMetaData().getDatabaseProductName();
			if (dpn.toUpperCase().indexOf("DB2") != -1)
				return DB2;
			if (dpn.toUpperCase().indexOf("ORACLE") != -1)
				return ORACLE;
			if (dpn.toUpperCase().indexOf("SYBASE") != -1)
				return SYBASE;
			if (dpn.toUpperCase().indexOf("SQL") != -1)
				return SQLSERVER;
			if (dpn.toUpperCase().indexOf("ACCESS") != -1)
				return SQLSERVER;
			return UNKOWNDATABASE;
		} catch (Exception e) {
			reportException(e);
			return -1;
		}
	}
	/**
	 * CtrlDeptDMO 构造子注解。
	 * 
	 * @exception javax.naming.NamingException
	 *                异常说明。
	 * @exception nc.bs.pub.SystemException
	 *                异常说明。
	 */
	public PsnInfDMO() throws javax.naming.NamingException,
			nc.bs.pub.SystemException {
		super();
	}
	
	public String getOldPsnDocPKOfRef(GeneralVO psndocVO) throws SQLException{
		String oldpk=null;
		Connection conn = null;
		PreparedStatement stmt = null;
		String pk_corp = (String)psndocVO.getAttributeValue("pk_corp");
		String pk_psnbasdoc = (String)psndocVO.getAttributeValue("pk_psnbasdoc");
		String sql = "select pk_psndoc from bd_psndoc where isreferenced ='Y' and pk_corp ='"+pk_corp+"' and pk_psnbasdoc = '" + pk_psnbasdoc + "'";//where psnclscope =0
		try {
			// 获取连接
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				oldpk = rs.getString(1);
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return oldpk;
	}

	/**
	 * CtrlDeptDMO 构造子注解。
	 * 
	 * @param dbName
	 *            java.lang.String
	 * @exception javax.naming.NamingException
	 *                异常说明。
	 * @exception nc.bs.pub.SystemException
	 *                异常说明。
	 */
	public PsnInfDMO(String dbName) throws javax.naming.NamingException,
			nc.bs.pub.SystemException {
		super(dbName);
	}
	/**
	 * 
	 * @throws javax.naming.NamingException
	 * @throws nc.bs.pub.SystemException
	 */
	public void batchUpatePsnCode(GeneralVO[] psnvo) throws java.sql.SQLException{
		if(psnvo ==null){
			return;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			// 获取连接
			conn = getConnection();
			String sql = " update bd_psndoc set psncode =? where pk_psndoc =?";
			sql = HROperatorSQLHelper.getSQLWithOperator(sql);
			stmt = prepareStatement(conn,sql);
			for(int i=0;i<psnvo.length;i++){
				stmt.setString(1,(String)psnvo[i].getAttributeValue("psncode"));
				stmt.setString(2,(String)psnvo[i].getAttributeValue("pk_psndoc"));
				stmt.executeUpdate();
			}
			executeBatch(stmt);
		
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 
	 */
	public void batchUpdate(String[] pk_psndocs, String tableCode,
			String fieldCode, Object value) throws java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			// 获取连接
			conn = getConnection();
			TempTable tt = new TempTable();
//			tt.dropTempTable(conn, "temppsntable");
			String temptable = tt.createTempTable(conn, "temppsntable", "pk_psndoc char(20),ts char(19),dr int", null);
			String tempsql = "insert into "+temptable+"(pk_psndoc) values (?)";
			stmt = prepareStatement(conn,tempsql);
			for(int i=0;i<pk_psndocs.length;i++){
				stmt.setString(1,pk_psndocs[i]);
				stmt.executeUpdate();
			}
			executeBatch(stmt);
			// 组织sql语句，并检查是否含有blob类型字段
//			for (int i = 0; i < pk_psndocs.length; i++) {
				String tableCode1 = tableCode;
				if (tableCode1.equalsIgnoreCase("hi_psndoc_part")) {
					tableCode = "hi_psndoc_deptchg";
				}
				String sql = "update " + tableCode + " set ";
				sql += fieldCode + "=";
				if (BSUtil.isSelfDef(fieldCode))
					sql += "'" + value.toString() + "'";
				else if (BSUtil.isNumeric(value))
					sql += value.toString();
				else
					sql += "'" + value.toString() + "'";
				sql += " where ";
				if (tableCode1.equalsIgnoreCase("hi_psndoc_deptchg")) {
					sql += " recordnum=0 and jobtype=0 and ";
				} else if (tableCode1.equalsIgnoreCase("hi_psndoc_part")) {
					sql += " recordnum=0 and jobtype>0 and ";// wangkf fixed
				} else if (!tableCode.equalsIgnoreCase("bd_psnbasdoc")
						&& !tableCode.equalsIgnoreCase("bd_psndoc")) {
					if(!tableCode.equalsIgnoreCase("hi_psndoc_edu")){
						sql += " recordnum=0 and ";
					}else{
						sql += " lasteducation='Y' and ";
					}
				}
				if (isTraceTable(tableCode)){
			     sql += "pk_psndoc in (select distinct pk_psndoc from "+temptable+")";
			    } else {
			     sql += "pk_psnbasdoc in (select distinct pk_psndoc from "+temptable+")";
			    }
				
				sql = HROperatorSQLHelper.getSQLWithOperator(sql);
				
				stmt = conn.prepareStatement( sql);
				stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	private boolean isTraceTable(String tableCode){
		if (tableCode.equalsIgnoreCase("hi_psndoc_deptchg")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_ctrt")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_part")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_training")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_ass")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_retire")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_orgpsn")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_psnchg")
			      ||tableCode.equalsIgnoreCase("hi_psndoc_dimission")
			      ||tableCode.equalsIgnoreCase("bd_psndoc")
			      ){
			return true;
		}else
			return false;
	}
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean hasPerson()throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = " select psncode from bd_psndoc group by psncode having count(pk_psnbasdoc)>1 ";//where psnclscope =0
		boolean hasperson = false;
		try {
			// 获取连接
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				hasperson = true;
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return hasperson;
	}
	/**
	 * 
	 * @param pk_deptdoc
	 * @return
	 * @throws SQLException
	 */
	public boolean isDeptCancled(String pk_deptdoc)throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = " select canceled from bd_deptdoc  where pk_deptdoc =? ";
		boolean iscancle = false;
		try {
			// 获取连接
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_deptdoc);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String cancle = rs.getString(1);
				if(cancle!=null && "Y".equalsIgnoreCase(cancle.trim())){
					iscancle =true;
				}
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return iscancle;
	}
	
	public boolean isDeptDrop(String pk_deptdoc) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = " select hrcanceled from bd_deptdoc  where pk_deptdoc =? ";
		boolean isdrop = false;
		try {
			// 获取连接
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_deptdoc);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String drop = rs.getString(1);
				if(drop!=null && "Y".equalsIgnoreCase(drop.trim())){
					isdrop =true;
				}
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return isdrop;
	}
	
	/**
	 * 
	 * @param pk_deptdoc
	 * @return
	 * @throws SQLException
	 */
	public boolean isJobAbort(String pk_om_job)throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = " select isabort from om_job  where pk_om_job =? ";
		boolean isabort = false;
		try {
			// 获取连接
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_om_job);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String abort = rs.getString(1);
				if(abort!=null && "Y".equalsIgnoreCase(abort.trim())){
					isabort =true;
				}
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return isabort;
	}
	/**
	 * 此处插入方法描述。 创建日期：(2004-7-15 9:31:50)
	 * 
	 * @return boolean
	 * @param vo
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public int checkPsn(GeneralVO vo, Integer flag)
			throws java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = null;
		String sPsnCodeUnique = "N";
		try {
			IParValue parvalue = PubDelegator.getIParValue();
			sPsnCodeUnique = parvalue.getParaString("0001", "HI_CODEUNIQUE");

			if (flag.intValue() == 0) {
				sql = "select 1 from bd_psndoc where pk_psnbasdoc = ? and indocflag=? and dr = 0";
			} else if (flag.intValue() == 1) {
				if ("N".equalsIgnoreCase(sPsnCodeUnique)|| sPsnCodeUnique == null) {
					Object pk_psnbasdoc = vo.getAttributeValue("pk_psnbasdoc");
					if(pk_psnbasdoc!=null){
						sql = "select 1 from bd_psndoc where psncode = ? and dr = 0 and pk_corp = ? and pk_psnbasdoc <> '"+pk_psnbasdoc.toString()+"'";
					}else{
						sql = "select 1 from bd_psndoc where psncode = ? and dr = 0 and pk_corp = ? ";
					}
				} else {
					Object pk_psnbasdoc = vo.getAttributeValue("pk_psnbasdoc");
					if(pk_psnbasdoc!=null){
						sql = "select 1 from bd_psndoc where psncode = ? and dr = 0 and pk_psnbasdoc <> '"+pk_psnbasdoc.toString()+"'";
					}else{
						sql = "select 1 from bd_psndoc where psncode = ? and dr = 0 ";
					}
				}
			} else if (flag.intValue() == 2) {
				int idLen = vo.getFieldValue("id") == null ? 0 : ((String) vo.getFieldValue("id")).trim().length();
				if (idLen == 18) {
					String id = ((String) vo.getFieldValue("id")).trim();
					String id15 = id.substring(0, 6) + id.substring(8, 17);
					String twoid = id + "' or ltrim(rtrim(id)) = '" + id15;
					sql = "select 1 from hi_psndoc_bad where ( ltrim(rtrim(id)) = '" + twoid+ "') and dr = 0 and psnname = ? and delflag = 0";
				} else {
					sql = "select 1 from hi_psndoc_bad where ltrim(rtrim(id)) = ? and dr = 0 and psnname = ? and delflag = 0";
				}
			} else if (flag.intValue() == 3) {
				sql = "select 1 from bd_psndoc where dr = 0 and psncode = ? ";
				if ("N".equalsIgnoreCase(sPsnCodeUnique)|| sPsnCodeUnique == null)
					sql += "and pk_corp = '"+ (String) vo.getFieldValue("pk_corp") + "'";
			} else if (flag.intValue() == 4) {
				String name = (String) vo.getAttributeValue("psnname");
				String pk_psnbasdoc = (String) vo.getAttributeValue("pk_psnbasdoc");
				String id = ((String) vo.getFieldValue("id")).trim();
				String whereid = "";
				if (id.length() == 18) {
					String id15 = id.substring(0, 6) + id.substring(8, 17);
					whereid = "(ltrim(rtrim(bd_psnbasdoc.id))||bd_psndoc.psnname = '"
							+ id
							+ name
							+ "' or ltrim(rtrim(bd_psnbasdoc.id))||bd_psndoc.psnname = '"
							+ id15 + name + "')";
				} else {
					whereid = "ltrim(rtrim(bd_psnbasdoc.id))||bd_psndoc.psnname = '"+ id + name + "'";
				}
				sql = "select 1 from bd_psndoc inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc where "
						+ whereid
						+ "and bd_psndoc.pk_psnbasdoc <> '"
						+ pk_psnbasdoc + "'";
			} else if (flag.intValue() == 5) {
				if ("N".equalsIgnoreCase(sPsnCodeUnique)|| sPsnCodeUnique == null) {
					sql = "select 1 from bd_psndoc where psncode = ? and dr = 0 and pk_psnbasdoc <> ? and pk_corp = ?";
				} else {
					sql = "select 1 from bd_psndoc where psncode = ? and dr = 0 and pk_psnbasdoc <> ?";
				}

			}
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (flag.intValue() == 0) {
				stmt.setString(1, (String) vo.getFieldValue("pk_psnbasdoc"));
				stmt.setString(2, (String) vo.getFieldValue("indocflag").toString());
				ResultSet rs = stmt.executeQuery();
				if (!rs.next()) {
					return 0;
				}
			} else if (flag.intValue() == 1) {
				if ("N".equalsIgnoreCase(sPsnCodeUnique)|| sPsnCodeUnique == null) {
					stmt.setString(1, (String) vo.getFieldValue("psncode"));
					stmt.setString(2, (String) vo.getFieldValue("pk_corp"));
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						return 1;
					}
				} else {
					stmt.setString(1, (String) vo.getFieldValue("psncode"));
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						return 1;
					}
				}
			} else if (flag.intValue() == 2) {
				int idLen = ((String) vo.getFieldValue("id")).trim().length();
				if (idLen == 18) {
					stmt.setString(1, (String) vo.getFieldValue("psnname"));
				} else {
					stmt.setString(1, ((String) vo.getFieldValue("id")).trim());
					stmt.setString(2, ((String) vo.getFieldValue("psnname")).trim());
				}
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return 2;
				}
			} else if (flag.intValue() == 3) {
				stmt.setString(1, ((String) vo.getFieldValue("psncode")).trim());
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return 3;
				}
			} else if (flag.intValue() == 4) {
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return 4;
				}
			} else if (flag.intValue() == 5) {
				if ("N".equalsIgnoreCase(sPsnCodeUnique)|| sPsnCodeUnique == null) {
					stmt.setString(1, (String) vo.getFieldValue("psncode"));
					stmt.setString(2, (String) vo.getFieldValue("pk_psnbasdoc"));
					stmt.setString(3, (String) vo.getFieldValue("pk_corp"));
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						return 5;
					}
				} else {
					stmt.setString(1, (String) vo.getFieldValue("psncode"));
					stmt.setString(2, (String) vo.getFieldValue("pk_psnbasdoc"));
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						return 5;
					}
				}
			}

		} catch (Exception ex) {
			return -1;
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return -1;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-7 17:14:15)
	 * 
	 * @param tableCode
	 *            java.lang.String
	 * @param where
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void deleteData(String tableCode, String where)
			throws java.sql.SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			if (tableCode.equalsIgnoreCase("hi_psndoc_part")) {
				tableCode = "hi_psndoc_deptchg";
				StringUtil.replaceAllString/*AllString*/(where,
						"hi_psndoc_part", "hi_psndoc_deptchg");
			}
			String sql = "delete from " + tableCode
					+ (where == null ? "" : " where " + where);
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-7 17:14:15)
	 * 
	 * @param tableCode
	 *            java.lang.String
	 * @param where
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public int deletePsnValidate(String pk_psnbasdoc)
			throws java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 0;
		try {

			String sql = "select count(pk_psndoc) from bd_psndoc where pk_psnbasdoc = ?";
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_psnbasdoc);
			rs = stmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return count;
	}

	/**
	 * 删除人员表信息。 创建日期：(2004-5-19 10:34:24)
	 * 
	 * @param tableCode
	 *            java.lang.String
	 * @param pk_psndoc
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void deletePsnData(String tableCode, String pk_psndoc)
			throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "delete", new Object[] {
				tableCode, pk_psndoc });
		/** ********************************************************** */

		Connection conn = null;
		Statement stmt = null;
		try {
			String sql = "delete from " + tableCode + " where pk_psndoc='"
					+ pk_psndoc + "'";
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "delete", new Object[] {
				tableCode, pk_psndoc });
		/** ********************************************************** */
	}

	/**
	 * 删除人员时校验存在两条管理档案以上就不让删除
	 * 
	 * @param corpPK
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String[] getAllUserCode(String corpPK) throws java.sql.SQLException {

		String[] code = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		Vector<String> v = new Vector<String>();
		try {
			// V55 modify 校验集团所有编码不能唯一
			String sql = "select pk_corp,user_code from sm_user"; // where pk_corp = '0001' or pk_corp = ?";
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			// stmt.setString(1, corpPK);
			result = stmt.executeQuery();
			while (result.next()) {
				v.addElement(result.getString(1) + result.getString(2));
			}
			code = new String[v.size()];
			v.copyInto(code);
		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return code;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-8 17:14:17)
	 * 
	 * @return java.lang.Object
	 * @param tableName
	 *            java.lang.String
	 * @param pkField
	 *            java.lang.String
	 * @param codeField
	 *            java.lang.String
	 * @param pk
	 *            java.lang.Object
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public Object getValue(String tableName, String pkField, String vField,
			Object pk) throws java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			if (tableName.equalsIgnoreCase("hi_psndoc_part")) {
				tableName = "hi_psndoc_deptchg";
			}
			String sql = "select " + vField + " from " + tableName + " where "
					+ pkField + "=?";
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setObject(1, pk);
			result = stmt.executeQuery();
			if (result.next())
				return result.getObject(1);
			return null;
		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 此函数
	 * 
	 * @param pk_psndoc
	 * @param table
	 * @return
	 * @throws java.sql.SQLException
	 * @throws nc.bs.pub.SystemException
	 * 查询时增加查询任职信息中的“备注”字段。 fengwei 2009-09-23
	 */
	public GeneralVO[] queryDeptChgInfos(String pk_psndoc,String isreturn)
			throws java.sql.SQLException, nc.bs.pub.SystemException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "queryDeptChgInfos",
				new Object[] { pk_psndoc });
		/** ********************************************************** */
		GeneralVO[] subInfoVOs = null;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		Vector v = new Vector();
		try {

			String sql = " select hi_psndoc_deptchg.begindate,hi_psndoc_deptchg.enddate,hi_psndoc_deptchg.pk_corp as pk_corp,om_duty.pk_om_duty as pk_om_duty ";
			sql += ",bd_deptdoc.pk_deptdoc as pk_deptdoc,om_job.pk_om_job as pk_postdoc,hi_psndoc_deptchg.lastflag,hi_psndoc_deptchg.recordnum,hi_psndoc_deptchg.pk_psnbasdoc,hi_psndoc_deptchg.pk_psndoc_sub "; 
			sql += ",bd_corp.unitname,om_duty.dutyname,bd_deptdoc.deptname,om_job.jobname, hi_psndoc_deptchg.memo from hi_psndoc_deptchg ";
			sql += " left outer join bd_corp on bd_corp.pk_corp = hi_psndoc_deptchg.pk_corp ";
			sql += " left outer join om_duty on  om_duty.pk_om_duty = hi_psndoc_deptchg.pk_om_duty ";
			sql += " left outer join bd_deptdoc on bd_deptdoc.pk_deptdoc = hi_psndoc_deptchg.pk_deptdoc ";
			sql += " left outer join om_job on om_job.pk_om_job = hi_psndoc_deptchg.pk_postdoc ";
			sql += " where hi_psndoc_deptchg.pk_psndoc = ? and hi_psndoc_deptchg.jobtype = 0";
			if (isreturn != null && "Y".equalsIgnoreCase(isreturn)) {
				sql += " and hi_psndoc_deptchg.isreturn = 'Y' and hi_psndoc_deptchg.recordnum = 0 and lastflag ='Y' ";
			}
			con = getConnection();
			stmt = con.prepareStatement(sql);
			// set PK fields:
			stmt.setString(1, pk_psndoc);
			result = stmt.executeQuery();
			while (result.next()) {
				GeneralVO vo = new GeneralVO();
				vo.setAttributeValue("pk_psndoc", pk_psndoc);
				vo.setAttributeValue("begindate", result.getString(1));
				vo.setAttributeValue("enddate", result.getString(2));
				vo.setAttributeValue("pk_corp", result.getString(3));
				vo.setAttributeValue("pk_om_duty", result.getString(4));
				vo.setAttributeValue("pk_deptdoc", result.getString(5));
				vo.setAttributeValue("pk_postdoc", result.getString(6));
				vo.setAttributeValue("lastflag", result.getString(7));
				vo.setAttributeValue("recordnum", new Integer(result.getInt(8)));
				vo.setAttributeValue("pk_psnbasdoc", result.getString(9));
				vo.setAttributeValue("pk_psndoc_sub", result.getString(10));
				vo.setAttributeValue("unitname", result.getString(11));
				vo.setAttributeValue("dutyname", result.getString(12));
				vo.setAttributeValue("deptname", result.getString(13));
				vo.setAttributeValue("jobname", result.getString(14));
				vo.setAttributeValue("memo", result.getString(15));//增加查询“备注”字段
				v.addElement(vo);

			}
			if (v.size() > 0) {
				subInfoVOs = new GeneralVO[v.size()];
				v.copyInto(subInfoVOs);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "queryDeptChgInfos",
				new Object[] { pk_psndoc });
		/** ********************************************************** */

		return subInfoVOs;
	}

	/**
	 * 更新子集表的记录号和标记 wangkf add 创建日期：(2004-7-1 14:50:27)
	 * 
	 * @return java.lang.String
	 * @param deptchgVO
	 *            nc.vo.hi.hi_301.HRMainVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public int updateRecornum(String table, Integer num, String pk_psndoc)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "updateRecornum",
				new Object[] { table, num, pk_psndoc });
		/** ********************************************************** */
		if (num == null) {
			return 0;
		}
		Connection con = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {
			String sql = " update " + table + " set recordnum=recordnum+"
					+ num.intValue() + ",lastflag='N' where pk_psndoc= ?";

			con = getConnection();
			stmt = con.prepareStatement(sql);

			if (pk_psndoc == null) {
				stmt.setNull(1, Types.CHAR);
			} else {
				stmt.setString(1, pk_psndoc);
			}
			count = stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "updateRecornum",
				new Object[] { table, num, pk_psndoc });
		/** ********************************************************** */

		return count;
	}

	/**
	 * 更新子集表的记录号和标记 wangkf add 创建日期：(2004-7-1 14:50:27)
	 * 
	 * @return java.lang.String
	 * @param deptchgVO
	 *            nc.vo.hi.hi_301.HRMainVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public int updateRecornumforWork(String table, Integer num, String pk_psnbasdoc)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "updateRecornum",
				new Object[] { table, num, pk_psnbasdoc });
		/** ********************************************************** */
		if (num == null) {
			return 0;
		}
		Connection con = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {
			String sql = " update " + table + " set recordnum=recordnum+"+ num.intValue() + ",lastflag='N' where pk_psnbasdoc= ?";

			con = getConnection();
			stmt = con.prepareStatement(sql);

			if (pk_psnbasdoc == null) {
				stmt.setNull(1, Types.CHAR);
			} else {
				stmt.setString(1, pk_psnbasdoc);
			}
			count = stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "updateRecornum",
				new Object[] { table, num, pk_psnbasdoc });
		/** ********************************************************** */

		return count;
	}
	/**
	 * 更新任职表的记录号和标记 ，不处理兼职的记录
	 * 
	 * @param num
	 * @param pk_psndoc
	 * @return
	 * @throws java.sql.SQLException
	 * @throws nc.bs.pub.SystemException
	 * @throws BusinessException 
	 */
	public UFDate updateDeptchgRecornum(int num, String pk_psndoc,UFDate onpostdate,UFDate indutydate)
			throws java.sql.SQLException, nc.bs.pub.SystemException, BusinessException {

		Connection con = null;
		PreparedStatement stmt = null;
		UFDate begindate = null;
		UFDate date = onpostdate==null?indutydate:onpostdate;
		UFDate oldbegindate = null;
		try {
			String sqlQ = " select enddate, begindate from hi_psndoc_deptchg where pk_psndoc = ? and jobtype= 0 and recordnum = 0 ";

			
			String sqlU = " update hi_psndoc_deptchg set recordnum = recordnum + "
				+ num + ",lastflag='N', poststat = 'N' where pk_psndoc= ? and jobtype= 0";			
			con = getConnection();

			// 查询上一条任职结束日期
			stmt = con.prepareStatement(sqlQ);
			stmt.setString(1, pk_psndoc);
			ResultSet rs = stmt.executeQuery();
			//任职开始日期规则：如果是第一次任职，首先取最新到岗日期，如果没有，则取到职日期。如果不是第一次任职，则只取最新到岗日期。
			if (rs.next()) {
				String strDate = rs.getString(1);
				// 如果存在任职记录，且任职记录结束日期为空或者到职日期在最新任职记录结束日期之前时，不写入任职开始日期
//				if (strDate == null
//				|| (!indutydate.after(new UFDate(strDate.trim())))) {
//				begindate = null;

				if (date != null &&strDate != null
						&& (!date.after(new UFDate(strDate.trim())))) {
					begindate = null;
				} else {
					begindate = date;
				}
				
				oldbegindate = new UFDate(rs.getString(2));
				
			}else{
				begindate = date;
			}

			//置最后一条历史的结束日期为最新到岗日期-1。
			if(onpostdate != null && oldbegindate != null){
				if(!onpostdate.after(oldbegindate)){
					throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("600704", "UPT600704-000360")/*
							 * @res
							 * "最新到岗日期应晚于历史任职开始日期！"*/);
				}
				String updatefirsthistorysql = "update hi_psndoc_deptchg set enddate='"+onpostdate.getDateBefore(1).toString()+"' where pk_psndoc = '"+pk_psndoc+"' and jobtype= 0 and recordnum = 0 and enddate is null";
				stmt = con.prepareStatement(updatefirsthistorysql);
				stmt.executeUpdate();
			}
			// 更新历史记录
			stmt = con.prepareStatement(sqlU);
			if (pk_psndoc == null) {
				stmt.setNull(1, Types.CHAR);
			} else {
				stmt.setString(1, pk_psndoc);
			}
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return begindate;
	}

	/**
	 * 把任职表中记录插入到履历表 --改进算法，支持批量处理 wangkf add 创建日期：(2004-7-1 14:50:27)
	 * 
	 * @return java.lang.String
	 * @param deptchgVO
	 *            nc.vo.hi.hi_301.HRMainVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 *插入时，将任职信息中的“备注”字段插入。fengwei 2009-09-23
	 */
	public String[] insertHiPsnWork(GeneralVO[] deptchgVOs)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "insertHiPsnWork",
				new Object[] { deptchgVOs });
		/** ********************************************************** */

		String[] keys = null;
		Connection con = null;
		PreparedStatement stmt = null;
		// ResultSet rs = null;
		// int[] counts = null;
		try {
			con = getConnection();

			keys = new String[deptchgVOs.length];
			String sql = "insert into hi_psndoc_work ";
			sql += " (pk_psndoc_sub,pk_psndoc,begindate,enddate,workcorp,workjob,workdept,workpost,lastflag,recordnum,pk_psnbasdoc,pk_deptchg, memo) ";// lastflag,recordnum
			sql += " values( ?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			
			sql = HROperatorSQLHelper.getSQLWithOperator(sql);
			
			stmt = prepareStatement(con, sql);

			for (int i = 0; i < deptchgVOs.length; i++) {
				keys[i] = getOID();
				// set PK fields:
				stmt.setString(1, keys[i]);
				stmt.setString(2, (String) deptchgVOs[i]
						.getAttributeValue("pk_psndoc"));
				// 开始日期
				if (deptchgVOs[i].getAttributeValue("begindate") == null) {
					stmt.setNull(3, Types.CHAR);
				} else {
					stmt.setString(3, (String) deptchgVOs[i]
							.getAttributeValue("begindate"));
				}
				// 结束日期
				if (deptchgVOs[i].getAttributeValue("enddate") == null) {
					stmt.setNull(4, Types.CHAR);
				} else {
					stmt.setString(4, (String) deptchgVOs[i]
							.getAttributeValue("enddate"));
				}
				// 工作单位workcorp
				if (deptchgVOs[i].getAttributeValue("unitname") == null) {
					stmt.setNull(5, Types.CHAR);
				} else {
					stmt.setString(5, (String) deptchgVOs[i]
							.getAttributeValue("unitname"));
				}
				// 工作职务 workjob
				if (deptchgVOs[i].getAttributeValue("dutyname") == null) {
					stmt.setNull(6, Types.CHAR);
				} else {
					stmt.setString(6, (String) deptchgVOs[i]
							.getAttributeValue("dutyname"));
				}
				// 工作部门 workdept
				if (deptchgVOs[i].getAttributeValue("deptname") == null) {
					stmt.setNull(7, Types.CHAR);
				} else {
					stmt.setString(7, (String) deptchgVOs[i]
							.getAttributeValue("deptname"));
				}
				// 工作岗位 workpost
				if (deptchgVOs[i].getAttributeValue("jobname") == null) {// 实际上是名称
					stmt.setNull(8, Types.CHAR);
				} else {
					stmt.setString(8, (String) deptchgVOs[i]
							.getAttributeValue("jobname"));
				}
				// --
				// 更新标记
				if (deptchgVOs[i].getAttributeValue("lastflag") == null) {
					stmt.setNull(9, Types.CHAR);
				} else {
					stmt.setString(9, (String) deptchgVOs[i]
							.getAttributeValue("lastflag"));
				}
				// 记录号
				if (deptchgVOs[i].getAttributeValue("recordnum") == null) {// 实际上是名称
					stmt.setNull(10, Types.INTEGER);
				} else {
					stmt.setInt(10, ((Integer) deptchgVOs[i]
							.getAttributeValue("recordnum")).intValue());
				}
				// 人员档案主键
				if (deptchgVOs[i].getAttributeValue("pk_psnbasdoc") == null) {
					stmt.setNull(11, Types.CHAR);
				} else {
					stmt.setString(11, (String) deptchgVOs[i]
							.getAttributeValue("pk_psnbasdoc"));
				}
				// 任职记录主键
				if (deptchgVOs[i].getAttributeValue("pk_psndoc_sub") == null) {
					stmt.setNull(12, Types.CHAR);
				} else {
					stmt.setString(12, (String) deptchgVOs[i]
							.getAttributeValue("pk_psndoc_sub"));
				}
				//备注 增加插入“备注”字段
				if(deptchgVOs[i].getAttributeValue("memo") == null) {
					stmt.setNull(13, Types.CHAR);
				} else {
					stmt.setString(13, (String) deptchgVOs[i].getAttributeValue("memo"));
				}
				stmt.executeUpdate();
				//executeUpdate(stmt);// stmt.addBatch();
			}

			//
			stmt.executeBatch();//executeBatch(stmt); // stmt.executeBatch();

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "insertHiPsnWork",
				new Object[] { deptchgVOs });
		/** ********************************************************** */

		return keys;
	}

	/**
	 * 把任职表中记录插入到履历表 wangkf add 创建日期：(2004-7-1 14:50:27)
	 * 
	 * @return java.lang.String
	 * @param deptchgVO
	 *            nc.vo.hi.hi_301.HRMainVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public String insertHiPsnWork(GeneralVO deptchgVO)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "insertHiPsnWork",
				new Object[] { deptchgVO });
		/** ********************************************************** */
		String key = null;
		Connection con = null;
		PreparedStatement stmt = null;
		// ResultSet rs = null;
		try {

			String sql = "insert into hi_psndoc_work ";
			sql += "	(pk_psndoc_sub,pk_psndoc,begindate,enddate,workcorp,workjob,workdept,workpost,lastflag,recordnum) ";// lastflag,recordnum
			sql += " values( ?,?,?,?,?,?,?,?,?,?) ";

			sql = HROperatorSQLHelper.getSQLWithOperator(sql);
			
			key = getOID();
			con = getConnection();

			stmt = con.prepareStatement(sql);
			// set PK fields:
			stmt.setString(1, key);
			stmt
					.setString(2, (String) deptchgVO
							.getAttributeValue("pk_psndoc"));
			// 开始日期
			if (deptchgVO.getAttributeValue("begindate") == null) {
				stmt.setNull(3, Types.CHAR);
			} else {
				stmt.setString(3, (String) deptchgVO
						.getAttributeValue("begindate"));
			}
			// 结束日期
			if (deptchgVO.getAttributeValue("enddate") == null) {
				stmt.setNull(4, Types.CHAR);
			} else {
				stmt.setString(4, (String) deptchgVO
						.getAttributeValue("enddate"));
			}
			// 工作单位workcorp
			if (deptchgVO.getAttributeValue("pk_corp") == null) {
				stmt.setNull(5, Types.CHAR);
			} else {
				stmt.setString(5, (String) deptchgVO
						.getAttributeValue("pk_corp"));
			}
			// 工作职务 workjob
			if (deptchgVO.getAttributeValue("pk_om_duty") == null) {
				stmt.setNull(6, Types.CHAR);
			} else {
				stmt.setString(6, (String) deptchgVO
						.getAttributeValue("pk_om_duty"));
			}
			// 工作部门 workdept
			if (deptchgVO.getAttributeValue("pk_deptdoc") == null) {
				stmt.setNull(7, Types.CHAR);
			} else {
				stmt.setString(7, (String) deptchgVO
						.getAttributeValue("pk_deptdoc"));
			}
			// 工作岗位 workpost
			if (deptchgVO.getAttributeValue("pk_postdoc") == null) {// 实际上是名称
				stmt.setNull(8, Types.CHAR);
			} else {
				stmt.setString(8, (String) deptchgVO
						.getAttributeValue("pk_postdoc"));
			}
			// --
			// 更新标记
			if (deptchgVO.getAttributeValue("lastflag") == null) {
				stmt.setNull(9, Types.CHAR);
			} else {
				stmt.setString(9, (String) deptchgVO
						.getAttributeValue("lastflag"));
			}
			// 记录号
			if (deptchgVO.getAttributeValue("recordnum") == null) {// 实际上是名称
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, ((Integer) deptchgVO
						.getAttributeValue("recordnum")).intValue());
			}
			//
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "insertHiPsnWork",
				new Object[] { deptchgVO });
		/** ********************************************************** */

		return key;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-7-1 14:50:27)
	 * 
	 * @return java.lang.String
	 * @param psndocMain
	 *            nc.vo.hi.hi_301.HRMainVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public String insertDeptChg(GeneralVO psndocVO,String[] fieldnames,HashMap hash)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "insertDeptChd",
				new Object[] { psndocVO });
		/** ********************************************************** */
		String key = null;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			String sql = "insert into hi_psndoc_deptchg ";
			sql += "(pk_psndoc_sub ,pk_psndoc, pk_corp,pk_deptdoc,pk_psncl,recordnum,lastflag,pk_jobrank,pk_jobserial" +
					",pk_om_duty,pk_detytype,pk_postdoc,begindate,pk_psnbasdoc,pk_dutyrank,isreturn";
			if(fieldnames!=null && fieldnames.length>0){
				for(int i=0 ;i<fieldnames.length;i++){
					sql+= ","+hash.get(fieldnames[i]);
				}
			}
			sql+=" )";
			sql += "values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
			if(fieldnames!=null && fieldnames.length>0){
				for(int i=0 ;i<fieldnames.length;i++){
					sql+= ",?";
				}
			}
			sql+=" )";
			
			sql = HROperatorSQLHelper.getSQLWithOperator(sql);
			
			key = getOID();
			con = getConnection();
			stmt = con.prepareStatement(sql);
			// set PK fields:
			stmt.setString(1, key);
			stmt.setString(2, (String) psndocVO.getFieldValue("pk_psndoc"));
			stmt
					.setString(3, (String) psndocVO
							.getFieldValue("belong_pk_corp"));
			if (psndocVO.getFieldValue("pk_deptdoc") == null) {
				stmt.setNull(4, Types.CHAR);
			} else {
				stmt
						.setString(4, (String) psndocVO
								.getFieldValue("pk_deptdoc"));
			}
			stmt.setString(5, (String) psndocVO.getFieldValue("pk_psncl"));
			stmt.setInt(6, 0);
			stmt.setString(7, "Y");
			// 20030723 ZHJ ADD
			// jobrank accpsndocVO-->psndocVO
			if (psndocVO.getFieldValue("jobrank") != null) {
				stmt.setString(8, psndocVO.getFieldValue("jobrank").toString());
			} else {
				stmt.setNull(8, Types.CHAR);
			}
			// jobseries
			if (psndocVO.getFieldValue("jobseries") != null) {
				stmt.setString(9, psndocVO.getFieldValue("jobseries")
						.toString());
			} else {
				stmt.setNull(9, Types.CHAR);
			}
			// pk_om_duty
			if (psndocVO.getFieldValue("dutyname") != null) {
				stmt.setString(10, psndocVO.getFieldValue("dutyname")
						.toString());
			} else {
				stmt.setNull(10, Types.CHAR);
			}
			// series
			if (psndocVO.getFieldValue("series") != null) {
				stmt.setString(11, psndocVO.getFieldValue("series").toString());
			} else {
				stmt.setNull(11, Types.CHAR);
			}
			// pk_om_job wangkf fixed accpsndocVO-> psndocVO
			if (psndocVO.getFieldValue("pk_om_job") != null) {
				stmt.setString(12, psndocVO.getFieldValue("pk_om_job")
						.toString());
			} else {
				stmt.setNull(12, Types.CHAR);
			}
			// indutydate
			if (psndocVO.getFieldValue("indutydate") != null) {
				stmt.setString(13, (String) psndocVO
						.getFieldValue("indutydate").toString());
			} else {
				stmt.setNull(13, Types.CHAR);
			}
			stmt.setString(14, (String) psndocVO.getFieldValue("pk_psnbasdoc"));
			// indutydate
			if (psndocVO.getFieldValue("pk_dutyrank") != null) {
				stmt.setString(15, (String) psndocVO
						.getFieldValue("pk_dutyrank").toString());
			} else {
				stmt.setNull(15, Types.CHAR);
			}
			if (psndocVO.getFieldValue("isreturn") != null) {
				stmt.setString(16, (String) psndocVO
						.getFieldValue("isreturn").toString());
			} else {
				stmt.setNull(16, Types.CHAR);
			}
			if(fieldnames!=null && fieldnames.length>0){
				for(int i=0 ;i<fieldnames.length;i++){
					String newname =fieldnames[i];
					if(fieldnames[i].indexOf(".")!=-1){
						//fieldnames[i] = fieldnames[i].substring(fieldnames[i].indexOf(".")+1);
						newname = fieldnames[i].substring(fieldnames[i].indexOf(".")+1);
					}
					
					Object o= psndocVO.getAttributeValue(newname);
					if(o!=null){
						stmt.setObject(17+i, psndocVO.getAttributeValue(newname));
					}else{
						stmt.setNull(17+i,Types.CHAR);
					}
				}
			}
			//
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "insertDeptChd",
				new Object[] { psndocVO });
		/** ********************************************************** */

		return key;
	}
	/**
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws java.sql.SQLException
	 * @throws nc.bs.pub.SystemException
	 */
	public String queryDimissionDate(String pk_psndoc)throws java.sql.SQLException, nc.bs.pub.SystemException {
		String sqldate = "select leavedate from hi_psndoc_dimission where pk_psndoc = ? and recordnum= 0 and lastflag ='Y'";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String leavedate = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sqldate);
			stmt.setString(1, pk_psndoc);
			rs = stmt.executeQuery();
			while (rs.next()) {
				leavedate = rs.getString(1);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}		
		return leavedate;
	}
	/**
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws java.sql.SQLException
	 * @throws nc.bs.pub.SystemException
	 */
	public String queryOutdutyDate(String pk_psndoc)throws java.sql.SQLException, nc.bs.pub.SystemException {
		String sqldate = "select outdutydate from bd_psndoc where pk_psndoc = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String outduty = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sqldate);
			stmt.setString(1, pk_psndoc);
			rs = stmt.executeQuery();
			while (rs.next()) {
				outduty = rs.getString(1);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}		
		return outduty;
	}
	/**
	 * 此处插入方法描述。 创建日期：(2004-7-1 14:50:27)
	 * 
	 * @return java.lang.String
	 * @param psndocMain
	 *            nc.vo.hi.hi_301.HRMainVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public String insertDismissionChd(GeneralVO psndocVO,String leavedate,GeneralVO deptchginfo,String[] fieldnames,HashMap hash)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsndocMainDMO","insertDismissionChd", new Object[] { psndocVO,leavedate });
		/** ********************************************************** */
		String key = null;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			String sql = "insert into hi_psndoc_dimission ";
			sql += "(pk_psndoc_sub ,pk_psndoc, pk_corp, pkdeptafter, pkdeptbefore,recordnum,lastflag,pkomdutybefore,pkpostbefore,psnclafter,pk_psnbasdoc,leavedate,psnclbefore,pk_corpafter ";
			if(fieldnames!=null && fieldnames.length>0){
				for(int i=0 ;i<fieldnames.length;i++){
					sql+= ","+hash.get(fieldnames[i]);
				}
			}
			sql+=" )";
			sql += "values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?";
			if(fieldnames!=null && fieldnames.length>0){
				for(int i=0 ;i<fieldnames.length;i++){
					sql+= ",?";
				}
			}
			sql+=" )";
			
			sql = HROperatorSQLHelper.getSQLWithOperator(sql);
			
			key = getOID();
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, (String) psndocVO.getFieldValue("pk_psndoc"));
			stmt.setString(3, (String) psndocVO.getFieldValue("belong_pk_corp"));
			if (psndocVO.getFieldValue("pk_deptdoc") == null) {
				stmt.setNull(4, Types.CHAR);
			} else {
				stmt.setString(4, (String) psndocVO.getFieldValue("pk_deptdoc"));
			}
			if(deptchginfo !=null && deptchginfo.getAttributeValue("pk_deptdoc")!=null){
				stmt.setString(5, (String)deptchginfo.getAttributeValue("pk_deptdoc"));
			} else {
				if (psndocVO.getFieldValue("pk_deptdoc") == null) {
					stmt.setNull(5, Types.CHAR);
				} else {
					stmt.setString(5, (String) psndocVO.getFieldValue("pk_deptdoc"));
				}
			}
			stmt.setInt(6, 0);
			stmt.setString(7, "Y");
			if(deptchginfo !=null){
				if( deptchginfo.getAttributeValue("pk_om_duty")!=null){
					stmt.setString(8, (String)deptchginfo.getAttributeValue("pk_om_duty"));
				}else{
					stmt.setNull(8, Types.CHAR);
				}
			} else {
				if (psndocVO.getFieldValue("dutyname") != null) {
					stmt.setString(8, psndocVO.getFieldValue("dutyname").toString());
				} else {
					stmt.setNull(8, Types.CHAR);
				}
			}
			if(deptchginfo !=null){
				if(deptchginfo.getAttributeValue("pk_postdoc")!=null){
					stmt.setString(9, (String)deptchginfo.getAttributeValue("pk_postdoc"));
				}else{
					stmt.setNull(9, Types.CHAR);
				}
			} else {
				if (psndocVO.getFieldValue("pk_om_job") != null) {
					stmt.setString(9, psndocVO.getFieldValue("pk_om_job").toString());
				} else {
					stmt.setNull(9, Types.CHAR);
				}
			}
			stmt.setString(10, (String) psndocVO.getFieldValue("pk_psncl"));
			stmt.setString(11, (String) psndocVO.getFieldValue("pk_psnbasdoc"));
			if(leavedate==null){
				stmt.setNull(12, Types.CHAR);
			}else{
				stmt.setString(12, leavedate);
			}
			if(deptchginfo !=null){
				if(deptchginfo.getAttributeValue("pk_psncl")!=null){
					stmt.setString(13, (String)deptchginfo.getAttributeValue("pk_psncl"));
				}else{
					stmt.setNull(13, Types.CHAR);
				}
			} else {
//				if (psndocVO.getFieldValue("pk_psncl") == null) {
					stmt.setNull(13, Types.CHAR);
//				} else {
//					stmt.setString(13, (String) psndocVO.getFieldValue("pk_psncl"));
//				}
			}
			stmt.setString(14,(String) psndocVO.getFieldValue("belong_pk_corp"));
			if(fieldnames!=null && fieldnames.length>0){
				for(int i=0 ;i<fieldnames.length;i++){
					Object o= psndocVO.getAttributeValue(fieldnames[i]);
					if(o!=null){
						stmt.setObject(15+i, psndocVO.getAttributeValue(fieldnames[i]));
					}else{
						stmt.setNull(15+i,Types.CHAR);
					}
				}
			}
			
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsndocMainDMO", "insertDismissionChd",
				new Object[] { psndocVO,leavedate  });
		/** ********************************************************** */

		return key;
	}

	/**
	 * 插入主信息集信息。 v53增加返聘处理
	 * @param psndocVO
	 * @param accpsndocVO
	 * @param mainaddpsndocVO
	 * @return
	 * @throws BusinessException
	 * @throws NamingException 
	 */
	public String[] insertMain(GeneralVO psndocVO, GeneralVO accpsndocVO,GeneralVO mainaddpsndocVO) throws BusinessException, NamingException {
		String[] keys = new String[2];
		if(mainaddpsndocVO !=null){//返聘处理,修改个人信息,然后增加工作信息
			String pk_psnbasdoc = (String) accpsndocVO.getFieldValue("pk_psnbasdoc");
			updateTable("bd_psnbasdoc", accpsndocVO, "pk_psnbasdoc='"+ pk_psnbasdoc + "'");
			keys[0] = pk_psnbasdoc;
			
		}else{//正常增加个人信息
			accpsndocVO.setAttributeValue("pk_corp", psndocVO.getAttributeValue("pk_corp"));
			accpsndocVO.setAttributeValue("indocflag", "N");
			keys[0] = insertTable("bd_psnbasdoc", accpsndocVO, "pk_psnbasdoc");
		}		
		psndocVO.setAttributeValue("pk_psnbasdoc", keys[0]);
		keys[1] = insertTable("bd_psndoc", psndocVO, "pk_psndoc");

		return keys;
	}

	/**
	 * 插入HI_PSNDOC_REF数据
	 * 
	 * @param psndocVO
	 * @return
	 * @throws NamingException 
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 */
	public String insertHiRef(GeneralVO psndocVO) throws BusinessException, NamingException {
		try {
			// 替换几个重要字段
			psndocVO = replaceMainFields(psndocVO);
		} catch (Exception e) {
			// TODO: handle exception
			throw new BusinessException(e.getMessage());
		}

		return new PsnInfDAO().insertTable_NOTAlwaysNewPK("hi_psndoc_ref", psndocVO, "pk_psndoc");
	}

	/**
	 * 批量插入HI_PSNDOC_REF数据
	 * 
	 * @param psndocVO
	 * @return
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 */
	public void insertHiRefs(GeneralVO[] psndocVOs) throws BusinessException {
		try {
			PsnInfDAO dao = new PsnInfDAO();
			PsnInfDMO dmo = new PsnInfDMO();
			for (GeneralVO psndocVO:psndocVOs){
				
				String oldpk_psndoc = dmo.getOldPsnDocPKOfRef(psndocVO);
				psndocVO.setAttributeValue("pk_psndoc", oldpk_psndoc);
				// 替换几个重要字段
				psndocVO = replaceMainFields(psndocVO);
				dao.insertTable_NOTAlwaysNewPK("hi_psndoc_ref", psndocVO, "pk_psndoc");
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}
	
	/**
	 * 批量插入HI_PSNDOC_REF数据
	 * 
	 * @param psndocVO
	 * @return
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 */
	public void insertPsndocs(GeneralVO[] psndocVOs) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			PsnInfDAO dao = new PsnInfDAO();
			for (GeneralVO psndocVO:psndocVOs){
				String oldpk_psndoc = dmo.getOldPsnDocPKOfRef(psndocVO);
				psndocVO.setAttributeValue("pk_psndoc", oldpk_psndoc);
				
				if(oldpk_psndoc!=null){//如果原来引用过，再直接插入原pk必定报主键冲突，因此现将原已取消引用的记录删除。
					deletePsnData("bd_psndoc",oldpk_psndoc);
				}
				dao.insertTable_NOTAlwaysNewPK("bd_psndoc", psndocVO, "pk_psndoc");
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}
	/**
	 * 替换几个重要字段 {公司、部门和岗位}
	 * 
	 * @param psndocVO
	 * @return
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 */
	private GeneralVO replaceMainFields(GeneralVO psndocVO)
			throws java.io.IOException, java.sql.SQLException {
		String pk_psndoc = (String) psndocVO.getAttributeValue("oripk_psndoc");
		String sql = "SELECT bd_psndoc.pk_corp,bd_corp.unitname ,bd_psndoc.pk_deptdoc,bd_deptdoc.deptname ,bd_psndoc.pk_om_job,om_job.jobname from bd_psndoc "
				+ " left outer join bd_corp on bd_psndoc.pk_corp = bd_corp.pk_corp "
				+ " left outer join bd_deptdoc on bd_psndoc.pk_deptdoc  = bd_deptdoc.pk_deptdoc "
				+ " left outer join om_job on bd_psndoc.pk_om_job = om_job.pk_om_job "
				+ " where bd_psndoc.pk_psndoc = ? ";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			rs = stmt.executeQuery();
			while (rs.next()) {
				psndocVO.setAttributeValue("oripk_corp", rs.getString(1));
				psndocVO.setAttributeValue("oriunitname", rs.getString(2));
				psndocVO.setAttributeValue("oripk_deptdoc", rs.getString(3));
				psndocVO.setAttributeValue("orideptname", rs.getString(4));
				psndocVO.setAttributeValue("oripk_om_job", rs.getString(5));
				psndocVO.setAttributeValue("orijobname", rs.getString(6));
			}
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return psndocVO;
	}

	
	public String insertRecievers(String pk_corp,String userids,int sendtype)
		throws java.sql.SQLException {
		String sql = "insert into hr_message (pk_message,pk_corp ,recieveruserids,sendtype)  values(?,?,?,?)";
		Connection conn = null;
		PreparedStatement stmt = null;
		String key = null;
		try {
			
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			key = getOID();
			stmt.setString(1, key);
			stmt.setString(2, pk_corp);
			stmt.setString(3, userids);
			stmt.setInt(4, sendtype);
			stmt.executeUpdate();
			
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return key;
	}
	/**
	 * 
	 * @param pk_corp
	 * @param userids
	 * @throws java.sql.SQLException
	 */
	public void updateRecievers(String pk_corp,String userids,int sendtype)
		throws java.sql.SQLException {
		String sql = "update hr_message set recieveruserids = ?,sendtype =? where pk_corp = ? ";
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userids);
			stmt.setInt(2, sendtype);
			stmt.setString(3, pk_corp);
			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
}
	/**
	 * 
	 * @param pk_corp
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean isExistRecievers(String pk_corp)
		throws java.sql.SQLException {
		String sql = "select pk_message from hr_message where pk_corp =?";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean exist = false;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_corp);
			rs = stmt.executeQuery();
			while (rs.next()) {
				exist = true;
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return exist;
}
	/**
	 * 插入表table信息。 创建日期：(2004-5-20 13:43:57)
	 * 
	 * @param table
	 *            java.lang.String
	 * @param data
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @throws NamingException 
	 * @exception java.io.IOException
	 *                异常说明。
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public String insertTable(String tableCode, GeneralVO data,
			String primaryKey) throws BusinessException, NamingException {
		
		PsnInfDAO dao = new PsnInfDAO();

		return dao.insertTable(tableCode, data, primaryKey);		

	}

	/**
	 * 转入人员档案。 创建日期：(2004-5-25 11:57:17)
	 * 
	 * @param psnList
	 *            nc.vo.hi.hi_301.GeneralVO[]
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void intoDoc(GeneralVO[] psnList) throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "intoDoc",new Object[] { psnList });
		/** ********************************************************** */
		Connection conn = null;
		Statement stmt = null;
		try {
			if (psnList == null || psnList.length == 0)
				return;
			conn = getConnection();
			stmt = conn.createStatement();
			for (int i = 0; i < psnList.length; i++) {
				String pk_psndoc = (String) psnList[i].getFieldValue("pk_psndoc");
				String sql = "update bd_psndoc set indocflag='Y' where pk_psndoc='"+ pk_psndoc + "'";
				
				sql = HROperatorSQLHelper.getSQLWithOperator(sql);
				
				stmt.executeUpdate(sql);
				nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psndoc", pk_psndoc);
			}
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "intoDoc",
				new Object[] { psnList });
		/** ********************************************************** */
	}
	
	/**
	 * 转入人员档案。 创建日期：(2004-5-25 11:57:17)
	 * 
	 * @param psnList
	 *            nc.vo.hi.hi_301.GeneralVO[]
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void intoDocBas(GeneralVO[] psnList) throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "intoDocBas",new Object[] { psnList });
		/** ********************************************************** */
		Connection conn = null;
		Statement stmt = null;
		try {
			if (psnList == null || psnList.length == 0)
				return;
			conn = getConnection();
			stmt = conn.createStatement();
			for (int i = 0; i < psnList.length; i++) {
				String pk_psnbasdoc = (String) psnList[i].getFieldValue("pk_psnbasdoc");
				String sql = "update bd_psnbasdoc set indocflag='Y',approveflag =1 where pk_psnbasdoc='"+ pk_psnbasdoc + "'";
				
				sql = HROperatorSQLHelper.getSQLWithOperator(sql);
				
				stmt.executeUpdate(sql);
				nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psnbasdoc", pk_psnbasdoc);
			}
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "intoDocBas",
				new Object[] { psnList });
		/** ********************************************************** */
	}

	/**
	 * 在信息采集模块，当身份证号和姓名重复时，返回存在的人员信息
	 * 
	 * @param psnname
	 * @param id
	 * @return
	 * @throws java.sql.SQLException
	 */
	public GeneralVO queryExistedPsnInfo(String psnname, String id)
			throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryExistedPsnInfo",
				new Object[] { psnname, id });
		/** ********************************************************** */

		GeneralVO psnvo = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		try {
			String sql = " select bd_psndoc.psncode,bd_deptdoc.deptname,om_job.jobname from bd_psndoc "
					+ " left outer join bd_deptdoc  on bd_deptdoc.pk_deptdoc = bd_psndoc.pk_deptdoc "
					+ " left outer join om_job on om_job.pk_om_job = bd_psndoc.pk_om_job "
					+ " where bd_psndoc.psnname = ? "
					+ " and  bd_psndoc.pk_psndoc in (select pk_psndoc from bd_accpsndoc where id =?) ";
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, psnname);
			stmt.setString(2, id);

			result = stmt.executeQuery();
			// Vector v = new Vector();
			if (result.next()) {
				psnvo = new GeneralVO();
				String psncode = result.getString(1);
				String deptname = result.getString(2);
				String jobname = result.getString(3);
				psnvo.setAttributeValue("psncode", psncode);
				psnvo.setAttributeValue("deptname", deptname);
				psnvo.setAttributeValue("jobname", jobname);
			}

		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryExistedPsnInfo",
				new Object[] { psnname, id });
		/** ********************************************************** */
		return psnvo;

	}

	/**
	 * 从hi_flddict何hi_setdic表中查询bd_accpsndoc中字段acc_fldcode的关联字段。 创建日期：(2004-5-30
	 * 15:00:59)
	 * 
	 * @return java.lang.String 返回"表名,字段,辅助信息集字段"的数组
	 * @param acc_fldcode
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public GeneralVO[] queryAllRelatedTableField(String corppk)
			throws java.sql.SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		try {
			String sql = "select hi_setdict.setcode, hi_flddict.fldcode, temp1.fldcode as accfldcode,temp2.setcode as accsetcode from hi_flddict "
					+ "inner join hi_setdict on hi_flddict.pk_setdict=hi_setdict.pk_setdict "
					+ "inner join hi_flddict as temp1 on hi_flddict.bdfldpk=temp1.pk_flddict "
					+ " inner join hi_setdict as temp2 on temp1.pk_setdict = temp2.pk_setdict "
					+ "where hi_flddict.bdfldpk in "
					+ "(select pk_flddict from hi_flddict "
					+ "inner join hi_setdict on hi_flddict.pk_setdict=hi_setdict.pk_setdict "
					+ "where hi_setdict.setcode='bd_psndoc' or hi_setdict.setcode='bd_psnbasdoc' )";// bd_accpsndoc
			if (corppk != null && corppk.length() > 0) {
				sql += " and (hi_flddict.create_pk_corp = '" + corppk
						+ "' or hi_flddict.isshare = 'Y' )";
			}
			conn = getConnection();
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);
			Vector v = new Vector();
			while (result.next()) {
				GeneralVO gvo = new GeneralVO();
				String setcode = result.getString(1);
				setcode = setcode == null ? null : setcode.trim();
				gvo.setAttributeValue("setcode", setcode);
				String fldcode = result.getString(2);
				fldcode = fldcode == null ? null : fldcode.trim();
				gvo.setAttributeValue("fldcode", fldcode);
				String accfldcode = result.getString(3);
				String accsetcode = result.getString(4);
				accfldcode = (accfldcode == null ? null
						: (accsetcode + "." + accfldcode.trim()));
				gvo.setAttributeValue("accfldcode", accfldcode);
				v.addElement(gvo);
			}
			return (GeneralVO[]) v.toArray(new GeneralVO[0]);
		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}

	}

	/**
	 * 查询所有pk_corp地子表。 创建日期：(2004-5-19 10:51:02)
	 * 
	 * @return java.lang.String[]
	 * @param pk_corp
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public String[] queryAllSubTable(String pk_corp)
			throws java.sql.SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		try {
			// modify 2008-3-21 remove " and ts < '2004-06-21 14:12:21'"
			String sql = "select setcode from hi_setdict where (pk_corp='0001' or pk_corp='"
					+ pk_corp
					+ "') and ismainset='N' and pk_hr_defdoctype='00000000000000000004' 	";

			conn = getConnection();
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);

			Vector v = new Vector();
			while (result.next()) {
				String tableCode = result.getString(1);
				if (tableCode != null && !"bd_corp".equalsIgnoreCase(tableCode) && !"bd_deptdoc".equalsIgnoreCase(tableCode))
					v.addElement(tableCode);
			}

			return (String[]) v.toArray(new String[0]);

		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 根据sql查询所有的信息。 创建日期：(2004-5-9 21:12:06)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param sql
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	public GeneralVO[] queryBySql(String sql) throws java.lang.Exception {
		return (GeneralVO[]) querySql(sql).toArray(new GeneralVO[0]);
	}
	
	/**
	 * 根据sql查询所有的信息。 创建日期：(2004-5-9 21:12:06)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param sql
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	public GeneralVO[] queryBySqlForQuery(String sql) throws java.lang.Exception {
		
		if(getDataBaseType()!=ORACLE){
			String newsql = "";
			//内层select
			String truesql = sql.substring(sql.indexOf("(")+1,sql.lastIndexOf(")"));
			//外层select
			String topsql = sql.substring(0,sql.indexOf("("));
			String num = topsql.substring(topsql.indexOf("top")+3, topsql.indexOf("*"));
			//有distinct情况
			if(truesql.indexOf("distinct")>0 && truesql.indexOf("distinct")<20 ){
				newsql = " select distinct top "+num+" "+truesql.substring(truesql.indexOf("distinct")+8);
			}else{
				newsql = " select top "+num+" "+truesql.substring(truesql.indexOf("select")+6);
			}
			return (GeneralVO[]) querySql(newsql).toArray(new GeneralVO[0]);

    	}
		return (GeneralVO[]) querySql(sql).toArray(new GeneralVO[0]);
	}

	/**
	 * 根据sql查询所有的信息。 创建日期：(2004-5-9 21:12:06)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param sql
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	public int queryRecordCountBySql(String sql) throws java.lang.Exception {
		int count = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);

			while (result.next()) {
				count = result.getInt(1);
			}

		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return count;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-5-31 15:46:52)
	 * 
	 * @return java.util.Vector
	 * @param sql
	 *            java.lang.String
	 */
	public Vector querySql(String sql) throws java.sql.SQLException,
			java.io.IOException {
		Connection conn = null;
		//Statement stmt = null;
		CrossDBPreparedStatement prestmt = null;
		CrossDBResultSet result = null;
		CrossDBResultSetMetaData meta = null;
		//ResultSet result = null;
//		ResultSetMetaData meta = null;
		boolean isPsnbas = false;// 是否查询人员基本情况
		if (sql.indexOf("select * from bd_psnbasdoc where") >= 0) {
			isPsnbas = true;
		}
		try {
			conn = getConnection();
			prestmt = (CrossDBPreparedStatement)conn.prepareStatement(sql);
			result = (CrossDBResultSet)prestmt.executeQuery();

			meta = (CrossDBResultSetMetaData)result.getMetaData();
			String[] fieldNames = new String[meta.getColumnCount()];
			int[] fieldTypes = new int[fieldNames.length];
			for (int i = 0; i < fieldNames.length; i++) {
				fieldNames[i] = meta.getColumnName(i + 1).toLowerCase();
				fieldTypes[i] = meta.getColumnType(i + 1);
			}

			Vector v = new Vector();
			while (result.next()) {
				GeneralVO gvo = new GeneralVO();
				for (int i = 0; i < fieldNames.length; i++) {
					if (BSUtil.isBinary(fieldTypes[i])) { // 如果是blob类型

						byte[] data = result.getBlobBytes(i+1);






						gvo.setFieldValue(fieldNames[i], data);
					} else if (!BSUtil.isSkipField(fieldNames[i])) {
						Object value = result.getObject(i + 1);
						if (value != null) {
							if (value instanceof String)
								value = ((String) value).trim();
							if (isPsnbas
									&& fieldNames[i]
											.equalsIgnoreCase("pk_corp")) {
								gvo.setFieldValue("belong_pk_corp", value);
							} else {
								gvo.setFieldValue(fieldNames[i], value);
							}
						}
					} else
						result.getObject(i + 1);
				}
				if (gvo.getAttributeValue("pk_corp") == null
						&& gvo.getAttributeValue("man_pk_corp") != null) {
					gvo.setAttributeValue("pk_corp", gvo
							.getAttributeValue("man_pk_corp"));
				}
				v.addElement(gvo);
			}

			return v;

		} finally {
			if (meta != null) 
				meta = null;
			if (result != null)
				result.close();
			if (prestmt != null)
				prestmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-7-1 16:14:26)
	 * 
	 * @param vos
	 *            nc.vo.hi.hi_301.GeneralVO[]
	 * @param isPsnToDeptChg
	 *            java.lang.Boolean true:人员情况到任职情况同步，false：任职情况到部门情况同步
	 * @exception java.sql.SQLException
	 *                异常说明。
	 * @exception nc.bs.pub.SystemException
	 *                异常说明。
	 */
	public void synchroDeptChg(GeneralVO[] vos, Boolean isPsnToDeptChg)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "synchroDeptChg",
				new Object[] { vos, isPsnToDeptChg });
		/** ********************************************************** */

		String sql = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		if (isPsnToDeptChg.booleanValue()) {
			sql = "update hi_psndoc_deptchg set pk_deptdoc = ? ,pk_psncl=?,pk_jobrank=?,pk_jobserial=?,pk_om_duty=?,pk_detytype=?,pk_postdoc=? where pk_psndoc = ? and recordnum = 0 and jobtype= 0 ";
			sql2 = "select pk_psndoc_sub,enddate,begindate  from hi_psndoc_deptchg where pk_psndoc = ? and jobtype= 0 order by recordnum asc ";
			sql3 = "update hi_psndoc_deptchg set begindate=? where pk_psndoc_sub = ?";
		} else {
			sql = "update bd_psndoc set pk_deptdoc = ? ,pk_psncl = ?, pk_om_job = ? ,psnclscope = (select psnclscope from bd_psncl where pk_psncl= ?),jobrank=? ,jobseries = ?, dutyname=?,series=? where pk_psndoc = ?";
			sql3 = "select max(recordnum) from hi_psndoc_deptchg where pk_psndoc = ?";
			sql4 = "update bd_psndoc set indutydate=? where pk_psndoc = ?";
		}
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			if (isPsnToDeptChg.booleanValue()) { // 同步到任职情况
				stmt = con.prepareStatement(sql);
				stmt.setString(1, (String) vos[0].getFieldValue("pk_deptdoc"));
				stmt.setString(2, (String) vos[0].getFieldValue("pk_psncl"));
				stmt.setString(3, (String) vos[0].getFieldValue("jobrank"));// V35
				// fixed
				// 1-->0
				stmt.setString(4, (String) vos[0].getFieldValue("jobseries"));// V35
				// fixed
				// 1-->0
				stmt.setString(5, (String) vos[0].getFieldValue("dutyname"));// V35
				// fixed
				// 1-->0
				stmt.setString(6, (String) vos[0].getFieldValue("series"));// V35
				// fixed
				// 1-->0
				stmt.setString(7, (String) vos[0].getFieldValue("pk_om_job"));
				stmt.setString(8, (String) vos[0].getFieldValue("pk_psndoc"));
				stmt.executeUpdate();
				stmt.close();

				if (vos[0].getAttributeValue("isSynIndutydate") != null
						&& "Y".equals(((String) vos[0]
								.getAttributeValue("isSynIndutydate")))) {// 到职日期是否同步的标记
					if (vos[0].getFieldValue("indutydate") != null) {
						// 查询任职信息
						stmt = con.prepareStatement(sql2);
						stmt.setString(1, (String) vos[0]
								.getFieldValue("pk_psndoc"));
						ResultSet rs = stmt.executeQuery();
						Vector v = new Vector();
						while (rs.next()) {
							Vector v2 = new Vector();
							v2.addElement(rs.getString(1));
							v2.addElement(rs.getString(2));
							v2.addElement(rs.getString(3));
							v.addElement(v2);
						}
						stmt.close();
						if (v.size() > 0) {
							Vector temp = (Vector) v.elementAt(v.size() - 1);
							String pk = (String) temp.elementAt(0);
							String indutydate = vos[0].getFieldValue(
									"indutydate").toString();
							// Object obj = temp.elementAt(1);
							Object begindate = temp.elementAt(2);
							if (begindate == null) {// 只有全为空时同步，如果开始日期不为空,则不用同步
								// 更新最后一条的起止日期
								stmt = con.prepareStatement(sql3);
								stmt.setString(1, indutydate);
								stmt.setString(2, pk);
								stmt.executeUpdate();
							}
						}
					}
				}
			} else { // 从任职情况同步到人员情况
				Object obj = vos[0].getFieldValue("recordnum");
				int recordnum = ((Integer) obj).intValue();
				if (recordnum == 0) {
					stmt = con.prepareStatement(sql);
					stmt.setString(1, (String) vos[0]
							.getFieldValue("pk_deptdoc"));
					stmt
							.setString(2, (String) vos[0]
									.getFieldValue("pk_psncl"));
					stmt.setString(3, (String) vos[0]
							.getFieldValue("pk_postdoc"));
					stmt
							.setString(4, (String) vos[0]
									.getFieldValue("pk_psncl"));
					// stmt.setString(5, (String) vos[0]
					// .getFieldValue("pk_psndoc"));
					// stmt.executeUpdate();
					// nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psndoc",
					// (String) vos[0].getFieldValue("pk_psndoc"));//wangkf
					// // add
					// stmt.close();
					// stmt = con.prepareStatement(sql2);
					stmt.setString(5, (String) vos[0]
							.getFieldValue("pk_jobrank"));
					stmt.setString(6, (String) vos[0]
							.getFieldValue("pk_jobserial"));
					stmt.setString(7, (String) vos[0]
							.getFieldValue("pk_om_duty"));
					stmt.setString(8, (String) vos[0]
							.getFieldValue("pk_detytype"));
					stmt.setString(9, (String) vos[0]
							.getFieldValue("pk_psndoc"));
					stmt.executeUpdate();
					stmt.close();
				}
				// 得到最后一条记录的记录序号
				stmt = con.prepareStatement(sql3);
				stmt.setString(1, (String) vos[0].getFieldValue("pk_psndoc"));
				ResultSet rs = stmt.executeQuery();
				int max = 0;
				if (rs.next()) {
					max = rs.getInt(1);
				}
				// 把最后一条记录的开始日期同步到辅表的入职日期
				Object lastObj = vos[vos.length - 1].getFieldValue("recordnum");
				int lastrecordnum = ((Integer) lastObj).intValue();
				if (lastrecordnum == max) {
					stmt.close();
					stmt = con.prepareStatement(sql4);
					if (vos[vos.length - 1].getFieldValue("begindate") != null) {
						stmt.setString(1, vos[vos.length - 1].getFieldValue(
								"begindate").toString());
					} else {
						stmt.setNull(1, Types.CHAR);
					}
					stmt.setString(2, (String) vos[vos.length - 1]
							.getFieldValue("pk_psndoc"));
					stmt.executeUpdate();
				}
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "synchroDeptChg",
				new Object[] { vos, isPsnToDeptChg });
		/** ********************************************************** */

	}

	/**
	 * 
	 * @param pk_psndoc
	 * @throws java.sql.SQLException
	 * @throws nc.bs.pub.SystemException
	 */
	public void synchroPart(String pk_psndoc) throws java.sql.SQLException,
			nc.bs.pub.SystemException {

		String sql = "update hi_psndoc_deptchg set bendflag='Y' where pk_psndoc = ? and  jobtype <> 0 ";
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.executeUpdate();

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-7-1 15:45:49)
	 * 
	 * @param tableCode
	 *            java.lang.String
	 * @param fldCode
	 *            java.lang.String[]
	 * @param type
	 *            java.lang.String[]
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void updateDept(String psnpk, String deptpk, Boolean flag)
			throws java.sql.SQLException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateDept",
				new Object[] { psnpk, deptpk, flag });
		/** ********************************************************** */

		String sql = "";
		if (flag.booleanValue()) {
			sql = "update bd_psndoc set pk_deptdoc = ? where pk_psndoc = ?";
		} else {
			sql = "update hi_psndoc_deptchg set pk_deptdoc = ? where pk_psndoc = ? and recordnum = 0";
		}
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, deptpk);
			stmt.setString(2, psnpk);
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateDept",
				new Object[] { psnpk, deptpk, flag });
		/** ********************************************************** */

	}

	/**
	 * 
	 * @param pk_psndoc
	 * @param ifdelete
	 * @throws java.sql.SQLException
	 * @throws java.io.IOException
	 */
	public void updateDeleteFlag(String pk_psndoc, boolean ifdelete)
			throws java.sql.SQLException, java.io.IOException {

		String sql = "";
		if (ifdelete) {
			sql = "update bd_psndoc set dr = 1 where pk_psndoc = ? ";
		} else {
			sql = "update bd_psndoc set dr = 0 where pk_psndoc = ? ";
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.executeUpdate();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();

		}
	}

	/**
	 * 更新指定表信息，更新条件sqlWhere
	 * 
	 * @param tableCode
	 * @param data
	 * @param sqlWhere
	 * @throws NamingException 
	 * @throws java.sql.SQLException
	 * @throws java.io.IOException
	 */
	public void updateTable(String tableCode, GeneralVO data, String sqlWhere)
			throws DAOException, NamingException {
		PsnInfDAO dao = new PsnInfDAO();





		dao.updateTable(tableCode, data, sqlWhere);

	}

	/**
	 * 执行sql语句检查是否存在记录
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public boolean isRecordExist(String sql)throws SQLException{
		boolean result = false;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			//String sql = "select 1 from sm_userandclerk where pk_psndoc = ? and pk_corp = ?";
			stmt = con.prepareStatement(sql);
			//stmt.setString(1, pk_psndoc);
			//stmt.setString(2, pkCorp);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				result = true;
			}

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return result;
	}
	/**
	 * 检查业务子集是否允许查看历史
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public boolean isTraceTableLookHistory(String tablename)throws SQLException{
		boolean result = false;
		String sql = "select islookhistory from hi_setdict where setcode =?";
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, tablename);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String look = rs.getString(1);
				if(look!= null && look.equalsIgnoreCase("Y")){
					result = true;
				}else{
					result = false;
				}
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return result;
	}
	/**
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean isNOtZaizhiPerson(String pk_psndoc)
			throws java.sql.SQLException {
		String sql = "select 1 from bd_psncl where (psnclscope <>0 and psnclscope <>5 ) and pk_psncl in (select pk_psncl from bd_psndoc where pk_psndoc = ?) ";//

		boolean isnotzaizhi = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			rs = stmt.executeQuery();
			while (rs.next()) {
				isnotzaizhi = true;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return isnotzaizhi;
	}

	/**
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean isNOtZaizhiPsnclscope(String pk_psncl)
			throws java.sql.SQLException {
		String sql = "select 1 from bd_psncl where (psnclscope <>0 and psnclscope <>5 ) and pk_psncl = ?  ";//

		boolean isnotzaizhi = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_psncl);
			rs = stmt.executeQuery();
			while (rs.next()) {
				isnotzaizhi = true;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return isnotzaizhi;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-8-6 11:58:42)
	 * 
	 * @return boolean
	 * @param pk_psndoc_sub
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public boolean checkPsnSub(String pk_psndoc_sub)
			throws java.sql.SQLException {
		return false;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-8-6 11:58:42)
	 * 
	 * @return boolean
	 * @param pk_psndoc_sub
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public boolean checkPsnSub(String pk_psndoc_sub, String tableCode)
			throws java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = null;
		try {
			sql = "select 1 from " + tableCode
					+ " where pk_psndoc_sub = ? and dr = 0";

			conn = getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, pk_psndoc_sub);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return false;
	}

	/**
	 * 判断此员工在本公司是否有用户。 创建日期：(2003-7-17 9:18:22)
	 * 
	 * @return boolean
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param pkCorp
	 *            java.lang.String
	 */
	public boolean checkUserClerk(String pk_psndoc, String pkCorp)
			throws java.sql.SQLException {
		boolean bool = true;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			String sql = "select 1 from sm_userandclerk where pk_psndoc = ? and pk_corp = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.setString(2, pkCorp);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				bool = false;
			}

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return bool;

	}
	
	/**
	 * 查询再聘返聘人员原始的归属公司
	 * @param pk_psnbasdoc
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String queryOriPkcorp(String pk_psnbasdoc,int hiretype)
			throws java.sql.SQLException {
		String pk_corp = null;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			String sql = "select pk_corp from bd_psndoc where pk_psnbasdoc = ? and psnclscope = ? ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psnbasdoc);
			stmt.setInt(2, hiretype);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				pk_corp = rs.getString(1);
			}

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return pk_corp;

	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-12-3 16:28:15)
	 * 
	 * @param tableCode
	 *            java.lang.String
	 * @param psndocvo
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void deletePsndoc(String tableCode, GeneralVO psndocvo)
			throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "delete", new Object[] {
				tableCode, psndocvo });
		/** ********************************************************** */
		String pk_psndoc = (String) psndocvo.getFieldValue("pk_psndoc");

		Connection conn = null;
		Statement stmt = null;
		try {

			if (!"bd_psnbasdoc".equalsIgnoreCase(tableCode)) {
				String sql = "delete from " + tableCode + " where pk_psndoc='"
						+ pk_psndoc + "'";
				conn = getConnection();
				stmt = conn.createStatement();
				stmt.execute(sql);
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "delete", new Object[] {
				tableCode, psndocvo });
		/** ********************************************************** */

	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-10-19 9:40:49)
	 * 
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 * @param pk_corp
	 *            java.lang.String
	 * @param funcode
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public CtrlDeptVO[] queryCreateCorpChildVOs(String pk_corp, String funcode,
			String userid, boolean isRelate) throws java.sql.SQLException {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		CtrlDeptVO[] childvos = null;
		try {
			String sql = "select bd_corp.pk_corp,unitcode,unitname from bd_corp where bd_corp.fathercorp = '"
					+ pk_corp + "' order by unitcode";
			String checkcreatecorpsql = "select  1  from  sm_createcorp  t1 ,  sm_codetocode  t2  where  t1.pk_corp  =  ?  and  t2.pk_codetocode  =  'HI'  and  t1.funccode  =  t2.funccode";
			String checkrelatesql = "select 1 from sm_user_role where pk_corp = ? and cuserid = '"+ userid + "'";
			//v50 修改，此处应改为用户关联的公司，通过角色关联公司

			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			Vector v = new Vector();
			while (rs.next()) {
				CtrlDeptVO vo = new CtrlDeptVO();
				vo.setPk_corp(rs.getString(1));
				vo.setCode(rs.getString(2));
				vo.setName(rs.getString(3));
				vo.setControlled(true);
				vo.setNodeType(CtrlDeptVO.CORP);
				v.addElement(vo);
			}
			stmt.close();
			for (int i = v.size() - 1; i >= 0; i--) {
				CtrlDeptVO vo = (CtrlDeptVO) v.elementAt(i);
				stmt2 = conn.prepareStatement(checkcreatecorpsql);
				stmt2.setString(1, vo.getPk_corp());
				rs2 = stmt2.executeQuery();
				if (!rs2.next()) {
					CtrlDeptVO tvo = (CtrlDeptVO) v.elementAt(i);
					tvo.setControlled(false);
					stmt2.close();
					continue;
				} else {
					stmt2.close();
				}
				if (isRelate) {
					stmt3 = conn.prepareStatement(checkrelatesql);
					stmt3.setString(1, vo.getPk_corp());
					rs3 = stmt3.executeQuery();
					if (!rs3.next()) {
						CtrlDeptVO tvo = (CtrlDeptVO) v.elementAt(i);
						tvo.setControlled(false);
					}
					stmt3.close();
				}
			}
			if (v.size() > 0) {
				childvos = new CtrlDeptVO[v.size()];
				v.copyInto(childvos);
			}
			return childvos;

		} finally {
			// if (rs != null)
			// rs.close();
			if (stmt != null)
				stmt.close();
			// if(rs2 !=null)
			// rs2.close();
			if (stmt2 != null)
				stmt2.close();
			if (isRelate) {
				// if(rs3 !=null)
				// rs3.close();
				if (stmt3 != null)
					stmt3.close();
			}
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 任职情况增加新纪录时，如果上一条记录的结束日期为空则更新为当前记录的起始日期的前一天。 创建日期：(2004-8-6 9:05:56)
	 * 
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param newDate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void synchroDeptChgEnddate(String pk_psndoc, UFDate newDate)
			throws SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "synchroDeptChgEnddate",
				new Object[] { pk_psndoc, newDate });
		/** ********************************************************** */
		if (newDate == null)
			return;
		String sql = " update hi_psndoc_deptchg set enddate = ? where pk_psndoc = ? and (enddate is null or ltrim(rtrim(enddate))= '') and dr =0 and recordnum=1 and jobtype=0 ";
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt = con.prepareStatement(sql);
			stmt.setString(1, newDate.toString());
			stmt.setString(2, pk_psndoc);
			stmt.executeUpdate();			
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "synchroDeptChgEnddate",
				new Object[] { pk_psndoc, newDate });
		/** ********************************************************** */
	}
	/**
	 * 任职情况增加新纪录时，如果上一条记录的结束日期为空则更新为当前记录的起始日期的前一天。 创建日期：(2004-8-6 9:05:56)
	 * 
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param newDate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void updateBasCorpPk(String pk_psnbasdoc,String pk_corp)
			throws SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateBasCorpPk",
				new Object[] { pk_psnbasdoc,pk_corp });
		/** ********************************************************** */

		String sql = " update bd_psnbasdoc set pk_corp = ? where pk_psnbasdoc = ? ";
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_corp);
			stmt.setString(2, pk_psnbasdoc);
			stmt.executeUpdate();
			// }
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateBasCorpPk",
				new Object[] { pk_psnbasdoc,pk_corp });
		/** ********************************************************** */
	}

	/**
	 * 同步人员标志表，从客户化采集的人员没有插入这个表的数据。 创建日期：(2004-8-23 10:36:59)
	 * 
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void synchroPsnFlag() throws java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = null;
		String sql2 = null;
		String sql3 = null;
		Vector v = new Vector();
		try {
			sql = "select pk_psndoc from bd_psndoc where pk_psndoc not in (select pk_psndoc from hi_psndoc_flag)";
			sql2 = "insert into hi_psndoc_flag(pk_psndoc,showorder) values (?,99)";
			sql3 = "update hi_psndoc_flag set regular = 'Y' where regular<>'N' or regular is null";
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				v.addElement(rs.getString(1));
			}
			if (v.size() > 0) {
				stmt.close();
				for (int i = 0; i < v.size(); i++) {
					stmt = conn.prepareStatement(sql2);
					stmt.setString(1, (String) v.elementAt(i));
					stmt.executeUpdate();
				}
			}
			stmt.close();
			stmt = conn.prepareStatement(sql3);
			stmt.executeUpdate();
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-9-8 16:53:26)
	 * 
	 * @param pk_psndoc
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void updateCtrt(String pk_psndoc, Boolean isAdd)
			throws java.sql.SQLException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateCtrt",
				new Object[] { pk_psndoc, isAdd });
		/** ********************************************************** */

		String sql = "";
		if (isAdd.booleanValue()) {
			sql = "update hi_psndoc_ctrt set icontstate = iconttype where pk_psndoc = ? and recordnum >0 and lastflag <> 'Y'  and isrefer = 'Y'";
		} else {
			sql = "update hi_psndoc_ctrt set icontstate = 2 where pk_psndoc = ? and recordnum = 0 and lastflag = 'Y' and iconttype = 1 and isrefer = 'Y'";
		}
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateCtrt",
				new Object[] { pk_psndoc, isAdd });
		/** ********************************************************** */

	}
	/**
	 * 此处插入方法描述。 创建日期：(2004-9-8 16:53:26)
	 * 
	 * @param pk_psndoc
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void updateEdu(String pk_psndoc,String pk_psndoc_sub)
			throws java.sql.SQLException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateEdu",
				new Object[] { pk_psndoc,pk_psndoc_sub});
		/** ********************************************************** */

		String sql = " update hi_psndoc_edu set lasteducation = 'N' where pk_psndoc = ? and pk_psndoc_sub <>?";
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.setString(2, pk_psndoc_sub);
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateEdu",
				new Object[] { pk_psndoc,pk_psndoc_sub});
		/** ********************************************************** */

	}
	
	/**
	 * 更新任职信息的“是否在岗”字段，设置最新增加的一条信息的“是否在岗”为ture，其余的为false。
	 * @param pk_psndoc
	 * @param pk_psndoc_sub
	 * @throws SQLException
	 * fengwei 2009-09-21
	 */
	public void updatePoststat(String pk_psndoc, String pk_psndoc_sub) throws SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updatePoststat",
				new Object[] { pk_psndoc,pk_psndoc_sub});
		/** ********************************************************** */
		String sql = " update hi_psndoc_deptchg set poststat = 'N' where pk_psndoc = ? and pk_psndoc_sub <> ? ";
		
		sql = HROperatorSQLHelper.getSQLWithOperator(sql);
		
		Connection con = null;
		PreparedStatement stmt = null;
		try{
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.setString(2, pk_psndoc_sub);
			stmt.executeUpdate();
		}finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updatePoststat",
				new Object[] { pk_psndoc,pk_psndoc_sub});
		/** ********************************************************** */
	}
	/**
	 * 修改非业务子集的recordnum
	 * @param pk_psnbasdoc
	 * @param Subsetdatas
	 * @throws java.sql.SQLException
	 */
	public void updateSubsetRecordnum(String pk_psnbasdoc,String tablecode,CircularlyAccessibleValueObject[] Subsetdatas)throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateSubsetRecordnum",
				new Object[] { pk_psnbasdoc, tablecode, Subsetdatas });
		/** ********************************************************** */
		if(Subsetdatas==null||Subsetdatas.length==0){
			return;
		}
		String sql = "update " +tablecode+" set recordnum = ? where pk_psndoc_sub =? ";
		String querysql = "select 1 from hi_setdict where pk_hr_defdoctype ='00000000000000000004' and reccharacter =3 and setcode = '" +tablecode.trim()+"'";
		String sql2 = " update "+ tablecode+ " set lastflag = 'Y' where pk_psnbasdoc = ? and dr =0 and recordnum=0";
		String sql3 = " update "+ tablecode+ " set lastflag = ? where pk_psnbasdoc = ? and dr =0 and recordnum>0";
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			
			for(int i=0; i<Subsetdatas.length ;i++){
				stmt.setInt(1, (Integer)Subsetdatas[i].getAttributeValue("recordnum"));
				stmt.setString(2, (String)Subsetdatas[i].getAttributeValue("pk_psndoc_sub"));
				stmt.executeUpdate();
			}
			stmt.executeBatch();
			stmt.close();
			// 更新lastflag
			stmt = con.prepareStatement(sql2);
			stmt.setString(1, pk_psnbasdoc);
			stmt.executeUpdate();
			stmt.close();
			//查询更改的表是否是同期记录，如果是同期记录，则历史记录lastflag改为‘Y’，否则改为‘N’
			boolean tongqi = false;
			stmt = con.prepareStatement(querysql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				tongqi = true;
			}
			stmt.close();
			
			stmt = con.prepareStatement(sql3);
			if(tongqi){
				stmt.setString(1, "Y");
			}else{
				stmt.setString(1, "N");
			}
			stmt.setString(2, pk_psnbasdoc);			
			stmt.executeUpdate();
			stmt.close();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateSubsetRecordnum",
				new Object[] { pk_psnbasdoc, tablecode, Subsetdatas  });
		/** ********************************************************** */
	}
	/**
	 * 更新子表的记录序号 创建日期：(2004-8-6 8:44:51)
	 * 
	 * @param tableName
	 *            java.lang.String
	 * @param recordNum
	 *            java.lang.Integer
	 * @param isAdd
	 *            boolean
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void updateRecornum(String tableName, String pk_psndoc,
			Integer recordNum, Boolean isAdd) throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateRecornum",
				new Object[] { tableName, pk_psndoc, recordNum, isAdd });
		/** ********************************************************** */

		String sql = "";
		String sql2 = " update "
				+ tableName
				+ " set lastflag = 'Y' where pk_psndoc = ? and dr =0 and recordnum=0";
		String sql3 = " update "
				+ tableName
				+ " set lastflag = 'N' where pk_psndoc = ? and dr =0 and recordnum>0";
		if (tableName.equalsIgnoreCase("hi_psndoc_part")) {
			String temptableName = "hi_psndoc_deptchg";
			sql2 = " update "
					+ temptableName
					+ " set lastflag = 'Y' where pk_psndoc = ? and dr =0 and recordnum=0 and jobtype>0";
		} else if (tableName.equalsIgnoreCase("hi_psndoc_deptchg")) {
			sql2 = " update "
					+ tableName
					+ " set lastflag = 'Y' where pk_psndoc = ? and dr =0 and recordnum=0 and jobtype=0";
			sql3 = " update "
					+ tableName
					+ " set lastflag = 'N' where pk_psndoc = ? and dr =0 and recordnum>0 and jobtype=0";
		}
		if (isAdd.booleanValue()) {
			if (tableName.equalsIgnoreCase("hi_psndoc_part")) {
				String temptableName = "hi_psndoc_deptchg";
				sql = "update "
						+ temptableName
						+ " set recordnum = recordnum+1 where pk_psndoc = ? and dr =0 and recordnum >= ? and jobtype>0";
			} else if (tableName.equalsIgnoreCase("hi_psndoc_deptchg")) {
				sql = "update "
						+ tableName
						+ " set recordnum = recordnum+1 where pk_psndoc = ? and dr =0 and recordnum >= ? and jobtype=0";
			} else {
				sql = "update "
						+ tableName
						+ " set recordnum = recordnum+1 where pk_psndoc = ? and dr =0 and recordnum >= ?";
			}
		} else {
			if (tableName.equalsIgnoreCase("hi_psndoc_part")) {
				String temptableName = "hi_psndoc_deptchg";
				sql = "update "
						+ temptableName
						+ " set recordnum = recordnum-1 where pk_psndoc = ? and dr =0 and recordnum >= ? and jobtype>0";
			} else if (tableName.equalsIgnoreCase("hi_psndoc_deptchg")) {
				sql = "update "
						+ tableName
						+ " set recordnum = recordnum-1 where pk_psndoc = ? and dr =0 and recordnum >= ? and jobtype=0";
			} else {
				sql = "update "
						+ tableName
						+ " set recordnum = recordnum-1 where pk_psndoc = ? and dr =0 and recordnum > ?";
			}
		}
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.setInt(2, recordNum.intValue());
			stmt.executeUpdate();
			stmt.close();
			// 更新lastflag
			stmt = con.prepareStatement(sql2);
			stmt.setString(1, pk_psndoc);
			stmt.executeUpdate();
			stmt.close();
			stmt = con.prepareStatement(sql3);
			stmt.setString(1, pk_psndoc);
			stmt.executeUpdate();
			stmt.close();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateRecornum",
				new Object[] { tableName, pk_psndoc, recordNum, isAdd });
		/** ********************************************************** */
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-5 14:32:47)
	 * 
	 * @param pk_psndocs
	 *            java.lang.String[]
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public void updateShoworder(String[] pk_psndocs,HashMap psnshoworder)
			throws java.sql.SQLException {
		if(psnshoworder==null||psnshoworder.size()<0){
			return;
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateShoworder",
				new Object[] { pk_psndocs, psnshoworder });
		/** ********************************************************** */

		String sqlupdate = "update bd_psndoc set showorder = ? where pk_psndoc = ?";
		
		sqlupdate = HROperatorSQLHelper.getSQLWithOperator(sqlupdate);
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sqlupdate);
			for (int i = 0; i < pk_psndocs.length; i++) {
				int showorder = 999999;
				if(psnshoworder.get(pk_psndocs[i])!=null){
					showorder = ((Integer)psnshoworder.get(pk_psndocs[i])).intValue();
				}else{
					continue;
				}
				stmt.setInt(1, showorder);
				stmt.setString(2, pk_psndocs[i]);
				stmt.executeUpdate();
			}
			stmt.executeBatch();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "updateShoworder",
				new Object[] { pk_psndocs, psnshoworder });
		/** ********************************************************** */
	}

	/**
	 * 此处插入方法描述。 创建日期：(2005-03-28 21:03:27)
	 * 
	 * @param power
	 *            java.lang.String
	 */
	public String getBillTempletID(String pk_corp, String userid,
			String nodecode) throws SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "getBillTempletID",
				new Object[] { pk_corp, userid, nodecode });
		/** ********************************************************** */
		String templateid = "0001AA10000000000B7G";
		String sql1 = "select templateid from pub_systemplate where funnode =? and tempstyle = 0 and operator = ? and pk_corp = ?";
		String sql2 = "select templateid from pub_systemplate inner join sm_user_rela on groupid = operator where funnode = ? and tempstyle = 0 and userid = ? and pub_systemplate.pk_corp = ?";
		String sql3 = "select templateid from pub_systemplate where funnode = ? and (operator ='' or operator is null) and (pk_corp = '@@@@' or pk_corp is null or pk_corp= '' ) and tempstyle = 0 ";

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			ResultSet rs = null;
			con = getConnection();
			stmt = con.prepareStatement(sql1);
			stmt.setString(1, nodecode);
			stmt.setString(2, userid);
			stmt.setString(3, pk_corp);
			rs = stmt.executeQuery();
			if (rs.next()) {
				templateid = rs.getString(1);
				return templateid;
			}
			stmt.close();
			// 更新lastflag
			stmt = con.prepareStatement(sql2);
			stmt.setString(1, nodecode);
			stmt.setString(2, userid);
			stmt.setString(3, pk_corp);
			rs = stmt.executeQuery();
			if (rs.next()) {
				templateid = rs.getString(1);
				return templateid;
			}
			stmt.close();
			stmt = con.prepareStatement(sql3);
			stmt.setString(1, nodecode);
			rs = stmt.executeQuery();
			if (rs.next()) {
				templateid = rs.getString(1);
				return templateid;
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "getBillTempletID",
				new Object[] { pk_corp, userid, nodecode });
		/** ********************************************************** */
		return templateid;
	}

	public void synchronAccpsndoc(String regulardata, String pk_psndoc)
			throws java.sql.SQLException, nc.bs.pub.SystemException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "synchronAccpsndoc",
				new Object[] { regulardata, pk_psndoc });
		/** ********************************************************** */

		String sql = "update bd_psndoc set regulardata = ?  where pk_psndoc =?";

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, regulardata);
			stmt.setString(2, pk_psndoc);
			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "synchronAccpsndoc",
				new Object[] { regulardata, pk_psndoc });
		/** ********************************************************** */

	}

	/**
	 * 根据条件查询部门信息。 创建日期：(2005-7-14 14:32:22)
	 * 
	 * @return nc.vo.bd.b04.DeptdocVO
	 * @param vos
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public DeptdocVO[] queryDeptVOs(String wheresql)
			throws java.sql.SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		String sql = "select pk_deptdoc,deptcode,deptname,pk_fathedept,pk_corp,canceled ,hrcanceled,innercode from bd_deptdoc where ";
		sql += wheresql;
		DeptdocVO[] vos = null;
		try {

			conn = getConnection();
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);

			Vector v = new Vector();
			while (result.next()) {
				DeptdocVO vo = new DeptdocVO();
				vo.setPk_deptdoc(result.getString(1));
				vo.setDeptcode(result.getString(2));
				vo.setDeptname(result.getString(3));
				String fatherpk = result.getString(4);
				vo.setPk_fathedept(fatherpk == null ? null : fatherpk);
				vo.setPk_corp(result.getString(5));
				String cancled = result.getString(6);
				if(cancled!=null&& cancled.equalsIgnoreCase("Y")){
					vo.setCanceled(new UFBoolean(true));
				}else{
					vo.setCanceled(new UFBoolean(false));
				}
				String hrcancled = result.getString(7);
				if(hrcancled!=null&& hrcancled.equalsIgnoreCase("Y")){
					vo.setHrcanceled(new UFBoolean(true));
				}else{
					vo.setHrcanceled(new UFBoolean(false));
				}
				// 
				vo.setInnercode(result.getString(8));
				v.addElement(vo);
			}
			vos = new DeptdocVO[v.size()];
			v.copyInto(vos);
			return vos;

		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 【员工信息采集】打印时，调用的方法，算法模仿querySql（String sql）， 因为bd_accpsndoc.dutyname
	 * 与om_duty.dutyname字段同名，而两个字段存放的值不一样：bd_accpsndoc.dutyname存放主键
	 * om_duty.dutyname存放名称。 把所有的值都存放在vo中，不管该值是否为空。 创建日期：(2004-5-31 15:46:52)
	 * 
	 * @return java.util.Vector
	 * @param sql
	 *            java.lang.String
	 */
	public GeneralVO[] querySqlForPrint(String sql)
			throws java.sql.SQLException, java.io.IOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		ResultSetMetaData meta = null;
		try {

			conn = getConnection();
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);

			meta = result.getMetaData();
			String[] fieldNames = new String[meta.getColumnCount()];
			int[] fieldTypes = new int[fieldNames.length];
			for (int i = 0; i < fieldNames.length; i++) {
				fieldNames[i] = meta.getColumnName(i + 1).toLowerCase();
				fieldTypes[i] = meta.getColumnType(i + 1);
			}

			Vector v = new Vector();
			while (result.next()) {
				GeneralVO gvo = new GeneralVO();
				for (int i = 0; i < fieldNames.length; i++) {
					if (BSUtil.isBinary(fieldTypes[i])) { // 如果是blob类型
						// 50 fixed
						// byte[] data = ((nc.bs.mw.sql.UFResultSet) result)
						// .getBlobBytes(i + 1);
						byte[] data = ((MemoryResultSet) result)
								.getBytes(i + 1);
						// 50 end
						gvo.setFieldValue(fieldNames[i], data);
					} else if (!BSUtil.isSkipField(fieldNames[i])) {
						Object value = result.getObject(i + 1);
						if (value != null) {
							if (value instanceof String)
								value = ((String) value).trim();
							// gvo.setFieldValue(fieldNames[i], value);
						}
						gvo.setFieldValue(fieldNames[i], value);
					} else
						result.getObject(i + 1);
				}
				if (gvo.getAttributeValue("pk_corp") == null
						&& gvo.getAttributeValue("man_pk_corp") != null) {
					gvo.setAttributeValue("pk_corp", gvo
							.getAttributeValue("man_pk_corp"));
				}
				v.addElement(gvo);
			}

			return (GeneralVO[]) v.toArray(new GeneralVO[0]);

		} finally {
			if (meta != null) {
				meta = null;
			}
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	/**
	 * 按照条件多表关联查询入职申请单
	 * @return
	 */
	public DocApplyHVO[] queryDocApplyBillByCon(String where)throws java.sql.SQLException, java.io.IOException{
		if(where.trim().toLowerCase().startsWith("and")){
			where = where.trim().substring(4);
		} 
		if(!where.trim().toLowerCase().startsWith("where")){
			where = "where "+where;
		}
		if(where.indexOf("pk_docapply_h")>0 && where.indexOf("h.pk_docapply_h")<1){
			where = where.replaceAll("pk_docapply_h", "h.pk_docapply_h");
		}
		
		where = where.replaceAll("hi_docapply_b","b");
		where =where.replaceAll("b.psncl", "p.pk_psncl");
		where =where.replaceAll("b.psncode", "p.psncode");
		where = where.replaceAll("hi_docapply_h", "h");
		
		DocApplyHVO[] docapplyhvos = null;

		String sql = " SELECT distinct h.PK_DOCAPPLY_H, h.PK_BILLTYPE, h.BILLSTATE, h.VBILLNO, h.PK_PROPOSER, h.APPLYDATE, h.APPROVEDATE, h.VSUMM, h.PK_CORP,  h.APPROVENOTE ,h.busitype,h.attachmentinf " +
		" from hi_docapply_h h left outer join hi_docapply_b b on h.pk_docapply_h = b.pk_docapply_h " +
		" left outer join bd_psndoc  p on b.pk_psndoc = p.pk_psndoc " +where +"order by h.VBILLNO";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Vector v  = new Vector();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);			
			rs = stmt.executeQuery();
			while (rs.next()) {
				DocApplyHVO temp = new DocApplyHVO();
				temp.setPk_docapply_h(rs.getString(1));
				temp.setPk_billtype(rs.getString(2));
				temp.setBillstate(rs.getInt(3));
				temp.setVbillno(rs.getString(4));
				temp.setPk_proposer(rs.getString(5));
				if(rs.getString(6)!=null){
					temp.setApplydate(new UFDate(rs.getString(6)));
				}			
				if(rs.getString(7)!=null){
					temp.setApprovedate(new UFDate(rs.getString(7)));
				}
				temp.setVsumm(rs.getString(8));
				temp.setPk_corp(rs.getString(9));
				temp.setApprovenote(rs.getString(10));
				temp.setBusitype(rs.getString(11));
				temp.setAttachmentinf(rs.getString(12));
				v.addElement(temp);
			}
		} finally {

			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		docapplyhvos = new DocApplyHVO[v.size()];
		if(v.size()>0){
			v.copyInto(docapplyhvos);
		}

		return docapplyhvos;
	}
	/**
	 * 
	 * @param psndoc
	 * @return
	 * @throws java.sql.SQLException
	 * @throws java.io.IOException
	 */
	public GeneralVO queryDetail(GeneralVO psndoc,String[] fieldnames)
			throws java.sql.SQLException, java.io.IOException {

		String sql = "select pk_deptdoc,pk_psncl,pk_dutyrank,pk_om_job,indutydate,outdutydate,jobrank,jobseries,dutyname,series,isreturn,bd_psndoc.pk_corp,bd_psndoc.onpostdate " ;
		if (fieldnames != null && fieldnames.length > 0){
			for (int i = 0; i < fieldnames.length; i++) {
					sql += ", " + fieldnames[i];
				}
		}
		sql += " from bd_psndoc left outer join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc where pk_psndoc= ? ";
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (psndoc.getAttributeValue("pk_psndoc") != null)
				stmt.setString(1, (String) psndoc
						.getAttributeValue("pk_psndoc"));
			rs = stmt.executeQuery();
			while (rs.next()) {
				psndoc.setAttributeValue("pk_deptdoc", rs.getString(1));
				psndoc.setAttributeValue("pk_psncl", rs.getString(2));
				psndoc.setAttributeValue("pk_dutyrank", rs.getString(3));
				psndoc.setAttributeValue("pk_om_job", rs.getString(4));
				psndoc.setAttributeValue("indutydate", rs.getString(5));
				psndoc.setAttributeValue("outdutydate", rs.getString(6));
				psndoc.setAttributeValue("jobrank", rs.getString(7));
				psndoc.setAttributeValue("jobseries", rs.getString(8));
				psndoc.setAttributeValue("dutyname", rs.getString(9));
				psndoc.setAttributeValue("series", rs.getString(10));
				psndoc.setAttributeValue("isreturn", rs.getString(11));
				psndoc.setAttributeValue("pk_corp", rs.getString(12));
				psndoc.setAttributeValue("onpostdate", rs.getString(13));
				if (fieldnames != null && fieldnames.length > 0){
					for (int i = 0; i < fieldnames.length; i++) {
							psndoc.setAttributeValue(fieldnames[i], rs.getObject(14+i));
						}
				}
			}
		} finally {

			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return psndoc;
	}
	
	/**
	 * 
	 * @param psndoc
	 * @return
	 * @throws java.sql.SQLException
	 * @throws java.io.IOException
	 */
	public GeneralVO queryDetailForDimis(GeneralVO psndoc,String[] fieldnames)
			throws java.sql.SQLException, java.io.IOException {

		String sql = "select pk_deptdoc,pk_psncl,pk_dutyrank,pk_om_job,indutydate,outdutydate,jobrank,jobseries,dutyname,series,isreturn,bd_psndoc.pk_corp " ;
		if (fieldnames != null && fieldnames.length > 0){
			for (int i = 0; i < fieldnames.length; i++) {
					sql += ", " + fieldnames[i];
				}
		}
		sql += " from bd_psndoc left outer join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc where pk_psndoc= ? ";
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (psndoc.getAttributeValue("pk_psndoc") != null)
				stmt.setString(1, (String) psndoc
						.getAttributeValue("pk_psndoc"));
			rs = stmt.executeQuery();
			while (rs.next()) {
				psndoc.setAttributeValue("pk_deptdoc", rs.getString(1));
				psndoc.setAttributeValue("pk_psncl", rs.getString(2));
				psndoc.setAttributeValue("pk_dutyrank", rs.getString(3));
				psndoc.setAttributeValue("pk_om_job", rs.getString(4));
				psndoc.setAttributeValue("indutydate", rs.getString(5));
				psndoc.setAttributeValue("outdutydate", rs.getString(6));
				psndoc.setAttributeValue("jobrank", rs.getString(7));
				psndoc.setAttributeValue("jobseries", rs.getString(8));
				psndoc.setAttributeValue("dutyname", rs.getString(9));
				psndoc.setAttributeValue("series", rs.getString(10));
				psndoc.setAttributeValue("isreturn", rs.getString(11));
				psndoc.setAttributeValue("pk_corp", rs.getString(12));
				if (fieldnames != null && fieldnames.length > 0){
					for (int i = 0; i < fieldnames.length; i++) {
							psndoc.setAttributeValue(fieldnames[i], rs.getObject(13+i));
						}
				}
			}
		} finally {

			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return psndoc;
	}

	public int[] getDefItem(String tablename) throws java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		int[] defItems = new int[2];// defItems[0]:记录公司自定义项的个数，defItems[1]:记录集团自定义项的个数.

		try {
			conn = getConnection();
			String sql = "select idefcorpnum,idefgroupnum from hr_defdoc where vtablename=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, tablename);
			result = stmt.executeQuery();
			if (result.next()) {
				defItems[0] = result.getInt(1);
				defItems[1] = result.getInt(2);
			}
			return defItems;
		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 得到在指定公司兼职的人员所在的公司s。 创建日期：(2004-11-24 10:58:31)
	 * 
	 * @return java.lang.String[]
	 * @param pk_corp
	 *            java.lang.String
	 */
	public String[] queryDeptPowerBySql(String sql) throws SQLException {

		Vector vecResult = new Vector();
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			//
			while (rs.next()) {
				String pk_deptdoc = rs.getString(1);
				vecResult.addElement(pk_deptdoc);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		String[] pk_deptdocs = null;
		if (vecResult.size() > 0) {
			pk_deptdocs = new String[vecResult.size()];
			vecResult.copyInto(pk_deptdocs);
		}
		return pk_deptdocs;
	}

	/**
	 * 根据查询条件获得要对应辅助信息。 --解决V30中问题编码为200507111204423395 和
	 * 200507210913444548中的同步问题 创建日期：(2005-7-13 10:21:02)
	 * 
	 * @return java.lang.String
	 * @param tablename
	 *            java.lang.String
	 * @param fldcode
	 *            java.lang.String
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param chkformula
	 *            java.lang.String
	 */
	public String findItemInf(String tablename, String fldcode,
			String pk_psnbasdoc, String chkformula) throws SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "findItemInf",
				new Object[] { tablename, fldcode, pk_psnbasdoc, chkformula });
		/** ********************************************************** */
		String strItemInf = null;
		String sql = null;
		StringBuffer strBuf = new StringBuffer("select ");
		strBuf.append(fldcode);
		strBuf.append(" from ");
		strBuf.append(tablename);
		strBuf.append(" where pk_psnbasdoc = ? and ( ");
		if (chkformula != null && chkformula.length() > 0) {
			strBuf.append(chkformula);
		} else {
			// updateBy lvguodong 2010-04-28 start
//			strBuf.append(" recordnum = 0");
			strBuf.append(" (recordnum = 0 or recordnum is null)");
			// updateBy lvguodong 2010-04-28 end
		}
		sql = strBuf.toString();
		sql += " ) ";
		if ("hi_psndoc_part".equalsIgnoreCase(tablename)) {
			sql = StringUtil.replaceAllString/*AllString*/(sql,
					"hi_psndoc_part", "hi_psndoc_deptchg");
			sql += " and jobtype <> 0 ";
		}
		sql += "  order by recordnum";

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psnbasdoc);
			ResultSet rs = stmt.executeQuery();
			//
			if (rs.next()) {
				// strItemInf :
				strItemInf = rs.getString(1);
			}

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "findItemInf",
				new Object[] { tablename, fldcode, pk_psnbasdoc, chkformula });
		/** ********************************************************** */

		return strItemInf;
	}

	/**
	 * V35
	 * 
	 * @param data
	 * @param tableCode
	 * @return
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 */
	public String[] insertHiItemSet(CircularlyAccessibleValueObject[] data,
			String tableCode) throws java.io.IOException, java.sql.SQLException {
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "insertTable",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		Connection con = null;
		PreparedStatement stmt = null;
		String[] keys = null;
		// int[] counts = null;
		if (data == null || data.length == 0) {
			return null;
		}
		try {
			String sql = " insert into hi_itemset "
					+ "\t (pk_itemset,pk_flddict,showorder,funcode,pk_corp,userid)  "
					+ " values( ?,?,?,?,?,?)";

			con = getConnection();

			keys = new String[data.length];
			stmt = prepareStatement(con, sql);

			for (int i = 1; i < data.length; i++) {
				keys[i] = getOID();
				stmt.setString(1, keys[i]);

				if (data[i].getAttributeValue("pk_flddict") == null) {
					stmt.setNull(2, Types.CHAR);
				} else {
					stmt.setString(2, (String) data[i]
							.getAttributeValue("pk_flddict"));
				}

				if (data[i].getAttributeValue("showorder") == null) {
					stmt.setNull(3, Types.INTEGER);
				} else {
					stmt.setInt(3, ((Integer) data[i]
							.getAttributeValue("showorder")).intValue());
				}

				if (data[0].getAttributeValue("funcode") == null) {
					stmt.setNull(4, Types.CHAR);
				} else {
					stmt.setString(4, (String) data[0]
							.getAttributeValue("funcode"));
				}
				if (data[0].getAttributeValue("pk_corp") == null) {
					stmt.setNull(5, Types.CHAR);
				} else {
					stmt.setString(5, (String) data[0]
							.getAttributeValue("pk_corp"));
				}
				if (data[i].getAttributeValue("userid") == null) {
					stmt.setNull(6, Types.CHAR);
				} else {
					stmt.setString(6, ((String) data[i]
							.getAttributeValue("userid")));
				}

				executeUpdate(stmt);
			}

			// int[] counts =
			executeBatch(stmt);

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "insertTable",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		return keys;
	}

	/**
	 * V35
	 * 
	 * @param data
	 * @param tableCode
	 * @return
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 */
	public int deleteTable(CircularlyAccessibleValueObject[] data,
			String tableCode) throws java.io.IOException, java.sql.SQLException {
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "deleteTable",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		Connection con = null;
		PreparedStatement stmt = null;
		int count = 0;
		// int[] counts = null;
		if (data == null || data.length == 0) {
			return 0;
		}
		try {
			String sql = " delete from hi_itemset where pk_corp=? and funcode=? and userid=?";

			con = getConnection();
			stmt = prepareStatement(con, sql);
			if (data[0].getAttributeValue("pk_corp") == null) {
				stmt.setNull(1, Types.CHAR);
			} else {
				stmt
						.setString(1, (String) data[0]
								.getAttributeValue("pk_corp"));
			}
			if (data[0].getAttributeValue("funcode") == null) {
				stmt.setNull(2, Types.CHAR);
			} else {
				stmt
						.setString(2, (String) data[0]
								.getAttributeValue("funcode"));
			}
			if (data[0].getAttributeValue("userid") == null) {
				stmt.setNull(3, Types.CHAR);
			} else {
				stmt
						.setString(3, (String) data[0]
								.getAttributeValue("userid"));
			}
			count = stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "deleteTable",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		return count;
	}

	/**
	 * V35 add
	 * 
	 * @param table
	 * @param data
	 * @param pkfield
	 * @param key
	 * @return
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 * @throws NamingException 

	 */
	public String[] insertSubTable(String table,
			nc.vo.pub.CircularlyAccessibleValueObject[] data, String pkfield)
			throws IOException, SQLException, DAOException, NamingException {
		Connection conn = null;
		PreparedStatement stmt = null;

		boolean hasByteField = false;
		String byteField = null;
		byte[] byteValue = null;
		String[] keys = null;
		// int[] counts = null;
		try {
			if (data == null || data.length == 0) {
				return null;
			}
			// 先更新当前表中人员的recordnum
			updateRecordnum(table, data);
			// 从data[0]中 过滤掉 "psncode", "psnname", "deptname" 字段
			String[] fieldNames = data[0].getAttributeNames();
			Vector vv = new Vector();
			for (int i = 0; i < fieldNames.length; i++) {
				if (!"psncode".equalsIgnoreCase(fieldNames[i])
						&& !"psnname".equalsIgnoreCase(fieldNames[i])
						&& !"deptname".equalsIgnoreCase(fieldNames[i])
						&& !BSUtil.isSkipField(fieldNames[i])) {
					vv.addElement(fieldNames[i]);
				}
			}
			String[] fields = new String[vv.size()];
			vv.copyInto(fields);
			// 组装sql语句
			String sql = "insert into " + table + "(" + pkfield;
			String fieldsPart = "";
			String valuePart = " values( ?";
			for (int i = 0; i < fields.length; i++) {
				fieldsPart += ",";
				fieldsPart += fields[i];
				valuePart += ",";
				valuePart += "?";
			}
			sql += fieldsPart + ",recordnum,lastflag) " + valuePart + ",?,?)";
			
			sql = HROperatorSQLHelper.getSQLWithOperator(sql);
			
			// 执行批量处理
			conn = getConnection();
			keys = new String[data.length];
			stmt = prepareStatement(conn, sql);
			for (int i = 0; i < data.length; i++) {
				keys[i] = getOID();
				stmt.setString(1, keys[i]);
				for (int j = 1; j <= fields.length; j++) {
					Object value = data[i].getAttributeValue("$"
							+ fields[j - 1]);
					if (value == null) {
						value = data[i].getAttributeValue(fields[j - 1]);
						if (value != null
								&& value.toString().trim().equalsIgnoreCase("")) {
							value = null;
						}
					}

					// if (value != null && !(value instanceof byte[]))
					{
						if (BSUtil.isSelfDef(fields[j - 1])) {
							if (value == null) {
								stmt.setNull(j + 1, Types.CHAR);
							} else {
								stmt.setString(j + 1, value.toString());
							}

						} else if (BSUtil.isNumeric(value)) {
							if (value instanceof Integer
									|| value instanceof Short) {
								if (value == null) {
									stmt.setNull(j + 1, Types.INTEGER);
								} else {
									stmt.setInt(j + 1, ((Integer) value)
											.intValue());
								}
							} else if (value instanceof BigDecimal) {
								if (value == null) {
									stmt.setNull(j + 1, Types.DECIMAL);
								} else {
									stmt.setBigDecimal(j + 1,
											(BigDecimal) value);
								}
							} else if (value instanceof Float) {
								if (value == null) {
									stmt.setNull(j + 1, Types.FLOAT);
								} else {
									stmt.setFloat(j + 1, ((Float) value)
											.floatValue());
								}
							} else if (value instanceof Double) {
								if (value == null) {
									stmt.setNull(j + 1, Types.DOUBLE);
								} else {
									stmt.setDouble(j + 1, ((Double) value)
											.doubleValue());
								}
							} else if (value instanceof UFDouble) {
								if (value == null) {
									stmt.setNull(j + 1, Types.DOUBLE);
								} else {
									stmt.setDouble(j + 1, ((UFDouble) value)
											.doubleValue());
								}
							}

						} else {
							if (value == null) {
								stmt.setNull(j + 1, Types.CHAR);
							} else {
								stmt.setString(j + 1, value.toString());
							}
						}
					}
					// else if (value != null && value instanceof byte[]) {
					// // hasByteField = true;
					// // byteField = fieldNames[i];
					// // byteValue = (byte[]) value;
					// }
				}
				stmt.setInt(fields.length + 2, 0);
				stmt.setString(fields.length + 3, "Y");

				// 如果当前更新的值中含有blob类型则：
				if (hasByteField) {
					// updateBlob(pkfield, keys[i], byteField, byteValue,
					// table);
					PsnInfDAO dao = new PsnInfDAO();
					dao.updateBlob(table, byteField, byteValue, pkfield,
							keys[i]);
				}
				executeUpdate(stmt);
				// stmt.addBatch();
				// stmt.executeUpdate();
			}
			// counts = stmt.executeBatch();
			// counts =
			executeBatch(stmt);

			return keys;
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}



	private int[] updateRecordnum(String table,
			nc.vo.pub.CircularlyAccessibleValueObject[] data)
			throws java.io.IOException, java.sql.SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		int[] counts = null;
		try {
			if (data == null || data.length == 0) {
				return null;
			}
			String psnpk = "pk_psnbasdoc";
			if(isTraceTable(table)){
				psnpk = "pk_psndoc";
			}
			String setsql = "select reccharacter from hi_setdict where setcode like ?";
			String updatelastflag = "update "+table+ " set lastflag = ? where "+psnpk+" = ?";
			conn = getConnection();
			String sql = " update " + table + " set recordnum =recordnum+1 ";
			String wheresql = " where "+psnpk+" = ?";

			sql = sql + wheresql;
			conn = getConnection();

			stmt = prepareStatement(conn, sql);
			for (int i = 0; i < data.length; i++) {
				Object value = data[i].getAttributeValue(psnpk);
				if (value == null) {
					stmt.setNull(1, Types.CHAR);
				} else {
					stmt.setString(1, (String) value);
				}
				executeUpdate(stmt);
			}
			counts = executeBatch(stmt);
			//查询信息集类型
			String lastflag = "Y";
			if("hi_psndoc_edu".equalsIgnoreCase(table)){
				lastflag = "Y";
			}else{
				stmt.close();
				stmt = conn.prepareStatement(setsql);
				stmt.setString(1, table);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()){
					String recflag = rs.getString(1);
					if("3".equals(recflag)){
						lastflag = "Y";
					}else{
						lastflag = "N";
					}
				}
			}
			//更新已有信息集数据的lastflag
			stmt = prepareStatement(conn, updatelastflag);
			for (int i = 0; i < data.length; i++) {
				Object value = data[i].getAttributeValue(psnpk);
				stmt.setString(1, lastflag);
				if (value == null) {
					stmt.setNull(2, Types.CHAR);
				} else {
					stmt.setString(2, (String) value);
				}
				executeUpdate(stmt);
			}
			counts = executeBatch(stmt);
			return counts;

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	public int deleteEmployeeRef(nc.vo.hi.hi_301.GeneralVO[] data,
			String tableCode) throws java.io.IOException, java.sql.SQLException {
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "deleteEmployeeRef",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		Connection con = null;
		PreparedStatement stmt = null;
		int count = 0;
		if (data == null || data.length == 0) {
			return 0;
		}
		try {
			String sql = " delete from hi_psndoc_ref where pk_psnbasdoc=?";

			con = getConnection();
			stmt = prepareStatement(con, sql);
			for (int i = 0; i < data.length; i++) {
				if (data[i].getAttributeValue("pk_psnbasdoc") == null) {
					stmt.setNull(1, Types.CHAR);
				} else {
					stmt.setString(1, (String) data[i]
							.getAttributeValue("pk_psnbasdoc"));
				}
				executeUpdate(stmt);
				count++;
			}
			executeBatch(stmt);

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "deleteEmployeeRef",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		return count;
	}
	
	/**
	 * 应判断人员是否离职或已调离
	 */
	public void checkPsnHaveChanged(nc.vo.hi.hi_301.GeneralVO[] data) throws BusinessException{
		if(data==null || data.length<1) return;
		String pks ="";
		for(int i=0;i<data.length;i++){
			//若是被拒绝的就不检查了
			if((Boolean)data[i].getAttributeValue("isRefused")) continue;
			pks += ",'"+data[i].getAttributeValue("oripk_psndoc")+"'";
		}
		if(pks.length()<20) return;
		pks = pks.substring(1);
		String sql = "select distinct psnname from bd_psndoc where  psnclscope not in (0,3,5) and " +
				"pk_psndoc in ("+pks+")";
		CommonVO[] illegalpsns = null;
		try{
			List<CommonVO> list =
				(List<CommonVO>) PubDelegator.getIPersistenceHome().executeQuery(sql,null,
						new CommonVOProcessor());

			if (!list.isEmpty())
			{
				illegalpsns = list.toArray(new CommonVO[list.size()]);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//如果有不符合的人员，则抛出异常
		if (illegalpsns!=null && illegalpsns.length>0){
			String psnnames = "";
			for(int i=0;i<illegalpsns.length;i++){
				String psnname = (String)illegalpsns[i].getAttributeValue("psnname");
				psnnames = psnnames+","+psnname;
			}
//			throw new BusinessException("以下这些人员已有变动，不能被引用：\n"+psnnames);
			throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600700", 
					"UPP600700-000236")/* @res "以下这些人员已有变动，不能被引用：\n" */
					+ psnnames);
		}
	}
	/**
	 * 把引用的人员转入到管理档案中
	 * 
	 */
	public int addRefEmployees(nc.vo.hi.hi_301.GeneralVO[] data,
			String tableCode) throws java.io.IOException, java.sql.SQLException {
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "addRefEmployees",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		Connection con = null;
		PreparedStatement stmt = null;
		int count = 0;
		if (data == null || data.length == 0) {
			return 0;
		}
		
		try {
			// 删除原有引用人员信息(已经取消引用)
			String sqlDel = "delete from bd_psndoc where pk_psnbasdoc = ? and pk_corp = ? and sealdate is not null";
		
			String selectfield = " pk_psndoc,amcode,clerkcode ,clerkflag,indocflag ,innercode,maxinnercode ,pk_corp,pk_deptdoc,pk_om_job,pk_psncl,psnclscope,psncode,psnname,sealdate,	def1,def10,def11,def12,def13,def14,def15,def16,def17,def18,def19,def2,def20,def3,def4,def5,def6,def7,def8,def9,indutydate,jobrank,jobseries,outdutydate,pk_psnbasdoc,directleader,dutyname,groupdef1,groupdef10,groupdef11,groupdef12,groupdef13,groupdef14,groupdef15,groupdef16,groupdef17,groupdef18,groupdef19,groupdef2,groupdef20,groupdef3,groupdef4,groupdef5,groupdef6,groupdef7,groupdef8,groupdef9,insource,iscalovertime,outmethod,pk_clerkclass,pk_dutyrank,pk_psntype,poststat,recruitresource,regular,regulardata,series,tbm_prop,timecardid,wastopdate ";
			String desttable = " bd_psndoc";
			String srctable = " hi_psndoc_ref";
			String sql = " insert into " + desttable + " ( " + selectfield
					+ ")" + " select " + selectfield + " from " + srctable
					+ " where pk_psnbasdoc = ?";
			//
			String sqlUpdate = "update bd_psndoc set isreferenced = 'Y' where pk_psndoc in (select pk_psndoc from hi_psndoc_ref where pk_psnbasdoc = ? ) ";
			
			sql = HROperatorSQLHelper.getSQLWithOperator(sql);
			
			sqlUpdate = HROperatorSQLHelper.getSQLWithOperator(sqlUpdate);
			
			con = getConnection();

			for (int i = 0; i < data.length; i++) {
				Boolean isAffirmed = (Boolean) data[i]
						.getAttributeValue("isAffirmed");// 只有确认的人员才转入人员档案
				if (isAffirmed != null && isAffirmed.booleanValue()) {
					// 更新是否引用标记
					stmt = con.prepareStatement(sqlDel);
					stmt.setString(1, (String) data[i]
							.getAttributeValue("pk_psnbasdoc"));
					stmt.setString(2, (String) data[i]
							.getAttributeValue("pk_corp"));
					stmt.executeUpdate();
					stmt.close();
					// 转入人员档案
					stmt = con.prepareStatement(sql);
					if (data[i].getAttributeValue("pk_psnbasdoc") == null) {
						stmt.setNull(1, Types.CHAR);
					} else {
						stmt.setString(1, (String) data[i]
								.getAttributeValue("pk_psnbasdoc"));
					}
					stmt.executeUpdate();
					stmt.close();
					// 更新是否引用标记
					stmt = con.prepareStatement(sqlUpdate);
					if (data[i].getAttributeValue("pk_psnbasdoc") == null) {
						stmt.setNull(1, Types.CHAR);
					} else {
						stmt.setString(1, (String) data[i]
								.getAttributeValue("pk_psnbasdoc"));
					}
					stmt.executeUpdate();
					stmt.close();
					count++;
				}
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "addRefEmployees",
				new Object[] { data, tableCode });
		/** ********************************************************** */
		return count;
	}

	public nc.vo.hi.hi_301.GeneralVO[] queryRefEmployees(String strParam,
			Integer type, String power) throws java.io.IOException,
			java.sql.SQLException {
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryRefEmployees",
				new Object[] { strParam, type });
		/** ********************************************************** */
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		nc.vo.hi.hi_301.GeneralVO[] refEmployees = null;
		if (strParam == null || type == null) {
			return null;
		}
		try {
			String sql = " select distinct ";
			String[] fields = { "hi_psndoc_ref.psncode", "bd_psndoc.psncode",
					"hi_psndoc_ref.psnname", "bd_corp.unitname",
					"bd_deptdoc.deptname", "om_job.jobname",
					"bd_psncl.psnclassname", "hi_psndoc_ref.pk_psnbasdoc",
					"hi_psndoc_ref.pk_corp", "hi_psndoc_ref.pk_deptdoc",
					"hi_psndoc_ref.pk_om_job", "hi_psndoc_ref.pk_psncl",
					"hi_psndoc_ref.userid", "hi_psndoc_ref.appdate",
					"hi_psndoc_ref.oriunitname", "hi_psndoc_ref.orideptname",
					"hi_psndoc_ref.orijobname","hi_psndoc_ref.oripk_psndoc",  };
			for (int i = 0; i < fields.length; i++) {
				String field = fields[i];
				// if(type.intValue() == 1 && i == 0){
				// field = "bd_psndoc.psncode";
				// }
				sql += field;
				if (i < fields.length - 1) {
					sql += ",";
				}
			}
			String from = " from hi_psndoc_ref  inner join bd_psnbasdoc on bd_psnbasdoc.pk_psnbasdoc = hi_psndoc_ref.pk_psnbasdoc left join bd_corp on bd_corp.pk_corp = hi_psndoc_ref.pk_corp left join bd_deptdoc on bd_deptdoc.pk_deptdoc = hi_psndoc_ref.pk_deptdoc left join om_job on om_job.pk_om_job =  hi_psndoc_ref.pk_om_job left join bd_psncl on bd_psncl.pk_psncl = hi_psndoc_ref.pk_psncl ";
			from += " inner join bd_psndoc on bd_psndoc.pk_psnbasdoc = hi_psndoc_ref.pk_psnbasdoc ";
			String where = null;
			if (type.intValue() == 0) {// 查看申请
				where = " where hi_psndoc_ref.userid = ? and bd_psnbasdoc.pk_corp =bd_psndoc.pk_corp ";
			} else if (type.intValue() == 1) {// 确认引用
				where = " where bd_psnbasdoc.pk_corp = ? and bd_psndoc.pk_corp = ? ";
			}
			/* V55 modify 取消部门档案权限
			if (power != null && !power.trim().equalsIgnoreCase("0=0")) {
					where += " and hi_psndoc_ref.oripk_deptdoc in (" + power + ")";
			}
			*/

			sql += (from + where);
			con = getConnection();
			stmt = prepareStatement(con, sql);
			if (type.intValue() == 0) {
				stmt.setString(1, strParam);
			} else if (type.intValue() == 1) {
				stmt.setString(1, strParam);
				stmt.setString(2, strParam);
			}
			rs = stmt.executeQuery();
			Vector vv = new Vector();
			while (rs.next()) {
				nc.vo.hi.hi_301.GeneralVO vo = new nc.vo.hi.hi_301.GeneralVO();
				for (int i = 0; i < fields.length; i++) {
					vo.setAttributeValue(fields[i], rs.getString(i + 1));
				}
				vv.addElement(vo);
			}
			if (vv.size() > 0) {
				refEmployees = new nc.vo.hi.hi_301.GeneralVO[vv.size()];
				vv.copyInto(refEmployees);
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryRefEmployees",
				new Object[] { strParam, type });
		/** ********************************************************** */
		return refEmployees;
	}

	/**
	 * 此方法可以写得更灵活：改成 queryPsnCountByPk(String table,String pk_field,Object
	 * field_obj)
	 * 
	 * @param table
	 * @param pk
	 * @return
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 */
	public int queryPsnCountByPk(String table, String pk, String condition)
			throws java.io.IOException, java.sql.SQLException {
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryPsnCountByPk",
				new Object[] { table, table });
		/** ********************************************************** */
		Connection con = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {

			String sql = "";
			if ("bd_deptdoc".equalsIgnoreCase(table)) {
				sql = " select count(*) from  hi_psndoc_deptchg left outer join bd_psndoc on hi_psndoc_deptchg.pk_psndoc = bd_psndoc.pk_psndoc  where hi_psndoc_deptchg.pk_deptdoc = ?  and indocflag = 'Y' and jobtype =0 and recordnum = 0 and "+condition;
			} else if ("om_job".equalsIgnoreCase(table)) {
				sql = " select count(*) from  bd_psndoc where pk_om_job = ?  and indocflag = 'Y' and "+condition;
			} else if ("bd_corp".equalsIgnoreCase(table)) {
				sql = " select count(*) from  hi_psndoc_deptchg left outer join bd_psndoc on hi_psndoc_deptchg.pk_psndoc = bd_psndoc.pk_psndoc  where hi_psndoc_deptchg.pk_corp = ?  and indocflag = 'Y' and jobtype =0 and recordnum = 0 and "+condition;
			}

			con = getConnection();
			stmt = prepareStatement(con, sql);

			if (pk == null) {
				stmt.setNull(1, Types.CHAR);
			} else {
				stmt.setString(1, pk);
			}
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryPsnCountByPk",
				new Object[] { table, table });
		/** ********************************************************** */
		return count;
	}

	public void deleteMainPsnDoc(GeneralVO psndocvo)
			throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "deleteMainPsnDoc",
				new Object[] { psndocvo });
		/** ********************************************************** */
		String pk_psnbasdoc = (String) psndocvo.getFieldValue("pk_psnbasdoc");

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql1 = " select 1 from bd_psndoc where pk_psnbasdoc='"
					+ pk_psnbasdoc + "'";
			ResultSet rs = stmt.executeQuery(sql1);
			if (rs.next()) {
				String sql2 = "delete from bd_psndoc where pk_psnbasdoc='"
						+ pk_psnbasdoc + "'";
				stmt.execute(sql2);
			} else {
				String sql = "delete from bd_psnbasdoc where pk_psnbasdoc='"
						+ pk_psnbasdoc + "'";
				stmt.execute(sql);
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "deleteMainPsnDoc",
				new Object[] { psndocvo });
		/** ********************************************************** */

	}

	public String[] queryRefPsndoc(String pk_psnbasdoc)
			throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryRefPsndoc",
				new Object[] { pk_psnbasdoc });
		/** ********************************************************** */

		Connection conn = null;
		Statement stmt = null;
		Vector vv = new Vector();
		String[] psndocs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = " select pk_psndoc from bd_psndoc where pk_psnbasdoc='"
					+ pk_psnbasdoc + "'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				vv.addElement(rs.getString(1));
			}

			psndocs = new String[vv.size()];
			vv.copyInto(psndocs);

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryRefPsndoc",
				new Object[] { pk_psnbasdoc });
		/** ********************************************************** */
		return psndocs;

	}

	public nc.vo.pub.msg.UserNameObject[] getUserObj(String[] userids)
			throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "getUserObj",
				new Object[] { userids });
		/** ********************************************************** */

		Connection conn = null;
		Statement stmt = null;
		Vector vv = new Vector();
		nc.vo.pub.msg.UserNameObject[] userObjs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "select user_name,user_code,cuserid from sm_user where cuserid in (";
			for (int i = 0; i < userids.length; i++) {
				sql += "'" + userids[i] + "'";
				if (i < userids.length - 1) {
					sql += ",";
				}
			}
			sql += " )";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				nc.vo.pub.msg.UserNameObject userObj = new nc.vo.pub.msg.UserNameObject(
						rs.getString(1));
				userObj.setUserCode(rs.getString(2));
				userObj.setUserPK(rs.getString(3));
				vv.addElement(userObj);
			}

			userObjs = new nc.vo.pub.msg.UserNameObject[vv.size()];
			vv.copyInto(userObjs);

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "getUserObj",
				new Object[] { userids });
		/** ********************************************************** */
		return userObjs;

	}
	/**
	 * 
	 * @param pk_corp
	 * @param funcode
	 * @return
	 * @throws java.sql.SQLException
	 */
	public nc.vo.pub.msg.UserNameObject[] getPowerUserid(String pk_corp,String funcode) throws java.sql.SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "getPowerUserid",
				new Object[] { pk_corp, funcode });
		/** ********************************************************** */

		Connection conn = null;
		Statement stmt = null;
		Vector vv = new Vector();
		nc.vo.pub.msg.UserNameObject[] userObjs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = " ";
//			update by zhyan v50 由于权限表改为角色后，查询用户分配的功能权限节点
			if(funcode !=null && funcode.equalsIgnoreCase("600707")){			
			sql = "select distinct user_name,user_code,u.cuserid from sm_user u "
				+"left outer join sm_user_role  ur on ur.cuserid = u.cuserid " 
				+"left outer join sm_power_func pf on ur.pk_role = pf.pk_role "
				+"left outer join sm_funcregister f on pf.resource_data_id = f.cfunid "
				+"where f.fun_code ='"+funcode +"' and (pf.pk_corp = '"+pk_corp+"' or pf.pk_corp ='0001')"
				+" and ur.pk_corp ='"+pk_corp+"' ";
//			}else if(funcode.equalsIgnoreCase("600706")){
//				sql = "select distinct user_name,user_code,u.cuserid from sm_user u "
//					+"left outer join sm_user_role  ur on ur.cuserid = u.cuserid " 
//					+"left outer join sm_power_func pf on ur.pk_role = pf.pk_role "
//					+"where (pf.pk_corp = '"+pk_corp+"' or pf.pk_corp ='0001')"
//					+" and ur.pk_corp ='"+pk_corp+"' ";
			}else {
				sql = "select distinct user_name,user_code,u.cuserid from sm_user_role ur "
					+" left outer join sm_user u on ur.cuserid=u.cuserid " 
					+" left outer join sm_power_func pf on ur.pk_role = pf.pk_role "
					+" where ur.pk_corp = '"+pk_corp+"'order by user_name ";

			}
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				nc.vo.pub.msg.UserNameObject userObj = new nc.vo.pub.msg.UserNameObject(rs.getString(1));
				userObj.setUserCode(rs.getString(2));
				userObj.setUserPK(rs.getString(3));
				vv.addElement(userObj);
			}

			userObjs = new nc.vo.pub.msg.UserNameObject[vv.size()];
			vv.copyInto(userObjs);

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "getPowerUserid",
				new Object[] { pk_corp, funcode });
		/** ********************************************************** */
		return userObjs;

	}
	/**
	 * 
	 * @param pk_corp
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String getRecievers(String pk_corp,int usertype)  throws java.sql.SQLException {
		String sql = "select recieveruserids from hr_message where pk_corp = ?";
		String recievers = null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_corp);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String r =rs.getString(1);
				if(r!=null){
					recievers = r.trim();
				}
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return recievers;
		}
	
	/**
	 * 
	 * @param pk_corp
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String getPsnchgpk(String pk_psndoc)  throws java.sql.SQLException {
		String sql = "select pk_psndoc_sub from hi_psndoc_deptchg where pk_psndoc = ? and recordnum=0 and lastflag='Y' and jobtype=0";
		String pk = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				pk = rs.getString(1).trim();
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return pk;
		}
	
	
	public GeneralVO[] queryListItem(String pk_corp, String funcode,
			String queryScope) throws SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryListItem",
				new Object[] { pk_corp, funcode, queryScope });
		/** ********************************************************** */
		GeneralVO[] items = null;
		boolean isAll = "all".equalsIgnoreCase(queryScope);
		Connection conn = null;
		PreparedStatement stmt = null;
		Vector v = new Vector();
		try {
			conn = getConnection();
			String sql = "select hi_itemset.pk_flddict,setcode,setname,hi_flddict.fldcode,hi_flddict.fldname,hi_flddict.datatype,hi_flddict.pk_fldreftype,hi_flddict.pk_flddict,hi_flddict.pk_setdict,hi_itemset.showorder,hi_flddict.showorder as dictshoworder " +
					"from hi_flddict inner join hi_setdict on hi_flddict.pk_setdict = hi_setdict.pk_setdict " +
					"left outer join hi_itemset on (hi_flddict.pk_flddict = hi_itemset.pk_flddict  and hi_itemset.funcode = '"
					+ funcode
					+ "' and hi_itemset.pk_corp = '"
					+ pk_corp
					+ "' and hi_itemset.userid='"+PubEnv.getPk_user()+"') where (hi_flddict.pk_setdict in ('40000000000000000001','40000000000000000002') and (fldcode not like 'UFAGE%' and fldcode <> 'photo' ) and ( fldcode not like 'UFFORMULA_DATA_%' ) and (hi_flddict.create_pk_corp = '"
					+ pk_corp
					//+ "' or hi_flddict.isshare = 'Y') and hi_flddict.isdisplay = 'Y') or ( hi_flddict.pk_setdict = '20000000000000000001' and hi_flddict.fldcode = 'deptcode') order by hi_itemset.showorder,hi_flddict.pk_setdict,hi_flddict.showorder";
					+ "' or hi_flddict.isshare = 'Y') and hi_flddict.isdisplay = 'Y') "
					+"or ( hi_flddict.pk_setdict = '20000000000000000001' and hi_flddict.fldcode = 'deptcode') "
					+"or ( hi_flddict.pk_setdict = '10000000000000000001' and hi_flddict.fldcode = 'unitcode') "
					+"or ( hi_flddict.pk_setdict = '10000000000000000001' and hi_flddict.fldcode = 'unitname') "
					+"or ( hi_flddict.pk_setdict = '40000000000000000003' and hi_flddict.fldcode = 'jobtype') "
					+"order by hi_itemset.showorder,hi_flddict.pk_setdict,hi_flddict.showorder";
			
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				GeneralVO item = new GeneralVO();
				String pkflddict1 = rs.getString(1);
				item.setAttributeValue("isDisplay",pkflddict1 == null ? new Boolean(false) : new Boolean(true));
				item.setAttributeValue("setcode", rs.getString(2));
				item.setAttributeValue("setname", HRResHiDictHelper.getSetName((String) item.getAttributeValue("setcode"), rs.getString(3)));
				String fldcode = rs.getString(4);
				item.setAttributeValue("fldcode", fldcode);
				item.setAttributeValue("fldname", HRResHiDictHelper.getFldName((String) item.getAttributeValue("setcode"),(String) item.getAttributeValue("fldcode"), rs.getString(5)));
				item.setAttributeValue("datatype", new Integer(rs.getInt(6)));
				item.setAttributeValue("fldreftype", rs.getString(7));
				item.setAttributeValue("pk_flddict", rs.getString(8));
				item.setAttributeValue("pk_setdict", rs.getString(9));
				item.setAttributeValue("showorder", new Integer(rs.getInt(10)));
				// del by zhyan 2006-04-14
				// int showorder = rs.getInt(11);
				// if(pkflddict1==null){
				// item.setAttributeValue("showorder",new Integer(showorder));
				// }

				if (isAll) {
					if ("psncode".equalsIgnoreCase(fldcode)	|| "psnname".equalsIgnoreCase(fldcode) 
							|| "unitname".equalsIgnoreCase(fldcode) || "jobtype".equalsIgnoreCase(fldcode)) {
						item.setAttributeValue("isDisplay", new Boolean(true));
					}
					if("psnname".equalsIgnoreCase(fldcode)){
						if(v.size()>1){
							v.insertElementAt(item, 1);
						}else{
							v.addElement(item);
						}
					}else{
						v.addElement(item);
					}
				} else {
					if (((Boolean) item.getAttributeValue("isDisplay"))
							.booleanValue()) {
						v.addElement(item);
					}
				}
			}
			items = new GeneralVO[v.size()];
			v.copyInto(items);

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryListItem",
				new Object[] { pk_corp, funcode, queryScope });
		/** ********************************************************** */
		return items;
	}

	public String dataUniqueValidate(String pk_corp,
			nc.vo.bd.psndoc.PsndocConsItmVO[] conitmUniqueFields,
			CircularlyAccessibleValueObject psnbasDocVO) throws SQLException {
		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "dataUniqueValidate",
				new Object[] { pk_corp, conitmUniqueFields, psnbasDocVO });
		/** ********************************************************** */
		Connection conn = null;
		PreparedStatement stmt = null;
		String returnMsg = null;
		try {
			conn = getConnection();
			String sql = " select pk_psnbasdoc,pk_corp from bd_psnbasdoc ";
			String where = null;
			if (conitmUniqueFields != null && psnbasDocVO != null) {
				Object pk_psnbasdocObj = psnbasDocVO
						.getAttributeValue("pk_psnbasdoc");
				if (pk_psnbasdocObj != null) {
					where = " where pk_psnbasdoc <> '"
							+ pk_psnbasdocObj.toString() + "'";
				}

				for (int i = 0; i < conitmUniqueFields.length; i++) {// 没有对数据类型为
					// 非字符的进行处理

					String fieldname = conitmUniqueFields[i].getField_name();
					Object value = psnbasDocVO.getAttributeValue(fieldname);
					String whereconditon = "";
					if(fieldname.equals("id")){
						String anotherID="";
						if (value.toString().length() == 18) {
							anotherID = value.toString().substring(0, 6) + value.toString().substring(8, 17);
						}else if(value.toString().length() ==15){//v53
							IDValidateUtil idutil = new IDValidateUtil(value.toString());
							anotherID = idutil.getUpgradeId();//v53
						}
						whereconditon ="(id = '"+value.toString()+"' or id ='"+anotherID+"')";
						
					}else{
						whereconditon = fieldname+ " = '"+ value.toString().trim() + "'";
					}
					if (i == 0) {
						if (where == null) {
							where = " where ";
							where += whereconditon;
						} else {
							where += " and " + whereconditon;
						}
					} else {
						where += " and " + whereconditon;
					}
				}
			}
			if (where != null) {
				sql += where;
			}
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {// 在存在基本档案的情况下，再查找是否存在人员管理档案，如果存在，得到公司，部门，岗位和是否在岗的信息
				String pk_psnbasdoc = rs.getString(1);
				String create_pk_corp = rs.getString(2);

				rs.close();
				stmt.close();
				if (!"0001".equalsIgnoreCase(create_pk_corp)) {
					String sql2 = "  select unitname,deptname,jobname,poststat from bd_psndoc "
							+ " left outer join bd_corp on bd_psndoc.pk_corp = bd_corp.pk_corp"
							+ " left outer join bd_deptdoc on bd_psndoc.pk_deptdoc = bd_deptdoc.pk_deptdoc "
							+ " left outer join om_job on bd_psndoc.pk_om_job = om_job.pk_om_job "
							+ " where pk_psnbasdoc=? order by psnclscope ";// and bd_psndoc.pk_corp
					// = ?
					stmt = conn.prepareStatement(sql2);
					stmt.setString(1, pk_psnbasdoc);
					// stmt.setString(2, create_pk_corp);

					rs = stmt.executeQuery();

					if (rs.next()) {
						String unitname = rs.getString(1);
						returnMsg = (unitname == null ? NCLangResOnserver
								.getInstance().getStrByID("600704",
										"UPP600704-000243")/* @res "无公司" */
								+ ", \n" : NCLangResOnserver.getInstance()
								.getStrByID("600704", "UPP600704-000053")/*
																			 * @res
																			 * "公司:"
																			 */
								+ ":" + unitname + ", \n");
						String deptname = rs.getString(2);
						returnMsg += (deptname == null ? NCLangResOnserver
								.getInstance().getStrByID("600704",
										"UPP600704-000244")/* @res ",无部门" */
								+ ", \n" : NCLangResOnserver.getInstance()
								.getStrByID("600704", "upt600704-000139")/*
																			 * @res
																			 * ",部门:"
																			 */
								+ ":" + deptname + ", \n");
						String jobname = rs.getString(3);
						returnMsg += (jobname == null ? NCLangResOnserver
								.getInstance().getStrByID("600704",
										"UPP600704-000245")/* @res ",无岗位" */
								+ ", \n" : NCLangResOnserver.getInstance()
								.getStrByID("600704", "upt600704-000094")/*
																			 * @res
																			 * ",岗位:"
																			 */
								+ ":" + jobname + ", \n");
						String poststat = rs.getString(4);
						if ("Y".equalsIgnoreCase(poststat)) {
							returnMsg += NCLangResOnserver.getInstance()
									.getStrByID("600704", "UPP600704-000246")/*
																				 * @res
																				 * "在岗"
																				 */;
						} else {
							returnMsg += NCLangResOnserver.getInstance()
									.getStrByID("600704", "UPP600704-000247")/*
																				 * @res
																				 * "不在岗"
																				 */;
						}

					}
				}

			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "dataUniqueValidate",
				new Object[] { pk_corp, conitmUniqueFields, psnbasDocVO });
		/** ********************************************************** */
		return returnMsg;
	}
	/**
	 * 查询是否为再聘返聘人员
	 * @param pk_corp
	 * @param conitmUniqueFields
	 * @param psnbasDocVO
	 * @param returntype --0 为返聘,1 为再聘,如果有其他类型,可再增加
	 * @return
	 * @throws SQLException
	 */
	public GeneralVO checkRehirePerson(String pk_corp,
			nc.vo.bd.psndoc.PsndocConsItmVO[] conitmUniqueFields,
			CircularlyAccessibleValueObject psnbasDocVO,int returntype) throws SQLException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		String returnMsg = null;
		GeneralVO result = null;
		try {
			conn = getConnection();
			String sql = " select bd_psnbasdoc.pk_psnbasdoc,bd_psnbasdoc.pk_corp from bd_psnbasdoc left outer join bd_psndoc on bd_psnbasdoc.pk_psnbasdoc= bd_psndoc.pk_psnbasdoc  ";
			String where = null;
			String subselect = null;
			Object pk_psnbasdocObj = psnbasDocVO.getAttributeValue("pk_psnbasdoc");
			if (returntype ==0){//返聘查询归属范围为离退休人员 psnclscope =3
				if (pk_psnbasdocObj != null) {
					where = " where pk_psnbasdoc <> '"
							+ pk_psnbasdocObj.toString() + "' and bd_psndoc.psnclscope =3 and bd_psndoc.indocflag ='Y' ";
				}else{
					where =" where bd_psndoc.psnclscope = 3 and bd_psndoc.indocflag ='Y' ";
				}
			}else if(returntype ==1){//再聘查询归属范围为解聘人员 psnclscope =2
				if (pk_psnbasdocObj != null) {
					where = " where pk_psnbasdoc <> '"
							+ pk_psnbasdocObj.toString() + "' and bd_psndoc.psnclscope =2 and bd_psndoc.indocflag ='Y' ";
				}else{
					where =" where bd_psndoc.psnclscope =2 and bd_psndoc.indocflag ='Y' ";
				}
			}
			//过滤掉还有在职的工作信息的人员,查询所有工作记录都是非在职
			subselect = " and bd_psndoc.pk_psnbasdoc not in (select pk_psnbasdoc  from bd_psndoc where psnclscope in (0) and pk_psnbasdoc in(select pk_psnbasdoc from bd_psnbasdoc where ";

			if (conitmUniqueFields != null && psnbasDocVO != null) {
				for (int i = 0; i < conitmUniqueFields.length; i++) {// 没有对数据类型为
					// 非字符的进行处理

					String fieldname = conitmUniqueFields[i].getField_name();
					Object value = psnbasDocVO.getAttributeValue(fieldname);
					//fengwei 2010-09-08 返聘、再聘时，身份证号不管是15位还是18位，只要是一个人即可。start
					String whereconditon = "";
					if(fieldname.equals("id")){
						String anotherID="";
						if (value.toString().length() == 18) {
							anotherID = value.toString().substring(0, 6) + value.toString().substring(8, 17);
						}else if(value.toString().length() ==15){//v53
							IDValidateUtil idutil = new IDValidateUtil(value.toString());
							anotherID = idutil.getUpgradeId();//v53
						}
						whereconditon ="(bd_psnbasdoc.id = '"+value.toString()+"' or bd_psnbasdoc.id ='"+anotherID+"')";
						
					}else{
						whereconditon = "bd_psnbasdoc." + fieldname+ " = '"+ value.toString().trim() + "'";
					}
					if (i == 0) {
						if (where == null) {
							where = " where ";
							where += whereconditon;
						} else {
							where += " and " + whereconditon;
						}
						subselect += whereconditon;
					} else {
						where += " and " + whereconditon;
						subselect += " and " + whereconditon;
					}
					// if (value != null) {
//					if (i == 0) {
//						if (where == null) {
//							where = " where ";
//							where += " bd_psnbasdoc."+fieldname + " = '"
//									+ value.toString().trim() + "'";
//						} else {
//							where += " and bd_psnbasdoc." + fieldname + " = '"
//									+ value.toString().trim() + "'";
//						}
//						subselect += " bd_psnbasdoc." + fieldname + " = '"
//									+ value.toString().trim() + "'";
//					} else {
//						where += " and bd_psnbasdoc." + fieldname + " = '"
//								+ value.toString().trim() + "'";
//						subselect += " and bd_psnbasdoc." + fieldname + " = '"
//								+ value.toString().trim() + "'";
//					}
					//fengwei 2010-09-08 返聘、再聘时，身份证号不管是15位还是18位，只要是一个人即可。end
					// }
				}
			}
			
			if (where != null) {
				sql += where;
			}
			sql+= subselect+"))";
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {// 在存在基本档案的情况下，再查找是否存在人员管理档案，如果存在，得到公司，部门，岗位和是否在岗的信息
				String pk_psnbasdoc = rs.getString(1);
				String create_pk_corp = rs.getString(2);

				rs.close();
				stmt.close();
				if (!"0001".equalsIgnoreCase(create_pk_corp)) {
					String sql2 = "";
					if(returntype ==0){//返聘查询离退人员
						sql2 = "  select unitname,deptname,jobname,poststat,psnclassname ,bd_psndoc.pk_psndoc from bd_psndoc "
							+ " left outer join bd_corp on bd_psndoc.pk_corp = bd_corp.pk_corp"
							+ " left outer join bd_deptdoc on bd_psndoc.pk_deptdoc = bd_deptdoc.pk_deptdoc "
							+ " left outer join om_job on bd_psndoc.pk_om_job = om_job.pk_om_job "
							+ " left outer join bd_psncl on bd_psndoc.pk_psncl = bd_psncl.pk_psncl "
							+ " where pk_psnbasdoc =? and bd_psndoc.psnclscope = 3 and bd_psndoc.pk_corp ='"+create_pk_corp+"'";
					}else if(returntype ==1){//再聘查询解聘人员 
						sql2 = "  select unitname,deptname,jobname,poststat,psnclassname ,bd_psndoc.pk_psndoc from bd_psndoc "
							+ " left outer join bd_corp on bd_psndoc.pk_corp = bd_corp.pk_corp"
							+ " left outer join bd_deptdoc on bd_psndoc.pk_deptdoc = bd_deptdoc.pk_deptdoc "
							+ " left outer join om_job on bd_psndoc.pk_om_job = om_job.pk_om_job "
							+ " left outer join bd_psncl on bd_psndoc.pk_psncl = bd_psncl.pk_psncl "
							+ " where pk_psnbasdoc=? and bd_psndoc.psnclscope = 2 and bd_psndoc.pk_corp ='"+create_pk_corp+"'";
					}
					//add by lq  --------------------------
					sql2 = sql2 +" and  not exists (select pk_psndoc from hi_psndoc_deptchg  where lastflag='Y' and isreturn='Y' and pk_psnbasdoc='"+pk_psnbasdoc+"' and bd_psndoc.pk_psndoc=hi_psndoc_deptchg.pk_psndoc)";
					//add by lq --------------------------
					stmt = conn.prepareStatement(sql2);
					stmt.setString(1, pk_psnbasdoc);

					rs = stmt.executeQuery();
					
					if (rs.next()) {
						result = new GeneralVO();
						String unitname = rs.getString(1);
						returnMsg = (unitname == null ? NCLangResOnserver
								.getInstance().getStrByID("600704",
										"UPP600704-000243")/* @res "无公司" */
								+ ", \n" : NCLangResOnserver.getInstance()
								.getStrByID("600704", "UPP600704-000053")/*
																			 * @res
																			 * "公司:"
																			 */
								+ ":" + unitname + ", \n");
						String deptname = rs.getString(2);
						returnMsg += (deptname == null ? NCLangResOnserver
								.getInstance().getStrByID("600704",
										"UPP600704-000244")/* @res ",无部门" */
								+ ", \n" : NCLangResOnserver.getInstance()
								.getStrByID("600704", "upt600704-000139")/*
																			 * @res
																			 * ",部门:"
																			 */
								+ ":" + deptname + ", \n");
						String jobname = rs.getString(3);
						returnMsg += (jobname == null ? NCLangResOnserver
								.getInstance().getStrByID("600704",
										"UPP600704-000245")/* @res ",无岗位" */
								+ ", \n" : NCLangResOnserver.getInstance()
								.getStrByID("600704", "upt600704-000094")/*
																			 * @res
																			 * ",岗位:"
																			 */
								+ ":" + jobname + ", \n");
						String poststat = rs.getString(4);
						if ("Y".equalsIgnoreCase(poststat)) {
							returnMsg += NCLangResOnserver.getInstance()
									.getStrByID("600704", "UPP600704-000246")/*
																				 * @res
																				 * "在岗"
																				 */+ ", \n";
						} else {
							returnMsg += NCLangResOnserver.getInstance()
									.getStrByID("600704", "UPP600704-000247")/*
																				 * @res
																				 * "不在岗"
																				 */+ ", \n";
						}
						String psncl = rs.getString(5);
						returnMsg += (psncl == null ? NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000237")//"无类别"
								+ ", \n" : NCLangResOnserver.getInstance()
								.getStrByID("600704", "upt600704-000042")/*
								 * @res"人员类别为"*/
								+ ":" + psncl + "\n");
						result.setAttributeValue("returnMsg", returnMsg);
						result.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
						result.setAttributeValue("pk_corp", create_pk_corp);
						result.setAttributeValue("pk_psndoc", rs.getString(6));

					}
				}

			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "dataUniqueValidate",
				new Object[] { pk_corp, conitmUniqueFields, psnbasDocVO });
		/** ********************************************************** */
		return result;
	}

	/**
	 * 根据人员主键查询人员信息
	 * 创建日期：(2002-4-26 15:08:18)
	 * @return int
	 * @param psndocMains nc.vo.hi.hi_301.PsndocMainVO
	 */
	public GeneralVO queryPsnInfo(String pk_psndoc,String id,String psnname) throws java.sql.SQLException, java.io.IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		GeneralVO gvo = new GeneralVO();
		try {
			String sql = "select psncode,bd_psndoc.psnname,deptname,jobname,unitname from bd_psndoc";
				sql += " inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc and bd_psndoc.pk_corp = bd_psnbasdoc.pk_corp";
				sql += " inner join bd_corp on bd_corp.pk_corp = bd_psndoc.pk_corp";
				sql += " left outer join bd_deptdoc on bd_psndoc.pk_deptdoc = bd_deptdoc.pk_deptdoc";
				sql += " left outer join om_job on bd_psndoc.pk_om_job = om_job.pk_om_job";
			conn = getConnection();
			stmt = null;
			if(pk_psndoc == null || pk_psndoc.trim().length() == 0){
				sql += " where bd_psndoc.psnname = ? and bd_psnbasdoc.id = ?";	
				stmt = conn.prepareStatement(sql);
				stmt.setString(1,psnname);
				stmt.setString(2,id);
			}else{
				sql += " where bd_psndoc.pk_psndoc <> ? and bd_psndoc.psnname = ? and bd_psnbasdoc.id = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1,pk_psndoc);
				stmt.setString(2,psnname);
				stmt.setString(3,id);
			}
			result = stmt.executeQuery();
			String[] fieldNames = new String[]{"psncode","psnname","deptname","jobname","unitname"};
			if (result.next()) {
				for(int i=0;i<fieldNames.length;i++){
					Object value = result.getObject(i + 1);
					if (value != null) {
						value = ((String) value).trim();
						gvo.setFieldValue(fieldNames[i], value);
					}
				}
			}
			return gvo;
		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	/**
	 * 
	 * @param recievers
	 * @return
	 * @throws SQLException
	 */
	public GeneralVO[] getRecieverEmails(String recievers)throws SQLException {
		GeneralVO[] address  = null;

		Vector v = new Vector();
		
		String sql ="select u.cuserid,u.user_name,b.email from sm_user u left outer join sm_userandclerk c on u.cuserid = c.userid";
				sql+= " left outer join bd_psnbasdoc b on b.pk_psnbasdoc = c.pk_psndoc " ;
				sql+= " where u.cuserid in "+recievers;

		Connection con = null;
		PreparedStatement stmt = null;
		String gorop_code = nc.vo.pub.CommonConstant.GROUP_CODE;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				GeneralVO vo = new GeneralVO();
				String userid = rs.getString(1);
				vo.setAttributeValue("cuserid", userid);
				String username = rs.getString(2);
				vo.setAttributeValue("username", username);
				String email = rs.getString(3);
				if(!" ".equalsIgnoreCase(email)){
					vo.setAttributeValue("email", email);
				}
				v.addElement(vo);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		address = new GeneralVO[v.size()];
		if (v.size() > 0) {
			v.copyInto(address);
		}
		return address;
	}
	/**
	 * @得到信息集中的自定义项
	 * @param pk_corp
	 * @param pk_setdict
	 * @return
	 * @throws SQLException
	 */
	public String[] queryAllDefFlddict(String pk_corp,String pk_setdict) throws SQLException {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryAllDefFlddict",
				new Object[] { pk_corp, pk_setdict });
		/** ********************************************************** */

		String sql = "";
		sql = "select fldcode from hi_flddict where pk_setdict = ? and fldcode like '%def%' and (pk_corp ='0001' or  pk_corp = ?)";

		String[] fldcodes = null;
		Vector v = new Vector();
		Connection con = null;
		PreparedStatement stmt = null;
		String gorop_code = nc.vo.pub.CommonConstant.GROUP_CODE;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_setdict);
			stmt.setString(2, pk_corp);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String fldcode = new String();
				fldcode = rs.getString(1);
				v.addElement(fldcode);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		fldcodes = new String[v.size()];
		if (v.size() > 0) {
			v.copyInto(fldcodes);
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryAllDefFlddict",
				new Object[] { pk_corp, pk_setdict});
		/** ********************************************************** */

		return fldcodes;

	}
	/**
	 * 修改人员状态
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public void updatePsnState(String[] pk_psndocs,int state)throws java.sql.SQLException {
		
		String sql = " update bd_psnbasdoc set approveflag = "+state+ " where pk_psnbasdoc in (select pk_psnbasdoc from bd_psndoc where pk_psndoc = ? )";
		
		sql = HROperatorSQLHelper.getSQLWithOperator(sql);
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			for (int i = 0; i < pk_psndocs.length; i++) {
				stmt.setString(1, pk_psndocs[i]);
				stmt.executeUpdate();
			}
			stmt.executeBatch();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 返聘再聘人员的处理，先删除非在职记录，然后将非在职记录主键更新到新记录上
	 * @param pk_psndoc
	 * @param oldpk_psndoc
	 * @throws java.sql.SQLException
	 */
	public void updatePsnpkAnddel(String pk_psndoc,String oldpk_psndoc) throws java.sql.SQLException {	
		
		String sql = " delete from bd_psndoc where pk_psndoc = '"+oldpk_psndoc+"'";
		String sql2 = " update bd_psndoc set pk_psndoc ='"+oldpk_psndoc+"' where pk_psndoc ='"+pk_psndoc+"'";
		//处理入职申请单
		String sql3 = " update hi_docapply_b set pk_psndoc ='"+oldpk_psndoc+"' where pk_psndoc ='"+pk_psndoc+"'";
//		//处理任职记录
//		String sql4 = " update hi_psndoc_deptchg set pk_psndoc ='"+oldpk_psndoc+"' where pk_psndoc ='"+pk_psndoc+"'";

		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
			stmt = con.prepareStatement(sql2);
			stmt.executeUpdate();
			stmt.close();
			stmt = con.prepareStatement(sql3);
			stmt.executeUpdate();
//			stmt.close();
//			stmt = con.prepareStatement(sql4);
//			stmt.executeUpdate();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

	}
	/**
	 * 校验申请单保存时的人员是否已经转入人员档案
	 * @param pk_psndocs
	 * @param pk_docapply_b
	 * @return
	 * @throws BusinessException
	 */
	public String checkIndoc(String pk_psndoc)throws java.sql.SQLException {
		
		String sql = " select psnname from bd_psndoc where pk_psndoc =? and indocflag ='Y' ";
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			ResultSet rs =stmt.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return null;

	}
	
	/**
	 * 查询返聘再聘人员的非在职记录的主键，更新到新的返聘再聘记录上
	 * @param pk_psnbasdoc
	 * @param pk_corp
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String getPkpsndoc(String pk_psnbasdoc,String pk_corp,String newpk_psndoc)throws java.sql.SQLException {
		//此时新的工作档案也已经indocflag = 'Y'，所以不能光通过indocflag = 'Y'来判断是老的工作记录
		String sql = " select pk_psndoc from bd_psndoc where psnclscope >0 and indocflag = 'Y' and  pk_psnbasdoc ='"+pk_psnbasdoc+"' and pk_corp ='"+pk_corp+"' and pk_psndoc<>'"+newpk_psndoc+"'";
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs =stmt.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return null;

	}
	/**
	 * 查询采集的离职人员的任职部门
	 * @param pk_psndoc
	 * @param pk_corp
	 * @return
	 * @throws java.sql.SQLException
	 */
	public GeneralVO getChginfo(String pk_psndoc,String pk_corp)throws java.sql.SQLException {
		GeneralVO vo = null;
		String sql = " select pk_deptdoc,pk_psncl,pk_postdoc,pk_om_duty from hi_psndoc_deptchg where jobtype =0 and recordnum =0 and lastflag ='Y'and pk_psndoc ='"+pk_psndoc+"' and pk_corp ='"+pk_corp+"'";
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs =stmt.executeQuery();
			if(rs.next()){
				vo = new GeneralVO();
				vo.setAttributeValue("pk_deptdoc", rs.getString(1));
				vo.setAttributeValue("pk_psncl", rs.getString(2));
				vo.setAttributeValue("pk_postdoc", rs.getString(3));
				vo.setAttributeValue("pk_om_duty", rs.getString(4));
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return vo;

	}
	
	/**
	 * 查询培训类别和培训方式名称
	 * @param tratypepkssql
	 * @param type 0为类别，1为方式
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String[] getNames(String trapkssql,int type)throws java.sql.SQLException {
		String[] names = null;
		String sql = "";
		if(type == 0){
			sql = " select item_name from trm_item where pk_trm_item in "+trapkssql;
		}else{
			sql = " select docname from bd_defdoc where pk_defdoc in "+trapkssql;
		}
		Vector v = new Vector();
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs =stmt.executeQuery();
			while(rs.next()){
				String name = rs.getString(1);
				v.addElement(name);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		if(v.size()>0){
			names = new String[v.size()];
			v.copyInto(names);
		}
		return names;

	}
	
	/**
	 * 
	 * @param pk_sub
	 * @param names
	 * @param type
	 * @throws java.sql.SQLException
	 */
	public void updateTraining(String pk_sub,String names ,int type)throws java.sql.SQLException {

		String sql = "";
		if(type == 0){
			sql = " update hi_psndoc_training set trm_class_names ='"+names+"' where pk_psndoc_sub ='"+pk_sub+"'";
		}else{
			sql = " update hi_psndoc_training set tra_mode_name ='"+names+"' where pk_psndoc_sub ='"+pk_sub+"'";
		}
		Vector v = new Vector();
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
			
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		

	}
	/**
	 * 校验申请单保存时的人员信息
	 * @param pk_psndocs
	 * @param pk_docapply_b
	 * @return
	 * @throws BusinessException
	 */
	public String checkApplyPsn(String pk_psndoc,String pk_docapply_b)throws java.sql.SQLException {
		

		String sql = " select psnname from hi_docapply_b b inner join hi_docapply_h h on b.pk_docapply_h = h.pk_docapply_h "
					+"  inner join bd_psndoc p on b.pk_psndoc= p.pk_psndoc ";
		
		if( pk_docapply_b != null && pk_docapply_b.trim().length()>0 )
		{
			sql += " where ( (h.billstate in(2,3,8) and b.bapprove ='Y' and b.pk_psndoc ='"+pk_psndoc+"'and b.pk_docapply_b<>'"+pk_docapply_b+"') or ( h.pk_docapply_h = b.pk_docapply_h and h.billstate in ( 3,8 ) and b.bapprove = 'N' and b.pk_psndoc ='"+pk_psndoc+"'and b.pk_docapply_b<>'"+pk_docapply_b+"'))";
		}else{
			sql += " where ( (h.billstate in(2,3,8) and b.bapprove ='Y' and b.pk_psndoc ='"+pk_psndoc+"') or ( h.pk_docapply_h = b.pk_docapply_h and h.billstate in ( 3,8 ) and b.bapprove = 'N' and b.pk_psndoc ='"+pk_psndoc+"'))";
		}
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs =stmt.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return null;

	}
	public void afterInsertChild(String tablecode, String[] pk_psndoc_subs) throws SQLException {
		if(tablecode==null||pk_psndoc_subs==null)
			return;
		Connection con = null;
		PreparedStatement stmt = null;
		String sql0 = "select pk_psnbasdoc,pk_corp from "+tablecode+" where pk_psndoc_sub = ?";
		String sql1 = "update " +tablecode+ " set recordnum=recordnum+1,lastflag='N' where pk_psnbasdoc = ? and pk_corp = ? and pk_psndoc_sub<>?";
		String sql2 = "update " +tablecode+ " set recordnum=0,lastflag='Y' where pk_psndoc_sub = ?";
		try {
			con = getConnection();
			for(String pk_psndoc_sub : pk_psndoc_subs){
				stmt = con.prepareStatement(sql0);
				stmt.setString(1, pk_psndoc_sub);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()){
					String pk_psnbasdoc = rs.getString(1);
					String pk_corp = rs.getString(2);
					stmt.close();
					stmt = con.prepareStatement(sql1);
					stmt.setString(1,pk_psnbasdoc);
					stmt.setString(2, pk_corp);
					stmt.setString(3, pk_psndoc_sub);
					stmt.executeUpdate();
					stmt.close();
					stmt = con.prepareStatement(sql2);
					stmt.setString(1,pk_psndoc_sub);
					stmt.executeUpdate();
					stmt.close();
				}
			}
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
	}


	public void deleteChildSet(String tablecode, String[] pk_psndoc_subs) throws SQLException {
		if(tablecode==null||pk_psndoc_subs==null)
			return;
		Connection con = null;
		PreparedStatement stmt = null;
		String sql = "delete from "+tablecode+" where pk_psndoc_sub = ?";
		String sql0 = "select pk_psnbasdoc,pk_corp,recordnum from "+tablecode+" where pk_psndoc_sub = ?";
		String sql1 = "update " +tablecode+ " set recordnum=recordnum-1,lastflag='N' where pk_psnbasdoc = ? and pk_corp = ? and recordnum>?";
		String sql2 = "update " +tablecode+ " set lastflag='Y' where pk_psnbasdoc = ? and pk_corp = ? and recordnum=0 ";
		try {
			con = getConnection();
			for(String pk_psndoc_sub : pk_psndoc_subs){
				stmt = con.prepareStatement(sql0); //查询基本表主键，业务公司，序列号
				stmt.setString(1, pk_psndoc_sub);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()){
					String pk_psnbasdoc = rs.getString(1);
					String pk_corp = rs.getString(2);
					int recordnum = rs.getInt(3);
					stmt.close();
					stmt = con.prepareStatement(sql); //删除记录
					stmt.setString(1, pk_psndoc_sub);
					stmt.executeUpdate();
					stmt.close();
					stmt = con.prepareStatement(sql1); //更新大于recordnum>当前删除行的序号减一
					stmt.setString(1,pk_psnbasdoc);
					stmt.setString(2, pk_corp);
					stmt.setInt(3, recordnum);
					stmt.executeUpdate();
					stmt.close();
					stmt = con.prepareStatement(sql2); //更新recordnum＝0的记录的最新标志为Y
					stmt.setString(1,pk_psnbasdoc);
					stmt.setString(2, pk_corp);
					stmt.executeUpdate();
					stmt.close();
				}
			}
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 是否存在自助用户
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Hashtable isNeedSelfUser(GeneralVO[] vos)
			throws java.sql.SQLException {
		// 公司主键
		String pk_corp = null;
		if (vos != null && vos.length > 0) {
			pk_corp = (String) vos[0].getAttributeValue("pk_corp");
		} else
			return null;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Hashtable isNeedSelfUser = new Hashtable();
		// 人员基本主键
		String pk_psnbasdoc = null;
		// 人员管理主键
		String pk_psndoc = null ;
		try {
			con = getConnection();
			// 生成临时表
			TempTable tt = new TempTable();
			// 创建临时表
			String temptable = tt.createTempTable(con, "tquerySelfUsertable",
					"pk_psnbasdoc char(20),pk_psndoc char(20),ts char(19)",
					null);
			// 插入记录到临时表
			String tempsql = "insert into " + temptable
					+ " (pk_psnbasdoc,pk_psndoc) values(?,?) ";
			StringBuffer bsql = new StringBuffer("select ");
			bsql
					.append(temptable)
					.append(".pk_psndoc from ")
					.append(temptable)
					.append(
							" where pk_psnbasdoc not in (select pk_psndoc from sm_userandclerk where pk_corp = ?) ");
			// 获取插入值
			stmt = con.prepareStatement(tempsql);
			for (int i = 0; i < vos.length; i++) {
				pk_psnbasdoc = (String) vos[i]
						.getAttributeValue("pk_psnbasdoc");
				pk_psndoc = (String) vos[i].getAttributeValue("pk_psndoc");
				stmt.setObject(1, pk_psnbasdoc);
				stmt.setObject(2, pk_psndoc);
				stmt.executeUpdate();
			}
			executeBatch(stmt);
			stmt = con.prepareStatement(bsql.toString());
			stmt.setObject(1, pk_corp);
			rs = stmt.executeQuery(); 
			while (rs.next()) {
				pk_psndoc = rs.getString(1);
				// 是否部门负责人
				if (isPrincipal(pk_psndoc))
					isNeedSelfUser.put(pk_psndoc, true);
				else
					isNeedSelfUser.put(pk_psndoc, false);
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		return isNeedSelfUser;
	}
	/**
	 * 是否是部门负责人
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean isPrincipal(String pk_psndoc)
			throws java.sql.SQLException {
		String sql = "select 1 from bd_deptdoc where pk_psndoc = ? or pk_psndoc2 = ? or pk_psndoc3 = ? ";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean isPrincipal = false;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_psndoc);
			stmt.setString(2, pk_psndoc);
			stmt.setString(3, pk_psndoc);
			rs = stmt.executeQuery();
			if (rs.next()) {
				isPrincipal = true;
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return isPrincipal;
	}
	/**
	 * 根据人员主键查询人员信息 创建日期：(2002-4-26 15:08:18)
	 * 
	 * @return int
	 * @param psndocMains
	 *            nc.vo.hi.hi_301.PsndocMainVO
	 */
	public GeneralVO[] queryPsnInfo(String pk, String sqlDate, String table,
			String[] fieldNames, GeneralVO[] psnList,
			BusinessFuncParser_sql funcParser)
			throws java.sql.SQLException,
			java.io.IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		try {
			sqlDate += " from bd_psndoc inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc ";

			if ("bd_psndoc".equalsIgnoreCase(table)) {
				sqlDate += " where bd_psndoc.pk_psndoc='" + pk + "'";
			} else {
				sqlDate += " where bd_psndoc.pk_psnbasdoc='"
						+ pk
						+ "' or bd_psndoc.pk_psnbasdoc = (select pk_psnbasdoc from bd_psndoc where pk_psndoc='"
						+ pk + "')";
			}
			conn = getConnection();
			stmt = null;
			stmt = conn.prepareStatement(sqlDate);
			result = stmt.executeQuery();
			if (result.next()) {
				for (int i = 0; i < fieldNames.length; i++) {
					Object value = result.getObject(i + 2);
					if (value != null) {
						if (value instanceof String)
							value = ((String) value).trim();
						// 
						psnList[0].setFieldValue(fieldNames[i], value);
					}
				}
			}
			return psnList;
		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	/**
	 * 
	 * @param pk_om_dumorg
	 * @return
	 * @throws java.sql.SQLException
	 * @throws java.io.IOException
	 */
	public GeneralVO queryDetailForDumorg(String pk_om_dumorg)
			throws java.sql.SQLException, java.io.IOException {
		String sql = "select builddate from om_dumorg where pk_om_dumorg = ? ";
		GeneralVO vo = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pk_om_dumorg);
			rs = stmt.executeQuery();
			//
			if (rs.next()) {
				vo = new GeneralVO();
				vo.setAttributeValue("builddate", rs.getString(1));
			}
		} finally {

			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return vo;
	}
	public String queryFieldDict(String pk_corp) throws java.sql.SQLException{
		String sql_setdict = "SELECT a.fldcode from hi_flddict a,HI_SETDICT b"
							+" WHERE a.PK_SETDICT = b.PK_SETDICT"
							+" and b.SETCODE = 'bd_psndoc' "
							+" AND (a.pk_corp ='0001' OR a.pk_corp = ?)"
							+" and (((a.fldcode like 'def%' or a.fldcode like 'groupdef%') and a.PK_FLDREFTYPE is not null )"
							+" or((a.fldcode not like 'def%' and a.fldcode not like 'groupdef%')))"
							+" and a.DATATYPE <>10"
							+" order by a.SHOWORDER";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String resultstr = "";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql_setdict);
			stmt.setString(1, pk_corp);
			rs = stmt.executeQuery();
			while (rs.next()) {
				resultstr+="bd_psndoc."+rs.getString(1)+",";
			}
		} finally {

			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return resultstr;
	}
	
	public GeneralVO[] queryPerson(String pk_psndocs[],String fields_select) throws java.sql.SQLException, IOException{
		String pk_psndoc_str = "";
		for(String pk_psndoc:pk_psndocs){
			pk_psndoc_str+=",'"+pk_psndoc+"'";
		}
		pk_psndoc_str = pk_psndoc_str.substring(1);
		String sql = "select "+fields_select+" from bd_psndoc";
				sql += " inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc and bd_psndoc.pk_corp = bd_psnbasdoc.pk_corp";
				sql += " inner join bd_corp on bd_corp.pk_corp = bd_psndoc.pk_corp";
				sql += " left outer join bd_deptdoc on bd_psndoc.pk_deptdoc = bd_deptdoc.pk_deptdoc";
				sql += " left outer join om_job on bd_psndoc.pk_om_job = om_job.pk_om_job";
				sql += " left outer join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl";
				sql += " left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty";
				sql += " where bd_psndoc.pk_psndoc in ("+pk_psndoc_str+")";
				return (GeneralVO[]) querySql(sql).toArray(new GeneralVO[0]);
	}
	public void checkIfExistsUnAuditBillofPSN(String[] pk_psndocs,String setcode)
	throws BusinessException {
		String pk_psndoc_str = "";
		for(String pk_psndoc:pk_psndocs){
			pk_psndoc_str+=",'"+pk_psndoc+"'";
		}
		pk_psndoc_str = pk_psndoc_str.substring(1);
		String sql ="";
		if(setcode.equals("maintable")){
			sql ="select bd_psndoc.psnname from hrss_setalter,bd_psndoc where bd_psndoc.pk_psndoc = hrss_setalter.pk_psndoc"
				+" and  bd_psndoc.pk_psndoc in ("+pk_psndoc_str+") and (hrss_setalter.setcode ='bd_psndoc' or hrss_setalter.setcode ='bd_psnbasdoc') and hrss_setalter.data_status=1 ";

		}else{
			sql ="select bd_psndoc.psnname from hrss_setalter,bd_psndoc where bd_psndoc.pk_psndoc = hrss_setalter.pk_psndoc"
				+" and  bd_psndoc.pk_psndoc in ("+pk_psndoc_str+") and hrss_setalter.setcode ='"+setcode+"' and hrss_setalter.data_status=1 ";
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String resultstr = "";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				resultstr+=","+rs.getString(1);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try{
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if("".equals(resultstr)) return ;
		
		resultstr = resultstr.substring(1);
		
		/*有未审批的人员信息变更，不能编辑！*/
		throw new BusinessException(resultstr+":"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("6007","UPT6007-000236"));
	}

	/*********************************************************************************************************
	 * 查询引用人员
	 * @param data
	 * @param tableCode
	 * @return GeneralVO[]
	 * @throws java.io.IOException
	 * @throws java.sql.SQLException
	 ********************************************************************************************************/
	public GeneralVO[] queryRefPsn(GeneralVO[] data) throws java.io.IOException, java.sql.SQLException {
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryRefPsn",
				new Object[] { data});
		/** ********************************************************** */
		Connection con = null;
		PreparedStatement stmt = null;
		if (data == null || data.length == 0) {
			return null;
		}
		ArrayList<GeneralVO> list = new ArrayList<GeneralVO>();
		try {			
			String sql = "select pk_deptdoc,pk_corp,pk_psnbasdoc,pk_psndoc " +
					" from bd_psndoc where isreferenced = 'Y' and pk_psndoc " +
					"in (select pk_psndoc from hi_psndoc_ref where pk_psnbasdoc = ? ) ";
			con = getConnection();
			for (int i = 0; i < data.length; i++) {					
					stmt = con.prepareStatement(sql);
					stmt.setString(1, (String) data[i].getAttributeValue("pk_psnbasdoc"));
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						GeneralVO  psndoc = new GeneralVO();
						psndoc.setAttributeValue("pk_dept_new", rs.getString(1));
						psndoc.setAttributeValue("pk_corp_new", rs.getString(2));
						psndoc.setAttributeValue("pk_psnbasdoc", rs.getString(3));
						psndoc.setAttributeValue("pk_psndoc", rs.getString(4));
						list.add(psndoc);
					}
					stmt.close();
			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryRefPsn",
				new Object[] { data });
		/** ********************************************************** */
		return list.toArray(new GeneralVO[0]);
	}
	
	/**
	 * 获取当前辅助信息集字段的关联信息,放在映射表中。 创建日期：(2004-5-30 16:42:44)
	 * @author lvgd1
	 */
	public Hashtable getTempRelationMap() {
		// 辅助信息表的关联字段逆向映射表
		Hashtable relationMap = new Hashtable();
		// 查询所有关联字段信息
		GeneralVO[] relatedFields;
		try {
			relatedFields = HIDelegator.getPsnInf().queryAllRelatedTableField(
					PubEnv.getPk_corp());
			// 放入映射表中
			for (int i = 0; i < relatedFields.length; i++) {
				String table = (String) relatedFields[i]
						.getAttributeValue("setcode");
				String field = (String) relatedFields[i]
						.getAttributeValue("fldcode");
				String accfield = (String) relatedFields[i]
						.getAttributeValue("accfldcode");
				relationMap.put(table + "." + field, accfield);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return relationMap;
	}
	
	public String queryPkPsnDoc(String pk_psnbasdoc,String pk_corp) throws SQLException{
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryPkPsnDoc",
				new Object[] { pk_psnbasdoc,pk_corp});
		/** ********************************************************** */
		String pk_psndoc=null;
		Connection con = null;
		PreparedStatement stmt = null;
		String sql = "select pk_psndoc from bd_psndoc where pk_psnbasdoc='"+pk_psnbasdoc+"' ";
		if(pk_corp !=null){
			sql += " and pk_corp='"+pk_corp+"'";
		}
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				pk_psndoc=rs.getString(1);
			}
			if(pk_psndoc == null || pk_psndoc.length() <= 0){
				pk_psndoc = queryPkPsnDoc(pk_psnbasdoc,null);
			}
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryPkPsnDoc",
				new Object[] { pk_psnbasdoc,pk_corp });
		/** ********************************************************** */
		return pk_psndoc;
	}
	
	public String queryPkPsnBasDoc(String pk_psndoc) throws SQLException{
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryPkPsnBasDoc",
				new Object[] { pk_psndoc});
		/** ********************************************************** */
		String pk_psnbasdoc=null;
		Connection con = null;
		PreparedStatement stmt = null;
		String sql = "select pk_psnbasdoc from bd_psndoc where pk_psndoc='"+pk_psndoc+"'";
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				pk_psnbasdoc=rs.getString(1);
			}
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.hi.hi_301.PsnInfDMO", "queryPkPsnBasDoc",
				new Object[] { pk_psnbasdoc });
		/** ********************************************************** */
		return pk_psnbasdoc;
	}
	
	public String getPkpsnbasdoc(String pk_psndoc)
    throws SQLException{
		    
		Connection conn = null;
	    
	    PreparedStatement stmt = null;
	    
		try{			
			String sql="";
		    
		    sql = "select pk_psnbasdoc from bd_psndoc where pk_psndoc='"+pk_psndoc+"'";
		    
		    String str = "";
		    
		    conn = getConnection();
		    
		    stmt = conn.prepareStatement(sql);
		    
		    ResultSet rs = stmt.executeQuery();
		    
		    if(rs.next()){
		    	
		    	str = rs.getString(1);
		    }		       
		    return str;
		}finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		    
}
}
