package nc.itf.hi;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import nc.vo.bd.b04.DeptdocVO;
import nc.vo.hi.hi_301.CtrlDeptVO;
import nc.vo.hi.hi_301.GeneralVO;
import nc.vo.hi.hi_306.DocApplyHVO;
import nc.vo.hr.formulaset.BusinessFuncParser_sql;
import nc.vo.hrp.hrhi02.PSNDOCEXPORTVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.msg.UserNameObject;
import nc.vo.pub.query.ConditionVO;
import nc.vo.sm.UserVO;
import nc.vo.sm.user.UserAndClerkVO;
import nc.vo.uap.rbac.RoleVO;



public interface IPsnInf {
	
	
	 /**
	 * 根据工作主键查询人员基本主键
	 * @param selectVOs
	 * @throws BusinessException
	 */
    public abstract String getPkpsnbasdoc(String pk_psndoc) throws BusinessException;
	
    /**
	 * 同步人员信息
	 * @param selectVOs
	 * @throws BusinessException
	 */
	public void synUpdatePsnInfo(String pk_psnbasdoc, String pk_psndoc)throws BusinessException;

	/**
	 * 批量增加用户角色
	 * @param selectVOs
	 * @throws BusinessException
	 */
	public abstract void batchInsertUserRoles(HashMap<String,RoleVO[]> userRoleMap,UserAndClerkVO[] userAndClerks) throws BusinessException;
	
	/**
	 * 批量增加用户
	 * @param selectVOs
	 * @throws BusinessException
	 */
	public abstract String[] batchInsertUsers(UserVO[] userVOs) throws BusinessException;

	/**
	 * 更新两个主表的信息。 
	 * 创建日期：(2004-5-19 16:36:40)
	 * 
	 * @param psndoc
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @param accpsndocVO
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @throws nc.vo.pub.BusinessException
	 *                异常说明。
	 */
	public abstract void updateMain(GeneralVO psndoc, GeneralVO accpsndocVO,
			GeneralVO mainaddpsndocVO) throws BusinessException;
	
	/**
	 * 修改非业务子集的recordnum
	 * v50
	 * @param pk_psnbasdoc
	 * @param Subsetdatas
	 */
	public abstract void updateSubsetRecordnum(String pk_psnbasdoc, String tablecode,
			CircularlyAccessibleValueObject[] Subsetdatas)
			throws BusinessException ;
	/**
	 * 保存引用人员接收消息用户
	 * @param pk_corp
	 * @param userids
	 * @param sendtype
	 * @return String 插入hr_message记录的主键
	 * @throws BusinessException
	 */
	public abstract String insertRecievers(String pk_corp, String userids,int sendtype)
			throws BusinessException;
			
			
	/**
	 * V35 add 扩展了updateAccpsndoc(GeneralVO accpsndocVO)方法,
	 * 专门用来处理信息子集与关联表（bd_psndoc bd_psnbasdoc）的信息
	 * @param hmRelVo 
	 * key:存放的是表名
	 * value:存放的是
	 * GeneralVO对象：表对应的数据字段值
	 * @throws nc.vo.pub.BusinessException
	 */
	public void updateRelTable(HashMap hmRelVo)
			throws BusinessException; ;

	/**
	 * 应聘信息中的档案所在地和档案记录子集转入人员信息的档案记录子集
	 * 
	 * @author lvgd1
	 * @exception BusinessException
	 *                异常说明。
	 */
	public void turnBDIntoPsn(String pk_psnbasdoc, String pk_psndoc)
			throws BusinessException;
	
	
	/**
	 * 更新辅助人员信息。 
	 * 创建日期：(2004-5-19 14:53:19)
	 * 
	 * @param accpsndocVO
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void updateAccpsndoc(GeneralVO accpsndocVO,String pk_psndoc)
			throws BusinessException;
	
	/**
	 * 批量更新人员信息。
	 */
	public void batchUpdateAccpsndoc(GeneralVO[] accpsndocVOs, String[] pk_psndocs) throws BusinessException ;
	
