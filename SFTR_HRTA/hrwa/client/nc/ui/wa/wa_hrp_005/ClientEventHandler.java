/**
 * 
 */
package nc.ui.wa.wa_hrp_005;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import bsh.StringUtil;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.hrp.pub.IHRPSysParam;
import nc.itf.hrwa.IFenpeiItemB;
import nc.itf.hrwa.IHRwaPub;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.ui.hrp.pub.bill.HRPEventhandle;
import nc.ui.hrp.pub.excel.ExcelOut;
import nc.ui.hrp.pub.excel.ImportExcelData;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.pub.query.QueryConditionClient;
import nc.ui.tbm.tbm_010.SouthPanel;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.wa.wa_hrp_003.MyCardPanelPRTS;
import nc.ui.wa.wa_hrp_007.AuditBillDlg;
import nc.ui.wa.wa_hrp_pub.WaPubCount;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.hrp.pf02.PerioddeptVO;
import nc.vo.hrwa.troo.DeptBonusSummariesVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.para.SysInitVO;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.sm.user.UserAndClerkVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wa.wa_020.WaDataVO;
import nc.vo.wa.wa_024.ItemVO;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrp_004.ItemSetBVO;
import nc.vo.wa.wa_hrp_005.DistributionTempVo;
import nc.vo.wa.wa_hrp_005.ExAggFenpeiHVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemBVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemHVO;

/**
 * @author 宋旨昊 2011-3-21上午10:34:30 说明：
 */
public class ClientEventHandler extends HRPEventhandle {
	private QryDlg2 m_qryDlg = null;
	private MyQueryConditionClient m_queryDialog = null;

	private static List<DistributionTempVo> tempDeptList = new ArrayList<DistributionTempVo>();

	private static Map<String, DistributionTempVo> tempDeptMap = new HashMap<String, DistributionTempVo>();

	/**
	 * @param billUI
	 * @param control
	 */
	public ClientEventHandler(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
		// TODO 自动生成构造函数存根
	}

	/**
	 * 期间处理,设置为前n月 create by dychf
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public Map<String, String> handleDate(String pk_corp, String year, String month) {
		HashMap<String, String> map = new HashMap<String, String>();

		try {
			// 获取系统预置参数：同仁奖金发放期间参数
			SysInitVO sysInitVO = SysinitAccessor.getInstance().getParaByAccuratePk_org(pk_corp, "TONGRENQJ");
			int advance = 2;
			if (sysInitVO != null && sysInitVO.getValue() != null) {
				advance = Integer.parseInt(sysInitVO.getValue());
			}
			Integer iyear = new Integer(year);
			Integer imonth = new Integer(month);

			int minus = imonth - advance;

			if (minus <= 0) {
				imonth = 12 + minus;
				iyear = iyear - 1;
			} else {
				imonth = minus;
			}
			map.put("year", iyear + "");
			map.put("month", imonth < 10 ? "0" + imonth : imonth + "");
			return map;
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			MessageDialog.showErrorDlg(this.getBillUI(), "错误", "日期处理出现异常");
			return null;
		}
	}

	@Override
	protected void onBoElse(int btn) throws Exception {
		// TODO 自动生成方法存根
		switch (btn) {
		case IHRPBtn.ExcelOut:
			onBoExcelOut();
			break;
		case IHRPBtn.ExcelImport:
			onBoReadImport();
			break;
		case IHRPBtn.COMMITREURN:
			onCommitReturn();
			break;
		case IHRPBtn.FileManage:
			onBoFileManage();
			break;
		default:
			break;
		}
	}

	protected QryDlg2 createQryDLG() {
		TemplateInfo tempinfo = getTempInfo();
		QryDlg2 dlg = new QryDlg2(this.getBillUI(), null, tempinfo);

		return dlg;
	}

	protected TemplateInfo getTempInfo() {
		ClientEnvironment ce = ClientEnvironment.getInstance();
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(ce.getCorporation().getPrimaryKey());
		tempinfo.setCurrentCorpPk(ce.getCorporation().getPrimaryKey());
		tempinfo.setFunNode("HY010102");
		tempinfo.setUserid(ce.getUser().getPrimaryKey());
		tempinfo.setNodekey("HY010102");
		return tempinfo;
	}

	public QryDlg2 getQryDlg2() {
		if (m_qryDlg == null) {
			m_qryDlg = createQryDLG();
		}
		return m_qryDlg;
	}

	public MyQueryConditionClient getQueryDialog() {
		if (m_queryDialog == null) {
			m_queryDialog = new MyQueryConditionClient(this.getBillUI());
			getQueryDialog().setTempletID(_getCorp().getPrimaryKey(), "HY010102", _getOperator(), null);
			getQueryDialog().hideNormal();
		}
		return m_queryDialog;
	}

	@Override
	protected void onBoLineIns() throws Exception {
		// TODO Auto-generated method stub

		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if (code.equals("wa_fundfenpei")) {
			return;
		}
		if (getQueryDialog().showModal() == QueryConditionClient.ID_OK) {
			int nindex = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject() != null ? Integer
					.parseInt(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject().toString()) : 0;
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < nindex; i++) {
				BillItem item = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getItemByKey("nmny" + (i + 1) + "");
				map.put(item.getKey(), item.getName());
			}
			if (map == null || map.size() <= 0)
				return;
			nc.vo.pub.query.ConditionVO[] vos = getQueryDialog().getConditionVO();
			String sql = getQueryDialog().getWhereSQL(vos);
			if (sql != null && sql.trim().length() > 0) {
				sql = sql.replace("right like", "like");
				sql = sql.replace("left like", "like");
			}
			PfDeptDLG dlg = new PfDeptDLG(this.getBillUI(), sql, map);
			FenpeiItemBVO[] oldvos = (FenpeiItemBVO[]) getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b")
					.getBodyValueVOs(FenpeiItemBVO.class.getName());
			dlg.setOldbvos(oldvos);
			int x = dlg.showModal();
			if (x == UIDialog.ID_OK) {
				FenpeiItemBVO[] rvos = dlg.getReturnvos();
				ArrayList<String> list_dept = dlg.getList_dept();
				HashMap<String, UFDouble> map_mny = dlg.getMap_mny();
				if (list_dept != null && list_dept.size() > 0 && map_mny != null && map_mny.size() > 0) {
					int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_fenpei_b").getRowCount();
					for (int i = 0; i < rowcount; i++) {
						Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "pk_wa_dept");
						if (value != null && value.toString().trim().length() > 0 && list_dept.contains(value.toString().trim())) {
							String[] keys = map_mny.keySet().toArray(new String[0]);
							for (String key : keys) {
								getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(map_mny.get(key), i, key);
							}
						}
						UFDouble nmny = new UFDouble(0);
						for (int j = 0; j < nindex; j++) {
							Object valueb = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b")
									.getValueAt(i, "nmny" + (j + 1) + "");
							nmny = nmny.add(valueb != null ? new UFDouble(valueb.toString()) : new UFDouble(0));
						}
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(nmny, i, "nmny");
					}
				}
				if (rvos != null && rvos.length > 0) {
					for (int i = 0; i < rvos.length; i++) {
						onBoLineAdd();
						int rows = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_fenpei_b").getSelectedRow();
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setBodyRowVO(rvos[i], rows);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").execEditFormulas(rows);
						UFDouble nmny = new UFDouble(0);
						for (int j = 0; j < nindex; j++) {
							Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b")
									.getValueAt(rows, "nmny" + (j + 1) + "");
							nmny = nmny.add(value != null ? new UFDouble(value.toString()) : new UFDouble(0));
						}
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(nmny, rows, "nmny");
					}
				}
			}
		}
	}

	/**
	 * 文件管理
	 * 
	 * @author 宋旨昊 2011-3-29上午09:20:30
	 */
	protected void onBoFileManage() {
		FileManageUI.showInDlg(getBillUI(), "模板下载", "部门维护薪酬项目");
	}

