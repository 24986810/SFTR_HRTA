package nc.itf.hr.ta;

import java.util.ArrayList;
import java.util.Map;

import nc.vo.hr.comp.formulaset.FuncParser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren.power.UserClassDeptVO;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;
import nc.vo.tbm.tbm_029.BclbItemVO;
import nc.vo.tbm.tbm_029.BclbVO;
import nc.vo.trade.pub.HYBillVO;

public interface IBclbDefining {

	/**
	 * 根据主键在数据库中删除一个VO对象。
	 *
	 * 创建日期：(2001-6-6)
	 * @param key String
	 * @return int 0：正常删除；1：纪录被其它表引用，无法删除
	 * @exception BusinessException 异常说明。
	 */
	public abstract void deleteBclb(BclbHeaderVO vo) throws BusinessException;

	/**
	 * 向数据库中插入一个VO对象。
	 *
	 * 创建日期：(2001-6-6)
	 * @param bclb nc.vo.pd.pd1020.BclbVO
	 * @return java.lang.String  所插入VO对象的主键字符串。
	 * @exception BusinessException 异常说明。
	 */
	public abstract String insertBclb029(BclbVO bclb) throws BusinessException;

	/**
	 * 根据主键在数据库中删除一个VO对象。
	 *
	 * 创建日期：(2001-6-6)
	 * @param key String
	 * @return int 0：没引用；1：纪录被其它表引用
	 * @exception BusinessException 异常说明。
	 */
	public abstract int isInUseBclb029(BclbHeaderVO vo) throws BusinessException;

	public abstract BclbHeaderVO[] queryBclb029AllBclbHeader(String unitCode,
			String gcbm) throws BusinessException;

	//刘鹏飞新增，同上方法，只新增变量sql
	public abstract BclbHeaderVO[] queryBclb029AllBclbHeader(String unitCode,
			String gcbm,String sql) throws BusinessException;

	public abstract BclbItemVO[] queryAllBclbItemBclb029(BclbHeaderVO bhvo)
			throws BusinessException;
	
	/**
	 * 查询公司中的默认班别
	 * @param pkCorp
	 * @return BclbHeaderVO
	 * @throws SQLException
	 */
	public BclbHeaderVO queryDefaultBclbHeader(String pkCorp)throws BusinessException ;

	/**
	 * 用VO对象的属性值更新数据库。
	 *
	 * 创建日期：(2001-6-6)
	 * @param bclb nc.vo.pd.pd1020.BclbVO
	 * @exception BusinessException 异常说明。
	 */
	public abstract void updateBclb029(BclbVO bclb,boolean isPsncalendarClear,FuncParser fpFuncParser,int l_intRuleScale) throws BusinessException;
	
	public abstract void updateBclb029(BclbVO bclb) throws BusinessException;
	
	
	/**
	 * 通过主键获得VO对象。
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	public abstract BclbHeaderVO findBclbHeaderByPrimaryKey(String key)throws BusinessException;
	
	
	public abstract BclbHeaderVO findBclbHeaderByClassName(String className,String pkCorp)throws BusinessException;
	
	/**
	 * 通过主键获得VO对象。
	 *
	 * 创建日期：(2002-1-22)
	 * @return nc.vo.tbm.tbm_002.BclbVO
	 * @param key String
	 * @exception BusinessException 异常说明。
	 */
	public abstract BclbVO findBclbByPrimaryKey(String key)
			throws BusinessException;
	
	public Map queryBclbByCorpMap(String pkCorp) throws BusinessException;
	
	public BclbVO[] queryBclbByCorp(String pkCorp) throws BusinessException;
	
	public abstract void insertBclbs(BclbHeaderVO[] bclbHeaderVOs) throws BusinessException;

	public boolean setRoleOrUserAtdTypeAuth(String tempid,ArrayList<UserDeptVO> list,int vflag,String pk_module,String pk_corp,int type) throws BusinessException;

	public boolean setClassRoleOrUserAtdTypeAuth(String tempid,ArrayList<UserClassDeptVO> list,int vflag,String pk_module,String pk_corp,int type) throws BusinessException;

	public boolean setClassRoleOrUserAtdClassTypeAuth(String tempid,ArrayList<UserClassTypeVO> list,int vflag,String pk_module,String pk_corp,int type) throws BusinessException;

	public void onAuditApply(HYBillVO billvo) throws BusinessException;
	
	public void onCancleAuditApply(HYBillVO billvo) throws BusinessException;
	
	public void onSaveApply(HYBillVO billvo) throws BusinessException;
	
	public void onDelApply(HYBillVO billvo) throws BusinessException;
	
	public void getOAHoilday(UFDate date) throws BusinessException;
	public void onBoAudit(ArrayList<String> listupdate) throws BusinessException;
	public void onBoUnAudit(ArrayList<String> listupdate) throws BusinessException;
	public void onBoOaUpload(ArrayList<String> listupdate) throws BusinessException;
	public void onBoZbCommit(ArrayList<String> listupdate) throws BusinessException;
	
}