	/**
	 * 修改子表记录。 创建日期：(2004-8-6 9:28:24)
	 * @param tableCode
	 * @param pk_psndoc
	 * @param vo
	 * @throws BusinessException
	 */
	public void updateChild(String tableCode, String pk_psndoc, GeneralVO vo)
			throws BusinessException;
	/**
	 * 更新主表的删除记录标志
	 * @param pk_psndoc
	 * 				java.lang.String
	 * @param ifdelete
	 * 				boolean
	 * @throws nc.vo.pub.BusinessException
	 */
	public void updateDeleteFlag(String pk_psndoc,boolean ifdelete)
			throws BusinessException;
	
	
	/**
	 * 更新关联的辅助人员信息。
	 * 解决V30中问题编码为200507111204423395 和 200507210913444548中的同步问题
	 * @param fields
	 * @param map
	 * @param tableCode
	 * @param accpsndocVO
	 * @return GeneralVO
	 * @throws BusinessException
	 */
	public GeneralVO updateAccRel(String[] fields, Hashtable map,
			String tableCode, GeneralVO accpsndocVO)
			throws BusinessException;
	
	/**
	 * 查询选中人员的当前子集数据
	 * 返回的 key = pk_psnbasdoc value = GeneralVO[] 的HashMap
	 * @param vPkPsnbasdocs
	 * 			java.util.Vector
	 * @param tablecode
	 * 			java.lang.String
	 * @return HashMap
	 * @throws nc.vo.pub.BusinessException
	 */
	public HashMap querySubInfo(Vector vPkPsnbasdocs,String tablecode)
			throws BusinessException;
	
	/**
	 * 查询用户在指定公司有权限的部门。
	 * 创建日期：(2004-10-19 9:08:23)
	 * 
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 * @param userID
	 *            java.lang.String
	 * @param pk_corp
	 *            java.lang.String
	 * @param isRelate
	 *            boolean
	 * @exception nc.vo.pub.BusinessException
	 *                异常说明。
	 */
	public CtrlDeptVO queryRelatedDepts(String userID, String pk_corp,
			boolean isRelate) 
			throws BusinessException;
	/**
	 *  
	 * @param strParam
	 * @param type
	 * @param power
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @throws BusinessException
	 */
	public nc.vo.hi.hi_301.GeneralVO[] queryRefEmployees(String strParam,Integer type,String power) 
			throws BusinessException;
	
	/**
	 * 采集节点查询人员记录数(过期)
	 * 
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param userid
	 * @param normalwheresql
	 * @return int
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition(boolean indocflag,
			String[] pk_corps, ConditionVO[] conditions, String loginCorp,
			String userid, String normalwheresql) throws BusinessException;
	
	
	/**
	 * 获得接受者
	 * @param pk_corp
	 * @return UserNameObject[]
	 * @throws BusinessException
	 */
	public UserNameObject[] getRecievers(String pk_corp,int usertype) throws BusinessException ;
	
	/**
	 * 维护节点查询人员信息记录总数。
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param scope
	 * @param loginCorp
	 * @param userid
	 * @param jobtype
	 * @param normalwheresql
	 * @return int
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition(boolean indocflag,
			String[] pk_corps, ConditionVO[] conditions, int scope,
			String loginCorp, String userid, int jobtype, String normalwheresql)
			throws BusinessException;
	/**
	 * 同岗位人数查询器等（任职等）
	 * @param table
	 * @param pk
	 * @param pk_corp
	 * @return int
	 * @throws BusinessException
	 */
	public int queryPsnCountByPk(String table,String pk,String pk_corp)
			throws BusinessException;
	/**
	 * 查询人员信息
	 * @param pk_psndoc
	 * @param pk_psnbasdoc
	 * @param table
	 * @param isTraceTable
	 * @param islookhistory	
	 * @param loginPkcorp
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPersonInfo(String pk_corp, String pk_psnbasdoc,String table,boolean isTraceTable,
										boolean islookhistory,String pk_psndoc,String loginPkcorp) throws BusinessException;
	/**
	 * 查询指定人员PK和子集编码的记录
	 * @param pk
	 * @param table
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPersonInfo(String pk, String table)
			throws BusinessException; 

	/**
	 * 根据人员档案主键，查询人员工作档案的信息
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPersonMainInfo(String[] pk_psndocs,String pk_corp) throws BusinessException; 
	/**
	 * 根据主键和id、psnname查询人员信息
	 * @param pk_psndoc
	 * @param id
	 * @param psnname
	 * @return GeneralVO
	 * @throws BusinessException
	 */
	public GeneralVO queryPsnInfo(String pk_psndoc, String id, String psnname)
			throws BusinessException;
	
