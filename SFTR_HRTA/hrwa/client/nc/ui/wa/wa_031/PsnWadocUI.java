package nc.ui.wa.wa_031;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.bd.ISetdict;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.bd.b04.DeptdocBO_Client;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hi.ref.JobRef;
import nc.ui.hr.base.HRToftPanel;
import nc.ui.hr.comp.quicksearch.IQuickSearch;
import nc.ui.hr.comp.quicksearch.QsbUtil;
import nc.ui.hr.comp.quicksearch.SearchType;
import nc.ui.hr.frame.HrQueryDialog;
import nc.ui.hr.frame.IQueryFieldValueEditor;
import nc.ui.hr.global.Global;
import nc.ui.hr.global.HiInfoForQuery;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.querytemplate.IQueryTemplateTotalVOProcessor;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.wa.pub.ButtonTipMessage;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wa.wa_031.adjust.BatchAdjustAction;
import nc.ui.wa.wa_031.xh.BfbkSetDialog;
import nc.ui.wa.wa_031.xh.PsnViewDialog;
import nc.ui.wa.wa_031.xh.XhPsnWadocSubPane;
import nc.ui.wabm.global.QueryConditionDlgBuilder;
import nc.ui.wabm.global.QueryConditionDlgUtils;
import nc.vo.bd.b04.DeptdocVO;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.hi.hi_rpt.WaItemVO;
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hi.wadoc.PsndocWadocb2VO;
import nc.vo.hrp.billsbtn.OkBtn;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.print.PrintRegionVO;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.wa.wa_026.AdjustWadocVO;
import nc.vo.wa.wa_026.BatchAdjustVO;

/**
 * 薪资档案子集，因为单据模板中处理不了主子表结构，因此单独取出一个节点。
 * 创建日期：(2004-6-3 14:49:40)
 * @author：Administrator
 */
/**
 * @author zhangg
 * 
 */
public class PsnWadocUI extends nc.ui.hr.base.HRToftPanel implements
BillEditListener, IQueryFieldValueEditor, IQuickSearch, ActionListener {

	private static final long serialVersionUID = -8980209236578752079L;
	private nc.ui.pub.beans.UIPanel ivjUIRightPnl = null;
	private nc.ui.pub.beans.UISplitPane ivjUIHSplitPane = null;
	// 按纽定义o

	private final ButtonObject m_bnQuery = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000143")/*
			 * @res
			 * "查询"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000143")/*
			  * @res "查询"
			  */, 0, "查询"); /*-=notranslate=-*/
	private final ButtonObject m_bnRefresh = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			"UPPT60130715-000144")/*
			 * @res "刷新"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000144")/*
			  * @res "刷新"
			  */, 0, "刷新"); /*-=notranslate=-*/
	private final ButtonObject m_boPrintGroup = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			"UPPT60130715-000145")/*
			 * @res "打印"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000145")/*
			  * @res "打印"
			  */, 0, "打印"); /*-=notranslate=-*/

//	private final ButtonObject m_boPrint = new ButtonObject(NCLangRes
//			.getInstance().getStrByID("60131600", "UPT60131600-000052")/*
//			 * @res
//			 * "模板打印"
//			 */, NCLangRes.getInstance().getStrByID("60131600", "UPT60131600-000052")/*
//			 * @res
//			 * "模板打印"
//			 */, 0, "模板打印"); /*-=notranslate=-*/
//	private final ButtonObject m_boDirectPrint = new ButtonObject(NCLangRes
//			.getInstance().getStrByID("60131600", "UPT60131600-000053")/*
//			 * @res
//			 * "直接打印"
//			 */, NCLangRes.getInstance().getStrByID("60131600", "UPT60131600-000053")/*
//			 * @res
//			 * "直接打印"
//			 */, 0, "直接打印"); /*-=notranslate=-*/
	
	private final ButtonObject m_boNomalPrint = new ButtonObject("正常晋升", "正常晋升", 0, "正常晋升");
	private final ButtonObject m_boGwbdPrint = new ButtonObject("岗位变动", "岗位变动", 0, "岗位变动");
	private final ButtonObject m_boNewLyPrint = new ButtonObject("新录用", "新录用", 0, "新录用");
	private final ButtonObject m_boOtherPrint = new ButtonObject("其它", "其它", 0, "其它");
	
	private final ButtonObject m_boRemark = new ButtonObject("备注", "备注", 0, "备注");// zhanghua
	//private final ButtonObject m_boTempSave = new ButtonObject("暂存", "暂存", 0, "暂存");// zhanghua
	
	private final ButtonObject m_btnWXYJ = new ButtonObject("五险一金", "五险一金", 0, "五险一金");// lizhuchao

	private final ButtonObject m_bnSave = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000147")/*
			 * @res
			 * "保存"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000147")/*
			  * @res "保存"
			  */, 0, "保存"); /*-=notranslate=-*/
	private final ButtonObject m_bnCancel = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000148")/*
			 * @res
			 * "取消"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000148")/*
			  * @res "取消"
			  */, 0, "取消"); /*-=notranslate=-*/
	private final ButtonObject m_bnInsert = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000149")/*
			 * @res
			 * "插入"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000149")/*
			  * @res "插入"
			  */, 0, "插入"); /*-=notranslate=-*/
	private final ButtonObject m_bnModify = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000150")/*
			 * @res
			 * "修改"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000150")/*
			  * @res "修改"
			  */, 0, "修改"); /*-=notranslate=-*/
	private final ButtonObject m_bnAdd = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000151")/*
			 * @res
			 * "增加"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000151")/*
			  * @res "增加"
			  */, 0, "增加"); /*-=notranslate=-*/
	
	private final ButtonObject m_bnTempAdd = new ButtonObject("增加暂存"
			, "增加暂存"
			, 0, "增加暂存"); /*-=notranslate=-*/
	
	private final ButtonObject m_bnDelete = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000152")/*
			 * @res
			 * "删除"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000152")/*
			  * @res "删除"
			  */, 0, "删除"); /*-=notranslate=-*/

	private final ButtonObject m_bnImport = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000011")/*"导入"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000011")/*"导入"*/, 0,
	"导入"); /*-=notranslate=-*/
	private final ButtonObject m_bnExport = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000056")/*"导出"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000056")/*"导出"*/, 0,
	"导出"); /*-=notranslate=-*/
	private final ButtonObject m_bnImportData = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000012")/*"导入数据"*/,
			nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000012")/*"导入数据"*/, 0, "导入数据"); /*-=notranslate=-*/
	private final ButtonObject m_bnExportData = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000013")/*"导出数据"*/,
			nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000013")/*"导出数据"*/, 0, "导出数据"); /*-=notranslate=-*/
	// private final ButtonObject m_bnDispaly = new
	// ButtonObject("显示","显示",0,"显示"); /*-=notranslate=-*/
	private final ButtonObject m_bnReturn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000038")/*"返回"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000038")/*"返回"*/, 0,
	"返回"); /*-=notranslate=-*/

	private final ButtonObject m_bnAdjust = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000010")/*"薪资普调"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000010")/*"薪资普调"*/, 0,
	"薪资普调"); /*-=notranslate=-*/

	//add by xhhrp suhf1 begin 定调资维护记录查询 Maintenance record 
	private final ButtonObject m_bnQuery_MaintenRecord =  new ButtonObject("维护记录","维护记录", 0, "维护记录");
//	zhf 薪资维护
	private final ButtonObject m_btnBfbk =  new ButtonObject("薪资维护","薪资维护", 0, "薪资维护");

//	zhf 人员信息查看
	private final ButtonObject m_btnPsn =  new ButtonObject("人员信息查看","人员信息查看", 0, "人员信息查看");

