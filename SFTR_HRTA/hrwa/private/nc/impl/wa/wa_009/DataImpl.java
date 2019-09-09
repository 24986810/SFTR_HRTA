package nc.impl.wa.wa_009;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pub.SuperDMO;
import nc.bs.pub.SystemException;
import nc.bs.uap.bd.BDRuntimeException;
import nc.bs.uap.lock.PKLock;
import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.hr.func.FuncProImpl;
import nc.impl.wa.func.WaFuncDMO;
import nc.impl.wa.wa_001.WaclassDMO;
import nc.impl.wa.wa_002.ClassitemDMO;
import nc.impl.wa.wa_002.ClassitemImpl;
import nc.impl.wa.wa_004.TaxbaseDMO;
import nc.impl.wa.wa_004.TaxbaseImpl;
import nc.impl.wa.wa_012.BankSheetImpl;
import nc.impl.wa.wa_014.DatapoolImpl;
import nc.impl.wa.wa_015.RedataImpl;
import nc.impl.wa.wa_016.PsnDMO;
import nc.impl.wa.wa_019.PeriodDMO;
import nc.impl.wa.wa_024.ItemDMO;
import nc.impl.wa.wa_050.PayrollImpl;
import nc.impl.wa.wa_hrp_009.CaculateHRPDMO;
import nc.impl.wa.wa_hrp_009.CaculateHRPDMODailyRen;
import nc.impl.wa.wa_hrp_009.CaculateHRPDMOHolidayRen;
import nc.impl.wa.wa_hrp_009.CaculateHRPDMOTongRen;
import nc.impl.wa.wa_hrp_009.CaculateHRPPFDMO;
import nc.itf.hr.bd.IGlobaldata;
import nc.itf.hr.comp.IParValue;
import nc.itf.hr.pub.PubDelegator;
import nc.itf.hr.wa.IRecaData;
import nc.itf.hr.wa.IWaClass;
import nc.itf.uap.bd.currtype.ICurrtype;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.itf.uap.sf.IProductVersionQueryService;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.bm.bm_001.PeriodVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.jcom.util.Convertor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.install.ProductVersionVO;
import nc.vo.sm.nodepower.OrgnizeTypeVO;
import nc.vo.tm.hrp.pub.TMBVO;
import nc.vo.wa.func.WaLangUtil;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaClassStateHelper.WaStates;
import nc.vo.wa.wa_001.DataPowerUtil;
import nc.vo.wa.wa_001.WaGlobalVO;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_004.TaxbaseVO;
import nc.vo.wa.wa_004.TaxtableVO;
import nc.vo.wa.wa_008.WaclassVO;
import nc.vo.wa.wa_009.CaculateInfoVO;
import nc.vo.wa.wa_009.DataVO;
import nc.vo.wa.wa_009.ReCacuVO;
import nc.vo.wa.wa_013.TaxVO;
import nc.vo.wa.wa_016.PsnVO;
import nc.vo.wa.wa_019.PeriodsetVO;
import nc.vo.wa.wa_024.ItemVO;
import nc.vo.wa.wa_051.PaySlipDataVO;
import nc.vo.wa.wa_051.PayslipVO;
import nc.vo.wa.wa_053.WarnMessage;
import nc.vo.wa.wa_053.WarnMessageVo;
import nc.vo.wa.wa_055.TmCorpYearPlanVO;
import nc.vo.wa.wa_056.TmDeptYearPlanVO;

/**
 * Data的Impl类
 *
 * 创建日期：(2001-4-29)
 *
 * @author：张森
 */
public class DataImpl implements IRecaData {

	/**
	 * DataImpl 构造子注解。
	 */
	public DataImpl() {
		super();
	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateFormuItem(ReCacuVO aRecaVO) throws Exception {
		try {
			caculateFormuItem_beforeTax(aRecaVO);
			caculateFormuItem_afterTax(aRecaVO);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public TaxVO getTaxValueVO(WaclassVO waClassVO, String waYear, String waPeriod, String psnId, UFDouble taxBase) throws Exception {

		new UFDouble(0);
		TaxVO re = new TaxVO();
		/* 缺省值 */
		// 减费用额
		re.setNdebuctamount(new UFDouble(0));

		// 应纳税所得额, 主币
		re.setFtaxmny(new UFDouble(0));

		// 税率
		re.setNtaxrate(new UFDouble(0));
		// 速算扣除数
		re.setNquickdebuct(new UFDouble(0));
		// 应缴税款
		re.setFtaxz(new UFDouble(0));

		try {
			String gzlbId = waClassVO.getPrimaryKey();
			nc.vo.wa.wa_016.PsnVO psnVO = new nc.vo.wa.wa_016.PsnVO();
			psnVO.setClassid(gzlbId);
			psnVO.setCyear(waYear);
			psnVO.setCperiod(waPeriod);
			psnVO.setPsnid(psnId);

			nc.impl.wa.wa_016.PsnDMO psnDMO = new nc.impl.wa.wa_016.PsnDMO();

			psnVO = psnDMO.queryByVO(psnVO, new Boolean(true))[0];

			if (psnVO.getItaxflag().booleanValue()) // 计税
			{
				String taxTblId = psnVO.getTaxtableid();

				if (taxTblId == null || taxTblId.trim().length() <= 0) {
					taxTblId = waClassVO.getTaxtableid();
				}
				// tax=getTaxValue(taxTblId,taxBase);
				re = getTaxValue(waClassVO, taxTblId, taxBase);
			}

		} catch (Exception ex) {
			reportException(ex);
			throw new Exception(ex.getMessage());
		}
		return re;
	}

	private void reportException(Exception e) {
		e.printStackTrace();
		Logger.error(e.getMessage(), e);
	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateFormuItem(ReCacuVO aRecaVO, String[] formula) throws Exception {

		if (formula[2].equals("watable")) {
			try {
				DataDMO dmo = new DataDMO();

				nc.impl.wa.wa_003.WageformDMO wageFormDMO = new nc.impl.wa.wa_003.WageformDMO();
				nc.vo.wa.wa_003.WageformVO wageFormVO = wageFormDMO.findByPrimaryKey(formula[1]);

				// 薪资表缺省值
				UFDouble defaultValue = wageFormVO.getNdefaultmny();
				dmo.caculateSaTblItemDefault(formula[0], defaultValue, aRecaVO);

				nc.impl.wa.wa_003.WageformdetDMO wageFormDetDMO = new nc.impl.wa.wa_003.WageformdetDMO();
				nc.vo.wa.wa_003.WageformdetVO wageFormDetVO = new nc.vo.wa.wa_003.WageformdetVO();
				wageFormDetVO.setPk_wa_wageform(formula[1]);

				// 薪资表条件
				nc.vo.wa.wa_003.WageformdetVO[] wageFormDetVOArray = wageFormDetDMO.queryByVO(wageFormDetVO, new Boolean(true));

				if (wageFormDetVOArray != null && wageFormDetVOArray.length > 0) {
					for (int j = wageFormDetVOArray.length - 1; j >= 0; j--) {
						String condition = wageFormDetVOArray[j].getVformula();
						UFDouble value = wageFormDetVOArray[j].getNvalue();
						dmo.caculateFormulaItem(formula[0], aRecaVO.getFpFuncParser().getBfpsBusinessFuncParser_sql().transToSql(condition), value.toString(), aRecaVO);
					}
				}
			} catch (Exception e) {
				reportException(e);
				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000004")/*
				 * @res
				 * "薪资表设置有误，请检查！"
				 */);
			}
		} else {
			String itemCode = "";
			try {
				if (formula[0] != null && formula[0].trim().length() > 0 && formula[1] != null && formula[1].trim().length() > 0) {
					DataDMO dmo = new DataDMO();
					if (formula[1].startsWith("<")) {// 来自薪资函数
						reCaculateWoForSequ(aRecaVO, formula, dmo);
					} else {// 普通公式
						String subFormula[][] = aRecaVO.getFpFuncParser().parseFunction(formula[1]);

						itemCode = formula[0];

						if (subFormula != null && subFormula.length > 0) {
							for (int subIndex = subFormula.length - 1; subIndex >= 0; subIndex--) {
								String aSubCondition = subFormula[subIndex][0];

								if (aSubCondition == null || aSubCondition.trim().length() <= 0) {
									aSubCondition = "(1=1)";
								}

								String aSubValue = subFormula[subIndex][1];

								dmo.caculateFormulaItem(itemCode, aSubCondition, aSubValue, aRecaVO);
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				String itemName = aRecaVO.getItemNameByCode(itemCode);

				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000000")/*
				 * @res
				 * "计算公式项："
				 */
						+ itemName + NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000001")/*
						 * @res
						 * "时出错，请重新检查它的公式！"
						 */);
			}
		}
	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateFormuItem_afterTax(ReCacuVO aRecaVO) throws Exception {
		try {
			// int NOTZERO = 0; //不扣零
			String[][] formulaItem = aRecaVO.getFormulaItem();

			Hashtable h = queryAllGroupAndClassIDS(aRecaVO.getWaPeriodvo().getClassid(), aRecaVO.getWaPeriodvo().getCaccyear(), aRecaVO.getWaPeriodvo().getCaccperiod());
			aRecaVO.setHash_Group_ClassIDS(h);
			boolean beginTax = false;
			if (formulaItem != null) {
				// 对每个公式作循环
				for (String[] element : formulaItem) {
					String itemCode = element[0].trim();
					if (itemCode.equalsIgnoreCase("f_9")) {
						beginTax = true;
					}
					if (beginTax) {
						if (isSysItem(itemCode)) {
							 if(itemCode.equals("f_5")){
			                    	System.out.print("f_f");
			                    }
							caculateSysItem(aRecaVO, itemCode);
						} else {
							caculateFormuItem(aRecaVO, element);
						}
					}
				}
			}
			//算完税，计算欠款及残疾人数据 
			XhDataDMO dmo = new XhDataDMO();
//			dmo.countCjrDk(aRecaVO);
			dmo.countQkAndAfterQk(aRecaVO);
			// 将数据复制到wa_dataz,wa_dataf中
			copyToDataZandDataF(aRecaVO);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateFormuItem_afterTax_zero(ReCacuVO aRecaVO) throws Exception {
		try {
			// int NOTZERO = 0; //不扣零
			String[][] formulaItem = aRecaVO.getFormulaItem();
			DataDMO dmo = new DataDMO();
			boolean beginTax = false;
			// 在取消发放后把本薪资项目的wa_dataz扣税项目清空。
			dmo.deDuctThisTax(aRecaVO);
			// 对每个公式作循环
			for (String[] element : formulaItem) {
				String itemCode = element[0].trim();
				if (itemCode.equalsIgnoreCase("f_9")) {
					beginTax = true;
				}
				if (beginTax) {
					if (isSysItem(itemCode)) {
						caculateSysItem_zero(aRecaVO, itemCode);
					} else {
						caculateFormuItem_zero(aRecaVO, element);
					}
				}
			}
			// 将数据复制到wa_dataz,wa_dataf中
			copyToDataZandDataF(aRecaVO);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateFormuItem_beforeTax(ReCacuVO aRecaVO) throws Exception {
		try {
			
			String vyear = aRecaVO.getWaYear();
			String vperiod = aRecaVO.getWaPeriod();
			String pk_corp = aRecaVO.getPk_corp();
			String wheresql_a  = aRecaVO.getSelCondition();
			SuperDMO  dmo = new SuperDMO();
			BaseDAO dao = new BaseDAO();
			
				DataDMO datadmo = new DataDMO();
				DataVO[] datavos = datadmo.queryAll(aRecaVO.getWaClassVO().getPrimaryKey(), vyear, vperiod, new String[]{"f_3"}, new String[]{"1"},wheresql_a);
				if(datavos!=null||datavos.length>0){//计算前先清空这些项目
					for(int i=0;i<datavos.length;i++){
						String updatesql = "update wa_data set dr=0 ";
						updatesql+=" ,f_417=0.00,f_416=0.00,f_481=0.00,f_482=0.00  ";
						updatesql+=" where pk_wa_data='"+datavos[i].getPrimaryKey()+"' ";
						dao.executeUpdate(updatesql);
					}
				}
			
			
			
			
			// int NOTZERO = 0; //不扣零
			String[][] formulaItem = aRecaVO.getFormulaItem();

			if (formulaItem == null || formulaItem.length == 0) {
				return;
			}
			// 对每个公式作循环
			for (String[] element : formulaItem) {
				String itemCode = element[0].trim();
				if (itemCode.equalsIgnoreCase("f_9")) {
					return;
				}
				if(itemCode.equals("f_405")){
					System.out.print("f_f");
				}
				if (isSysItem(itemCode)) {
                    if(itemCode.equals("f_5")){
                    	System.out.print("f_f");
                    }
					caculateSysItem(aRecaVO, itemCode);
				} else {
					caculateFormuItem(aRecaVO, element);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateFormuItem_zero(ReCacuVO aRecaVO, String[] formula) throws Exception {
		if (formula[2].equals("watable")) {
			try {
				DataDMO dmo = new DataDMO();

				nc.impl.wa.wa_003.WageformDMO wageFormDMO = new nc.impl.wa.wa_003.WageformDMO();
				nc.vo.wa.wa_003.WageformVO wageFormVO = wageFormDMO.findByPrimaryKey(formula[1]);

				// 薪资表缺省值
				UFDouble defaultValue = wageFormVO.getNdefaultmny();
				dmo.caculateSaTblItemDefault(formula[0], defaultValue, aRecaVO);

				nc.impl.wa.wa_003.WageformdetDMO wageFormDetDMO = new nc.impl.wa.wa_003.WageformdetDMO();
				nc.vo.wa.wa_003.WageformdetVO wageFormDetVO = new nc.vo.wa.wa_003.WageformdetVO();
				wageFormDetVO.setPk_wa_wageform(formula[1]);

				// 薪资表条件
				nc.vo.wa.wa_003.WageformdetVO[] wageFormDetVOArray = wageFormDetDMO.queryByVO(wageFormDetVO, new Boolean(true));

				if (wageFormDetVOArray != null && wageFormDetVOArray.length > 0) {
					for (int j = wageFormDetVOArray.length - 1; j >= 0; j--) {
						String condition = wageFormDetVOArray[j].getVformula();
						UFDouble value = wageFormDetVOArray[j].getNvalue();
						dmo.caculateFormulaItem(formula[0], aRecaVO.getFpFuncParser().getBfpsBusinessFuncParser_sql().transToSql(condition), value.toString(), aRecaVO);
					}
				}
			} catch (Exception e) {
				reportException(e);
				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000004")/*
				 * @res
				 * "薪资表设置有误，请检查！"
				 */);
			}
		} else {

			String itemCode = "";
			try {
				if (formula[0] != null && formula[0].trim().length() > 0 && formula[1] != null && formula[1].trim().length() > 0) {
					DataDMO dmo = new DataDMO();
					if (formula[1].startsWith("<")) {// 来自薪资函数
						reCaculateWoForSequ(aRecaVO, formula, dmo);
					} else {// 普通公式
						String subFormula[][] = aRecaVO.getFpFuncParser().parseFunction(formula[1]);

						itemCode = formula[0];

						if (subFormula != null && subFormula.length > 0) {
							for (int subIndex = subFormula.length - 1; subIndex >= 0; subIndex--) {
								String aSubCondition = subFormula[subIndex][0];

								if (aSubCondition == null || aSubCondition.trim().length() <= 0) {
									aSubCondition = "(1=1)";
								}

								String aSubValue = "";
								if (aRecaVO.isDigitItemRefWithCurr(itemCode) || aRecaVO.isDigitItemRefWithoutCurr(itemCode)) {
									aSubValue = "0";
								}

								dmo.caculateFormulaItem(itemCode, aSubCondition, aSubValue, aRecaVO);
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				String itemName = aRecaVO.getItemNameByCode(itemCode);

				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000000")/*
				 * @res
				 * "计算公式项："
				 */
						+ itemName + NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000001")/*
						 * @res
						 * "时出错，请重新检查它的公式！"
						 */);
			}
		}
	}

	/**
	 * 计算: 已扣税和已扣税基数 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wwa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception javsql.SQLException
	 *                    异常说明。
	 */
	private void caculateHaveTaxBase(ReCacuVO aRecaVO) throws Exception {

		try {
			aRecaVO.getWaYear();
			aRecaVO.getWaPeriod();

			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			waClassVO.getPrimaryKey();

			// int taxType = waClassVO.getItaxsetting().intValue();
			boolean isInTaxgroup = new TaxgroupDAO().isInTaxGroup(waClassVO.getPrimaryKey());

			// 合并(联合)扣税
			if (isInTaxgroup) {
				caculateHaveTaxBaseBind(aRecaVO);
			} else {// 非合并(联合)扣税
				caculateHaveTaxBaseNotBind(aRecaVO);
			}
		} catch (Exception e) {
			reportException(e);
			throw e;
		}

	}

	/**
	 * 计算已扣税基数(合并) 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wwa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception javsql.SQLException
	 *                    异常说明。
	 */
	private void caculateHaveTaxBaseBind(ReCacuVO aRecaVO) throws Exception {
		try {
			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			waClassVO.getPrimaryKey();

			aRecaVO.getWaYear();
			aRecaVO.getWaPeriod();

			// 首先得到主币
			DataDMO dmo = new DataDMO();
			// WaclassVO[] allUnitWaCalssVO = getAllUnitWaClass(waClassVO, 1);
			// String[] unitTaxWaPks = aRecaVO.getUnitTaxWaPks();

			// 将参与合并计税的类别汇总到 wa_dataz 中
			// dmo.caculateHaveTaxBindOfDataz(aRecaVO, unitTaxWaPks);
			// 2008-12-30changed
			new TaxgroupDAO().caculateHaveTaxBindOfDataz(aRecaVO);

			if (aRecaVO.getCurrentBO().isSingleMain() || aRecaVO.getCurrentBO().isZhuBi()) // 单主币
				// 或
				// 主币=原币
			{
				String rateZtoY = "1";
				String oprator = "*";
				// 通过主币更新原币
				if (!aRecaVO.getCurrentBO().isZhuBi()) {
					// 非主币
					rateZtoY = aRecaVO.getCurrentBO().getRate(waClassVO.getCurrid()).toString();
					oprator = aRecaVO.getCurrentBO().getOperatorReverse(waClassVO.getCurrid());
				}
				dmo.updateFromAnotherTable("wa_dataz", "wa_data", new String[] { "f_8", "f_9" }, rateZtoY, aRecaVO, oprator);
			}
			if (!aRecaVO.getCurrentBO().isSingleMain()) {
				// 主辅币
				// 更新辅币

				String rateZtoF = aRecaVO.getCurrentBO().getRate(aRecaVO.getCurrentBO().getFuCurrPK()).toString();
				dmo.updateFromAnotherTable("wa_dataz", "wa_dataf", new String[] { "f_8", "f_9" }, rateZtoF, aRecaVO, aRecaVO.getCurrentBO().getOperatorReverse(aRecaVO.getCurrentBO().getFuCurrPK()));

				// 通过辅币更新原币
				if (!aRecaVO.getCurrentBO().isZhuBi()) // 原币非主币
				{

					String rateFtoY = "1";
					String oprator = "*";

					if (!aRecaVO.getCurrentBO().isFuBi()) // 非辅币
					{
						rateFtoY = aRecaVO.getCurrentBO().getRate(waClassVO.getCurrid()).toString();
						oprator = aRecaVO.getCurrentBO().getOperatorReverse(waClassVO.getCurrid());
					}

					dmo.updateFromAnotherTable("wa_dataf", "wa_data", new String[] { "f_8", "f_9" }, rateFtoY, aRecaVO, oprator);
				}

			}
		} catch (Exception e) {
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000002")/*
			 * @res
			 * "计算合并计税的已扣税基数时出错，请检查参与合并计税的薪资类别的已扣税基数！"
			 */);
		}
	}

	/**
	 * 计算不合并计税的已扣税基数 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wwa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception javsql.SQLException
	 *                    异常说明。
	 */
	private void caculateHaveTaxBaseNotBind(ReCacuVO aRecaVO) throws Exception {

		try {
			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			waClassVO.getPrimaryKey();

			aRecaVO.getWaYear();
			aRecaVO.getWaPeriod();

			DataDMO dmo = new DataDMO();
			// 更新原币
			dmo.updateFromAnotherTable("wa_data", "wa_data", new String[] { "f_8", "f_9" }, "0", aRecaVO);

			// if (!aRecaVO.getCurrentBO().isSingleMain() &&
			// !aRecaVO.getCurrentBO().isZhuBi())//主辅币
			if (!aRecaVO.getCurrentBO().isSingleMain())// 主辅币
			{
				// 更新辅币
				dmo.updateFromAnotherTable("wa_dataf", "wa_dataf", new String[] { "f_8", "f_9" }, "0", aRecaVO);
			}
			// 更新主币
			dmo.updateFromAnotherTable("wa_dataz", "wa_dataz", new String[] { "f_8", "f_9" }, "0", aRecaVO);
		} catch (Exception e) {
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000003")/*
			 * @res
			 * "读取数据时出错，请刷新后再试！"
			 */);
		}

	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateSysItem(ReCacuVO aRecaVO, String itemCode) throws Exception {
		try {
			int NOTZERO = 0; // 不扣零

			DataDMO dmo = new DataDMO();

			if (itemCode.equals("f_1")) // 应发合计
			{
				dmo.caculateAllPos(aRecaVO);
			} else if (itemCode.equals("f_2")) // 扣款合计
			{
				// 扣款合计,此时不考虑本月扣零
				dmo.caculateAllNeg(aRecaVO);
			} else if (itemCode.equals("f_3")) // 实发合计
			{
				// 实发合计,此时不考虑本月扣零
				dmo.caculateFactTotal(aRecaVO);
				// 国信
				// nc.impl.wa.gx_002.DeductMgrImpl mgrbo = new
				// nc.impl.wa.gx_002.DeductMgrImpl();
				// mgrbo.reCaculate(aRecaVO.getWaClassVO().getPrimaryKey(),aRecaVO.getWaYear(),aRecaVO.getWaPeriod(),aRecaVO);
				//
			} else if (itemCode.equals("f_6")) // 本月扣零
			{
				WaclassVO waClassVo = aRecaVO.getWaClassVO();
				int zeroFlag = waClassVo.getIoddsetting().intValue();
				if (zeroFlag != NOTZERO) {
					// 重新考虑扣款合计，实发合计
					dmo.caculateKouLing(aRecaVO);
				} else {
					dmo.updateFromSameTable("wa_data", "wa_data", new String[] { "f_6" }, "0", aRecaVO, "*", true);
				}
			} else if (itemCode.equals("f_9")) // 算税
			{
				recaculateTax(aRecaVO);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * 公式项 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateSysItem_zero(ReCacuVO aRecaVO, String itemCode) throws Exception {
		try {
			DataDMO dmo = new DataDMO();

			if (!itemCode.equals("f_9")) {
				dmo.updateFromSameTable("wa_data", "wa_data", new String[] { itemCode }, "0", aRecaVO, "*", true);
			} else // 算税
			{
				dmo.updateFromSameTable("wa_data", "wa_data", new String[] { "f_4", "f_5", "f_8", "f_9" }, "0", aRecaVO, "*", true);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 *
	 * 计算应缴税款，同时将数据保存到wa_tax中 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateTax_yh(ReCacuVO aRecaVO) throws Exception {

		try {
			new nc.impl.wa.wa_004.TaxtableDMO();

			nc.vo.wa.wa_004.TaxbaseVO defaultTaxTable = new TaxbaseImpl().findTaxbaseByPk(aRecaVO.getWaClassVO().getTaxtableid());

			// 缺省税率表
			if (defaultTaxTable != null) {
				caculateTaxByTaxBase(aRecaVO, defaultTaxTable, true);
			} else {
				nc.bs.logging.Logger.error("defaultTaxTable is null，请重新设置，否则找不到税率表的人不会计算税！！！");
			}

			// 其他税率表
			WaclassVO waClass = aRecaVO.getWaClassVO();
			String gzlbId = waClass.getPrimaryKey();
			String waYear = aRecaVO.getWaYear();
			String waPeriod = aRecaVO.getWaPeriod();

			nc.vo.wa.wa_004.TaxbaseVO[] taxTables = getTaxTables(gzlbId, waYear, waPeriod);
			if (taxTables != null && taxTables.length > 0) {
				for (int i = 0; i < taxTables.length; i++) {
					if (taxTables[i] != null && (defaultTaxTable == null || !taxTables[i].getPrimaryKey().equals(defaultTaxTable.getPrimaryKey()))) {
						// 不空，且与默认税率表不一样的，则计算
						caculateTaxByTaxBase(aRecaVO, taxTables[i], false);
					}
				}
			}

			// 将本次计税从wa_dataz传至wa_data中
			updateFactTax(aRecaVO);
		} catch (Exception e) {
			reportException(e);
			if (e.getMessage() == null || e.getMessage().trim().length() < 1) {
				e = new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000005")/*
				 * @res
				 * "计税时出错，请重新检查税率表！"
				 */);
			}
			throw e;
		}

	}

	/**
	 *
	 * 计算扣税基数，同时将结果保存在wa_dataz,wa_dataf中 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateTaxBase(ReCacuVO aRecaVO) throws Exception {

		try {
			DataDMO dmo = new DataDMO();
			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			int nsysflag = waClassVO.getSysflag() == null ? 0 : waClassVO.getSysflag().intValue();

			if (nsysflag == 1) {
				caculateTaxBaseAnn(aRecaVO);
			} else {
				dmo.caculateTaxBase(aRecaVO);
			}

			// 如果在薪资档案中本人不扣税， 本次扣税基数和本次扣税为0
			dmo.setTaxBaseToZero(aRecaVO);

			String bzPk = waClassVO.getCurrid();

			boolean isZhuBi = aRecaVO.getCurrentBO().isZhuBi();
			boolean isFuBi = false;

			boolean isSingleMain = aRecaVO.getCurrentBO().isSingleMain();

			aRecaVO.getCurrentBO().getPrecisionForDatabase(aRecaVO.getCurrentBO().getMainCurrPK());
			aRecaVO.getCurrentBO().getPrecisionForDatabase(bzPk);

			UFDouble rateY = new UFDouble(1);
			UFDouble rateF = new UFDouble(1);

			String opraterY = "*";
			String opraterF = "*";
			String[] fields = new String[] { "f_5" };
			if (nsysflag == 1) {
				fields = new String[] { "f_5", "f_14" };
			}

			if (isSingleMain || isZhuBi) {
				// 单主币
				if (!isZhuBi) {
					rateY = aRecaVO.getCurrentBO().getRate(bzPk);
					opraterY = aRecaVO.getCurrentBO().getOperator(bzPk);
				}

				dmo.updateFromAnotherTable("wa_data", "wa_dataz", fields, rateY.toString(), aRecaVO, opraterY);

			}

			if (!isSingleMain)// 主辅币
			{
				// 非主币
				if (!isZhuBi) {
					isFuBi = aRecaVO.getCurrentBO().isFuBi();

					if (!isFuBi) {
						rateY = aRecaVO.getCurrentBO().getRate(bzPk);
						opraterY = aRecaVO.getCurrentBO().getOperator(bzPk);
					}
					dmo.updateFromAnotherTable("wa_data", "wa_dataf", fields, rateY.toString(), aRecaVO, opraterY);

					rateF = aRecaVO.getCurrentBO().getRate(aRecaVO.getCurrentBO().getFuCurrPK());
					opraterF = aRecaVO.getCurrentBO().getOperator(aRecaVO.getCurrentBO().getFuCurrPK());

					dmo.updateFromAnotherTable("wa_dataf", "wa_dataz", fields, rateF.toString(), aRecaVO, opraterF);
				} else// 主币
				{
					rateF = aRecaVO.getCurrentBO().getRate(aRecaVO.getCurrentBO().getFuCurrPK());
					opraterF = aRecaVO.getCurrentBO().getOperatorReverse(aRecaVO.getCurrentBO().getFuCurrPK());

					dmo.updateFromAnotherTable("wa_data", "wa_dataf", fields, rateF.toString(), aRecaVO, opraterF);
				}
			}
			// 更新所得税表的原币发生额
			dmo.updateTaxBaseYByWa_data(aRecaVO);

		} catch (Exception e) {
			reportException(e);
			if (e.getMessage() == null || e.getMessage().trim().length() < 1) {
				e = new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000006")/*
				 * @res
				 * "读取数据时出错，请刷新后再试！！"
				 */);
			}
			throw e;
		}

	}

	/**
	 *
	 * 计算扣税基数，同时将结果保存在wa_dataz,wa_dataf中 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateTaxBaseAnn(ReCacuVO aRecaVO) throws Exception {

		try {
			DataDMO dmo = new DataDMO();
			nc.vo.wa.wa_004.TaxbaseVO defaultTaxVO = new TaxbaseImpl().findTaxbaseByPk(aRecaVO.getWaClassVO().getTaxtableid());

			if (defaultTaxVO != null) {
				dmo.caculateTaxBaseAnn(aRecaVO, defaultTaxVO, true, 0);
				dmo.caculateTaxBaseAnn(aRecaVO, defaultTaxVO, true, 1);
			}

			// 其他税率表
			// 其他税率表
			WaclassVO waClass = aRecaVO.getWaClassVO();
			String gzlbId = waClass.getPrimaryKey();
			String waYear = aRecaVO.getWaYear();
			String waPeriod = aRecaVO.getWaPeriod();

			nc.vo.wa.wa_004.TaxbaseVO[] taxTables = getTaxTables(gzlbId, waYear, waPeriod);

			if (taxTables != null && taxTables.length > 0) {
				for (int i = 0; i < taxTables.length; i++) {
					if (taxTables[i] != null && (defaultTaxVO == null || !taxTables[i].getPrimaryKey().equals(defaultTaxVO.getPrimaryKey()))) {
						// 不空，且与默认税率表不一样的，则计算
						dmo.caculateTaxBaseAnn(aRecaVO, taxTables[i], false, 0);
						dmo.caculateTaxBaseAnn(aRecaVO, taxTables[i], false, 1);
					}
				}
			}

			dmo.caculateMonthAvg(aRecaVO);
		} catch (Exception e) {
			reportException(e);
			if (e.getMessage() == null || e.getMessage().trim().length() < 1) {
				e = new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000006")
				/* * @res * "读取数据时出错，请刷新后再试！！" */);
			}
			throw e;
		}
	}

	/**
	 *
	 *
	 * 计算应缴税款，同时将数据保存到wa_tax中 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void caculateTaxByTaxBase(ReCacuVO aRecaVO, nc.vo.wa.wa_004.TaxbaseVO aTaxbaseVO, boolean isDefault) throws Exception {

		try {
			DataDMO dmo = new DataDMO();

			switch (aTaxbaseVO.getItbltype().intValue()) // 固定税率
			{
			case 0:
				// modify by guoad:修改以前代缴税分支永远走不到，造成固定税率的代缴税也按照代扣税计算了

				/**
				 * <p>
				 * Modified by: zhangg on 2007-11-12
				 * <p>
				 * Reason:
				 *
				 * 固定税率表的计算规则： 假设： 固定税率表中的 固定税率＝R(rate) 扣除费用比例＝P(percentage)
				 * 最低费用扣除＝L(lowest derate fee) 费用扣除标准＝S(standard derate free)
				 *
				 * 薪资发放（个人所得税）中的 本次计税基数＝B(base) 减费用额＝D(derate fee)
				 * 应纳税所得额＝I(income amount)
				 *
				 * 1， 如果 B >=S 则 D=B*P, I=B-D 2， 如果 S>B>L 则 D=L, I=B-D 3， 如果B<=L
				 * 则 D=L, I=0
				 *
				 * 由于上面条件的存在， 可见固定税率表中的S*P应该大于等于L
				 *
				 */
				if ("2".equals(aRecaVO.getWaClassVO().getItaxsetting().toString())) {
					// 分三种情况
					// 收入额合计 〉= 代缴税费用扣除标准
					dmo.caculateTax_fixRate(aTaxbaseVO, aRecaVO, 3, isDefault);
					// 代缴税费用扣除标准 〉 收入额合计 〉 最低扣除标准
					dmo.caculateTax_fixRate(aTaxbaseVO, aRecaVO, 4, isDefault);
					// 收入额合计 〈= 最低扣除标准
					dmo.caculateTax_fixRate(aTaxbaseVO, aRecaVO, 5, isDefault);
				} else { // 代扣税
					// 分三种情况
					// 扣税基数 〉= 费用扣除标准
					dmo.caculateTax_fixRate(aTaxbaseVO, aRecaVO, 0, isDefault);
					// 费用扣除标准 〉 扣税基数 〉 最低扣除标准
					dmo.caculateTax_fixRate(aTaxbaseVO, aRecaVO, 1, isDefault);
					// 扣税基数 〈 = 最低扣除标准
					dmo.caculateTax_fixRate(aTaxbaseVO, aRecaVO, 2, isDefault);
				}
				break;
			case 1: // 变动税率
				nc.impl.wa.wa_004.TaxtableDMO taxTableDMO = new nc.impl.wa.wa_004.TaxtableDMO();
				nc.vo.wa.wa_004.TaxtableVO[] taxTableVO = taxTableDMO.queryByPKTaxbase(aTaxbaseVO.getPrimaryKey());

				// Modified by: zhangg on 2007-7-23 <p>Reason: 修改了扣税的运算规则。 提高效率
				if (taxTableVO != null && taxTableVO.length > 0) {
					// 1,将需要计算的人员的税全部清0
					dmo.caculateTaxToZero(aTaxbaseVO, aRecaVO, isDefault);
					// 2,按照税率表进行计税运算
					dmo.caculateTax(aTaxbaseVO, aRecaVO, isDefault);
					// 3,同步扣税表wa_tax
					dmo.updateWa_tax(aTaxbaseVO, aRecaVO, isDefault);
				}

				/*
				 * //达不到扣税标准 dmo.caculateTax_varryRate(aTaxbaseVO, null,
				 * aRecaVO, isDefault); if (taxTableVO != null &&
				 * taxTableVO.length > 0) { for (int j = 0; j <
				 * taxTableVO.length; j++) { //long sTime =
				 * System.currentTimeMillis();
				 * dmo.caculateTax_varryRate(aTaxbaseVO, taxTableVO[j], aRecaVO,
				 * isDefault); //nc.bs.logging.Logger.error("重新计算：3: //
				 * 耗时"+(System.currentTimeMillis() - sTime)+ "ms "); } }
				 */
				break;
			case 2: // 个人劳务报酬所得税率

			}
		} catch (Exception e) {
			reportException(e);
			if (e.getMessage() == null || e.getMessage().trim().length() < 1) {
				e = new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000005")/*
				 * @res
				 * "计税时出错，请重新检查税率表！"
				 */);
			}
			throw e;
		}

	}

	/**
	 * 将数据复制到wa_dataz,wa_dataf中 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void copyToDataZandDataF(ReCacuVO aRecaVO) throws Exception {
		try {
			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			// String gzlbId = waClassVO.getPrimaryKey();

			// String waYear = aRecaVO.getWaYear();
			// String waPeriod = aRecaVO.getWaPeriod();

			DataDMO dmo = new DataDMO();

			// 复制
			String bzPk = waClassVO.getCurrid();
			String rateY = "1";
			String rateF = "1";
			String opratorY = "*";
			String opratorF = "*";

			boolean isSingleMain = aRecaVO.getCurrentBO().isSingleMain();
			boolean isZhuBi = aRecaVO.getCurrentBO().isZhuBi();
			boolean isFuBi = false;

			if (!isZhuBi) {
				rateY = aRecaVO.getCurrentBO().getRate(bzPk).toString();
				opratorY = aRecaVO.getCurrentBO().getOperator(bzPk);
			}

			if (!isSingleMain) {
				// if (!isZhuBi)
				{
					isFuBi = aRecaVO.getCurrentBO().isFuBi();
					rateF = aRecaVO.getCurrentBO().getRate(aRecaVO.getCurrentBO().getFuCurrPK()).toString();
					opratorF = aRecaVO.getCurrentBO().getOperator(aRecaVO.getCurrentBO().getFuCurrPK());
				}
			}

			String[] digitItemRefWithCurr = aRecaVO.getDigitItemRefWithCurr(); // 与币种有关
			String[] digitItemRefWithoutCurr = aRecaVO.getDigitItemRefWithoutCurr();
			// 与币种无关

			// 去掉已扣税
			digitItemRefWithCurr = (String[]) Convertor

			.removeObjectFromArray(digitItemRefWithCurr, null, "f_8");
			// 去掉已扣税基数
			digitItemRefWithCurr = (String[]) Convertor.removeObjectFromArray(digitItemRefWithCurr, null, "f_9");
			// 去掉本次扣税
			digitItemRefWithCurr = (String[]) Convertor.removeObjectFromArray(digitItemRefWithCurr, null, "f_4");
			// 去掉本次扣税基数
			digitItemRefWithCurr = (String[]) Convertor.removeObjectFromArray(digitItemRefWithCurr, null, "f_5");

			dmo.updateFromAnotherTable(// 与币种无关
					"wa_data", "wa_dataz", digitItemRefWithoutCurr, "1", aRecaVO, "*");

			if (isSingleMain || isZhuBi) {
				// 单主币 或 主币
				// 更新主币
				if (isZhuBi) {
					rateY = "1";
					opratorY = "*";
				}
				dmo.updateFromAnotherTable("wa_data", "wa_dataz", digitItemRefWithCurr, rateY, aRecaVO, opratorY);
				dmo.updateFromAnotherTableWithout("wa_data", "wa_dataz", digitItemRefWithoutCurr, "1", aRecaVO, "*");
			}
			// 主辅币核算，同时原币不是主币
			if (!isSingleMain && !isZhuBi) { // 非（单主币）
				if (!isZhuBi) {
					if (isFuBi) {
						rateY = "1";
						opratorY = "*";
					}
					// 更新辅币
					dmo.updateFromAnotherTable("wa_data", "wa_dataf", digitItemRefWithCurr, rateY, aRecaVO, opratorY);
					dmo.updateFromAnotherTableWithout("wa_data", "wa_dataf", digitItemRefWithoutCurr, "1", aRecaVO, "*");

					// 原币非主币
					// 更新主币
					dmo.updateFromAnotherTable("wa_dataf", "wa_dataz", digitItemRefWithCurr, rateF, aRecaVO, opratorF);
					dmo.updateFromAnotherTableWithout("wa_dataf", "wa_dataz", digitItemRefWithoutCurr, "1", aRecaVO, "*");
				} else {
					opratorF = aRecaVO.getCurrentBO().getOperatorReverse(aRecaVO.getCurrentBO().getFuCurrPK());
					// 更新辅币
					dmo.updateFromAnotherTable("wa_data", "wa_dataf", digitItemRefWithCurr, rateF, aRecaVO, opratorF);
					dmo.updateFromAnotherTableWithout("wa_data", "wa_dataf", digitItemRefWithoutCurr, "1", aRecaVO, "*");
				}
			}
			// nc.bs.logging.Logger.error("updateFromAnotherTable end!");
		} catch (Exception e) {
			reportException(e);
			throw e;
		}
	}

	/**
	 * 得到所有合并计税薪资类别的项目 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private String[] getAllUnitDigitItem(WaclassVO[] allWaCalssVO, String waYear, String waPeriod) throws Exception {
		try {
			Vector<String> vecItem = new Vector<String>();

			nc.vo.wa.wa_002.ClassitemVO[] classItemVO = new nc.vo.wa.wa_002.ClassitemVO[allWaCalssVO.length];
			// modified by lhp 2001.12.10
			nc.impl.wa.wa_002.ClassitemDMO classitemdmo = new nc.impl.wa.wa_002.ClassitemDMO();
			for (WaclassVO element : allWaCalssVO) {
				nc.vo.wa.wa_002.ClassitemVO sqlVO = new nc.vo.wa.wa_002.ClassitemVO();
				sqlVO.setPk_wa_class(element.getPrimaryKey());
				sqlVO.setIsealflag(new Integer(0));
				/*
				 * classItemVO =
				 * nc.impl.wa.wa_002.ClassitemImpl_Client.queryByVO(sqlVO, new
				 * Boolean(true));
				 */
				classItemVO = classitemdmo.queryByVO(sqlVO, new Boolean(true));
				for (int j = 0; j < classItemVO.length; j++) {
					if (!vecItem.contains(classItemVO[j].getPk_wa_item())) {
						vecItem.addElement(classItemVO[j].getPk_wa_item());
					}
				}
			}

			Vector<String> vecDigitItem = new Vector<String>();
			nc.impl.wa.wa_024.ItemDMO itemDmo = new nc.impl.wa.wa_024.ItemDMO();

			for (int i = 0; i < vecItem.size(); i++) {
				nc.vo.wa.wa_024.ItemVO itemVO = itemDmo.findByPrimaryKey(vecItem.elementAt(i).toString());
				if (itemVO.getIitemtype().intValue() == 1) // 数字型
				{
					vecDigitItem.addElement("f_" + itemVO.getIitemid().toString());
				}
			}
			String[] re = new String[vecDigitItem.size()];
			vecDigitItem.copyInto(re);
			return re;
		} catch (Exception ex) {
			reportException(ex);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000008")/*
			 * @res
			 * "查询参与合并计税的薪资类别的薪资项目时出错!"
			 */);
		}
	}

	/**
	 * 得到所有合并计税薪资类别的项目 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private String[] getAllUnitDigitItemRefWithCurr(WaclassVO[] allWaCalssVO, String waYear, String waPeriod) throws Exception {
		try {
			Vector<String> vecItem = new Vector<String>();

			nc.vo.wa.wa_002.ClassitemVO[] classItemVO = new nc.vo.wa.wa_002.ClassitemVO[allWaCalssVO.length];
			// modified by lhp 2001.12.10
			nc.impl.wa.wa_002.ClassitemDMO classitemdmo = new nc.impl.wa.wa_002.ClassitemDMO();
			for (WaclassVO element : allWaCalssVO) {
				nc.vo.wa.wa_002.ClassitemVO sqlVO = new nc.vo.wa.wa_002.ClassitemVO();
				sqlVO.setPk_wa_class(element.getPrimaryKey());
				sqlVO.setIsealflag(new Integer(0));
				classItemVO = classitemdmo.queryByVO(sqlVO, new Boolean(true));
				for (int j = 0; j < classItemVO.length; j++) {
					if (!vecItem.contains(classItemVO[j].getPk_wa_item())) {
						vecItem.addElement(classItemVO[j].getPk_wa_item());
					}
				}
			}

			Vector<String> vecDigitItem = new Vector<String>();
			nc.impl.wa.wa_024.ItemDMO itemDmo = new nc.impl.wa.wa_024.ItemDMO();

			for (int i = 0; i < vecItem.size(); i++) {
				nc.vo.wa.wa_024.ItemVO itemVO = itemDmo.findByPrimaryKey(vecItem.elementAt(i).toString());
				if (itemVO.getIitemtype().intValue() == 1 && itemVO.getIproperty().intValue() != 3) // 数字型
				{
					vecDigitItem.addElement("f_" + itemVO.getIitemid().toString());
				}
			}
			String[] re = new String[vecDigitItem.size()];
			vecDigitItem.copyInto(re);
			return re;
		} catch (Exception ex) {
			reportException(ex);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000008")/*
			 * @res
			 * "查询参与合并计税的薪资类别的薪资项目时出错!"
			 */);
		}
	}

	/**
	 * 得到所有合并计税薪资类别的项目 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private String[] getAllUnitDigitItemRefWithoutCurr(WaclassVO[] allWaCalssVO, String waYear, String waPeriod) throws Exception {
		try {
			Vector<String> vecItem = new Vector<String>();

			nc.vo.wa.wa_002.ClassitemVO[] classItemVO = new nc.vo.wa.wa_002.ClassitemVO[allWaCalssVO.length];
			// modified by lhp 2001.12.10
			nc.impl.wa.wa_002.ClassitemDMO classitemdmo = new nc.impl.wa.wa_002.ClassitemDMO();
			for (WaclassVO element : allWaCalssVO) {
				nc.vo.wa.wa_002.ClassitemVO sqlVO = new nc.vo.wa.wa_002.ClassitemVO();
				sqlVO.setPk_wa_class(element.getPrimaryKey());
				sqlVO.setIsealflag(new Integer(0));
				classItemVO = classitemdmo.queryByVO(sqlVO, new Boolean(true));
				for (int j = 0; j < classItemVO.length; j++) {
					if (!vecItem.contains(classItemVO[j].getPk_wa_item())) {
						vecItem.addElement(classItemVO[j].getPk_wa_item());
					}
				}
			}

			Vector<String> vecDigitItem = new Vector<String>();
			nc.impl.wa.wa_024.ItemDMO itemDmo = new nc.impl.wa.wa_024.ItemDMO();

			for (int i = 0; i < vecItem.size(); i++) {
				nc.vo.wa.wa_024.ItemVO itemVO = itemDmo.findByPrimaryKey(vecItem.elementAt(i).toString());
				if (itemVO.getIitemtype().intValue() == 1 && itemVO.getIproperty().intValue() == 3) // 数字型
				{
					vecDigitItem.addElement("f_" + itemVO.getIitemid().toString());
				}
			}
			String[] re = new String[vecDigitItem.size()];
			vecDigitItem.copyInto(re);
			return re;
		} catch (Exception ex) {
			reportException(ex);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000008")/*
			 * @res
			 * "查询参与合并计税的薪资类别的薪资项目时出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#getAllUnitWaClass(nc.vo.wa.wa_008.WaclassVO,
	 *      int)
	 */
	public WaclassVO[] getAllUnitWaClass(WaclassVO waClassVO, int unitType) throws nc.vo.pub.BusinessException {

		WaclassVO[] reVO = null;

		try {
			WaclassDMO unitctgDMO = new WaclassDMO();
			nc.vo.wa.wa_008.UnitctgVO unitctgVO = new nc.vo.wa.wa_008.UnitctgVO();
			unitctgVO.setClassid(waClassVO.getPrimaryKey());
			unitctgVO.setIunittype(new Integer(unitType));
			nc.vo.wa.wa_008.UnitctgVO[] allUnitVO = unitctgDMO.queryByVOUnit(unitctgVO, new Boolean(true));

			if (allUnitVO == null || allUnitVO.length < 1) {
				return null;
			}

			reVO = new WaclassVO[allUnitVO.length];
			SuperDMO waDmo = new SuperDMO();

			for (int i = 0; i < allUnitVO.length; i++) {
				reVO[i] = (WaclassVO) waDmo.queryByPrimaryKey(WaclassVO.class, allUnitVO[i].getClassedid());
			}

		} catch (Exception ex) {
			reportException(ex);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000009")/*
			 * @res
			 * "查询参与合并计税的薪资类别时出错!"
			 */);
		}
		return reVO;
	}

	/**
	 * 得到薪资类别的审核状态。 创建日期：(2001-8-28 19:48:43)
	 *
	 * @return int
	 * @param waClassPk
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 */
	private int getcheckstate(String waClassPk, String waYear, String waPeriod) throws Exception {
		int re = -5;

		try {

			re = PubDelegator.getIGlobaldata().checkState(waClassPk, waYear, waPeriod);

		} catch (Exception ex) {
			reportException(ex);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000010")/*
			 * @res
			 * "查询薪资类别的审核状态时出错!"
			 */);
		}

		return re;
	}

