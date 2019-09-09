package nc.ui.wa.wa_020;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.FrameworkEJBException;
import nc.bs.logging.Logger;
import nc.itf.hr.wa.IPayTotalInfo;
import nc.itf.wa.wa_020.IWaServ;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.HrQueryDialog;
import nc.ui.hr.frame.IQueryFieldValueEditor;
import nc.ui.hr.global.Global;
import nc.ui.hr.global.HiInfoForQuery;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIList;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISpinBox;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.report.ReportBaseClass;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wabm.category.WaClassRefUIModel;
import nc.ui.wabm.global.ItemFilterEditorFactory;
import nc.ui.wabm.global.QueryConditionDlgBuilder;
import nc.ui.wabm.global.QueryConditionDlgUtils;
import nc.ui.wabm.global.WAGlobalData;
import nc.ui.wabm.global.WaGlobal;
import nc.vo.bd.b00.IDataType;
import nc.vo.bd.b20.CurrtypeVO;
import nc.vo.comw.validator.Validator;
import nc.vo.hash.hashVO.CircHashVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CommonConstant;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.voutils.SafeCompute;
import nc.vo.trade.voutils.VOUtil;
import nc.vo.wa.wa_001.WaGlobalVO;
import nc.vo.wa.wa_008.WaclassVO;
import nc.vo.wa.wa_020.WaCurrPeriodVO;
import nc.vo.wa.wa_020.WaMultiReportVO01;
import nc.vo.wa.wa_020.WaMultiReportVO02;
import nc.vo.wa.wa_024.ItemVO;

/**
 * 多类别薪资报表主界面
 * 
 * 创建日期：(2001-9-5 15:38:19)
 * 
 * @author：Administrator
 */
