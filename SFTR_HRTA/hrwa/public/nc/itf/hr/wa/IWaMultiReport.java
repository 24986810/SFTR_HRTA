/*
 * �������� 2006-7-13
 *
 * TODO Ҫ���Ĵ����ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package nc.itf.hr.wa;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.vo.hash.hashVO.CircHashVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.wa.wa_020.WaMultiReportVO01;
import nc.vo.wa.wa_020.WaMultiReportVO02;
import nc.vo.wa.wa_024.ItemVO;

/**
 * @author zhangg
 *
 * TODO Ҫ���Ĵ����ɵ�����ע�͵�ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
public interface IWaMultiReport {

	/**
	 * EJB�淶��Ҫ��ķ�����
	 *
	 * �������ڣ�(2001-8-14)
	 */

	public abstract WaMultiReportVO01[] queryWaMultiReport01(nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL, nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO, String a_strYear,
			String a_strPeriod, Integer maxRow)  throws nc.vo.pub.BusinessException;
	/**
	 * ���ն��н���ڼ� ����н�ʱ���.˵��:н���ڼ�����һ��list����.����:{[2007,06],[2007,07]}
	 * �޸���ʷ:<Strong>xuanlt 2007 ���� 20<Strong>
	 * @param a_aryItemInfoVO
	 * @param a_strWhereSQL
	 * @param a_aryWaClassVO
	 * @param alist    ���н���ڼ���б�
	 * @param maxRow
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 * @see
	 */
	public abstract WaMultiReportVO01[] queryWaMultiReport01Bylist(
			nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL,
			nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO,
			ArrayList alist, Integer maxRow)
	throws nc.vo.pub.BusinessException;



	public abstract WaMultiReportVO02[] queryWaMultiReport02(
			nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL,
			nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO, String a_strYear,
			String a_strPeriod) throws nc.vo.pub.BusinessException;

	public abstract WaMultiReportVO02[] queryWaMultiReport02Bylist(
			nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL,
			nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO,
			ArrayList aList) throws nc.vo.pub.BusinessException;

	/**
	 * ����н�����,����ڼ�Թ��ʽ��л���
	 * @param a_aryItemInfoVO
	 * @param a_strWhereSQL
	 * @param a_aryWaClassVO    н�����
	 * @param accYear           �����
	 * @param accPeriod         ����ڼ�
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 */
	public abstract WaMultiReportVO02[] queryWaMultiReport02ByAccperiod(
			nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL,
			nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO,String accYear, String accPeriod, String userid
	) throws nc.vo.pub.BusinessException;


	/**
	 * ������Ϣ����ڼ�,������,н����� ���в�ѯ
	 * @param a_aryItemInfoVO
	 * @param a_strWhereSQL
	 * @param a_aryWaClassVO    н�����
	 * @param accYear           ������
	 * @param accMonth          ����ڼ�
	 * @param maxRow            ���ص��������
	 * @return
	 * @author xuanlt
	 * @throws nc.vo.pub.BusinessException
	 */
	public abstract WaMultiReportVO01[] queryWaMultiReport01ByAccPeriod(
			nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL,
			nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO,
			String accYear, String accMonth,Integer maxRow, String userid,
			nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO1)
	throws nc.vo.pub.BusinessException;




	/**
	 * �����ܶ�ʵ�����ͳ��
	 *
	 * @author wzq
	 */
	public abstract CircHashVO[] queryPracMoney(String pkcorp, String pkdept, String cyear, boolean iscontra)  throws nc.vo.pub.BusinessException;

	/**
	 * ��λ�����ܶ�̨��
	 *
	 * @author guoad
	 */
	//	public CircHashVO[] getPeriodMoney(String pkcorp, String accYear, String accPeriod,
	//			boolean iscontra)  throws nc.vo.pub.BusinessException {
	//		try {
	//			DataDMO dmo = new DataDMO();
	//			WaMultiReportDMO reportdmo = new WaMultiReportDMO();
	//
	//			//�õ���ǰ��˾���������빤���ܶ��н�����
	//			String[] classId = dmo.getClassId(pkcorp);
	//
	//			if(classId == null || classId.length < 1){
	//				return null;
	//			}
	//			//ȡ�����빤���ܶ��������빤���ܶ���Ŀ
	//			HashMap itemMap = new HashMap();
	//			for (int i = 0; i < classId.length; i++) {
	//				String cacuItem = dmo.getCacuItem(classId[i]);
	//				//���û�����빤���ܶ����Ŀ
	//				if (cacuItem == null) {
	//					itemMap.put(classId[i], null);
	//				} else {
	//					itemMap.put(classId[i], cacuItem);
	//				}
	//			}
	//			//ȡ��ÿ��н������Ӧ�Ļ���ڼ�Ĳ���
	//			int startNum = 1;
	//			int ednNum = 20;
	//			String[] periods = reportdmo.getallPeriod(accYear,classId);
	//
	//			for(int i = 0; i < periods.length; i++){
	//                //���ݻ���ڼ�õ���Ҫ���뱾����ڼ��н���ڼ�
	//				for(){
	//
	//
	//				}
	//				//String periodCon = reportdmo.getPeriodCon(accYear, accPeriod, classId[i]);
	//				//�����ǰн����𲻴������빤���ܶ��������Ŀ��������ǰ��������һ��н�����ļ���
	//				if(itemMap.get(classId[i]) == null){
	//				continue;
	//				}
	//
	//
	//
	//			}
	//
	//			return null;
	//		} catch (Exception ex) {
	//			reportException(ex);
	//			throw new BusinessException("");
	//		}
	//	}
	/**
	 * Ա������̨��
	 *
	 * @author wzq
	 */
	public abstract GeneralVO[] queryStuffReport(String pkdept, String userid, String cyear, ItemVO[] itemvos)  throws nc.vo.pub.BusinessException;

	/**
	 * ȡ�����л����ȵ����л���ڼ�
	 *
	 * �������ڣ�(2005-3-2)
	 *
	 * @exception nc.vo.pub.BusinessException
	 */
	public abstract Hashtable getReportPeriodByYear()  throws nc.vo.pub.BusinessException;

	/**
	 * mapping the account period which is consist of  the year and month  to wage period
	 * �޸���ʷ:<Strong>xuanlt 2007 ���� 19<Strong>
	 * @param year
	 * @param period
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 * @see
	 */
	public ArrayList getWAPeriodByAccperiod(String year ,String period) throws nc.vo.pub.BusinessException ;
}