	/**
	 * 根据固定税率计税 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private TaxVO getFixTax(WaclassVO waClassVO, nc.vo.wa.wa_004.TaxbaseVO taxBaseVO, UFDouble taxBase) throws Exception {

		UFDouble tax = new UFDouble(0);
		TaxVO re = new TaxVO();
		try {
			// 最低扣除数, 减费用额
			UFDouble lowestRemoveValue = taxBaseVO.getNdebuctlowest();

			// 扣除比例
			UFDouble debRate = taxBaseVO.getNdebuctrate();
			debRate = debRate.multiply(0.01);

			// 取大值
			UFDouble removeValue = (lowestRemoveValue.compareTo(taxBase.multiply(debRate, -9))) >= 0 ? lowestRemoveValue : taxBase.multiply(debRate, -9);

			// 减费用额
			re.setNdebuctamount(removeValue);

			// 扣税比例
			UFDouble fixRate = taxBaseVO.getNfixrate();

			re.setNtaxrate(fixRate);

			fixRate = fixRate.multiply(0.01);

			// 应纳税收入,主币
			tax = taxBase.sub(removeValue);

			// 与零比较
			tax = (tax.compareTo(new UFDouble(0))) >= 0 ? tax : (new UFDouble(0));

			re.setFtaxmny(tax);// 主币

			// 应纳税
			tax = tax.multiply(fixRate, -9);

			re.setFtaxz(tax);

			// 速算扣除数
			re.setNquickdebuct(new UFDouble(0));
		} catch (Exception e) {
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000011")/*
			 * @res
			 * "根据固定税率表计税时出错!"
			 */);
		}
		return re;
	}

	/**
	 * /** * 假期结算方式 *
	 *
	 * @param 参数说明 *
	 * @return 返回值 *
	 * @exception 异常描述 *
	 * @see 需要参见的其它内容 *
	 * @since 从类的那一个版本，此方法被添加进来。（可选） *
	 * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选） *-/
	 *
	 * @return boolean
	 */
	// 0:过期作废；1：转下年；2：转薪资
	private int getLeaveProType(String combound) {
		try {
			String pk_timeitem = combound.substring("LEAVE->".length());
			// //取系统参数
			// nc.bs.pub.para.SysInitDMO dmo = new nc.bs.pub.para.SysInitDMO();
			// nc.impl.tbm.tbm_004.TimeitemDMO dmo = new
			// nc.impl.tbm.tbm_004.TimeitemDMO();

			DataDMO dmo = new DataDMO();
			nc.vo.tbm.tbm_004.TimeitemVO timeitem = dmo.findByPrimaryKey(pk_timeitem);
			Integer integer = timeitem.getLeavesettlement();
			return integer.intValue();
		} catch (Exception e) {
			// 出错认为无关
			reportException(e);
			return 0;// 默认过期作废
		}
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-6-10 13:00:14)
	 *
	 * @return java.lang.String[][][]
	 * @param tbmItems
	 *                java.lang.String[][]
	 */
	private String[][][] getSpecialTbmItem(String[][] tbmItems, String pk_corp) {
		Vector<String[]> vecBase = new Vector<String[]>();
		Vector<String[]> vecSpecial = new Vector<String[]>();
		Vector<String[]> vecwork = new Vector<String[]>();
		Vector<String[]> vecorther = new Vector<String[]>();
		Vector<String[]> veuserDefinedItem = new Vector<String[]>();

		for (String[] tbmItem : tbmItems) {

			if (tbmItem[1].startsWith("MONTHITEM")) {// 出勤
				veuserDefinedItem.addElement(new String[] { tbmItem[0], tbmItem[1].substring(10) });
			} else if (tbmItem[1].startsWith("BC->")) {// 出勤
				vecwork.addElement(tbmItem);
			} else if (tbmItem[1].length() == 20)// special
			{
				vecSpecial.addElement(tbmItem);
			} else if (tbmItem[1].startsWith("LEAVE->") && getLeaveProType(tbmItem[1]) == 2)// special，应该再加一个条件--判断假期结算方式
			{// 要是假期结算不是折算成薪资，则不应该处理此公式!!!!!
				vecSpecial.addElement(tbmItem);
			} else if (tbmItem[1].startsWith("valueOf")) {// 统计函数
				vecorther.addElement(tbmItem);
			} else {// 基本
				if (tbmItem[1].startsWith("LEAVE->")) {

				} else {
					vecBase.addElement(tbmItem);
				}
			}

		}

		String[][][] str = new String[5][][];

		str[0] = new String[vecBase.size()][];

		for (int i = 0; i < vecBase.size(); i++) {
			str[0][i] = vecBase.elementAt(i);
		}

		str[1] = new String[vecSpecial.size()][];

		for (int i = 0; i < vecSpecial.size(); i++) {
			str[1][i] = vecSpecial.elementAt(i);
		}
		str[2] = new String[vecwork.size()][];

		for (int i = 0; i < vecwork.size(); i++) {
			str[2][i] = vecwork.elementAt(i);
		}
		str[3] = new String[vecorther.size()][];

		for (int i = 0; i < vecorther.size(); i++) {
			str[3][i] = vecorther.elementAt(i);
		}

		str[4] = new String[veuserDefinedItem.size()][];

		for (int i = 0; i < veuserDefinedItem.size(); i++) {
			str[4][i] = veuserDefinedItem.elementAt(i);
		}

		return str;
	}

	/**
	 * 根据税率表计税 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private TaxVO getTaxFromTaxTbl(WaclassVO waClassVO, nc.vo.wa.wa_004.TaxbaseVO taxBaseVO, UFDouble taxBase) throws Exception {

		UFDouble tax = new UFDouble(0);
		TaxVO re = new TaxVO();

		try {
			// 减费用额
			UFDouble removeValue = taxBaseVO.getNdebuctamount();
			re.setNdebuctamount(removeValue);

			// 应纳税所得额, 主币
			UFDouble shouldBeTaxed = taxBase.sub(removeValue);
			shouldBeTaxed = shouldBeTaxed.compareTo(new UFDouble(0)) >= 0 ? shouldBeTaxed : new UFDouble(0);
			re.setFtaxmny(shouldBeTaxed);

			// 税率
			re.setNtaxrate(new UFDouble(0));
			// 速算扣除数
			re.setNquickdebuct(new UFDouble(0));
			// 应缴税款
			re.setFtaxz(new UFDouble(0));

			if ((shouldBeTaxed.compareTo(tax)) > 0) {
				nc.impl.wa.wa_004.TaxtableDMO taxTableDMO = new nc.impl.wa.wa_004.TaxtableDMO();
				nc.vo.wa.wa_004.TaxtableVO[] taxTableVO = taxTableDMO.queryByPKTaxbase(taxBaseVO.getPrimaryKey());

				if (taxTableVO != null && taxTableVO.length > 0) {
					UFDouble rate = new UFDouble(0);
					UFDouble fastRemove = new UFDouble(0);

					for (TaxtableVO element : taxTableVO) {
						UFDouble min = element.getNminamount();
						UFDouble max = element.getNmaxamount();
						int level = element.getItaxlevel().intValue();

						if (level < taxTableVO.length)// 非末级
						{
							if ((shouldBeTaxed.compareTo(min) > 0) && (shouldBeTaxed.compareTo(max) <= 0)) {
								rate = element.getNtaxrate();
								fastRemove = element.getNquickdebuct();
								break;
							}
						} else// 末级
						{
							if (shouldBeTaxed.compareTo(min) > 0) {
								rate = element.getNtaxrate();
								fastRemove = element.getNquickdebuct();
								break;
							}
						}
					}
					// 税率
					re.setNtaxrate(rate);
					// 速算扣除数
					re.setNquickdebuct(fastRemove);

					rate = rate.multiply(new UFDouble(0.01));
					tax = (shouldBeTaxed.multiply(rate)).sub(fastRemove);
					tax = tax.compareTo(new UFDouble(0)) >= 0 ? tax : new UFDouble(0);

					// 所得税，主币
					re.setFtaxz(tax);
				}
			}
		} catch (Exception e) {
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000012")/*
			 * @res
			 * "根据税率表计税时出错!"
			 */);
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#getTaxTables(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public nc.vo.wa.wa_004.TaxbaseVO[] getTaxTables(String waClassPk, String waYear, String waPeriod) throws nc.vo.pub.BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			String[] taxTablePks = dmo.getAllTaxTablePk(waClassPk, waYear, waPeriod);

			if (taxTablePks == null || taxTablePks.length < 1) {
				return null;
			}
			Vector<TaxbaseVO> vecTaxTable = new Vector<TaxbaseVO>();

			TaxbaseDMO taxBaseDmo = new TaxbaseDMO();

			for (String taxTablePk : taxTablePks) {
				vecTaxTable.addElement(taxBaseDmo.findByPrimaryKey(taxTablePk));
			}

			nc.vo.wa.wa_004.TaxbaseVO[] taxTables = new nc.vo.wa.wa_004.TaxbaseVO[vecTaxTable.size()];
			vecTaxTable.copyInto(taxTables);

			return taxTables;
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000013")/*
			 * @res
			 * "查找税率表失败，请检查税率表设置!"
			 */);
		}
	}

	/**
	 * 计税 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private TaxVO getTaxValue(WaclassVO waClassVO, String taxTblId, UFDouble taxBase) throws Exception {

		/** ********************************************************** */
		// 保留的系统管理接口：
		TaxVO re = new TaxVO();
		try {
			nc.impl.wa.wa_004.TaxbaseDMO taxBaseDMO = new nc.impl.wa.wa_004.TaxbaseDMO();
			nc.vo.wa.wa_004.TaxbaseVO taxBaseVO = taxBaseDMO.findByPrimaryKey(taxTblId);

			if (taxBaseVO.getItbltype().intValue() == 0)// 固定税率
			{
				re = getFixTax(waClassVO, taxBaseVO, taxBase);
			} else {
				re = getTaxFromTaxTbl(waClassVO, taxBaseVO, taxBase);
			}
		} catch (Exception e) {
			reportException(e);
			throw e;
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#getTbmScale(java.lang.String)
	 */
	public int getTbmScale(String pk_corp) throws nc.vo.pub.BusinessException {
		try {
			nc.vo.tbm.tbm_005.TimeruleVO l_TimeruleVOCond = new nc.vo.tbm.tbm_005.TimeruleVO();

			l_TimeruleVOCond.setPk_corp(pk_corp);
			l_TimeruleVOCond.setDr(new Integer(0));

			// nc.impl.tbm.tbm_005.TimeruleDMO aTimeruleDMO =
			// new nc.impl.tbm.tbm_005.TimeruleDMO();
			// nc.vo.tbm.tbm_005.TimeruleVO[] l_TimeruleVO =
			// aTimeruleDMO.queryByVO(l_TimeruleVOCond, new Boolean(true));
			// nc.impl.agent.wa.WaBmAgentHome home
			// =(nc.impl.agent.wa.WaBmAgentHome)getBeanHome(nc.impl.agent.wa.WaBmAgentHome.class,"nc.impl.agent.wa.WaBmAgentImpl");
			// nc.impl.agent.wa.WaBmAgent bo=home.create();

			nc.vo.tbm.tbm_005.TimeruleVO[] l_TimeruleVO = nc.impl.wa.agent.WaBmAgentImpl.queryByVO(l_TimeruleVOCond, new Boolean(true));

			// edit by bsj, 如果没有考勤数据，也就是没有返回任何VO，则返回0
			if (l_TimeruleVO != null & l_TimeruleVO.length > 0) {
				int m_intRuleScale = l_TimeruleVO[0].getTimedecimal().intValue();
				return m_intRuleScale;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000014")/*
			 * @res
			 * "取得考勤数据的精度时出错！"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#getUnitTaxWaPks(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String[] getUnitTaxWaPks(String pk_waclass, String accYear, String accPeriod) throws nc.vo.pub.BusinessException {

		String[] unitTaxWaPks = null;
		String[] unitTaxWaPks_waPeriod = null;
		try {
			nc.impl.wa.wa_001_03.TaxgroupDMO tgDmo = new nc.impl.wa.wa_001_03.TaxgroupDMO();
			unitTaxWaPks = tgDmo.findItemsByMemberPk(pk_waclass);
			if (unitTaxWaPks != null && unitTaxWaPks.length > 0) {
				PeriodDMO pDmo = new PeriodDMO();
				unitTaxWaPks_waPeriod = pDmo.queryAllByClassIdAndAccPeriod(unitTaxWaPks, accYear, accPeriod);
			}
		} catch (Exception ex) {
			reportException(ex);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000009")/*
			 * @res
			 * "查询参与合并计税的薪资类别时出错!"
			 */, ex);
		}
		return unitTaxWaPks_waPeriod;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#havePsnNotCheck(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean havePsnNotCheck(String gzlbId, String waYear, String waPeriod, String swhere, boolean isrecheck) throws nc.vo.pub.BusinessException {
		boolean re = false;
		try {
			DataDMO dmo = new DataDMO();
			re = dmo.havePsnNotCheck(gzlbId, waYear, waPeriod, swhere, isrecheck);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::havePsnNotCheck() Exception!");
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#havePsnNotRecacu(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean havePsnNotRecacu(String gzlbId, String waYear, String waPeriod) throws nc.vo.pub.BusinessException {

		try {

			DataDMO dmo = new DataDMO();
			return dmo.havePsnNotRecacu(gzlbId, waYear, waPeriod);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000015")/*
			 * @res
			 * "判断是否所有人都计算完毕时出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#havePsnNotRecacu(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean havePsnNotRecacu(String gzlbId, String waYear, String waPeriod, String swhere) throws nc.vo.pub.BusinessException {

		try {

			DataDMO dmo = new DataDMO();
			return dmo.havePsnNotRecacu(gzlbId, waYear, waPeriod, swhere);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000015")/*
			 * @res
			 * "判断是否所有人都计算完毕时出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#insert(nc.vo.wa.wa_009.DataVO,
	 *      nc.vo.wa.wa_009.ReCacuVO)
	 */
	public String insertWaDataX(DataVO data, ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		try {
			DataDMO dmo = new DataDMO();
			String key = dmo.insert(data, aRecaVO);

			data.setPk_wa_data(key);

			dmo.insertDataZorDataY("wa_dataz", data);

			if (!aRecaVO.getCurrentBO().isSingleMain() && !aRecaVO.getCurrentBO().isZhuBi())// 主辅币
			{
				dmo.insertDataZorDataY("wa_dataf", data);
			}

			insertWa_Tax(new int[] { data.getIstopFlag().intValue() }, data.getClassid(), data.getCyear(), data.getCperiod(), new String[] { data.getPsnid() });

			return key;
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::insert(DataVO) Exception!");
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#insertArray(nc.vo.wa.wa_009.DataVO[],
	 *      nc.vo.wa.wa_009.ReCacuVO)
	 */
	public String[] insertWaDataXArray(DataVO[] datas, ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		try {
			DataDMO dmo = new DataDMO();
			String key[] = dmo.insertArray(datas, aRecaVO);

			for (int i = 0; i < datas.length; i++) {
				datas[i].setPk_wa_data(key[i]);
			}

			dmo.insertDataZorDataYArray("wa_dataz", datas);

			if (aRecaVO.getCurrentBO() == null) {
				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000016")/*
				 * @res
				 * "不能正确读取币种的信息，请检查！"
				 */);
			}

			// if (!aRecaVO.getCurrentBO().isSingleMain() &&
			// !aRecaVO.getCurrentBO().isZhuBi())//主辅币
			if (!aRecaVO.getCurrentBO().isSingleMain() && !aRecaVO.getCurrentBO().isZhuBi())// 主辅币
			{
				dmo.insertDataZorDataYArray("wa_dataf", datas);
			}

			/*
			 * int[] stopFlag=new int[datas.length]; String[] psnIds=new
			 * String[datas.length];
			 *
			 * for(int i=0;i <datas.length;i++) {
			 * stopFlag[i]=datas[i].getIstopFlag().intValue();
			 * psnIds[i]=datas[i].getPsnid(); }
			 */
			if (datas.length > 0) {
				nc.impl.wa.wa_013.TaxDMO taxDmo = new nc.impl.wa.wa_013.TaxDMO();
				taxDmo.insertArrayFromData(datas);
				// insertWa_Tax(stopFlag,datas[0].getClassid(),datas[0].getCyear(),datas[0].getCperiod(),psnIds);
			}
			return key;
		} catch (Exception e) {
			reportException(e);

			if (e.getMessage() == null) {
				throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000017")/*
				 * @res
				 * "保存失败，请刷新后再试！"
				 */);
			} else {
				throw new nc.vo.pub.BusinessException(e.getMessage());
			}
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#insertArrayForJz(nc.vo.wa.wa_009.DataVO[],
	 *      nc.vo.wa.wa_009.ReCacuVO)
	 */
	public String[] insertArrayForJz(DataVO[] datas, ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		try {
			DataDMO dmo = new DataDMO();
			String key[] = dmo.insertArray(datas, aRecaVO);

			/*
			 * for(int i=0;i <datas.length;i++) {
			 * datas[i].setPk_wa_data(key[i]); }
			 */

			dmo.insertDataZorDataYArray("wa_dataz", datas[0].getClassid(), datas[0].getCyear(), datas[0].getCperiod());

			if (aRecaVO.getCurrentBO() == null) {
				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000016")/*
				 * @res
				 * "不能正确读取币种的信息，请检查！"
				 */);
			}

			// if (!aRecaVO.getCurrentBO().isSingleMain() &&
			// !aRecaVO.getCurrentBO().isZhuBi())//主辅币
			if (!aRecaVO.getCurrentBO().isSingleMain() && !aRecaVO.getCurrentBO().isZhuBi())// 主辅币
			{
				dmo.insertDataZorDataYArray("wa_dataf", datas[0].getClassid(), datas[0].getCyear(), datas[0].getCperiod());
				// dmo.insertDataZorDataYArray("wa_dataf",datas);
			}

			if (datas.length > 0) {
				nc.impl.wa.wa_013.TaxDMO taxDmo = new nc.impl.wa.wa_013.TaxDMO();
				taxDmo.insertArrayFromData(datas);
				// insertWa_Tax(stopFlag,datas[0].getClassid(),datas[0].getCyear(),datas[0].getCperiod(),psnIds);
			}
			return key;
		} catch (Exception e) {
			reportException(e);

			if (e.getMessage() == null) {
				throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000017")/*
				 * @res
				 * "保存失败，请刷新后再试！"
				 */);
			} else {
				throw new nc.vo.pub.BusinessException(e.getMessage());
			}
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#insertArrayPsn(nc.vo.wa.wa_016.PsnVO[],
	 *      java.lang.String, nc.vo.wa.wa_009.ReCacuVO)
	 */
	public String[] insertArrayPsn(PsnVO[] psns, String userId, ReCacuVO aRecacuVO) throws nc.vo.pub.BusinessException {

		boolean bankLocked = false;
		boolean taxTableLocked = false;

		String bankId = psns[0].getBankid();
		String taxTableId = psns[0].getTaxtableid();

		try {
			int checkState = getcheckstate(psns[0].getClassid(), psns[0].getCyear(), psns[0].getCperiod());

			if (checkState == -2) {
				return new String[] { "-6" };
			} else if (checkState > 0) {
				return new String[] { "-7" };
			} else if (checkState == -5) {
				return new String[] { "-8" };
			}
			if (userId != null && userId.trim().length() > 0) {

				if (bankId != null && bankId.trim().length() > 0) {
					if (nc.bs.uap.lock.PKLock.getInstance().acquireLock(bankId, userId, null)) {
						bankLocked = true;
					}
				}

				if (taxTableId != null && taxTableId.trim().length() > 0) {
					if (nc.bs.uap.lock.PKLock.getInstance().acquireLock(taxTableId, userId, null)) {
						taxTableLocked = true;
					}
				}
			}

			PsnDMO dmo = new PsnDMO();
			String[] keys = dmo.insertArray(psns);

			Vector<PsnVO> vecAdd = new Vector<PsnVO>();

			if (keys != null && keys.length > 0) {
				for (int i = 0; i < keys.length; i++) {
					if (keys[i] != null) {
						vecAdd.addElement(psns[i]);
					}
				}

				// 如果已插入纪录

				if (vecAdd.size() > 0) {
					PsnVO[] addPsn = new PsnVO[vecAdd.size()];
					vecAdd.copyInto(addPsn);
					nc.vo.wa.wa_009.DataVO[] dVo = new nc.vo.wa.wa_009.DataVO[vecAdd.size()];
					for (int i = 0; i < addPsn.length; i++) {
						dVo[i] = new nc.vo.wa.wa_009.DataVO();

						dVo[i].setClassid(addPsn[i].getClassid());
						dVo[i].setCperiod(addPsn[i].getCperiod());
						dVo[i].setCyear(addPsn[i].getCyear());
						dVo[i].setDeptid(addPsn[i].getDeptId());
						dVo[i].setPsnid(addPsn[i].getPsnid());
						dVo[i].setPsnclid(addPsn[i].getPsnClId());

						dVo[i].setPkOmJob(addPsn[i].getPkOmJob());
						dVo[i].setNestPkOmJob(addPsn[i].getNestPkOmJob());
						dVo[i].setNestDeptid(addPsn[i].getNestDeptid());
						dVo[i].setNestPsnclid(addPsn[i].getNestPsnclid());

						dVo[i].setPsnbasid(addPsn[i].getPsnbasdocPK());
						if (addPsn[i].getItaxflag().booleanValue()) {
							dVo[i].setItaxflag(new Integer(1));
						} else {
							dVo[i].setItaxflag(new Integer(0));
						}

						if (addPsn[i].getIstopflag().booleanValue()) // 停发
						{
							dVo[i].setIstopFlag(new Integer(1));
						} else {
							dVo[i].setIstopFlag(new Integer(0));
						}
					}

					// 项wa_data表中插入数据
					/*
					 * DataHome dataHome = (DataHome)
					 * getBeanHome(DataHome.class,
					 * "nc.impl.wa.wa_009.DataImpl"); Data dataBo =
					 * dataHome.create();
					 */

					aRecacuVO.setWaYear(dVo[0].getCyear());
					aRecacuVO.setWaPeriod(dVo[0].getCperiod());
					// aRecacuVO.getWaClassVO().setPk_wa_class(dVo[0].getClassid());
					aRecacuVO.setWaClassVO((WaclassVO) (new SuperDMO().queryByPrimaryKey(WaclassVO.class, dVo[0].getClassid())));
					aRecacuVO.setPk_corp(psns[0].getPk_corp());

					insertWaDataXArray(dVo, aRecacuVO);
					// dataBo.remove();

					// 更新薪资期间信息
					nc.vo.wa.wa_019.PeriodVO pVo = new nc.vo.wa.wa_019.PeriodVO();

					pVo.setPk_corp(addPsn[0].getPk_corp());
					pVo.setClassid(addPsn[0].getClassid());
					pVo.setCperiod(addPsn[0].getCperiod());
					pVo.setCyear(addPsn[0].getCyear());

					PeriodDMO pDmo = new PeriodDMO();
					pVo = pDmo.queryByVO(pVo, new Boolean(true))[0];

					pVo.setIrecaculateflag(new Integer(0));
					pVo.setIrecheckflag(new Integer(0));
					pVo.setDaccdate(new UFDate());
					pVo.setIaccountmark(new Integer(0));
					pVo.setIcheckflag(new Integer(0));
					pVo.setIrecaculateflag(new Integer(0));

					pDmo.update(pVo);
				}

			}

			return keys;
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("PsnImpl::insertArray(PsnVO[]) Exception!");
		} finally {
			try {
				if (bankLocked) {
					nc.bs.uap.lock.PKLock.getInstance().releaseLock(bankId, userId, null);
				}
				if (taxTableLocked) {
					nc.bs.uap.lock.PKLock.getInstance().releaseLock(taxTableId, userId, null);
				}

			} catch (Exception e) {
			}

		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#insertWa_Tax(int[], java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[])
	 */
	public void insertWa_Tax(int[] istopflag, String gzlbId, String waYear, String waPeriod, String[] psnId) throws nc.vo.pub.BusinessException {
		try {
			if (istopflag.length > 0) {
				nc.impl.wa.wa_013.TaxDMO taxDmo = new nc.impl.wa.wa_013.TaxDMO();

				nc.vo.wa.wa_013.TaxVO taxVo[] = new nc.vo.wa.wa_013.TaxVO[istopflag.length];

				for (int i = 0; i < istopflag.length; i++) {
					taxVo[i] = new nc.vo.wa.wa_013.TaxVO();
					nc.vo.bd.b06.PsndocVO tmpvo = (PsndocVO) (new SuperDMO()).queryByPrimaryKey(PsndocVO.class, psnId[i]);

					taxVo[i].setPk_wa_class(gzlbId);
					taxVo[i].setVcalyear(waYear);
					taxVo[i].setVcalmonth(waPeriod);
					taxVo[i].setPsnid(psnId[i]);
					taxVo[i].setPsnbasid(tmpvo.getPk_psnbasdoc());
				}
				taxDmo.insertArray(taxVo);
			}

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::updateWa_Tax Exception!");
		}
	}

	/**
	 * 判别是否系统薪资项目。 创建日期：(2001-6-20 17:01:58)
	 *
	 * @return boolean
	 * @param item
	 *                java.lang.String
	 */
	private boolean isSysItem(String item) {
		item = item.trim();

		if (item.equals("f_1") || item.equals("f_2") || item.equals("f_3") || item.equals("f_4") || item.equals("f_5") || item.equals("f_6") || item.equals("f_7") || item.equals("f_8")
				|| item.equals("f_9") || item.equals("f_10") || item.equals("f_11")) {
			return true;
		}
		return false;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCancelPayOff(nc.vo.wa.wa_009.ReCacuVO)
	 */
	public WaclassVO onCancelPayOff(ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {
		try {
			WaclassVO waclassVO = aRecaVO.getWaClassVO();
			String gzlbId = waclassVO.getPrimaryKey();
			String waYear = waclassVO.getCurrentPeriodVO().getCyear();
			String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();

			boolean isbill = new DataImpl().isHaveBill(gzlbId, waYear, waPeriod);
			if (isbill == true) {
				throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000168"));// "该工资类别已经制单，不能取消发放！"

			}

			boolean ifHaveMadeBill = new DatapoolImpl().isHaveDatapoolBill(gzlbId, waYear, waPeriod, "WA");

			if (ifHaveMadeBill) {
				throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000168"));// "该工资类别已经制单，不能取消发放！"
			}

			ifHaveMadeBill = new BankSheetImpl().hasBeanToFip(gzlbId, waYear, waPeriod);
			if (ifHaveMadeBill) {
				throw new BusinessException(ResHelper.getString("6013v57_2","UPP6013v57_2-000022")//@res "已经现金结算, 不能取消发放."
				);//
			}

			// 检查状态
			checkWaClassStateChange(aRecaVO.getWaClassVO(), null);

			PeriodDMO pDmo = new PeriodDMO();
			boolean usedByOther = pDmo.usedByOther(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getWaPeriodvo().getCaccyear(), aRecaVO.getWaPeriodvo().getCaccperiod());
			if (usedByOther) {
				throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000018"));// "该工资类别发放后已有别的类别引用该类别的数据进行了计算，并审核，该类别不能取消发放！"
			}

			nc.vo.wa.wa_019.PeriodVO period = waclassVO.getCurrentPeriodVO();
			if (period != null && (period.getApprovetype().intValue() == 1)) {
				// 更新wa_periodstate表isapproved字段值
				new PayrollImpl().updateClassState(gzlbId, waYear, waPeriod, nc.vo.trade.pub.IBillStatus.NOPASS);
			}

			nc.vo.wa.wa_019.PeriodVO thisPeiordVO = aRecaVO.getWaPeriodvo();
			thisPeiordVO.setIpayoffflag(0);
			thisPeiordVO.setCpaydate(null);
			thisPeiordVO.setCpreclassid(null);
			thisPeiordVO.setVpaycomment(null);

			pDmo.updatePayOffFlagAtPeriod(thisPeiordVO);
			pDmo.updateCalFlagForCancelPayOff(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getWaPeriodvo().getCaccyear(), aRecaVO.getWaPeriodvo().getCaccperiod());

			boolean isInTaxgroup = new TaxgroupDAO().isInTaxGroup(aRecaVO.getWaClassVO().getPrimaryKey());

			/*工资发放后，把工资信息写入奖金津贴税额表，供奖金津贴算税使用，目前暂不考虑5号发工资前还发其他钱的情况。 新华用*/
			if(aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG)
					||aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG_FB)
					||aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG_HT)
					||aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG_HTZB)){
				onWriteSj(aRecaVO,false);
			}
			if (isInTaxgroup) {
				caculateFormuItem_afterTax_zero(aRecaVO);
			}
			
			return getNewWaclassVOWithState(waclassVO, null);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCancelReCheck(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public WaclassVO onCancelReCheck(WaclassVO waclassVO, String checkwhere, String selectCondition) throws nc.vo.pub.BusinessException {

		// 查看类别状态是否发生变化
		checkWaClassStateChange(waclassVO, selectCondition);

		String gzlbId = waclassVO.getPrimaryKey();
		String waYear = waclassVO.getCurrentPeriodVO().getCyear();
		String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();

		// 审批流审批的类别，如果在审批中的单据则不能取消，//如不需要复审且已经增加到审批单据中的，则不能取消审核
		nc.vo.wa.wa_019.PeriodVO period = waclassVO.getCurrentPeriodVO();
		if (period != null && (period.getApprovetype().intValue() == 1)) {
			if (period.getIsapproved().intValue() == 1) {
				throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000307"));// "该薪资类别已经审批通过，无法取消审核！"

			} else {
				Boolean isCanCancel = new PayrollImpl().isCanCancel(gzlbId, waYear, waPeriod);
				if (!isCanCancel.booleanValue()) {
					throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000308"));// "该薪资类别已经加到审批单据中，无法取消审核！"

				}
			}
		}

		String swhere = " wa_data.irecheckflag = 1 and wa_data.istopflag = 0 ";
		if (!StringUtil.isEmpty(checkwhere.trim())) {
			checkwhere = swhere + checkwhere;
		} else {
			checkwhere = swhere;
		}
		try {
			DataDMO dmo = new DataDMO();

			// 先设置wa_data的复审标志
			dmo.updateCheckFlag(gzlbId, waYear, waPeriod, "0", checkwhere, "irecheckflag");

			dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "0" });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BusinessException(e);
		}

		return getNewWaclassVOWithState(waclassVO, selectCondition);
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCheck(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public int onCheck(String gzlbId, String waYear, String waPeriod) throws nc.vo.pub.BusinessException {
		int re = -5;

		try {
			re = getcheckstate(gzlbId, waYear, waPeriod);

			if (re == 0) {
				DataDMO dmo = new DataDMO();
				// 先设置wa_data的审核标志
				dmo.updateCheckFlag(gzlbId, waYear, waPeriod, "1", "", "icheckflag");
				// 再设置wa_period的
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "icheckflag" }, new String[] { "1" });// onCheck(gzlbId,waYear,waPeriod);
			}

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::onCheck() Exception!");
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onPayOff(nc.vo.wa.wa_009.ReCacuVO)
	 */
	public WaclassVO onPayOff(ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		boolean lockPeriod = false;
		nc.vo.wa.wa_019.PeriodVO thisPeiordVO = aRecaVO.getWaPeriodvo();

		try {
			// 检查状态
			checkWaClassStateChange(aRecaVO.getWaClassVO(), null);

			PeriodDMO pDmo = new PeriodDMO();

			nc.vo.wa.wa_019.PeriodVO checkVO = pDmo.findByPrimaryKey(thisPeiordVO.getPrimaryKey());
			if (checkVO == null) {
				throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000019")/* "判断该期间是否已发放时出现错误，请刷新后再试！" */);
			}
			if (checkVO.getIpayoffflag() == 1) {
				throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000020")/*
				 * @res
				 * "该期间数据已发放，请刷新后再试！"
				 */);
			}
			lockPeriod = nc.bs.uap.lock.PKLock.getInstance().acquireLock(thisPeiordVO.getPk_wa_period(), aRecaVO.getUserid(), null);
			if (!lockPeriod) {
				throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000303")/*
				 * @res
				 * "有人正在处理该数据，请稍候再试！"
				 */);
			}

			boolean isInTaxgroup = new TaxgroupDAO().isInTaxGroup(aRecaVO.getWaClassVO().getPrimaryKey());

			if (isInTaxgroup) {
				caculateFormuItem_afterTax(aRecaVO);
			}
			/*工资发放后，把工资信息写入奖金津贴税额表，供奖金津贴算税使用，目前暂不考虑5号发工资前还发其他钱的情况。 新华用*/
			if(aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG)
					||aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG_HT)
					||aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG_FB)
					||aRecaVO.getWaClassVO().getPrimaryKey().equals(IHRPWABtn.PK_GONG_HTZB)){
				onWriteSj(aRecaVO,true);
			}
			thisPeiordVO.setIpayoffflag(1);

			pDmo.updatePayOffFlagAtPeriod(thisPeiordVO);

			return getNewWaclassVOWithState(aRecaVO.getWaClassVO(), null);

		} catch (Exception e) {
			if (e instanceof BusinessException) {
				BusinessException businessException = (BusinessException) e;
				throw businessException;
			}
			reportException(e);
			throw new nc.vo.pub.BusinessException("onPayOff", e);
		} finally {
			if (lockPeriod) {
				nc.bs.uap.lock.PKLock.getInstance().releaseLock(thisPeiordVO.getPk_wa_period(), aRecaVO.getUserid(), null);
			}
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onReCheck(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public WaclassVO onReCheck(WaclassVO waclassVO) throws nc.vo.pub.BusinessException {

		String gzlbId = waclassVO.getPrimaryKey();
		String waYear = waclassVO.getCurrentPeriodVO().getCyear();
		String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();

		checkWaClassStateChange(waclassVO, null);

		try {
			DataDMO dmo = new DataDMO();
			dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "1" });// onReCheck(gzlbId,waYear,waPeriod);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BusinessException(e.getMessage());
		}

		return getNewWaclassVOWithState(waclassVO, null);
	}

	public WaclassVO onReCheck(WaclassVO waclassVO, String datapower, String selectCondtion) throws nc.vo.pub.BusinessException {

		String gzlbId = waclassVO.getPrimaryKey();
		String waYear = waclassVO.getCurrentPeriodVO().getCyear();
		String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();

		// if (datapower == null || datapower.trim().length() == 0) {
		// return onReCheck(waclassVO);
		// }
		// 查看类别状态是否发生变化
		checkWaClassStateChange(waclassVO, datapower);

		String chenckwhere = " wa_data.istopflag = 0 and  wa_data.icheckflag = 1  ";
		datapower = chenckwhere + datapower;

		try {
			DataDMO dmo = new DataDMO();
			// 先设置wa_data的复审标志
			dmo.updateCheckFlag(gzlbId, waYear, waPeriod, "1", datapower, "irecheckflag");
			if (!dmo.havePsnNotCheck(gzlbId, waYear, waPeriod, "", true)) {
				// 已经全部审核完，则设置wa_period的
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "1" });
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BusinessException(e.getMessage());
		}

		return getNewWaclassVOWithState(waclassVO, selectCondtion);

	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#queryAll(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[],
	 *      java.lang.String[])
	 */
	public DataVO[] queryAll(String gzlbId, String waYear, String waPeriod, String[] appendItems, String[] appendItemTypes) throws nc.vo.pub.BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			DataVO[] datas = dmo.queryAll(gzlbId, waYear, waPeriod, appendItems, appendItemTypes);
			return datas;
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::queryAll() Exception!");
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#queryAll(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[],
	 *      java.lang.String[], java.lang.String)
	 */
	public DataVO[] queryAll(String gzlbId, String waYear, String waPeriod, String[] appendItems, String[] appendItemTypes, String sqlWhere) throws nc.vo.pub.BusinessException {
		return queryAll(gzlbId, waYear, waPeriod, appendItems, appendItemTypes, sqlWhere, true);
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#queryAll(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[],
	 *      java.lang.String[], java.lang.String, boolean)
	 */
	public DataVO[] queryAll(String gzlbId, String waYear, String waPeriod, String[] appendItems, String[] appendItemTypes, String sqlWhere, boolean nestIsSelf) throws nc.vo.pub.BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			DataVO[] datas = null;
			if (nestIsSelf) {
				datas = dmo.queryAll(gzlbId, waYear, waPeriod, appendItems, appendItemTypes, sqlWhere);
			} else {
				datas = dmo.queryAll_nest(gzlbId, waYear, waPeriod, appendItems, appendItemTypes, sqlWhere);
			}
			return datas;
		} catch (Exception e) {
			reportException(e);
			throw new BusinessException(e.getMessage());
		}
	}

	// /* （非 Javadoc）
	// * @see nc.impl.wa.wa_009.IRecaData#queryAll(java.lang.String,
	// java.lang.String, java.lang.String, java.lang.String[],
	// java.lang.String[], java.lang.String, boolean)
	// */
	// public PaySlipDataVO[] queryAllForPayslip(String gzlbId, String waYear,
	// String waPeriod,
	// String[] appendItems, String[] appendItemTypes, String sqlWhere,
	// boolean nestIsSelf) throws nc.vo.pub.BusinessException {
	// try {
	// DataDMO dmo = new DataDMO();
	// PaySlipDataVO[] datas = null;
	// // if (nestIsSelf) {
	// // datas = dmo.queryAllForPayslip(gzlbId, waYear, waPeriod, appendItems,
	// // appendItemTypes, sqlWhere);
	// // } else {
	// // datas = dmo.queryAll_nest(gzlbId, waYear, waPeriod,
	// // appendItems, appendItemTypes, sqlWhere);
	// // }
	// return datas;
	// } catch (Exception e) {
	// reportException(e);
	// throw new nc.vo.pub.BusinessException("DataImpl::queryAll() Exception!");
	// }
	// }

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#queryAll_less_stop(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[],
	 *      java.lang.String[], java.lang.String)
	 */
	public DataVO[] queryAll_less_stop(String gzlbId, String waYear, String waPeriod, String[] appendItems, String[] appendItemTypes, String sqlWhere) throws nc.vo.pub.BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			DataVO[] datas = dmo.queryAll_less_stop(gzlbId, waYear, waPeriod, appendItems, appendItemTypes, sqlWhere);
			return datas;
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::queryAll() Exception!");
		}
	}

	/**
	 *
	 * @author zhangg on 2009-8-12
	 * @see nc.itf.hr.wa.IRecaData#reCaculate(nc.vo.wa.wa_009.ReCacuVO,
	 *      nc.vo.wa.wa_009.CaculateInfoVO)
	 */
	public WaclassVO reCaculate(ReCacuVO aRecaVO, CaculateInfoVO caculateInfoVO) throws nc.vo.pub.BusinessException {
		return reCaculate(aRecaVO);
	}

	/**
	 * 判断状态是否发生 变化
	 *
	 * @author zhangg on 2009-8-12
	 * @param waclassVO
	 * @param wa_data_where
	 * @return
	 * @throws BusinessException
	 */
	public void checkWaClassStateChange(WaclassVO waclassVO, String wa_data_where) throws BusinessException {
		WaGlobalVO globalVO = new WaGlobalVO();
		globalVO.setWaClassPK(waclassVO.getPrimaryKey());
		globalVO.setWaYear(waclassVO.getCurrentPeriodVO().getCyear());
		globalVO.setWaPeriod(waclassVO.getCurrentPeriodVO().getCperiod());
		globalVO.setCurUserid(waclassVO.getCurUserid());

		WaClassStateHelper.WaStates oldStates = waclassVO.getCurrentstate();
		WaClassStateHelper.WaStates newStates = WaClassStateHelper.getWaclassVOWithState(globalVO, wa_data_where).getCurrentstate();

		if (!(oldStates == newStates)) {
			if (newStates == WaStates.SELECTED_WA_DATA_ALL_CHECKED_EXIST_RECHECKED) {
				//added for V57 by liangxr 如果类别是部分复审，则选择的部分人员可能是未复审或已复审，认为状态未发生 变化
				if (oldStates == WaStates.SELECTED_WA_DATA_ALL_CHECKED_NONE_RECHECKED ||oldStates == WaStates.SELECTED_WA_DATA_ALL_RECHECKED_CLASS_NONE_RECHECKED ) {
					return;
				}
			}
			//added for V57 by liangxr 如果类别是部分审核，则选择的部分人员可能是未审或已审，认为状态未发生 变化
			if(newStates == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_EXIST_CHECKED){
				if(oldStates == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_NONE_CHECKED){
					return;
				}
			}
			if(newStates == WaStates.SELECTED_WA_DATA_NOT_ALL_RECACULATED_NONE_CHECKED){
				if(oldStates == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_NONE_CHECKED){
					return;
				}
			}
			throw new BusinessException(WaLangUtil.getStrByID("UPP60131004-000405"));// 选择的薪资类别状态发生变化,
			// 请刷新后再试！
		}
	}

	/**
	 * 判断补发
	 *
	 * @author zhangg on 2009-8-12
	 * @param aRecaVO
	 * @throws BusinessException
	 */
	public void checkReData(ReCacuVO aRecaVO) throws BusinessException {
		boolean b;
		try {
			WaclassVO waclassVO = aRecaVO.getWaClassVO();

			String deptpower2 = "";
			String psnclpower2 = "";
			String[] powers = DataPowerUtil.getDataPower(aRecaVO.getUserid(), aRecaVO.getPk_corp(), false);
			String powerSql = powers[0];

			if (powerSql != null && powerSql.length() > 0) {
				deptpower2 = " and wa_redata.deptid in (" + powerSql + ")";
			}

			// 人员类别权限
			powerSql = powers[1];
			if (powerSql != null && powerSql.length() > 0) {
				psnclpower2 = " and wa_redata.psnclid in (" + powerSql + ") ";
			}

			RedataImpl redataImpl = new RedataImpl();
			b = redataImpl.haveMakeRedata(waclassVO.getPrimaryKey(), waclassVO.getCurrentPeriodVO().getCyear(), waclassVO.getCurrentPeriodVO().getCperiod(), deptpower2 + psnclpower2);
		} catch (Exception ex) {
			throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000188"));// "不能确定补发数据是否已传递过来，无法继续，请刷新后再试！"
		}
		if (!b) {
			throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000189"));// "补发数据尚未传递过来，无法继续！"
		}

	}

	/**
	 * 检查薪资项目是否发生了变化
	 *
	 * @throws Exception
	 */
	public void checkItemChange(ReCacuVO aRecaVO) throws BusinessException {
		ClassitemVO[] localClassitemVOs = aRecaVO.getClassitemVOs();
		WaclassVO waclassVO = aRecaVO.getWaClassVO();
		ClassitemVO[] newClassitemVOs = new ClassitemImpl().queryByPKclass(waclassVO.getPrimaryKey(), waclassVO.getCurrentPeriodVO().getCyear(), waclassVO.getCurrentPeriodVO().getCperiod());
		// 薪资项目的个数必须相同
		if (localClassitemVOs != null && newClassitemVOs != null && localClassitemVOs.length == newClassitemVOs.length) {
			HashMap<String, String> newiteMap = new HashMap<String, String>();
			for (ClassitemVO classitemVO : newClassitemVOs) {
				newiteMap.put(classitemVO.getPk_wa_classitem(), classitemVO.getTs());
			}

			for (ClassitemVO classitemVO : localClassitemVOs) {// 当项目不存在或者项目的TS发生变化时，
				// 就认为项目需要刷新了
				if (newiteMap.get(classitemVO.getPk_wa_classitem()) == null || !newiteMap.get(classitemVO.getPk_wa_classitem()).equals(classitemVO.getTs())) {
					throw new BusinessException(WaLangUtil.getStrByID("UPP60131004-000406"));// 薪资项目发生变化,
					// 请刷新！
				}
			}
		} else {
			throw new BusinessException(WaLangUtil.getStrByID("UPP60131004-000406"));// 薪资项目发生变化,
			// 请刷新！
		}
	}

	/**
	 *
	 * @author zhangg on 2009-8-12
	 * @param pk_wa_class
	 * @param userid
	 * @throws Exception
	 */
	public void lockWaclass(String pk_wa_class, String userid) throws Exception {
		if (!PKLock.getInstance().acquireLock(pk_wa_class, userid, null)) {
			throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000160"));// "正在有人处理该薪资类别的数据，请刷新后再试！"
		}
	}

	/**
	 *
	 * @author zhangg on 2009-8-12
	 * @param pk_wa_class
	 * @param userid
	 */

	public void releaseLock(String pk_wa_class, String userid) {
		nc.bs.uap.lock.PKLock.getInstance().releaseLock(pk_wa_class, userid, null);
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#reCaculate(nc.vo.wa.wa_009.ReCacuVO)
	 */
	public WaclassVO reCaculate(ReCacuVO aRecaVO) throws BusinessException {

		try {
			// 判断状态是否发生变化
			// 查询条件与计算条件不等价。因为计算条件里面有计算范围与计算模式的附加条件
			DataDMO dataDMO = new DataDMO();
			String selConditon = aRecaVO.getSelCondition();
			selConditon = dataDMO.getRecacuConditonWithPsndoc(selConditon);
			String wa_data_where = "";
			if (selConditon != null && selConditon.trim().length() > 0) {
				wa_data_where += " and " + selConditon + "  ";
			}
			// checkWaClassStateChange(aRecaVO.getWaClassVO(), wa_data_where);

			// 判断补发
			checkReData(aRecaVO);

			// 判断项目是否发生变化2008-06-03
			checkItemChange(aRecaVO);

			// 加锁
			lockWaclass(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getUserid());

			checkSateForCaculate(aRecaVO);

			caculatePreparing(aRecaVO);

			// add by xjl 2008/11/13 start
			// 外部数据源
			reCaculateWAOrtherDS(aRecaVO, aRecaVO.getWaPeriodvo().getVcalyear(), aRecaVO.getWaPeriodvo().getVcalmonth());

			reCaculateOrther(aRecaVO, aRecaVO.getWaPeriodvo().getVcalyear(), aRecaVO.getWaPeriodvo().getVcalmonth());

			// 考勤系统
			reCaculateTbm(aRecaVO);

			// 薪资管理
			// reCaculateWm(aRecaVO);

			// 人事信息
			reCaculateHi(aRecaVO);

			// 绩效考核
			reCaculatePe(aRecaVO);

			// 其他类别
			reCaculateWo(aRecaVO);
			
//			绩效奖金管理系统 帅映杰 2011-05-13 医疗
//			CaculateHRPPFDMO hrppfdmo = new CaculateHRPPFDMO();
//			hrppfdmo.reCaculateHRP(aRecaVO);

//			部门薪酬项目管理系统  宋旨昊 2011-03-23 医疗
			CaculateHRPDMO hrpdmo = new CaculateHRPDMO();
			hrpdmo.reCaculateHRP(aRecaVO);
			
			//科室奖金计算 tianxfc 2018-12-03
			CaculateHRPDMOTongRen hrpTRdmo = new CaculateHRPDMOTongRen();
			hrpTRdmo.reCaculateHRP(aRecaVO);
			
			// 统计排班加班天数 zhanghua 2019-04-29
			CaculateHRPDMOHolidayRen hrpTRHolidayDmo = new CaculateHRPDMOHolidayRen();
			hrpTRHolidayDmo.reCaculateHRP(aRecaVO);
			
			// 统计缺勤天数 zhanghua 2019-05-14
			CaculateHRPDMODailyRen hrpTRDailyDmo = new CaculateHRPDMODailyRen();
			hrpTRDailyDmo.reCaculateHRP(aRecaVO);
			
			// 再计算薪资自己的公式
			reCaculateWa(aRecaVO);

			WaGlobalVO globalVO = new WaGlobalVO();
			WaclassVO waclassVO = aRecaVO.getWaClassVO();
			globalVO.setWaClassPK(waclassVO.getPrimaryKey());
			globalVO.setWaYear(waclassVO.getCurrentPeriodVO().getCyear());
			globalVO.setWaPeriod(waclassVO.getCurrentPeriodVO().getCperiod());
			globalVO.setCurUserid(waclassVO.getCurUserid());

			return WaClassStateHelper.getWaclassVOWithState(globalVO, wa_data_where);

		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw (BusinessException) e;
			} else {
				e.printStackTrace();
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage());
			}

		} finally {
			releaseLock(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getUserid());
		}
	}

	// add by xjl 2008/11/13 start
	/**
	 * 外部数据源系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	// private void reCaculateWAOrtherDS(ReCacuVO aRecaVO, String curYear,
	// String curMonth) throws Exception {
	// try {
	// String[][] items = aRecaVO.getWaorthers();
	// if (items != null && items.length > 0) {
	// int length = 0;
	// for (String[] item : items) {
	// if (item[1].startsWith("valueOfWAORTHERDS")) {
	// length++;
	// }
	// }
	// String[][] newItems = new String[length][2];
	// int index = 0;
	// for (String[] item : items) {
	// if (item[1].startsWith("valueOfWAORTHERDS")) {
	// newItems[index][0] = item[0];
	// newItems[index][1] = item[1];
	// index++;
	// }
	// }
	// if (newItems == null || newItems.length == 0) {
	// return;
	// }
	// items = newItems;
	// if (aRecaVO.getWaorthers() == null
	// || aRecaVO.getWaorthers().length == 0) {
	// return;
	// }
	// /* 先计算从其他系统取数的函数 */
	//
	// FuncProImpl funcProBo = new FuncProImpl();
	//
	// String condition = " and " + aRecaVO.getReCacuCondition();
	//
	// funcProBo.funcProForWaBat(curYear, curMonth, NCLangResOnserver
	// .getInstance().getStrByID("60390704","UPP60390704-000022")/* @res
	// "从外系统取数的薪资项目" */,
	// condition, aRecaVO, items);
	//
	// DataDMO dmo = new DataDMO();
	//
	// dmo.updateBM(items, aRecaVO);
	// }
	// } catch (Exception e) {
	// reportException(e);
	// throw new
	// Exception(WaLangUtil.getStrByID("UPP60131004-000407"));//从外部数据源系统取数时出错!
	// }
	// }
	// add by xjl 2008/11/13 end
	private void reCaculateWAOrtherDS(ReCacuVO aRecaVO, String curYear, String curMonth) throws Exception {
		String erritem = null;
		try {
			String[][] items = aRecaVO.getWaorthers();
			if (items != null && items.length > 0) {
				int length = 0;
				for (int i = 0; i < items.length; i++) {
					if (items[i][1].startsWith("valueOfWAORTHERDS")) {
						length++;
					}
				}
				String[][] newItems = new String[length][2];
				int index = 0;
				for (int i = 0; i < items.length; i++) {
					if (items[i][1].startsWith("valueOfWAORTHERDS")) {
						newItems[index][0] = items[i][0];
						newItems[index][1] = items[i][1];
						index++;
					}
				}
				if (newItems == null || newItems.length == 0) {
					return;
				}
				items = newItems;
				if (aRecaVO.getWaorthers() == null || aRecaVO.getWaorthers().length == 0) {
					return;
				}
				/* 先计算从其他系统取数的函数 */

				FuncProImpl funcProBo = new FuncProImpl();

				String condition = " and " + aRecaVO.getReCacuCondition();

				// funcProBo.funcProForWaBat(curYear, curMonth,
				// NCLangResOnserver
				// .getInstance().getStrByID("60390704","UPP60390704-000022")/*
				// @res "从外系统取数的薪资项目" */,
				// condition, aRecaVO, items);
				// update by zhoulei 为了定位哪个项目出错，此修改只限于真功夫项目.
				for (int i = 0; i < items.length; i++) {
					erritem = items[i][1].trim();
					erritem = erritem.substring(erritem.indexOf("(") + 1, erritem.length() - 1);

					funcProBo.funcProForWaBat(curYear, curMonth, NCLangResOnserver.getInstance().getStrByID("60390704", "UPP60390704-000022")/*
					 * @res
					 * "从外系统取数的薪资项目"
					 */, condition, aRecaVO, new String[][] { items[i] });
				}
				// ----此处修改不合适，合并代码时还原2519--2521行

				DataDMO dmo = new DataDMO();

				dmo.updateBM(items, aRecaVO);
			}
		} catch (Exception e) {
			reportException(e);
			throw new Exception(WaLangUtil.getStrByID("UPP60131004-000407"));// 从外部数据源系统取数时出错!
		}
	}

	// add by xjl 2008/11/13 end

	/**
	 *
	 * Created on 2007-10-12
	 *
	 * @author zhangg
	 * @param aRecaVO
	 * @throws BusinessException
	 */
	private void checkSateForCaculate(ReCacuVO aRecaVO) throws BusinessException {

		WaclassVO waClassVO = aRecaVO.getWaClassVO();
		String classpk = waClassVO.getPrimaryKey();

		String curyear = aRecaVO.getWaYear();
		String curmonth = aRecaVO.getWaPeriod();
		// boolean rangeAll = aRecaVO.isRangeAll();
		boolean modeAll = aRecaVO.isModeAll();

		// String condition = aRecaVO.getSelCondition();

		try {
			DataDMO dmo = new DataDMO();
			int i = dmo.checkState(classpk, curyear, curmonth);

			if (i != -1 && i != 0) {
				throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000335")/*
				 * @res
				 * "当前期间的薪资类别的状态不正确,
				 * 请刷新后在试."
				 */);//
			}

			// 如果是false则代表着只计算未计算的， 不管是全部人员还是选择的部分人员应该都可以计算
			if (modeAll) {/*
			 * String whereCondition = ""; //查看是否有的已经审核完成
			 * if(rangeAll){//全部人员里面是否有已经审核的人员 whereCondition = "
			 * and 1=1 "; }else{//在选择的人员里面是否有已经审核的人员
			 * whereCondition = " and " + new
			 * DataDMO().getRecacuConditonWithPsndoc(condition); }
			 *
			 * String sql = "select 1 from wa_data where cyear=?
			 * and cperiod =? and icheckflag = 1 and dr=0 and
			 * istopflag =0 and classid = ?"; sql = sql +
			 * whereCondition;
			 *
			 * SQLParameter para = new SQLParameter();
			 * para.addParam(curyear); para.addParam(curmonth);
			 * para.addParam(classpk);
			 *
			 *
			 * boolean isExist = (Boolean) new
			 * BaseDAO().executeQuery(sql, para, new
			 * BooleanProcessor());
			 *
			 * if(isExist){
			 *
			 * throw new
			 * nc.vo.pub.BusinessException("存在已经审核的薪资数据，请更改计算方式为:只计算尚未计算的人员" //
			 * nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("60131004", //
			 * "UPP60131004-000140") ) // // * // ; }
			 *
			 *
			 */
			}

		} catch (NamingException e) {
			reportException(e);
		} catch (SQLException e) {
			reportException(e);
		} catch (SystemException e) {
			reportException(e);
		}

	}

	/**
	 * 人事信息系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void reCaculateHi(ReCacuVO aRecaVO) throws Exception {
		try {
			// 来自人事信息理系统的项目
			String[][] hiItems = aRecaVO.getHiItem();

			if (hiItems == null || hiItems.length < 1) {
				return;
			}
			String l_pk_wa_item = "";
			nc.impl.wa.func.WaFuncDMO dmo = new nc.impl.wa.func.WaFuncDMO();
			nc.impl.wa.wa_024.ItemDMO itemdmo = new nc.impl.wa.wa_024.ItemDMO();
			for (int i = 0; i < hiItems.length; i++) {

				/* 先处理相对期间问题PERIODIS */
				if (hiItems[i][1].indexOf("PERIODIS") > 0) {
					String formula = hiItems[i][1];
					int startindex = formula.indexOf("PERIODIS");
					int endindex = formula.indexOf(")", startindex);
					String prestr = formula.substring(0, startindex);
					String nextstr = formula.substring(endindex);
					String period = formula.substring(startindex, endindex);
					String iflag = period.substring(period.length() - 1);
					period = period.substring(8, period.length() - 2);
					// 2003-11-02lhp:目前按照归属期间传递，若不合适可以修改为发放期间
					String funcdate = dmo.getAbsPeriodDate(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getNestWaPeriodvo().getCyear(), aRecaVO.getNestWaPeriodvo().getCperiod(),
							new Integer(period), new Integer(iflag));
					formula = prestr + funcdate + nextstr;
					hiItems[i][1] = formula;
				}

				if (hiItems[i][1].startsWith("DATEDIFF")) {// 天数
					dmo.updateDaysBetween(hiItems[i], aRecaVO);
				} else if (hiItems[i][1].startsWith("valueOfSubtable")) {// 子集信息
					dmo.updateHiSubtable(hiItems[i], aRecaVO);
				} else if (hiItems[i][1].startsWith("valueOfWadoc")) {// 薪资变动档案
					updateWaDoc(hiItems[i], aRecaVO);
				} else {// 工龄、年龄
					dmo.updateWorkAge(hiItems[i], aRecaVO);
				}
			}
		} catch (Exception e) {
			reportException(e);
			if (e instanceof BusinessException) {
				throw e;

			} else {
				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000021")/* "计算员工信息函数时出错!" */);
			}

		}
	}

	public void updateWaDoc(String[] valueOfDocItem, ReCacuVO aRecaVO) throws Exception {
		String valueOfWadoc = valueOfDocItem[1];
		if (!valueOfWadoc.startsWith("valueOfWadoc")) {
			return;
		}
		String l_pk_wa_item = "";
		String[] para = valueOfWadoc.split("#");
		nc.impl.wa.func.WaFuncDMO dmo = new nc.impl.wa.func.WaFuncDMO();
		if (para.length != 4) {
			// 先查找该薪资项目在wa_item中的pk
			String iitemid = valueOfDocItem[0].substring(valueOfDocItem[0].indexOf("f_") + 2);
			l_pk_wa_item = getPk_wa_item(aRecaVO.getPk_corp(), iitemid);
			dmo.updateWaDoc(valueOfDocItem, aRecaVO, l_pk_wa_item);
		} else {// valueOfWadoc(#1#15#)分成4部分
			Integer type = new Integer(para[1]);
			String iitemid = para[2];
			l_pk_wa_item = getPk_wa_item(aRecaVO.getPk_corp(), iitemid);
			if (type == 1) {// 原发放额
				dmo.updatePreWaDoc(valueOfDocItem, aRecaVO, l_pk_wa_item);
			} else if (type == 2) {// 现发放额
				dmo.updateWaDoc(valueOfDocItem, aRecaVO, l_pk_wa_item);
			}
		}
	}

	public String getPk_wa_item(String pk_corp, String iitemid) throws SystemException, NamingException, SQLException {
		ItemDMO itemDMO = new ItemDMO();
		ItemVO[] itemVOs = itemDMO.queryAll(pk_corp);
		if (itemVOs == null) {
			return null;
		}

		for (ItemVO itemVO : itemVOs) {
			if (itemVO.getIitemid().toString().equals(iitemid)) {
				return itemVO.getPk_wa_item();
			}
		}
		return null;
	}

	/**
	 * 福利系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void reCaculateOrther(ReCacuVO aRecaVO, String curYear, String curMonth) throws Exception {
		try {
			String[][] items = aRecaVO.getOtherItem();
			if (items != null && items.length > 0) {

				// update by xjl 2008/12/18 start
				int length = 0;
				for (int i = 0; i < items.length; i++) {
					if (!items[i][1].startsWith("valueOfWAORTHERDS")) {
						length++;
					}
				}
				String[][] newItems = new String[length][2];
				int index = 0;
				for (int i = 0; i < items.length; i++) {
					if (!items[i][1].startsWith("valueOfWAORTHERDS")) {
						newItems[index][0] = items[i][0];
						newItems[index][1] = items[i][1];
						index++;
					}
				}
				if (newItems == null || newItems.length == 0) {
					return;
				}
				items = newItems;
				// update by xjl 2008/12/18 end

				if (aRecaVO.getOtherItem() == null || aRecaVO.getOtherItem().length == 0) {
					// nc.bs.logging.Logger.error("reCaculateOrther end!");
					return;
				}
				/* 先计算从其他系统取数的函数 */

				FuncProImpl funcProBo = new FuncProImpl();

				// String swhere =" and irecaculateflag <> 1 ";
				// modified by lhp 2001.12.10
				// 原来是算全部中未计算过的，修改为按照用户选择来判断
				// ----2006-11-30 zhoucx modified begin ---->
				// 修改说明：前台传aRecaVO已经加上了
				String condition = " and " + aRecaVO.getReCacuCondition();
				// String condition = aRecaVO.getSelCondition();
				// if (condition.trim().length() > 0) {
				// condition = " and " + condition;
				// if (!aRecaVO.isModeAll()) {
				// //不是全部计算
				// condition += " and wa_data.irecaculateflag<>1 ";
				// }
				// } else {
				// if (!aRecaVO.isModeAll()) {
				// //不是全部计算
				// condition += " and wa_data.irecaculateflag<>1 ";
				// }
				// }
				// ----2006-11-30 zhoucx modified end-----<

				// 外系统导入算法,one time
				// String [][] items = aRecaVO.getOtherItem();
				// for (int i = 0 ;i< items.length; i++)
				funcProBo.funcProForWaBat(curYear, curMonth, NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000022")/*
				 * @res
				 * "从外系统取数的薪资项目"
				 */, condition, aRecaVO, items);

				DataDMO dmo = new DataDMO();

				dmo.updateBM(items, aRecaVO);
			}
		} catch (Exception e) {
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000023")/*
			 * @res
			 * "从福利系统取数时出错!"
			 */);
		}
	}

	/**
	 * 计算来自绩效系统的薪资项目。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @param aRecaVO
	 *                ReCacuVO
	 * @return Exception
	 */
	private void reCaculatePe(ReCacuVO aRecaVO) throws Exception {
		try {
			// 来自绩效管理系统的项目
			String[][] peItems = aRecaVO.getPeItem();

			if (peItems == null || peItems.length < 1) {
				return;
			}
			WaFuncDMO dmo = new WaFuncDMO();
			dmo.updatePe(aRecaVO);

		} catch (Exception e) {
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000024")/*
			 * @res
			 * "计算绩效函数时出错!"
			 */);
		}
	}

	/**
	 *
	 * 计算应缴税款，同时将数据保存到wa_tax中 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	private void recaculateTax(ReCacuVO aRecaVO) throws Exception {

		int NOTTAX = 0; // 不扣税
		WaclassVO waClassVO = aRecaVO.getWaClassVO();
		String gzlbId = waClassVO.getPrimaryKey();

		String waYear = aRecaVO.getWaYear();
		String waPeriod = aRecaVO.getWaPeriod();

		DataDMO dmo = new DataDMO();

		int taxFlag = waClassVO.getItaxsetting().intValue();

		boolean isRangeAll = aRecaVO.isRangeAll();
		boolean isModeAll = aRecaVO.isModeAll();
		String selCondition = aRecaVO.getSelCondition();
		String recaCondition = aRecaVO.getReCacuCondition();

		boolean allNotTax = false;

		// 计算基数
		caculateHaveTaxBase(aRecaVO); // 包括已扣税和已扣税基数

		caculateTaxBase(aRecaVO); // 本次扣税基数

		// 本次扣税
		if (taxFlag != NOTTAX) // 扣税
		{
			caculateTax_yh(aRecaVO); // 本次扣税,优化

			// nc.bs.logging.Logger.error("calculate tax end!");
		}

		// 不扣税的情况
		else {

			// 更新原币
			int num = dmo.updateFromSameTable("wa_data", "wa_data", new String[] { "f_4", "f_5", "f_8", "f_9" }, "0", aRecaVO, "*", allNotTax);

			if (num > 0) {
				// 更新主币
				dmo.updateFromSameTable("wa_dataz", "wa_dataz", new String[] { "f_4", "f_5", "f_8", "f_9" }, "0", aRecaVO, "*", allNotTax);

				// if (!aRecaVO.getCurrentBO().isSingleMain() &&
				// !aRecaVO.getCurrentBO().isZhuBi()) //主辅币
				if (!aRecaVO.getCurrentBO().isSingleMain()) // 主辅币
				{
					// 更新辅币
					dmo.updateFromSameTable("wa_dataf", "wa_dataf", new String[] { "f_4", "f_5", "f_8", "f_9" }, "0", aRecaVO, "*", allNotTax);
				}

				// 收入额折主，减费用额，应纳税所得额，税率，速算扣除数,本次应扣税,已扣税，补发扣税
				String col_tax[] = new String[] { "fmnyz", "ndebuctamount", "ftaxmny", "ntaxrate", "nquickdebuct", "ftaxz", "f_8", "f_10" };
				String col_dataz[] = new String[] { "wa_dataz.f_5", "0", "0", "0", "0", "0", "0", "0" };

				String condition = "  wa_data.istopflag=0 ";

				if (!isRangeAll) {
					// 只计算选择人员
					condition += " and " + selCondition + " ";
				}

				if (!isModeAll) {
					// 只计算没有重新计算的人员
					condition += " and wa_data.irecaculateflag <>1 ";
				}

				String psnCondition = " and wa_tax.psnid in ";

				psnCondition += " (select psnid from wa_data ";

				// psnCondition += " where wa_data.psnid=wa_psn.psnid ";// and("
				// +
				// condition
				// + ") ";

				psnCondition += " where (wa_data.classid='" + gzlbId + "' and wa_data.cyear='" + waYear + "' and wa_data.cperiod='" + waPeriod + "' " + ") ";

				// psnCondition += " and (wa_psn.classid='" + gzlbId
				// + "' and wa_psn.cyear='" + waYear
				// + "' and wa_psn.cperiod='" + waPeriod + "' " + " ";

				if (!allNotTax) {
					psnCondition += " and wa_data.itaxflag=0 ";
				}
				// psnCondition += ") ";
				psnCondition += " and " + nc.vo.hr.global.GlobalTool.getConditonWithPsndoc(recaCondition, selCondition, "wa_data", "psnid");
				psnCondition += ")";

				dmo.updateWa_Tax(col_tax, col_dataz, psnCondition, aRecaVO);
			}
			// nc.bs.logging.Logger.error("not tax end!");

		}

	}

	/**
	 * 考勤系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void reCaculateTbm(ReCacuVO aRecaVO) throws Exception {
		try {
			// 来自考勤系统的项目
			String[][] tbmItems = aRecaVO.getTbmItem();

			if (tbmItems == null || tbmItems.length < 1) {
				return;
			}

			String pk_corp = aRecaVO.getPk_corp();
			// String waYear = aRecaVO.getWaYear();
			// String waPeriod = aRecaVO.getWaPeriod();

			nc.vo.tbm.tbm_006.PeriodVO l_PeriodVOCond = new nc.vo.tbm.tbm_006.PeriodVO();
			l_PeriodVOCond.setDr(new Integer(0));
			l_PeriodVOCond.setPk_corp(pk_corp);
			/*
			 * 2003 09 19 l_PeriodVOCond.setWageyear(waYear);
			 * l_PeriodVOCond.setWagemonth(waPeriod);
			 */
			l_PeriodVOCond.setWageyear(aRecaVO.getAccountYear());
			l_PeriodVOCond.setWagemonth(aRecaVO.getAccountPeriod());

			nc.vo.tbm.tbm_006.PeriodVO[] l_aryPeriodVO = nc.impl.wa.agent.WaBmAgentImpl.queryPeriodByVO(l_PeriodVOCond, new Boolean(true));

			if (l_aryPeriodVO == null || l_aryPeriodVO.length < 1) {
				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000025")
				/* @res * "没有与本薪资期间对应的考勤期间，不能继续！" */);
			}

			nc.vo.tbm.tbm_006.PeriodVO l_PeriodVO = l_aryPeriodVO[0];

			String ls_TBMyear = l_PeriodVO.getTimeyear();
			String ls_TBMmonth = l_PeriodVO.getTimemonth();

			String[][][] specialTbmItem = getSpecialTbmItem(tbmItems, pk_corp);

			String[][] tbmItemBase = specialTbmItem[0];
			String[][] tbmItemSpecial = specialTbmItem[1];
			String[][] tbmItemWork = specialTbmItem[2];
			String[][] tbmItemOrther = specialTbmItem[3]; // 3003-09-13新加的统计函数等

			String[][] tbmuserDefinedItem = specialTbmItem[4]; // 自定义项目

			int tbmScale = getTbmScale(pk_corp);
			aRecaVO.setTbmScale(tbmScale);
			if (tbmItemBase != null && tbmItemBase.length > 0) {
				updateTbm_base(ls_TBMyear, ls_TBMmonth, tbmItemBase, aRecaVO, tbmScale);
			}

			if (tbmItemSpecial != null && tbmItemSpecial.length > 0) {
				updateTbm_special(ls_TBMyear, ls_TBMmonth, tbmItemSpecial, aRecaVO, tbmScale);
			}

			if (tbmItemWork != null && tbmItemWork.length > 0) {
				updateTbm_work(ls_TBMyear, ls_TBMmonth, tbmItemWork, aRecaVO, tbmScale);
			}
			if (tbmItemOrther != null && tbmItemOrther.length > 0) {
				FuncProImpl funcProBo = new FuncProImpl();
				String condition = aRecaVO.getSelCondition();
				if (condition.trim().length() > 0) {
					condition = " and " + condition;
					if (!aRecaVO.isModeAll()) {
						// 不是全部计算
						condition += " and wa_data.irecaculateflag<>1 ";
					}
				} else {
					if (!aRecaVO.isModeAll()) {
						// 不是全部计算
						condition += " and wa_data.irecaculateflag<>1 ";
					}
				}

				// 外系统导入算法,one time
				funcProBo.funcProForWaBat(ls_TBMyear, ls_TBMmonth, NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000026")/*
				 * @res
				 * "从考勤系统取数的薪资项目"
				 */, condition, aRecaVO, tbmItemOrther);

				// 最后对更新的数据进行格式化
				DataDMO dmo = new DataDMO();
				dmo.updateBM(tbmItemOrther, aRecaVO);
			}

			if (tbmuserDefinedItem != null && tbmuserDefinedItem.length > 0) {
				updateTbm_base(ls_TBMyear, ls_TBMmonth, tbmuserDefinedItem, aRecaVO, tbmScale);

			}
		} catch (Exception e) {
			reportException(e);

			if (e.getMessage() == null) {
				throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000027")/*
				 * @res
				 * "从考勤系统取数时出错!"
				 */);
			} else {
				throw e;
			}
		}
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void reCaculateWa(ReCacuVO aRecaVO) throws Exception {
		try {
			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			String gzlbId = waClassVO.getPrimaryKey();

			String waYear = aRecaVO.getWaYear();
			String waPeriod = aRecaVO.getWaPeriod();

			DataDMO dmo = new DataDMO();

			// WaclassVO waClassVo = aRecaVO.getWaClassVO();

			if (aRecaVO.getFixItem() != null && aRecaVO.getFixItem().length > 0) {
				// 固定金额
				dmo.caculateFixItem(aRecaVO);
			}

			// nc.bs.logging.Logger.error("caculateFixItem end!");

			// if (aRecaVO.getSaTblItem() != null &&
			// aRecaVO.getSaTblItem().length > 0) {
			// //薪资表
			// caculateSaTblItem(aRecaVO);
			// }

			// 公式项，包含系统薪资项
			caculateFormuItem(aRecaVO);

			// 将数据复制到wa_dataz,wa_dataf中
			// copyToDataZandDataF(aRecaVO);

			// nc.bs.logging.Logger.error("copyToDataZandDataF end!");

			// 重计算标志
			dmo.updateRecaFlag(aRecaVO, "1");

			// nc.bs.logging.Logger.error("updateRecaFlag end!");
			String recaculateFlag = "1";

			if (dmo.havePsnNotRecacu(gzlbId, waYear, waPeriod)) {
				// 尚有人员未参加重新计算
				recaculateFlag = "0";
			}

			// nc.bs.logging.Logger.error("havePsnNotRecacu end!");

			dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecaculateflag" }, new String[] { recaculateFlag });

		} catch (Exception e) {
			reportException(e);
			throw e;
		}
	}

	/**
	 * 薪资管理系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void reCaculateWo(ReCacuVO aRecaVO) throws Exception {
		try {
			// 来自薪资的其他类别项目
			String[][] woItems = aRecaVO.getWaortherItem();

			if (woItems == null || woItems.length < 1) {
				return;
			}

			nc.impl.wa.func.WaFuncDMO dmo = new nc.impl.wa.func.WaFuncDMO();
			FuncProImpl funcProBo = new FuncProImpl();
			String condition = aRecaVO.getReCacuCondition();
			DataDMO datacaldmo = new DataDMO();
			for (String[] woItem : woItems) {
				String fldname = woItem[0].toString();
				/* 解析参数 */
				String formula = woItem[1].toString().trim();
				int itemtype = 0;// 默认字符
				if (aRecaVO.isDigitItemRefWithCurr(fldname) || aRecaVO.isDigitItemRefWithoutCurr(fldname)) {
					itemtype = 1;// 是数字
				}
				if (formula.indexOf("valueOfWAOrther") >= 0 || formula.indexOf("valueOfDept") >= 0) {// 取其他类别函数或统计函数
					// String forname = formula.substring(0,
					// formula.indexOf("("));//函数名
					formula = formula.substring(formula.indexOf("(") + 1, formula.length() - 1);
					String classid = formula.substring(0, formula.indexOf(","));
					formula = formula.substring(formula.indexOf(",") + 1, formula.length());
					String itemid = formula.substring(0, formula.indexOf(","));
					formula = formula.substring(formula.indexOf(",") + 1, formula.length());
					String where = "";
					/*
					 * 2003 09 19 String wayear = aRecaVO.getWaYear();//默认当前年
					 * String waperiod = aRecaVO.getWaPeriod();//默认当前期间
					 */
					String wayear = aRecaVO.getNestWaPeriodvo().getCyear();// 默认当前年
					String waperiod = aRecaVO.getNestWaPeriodvo().getCperiod();// 默认当前期间

					String opstr = formula;
					if (formula.indexOf(",") > 0) {
						if (!formula.trim().startsWith("sum") && !formula.trim().startsWith("avg") && !formula.trim().startsWith("min") && !formula.trim().startsWith("max")) {
							// 有期间数据，则读取
							String l_wayear = formula.substring(0, formula.indexOf(","));
							formula = formula.substring(formula.indexOf(",") + 1, formula.length());
							String l_waperiod = formula.substring(0, formula.indexOf(","));
							formula = formula.substring(formula.indexOf(",") + 1, formula.length());
							if (!l_wayear.trim().equals("-1")) {// 非当前
								wayear = l_wayear;
								waperiod = l_waperiod;
							}
						}
						opstr = formula;
						if (formula.indexOf(",") > 0) {
							// 有部门或人员类别条件
							opstr = formula.substring(0, formula.indexOf(","));
							where = formula.substring(formula.indexOf(",") + 1, formula.length());
						}
					}
					if (where.equals("DEPT") || where.equals("PSNCLASS") || where.indexOf("f_") > 0) {
						// 好象参数太多
						dmo.valueOfDept(classid, wayear, waperiod, itemid, where, opstr, fldname, itemtype, aRecaVO);
					} else {// 取公司合计或其他类别函数
						if (where.equals("CORP")) {
							where = "";// 取公司数据，视做取其他类别一样处理
						}
						Object value = dmo.aveOfWAItem(classid, wayear, waperiod, itemid, where, opstr);
						String l_value = null;
						if (value == null) {
							if (itemtype == 1) {
								l_value = "0.00";
							}
						} else {
							l_value = value.toString();
						}
						datacaldmo.caculateFormulaItem(fldname, "(1=1)", l_value, aRecaVO);
					}
				} else {
					funcProBo.funcProForWa(aRecaVO.getWaClassVO().getPrimaryKey(),

							aRecaVO.getWaYear(), aRecaVO.getWaPeriod(),

							// aRecaVO.getNestWaPeriodvo().getCyear(),
							// aRecaVO.getNestWaPeriodvo().getCperiod(),
							null, null, fldname, itemtype, NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000029")/*
							 * @res
							 * "从其它期间取数的项目"
							 */, formula, condition);
					if (itemtype == 1) {// 数字，需要格式化
						datacaldmo.updateFromAnotherPeriodAfter(aRecaVO.getWaClassVO().getPrimaryKey(),

								aRecaVO.getWaYear(), aRecaVO.getWaPeriod(),

								// aRecaVO.getNestWaPeriodvo().getCyear(),
								// aRecaVO.getNestWaPeriodvo().getCperiod(),
								condition, fldname, aRecaVO.getWaItemDecimal(fldname));
					}
				}
			}

		} catch (Exception e) {
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000030")/*
			 * @res
			 * "从其他薪资类别或期间取数时出错!"
			 */);
		}
	}

	/**
	 * 薪资管理系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void reCaculateWoForSequ(ReCacuVO aRecaVO, String[] woItem, DataDMO datacaldmo) throws Exception {
		try {
			nc.impl.wa.func.WaFuncDMO dmo = new nc.impl.wa.func.WaFuncDMO();
			String condition = aRecaVO.getReCacuCondition();
			// 来自薪资的其他类别项目
			String fldname = woItem[0].toString();
			/* 解析参数 */
			String formula = woItem[1].substring(1).trim();
			int itemtype = 0; // 默认字符
			if (aRecaVO.isDigitItemRefWithCurr(fldname) || aRecaVO.isDigitItemRefWithoutCurr(fldname)) {
				itemtype = 1; // 是数字
			}
			if (formula.indexOf("valueOfWAOrther") >= 0 || formula.indexOf("valueOfDept") >= 0) {
				// 取其他类别函数或统计函数
				// String forname = formula.substring(0, formula.indexOf("("));
				// //函数名
				formula = formula.substring(formula.indexOf("(") + 1, formula.length() - 1);
				String classid = formula.substring(0, formula.indexOf(","));
				String pkcorp = aRecaVO.getPk_corp();
				classid = dmo.transGroupPk(classid, pkcorp);
				formula = formula.substring(formula.indexOf(",") + 1, formula.length());
				String itemid = formula.substring(0, formula.indexOf(","));
				formula = formula.substring(formula.indexOf(",") + 1, formula.length());
				String where = "";
				/*
				 * zs 2003 09 19 String wayear = aRecaVO.getWaYear(); //默认当前年
				 * String waperiod = aRecaVO.getWaPeriod(); //默认当前期间
				 */
				String wayear = aRecaVO.getWaPeriodvo().getCyear(); // 默认当前年
				String waperiod = aRecaVO.getWaPeriodvo().getCperiod(); // 默认当前期间

				String opstr = formula;
				if (formula.indexOf(",") > 0) {
					if (!formula.trim().startsWith("sum") && !formula.trim().startsWith("avg") && !formula.trim().startsWith("min") && !formula.trim().startsWith("max")) {
						// 有期间数据，则读取
						String l_wayear = formula.substring(0, formula.indexOf(","));
						formula = formula.substring(formula.indexOf(",") + 1, formula.length());
						String l_waperiod = formula.substring(0, formula.indexOf(","));
						formula = formula.substring(formula.indexOf(",") + 1, formula.length());
						if (!l_wayear.trim().equals("-1")) {
							// 非当前
							wayear = l_wayear;
							waperiod = l_waperiod;
						}
					}
					opstr = formula;
					if (formula.indexOf(",") > 0) {
						// 有部门或人员类别条件
						opstr = formula.substring(0, formula.indexOf(","));
						where = formula.substring(formula.indexOf(",") + 1, formula.length());
					}
				}
				if (where.equals("DEPT") || where.equals("PSNCLASS") || where.indexOf("f_") >= 0) {
					// 好象参数太多
					dmo.valueOfDept(classid, wayear, waperiod, itemid, where, opstr, fldname, itemtype, aRecaVO);
				} else {
					// 取公司合计或其他类别函数
					if (where.equals("CORP")) {
						where = ""; // 取公司数据，视做取其他类别一样处理
					}
					Object value = dmo.aveOfWAItem(classid, wayear, waperiod, itemid, where, opstr);
					String l_value = null;
					if (value == null) {
						if (itemtype == 1) {
							l_value = "0.00";
						}
					} else {
						l_value = value.toString();
					}
					datacaldmo.caculateFormulaItem(fldname, "(1=1)", l_value, aRecaVO);
				}
			} else if (formula.indexOf("valueOfWAGrade") >= 0) {
				// 取其他类别函数或统计函数
				// String forname = formula.substring(0, formula.indexOf("("));
				// //函数名
				formula = formula.substring(formula.indexOf("(") + 1, formula.length() - 1);
				String classid = formula.substring(0, formula.indexOf(","));
				formula = formula.substring(formula.indexOf(",") + 1, formula.length());
				String itemid = formula;
				// String wayear = aRecaVO.getWaPeriodvo().getCyear(); //默认当前年
				// String waperiod = aRecaVO.getWaPeriodvo().getCperiod();
				// //默认当前期间

				if (itemid.equalsIgnoreCase(nc.vo.hr.func.FuncCommonValue.PARA_V_PRMLV_NAME)) {
					dmo.updateWaGradePrmlvName(woItem, aRecaVO, classid);
				} else if (itemid.equalsIgnoreCase(nc.vo.hr.func.FuncCommonValue.PARA_V_PRMLV_PK)) {
					dmo.updateWaGradePrmlvPK(woItem, aRecaVO, classid);
				} else if (itemid.equalsIgnoreCase(nc.vo.hr.func.FuncCommonValue.PARA_V_SECLV_NAME)) {
					dmo.updateWaGradeSeclvName(woItem, aRecaVO, classid);
				} else if (itemid.equalsIgnoreCase(nc.vo.hr.func.FuncCommonValue.PARA_V_SECLV_PK)) {
					dmo.updateWaGradeSeclvPK(woItem, aRecaVO, classid);
				}
			} else {
				nc.impl.hr.func.FuncProImpl funcProBo = new nc.impl.hr.func.FuncProImpl();
				funcProBo.funcProForWa(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getWaYear(), aRecaVO.getWaPeriod(), null, null, fldname, itemtype, NCLangResOnserver.getInstance().getStrByID(
						"60131004", "UPP60131004-000029")/* @res "从其它期间取数的项目" */, formula, condition, aRecaVO.getPk_corp());
				if (itemtype == 1) {
					// 数字，需要格式化
					datacaldmo.updateFromAnotherPeriodAfter(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getWaYear(), aRecaVO.getWaPeriod(),
							// aRecaVO.getNestWaPeriodvo().getCyear(),
							// aRecaVO.getNestWaPeriodvo().getCperiod(),
							condition, fldname, aRecaVO.getWaItemDecimal(fldname));
				}
			}
		} catch (Exception e) {
			reportException(e);
			throw new Exception(woItem[0] + NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000031")/*
			 * @res
			 * "计算出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#replaceAll(nc.vo.wa.wa_009.ReCacuVO,
	 *      java.lang.String[][], java.lang.String[])
	 */
	public int replaceAll(ReCacuVO aRecaVO, String[][] replaceItems, String[] formula) throws nc.vo.pub.BusinessException {
		int re = -5;
		WaclassVO waClassVO = aRecaVO.getWaClassVO();
		String gzlbId = waClassVO.getPrimaryKey();

		String waYear = aRecaVO.getWaYear();
		String waPeriod = aRecaVO.getWaPeriod();

		try {
			re = getcheckstate(gzlbId, waYear, waPeriod);
			if (re == 5) {
				re = -1;
			}
			if (re == 6) {
				re = 0;
			}

			if (re == 0 || re == -1) {
				DataDMO dmo = new DataDMO();
				dmo.replaceAll(replaceItems, formula, aRecaVO);
			}

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000032")/*
			 * @res
			 * "替换失败，请重新检查公式!"
			 */);
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#replaceSel(java.lang.String,
	 *      java.lang.String[][], java.lang.String[], nc.vo.wa.wa_009.ReCacuVO)
	 */
	public int replaceSel(String selCondition, String[][] replaceItems, String[] formula, ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {
		int re = -5;
		WaclassVO waClassVO = aRecaVO.getWaClassVO();
		String gzlbId = waClassVO.getPrimaryKey();

		String waYear = aRecaVO.getWaYear();
		String waPeriod = aRecaVO.getWaPeriod();

		try {
			re = getcheckstate(gzlbId, waYear, waPeriod);
			if (re == 5) {
				re = -1;
			}
			if (re == 6) {
				re = 0;
			}

			if (re == 0 || re == -1) {
				DataDMO dmo = new DataDMO();
				dmo.replaceSel(selCondition, replaceItems, formula, aRecaVO);
			}

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000032")/*
			 * @res
			 * "替换失败，请重新检查公式!"
			 */);
		}
		return re;
	}

	/**
	 *
	 * @author zhangg on 2009-3-26
	 * @see nc.itf.hr.wa.IRecaData#reTotal(nc.vo.wa.wa_009.ReCacuVO,
	 *      nc.vo.wa.wa_008.WaclassVO[])
	 */
	public int reTotal(ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		WaclassVO waClassVO = aRecaVO.getWaClassVO();
		String gzlbId = waClassVO.getPrimaryKey();

		String waYear = aRecaVO.getWaYear();
		String waPeriod = aRecaVO.getWaPeriod();

		WaGlobalVO globalVO = new WaGlobalVO();
		globalVO.setWaClassPK(gzlbId);
		globalVO.setWaYear(waYear);
		globalVO.setWaPeriod(waPeriod);

		WaClassStateHelper.WaStates state = WaClassStateHelper.getWaclassVOWithState(globalVO).getCurrentstate();

		if (state == WaStates.CLASS_WITHOUT_RECACULATED || state == WaStates.CLASS_RECACULATED_WITHOUT_CHECK || state == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_NONE_CHECKED) {
			try {
				DataDMO dmo = new DataDMO();

				UnitDataDAO dataDAO = new UnitDataDAO();
				if (!dataDAO.isAllJZ(aRecaVO)) {
					throw new BusinessException(WaLangUtil.getStrByID("UPP60131004-000408"));// 参与汇总的类别有的尚未结帐！
				}

				ItemVO[] itemVOs = dataDAO.getUnitDigitItem(aRecaVO);

				dataDAO.sumWaData(aRecaVO, itemVOs);

				if (itemVOs != null) {
					Vector<String> vecDigitItemWithCurr = new Vector<String>();
					Vector<String> vecDigitItemWithoutCurr = new Vector<String>();

					for (ItemVO itemVO : itemVOs) {
						if (itemVO.getIproperty().intValue() == 3) {// 数字型
							vecDigitItemWithoutCurr.add("f_" + itemVO.getIitemid().toString());
						} else {
							vecDigitItemWithCurr.add("f_" + itemVO.getIitemid().toString());
						}
					}

					String[] unitDigitItemRefWithCurr = vecDigitItemWithCurr.toArray(new String[vecDigitItemWithCurr.size()]);
					String[] unitDigitItemRefWithoutCurr = vecDigitItemWithoutCurr.toArray(new String[vecDigitItemWithoutCurr.size()]);

					boolean isSingleMain = aRecaVO.getCurrentBO().isSingleMain();
					boolean isZhuBi = aRecaVO.getCurrentBO().isZhuBi();
					boolean isFuBi = false;

					// 重新汇总,得到主币汇总数据

					String bzPk = waClassVO.getCurrid();
					String rateY = "1";
					String rateF = "1";
					String opratorY = "*";
					String opratorF = "*";

					if (isZhuBi) {
						// 更新原币
						dmo.updateFromAnotherTable("wa_dataz", "wa_data", unitDigitItemRefWithCurr, "1", aRecaVO, "*");
						dmo.updateFromAnotherTableWithout("wa_dataz", "wa_data", unitDigitItemRefWithoutCurr, "1", aRecaVO, "*");
						if (!isSingleMain) {
							// 更新辅币
							rateF = aRecaVO.getCurrentBO().getRate(aRecaVO.getCurrentBO().getFuCurrPK()).toString();
							opratorF = aRecaVO.getCurrentBO().getOperatorReverse(aRecaVO.getCurrentBO().getFuCurrPK());

							if (!opratorF.equalsIgnoreCase("/") || !(Math.abs(new Double(rateF).doubleValue()) <= 0.00000001)) {
								dmo.updateFromAnotherTable("wa_dataz", "wa_dataf", unitDigitItemRefWithCurr, rateF, aRecaVO, opratorF);
							}
							dmo.updateFromAnotherTableWithout("wa_dataz", "wa_dataf", unitDigitItemRefWithoutCurr, "1", aRecaVO, "*");
						}

					} else// 非主币
					{
						rateY = aRecaVO.getCurrentBO().getRate(bzPk).toString();
						opratorY = aRecaVO.getCurrentBO().getOperator(bzPk);

						if (isSingleMain) // 单主币
						{
							// 更新原币
							dmo.updateFromAnotherTable("wa_data", "wa_dataz", unitDigitItemRefWithCurr, rateY, aRecaVO, opratorY);
							dmo.updateFromAnotherTableWithout("wa_data", "wa_dataz", unitDigitItemRefWithoutCurr, "1", aRecaVO, "*");
						} else// 主辅币
						{
							isFuBi = aRecaVO.getCurrentBO().isFuBi();

							// 更新辅币
							if (isFuBi) {
								rateY = "1";
								opratorY = "*";
							}

							dmo.updateFromAnotherTable("wa_data", "wa_dataf", unitDigitItemRefWithCurr, rateY, aRecaVO, opratorY);
							dmo.updateFromAnotherTableWithout("wa_data", "wa_dataf", unitDigitItemRefWithoutCurr, "1", aRecaVO, "*");

							// 更新主币
							rateF = aRecaVO.getCurrentBO().getRate(aRecaVO.getCurrentBO().getFuCurrPK()).toString();
							opratorF = aRecaVO.getCurrentBO().getOperator(aRecaVO.getCurrentBO().getFuCurrPK());

							if (!opratorF.equalsIgnoreCase("/") || !(Math.abs(new Double(rateF).doubleValue()) <= 0.00000001)) {
								dmo.updateFromAnotherTable("wa_dataf", "wa_dataz", unitDigitItemRefWithCurr, rateF, aRecaVO, opratorF);

							}

							dmo.updateFromAnotherTableWithout("wa_dataf", "wa_dataz", unitDigitItemRefWithoutCurr, "1", aRecaVO, "*");
						}
					}
				}
				// 重计算标志
				dmo.updateRecaFlag(aRecaVO, "2");

				// 所有人员都已计算完毕（汇总）
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecaculateflag" }, new String[] { "1" });

			} catch (Exception e) {
				if (e instanceof BusinessException) {
					BusinessException businessException = (BusinessException) e;
					throw businessException;
				} else {
					throw new DAOException(e);
				}
			}

		} else {
			throw new BusinessException(new WaClassStateHelper().translateState(state));
		}

		return 0;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#update(nc.vo.wa.wa_009.DataVO, boolean,
	 *      nc.vo.wa.wa_009.ReCacuVO)
	 */
	public void update(DataVO data, boolean needUpdatePeriod, ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		try {
			DataDMO dmo = new DataDMO();
			dmo.update(data, needUpdatePeriod, aRecaVO);

			if (data.getIstopFlag().intValue() == 1) // 停发
			{
				dmo.deletePsn("wa_dataz", data);

				if (!aRecaVO.getCurrentBO().isSingleMain()) // 主辅币
				{
					dmo.deletePsn("wa_dataf", data);
				}
			}
			updateWa_Tax(data.getIstopFlag().intValue(), data.getClassid(), data.getCyear(), data.getCperiod(), data.getPsnid());

			if (needUpdatePeriod) {
				dmo.updateRecaFlagAtPeriod(data.getClassid(), data.getCyear(), data.getCperiod(), new String[] { "irecaculateflag" }, new String[] { "0" });
			}
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::update(DataVO) Exception!");
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#updateArray(nc.vo.wa.wa_009.DataVO[],
	 *      nc.vo.wa.wa_009.ReCacuVO)
	 */
	public int updateWaDataXArray(DataVO[] data, ReCacuVO aRecaVO, Map<String, GeneralVO[]> editRecordMap) throws nc.vo.pub.BusinessException {

		int re = -5;
		try {
			re = getcheckstate(data[0].getClassid(), data[0].getCyear(), data[0].getCperiod());
			if (re == 5) {
				re = -1;
			}
			if (re == 6) {
				re = 0;
			}

			if (re == 0 || re == -1) {
				if (data == null || data.length < 1) {
					return 0;
				}

				//锁定薪资类别
//				PKLock.getInstance().acquireLock(data[0].getClassid(), userId, null);
				DataDMO dmo = new DataDMO();
				dmo.updateArray(data, aRecaVO, editRecordMap);

				for (DataVO element : data) {
					if (element.getIstopFlag().intValue() == 1) // 停发
					{
						dmo.deletePsn("wa_dataz", element);

						if (!aRecaVO.getCurrentBO().isSingleMain()) // 主辅币
						{
							dmo.deletePsn("wa_dataf", element);
						}
					}
				}
				//释放薪资类别

			}
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::update(DataVO) Exception!");
		}
		return re;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-12-27 21:05:07)
	 *
	 * @param aRecacuVO
	 *                nc.vo.wa.wa_009.ReCacuVO
	 */
	private void updateFactTax(ReCacuVO aRecaVO) throws Exception {
		try {
			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			String bzPk = waClassVO.getCurrid();

			boolean isZhuBi = aRecaVO.getCurrentBO().isZhuBi();
			boolean isFuBi = false;

			boolean isSingleMain = aRecaVO.getCurrentBO().isSingleMain();

			UFDouble rateY = new UFDouble(1);
			UFDouble rateF = new UFDouble(1);

			String opZ_rev = "*";
			String opF_rev = "*";

			DataDMO dmo = new DataDMO();

			if (isSingleMain || isZhuBi) {
				// 单主币
				if (!isZhuBi) {
					rateY = aRecaVO.getCurrentBO().getRate(bzPk);
					opZ_rev = aRecaVO.getCurrentBO().getOperatorReverse(bzPk);
				}
				if (rateY.doubleValue() == 0) {
					throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000062")/*
					 * @res
					 * "汇率表为空,必须先要设置主辅币汇率!"
					 */);
				}
				dmo.updateFromAnotherTable("wa_dataz", "wa_data", new String[] { "f_4" }, rateY.toString(), aRecaVO, opZ_rev);
				dmo.updateFromAnotherTable("wa_dataz", "wa_data", new String[] { "f_539" }, rateY.toString(), aRecaVO, opZ_rev);
			}

			if (!isSingleMain) {
				// 主辅币
				rateF = aRecaVO.getCurrentBO().getRate(aRecaVO.getCurrentBO().getFuCurrPK());
				if (rateF == null) {
					throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000034")/*
					 * @res
					 * "辅币的汇率没有指定，不能继续！"
					 */);
				}
				opZ_rev = aRecaVO.getCurrentBO().getOperatorReverse(aRecaVO.getCurrentBO().getFuCurrPK());

				dmo.updateFromAnotherTable("wa_dataz", "wa_dataf", new String[] { "f_4" }, rateF.toString(), aRecaVO, opZ_rev);
				dmo.updateFromAnotherTable("wa_dataz", "wa_dataf", new String[] { "f_539" }, rateF.toString(), aRecaVO, opZ_rev);
				if (!isZhuBi) {
					isFuBi = aRecaVO.getCurrentBO().isFuBi();

					if (!isFuBi) {
						rateY = aRecaVO.getCurrentBO().getRate(bzPk);
						opF_rev = aRecaVO.getCurrentBO().getOperatorReverse(bzPk);
					}

					dmo.updateFromAnotherTable("wa_dataf", "wa_data", new String[] { "f_4" }, rateY.toString(), aRecaVO, opF_rev);
					dmo.updateFromAnotherTable("wa_dataf", "wa_data", new String[] { "f_539" }, rateY.toString(), aRecaVO, opF_rev);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private static IParValue iwaParValue;

	public static IParValue getWaParValue() {
		if (iwaParValue == null) {
			iwaParValue = (IParValue) NCLocator.getInstance().lookup(IParValue.class.getName());
		}
		return iwaParValue;
	}

	private static ICreateCorpQueryService createCorpQuery;

	public static ICreateCorpQueryService getCreateCorpQuery() {
		if (createCorpQuery == null) {
			createCorpQuery = (ICreateCorpQueryService) NCLocator.getInstance().lookup(ICreateCorpQueryService.class.getName());
		}

		return createCorpQuery;
	}

	/**
	 * 人员变动后更新薪资福利接口
	 *
	 * @param psndoc
	 *                PsndocVO 人员VO
	 * @param pk_psndoc_sub
	 *                Object 在职、兼职、离职记录的主键
	 * @param type
	 *                int 值为：NORMARL、PARTTIME、DIMISSION，分别为：任职、兼职、离职
	 * @see nc.impl.wa.wa_009.IRecaData#updateForPsn(nc.vo.bd.b06.PsndocVO,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void updateForPsn(nc.vo.bd.b06.PsndocVO data, String pk_corp, UFDate changedDate, String pk_psndoc_sub, int type) throws nc.vo.pub.BusinessException {

		try {
			String isUpdateNow = getWaParValue().getParaString(pk_corp, "WA-PSN001").toString();
			if (isUpdateNow == null || isUpdateNow.length() == 0) {
				throw new BusinessException("isUpdateNow param is null");
			}

			if (!(new UFBoolean(isUpdateNow)).booleanValue()) {
				Logger.info("======WA-PSN001=N，不立即反应");
				return;
			}

			/**
			 * v57以后，返聘人员的changedDate 有可能为空
			 */
//			if (changedDate == null) {
//			throw new BusinessException("changedDate is null");
//			}

			String sChangedDate = null;
			if(changedDate!=null){
				sChangedDate = changedDate.toString();
			}


			String benchmarkDay = getWaParValue().getParaString(pk_corp, "WA-PSN002").toString();
			if (benchmarkDay == null || benchmarkDay.length() == 0) {
				throw new BusinessException("WA-PSN002 is null");
			}

			if (benchmarkDay.length() == 1) {
				benchmarkDay = "0" + benchmarkDay;
			}
			Logger.info("======pk_corp=" + pk_corp + ",benchmarkDay=" + benchmarkDay + ",changedDate=" + sChangedDate + ",pk_psndoc=" + data.getPk_psndoc() + ",pk_psndoc_sub=" + pk_psndoc_sub
					+ ",type=" + type);

			DataDMO dmo = new DataDMO();

			// 如果薪资期间开始日期比立即变化日期晚，则需要更新，如薪资期间：2007-03-01至2007-03-31，立即变化日期为2007-02-10；
			// 或者立即变化日在2007-03-01至2007-03-31之间，需要更新，大于2007-03-31的则不需要更新
			// 查询所有未审核的符合上述两种情况的薪资类别
			PeriodsetVO[] waPeriods = dmo.queryWaclassForPsnChanged(sChangedDate, pk_corp);
			Vector vWaClassid = getWaClassForPsnChanged(waPeriods, changedDate, benchmarkDay);
			String waClassSQL = getWaClassidSQL(vWaClassid);

			boolean isEnabledBM = getCreateCorpQuery().isEnabled(pk_corp, "BM");
			String bmClassSQL = "0>1";

			if (isEnabledBM) {
				PeriodVO[] bmPeriods = dmo.queryBmclassForPsnChanged(sChangedDate, pk_corp);
				Vector<PeriodVO> vBmClassid = getBmClassForPsnChanged(bmPeriods, changedDate, benchmarkDay);
				bmClassSQL = getBmClassidSQL(vBmClassid);
			}

			dmo.updateForPsn(data.getPk_psndoc(), pk_psndoc_sub, type, waClassSQL, bmClassSQL, isEnabledBM);

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
	}

	private String getWaClassidSQL(Vector vecWaclass) {
		String sqlWaClass = "0>1";

		if (vecWaclass != null && vecWaclass.size() > 0) {
			sqlWaClass = "";
			for (int i = 0; i < vecWaclass.size() - 1; i++) {
				PeriodsetVO period = (PeriodsetVO) vecWaclass.elementAt(i);
				sqlWaClass += "(classid='" + period.getClassID() + "' and cyear ='" + period.getCYear() + "' and cperiod='" + period.getCPeriod() + "') or ";
			}
			PeriodsetVO lastPeriod = (PeriodsetVO) vecWaclass.elementAt(vecWaclass.size() - 1);
			sqlWaClass += "(classid='" + lastPeriod.getClassID() + "' and cyear ='" + lastPeriod.getCYear() + "' and cperiod='" + lastPeriod.getCPeriod() + "')";
		}
		return " ( " + sqlWaClass + " ) ";
	}

	private String getBmClassidSQL(Vector vecBmclass) {
		String sqlBmClass = "0>1";

		if (vecBmclass != null && vecBmclass.size() > 0) {
			sqlBmClass = "";
			for (int i = 0; i < vecBmclass.size() - 1; i++) {
				nc.vo.bm.bm_001.PeriodVO period = (nc.vo.bm.bm_001.PeriodVO) vecBmclass.elementAt(i);
				sqlBmClass += "(bmclassid='" + period.getBmclassid() + "' and cfundyear='" + period.getCyear() + "' and cfundmonth='" + period.getCperiod() + "') or ";
			}
			nc.vo.bm.bm_001.PeriodVO lastPeriod = (nc.vo.bm.bm_001.PeriodVO) vecBmclass.elementAt(vecBmclass.size() - 1);
			sqlBmClass += "(bmclassid='" + lastPeriod.getBmclassid() + "' and cfundyear='" + lastPeriod.getCyear() + "' and cfundmonth='" + lastPeriod.getCperiod() + "') ";
		}
		return " ( " + sqlBmClass + " ) ";
	}

	private Vector getBmClassForPsnChanged(nc.vo.bm.bm_001.PeriodVO[] bmPeriods, UFDate changedDate, String benchmarkDay) {
		Vector<PeriodVO> vBmClassid = new Vector<PeriodVO>();

		if (bmPeriods == null || bmPeriods.length == 0) {
			return null;
		}

		/**
		 * 如果changedDate==null ，则所有的期间都是有效的
		 */
		if(changedDate==null){
			for (PeriodVO period : bmPeriods) {
				vBmClassid.addElement(period);
			}		
			return vBmClassid;
		}

		for (PeriodVO period : bmPeriods) {
			// 福利类别对应福利期间的开始日期
			UFDate beginDate = new UFDate(period.getCyear() + "-" + period.getCperiod() + "-" + "01");
			// 福利类别对应福利期间的结束日期
			UFDate endDate = new UFDate(period.getCyear() + "-" + period.getCperiod() + "-" + beginDate.getDaysMonth());
			// 立即变化的基准日期
			// UFDate benchmarkDate = new
			// UFDate(beginDate.getYear()+"-"+period.getCperiod()+"-"+benchmarkDay);
			UFDate benchmarkDate = getBenchmarkDate(beginDate.getYear() + "", period.getCperiod(), benchmarkDay);

			if (changedDate.before(beginDate)) {
				vBmClassid.addElement(period);
			} else if (beginDate.compareTo(changedDate) <= 0 && endDate.compareTo(changedDate) >= 0) {
				if (changedDate.compareTo(benchmarkDate) <= 0) {
					vBmClassid.addElement(period);
				}
			}
		}
		return vBmClassid;
	}

	/**
	 * 获得基准日期
	 * 解决的问题：如果立即变化日参数设置为31日，当月只有30天情况，再UFDate(sYear+"-"+sMonth+"-"+sDay)构造基准日期会抛IllegalArgumentException
	 *
	 * @param sYear
	 *                String 薪资年度
	 * @param sMonth
	 *                String 薪资期间
	 * @param sDay
	 *                String 立即变化基准日
	 * @return benchmarkDate UFDate 结合期间的立即变化基准日期
	 * @author zhoucx 2007-11-27 上午09:18:09
	 */
	private UFDate getBenchmarkDate(String sYear, String sMonth, String sDay) {
		int year = Integer.parseInt(sYear);
		int month = Integer.parseInt(sMonth);
		int day = Integer.parseInt(sDay);
		UFDate benchmarkDate = null;

		int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int LEAP_MONTH_LENGTH[] = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int daymax = UFDate.isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1] : MONTH_LENGTH[month - 1];

		if (day > daymax) {
			benchmarkDate = new UFDate(sYear + "-" + sMonth + "-" + daymax);
		} else {
			benchmarkDate = new UFDate(sYear + "-" + sMonth + "-" + sDay);
		}

		return benchmarkDate;
	}

	/**
	 * 变化日期为空。则所有的期间都是有效的
	 * 
	 * 变化日期不为空，则需要比较（与原来的一样）
	 * @param periods
	 * @param changedDate
	 * @param benchmarkDay
	 * @return
	 */
	private Vector getWaClassForPsnChanged(PeriodsetVO[] periods, UFDate changedDate, String benchmarkDay) {
		Vector<PeriodsetVO> vClassid = new Vector<PeriodsetVO>();


		if (periods == null || periods.length == 0) {
			return null;
		}


		/**
		 * 变化日期为空。则所有的期间都是有效的
		 */
		if(changedDate==null){
			for (PeriodsetVO period : periods) {
				vClassid.addElement(period);
			}		
			return  vClassid ;
		}

		for (PeriodsetVO period : periods) {
			UFDate beginDate = new UFDate(period.getCStartDate());
			UFDate endDate = new UFDate(period.getCEndDate());
			UFDate  benchmarkDate =  getBenchmarkDate(String.valueOf(changedDate.getYear()),changedDate.getStrMonth(),benchmarkDay);

			if(isAdd(changedDate, benchmarkDate, beginDate, endDate)){
				vClassid.addElement(period);
			}
		}
		// 过滤需要更新的类别
//		for (PeriodsetVO period : periods) {
//		UFDate beginDate = new UFDate(period.getCStartDate());
//		UFDate endDate = new UFDate(period.getCEndDate());

//		// 立即变化业务日期大于薪资期间开始日期，比如："2007-07-01" >= "2007-06-10"，则需要更新
//		if (changedDate.before(beginDate)) {
//		vClassid.addElement(period);
//		}
//		// 立即变化业务日期在薪资期间开始日期、结束日期内，立即变化业务日期晚于薪资期间结束日期的不予考虑，下月变化
//		else if (beginDate.compareTo(changedDate) <= 0 && endDate.compareTo(changedDate) >= 0) {
//		// 薪资期间开始日期、结束日期不跨月
//		if (beginDate.getMonth() == endDate.getMonth()) {
//		// 基准日期：薪资期间年月+立即变化日
//		// UFDate benchmarkDate = new
//		// UFDate(beginDate.getYear()+"-"+beginDate.getStrMonth()+"-"+benchmarkDay);
//		UFDate benchmarkDate = getBenchmarkDate(beginDate.getYear() + "", beginDate.getStrMonth(), benchmarkDay);

//		// 当月如果，基准日期比薪资期间开始日期早，或者比薪资期间结束日期晚，说明当月存在>1个薪资期间，需要立即变化
//		if (benchmarkDate.before(beginDate) || benchmarkDate.after(endDate)) {
//		vClassid.addElement(period);
//		} else {
//		if (changedDate.compareTo(benchmarkDate) <= 0) {
//		vClassid.addElement(period);
//		}
//		}
//		}
//		// 薪资期间开始日期、结束日期跨月
//		else {
//		int betweenDays = UFDate.getDaysBetween(beginDate, endDate);
//		// 薪资期间开始日期、结束日期，之间超过31天，即薪资期间跨多个月
//		if (betweenDays <= 31) {
//		String beginDay = beginDate.getStrDay();
//		// String endDay = endDate.getStrDay();

//		// 基准日期：薪资期间年月+立即变化日
//		UFDate benchmarkDate = null;
//		if (Integer.parseInt(beginDay) <= Integer.parseInt(benchmarkDay)) {
//		// benchmarkDate = new
//		// UFDate(beginDate.getYear()+"-"+beginDate.getStrMonth()+"-"+benchmarkDay);
//		benchmarkDate = getBenchmarkDate(beginDate.getYear() + "", beginDate.getStrMonth(), benchmarkDay);
//		} else {
//		// benchmarkDate = new
//		// UFDate(endDate.getYear()+"-"+endDate.getStrMonth()+"-"+benchmarkDay);
//		benchmarkDate = getBenchmarkDate(endDate.getYear() + "", endDate.getStrMonth(), benchmarkDay);
//		}

//		if (changedDate.compareTo(benchmarkDate) <= 0) {
//		vClassid.addElement(period);
//		}
//		} else {
//		vClassid.addElement(period);
//		}
//		}
//		}
//		}
		return vClassid;
	}

	private boolean  isAdd(UFDate changedDate,UFDate benchmarkDate,UFDate begindate,UFDate endDate){
		UFDate compareDate = 	getCompareDate(benchmarkDate,begindate,endDate);

		if(changedDate.compareTo(compareDate)<=0){
			return true;
		}else{
			return false;
		}
	}

	private UFDate getCompareDate(UFDate benchmarkDate,UFDate begindate,UFDate endDate){
		//benchmarkDate 在 begindate 之前
		if(benchmarkDate.before(begindate)){
			return begindate;

		}else if(benchmarkDate.compareTo(begindate)>=0 && benchmarkDate.compareTo(endDate)<=0 ){
			//benchmarkDate 在 begindate 与endDate 之中
			return  benchmarkDate;
		}else{
			// benchmarkDate 在 endDate 之后    		
			return endDate;
		}
	}

	/**
	 * 考勤系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void updateTbm_base(String tbmYear, String tbmPeriod, java.lang.String[][] waItemBase, nc.vo.wa.wa_009.ReCacuVO aReCacuVO, int tbmScale) throws Exception {
		try {
			nc.impl.wa.func.WaFuncDMO dmo = new nc.impl.wa.func.WaFuncDMO();
			dmo.updateTbm_base(tbmYear, tbmPeriod, waItemBase, aReCacuVO, tbmScale);
		} catch (Exception e) {
			nc.bs.logging.Logger.error("tbm base error!");
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000027")/*
			 * @res
			 * "从考勤系统取数时出错!"
			 */);
		}
	}

	/**
	 * 考勤系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void updateTbm_special(String tbmYear, String tbmPeriod, java.lang.String[][] waItemSpecial, nc.vo.wa.wa_009.ReCacuVO aReCacuVO, int tbmScale) throws Exception {
		try {
			nc.impl.wa.func.WaFuncDMO dmo = new nc.impl.wa.func.WaFuncDMO();
			dmo.updateTbm_special(tbmYear, tbmPeriod, waItemSpecial, aReCacuVO, tbmScale);
		} catch (Exception e) {
			nc.bs.logging.Logger.error("tbm special error!");
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000027")/*
			 * @res
			 * "从考勤系统取数时出错!"
			 */);
		}
	}

	/**
	 * 考勤系统。 创建日期：(2001-6-18 14:15:51)
	 *
	 * @return java.lang.Exception[]
	 * @param gzlbId
	 *                java.lang.String
	 * @param waYear
	 *                java.lang.String
	 * @param waPeriod
	 *                java.lang.String
	 * @param appendItems
	 *                java.lang.String[]
	 */
	private void updateTbm_work(String tbmYear, String tbmPeriod, java.lang.String[][] waItemWork, nc.vo.wa.wa_009.ReCacuVO aReCacuVO, int tbmScale) throws Exception {
		try {
			nc.impl.wa.func.WaFuncDMO dmo = new nc.impl.wa.func.WaFuncDMO();
			dmo.updateTbm_work(tbmYear, tbmPeriod, waItemWork, aReCacuVO, tbmScale);
		} catch (Exception e) {
			nc.bs.logging.Logger.error("tbm work error!");
			reportException(e);
			throw new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000027")/*
			 * @res
			 * "从考勤系统取数时出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#updateVO(nc.vo.wa.wa_009.DataVO,
	 *      java.lang.String[][], java.lang.String[], nc.vo.wa.wa_009.ReCacuVO)
	 */
	public int updateVO(DataVO dataVO, String[][] colNameAndValue, String[] colType, ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		int re = -5;
		try {
			re = getcheckstate(dataVO.getClassid(), dataVO.getCyear(), dataVO.getCperiod());

			if (re == 0 || re == -1) {
				DataDMO dmo = new DataDMO();
				if (dataVO.getIstopFlag().intValue() == 1) // 停发
				{
					dmo.updateVO(dataVO, colNameAndValue, colType, aRecaVO);
					dmo.deletePsn("wa_dataz", dataVO);

					if (!aRecaVO.getCurrentBO().isSingleMain()) // 主辅币
					{
						dmo.deletePsn("wa_dataf", dataVO);
					}
				} else {
					Integer istopflag = dmo.findStopflagForPk(dataVO.getPrimaryKey());
					dmo.updateVO(dataVO, colNameAndValue, colType, aRecaVO);
					if (istopflag != null && istopflag.intValue() == 1) {
						// 由停发修改为不停发，需要插入datax和dataf表数据
						dmo.recorveDataZorDataY("wa_dataz", dataVO);

						if (!aRecaVO.getCurrentBO().isSingleMain() && !aRecaVO.getCurrentBO().isZhuBi())// 主辅币
						{
							dmo.recorveDataZorDataY("wa_dataf", dataVO);
						}
					}
				}

				updateWa_Tax(dataVO.getIstopFlag().intValue(), dataVO.getClassid(), dataVO.getCyear(), dataVO.getCperiod(), dataVO.getPsnid(), dataVO.getPsnbasid());
			}

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::update(DataVO) Exception!");
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#updateWa_Tax(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void updateWa_Tax(int istopflag, String gzlbId, String waYear, String waPeriod, String psnId) throws nc.vo.pub.BusinessException {
		try {
			nc.impl.wa.wa_013.TaxDMO taxDmo = new nc.impl.wa.wa_013.TaxDMO();
			if (istopflag == 1) {
				taxDmo.delete(gzlbId, waYear, waPeriod, psnId);
			} else {
				nc.vo.wa.wa_013.TaxVO taxVos[] = taxDmo.queryAll(gzlbId, waYear, waPeriod, " psnid='" + psnId + "' ", null);
				if (taxVos == null || taxVos.length < 1) {
					nc.vo.wa.wa_013.TaxVO taxVo = new nc.vo.wa.wa_013.TaxVO();
					taxVo.setPk_wa_class(gzlbId);
					taxVo.setVcalyear(waYear);
					taxVo.setVcalmonth(waPeriod);
					taxVo.setPsnid(psnId);

					taxDmo.insert(taxVo);
				}
			}
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::updateWa_Tax Exception!");
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#updateWa_Tax(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public void updateWa_Tax(int istopflag, String gzlbId, String waYear, String waPeriod, String psnId, String psnbasid) throws nc.vo.pub.BusinessException {
		try {
			nc.impl.wa.wa_013.TaxDMO taxDmo = new nc.impl.wa.wa_013.TaxDMO();
			if (istopflag == 1) {
				taxDmo.delete(gzlbId, waYear, waPeriod, psnId);
			} else {
				nc.vo.wa.wa_013.TaxVO taxVos[] = taxDmo.queryAll(gzlbId, waYear, waPeriod, " psnid='" + psnId + "' ", null);
				if (taxVos == null || taxVos.length < 1) {
					nc.vo.wa.wa_013.TaxVO taxVo = new nc.vo.wa.wa_013.TaxVO();
					taxVo.setPk_wa_class(gzlbId);
					taxVo.setVcalyear(waYear);
					taxVo.setVcalmonth(waPeriod);
					taxVo.setPsnid(psnId);
					taxVo.setPsnbasid(psnbasid);

					taxDmo.insert(taxVo);
				}
			}
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::updateWa_Tax Exception!");
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#caculatePreparing(nc.vo.wa.wa_009.ReCacuVO)
	 */
	public void caculatePreparing(ReCacuVO aRecaVO) throws nc.vo.pub.BusinessException {

		try {
			DataDMO dmo = new DataDMO();
			dmo.caculatePreparing(aRecaVO);

			dmo.updateNestInfo(aRecaVO);

			ClassitemDMO itemdmo = new ClassitemDMO();
			ClassitemVO[] itemvos = itemdmo.queryUserItemByPKclass(aRecaVO.getWaClassVO().getPrimaryKey(), aRecaVO.getWaYear(), aRecaVO.getWaPeriod());
			dmo.ResetCalculateData(aRecaVO, itemvos);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				BusinessException new_name = (BusinessException) e;
				throw new_name;
			} else {
				throw new nc.vo.pub.BusinessException("DataImpl:caculatePreparing(ReCacuVO aRecaVO)");
			}
		}

	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#checkHasPsn(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean checkHasPsn(String waclsid, String year, String period, String deptpower) throws nc.vo.pub.BusinessException {

		try {
			DataDMO dmo = new DataDMO();
			return dmo.checkHasPsn(waclsid, year, period, deptpower);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000035")/*
			 * @res
			 * "判断薪资类别下是否有人时出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#deDuctThisTax(nc.vo.wa.wa_009.ReCacuVO)
	 */
	public void deDuctThisTax(ReCacuVO vo) throws nc.vo.pub.BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			dmo.deDuctThisTax(vo);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000036")/*
			 * @res
			 * "取消发放后把本薪资项目的扣税项目时出错!"
			 */);
		}
	}

	public void deleteWaDataX(String tableName, String psnId, ReCacuVO aRecaVO) throws BusinessException {

		try {
			WaclassVO waClassVO = aRecaVO.getWaClassVO();
			String gzlbId = waClassVO.getPrimaryKey();

			String waYear = aRecaVO.getWaYear();
			String waPeriod = aRecaVO.getWaPeriod();

			DataDMO dmo = new DataDMO();
			dmo.delete(tableName, gzlbId, waYear, waPeriod, psnId);

			if (tableName.equalsIgnoreCase("wa_data")) {

				/**
				 * Modified by Young 2006-05-31 Start
				 */
				// 是否安装了员工信息模块
				boolean installHI = SFServiceFacility.getCreateCorpQueryService().isEnabled(waClassVO.getPk_corp(), "HI");

				if (installHI) {
					dmo.deleteHiWa(gzlbId, waYear, waPeriod, psnId);
				}
				/**
				 * Modified by Young 2006-05-31 End
				 */

				dmo.delete("wa_dataz", gzlbId, waYear, waPeriod, psnId);

				if (!aRecaVO.getCurrentBO().isSingleMain())// 主辅币
				{
					dmo.delete("wa_dataf", gzlbId, waYear, waPeriod, psnId);
				}

			}

			nc.impl.wa.wa_013.TaxDMO taxDmo = new nc.impl.wa.wa_013.TaxDMO();
			// nc.vo.wa.wa_013.TaxVO taxVo = new nc.vo.wa.wa_013.TaxVO();

			taxDmo.delete(gzlbId, waYear, waPeriod, psnId);

			dmo.delete("wa_data_dept", gzlbId, waYear, waPeriod, psnId);
		} catch (Exception e) {
			reportException(e);
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000007")/*
			 * @res
			 * "删除薪资数据表中人员时出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#havePsnChecked(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean havePsnChecked(String gzlbId, String waYear, String waPeriod, String swhere) throws nc.vo.pub.BusinessException {
		boolean re = false;
		try {
			DataDMO dmo = new DataDMO();
			re = dmo.havePsnChecked(gzlbId, waYear, waPeriod, swhere);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::havePsnChecked() Exception!");
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#havePsnRecacued(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean havePsnRecacued(String gzlbId, String waYear, String waPeriod, String swhere) throws nc.vo.pub.BusinessException {

		try {

			DataDMO dmo = new DataDMO();
			return dmo.havePsnRecacued(gzlbId, waYear, waPeriod, swhere);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000037")/*
			 * @res
			 * "判断是否有人计算完毕时出错!"
			 */);
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#isHaveBill(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean isHaveBill(String gzlbId, String waYear, String waPeriod) throws nc.vo.pub.BusinessException {
		boolean re = false;
		try {
			DataDMO dmo = new DataDMO();
			re = dmo.isHaveBill(gzlbId, waYear, waPeriod);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::isHaveBill() Exception!");
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCancelCheckForLock(nc.vo.wa.wa_009.ReCacuVO,
	 *      java.lang.String, java.lang.Boolean)
	 */
	public WaclassVO onCancelCheckForLock(WaclassVO waclassVO, String swhere, Boolean needRecheck, String selectConditon) throws nc.vo.pub.BusinessException {

		// 查看类别状态是否发生变化
		checkWaClassStateChange(waclassVO, selectConditon);

		String gzlbId = waclassVO.getPrimaryKey();
		String waYear = waclassVO.getCurrentPeriodVO().getCyear();
		String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();



		// 审批流审批的类别，如果在审批中的单据则不能取消，//如不需要复审且已经增加到审批单据中的，则不能取消审核
		nc.vo.wa.wa_019.PeriodVO period = waclassVO.getCurrentPeriodVO();
		if (!needRecheck && period != null && (period.getApprovetype().intValue() == 1)) {
			if (period.getIsapproved().intValue() == 1) {
				throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000307"));// "该薪资类别已经审批通过，无法取消审核！"

			} else {
				Boolean isCanCancel = new PayrollImpl().isCanCancel(gzlbId, waYear, waPeriod);
				if (!isCanCancel.booleanValue()) {
					throw new BusinessException(WaLangUtil.getStrByID("60131004", "UPP60131004-000308"));// "该薪资类别已经加到审批单据中，无法取消审核！"

				}
			}
		}

		//设置业务锁
		boolean lockPeriod  = false;
		lockPeriod = nc.bs.uap.lock.PKLock.getInstance().acquireLock(period.getPk_wa_period(), PubEnv.getPk_user(), null);
		if (!lockPeriod) {
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000303")/*
			 * @res
			 * "有人正在处理该数据，请稍候再试！"
			 */);
		}
		try {
			if (swhere == null) {
				swhere = "";
			}
			DataDMO dmo = new DataDMO();
			dmo.updateCheckFlag(gzlbId, waYear, waPeriod, "0", swhere, "icheckflag");// 弃审部分
			if (!needRecheck.booleanValue()) {
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "0" });
			}
			dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "icheckflag" }, new String[] { "0" });// onCancelCheck(gzlbId,waYear,waPeriod);


		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}finally{
			//打开业务锁
			if(lockPeriod)
				PKLock.getInstance().releaseLock(period.getPk_wa_period(),  PubEnv.getPk_user(), null);

		}

		return getNewWaclassVOWithState(waclassVO, selectConditon);
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCheck(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	public WaclassVO onCheck(WaclassVO waclassVO, Boolean needRecheck) throws nc.vo.pub.BusinessException {


		// 查看类别状态是否发生变化
		checkWaClassStateChange(waclassVO, null);

		String gzlbId = waclassVO.getPrimaryKey();
		String waYear = waclassVO.getCurrentPeriodVO().getCyear();
		String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();
		//设置业务锁
		boolean lockPeriod  = false;
		nc.vo.wa.wa_019.PeriodVO period = waclassVO.getCurrentPeriodVO();
		lockPeriod = nc.bs.uap.lock.PKLock.getInstance().acquireLock(period.getPk_wa_period(), PubEnv.getPk_user(), null);
		if (!lockPeriod) {
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000303")/*
			 * @res
			 * "有人正在处理该数据，请稍候再试！"
			 */);
		}
		try {
			DataDMO dmo = new DataDMO();
			// 先设置wa_data的审核标志
			dmo.updateCheckFlag(gzlbId, waYear, waPeriod, "1", "", "icheckflag");
			// 再设置wa_period的
			dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "icheckflag" }, new String[] { "1" });// onCheck(gzlbId,waYear,waPeriod);
			if (!needRecheck.booleanValue()) {
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "1" });// 一起复审过
			}

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}finally{
			//打开业务锁
			if(lockPeriod)
				PKLock.getInstance().releaseLock(period.getPk_wa_period(),  PubEnv.getPk_user(), null);

		}

		return getNewWaclassVOWithState(waclassVO, null);

	}

	/**
	 * 得到状态变化了的VO
	 *
	 * @author zhangg on 2009-8-13
	 * @param waclassVO
	 * @param wa_data_where
	 * @return
	 * @throws BusinessException
	 */
	private WaclassVO getNewWaclassVOWithState(WaclassVO waclassVO, String wa_data_where) throws BusinessException {
		WaGlobalVO globalVO = new WaGlobalVO();
		globalVO.setWaClassPK(waclassVO.getPrimaryKey());
		globalVO.setWaYear(waclassVO.getCurrentPeriodVO().getCyear());
		globalVO.setWaPeriod(waclassVO.getCurrentPeriodVO().getCperiod());
		globalVO.setCurUserid(waclassVO.getCurUserid());

		return WaClassStateHelper.getWaclassVOWithState(globalVO, wa_data_where);
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCheck(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Boolean)
	 */
	public WaclassVO onCheck(WaclassVO waclassVO, String swhere, Boolean needRecheck, String selectCondtion) throws nc.vo.pub.BusinessException {

		try {

			String gzlbId = waclassVO.getPrimaryKey();
			String waYear = waclassVO.getCurrentPeriodVO().getCyear();
			String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();

//			if (swhere == null || swhere.trim().length() == 0) {
//			return onCheck(waclassVO, needRecheck);
//			}

			// 查看类别状态是否发生变化
			checkWaClassStateChange(waclassVO, swhere);
			DataDMO dmo = new DataDMO();

			String chenckwhere = "wa_data.icheckflag = 0 and wa_data.istopflag = 0 and ( wa_data.irecaculateflag = 1 or wa_data.irecaculateflag = 2 )  ";

			swhere = chenckwhere + swhere;
			// 先设置wa_data的审核标志
			dmo.updateCheckFlag(gzlbId, waYear, waPeriod, "1", swhere, "icheckflag");
			if (!dmo.havePsnNotCheck(gzlbId, waYear, waPeriod, "", false)) {
				// 已经全部审核完，则设置wa_period的
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "icheckflag" }, new String[] { "1" });// onCheck(gzlbId,waYear,waPeriod);
				if (!needRecheck.booleanValue()) {
					dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "1" });// 一起复审过
				}
			}
			return getNewWaclassVOWithState(waclassVO, selectCondtion);

		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#queryAllGroupAndClassIDS(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public Hashtable queryAllGroupAndClassIDS(String pk_waclass, String accYear, String accPeriod) throws nc.vo.pub.BusinessException {

		Hashtable h = null;
		try {
			nc.impl.wa.wa_001_03.TaxgroupDMO tgDmo = new nc.impl.wa.wa_001_03.TaxgroupDMO();
			h = tgDmo.queryAllGroupAndClassIDS(pk_waclass);
			Enumeration<String> en = h.keys();
			PeriodDMO pDmo = new PeriodDMO();
			String[] unitTaxWaPks = null;
			String[] unitTaxWaPks_waPeriod = null;
			String key = null;
			while (en.hasMoreElements() == true) {
				key = en.nextElement();
				unitTaxWaPks = (String[]) h.get(key);
				if (unitTaxWaPks != null && unitTaxWaPks.length > 0) {
					unitTaxWaPks_waPeriod = pDmo.queryAllByClassIdAndAccPeriod(unitTaxWaPks, accYear, accPeriod);
					h.put(key, unitTaxWaPks_waPeriod);
				}
			}
		} catch (Exception ex) {
			reportException(ex);
			throw new nc.vo.pub.BusinessException(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000009")/*
			 * @res
			 * "查询参与合并计税的薪资类别时出错!"
			 */, ex);
		}
		return h;
	}

	/**
	 * 工资总额提示 创建日期：(2001-6-5)
	 *
	 * @return String
	 * @exception BusinessException
	 *                    异常说明。
	 */
	// public String tmAlert1(String waCorpPk, String gzlbId, String waYear,
	// String waPeriod, String whereStr) throws BusinessException {
	//
	// StringBuffer alertStr = new StringBuffer("");
	// //alertStr = "工资总额超出预算，是否继续！";
	// String[] strDeptTm = null;
	//
	// try {
	// DataDMO dmo = new DataDMO();
	// String cacuItem = dmo.getCacuItem(gzlbId);
	// //如果没有纳入工资总额的项目
	// if (cacuItem == null) {
	// return alertStr.toString();
	// }
	//
	// strDeptTm = dmo.getDeptTm(cacuItem, waCorpPk, gzlbId, waYear,
	// waPeriod, whereStr);
	// //没有取得部门工资总额表
	// if (strDeptTm == null) {
	// return alertStr.toString();
	// }
	// for (int i = 0; i < strDeptTm.length; i++) {
	// String[] tempStr = strDeptTm[i].split("@@@");
	//
	// String deptId = tempStr[0];
	// BigDecimal deptTm = new BigDecimal(tempStr[2]);
	// String deptname = tempStr[1];
	//
	// BigDecimal deptBuget = dmo.getDeptBuget(deptId, waYear,
	// waPeriod);
	//
	// if (deptTm.compareTo(deptBuget) > 0) {
	// if (alertStr.toString().length() > 0) {
	// alertStr.append("," + deptname);
	// } else {
	// alertStr.append(deptname);
	// }
	//
	// }
	// }
	// if (alertStr.toString().length() > 0) {
	//
	// alertStr.append(NCLangResOnserver.getInstance().getStrByID("60131004",
	// "UPP60131004-000301")/* @res "超出部门总额预算，是否继续？!" */);
	//
	// }
	// return alertStr.toString();
	// } catch (Exception ex) {
	// reportException(ex);
	// throw new nc.vo.pub.BusinessException("");
	// }
	// }
	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#tmAlert(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	/*
	 * public String tmAlert(String waCorpPk, String gzlbId, String accYear,
	 * String accPeriod, String whereStr) throws nc.vo.pub.BusinessException {
	 *
	 * //工资总额提示内容 StringBuffer alertStr = new StringBuffer("");
	 *
	 * //部门实发工资总额map Hashtable deptTmMap = new Hashtable();
	 *
	 * try { DataDMO dmo = new DataDMO(); //判断当前类别是否需要纳入工资总额，如果否，直接返回
	 *
	 * //取得需要进行工资总额计算的工资类别1。当前类别2。其他类别
	 * //其它类别取得的条件1。属于当前公司(2。此类别在所计算的期间wa_data中有工资记录，并且工资记录已经通过审核)3。此类别为纳入工资总额类别
	 * String[] classId = dmo.getClassId(waCorpPk);
	 *
	 * boolean isInclude = false; if (classId != null && classId.length > 0) {
	 * for (int i = 0; i < classId.length; i++) { if (classId[i].equals(gzlbId)) {
	 * isInclude = true; } } } //如果不包含当前类别，则当前类别不纳入工资总额，退出计算 if (!isInclude) {
	 * return alertStr.toString(); }
	 *
	 * //取得纳入工资总额类别的纳入工资总额项目 HashMap itemMap = new HashMap(); for (int i = 0; i <
	 * classId.length; i++) { String cacuItem = dmo.getCacuItem(classId[i]);
	 * //如果没有纳入工资总额的项目 if (cacuItem == null) { itemMap.put(classId[i], null); }
	 * else { itemMap.put(classId[i], cacuItem); } }
	 *
	 * //判断系统参数，看是按照薪资期间还是归属期间计算 //perioType的值为1时为按照薪资发放期间，为0时按照薪资归属期间
	 * //取得各个薪资类别的有效的薪资年度、期间列表 // SysInitImpl sysInitImpl = new SysInitImpl(); //
	 * String perioType = sysInitImpl.getParaString(waCorpPk, "WA-TMTIP02"); //
	 * if((perioType == null) || (perioType.length() <=0)){ // perioType = "1"; // } //
	 * if("1".equals(perioType)){ // year = " and (cyear ='" + waYear + "')"; //
	 * period = " and (cperiod ='" + waPeriod + "')"; // }else
	 * if("2".equals(perioType)){ // year = " and (cyear ='" + waYear + "')"; //
	 * period = " and (cperiod ='" + waPeriod + "')"; // }else{ // year = " and
	 * (cyear ='" + waYear + "')"; // period = " and (cperiod ='" + waPeriod +
	 * "')"; // } //暂时都按照薪资发放期间来提示 // String cyear = null; // String cperiod =
	 * null; // cyear = " and (cyear ='" + accYear + "')"; // cperiod = " and
	 * (cperiod ='" + accPeriod + "')"; String periodCon ="";
	 *
	 * //对各个薪资类别注意计算工资总额 for (int i = 0; i < classId.length; i++) { //
	 * 未考虑当前类别的人员条件,如果是当前类别，则按照whereStr条件筛选计算 String isCurclass = null; if
	 * (classId[i].equals(gzlbId)) { isCurclass = " and wa_dataz.classid = '" +
	 * classId[i] + "'"; } else { isCurclass = "AND icheckflag = 1 and
	 * wa_dataz.classid = '" + classId[i] + "'"; } //根据会计期间找到所有的发放期间 periodCon =
	 * dmo.getPeriodCon(accYear, accPeriod, classId[i]); //取得当前类别的部门工资总额 Object
	 * objtmp = itemMap.get(classId[i]); if( objtmp == null ) { continue; }
	 * String[] strDeptTm = dmo.getDeptTm(objtmp.toString(), waCorpPk,
	 * isCurclass, periodCon, whereStr); //合并各个类别下个部门的实发值到deptTmMap if
	 * (strDeptTm != null && strDeptTm.length > 0) { for (int j = 0; j <
	 * strDeptTm.length; j++) { String[] tempStr = strDeptTm[j].split("@@@");
	 * String deptId = tempStr[0]; // String deptName = tempStr[1]; BigDecimal
	 * deptTm = new BigDecimal(tempStr[2]);
	 *
	 * if (deptTmMap.get(deptId) == null) { deptTmMap.put(deptId, deptTm); }
	 * else { deptTmMap.put(deptId, deptTm .add((BigDecimal)
	 * deptTmMap.get(deptId))); } } } }
	 *
	 * //取得工资总额预算，与实际工资总额比较 //String[] deptBugets = dmo.getDeptBugets(waCorpPk,
	 * accYear, accPeriod);gzlbId String[] deptBugets =
	 * dmo.getDeptBugets(gzlbId, accYear, accPeriod);// if( deptBugets == null) {
	 * return null; }
	 *
	 * for (int i = 0; i < deptBugets.length; i++) { String[] tempBugetStr =
	 * deptBugets[i].split("@@@"); String deptBugetId = tempBugetStr[0]; String
	 * deptBugetName = tempBugetStr[1]; BigDecimal deptBugetTm = new
	 * BigDecimal(tempBugetStr[2]); //比较工资总额预算与实发值，如果超过预算则提示 if
	 * (deptTmMap.get(deptBugetId) != null && deptBugetTm.compareTo((BigDecimal)
	 * deptTmMap.get(deptBugetId)) < 0) { if (alertStr.toString().length() > 0) {
	 * alertStr.append("," + deptBugetName); } else {
	 * alertStr.append(deptBugetName); } } }
	 */

	// if (alertStr.toString().length() > 0) {
	// alertStr.append(NCLangResOnserver.getInstance().getStrByID("60131004",
	// "UPP60131004-000301")/* @res "超出部门总额预算，是否继续？!" */);
	// }
	/*
	 * return alertStr.toString(); } catch (Exception ex) { reportException(ex);
	 * throw new nc.vo.pub.BusinessException(ex.getMessage()); } }
	 */

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#tmAlert(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	PersistenceDAO pdao = new PersistenceDAO();

	public String tmAlert(String waCorpPk, String gzlbId, String accYear_cur, String accPeriod_cur, String whereStr) throws nc.vo.pub.BusinessException {
		// 工资总额提示内容
		WarnMessageVo wmv = new WarnMessageVo();
		try {
			DataDMO dmo = new DataDMO();
			// 取到本公司所有控制指标,对每一个薪资类别的项目
			Map cacuItem = dmo.getCacuItem(waCorpPk, accYear_cur, gzlbId, accYear_cur, accPeriod_cur);
			// Map cacuItemByCorp = dmo.getCacuItemByCorp(waCorpPk,accYear_cur);
			// 如果没有纳入工资总额的项目
			if (cacuItem.size() == 0) {
				return "";
			}
			// 对各个指标作预警处理
			getCorpWaringInfo(wmv, cacuItem, waCorpPk, accYear_cur, accPeriod_cur, whereStr, gzlbId);
			getDeptWaringInfo(wmv, cacuItem, waCorpPk, accYear_cur, accPeriod_cur, whereStr, gzlbId);
			// 如果出现预警信息，落实严格控制问题
			// 部门预警
			if (wmv.deptWarns.size() > 0) {
				// 首先取得所有的部门本年严格控制信息
				Map info = new HashMap();
				TmDeptYearPlanVO tdypvo = new TmDeptYearPlanVO();
				String sql_tem = "is_release='Y'  and theyear='" + accYear_cur + "' and pk_deptdoc in";
				int y = wmv.deptWarns.size() % 200;
				int x = 0;
				do {
					StringBuffer sb = new StringBuffer();
					sb.append(sql_tem);
					sb.append("(");
					for (; x < y; x++) {
						WarnMessage wm = (WarnMessage) wmv.deptWarns.get(x);
						String deptId = wm.unitId;
						sb.append("'");
						sb.append(deptId);
						sb.append("'");
						if (x != y - 1) {
							sb.append(",");
						}
					}
					sb.append(")");
					TmDeptYearPlanVO[] tdypvos = (TmDeptYearPlanVO[]) pdao.retrieveByClause(tdypvo.getClass(), sb.toString());
					for (TmDeptYearPlanVO tdypvo2 : tdypvos) {
						String pk_deptdoc = tdypvo2.getPk_deptdoc();
						info.put(pk_deptdoc, tdypvo2);
					}
					y += 200;
				} while (y <= wmv.deptWarns.size());
				// 然后对所有预警信息置位。
				for (int i = 0; i < wmv.deptWarns.size(); i++) {
					WarnMessage wmvwm = (WarnMessage) wmv.deptWarns.get(i);
					if (wmvwm.isstrongcontrol.equalsIgnoreCase("Y")) {
						TmDeptYearPlanVO tdyp = (TmDeptYearPlanVO) info.get(wmvwm.unitId);
						UFBoolean yc = tdyp.getYear_control();
						UFBoolean mc = tdyp.getMonth_control();
						UFBoolean cc = tdyp.getCumulatecontrol();
						if (yc.booleanValue()) {
							if ("年度区间预警".equals(wmvwm.controlType)) {
								wmvwm.isControlStongly = "是";
								wmv.isControled = true;
							}
						}
						if (mc.booleanValue()) {
							if ("月度预警".equals(wmvwm.controlType)) {
								wmvwm.isControlStongly = "是";
								wmv.isControled = true;
							}
						}
						if (cc.booleanValue()) {
							if ("累积预警".equals(wmvwm.controlType)) {
								wmvwm.isControlStongly = "是";
								wmv.isControled = true;
							}
						}
					}
				}

			}
			// 公司预警
			if (wmv.corpWarns.size() > 0) {
				TmCorpYearPlanVO[] tcypvos = (TmCorpYearPlanVO[]) pdao.retrieveByClause(TmCorpYearPlanVO.class, "is_release='Y'  and theyear='" + accYear_cur + "' and pk_corp='" + waCorpPk + "'");
				for (int i = 0; i < wmv.corpWarns.size(); i++) {
					WarnMessage wmvwm = (WarnMessage) wmv.corpWarns.get(i);
					if (wmvwm.isstrongcontrol.equalsIgnoreCase("Y")) {
						TmCorpYearPlanVO tcyp = tcypvos[0];
						UFBoolean yc = tcyp.getYear_control();
						UFBoolean mc = tcyp.getMonth_control();
						UFBoolean cc = tcyp.getCumulatecontrol();
						if (yc.booleanValue()) {
							if ("年度区间预警".equals(wmvwm.controlType)) {
								wmvwm.isControlStongly = "是";
								wmv.isControled = true;
							}
						}
						if (mc.booleanValue()) {
							if ("月度预警".equals(wmvwm.controlType)) {
								wmvwm.isControlStongly = "是";
								wmv.isControled = true;
							}
						}
						if (cc.booleanValue()) {
							if ("累积预警".equals(wmvwm.controlType)) {
								wmvwm.isControlStongly = "是";
								wmv.isControled = true;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			reportException(ex);
			throw new nc.vo.pub.BusinessException("");
		}
		return wmv.toString();
	}

	private void addTwoMaps(Map deptTmMapSelected, Map deptTmMap) {
		Set tmSelectedSet = deptTmMapSelected.keySet();
		Iterator it = tmSelectedSet.iterator();
		while (it.hasNext()) {
			String deptId = (String) it.next();
			BigDecimal seltemp = (BigDecimal) deptTmMapSelected.get(deptId);
			BigDecimal temp = (BigDecimal) deptTmMap.get(deptId);
			if (temp != null) {
				temp = temp.add(seltemp);
				deptTmMap.put(deptId, temp);
			} else {
				deptTmMap.put(deptId, seltemp);
			}
		}
	}

	private void compareConsumAndPlan(String type, String tName, WarnMessageVo wmv, Map tmMap, Map bugetsMap, BigDecimal alarmtime, String isstrongcontrol) {
		// 取得期间工资总额预算，与实际工资总额比较
		Iterator it = bugetsMap.keySet().iterator();
		while (it.hasNext()) {
			String bugetId = (String) it.next();
			Object[] values = (Object[]) bugetsMap.get(bugetId);
			String code = (String) values[0];
			String name = (String) values[1];
			BigDecimal bugetTm = (BigDecimal) values[2];
			// 比较工资总额预算与实发值，如果超过预算则提示
			BigDecimal consume = (BigDecimal) tmMap.get(bugetId);
			if (consume != null && bugetTm.multiply(alarmtime).compareTo(consume) < 0) {
				WarnMessage wmvwm = new WarnMessage();
				wmvwm.budget = bugetTm.multiply(alarmtime);
				wmvwm.consume = consume;
				wmvwm.unitName = name;
				wmvwm.unitCode = code;
				wmvwm.tmitem = tName;
				wmvwm.controlType = type;
				wmvwm.unitId = bugetId;
				wmvwm.isstrongcontrol = isstrongcontrol;
				wmv.deptWarns.add(wmvwm);
			}
		}
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#updateArrayForKJ(java.lang.String,
	 *      java.lang.String, java.lang.String, nc.vo.wa.wa_009.DataVO)
	 */
	public void updateArrayForKJ(String waClass, String cYear, String cPeriod, nc.vo.wa.wa_009.DataVO dataVo) throws nc.vo.pub.BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			dmo.updateForKJ("wa_data", waClass, cYear, cPeriod, dataVo);
			dmo.updateForKJ("wa_dataz", waClass, cYear, cPeriod, dataVo);
			dmo.updateForKJ("wa_dataf", waClass, cYear, cPeriod, dataVo);
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
	}

	/**
	 * Added by Young 2006-03-20 Start 审核查询
	 */

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCancelCheckChg(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Boolean, java.lang.String[])
	 */
	public int onCancelCheckChg(String gzlbId, String waYear, String waPeriod, String swhere, Boolean needRecheck, String[] appendItems) throws nc.vo.pub.BusinessException {
		int re = -5;
		try {
			if (swhere == null) {
				swhere = "";
			}
			re = getcheckstate(gzlbId, waYear, waPeriod);
			if (re == 1 || re == 0 || re == 5 || re == 6 || (!needRecheck.booleanValue() && re == 2)) {
				DataDMO dmo = new DataDMO();
				dmo.updateChgCheckFlag(gzlbId, waYear, waPeriod, "0", swhere, appendItems);// 弃审部分
				if (!needRecheck.booleanValue()) {
					dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "0" });
				}
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "icheckflag" }, new String[] { "0" });// onCancelCheck(gzlbId,waYear,waPeriod);
			}
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
		return re;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#onCheckChg(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Boolean, java.lang.String[])
	 */
	public WaclassVO onCheckChg(WaclassVO waclassVO, String swhere, Boolean needRecheck, String[] appendItems, String selectCondition) throws nc.vo.pub.BusinessException {

		try {
			if (swhere == null || swhere.trim().length() == 0) {
				return onCheck(waclassVO, needRecheck);
			}

			String gzlbId = waclassVO.getPrimaryKey();
			String waYear = waclassVO.getCurrentPeriodVO().getCyear();
			String waPeriod = waclassVO.getCurrentPeriodVO().getCperiod();

			DataDMO dmo = new DataDMO();
			dmo.updateChgCheckFlag(gzlbId, waYear, waPeriod, "1", swhere, appendItems);
			if (!dmo.havePsnNotCheck(gzlbId, waYear, waPeriod, "", false)) {
				// 已经全部审核完，则设置wa_period的
				dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "icheckflag" }, new String[] { "1" });// onCheck(gzlbId,waYear,waPeriod);
				if (!needRecheck.booleanValue()) {
					dmo.updateRecaFlagAtPeriod(gzlbId, waYear, waPeriod, new String[] { "irecheckflag" }, new String[] { "1" });// 一起复审过
				}
			}
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
		return getNewWaclassVOWithState(waclassVO, selectCondition);
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see nc.impl.wa.wa_009.IRecaData#queryAllChg(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[],
	 *      java.lang.String[], java.lang.String)
	 */
	public DataVO[] queryAllChg(String gzlbId, String waYear, String waPeriod, String[] appendItems, String[] appendItemTypes, String sqlWhere) throws nc.vo.pub.BusinessException {
		try {
			return new DataDMO().queryAllChg(gzlbId, waYear, waPeriod, appendItems, appendItemTypes, sqlWhere);
		} catch (Exception e) {
			e.printStackTrace();
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
	}

	public DataVO[] queryContrast(String classid, String waYear, String waPeriod, String[] appendItems, String[] appendItemTypes, String sqlWhere) throws BusinessException {
		try {
			return new DataDMO().queryContrast(classid, waYear, waPeriod, appendItems, appendItemTypes, sqlWhere);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	public PaySlipDataVO[] queryAllForPayslip(String gzlbId, String waYear, String waPeriod, String[] appendItems, String[] appendItemTypes, String sqlWhere) throws BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			PaySlipDataVO[] datas = null;
			datas = dmo.queryAllForPayslip(gzlbId, waYear, waPeriod, appendItems, appendItemTypes, sqlWhere);

			return datas;
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::queryAllForPayslip() Exception!");
		}
	}

	/**
	 * @param vo
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 */
	public PaySlipDataVO[] queryAllForPayslip(PayslipVO vo, String sqlWhere) throws BusinessException {
		try {
			DataDMO dmo = new DataDMO();
			PaySlipDataVO[] datas = null;
			datas = dmo.queryAllForPayslip(vo, sqlWhere);

			return datas;
		} catch (Exception e) {
			reportException(e);
			throw new nc.vo.pub.BusinessException("DataImpl::queryAccForPayslip() Exception!");
		}
	}

	public PaySlipDataVO[] queryAccForPayslip(PayslipVO vo, String sqlWhere) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 此方法的主要目的是在薪资发放的时，查看单位的信息设置是否已经被超过
	 *
	 * @param wmv
	 * @param corpmap
	 * @return
	 * @throws BusinessException
	 */
	public WarnMessageVo getCorpWaringInfo(WarnMessageVo wmv, Map cacuItem, String waCorpPk, String accYear_cur, String accPeriod_cur, String whereStr, String gzlbId) throws BusinessException {
		Iterator cii = cacuItem.keySet().iterator();
		try {
			// 对各个指标作预警处理
			DataDMO dmo = new DataDMO();
			while (cii.hasNext()) {
				// 得到每一个指标的具体设置
				String targetId = (String) cii.next();
				Map tMap = (Map) cacuItem.get(targetId);
				String tName = (String) tMap.get("targetName");
				String ismonthalarm = (String) tMap.get("ismonthalarm");
				String iscumalarm = (String) tMap.get("iscumalarm");
				String isyearalarm = (String) tMap.get("isyearalarm");
				String ismcontrol = (String) tMap.get("ismcontrol");
				String isycontrol = (String) tMap.get("isycontrol");
				BigDecimal alarmtime = (BigDecimal) tMap.get("alarmtime");
				String isstrongcontrol = (String) tMap.get("isstrongcontrol");
				String computerrule = (String) tMap.get("computerule");
				// 首先计算当前选中没有的审核人员数据
				BigDecimal corpTmSelected = dmo.getCorpTmSelected(computerrule, whereStr);
				// 根据不同的具体设置进行计算。
				// 如果是年度控制
				// if ("Y".equalsIgnoreCase(isycontrol)) {
				// 如果有月度控制
				if ("Y".equalsIgnoreCase(ismcontrol)) {
					// 如果按月度预警
					if ("Y".equalsIgnoreCase(ismonthalarm)) {
						// 取得期间实际工资总额
						BigDecimal corpTm = dmo.getCorpTmYearPeriod(computerrule, waCorpPk, accYear_cur, accPeriod_cur, gzlbId);
						// 最后考量公司
						BigDecimal tmCorp = corpTm.add(corpTmSelected);
						Object[] budgets = dmo.getCorpBugetsMonth(targetId, waCorpPk, accYear_cur, accPeriod_cur);
						if (budgets != null) {
							String corpCode = (String) budgets[1];
							String corpName = (String) budgets[2];
							BigDecimal budgetCorp = (BigDecimal) budgets[3];
							if (budgetCorp.compareTo(tmCorp) < 0) {
								WarnMessage wmvwm = new WarnMessage();
								wmvwm.budget = budgetCorp;
								wmvwm.consume = tmCorp;
								wmvwm.unitCode = corpCode;
								wmvwm.unitName = corpName;
								wmvwm.tmitem = tName;
								wmvwm.controlType = "月度预警";
								wmvwm.unitId = waCorpPk;
								wmvwm.isstrongcontrol = isstrongcontrol;
								wmv.corpWarns.add(wmvwm);
							}
						}
					}
					// 如果按累积预警
					if ("Y".equalsIgnoreCase(iscumalarm)) {
						// 计算累积部门总值
						BigDecimal corpTm = dmo.getCorpTmCumPeriod(computerrule, waCorpPk, accYear_cur, accPeriod_cur, gzlbId);
						// 最后考量公司
						BigDecimal tmCorp = corpTm.add(corpTmSelected);
						Object[] budgets = dmo.getCorpBugetsCumMonth(targetId, waCorpPk, accYear_cur, accPeriod_cur);
						if (budgets != null) {
							String corpCode = (String) budgets[1];
							String corpName = (String) budgets[2];
							BigDecimal budgetCorp = (BigDecimal) budgets[3];
							if (budgetCorp.compareTo(tmCorp) < 0) {
								WarnMessage wmvwm = new WarnMessage();
								wmvwm.budget = budgetCorp;
								wmvwm.consume = tmCorp;
								wmvwm.unitName = corpName;
								wmvwm.unitCode = corpCode;
								wmvwm.unitId = waCorpPk;
								wmvwm.tmitem = tName;
								wmvwm.controlType = "累积预警";
								wmvwm.isstrongcontrol = isstrongcontrol;
								wmv.corpWarns.add(wmvwm);
							}
						}
					}
					// 如果按区间预警
					if ("Y".equalsIgnoreCase(isyearalarm) && "Y".equalsIgnoreCase(isycontrol)) {
						String accYear = accYear_cur;
						// 区间则按整年算
						// 计算累积公司总值
						BigDecimal corpTm = dmo.getCorpTmYear(computerrule, waCorpPk, accYear_cur, gzlbId);
						// 最后考量公司
						BigDecimal tmCorp = corpTm.add(corpTmSelected);
						Object[] budgets = dmo.getCorpBugetsYear(targetId, waCorpPk, accYear);
						if (budgets != null) {
							String corpCode = (String) budgets[1];
							String corpName = (String) budgets[2];
							BigDecimal budgetCorp = ((BigDecimal) budgets[3]).multiply(alarmtime);
							if (budgetCorp.compareTo(tmCorp) < 0) {
								WarnMessage wmvwm = new WarnMessage();
								wmvwm.budget = budgetCorp;
								wmvwm.consume = tmCorp;
								wmvwm.unitName = corpName;
								wmvwm.unitCode = corpCode;
								wmvwm.unitId = waCorpPk;
								wmvwm.tmitem = tName;
								wmvwm.controlType = "年度区间预警";
								wmvwm.isstrongcontrol = isstrongcontrol;
								wmv.corpWarns.add(wmvwm);
							}
						}
					}
				} else { // 如果没有月度控制
					// 如果按月度预警
					if ("Y".equalsIgnoreCase(ismonthalarm)) {
					}
					// 如果按累积预警
					if ("Y".equalsIgnoreCase(iscumalarm)) {
					}
					// 如果按区间预警
					if ("Y".equalsIgnoreCase(isyearalarm) && "Y".equalsIgnoreCase(isycontrol)) {
						String accYear = accYear_cur;
						// 区间则按整年算
						// 计算累积部门总值
						BigDecimal corpTm = dmo.getCorpTmYear(computerrule, waCorpPk, accYear_cur, gzlbId);
						// 最后考量公司
						BigDecimal tmCorp = corpTm.add(corpTmSelected);
						Object[] budgets = dmo.getCorpBugetsYear(targetId, waCorpPk, accYear);
						if (budgets != null) {
							String corpCode = (String) budgets[1];
							String corpName = (String) budgets[2];
							BigDecimal budgetCorp = ((BigDecimal) budgets[3]).multiply(alarmtime);
							if (budgetCorp.compareTo(tmCorp) < 0) {
								WarnMessage wmvwm = new WarnMessage();
								wmvwm.budget = budgetCorp;
								wmvwm.consume = tmCorp;
								wmvwm.unitName = corpName;
								wmvwm.unitCode = corpCode;
								wmvwm.unitId = waCorpPk;
								wmvwm.tmitem = tName;
								wmvwm.controlType = "年度区间预警";
								wmvwm.isstrongcontrol = isstrongcontrol;
								wmv.corpWarns.add(wmvwm);
							}
						}
					}
				}
				// }
			}
		} catch (Exception ex) {
			throw new nc.vo.pub.BusinessException(WaLangUtil.getStrByID("UPP60131004-000409"));// 处理单位信息时发生问题!
		}
		return wmv;
	}

	/**
	 * 此方法的主要功能是对部门的预警信息进行判断
	 *
	 * @param wmv
	 * @param cacuItem
	 * @param waCorpPk
	 * @param accYear_cur
	 * @param accPeriod_cur
	 * @param whereStr
	 * @return
	 * @throws BusinessException
	 */
	public WarnMessageVo getDeptWaringInfo(WarnMessageVo wmv, Map cacuItemByCorp, String waCorpPk, String accYear_cur, String accPeriod_cur, String whereStr, String gzlbId) throws BusinessException {
		try {
			// 现在对没处理的部门指标进行计算
			Iterator cii = cacuItemByCorp.keySet().iterator();
			DataDMO dmo = new DataDMO();
			// 对各个指标作预警处理
			while (cii.hasNext()) {
				// 得到每一个指标的具体设置
				String targetId = (String) cii.next();
				Map tMap = (Map) cacuItemByCorp.get(targetId);
				String tName = (String) tMap.get("targetName");
				String ismonthalarm = (String) tMap.get("ismonthalarm");
				String iscumalarm = (String) tMap.get("iscumalarm");
				String isyearalarm = (String) tMap.get("isyearalarm");
				String ismcontrol = (String) tMap.get("ismcontrol");
				String isycontrol = (String) tMap.get("isycontrol");
				BigDecimal alarmtime = (BigDecimal) tMap.get("alarmtime");
				String isstrongcontrol = (String) tMap.get("isstrongcontrol");
				String computerrule = (String) tMap.get("computerule");
				// 首先计算当前选中没有的审核人员数据
				Map deptTmMapSelected = dmo.getDeptTmSelected(computerrule, whereStr);
				// 根据不同的具体设置进行计算。
				// 如果是年度控制
				// if ("Y".equalsIgnoreCase(isycontrol)) {
				// 如果有月度控制
				if ("Y".equalsIgnoreCase(ismcontrol)) {
					// 如果按月度预警
					if ("Y".equalsIgnoreCase(ismonthalarm)) {
						// 取得期间实际工资总额
						Map deptTmMap = dmo.getDeptTmYearPeriod(computerrule, waCorpPk, accYear_cur, accPeriod_cur, gzlbId);
						addTwoMaps(deptTmMapSelected, deptTmMap);
						// 取得期间工资总额预算
						Map deptBugets = dmo.getDeptBugetsMonth(targetId, waCorpPk, accYear_cur, accPeriod_cur);
						// 进行部门预警
						compareConsumAndPlan("月度预警", tName, wmv, deptTmMap, deptBugets, new BigDecimal(1), isstrongcontrol);
					}
					// 如果按累积预警
					if ("Y".equalsIgnoreCase(iscumalarm)) {
						// 计算累积部门总值
						Map deptTmMap = new HashMap();
						deptTmMap = dmo.getDeptTmCumPeriod(computerrule, waCorpPk, accYear_cur, accPeriod_cur, gzlbId);
						addTwoMaps(deptTmMapSelected, deptTmMap);
						// 计算累积部门预算总值
						Map deptBugets = dmo.getDeptBugetsCumMonth(targetId, waCorpPk, accYear_cur, accPeriod_cur);
						// 比较
						// 进行部门预警
						compareConsumAndPlan("累积预警", tName, wmv, deptTmMap, deptBugets, new BigDecimal(1), isstrongcontrol);
					}
					// 如果按区间预警
					if ("Y".equalsIgnoreCase(isyearalarm) && "Y".equalsIgnoreCase(isycontrol)) {
						String accYear = accYear_cur;
						// 区间则按整年算
						// 计算累积部门总值
						Map deptTmMap = dmo.getDeptTmYear(computerrule, waCorpPk, accYear_cur, gzlbId);
						addTwoMaps(deptTmMapSelected, deptTmMap);
						// 得到部门年预算
						Map deptBugets = dmo.getDeptBugetsYear(targetId, waCorpPk, accYear);
						// 进行部门预警
						compareConsumAndPlan("年度区间预警", tName, wmv, deptTmMap, deptBugets, alarmtime, isstrongcontrol);
					}
				} else { // 如果没有月度控制
					// 如果按月度预警
					if ("Y".equalsIgnoreCase(ismonthalarm)) {
					}
					// 如果按累积预警
					if ("Y".equalsIgnoreCase(iscumalarm)) {
					}
					// 如果按区间预警
					if ("Y".equalsIgnoreCase(isyearalarm) && "Y".equalsIgnoreCase(isycontrol)) {
						String accYear = accYear_cur;
						// 区间则按整年算
						// 计算累积部门总值
						Map deptTmMap = dmo.getDeptTmYear(computerrule, waCorpPk, accYear_cur, gzlbId);
						addTwoMaps(deptTmMapSelected, deptTmMap);
						// 得到部门年预算
						Map deptBugets = dmo.getDeptBugetsYear(targetId, waCorpPk, accYear);
						// 进行部门预警
						compareConsumAndPlan("年度区间预警", tName, wmv, deptTmMap, deptBugets, alarmtime, isstrongcontrol);
					}
				}
				// }
			}
		} catch (Exception e) {
			throw new nc.vo.pub.BusinessException(WaLangUtil.getStrByID("UPP60131004-000410"));// 处理部门信息时发生问题!
		}
		return wmv;
	}

	public BigDecimal getCorpTmCumPeriod(String cacuItem, String waCorpPk, String accYear, String accPeriod, String gzlbId) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getCorpTmCumPeriod(cacuItem, waCorpPk, accYear, accPeriod, gzlbId);
	}

	public BigDecimal getCorpTmSelected(String cacuItem, String whereStr) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getCorpTmSelected(cacuItem, whereStr);
	}

	public BigDecimal getCorpTmYear(String cacuItem, String waCorpPk, String accYear, String gzlbId) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getCorpTmYear(cacuItem, waCorpPk, accYear, gzlbId);
	}

	public BigDecimal getCorpTmYearPeriod(String cacuItem, String waCorpPk, String accYear, String accPeriod, String gzlbId) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getCorpTmYearPeriod(cacuItem, waCorpPk, accYear, accPeriod, gzlbId);
	}

	public Map getDeptTmCumPeriod(String cacuItem, String waCorpPk, String accYear, String accPeriod, String gzlbId) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getDeptTmCumPeriod(cacuItem, waCorpPk, accYear, accPeriod, gzlbId);
	}

	public Map getDeptTmSelected(String cacuItem, String whereStr) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getDeptTmSelected(cacuItem, whereStr);
	}

	public Map getDeptTmYear(String cacuItem, String waCorpPk, String accYear, String gzlbId) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getDeptTmYear(cacuItem, waCorpPk, accYear, gzlbId);
	}

	public Map getDeptTmYearPeriod(String cacuItem, String waCorpPk, String accYear, String accPeriod, String gzlbId) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getDeptTmYearPeriod(cacuItem, waCorpPk, accYear, accPeriod, gzlbId);
	}

	public Map getCacuItem(String corpId, String year, String gzlbId, String accYear_cur, String accPeriod_cur) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getCacuItem(corpId, year, gzlbId, accYear_cur, accPeriod_cur);
	}

	public String getComputerrule(String corpId, String year, String gzlbId, String accYear_cur, String accPeriod_cur, String targetID) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.getComputerrule(corpId, year, gzlbId, accYear_cur, accPeriod_cur, targetID);
	}

	public boolean isWaItemSet(String pkItem) throws SQLException {
		DataDMO dmo = null;
		try {
			dmo = new DataDMO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmo.isWaItemSet(pkItem);
	}

	/*********************************************************************************************************
	 * 执行一个查询的SQL语句<br>
	 * Created on 2006-9-4 下午02:57:18<br>
	 * @see nc.itf.hr.frame.IPersistenceRetrieve#executeQuery(java.lang.String,
	 *      nc.jdbc.framework.processor.ResultSetProcessor)
	 ********************************************************************************************************/
	public Object executeQuery(String arg0, ResultSetProcessor arg1) throws BusinessException
	{
		PersistenceDAO dao = new PersistenceDAO();

		return dao.executeQuery(arg0, arg1);
	}

	/**
	 * (non-Javadoc)
	 * @see nc.itf.hr.wa.IRedata#getBoData(java.lang.String, java.lang.String, java.lang.String)
	 */
	public  Map getBoData(String pkcorp,String userid,String waclassid,String cyear,String cperiod) throws nc.vo.pub.BusinessException{
		WaclassVO waClassVO = NCLocator.getInstance().lookup(IWaClass.class).findWaClassByPk(waclassid);

		boolean isLocalCurrType =  isLocalCurrType(pkcorp,waClassVO.getCurrid());

		int currdigit =  getCurrDecimal(waClassVO.getCurrid());

		int state =  ((IGlobaldata) NCLocator.getInstance().lookup(IGlobaldata.class.getName())).checkState(waclassid,cyear,cperiod);

		HashMap map = new HashMap();
		map.put("waClassVO", waClassVO);
		map.put("isLocalCurrType", isLocalCurrType);
		map.put("currdigit", currdigit);
		map.put("state", state);

		return map;



	}


	public  Map getBoData(String pkcorp,String userid,UFDate m_logDate,String waclassid,String cyear,String cperiod,String datapower) throws nc.vo.pub.BusinessException{


		WaGlobalVO globalVO = new WaGlobalVO();
		globalVO.setCurPk_corp(pkcorp);
		globalVO.setCurUserid(userid);
		globalVO.setLogDate(m_logDate);
		globalVO.setWaClassPK(waclassid);
		globalVO.setWaYear(cyear);
		globalVO.setWaPeriod(cperiod);

		WaclassVO  waClassVO = WaClassStateHelper.getWaclassVOWithState(globalVO, datapower);


		int currdigit =  getCurrDecimal(waClassVO.getCurrid());

		HashMap map = new HashMap();
		map.put("waClassVO", waClassVO);
//		map.put("isLocalCurrType", isLocalCurrType);
		map.put("currdigit", currdigit);

		boolean bValue = false;
		if ("0001".equals(pkcorp)) {
			bValue = isInstall("6007");
		} else {
			bValue = ((ICreateCorpQueryService) NCLocator.getInstance().lookup(ICreateCorpQueryService.class.getName())).isEnabled(pkcorp, "HI");
		}

		map.put("HIEnable", bValue);
		return map;

	}

	private static boolean isInstall(String productCode) {
		boolean isInstall = false;
		try {
			IProductVersionQueryService productVersionQueryService = (IProductVersionQueryService) NCLocator.getInstance().lookup(IProductVersionQueryService.class.getName());
			ProductVersionVO[] productVersionVOs = productVersionQueryService.queryByProductCode(productCode);
			if (productVersionVOs != null && productVersionVOs.length > 0) {
				isInstall = true;
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return isInstall;
	}



	public  int getCurrDecimal(String currpk) throws BusinessException{
		if ((currpk != null) && (!currpk.trim().equals(""))) {
			ICurrtype 	service = (ICurrtype) NCLocator.getInstance().lookup(
					ICurrtype.class.getName());
			nc.vo.bd.b20.CurrtypeVO vo=  service.findCurrtypeVOByPK(currpk);


			if ((vo != null) && vo.getCurrdigit() != null) {
				return vo.getCurrdigit().intValue();// 指定的小数位数
			}
		}
		return 2;
	}

	public boolean isLocalCurrType(String pk_corp,String pk_currtype) throws BusinessException {
		return (pk_currtype.equalsIgnoreCase(getLocalCurrPK(pk_corp)));
	}

	public synchronized String getLocalCurrPK(String pk_corp) throws BusinessException {
		if(pk_corp == null || pk_corp.trim().length() == 0)  {
			throw new IllegalArgumentException("Parameter pk_corp is null");
		}
		return getPkLocalCurrtype(pk_corp);
	}

	private String getPkLocalCurrtype(String pk_corp) {
		String localCurrtype;
		try {
			if("0001".equalsIgnoreCase(pk_corp)) {
				localCurrtype = getPkValue(pk_corp,"BD211");
			} else {
				localCurrtype = getPkValue(pk_corp,"BD301");
			}
		} catch (Exception e) {
			throw new BDRuntimeException(nc.vo.bd.MultiLangTrans.getTransStr("MT7",
					new String[] {ResHelper.getString("bdpub",
					"UC000-0002592") /* @res "本币" */})/* 获取本币失败 */);
		}
		return localCurrtype;
	}


	public String getPkValue(String pk_corp, String initCode) throws BusinessException {
		return (String) SysinitAccessor.getInstance().getParaByPk_org(OrgnizeTypeVO.COMPANY_TYPE, pk_corp, initCode,
				UFBoolean.valueOf(true));
	}
	
	public void onWriteSj(ReCacuVO aRecaVO,boolean flag) throws BusinessException{
		try {
			XhDataDMO dmo = new XhDataDMO();
			dmo.onWriteSj(aRecaVO,flag);
			
			//并且把工资取数的都确认
			SuperDMO ddmo = new SuperDMO();
			String vyear = aRecaVO.getWaYear();
			String vperiod = aRecaVO.getWaPeriod();
			TMBVO[] bvos = (TMBVO[])ddmo.queryByWhereClause(TMBVO.class, "  isconfirm='Y' and isdeduct='Y' and isnull(dr,0)=0 and month='"+vyear+""+vperiod+"' ");
		    if(bvos!=null&&bvos.length>0){
		    	for(TMBVO bvo:bvos){
		    		bvo.setStatus(VOStatus.UPDATED);
		    		bvo.setBiscount_gz(new UFBoolean(flag));
		    	}
		    	ddmo.updateArray(bvos, new String[]{"biscount_gz"});
		    }
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}