	/**
	 * 查询指定部门和公司PK的部门树
	 * @param pk_deptdoc
	 * @param pk_corp
	 * @return CtrlDeptVO
	 * @throws BusinessException
	 */
	public CtrlDeptVO queryMgrDepts(String pk_deptdoc, String pk_corp,boolean includehrcanceled)
			throws BusinessException;
	
	/**
	 * 查询项目设置列表
	 * @param pk_corp
	 * @param funcode
	 * @param queryScope
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryListItem(String pk_corp, String funcode,
			String queryScope) 
			throws BusinessException;
	
	/**
	 * 查询能够控制的部门
	 * @param userID
	 * @param corpVO
	 * @param useDeptPower
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 * @throws BusinessException
	 */
	public nc.vo.hi.hi_301.CtrlDeptVO queryCorpCtrlDepts(String userID,
			CtrlDeptVO corpVO, boolean useDeptPower,boolean includeHrCanceld)
			throws BusinessException;
	
	/**
	 * V31SP1 照片导出功能
	 * @param pk_corp
	 * @param psnPK
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByPsnPK(String pk_corp, String psnPK)
			throws BusinessException;  
	
	/**
	 * 经理自助查询人员信息
	 * @param indocflag
	 * @param pk_corp
	 * @param conditions
	 * @param scope
	 * @param powerSql
	 * @param jobtype
	 * @param listfield
	 * @param normalwheresql
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByConditionDepts(boolean indocflag, String pk_corp,
			ConditionVO[] conditions, int scope, String powerSql, int jobtype,
			String listfield,String normalwheresql) 
			throws BusinessException; 
	
	/**
	 * 采集节点保存完人员后查询人员信息
	 * @param pk_corp
	 * @param conditions
	 * @param powerSql
	 * @param listfield
	 * @param normalwheresql
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition(String pk_corp,
			ConditionVO[] conditions, String powerSql, String listfield,String normalwheresql)
			throws BusinessException; 
	
	/**
	 * 采集节点查询人员信息(过期)
	 * @deprecated
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param loginCorp
	 * @param userid
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition(boolean indocflag, String[] pk_corps,
			ConditionVO[] conditions, String loginCorp, String userid,
			String listfield, String normalwheresql, int recordcount)
			throws BusinessException;

	/**
	 * 维护节点查询人员信息
	 * @param indocflag
	 * @param pk_corp
	 * @param conditions
	 * @param scope
	 * @param loginCorp
	 * @param userid
	 * @param jobtype
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition(boolean indocflag, String[] pk_corp,
			ConditionVO[] conditions, int scope, String loginCorp,
			String userid, int jobtype, String listfield,
			String normalwheresql, int recordcount) throws BusinessException;
	
	/**
	 * 维护节点查询人员信息
	 * @param indocflag
	 * @param pk_corp
	 * @param conditions
	 * @param scope
	 * @param loginCorp
	 * @param userid
	 * @param jobtype
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @param isCheckDeptPower
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition(boolean indocflag, String[] pk_corp,
			ConditionVO[] conditions, int scope, String loginCorp,
			String userid, int jobtype, String listfield,
			String normalwheresql, int recordcount, boolean isCheckDeptPower)
			throws BusinessException;
	
	/**
	 * 从hi_flddict何hi_setdic表中查询bd_accpsndoc中字段acc_fldcode的关联字段
	 * @param corppk
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryAllRelatedTableField(String corppk)
			throws BusinessException; 
	
	/**
	 * 把引用的人员转入到管理档案中
	 * @param psnvos
	 * @return int
	 * @throws BusinessException
	 */
	public int addRefEmployees(nc.vo.hi.hi_301.GeneralVO[] psnvos) 
			throws BusinessException; 
	
