/*
 * 创建日期 2006-7-13
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
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
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public interface IWaMultiReport {

	/**
	 * EJB规范中要求的方法。
	 *
	 * 创建日期：(2001-8-14)
	 */

	public abstract WaMultiReportVO01[] queryWaMultiReport01(nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL, nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO, String a_strYear,
			String a_strPeriod, Integer maxRow)  throws nc.vo.pub.BusinessException;
	/**
	 * 按照多个薪资期间 查找薪资报表.说明:薪资期间存放在一个list里面.例如:{[2007,06],[2007,07]}
	 * 修改历史:<Strong>xuanlt 2007 六月 20<Strong>
	 * @param a_aryItemInfoVO
	 * @param a_strWhereSQL
	 * @param a_aryWaClassVO
	 * @param alist    存放薪资期间的列表
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
	 * 根据薪资类别,会计期间对工资进行汇总
	 * @param a_aryItemInfoVO
	 * @param a_strWhereSQL
	 * @param a_aryWaClassVO    薪资类别
	 * @param accYear           会计年
	 * @param accPeriod         会计期间
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 */
	public abstract WaMultiReportVO02[] queryWaMultiReport02ByAccperiod(
			nc.vo.wa.wa_024.ItemVO[] a_aryItemInfoVO, String a_strWhereSQL,
			nc.vo.wa.wa_008.WaclassVO[] a_aryWaClassVO,String accYear, String accPeriod, String userid
	) throws nc.vo.pub.BusinessException;


	/**
	 * 根据信息会计期间,会计年度,薪资类别 进行查询
	 * @param a_aryItemInfoVO
	 * @param a_strWhereSQL
	 * @param a_aryWaClassVO    薪资类别
	 * @param accYear           会计年度
	 * @param accMonth          会计期间
	 * @param maxRow            返回的最大行数
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
	 * 工资总额实发金额统计
	 *
	 * @author wzq
	 */
	public abstract CircHashVO[] queryPracMoney(String pkcorp, String pkdept, String cyear, boolean iscontra)  throws nc.vo.pub.BusinessException;

	/**
	 * 单位工资总额台账
	 *
	 * @author guoad
	 */
	//	public CircHashVO[] getPeriodMoney(String pkcorp, String accYear, String accPeriod,
	//			boolean iscontra)  throws nc.vo.pub.BusinessException {
	//		try {
	//			DataDMO dmo = new DataDMO();
	//			WaMultiReportDMO reportdmo = new WaMultiReportDMO();
	//
	//			//得到当前公司的所有纳入工资总额的薪资类别
	//			String[] classId = dmo.getClassId(pkcorp);
	//
	//			if(classId == null || classId.length < 1){
	//				return null;
	//			}
	//			//取得纳入工资总额类别的纳入工资总额项目
	//			HashMap itemMap = new HashMap();
	//			for (int i = 0; i < classId.length; i++) {
	//				String cacuItem = dmo.getCacuItem(classId[i]);
	//				//如果没有纳入工资总额的项目
	//				if (cacuItem == null) {
	//					itemMap.put(classId[i], null);
	//				} else {
	//					itemMap.put(classId[i], cacuItem);
	//				}
	//			}
	//			//取得每个薪资类别对应的会计期间的并集
	//			int startNum = 1;
	//			int ednNum = 20;
	//			String[] periods = reportdmo.getallPeriod(accYear,classId);
	//
	//			for(int i = 0; i < periods.length; i++){
	//                //根据会计期间得到需要记入本会计期间的薪资期间
	//				for(){
	//
	//
	//				}
	//				//String periodCon = reportdmo.getPeriodCon(accYear, accPeriod, classId[i]);
	//				//如果当前薪资类别不存在纳入工资总额的新资项目，跳过当前类别进行下一个薪资类别的计算
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
	 * 员工工资台账
	 *
	 * @author wzq
	 */
	public abstract GeneralVO[] queryStuffReport(String pkdept, String userid, String cyear, ItemVO[] itemvos)  throws nc.vo.pub.BusinessException;

	/**
	 * 取得所有会计年度的所有会计期间
	 *
	 * 创建日期：(2005-3-2)
	 *
	 * @exception nc.vo.pub.BusinessException
	 */
	public abstract Hashtable getReportPeriodByYear()  throws nc.vo.pub.BusinessException;

	/**
	 * mapping the account period which is consist of  the year and month  to wage period
	 * 修改历史:<Strong>xuanlt 2007 六月 19<Strong>
	 * @param year
	 * @param period
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 * @see
	 */
	public ArrayList getWAPeriodByAccperiod(String year ,String period) throws nc.vo.pub.BusinessException ;
}