//	zhf 人员信息查看
	private final ButtonObject m_btnUpdateGjj =  new ButtonObject("公积金及房贴同步","公积金及房贴同步", 0, "公积金及房贴同步");
	
	private final ButtonObject m_btnUpdateFF =  new ButtonObject("发放标志批改","发放标志批改", 0, "发放标志批改");

	private final ButtonObject[] bnGroup = { m_bnAdd, m_bnTempAdd,m_bnInsert, m_bnModify,
			m_bnDelete, m_bnAdjust, m_bnSave, m_bnCancel, m_bnImport,
			m_bnExport, m_bnQuery, m_bnRefresh, m_boPrintGroup, m_bnImportData,
			m_bnExportData, m_bnReturn,m_bnQuery_MaintenRecord,m_btnBfbk,m_btnPsn,m_btnUpdateGjj,m_btnUpdateFF,m_boRemark,m_btnWXYJ};
	private HrQueryDialog queryDlg = null;
	private nc.vo.hi.pub.FldreftypeVO[] fldreftypeVOs = null;
	private String deptPower = "0=0";
	private PsnWadocMainPane ivjPsnWadocMainPane = null;
	private PsnWadocSubPane ivjPsnWadocSubPane = null;
	protected int UI_MAIN = 0;
	protected int UI_MGR_SELF = 1;
	private int currentSelectRow = 0;
	// add by sunxj 2010-02-23 快速查询插件 start
	protected boolean isQuery = false;// 标记是否查询过
	protected boolean isQuickSearch = false;// 标记是否快速查询过
	protected String quickWherePart = null;
	// add by sunxj 2010-02-23 快速查询插件 end

	protected UIPanel topPanel = null;

	protected UIPanel ivjTopPanel = null;
	private nc.ui.pub.beans.UILabel ivjlblFileName = null;
	private nc.ui.pub.beans.UITextField ivjtfFileName = null;
	private nc.ui.pub.beans.UIButton ivjUIBnBrowser = null;
	private UIFileChooser ivjUIFileChooser = null;

	protected UIPanel ivjTopPanelim = null;
	private nc.ui.pub.beans.UILabel ivjlblFileNameim = null;
	private nc.ui.pub.beans.UITextField ivjtfFileNameim = null;
	private nc.ui.pub.beans.UIButton ivjUIBnBrowserim = null;
	private UIFileChooser ivjUIFileChooserim = null;

	//add by xhhrp suhf1
	private UIPanel maintenRecordPanel = null;
	private nc.ui.pub.beans.UIRadioButton allRecordsRB = null;
	private nc.ui.pub.beans.UIRadioButton latestRecordsRB = null;
	private nc.ui.pub.beans.UIRadioButton certainRecordRB = null;
	private UIRefPane wa_itemRF = null;
	private UIDialog dialog_wa = null;
	private UIButton confiromB = null;
	private UIButton cancleB = null;
	//add by xhhrp suhf1 end

	// 导出状态
	public final static int UI_STAT_EXPOTRT = 10;

	public final static int UI_STAT_IMPOTRT = 11;

	private List<String> insertlist;

	private int currentstat = -1;

	private String querywhere = "";// 查询对话框条件
	/**
	 * PsnWadocUI 构造子注解。
	 */
	public PsnWadocUI() {
		super();
		initialize();
	}

	/**
	 * 添加监听接口。 创建日期：(2004-6-4 16:49:27)
	 */
	public void addListener() {

	}

	/**
	 * 用于在开发环境获得节点编号。 创建日期：(2003-2-27 12:28:41)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getModuleCodeWithDebug() {
		return "60130715";
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
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
		"UPP60130715-000002")/*
		 * @res "员工薪资变动情况"
		 */;
	}

	/**
	 * 定调资维护记录查询 dialog
	 * @author suhf1
	 */
	private UIDialog getUIdialog(){
		if(dialog_wa == null){
			dialog_wa = new UIDialog(this,"定调资维护记录界面");
			dialog_wa.setContentPane(getMaintenRecord());
			dialog_wa.setSize(400, 260);
		}
		return dialog_wa;
	}
	/**
	 * 定调资维护记录查询 panel
	 * @author suhf1
	 */
	private UIPanel getMaintenRecord(){
		if(maintenRecordPanel == null){
			maintenRecordPanel = new UIPanel();
			SpringLayout layout = new SpringLayout();
			maintenRecordPanel.setLayout(layout);
			ButtonGroup bg = new ButtonGroup();
			bg.add(getUIRadioAllRecordsRB());
			bg.add(getUIRadioLatestRecordsRB());
			bg.add(getUIRadioCertainRecordRB());
			maintenRecordPanel.setSize(400, 300);
			maintenRecordPanel.add(getUIRadioAllRecordsRB());
			maintenRecordPanel.add(getUIRadioLatestRecordsRB());
			maintenRecordPanel.add(getUIRadioCertainRecordRB());
			maintenRecordPanel.add(getUIRefWa_item());
			maintenRecordPanel.add(getUIButtonConfrim());
			maintenRecordPanel.add(getUIButtonCancle());
			layout.putConstraint(SpringLayout.WEST, getUIRadioAllRecordsRB(), 80, SpringLayout.WEST, getMaintenRecord());
			layout.putConstraint(SpringLayout.NORTH, getUIRadioAllRecordsRB(), 50, SpringLayout.NORTH, getMaintenRecord());
			layout.putConstraint(SpringLayout.WEST, getUIRadioLatestRecordsRB(), 80, SpringLayout.WEST, getMaintenRecord());
			layout.putConstraint(SpringLayout.NORTH, getUIRadioLatestRecordsRB(), 80, SpringLayout.NORTH, getMaintenRecord());
			layout.putConstraint(SpringLayout.WEST, getUIRadioCertainRecordRB(), 80, SpringLayout.WEST, getMaintenRecord());
			layout.putConstraint(SpringLayout.NORTH, getUIRadioCertainRecordRB(), 110, SpringLayout.NORTH, getMaintenRecord());
			layout.putConstraint(SpringLayout.WEST, getUIRefWa_item(), 230, SpringLayout.WEST, getMaintenRecord());
			layout.putConstraint(SpringLayout.NORTH, getUIRefWa_item(), 110, SpringLayout.NORTH, getMaintenRecord());
			layout.putConstraint(SpringLayout.WEST, getUIButtonConfrim(), 60, SpringLayout.WEST, getMaintenRecord());
			layout.putConstraint(SpringLayout.NORTH, getUIButtonConfrim(), 170, SpringLayout.NORTH, getMaintenRecord());
			layout.putConstraint(SpringLayout.WEST, getUIButtonCancle(), 250, SpringLayout.WEST, getMaintenRecord());
			layout.putConstraint(SpringLayout.NORTH, getUIButtonCancle(), 170, SpringLayout.NORTH, getMaintenRecord());
		}
		return maintenRecordPanel;
	}
	/**
	 * 确定按钮
	 * @return
	 * @author suhf1
	 */
	private UIButton getUIButtonConfrim(){
		if(confiromB == null){
			confiromB = new UIButton("确定");
			confiromB.setVisible(true);
			confiromB.addActionListener(this);
		}
		return confiromB;
	}
	/**
	 * 取消按钮
	 * @return
	 * @author suhf1
	 */
	private UIButton getUIButtonCancle(){
		if(cancleB == null){
			cancleB = new UIButton("取消");
			cancleB.setVisible(true);
			cancleB.addActionListener(this);
		}
		return cancleB;
	}
	/**
	 * 默认状态___所有定薪项目所有记录
	 * @author suhf1
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioAllRecordsRB() {
		if (allRecordsRB == null) {
			try {
				allRecordsRB = new nc.ui.pub.beans.UIRadioButton();
				allRecordsRB.setName("UIRadioAllRecordsRB");
				allRecordsRB.setText("默认状态");
				allRecordsRB.setPreferredSize(new Dimension(100, 22));
				allRecordsRB.setSelected(true);
				allRecordsRB.setVisible(true);
				allRecordsRB.addActionListener(this);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return allRecordsRB;
	}
	/**
	 * 最新薪资项目_所有定薪项目最新记录
	 * @author suhf1
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioLatestRecordsRB() {
		if (latestRecordsRB == null) {
			try {
				latestRecordsRB = new nc.ui.pub.beans.UIRadioButton();
				latestRecordsRB.setName("UIRadioAllRecordsRB");
				latestRecordsRB.setText("最新薪资项目");
				latestRecordsRB.setPreferredSize(new Dimension(100, 22));
				latestRecordsRB.setVisible(true);
				latestRecordsRB.addActionListener(this);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return latestRecordsRB;
	}
	/**
	 *某一个薪资项目_一个薪资项目所有记录
	 * @author suhf1
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioCertainRecordRB() {
		if (certainRecordRB == null) {
			try {
				certainRecordRB = new nc.ui.pub.beans.UIRadioButton();
				certainRecordRB.setName("UIRadioCertainRecordRB");
				certainRecordRB.setText("某一个薪资项目");
				certainRecordRB.setPreferredSize(new Dimension(130, 22));
				certainRecordRB.setVisible(true);
				certainRecordRB.addActionListener(this);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return certainRecordRB;
	}
	/**
	 * 薪资项目参照
	 * @author suhf1
	 */
	private nc.ui.pub.beans.UIRefPane getUIRefWa_item() {
		if (wa_itemRF == null) {
			try {
				wa_itemRF = new nc.ui.pub.beans.UIRefPane();
				wa_itemRF.setName("UIRefAlterCode");
				wa_itemRF.setPreferredSize(new Dimension(135, 22));
				wa_itemRF.setReturnCode(true);
				wa_itemRF.setRefType(IRefConst.GRID);
				wa_itemRF.setIsCustomDefined(true);
				wa_itemRF.setButtonFireEvent(true);
				wa_itemRF.setIsCustomDefined(true);
				nc.ui.wa.wa_031.WaItemRefModel remodel = new nc.ui.wa.wa_031.WaItemRefModel();
				wa_itemRF.setRefModel(remodel);
				wa_itemRF.setVisible(false);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return wa_itemRF;
	}
	/**
	 * 返回 UISplitPane1 特性值。
	 * 
	 * @return nc.ui.pub.beans.UISplitPane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UISplitPane getUIHSplitPane() {
		if (ivjUIHSplitPane == null) {
			try {
				ivjUIHSplitPane = new nc.ui.pub.beans.UISplitPane(
						javax.swing.JSplitPane.VERTICAL_SPLIT);
				ivjUIHSplitPane.setName("UIHSplitPane");
				ivjUIHSplitPane.setDividerLocation(200);
				ivjUIHSplitPane.setPreferredSize(new java.awt.Dimension(200,
						200));
				ivjUIHSplitPane.setDividerSize(4);
				getUIHSplitPane().add(getPsnWadocMainPane(), "top");
				getUIHSplitPane().add(getPsnWadocSubPane(), "bottom");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIHSplitPane;
	}

	/**
	 * 返回 UIRightPnl 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIRightPnl() {
		if (ivjUIRightPnl == null) {
			try {
				ivjUIRightPnl = new nc.ui.pub.beans.UIPanel();
				ivjUIRightPnl.setName("UIRightPnl");
				ivjUIRightPnl.setLayout(new java.awt.BorderLayout());
				getUIRightPnl().add(getUIHSplitPane(), "Center");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIRightPnl;
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

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			// user code begin {1}
			m_boPrintGroup.removeAllChildren();
//			m_boPrintGroup.addChildButton(m_boPrint);
//			m_boPrintGroup.addChildButton(m_boDirectPrint);
			
			m_boPrintGroup.addChildButton(m_boNomalPrint); //正常晋升
			m_boPrintGroup.addChildButton(m_boGwbdPrint);  //岗位变动
			m_boPrintGroup.addChildButton(m_boNewLyPrint); //新录用
			m_boPrintGroup.addChildButton(m_boOtherPrint); //其它

			setName("PsnWadocUI");
			setSize(774, 419);
			add(getExportTopPanel(), "North");
			getExportTopPanel().setVisible(false);
			add(getUIRightPnl(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		setButtons(bnGroup);
		addListener();
		beforeInit();
		initTable();
		setBtnsStatWithUIStat(-1);
		// user code end
	}

	/**
	 * 得到顶部Panel
	 * 
	 * @return
	 */
	public UIPanel getExportTopPanel() {
		if (ivjTopPanel == null) {
			try {
				ivjTopPanel = new nc.ui.pub.beans.UIPanel();
				ivjTopPanel.setName("TopPanel");
				ivjTopPanel.setPreferredSize(new java.awt.Dimension(10, 50));
				ivjTopPanel.setLayout(null);

				ivjTopPanel.add(getlblFileName(), getlblFileName().getName());
				ivjTopPanel.add(gettfFileName(), gettfFileName().getName());
				ivjTopPanel.add(getUIBnBrowser(), getUIBnBrowser().getName());
				ivjTopPanel.add(getUIFileChooser(), getUIFileChooser()
						.getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTopPanel;
	}

	/**
	 * 得到Label
	 * 
	 * @return
	 */
	protected nc.ui.pub.beans.UILabel getlblFileName() {
		if (ivjlblFileName == null) {
			try {
				ivjlblFileName = new nc.ui.pub.beans.UILabel();
				ivjlblFileName.setName("lblFileName");
				ivjlblFileName.setPreferredSize(new java.awt.Dimension(60, 22));
				ivjlblFileName.setText(ResHelper.getString("60130715",
				"UPP60130715-000081")// @res "导出文件路径、名称"
				);
				ivjlblFileName.setBounds(17, 16, 110, 22);
				ivjlblFileName.setMinimumSize(new java.awt.Dimension(52, 22));
				ivjlblFileName.setMaximumSize(new java.awt.Dimension(52, 22));
				// user code begin {1}
				ivjlblFileName.setVisible(true);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjlblFileName;
	}

	/**
	 * 得到文件名称
	 * 
	 * @return
	 */
	public nc.ui.pub.beans.UITextField gettfFileName() {
		if (ivjtfFileName == null) {
			try {
				ivjtfFileName = new nc.ui.pub.beans.UITextField();
				ivjtfFileName.setName("tfFileName");
				ivjtfFileName.setBounds(132, 16, 200, 22);
				ivjtfFileName.setMaxLength(300);
				// user code begin {1}
				ivjtfFileName.setVisible(true);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjtfFileName;
	}

	/**
	 * 浏览按钮
	 * 
	 * @return
	 */
	protected nc.ui.pub.beans.UIButton getUIBnBrowser() {
		if (ivjUIBnBrowser == null) {
			try {
				ivjUIBnBrowser = new nc.ui.pub.beans.UIButton();
				ivjUIBnBrowser.setName("UIBnBrowser");
				ivjUIBnBrowser.setIButtonType(2/** 参照按钮 */
				);
				ivjUIBnBrowser.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("common", "UC001-0000021")/*
						 * @res "浏览"
						 */);
				ivjUIBnBrowser.setBounds(332, 16, 31, 22);
				ivjUIBnBrowser.setMargin(new java.awt.Insets(2, 0, 2, 0));
				// user code begin {1}
				ivjUIBnBrowser.setVisible(true);
				ivjUIBnBrowser.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIBnBrowser;
	}

	public nc.ui.pub.beans.UIFileChooser getUIFileChooser() {
		if (ivjUIFileChooser == null) {
			try {
				ivjUIFileChooser = new nc.ui.pub.beans.UIFileChooser();
				ivjUIFileChooser.setName("UIFileChooser");
				ivjUIFileChooser.setBounds(425, 8, 7, 34);
				ivjUIFileChooser.setVisible(true);

				ivjUIFileChooser.setFileFilter(new FileFilter() {
					String type = "*.xls";
					String ext = ".xls";

					@Override
					public boolean accept(java.io.File f) {
						if (f.isDirectory())
							return true;
						return (f.getName().endsWith(ext)) ? true : false;
					}

					@Override
					public String getDescription() {
						if (type.equals("*.xls"))
							return ResHelper.getString("60130715",
							"UPP60130715-000082")// @res
							// "Excel文件(*.xls)"
							;
						;
						return null;
					}
				});

				// user code begin {1}

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIFileChooser;
	}

	/**
	 * 得到顶部Panel
	 * 
	 * @return
	 */
	public UIPanel getImportTopPanel() {
		if (ivjTopPanelim == null) {
			try {
				ivjTopPanelim = new nc.ui.pub.beans.UIPanel();
				ivjTopPanelim.setName("TopPanelim");
				ivjTopPanelim.setPreferredSize(new java.awt.Dimension(10, 50));
				ivjTopPanelim.setLayout(null);

				ivjTopPanelim.add(getlblFileNameim(), getlblFileNameim()
						.getName());
				ivjTopPanelim.add(gettfFileNameim(), gettfFileNameim()
						.getName());
				ivjTopPanelim.add(getUIBnBrowserim(), getUIBnBrowserim()
						.getName());
				ivjTopPanelim.add(getUIFileChooserim(), getUIFileChooserim()
						.getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTopPanelim;
	}

	/**
	 * 得到Label
	 * 
	 * @return
	 */
	protected nc.ui.pub.beans.UILabel getlblFileNameim() {
		if (ivjlblFileNameim == null) {
			try {
				ivjlblFileNameim = new nc.ui.pub.beans.UILabel();
				ivjlblFileNameim.setName("lblFileName");
				ivjlblFileNameim
				.setPreferredSize(new java.awt.Dimension(60, 22));
				ivjlblFileNameim.setText(ResHelper.getString("60130715",
				"UPP60130715-000107")// @res "导入文件路径、名称"
				);
				ivjlblFileNameim.setBounds(17, 16, 110, 22);
				ivjlblFileNameim.setMinimumSize(new java.awt.Dimension(52, 22));
				ivjlblFileNameim.setMaximumSize(new java.awt.Dimension(52, 22));
				// user code begin {1}
				ivjlblFileNameim.setVisible(true);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjlblFileNameim;
	}

	/**
	 * 得到文件名称
	 * 
	 * @return
	 */
	public nc.ui.pub.beans.UITextField gettfFileNameim() {
		if (ivjtfFileNameim == null) {
			try {
				ivjtfFileNameim = new nc.ui.pub.beans.UITextField();
				ivjtfFileNameim.setName("tfFileName");
				ivjtfFileNameim.setBounds(132, 16, 200, 22);
				ivjtfFileNameim.setMaxLength(300);
				// user code begin {1}
				ivjtfFileNameim.setVisible(true);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjtfFileNameim;
	}

	/**
	 * 浏览按钮
	 * 
	 * @return
	 */
	protected nc.ui.pub.beans.UIButton getUIBnBrowserim() {
		if (ivjUIBnBrowserim == null) {
			try {
				ivjUIBnBrowserim = new nc.ui.pub.beans.UIButton();
				ivjUIBnBrowserim.setName("UIBnBrowserim");
				ivjUIBnBrowserim.setIButtonType(2/** 参照按钮 */
				);
				ivjUIBnBrowserim.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("common", "UC001-0000021")/*
						 * @res "浏览"
						 */);
				ivjUIBnBrowserim.setBounds(332, 16, 31, 22);
				ivjUIBnBrowserim.setMargin(new java.awt.Insets(2, 0, 2, 0));
				// user code begin {1}
				ivjUIBnBrowserim.setVisible(true);
				ivjUIBnBrowserim.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIBnBrowserim;
	}

	public nc.ui.pub.beans.UIFileChooser getUIFileChooserim() {
		if (ivjUIFileChooserim == null) {
			try {
				ivjUIFileChooserim = new nc.ui.pub.beans.UIFileChooser();
				ivjUIFileChooserim.setName("ivjUIFileChooserim");
				ivjUIFileChooserim.setBounds(425, 8, 7, 34);
				ivjUIFileChooserim.setVisible(true);

				ivjUIFileChooserim.setFileFilter(new FileFilter() {
					String type = "*.xls";
					String ext = ".xls";

					@Override
					public boolean accept(java.io.File f) {
						if (f.isDirectory())
							return true;
						return (f.getName().endsWith(ext)) ? true : false;
					}

					@Override
					public String getDescription() {
						if (type.equals("*.xls"))
							return ResHelper.getString("60130715",
							"UPP60130715-000066")// @res
							// "Excel文件(*.xls)"
							;
						;
						return null;
					}
				});
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIFileChooserim;
	}

	/**
	 * 子类实现该方法，响应按钮事件。 中转到controller中去
	 * 
	 * @version (00-6-1 10:32:59)
	 * 
	 * @param bo
	 *            ButtonObject
	 */
	@Override
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {

		try {
			String suffix = null;
			if (bo == m_bnAdd) {// add one record
				onAdd();
				suffix = ButtonTipMessage.ING;
			} else if (bo == m_bnInsert) {// insert one record
				onInsert();
				suffix = ButtonTipMessage.ING;
			} else if (bo == m_bnModify) {
				onModify();
				suffix = ButtonTipMessage.ING;
			} else if (bo == m_bnDelete) {
				onDelete();
				suffix = ButtonTipMessage.SUCCESSED;
			} else if (bo == m_bnQuery) {
				onQuery();
			} else if (bo == m_bnRefresh) {
				onRefresh();
				suffix = ButtonTipMessage.SUCCESSED;
//			} else if (bo == m_boPrint) {// 模板打印
//				onPrintBytemplate();
//			} else if (bo == m_boDirectPrint) {// 直接打印
//				onPrintDirect();
			} else if (bo == m_boNomalPrint) { // 正常晋升
				onPrintNomal();
				//MessageDialog.showHintDlg(this, "提示", "正常晋升打印");
			} else if (bo == m_boGwbdPrint) {// 岗位变动
				onPrintGwbd();
				//MessageDialog.showHintDlg(this, "提示", "岗位变动打印");
			} else if (bo == m_boNewLyPrint) {// 新录用
				onPrintNewLy();
				//MessageDialog.showHintDlg(this, "提示", "新录用打印");
			} else if (bo == m_boOtherPrint) {// 其它
				onPrintOther();
				//MessageDialog.showHintDlg(this, "提示", "其它打印");
			} else if (bo == m_bnSave) {
				onSave();
				suffix = ButtonTipMessage.SUCCESSED;
			} else if (bo == m_bnCancel) {
				onCancel();
				suffix = ButtonTipMessage.SUCCESSED;
			} else if (bo == m_bnImport) { // 导入
				onImport();
			} else if (bo == m_bnExport) { // 导出
				onExport();
			} else if (bo == m_bnReturn) {// 返回
				onReturn();
			} else if (bo == m_bnExportData) {// 导出数据
				onExportData();
			} else if (bo == m_bnImportData) {// 导入数据
				onImportData();
			}
			// 薪资普调
			else if (bo == m_bnAdjust) {
				onBatchAdjust();
			} 
			//add by xhhrp suhf1 定调资维护记录
			else if(bo == m_bnQuery_MaintenRecord) {
				getUIdialog().showModal();
			}else if(bo == m_btnBfbk){
				//薪资维护
				onBfbkSet();
			}else if(bo == m_btnPsn){
				//人员信息查看
				onPsnView();
			}else if(bo == m_btnUpdateGjj){
				onUpdateGjj();
			}else if(bo == m_btnUpdateFF){
				onUpdateff();
			}else if(bo == m_boRemark){
				//备注
				onRemarkDlg();
			}else if(bo == m_bnTempAdd){
				ontTempAdd();
			}else if(bo== m_btnWXYJ){
				//五险一金查看
				onWXYJDlg();
			}
			
			if (suffix != null) {
				showHintMessage(bo.getName() + suffix);
			} else {
				showHintMessage("");
			}
		} catch (Exception e) {
			showHintMessage("");
			showErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}
	protected void onUpdateff() throws Exception{
		showHintMessage("批改发放标志");
		int rows[] = getPsnWadocSubPane().getBillScrollPane().getTable().getSelectedRows();
		if(rows==null||rows.length<=0) return;
		if(MessageDialog.showOkCancelDlg(this, "提示", "确认批改选中行的发放标志")!=UIDialog.ID_OK) return;
		PsndocWadocVO[] vos = new PsndocWadocVO[rows.length];
		String psnid = null;
		boolean gwgzitem = false;
		for(int i=0;i<rows.length;i++){
			vos[i] = new PsndocWadocVO();
			String pk = (String)getPsnWadocSubPane().getBillScrollPane().getTableModel().getValueAt(rows[i], "pk_psndoc_sub");
			vos[i] = (PsndocWadocVO)HYPubBO_Client.queryByPrimaryKey(PsndocWadocVO.class, pk);
			vos[i].setWaflag(new UFBoolean(false));
			psnid = vos[i].getPk_psndoc();
			String iitemid = vos[i].getPk_wa_item();
			UFBoolean  lastflag = vos[i].getLastflag();
			if(iitemid.equals("0001691000000000BYWP")&&lastflag!=null&&lastflag.booleanValue()){
				gwgzitem = true;
			}
		}
		if(gwgzitem){
			PsndocVO psnvo = (PsndocVO)HYPubBO_Client.queryByPrimaryKey(PsndocVO.class, psnid);
			psnvo.setGroupdef33(null);
			HYPubBO_Client.update(psnvo);
		}
		
		HYPubBO_Client.updateAry(vos);
		onRefresh();
		showHintMessage("批改发放完成");
	}
	
	
	
	private InfoDLG m_psnInfoDlg = null;
	private InfoDLG getPsnInfoDlg(String pk_psndoc){
		m_psnInfoDlg = new InfoDLG(this,pk_psndoc);
		return m_psnInfoDlg;
	}
	
	private ShowWxyjDLG m_WXYJDlg = null;
	private ShowWxyjDLG getShowWxyjDlg(String pk_psndoc ,String psnclassname ){
		m_WXYJDlg = new ShowWxyjDLG(this,pk_psndoc,psnclassname);
		return m_WXYJDlg;
	}
	
	protected void onRemarkDlg() throws Exception{
		if(getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount() < 1){
			showWarningMessage("请选中一条人员");
			return;
		}
		if(currentSelectRow < 0){
			showWarningMessage("请选中一条人员");
			return;
		}
		PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
				currentSelectRow);
		if(vo == null){
			showWarningMessage("请选中一条人员");
			return;
		}
		if(vo.getPk_psndoc() == null){
			showWarningMessage("请选中一条人员");
			return;
		}
		InfoDLG dlg =  getPsnInfoDlg(vo.getPk_psndoc());
		if(dlg.showModal() == UIDialog.ID_OK){
			onRefresh();
		}
		
		
	}
	
	protected void onWXYJDlg() throws Exception{
		if(getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount() < 1){
			showWarningMessage("请选中一条人员");
			return;
		}
		if(currentSelectRow < 0){
			showWarningMessage("请选中一条人员");
			return;
		}
		PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
				currentSelectRow);
		if(vo == null){
			showWarningMessage("请选中一条人员");
			return;
		}
		if(vo.getPk_psndoc() == null){
			showWarningMessage("请选中一条人员");
			return;
		}
		
		if(vo.getPk_psndoc() != null){
			PsndocWadocVO psndocWadocVO = getPsndocWadocVO(vo.getPk_psndoc());
			if(psndocWadocVO == null){
				nc.ui.pub.beans.MessageDialog.showErrorDlg(this, "错误", "未维护社保基数！！！");
				return;
			}
			
		}
		
		ShowWxyjDLG dlg =  getShowWxyjDlg(vo.getPk_psndoc(),vo.getPsnclassname());
		if(dlg.showModal() == UIDialog.ID_OK){
			onRefresh();
		}
		
		
	}
	
	 public PsndocWadocVO getPsndocWadocVO(String pk_psndoc) throws BusinessException{
	    	IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			String sql="select * from hi_psndoc_wadoc where isnull(dr,0)=0 and pk_wa_item='10028L1000000001VW2H' and lastflag='Y' and pk_psndoc='"+pk_psndoc+"'";
			PsndocWadocVO psndocWadocVo = (PsndocWadocVO) service.executeQuery(sql, new BeanProcessor(PsndocWadocVO.class)); 
	    	return psndocWadocVo;
	    }
	
	protected void onUpdateGjj() throws Exception{
		showHintMessage("跟新公积金基数及房贴");
		String dwft = "0001691000000000BYY5";
		String gjjjs = "0001691000000000T5E2";
		String sbjs = "0001691000000000EIOF";
		if(MessageDialog.showOkCancelDlg(this, "提示", "确认批量跟新")!=UIDialog.ID_OK) return;
		UFDate date = Global.getLogDate();
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String sql2 = "select psnid from wa_data where istopflag=0 and isnull(dr,0)=0 and cyear='"+date.toString().substring(0,4)+"' and cperiod='"+date.toString().substring(5,7)+"' and classid in('"+IHRPWABtn.PK_GONG+"','"+IHRPWABtn.PK_GONG_HT+"','"+IHRPWABtn.PK_GONG_HTZB+"','"+IHRPWABtn.PK_GONG_FB+"') ";
		Vector o2 = (Vector)service.executeQuery(sql2, new VectorProcessor());
		ArrayList<String> list_psn = new ArrayList<String>();
		for(int i = 0; i < o2.size(); i++){
			String psnid = ((Vector)o2.elementAt(i)).elementAt(0) != null?((Vector)o2.elementAt(i)).elementAt(0).toString():"";
			list_psn.add(psnid);
		}
		String sql = HRPPubTool.formInSQL("pk_psndoc",list_psn);
		PsndocWadocVO[] vos = (PsndocWadocVO[])HYPubBO_Client.queryByCondition(PsndocWadocVO.class,
				" isnull(dr,0)=0  and isnull(lastflag,'N')='Y' and pk_wa_item in('"+dwft+"','"+gjjjs+"','"+sbjs+"') "+sql+" order by pk_psndoc ");
		
		HashMap<String,PsndocWadocVO> mapold = new HashMap<String, PsndocWadocVO>();
		HashMap<String,PsndocWadocVO> mapold_g = new HashMap<String, PsndocWadocVO>();
		HashMap<String,PsndocWadocVO> mapold_d = new HashMap<String, PsndocWadocVO>();
		if(vos!=null&&vos.length>0){
			for(PsndocWadocVO vo:vos){
				if(vo.getPk_wa_item().equals(sbjs)){
					mapold.put(vo.getPk_psndoc(), vo);
				}else if(vo.getPk_wa_item().equals(gjjjs)){
					mapold_g.put(vo.getPk_psndoc(), vo);
				}else if(vo.getPk_wa_item().equals(dwft)){
					mapold_d.put(vo.getPk_psndoc(), vo);
				}
			}
			if(mapold==null||mapold.size()<=0) {
				showHintMessage("跟新完成,共跟新生成0条数据");
				return;
			}
			String[] keys = mapold.keySet().toArray(new String[0]);
			ArrayList<PsndocWadocVO> list = new ArrayList<PsndocWadocVO>();
			ArrayList<PsndocWadocVO> updatelist = new ArrayList<PsndocWadocVO>();
			for(String key:keys){
				PsndocWadocVO vo = mapold.get(key);
				UFDate begindate = date.before(vo.getBegindate())?vo.getBegindate():date;
				begindate = new UFDate(begindate.toString().substring(0,7)+"-01");
				PsndocWadocVO vog = mapold_g.get(key);
				PsndocWadocVO vod = mapold_d.get(key);
				if(vog==null||!vog.getBegindate().after(begindate)){
					PsndocWadocVO nvo = new PsndocWadocVO();
					nvo.setPk_psndoc(key);
					nvo.setLastflag(new UFBoolean(true));
					nvo.setBegindate(begindate);
					nvo.setChangedate(begindate);
					nvo.setIadjustmatter(1);
					nvo.setNegotiation_wage(new UFBoolean(true));
					nvo.setNmoney(vo.getNmoney());
					nvo.setPrimaryKey(null);
					nvo.setStatus(VOStatus.NEW);
					nvo.setWaflag(new UFBoolean(true));
					nvo.setWorkflowflag(new UFBoolean(false));
					nvo.setRecordnum(vog!=null?vog.getRecordnum()+1:0);
					nvo.setPk_wa_item(gjjjs);
					nvo.setPk_wa_grd("100169100000000F1ATF");
					list.add(nvo);
					if(vog!=null){
						vog.setStatus(VOStatus.UPDATED);
						vog.setLastflag(new UFBoolean(false));
						updatelist.add(vog);
					}
				}
				if(vod!=null&&!vod.getBegindate().after(begindate)){
					PsndocWadocVO nvo = new PsndocWadocVO();
					nvo.setPk_psndoc(key);
					nvo.setLastflag(new UFBoolean(true));
					nvo.setBegindate(begindate);
					nvo.setChangedate(begindate);
					nvo.setIadjustmatter(1);
					nvo.setNegotiation_wage(new UFBoolean(true));
					nvo.setNmoney((vo.getNmoney()!=null?vo.getNmoney().multiply(0.05):new UFDouble(0)).setScale(0, 2));
					nvo.setPrimaryKey(null);
					nvo.setStatus(VOStatus.NEW);
					nvo.setWaflag(new UFBoolean(true));
					nvo.setWorkflowflag(new UFBoolean(false));
					nvo.setRecordnum(vod!=null?vod.getRecordnum()+1:0);
					nvo.setPk_wa_item(dwft);
					nvo.setPk_wa_grd("100169100000000EVDPI");
					list.add(nvo);
					if(vod!=null){
						vod.setStatus(VOStatus.UPDATED);
						vod.setLastflag(new UFBoolean(false));
						updatelist.add(vod);
					}
				}
			}
			if(list!=null&&list.size()>0){
				HYPubBO_Client.insertAry(list.toArray(new PsndocWadocVO[0]));
				HYPubBO_Client.updateAry(updatelist.toArray(new PsndocWadocVO[0]));
				showHintMessage("跟新完成,共生成"+list.size()+"条数据");
				MessageDialog.showHintDlg(this, "提示", "跟新完成,共生成"+list.size()+"条数据");
			}else{
				MessageDialog.showHintDlg(this, "提示", "跟新完成,共生成0条数据");
			}
		}
//		PsndocWadocMainVO[] mainVOs = getWadocMain(Global.getCorpPK(),getQueryDlg());
//		WADelegator.getWaPsndoc().queryWadocMainDataMorePsnclscope(Global.getCorpPK(), "",
//		null, null, null);
		onRefresh();
	}

	/**
	 * 显示数据
	 */
	public void onShowInfo() {
		try {
			String key = "";
			if(insertlist.size()<1){
				MessageDialog.showErrorDlg(this, null, ResHelper.getString("60130715", "UPP60130715-000109")/*"没有可以导入的数据"*/);
				return;
			}
			for (int i = 0; i < insertlist.size(); i++) {
				key = "'" + insertlist.get(i) + "'," + key;
			}
			// String swhere = " (hi_psndoc_wadoc.pk_psndoc in
			// ("+key.substring(0, key.length()-1)+")) ";
			String swhere = " (bd_psndoc.pk_psndoc in ("
				+ key.substring(0, key.length() - 1) + ")) ";
			List<String> linkedList = new LinkedList<String>();
			linkedList.add("bd_psndoc");
			PsndocWadocMainVO[] mainVOs = WADelegator.getWaPsndoc()
			.queryWadocMainData(Global.getCorpPK(), swhere, linkedList,
					null, null);

			getPsnWadocMainPane().setWadocData(mainVOs);
			if (mainVOs != null && mainVOs.length > 0) {
				getPsnWadocMainPane().getBillScrollPane().getTable()
				.addRowSelectionInterval(0, 0);
				getPsnWadocMainPane().getBillScrollPane().getTable()
				.addColumnSelectionInterval(0, 0);
			} else {
				getPsnWadocSubPane().setWadocData(null);
				setBtnsStatWithUIStat(-1);
			}
			MessageDialog.showHintDlg(null, ResHelper.getString("60130715",
			"UPP60130715-000054")// @res "提示"
			, ResHelper.getString("60130715", "UPP60130715-000084")// @res
			// "文件导入成功"
			);
			// this.insertlist = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击导入按钮
	 */
	public void onImport() {
		this.remove(this.getExportTopPanel());
		this.add(this.getImportTopPanel(), "North");
		this.getImportTopPanel().setVisible(true);
		this.currentstat = UI_STAT_IMPOTRT;
		this.setBtnsStatWithUIStat(UI_STAT_IMPOTRT);
	}

	/**
	 * 导入数据
	 */
	public void onImportData() {
		try {
			// 判断输出文件名是否为空
			String imfilename = getOutFileNameim();
			if (imfilename == null || imfilename.trim().length() < 1) {
				return;
			}
			boolean fileHasExisted = false;
			try {
				// 判断要导出的文件是否已存在
				fileHasExisted = fileExist(imfilename);
				if (!fileHasExisted) {
					int choice = MessageDialog.showYesNoDlg(this, ResHelper
							.getString("60130715", "UPP60130715-000054")// @res
							// "提示"
							, ResHelper.getString("60130715",
							"UPP60130715-000085")// @res
							// "要导入的文件不存在或导出文件的路径、名称非法，继续执行吗?"
					);
					if (choice != MessageDialog.ID_YES) {
						return;
					}
				}
			} catch (Exception e) {
				int choice = MessageDialog.showYesNoDlg(this, ResHelper
						.getString("60130715", "UPP60130715-000054")// @res "提示"
						, ResHelper.getString("60130715", "UPP60130715-000086")// @res
						// "判断要导入的文件是否已存在时出错，继续执行将替换原有文件，要继续吗?"
				);
				if (choice != MessageDialog.ID_YES) {
					return;
				}
			}

			DataExpImp filein = new DataExpImp();
			filein.openFile(imfilename);
			PsndocWadocVO[] wadocvos = filein.readData();
			wadocvos = filein.getSaveData(wadocvos);
			for(int i=0;i<wadocvos.length;i++){
				if(wadocvos[i].getCriterionvalue()!=null&&wadocvos[i].getCriterionvalue().doubleValue()!=0
						&&wadocvos[i].getCriterionvalue().doubleValue()!=(wadocvos[i].getNmoney()!=null?wadocvos[i].getNmoney().doubleValue():0.00)){
					MessageDialog.showHintDlg(this,"提示","第"+(i+1)+"行级别金额与金额不匹配");
					return;
				}
			}
			filein.closeFile();
			// 得到所有的导入信息
			// List<String> keylist =
			// WADelegator.getWaPsndoc().importExcelData(wadocvos);
			List<String> keylist = null;
			Map mapinfo = WADelegator.getWaPsndoc().compareDataInfo(wadocvos);
			if (mapinfo == null) {
				keylist = WADelegator.getWaPsndoc().insertInfo(wadocvos);
				this.insertlist = keylist;
				// 数据更新
				onShowInfo();
			} else {
				boolean bool = (Boolean) mapinfo.get("flag");
				//=====================================beg=========================================================
				//导入时增加覆盖功能，之前是没有覆盖功能 只有插入取消 sqt
				if (!bool) {
					int answer = MessageDialog031.showYesNoCancelDlg(this,"询问",(String) mapinfo.get("info"));
					if (answer == UIDialog.ID_YES) {
						keylist = WADelegator.getWaPsndoc()
						.insertInfo(wadocvos);
						this.insertlist = keylist;
						// 数据更新
						onShowInfo();
					}else if(answer == UIDialog.ID_NO){
						wadocvos = (PsndocWadocVO[]) mapinfo.get("rvo");
						for(int i =0;i<wadocvos.length;i++){
							WADelegator.getWaPsndoc()
							.updatePsndocWadoc(wadocvos[i]);
						}
						this.insertlist = (List<String>) mapinfo.get("keylist");
						// 数据更新
						onShowInfo();
					}
					//=====================================beg=========================================================
				} else {
					keylist = WADelegator.getWaPsndoc().insertInfo(wadocvos);
					this.insertlist = keylist;
					// 数据更新
					onShowInfo();
				}
			}
		} catch (nc.vo.pub.BusinessException e) {
			e.printStackTrace();
			showErrorMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			showErrorMessage(ResHelper.getString("60130715",
			"UPP60130715-000087")// @res
			// "文件导入失败！请检查导出文件路径、名称是否正确，或者文件已存在并且处在打开状态。"
			);
		}
	}

	public void onReturn() {
		m_bnAdd.setVisible(true);
		m_bnTempAdd.setVisible(true);
		m_bnInsert.setVisible(true);
		m_bnModify.setVisible(true);
		m_bnAdjust.setVisible(true);
		m_bnDelete.setVisible(true);
		m_bnQuery.setVisible(true);
		m_bnRefresh.setVisible(true);
		m_boPrintGroup.setVisible(true);
		m_bnSave.setVisible(true);
		m_bnCancel.setVisible(true);
		m_bnImport.setVisible(true);
		m_bnExport.setVisible(true);
		m_boRemark.setVisible(true);
		this.setBtnsStatWithUIStat(-1);
		this.getExportTopPanel().setVisible(false);
		this.getImportTopPanel().setVisible(false);
		this.currentstat = -1;
	}

	/**
	 * 执行数据导出功能
	 */
	public void onExport() {
		this.remove(this.getImportTopPanel());
		this.add(this.getExportTopPanel(), "North");
		this.getExportTopPanel().setVisible(true);
		this.currentstat = UI_STAT_EXPOTRT;
		this.setBtnsStatWithUIStat(UI_STAT_EXPOTRT);
	}

	/**
	 * 执行数据导出操作
	 */
	public void onExportData() {
		try {
			// 判断输出文件名是否为空
			String outfilename = getOutFileName();
			if (outfilename == null || outfilename.trim().length() < 1) {
				// int choice =
				// MessageDialog.showYesNoDlg(this,"提示","请先选择要导出的数据!");
				return;
			}
			boolean fileHasExisted = false;
			try {
				// 判断要导出的文件是否已存在
				fileHasExisted = fileExist(outfilename);
				if (fileHasExisted) {
					int choice = MessageDialog.showYesNoDlg(this, ResHelper
							.getString("60130715", "UPP60130715-000054")// @res
							// "提示"
							, ResHelper.getString("60130715",
							"UPP60130715-000088")// @res
							// "要导出的文件已存在或导出文件的路径、名称非法，继续执行吗?"
					);
					if (choice != MessageDialog.ID_YES) {
						return;
					}
				}
			} catch (Exception e) {
				int choice = MessageDialog.showYesNoDlg(this, ResHelper
						.getString("60130715", "UPP60130715-000054")// @res "提示"
						, ResHelper.getString("60130715", "UPP60130715-000089")// @res
						// "判断要导出的文件是否已存在时出错，继续执行将替换原有文件，要继续吗?"
				);
				if (choice != MessageDialog.ID_YES) {
					return;
				}
			}
			// 得到列名
			TableModel model = this.getPsnWadocSubPane().getBillScrollPane()
			.getTable().getModel();
			String[] l_straryColNames = new String[model.getColumnCount() - 6];
			for (int i = 0; i < l_straryColNames.length; i++) {
				String colname = model.getColumnName(i);
				if (!colname.equalsIgnoreCase("pk_wa_item")
						&& !colname.equalsIgnoreCase("recordnum")
						&& !colname.equalsIgnoreCase("pk_psndoc_sub")
						&& !colname.equalsIgnoreCase("workflowflag")
						&& !colname.equalsIgnoreCase("pk_wa_pralv")
						&& !colname.equalsIgnoreCase("pk_wa_seclv")
						&& !colname.equalsIgnoreCase("pk_wa_grd")
						&& !colname.equalsIgnoreCase("pk_changecause")/** &&!colname.equalsIgnoreCase("最新标志") */
				) {
					l_straryColNames[i] = colname;
				}
			}
			l_straryColNames[14] = ResHelper.getString("common",
			"UC000-0004069")// @res "部门名称"
			;
			l_straryColNames[15] = ResHelper.getString("common",
			"UC000-0000147")// @res "人员编码"
			;
			l_straryColNames[16] = ResHelper.getString("common",
			"UC000-0000134")// @res "人员名称"
			;
			l_straryColNames[17] = ResHelper.getString("common",
			"UC000-0000787")// @res "单位编码"
			;
			l_straryColNames[18] = ResHelper.getString("common",
			"UC000-0000783")// @res "单位名称"
			;
			// 组织表格数据
			if (currentSelectRow >= 0) {
				PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
						currentSelectRow);
				String[][] l_outdata = new String[model.getRowCount()][l_straryColNames.length];
				for (int i = 0; i < model.getRowCount(); i++) {
					for (int j = 0; j < l_straryColNames.length; j++) {
						if (j < 14) {
							Object Ob = model.getValueAt(i, j);
							if (Ob != null) {
								if (Ob.toString().trim().equalsIgnoreCase(
								"true")) {
									l_outdata[i][j] = "Y";
								} else if (Ob.toString().trim()
										.equalsIgnoreCase("false")) {
									l_outdata[i][j] = "N";
								} else {
									l_outdata[i][j] = Ob.toString().trim();
								}
							} else {
								l_outdata[i][j] = "";
							}
						} else {
							l_outdata[i][14] = vo.getDeptName();
							l_outdata[i][15] = vo.getPsnCode();
							l_outdata[i][16] = vo.getPsnName();
							l_outdata[i][17] = Global.getCorpPK();   //Global.getWaCorpCode();
							l_outdata[i][18] = Global.getCorpname();  //Global.getWaCorpName();
						}
					}
				}
				// 输出数据
				DataExpImp fileout = new DataExpImp(outfilename,
						l_straryColNames, l_outdata);
				// 输出
				int result = fileout.exportChkAttdDataExcelFile();
				if (result == 0) {
					MessageDialog.showHintDlg(this, ResHelper.getString(
							"60130715", "UPP60130715-000054")// @res "提示"
							, ResHelper.getString("60130715",
							"UPP60130715-000106")// @res "文件导出成功"
					);
				}
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			showErrorMessage(ResHelper.getString("60130715",
			"UPP60130715-000090")// @res
			// "文件导出失败！请检查导出文件路径、名称是否正确，或者文件已存在并且处在打开状态。"
			);
		}
	}

	/**
	 * 将表中内容输出到指定文件。 add by ddw at 2003-11-10
	 */
	public String getOutFileName() {
		String outfilename = gettfFileName().getText();

		if (outfilename == null || outfilename.trim().length() < 1) {
			showErrorMessage(ResHelper.getString("60130715",
			"UPP60130715-000091")// @res "导出路径、文件名不能为空!"
			);

		}
		return outfilename;
	}

	/**
	 * 将表中内容导入到指定文件。 add by ddw at 2003-11-10
	 */
	public String getOutFileNameim() {
		String imfilename = gettfFileNameim().getText();

		if (imfilename == null || imfilename.trim().length() < 1) {
			showErrorMessage(ResHelper.getString("60130715",
			"UPP60130715-000092")// @res "导入路径、文件名不能为空!"
			);

		}
		return imfilename;
	}

	/**
	 * 此处插入方法说明。 add by ddw at 2003-11-11
	 * 
	 * @return boolean
	 * @param fileName
	 *            java.lang.String
	 */
	public boolean fileExist(String fileName) {
		java.io.File file = new java.io.File(fileName);
		return file.exists();
	}

	/**
	 * 增加：第一条记录，“发放标志”、“最新标志”全打勾选中；增加第二条记录，则第一条记录的“最新标志”选中去掉
	 * 
	 */
	public void onAdd() {
		setTopPanelProperty(false);
		getPsnWadocSubPane().onAdd();
	}
	
	public void ontTempAdd() {
		setTopPanelProperty(false);
		getPsnWadocSubPane().onTempAdd();
	}

	/**
	 * 插入：选中非最新的记录插入，则“最新标志”recordnum为选中记录的值-1，选中最新记录插入，则“最新标志”recordnum为最大值＋1；
	 * 
	 */
	public void onInsert() {
		setTopPanelProperty(false);
		getPsnWadocSubPane().onInsert();
	}

	/**
	 * 取消：取消当前操作的数据。
	 */
	@Override
	public void onCancel() {

		onRefresh();
		getPsnWadocSubPane().setState(PsnWadocSubPane.UNKNOWN_STATE);
	}

	/**
	 * @throws Exception
	 * @throws Exception
	 * 
	 * 
	 */
	public void onDelete() throws Exception {

		if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"60130425", "UPP60130425-000009")/*
				 * @res "确实要删除吗！"
				 */) == UIDialog.ID_YES) {
			getPsnWadocSubPane().onDelte();

			setBtnsStatWithUIStat(-1);
			onRefresh();

		}

	}

	/**
	 * 
	 * 保存：保存增加/修改的数据。
	 * 
	 * @throws Exception
	 */
	public void onSave() throws Exception {
		if (getPsnWadocSubPane().onSave()) {
			setBtnsStatWithUIStat(-1);
			onRefresh();
		}
	}

	/**
	 * 
	 * 修改：修改只能修改最新记录的“发放标志”；历史的记录“发放标志”不能修改
	 * 
	 * @throws Exception
	 * @throws Exception
	 */
	public void onModify() throws Exception {
		setTopPanelProperty(false);
		getPsnWadocSubPane().onModify();
		setBtnsStatWithUIStat(HRToftPanel.UI_STAT_UPDATE);
	}

	/**
	 * 根据界面状态设定按钮组状态。 当同处某种界面状态下，有类似不同的树节点需要有不同的按钮状态时，
	 * 可以考虑用setBtnStatWithBuss的方法再补充一次 创建日期：(2003-3-27 20:09:48)
	 * 
	 * @param stat
	 *            int
	 */
	@Override
	public void setBtnsStatWithUIStat(int stat) {
		if (stat == -1) {
			m_bnAdd.setEnabled(getPsnWadocMainPane().getBillScrollPane()
					.getTable().getSelectedRow() < 0 ? false : true);
			m_boRemark.setEnabled(getPsnWadocMainPane().getBillScrollPane()
					.getTable().getSelectedRow() < 0 ? false : true);
			m_bnTempAdd.setEnabled(getPsnWadocMainPane().getBillScrollPane()
					.getTable().getSelectedRow() < 0 ? false : true);
			m_bnAdjust.setEnabled(getPsnWadocMainPane().getBillScrollPane()
					.getTable().getSelectedRow() < 0 ? false : true);
			m_bnQuery_MaintenRecord.setEnabled(m_bnAdd.isEnabled());
			m_btnBfbk.setEnabled(m_bnAdd.isEnabled());
			m_bnInsert.setEnabled(false);
			m_bnModify.setEnabled(false);
			m_bnDelete.setEnabled(false);
			m_bnQuery.setEnabled(true);
			m_bnRefresh.setEnabled(true);
			m_boPrintGroup.setEnabled(true);
			m_bnSave.setEnabled(false);
			m_bnCancel.setEnabled(false);
			m_bnImport.setEnabled(true);
			m_bnExport.setEnabled(true);
			m_bnReturn.setVisible(false);
			m_bnExportData.setVisible(false);
			m_bnImportData.setVisible(false);
			m_btnUpdateGjj.setVisible(true);
			m_btnUpdateGjj.setEnabled(true);
			m_btnUpdateFF.setVisible(true);
			m_btnUpdateFF.setEnabled(true);
			// m_bnDispaly.setVisible(false);
		} else if (stat == UI_STAT_BROWSE) {
			m_bnAdd.setEnabled(true);
			m_boRemark.setEnabled(true);
			m_bnTempAdd.setEnabled(true);
			m_bnQuery_MaintenRecord.setEnabled(true);
			m_btnBfbk.setEnabled(true);
			m_bnAdjust.setEnabled(true);
			m_bnInsert.setEnabled(true);
			m_bnModify.setEnabled(true);
			m_bnDelete.setEnabled(true);
			m_bnQuery.setEnabled(true);
			m_bnRefresh.setEnabled(true);
			m_boPrintGroup.setEnabled(true);
			m_bnSave.setEnabled(false);
			m_bnCancel.setEnabled(false);
			m_bnImport.setEnabled(true);
			m_bnExport.setEnabled(true);
			m_btnUpdateGjj.setVisible(true);
			m_btnUpdateGjj.setEnabled(true);
			m_btnUpdateFF.setVisible(true);
			m_btnUpdateFF.setEnabled(true);
		} else if (stat == UI_STAT_UPDATE) {
			m_bnAdd.setEnabled(false);
			m_boRemark.setEnabled(false);
			m_bnTempAdd.setEnabled(false);
			m_bnQuery_MaintenRecord.setEnabled(false);
			m_btnBfbk.setEnabled(false);
			m_bnAdjust.setEnabled(false);
			m_bnInsert.setEnabled(false);
			m_bnModify.setEnabled(false);
			m_bnDelete.setEnabled(false);
			m_bnQuery.setEnabled(false);
			m_bnRefresh.setEnabled(false);
			m_boPrintGroup.setEnabled(false);
			m_bnSave.setEnabled(true);
			m_bnCancel.setEnabled(true);
			m_bnImport.setEnabled(false);
			m_bnExport.setEnabled(false);
			m_btnUpdateGjj.setEnabled(false);
			m_btnUpdateFF.setEnabled(false);
		} else if (stat == UI_STAT_EXPOTRT) {
			m_bnAdd.setVisible(false);
			m_bnAdd.setEnabled(false);
			m_boRemark.setEnabled(false);
			m_boRemark.setVisible(false);
			m_bnTempAdd.setVisible(false);
			m_bnTempAdd.setEnabled(false);
			m_bnQuery_MaintenRecord.setEnabled(false);
			m_btnBfbk.setEnabled(false);
			m_bnAdjust.setVisible(false);
			m_bnInsert.setVisible(false);
			m_bnModify.setVisible(false);
			m_bnDelete.setVisible(false);
			m_bnQuery.setVisible(false);
			m_bnRefresh.setVisible(false);
			m_boPrintGroup.setVisible(false);
			m_bnSave.setVisible(false);
			m_bnCancel.setVisible(false);
			m_bnImport.setVisible(false);
			m_bnExport.setVisible(false);
			m_bnImportData.setVisible(false);
			// m_bnDispaly.setVisible(false);

			m_bnExportData.setVisible(true);
			m_bnReturn.setVisible(true);
			m_bnExportData.setEnabled(true);
			m_bnReturn.setEnabled(true);
			m_btnUpdateGjj.setVisible(false);
			m_btnUpdateFF.setVisible(false);
		} else if (stat == UI_STAT_IMPOTRT) {
			m_bnAdd.setVisible(false);
			m_bnAdd.setEnabled(false);
			m_boRemark.setEnabled(false);
			m_boRemark.setVisible(false);
			m_bnTempAdd.setVisible(false);
			m_bnTempAdd.setEnabled(false);
			m_bnQuery_MaintenRecord.setEnabled(false);
			m_btnBfbk.setEnabled(false);
			m_bnAdjust.setVisible(false);
			m_bnInsert.setVisible(false);
			m_bnModify.setVisible(false);
			m_bnDelete.setVisible(false);
			m_bnQuery.setVisible(false);
			m_bnRefresh.setVisible(false);
			m_boPrintGroup.setVisible(false);
			m_bnSave.setVisible(false);
			m_bnCancel.setVisible(false);
			m_bnImport.setVisible(false);
			m_bnExport.setVisible(false);
			m_bnExportData.setVisible(false);
			m_bnImportData.setVisible(true);
			m_bnReturn.setVisible(true);
			// m_bnDispaly.setVisible(true);
			m_bnImportData.setEnabled(true);
			m_bnReturn.setEnabled(true);
			m_btnUpdateGjj.setVisible(false);
			m_btnUpdateFF.setVisible(false);
			// m_bnDispaly.setEnabled(true);
		}
		updateButtons();
	}

	/**
	 * 根据当前界面状态设定界面空间的编辑状态。 创建日期：(2003-3-27 20:08:38)
	 * 
	 * @param stat
	 *            int
	 */
	@Override
	public void setPanelStat(int stat) {
	}

	/**
	 * 编辑后事件。 创建日期：(2001-3-23 2:02:27)
	 * 
	 * @param e
	 *            ufbill.BillEditEvent
	 */
	public void afterEdit(nc.ui.pub.bill.BillEditEvent e) {
		e.getKey();
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-9 13:53:26)
	 */
	public void beforeInit() {
		String pk_corp = nc.ui.hr.global.Global.getCorpPK();
		String userID = nc.ui.hr.global.Global.getUserID();

		try {
			if (getNodeFlag() == UI_MGR_SELF) {
				String pk_deptdoc = nc.ui.hr.global.Global.getPsnProp()
				.getDeptdocVO().getPk_deptdoc();
				DeptdocVO[] vos = DeptdocBO_Client.queryAllchildern(pk_deptdoc);
				deptPower = " bd_psndoc.pk_deptdoc in ('" + pk_deptdoc + "'";
				if (vos != null) {
					for (DeptdocVO vo : vos) {
						deptPower += ",'" + vo.getPk_deptdoc() + "'";
					}
				}
				deptPower += ")";
			} else {
				if (nc.ui.hr.global.GlobalTool.isUsedDataPower("bd_deptdoc",
						pk_corp)) {
					String powerSql = nc.ui.hr.global.GlobalTool.getPowerSql(
							"bd_deptdoc", userID, pk_corp);
					if (powerSql != null && powerSql.length() > 0) {
						deptPower = " bd_psndoc.pk_deptdoc in (" + powerSql
						+ ")";

					} else {
						deptPower = " 0>1";
					}

				}
			}
		} catch (Exception e) {
			nc.bs.logging.Logger.error("get deptpower error:" + e.getMessage());
		}
	}

	/**
	 * 行改变事件。 创建日期：(2001-3-23 2:02:27)
	 * 
	 * @param e
	 *            ufbill.BillEditEvent
	 */
	public void bodyRowChange(nc.ui.pub.bill.BillEditEvent e) {

		int rowCount = getPsnWadocMainPane().getBillScrollPane().getTable()
		.getRowCount();
		if (rowCount <= 0) { // 总行数小于等于一，不可移动
		} else if (rowCount > 0) { // 总行数大于零
			currentSelectRow = e.getRow();// getReportTemplate().getBillTable().getSelectedRow();
			getPsnWadocSubPane().setState(PsnWadocSubPane.UNKNOWN_STATE);

			PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
					currentSelectRow);

			if (vo != null) {
				setSubVOs(vo);
				getPsnWadocSubPane().setMainVO(vo);
				if (this.currentstat == UI_STAT_EXPOTRT) {
					setBtnsStatWithUIStat(UI_STAT_EXPOTRT);
				} else if (this.currentstat == UI_STAT_IMPOTRT) {
					setBtnsStatWithUIStat(UI_STAT_IMPOTRT);
				} else {
					setBtnsStatWithUIStat(-1);
				}
			}
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-5-13 19:52:04)
	 * 
	 * @return nc.vo.hi.pub.FldreftypeVO[]
	 */
	public nc.vo.hi.pub.FldreftypeVO[] getFldreftypeVOs() {
		try {
			if (fldreftypeVOs == null) {
				ISetdict iSetdict = (ISetdict) NCLocator.getInstance().lookup(
						ISetdict.class.getName());
				fldreftypeVOs = iSetdict.queryAllRefType(nc.ui.hr.global.Global
						.getCorpPK());
			}
		} catch (Exception e) {
			fldreftypeVOs = null;
			reportException(e);
		}
		return fldreftypeVOs;
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

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-21 16:32:35)
	 * 
	 * @return int
	 */
	public int getNodeFlag() {
		return UI_MAIN;
	}

	/**
	 * 返回 PsnWadocMainPane1 特性值。
	 * 
	 * @return nc.ui.hi.hi_307.PsnWadocMainPane
	 */
	/* 警告：此方法将重新生成。 */
	private PsnWadocMainPane getPsnWadocMainPane() {
		if (ivjPsnWadocMainPane == null) {
			try {
				ivjPsnWadocMainPane = new PsnWadocMainPane();
				ivjPsnWadocMainPane.setName("PsnWadocMainPane");
				ivjPsnWadocMainPane.setPreferredSize(new java.awt.Dimension(
						100, 200));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPsnWadocMainPane;
	}

	/**
	 * 返回 PsnWadocSubPane1 特性值。
	 * 
	 * @return nc.ui.hi.hi_307.PsnWadocSubPane
	 */
	/* 警告：此方法将重新生成。 */
	private PsnWadocSubPane getPsnWadocSubPane() {
		if (ivjPsnWadocSubPane == null) {
			try {
				ivjPsnWadocSubPane = new XhPsnWadocSubPane(this);
				ivjPsnWadocSubPane.setName("PsnWadocSubPane");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPsnWadocSubPane;
	}

	/**
	 * 获得查询对话框。
	 * 
	 * 创建日期：(2004-6-5 16:38:40)
	 * 
	 * @return QueryConditionDLG
	 */
	public QueryConditionDLG getQueryDlg() {
		try {
			if (queryDlg == null) {
				queryDlg = new QueryConditionDlgBuilder()
				.createQueryConditionDLG(this);
				queryDlg.setFieldValueEditor(this);

				queryDlg.setMultiTable(true);
				queryDlg
				.registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
					public void processQueryTempletTotalVO(
							QueryTempletTotalVO totalVO) {
						if (totalVO == null
								|| totalVO.getConditionVOs() == null) {
							return;
						}
						QueryConditionVO[] conds = totalVO
						.getConditionVOs();
						for (QueryConditionVO queryConditionVO : conds) {
							if (queryConditionVO.getFieldCode().equals(
							"bd_psndoc.psnname")) {// 5.5后期，
								// 发现ReturnType不对。不想改数据库了。
								queryConditionVO.setReturnType(1);
							}
							if (queryConditionVO.getFieldCode().equals(
							"bd_psndoc.psncode")) {
								queryConditionVO
								.setFieldCode("bd_psndoc.pk_psndoc");
							}
						}
					}
				});
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(NCLangRes.getInstance().getStrByID("60130715",
			"UPP60130715-000005")/*
			 * @res "查询模板初始化错误！"
			 */);
		}
		return queryDlg;
	}

	/**
	 * 初始化表格。 创建日期：(2004-6-5 11:49:40)
	 */
	public void initTable() {
		try {
			WaItemVO[] items = WADelegator.getWaPsndoc().queryAllItemForWadoc(
					Global.getCorpPK());
			getPsnWadocMainPane().initTable(items);
			getPsnWadocMainPane().getBillScrollPane().addEditListener(this);

		} catch (Exception e) {
			reportException(e);
			showErrorMessage(NCLangRes.getInstance().getStrByID("60130715",
			"UPP60130715-000006")/*
			 * @res "表格初始化失败！"
			 */);
		}
	}

	/**
	 * 打印。 创建日期：(2004-6-5 14:10:14)
	 */
	public void onPrintDirect() {
		try {
			getPsnWadocSubPane().onPrintDetailDirect();
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 打印。 创建日期：(2004-6-5 14:10:14)
	 */
	public void onPrintBytemplate() {
		try {
			getPsnWadocSubPane().onPrintBytemplate();
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 正常打印
	 * @throws Exception 
	 */
	@SuppressWarnings("restriction")
	public void onPrintNomal() throws Exception {
		/*try {
			getPsnWadocSubPane().onPrintByNomal();
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
		*/
		
		cardPrint(MyListPRTS.NOMAL);
		
	}

	/**
	 * 卡片方式批量打印
	 * @param type
	 * @throws Exception
	 */
	private void cardPrint(int type) throws Exception {
//		String stWherePart = QueryConditionDlgUtils.getStWherePart(getQueryDlg());
//		if (getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount() > 0) {
//			getPsnWadocMainPane().getBillScrollPane().getTableModel().getDataVector();
//		}
		if (isQuery || isQuickSearch) {
			PsndocWadocMainVO[] mainVOs = null;
			if (isQuery && queryDlg != null) {
				mainVOs = getWadocMain(nc.ui.hr.global.Global.getCorpPK(),
						getQueryDlg());
			} else if (isQuickSearch) {
				mainVOs = getWadocMain(nc.ui.hr.global.Global.getCorpPK(),
						quickWherePart, null, null, null);
			}
			/**************************** add xieye 2018-12-26 21:00:00 start ****************************/
			HashMap<String, String> paramMap = new HashMap<String, String>();
			ParamDialog pd = new ParamDialog(this, "打印参数设置", paramMap);
			
			if(pd.showModal() == UIDialog.ID_OK ){
				IDataSource dataSource = null;
				PrintEntry print = new PrintEntry(this, null);
				for (PsndocWadocMainVO psndocWadocMainVO : mainVOs) {
					setSubVOs(psndocWadocMainVO);
					dataSource = new MyListPRTS(new PsndocWadocMainVO[]{psndocWadocMainVO}, paramMap, type);
					print.setDataSource(dataSource);
				}
//				 设定数据模板
				print.setTemplateID(Global.getCorpPK(), this.getModuleCode(), Global.getUserID(), null);
				print.getTemplate().setPagination(4);
				if (print.selectTemplate() >= 0) {
					print.preview();
				}
				/**************************** add xieye 2018-12-26 21:00:00 end ****************************/
			}
			
			
			
//			for (PsndocWadocMainVO psndocWadocMainVO : mainVOs) {
//				setSubVOs(psndocWadocMainVO);
//			}
//			HashMap<String, String> paramMap = new HashMap<String, String>();
//			ParamDialog pd = new ParamDialog(this, "打印参数设置", paramMap);
//			pd.showModal();
//			IDataSource dataSource = null;
//			dataSource = new MyListPRTS(mainVOs, paramMap, type);
//			PrintEntry print = new PrintEntry(this, dataSource);
//			// 设定数据模板
//			print.setTemplateID(Global.getCorpPK(), this.getModuleCode(), Global.getUserID(), null);
//			print.getTemplate().setPagination(4);
//			if (print.selectTemplate() >= 0) {
//				print.preview();
//			}
		} else {
			showErrorMessage("请先查询打印数据");
		}
	}
	
	/**
	 * 岗位变动打印
	 */
	@SuppressWarnings("restriction")
	public void onPrintGwbd() throws Exception {
//		try {
//			getPsnWadocSubPane().onPrintByGwbd();
//		} catch (Exception e) {
//			showErrorMessage(e.getMessage());
//		}
		cardPrint(MyListPRTS.GWBG);
	}
	
	/**
	 * 新录用打印
	 */
	@SuppressWarnings("restriction")
	public void onPrintNewLy() throws Exception {
		/*try {
			getPsnWadocSubPane().onPrintByNewLy();
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
		}*/
		cardPrint(MyListPRTS.NEWLY);
	}
	
	/**
	 * 其它打印
	 */
	@SuppressWarnings("restriction")
	public void onPrintOther() throws Exception {
		/*try {
			getPsnWadocSubPane().onPrintByOther();
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
		}*/
		cardPrint(MyListPRTS.OTHER);
	}
	
	/**
	 * 查询。 创建日期：(2004-6-5 14:09:58)
	 */
	public void onQuery() {
		try {
			getQueryDlg().showModal();
			if (getQueryDlg().getResult() == UIDialog.ID_OK) {
				// update by sunxj 2010-02-23 快速查询插件 start
				isQuery = true;
				isQuickSearch = false;
				// update by sunxj 2010-02-23 快速查询插件 end
				getPsnWadocMainPane().setWadocData(null);
				PsndocWadocMainVO[] mainVOs = getWadocMain(Global.getCorpPK(),getQueryDlg());
				
				// zhanghua
				if (mainVOs != null && mainVOs.length > 0) {
					for(int i=0;i<mainVOs.length;i++){
						PsndocWadocb2VO headvo = getPsndocWadocb2VO(mainVOs[i].getPk_psndoc());
						if(headvo != null){
							mainVOs[i].setRemark(headvo.getRemark());
						}
						
					}
				}
				getPsnWadocMainPane().setWadocData(mainVOs);
				if (mainVOs != null && mainVOs.length > 0) {
					getPsnWadocMainPane().getBillScrollPane().getTable()
					.addRowSelectionInterval(0, 0);
					getPsnWadocMainPane().getBillScrollPane().getTable()
					.addColumnSelectionInterval(0, 0);
				} else {
					getPsnWadocSubPane().setWadocData(null);
					setBtnsStatWithUIStat(-1);
				}

			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(NCLangRes.getInstance().getStrByID("60130715",
			"UPP60130715-000007")/*
			 * @res "查询出错!"
			 */);
		}
	}

	 public PsndocWadocb2VO getPsndocWadocb2VO(String pk_psndoc) throws BusinessException{
	    	IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			String sql="select * from hi_psndoc_wadoc_b2 where isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"'";
			PsndocWadocb2VO wadocb2VO = (PsndocWadocb2VO) service.executeQuery(sql, new BeanProcessor(PsndocWadocb2VO.class)); 
	    	return wadocb2VO;
	    }
	 
	private PsndocWadocMainVO[] getWadocMain(String corpPK,
			QueryConditionDLG queryConditionDLG) throws BusinessException {

		String strWhere = "";
		if (deptPower.length() > 0) {
			strWhere += " and " + deptPower;
		}

		strWhere = QueryConditionDlgUtils.getStWherePart(queryConditionDLG,
				null, strWhere);

		HashSet<String> hashSet = QueryConditionDlgUtils
		.getSqlConditoinTableCodes(queryConditionDLG, null);
		List<String> linkedList = new LinkedList<String>();
		for (String tableName : hashSet) {
			linkedList.add(tableName);
		}
		
		querywhere = strWhere;// zhanghua 
		return WADelegator.getWaPsndoc().queryWadocMainDataMorePsnclscope(corpPK, strWhere,
				linkedList, null, null);

	}

	/**
	 * 刷新。 创建日期：(2004-6-5 14:10:06)
	 */
	public void onRefresh() {
		try {

			getPsnWadocSubPane().getBillScrollPane().getTable()
			.clearSelection();

			getPsnWadocMainPane().setWadocData(null);
			getPsnWadocSubPane().setWadocData(null);
			getPsnWadocMainPane().getBillScrollPane().removeEditListener();
			initTable();
			// delete by sunxj 2010-02-23 快速查询插件 start
			// if (queryDlg != null) {
			// PsndocWadocMainVO[] mainVOs =
			// getWadocMain(nc.ui.hr.global.Global.getCorpPK(),getQueryDlg());
			// getPsnWadocMainPane().setWadocData(mainVOs);
			// if (mainVOs != null && mainVOs.length > 0) {
			// int selectRow = mainVOs.length >= currentSelectRow ?
			// currentSelectRow : 0;
			// getPsnWadocMainPane().getBillScrollPane().getTable().addRowSelectionInterval(selectRow,
			// selectRow);
			// getPsnWadocMainPane().getBillScrollPane().getTable().addColumnSelectionInterval(0,
			// 0);
			// } else {
			// getPsnWadocSubPane().setWadocData(null);
			// }
			//
			// setBtnsStatWithUIStat(-1);
			// }
			// delete by sunxj 2010-02-23 快速查询插件 end
			// add by sunxj 2010-02-23 快速查询插件 start
			if (isQuery || isQuickSearch) {
				PsndocWadocMainVO[] mainVOs = null;
				if (isQuery && queryDlg != null) {
					mainVOs = getWadocMain(nc.ui.hr.global.Global.getCorpPK(),
							getQueryDlg());
				} else if (isQuickSearch) {
					mainVOs = getWadocMain(nc.ui.hr.global.Global.getCorpPK(),
							quickWherePart, null, null, null);
				}
				
				// zhanghua
				if (mainVOs != null && mainVOs.length > 0) {
					for(int i=0;i<mainVOs.length;i++){
						PsndocWadocb2VO headvo = getPsndocWadocb2VO(mainVOs[i].getPk_psndoc());
						if(headvo != null){
							mainVOs[i].setRemark(headvo.getRemark());
						}
						
					}
				}
				
				getPsnWadocMainPane().setWadocData(mainVOs);

				if (mainVOs != null && mainVOs.length > 0) {
					int selectRow = mainVOs.length >= currentSelectRow ? currentSelectRow
							: 0;
					getPsnWadocMainPane().getBillScrollPane().getTable()
					.addRowSelectionInterval(selectRow, selectRow);
					getPsnWadocMainPane().getBillScrollPane().getTable()
					.addColumnSelectionInterval(0, 0);
				} else {
					getPsnWadocSubPane().setWadocData(null);
				}

				setBtnsStatWithUIStat(-1);
			}
			// add by sunxj 2010-02-23 快速查询插件 end
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示选中人员的所有薪资变动情况。 创建日期：(2004-6-9 19:31:55)
	 */
	public void setSubVOs(PsndocWadocMainVO vo) {
		try {
			PsndocWadocVO[] subvos = WADelegator.getWaPsndoc().queryAllVOsByPsnPKForHI(vo.getPk_psndoc());

			if (subvos == null) {
				getPsnWadocSubPane().setWadocData(subvos);
				return;
			}
			//add by suhf1 
			List<PsndocWadocVO> list = new ArrayList<PsndocWadocVO>();

			for (PsndocWadocVO subvo : subvos) {
				subvo.setDeptCode(vo.getDeptCode());
				subvo.setDeptName(vo.getDeptName());
				subvo.setPsnCode(vo.getPsnCode());
				subvo.setPsnName(vo.getPsnName());
				if(itemstatus == 0){
					list.add(subvo);
				}else if(itemstatus == 1){
					if(subvo.getLastflag() != null && subvo.getLastflag().booleanValue() == true){
						list.add(subvo);
					}
				}else if(itemstatus == 2){
					if(refpk != null && subvo.getPk_wa_item().equals(refpk)){
						list.add(subvo);
					}
				}
			}
			if(list.size() >= 0){
				getPsnWadocSubPane().setWadocData(list.toArray(new PsndocWadocVO[0]));//list.toArray(new PsndocWadocVO[0])
			}
			getPsnWadocSubPane().setPsnname(vo.getPsnName());
			getPsnWadocSubPane().setDeptname(vo.getDeptName());
			getPsnWadocSubPane().setPk_psndoc(vo.getPk_psndoc());
			getPsnWadocSubPane().setDeptCode(vo.getDeptCode());
			getPsnWadocSubPane().setPsnCode(vo.getPsnCode());
			vo.setSubVOs(subvos);
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60130715", "UPP60130715-000008")/*
					 * @res
					 * "显示人员薪资变动情况详细信息出错！"
					 */);
		}
	}

	public JComponent createFieldValueEditor(FilterMeta filterMeta) {
		// TODO Auto-generated method stub
		if (filterMeta.getFieldCode().equalsIgnoreCase("bd_psndoc.pk_psndoc")) {
			UIRefPane psndocref = new UIRefPane();
			psndocref.setRefNodeName("人员档案");
			psndocref.getRefModel().setUseDataPower(false);
			return psndocref;
		}

		if (filterMeta.getFieldCode().equalsIgnoreCase("bd_psndoc.pk_om_job")) {
			JobRef jobmodel = new JobRef();
			String where = jobmodel.getWherePart();

			if (deptPower.trim().startsWith("bd_psndoc")) {
				where += " and om_job"
					+ deptPower.substring("bd_psndoc ".length());
			} else {
				where += " and (" + deptPower + ")";
			}

			jobmodel.setWherePart(where);
			jobmodel.setRefTitle(NCLangRes.getInstance().getStrByID("60130715",
			"UPP60130715-000004")/*
			 * @res "岗位参照"
			 */);
			UIRefPane jobref = new UIRefPane();
			jobref.setRefType(1);
			jobref.setRefInputType(1);
			jobref.setRefModel(jobmodel);
			return jobref;
		}

		if (filterMeta.getFieldCode().equalsIgnoreCase("bd_psndoc.pk_psncl")) {
			// 设置人员类别显示已封存数据
			UIRefPane psnclsref = new UIRefPane();
			psnclsref.setRefType(1);
			psnclsref.setRefInputType(1);
			psnclsref.setRefNodeName("人员类别");
			psnclsref.getRefModel().setSealedDataShow(true);
			return psnclsref;
		}

		try {
			return new HiInfoForQuery().getWaBmQueryValueRef(filterMeta
					.getFieldCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getRefPanelWherePart(FilterMeta filterMeta) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNeedPrompt() throws BusinessException {
		return false;
	}

	public String quickSearch(String psnWherePart, SearchType searchType)
	throws BusinessException {
		try {
			// 判断是否可以执行查询
			String powerStr = QsbUtil.checkSearchPower(this, m_bnQuery, true);
			if (powerStr != null) {
				return powerStr;
			}
			isQuery = false;
			isQuickSearch = true;
			quickWherePart = psnWherePart;

			getPsnWadocMainPane().setWadocData(null);
			ArrayList reList = new ArrayList();
			PsndocWadocMainVO[] mainVOs = getWadocMain(Global.getCorpPK(),
					psnWherePart, reList, null, null);
			getPsnWadocMainPane().setWadocData(mainVOs);
			if (mainVOs != null && mainVOs.length > 0) {
				getPsnWadocMainPane().getBillScrollPane().getTable()
				.addRowSelectionInterval(0, 0);
				getPsnWadocMainPane().getBillScrollPane().getTable()
				.addColumnSelectionInterval(0, 0);
			} else {
				getPsnWadocSubPane().setWadocData(null);
				setBtnsStatWithUIStat(-1);
			}
			// 返回未成功加载人数
			if (reList.size() > 0) {
				return (reList.get(0)).toString();
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(NCLangRes.getInstance().getStrByID("60130715",
			"UPP60130715-000007")/*
			 * @res "查询出错!"
			 */);
		}
		return null;
	}

	private PsndocWadocMainVO[] getWadocMain(String corpPK,
			String psnWherePart, ArrayList reList, String strPkWaItem,
			String strPkWaGrd) throws BusinessException {
		String strWhere = "";
		if (deptPower.length() > 0) {
			strWhere += deptPower;
		}
		strWhere = strWhere + " and bd_psndoc.pk_psndoc in (" + psnWherePart
		+ ")";
		PsndocWadocMainVO[] mainVOs = WADelegator.getWaPsndoc()
		.queryWadocMainData(corpPK, strWhere, null, strPkWaItem,
				strPkWaGrd);
		return QsbUtil.orderbyResult(psnWherePart, PsndocWadocMainVO.class,
				mainVOs, "pk_psndoc", reList);
	}
	/**
	 * 维护记录 确定
	 */
	private void confirm(){
		getUIdialog().closeOK();
		if(getUIRadioAllRecordsRB().isSelected() == true){
			itemstatus = 0;
		}else if(getUIRadioLatestRecordsRB().isSelected() == true){
			itemstatus =1;
		}else{
			itemstatus =2;
			reftext = getUIRefWa_item().getText();
			refpk = getUIRefWa_item().getRefPK();
		}
		int rows = getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount();
		if(rows > 0){
			int selectedRow = getPsnWadocMainPane().getBillScrollPane().getTable().getSelectedRow();
			getPsnWadocMainPane().getBillScrollPane().getTable().clearSelection();
			getPsnWadocMainPane().getBillScrollPane().getTable().addRowSelectionInterval(selectedRow, selectedRow);
			getPsnWadocMainPane().getBillScrollPane().getTable().addColumnSelectionInterval(0, 0);
		}

	}
//	维护记录 的状态 0，默认状态；1，最新薪资项目；2，某一个薪资项目
	private int itemstatus = 0;
	private String reftext = null;
	private String refpk = null;
	public void actionPerformed(ActionEvent arg0)  {
		//add by xhhrp suhf1 
		if(arg0.getSource() == getUIRadioAllRecordsRB()){
			getUIRefWa_item().setText("");
			getUIRefWa_item().setVisible(false);
		}else if(arg0.getSource() == getUIRadioCertainRecordRB()){
			getUIRefWa_item().setVisible(true);
		}else if(arg0.getSource() == getUIRadioLatestRecordsRB()){
			getUIRefWa_item().setText("");
			getUIRefWa_item().setVisible(false);
		}else if(arg0.getSource() == getUIButtonConfrim()){
			confirm();
		}else if(arg0.getSource() == getUIButtonCancle()){
			getUIdialog().closeCancel();
			if(itemstatus == 0){
				getUIRadioAllRecordsRB().setSelected(true);
				getUIRefWa_item().setText("");
				getUIRefWa_item().setVisible(false);
			}else if(itemstatus == 1){
				getUIRadioLatestRecordsRB().setSelected(true);
				getUIRefWa_item().setText("");
				getUIRefWa_item().setVisible(false);
			}else{
				getUIRadioCertainRecordRB().setSelected(true);
				getUIRefWa_item().setVisible(true);
				getUIRefWa_item().setText(reftext);
				getUIRefWa_item().setPK(refpk);
			}
		}else {
			String buttonname = ((UIButton) arg0.getSource()).getName();
			if (buttonname.equalsIgnoreCase("UIBnBrowser")) {
				getUIFileChooser().showOpenDialog(this);
				String str = getUIFileChooser().getSelectedFile() == null ? ""
						: getUIFileChooser().getSelectedFile().getPath();
				if (str != null && str.length() != 0) {
					String mm = getUIFileChooser().getFileFilter().getDescription();
					if (!str.toUpperCase().endsWith("XLS")) {
						if (mm.equals(ResHelper.getString("60130715",
						"UPP60130715-000066")// @res "Excel文件(*.xls)"
						)) {
							str = str + ".xls";
						}
					}
					if (str != null && str.length() != 0)
						gettfFileName().setText(str);
				}
			} else {
				getUIFileChooserim().showOpenDialog(this);
				String str = getUIFileChooserim().getSelectedFile() == null ? ""
						: getUIFileChooserim().getSelectedFile().getPath();
				if (str != null && str.length() != 0) {
					String mm = getUIFileChooserim().getFileFilter()
					.getDescription();
					if (!str.toUpperCase().endsWith("XLS")) {
						if (mm.equals(ResHelper.getString("60130715",
						"UPP60130715-000066")// @res "Excel文件(*.xls)"
						)) {
							str = str + ".xls";
						}
					}
					if (str != null && str.length() != 0)
						gettfFileNameim().setText(str);
				}
			}
		}
	}

	private void onBatchAdjust() {
		try {
			new BatchAdjustAction(this).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 薪资普调查询<BR>
	 * 薪资普调是在薪资信息维护的查询基础上进行操作，所以这里借用信息维护的查询对象再次查询，相当于刷新操作<BR>
	 * 
	 * @param strPkWaItem
	 * @param strPkWaGrd
	 * @return
	 * @throws BusinessException
	 */
	public AdjustWadocVO[] onRefresh4BatchAdjust(BatchAdjustVO batchadjustVO)
	throws BusinessException {
		if (queryDlg != null) {
			return getWadocMain4BatchAdjust(nc.ui.hr.global.Global.getCorpPK(),
					getQueryDlg(), batchadjustVO);
		}

		return null;
	}

	/**
	 * 薪资普调
	 * 
	 * @param corpPK
	 * @param queryConditionDLG
	 * @param strPkWaItem
	 * @param strPkWaGrd
	 * @return
	 * @throws BusinessException
	 */
	private AdjustWadocVO[] getWadocMain4BatchAdjust(String corpPK,
			QueryConditionDLG queryConditionDLG, BatchAdjustVO batchadjustVO)
	throws BusinessException {

		String strWhere = "";
		if (deptPower.length() > 0) {
			strWhere += " and " + deptPower;
		}

		strWhere = QueryConditionDlgUtils.getStWherePart(queryConditionDLG,
				null, strWhere);

		HashSet<String> hashSet = QueryConditionDlgUtils
		.getSqlConditoinTableCodes(queryConditionDLG, null);
		List<String> linkedList = new LinkedList<String>();
		for (String tableName : hashSet) {
			linkedList.add(tableName);
		}
		return WADelegator.getWaPsndoc().queryWadocMainData4AdjustInfo(corpPK,
				strWhere, linkedList, batchadjustVO);

	}

	/**
	 * 设置顶部Panel的只读属性
	 * 
	 * @param flag
	 */
	public void setTopPanelProperty(boolean flag) {
		getPsnWadocMainPane().getBillScrollPane().getTable().setEnabled(flag);
		getPsnWadocMainPane().getBillScrollPane().getTable().setSortEnabled(
				flag);
	}

	private BfbkSetDialog m_bfbkDlg = null;

	private BfbkSetDialog getBfbkSetDlg(){
		m_bfbkDlg = new BfbkSetDialog(this);
		return m_bfbkDlg;
	}

	private void onBfbkSet(){
		if(getPsnWadocSubPane().getState() == PsnWadocSubPane.ADD_STATE || getPsnWadocSubPane().getState() == PsnWadocSubPane.MODIFY_STATE){
			showWarningMessage("请先保存数据");
			return;
		}

		int rows = getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount();
		if(rows == 0){
			showWarningMessage("未选中人员信息");
			return;

		}
		if(currentSelectRow < 0){
			showWarningMessage("未选中人员信息");
			return;
		}
		PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
				currentSelectRow);
		BfbkSetDialog dlg = getBfbkSetDlg();
		dlg.setPsnInfor(vo);
		dlg.showModal(); 
	}

	private PsnViewDialog m_psnViewDlg = null;
	private PsnViewDialog getPsnViewDlg(){
		m_psnViewDlg = new PsnViewDialog(this);
		return m_psnViewDlg;
	}
	private void onPsnView(){

		if(getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount() < 1){
			showWarningMessage("请选中一条人员");
			return;
		}
		if(currentSelectRow < 0){
			showWarningMessage("请选中一条人员");
			return;
		}
		PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
				currentSelectRow);
		if(vo == null){
			showWarningMessage("请选中一条人员");
			return;
		}
		if(vo.getPk_psndoc() == null){
			showWarningMessage("请选中一条人员");
			return;
		}
		PsnViewDialog dlg = getPsnViewDlg();
		dlg.initData(vo.getPk_psndoc());
		dlg.showModal();
	}

	
}