public class WaMultiReportUI extends ToftPanel implements IDataSource,
IQueryFieldValueEditor {

	private static final long serialVersionUID = 7297486171329558592L;
	private UILabel ivjUILabelPeriod = null;
	private UIPanel ivjUIPanel = null;
	private BillCardPanel ivjBillCardPanel = null;
	// 当前参数 strCurYear is the account year ,and strCurPeriod is the account
	// period
	private String strCurYear = "2012";
	private String strCurPeriod = "10";
	// xlt start strWaperiods 代表相应的薪资期间
	// For exmaple ,user type{'2007' --'6' }and the coresponding wage periods
	// are {'2007'-'5','2007'-'6'}
	private ArrayList<String[]> strWaperiods = new ArrayList<String[]>();
	private UIList listForSelItems = null;
	private UIScrollPane sclPane = null;
	private UILabel lableForSel = null;
	// xlt end
	private String strPk_corp = Global.getWaCorpPk();
	private Integer iZBdigit = null;
	// 按钮设置
	private final ButtonObject bnOk = new ButtonObject(NCLangRes.getInstance()
			.getStrByID("common", "UC001-0000044")/* @res "确定" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
			"UPP60131604-000038")/* @res "查看所选报表" */, 2, "确定"); /*-=notranslate=-*/
	private final ButtonObject bnCancel = new ButtonObject(NCLangRes
			.getInstance().getStrByID("common", "UC001-0000008")/* @res "取消" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
			"UPP60131604-000039")/* @res "返回到主界面" */, 2, "取消"); /*-=notranslate=-*/
	private final ButtonObject bnPrint = new ButtonObject(NCLangRes
			.getInstance().getStrByID("common", "UC001-0000007")/* @res "打印" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
			"UPP60131604-000040")/* @res "打印所选报表" */, 2, "打印"); /*-=notranslate=-*/
	private final ButtonObject bnExport = new ButtonObject("导出",
			"导出", 2, "导出"); /*-=notranslate=-*/
	private final ButtonObject[] ButtonGroupReport = { bnOk, bnCancel, bnPrint,bnExport };
	// 表格栏
	private String[] aryFixnames = null;
	private String[] aryKeys = null;

	// 表格
	private BillData bill = null;
	// 查询条件
	private String strWhereSql = "";// sql

	private final String[] MULREPORTKIND = new String[] {
			NCLangRes.getInstance()
			.getStrByID("60131604", "UPP60131604-000026")/*
			 * @res
			 * "多类别薪资发放明细表"
			 */,
			 NCLangRes.getInstance()
			 .getStrByID("60131604", "UPP60131604-000027")/*
			  * @res
			  * "多类别薪资发放汇总表"
			  */,
			  // NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000028")/*@res
			  // "单位工资总额台账"*/+"-"+NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000029")/*@res
			  // "实发模式"*/,
			  // NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000028")/*@res
			  // "单位工资总额台账"*/+"-"+NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000030")/*@res
			  // "对照模式"*/,
			  // NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000031")/*@res
			  // "部门工资总额台账"*/+"-"+NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000029")/*@res
			  // "实发模式"*/,
			  // NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000031")/*@res
			  // "部门工资总额台账"*/+"-"+NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000030")/*@res
			  // "对照模式"*/,
			  NCLangRes.getInstance()
			  .getStrByID("60131604", "UPP60131604-000141") /*
			   * @res
			   * "员工工资台账"
			   */
	};
	// 报表名称
	private String title = "";// 报表名称
	// 币种主键名称哈希表
	private Hashtable<String, String> hCurrtype = null;
	// 币种精度
	private Vector<Integer> vCurrDigit = null;
	// VO
	private ItemVO[] itemSelectedVOs = null;
	private ItemVO[] itemSelectedExpVOs = null;// 需要导出的字段 zhanghua
	private WaclassVO[] waclassSelectedVOs = null;
	private WaMultiReportVO01[] vo001s = null;// 明细表
	private WaMultiReportVO02[] vo002s = null;// 汇总表
	// yang add billCardPanel 表尾中 制表名称，审核名称
	private final String m_sTailItemLister = "lister";
	private final String m_sTailItemassessor = "assessor";
	// yxl add: 增加人员部门权限控制
	private String m_userId = null;
	private UILabel ivjUILblPeriod = null;
	private UILabel ivjUILblYear = null;
	private UITextField ivjUITfWaPeriod = null;
	private UITextField ivjUITfWaYear = null;
	// add by lq ------------
	private UILabel startL = null;
	private UILabel endL = null;
	private UIRefPane startPane = null;
	private UIRefPane endPane = null;
	private String vstartPeriod = null;
	private String vendPeriod = null;
	private List<String> startPeriod = null;
	private List<String> endPeriod = null;
	// end
	private String deptPower = null;
	private String[] l_straryitemsColNames = null;
	private String[] l_strarywaclassColNames = null;
	private String[] l_printitemvalues = null;
	private String[] l_psnnames = null;
	private String[] l_psncodes = null;
	private String[] l_deptnames = null;
	private String[] l_deptcodes = null;
	private String[] l_jobnames = null;
	private String[] l_IDs = null;
	private String[] l_bankname = null;
	private String[] l_vacccode = null;
	private String[] l_vperiods = null;

	private UILabel ivjUILabelMaxRow = null;
	private UISpinBox ivjUISpinBoxManRow = null;
	// bsj edit:增加返回的最大行数
	private ReportBaseClass gzpanl = null;
	private int nGzIdx = -1;
	// deptment selected
	private DeptInfoVOSelDlgUI deptInfoDlg = null;
	//
	private UILabel lbPsnName = null;
	private UIRefPane refPerson = null;

	//
	private ItemInfoTRSelDlgForMultiReportUI itemInfoDlg = null;

	// current show vo
	private CircHashVO[] showVOBuf = null;

	// 所有会计年度的所有会计期间
	private Hashtable hashPeriod = null;

	// 是否打印 0 值的相关参数
	private boolean printZero = true;
	private boolean getFlag = false;
	private String psnclspowerSql;

	/**
	 * WaMultiReportUI 构造子注解。
	 */
	public WaMultiReportUI() {
		super();
		initialize();
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-3-12 12:35:32)
	 * 
	 * @return boolean
	 * @param queryref
	 *            nc.ui.pub.query.QueryConditionClient
	 */
	boolean checkSearchUIOperator(QueryConditionDLG queryref,
			ItemInfoTRSelDlgForMultiReportUI itemref) {
		if (queryref.getResult() == UIDialog.ID_OK) { // 得到选择条件
			String sqlWhere = " ";

			strWhereSql = QueryConditionDlgUtils.getStWherePart(queryref, null,
					null);
			;
			// ////////////
			// yxl add 仿照薪资发放代码

			if (getDeptPower() != null && !getDeptPower().equals("")) {
				if ((sqlWhere != null) && (!sqlWhere.trim().equals(""))) {
					sqlWhere = sqlWhere.substring(5);
					// strWhereSql = strWhereSql + " and " + "(" + sqlWhere +"
					// and "++ ")";
					strWhereSql = StringUtils.joinCondition(strWhereSql,
							sqlWhere, getDeptPower(), false);
				} else {
					strWhereSql = StringUtils.joinCondition(strWhereSql,
							getDeptPower(), "", false);// strWhereSql + " and "
					// + getDeptPower();
				}
			}
			// ////////////
		} else if (queryref.getResult() == UIDialog.ID_CANCEL) {
			// 返回上一个界面，如果确定，显示查询界面，否则，取消此次查询
			itemref.showModal();
			//
			if (itemref.getResult() == UIDialog.ID_OK) { // 确定按钮
				itemSelectedVOs = itemref.getSelectedVo(); // 得到选中的薪资项目
				if (itemSelectedVOs == null || itemSelectedVOs.length == 0) {
					showErrorMessage(nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("60131604", "UPP60131604-000110")/*
							 * @res
							 * "没有选择薪资项目！"
							 */);
					return false;
				}
				// view search dlg--return
				queryref.showModal();
				checkSearchUIOperator(queryref, itemref);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-3-12 12:14:50)
	 * 
	 * @return boolean
	 * @param itemUI
	 *            nc.ui.wa.wa_024.ItemInfoVOSelDlgUI
	 */
	boolean checkWaItemOperator(ItemInfoTRSelDlgForMultiReportUI itemref) {
		if (itemref.getResult() == UIDialog.ID_OK) {
			itemSelectedVOs = itemref.getSelectedVo(); // 得到选中的薪资项目
			if (itemSelectedVOs == null || itemSelectedVOs.length == 0) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000110")/* @res "没有选择薪资项目！" */);
				return false;
			}
			
			itemSelectedExpVOs = itemref.getSelectedExpVo();// 得到选中的导出项目
			
		} else if (itemref.getResult() == UIDialog.ID_CANCEL) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 得到所有的数据项表达式数组 也就是返回所有定义的数据项的表达式
	 * 
	 */
	public java.lang.String[] getAllDataItemExpress() {
		return null;
	}

	/*
	 * 下面三种表格模版得到的结果都一样，但它的定义方式不同： ←↑→↓
	 * 
	 * (1) 每一个数据项(制表人除外) 都是向下扩展,没有任何依赖关系
	 * ------------------------------------------ 金额\科目 | 科目01 | 科目02
	 * ---------------------------------- (日期) ↓ | (科目01)↓ | (科目02)↓
	 * ------------------------------------------ 制表人: (制表人)
	 * 
	 * (2) (日期) 下扩展 (科目) 右扩展 (金额) 依赖于 (科目) ------------------------ 金额\科目 | (科目) →
	 * ------------------------ (日期) ↓ | (金额) ----------------------- 制表人: (制表人)
	 * 
	 * (3) (日期) 下扩展 (科目) 右扩展 (金额) 依赖于 (科目 日期) ------------------------ 金额\科目 |
	 * (科目) → ------------------------ (日期) ↓ | (金额) ------------------------
	 * 制表人: (制表人)
	 * 
	 * 打印结果: -------------------------------- 金额\科目 | 科目1 | 科目2
	 * -------------------------------- 1999 | 100 | 400 2000 | 200 | 500 3001 |
	 * 300 | 600 -------------------------------- 制表人: xxx
	 */
	public java.lang.String[] getAllDataItemNames() {
		return null;
	}

	/**
	 * 返回 BillCardPanel 特性值。
	 * 
	 * @return nc.ui.pub.bill.BillCardPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.bill.BillCardPanel getBillCardPanel() {
		if (ivjBillCardPanel == null) {
			try {
				ivjBillCardPanel = new nc.ui.pub.bill.BillCardPanel();
				ivjBillCardPanel.setName("BillCardPanel");
				ivjBillCardPanel.setBounds(462, 443, 91, 31);
				// user code begin {1}
				ivjBillCardPanel.setBillData(bill);
				ivjBillCardPanel.setBodyMenuShow(false);
				ivjBillCardPanel.setTatolRowShow(true);
				ivjBillCardPanel.getBodyPanel().getTable()
				.setSortEnabled(false);

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjBillCardPanel;
	}

	/**
	 * 获得原币币种精度
	 * 
	 * 创建日期：(2001-11-20 20:36:25)
	 * 
	 * @return int
	 * @param waclassid
	 *            java.lang.String
	 */
	public int getClassCurrDigit(String waclassid) {
		return WAGlobalData.getCurrDecimal(waclassid);
	}

	/**
	 * 得到指定的薪资类别、薪资项目、查询条件 调整顺序：查询模板-->薪资类别-->薪资项目
	 * 
	 * 创建日期：(2001-9-5 20:14:44)
	 * 
	 * @return boolean
	 */
	public boolean getCondition(int iSel, List<WaCurrPeriodVO> periodList) {
		try {
			if (isAfterCurWaPeriod()) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000112")/*
						 * @res
						 * "所选期间在当前登录期间之后，没有数据！"
						 */);
				return false;
			}
			// 清空
			itemSelectedVOs = null;
			waclassSelectedVOs = null;
			strWhereSql = "";
			// ------------------------------------------------------------------------
			// 选择薪资类别
			// -----------------------------------------------------------
			WaClassVOSelDlgUI waclassref = new WaClassVOSelDlgUI(this,
					periodList);
			// -----------------------------------------------------------
			waclassref.showModal();
			// ----------------------------------------------------------------------

			if (waclassref.getResult() == UIDialog.ID_OK) {
				waclassSelectedVOs = waclassref.getSelectedVo(); // 得到选择的薪资类别
				if (waclassSelectedVOs == null
						|| waclassSelectedVOs.length == 0) {
					showErrorMessage(nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("60131604", "UPP60131604-000111")/*
							 * @res
							 * "没有选择薪资类别！"
							 */);
					return false;
				}
			} else {
				return false;
			}

			List<WaGlobalVO> list = new ArrayList<WaGlobalVO>();
			for (WaCurrPeriodVO pervo : periodList) {
				WaclassVO[] waclassVOs = waclassref.getSelectedVo();
				List<WaGlobalVO> waList = new ArrayList<WaGlobalVO>();
				for (int i = 0; i < waclassVOs.length; i++) {
					if (waclassVOs[i] == null) {
						continue;
					}
					WaGlobalVO waGlobalVO = new WaGlobalVO();
					waGlobalVO.setCurPk_corp(Global.getCorpPK());
					waGlobalVO.setCurUserid(Global.getUserID());
					waGlobalVO.setWaClassPK(waclassVOs[i].getPrimaryKey());
					waGlobalVO.setWaYear(pervo.getVyear());
					waGlobalVO.setWaPeriod(pervo.getVmonth());

					//、
					waGlobalVO.setWaYear_end(vendPeriod.split("-")[0]);
					waGlobalVO.setWaPeriod_end(vendPeriod.split("-")[1]);
					//、
					waList.add(waGlobalVO);

				}
				list.addAll(waList);
			}

			// ------------------------------------------------------------------------
			// 选择薪资项目 iSel=1明细表 iSel=2单类别汇总表 iSel=3多类别汇总表
			ItemInfoTRSelDlgForMultiReportUI itemref = new ItemInfoTRSelDlgForMultiReportUI(
					this, iSel, list.toArray(new WaGlobalVO[0]));
			itemref.showModal();
			// ------------------------------------------------------------------------
			// 薪资项目选择操作
			// boolean checkWaItemOperator(nc.ui.wa.wa_024.ItemInfoVOSelDlgUI
			// itemUI)
			boolean itemStat = checkWaItemOperator(itemref);
			if (!itemStat) {
				return itemStat;
			}
			// ------------------------------------------------------------------------
			// 设置查询条件
			HrQueryDialog queryref = new QueryConditionDlgBuilder()
			.createQueryConditionDLG(this);
			queryref.setFieldValueEditor(this);
			queryref.registerFilterEditorFactory(new ItemFilterEditorFactory(
					queryref.getQueryContext()) {
				@Override
				public ItemVO[] getItemVOs() throws BusinessException {
					return nc.ui.wa.pub.WADelegator.getWaItem().queryAllItem(
							strPk_corp);
				}
			});

			queryref.showModal();
			boolean searchStat = checkSearchUIOperator(queryref, itemref);
			if (!searchStat) {
				return false;
			}
		} catch (Exception e) {
			reportException(e);
		}
		return true;
	}

	/**
	 * 整合得到的WaMultiReportVO01[]
	 * 
	 * 创建日期：(2001-9-10 17:24:26)
	 * 
	 * @return nc.vo.wa.wa_020.WaMultiReportVO01[]
	 */
	public WaMultiReportVO01[] getConform001VO(String vyear, String vmonth)
	throws BusinessException {
		WaMultiReportVO01[] vos = null;// 整合后的VO

		int intMaxRow = 0;
		if (getUISpinBoxManRow().getValue() == 0) {
			intMaxRow = 1000;
		} else {
			intMaxRow = getUISpinBoxManRow().getValue();
		}

		try {
			vos = WADelegator.getWaWaMultiReport()
			.queryWaMultiReport01ByAccPeriod(itemSelectedVOs,
					strWhereSql, waclassSelectedVOs, vyear, vmonth,
					new Integer(intMaxRow), Global.getUserID(),itemSelectedExpVOs);// 要整合的VO zhanghua
		} catch (FrameworkEJBException e) {
			e.printStackTrace();
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("60131604", "UPP60131604-000181")/*
					 * @res
					 * "数据溢出错误，
					 * 原因选择的类别太多或者项目太多."
					 */);
		}

		return vos;
	}

	/**
	 * 整合得到的WaMultiReportVO02[]
	 * 
	 * 创建日期：(2001-9-10 17:24:26)
	 * 
	 * @return nc.vo.wa.wa_020.WaMultiReportVO02[]
	 */
	public WaMultiReportVO02[] getConform002VO(String vyear, String vmonth)
	throws Exception {
		WaMultiReportVO02[] vos = null;// 整合后的VO
		try {
			// String sPeriod =
			// getUIComboBoxPeriod().getSelectedItem().toString();
			vos = WADelegator.getWaWaMultiReport()
			.queryWaMultiReport02ByAccperiod(itemSelectedVOs,
					strWhereSql, waclassSelectedVOs, vyear, vmonth,
					Global.getUserID());

		} catch (Exception e) {
			reportException(e);
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000113")/* @res "查询数据库出错!" */);
			throw new Exception(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000114")/* @res "查询数据库出错" */);
		}
		return vos;
	}

	/**
	 * 获得币种精度
	 * 
	 * 创建日期：(2001-11-22 9:07:22)
	 * 
	 * @return java.util.Vector
	 */
	public boolean getCurrDigitVector() {
		try {
			if (waclassSelectedVOs == null && waclassSelectedVOs.length <= 0) {
				return false;
			}
			vCurrDigit = new Vector<Integer>();

			for (WaclassVO waclassSelectedVO : waclassSelectedVOs) {
				if (waclassSelectedVO == null) {
					continue;
				}
				int iDigit = getClassCurrDigit(waclassSelectedVO
						.getPrimaryKey());

				vCurrDigit.addElement(new Integer(iDigit));
			}
			vCurrDigit.addElement(new Integer(getZBDigit()));
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000115")/* @res "币种精度取值异常！" */);
			return false;
		}
		return true;
	}

	/**
	 * 获得币种主键名称哈希表
	 * 
	 * 创建日期：(2001-10-12 12:23:41)
	 */
	public Hashtable<String, String> getCurrtypeHash() {
		try {
			if (hCurrtype == null) {
				nc.vo.bd.b20.CurrtypeVO[] vo = nc.ui.bd.b20.CurrtypeBO_Client
				.queryAll(null);
				hCurrtype = new Hashtable<String, String>();
				for (CurrtypeVO element : vo) {
					hCurrtype.put(element.getPrimaryKey(), element
							.getCurrtypename());
				}
			}

		} catch (Exception e) {
			hCurrtype = new Hashtable<String, String>();
		}

		return hCurrtype;
	}

	/**
	 * 
	 * 返回依赖项的名称数组，该数据项长度只能为 1 或者 2 返回 null : 没有依赖 长度 1 : 单项依赖 长度 2 : 双向依赖
	 * 
	 */
	public java.lang.String[] getDependentItemExpressByExpress(
			java.lang.String itemName) {
		if (itemName.equals("itemvalues")) {
			return new String[] { "items" };
		} else {
			return null;
		}
	}

	/*
	 * 返回所有的数据项对应的内容 参数： 数据项的名字 返回： 数据项对应的内容，只能为 String[]； 如果 itemName 拥有依赖项，则：
	 * 1 个依赖项：打印系统将根据依赖项的内容的顺序来判断 String[] 中的存放的数据 2 个依赖项：打印系统将根据两个依赖项的索引来决定数据
	 * 
	 * 模板 2 的情况: [科目] ==> [100 200 300 --> 400 500 600]
	 * 
	 * 模板 3 的情况: 如果 getDependItemNamesByName("金额") ==
	 * 
	 * [科目 日期] ==> [100 200 300 400 500 600] 先列后行 [日期 科目] ==> [100 400 200 500
	 * 300 600] 先行后列
	 * 
	 */
	public java.lang.String[] getItemValuesByExpress(
			java.lang.String itemExpress) {

		int iSel = getUIListSel().getSelectedIndex() + 1;
		if (iSel == 1 || iSel == 2) {
			if (itemExpress.equals("deptname")) {
				return l_deptnames;
			} else if (itemExpress.equals("deptcode")) {
				return l_deptcodes;
			} else if (itemExpress.equals("psnname")) {
				return l_psnnames;
			} else if (itemExpress.equals("psncode")) {
				return l_psncodes;
			} else if (itemExpress.equals("jobname")) {
				return l_jobnames;
			} else if (itemExpress.equals("vperiod")) {
				return l_vperiods;
			} else if (itemExpress.equals("id")) {
				return l_IDs;
			} else if (itemExpress.equals("bankname")) {
				return l_bankname;
			} else if (itemExpress.equals("vacccode")) {
				return l_vacccode;
			} else if (itemExpress.equals("items")) {
				return l_straryitemsColNames;
			} else if (itemExpress.equals("waclasses")) {
				return l_strarywaclassColNames;
			} else if (itemExpress.equals("itemvalues")) {//
				getBillCardPanel().getBillModel().getBodyItems();
				if (l_printitemvalues != null && l_printitemvalues.length > 0) {
					String[] tempvalues = new String[l_printitemvalues.length];
					for (int i = 0; i < l_printitemvalues.length; i++) {
						if (l_printitemvalues[i] == null
								|| "".equals(l_printitemvalues[i].trim())) {
							tempvalues[i] = "";
						} else {
							tempvalues[i] = l_printitemvalues[i];
							tempvalues[i] = getValueByPara(tempvalues[i]);
						}

					}
					return tempvalues;
				}

				return getValueByPara(l_printitemvalues);
			} else if (itemExpress.equals("cperiod")) {
				return new String[] { strCurYear + "-" + strCurPeriod };
			} else if (itemExpress.equals("lister")) {// 制表人 不需要经过是否打印0值的检验
				return new String[] { getBillCardPanel().getTailItem(
						m_sTailItemLister).getValue() };
			} else if (itemExpress.equals("assessor")) {// 审核人 不需要经过是否打印0值的检验
				return new String[] { getBillCardPanel().getTailItem(
						m_sTailItemassessor).getValue() };
			}
		} else {
			if ("theyear".equals(itemExpress)) {
				return new String[] { getGzpanl(iSel).getHeadItem("theyear")
						.getValue() };
			} else if ("hdeptname".equals(itemExpress)) {
				return new String[] { getGzpanl(iSel).getHeadItem("hdeptname")
						.getValue() };
			} else if ("hpsnname".equals(itemExpress)) {
				return new String[] { getGzpanl(iSel).getHeadItem("hpsnname")
						.getValue() };
			} else {
				ArrayList<String> array = new ArrayList<String>();
				String[] names = showVOBuf[0].getAttributeNames();
				for (String name2 : names) {
					array.add(name2);
				}

				if (array.contains(itemExpress)) {
					array.clear();
					for (CircHashVO element : showVOBuf) {
						Object obj = element.getAttributeValue(itemExpress);
						if (obj != null) {
							if (element.getAttributeType(itemExpress) == IDataType.NUMERIC) {
								array.add(getValueByPara(obj.toString()));
							} else {
								array.add(obj.toString());
							}

						} else {
							array.add("");
						}

					}

					if (getGzpanl(nGzIdx).getBodyPanel().isTatolRow()) {
						Object o = getGzpanl(nGzIdx).getTotalTableModel()
						.getValueAt(
								0,
								getGzpanl(nGzIdx).getBodyColByKey(
										itemExpress));
						if (itemExpress.equals("corpname")
								|| itemExpress.equals("deptname")) {
							array
							.add(nc.ui.ml.NCLangRes.getInstance()
									.getStrByID("common",
									"UC000-0001146")/* @res "合计" */);
						} else {
							if (o == null) {
								array.add("");
							} else {
								if (isUFDouble(o.toString())) {// 只好这样了,怎么判断该列的类型呢?
									array.add(getValueByPara(o.toString()));
								} else {
									array.add(o.toString());
								}
							}
						}
					}
					return array.toArray(new String[0]);
				}
			}
		}

		return null;
	}

	/**
	 * 核查一个字符串是否是 double 字符串
	 * 
	 * @param str
	 * @return
	 */
	private boolean isUFDouble(String str) {
		try {
			new UFDouble(str);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private String[] getValueByPara(String[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = getValueByPara(a[i]);
		}
		return a;
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	private String getValueByPara(String a) {
		if (!isUFDouble(a)) {
			return a;
		}
		UFDouble temp = new nc.vo.pub.lang.UFDouble(a);
		String tempValue = "";

		if (temp != null) {
			if (Validator.isZero(temp) && !printZero()) {
				tempValue = "";
			} else {
				tempValue = temp.toString();
			}
		} else {
			tempValue = "";
		}
		return tempValue;
	}

	/**
	 * 参数设置中是否打印零值
	 * 
	 * @return
	 * @author xuanlt 2007-08-20
	 */
	private boolean printZero() {
		try {
			if (!getFlag) {
				printZero = WADelegator.getWaParValue().getParaBoolean(
						Global.getCorpPK(), "WA-PRINTZERO").booleanValue();
				getFlag = true;
			}
			return printZero;

		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 返回节点号。
	 * 
	 * @version (00-6-6 13:33:25)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getModuleCode() {
		String code = null;// super.getModuleCode();
		if (code == null || code.trim().length() < 1 || "100000".equals(code)) {
			code = "60131604";// 默认
		}
		return code;
	}

	/*
	 * 返回该数据源对应的节点编码
	 */
	public java.lang.String getModuleName() {
		return null;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-9-18 12:56:52) 返回显示在状态条上的提示信息，
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getStatusHintStr() {
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
		"UPP60131604-000052")/* @res "当前公司" */
		+ ": "/* @res "：" */+ nc.ui.hr.global.Global.getWaCorpName();
	}

	/**
	 * 子类实现该方法，返回业务界面的标题。
	 * 
	 * @version (00-6-6 13:33:25)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getTitle() {
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
		"UPP60131604-000117")/* @res "多类别薪资变动报表" */;
	}

	/**
	 * 返回 UILabelMaxRow 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabelMaxRow() {
		if (ivjUILabelMaxRow == null) {
			try {
				ivjUILabelMaxRow = new nc.ui.pub.beans.UILabel();
				ivjUILabelMaxRow.setName("UILabelMaxRow");
				ivjUILabelMaxRow
				.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000118")/*
						 * @res
						 * "最大行数："
						 */);
				ivjUILabelMaxRow.setBounds(159, 74, 66, 22);
				ivjUILabelMaxRow.setVisible(true);
				// user code begin {1}
				ivjUILabelMaxRow.setILabelType(UILabel.STYLE_NOTNULL);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabelMaxRow;
	}

	/**
	 * 返回 UILabelMaxRow 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILblPsn() {
		if (lbPsnName == null) {
			try {
				lbPsnName = new nc.ui.pub.beans.UILabel();
				lbPsnName.setName("UILabelPsn");
				lbPsnName.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"common", "UC000-0000135")/* @res "最大行数：" */);
				lbPsnName.setBounds(159, 74, 66, 22);
				// user code begin {1}
				lbPsnName.setVisible(false);
				lbPsnName.setILabelType(UILabel.STYLE_NOTNULL);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return lbPsnName;
	}

	/**
	 * 返回 refPerson 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIRefPane getrefPerson() {

		if (refPerson == null) {
			try {
				refPerson = new nc.ui.pub.beans.UIRefPane();
				refPerson.setName("refpanel");
				refPerson.setText("");
				refPerson.setBounds(502, 66, 135, 22);
				refPerson.setRefNodeName("人员档案");
				refPerson.setReturnCode(false);
				refPerson.setRefInputType(1/** 名称 */
				);
				refPerson.setBounds(291, 73, 120, 22);
				// user code begin {1}
				refPerson.setRefType(IRefConst.GRIDTREE);
				refPerson.setIsCustomDefined(true);

				String st = " bd_psndoc.pk_psndoc in (select distinct psnid from wa_data left outer join wa_waclass on wa_data.classid = wa_waclass.pk_wa_class "
					+ " where wa_waclass.pk_corp in ('0001', '"
					+ Global.getCorpPK()
					+ "' ) and wa_data.dr = 0 and wa_waclass.dr = 0 )";

				refPerson.getRefModel().setWherePart(st);
				refPerson.setButtonFireEvent(true);
				refPerson.setVisible(false);
				getrefPerson().getRefModel().setUseDataPower(false);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				ivjExc.printStackTrace();
			}
		}

		return refPerson;
	}

	/**
	 * 返回 UILabelPeriod 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabelPeriod() {
		if (ivjUILabelPeriod == null) {
			try {
				ivjUILabelPeriod = new nc.ui.pub.beans.UILabel();
				ivjUILabelPeriod.setName("UILabelPeriod");
				ivjUILabelPeriod
				.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000119")/*
						 * @res
						 * "请输入报表的期间"
						 */);
				ivjUILabelPeriod.setBounds(159, 39, 108, 22);
				// user code begin {1}
				ivjUILabelPeriod.setILabelType(UILabel.STYLE_NOTNULL);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabelPeriod;
	}

	/**
	 * 返回 UILblPeriod 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILblPeriod() {
		if (ivjUILblPeriod == null) {
			try {
				ivjUILblPeriod = new nc.ui.pub.beans.UILabel();
				ivjUILblPeriod.setName("UILblPeriod");
				ivjUILblPeriod.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("common", "UC000-0002560")/* @res "期间" */);
				ivjUILblPeriod.setLocation(495, 39);
				// user code begin {1}
				ivjUILblPeriod.setSize(120, 20);
				ivjUILblPeriod.setILabelType(UILabel.STYLE_NOTNULL);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILblPeriod;
	}

	/**
	 * 返回 UILblYear 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILblYear() {
		if (ivjUILblYear == null) {
			try {
				ivjUILblYear = new nc.ui.pub.beans.UILabel();
				ivjUILblYear.setName("UILblYear");
				ivjUILblYear
				.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000201")/* @res "年" */);
				ivjUILblYear.setBounds(396, 39, 32, 22);
				// user code begin {1}
				ivjUILblYear.setILabelType(UILabel.STYLE_NOTNULL);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILblYear;
	}

	/**
	 * 返回 UIPanel1 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIPanel() {
		if (ivjUIPanel == null) {
			try {
				ivjUIPanel = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel.setName("UIPanel");
				ivjUIPanel.setLayout(null);
				getUIPanel().add(getUILabelPeriod(),
						getUILabelPeriod().getName());
				getUIPanel().add(getLStartPeriod());
				getUIPanel().add(getStartPeriod());
				getUIPanel().add(getLEndPeriod());
				getUIPanel().add(getEndPeriod());
				getUIPanel().add(getUILblYear());
				getUIPanel().add(getUITfWaYear());
				getUIPanel().add(getUISpinBoxManRow(),
						getUISpinBoxManRow().getName());
				getUIPanel().add(getUILabelMaxRow(),
						getUILabelMaxRow().getName());
				// user code begin {1}
				getUIPanel().add(getUILblPsn(), getUILblPsn().getName());
				getUIPanel().add(getrefPerson(), getrefPerson().getName());
				getUIPanel().add(getLableForSel(), getLableForSel().getName());
				getUIPanel().add(getSclPane(), getSclPane().getName());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIPanel;
	}

	/**
	 * 返回 UISpinBoxManRow 特性值。
	 * 
	 * @return nc.ui.pub.beans.UISpinBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UISpinBox getUISpinBoxManRow() {
		if (ivjUISpinBoxManRow == null) {
			try {
				ivjUISpinBoxManRow = new nc.ui.pub.beans.UISpinBox();
				ivjUISpinBoxManRow.setName("UISpinBoxManRow");
				ivjUISpinBoxManRow.setMaximumValue(10000);
				ivjUISpinBoxManRow.setToolTipText(nc.ui.ml.NCLangRes
						.getInstance().getStrByID("60131604",
						"UPP60131604-000120")/* @res "设置您要查看的最大记录数" */);
				ivjUISpinBoxManRow.setVisible(true);
				ivjUISpinBoxManRow.setValue(1000);
				ivjUISpinBoxManRow.setStep(100);
				ivjUISpinBoxManRow.setMaximumLength(4);
				ivjUISpinBoxManRow.setMinimumValue(10);
				ivjUISpinBoxManRow.setBounds(291, 73, 65, 22);
				ivjUISpinBoxManRow.setEditable(true);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUISpinBoxManRow;
	}

	/**
	 * 返回 UITfWaPeriod 特性值。
	 * 
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getUITfWaPeriod() {
		if (ivjUITfWaPeriod == null) {
			try {
				ivjUITfWaPeriod = new nc.ui.pub.beans.UITextField();
				ivjUITfWaPeriod.setName("UITfWaPeriod");
				ivjUITfWaPeriod.setMaxValue(99.0);
				ivjUITfWaPeriod.setNumPoint(0);
				ivjUITfWaPeriod.setMinValue(0.0);
				ivjUITfWaPeriod.setBounds(421, 40, 66, 20);
				ivjUITfWaPeriod.setTextType("TextInt");
				ivjUITfWaPeriod.setMaxLength(2);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUITfWaPeriod;
	}

	private UILabel getLStartPeriod() {
		if (startL == null) {
			startL = new UILabel();
			startL.setName("UILblYear");
			startL.setText("开始期间");
			startL.setBounds(261, 40, 66, 20);
			startL.setILabelType(UILabel.STYLE_NOTNULL);
		}
		return startL;
	}

	private UILabel getLEndPeriod() {
		if (endL == null) {
			endL = new UILabel();
			endL.setName("UILblYear");
			endL.setText("结束期间");
			endL.setBounds(441, 40, 66, 20);
			endL.setILabelType(UILabel.STYLE_NOTNULL);
		}
		return endL;
	}

	private UIRefPane getStartPeriod() {
		if (startPane == null) {
			startPane = new UIRefPane("会计期间");
			startPane.setBounds(321, 40, 100, 20);
		}
		return startPane;
	}

	private UIRefPane getEndPeriod() {
		if (endPane == null) {
			endPane = new UIRefPane("会计期间");
			endPane.setBounds(541, 40, 100, 20);
		}
		return endPane;

	}

	/**
	 * 返回 UITfWaYear 特性值。
	 * 
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getUITfWaYear() {
		if (ivjUITfWaYear == null) {
			try {
				ivjUITfWaYear = new nc.ui.pub.beans.UITextField();
				ivjUITfWaYear.setName("UITfWaYear");
				ivjUITfWaYear.setMaxValue(9999.0);
				ivjUITfWaYear.setNumPoint(0);
				ivjUITfWaYear.setMinValue(0.0);
				ivjUITfWaYear.setLocation(291, 40);
				ivjUITfWaYear.setTextType("TextInt");
				ivjUITfWaYear.setMaxLength(4);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUITfWaYear;
	}

	/**
	 * 获得本币币种精度
	 * 
	 * 创建日期：(2001-11-20 20:39:02)
	 */
	public Integer getZBDigit() {
		if (iZBdigit == null) {
			iZBdigit = WAGlobalData.getCurrDecimal(WAGlobalData
					.getLocalCurrPk());
		}
		return iZBdigit;
	}

	/**
	 * 每当部件抛出异常时被调用
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* 除去下列各行的注释，以将未捕捉到的异常打印至 stdout。 */
		exception.printStackTrace(System.out);
	}

	private int getItemSymbol(Object pk_wa_item, ItemVO[] itemvos) {
		int symbol = 0;
		String pk_item = pk_wa_item.toString();
		for (ItemVO itemVO : itemvos) {
			if (itemVO.getPk_wa_item().equals(pk_item)) {
				/**
				 * 大家好！
				 * 
				 * 关于员工收入台账，最终讨论结果如下：
				 * 
				 * 系统项目可加入员工收入台账选择，系统项中的本次扣税作为减项处理，其它系统项 不参与计算。
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 张旭
				 * 
				 * 2006年12月15日
				 * 
				 */
				if (itemVO.getIdefaultflag().intValue() == 1) {// 系统项目
					if (itemVO.getIitemid() == 4) {// 本次扣税
						symbol = -1;
					} else {
						symbol = 0;
					}

				} else {
					if (itemVO.getIproperty().intValue() == 0) {// 增项
						symbol = 1;
					} else if (itemVO.getIproperty().intValue() == 1) {// 减项
						symbol = -1;
					} else {// 其他项目
						symbol = 0;
					}
				}
			}
		}
		return symbol;
	}

	private GeneralVO[] getYearAvg(GeneralVO[] showVOBuf) {
		int nSize = showVOBuf.length;

		for (int i = 0; i < nSize; i++) {
			String[] names = showVOBuf[i].getAttributeNames();
			UFDouble yearAvg = new UFDouble(0);
			UFDouble yearSum = new UFDouble(0);
			int numberOfPeriod = 0;
			for (String name2 : names) {
				if (name2.startsWith("m")) {
					Object tmpobj = showVOBuf[i].getAttributeValue(name2);
					if (tmpobj != null) {
						yearSum = yearSum.add(new UFDouble(tmpobj.toString()));
						numberOfPeriod++;
					}
				}

			}
			if (numberOfPeriod != 0) {
				yearAvg = yearSum.div(numberOfPeriod);
				yearAvg = yearAvg.setScale(2, UFDouble.ROUND_HALF_UP);
				if (showVOBuf[i].getAttributeValue("yearavg") == null) {
					showVOBuf[i].setAttributeValue("yearavg", yearAvg);
					showVOBuf[i].setAttributeValue("yearsum", yearSum.setScale(
							2, UFDouble.ROUND_HALF_UP));
				}
			}
		}
		return showVOBuf;
	}

	/**
	 * 初始化报表
	 * 
	 * 创建日期：(2001-9-5 19:41:33)
	 */
	public void initData(List<WaCurrPeriodVO> periodList) {
		try {
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000121")/* @res "正在打开报表......" */);
			setBnState(false, false, false);
			setButtons(ButtonGroupReport);
			int iSel = getUIListSel().getSelectedIndex() + 1; // 获得当前选择的表序号
			if (!getCondition(iSel, periodList)) {
				setBnState(true, false, false);
				setButtons(ButtonGroupReport);
				return;
			}
			if (!getCurrDigitVector()) { // 获得所选类别币种精度和主币精度
				setBnState(true, false, false);
				setButtons(ButtonGroupReport);
				return;
			}

			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000122")/* @res "正在初始化表格......" */);
			ivjBillCardPanel = null;

			setTitleText(getUIListSel().getSelectedValue().toString());
			title = getUIListSel().getSelectedValue().toString();

			if (iSel == 1) { // 明细表
				initTable_list(); // 表格栏
				setData_list(periodList); // 设置数据
			} else if (iSel == 2) { // 汇总表
				initTable_collect(); // 表格栏
				setData_collect(periodList); // 设置数据
			}
			remove(ivjUIPanel);
			add(getBillCardPanel());
			bnOk.setEnabled(true);
			setBnState();
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000123")/* @res "打开报表完成" */);
		} catch (Exception e) {
			reportException(e);
			showWarningMessage(e.getMessage());
		}
	}

	/**
	 * 得到部门权限 lazy init
	 * 
	 * @return
	 */
	private String getDeptPower() {
		try {
			if (deptPower == null) {
				String[] strings = WaGlobal.getDataPower(false);
				String powerSql = strings[0];// nc.ui.hr.global.GlobalTool.getPowerSql("bd_deptdoc",Global.getUserID(),Global.getCorpPK());
				String psnclspowerSql = strings[1];//
				if (powerSql == null) {
					// 没有启用部门权限
					deptPower = "";
				} else {
					// 启用部门权限
					if (powerSql.length() > 0) {
						deptPower = "wa_data.deptid in (" + powerSql + ")";
					} else {
						// 启用了，但是没有分配给权限
						deptPower = " and 0>1";
					}
				}

				if (psnclspowerSql == null) {
					// 没有启用部门权限
					psnclspowerSql = "";
				} else {
					// 启用部门权限
					if (psnclspowerSql.length() > 0) {
						psnclspowerSql = "wa_data.psnclid in ("
							+ psnclspowerSql + ")";
					} else {
						// 启用了，但是没有分配给权限
						psnclspowerSql = " and 0>1";
					}
				}

			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			deptPower = " and 0>1";
			psnclspowerSql = " and 0>1 ";
		}
		return StringUtils.joinCondition("", deptPower, psnclspowerSql, false);
	}

	// private String getPower(){
	// String[] strings = WaGlobal.getDataPower(false);
	// String powerSql =
	// strings[0];//nc.ui.hr.global.GlobalTool.getPowerSql("bd_deptdoc",Global.getUserID(),Global.getCorpPK());
	// String psnclspowerSql = strings[1];
	// if(powerSql==null || psnclspowerSql==null){
	// //没有启用部门权限
	//			
	// }
	// //
	// }

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {

			// user code end
			setName("WaMultiReportUI");
			setLayout(new java.awt.BorderLayout());
			setSize(774, 419);
			add(getUIPanel(), "Center");
			bnOk.setEnabled(true);
			bnCancel.setEnabled(false);
			bnPrint.setEnabled(false);
			bnExport.setEnabled(false);
			setButtons(ButtonGroupReport);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		strPk_corp = Global.getWaCorpPk();
		m_userId = Global.getUserID();
		getUILblYear().setVisible(false);
		getUITfWaYear().setVisible(false);
		getUILabelMaxRow().setVisible(false);
		getUISpinBoxManRow().setVisible(false);
	}

	private Hashtable getAccPeriods() {
		try {
			if (hashPeriod == null) {
				hashPeriod = WADelegator.getWaWaMultiReport()
				.getReportPeriodByYear();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			hashPeriod = new Hashtable();
		}

		return hashPeriod;
	}

	/**
	 * 初始化汇总表格栏
	 * 
	 * 创建日期：(2001-9-10 11:05:16)
	 */
	public void initTable_collect() {

		int fixLength =3;
		try {
			// 设置表格栏
			aryFixnames = new String[fixLength + (itemSelectedVOs.length)];
			aryKeys = new String[fixLength + (itemSelectedVOs.length)];
			// 第一列
			aryFixnames[0] = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"common", "UC000-0004069")/* @res "部门名称" */;
			aryKeys[0] = "deptname";
			aryFixnames[1] = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"common", "UC000-0004073")/* @res "部门编码" */;
			aryKeys[1] = "deptcode";

			aryFixnames[2] = "期间";
			aryKeys[2] = "vperiod";
			// 其他列
			int iItemCount = itemSelectedVOs.length;
			int skip = fixLength;
			for (int i = 0; i < iItemCount; i++) {
				aryFixnames[skip] = itemSelectedVOs[i].getVname();
				aryKeys[skip] = "SUM(WA_DATAZ.F_"
					+ itemSelectedVOs[i].getIitemid().intValue() + ")";
				skip++;
			}
			// 表体格式
			BillItem[] bodyItem = new BillItem[aryFixnames.length];
			for (int i = 0; i < aryFixnames.length; i++) {
				bodyItem[i] = new BillItem();
				bodyItem[i].setName(aryFixnames[i]);
				bodyItem[i].setKey(aryKeys[i]);
				bodyItem[i].setPos(BillItem.BODY);
				bodyItem[i].setEdit(false);
				bodyItem[i].setShow(true);
				bodyItem[i].setWidth(120);
			}
			bodyItem[0].setDataType(BillItem.STRING);
			bodyItem[1].setDataType(BillItem.STRING);
			bodyItem[2].setDataType(BillItem.STRING);
			for (int i = 0; i < iItemCount; i++) {
				int itemIndex = fixLength + i;
				bodyItem[itemIndex].setTatol(false);
				if (itemSelectedVOs[i].getIitemtype().intValue() == 0) {
					bodyItem[itemIndex].setDataType(BillItem.STRING);
				} else if (itemSelectedVOs[i].getIitemtype().intValue() == 1) {
					bodyItem[itemIndex].setDataType(BillItem.DECIMAL);
					// 设置原币、本币精度

					if (itemSelectedVOs[i].getIproperty().intValue() < 3) {// 与币种相关
						bodyItem[itemIndex]
						         .setDecimalDigits(((Integer) vCurrDigit
						        		 .elementAt(0)).intValue());
						bodyItem[itemIndex].setTatol(true);
					} else {// 与币种无关
						bodyItem[itemIndex]
						         .setDecimalDigits(itemSelectedVOs[i]
						                                           .getIflddecimal().intValue());
					}
				} else if (itemSelectedVOs[i].getIitemtype().intValue() == 3) {
					bodyItem[itemIndex].setDataType(BillItem.DATE);
				}
			}
			// 表头
			BillItem[] headItem = new BillItem[1];
			headItem[0] = new BillItem();
			headItem[0].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPT60131604-000004")/* @res "会计期间：" */);
			headItem[0].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000182")/* @res "会计期间：" */);
			headItem[0].setKey("cperiod");
			headItem[0].setPos(BillItem.HEAD);
			headItem[0].setDataType(BillItem.STRING);
			headItem[0].setEdit(false);
			headItem[0].setShow(true);
			headItem[0].setWidth(3);
			headItem[0].setEnabled(false);
			// headItem[1] = new BillItem();
			// headItem[1].setName("查询条件：");
			// headItem[1].setKey("");
			// headItem[1].setPos(BillItem.HEAD);
			// headItem[1].setDataType(BillItem.STRING);
			// headItem[1].setEdit(false);
			// headItem[1].setShow(true);
			// headItem[1].setWidth(2);
			// headItem[1].setEnabled(false);
			// 表尾
			BillItem[] tailItem = new BillItem[2];
			tailItem[0] = new BillItem();
			tailItem[0].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000068")/* @res "制表：" */);
			tailItem[0].setKey(m_sTailItemLister);// yxl update
			tailItem[0].setPos(BillItem.TAIL);
			tailItem[0].setDataType(BillItem.STRING);
			tailItem[0].setEdit(true);
			tailItem[0].setShow(true);
			tailItem[0].setWidth(1);
			tailItem[1] = new BillItem();
			tailItem[1].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000069")/* @res "审核：" */);
			tailItem[1].setKey(m_sTailItemassessor);// yxl update
			tailItem[1].setPos(BillItem.TAIL);
			tailItem[1].setDataType(BillItem.STRING);
			tailItem[1].setEdit(true);
			tailItem[1].setShow(true);
			tailItem[1].setWidth(1);
			// 显示表格
			bill = new BillData();
			bill.setHeadItems(headItem);
			bill.setBodyItems(bodyItem);
			bill.setTailItems(tailItem);
		} catch (Exception e) {
			reportException(e);
		}
	}

	/**
	 * 初始化明细表格栏
	 * 
	 * 创建日期：(2001-9-5 19:51:38)
	 */
	public void initTable_list() {
		int fixLen = 7;
		try {
			int iExpCount = 0;
			if(itemSelectedExpVOs != null){
				 iExpCount = itemSelectedExpVOs.length;
			}
			// 设置表格栏
			aryFixnames = new String[fixLen + iExpCount +(itemSelectedVOs.length)];
			aryKeys = new String[fixLen + iExpCount + (itemSelectedVOs.length)];
			// 第一列
			aryFixnames[0] = NCLangRes.getInstance().getStrByID("common",
			"UC000-0000134")/* @res "人员名称" */;
			aryKeys[0] = "psnname";
			//
			aryFixnames[1] = NCLangRes.getInstance().getStrByID("common",
			"UC000-0000147")/* @res "人员编码" */;
			aryKeys[1] = "psncode";
			//
			aryFixnames[2] = NCLangRes.getInstance().getStrByID("60131004",
			"UPT60131004-000001")/* @res "所在岗位" */;
			aryKeys[2] = "jobname";

			aryFixnames[3] = NCLangRes.getInstance().getStrByID("common",
			"UC000-0003914")/* @res "身份证号" */;
			aryKeys[3] = "id";

			aryFixnames[4] = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131004", "UPP60131004-000341")/* @res "代发银行" */;
			aryKeys[4] = "bankname";

			aryFixnames[5] = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131004", "UPP60131004-000342")/* @res "银行账号" */;
			aryKeys[5] = "vacccode";
			
			aryFixnames[6] = "期间";
			aryKeys[6] = "vperiod";
			
			
			
			for(int i=0;i<iExpCount;i++){
				aryFixnames[fixLen + i] = itemSelectedExpVOs[i].getVname();
				aryKeys[fixLen + i] = itemSelectedExpVOs[i].getPk_wa_item();
			}
			
			int skip = fixLen + iExpCount;
			// 其他列
			int iItemCount = itemSelectedVOs.length;
			for (int i = 0; i < iItemCount; i++) {
				aryFixnames[skip] = itemSelectedVOs[i].getVname();
				aryKeys[skip] = "WA_DATAZ.F_"
					+ itemSelectedVOs[i].getIitemid().intValue();
				skip++;
			}

			// 表体格式
			BillItem[] bodyItem = new BillItem[aryFixnames.length];
			for (int i = 0; i < aryFixnames.length; i++) {
				bodyItem[i] = new BillItem();
				bodyItem[i].setName(aryFixnames[i]);
				bodyItem[i].setKey(aryKeys[i]);
				bodyItem[i].setPos(BillItem.BODY);
				bodyItem[i].setEdit(false);
				bodyItem[i].setShow(true);
				bodyItem[i].setWidth(145);
				bodyItem[i].setNull(false);
			}
			bodyItem[0].setDataType(BillItem.STRING);
			bodyItem[1].setDataType(BillItem.STRING);

			for (int i = 0; i < iItemCount; i++) {
				int itemIndex = fixLen + iExpCount + i;//zhanghua
				bodyItem[itemIndex].setTatol(false);
				if (itemSelectedVOs[i].getIitemtype().intValue() == 0) {
					bodyItem[itemIndex].setDataType(BillItem.STRING);
				} else if (itemSelectedVOs[i].getIitemtype().intValue() == 1) {
					// && itemSelectedVOs[i].getIproperty().intValue() != 2){
					bodyItem[itemIndex].setDataType(BillItem.DECIMAL);
					// 币种精度
					if (itemSelectedVOs[i].getIproperty().intValue() < 3) {// 与币种相关
						bodyItem[itemIndex]
						         .setDecimalDigits(((Integer) vCurrDigit
						        		 .elementAt(0)).intValue());
						bodyItem[itemIndex].setTatol(true);
					} else {// 与币种无关
						bodyItem[itemIndex].setDecimalDigits(itemSelectedVOs[i]
						                                                     .getIflddecimal().intValue());
					}
				} else if (itemSelectedVOs[i].getIitemtype().intValue() == 3) {
					bodyItem[itemIndex].setDataType(BillItem.DATE);
				} else {
					bodyItem[itemIndex].setDataType(BillItem.STRING);
				}
			}

			// 表头
			BillItem[] headItem = new BillItem[1];
			headItem[0] = new BillItem();
			headItem[0].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000172")/* @res "薪资期间：" */);

			headItem[0].setKey("cperiod");
			headItem[0].setPos(BillItem.HEAD);
			headItem[0].setDataType(BillItem.STRING);
			headItem[0].setEdit(false);
			headItem[0].setShow(true);
			headItem[0].setWidth(3);
			headItem[0].setEnabled(false);
			headItem[0].setNull(false);

			// 表尾
			BillItem[] tailItem = new BillItem[2];
			tailItem[0] = new BillItem();
			tailItem[0].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000068")/* @res "制表：" */);
			tailItem[0].setKey(m_sTailItemLister);// yxl update
			tailItem[0].setPos(BillItem.TAIL);
			tailItem[0].setDataType(BillItem.STRING);
			tailItem[0].setEdit(true);
			tailItem[0].setShow(true);
			tailItem[0].setWidth(1);
			tailItem[0].setNull(false);
			tailItem[1] = new BillItem();
			tailItem[1].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000069")/* @res "审核：" */);
			tailItem[1].setKey(m_sTailItemassessor);// yxl update
			tailItem[1].setPos(BillItem.TAIL);
			tailItem[1].setDataType(BillItem.STRING);
			tailItem[1].setEdit(true);
			tailItem[1].setShow(true);
			tailItem[1].setWidth(1);
			tailItem[1].setNull(false);
			// 显示表格
			bill = new BillData();
			bill.setHeadItems(headItem);
			bill.setBodyItems(bodyItem);
			bill.setTailItems(tailItem);

		} catch (Exception e) {
			reportException(e);
		}
	}

	/**
	 * 返回所选期间是否在当前期间之后
	 * 
	 * 创建日期：(2001-9-20 15:37:26)
	 * 
	 * @return boolean
	 */
	public boolean isAfterCurWaPeriod() {
		// int iperiod = new Integer(strCurPeriod).intValue();
		// if (iperiod < (new
		// Integer(getUIComboBoxPeriod().getSelectedItem().toString()).intValue())){
		// return true;
		// }
		return false;
	}

	/*
	 * 返回该数据项是否为数字项 数字项可参与运算；非数字项只作为字符串常量 如“数量”为数字项、“存货编码”为非数字项
	 */
	public boolean isNumber(java.lang.String itemExpress) {
		return false;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2003-10-18 20:13:12)
	 */
	public void onBnOk() throws BusinessException {

		if (getUIListSel().getSelectedIndex() < 0) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000127")/* @res "请先选择要查看的报表！" */);
			return;
		}

		 strCurYear=getUITfWaYear().getText();
		if (getUIListSel().getSelectedIndex() == 2) {
			strCurYear = getUITfWaYear().getText();
			if (getUITfWaYear().getText() == null
					|| getUITfWaYear().getText().equals("")) {
				showErrorMessage("请输入要查看工资的年份信息。");
				return;
			}
		} else {
			Vector vstart = (Vector) getStartPeriod().getSelectedData().get(0);
			vstartPeriod = (String) vstart.get(1);
			Vector vend = (Vector) getEndPeriod().getSelectedData().get(0);
			vendPeriod = (String) vend.get(1);
			if (getStartPeriod().getText() == null
					|| getEndPeriod().getText() == null
					|| getStartPeriod().getText().equals("")
					|| getEndPeriod().getText().equals("")) {
				showErrorMessage("请输入要查看工资的期间信息。");
				return;
			}
		}

		

		if (getrefPerson().isVisible()) {
			String refpk = getrefPerson().getRefPK();
			if (refpk == null) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000143")/* @res "请选择想要查看的员工" */);
				return;
			}
		}

		if (getUIListSel().getSelectedIndex() > 1) { // 单位工资总额台账
			showZEPanel(null);
		} else {
			IWaServ serv = NCLocator.getInstance().lookup(IWaServ.class);
			List<WaCurrPeriodVO> periodList = serv.getWaPeriodData(
					vstartPeriod, vendPeriod);

			initData(periodList);
		}
	}

	/**
	 * 子类实现该方法，响应按钮事件。
	 * 
	 * @version (00-6-1 10:32:59)
	 * 
	 * @param bo
	 *            ButtonObject
	 */
	@Override
	public void onButtonClicked(ButtonObject bo) {
		if (bo == bnOk) {
			try {
				onBnOk();
			} catch (Exception e) {
				showErrorMessage(e.getMessage());
			}
		}
		if (bo == bnCancel) {
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000130")/* @res "选择报表" */);
			// 由报表界面返回初始界面并设置按钮状态
			if (ivjBillCardPanel != null) {
				getBillCardPanel().setVisible(false);
				remove(getBillCardPanel());
			}
			if (nGzIdx != -1) {
				remove(getGzpanl(nGzIdx));
			}
			add(ivjUIPanel);
			setTitleText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000131")/* @res "多类别薪资发放表" */);
			setBnState();
			updateUI();
		}
		if (bo == bnPrint) {
			// bnPrint.setEnabled(false);
			// setButtons(ButtonGroupReport);
			//onPrint();
			onPrintBak();
		}
		
		if(bo == bnExport){
			onExport();
		}
	}

	/**
	 *数据导出 
	 *
	 */
	public void onExport(){
		ExcelOut excel = new ExcelOut(this,title,"");
		
		// 得到列名
		String[] l_straryColNames = new String[getBillCardPanel()
		                                       .getBillTable().getColumnCount()];
		for (int i = 0; i < l_straryColNames.length; i++) {
			l_straryColNames[i] = getBillCardPanel().getBillTable()
			.getColumnName(i);
		}
		
		int li_row = getBillCardPanel().getBillModel().getRowCount();
		Object[][] l_printdata = new Object[li_row + 2][l_straryColNames.length];
		
		// 列名头导出
		for(int i = 0; i < l_straryColNames.length; i++){
			l_printdata[0][i] = l_straryColNames[i];
		}
		
		// 行数据导出
		for (int i = 1; i < li_row; i++) {
			for (int j = 0; j < l_straryColNames.length; j++) {
				l_printdata[i][j] = getBillCardPanel().getBillModel()
				.getValueAt(i, j);
			}
		}
		// 合计行导出
		if (getBillCardPanel().getBillModel().getTotalTableModel() != null) {
			for (int j = 0; j < l_straryColNames.length; j++) {
				l_printdata[li_row][j] = getBillCardPanel().getBillModel()
				.getTotalTableModel().getValueAt(0, j);
			}
		}
		
		boolean flag = excel.createExcelFile(l_printdata);
		if(flag){
			MessageDialog.showHintDlg(this, "提示", "导出完成");
		}
	}
	/**
	 * v57 后，总额的相关报表都到人力资源预算里面去了 1 : 60391604_01 多类别薪资发放明细表 detailtable 2 :
	 * 60391604_02 多类别薪资发放汇总表 collecttable 3 : 60391604_07 员工工资台账 yggztz 打印
	 * 
	 * 创建日期：(2001-9-5 19:55:28)
	 */
	public void onPrint() {
		try {
			int iSel = getUIListSel().getSelectedIndex() + 1;
			if ((iSel == 1 || iSel == 2)
					&& getBillCardPanel().getBillModel().getRowCount() == 0) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000083")/*
						 * @res
						 * "当前没有需要打印的数据。"
						 */);
				return;
			} else if (iSel > 2 && (showVOBuf == null || showVOBuf.length < 1)) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000083")/*
						 * @res
						 * "当前没有需要打印的数据。"
						 */);
				return;
			}

			// 设置 标示,从而重新得到 是否打印0值 的参数值
			setGetFlag(false);

			if (iSel == 1) {
				// 得到列名
				// 固定的四列 人员名称，人员编码 所在岗位 身份证号码 代发银行 银行账号
				int preCol = 7;
				int col = getBillCardPanel().getBillTable().getColumnCount()
				- preCol;
				int row = getBillCardPanel().getBillTable().getRowCount();
				l_psnnames = new String[row];
				l_psncodes = new String[row];
				l_jobnames = new String[row];
				l_IDs = new String[row];
				l_bankname = new String[row];
				l_vacccode = new String[row];
				l_vperiods = new String[row];

				l_straryitemsColNames = new String[col];
				// l_strarywaclassColNames=new String[col];
				l_printitemvalues = new String[(row) * col];
				// for (int i=preCol; i < col+preCol; i++) {
				// l_strarywaclassColNames[i-preCol]=getBillCardPanel().getBillTable().getColumnName(i);
				// }

				for (int i = 0; i < itemSelectedVOs.length; i++) {
					l_straryitemsColNames[i] = itemSelectedVOs[i].getVname();
					;

				}

				// 组织表格数据

				for (int i = 0; i < row; i++) {
					for (int j = 0; j < col + preCol; j++) {
						if (j == 0) {
							l_psnnames[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else if (j == 1) {
							l_psncodes[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else if (j == 2) {
							l_jobnames[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else if (j == 3) {
							l_IDs[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else if (j == 4) {
							l_bankname[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else if (j == 5) {
							l_vacccode[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else if (j == 6) {
							l_vperiods[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else {
							Object obj = getBillCardPanel().getBillModel()
							.getValueAt(i, j);
							if (obj != null) {
								l_printitemvalues[i * col + (j - preCol)] = obj
								.toString();

							} else {
								l_printitemvalues[i * col + (j - preCol)] = "";
							}
						}
					}
				}

				// nc.ui.sm.cmenu.Desktop.getApplet().setIsUseHuabiao(true);
				nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(
						this, this);

				print.setTemplateID(strPk_corp, getModuleCode(), m_userId,
						null, "detailtable");
				if (print.selectTemplate() >= 0) {
					print.preview();
				}
				// bnPrint.setEnabled(true);
				// setButtons(ButtonGroupReport);
			} else if (iSel == 2) {

				int fix = 3;
				// 得到列名
				int col = getBillCardPanel().getBillTable().getColumnCount() - fix;
				int row = getBillCardPanel().getBillTable().getRowCount();
				l_psnnames = new String[row];
				l_psncodes = new String[row];
				l_vperiods = new String[row];
				l_straryitemsColNames = new String[col];
				l_strarywaclassColNames = new String[col];
				l_printitemvalues = new String[(row) * col];

				for (int i = fix; i < col + fix; i++) {
					l_strarywaclassColNames[i - fix] = getBillCardPanel()
					.getBillTable().getColumnName(i);
				}
				for (int i = 0; i < itemSelectedVOs.length; i++) {
					l_straryitemsColNames[i] = itemSelectedVOs[i].getVname();
				}
				// 组织表格数据
				for (int i = 0; i < row; i++) {
					for (int j = 0; j < col + fix; j++) {
						if (j == 0) {
							l_psnnames[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else if (j == 1) {
							l_psncodes[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						}else if (j==2){
							l_vperiods[i] = (String) getBillCardPanel()
							.getBillModel().getValueAt(i, j);
						} else {
							Object obj = getBillCardPanel().getBillModel()
							.getValueAt(i, j);
							if (obj != null) {
								l_printitemvalues[i * col + (j - fix)] = obj
								.toString();
							} else {
								l_printitemvalues[i * col + (j - fix)] = "";
							}
						}
					}
				}
				nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(
						this, this);
				l_deptnames = l_psnnames;
				l_deptcodes = l_psncodes;
				print.setTemplateID(strPk_corp, getModuleCode(), m_userId,
						null, "collecttable");
				if (print.selectTemplate() >= 0) {
					print.preview();
				}
			} else if (iSel > 2) {
				nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(
						this, this);

				print.setTemplateID(strPk_corp, getModuleCode(), m_userId,
						null, "yggztz");

				if (print.selectTemplate() >= 0) {
					print.preview();
				}
			}
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
			reportException(e);

		}
	}

	/**
	 * 打印
	 * 
	 * 创建日期：(2001-9-5 19:55:28)
	 */
	public void onPrintBak() {
		try {
			if (getBillCardPanel().getBillModel().getRowCount() == 0) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000083")/*
						 * @res
						 * "当前没有需要打印的数据。"
						 */);
				return;
			}

			// 得到列名
			String[] l_straryColNames = new String[getBillCardPanel()
			                                       .getBillTable().getColumnCount()];
			for (int i = 0; i < l_straryColNames.length; i++) {
				l_straryColNames[i] = getBillCardPanel().getBillTable()
				.getColumnName(i);
			}
			javax.swing.table.TableColumnModel l_temp = getBillCardPanel()
			.getBillTable().getColumnModel();

			// 构造符合条件的打印列名二维数组
			String[][] l_straryPrintColNames = new String[2][l_straryColNames.length];
			l_straryPrintColNames[0][0] = l_straryColNames[0]; // 首行首格写入“人员名称”或“部门名称”
			l_straryPrintColNames[0][1] = l_straryColNames[1]; // 首行二格写入“人员编码”或“部门编码”
			for (int i = 2; i < l_straryColNames.length; i++) { // 次行从第三格起，写入对应薪资类别名或折本合计
				l_straryPrintColNames[1][i] = l_straryColNames[i];
			}
			for (int i = 0; i < itemSelectedVOs.length; i++) { // 首行从第三格起，写入对应的薪资项目名称
				for (int j = 0; j < waclassSelectedVOs.length + 1; j++) {

					l_straryPrintColNames[0][j + 2 + i
					                         * (waclassSelectedVOs.length + 1)] = itemSelectedVOs[i]
					                                                                              .getVname();
				}
			}

			// 设置列宽和每列的对齐方式
			int[] l_aryintColWidth = new int[l_straryColNames.length]; // 列宽
			int[] l_aryintColAlignflag = new int[l_straryColNames.length]; // 表格每列的对齐方式

			for (int i = 0; i < l_straryColNames.length; i++) { // 应改为可调，此处固定
				l_aryintColWidth[i] = 120;
				if (i == 0 || i == 1) {
					l_aryintColAlignflag[i] = 0; // 中
				} else {
					l_aryintColAlignflag[i] = 2; // 右
				}
			}

			// 组织表格数据
			int li_row = getBillCardPanel().getBillModel().getRowCount();
			Object[][] l_printdata = new Object[li_row + 1][l_straryColNames.length];

			for (int i = 0; i < li_row; i++) {
				for (int j = 0; j < l_straryColNames.length; j++) {
					l_printdata[i][j] = getBillCardPanel().getBillModel()
					.getValueAt(i, j);
				}
			}
			if (getBillCardPanel().getBillModel().getTotalTableModel() != null) {
				for (int j = 0; j < l_straryColNames.length; j++) {
					l_printdata[li_row][j] = getBillCardPanel().getBillModel()
					.getTotalTableModel().getValueAt(0, j);
				}
			}
			// String title = getTitle();
			java.awt.Font font = new java.awt.Font("dialog",
					java.awt.Font.BOLD, 30);
			java.awt.Font font1 = new java.awt.Font("dialog",
					java.awt.Font.PLAIN, 12);
			String topstr = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPT60131604-000004")/* @res "薪资期间：" */
					+ strCurYear + "-" + strCurPeriod;
			// + " 查询条件：" + strWheredisplay; //???????待加
			// yxl update
			// String botstr =
			// "制表人："
			// + getClientEnvironment().getUser().getUserName()
			// + " 制表日期："
			// + getClientEnvironment().getDate();
			String botstr = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000085")/* @res "制表人：" */
					+ getBillCardPanel().getTailItem(m_sTailItemLister)
					.getValue()
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
					"UPP60131604-000132")/* @res " 审核人：" */
					+ getBillCardPanel().getTailItem(m_sTailItemassessor)
					.getValue()
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
					"UPP60131604-000133")/* @res " 制表日期：" */
					+ getClientEnvironment().getDate();

			// 多表头部分start――合并单元格范围
			nc.ui.pub.print.datastruct.CellRange[] l_aryCellRange = new nc.ui.pub.print.datastruct.CellRange[itemSelectedVOs.length + 2];
			l_aryCellRange[0] = new nc.ui.pub.print.datastruct.CellRange(0, 0,
					1, 0);
			// “人员名称”或“部门名称”
			l_aryCellRange[1] = new nc.ui.pub.print.datastruct.CellRange(0, 1,
					1, 1);
			// “人员编码”或“部门编码”

			for (int i = 2; i < itemSelectedVOs.length + 2; i++) {
				l_aryCellRange[i] = new nc.ui.pub.print.datastruct.CellRange(0,
						((i - 2) * (waclassSelectedVOs.length + 1)) + 2, 0,
						((i - 2) * (waclassSelectedVOs.length + 1))
						+ waclassSelectedVOs.length + 2);
			}
			// 多表头部分end--合并单元格范围

			nc.ui.pub.print.PrintDirectEntry print = new nc.ui.pub.print.PrintDirectEntry();
			print.setTitle(title); // 标题 可选
			print.setTitleFont(font); // 标题字体 可选
			print.setContentFont(font1); // 内容字体（表头、表格、表尾） 可选
			print.setTopStr(topstr); // 表头信息 可选
			print.setBottomStr(botstr); // 表尾信息 可选
			print.setColNames(l_straryPrintColNames); // 表格列名（二维数组形式）
			print.setCombinCellRange(l_aryCellRange);// 设置多表头
			print.setColWidth(l_aryintColWidth); // 表格列宽 可选
			print.setAlignFlag(l_aryintColAlignflag); // 表格每列的对齐方式（0-左, 1-中,
			// 2-右）可选
			print.setData(l_printdata); // 表格数据

			//
			print.preview(); // 预览
			//
			bnPrint.setEnabled(true);
			bnExport.setEnabled(true);
			
			setButtons(ButtonGroupReport);

		} catch (Exception e) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60131604", "UPP60131604-000084")/* @res "系统出现异常，操作失败！" */);
			reportException(e);

		}

	}

	/**
	 * 设置按钮状态
	 * 
	 * 创建日期：(2001-8-13 16:03:16)
	 */
	public void setBnState() {
		if (bnOk.isEnabled()) {
			bnOk.setEnabled(false);
			bnCancel.setEnabled(true);
			bnPrint.setEnabled(true);
			bnExport.setEnabled(true);
		} else {
			bnOk.setEnabled(true);
			bnCancel.setEnabled(false);
			bnPrint.setEnabled(false);
			bnExport.setEnabled(false);
		}
		setButtons(ButtonGroupReport);
	}

	/**
	 * 设置按钮状态
	 * 
	 * 创建日期：(2001-8-13 16:03:16)
	 */
	public void setBnState(boolean ok, boolean cancel, boolean print) {
		bnOk.setEnabled(ok);
		bnCancel.setEnabled(cancel);
		bnPrint.setEnabled(print);
		bnExport.setEnabled(print);
	}

	/**
	 * 设置汇总表数据
	 * 
	 * 创建日期：(2001-9-10 11:08:42)
	 */
	public void setData_collect(List<WaCurrPeriodVO> periodList)
	throws Exception {

		StringBuffer sb = new StringBuffer();
		// 折本合计临时数组
		String[] totalNames = new String[itemSelectedVOs.length];

		for (int i = 0; i < itemSelectedVOs.length; i++) {
			totalNames[i] = "SUM(WA_DATAZ.F_"
				+ itemSelectedVOs[i].getIitemid().intValue() + ")";
		}

		List<WaMultiReportVO02> list = new ArrayList<WaMultiReportVO02>();
		ArrayList<String> list_deptid = new ArrayList<String>();
		for (WaCurrPeriodVO pervo : periodList) {
			String ym = pervo.getVyear() + pervo.getVmonth();
			sb.append(pervo.getVyear() + "-"+ pervo.getVmonth() + ", ");
			try {
				vo002s = getConform002VO(pervo.getVyear(), pervo.getVmonth());

				for(WaMultiReportVO02 tmpVO : vo002s){

					tmpVO.setVperiod(ym);

					list.add(tmpVO);
					if(tmpVO.getDeptid()!=null&&tmpVO.getDeptid().trim().length()>0&&!list_deptid.contains(tmpVO.getDeptid())){
						list_deptid.add(tmpVO.getDeptid());
					}
				}
			} catch (Exception e) {
			}
		}
		WaMultiReportVO02[] vos = list.toArray(new WaMultiReportVO02[0]);
		VOUtil.ascSort(vos, new String[] { "deptcode","vperiod" });
		getBillCardPanel().getBillModel().setBodyDataVO(vos);
		// 取消行号
		getBillCardPanel().setRowNOShow(false);
		String periodStr = sb.toString().substring(0, sb.length() - 2);
		// 表头
		getBillCardPanel().setHeadItem("cperiod", periodStr);
		// getBillCardPanel().setHeadItem("","");
		// 表尾
		// yxl update
		getBillCardPanel().setTailItem(m_sTailItemassessor, "");
		getBillCardPanel().setTailItem(m_sTailItemLister, "");

		if (getBillCardPanel().getBillModel().getTotalTableModel() != null) {
			getBillCardPanel()
			.getBillModel()
			.getTotalTableModel()
			.setValueAt(
					nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"60131604", "UPP60131604-000134")/*
							 * @res
							 * "合计（共"
							 */
							+ list_deptid.size()
							+ nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("60131604",
							"UPP60131604-000135")/*
							 * @res
							 * "部门）"
							 */,
							 0, 0);
		}
	}

	/**
	 * 设置明细表数据
	 * 
	 * 创建日期：(2001-9-5 19:58:21)
	 */

	public void setData_list(List<WaCurrPeriodVO> periodList) throws Exception {
		// 折本合计临时数组
		StringBuffer sb = new StringBuffer();
		List<WaMultiReportVO01> list = new ArrayList<WaMultiReportVO01>();
		// 人数
//		int npsnnum = 0;
		ArrayList<String> psnid_list = new ArrayList<String>();
		for (WaCurrPeriodVO pervo : periodList) {
			String ym = pervo.getVyear() + pervo.getVmonth();
			sb.append(pervo.getVyear() + "-" + pervo.getVmonth() + ", ");
			vo001s = getConform001VO(pervo.getVyear(), pervo.getVmonth());
			for (WaMultiReportVO01 tmpVO : vo001s) {
				tmpVO.setVperiod(ym);
				list.add(tmpVO);
				if(tmpVO.getPsnid()!=null&&tmpVO.getPsnid().trim().length()>0&&!psnid_list.contains(tmpVO.getPsnid())){
					psnid_list.add(tmpVO.getPsnid());
				}
//				npsnnum = vo001s.length;
			}
		}
		WaMultiReportVO01[] vos = list.toArray(new WaMultiReportVO01[0]);
		VOUtil.ascSort(vos, new String[] {  "psncode","vperiod" });

		if (vos != null && vos.length > 0) {
			int intMaxRow = 1000;
			if (getUISpinBoxManRow().getValue() == 0) {
				intMaxRow = 1000;
			} else {
				intMaxRow = getUISpinBoxManRow().getValue();
			}

			// 设置表体数据
			getBillCardPanel().getBillModel().setBodyDataVO(vos);
		}

		// 取消行号
		getBillCardPanel().setRowNOShow(false);
		String strPeriods = sb.toString().substring(0, sb.length() - 2);
		// 表头
		getBillCardPanel().setHeadItem("cperiod", strPeriods);
		getBillCardPanel().setTailItem(m_sTailItemassessor, "");
		getBillCardPanel().setTailItem(m_sTailItemLister, "");

		if (getBillCardPanel().getBillModel().getTotalTableModel() != null) {
			getBillCardPanel()
			.getBillModel()
			.getTotalTableModel()
			.setValueAt(
					nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"60131604", "UPP60131604-000134")/*
							 * @res
							 * "合计（共"
							 */
							+ psnid_list.size()/* vo001s.length */
							+ nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("60131604",
							"UPP60131604-000136")/*
							 * @res
							 * "人）"
							 */,
							 0, 0);
		}
	}

	/**
	 * @return 返回 gzpanl。
	 */
	private ReportBaseClass getGzpanl(int nIdx) {
		if (nGzIdx != nIdx) {
			nGzIdx = nIdx;
			if (gzpanl == null) {
				gzpanl = new ReportBaseClass();
				gzpanl
				.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000137")/*
						 * @res
						 * "单位工资总额台账模板"
						 */);
			}
			//
			try {
				// if( nIdx == 7 ){
				if (nIdx == 3) {
					gzpanl.setTempletID(Global.getCorpPK(), getModuleCode(),
							Global.getUserID(), null);
				} else {
					gzpanl.setTempletID("0001AA100000000038BR");
					BillData billdata = gzpanl.getBillData();
					String tablecode = "main";
					if (billdata.getTableCodes(0).length > 0) {
						tablecode = billdata.getTableCodes(0)[0];
					}
					billdata.removeBillItem(0, tablecode, "hdeptname");
					billdata.removeBillItem(0, tablecode, "hpsnname");
					gzpanl.setBillData(billdata);

				}
				gzpanl.setTatolRowShow(true);
				gzpanl.getBodyPanel().getTable().setSortEnabled(false);

			} catch (Exception e) {
				reportException(e);
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000138")/*
						 * @res
						 * "没有找到到默认的账表模板！"
						 */);
			}
		}
		return gzpanl;
	}

	/**
	 * @return 返回 datas。
	 */
	private CircHashVO[] getShowData(int nIdx) {
		if (showVOBuf == null || showVOBuf.length < 1) {
			return null;
		}

		BillItem[] items = getGzpanl(nIdx).getBodyItems();

		int nVOLen = showVOBuf.length;
		for (int j = 0; j < nVOLen; j++) {
			for (BillItem item : items) {
				if (item.getDataType() == BillItem.STRING) {
					showVOBuf[j].setAttribute(item.getKey(), getGzpanl(nIdx)
							.getBodyValueAt(j, item.getKey()), IDataType.CHAR);
				} else {
					showVOBuf[j].setAttribute(item.getKey(), getGzpanl(nIdx)
							.getBodyValueAt(j, item.getKey()),
							IDataType.NUMERIC);
				}
			}
		}

		return showVOBuf;
	}

	private void showZEPanel(Object objSort) {
		showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604",
		"UPP60131604-000139")/* @res "正在显示台账..." */);

		// 分配总额
		try {
			int nIdx = getUIListSel().getSelectedIndex() + 1;
			getGzpanl(nIdx).getBillData().clearViewData();
			// add by guoad
			preTempletCol(nIdx);
			ItemVO[] itemvos = null;

			GeneralVO[] zeData = null;
			IPayTotalInfo service = (IPayTotalInfo) NCLocator.getInstance()
			.lookup(IPayTotalInfo.class.getName());

			// if( nIdx == 3 || nIdx == 4 ){
			// getGzpanl(nIdx).hideColumn("deptname");
			// getGzpanl(nIdx).showHiddenColumn("vname");
			//
			// getGzpanl(nIdx).showHiddenColumn("corpname");
			//
			// if( nIdx == 3 ){
			// zeData =
			// service.queryPracMoney(Global.getCorpPK(),null,strCurYear,false);
			// }
			// else{
			// getGzpanl(nIdx).setTatolRowShow(false);
			// zeData =
			// service.queryPracMoney(Global.getCorpPK(),null,strCurYear,true);
			// }
			// }
			// else if( nIdx == 5 || nIdx == 6 ){
			// getGzpanl(nIdx).hideColumn("corpname");
			// getGzpanl(nIdx).showHiddenColumn("vname");
			// getGzpanl(nIdx).showHiddenColumn("deptname");
			// getGzpanl(nIdx).showHiddenColumn("zesort");
			//
			// if( getDeptInfoDlg().showModal() != UIDialog.ID_OK ){
			// return;
			// }
			// Vector deptv = getDeptInfoDlg().getSelectedDeptVect();
			// boolean containChild = getDeptInfoDlg().isContainNextDept();
			//
			// DeptVO[] deptvos = (DeptVO[])deptv.toArray(new DeptVO[0]);
			//
			// for (DeptVO deptVO : deptvos) {
			// deptVO.setIsincludechild(containChild);
			// }
			// ArrayList<CircHashVO> array = new ArrayList<CircHashVO>();
			// if (nIdx == 5) {
			// getGzpanl(nIdx).setTatolRowShow(false);
			//
			// zeData = service.queryPracMoney(Global.getCorpPK(), deptvos,
			// strCurYear, false);
			//
			// }
			// else{
			// getGzpanl(nIdx).setTatolRowShow(false);
			// zeData = service.queryPracMoney(Global.getCorpPK(), deptvos,
			// strCurYear, true);
			// }
			//
			// }
			// else{
			if (nIdx == 3) {
				getGzpanl(nIdx).setTatolRowShow(true);
				getGzpanl(nIdx).hideColumn("corpname");
				getGzpanl(nIdx).hideColumn("deptname");
				getGzpanl(nIdx).hideColumn("zesort");
				getGzpanl(nIdx).showHiddenColumn("vname");

				getGzpanl(nIdx).getHeadItem("hdeptname").setValue(
						getrefPerson().getRefValue("bd_deptdoc.deptname"));
				getGzpanl(nIdx).getHeadItem("hpsnname").setValue(
						getrefPerson().getRefValue("bd_psnbasdoc.psnname"));

				if (getItemInfoDlg().showModal() == UIDialog.ID_OK) {
					itemvos = getItemInfoDlg().getSelectedVo();
					zeData = WADelegator.getWaWaMultiReport()
					.queryStuffReport(
							(String) getrefPerson().getRefValue(
							"bd_psndoc.pk_deptdoc"),
							(String) getrefPerson().getRefValue(
							"bd_psndoc.pk_psndoc"), strCurYear,
							itemvos);
				} else {
					return;
				}
			}
			// }

			remove(getUIPanel());
			add(getGzpanl(nIdx), "Center");

			getGzpanl(nIdx).getHeadItem("theyear").setValue(strCurYear);
			if (zeData != null && zeData.length > 0) {
				zeData = getYearAvg(zeData);
				getGzpanl(nIdx).setBodyDataVO(zeData);
				showVOBuf = new CircHashVO[zeData.length];
				for (int i = 0; i < zeData.length; i++) {
					showVOBuf[i] = new CircHashVO();

				}
				showVOBuf = getShowData(nIdx);

				int nSize = showVOBuf.length;
				String[] names = showVOBuf[0].getAttributeNames();
				int nNameLen = names.length;

				for (int i = 0; i < nNameLen; i++) {
					if (showVOBuf[0].getAttributeType(names[i]) == IDataType.NUMERIC) {
						UFDouble sumdata = null;
						for (int j = 0; j < nSize; j++) {
							Object tmpobj = showVOBuf[j]
							                          .getAttributeValue(names[i]);
							if (tmpobj != null) {
								UFDouble tmpUfdoubl = (UFDouble) tmpobj;
								// if (nIdx == 7) {
								if (nIdx == 3) {
									int symbol = getItemSymbol(showVOBuf[j]
									                                     .getAttributeValue("pk_wa_item"),
									                                     itemvos);
									tmpUfdoubl = tmpUfdoubl.multiply(symbol);
								}
								if (sumdata == null) {
									sumdata = new UFDouble(0);
								}
								sumdata = sumdata.add(tmpUfdoubl);
							}
						}
						if (sumdata != null) {
							sumdata = sumdata.setScale(2,
									UFDouble.ROUND_HALF_UP);
						}
						getGzpanl(nIdx).getTotalTableModel().setValueAt(
								sumdata, 0,
								getGzpanl(nIdx).getBodyColByKey(names[i]));
					}
				}
				/*
				 * getGzpanl(nIdx).getTotalTableModel().setValueAt(nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0001146")@res
				 * "合计", 0, getGzpanl(nIdx).getBodyColByKey("vname"));
				 */
			}

			setBnState();

			updateUI();
			setTitleText(MULREPORTKIND[nIdx - 1]);
			showHintMessage(MULREPORTKIND[nIdx - 1]);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BusinessException) {
				showErrorMessage(e.getMessage());

			} else {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000140")/*
						 * "数据查询出错!"
						 */);

			}
			return;
		}

	}

	/**
	 * 初始化部门查询条件
	 * 
	 * 创建日期：(2001-8-13 16:03:02)
	 */
	private DeptInfoVOSelDlgUI getDeptInfoDlg() throws Exception {
		if (deptInfoDlg == null) {
			deptInfoDlg = new DeptInfoVOSelDlgUI(this, 3);
		}
		return deptInfoDlg;
	}

	/**
	 * 函数的功能、用途、对属性的更改，以及函数执行前后对象的状态。
	 * 
	 * @param 参数说明
	 * @return 返回值
	 * @exception 异常描述
	 * @see 需要参见的其它内容
	 * @since 从类的那一个版本，此方法被添加进来。（可选）
	 * @return nc.ui.wa.wa_020.ItemInfoVOSelDlgUI
	 */
	public ItemInfoTRSelDlgForMultiReportUI getItemInfoDlg() {
		if (itemInfoDlg == null) {

			WaGlobalVO waGlobalVO = new WaGlobalVO();
			waGlobalVO.setCurPk_corp(Global.getCorpPK());
			waGlobalVO.setCurUserid(Global.getUserID());
			List<WaGlobalVO> list = new ArrayList<WaGlobalVO>();
			list.add(waGlobalVO);
			itemInfoDlg = new ItemInfoTRSelDlgForMultiReportUI(this, 1, list
					.toArray(new WaGlobalVO[0]));
		}

		return itemInfoDlg;
	}

	public void preTempletCol(int nIdx) {

		int startNum = 1;
		int endNum = 20;
		String year = getUITfWaYear().getText().trim();
		String[] period = (String[]) getAccPeriods().get(year);
		if (period != null && period.length > 0) {
			if (period[0].startsWith("0")) {
				startNum = (new Integer(period[0].substring(1))).intValue();
			} else {
				startNum = (new Integer(period[0])).intValue();
			}
			endNum = startNum + period.length - 1;
		}
		for (int i = 1; i < 21; i++) {
			if (i < startNum || i > endNum) {
				getGzpanl(nIdx).hideColumn("m" + i);
			}
		}
	}

	/**
	 * 得到报表类型的选择列表 修改历史:<Strong>xuanlt 2007 六月 26<Strong>
	 * 
	 * @return
	 * @see
	 */
	private nc.ui.pub.beans.UIList getUIListSel() {
		if (listForSelItems == null) {
			try {
				listForSelItems = new nc.ui.pub.beans.UIList(MULREPORTKIND);
				listForSelItems.setName("UIListSel");
				listForSelItems.setBounds(159, 135, 321, 135);
				// user code begin {1}
				listForSelItems.setTranslate(true);

				listForSelItems
				.addListSelectionListener(new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent e) {
						if (e.getSource() == getUIListSel()) {
							getUILblPeriod().setVisible(true);
							getUITfWaPeriod().setVisible(true);
							getUILblPsn().setVisible(false);
							getrefPerson().setVisible(false);
							if (getUIListSel().getSelectedIndex() == 0) {
								// 多类别薪资发放明细表
								getUILabelMaxRow().setVisible(true);
								getUISpinBoxManRow().setVisible(true);

								getLStartPeriod().setVisible(true);
								getStartPeriod().setVisible(true);
								getLEndPeriod().setVisible(true);
								getEndPeriod().setVisible(true);
								getUILblYear().setVisible(false);
								getUITfWaYear().setVisible(false);
							} else if (getUIListSel()
									.getSelectedIndex() == 1) {
								getUILabelMaxRow().setVisible(false);
								getUISpinBoxManRow().setVisible(false);
								getLStartPeriod().setVisible(true);
								getStartPeriod().setVisible(true);
								getLEndPeriod().setVisible(true);
								getEndPeriod().setVisible(true);
								getUILblYear().setVisible(false);
								getUITfWaYear().setVisible(false);
							} else if (getUIListSel()
									.getSelectedIndex() == 2) { // .getSelectedIndex()
								// == 6
								// 员工收入台账
								getUILabelMaxRow().setVisible(false);
								getUISpinBoxManRow().setVisible(false);
								getUILblPsn().setVisible(true);
								getrefPerson().setVisible(true);
								getLStartPeriod().setVisible(false);
								getStartPeriod().setVisible(false);
								getLEndPeriod().setVisible(false);
								getEndPeriod().setVisible(false);
								getUILblYear().setVisible(true);
								getUITfWaYear().setVisible(true);
							}
						}
					}
				});

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return listForSelItems;
	}

	/**
	 * 
	 * 修改历史:<Strong>xuanlt 2007 六月 26<Strong>
	 * 
	 * @return
	 * @see
	 */
	public UIScrollPane getSclPane() {
		if (sclPane == null) {
			try {
				sclPane = new UIScrollPane();
				sclPane.setBounds(159, 135, 330, 142);
				sclPane.setName("sclPane");
				sclPane.setViewportView(getUIListSel());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return sclPane;
	}

	/**
	 * 
	 * 修改历史:<Strong>xuanlt 2007 六月 26<Strong>
	 * 
	 * @return
	 * @see
	 */
	public UILabel getLableForSel() {
		if (lableForSel == null) {
			try {
				lableForSel = new UILabel();
				lableForSel.setBounds(159, 109, 200, 20);
				lableForSel.setName("sclPane");
				lableForSel
				.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"60131604", "UPP60131604-000056")/*
						 * @res
						 * "请选择要查看的报表 "
						 */);
				lableForSel.setILabelType(UILabel.STYLE_NOTNULL);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return lableForSel;
	}

	public boolean isGetFlag() {
		return getFlag;
	}

	public void setGetFlag(boolean getFlag) {
		this.getFlag = getFlag;
	}

	public JComponent createFieldValueEditor(FilterMeta filterMeta) {
		if (filterMeta.getFieldCode().equalsIgnoreCase("wa_data.psnclid")) {
			UIRefPane rfPsnclForSel = new nc.ui.pub.beans.UIRefPane();
			rfPsnclForSel.setName("rfPsncl");
			rfPsnclForSel.setText("");
			rfPsnclForSel.setBounds(502, 66, 135, 22);
			rfPsnclForSel.setRefNodeName("人员类别");
			rfPsnclForSel.setReturnCode(false);
			rfPsnclForSel.setRefInputType(1/** 名称 */
			);
			rfPsnclForSel.setRefType(IRefConst.GRID);
			rfPsnclForSel.setIsCustomDefined(true);
			String st = "pk_corp='" + Global.getWaCorpPk() + "' or pk_corp= '"
			+ CommonConstant.GROUP_CODE + "' or pk_corp is null";
			rfPsnclForSel.getRefModel().setWherePart(st);
			rfPsnclForSel.setButtonFireEvent(true);
			return rfPsnclForSel;
		}

		try {
			return new HiInfoForQuery().getWaBmQueryValueRef(filterMeta
					.getFieldCode());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage(), e);
		}

		return null;
	}

	public String getRefPanelWherePart(FilterMeta filterMeta) {
		return null;
	}

}