	/**
	 * 批量修改
	 * @param pk_psndocs
	 * @param tableCode
	 * @param fieldCode
	 * @param value
	 * @throws BusinessException
	 */
	public void batchUpdate(String[] pk_psndocs, String tableCode,
			String fieldCode, Object value,String modulcode,String pk_corp) 
			throws BusinessException; 
	/**
	 * 身份证不为空检查是否已经加入黑名单（按身份证和姓名）
	 * @param accpsndocVO
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean checkBadlist(GeneralVO accpsndocVO)
			throws BusinessException; 

	/**
	 * 检查是否存在记录
	 * @param sql
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean isRecordExist(String sql) throws BusinessException;
	
	/**
	 * 检查是否存在记录
	 * @param sql
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean[] isRecordExists(String[] sqls) throws BusinessException;
	/**
	 * 判断此员工在本公司是否有用户。
	 * @param pk_psndoc
	 * @param pkCorp
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean checkUserClerk(String pk_psndoc, String pkCorp) 
			throws BusinessException;
	/**
	 * 得到指定公司PK及人员信息的校验信息
	 * @param pk_corp
	 * @param conitmUniqueFields
	 * @param psnbasDocVO
	 * @return String
	 * @throws BusinessException
	 */
	public String dataUniqueValidate(String pk_corp,nc.vo.bd.psndoc.PsndocConsItmVO[] conitmUniqueFields,CircularlyAccessibleValueObject psnbasDocVO)
			throws BusinessException; 
	/**
	 * 删除指定表编码和人员主键及通用人员信息GeneralVO的记录
	 * @param tableCode
	 * @param pk_psndoc
	 * @param vo
	 * @throws BusinessException
	 */
	public void deleteChild(String tableCode, String pk_psndoc, GeneralVO vo)
			throws BusinessException; 
	
	/**
	 * 得到被删除人员基本信息PK对应的人员个数
	 * @param pk_psnbasdoc
	 * @return int
	 * @throws BusinessException
	 */
	public int deletePsnValidate(String pk_psnbasdoc)
			throws BusinessException;//throws RemoteException
	
	/**
	 * 
	 * @param tableCode
	 * @param pk_psndoc
	 * @throws BusinessException
	 */
	public void deletePsnData(String tableCode, String pk_psndoc)
			throws BusinessException;
	
	/**
	 * 删除维护节点人员记录
	 * @param psnvo
	 * @param pk_corp
	 * @throws BusinessException
	 */
	public void deletePersonDoc(GeneralVO psnvo, String pk_corp)
			throws BusinessException;

	/**
	 * 删除采集节点人员。 
	 * 创建日期：(2004-12-3 16:23:41) 
	 * @param psnvo GeneralVO
	 * @param pk_corp  java.lang.String
	 * @exception BusinessException  异常说明。
	 */
	public void deletePersonnotinDoc(GeneralVO psnvo, String pk_corp)
			throws BusinessException;
	
	/**
	 * 删除返聘人员。 
	 * 创建日期：(2004-12-3 16:23:41) 
	 * @param psnvo GeneralVO
	 * @param pk_corp  java.lang.String
	 * @param hiretype  int 0 返聘，1 再聘
	 * @exception BusinessException  异常说明。
	 */
	public void deletePersonRehire(GeneralVO psnvo, String pk_corp,int hiretype)
			throws BusinessException;
	
	/**
	 * 查询部门pk_deptdoc的所有子部门和其自身
	 * @param pk_deptdoc
	 * @return DeptdocVO[]
	 * @throws BusinessException
	 */
	public DeptdocVO[] deptChildren(String pk_deptdoc,boolean includehrcanceled)
			throws BusinessException;
	/**
	 * 过滤单据模板，除去不可见页签，设置追踪信息集
	 * @param corp
	 * @param newTempletVO
	 * @return Object[]
	 * @throws BusinessException
	 */
	public Object[] filter(String corp, BillTempletVO newTempletVO)
			throws BusinessException; 
	
	/**
	 * 按自定义档案主键过滤VO
	 * @param pk_defdoclist
	 * @return nc.vo.bd.def.DefdoclistVO
	 * @throws BusinessException
	 */
	public nc.vo.bd.def.DefdoclistVO findDefdoclistByPk(String pk_defdoclist)
			throws BusinessException; 
	
	/**
	 * 得到指定公司的所有用户数组
	 * @param corpPK
	 * @return String[]
	 * @throws BusinessException
	 */
	public String[] getAllUserCode(String corpPK)
			throws BusinessException; 
	
	
	