	/**
	 * @author 宋旨昊 2011-3-21下午01:22:34
	 * @throws Exception
	 *             说明：提交回撤功能
	 */
	private void onCommitReturn() throws Exception {
		// TODO Auto-generated method stub
		ExAggFenpeiHVO billvo = (ExAggFenpeiHVO) getBufferData().getCurrentVO();
		if (billvo == null || billvo.getParentVO() == null)
			return;
		FenpeiItemHVO hvo = (FenpeiItemHVO) billvo.getParentVO();
		PERIODVO pvo = ((ClientUI) getBillUI()).getWaPeriodVO();
		if (hvo.getPk_wa_period() == null || !hvo.getPk_wa_period().equals(pvo.getPrimaryKey())) {
			MessageDialog.showHintDlg(this.getBillUI(), "收回提示", "非当月单据不能操作");
			return;
		}
		AuditBillDlg dlg = new AuditBillDlg(this.getBillUI());
		int x = dlg.showModal();
		if (x != UIDialog.ID_OK) {
			return;
		}
		int ok = MessageDialog.showOkCancelDlg(this.getBillUI(), "驳回", "确认驳回选择的单据?");
		if (ok != UIDialog.ID_OK)
			return;

		int status_audit = WaHrpBillStatus.NOPASS_RETURN;
		hvo.setDapprovedate(null);
		hvo.setVapproveid(null);
		hvo.setVapprovenote(dlg.getField_auditnode().getFieldText());
		hvo.setVbillstatus_audit(status_audit);
		HYPubBO_Client.update(hvo);
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "驳回完成");
		onBoRefresh();
	}

	private boolean checkBeforeSave() {
		IFenpeiItemB fpib = NCLocator.getInstance().lookup(IFenpeiItemB.class);
		try {
			String period = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();
			String year = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();

			// date handle
			/*
			 * Map<String, String> map = handleDate(_getCorp().getPrimaryKey(),
			 * year, period); year = map.get("year"); period = map.get("month");
			 */

			String type = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef18").getValueObject();
			// 科室信息
			PerioddeptVO[] deptvos = (PerioddeptVO[]) HYPubBO_Client.queryByCondition(PerioddeptVO.class, " isnull(dr,0)=0 and pk_corp='"
					+ _getCorp().getPrimaryKey() + "' ");
			HashMap<String, PerioddeptVO> deptMap = new HashMap<String, PerioddeptVO>();
			HashMap<String, PerioddeptVO> deptMapCode = new HashMap<String, PerioddeptVO>();
			for (PerioddeptVO deptvo : deptvos) {
				deptMap.put(deptvo.getPrimaryKey(), deptvo);
				deptMapCode.put(deptvo.getVcode(), deptvo);
			}

			Map<String, DistributionTempVo> localMap = getLocalDistribution(period, year);

			int rowCount = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getRowCount();
			tempDeptList = new ArrayList();
			for (int i = 0; i < rowCount; i++) {
				DistributionTempVo vo = new DistributionTempVo();
				String pkDept = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "pk_wa_dept");
				String deptCode = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "pk_deptdoc");
				String deptName = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "pk_deptdocname");
				UFDouble monthlyAmount = (UFDouble) getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "nmny1");
				UFDouble onceAmount = (UFDouble) getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "nmny2");
				vo.setPkDept(pkDept);
				vo.setDeptCode(deptCode);
				vo.setDeptName(deptName);
				vo.setMonthlyAmount(monthlyAmount);
				vo.setOnceAmount(onceAmount);
				tempDeptList.add(vo);
			}
			// 当前页面上的数据
			List<DistributionTempVo> iterList = tempDeptList;

			// 下发金额
			String whereSql = "vyear='" + year + "' and vperiod='" + period + "' and nvl(dr,0)=0";
			PsnClassItemHVO[] psnvo = (PsnClassItemHVO[]) HYPubBO_Client.queryByCondition(PsnClassItemHVO.class, whereSql);
			HashMap<String, PsnClassItemHVO> psnMap = new HashMap<String, PsnClassItemHVO>();
			for (PsnClassItemHVO psnVO : psnvo) {
				String pk_dept = psnVO.getPk_dept();
				psnMap.put(deptMap.get(pk_dept).getVcode(), psnVO);
			}

			StringBuffer sbf = new StringBuffer();
			String space = "                        \n";
			DecimalFormat smf = new DecimalFormat("#.##");
			for (DistributionTempVo newVO : iterList) {
				String deptCode = newVO.getDeptCode();
				String deptName = newVO.getDeptName();
				if (deptName.equals("心血管内科")) {
					System.out.println();
				}

				if (localMap != null)
					if (localMap.containsKey(deptCode)) {// 当期奖金信息包含该科室
						DistributionTempVo localVO = localMap.get(deptCode);
						Map<String, UFDouble> deptOutMoney = fpib.getDeptOutMoney(deptCode, year, period);
						// 月考核奖校验
						UFDouble localMonthlyAmount = localVO.getMonthlyAmount() == null ? new UFDouble(0) : localVO.getMonthlyAmount();
						UFDouble remoteMonthlyAmount = newVO.getMonthlyAmount() == null ? new UFDouble(0) : newVO.getMonthlyAmount();
						if (!localMonthlyAmount.equals(remoteMonthlyAmount)) {// 金额不相等
							PsnClassItemHVO psnVO = psnMap.get(deptCode);
							if (psnVO != null) {
								Integer pk_item12 = psnVO.getPk_item12() == null ? 0 : psnVO.getPk_item12();
								if (pk_item12 == WaHrpBillStatus.PASS) {// 科主任已经审批
									sbf.append(deptName + "科室奖金科主任已经审批,不能保存" + space);
									continue;
								} else if (pk_item12 == WaHrpBillStatus.NEEDPASS || pk_item12 == WaHrpBillStatus.NOPASS) {// 科主任未审批,但二次分配已提交
									sbf.append(deptName + "科室奖金已提交,不能更新" + space);
									continue;
								} else {
									UFDouble monthOutTotal = deptOutMoney.get("month") == null ? new UFDouble(0) : deptOutMoney.get("month");
									// 新金额减去被分出的金额
									UFDouble result = newVO.getMonthlyAmount().sub(monthOutTotal);
									if (result.doubleValue() < 0) {
										sbf.append(deptName + "科室奖金已被分配" + smf.format(monthOutTotal) + "元,超出新金额,请核对" + space);
										continue;
									}
								}
							}
						}
						// 一次性奖金校验
						UFDouble localOnceAmount = localVO.getOnceAmount() == null ? new UFDouble(0) : localVO.getOnceAmount();
						UFDouble remoteOnceAmount = newVO.getOnceAmount() == null ? new UFDouble(0) : newVO.getOnceAmount();
						if (!localOnceAmount.equals(remoteOnceAmount)) {// 金额不相等
							PsnClassItemHVO psnVO = psnMap.get(deptCode);
							if (psnVO != null) {
								Integer pk_item12 = psnVO.getPk_item12() == null ? 0 : psnVO.getPk_item12();
								if (pk_item12 == WaHrpBillStatus.PASS) {// 科主任已经审批
									sbf.append(deptName + "科室奖金科主任已经审批,不能保存");
									continue;
								} else if (pk_item12 == WaHrpBillStatus.NEEDPASS || pk_item12 == WaHrpBillStatus.NOPASS) {// 科主任未审批,但二次分配已提交
									sbf.append(deptName + "科室奖金已提交,不能更新");
									continue;
								} else {
									UFDouble onceOutTotal = deptOutMoney.get("once") == null ? new UFDouble(0) : deptOutMoney.get("once");
									// 使用新金额减去被分出去的金额
									UFDouble result = newVO.getOnceAmount().sub(onceOutTotal);
									if (result.doubleValue() < 0) {
										sbf.append(deptName + "科室奖金已被分配" + smf.format(onceOutTotal) + "元,超出新金额,请核对" + space);
										continue;
									}
								}
							}
						}
					}
			}
			if (!StringUtils.isEmpty(sbf.toString())) {
				MessageDialog.showHintDlg(this.getBillUI(), "提示", sbf.toString());
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			MessageDialog.showHintDlg(this.getBillUI(), "错误", "奖金校验时出现异常");
			return false;
		}
	}

	// 保存按钮调用时用0，其他时候用1
	private boolean onBoSaveMySelf(int x) throws Exception {

		// TODO 自动生成方法存根
		getBillCardPanelWrapper().getBillCardPanel().dataNotNullValidate();

		Object vbatchcode_dept = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbatchcode").getValueObject();

		String type = getUIController().getBillType();
		if (vbatchcode_dept == null || vbatchcode_dept.toString().trim().length() <= 0) {
			FenpeiItemHVO[] oldvos = (FenpeiItemHVO[]) HYPubBO_Client.queryByCondition(FenpeiItemHVO.class, " pk_billtype='" + type
					+ "' and isnull(dr,0)=0 and pk_corp='" + _getCorp().getPrimaryKey() + "' and vbatchcode is not null order by vbatchcode desc ");
			int batchcode = 1;
			if (oldvos != null && oldvos.length > 0) {
				batchcode = oldvos[0].getVbatchcode() != null ? oldvos[0].getVbatchcode() + 1 : 1;
			}
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbatchcode").setValue(batchcode);
		}

		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_fenpei_b").getRowCount();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nrowcount").setValue(rowcount);
		ArrayList<String> list = new ArrayList<String>();
		// 计算申请金额
		UFDouble totalMoney = new UFDouble(0); // 申请金额
		for (int i = 0; i < rowcount; i++) {
			Object nmny = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "nmny");
			totalMoney = totalMoney.add(nmny != null ? new UFDouble(nmny.toString()) : new UFDouble(0));
			Object deptdoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "pk_wa_dept");
			if (deptdoc != null && deptdoc.toString().trim().length() > 0) {
				if (list.contains(deptdoc.toString().trim())) {
					int xx = MessageDialog.showOkCancelDlg(this.getBillUI(), "保存提示", "表体科室重复，是否通过");
					if (xx != UIDialog.ID_OK) {
						return false;
					}
				} else {
					list.add(deptdoc.toString().trim());
				}
			}
		}

		AggregatedValueObject aggVO = getBillCardPanelWrapper().getBillVOFromUI();

		// 校验表头部门是否包含表体支付部门 qiutian 2012-05-28
		// update by dychf
		// String pk_dept =
		// getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").getValueObject().toString().trim();

		String pk_wa_class = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_wa_class").getValueObject().toString().trim();

		// update by dychf
		// 表头部门以及下级主键
		// ArrayList listDeptPK = getChildByPK(pk_dept, null, DeptdocVO.class);
		// 是否提示表头不包含表体部门
		// boolean isHit = false;

		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("totalmoney").setValue(totalMoney);
		// end qiutian 2012-05-29
		Object period = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();
		Object year = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();

		// date handle
		/*
		 * Map<String, String> map = handleDate(_getCorp().getPrimaryKey(),
		 * year.toString(), period.toString()); year = map.get("year"); period =
		 * map.get("month");
		 * 
		 * // 更新表头的时间
		 * getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear"
		 * ).setValue(year);
		 * getBillCardPanelWrapper().getBillCardPanel().getHeadItem
		 * ("vperiod").setValue(period);
		 */

		String pk = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_fenpei_h").getValueObject();

		if (year != null && year.toString().trim().length() > 0 && period != null && period.toString().trim().length() > 0) {
			String wheresql = " isnull(dr,0)=0 and pk_wa_class='" + pk_wa_class + "' and vyear='" + year.toString().trim() + "' and vperiod='"
					+ period.toString().trim() + "' ";
			if (pk != null && pk.toString().trim().length() > 0) {
				wheresql += " and pk_fenpei_h<>'" + pk.toString().trim() + "' ";
			}
			FenpeiItemHVO[] hvos = (FenpeiItemHVO[]) HYPubBO_Client.queryByCondition(FenpeiItemHVO.class, wheresql);
			if (hvos != null && hvos.length > 0) {
				// MessageDialog.showHintDlg(this.getBillUI(), "提示",
				// "当前薪酬类别期间已存在单据，请检查");
				// return false;
			}
		}

		IFenpeiItemB fpib = NCLocator.getInstance().lookup(IFenpeiItemB.class);
		String fenPeiHPk = fpib.getFenpeiHPK(year.toString(), period.toString());
		FenpeiItemBVO[] bvos = (FenpeiItemBVO[]) getBillCardPanelWrapper().getBillVOFromUI().getChildrenVO();
		if (bvos == null || bvos.length <= 0) {
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "请读取科室信息");
			return false;
		}
		// dychf
		if (StringUtils.isEmpty(fenPeiHPk)) {// 首次添加
			super.onBoSave();
		} else {// 修改时操作表体行
			HYPubBO_Client.deleteByWhereClause(FenpeiItemBVO.class, "pk_fenpei_h='" + fenPeiHPk + "'");
			for (FenpeiItemBVO fenpeiItemBVO : bvos) {
				fenpeiItemBVO.setPk_fenpei_h(fenPeiHPk);
				fenpeiItemBVO.setDr(0);
				fenpeiItemBVO.setPk_fenpei_b(null);
			}
			HYPubBO_Client.insertAry(bvos);
		}
		onBoRefresh();
		return true;
	}

	@Override
	public void onBoExcelOut() {
		// TODO 自动生成方法存根
		BillItem[] items = getBillCardPanelWrapper().getBillCardPanel().getBodyShowItems();
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		Object value[][] = new Object[rowcount + 1][items.length];
		for (int i = 0; i < items.length; i++) {
			value[0][i] = items[i].getName();
			for (int j = 0; j < rowcount; j++) {
				if (items[i].getDataType() == 4) {
					Object tmpvalue = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j, items[i].getKey());
					value[j + 1][i] = tmpvalue != null && new UFBoolean(tmpvalue.toString()) != null
							&& new UFBoolean(tmpvalue.toString()).booleanValue() ? "是" : "否";
				} else {
					value[j + 1][i] = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j, items[i].getKey());
				}
			}
		}
		String billno = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillno").getValueObject();
		String billname = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillname").getValueObject();
		HashMap<String, Object[][]> map = new HashMap<String, Object[][]>();
		String vbillname = billno != null ? billno : "" + billname != null ? billname : "";
		map.put(vbillname, value);
		ExcelOut excel = new ExcelOut(this.getBillUI());
		excel.createExcelFile(map);
	}

	public static DeptBonusSummariesVO[] copyOf(DeptBonusSummariesVO[] original, int newLength) {
		DeptBonusSummariesVO[] copy = new DeptBonusSummariesVO[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

	public List<DistributionTempVo> checkDeptChange(Map<String, DistributionTempVo> localMap, String type) {
		tempDeptList = new ArrayList<DistributionTempVo>();
		StringBuffer sbf = new StringBuffer();
		DecimalFormat df = new DecimalFormat("#.##");
		String space = "                   \n";
		boolean typeFlag = "月考核奖".equals(type);
		if (localMap == null || localMap.size() <= 0)
			return null;

		HashMap<String, DistributionTempVo> existDeptMap = new HashMap<String, DistributionTempVo>();
		for (String deptCode : tempDeptMap.keySet()) {
			DistributionTempVo remote = tempDeptMap.get(deptCode);
			DistributionTempVo local = localMap.get(deptCode);
			String deptName = remote.getDeptName();
			if (local != null) {
				existDeptMap.put(deptCode, local);
				if (typeFlag) {
					if (!local.getMonthlyAmount().equals(remote.getMonthlyAmount())) {// 金额不相等
						sbf.append("科室:" + deptName + "月考核奖由 " + df.format(local.getMonthlyAmount()) + "元, 变为 "
								+ df.format(remote.getMonthlyAmount()) + "元" + space);
						local.setMonthlyAmount(remote.getMonthlyAmount());
					}
				} else {
					if (!local.getOnceAmount().equals(remote.getOnceAmount())) {// 金额不相等
						sbf.append("科室:" + deptName + "一次奖金由 " + df.format(local.getOnceAmount()) + "元, 变为 " + df.format(remote.getOnceAmount())
								+ "元" + space);
						local.setOnceAmount(remote.getOnceAmount());
					}
				}
				tempDeptList.add(local);
			} else {// 添加了新科室
				sbf.append("添加了科室,科室编号:" + remote.getDeptCode() + ",科室编码:" + remote.getDeptName() + space);
				DistributionTempVo dvo = new DistributionTempVo();
				dvo.setDeptCode(remote.getDeptCode());
				dvo.setDeptName(remote.getDeptName());
				if (typeFlag) {// 月考核奖
					dvo.setMonthlyAmount(remote.getMonthlyAmount());
				} else {// 一次性奖金
					dvo.setOnceAmount(remote.getOnceAmount());
				}
				tempDeptList.add(dvo);
			}
		}

		for (String deptCode : localMap.keySet()) {
			if (!existDeptMap.containsKey(deptCode)) {// oa系统减少了科室
				DistributionTempVo dvo = localMap.get(deptCode);
				if (typeFlag) {
					if (!new UFDouble(0).equals(dvo.getMonthlyAmount())) {
						sbf.append("减少了科室,编号:" + deptCode + ",名称:" + dvo.getDeptName() + space);
						dvo.setMonthlyAmount(new UFDouble(0));
					}
				} else {
					if (!new UFDouble(0).equals(dvo.getOnceAmount())) {
						sbf.append("减少了科室,编号:" + deptCode + ",名称:" + dvo.getDeptName() + space);
						dvo.setOnceAmount(new UFDouble(0));
					}
				}
				tempDeptList.add(dvo);
			}
		}

		if (StringUtils.isNotEmpty(sbf.toString())) {
			MessageDialog.showHintDlg(this.getBillUI(), "科室存在以下变动", sbf.toString());
		}
		/*
		 * else { MessageDialog.showHintDlg(this.getBillUI(), "提示",
		 * "科室信息未发生变化"); }
		 */

		return tempDeptList;
	}

	// create by dychf
	// 将从OA读取到的数据进行封装
	private Map<String, DistributionTempVo> getDataFromOA(String period, String year, String type) {
		DeptBonusSummariesVO[] vos = new DeptBonusSummariesVO[] {};
		boolean typeFlag = "月考核奖".equals(type);
		tempDeptMap = new HashMap<String, DistributionTempVo>();
		try {
			IHRwaPub pub = NCLocator.getInstance().lookup(IHRwaPub.class);
			vos = pub.getDeptVos(period, year, type);
			if (vos != null && vos.length > 0)
				for (DeptBonusSummariesVO oaVO : vos) {
					String deptcode = oaVO.getDeptcode();
					String deptname = oaVO.getDeptname();
					UFDouble amount = oaVO.getAmount();
					if (tempDeptMap.containsKey(deptcode)) {// 已存在该科室
						DistributionTempVo vo = tempDeptMap.get(deptcode);
						if (typeFlag)
							vo.setMonthlyAmount(amount);
						else
							vo.setOnceAmount(amount);
					} else {// 不存在该科室
						DistributionTempVo dvo = new DistributionTempVo();
						dvo.setDeptCode(deptcode);
						dvo.setDeptName(deptname);
						if (typeFlag) {
							dvo.setMonthlyAmount(amount);
						} else {
							dvo.setOnceAmount(amount);
						}
						tempDeptMap.put(deptcode, dvo);
					}
				}
			return tempDeptMap;
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return null;
		}
	}

	// create by dychf
	// 将已分配记录进行封装
	private Map<String, DistributionTempVo> getLocalDistribution(String period, String year) {
		Map<String, DistributionTempVo> resultMap = new HashMap<String, DistributionTempVo>();

		// 科室信息
		PerioddeptVO[] deptvos;
		try {
			deptvos = (PerioddeptVO[]) HYPubBO_Client.queryByCondition(PerioddeptVO.class, " isnull(dr,0)=0 and pk_corp='"
					+ _getCorp().getPrimaryKey() + "' ");
			HashMap<String, PerioddeptVO> deptMap = new HashMap<String, PerioddeptVO>();
			for (PerioddeptVO deptvo : deptvos) {
				deptMap.put(deptvo.getPrimaryKey(), deptvo);
			}

			IFenpeiItemB fpib = NCLocator.getInstance().lookup(IFenpeiItemB.class);
			String fenPeiHPk = fpib.getFenpeiHPK(year, period);
			if (StringUtils.isEmpty(fenPeiHPk))// 没有分配记录
				return null;
			List<FenpeiItemBVO> fenpeiItemBVo = fpib.getFenpeiItemBVo(fenPeiHPk);
			for (FenpeiItemBVO vo : fenpeiItemBVo) {
				// 根据主键获取科室信息
				String pk_wa_dept = vo.getPk_wa_dept();
				PerioddeptVO perioddeptVO = deptMap.get(pk_wa_dept);
				// 封装科室信息
				DistributionTempVo dvo = new DistributionTempVo();
				dvo.setPkDept(pk_wa_dept);
				dvo.setDeptName(perioddeptVO.getVname());
				dvo.setDeptCode(perioddeptVO.getVcode());
				dvo.setMonthlyAmount(vo.getNmny1());
				dvo.setOnceAmount(vo.getNmny2());
				resultMap.put(perioddeptVO.getVcode(), dvo);
			}
			return resultMap;
		} catch (UifException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 远程获取分配科室 update by dychf
	 */
	public void onBoReadImport() {
		try {
			String period = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();
			String year = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();

			// date handle
			Map<String, String> map = handleDate(_getCorp().getPrimaryKey(), year.toString(), period.toString());
			String newyear = map.get("year");
			String newperiod = map.get("month");

			String type = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef18").getValueObject();
			if (type == null) {
				MessageDialog.showHintDlg(null, "提示", "请选择奖金类型");
				return;
			}
			// 科室信息
			PerioddeptVO[] deptvos = (PerioddeptVO[]) HYPubBO_Client.queryByCondition(PerioddeptVO.class, " isnull(dr,0)=0 and pk_corp='"
					+ _getCorp().getPrimaryKey() + "' ");
			// 当前系统存在的科室信息
			HashMap<String, PerioddeptVO> deptMap = new HashMap<String, PerioddeptVO>();
			HashMap<String, PerioddeptVO> deptMapCode = new HashMap<String, PerioddeptVO>();
			for (PerioddeptVO deptvo : deptvos) {
				deptMap.put(deptvo.getPrimaryKey(), deptvo);
				deptMapCode.put(deptvo.getVcode(), deptvo);
			}

			List<DistributionTempVo> iterList = new ArrayList<DistributionTempVo>();

			Map<String, DistributionTempVo> dataFromOA = getDataFromOA(newperiod, newyear, type);
			Map<String, DistributionTempVo> localMap = getLocalDistribution(period, year);
			if (localMap != null && localMap.size() > 0 && localMap != null) {
				iterList = checkDeptChange(localMap, type);
			} else if (dataFromOA != null) {
				for (String deptCode : dataFromOA.keySet()) {
					iterList.add(dataFromOA.get(deptCode));
				}
			}

			if (iterList != null) {
				// 清除科室分配记录-dychf
				int rowCount = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getRowCount();
				for (int i = rowCount; i > 0; i--)
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").removeRow(i - 1);

				String key = type.equals("月考核奖") ? "nmny1" : "nmny2";
				for (DistributionTempVo vo : iterList) {
					if (deptMapCode.containsKey(vo.getDeptCode())) {
						onBoLineAdd();
						PerioddeptVO deptvo = deptMapCode.get(vo.getDeptCode());
						int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_fenpei_b").getSelectedRow();
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b")
								.setValueAt(deptvo.getPrimaryKey(), row, "pk_wa_dept");
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(deptvo.getVcode(), row, "pk_deptdoc");
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(deptvo.getVname(), row, "pk_deptdocname");
						// 月考核奖判空
						UFDouble monthlyAmount = vo.getMonthlyAmount() == null ? new UFDouble(0) : vo.getMonthlyAmount();
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(monthlyAmount, row, "nmny1");
						// 一次性奖金判空
						UFDouble onceAmount = vo.getOnceAmount() == null ? new UFDouble(0) : vo.getOnceAmount();
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(onceAmount, row, "nmny2");
						UFDouble total = vo.getMonthlyAmount().add(vo.getOnceAmount() == null ? new UFDouble(0) : vo.getOnceAmount());
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(total, row, "nmny");
						getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").execLoadFormula();
					} else {
						// 科室在本系统中不存在
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBoExcelImport() {
		// TODO 自动生成方法存根
		// 弹出数据等待框

		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if (!code.equals("wa_fenpei_b")) {
			return;
		}
		ImportExcelData excel = new ImportExcelData();
		try {
			Object[][] values = excel.executeImport();

			if (values == null || values.length <= 1)
				return;
			try {
				// String pk_corp, String waYear, String waPeriod, String
				// stWhere, String tableName,Boolean bflag
				HashMap<String, PerioddeptVO> map_dept = new HashMap<String, PerioddeptVO>();
				PerioddeptVO[] deptvos = (PerioddeptVO[]) HYPubBO_Client.queryByCondition(PerioddeptVO.class,
						" isnull(vdef5,'N')='Y' and isnull(dr,0)=0 and pk_corp='" + _getCorp().getPrimaryKey() + "'");
				if (deptvos != null && deptvos.length > 0) {
					for (int uu = 0; uu < deptvos.length; uu++) {// 编码名称都存
						map_dept.put(deptvos[uu].getVcode().trim().toLowerCase(), deptvos[uu]);
						// map_dept.put(deptvos[uu].getVname().trim().toLowerCase(),
						// deptvos[uu]);
					}
				}

				Object tmpindex = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject();
				int nindex = tmpindex != null && tmpindex.toString().trim().length() > 0 ? Integer.parseInt(tmpindex.toString()) : 0;
				HashMap<String, BillItem> map = new HashMap<String, BillItem>();
				for (int i = 0; i < nindex; i++) {
					BillItem item = getBillCardPanelWrapper().getBillCardPanel().getBodyItem("nmny" + "" + (i + 1) + "");
					if (item != null && item.getKey() != null && item.getName() != null) {
						map.put(item.getName().trim(), item);
					}
				}
				int indexpsncode = -1;
				int indexpsnname = -1;
				int indexitemname = -1;
				int indexerrmess = -1;
				int indexvmemo = -1;
				boolean iserrorlie = false;
				HashMap<String, Integer> mapindex = new HashMap<String, Integer>();
				for (int i = 0; i < values[0].length; i++) {
					String value = values[0][i] != null && values[0][i].toString().trim().length() > 0 ? values[0][i].toString().trim() : "";
					if (value.equals("奖金科室编码")) {
						indexpsncode = i;
					}
					if (value.equals("奖金科室名称")) {
						indexpsnname = i;
					} else if (value.equals("备注说明")) {
						indexvmemo = i;
					} else if (value.equals("错误信息列")) {
						indexerrmess = i;
						iserrorlie = true;
					}
					if (map.containsKey(value)) {
						mapindex.put(value, i);
					}
				}
				if (indexpsncode == -1) {
					MessageDialog.showHintDlg(getBillUI(), "提示", "请检查选择文件是否正确，缺少必需的奖金科室列");
					return;
				}
				int x = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount() - 1;
				// 记录每行的标志 是否为 不符合规则的数据 1、编码错误2、编码跟人员姓名对不上
				ArrayList<String> errorlist = new ArrayList<String>();
				// values[0][values[0].length+1]="错误信息列";
				Object[][] valueexp;
				if (iserrorlie) {
					valueexp = values;
				} else {
					valueexp = new Object[values.length][values[0].length + 1];
					valueexp[0][values[0].length] = "错误信息列";
				}
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < values[i].length; j++) {
						valueexp[i][j] = values[i][j];
					}
				}
				if (indexerrmess == -1) {
					indexerrmess = values[0].length;
				}
				for (int i = 1; i < values.length; i++) {
					Object tmppsn = values[i][indexpsncode];
					Object tmppsnname = values[i][indexpsnname];
					if (tmppsn == null || tmppsn.toString().trim().equals("")) {
						errorlist.add("奖金科室编码为空");
						valueexp[i][indexerrmess] = "奖金科室编码为空";
						continue;
					}
					if (map_dept.get(tmppsn.toString().toLowerCase()) == null) {
						errorlist.add("科室在系统中找不到对应的档案");
						valueexp[i][indexerrmess] = "科室在系统中找不到对应的档案";
						continue;
					}
					PerioddeptVO tempdepvo = map_dept.get(tmppsn.toString().toLowerCase());
					if (tempdepvo.getVname().equals(tmppsnname)) {
						continue;
					} else {
						errorlist.add("科室编码和科室名称对不上，请核查当前科室是否正确");
						valueexp[i][indexerrmess] = "科室编码和科室名称对不上，请核查当前科室是否正确";
						continue;
					}

				}

				if (errorlist.size() > 0) {// 如果有错误 回写excel 让用户去更改。
					File file = new File(excel.getPath());
					if (file.exists()) {
						file.delete();
					}
					WritableWorkbook wwb = Workbook.createWorkbook(new File(excel.getPath()));
					WritableSheet wsa = wwb.createSheet("薪酬项目信息", 0);
					for (int j = 0; j < valueexp.length; j++) {
						for (int k = 0; k < valueexp[j].length; k++) {
							String value = valueexp[j][k] != null ? valueexp[j][k].toString() : "";
							Label label00 = new Label(k, j, value);
							wsa.addCell(label00);
						}
					}
					wwb.write();
					wwb.close();
					MessageDialog.showHintDlg(this.getBillUI(), "提示", "重新打开excel核查数据，错误提示在最后一列");
					return;
				}

				for (int i = 1; i < values.length; i++) {
					Object tmppsn = values[i][indexpsncode];// 人员编码
					Object tmpmemo = indexvmemo == -1 ? "" : values[i][indexvmemo];
					if (tmppsn == null || tmppsn.toString().trim().equals("")) {
						break;
					}
					String pk_wa_dept = map_dept.get(tmppsn.toString().trim().toLowerCase()) != null ? map_dept.get(
							tmppsn.toString().trim().toLowerCase()).getPk_perioddept() : null;
					x++;
					onBoLineAdd();
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(tmpmemo + "", x, "vmemo");
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(pk_wa_dept, x, "pk_wa_dept");
					if (mapindex != null && mapindex.size() > 0) {
						String[] keys = mapindex.keySet().toArray(new String[0]);
						for (int j = 0; j < keys.length; j++) {
							BillItem item = map.get(keys[j]);
							int index = mapindex.get(keys[j]);
							Object tmpnmny = values[i][index];
							try {
								UFDouble nmny = tmpnmny != null ? new UFDouble(tmpnmny.toString()) : null;
								getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(nmny, x, item.getKey());
							} catch (Exception e) {
								//
							}
						}
					}
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").execEditFormulas(x);
				}

				int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_fenpei_b").getRowCount();
				int index = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject() != null ? Integer
						.parseInt(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject().toString()) : 0;
				UFDouble totalMoney = new UFDouble(0); // 申请金额
				for (int i = 0; i < rowcount; i++) {
					UFDouble nmny = new UFDouble(0);
					for (int j = 0; j < index; j++) {
						Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "nmny" + (j + 1) + "");
						nmny = nmny.add(value != null ? new UFDouble(value.toString()) : new UFDouble(0));
					}
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").setValueAt(nmny, i, "nmny");
					totalMoney = totalMoney.add(nmny);
				}
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("totalmoney").setValue(totalMoney);
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MessageDialog.showHintDlg(getBillUI(), "提示", e.getMessage());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MessageDialog.showHintDlg(getBillUI(), "提示", e.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MessageDialog.showHintDlg(getBillUI(), "提示", e.getMessage());
			}

			// } catch(Exception e) {
			// e.printStackTrace();
			// getBillUI().showErrorMessage(e.getMessage());
			// } finally {
			// //销毁系统运行提示框
			// dialog.end();
			// }
			// }
			// };
			// // 启用线程
			// new Thread(checkRun).start();
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			MessageDialog.showHintDlg(getBillUI(), "提示", e1.getMessage());
			return;
		}

	}

	@Override
	protected void onBoCancel() throws Exception {
		// TODO 自动生成的方法存根
		super.onBoCancel();
		// System.out.println();
	}

	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {

		super.onBoAdd(bo);
		plottable();
		// 设置薪酬项目
		HashMap<String, String> mapkeytoitem = ((ClientUI) getBillUI()).getMapkeytoitem();
		HashMap<String, String> mapkeytofundsource = ((ClientUI) getBillUI()).getMapkeytofundsource();
		if (mapkeytoitem != null && mapkeytoitem.size() > 0) {
			String[] keys = mapkeytoitem.keySet().toArray(new String[0]);
			int index = 0;
			for (String key : keys) {
				getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fundfenpei").addLine();
				getBillCardPanelWrapper().getBillCardPanel().setHeadItem(key, mapkeytoitem.get(key));
				getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fundfenpei").setValueAt(mapkeytoitem.get(key), index, "pk_item");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fundfenpei")
						.setValueAt(mapkeytofundsource.get(key), index, "pk_fundsource");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fundfenpei").execEditFormulas(index);
				index++;
			}
			getBillCardPanelWrapper().getBillCardPanel().setHeadItem("nindex", mapkeytoitem.size());
		} else {
			getBillCardPanelWrapper().getBillCardPanel().setHeadItem("nindex", 0);
		}
		getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fundfenpei").execLoadFormula();

		// update by dychf
		IFenpeiItemB fpib = NCLocator.getInstance().lookup(IFenpeiItemB.class);
		String period = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();
		String year = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();
		String fenPeiHPk = fpib.getFenpeiHPK(year, period);
		// date handle
		/*
		 * Map<String, String> map = handleDate(_getCorp().getPrimaryKey(),
		 * year.toString(), period.toString()); year = map.get("year"); period =
		 * map.get("month");
		 * 
		 * Integer intPeriod = new Integer(period); String fenPeiHPk1 =
		 * fpib.getFenpeiHPK(year, intPeriod.toString());
		 */

		if (StringUtils.isNotEmpty(fenPeiHPk)) {
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "该发放期间已存在分配记录,请查询后修改");
			this.onBoCancel();
		}
	}

	public void plottable() {
		// 无薪酬项目及部门信息的不能增加
		ItemSetBVO[] itemvos = ((ClientUI) getBillUI()).getItemvos();
		HashMap<String, ItemVO> mapitem = ((ClientUI) getBillUI()).getMapitem();
		HashMap<String, String> mapkeytoitem = ((ClientUI) getBillUI()).getMapkeytoitem();
		HashMap<String, String> mapkeytofundsource = ((ClientUI) getBillUI()).getMapkeytofundsource();
		mapkeytoitem.clear();
		mapkeytofundsource.clear();
		// 先隐藏 再根据条件显示
		BillItem[] showItems = getBillCardPanelWrapper().getBillCardPanel().getBodyShowItems();
		for (int i = 1; i < showItems.length - 3; i++) {
			BillItem item = showItems[i];
			if (item != null && item.getKey() != null) {
				// dychf
				item.setShow(true);
				// item.setShow(false);
			}
		}

		for (int i = 0; i < itemvos.length; i++) {
			BillItem item = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nmny" + "" + (i + 1) + "");
			if (item != null && item.getKey() != null) {
				mapkeytoitem.put("pk_item" + "" + (i + 1) + "", itemvos[i].getPk_item());
				mapkeytofundsource.put("pk_item" + "" + (i + 1) + "", itemvos[i].getPk_fundsource());
				item.setShow(true);
				item.setName(mapitem.get(itemvos[i].getPk_item()) != null ? mapitem.get(itemvos[i].getPk_item()).getVname() : item.getName());
			}
		}
		((ClientUI) getBillUI()).setMapkeytoitem(mapkeytoitem);
		((ClientUI) getBillUI()).setMapkeytofundsource(mapkeytofundsource);
		if (((ClientUI) getBillUI()).lastbillstatus == IBillOperate.OP_INIT) {
			getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
			((ClientUI) getBillUI()).updateUI();
		}

		getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());

	}

	@Override
	public void onButton(ButtonObject bo) {
		// TODO Auto-generated method stub
		((ClientUI) getBillUI()).lastbillstatus = ((ClientUI) getBillUI()).getBillOperate();
		super.onButton(bo);
	}

	@Override
	protected void onBoDelete() throws Exception {
		// TODO 自动生成方法存根
		super.onBoDelete();
	}

	@Override
	protected void onBoPrint() throws Exception {
		// TODO 自动生成方法存根
		nc.ui.pub.print.IDataSource dataSource = new MyCardPanelPRTS(getBillUI()._getModuleCode(), getBillCardPanelWrapper().getBillCardPanel());
		nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(null, dataSource);
		print.setTemplateID(getBillUI()._getCorp().getPrimaryKey(), getBillUI()._getModuleCode(), getBillUI()._getOperator(), getBillUI()
				.getBusinessType(), getBillUI().getNodeKey());
		if (print.selectTemplate() == 1)
			print.preview();
	}

	@Override
	protected void onBoSave() throws Exception {
		if (checkBeforeSave()) {

			if (!onBoSaveMySelf(0))
				return;

			// update by dychf
			// 保存后修改单据状态
			cancleAuditBeforeEdit();
			onBoRefresh();
		}
	}

	@Override
	protected void onBoLineDel() throws Exception {
		// TODO Auto-generated method stub
		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if (code.equals("wa_fundfenpei")) {
			return;
		}
		super.onBoLineDel();
	}

	@Override
	protected void onBoLineAdd() throws Exception {
		// TODO 自动生成方法存根
		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if (code.equals("wa_fundfenpei")) {
			return;
		}

		super.onBoLineAdd();
		// 设置能否编辑
		getBillCardPanelWrapper().getBillCardPanel().getBillTable().setEnabled(false);
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(_getCorp().getPrimaryKey(), row, "pk_corp");
	}

	@Override
	protected void onBoQuery() throws Exception {
		// TODO 自动生成方法存根
		StringBuffer strWhere = new StringBuffer();

		if (askForQueryCondition(strWhere) == false)
			return;// 用户放弃了查询
		String wheresql = strWhere.toString();

		// update by dychf
		/*
		 * if (wheresql != null && wheresql.trim().length() > 0) { wheresql +=
		 * " and pk_dept in (select pk_deptdoc from bd_wa_userdept where isnull(dr,0)=0 and pk_user='"
		 * + _getOperator() + "') "; } else { wheresql =
		 * "  pk_dept in (select pk_deptdoc from bd_wa_userdept where isnull(dr,0)=0 and pk_user='"
		 * + _getOperator() + "') "; }
		 */
		UserAndClerkVO[] uservo = (UserAndClerkVO[]) HYPubBO_Client.queryByCondition(UserAndClerkVO.class, " isnull(dr,0)=0 and userid='"
				+ _getOperator() + "' ");
		if (uservo != null && uservo.length > 0) {
			String pk_psn = uservo[0].getPk_psndoc();
			PsndocVO[] docvos = (PsndocVO[]) HYPubBO_Client.queryByCondition(PsndocVO.class, " pk_corp='" + _getCorp().getPrimaryKey()
					+ "' and isnull(dr,0)=0 and pk_psnbasdoc='" + pk_psn + "' and groupdef51='000169100000001051BE' ");
			if (docvos != null && docvos.length > 0) {// 主任

			} else {
				wheresql += " and voperatorid='" + _getOperator() + "' ";
			}
		} else {
			wheresql += " and voperatorid='" + _getOperator() + "' ";
		}

		SuperVO[] queryVos = queryHeadVOs(wheresql);

		getBufferData().clear();
		// 增加数据到Buffer
		addDataToBuffer(queryVos);

		updateBuffer();
	}

	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub
		super.onBoRefresh();
	}

	@Override
	protected void onBoCard() throws Exception {
		// TODO Auto-generated method stub
		super.onBoCard();
		int currRow = getBufferData().getCurrentRow();
		getBufferData().setCurrentRow(currRow);
	}

	@Override
	protected void onBoCommit() throws Exception {

		// TODO Auto-generated method stub
		ExAggFenpeiHVO billvo = (ExAggFenpeiHVO) getBufferData().getCurrentVO();
		if (billvo == null || billvo.getParentVO() == null)
			return;
		FenpeiItemHVO hvo = (FenpeiItemHVO) billvo.getParentVO();// ---
		// System.out.println("old vo ts-->" + hvo.getTs());
		PERIODVO pvo = ((ClientUI) getBillUI()).getWaPeriodVO();

		// update by dychf

		if (hvo.getPk_wa_period() == null || !hvo.getPk_wa_period().equals(pvo.getPrimaryKey())) {
			MessageDialog.showHintDlg(this.getBillUI(), "提交提示", "非当月单据不能操作");
			return;
		}

		UFDate dmakedate = hvo.getDmakedate();
		if (dmakedate.after(_getDate())) {
			MessageDialog.showHintDlg(this.getBillUI(), "提交提示", "提交日期不能早于制单日期");
			return;
		}
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提交提示", "确认要提交当前单据？");
		if (x != UIDialog.ID_OK)
			return;
		// super.onBoEdit();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vcommitid").setValue(_getOperator());
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("dcommitdate").setValue(_getDate());
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus").setValue(IBillStatus.COMMIT);

		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus_audit").setValue(WaHrpBillStatus.COMMIT);
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getRowCount();
		ArrayList<Integer> list_del = new ArrayList<Integer>();
		for (int i = 0; i < rowcount; i++) {
			// ---------
			int index = 2;
			boolean flag = true;
			for (int j = 0; j < index; j++) {
				Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_fenpei_b").getValueAt(i, "nmny" + (j + 1) + "");
				if (value != null && new UFDouble(value.toString()).doubleValue() != 0.0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				list_del.add(i);
			}
		}
		if (list_del.size() > 0) {
			int[] delrows = new int[list_del.size()];
			for (int i = 0; i < delrows.length; i++) {
				delrows[i] = list_del.get(i);
			}
			getBillCardPanelWrapper().getBillCardPanel().getBodyPanel("wa_fenpei_b").delLine(delrows);
		}

		// update by dychf
		// 提交后审核
		auditAfterCommit();

	}

	/** 删除选择行(多行操作) */
	protected void onboLineDelAry() throws Exception {
		// TODO Auto-generated method stub
		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if (code.equals("wa_fundfenpei")) {
			return;
		}
		int rowCount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		if (rowCount > 0) {
			getBillCardPanelWrapper().getBillCardPanel().getBillTable().selectAll();
			int[] selectRows = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRows();
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().delLine(selectRows);
		}
	}

	/**
	 * 查询当前主键的全部下级PK(包括当前pk)
	 * 
	 * @author qiutian
	 * @date 2012-02-28
	 * @param pk
	 *            要查询的主键
	 * @param voClass
	 *            要查询的VO
	 */
	public ArrayList<String> getChildByPK(String pk, ArrayList<String> listPK, Class voClass) {

		if (listPK == null) {
			listPK = new ArrayList<String>();
		}
		try {

			String strWhere = " isnull(dr,0)=0 and (pk_corp='" + IHRPSysParam.corp + "' or " + " pk_corp= '"
					+ ClientEnvironment.getInstance().getCorporation().getPrimaryKey() + "')" + " " + " and pk_fathedept ='" + pk + "'";

			SuperVO[] vos = HYPubBO_Client.queryByCondition(voClass, strWhere);

			// 只加不存在的
			if (!listPK.contains(pk)) {
				listPK.add(pk);
			}
			// 递归
			for (SuperVO vo : vos) {
				getChildByPK(vo.getPrimaryKey(), listPK, voClass);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println("查询下级主键错误");
		}
		return listPK;
	}

	/**
	 * 将VO数组组装成 <传入key + VO >的形式
	 * 
	 * @param key
	 *            如果为空则默认主键为key
	 * @author qiutian
	 * @date 2012-3-12
	 */
	public static HashMap<String, WaDataVO> getKeyToVO(WaDataVO[] vos) {

		HashMap<String, WaDataVO> map = new HashMap<String, WaDataVO>();
		if (null != vos) {

			for (WaDataVO superVO : vos) {
				map.put(superVO.getPsnid(), superVO);
			}
		}
		return map;
	}

	// create by dychf
	@SuppressWarnings("restriction")
	public void auditAfterCommit() {
		FenpeiItemHVO newhvo;
		try {
			FenpeiItemHVO hvo = (FenpeiItemHVO) getBillCardPanelWrapper().getBillVOFromUI().getParentVO();
			newhvo = (FenpeiItemHVO) HYPubBO_Client.queryByPrimaryKey(FenpeiItemHVO.class, hvo.getPk_fenpei_h());
			int status_audit = WaHrpBillStatus.PASS;

			String rmsg = WaPubCount.onAudit(newhvo, status_audit, "通过", ((ClientUI) getBillUI()).getWaPeriodVO(), true);
			getBufferData().getCurrentVO().setParentVO(newhvo);
			updateBuffer();
			MessageDialog.showHintDlg(getBillUI(), "提示", "提交并审批通过");
			onBoRefresh();
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(getBillUI(), "提示", "提交并审批时出现异常");
		}
	}

	@Override
	public void onBoAudit() throws Exception {
		// TODO Auto-generated method stub
		int ok = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认审核通过选择的单据?");
		if (ok != UIDialog.ID_OK)
			return;
		int status_audit = WaHrpBillStatus.PASS;
		boolean islist = ((ClientUI) getBillUI()).isListPanelSelected();
		String msg = "";
		if (islist) {
			int rowcount = ((ClientUI) getBillUI()).getBillListPanel().getHeadTable().getRowCount();
			for (int i = 0; i < rowcount; i++) {
				ExAggFenpeiHVO value = (ExAggFenpeiHVO) getBufferData().getVOByRowNo(i);
				FenpeiItemHVO hvo = (FenpeiItemHVO) value.getParentVO();
				boolean select = ((ClientUI) getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "isselect") != null ? new UFBoolean(
						((ClientUI) getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "isselect").toString()).booleanValue() : false;
				if (select) {
					String rmsg = WaPubCount.onAudit(hvo, status_audit, "通过", ((ClientUI) getBillUI()).getWaPeriodVO(), true);
					msg += rmsg;
					FenpeiItemHVO newhvo = (FenpeiItemHVO) HYPubBO_Client.queryByPrimaryKey(FenpeiItemHVO.class, hvo.getPrimaryKey());
					value.setParentVO(newhvo);
					getBufferData().setVOAt(i, value);
				}

			}
			updateBuffer();
			if (msg.trim().length() <= 0) {
				MessageDialog.showHintDlg(this.getBillUI(), "提示", "没有需要审批的单据");
			} else {
				MessageDialog.showHintDlg(this.getBillUI(), "提示", msg);
			}
		} else {
			if (getBufferData() == null || getBufferData().getCurrentVO() == null)
				return;
			FenpeiItemHVO hvo = (FenpeiItemHVO) getBufferData().getCurrentVO().getParentVO();
			String rmsg = WaPubCount.onAudit(hvo, status_audit, "通过", ((ClientUI) getBillUI()).getWaPeriodVO(), true);
			MessageDialog.showHintDlg(this.getBillUI(), "提示", rmsg);
			onBoRefresh();
		}
	}

	@Override
	protected void onBoDel() throws Exception {
		// TODO Auto-generated method stub
		/*
		 * if (getBufferData().getCurrentVO() != null &&
		 * getBufferData().getCurrentVO().getParentVO() != null) { FenpeiItemHVO
		 * hvo = (FenpeiItemHVO) getBufferData().getCurrentVO().getParentVO();
		 * FenpeiItemHVO oldvo = (FenpeiItemHVO)
		 * HYPubBO_Client.queryByPrimaryKey(FenpeiItemHVO.class,
		 * hvo.getPrimaryKey()); if (oldvo == null ||
		 * !oldvo.getTs().equals(hvo.getTs())) {
		 * MessageDialog.showHintDlg(this.getBillUI(), "提示", "数据已被修改，请刷新后重试!");
		 * return; } int x = MessageDialog.showOkCancelDlg(this.getBillUI(),
		 * "提示", "确认作废当前单据，作废后不能恢复"); if (x != UIDialog.ID_OK) { return; }
		 * hvo.setVbillstatus_audit(WaHrpBillStatus.DEL);
		 * hvo.setDapprovedate(_getDate()); hvo.setVapproveid(_getOperator());
		 * HYPubBO_Client.update(hvo); onBoRefresh(); }
		 */
		FenpeiItemHVO newhvo;
		String period = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();
		String year = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();

		/*
		 * // date handle Map<String, String> map =
		 * handleDate(_getCorp().getPrimaryKey(), year.toString(),
		 * period.toString()); year = map.get("year"); period =
		 * map.get("month");
		 */

		IFenpeiItemB fpib = NCLocator.getInstance().lookup(IFenpeiItemB.class);
		String fenPeiHPk = fpib.getFenpeiHPK(year.toString(), period.toString());
		HYPubBO_Client.deleteByWhereClause(FenpeiItemHVO.class, "pk_fenpei_h ='" + fenPeiHPk + "'");
		HYPubBO_Client.deleteByWhereClause(FenpeiItemBVO.class, "pk_fenpei_h ='" + fenPeiHPk + "'");
		onBoRefresh();
	}

	// update by dychf
	@Override
	protected void onBoEdit() throws Exception {
		onBoRefresh();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_fenpei_h").setValue(null);
		// TODO 自动生成的方法存根
		super.onBoEdit();
	}

	protected void onBoCancelAudit() throws Exception {
		// TODO Auto-generated method stub
		int ok = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认弃审选择的单据?");
		if (ok != UIDialog.ID_OK)
			return;
		int status_audit = WaHrpBillStatus.NOPASS;
		boolean islist = ((ClientUI) getBillUI()).isListPanelSelected();
		String msg = "";
		if (islist) {
			int rowcount = ((ClientUI) getBillUI()).getBillListPanel().getHeadTable().getRowCount();
			for (int i = 0; i < rowcount; i++) {
				ExAggFenpeiHVO value = (ExAggFenpeiHVO) getBufferData().getVOByRowNo(i);
				FenpeiItemHVO hvo = (FenpeiItemHVO) value.getParentVO();
				boolean select = ((ClientUI) getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "isselect") != null ? new UFBoolean(
						((ClientUI) getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "isselect").toString()).booleanValue() : false;
				if (select) {
					String rmsg = WaPubCount.onAudit(hvo, status_audit, "不通过", ((ClientUI) getBillUI()).getWaPeriodVO(), false);
					msg += rmsg;
					FenpeiItemHVO newhvo = (FenpeiItemHVO) HYPubBO_Client.queryByPrimaryKey(FenpeiItemHVO.class, hvo.getPrimaryKey());
					value.setParentVO(newhvo);
					getBufferData().setVOAt(i, value);
				}

			}
			updateBuffer();
			if ((msg).trim().length() <= 0) {
				MessageDialog.showHintDlg(this.getBillUI(), "提示", "没有需要弃审的单据");
			} else {
				MessageDialog.showHintDlg(this.getBillUI(), "提示", msg);
			}
		} else {
			if (getBufferData() == null || getBufferData().getCurrentVO() == null)
				return;
			FenpeiItemHVO hvo = (FenpeiItemHVO) getBufferData().getCurrentVO().getParentVO();
			String rmsg = WaPubCount.onAudit(hvo, status_audit, "不通过", ((ClientUI) getBillUI()).getWaPeriodVO(), false);
			MessageDialog.showHintDlg(this.getBillUI(), "提示", rmsg);
			onBoRefresh();
		}
	}

	// create by dychf
	public void cancleAuditBeforeEdit() {
		int status_audit = WaHrpBillStatus.SAVE_UNCOMMIT;
		FenpeiItemHVO newhvo;
		try {
			String pk = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_fenpei_h").getValueObject();

			// dychf
			if (!StringUtils.isEmpty(pk)) {
				newhvo = (FenpeiItemHVO) HYPubBO_Client.queryByPrimaryKey(FenpeiItemHVO.class, pk);
				String rmsg = WaPubCount.onAudit(newhvo, status_audit, "保存未复核", ((ClientUI) getBillUI()).getWaPeriodVO(), false);
			} else {
				String period = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();
				String year = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();

				/*
				 * // date handle Map<String, String> map =
				 * handleDate(_getCorp().getPrimaryKey(), year.toString(),
				 * period.toString()); year = map.get("year"); period =
				 * map.get("month");
				 */

				IFenpeiItemB fpib = NCLocator.getInstance().lookup(IFenpeiItemB.class);
				String fenPeiHPk = fpib.getFenpeiHPK(year.toString(), period.toString());
				newhvo = (FenpeiItemHVO) HYPubBO_Client.queryByPrimaryKey(FenpeiItemHVO.class, fenPeiHPk);
				String rmsg = WaPubCount.onAudit(newhvo, status_audit, "保存未复核", ((ClientUI) getBillUI()).getWaPeriodVO(), false);
			}
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
