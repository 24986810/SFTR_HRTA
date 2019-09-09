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
	 * �������������ݿ���ɾ��һ��VO����
	 *
	 * �������ڣ�(2001-6-6)
	 * @param key String
	 * @return int 0������ɾ����1����¼�����������ã��޷�ɾ��
	 * @exception BusinessException �쳣˵����
	 */
	public abstract void deleteBclb(BclbHeaderVO vo) throws BusinessException;

	/**
	 * �����ݿ��в���һ��VO����
	 *
	 * �������ڣ�(2001-6-6)
	 * @param bclb nc.vo.pd.pd1020.BclbVO
	 * @return java.lang.String  ������VO����������ַ�����
	 * @exception BusinessException �쳣˵����
	 */
	public abstract String insertBclb029(BclbVO bclb) throws BusinessException;

	/**
	 * �������������ݿ���ɾ��һ��VO����
	 *
	 * �������ڣ�(2001-6-6)
	 * @param key String
	 * @return int 0��û���ã�1����¼������������
	 * @exception BusinessException �쳣˵����
	 */
	public abstract int isInUseBclb029(BclbHeaderVO vo) throws BusinessException;

	public abstract BclbHeaderVO[] queryBclb029AllBclbHeader(String unitCode,
			String gcbm) throws BusinessException;

	//������������ͬ�Ϸ�����ֻ��������sql
	public abstract BclbHeaderVO[] queryBclb029AllBclbHeader(String unitCode,
			String gcbm,String sql) throws BusinessException;

	public abstract BclbItemVO[] queryAllBclbItemBclb029(BclbHeaderVO bhvo)
			throws BusinessException;
	
	/**
	 * ��ѯ��˾�е�Ĭ�ϰ��
	 * @param pkCorp
	 * @return BclbHeaderVO
	 * @throws SQLException
	 */
	public BclbHeaderVO queryDefaultBclbHeader(String pkCorp)throws BusinessException ;

	/**
	 * ��VO���������ֵ�������ݿ⡣
	 *
	 * �������ڣ�(2001-6-6)
	 * @param bclb nc.vo.pd.pd1020.BclbVO
	 * @exception BusinessException �쳣˵����
	 */
	public abstract void updateBclb029(BclbVO bclb,boolean isPsncalendarClear,FuncParser fpFuncParser,int l_intRuleScale) throws BusinessException;
	
	public abstract void updateBclb029(BclbVO bclb) throws BusinessException;
	
	
	/**
	 * ͨ���������VO����
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	public abstract BclbHeaderVO findBclbHeaderByPrimaryKey(String key)throws BusinessException;
	
	
	public abstract BclbHeaderVO findBclbHeaderByClassName(String className,String pkCorp)throws BusinessException;
	
	/**
	 * ͨ���������VO����
	 *
	 * �������ڣ�(2002-1-22)
	 * @return nc.vo.tbm.tbm_002.BclbVO
	 * @param key String
	 * @exception BusinessException �쳣˵����
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