	/**
	 * 查询指定单据模板的主键
	 * @param pk_corp
	 * @param userid
	 * @param nodecode
	 * @return String TempletID
	 * @throws BusinessException
	 */
	public String getBillTempletID(String pk_corp, String userid,
			String nodecode)
			throws BusinessException;//throws ...
	/**
	 * 得到在指定公司兼职的人员所在的公司
	 * @param sql
	 * @return String[]
	 * @throws BusinessException
	 */
	public String[] queryDeptPowerBySql(String sql) 
			throws BusinessException; 
	/**
	 * 根据条件,查询打印数据
	 * @param conditions
	 * @param hmPower
	 * @param corps
	 * @param indocflag
	 * @param scope
	 * @param jobtype
	 * @param normalwheresql
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] getExtendedData(ConditionVO[] conditions,
			HashMap hmPower, String[] corps, boolean indocflag, int scope,int jobtype,String listfield,
			String normalwheresql) throws BusinessException;
//	/**
//	 * 得到参数
//	 * @param cls
//	 * @param unitcode
//	 * @param initcode
//	 * @return
//	 * @throws BusinessException
//	 */
//	public Object getParam(Class cls,String unitcode,String initcode) throws BusinessException;
	/**
	 * 查询指定公司PK和节点功能的有权限的用户数组
	 * @param pk_corp
	 * @param funcode
	 * @return nc.vo.pub.msg.UserNameObject[]
	 * @throws BusinessException
	 */
	public nc.vo.pub.msg.UserNameObject[] getPowerUserid(String pk_corp,String funcode)
			throws BusinessException; 
	
	/**
	 * 得到用户对象
	 * @param userids
	 * @return nc.vo.pub.msg.UserNameObject[]
	 * @throws BusinessException
	 */
	public nc.vo.pub.msg.UserNameObject[] getUserObj(String[] userids)
			throws BusinessException; 
	
	/**
	 * 根据pk得到某个表的字段的值
	 * @param tableName
	 * @param pkField
	 * @param vField
	 * @param pk
	 * @return Object 值
	 * @throws BusinessException
	 */
	public Object getValue(String tableName, String pkField, String vField,
			Object pk) throws BusinessException; 
	
	/**
	 * 插入指定人员PK的某子集记录
	 * @param tableCode
	 * @param pk_psndoc
	 * @param vo
	 * @return String 插入字表后形成的主键pk
	 * @throws BusinessException
	 */
	public String insertChild(String tableCode, String pk_psndoc, GeneralVO vo)
			throws BusinessException;//
	/**
	 * 保存引用人员的工作信息 (hi_psndoc_ref)
	 * @param psndocVO
	 * @return String插入表的记录的pk
	 * @throws BusinessException
	 */
	public String insertHiRef(GeneralVO psndocVO,boolean isNeedAFirm) 
			throws BusinessException; 
 
	/**
	 * 保存多条引用人员的工作信息 (hi_psndoc_ref)
	 * @param psndocVOs
	 * @param isNeedAFirm
	 * @param billcodevo
	 * @return String
	 * @throws BusinessException
	 */
	public String insertHiRefs(GeneralVO[] psndocVOs,boolean isNeedAFirm,nc.vo.pub.billcodemanage.BillCodeObjValueVO billcodevo) throws BusinessException;
	/**
	 * 插入主信息集
	 * @param psndocVO
	 * @param accpsndocVO
	 * @param mainaddpsndocVO
	 * @param billcodevo
	 * @return String
	 * @throws BusinessException
	 */
	public String insertMain(GeneralVO psndocVO, GeneralVO accpsndocVO,
			GeneralVO mainaddpsndocVO,nc.vo.pub.billcodemanage.BillCodeObjValueVO billcodevo) 
			throws BusinessException; 
	/**
	 * V35 add 保存批量数据
	 * @param data
	 * @param tableCode
	 * @return String[]插入记录的pk数组
	 * @throws BusinessExcepiotn
	 */
	public String[] insertTable(
			nc.vo.pub.CircularlyAccessibleValueObject[] data, String tableCode)
			throws BusinessException; 
	
	/**
	 * 转入人员档案,同时把 任职子集 的数据同步到 工作履历表
	 * @param psnList
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public void intoDoc(GeneralVO[] psnList, String[] pk_psndocs,String userid)
			throws BusinessException; 
	/**
	 * 原接口
	 * 转入人员档案,同时把 任职子集 的数据同步到 工作履历表
	 * @param psnList
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public void intoDoc(GeneralVO[] psnList, String[] pk_psndocs)
			throws BusinessException; 
	/**
	 * 更新主表的显示字段
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public void updateShoworder(String[] pk_psndocs,HashMap psnshoworder) 
			throws BusinessException;
	
	/**
	 * 检查业务子集是否允许查看历史
	 * @param tablename
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean isTraceTableLookHistory(String tablename) throws BusinessException;
	
	/**
	 * 是否存在人员编码重复的人员
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean hasPerson() throws BusinessException;
	
	/**
	 * 引用人员修改主集时处理薪资情况
	 * @param psndoc
	 * @param accpsndocVO
	 * @param mainaddpsndocVO
	 * @param logdate
	 * @throws BusinessException
	 */
	public void updateRefPersonMain(GeneralVO psndoc, GeneralVO accpsndocVO,GeneralVO mainaddpsndocVO,UFDate logdate) throws BusinessException;
	/**
	 * 判断人员类别是否为非在职
	 * @param pk_psncl
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean isOutdutyPsncl(String pk_psncl)throws BusinessException;

	
	/**
	 * 取得用户的email地址
	 * @param recievers
	 * @return GeneralVO[]
	 */
	public GeneralVO[] getRecieverEmails(String recievers)throws BusinessException ;
	/**
	 * 发送mail
	 * @param subject
	 * @param content
	 * @param emailAddress
	 */
	public void sendMail(String subject, String content, String[] emailAddress)throws BusinessException;

	/**
	 * 批量修改人员编码
	 * @param psnvos
	 * @throws BusinessException
	 */
	public void batchUpdatePsnCode(GeneralVO[] psnvos) throws BusinessException;
	
	/**
	 * 按照条件多表关联查询入职申请单
	 * @param table
	 * @param Where
	 * @return DocApplyHVO[] 入职申请单数组
	 * @throws BusinessException
	 */
	public  abstract  DocApplyHVO[] queryDocApplyBillByCon(String tableandWhere)throws BusinessException;
	
	/**
	 * 检验返聘再聘人员
	 * @param pk_corp
	 * @param conitmUniqueFields
	 * @param psnbasDocVO
	 * @return GeneralVO
	 * @throws BusinessException
	 */
	public abstract GeneralVO checkRehirePerson(String pk_corp,
			nc.vo.bd.psndoc.PsndocConsItmVO[] conitmUniqueFields,
			CircularlyAccessibleValueObject psnbasDocVO,int returntype) throws BusinessException;
	

	/**
	 * 修改人员状态
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public abstract void updatePsnState(String[] pk_psndocs ,int state)throws BusinessException;

	/**
	 * 校验申请单保存时的人员信息
	 * @param pk_psndocs
	 * @param bodypkMap
	 * @return String
	 * @throws BusinessException
	 */
	public String checkApplyPsn(String[] pk_psndocs,HashMap bodypkMap)throws BusinessException;
	/**
	 * 查询所有关联公司
	 * @param userID
	 * @param pk_corp
	 * @param isRelate
	 * @return CtrlDeptVO[]
	 * @throws BusinessException
	 */
	public CtrlDeptVO[] queryAllRelatedCorps(String userID, String pk_corp,
			boolean isRelate) throws BusinessException;
	
	/**
	 * 增加业务子集记录后需要处理recordnum和lastflag 
	 * 该方法是在插入数据记录后调用，该接口负责更新记录的recordnum和lastflag 
	 * @param tablecode  业务子集表名
	 * @param pk_psndoc_subs  新增子集记录主键数组
	 * @throws BusinessException
	 */
	public void afterInsertChild(String tablecode,String[] pk_psndoc_subs) throws BusinessException;
	/**
	 * 删除业务子集记录  
	 * @param tablecode  业务子集表名
	 * @param pk_psndoc_sub  子集记录主键
	 * @throws BusinessException
	 */
	public void deleteChildSet(String tablecode,String[] pk_psndoc_sub) throws BusinessException;
	
	/**
	 * 查询兼职人员业务子集信息
	 * @param pk_psndoc
	 * @param pk_corp
	 * @param table
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPartPersonChildInfo(String pk_psndoc, String pk_corp,String table)
		throws BusinessException;
	/**
	 * 根据人员基本档案主键和公司主键查询人员bd_psndoc信息表的记录
	 * @param pk
	 * @param table
	 * @param pk_corp
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPersonInfo(String pk, String table,String pk_corp)
			throws BusinessException ;
	/**
	 * v55 add 处理自助用户
	 * @param vos
	 * @return Hashtable<人员主键，是否部门负责人> 
	 * @throws BusinessException
	 */
	public Hashtable isNeedSelfUser(GeneralVO[] vos) throws BusinessException;

	/**
	 * 批量取消人员引用
	 * @param vos
	 * @throws BusinessException
	 */
	public void batchCancelRefPsn(GeneralVO[] vos) throws BusinessException;

	/**
	 * 保存多条子集记录信息
	 * @param tableCode
	 * @param pk_psndoc
	 * @param vos
	 * @param delPkPsndocSubs
	 * @throws BusinessException
	 */
	public void saveSubSetInfos(String tableCode, String pk_psndoc,
			GeneralVO[] vos,String[] delPkPsndocSubs) throws BusinessException;
	
	/**
	 * 人员信息采集节点使用的查询人员方法
	 * @param indocflag
	 * @param pk_corp
	 * @param DLGwheresql
	 * @param DLGtables
	 * @param loginCorp
	 * @param userid
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @param funcParser
	 * @param ordrebyclause
	 * @param includesubcorp
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition_Collect(boolean indocflag,
			String pk_corp, String DLGwheresql, String[] DLGtables,
			String loginCorp, String userid, String listfield,
			String normalwheresql, int recordcount,
			BusinessFuncParser_sql funcParser,String ordrebyclause,boolean includesubcorp) throws BusinessException;

	/**
	 * 人员信息采集节点使用的查询人员数量方法
	 * @param indocflag
	 * @param pk_corp
	 * @param DLGwheresql
	 * @param DLGtables
	 * @param loginCorp
	 * @param userid
	 * @param normalwheresql
	 * @param funcParser
	 * @param includesubcorp
	 * @return int 人员数量
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition_Collect(boolean indocflag,
			String pk_corp, String DLGwheresql, String[] DLGtables,
			String loginCorp, String userid, String normalwheresql,
			BusinessFuncParser_sql funcParser,boolean includesubcorp) throws BusinessException;

	/**
	 * 人员信息维护节点使用的查询人员数量方法
	 * @param indocflag
	 * @param pk_corp
	 * @param DLGwheresql
	 * @param DLGtables
	 * @param scope
	 * @param loginCorp
	 * @param userid
	 * @param jobtype
	 * @param normalwheresql
	 * @param funcParser
	 * @param includesubcorp
	 * @return int 人员数量
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition_Maintain(boolean indocflag,
			String pk_corp, String DLGwheresql, String[] DLGtables,
			int scope, String loginCorp, String userid, int jobtype,
			String normalwheresql, BusinessFuncParser_sql funcParser,boolean includesubcorp)
			throws BusinessException;

	/**
	 * 人员信息维护节点使用的查询人员方法
	 * @param indocflag
	 * @param pk_corp
	 * @param DLGwheresql
	 * @param DLGtables
	 * @param scope
	 * @param loginCorp
	 * @param userid
	 * @param jobtype
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @param funcParser
	 * @param orderbyclause
	 * @param includesubcorp
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition_Maintain(boolean indocflag,
			String pk_corp, String DLGwheresql, String[] DLGtables,
			int scope, String loginCorp, String userid, int jobtype,
			String listfield, String normalwheresql, int recordcount,
			BusinessFuncParser_sql funcParser,String orderbyclause,boolean includesubcorp) throws BusinessException;
	/**
	 * 培训模块所使用的查询。
	 * @param indocflag
	 * @param pk_corps
	 * @param DLGwheresql
	 * @param DLGtables
	 * @param loginCorp
	 * @param userid
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @param funcParser
	 * @param ordrebyclause
	 * @param isDeptPower
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition_Train(boolean indocflag,
			String[] pk_corps, String DLGwheresql, String[] DLGtables,
			String loginCorp, String userid, String listfield,
			String normalwheresql, int recordcount,
			BusinessFuncParser_sql funcParser,String ordrebyclause,boolean isDeptPower)
			throws BusinessException;

	/**
	 * 关键人员条件选择增加时的查询方法。
	 */
	public GeneralVO[] queryByCondition_KeyPerson(boolean indocflag,
			String pk_corp, String DLGwheresql, String[] DLGtables,
			int scope, String loginCorp, String userid, int jobtype,
			String listfield, String normalwheresql, int recordcount,
			BusinessFuncParser_sql funcParser,String orderbyclause,boolean includesubcorp)
			throws BusinessException ;
	/**
	 * 查询指定人员PK和子集编码的记录
	 * @param pk
	 * @param pk_corp
	 * @param table
	 * @param funcParser
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	public GeneralVO[] queryMainPersonInfo(String pk, String pk_corp,
			String table,
			BusinessFuncParser_sql funcParser) throws BusinessException; 
	
	/**
	 * 通过pk取得om_dumorg信息
	 * @param pk_om_dumorg
	 * @return GeneralVO
	 * @throws BusinessException
	 */
	public GeneralVO queryDetailForDumorg(String pk_om_dumorg)
			throws BusinessException;

	/**
	 * 校验PsndocInf
	 * @param psninfVO
	 * @param pk_corp
	 * @return GeneralVO
	 * @throws BusinessException
	 */
	public GeneralVO validatePsndocInf(GeneralVO psninfVO, String pk_corp)
			throws BusinessException; 
	
	/**
	 * 得到对应公司的权限sql,
	 * 2008.11.24新需求：若是查看下级单位，则不受部门权限控制，本单位（登录单位）受部门权限限制
	 * @param depttable
	 * @param userid
	 * @param pk_corp
	 * @param loginCorp
	 * @param includesubcorp
	 * @return String powersql
	 * @throws BusinessException
	 */
	public String getDeptPowerSqls(String depttable, String userid,
			String pk_corp,String loginCorp,boolean includesubcorp) throws BusinessException;
	
	/**
	 * 得到对应公司的权限sql,
	 * 2008.11.24新需求：若是查看下级单位，则不受部门权限控制，本单位（登录单位）受部门权限限制
	 * 2012.04.01新需求：增加多公司选择。
	 * @param depttable
	 * @param userid
	 * @param pk_corp
	 * @param loginCorp
	 * @param includesubcorp
	 * @return String powersql
	 * @throws BusinessException
	 */
	public String getDeptPowerSqlsForPkCorps(String depttable, String userid,
			String pk_corp,String loginCorp,boolean includesubcorp,Vector vecCorp) throws BusinessException;
	/**
	 * 检查是否存在有未审核的（人员信息变更）单据。
	 * @param pk_psndocs
	 */
	public void checkIfExistsUnAuditBillofPSN(String[] pk_psndocs,String setcode) throws BusinessException;

	/**
	 * 获得审批通过的人员档案的GeneralVO
	 * @return
	 * @throws BusinessException 
	 * @throws BusinessException 
	 */
	public GeneralVO[] getIntoDocData(GeneralVO[] psnlistVOS )throws BusinessException;
	
	public void updatePsnnamePinyin() throws BusinessException;
	
	/***
	 * 根据主键查询工作记录<br>
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	GeneralVO queryByPsndocPK(String pk_psndoc) throws BusinessException;

    /**
     * 传入新查询窗口的tables数组，根据个table表名，关联表。 替换原getTableStr方法。要求查询模板条件中使用defdoc的字段对应的表别名要有规则。
     * 
     * @param tables
     * @param flag
     *            =0表示不需再拼hi_psndoc_deptchg。（在职）
     * @return
     */
    public String getTableStr_newDLG(String[] tables, int flag);
    
    /**
	 * 根据工作记录生成任职记录——招聘管理中人员转入用
	 * @param pk_psndoc 人员工作记录主键
	 * @param userid　用户主键
	 * @param pk_corp　人员的归属公司
	 * @throws BusinessException
	 */
	public void generateDeptchgByPsndoc(String pk_psndoc, String userid, String pk_corp) throws BusinessException;
	
	
	public void saveexptvo(List<PSNDOCEXPORTVO> tvo)throws BusinessException;
	
	
	/**
	 * 保存年度考核信息
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public abstract void savePsndocGrpDef15(CircularlyAccessibleValueObject[] datadests)throws BusinessException;
}
