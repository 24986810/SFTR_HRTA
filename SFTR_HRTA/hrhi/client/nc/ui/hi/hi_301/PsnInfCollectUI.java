package nc.ui.hi.hi_301;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.bs.uap.lock.PKLock;
import nc.itf.hi.HIDelegator;
import nc.itf.hi.IPsnInf;
import nc.itf.hr.bd.ICorpWorkout;
import nc.itf.hr.comp.IAttachment;
import nc.itf.hr.pub.PubDelegator;
import nc.itf.hrp.hrhi01.IUserChildInfoAuth;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.IVOPersistence;
import nc.itf.uap.bd.corp.ICorpQry;
import nc.itf.uap.bd.def.IDefdoc;
import nc.itf.uap.bd.psn.IPsncl;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.bd.ref.AbstractRefDialog;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.DefaultRefTreeModel;
import nc.ui.bd.ref.busi.PsndocDefaulRefModel;
import nc.ui.hi.hi_301.ref.PsnClsRef;
import nc.ui.hi.hi_301.trigger.IAccessible;
import nc.ui.hi.hi_301.trigger.ITriggerListener;
import nc.ui.hi.hi_301.trigger.TriggerEvent;
import nc.ui.hi.hi_301.trigger.TriggerTable;
import nc.ui.hi.hi_302.AbilityMatchDialog;
import nc.ui.hi.hi_302.BatchCancelAffirmDlg;
import nc.ui.hi.hi_302.UIFileChooserForOutport;
import nc.ui.hi.hi_303.CardBrowseMainPanel;
import nc.ui.hi.hi_303.CardReportViewFrame;
import nc.ui.hi.hi_306.DocLinkAddData;
import nc.ui.hi.ref.DeptRefModel;
import nc.ui.hi.ref.DutyRef;
import nc.ui.hi.ref.JobRef;
import nc.ui.hi.ref.SpaDeptdocRefGridTreeModel;
import nc.ui.hi.ref.SpaDeptdocRefTreeModel;
import nc.ui.hr.bd.setdict.HRResHiDictHelper;
import nc.ui.hr.comp.attachment.AttachmentDialog;
import nc.ui.hr.comp.combinesort.Attribute;
import nc.ui.hr.comp.combinesort.SortConfigDialog;
import nc.ui.hr.comp.quicksearch.IQuickSearch;
import nc.ui.hr.comp.quicksearch.QsbUtil;
import nc.ui.hr.comp.quicksearch.SearchType;
import nc.ui.hr.frame.HrQueryDialog;
import nc.ui.hr.frame.IQueryFieldValueEditor;
import nc.ui.hr.global.Global;
import nc.ui.hr.global.GlobalTool;
import nc.ui.hr.pub.carddef.HrCardReportViewPanel;
import nc.ui.hr.pub.carddef.word.RefDataTypeHelper;
import nc.ui.hr.pub.carddef.word.WordUtil;
import nc.ui.hr.tools.trans.FileTransClient;
import nc.ui.hrp.pub.datacache.ClientDbCache;
import nc.ui.hrp.pub.excel.ExcelOut;
import nc.ui.hrp.pub.excel.ImportExcelData;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.FuncNodeStarter;
import nc.ui.pub.IFuncWindow;
import nc.ui.pub.MenuButton;
import nc.ui.pub.MenuCheckboxItem;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIPopupMenu;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.bill.BillCardLayout;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillMouseEnent;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.pub.linkoperate.ILinkMaintain;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.pub.query.QueryConditionClient;
import nc.ui.pub.tools.BannerDialog;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.IQueryTemplateTotalVOProcessor;
import nc.ui.querytemplate.QueryTempletService;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.sm.power.FuncRegister;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uap.sf.SFClientUtil;
import nc.vo.bd.CorpVO;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.bd.def.DefdocVO;
import nc.vo.bd.psndoc.PsndocConsItmVO;
import nc.vo.hi.hi_301.CtrlDeptVO;
import nc.vo.hi.hi_301.GeneralVO;
import nc.vo.hi.hi_301.PersonEAVO;
import nc.vo.hi.hi_301.SubTable;
import nc.vo.hi.hi_301.ValidException;
import nc.vo.hi.hi_301.validator.AbstractValidator;
import nc.vo.hi.hi_301.validator.FieldValidator;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hr.bd.setdict.FlddictVO;
import nc.vo.hr.bd.setdict.SetdictVO;
import nc.vo.hr.pub.carddef.HrReportDataVO;
import nc.vo.hr.pub.carddef.RptAuthVO;
import nc.vo.hr.pub.carddef.RptDefVO;
import nc.vo.hr.pub.carddef.word.WordVO;
import nc.vo.hr.tools.pub.StringUtils;
import nc.vo.hr.validate.IDValidateUtil;
import nc.vo.hrp.hrhi02.PSNDOCEXPORTVO;
import nc.vo.om.om_013.WorkoutResultVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.msg.UserNameObject;
import nc.vo.pub.query.ConditionVO;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.rm.psndocdef.PsndocGrpDef15;
import nc.vo.scm.pattern.domain.uap.basedoc.BD_PSNDOC;
import nc.vo.sm.funcreg.FuncRegisterVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ufsoft.report.ContextVO;
import com.ufsoft.report.sysplugin.excel.ExcelExpUtil;
import com.ufsoft.table.CellsModel;

/**
 * 人员采集界面 on
 * 
 * @author ZHANGYAN
 * 
 */
public class PsnInfCollectUI extends nc.ui.hr.base.HRToftPanel implements
		ListSelectionListener, IDataSource, TableModelListener,
		BillEditListener, IQueryFieldValueEditor, ILinkMaintain, IQuickSearch {
	/**
	 * PsnInfCollectUI.java field:serialVersionUID field_type:long
	 */
	private static final long serialVersionUID = 1L;
	

	private boolean isBeginEdit = false;

	protected final int SPLIT_MAX = 1000;

	protected final int SPLIT_STANDARD = 200;

	protected boolean codeflag = true;

	public HrQueryDialog queryDialog = null;

	private UFBoolean isNeedAFirm = null;

	// magic number!!
	private final static String PSNCODE_AUTO_GENERATE = "1";

	// 只要点击过编辑按钮.
	protected boolean isUpdated = false;

	@SuppressWarnings("unchecked")
	protected Hashtable htbEdit = new Hashtable();

	@SuppressWarnings("unchecked")
	protected Hashtable htbRegularEdit = new Hashtable();

	// 人员归属范围选项,该类主要是用来实现下拉框选择项对象
	public class Item {
		// 该归属范围的类型，对应于数据库中的bd_psndoc.psnscope
		private int value;

		// 显示名称
		private String name;

		// 该归属范围对应的模板的nodekey
		private String nodeKey;

		// 改模板的beanName
		private String cardName;

		// 改范围的模板是否已经加载？
		private boolean templetLoaded;

		public void setCardName(String cn) {
			cardName = cn;
		}

		public String getCardName() {
			return cardName;
		}

		public void setTempletLoaded(boolean b) {
			templetLoaded = b;
		}

		public boolean isTempletLoaded() {
			return templetLoaded;
		}

		public void setNodeKey(String key) {
			nodeKey = key;
		}

		public String getNodeKey() {
			return nodeKey;
		}

		public void setValue(int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}

		public void setName(String n) {
			name = n;
		}

		public String getName() {
			return name;
		}

		public Item() {
		}

		public Item(int v, String key, String cn, String n) {
			setValue(v);
			setNodeKey(key);
			setCardName(cn);
			setName(n);
		}

		public String toString() {
			// 考虑国际化
			return name;
		}
	}

	// 该类用来实现触发器目标对象的触发
	class AccessibleItem implements IAccessible {
		private BillItem destItem;

		private PsnInfCollectUI mainUI;

		public AccessibleItem(BillItem billItem, PsnInfCollectUI ui) {
			destItem = billItem;
			mainUI = ui;
		}

		public BillItem getBillItem() {
			return destItem;
		}

		public BillItem getBillItem(String tablefield) {
			return mainUI.getBillItem(tablefield);
		}

		public String getSelCorpPK() {
			return mainUI.getSelCorpPK();
		}

		private void setComponentValue(Object value) {
			// 根据组件类型
			switch (destItem.getDataType()) {
			// 字符、整数、小数、时间都使用参照的textfield
			case IBillItem.STRING: // 字符
			case IBillItem.INTEGER: // 整数
			case IBillItem.DECIMAL: // 小数
			case IBillItem.TIME: // 时间
			case IBillItem.DATE: // 日期
				UIRefPane ref = (UIRefPane) getBillItem().getComponent();
				UITextField text = ref.getUITextField();
				text.setText(value == null ? "" : value.toString());
				break;
			// 参照、自定义项档案都是使用参照
			case IBillItem.UFREF: // 参照
			case IBillItem.USERDEF: // 自定义项档案
				ref = (UIRefPane) getBillItem().getComponent();
				ref.setPK(value);
				break;
			// 逻辑型使用复选框
			case IBillItem.BOOLEAN: // 逻辑
				UICheckBox checkBox = (UICheckBox) getBillItem().getComponent();
				checkBox.setSelected(value == null ? false : ((Boolean) value)
						.booleanValue());
				break;
			// 下拉框类型
			case IBillItem.COMBO: // 下拉
				UIComboBox comboBox = (UIComboBox) getBillItem().getComponent();
				comboBox.setSelectedItem(value);
				break;
			// 大文本、图片不激活触发器
			case IBillItem.TEXTAREA: // 大文本
				UITextArea area = (UITextArea) getBillItem().getComponent();
				area.setText(value == null ? "" : value.toString());
				break;
			case IBillItem.IMAGE: // 图片
			case IBillItem.OBJECT: // 图片
				break;
			}
		}

		private void setTableValue(Object value) {
			BillModel model = getCard().getBillModel();
			int row = getEditLineNo();
			model.setValueAt(value, row, destItem.getKey());
		}

		public void setValue(Object value) {
			if (destItem.getPos() == IBillItem.HEAD) {
				setComponentValue(value);
			} else {
				setTableValue(value);
			}
		}
	}

	/**
	 * 快查选项菜单 <br>
	 * Create Date:2010-4-20
	 * 
	 * @author caizl
	 */
	public class QuickMenuBar extends MenuButton {

		private static final long serialVersionUID = -7632237146153839773L;

		private UIPopupMenu quickMenu = new UIPopupMenu();

		private List<MenuCheckboxItem> checkList = new ArrayList<MenuCheckboxItem>();

		public List<MenuCheckboxItem> getCheckList() {
			return checkList;
		}

		public void setCheckList(List<MenuCheckboxItem> checkList) {
			this.checkList = checkList;
		}

		public QuickMenuBar() {
			super("▼");
			setPreferredSize(new Dimension(14, 13));
			setBackground(new Color(236, 244, 251));
			setPopupMenu(quickMenu);
			initMenuItem();

			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mouseevent) {
					quickMenu.show(QuickMenuBar.this, 1, 13);
				}
			});
		}

		private void initMenuItem() {
			IDefdoc iDefdoc = NCLocator.getInstance().lookup(IDefdoc.class);
			try {
				DefdocVO[] defdocVOs = iDefdoc
						.queryByWhere(" pk_defdoclist in (select pk_defdoclist from bd_defdoclist where doclistcode='HR2KC') order by pk_defdoc desc");
				MenuCheckboxItem menuCheckboxItem = null;
				if (defdocVOs == null)
					return;
				for (int i = 0; i < defdocVOs.length; i++) {
					menuCheckboxItem = new MenuCheckboxItem(defdocVOs[i]
							.getDocname());
					menuCheckboxItem
							.setActionCommand(defdocVOs[i].getDoccode());
					menuCheckboxItem.addActionListener(new MenuActionListener(
							quickMenu, menuCheckboxItem));
					quickMenu.add(menuCheckboxItem);
					if (defdocVOs[i].getDoccode().endsWith("psncode")
							|| defdocVOs[i].getDoccode().endsWith("psnname")) {
						menuCheckboxItem.setSelected(true);
					}
					checkList.add(menuCheckboxItem);
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private class MenuActionListener implements ActionListener {

		private JPopupMenu jPopupMenu;

		// private JCheckBoxMenuItem jCheckBoxMenuItem;

		public MenuActionListener(JPopupMenu jPopupMenu,
				JCheckBoxMenuItem jCheckBoxMenuItem) {
			this.jPopupMenu = jPopupMenu;
			// this.jCheckBoxMenuItem = jCheckBoxMenuItem;
		}

		public void actionPerformed(ActionEvent actionevent) {
			jPopupMenu.setVisible(true);
			// jCheckBoxMenuItem.setBorderPainted(true);
		}

	}

	private QuickMenuBar quickMenuBar = new QuickMenuBar();

	protected QuickMenuBar getQuickMenuBar() {
		return quickMenuBar;
	}

	// 当前编辑内容：空
	private static final int EDIT_NONE = 0;

	// 当前编辑内容:主集
	private static final int EDIT_MAIN = 1;

	// 当前编辑内容:子集
	private static final int EDIT_SUB = 2;

	// 当前编辑内容：调整顺序
	private static final int EDIT_SORT = 3;

	// V35 add 人员引用的编辑状态
	private static final int EDIT_REF = 4;

	// v53 add 关键人员增加编辑状态
	// protected static final int EDIT_KEYPSN = 5;
	// v53 add 返聘人员编辑状态
	protected static final int EDIT_RETURN = 6;

	// 增加人员
	protected boolean adding;

	// 当前编辑内容
	protected int editType;

	// 当前编辑行
	protected int editLineNo;

	// 当前是否正在编辑行
	protected boolean editLine;

	// 人员采集自动编码, 自动生成还是手工输入
	private String psncodeAutoGenerate = null;

	// 单据编码VO
	private nc.vo.pub.billcodemanage.BillCodeObjValueVO objBillCodeVO = null;

	private GeneralVO[] subBackupVOs = null;// wangkf add 用来保存子集的信息。

	@SuppressWarnings("unchecked")
	private Vector vBookConds = new Vector();// wangkf add 用来保存新增加的人员主键。

	protected int intodocdirect = 0;// add by zhyan v53 转入人员档案参数0：直接转入，1：入职审批

	protected boolean caneditkeypsn = false;// add by zhyan v53

	// 是否允许在人员信息维护中修改关键人员信息

	protected int prounit = 1;// add by zhyan v502 合同试用期限 1:月 0:天

	protected int conunit = 1;// add by zhyan v502 合同期限 1:月 0:天 合同子集

	protected int CARD_ADD = 2000;

	protected int CARD_EDIT = 2001;

	protected int CARD_MAIN_BROWSE = 20021;

	protected int CARD_CHILD_BROWSE = 20022;

	protected int LIST_BROWSE = 2003;

	protected int LIST_INIT = 20031;

	protected int LIST_GROUP = 2004;

	protected int LIST_INIT_GROUP = 20041;

	protected int CARD_GROUP = 2005;

	protected int EMPLOYEE_REF_LIST = 2006;

	protected int EMPLOYEE_REF_CARD = 2007;

	protected int EMPLOYEE_REF_EDIT = 20061;

	protected int EMPLOYEE_REF_CARD_BROWSE = 20062;

	protected int EMPLOYEE_REF_LIST_BROWSE = 20063;

	// 关键人员状态
	protected int KEYPSN_LIST = 2008;

	protected int KEYPSN_CARD = 200801;

	protected int KEYPSN_CARD_EDIT = 200802;

	// 业务子集编辑
	protected int CARD_EDIT_SUB_TRACEABLE = 20010101;

	// 非业务子集编辑
	protected int CARD_EDIT_SUB_UNTRACEABLE = 20010101;

	//
	private Vector<String> vDelPkPsndocSub = new Vector<String>();

	//
	private boolean showordervisible = false;

	// 是否启用部门档案权限
	Boolean isUsedDeptPower = null;

	// 删除人员子集信息时后保存时，如果子集信息为空，并且子集信息项对应了个人信息或者工作信息的信息项，则应该清空信息项
	private Vector<GeneralVO> vDelSubVOs = new Vector<GeneralVO>();

	/**
	 * 得到是否自动编码参数
	 */
	public String getPsncodeAutoGenerateParam() {
		try {
			if (psncodeAutoGenerate == null) {
				// 效率优化
				// psncodeAutoGenerate =
				// PubDelegator.getIParValue().getParaString("0001",
				// "HI_CODECRTTYPE");
				psncodeAutoGenerate = getPara("HI_CODECRTTYPE");
				if (psncodeAutoGenerate == null) {
					psncodeAutoGenerate = PSNCODE_AUTO_GENERATE;
				}
			}
		} catch (Exception e) {
			reportException(e);
			showHintMessage(e.getMessage());
		}
		return psncodeAutoGenerate;
	}

	protected ButtonObject bnEmployeeReference = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000201")/* @res "人员引用" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000201")/* @res "人员引用" */, 0, "人员引用");

	protected ButtonObject bnApplicate = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000202")/* @res "申请" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000202")/* @res "申请" */, 0, "申请");

	protected ButtonObject bnBatchApplicate = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000312")/* @res "批量申请" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000312")/* @res "批量申请" */, 0, "批量申请");

	protected ButtonObject bnAffirm = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000203")/* @res "确认引用" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000203")/* @res "确认引用" */, 0, "确认引用");

	protected ButtonObject bnQueryAffirm = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000204")/* @res "查看申请" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000204")/* @res "查看申请" */, 0, "查看申请");

	// V55 add
	protected ButtonObject bnCancelAffirm = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000305")/* @res "取消引用" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000305")/* @res "取消引用" */, 0, "取消引用");

	protected ButtonObject bnList = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000184")/* @res "返回列表" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000184")/* @res "返回列表" */, 0, "返回列表");

	protected ButtonObject bnBatchAddSet = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000185")/* @res "批量增加子集" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000185")/* @res "批量增加子集" */, 0, "批量增加子集");

	protected ButtonObject bnChildEdit = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000186")/* @res "子集编辑" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000186")/* @res "子集编辑" */, 0, "子集编辑");

	protected ButtonObject bnUpRecord = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000187")/* @res "上一页" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000187")/* @res "上一页" */, 0, "上一页");

	protected ButtonObject bnDownRecord = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000188")/* @res "下一页" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000188")/* @res "下一页" */, 0, "下一页");

	protected ButtonObject bnToFirst = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000189")/* @res "首页" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000189")/* @res "首页" */, 0, "首页");

	protected ButtonObject bnToLast = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000190")/* @res "末页" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000190")/* @res "末页" */, 0, "末页");

	protected ButtonObject bnListItemSet = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000191")/* @res "项目设置" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000191")/* @res "项目设置" */, 0, "项目设置");

	protected ButtonObject bnAdd = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000148")/* @res "增加" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000148")/* @res "增加" */, 0, "增加");

	protected ButtonObject bnDel = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000150")/* @res "删除" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000150")/* @res "删除" */, 0, "删除");

	protected ButtonObject bnSave = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000154")/* @res "保存" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000154")/* @res "保存" */, 0, "保存");

	protected ButtonObject bnCancel = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000155")/* @res "取消" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000155")/* @res "取消" */, 0, "取消");

	protected ButtonObject bnSort = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000055")/* @res "调整顺序" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000055")/* @res "调整顺序" */, 0, "调整顺序");

	protected ButtonObject bnSetSort = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000149")/* @res "排序" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000149")/* @res "排序" */, 0, "排序");

	protected ButtonObject bnQuery = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000159")/* @res "查询" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000159")/* @res "查询" */, 0, "查询");

	protected ButtonObject bnBatch = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000164")/* @res "批量修改" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000164")/* @res "批量修改" */, 0, "批量修改");

	protected ButtonObject bnPrint = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000153")/* @res "打印" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000153")/* @res "打印" */, 0, "打印");
	
	// zhanghua
	protected ButtonObject bnImport = new ButtonObject(
			"导入",
			"导入", 0, "导入");

	protected ButtonObject bnFresh = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000156")/* @res "刷新" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000156")/* @res "刷新" */, 0, "刷新");

	protected ButtonObject bnIntoDoc = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000152")/* @res "转入人员档案" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000152")/* @res "转入人员档案" */, 0, "转入人员档案");

	protected ButtonObject bnSmUser = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000056")/* @res "增加自助用户" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000056")/* @res "增加自助用户" */, 0, "增加自助用户");

	// 主集操作按钮
	protected ButtonObject bnEdit = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000167")/* @res "修改" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000167")/* @res "修改" */, 0, "修改");

	protected ButtonObject bnChildOp = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000160")/* @res "子集操作" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000160")/* @res "子集操作" */, 0, "子集操作");// v50

	// del

	protected ButtonObject bnReturn = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000168")/* @res "返回" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000168")/* @res "返回" */, 0, "返回");

	// 子集操作按钮
	protected ButtonObject bnAddChild = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000145")/* @res "增加行" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000145")/* @res "增加行" */, 0, "增行");

	protected ButtonObject bnInsertChild = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000157")/* @res "插入行" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000157")/* @res "插入行" */, 0, "插入行");

	protected ButtonObject bnUpdateChild = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000146")/* @res "编辑行" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000146")/* @res "编辑行" */, 0, "编辑行");

	protected ButtonObject bnDelChild = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000147")/* @res "删除行" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000147")/* @res "删除行" */, 0, "删行");

	// 员工附加文档信息集的操作按钮
	protected ButtonObject bnUpload = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000161")/* @res "上传" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000161")/* @res "上传" */, 0, "附件");

	// 卡片和花名册按钮
	protected ButtonObject bnCard = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000165")/* @res "卡片" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000165")/* @res "卡片" */, 0, "卡片");

	protected ButtonObject bnAssist = new ButtonObject(NCLangRes.getInstance()
			.getStrByID("600704", "UPP600704-100231"), NCLangRes.getInstance()
			.getStrByID("600704", "UPP600704-100231"), 0, "辅助功能");

	protected ButtonObject bnDisplay = new ButtonObject(NCLangRes.getInstance()
			.getStrByID("600704", "UPP600704-100232"), NCLangRes.getInstance()
			.getStrByID("600704", "UPP600704-100232"), 0, "显示设置");

	protected ButtonObject bnView = new ButtonObject(NCLangRes.getInstance()
			.getStrByID("600704", "UPP600704-100183"), NCLangRes.getInstance()
			.getStrByID("600704", "UPP600704-100183"), 0, "预览");

	protected ButtonObject bnBatchExport = new ButtonObject(NCLangRes
			.getInstance().getStrByID("600704", "UPP600704-100184"), NCLangRes
			.getInstance().getStrByID("600704", "UPP600704-100184"), 0, "批量导出");

	protected ButtonObject bnBook = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000166")/* @res "花名册" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"upt600704-000166")/* @res "花名册" */, 0, "花名册");

	// 排序按钮
	protected ButtonObject bnSubsetMove = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000273")/* "调整子集顺序" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000273")/* "调整子集顺序" */, 0, "调整子集顺序");// v50 add

	// V31SP1
	protected ButtonObject bnExportPic = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000182")/* @res "照片导出" */,
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000182")/* @res "照片导出" */, 0, "照片导出");

	/** 卡片按钮组 */
	protected ButtonObject[] grpCardChild = { bnView, bnBatchExport };

	// V53
	protected ButtonObject bnIndocApp = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000280"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000280"), 0, "入职申请");

	// added by zhangdd for V57
	/** 人岗能力匹配按钮 */
	protected ButtonObject bnAbilityMatch = new ButtonObject(NCLangRes
			.getInstance().getStrByID("600704", "UPP600704-100185"), NCLangRes
			.getInstance().getStrByID("600704", "UPP600704-100185"), 0,
			"人岗能力匹配");

	/** 能力素质模块是否安装 */
	protected boolean isHRCPInstalled = false;

	//
	// 关键人员增加按钮
	protected ButtonObject bnPsnGroup = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000281"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000281"), 0, "关键人员组维护");

	protected ButtonObject bnAddPsnGroup = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000282"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000282"), 0, "增加关键人员组");

	protected ButtonObject bnEditPsnGroup = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000283"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000283"), 0, "修改关键人员组");

	protected ButtonObject bnDelPsnGroup = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000284"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000284"), 0, "删除关键人员组");

	/**
	 * 武燕军 2011-03-18 添加导入导出按钮
	 */

	/*protected ButtonObject MyImport = new ButtonObject("导入", "", "导入");

	protected ButtonObject MyExport = new ButtonObject("导出", "", "导出");

	protected ButtonObject exlImport = new ButtonObject("Exc导入", "", "Exc导入");

	// protected ButtonObject exlExport =new ButtonObject("Exc导出","","Exc导出");
	protected ButtonObject exlDownload = new ButtonObject("模板下载", "", "模板下载");

	protected ButtonObject exlExportUi = new ButtonObject("导出设置", "", "导出设置");

	protected ButtonObject[] MyImports = { exlDownload, exlImport };

	protected ButtonObject[] MyExports = { exlExportUi };*/
	
	protected ButtonObject bnBatchGl = new ButtonObject("计算工龄","计算工龄",0,"计算工龄");// zhf add
	protected ButtonObject bnCanBatchGl = new ButtonObject("取消计算工龄","取消计算工龄",0,"取消计算工龄");// zhf add
	
	protected ButtonObject getGlColButton(){
		return bnBatchGl;
	}
	
	protected ButtonObject getCanGlColButton(){
		return bnCanBatchGl;
	}

	// //武燕军

	// 人员组维护
	protected ButtonObject[] grpPsnGroup = { bnAddPsnGroup, bnEditPsnGroup,
			bnDelPsnGroup };

	protected ButtonObject bnKeyPsn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000285"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000285"), 0, "关键人员维护");

	protected ButtonObject bnAddKeyPsn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000286"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000286"), 0, "参照选择增加");

	protected ButtonObject bnMulAddKeyPsn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000328"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000328"), 0, "条件选择增加");

	protected ButtonObject bnEditKeyPsn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000287"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000287"), 0, "修改关键人员");

	protected ButtonObject bnDelKeyPsn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000288"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000288"), 0, "删除关键人员");

	protected ButtonObject bnSealKeyPsn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("600704", "UPT600704-000289"),
			nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPT600704-000289"), 0, "封存关键人员");

	protected ButtonObject[] keyPsnmaintain = { bnAddKeyPsn, bnMulAddKeyPsn,
			bnEditKeyPsn, bnDelKeyPsn, bnSealKeyPsn };// bnEditKeyPsn,

	// 关键人员列表界面浏览组
	protected ButtonObject[] grpKeyPsnList = { bnPsnGroup, bnKeyPsn, bnBatch,
			bnBatchAddSet, bnCard, bnBook, bnExportPic, bnDisplay, bnUpload,
			bnQuery, bnFresh, bnPrint };

	// 卡片界面浏览组
	protected ButtonObject[] grpKeyPsnCard = { bnEdit, bnChildEdit, bnSave,
			bnCancel, bnUpload, bnCard, bnToFirst, bnUpRecord, bnDownRecord,
			bnToLast, bnSubsetMove, bnList };

	// 员工信息采集
	/* V35 列表组 【增加】【编辑】【删除】【批量修改】【批量增加子集】【卡片】【花名册】【排序】【查询】【转入人员档案】【项目设置】【刷新】【打印】 */
	public ButtonObject[] grpCollect = { bnAdd, bnEdit, bnDel, bnBatch,
			bnBatchAddSet, bnIntoDoc, bnCard, bnBook, bnDisplay, bnUpload,
			bnQuery, bnFresh, bnPrint };

	// 武燕军 MyImport,MyExport

	// 员工信息维护
	protected ButtonObject[] grpSortMaintain = new ButtonObject[] { bnEdit,
			bnDel, bnBatch, bnBatchAddSet, bnCard, bnBook, bnAbilityMatch,
			bnEmployeeReference, bnDisplay, bnAssist, bnUpload, bnQuery,
			bnFresh, bnPrint,bnImport/**bnBatchGl,bnCanBatchGl*/};

	// 集团showorder排序子按钮
	protected ButtonObject[] grpShoworder = { bnSave, bnCancel };

	// 人员引用
	protected ButtonObject[] grpEmployeeReference = { bnApplicate,
			bnBatchApplicate, bnQueryAffirm, bnAffirm, bnCancelAffirm };

	// 维护节点修改子按钮
	/*
	 * 卡片组 【编辑】【删除】[子集编辑] [保存] [取消]【上一条】【下一条】【至头条】【至末条】[列表]【刷新】 [增加行][插入行][编辑行]
	 * [删除行]
	 */
	protected ButtonObject[] grpCardDisplayMaintain = { bnEdit, bnDel,
			bnChildEdit, bnSave, bnCancel, bnUpload, bnAbilityMatch, bnCard,
			bnToFirst, bnUpRecord, bnDownRecord, bnToLast, bnSubsetMove, bnList };

	protected ButtonObject[] grpCardRef = { bnEmployeeReference, bnSave,
			bnCancel, bnList };

	// 采集卡片显示按钮
	protected ButtonObject[] grpCardDisplayCollect = { bnAdd, bnEdit, bnDel,
			bnChildEdit, bnSave, bnCancel, bnUpload, bnToFirst, bnUpRecord,
			bnDownRecord, bnToLast, bnSubsetMove, bnList };

	// V35
	protected ButtonObject[] grpChild = { bnAddChild, bnInsertChild,
			bnUpdateChild, bnDelChild };

	// 界面跟踪历史
	@SuppressWarnings("unchecked")
	protected Stack bnHistory = new Stack();

	// 部门权限过滤语句，不启用权限时为空
	protected String powerSql = "";

	// 部门树根结点
	protected CtrlDeptVO ctrlDeptRoot;

	// 当前人员列表的信息
	protected GeneralVO[] psnList;

	// 因为打印时需要查出所有信息（包括扩展自定义信息），人员列表中查出的信息和查出所有信息的sql语句造成数据顺序不一致（distinct引起的）
	protected GeneralVO[] psnListForPrint;

	// 当前查询结果
	protected GeneralVO[] queryResult = new GeneralVO[0];

	// 人员按照部门分类缓冲
	@SuppressWarnings("unchecked")
	protected Hashtable psnDeptCache = new Hashtable();

	// 部门权限
	protected String deptPowerInit = " 0 = 0 ";

	// 人员跟踪信息集合，包括表名和模块标识
	private static final String[][] traceTables = {
			{ "hi_psndoc_deptchg", "HI" },/* 任职情况 */
			{ "hi_psndoc_ctrt", "HRCM" },/* 劳动合同 */
			{ "hi_psndoc_part", "HI" },/* 兼职情况 */
			{ "hi_psndoc_training", "TRM" },/* 培训记录 */
			{ "hi_psndoc_ass", "PE" },/* 考核记录 */
			{ "hi_psndoc_retire", "HI" },/* 离退待遇 */
			{ "hi_psndoc_orgpsn", "HI" },/* 虚拟组织 */
			{ "hi_psndoc_psnchg", "HI" }, /* 员工流动 */
			{ "hi_psndoc_dimission", "HI" }, /* 离职情况 */
			{ "hi_psndoc_capa", "HRCP" } /* 素质指标 */
	};

	// 跟踪信息集映射，主要是为了高速存取
	@SuppressWarnings("unchecked")
	private static final Hashtable traceTableMap = new Hashtable();

	/**
	 * 初始化人员信息跟踪映射表
	 */
	@SuppressWarnings("unchecked")
	private void initTraceTableMap() {
		for (int i = 0; i < traceTables.length; i++) {
			traceTableMap.put(traceTables[i][0], traceTables[i][1]);
		}
	}

	@SuppressWarnings("deprecation")
	protected void initButtonGroup() throws Exception {
		// 对按钮进行分组
		// 所有相关节点都要加入卡片的分组
		bnCard.addChileButtons(grpCardChild);

		if (getModuleName().equalsIgnoreCase("600704")) {// 人员信息采集
			// 显示设置
			bnDisplay.addChileButtons(new ButtonObject[] { bnListItemSet,
					bnSort, bnSetSort });
		} else if (getModuleName().equalsIgnoreCase("600707")) {// 人员信息维护
			// 辅助信息
			// 武燕军
			//MyImport.addChileButtons(MyImports);
			//MyExport.addChileButtons(MyExports);
			bnAssist
					.addChileButtons(new ButtonObject[] { bnSmUser, bnExportPic });
			// 显示设置
			bnDisplay.addChileButtons(new ButtonObject[] { bnListItemSet,
					bnSort, bnSetSort });
		} else if (getModuleName().equalsIgnoreCase("600708")) {// 人员信息查询
			// 显示设置
			bnDisplay.addChileButtons(new ButtonObject[] { bnListItemSet,
					bnSetSort });
		} else if (getModuleName().equalsIgnoreCase("600710")) {// 关键人员
			// 显示设置
			bnDisplay.addChileButtons(new ButtonObject[] { bnListItemSet,
					bnSort, bnSetSort });
		}
	}

	// 任职类型
	protected final Item[] jobTypeItems = new Item[] {
			new Item(0, "fullwork", "WorkCard", nc.ui.ml.NCLangRes
					.getInstance().getStrByID("600704", "UPP600704-000170")/* "全职" */),
			new Item(1, "parttime", "WorkCard", nc.ui.ml.NCLangRes
					.getInstance().getStrByID("600704", "UPP600704-000171")/* "兼职" */),
			new Item(2, "work", "WorkCard", nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-000172")/* "借调" */),
			new Item(3, "work", "WorkCard", nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-000174")/* "交流" */),
			new Item(4, "despatch", "WorkCard", nc.ui.ml.NCLangRes
					.getInstance().getStrByID("600704", "UPT600704-000307")/* "外派" */),
			new Item(-1, "all", "WorkCard", nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-000198")/* "全部" */),
			new Item(11, "lg", "WorkCard","轮岗"/* "全部" */)
	};

	// 人员归属范围
	protected final Item[] psnScopeItems = new Item[] {
			new Item(CommonValue.PSNCLSCOPE_WORK, "work", "WorkCard",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000059")/* @res "在职人员" */),
			new Item(CommonValue.PSNCLSCOPE_DISMISS, "dismiss", "DismissCard",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000060")/* @res "解聘人员" */),
			new Item(CommonValue.PSNCLSCOPE_RETIRE, "retire", "RetireCard",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000061")/* @res "离退人员" */),
			new Item(CommonValue.PSNCLSCOPE_LEAVE, "leave", "LeaveCard",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000062")/* @res "调离人员" */),
			new Item(CommonValue.PSNCLSCOPE_OTHER, "other", "OtherCard",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000063")/* @res "其他人员" */) };

	// 当前人员的所有信息
	protected PersonEAVO person;

	// 当前人员数据信息缓冲
	@SuppressWarnings("unchecked")
	protected HashMap persons = new HashMap();

	// 当前查询条件,初始化为空条件
	protected ConditionVO[] conditions = new ConditionVO[0];

	// 常用查询条件
	protected String wheresql = "";

	// 人员列表需要显示的条目
	protected Pair[] listItems = null;

	// 移至对话框
	protected GotoDialog gotoDialog = null;

	// 进度对话框
	protected ProgressDialog progressDialog = null;

	protected SortableBillScrollPane ivjPsnList = null;

	// 批量修改对话框
	protected BatchUpdateDlg batchDlg = null;

	// 查询对话框
	protected QueryConditionClient queryDlg = null;

	public String[] tablesFromQueryDLG = null;

	// 打印对话框
	protected PrintEntry printEntry = null;

	protected int recordcount = 0;

	private static Hashtable headTables = null;

	private UITree ivjDeptTree = null;

	private UIPanel ivjPsnCard = null;

	private UIScrollPane ivjUIScrollPane = null;

	private UISplitPane ivjUISplitPane = null;

	private UIComboBox ivjCmbPsncl = null;

	private UIPanel ivjNorthPanel = null;

	private UILabel ivjUILabel1 = null;

	private HIBillCardPanel ivjWorkCard = null;

	private HIBillCardPanel ivjDismissCard = null;

	private HIBillCardPanel ivjLeaveCard = null;

	private HIBillCardPanel ivjOtherCard = null;

	private HIBillCardPanel ivjRetireCard = null;

	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	static {
		headTables = new Hashtable();
		headTables.put("bd_psndoc", "bd_psndoc");
		headTables.put("bd_psnbasdoc", "bd_psnbasdoc");// bd_accpsndoc
	}

	private UIPanel ivjCenterPanel = null;

	SortConfigDialog configDialog = null; // 排序对话框

	// Registered Triggers
	TriggerTable triggerTable = new TriggerTable();

	private boolean isLoadedBasicData = false;// wangkf add 标记员工基本情况和辅助情况是否加载的

	private boolean isLoadedAccData = false;

	String autopsncode = null;// 当人员编码是自动生成时，如果点取消则保留第一次生成的编号。如果点【保存】则把该字段清空。

	String isUniquePsncodeInGroup = null;// 是否设置 集团内唯一的参数

	protected boolean isQuery = false;// 标记是否查询过

	// add by sunxj 2010-01-03 快速查询插件 start
	protected boolean isQuickSearch = false;// 标记是否快速查询过

	protected String quickWherePart = null;

	protected String unloadPsn = null;

	// add by sunxj 2010-01-03 快速查询插件 end

	protected boolean isQuickQuery = false; // 标记是否快查过

	protected String quickDLGsql = "";

	protected HashMap hmSortCondition = new HashMap();// wangkf add 存放最新的排序条件。

	private Vector vTempUploadFile = new Vector();

	private boolean isRelateToAcc = false;// 是否有字段与辅助表相关联。

	public String[] allCorpPks = null; // 所有关联公司

	public String corpPK = Global.getCorpPK();

	public String curTempCorpPk = Global.getCorpPK();

	// 保存各公司的部门权限SQL语句
	protected Hashtable deptPowerlist = new Hashtable();

	protected boolean isMaintain = false;

	// 当前兼职人员是否是本公司兼职人员
	protected boolean isPartPsnCurCorp = false;

	// 当前显示界面
	protected boolean isShowBillTemp = false;

	// 人员按照公司分类缓冲
	protected Hashtable psnCorpCache = new Hashtable();

	public DefaultTreeModel treemodel = null;

	private UIPanel ivjCenterPanelRight = null;

	private UISplitPane ivjUISplitPaneVertical = null;

	private UIComboBox ivjCmbJobType = null;

	private UILabel ivjUILabelType = null;

	private boolean isrehire = false;// 返聘标志,默认为不是返聘

	private boolean isbackrehire = false;// 再聘标志,默认为不是再聘

	private String rehirePsnbaspk = null;// 返聘人员基本表主键

	private String rehirePsnpk = null;// 返聘人员工作信息表主键

	private String rehirePsnBelonpk = null;// 返聘人员归属公司主键

	class IvjEventHandler implements java.awt.event.ItemListener,
			java.awt.event.MouseListener,
			javax.swing.event.TreeSelectionListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == PsnInfCollectUI.this.getCmbPsncl()) {
				connEtoC1(e);
			} else if (e.getSource() == PsnInfCollectUI.this.getCmbJobType()) {

				cmbJobTypItemStateChanged(e);
			}
		};

		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == PsnInfCollectUI.this.getDeptTree()) {
				connEtoC3(e);
			}
		};

		public void mouseEntered(java.awt.event.MouseEvent e) {
		};

		public void mouseExited(java.awt.event.MouseEvent e) {
		};

		public void mousePressed(java.awt.event.MouseEvent e) {
		};

		public void mouseReleased(java.awt.event.MouseEvent e) {
		};

		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == PsnInfCollectUI.this.getDeptTree()) {
				listSelectRow = -1;
				connEtoC2(e);
			}
		};
	};

	protected int splitLocation = SPLIT_MAX;// 记录是否显示子集状态，界面间切换时使用

	private CheckUniqueDlg checkAddDlg = null;

	// 保存各公司的人员类别权限SQL语句
	protected Hashtable psnclPowerlist = new Hashtable();

	// 当前是否进行多行编辑
	private boolean isEditSub = false;

	/**
	 * V35 列表鼠标响应事件
	 * 
	 * @param mouseEvent
	 */
	public void PsnListMouseClicked(BillMouseEnent mouseEvent) {
		try {
			if (isEmployeeRef()) {
				switchButtonGroup(EMPLOYEE_REF_CARD);
				setButtonGroup(EMPLOYEE_REF_CARD);
			} else {
				switchButtonGroup(CARD_GROUP);
				setButtonGroup(CARD_GROUP);
			}
			listSelectRow = getPsnList().getTable().getSelectedRow();
			loadPsnInfo(listSelectRow);// 装载 当前行人员数据
			if (isEmployeeRef()) {
				setButtonsState(EMPLOYEE_REF_CARD_BROWSE);
			} else {
				setButtonsState(CARD_MAIN_BROWSE);
				setButtonsState(CARD_CHILD_BROWSE);
			}
			getCard().setPosMaximized(-1);

		} catch (Exception e) {
			Util.getTopFrame(this).setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * PsnInfCollectUI 构造子注解。
	 */
	public PsnInfCollectUI() {
		super();
		initialize();
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-10-19 15:57:09)
	 * 
	 * @param parentvo
	 *            nc.vo.hi.hi_301.CtrlDeptVO
	 */
	protected DefaultMutableTreeNode addDeptNodeToTree(
			DefaultMutableTreeNode parentnode, CtrlDeptVO parentvo) {
		// 添加孩子
		int childCorpCount = 0;
		Vector children = null;
		if (parentnode == null) {
			parentnode = new DefaultMutableTreeNode(parentvo);
		}
		if (parentvo.nodeType == CtrlDeptVO.CORP) {
			children = parentvo.getDeptchildren();
			if (parentvo.getChildren() == null) {
				childCorpCount = 0;
			} else {
				childCorpCount = parentvo.getChildren().size();
			}
		} else if (parentvo.nodeType == CtrlDeptVO.DEPT) {
			children = parentvo.getChildren();
		}
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				CtrlDeptVO childvo = (CtrlDeptVO) children.elementAt(i);
				DefaultMutableTreeNode childNode = addDeptNodeToTree(null,
						childvo);
				if (parentvo.nodeType == CtrlDeptVO.CORP) {// 如果子节点是的父节点为公司，则把节点添加在子公司之上
					if (childCorpCount >= 0) {
						int index = parentnode.getChildCount() - childCorpCount;
						parentnode.insert(childNode, index);
					}
				} else {
					parentnode.add(childNode);
				}
			}
		}
		return parentnode;
	}

	/**
	 * 新增人员时的保存动作函数。 创建日期：(2004-5-27 10:04:31)
	 * 
	 * @param eavO
	 *            nc.vo.hi.hi_301.PersonEAVO
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void addPerson(PersonEAVO eavo) throws java.lang.Exception {
		String pk_psndoc = null;
		if (isrehire) {// 返聘处理
			String pk_psnbasdoc = getRehirePsnbaspk();
			eavo.getAccpsndocVO().setAttributeValue("pk_psnbasdoc",
					pk_psnbasdoc);
			eavo.getPsndocVO().setAttributeValue("isreturn", "Y");
			eavo.getPsndocVO().setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
			eavo.getAccpsndocVO().setAttributeValue("indocflag", "Y");
			// pk_psndoc = getRehirePsnpk();
			// 人员返聘,需要增加工作信息,修改人员个人信息
			eavo.getAccpsndocVO().setAttributeValue("pk_corp",
					Global.getCorpPK());
			pk_psndoc = HIDelegator.getPsnInf().insertMain(eavo.getPsndocVO(),
					eavo.getAccpsndocVO(), eavo.getAccpsndocVO(), getObjVO());// 第三参数作为返聘标志
		} else if (isbackrehire) {// 再聘处理
			String pk_psnbasdoc = getRehirePsnbaspk();
			eavo.getAccpsndocVO().setAttributeValue("pk_psnbasdoc",
					pk_psnbasdoc);
			eavo.getPsndocVO().setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
			eavo.getAccpsndocVO().setAttributeValue("indocflag", "Y");
			// pk_psndoc = getRehirePsnpk();
			// 人员再聘,需要增加工作信息,修改人员个人信息
			eavo.getAccpsndocVO().setAttributeValue("pk_corp",
					Global.getCorpPK());
			pk_psndoc = HIDelegator.getPsnInf().insertMain(eavo.getPsndocVO(),
					eavo.getAccpsndocVO(), eavo.getAccpsndocVO(), getObjVO());// 第三参数作为再聘标志
		} else {
			// 保存并设置人员主键
			pk_psndoc = HIDelegator.getPsnInf().insertMain(eavo.getPsndocVO(),
					eavo.getAccpsndocVO(), null, getObjVO());
		}
		eavo.getPsndocVO().setAttributeValue("pk_psndoc", pk_psndoc);
		eavo.setPk_psndoc(pk_psndoc);
		eavo.getAccpsndocVO().setAttributeValue("belong_pk_corp",
				Global.getCorpPK());
		// 设置人员条件
		String strWhere = " and bd_psndoc.pk_psndoc = '" + pk_psndoc + "' ";
		// 更新改人员信息
		GeneralVO[] gvos = HIDelegator.getPsnInf().queryByCondition(
				Global.getCorpPK(), null, powerSql, getListField(), strWhere);

		// 往人员查询结果和人员列表中加入新增人员
		GeneralVO[] result = new GeneralVO[queryResult.length + 1];
		System.arraycopy(queryResult, 0, result, 0, queryResult.length);
		if (gvos != null && gvos.length > 0) {
			eavo.getAccpsndocVO().setAttributeValue("pk_psnbasdoc",
					gvos[0].getAttributeValue("pk_psnbasdoc"));
			result[queryResult.length] = gvos[0];
		}
		queryResult = result;
		vBookConds.addElement(pk_psndoc);// 用于花名册的查询条件
		// 清除缓冲
		psnDeptCache.clear();
		psnCorpCache.clear();

		// 重新选择树节点（部门参数可能不是当前的部门)
		deptTreeValueChanged(null, false);
		listSelectRow = getRowInPsnList(eavo, psnList);// V35
		bodyTabbedPane_stateChanged(null);
		if (listSelectRow == -1) {
			person = eavo;
		}
	}

	/**
	 * 将触发源与触发对象使用触发器关联起来。 创建日期：(2004-5-17 20:19:19)
	 * 
	 * @param srcItem
	 *            nc.ui.pub.bill.BillItem 触发源的BillItem
	 * @param destItem
	 *            nc.ui.pub.bill.BillItem 触发对象的BillItem
	 * @param listener
	 *            nc.ui.hi.hi_301.trigger.ITriggerListener 触发处理器
	 */
	private void addTriggerListener(final BillItem srcItem,
			final BillItem destItem, final ITriggerListener listener) {

		// 设置触发对象
		listener.setTarget(new AccessibleItem(destItem, this));
		// 获得触发源组件
		Component srcComponent = srcItem.getComponent();
		// 根据组件类型添加触发处理器
		switch (srcItem.getDataType()) {
		// 字符、整数、小数、时间都使用参照的textfield
		case IBillItem.STRING: // 字符
			UIRefPane ref = (UIRefPane) srcComponent;
			UITextField text = ref.getUITextField();
			ref.setButtonFireEvent(true);
			text.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if ("ID".equalsIgnoreCase((srcItem.getKey()))) {
								String id = srcItem.getValue();
								if (id != null && id.length() != 0) {
									if (id.length() != 15 && id.length() != 18) {
										// MessageDialog.showWarningDlg(getCard(),
										// nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
										// "UPP600704-000009"),
										// "输入的身份证号码为非15位或18位!");
									} else {
										TriggerEvent event = new TriggerEvent(
												srcItem);
										listener.trigger(event);
									}
								}
							}
						}
					});
				}

				public void focusGained(FocusEvent e) {
				}
			});
			break;
		case IBillItem.INTEGER: // 整数
		case IBillItem.DECIMAL: // 小数
		case IBillItem.TIME: // 时间
			ref = (UIRefPane) srcComponent;
			text = ref.getUITextField();
			ref.setButtonFireEvent(true);
			text.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TriggerEvent event = new TriggerEvent(srcItem);
					listener.trigger(event);
				}
			});
			break;
		// 日期、参照、自定义项档案都是使用参照
		case IBillItem.DATE: // 日期
		case IBillItem.UFREF: // 参照
		case IBillItem.USERDEF: // 自定义项档案
			ref = (UIRefPane) srcComponent;
			ref.setButtonFireEvent(true);
			ref.addValueChangedListener(new ValueChangedListener() {
				public void valueChanged(ValueChangedEvent e) {
					TriggerEvent event = new TriggerEvent(srcItem);
					listener.trigger(event);
				}
			});
			break;
		// 逻辑型使用复选框
		case IBillItem.BOOLEAN: // 逻辑
			final UICheckBox checkBox = (UICheckBox) srcComponent;
			checkBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					TriggerEvent event = new TriggerEvent(srcItem);
					listener.trigger(event);
				}
			});
			break;
		// 下拉框类型
		case IBillItem.COMBO: // 下拉
			final UIComboBox comboBox = (UIComboBox) srcComponent;
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					TriggerEvent event = new TriggerEvent(srcItem);
					listener.trigger(event);
				}
			});
			break;
		// 大文本、图片不激活触发器
		case IBillItem.TEXTAREA: // 大文本
		case IBillItem.IMAGE: // 图片
		case IBillItem.OBJECT: // 图片
			break;
		}
	}

	// add by zhyan v50
	private RecieverSelectDlg userSelectDlg = null;

	public RecieverSelectDlg getUserSelectDlg() {
		if (userSelectDlg == null) {
			userSelectDlg = new RecieverSelectDlg(this, app_pk_corp,
					getModuleName());
		}
		return userSelectDlg;
	}

	/**
	 * 表体的标签切换引发的事件处理。 创建日期：(2004-5-12 14:26:19)
	 * 
	 * @param event
	 *            javax.swing.event.ChangeEvent
	 */
	protected void bodyTabbedPane_stateChanged(ChangeEvent event) {
		String tableCode = getBodyTableCode();
		getCard().setBodyMenuShow(tableCode, false);
		getCard().addEditListener(this);
		loadPsnChildInfo(listSelectRow, tableCode);
		if (getButtonGroup() == CARD_GROUP) {
			switchButtonGroup(CARD_GROUP);
			setChildButtonState();
			setBillTempletState(isEditing());// setEditState(false); ||
			// isAdding()
		} else if (getButtonGroup() == EMPLOYEE_REF_CARD) {
			setBillTempletState(isEditing());
			setButtonsState(EMPLOYEE_REF_EDIT);
			if ("bd_psnbasdoc".equalsIgnoreCase(tableCode)) {
				setHeadBillItemEditable("bd_psnbasdoc", false);
			}
		} else {
			setButtonsState(LIST_BROWSE);
		}
	}

	/**
	 * 取消编辑主信息集。 创建日期：(2004-5-26 21:37:48)
	 */
	private void cancelEditMain() {
		if (!isAdding()) {
			getCard().setBillValueVO(person);// 如果当前为维护状态，将原来的值恢复到界面上
			CircularlyAccessibleValueObject[] records = person
					.getTableVO(getCurrentSetCode());// 当前信息集的所有记录
			getCard().getBillModel().setBodyDataVO(records);
		} else {
			getCard().resumeValue();// 否则清空编辑界面
		}
		setBillTempletState(false);
		setEditState(false);
	}

	/**
	 * 取消编辑子表的操作。 创建日期：(2004-5-26 21:42:21)
	 */
	private void cancelEditSub() {
		// 设置取消编辑子表信息集时按钮状态
		setCancelEditSubButtonState();
		// 子表页签恢复可用
		getCard().getBodyTabbedPane().setEnabled(true);
		// 编辑界面禁止掉
		setBillTempletState(false);
		if (isEditLine()) {
			// 停止编辑
			getCard().stopEditing();
			// 恢复老的数据
			BillItem[] billitems = getCard().getBillModel().getBodyItems();
			for (int i = 0; i < billitems.length; i++) {
				if (billitems[i].getDataType() == BillItem.UFREF) {
					((UIRefPane) billitems[i].getComponent()).setPK(null);
					((UIRefPane) billitems[i].getComponent()).setText(null);
				}
			}
			getCard().getBillModel().setBodyDataVO(subBackupVOs);
			person.setTableVO(getBodyTableCode(), subBackupVOs);
		} else {
			// 否则选中当前编辑行，并且删除
			int line = getEditLineNo();
			getCard().getBillTable().getSelectionModel().setSelectionInterval(
					line, line);
			getCard().delLine();
			// 从子表记录中删除该行
			SubTable subtable = person.getSubTable(getBodyTableCode());
			subtable.getRecords().removeElementAt(line);
		}
		if (isEditSub()) {
			setEditSub(false);
			vDelPkPsndocSub.clear();
			// 
			vDelSubVOs.clear();
		}
		if ("hi_psndoc_edu".equalsIgnoreCase(getBodyTableCode())) {
			loadPsnChildInfo(listSelectRow, getBodyTableCode());
		}
	}

	/**
	 * 排序后取消排序排序结果。 创建日期：(2004-5-26 21:35:47)
	 */
	private void cancelSort() throws java.lang.Exception {
		setCancelSortButtonState();
		// 返回主界面
		returnToMain();
		// 重新选择当前节点
		deptTreeValueChanged(null);
		// 设置序号列不可编辑
		getPsnList().getTableModel().getItemByKey("showorder").setEdit(false);
		getPsnList().getTableModel().getItemByKey("showorder")
				.setEnabled(false);
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-9-25 15:19:02)
	 */
	private void changeBillTempRef() {
		String mainrefcorp = null;
		mainrefcorp = (String) psnList[listSelectRow]
				.getAttributeValue("pk_corp");
		BillItem clItem = getBillItem("bd_psndoc.pk_psncl");
		UIRefPane psnclsref = (UIRefPane) clItem.getComponent();
		nc.ui.hi.hi_301.ref.PsnClsRef psncls = null;
		psncls = new nc.ui.hi.hi_301.ref.PsnClsRef(mainrefcorp);
		psnclsref.setRefType(1);
		psnclsref.setRefInputType(1);
		psnclsref.setRefModel(psncls);

		BillItem deptItem = getBillItem("bd_psndoc.pk_deptdoc");
		UIRefPane deptref = (UIRefPane) deptItem.getComponent();
		SpaDeptdocRefTreeModel dept = null;
		dept = new SpaDeptdocRefTreeModel(mainrefcorp, false);
		//
		setWhereToModel(dept,
				"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
		deptref.setRefType(1);
		deptref.setRefInputType(1);
		deptref.setRefModel(dept);
		BillItem jobItem = getBillItem("bd_psndoc.pk_om_job");
		UIRefPane jobref = (UIRefPane) jobItem.getComponent();
		nc.ui.hi.ref.JobRef job = null;
		job = new nc.ui.hi.ref.JobRef(mainrefcorp, false);
		jobref.setRefType(1);
		jobref.setRefInputType(1);
		jobref.setRefModel(job);

		BillItem dutyItem = getBillItem("bd_psndoc.dutyname");
		UIRefPane dutyref = (UIRefPane) dutyItem.getComponent();
		nc.ui.hi.ref.DutyRef duty = null;// DutySimpleRef--〉DutyRef
		duty = new nc.ui.hi.ref.DutyRef(mainrefcorp);
		dutyref.setRefType(1);
		dutyref.setRefInputType(1);
		dutyref.setRefModel(duty);
		// hi_psndoc_deptchg.pk_psncl
		/*
		 * BillItem clItem2 = getBillItem("hi_psndoc_deptchg.pk_psncl");
		 * UIRefPane psnclsref2 = (UIRefPane) clItem2.getComponent();
		 * nc.ui.hi.hi_301.ref.PsnClsRef psncls2 = new
		 * nc.ui.hi.hi_301.ref.PsnClsRef(mainrefcorp);// wangkf fixed
		 * curTempCorpPk->mainrefcorp psnclsref2.setRefType(1);
		 * psnclsref2.setRefInputType(1); psnclsref2.setRefModel(psncls2);
		 */
		BillItem deptItem2 = getBillItem("hi_psndoc_deptchg.pk_deptdoc");
		UIRefPane deptref2 = (UIRefPane) deptItem2.getComponent();
		SpaDeptdocRefTreeModel dept2 = new SpaDeptdocRefTreeModel(mainrefcorp);
		//
		setWhereToModel(dept2,
				"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");

		deptref2.setRefType(1);
		deptref2.setRefInputType(1);
		deptref2.setRefModel(dept2);

		BillItem jobItem2 = getBillItem("hi_psndoc_deptchg.pk_postdoc");
		UIRefPane jobref2 = (UIRefPane) jobItem2.getComponent();
		nc.ui.hi.ref.JobRef job2 = null;
		job2 = new nc.ui.hi.ref.JobRef(mainrefcorp, false);
		// curTempCorpPk->mainrefcorp
		jobref2.setRefType(1);
		jobref2.setRefInputType(1);
		jobref2.setRefModel(job2);
		UIRefPane dutyref2 = (UIRefPane) dutyItem.getComponent();
		nc.ui.hi.ref.DutyRef duty2 = null;
		duty2 = new nc.ui.hi.ref.DutyRef(mainrefcorp);// wangkf fixed
		// curTempCorpPk->mainrefcorp
		dutyref2.setRefType(1);
		dutyref2.setRefInputType(1);
		dutyref2.setRefModel(duty2);
		// hi_psndoc_req.pk_postrequire_h
		BillItem postrequire_hItem = getBillItem("hi_psndoc_req.pk_postrequire_h");
		UIRefPane postrequire_hItemref = (UIRefPane) postrequire_hItem
				.getComponent();
		nc.ui.hi.ref.RequireRef postrequire_h = new nc.ui.hi.ref.RequireRef(
				curTempCorpPk);
		postrequire_hItemref.setRefType(1);
		postrequire_hItemref.setRefInputType(1);
		postrequire_hItemref.setRefModel(postrequire_h);
		// hi_psndoc_req.pk_postrequire_b
		BillItem postrequire_bItem = getBillItem("hi_psndoc_req.pk_postrequire_b");
		UIRefPane postrequire_bItemref = (UIRefPane) postrequire_bItem
				.getComponent();
		nc.ui.hi.ref.RequireLevelRef postrequire_b = new nc.ui.hi.ref.RequireLevelRef(
				curTempCorpPk);
		postrequire_bItemref.setRefType(1);
		postrequire_bItemref.setRefInputType(1);
		postrequire_bItemref.setRefModel(postrequire_b);
		//
		BillItem[] items2 = getCard().getBodyItems();// ((HIBillData)
		// getCard().getBillData()).getBodyAllItems();
		if (items2 != null && items2.length > 0) {
			for (int i = 0; i < items2.length; i++) {
				if (items2[i].getDataType() == 5) { // 参照类型
					UIRefPane Myref = (UIRefPane) items2[i].getComponent();
					String refNodeName = Myref.getRefNodeName();

					if (Util.isDefdocPk(items2[i].getRefType())) {
						UIRefPane ref = (UIRefPane) items2[i].getComponent();
						ref.getRefModel().setCacheEnabled(false);
						if ((items2[i].getKey().equalsIgnoreCase(
								"bd_psndoc.jobrank")
								|| items2[i].getKey().equalsIgnoreCase(
										"bd_psndoc.jobseries") || items2[i]
								.getKey().equalsIgnoreCase("bd_psndoc.series"))) {
							ref
									.getRefModel()
									.setWherePart(
											"pk_defdoclist = '"
													+ items2[i].getRefType()
													+ "'  and (pk_corp= '0001' or pk_corp= '"
													+ mainrefcorp
													+ "')  and (sealflag is null or sealflag<>'Y')");
						} else {
							ref
									.getRefModel()
									.setWherePart(
											"pk_defdoclist = '"
													+ items2[i].getRefType()
													+ "'  and (pk_corp= '0001' or pk_corp= '"
													+ curTempCorpPk
													+ "')  and (sealflag is null or sealflag<>'Y')");
						}
					} else if ("人员档案".equals(refNodeName)
							|| "人员档案HR".equals(refNodeName)) {
						if (Myref != null) {
							Myref.getRefModel().setUseDataPower(false);
							if (getModuleName().equalsIgnoreCase("600704")) {
								setWhereToModel(
										Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
							} else {
								setWhereToModel(Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
							}
						}
					} else if ("部门档案".equals(refNodeName)
							|| "部门档案HR".equals(refNodeName)
							|| "<nc.ui.hi.ref.DeptRefModel>"
									.equals(refNodeName)) {
						if (Myref != null) {
							setWhereToModel(Myref.getRefModel(),
									"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");

						}
					}
				}
			}
		}
		BillItem[] items = getCard().getHeadItems();// ((HIBillData)
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getDataType() == 5) { // 参照类型
					UIRefPane Myref = (UIRefPane) items[i].getComponent();
					String refNodeName = Myref.getRefNodeName();

					if (Util.isDefdocPk(items[i].getRefType())) {
						UIRefPane ref = (UIRefPane) items[i].getComponent();
						ref.getRefModel().setCacheEnabled(false);
						ref
								.getRefModel()
								.setWherePart(
										"pk_defdoclist = '"
												+ items[i].getRefType()
												+ "'  and (pk_corp= '0001' or pk_corp= '"
												+ curTempCorpPk
												+ "')  and (sealflag is null or sealflag<>'Y')");
					} else if ("人员档案".equals(refNodeName)
							|| "人员档案HR".equals(refNodeName)) {
						if (Myref != null) {
							Myref.getRefModel().setUseDataPower(false);
							if (getModuleName().equalsIgnoreCase("600704")) {
								setWhereToModel(
										Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
							} else {
								setWhereToModel(Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
							}
						}
					} else if ("部门档案".equals(refNodeName)
							|| "部门档案HR".equals(refNodeName)
							|| "<nc.ui.hi.ref.DeptRefModel>"
									.equals(refNodeName)) {
						if (Myref != null) {
							setWhereToModel(Myref.getRefModel(),
									"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
						}
					}
				}
			}
		}
		System.gc();
	}

	/**
	 * 改变各个参照的公司主键。
	 */
	private void changeBillTempRef(String pk_corp) {
		String mainrefcorp = pk_corp;
		BillItem clItem = getBillItem("bd_psndoc.pk_psncl");
		UIRefPane psnclsref = (UIRefPane) clItem.getComponent();
		nc.ui.hi.hi_301.ref.PsnClsRef psncls = null;
		psncls = new nc.ui.hi.hi_301.ref.PsnClsRef(mainrefcorp);
		psnclsref.setRefType(1);
		psnclsref.setRefInputType(1);
		psnclsref.setRefModel(psncls);
		BillItem deptItem = getBillItem("bd_psndoc.pk_deptdoc");
		UIRefPane deptref = (UIRefPane) deptItem.getComponent();
		SpaDeptdocRefTreeModel dept = null;
		dept = new SpaDeptdocRefTreeModel(mainrefcorp, false);
		//
		setWhereToModel(dept,
				"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");

		deptref.setRefType(1);
		deptref.setRefInputType(1);

		deptref.setRefModel(dept);
		// bd_psndoc.pk_om_job
		BillItem jobItem = getBillItem("bd_psndoc.pk_om_job");
		UIRefPane jobref = (UIRefPane) jobItem.getComponent();
		nc.ui.hi.ref.JobRef job = null;
		job = new nc.ui.hi.ref.JobRef(mainrefcorp, false);
		jobref.setRefType(1);
		jobref.setRefInputType(1);
		jobref.setRefModel(job);
		// bd_accpsndoc.dutyname
		BillItem dutyItem = getBillItem("bd_psndoc.dutyname");// bd_accpsndoc
		UIRefPane dutyref = (UIRefPane) dutyItem.getComponent();
		nc.ui.hi.ref.DutyRef duty = null;
		duty = new nc.ui.hi.ref.DutyRef(mainrefcorp);
		dutyref.setRefType(1);
		dutyref.setRefInputType(1);
		dutyref.setRefModel(duty);
		// hi_psndoc_deptchg.pk_psncl
		BillItem clItem2 = getBillItem("hi_psndoc_deptchg.pk_psncl");
		UIRefPane psnclsref2 = (UIRefPane) clItem2.getComponent();
		nc.ui.hi.hi_301.ref.PsnClsRef psncls2 = new nc.ui.hi.hi_301.ref.PsnClsRef(
				mainrefcorp);// wangkf fixed curTempCorpPk->mainrefcorp
		psnclsref2.setRefType(1);
		psnclsref2.setRefInputType(1);
		psnclsref2.setRefModel(psncls2);
		// hi_psndoc_deptchg.pk_deptdoc
		BillItem deptItem2 = getBillItem("hi_psndoc_deptchg.pk_deptdoc");
		UIRefPane deptref2 = (UIRefPane) deptItem2.getComponent();
		SpaDeptdocRefTreeModel dept2 = new SpaDeptdocRefTreeModel(mainrefcorp);// wangkf
		//
		setWhereToModel(dept2,
				"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");

		deptref2.setRefType(1);
		deptref2.setRefInputType(1);

		deptref2.setRefModel(dept2);
		// hi_psndoc_deptchg.pk_postdoc
		BillItem jobItem2 = getBillItem("hi_psndoc_deptchg.pk_postdoc");
		UIRefPane jobref2 = (UIRefPane) jobItem2.getComponent();
		nc.ui.hi.ref.JobRef job2 = null;
		job2 = new nc.ui.hi.ref.JobRef(mainrefcorp, false);// wangkf fixed
		// curTempCorpPk->mainrefcorp
		jobref2.setRefType(1);
		jobref2.setRefInputType(1);
		jobref2.setRefModel(job2);
		// hi_psndoc_deptchg.pk_om_duty
		// BillItem dutyItem2 = getBillItem("hi_psndoc_deptchg.pk_om_duty");
		UIRefPane dutyref2 = (UIRefPane) dutyItem.getComponent();
		nc.ui.hi.ref.DutyRef duty2 = null;
		duty2 = new nc.ui.hi.ref.DutyRef(mainrefcorp);// wangkf fixed
		// curTempCorpPk->mainrefcorp
		dutyref2.setRefType(1);
		dutyref2.setRefInputType(1);
		dutyref2.setRefModel(duty2);
		// hi_psndoc_req.pk_postrequire_h
		BillItem postrequire_hItem = getBillItem("hi_psndoc_req.pk_postrequire_h");
		UIRefPane postrequire_hItemref = (UIRefPane) postrequire_hItem
				.getComponent();
		nc.ui.hi.ref.RequireRef postrequire_h = new nc.ui.hi.ref.RequireRef(
				mainrefcorp);
		postrequire_hItemref.setRefType(1);
		postrequire_hItemref.setRefInputType(1);
		postrequire_hItemref.setRefModel(postrequire_h);
		// hi_psndoc_req.pk_postrequire_b
		BillItem postrequire_bItem = getBillItem("hi_psndoc_req.pk_postrequire_b");
		UIRefPane postrequire_bItemref = (UIRefPane) postrequire_bItem
				.getComponent();
		nc.ui.hi.ref.RequireLevelRef postrequire_b = new nc.ui.hi.ref.RequireLevelRef(
				mainrefcorp);
		postrequire_bItemref.setRefType(1);
		postrequire_bItemref.setRefInputType(1);
		postrequire_bItemref.setRefModel(postrequire_b);
		//
		BillItem[] items2 = getCard().getBodyItems();// ((HIBillData)
		// getCard().getBillData()).getBodyAllItems();
		if (items2 != null && items2.length > 0) {
			for (int i = 0; i < items2.length; i++) {
				if (items2[i].getDataType() == 5) { // 参照类型
					UIRefPane Myref = (UIRefPane) items2[i].getComponent();
					String refNodeName = Myref.getRefNodeName();

					if (Util.isDefdocPk(items2[i].getRefType())) {
						UIRefPane ref = (UIRefPane) items2[i].getComponent();
						ref.getRefModel().setCacheEnabled(false);
						if ((items2[i].getKey().equalsIgnoreCase(
								"bd_psndoc.jobrank")
								|| items2[i].getKey().equalsIgnoreCase(
										"bd_psndoc.jobseries") || items2[i]
								.getKey().equalsIgnoreCase("bd_psndoc.series"))) {
							ref
									.getRefModel()
									.setWherePart(
											"pk_defdoclist = '"
													+ items2[i].getRefType()
													+ "'  and (pk_corp= '0001' or pk_corp= '"
													+ mainrefcorp
													+ "')  and (sealflag is null or sealflag<>'Y')");
						}
					} else if ("人员档案".equals(refNodeName)
							|| "人员档案HR".equals(refNodeName)) {
						if (Myref != null) {
							Myref.getRefModel().setUseDataPower(false);
							if (getModuleName().equalsIgnoreCase("600704")) {
								setWhereToModel(
										Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
							} else {
								setWhereToModel(Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
							}
						}
					} else if ("部门档案".equals(refNodeName)
							|| "部门档案HR".equals(refNodeName)
							|| "<nc.ui.hi.ref.DeptRefModel>"
									.equals(refNodeName)) {
						if (Myref != null) {
							setWhereToModel(Myref.getRefModel(),
									"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
						}
					}
				}
			}
		}
		BillItem[] items = getCard().getHeadItems();// ((HIBillData)
		// getCard().getBillData()).getHeadItems();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getDataType() == 5) { // 参照类型
					UIRefPane Myref = (UIRefPane) items[i].getComponent();
					String refNodeName = Myref.getRefNodeName();

					if (Util.isDefdocPk(items[i].getRefType())) {
						UIRefPane ref = (UIRefPane) items[i].getComponent();
						ref.getRefModel().setCacheEnabled(false);
						ref
								.getRefModel()
								.setWherePart(
										"pk_defdoclist = '"
												+ items[i].getRefType()
												+ "'  and (pk_corp= '0001' or pk_corp= '"
												+ mainrefcorp
												+ "')  and (sealflag is null or sealflag<>'Y')");
					} else if ("人员档案".equals(refNodeName)
							|| "人员档案HR".equals(refNodeName)) {
						if (Myref != null) {
							Myref.getRefModel().setUseDataPower(false);
							if (getModuleName().equalsIgnoreCase("600704")) {
								setWhereToModel(
										Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
							} else {
								setWhereToModel(Myref.getRefModel(),
										"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
							}
						}
					} else if ("部门档案".equals(refNodeName)
							|| "部门档案HR".equals(refNodeName)
							|| "<nc.ui.hi.ref.DeptRefModel>"
									.equals(refNodeName)) {
						if (Myref != null) {
							setWhereToModel(Myref.getRefModel(),
									"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
						}
					}
				}
			}
		}
		System.gc();
	}

	/**
	 * 
	 * 
	 */
	private void changePsnclScope() {
		try {
			// 设置当前鼠标为忙状态
			{
				Util.getTopFrame(this).setCursor(
						Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// 当前选中归属范围类型
				Item item = (Item) getCmbPsncl().getSelectedItem();
				if (!item.isTempletLoaded()) {
					// 该类型的模版没有加载则加载
					initBillTemp();
					initUI();
				}
				// 显示相应的页
				CardLayout layout = (CardLayout) getPsnCard().getLayout();
				layout.show(getPsnCard(), item.getCardName());

			}
			// 刷新所有缓冲
			psnList = null;
			queryResult = null;
			person = null;
			psnDeptCache.clear();
			psnCorpCache.clear();
			persons.clear();
			// update by sunxj 2010-02-03 快速查询插件 start
			// if ((queryDlg != null && isQuery)) {// 记录是否查询过。
			if ((queryDlg != null && isQuery) || isQuickSearch) {// 记录是否查询过。
				// 初始化缺省数据
				initPsnListData();
			}

			if (!isHRCPInstalled) {
				Component[] components = getCard().getBodyTabbedPane()
						.getComponents();

				if (!ArrayUtils.isEmpty(components)) {
					for (Component component : components) {
						if (component instanceof BillScrollPane
								&& "hi_psndoc_capa"
										.equals(((BillScrollPane) component)
												.getTableCode())) {
							getCard().getBodyTabbedPane().remove(component);
							break;
						}
					}
				}
			}

			// update by sunxj 2010-02-03 快速查询插件 start
			// 恢复按钮状态
			Util.getTopFrame(this).setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (Exception e) {
			e.printStackTrace();
			{
				Util.getTopFrame(this).setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private final static int[] PSNCLSCOPE_GROUP = {
			nc.vo.hi.pub.CommonValue.PSNCLSCOPE_WORK, // 在职
			nc.vo.hi.pub.CommonValue.PSNCLSCOPE_DISMISS,// 解聘
			nc.vo.hi.pub.CommonValue.PSNCLSCOPE_RETIRE, // 离退
			nc.vo.hi.pub.CommonValue.PSNCLSCOPE_LEAVE, // 调离
			nc.vo.hi.pub.CommonValue.PSNCLSCOPE_OTHER // 其他
	};

	/**
	 * 得到选中的人员类别归属范围
	 * 
	 * @return
	 */
	private int getSelPsnclscope() {
		int index = getCmbPsncl().getSelectedIndex();
		if (index < 0 || index > PSNCLSCOPE_GROUP.length - 1)
			return -1;// 未定义的某个指
		return PSNCLSCOPE_GROUP[index];
	}

	/**
	 * 人员归属范围变化时更新参照。 创建日期：(2004-7-21 16:07:44)
	 */
	public void changePsnInfRef() {
		if (queryDlg != null) {
			try {
				// 获得查询模板参照
				nc.ui.pub.beans.UIRefPane psnref = (UIRefPane) queryDlg
						.getValueRefObjectByFieldCode("bd_psndoc.pk_psndoc");
				psnref.setRefType(1);
				psnref.setRefInputType(1);

				SpaDeptdocRefGridTreeModel psnrefmodel = new SpaDeptdocRefGridTreeModel();
				String wheresql = " bd_deptdoc.pk_deptdoc = V_HR_PSNDOC_MULTI_JOB.pk_deptdoc and bd_psndoc.dr = 0 "
						+ "and bd_psndoc.indocflag = '" + getIndocflag() + "' ";

				psnrefmodel.setCacheEnabled(false);
				if (getModuleName().equals("600707")
						|| getModuleName().equals("600708")) {
					wheresql += "and bd_psndoc.psnclscope = "
							+ getSelPsnclscope();
				}

				psnrefmodel.setUseDataPower(false);
				if (getModuleName().equalsIgnoreCase("600704")) {
					setWhereToModel(
							psnrefmodel,
							"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or "
									+ "( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
				} else {
					setWhereToModel(psnrefmodel,
							"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
					if ((powerSql.length() > 0 && powerSql.trim().startsWith(
							"select"))
							&& !getModuleName().equals("600704")) {
						setWhereToModel(psnrefmodel,
								"( bd_deptdoc.pk_deptdoc in (" + powerSql
										+ ") )");
					}
				}
				// 人员类别权限
				// 连接数优化
				// if (GlobalTool.isUsedDataPower("bd_psncl",
				// Global.getCorpPK())) {
				// String powerPsnclSql = nc.ui.hr.global.GlobalTool
				// .getPowerSql("bd_psncl", Global.getUserID(),
				// Global.getCorpPK());
				// if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
				// wheresql += " and bd_psndoc.pk_psncl in ("
				// + powerPsnclSql + ") ";
				// }
				// }
				String powerPsnclSql = nc.ui.hr.global.GlobalTool.getPowerSql(
						"bd_psncl", Global.getUserID(), Global.getCorpPK());
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					wheresql += " and bd_psndoc.pk_psncl in (" + powerPsnclSql
							+ ") ";
				}
				psnrefmodel.setWherePart(wheresql);
				psnref.setRefModel(psnrefmodel);
				// queryDlg.setValueRef("bd_psndoc", "bd_psndoc.pk_psndoc",
				// psnref);
				// ConditionVO[] vvos = queryDlg.getConditionVO();
				// queryDlg.initData();
				// if(vvos!=null&&vvos.length>0){
				// queryDlg.changeDefaultConditions(vvos);
				// }
			} catch (Exception e) {
			}
		}
	}

	public void adjustQueryDialog() {
		if (queryDialog == null)
			return;
		QueryTempletService qts = new QueryTempletService();
		Item item = (Item) getCmbPsncl().getSelectedItem();
		// 调整“是否返聘”可见
		if (item.getValue() == 0
				&& (getModuleName().equalsIgnoreCase("600707") || getModuleName()
						.equalsIgnoreCase("600708"))) {
			qts.addFilterByCode(queryDialog, "hi_psndoc_deptchg.isreturn");
		} else if (item.getValue() != 0
				&& (getModuleName().equalsIgnoreCase("600707") || getModuleName()
						.equalsIgnoreCase("600708"))) {
			qts.removeFilterByCode(queryDialog, "hi_psndoc_deptchg.isreturn");
		}
		// 设置参照过滤条件。
		qts.fireCriteriaChanged(queryDialog, "bd_psndoc.pk_psndoc");

		qts.fireCriteriaChanged(queryDialog, "bd_psndoc.pk_psncl");
	}

	/**
	 * 检查节点可入性。 创建日期：(2004-6-5 9:06:19)
	 */
	protected boolean checkEntrance() {// wangkf fixed void->boolean
		return true;// wangkf add
	}

	/**
	 * 检查输入的有效性。
	 * 
	 * @param eavo
	 * @param tableCodes
	 * @throws java.lang.Exception
	 */
	private void validateInput(PersonEAVO eavo, String[] tableCodes)
			throws java.lang.Exception {
		try {
			// 设置维护标识
			setMaintainMarkBeforeValidation(eavo);
			// 检查tableCodes表输入的有效性
			if (tableCodes != null)
				for (int i = 0; i < tableCodes.length; i++) {
					eavo.validate(tableCodes[i]);
				}
			// 删除维护标识
			eavo.getPsndocVO().removeAttributeName("maintain");
		} catch (ValidException ve) {
			// 具体化报错信息位置
			traceErrorPoint(ve);

		}
	}

	/**
	 * 转入人员档案的人必须已经有部门，检查。 创建日期：(2004-5-30 13:22:15)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void checkPsnListDept(GeneralVO[] psnListTemp)
			throws java.lang.Exception {
		if (psnListTemp != null) {
			for (int i = 0; i < psnListTemp.length; i++) {
				String pk_deptdoc = (String) psnListTemp[i]
						.getAttributeValue("pk_deptdoc");
				String psnname = (String) psnListTemp[i]
						.getAttributeValue("psnname");
				FieldValidator.validate(psnname
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000067")/* @res "的部门" */,
						"notnull", pk_deptdoc);
			}
		}
	}

	/**
	 * 转入人员档案的人必须已经有人员编码，检查。
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void checkPsnListPsnCode(GeneralVO[] psnListTemp)
			throws ValidationException {
		if (psnListTemp != null) {
			for (int i = 0; i < psnListTemp.length; i++) {
				String psncode = (String) psnListTemp[i]
						.getAttributeValue("psncode");
				String psnname = (String) psnListTemp[i]
						.getAttributeValue("psnname");
				try {
					// FieldValidator.validate(psnname + "的人员编码",
					// "notnull",psncode);
					FieldValidator.validate(psnname
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000107")/*
																	 * @res
																	 * "的人员编码"
																	 */, "notnull", psncode);
				} catch (Exception e) {
					throw new ValidationException(e.getMessage());
				}
			}
		}
	}

	/**
	 * 
	 * @param psnListTemp
	 */
	private void autoSetInDocPsnCode(GeneralVO[] psnListTemp)
			throws java.lang.Exception {
		if (psnListTemp == null) {
			return;
		}
		retrivePsncodeInGroup();
		Vector v = new Vector();
		GeneralVO[] vos = null;
		for (int i = 0; i < psnListTemp.length; i++) {
			String pk_psndoc = (String) psnListTemp[i]
					.getAttributeValue("pk_psndoc");
			Object psncode = psnListTemp[i].getAttributeValue("psncode");
			String newpsncode = null;
			if (psncode == null
					|| "".equalsIgnoreCase(psncode.toString().trim())) {
				if ("Y".equalsIgnoreCase(isUniquePsncodeInGroup)) // 如果是集团内唯一，则按集团产生人员编码
				{
					newpsncode = HIDelegator.getBillcodeRule()
							.getBillCode_RequiresNew("BS", "0001", null,
									getObjVO());
				} else {
					newpsncode = HIDelegator.getBillcodeRule()
							.getBillCode_RequiresNew("BS", Global.getCorpPK(),
									null, getObjVO());
				}

				GeneralVO vo = new GeneralVO();
				vo.setAttributeValue("pk_psndoc", pk_psndoc);
				vo.setAttributeValue("psncode", newpsncode);
				v.addElement(vo);
			}
		}
		if (v.size() > 0) {
			vos = new GeneralVO[v.size()];
			v.copyInto(vos);
		}
		HIDelegator.getPsnInf().batchUpdatePsnCode(vos);

	}

	/**
	 * 人员范围下拉框的响应事件处理代码
	 */
	public void cmbPsnclItemStateChanged(java.awt.event.ItemEvent itemEvent) {
		if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
			// if (isInitByPsnclScope) {// 在第一次启动该模块时，不调用此方法 V35 add
			Item item = (Item) getCmbPsncl().getSelectedItem();
			if (item.getValue() > 0) {// && item.getValue()!= 5
				getUILabelType().setVisible(false);
				getCmbJobType().setVisible(false);
			} else {
				getUILabelType().setVisible(true);
				getCmbJobType().setVisible(true);
			}
			// 如果选中，启动加载模板的线程。
			// changePsnInfRef();
			// 改变人员范围后要改变查询模板，用adjustQueryDialog取代changePsnInfRef方法。
			adjustQueryDialog();
			changePsnclScope();
			// 如果选择“其他人员”，则“取消引用”按钮可用，否则不可用。
			if (item.getValue() == 5) {
				bnCancelAffirm.setEnabled(true);
			} else {
				bnCancelAffirm.setEnabled(false);
			}
			updateButtons();
			listSelectRow = -1;
		}
	}

	public void cmbJobTypItemStateChanged(java.awt.event.ItemEvent e) {
		try {
			// 根据任职类型过滤人员
			Util.getTopFrame(this).setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// 刷新所有缓冲
			psnList = null;
			queryResult = null;
			person = null;
			psnDeptCache.clear();
			psnCorpCache.clear();
			persons.clear();
			listSelectRow = -1;
			if (queryDlg != null) {// 记录是否查询过。
				initPsnListData();
			}
			String tableCode = getBodyTableCode();
			getCard().setBodyMenuShow(tableCode, false);
			person = loadPsnChildInfo(listSelectRow, tableCode);
			// 恢复按钮状态
			Util.getTopFrame(this).setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}

			handleException(ivjExc);
		}
	}

	/**
	 * 
	 * @param arg1
	 */
	public void connEtoC1(java.awt.event.ItemEvent arg1) {
		try {
			this.cmbPsnclItemStateChanged(arg1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	/**
	 * 
	 * @param arg1
	 */
	private void connEtoC2(javax.swing.event.TreeSelectionEvent arg1) {
		try {
			this.deptTreeValueChanged(arg1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	/**
	 * 
	 * @param arg1
	 */
	private void connEtoC3(java.awt.event.MouseEvent arg1) {
		try {
			this.deptTreeMouseClicked(arg1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	/**
	 * 主集记录的唯一性校验
	 * 
	 * @param tablecode
	 * @param onePerson
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean dataUniqueValidate(String tablecode, PersonEAVO onePerson)
			throws java.lang.Exception {
		if (!"bd_psnbasdoc".equalsIgnoreCase(tablecode))
			return true;
		String message = null;
		ValidException vException = null;
		PsndocConsItmVO[] uniqueFields = HIDelegator.getPsnDocQueryService()
				.getPsndocUniqueFields();
		if (uniqueFields != null && uniqueFields.length > 0) {
			// ---唯一组合字段非空校验--begin
			String nullMsg = null;
			for (int i = 0; i < uniqueFields.length; i++) {
				String field_name = uniqueFields[i].getField_name();
				String display_name = null;// conitmUniqueFields[i].getItem_name();
				Object value = null;
				// if ("psnname".equalsIgnoreCase(field_name)) {
				// value = onePerson.getPsndocVO().getAttributeValue(
				// field_name);
				// } else {
				value = onePerson.getAccpsndocVO()
						.getAttributeValue(field_name);
				// }
				if (value == null
						|| value.toString().trim().equalsIgnoreCase("")) {
					if (nullMsg == null) {
						nullMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("600704", "UPP600704-000241")/*
																			 * @res
																			 * "唯一组合字段"
																			 */
								+ ":"
								+ display_name
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("600704",
												"UPP600704-000156")/*
																	 * @res
																	 * "不能为空"
																	 */
								+ " ,\n";
					} else {
						nullMsg += display_name
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("600704",
												"UPP600704-000156")/*
																	 * @res
																	 * "不能为空"
																	 */
								+ " ,\n";
					}
				}
				if (nullMsg != null) {
					vException = new ValidException(nullMsg);
					vException.setTableCode(tablecode);
					vException.setFieldCode(field_name);
					traceErrorPoint(vException);
				}
			}
			onePerson.getAccpsndocVO().setAttributeValue("pk_psnbasdoc",
					onePerson.getPk_psnbasdoc());
			// onePerson.getAccpsndocVO().setAttributeValue("psnname",
			// onePerson.getPsndocVO().getAttributeValue("psnname"));
			String uniqueMsg = HIDelegator.getPsnInf().dataUniqueValidate(
					Global.getCorpPK(), uniqueFields,
					onePerson.getAccpsndocVO());
			onePerson.getAccpsndocVO().removeAttributeName("pk_psnbasdoc");
			if (uniqueMsg != null) {
				message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("600704", "UPP600704-000242")/*
																	 * @res
																	 * "该人员已经存在,其详细信息为:"
																	 */
						+ uniqueMsg;
				vException = new ValidException(message);
				vException.setTableCode(tablecode);
				vException.setFieldCode(uniqueFields[0].getField_name());
			}
		} else {
			String psnname = (String) onePerson.getAccpsndocVO()
					.getAttributeValue("psnname");
			String id = (String) onePerson.getAccpsndocVO().getAttributeValue(
					"id");
			String pk_psnbasdoc = (String) onePerson.getPsndocVO()
					.getAttributeValue("pk_psnbasdoc");
			String pk_psndoc = (String) onePerson.getPsndocVO()
					.getAttributeValue("pk_psndoc");
			if (id != null && id.trim().length() > 0) {
				String whereid = "";
				if (id.length() == 18) {
					String id15 = id.substring(0, 6) + id.substring(8, 17);
					whereid = "(ltrim(rtrim(bd_psnbasdoc.id))||bd_psnbasdoc.psnname = '"
							+ id
							+ psnname
							+ "' or ltrim(rtrim(bd_psnbasdoc.id))||bd_psnbasdoc.psnname = '"
							+ id15 + psnname + "')";
				} else if (id.length() == 15) {// v53
					IDValidateUtil idutil = new IDValidateUtil(id);
					String id18 = idutil.getUpgradeId();// v53
					whereid = "(ltrim(rtrim(bd_psnbasdoc.id))||bd_psnbasdoc.psnname = '"
							+ id
							+ psnname
							+ "' or ltrim(rtrim(bd_psnbasdoc.id))||bd_psnbasdoc.psnname = '"
							+ id18 + psnname + "')";
				} else {
					whereid = "ltrim(rtrim(bd_psnbasdoc.id))||bd_psnbasdoc.psnname = '"
							+ id + psnname + "'";
				}
				String sql = "select 1 from bd_psnbasdoc where " + whereid
						+ "and bd_psnbasdoc.pk_psnbasdoc <> '" + pk_psnbasdoc
						+ "'";

				boolean exists = HIDelegator.getPsnInf().isRecordExist(sql);
				if (exists) {

					GeneralVO vo = HIDelegator.getPsnInf().queryPsnInfo(
							pk_psndoc, id, psnname);
					// if(vo!=null){
					String errordisplayText = "  ";// 无
					String psncode = errordisplayText;
					String deptname = errordisplayText;
					String omjob = errordisplayText;
					String unitname = errordisplayText;
					if (vo != null) {
						psncode = (String) vo.getAttributeValue("psncode");
						deptname = (String) vo.getAttributeValue("deptname");
						deptname = (deptname == null ? errordisplayText
								: deptname.trim());
						omjob = (String) vo.getAttributeValue("jobname");
						omjob = (omjob == null ? errordisplayText : omjob
								.trim());
						unitname = (String) vo.getAttributeValue("unitname");
						unitname = (unitname == null ? errordisplayText
								: unitname.trim());
					}
					if (psncode == null || psncode.length() == 0) {
						if (id != null && id.trim().length() == 18) {
							String id15 = id.substring(0, 6)
									+ id.substring(8, 17);
							vo = HIDelegator.getPsnInf().queryPsnInfo(
									pk_psndoc, id15, psnname);
							if (vo != null) {
								psncode = (String) vo
										.getAttributeValue("psncode");
								deptname = (String) vo
										.getAttributeValue("deptname");
								deptname = (deptname == null ? errordisplayText
										: deptname.trim());
								omjob = (String) vo
										.getAttributeValue("jobname");
								omjob = (omjob == null ? errordisplayText
										: omjob.trim());
								unitname = (String) vo
										.getAttributeValue("unitname");
								unitname = (unitname == null ? errordisplayText
										: unitname.trim());
							}
						} else if (id != null && id.trim().length() == 15) {
							IDValidateUtil idutil = new IDValidateUtil(id);
							String id18 = idutil.getUpgradeId();// v53
							vo = HIDelegator.getPsnInf().queryPsnInfo(
									pk_psndoc, id18, psnname);
							if (vo != null) {
								psncode = (String) vo
										.getAttributeValue("psncode");
								deptname = (String) vo
										.getAttributeValue("deptname");
								deptname = (deptname == null ? errordisplayText
										: deptname.trim());
								omjob = (String) vo
										.getAttributeValue("jobname");
								omjob = (omjob == null ? errordisplayText
										: omjob.trim());
								unitname = (String) vo
										.getAttributeValue("unitname");
								unitname = (unitname == null ? errordisplayText
										: unitname.trim());
							}
						}
					}
					throw new Exception(unitname
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("600704", "UPP600704-000126")/*
																				 * @res
																				 * "中已有身份证号码为 "
																				 */
							+ id
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("600704", "UPP600704-000127")/*
																				 * @res
																				 * ",人员姓名为 "
																				 */
							+ psnname
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("600704", "UPP600704-000128")/*
																				 * @res
																				 * ",人员编码为 "
																				 */
							+ psncode
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("600704", "UPP600704-100001")/*
																				 * @res
																				 * "的记录,现在在 "
																				 */
							+ deptname
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("600704", "UPP600704-100002")/*
																				 * @res
																				 * "部门, "
																				 */
							+ omjob
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("600704", "UPP600704-100003")/*
																				 * @res
																				 * "岗位 "
																				 */);
				}
			}
		}

		if (vException != null) {
			traceErrorPoint(vException);
		}
		return true;
	}

	/**
	 * 校验非空。 创建日期：(2005-5-9 11:49:00)
	 * 
	 * @param tablecode
	 *            java.lang.String
	 * @param vos
	 *            nc.vo.hi.hi_301.GeneralVO[]
	 */
	public boolean dataNotNullValidate(String tablecode, GeneralVO[] vos,
			int curRow) throws java.lang.Exception {
		String message = null;
		ValidException vException = null;// wangkf add
		if ("bd_psndoc,bd_psnbasdoc".indexOf(tablecode) >= 0) {
			BillItem[] headItems = getCard().getBillData().getHeadTailItems();
			if (headItems != null && headItems.length > 0) {
				for (int i = 0; i < headItems.length; i++) {
					BillItem item = headItems[i];
					if (!item.isNull() || !isNULL(item.getValue()))
						continue;
					if (item.getKey().equalsIgnoreCase("psncode")
							&& isPsncodeAutoGenerate())
						continue;
					message = item.getName();
					message += nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"600704", "UPP600704-000156")/*
															 * @res "不能为空"
															 */;
					// wangkf add
					vException = new ValidException(message);
					vException.setTableCode(item.getTableCode());
					vException.setFieldCode(item.getKey());
					vException.setLineNo(curRow);// -1
					break;
				}
			}
			// 采集节点，如果启用部门档案权限，必须输入部门
			if (isUsedDeptPower == null)
				isUsedDeptPower = GlobalTool.isUsedDataPower("bd_deptdoc",
						"部门档案", Global.getCorpPK());
			if ("600704".equalsIgnoreCase(getModuleName()) && isUsedDeptPower) {
				BillItem item = getCard().getHeadItem("pk_deptdoc");
				if (item == null || isNULL(item.getValue())) {
					message = item.getName();
					message += nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"600704", "UPP600704-000156")/*
															 * @res "不能为空"
															 */;
					// wangkf add
					vException = new ValidException(message);
					vException.setTableCode(item.getTableCode());
					vException.setFieldCode(item.getKey());
					vException.setLineNo(curRow);// -1
				}
			}
		} else {
			BillItem[] bodyItems = getCard().getBillData()
					.getBodyItemsForTable(tablecode);
			if (bodyItems != null && bodyItems.length > 0) {
				for (int j = 0; j < bodyItems.length; j++) {
					BillItem item = bodyItems[j];
					Object value = vos[0].getAttributeValue(item.getKey());
					if (!item.isNull() || !isNULL(value))
						continue;
					message = item.getName();
					message += nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"600704", "UPP600704-000156")/* @res "不能为空" */;
					vException = new ValidException(message);
					vException.setTableCode(item.getTableCode());
					vException.setFieldCode(item.getKey());
					vException.setLineNo(curRow);
					break;
				}
			}
		}
		if (vException != null) {
			traceErrorPoint(vException);
		}
		return true;
	}

	/**
	 * 校验非空。 创建日期：(2005-5-9 11:49:00)
	 * 
	 * @param tablecode
	 *            java.lang.String
	 * @param vos
	 *            nc.vo.hi.hi_301.GeneralVO[]
	 */
	public boolean dataNotNullValidateforRef(int curRow)
			throws java.lang.Exception {
		String message = null;
		ValidException vException = null;
		BillItem[] headItems = getCard().getBillData().getHeadTailItems();
		final String keys = "psncode,psnname,pk_psncl,pk_deptdoc";
		if (headItems != null) {
			for (int i = 0; i < headItems.length; i++) {
				if (keys.indexOf(headItems[i].getKey()) < 0)
					continue;
				if (!headItems[i].isNull() || !isNULL(headItems[i].getValue()))
					continue;

				if (headItems[i].getKey().equalsIgnoreCase("psncode")
						&& isPsncodeAutoGenerate()) {
					continue;
				}
				message = headItems[i].getName();
				message += nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600704", "UPP600704-000156")/*
														 * @res "不能为空"
														 */;
				// wangkf add
				vException = new ValidException(message);
				vException.setTableCode(headItems[i].getTableCode());
				vException.setFieldCode(headItems[i].getKey());
				vException.setLineNo(curRow);// -1
				break;
			}
		}

		if (vException != null) {
			traceErrorPoint(vException);
		}
		return true;
	}

	/**
	 * 从queryResult中将当前psnList中对应人员全部删除， 用于人员转储入档案后，将当前人员删除。 创建日期：(2004-5-25
	 * 13:45:37)
	 */
	private void deleteListFromResult(GeneralVO[] curIntoDocData) {
		// 将删除后的结果保存到v
		Vector v = new Vector();
		// 遍历queryResult中的每一个元素,在psnList查找当前元素
		for (int i = 0; i < queryResult.length; i++) {
			int j;
			for (j = 0; j < curIntoDocData.length; j++) {
				if (curIntoDocData[j].getAttributeValue("pk_psndoc").equals(
						queryResult[i].getAttributeValue("pk_psndoc")))
					break;
			}
			// 如果psnList不包含当前元素，添加到v
			if (j == curIntoDocData.length)
				v.addElement(queryResult[i]);
		}

		// 转化成数组
		queryResult = (GeneralVO[]) v.toArray(new GeneralVO[0]);
	}

	/**
	 * 从数组persons查找出人员主键是pk_psndoc的人员，并将之删除。 创建日期：(2004-5-19 11:05:14)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param persons
	 *            nc.vo.hi.hi_301.GeneralVO[]
	 * @param pk_psndoc
	 *            java.lang.String
	 */
	protected GeneralVO[] deletePsnFromArray(GeneralVO[] persons,
			String pk_psndoc) {
		// 顺序查找
		int i;
		for (i = 0; i < persons.length; i++) {
			String pk = (String) persons[i].getAttributeValue("pk_psndoc");
			if (pk != null && pk.equals(pk_psndoc)) {
				break;
			}
		}

		// 没有查找该人，则直接返回原来数组
		if (i == persons.length)
			return persons;

		// 数组长度缩小1，并将通过arraycopy高效删除该元素。
		GeneralVO[] newPersons = new GeneralVO[persons.length - 1];
		System.arraycopy(persons, 0, newPersons, 0, i);
		System.arraycopy(persons, i + 1, newPersons, i, persons.length - i - 1);

		return newPersons;
	}

	/**
	 * Comment
	 */
	public void deptTreeMouseClicked(java.awt.event.MouseEvent mouseEvent)
			throws Exception {
		try {
			if (mouseEvent.getClickCount() == 2) {
				// System.out.println("双击事件");
				TreePath path = getDeptTree().getPathForLocation(
						mouseEvent.getX(), mouseEvent.getY());
				if (path == null)
					return;
				DefaultMutableTreeNode node = getSelectedNode();
				if (node == null) {
					return;
				}
				CtrlDeptVO corpvo = (CtrlDeptVO) node.getUserObject();
				if (!corpvo.isControlled()
						|| corpvo.nodeType == CtrlDeptVO.DEPT) {
					return;
				}
				if (corpvo.isLoadDept()) {
					return;
				}
				Util.getTopFrame(this).setCursor(
						Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				corpvo.removeAllDeptChild();
				String strDeptPower = getDeptPowerByCorp(corpvo.getPk_corp());
				boolean isUsedPower = strDeptPower.length() > 3;

				corpvo.setModuleCode(getModuleName());
				boolean includeHrCanceld = getincludeCancleDept().isSelected();// v53
				corpvo = HIDelegator.getPsnInf().queryCorpCtrlDepts(
						Global.getUserID(), corpvo, isUsedPower,
						includeHrCanceld);
				node.setUserObject(corpvo);
				corpvo.setLoadDept(true);
				addDeptNodeToTree(node, corpvo);

				getDeptTreeModel().reload(node);
				getDeptTree().setCellRenderer(new DeptTreeCellRender());
				getDeptTree().expandPath(path);
				Util.getTopFrame(this).setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		} catch (Exception e) {
			Util.getTopFrame(this).setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 部门树的节点被选中时触发的事件处理
	 */
	protected void deptTreeValueChanged(
			javax.swing.event.TreeSelectionEvent treeSelectionEvent)
			throws java.lang.Exception {
		// 默认需要重新刷新
		deptTreeValueChanged(treeSelectionEvent, true);
	}

	/**
	 * 部门树的节点被选中时触发的事件处理
	 */
	protected void deptTreeValueChanged(
			javax.swing.event.TreeSelectionEvent treeSelectionEvent,
			boolean isNeedQuery) throws java.lang.Exception {
		// 当前选中的节点
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null)
			return;
		// 如果当前节点为不可控制部门，清空
		if (queryDlg != null && isQuery && isNeedQuery) {
			queryResult();
		}
		// add by sunxj 2010-02-03 快速查询插件 start
		else if (isQuickSearch && isNeedQuery) {
			queryResult(quickWherePart);
		}
		// add by sunxj 2010-02-03 快速查询插件 end
		psnList = queryResult;
		// 
		getPsnList().setBodyData(psnList);
		getPsnList().getTableModel().execLoadFormula();

		String recordCountDecrip = nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"600704", "UPP600704-000192")/* @res "共有" */;
		String tiao = nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
				"UPP600704-000240")/* @res "条" */;
		// Integer maxline = PubDelegator.getIParValue().getParaInt("0001",
		// "HI_MAXLINE");//效率优化
		Integer maxline = Integer.parseInt(getPara("HI_MAXLINE"));
		String max = ","
				+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
						"UPP600704-000239")/* @res "显示前" */
				+ (maxline == null ? 1000 : maxline.intValue())
				+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
						"UPP600704-000240")/* @res "条" */;
		if (recordcount <= (maxline == null ? 1000 : maxline.intValue())) {
			max = "";
		}
		getUILabelRecords().setText(
				recordCountDecrip + recordcount + tiao + max + "   ");

		setTreeSelectButtonState(node);
	}

	/**
	 * 设置当前子表的第no行可编辑。 创建日期：(2004-5-26 21:22:27)
	 * 
	 * @param no
	 *            int
	 */
	private void enableTableLine(int no) {
		// 禁止整个表不能编辑
		setTableLineEditable(getCard().getBillModel(), -1, false);

		// 设置该行可编辑
		setTableLineEditable(getCard().getBillModel(), no, true);

		// 设置当前值并触发触发器
		String tableCode = getBodyTableCode();
		String[] seqFields = (String[]) triggerTable.fieldAOE.get(tableCode);

		// 如果没有触发器，则退出
		if (seqFields == null)
			return;

		// 获得当前记录
		CircularlyAccessibleValueObject record = person.getTableVO(tableCode)[getEditLineNo()];
		for (int i = 0; i < seqFields.length; i++) {
			BillItem item = getBillItem(tableCode + "." + seqFields[i]);
			if (item == null)
				continue;
			if (item.getDataType() == IBillItem.UFREF) {
				// 如果是参照类型，则需要触发
				// 其他组件，在设置数值时就已经触发，这是由于UIRefPane设置值时不能自动触发事件造成的
				Object v = record.getAttributeValue(Util.PK_PREFIX
						+ item.getKey());
				if (v == null)
					continue;
				((UIRefPane) item.getComponent()).setPK(v);
			}
		}
	}

	/**
	 * 设置当前子表的第no行可编辑。 wangkf add 编辑行调用此方法
	 * 
	 * @param no
	 *            int
	 */
	private void enableTableLineModify(int no) {
		// 禁止整个表不能编辑
		setTableLineEditable(getCard().getBillModel(), -1, false);

		// 设置该行可编辑
		setTableLineEditable(getCard().getBillModel(), no, true);
	}

	/**
	 * V31SP1 照片导出 功能。 创建日期：(2005-4-28 14:29:10)
	 */
	public void exportPic() {
		GeneralVO[] psnPic = new GeneralVO[0];
		String strFileName = "";
		try {
			// 判断是否选择有人
			int row = getPsnList().getTable().getSelectedRow();
			if (row != -1 && row < psnList.length) {
				// 取当前人员的主键
				String[] keys = getTempPsnPk();
				String pk_psnbasdoc = keys[0];
				// 当前登录公司
				String corp = Global.getCorpPK();
				// 按照人员编码
				psnPic = HIDelegator.getPsnInf().queryByPsnPK(corp,
						pk_psnbasdoc);
				if (psnPic == null || psnPic.length <= 0) {
					String photoTitle = nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("600704", "UPP600704-000017")/*
																		 * @res
																		 * "警告"
																		 */;
					String photoMsg = nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("600704", "UPP600704-000183")/*
																		 * @res
																		 * "该员工没有照片"
																		 */;
					nc.ui.pub.beans.MessageDialog.showHintDlg(this, photoTitle,
							photoMsg);
					return;
				}
			} else {
				return;
			}
			// 获取合适的文件名称:工号+姓名+部门名称
			strFileName = "/" + strFileName
					+ psnList[row].getAttributeValue("psncode").toString();
			strFileName = strFileName
					+ psnList[row].getAttributeValue("psnname").toString();
			String photoDeptName = (psnList[row].getAttributeValue("deptname") == null ? ""
					: psnList[row].getAttributeValue("deptname").toString());
			strFileName = strFileName + photoDeptName;
			strFileName = strFileName + ".jpg";
			File file = new File(strFileName);
			getFileChooser().setFileSelectionMode(
					javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
			getFileChooser().setSelectedFile(file);
			int userOperate = getFileChooser().showSaveDialog(this);
			if (userOperate == 0) {// 0:保存,1:撤销
				file = getFileChooser().getSelectedFile();
				if (file == null) {// 不会走到此分支--wangkf 说明
					// nc.ui.pub.beans.MessageDialog.showHintDlg(this,
					// "警告","您没有选择要下载的目录！");
					nc.ui.pub.beans.MessageDialog.showHintDlg(this,
							nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000108")/*
																	 * @res "警告"
																	 */, nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000109")/*
																	 * @res
																	 * "您没有选择要下载的目录！"
																	 */
					);
					return;
				}
				byte[] data = (byte[]) psnPic[0].getFieldValue("photo");
				FileOutputStream outstream = new FileOutputStream(file);
				outstream.write(data);
				outstream.flush();
				outstream.close();
			}
		} catch (Exception e) {
			reportException(e);
		}
		return;
	}

	/**
	 * 从查询结果queryResult查找人员主键为pk_psndoc的人员，返回该人在queryResult中的索引。 创建日期：(2004-5-20
	 * 14:27:15)
	 * 
	 * @return int
	 * @param psn
	 *            nc.vo.hi.hi_301.GeneralVO
	 */
	private int findPersonInQueryResult(String pk_psndoc) {
		// 如果queryResult，返回-1
		if (queryResult == null) {
			return -1;
		}
		// 人员主键是null,返回-1
		if (pk_psndoc == null) {
			return -1;
		}
		// 顺序查找该人
		for (int i = 0; i < queryResult.length; i++) {
			String pk = (String) queryResult[i].getAttributeValue("pk_psndoc");
			if (pk != null && pk.equals(pk_psndoc))
				return i;
		}
		// 没有找到,返回-1
		return -1;
	}

	/**
	 * V35 add wangkf 从指定的数据中 查找所在数据的位置
	 * 
	 * @param pk
	 * @param pk_field
	 * @param datas
	 * @return
	 */
	protected int findPersonPos(String pk, String pk_field, GeneralVO[] datas) {
		if (datas == null) {
			return -1;
		}
		if (pk == null) {
			return -1;
		}
		// 顺序查找该人
		for (int i = 0; i < datas.length; i++) {
			String pk_src = (String) datas[i].getAttributeValue(pk_field);
			if (pk_src != null && pk_src.equals(pk))
				return i;
		}
		return -1;
	}

	/**
	 * 解锁记录。 创建日期：(2004-7-14 19:12:37)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	public void freeLockPsn(String psnpk) throws java.lang.Exception {
		try {
			PKLock.getInstance().releaseLock(psnpk,
					nc.ui.hr.global.Global.getUserID(), null);
		} catch (Exception e) {
			reportException(e);
			throw e;
		}
	}

	/**
	 * 得到所有的数据项表达式数组 也就是返回所有定义的数据项的表达式
	 */
	public java.lang.String[] getAllDataItemExpress() {
		return PersonEAVO.mainFields;
	}

	public java.lang.String[] getAllDataItemNames() {
		return null;
	}

	/**
	 * @return 返回 attributesOfSort。
	 */
	public Vector getAttributesOfSort() {
		/** 保存排序* */
		java.util.Vector attributes = null;
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null) {
			attributes = (java.util.Vector) hmSortCondition.get("all");
		} else {
			CtrlDeptVO dept = getSelectedDept();
			if (node.isRoot()) {
				attributes = (java.util.Vector) hmSortCondition.get("all");
			} else if (dept.isControlled()) {
				if (dept.getNodeType() == CtrlDeptVO.DEPT) {
					attributes = (java.util.Vector) hmSortCondition.get(dept
							.getPk_dept());
				} else if (dept.getNodeType() == CtrlDeptVO.CORP) {
					attributes = (java.util.Vector) hmSortCondition.get(dept
							.getPk_corp());
				}
			}
		}
		return attributes;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-5-30 16:15:45)
	 * 
	 * @return nc.ui.hi.hi_301.BatchUpdateDlg
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected BatchUpdateDlg getBatchDlg() throws java.lang.Exception {
		if (batchDlg == null) {
			batchDlg = new BatchUpdateDlg(this, nc.ui.ml.NCLangRes
					.getInstance().getStrByID("600704", "upt600704-000164")/*
																			 * @res
																			 * "批量修改"
																			 */);
			batchDlg.setLocationRelativeTo(this);
		}
		return batchDlg;
	}

	/**
	 * 获取单据模板的字段映射表，从该映射表中能够高速查询改字段的属性。 创建日期：(2004-5-25 14:19:39)
	 * 
	 * @return java.util.Hashtable
	 */
	private Hashtable getBillBodyMap() {
		return getCard().getBillBodyMap();
	}

	/**
	 * 根据tablefield获得该字段的BillItem。
	 * 
	 * @param tablefield
	 * @return
	 */
	private BillItem getBillItem(String tablefield) {
		int dot = tablefield.indexOf(".");
		String tableCode = tablefield.substring(0, dot);
		String fieldCode = tablefield.substring(dot + 1);
		if (headTables.get(tableCode) != null)
			
			return getCard().getHeadItem(fieldCode);
		else
			return getCard().getBodyItem(tableCode, fieldCode);
	}

	/**
	 * 返回当前节点的单据模板类型。 创建日期：(2004-5-12 20:50:29)
	 * 
	 * @return java.lang.String
	 */
	protected String getBillType() {
		return getModuleName();
	}

	/**
	 * 返回当前表体选中的物理表的名称。 创建日期：(2004-5-26 14:26:22)
	 * 
	 * @return java.lang.String
	 */
	protected String getBodyTableCode() {
		return getCard().getBodyTableCode(getCard().getBodyPanel());
	}

	/**
	 * 返回当前表体选中的物理表的显示名称。 创建日期：(2004-5-25 16:11:23)
	 * 
	 * @return java.lang.String
	 */
	private String getBodyTableName() {
		return getCard().getBillData().getBodyTableName(getBodyTableCode());
	}

	/**
	 * 返回当前单据。 创建日期：(2004-5-11 21:08:01)
	 * 
	 * @return nc.ui.hi.hi_301.HIBillCardPanel
	 */
	protected HIBillCardPanel getCard() {
		// 当前节点是人员信息采集节点使用在职模板
		return getWorkCard();
	}

	/**
	 * 返回 CenterPanel 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getCenterPanel() {
		if (ivjCenterPanel == null) {
			try {
				ivjCenterPanel = new nc.ui.pub.beans.UIPanel();
				ivjCenterPanel.setName("CenterPanel");
				ivjCenterPanel.setLayout(new java.awt.BorderLayout());
				getCenterPanel().add(getUISplitPane(), "Center");
				ivjCenterPanel.addComponentListener(new ComponentAdapter() {
					public void componentResized(ComponentEvent event) {
						if (isShowBillTemp) {// 如果是卡片界面
							getUISplitPane().setDividerLocation(0);
						}
					}
				});
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCenterPanel;
	}

	/**
	 * 返回 CenterPanelRight 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	protected nc.ui.pub.beans.UIPanel getCenterPanelRight() {
		if (ivjCenterPanelRight == null) {
			try {
				ivjCenterPanelRight = new nc.ui.pub.beans.UIPanel();
				ivjCenterPanelRight.setName("CenterPanelRight");
				ivjCenterPanelRight.setLayout(new java.awt.BorderLayout());
				getCenterPanelRight().add(getUISplitPaneVertical(), "Center");
				ivjCenterPanelRight
						.addComponentListener(new ComponentAdapter() {
							public void componentResized(ComponentEvent event) {
								if (splitLocation == SPLIT_MAX
										&& (getIsSelSet().isVisible())) {// 如果是列表界面
									getUISplitPaneVertical()
											.setDividerLocation(SPLIT_MAX);
								}
								if (isShowBillTemp) {// 如果是卡片界面
									getUISplitPaneVertical()
											.setDividerLocation(0);
								}
							}
						});
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCenterPanelRight;
	}

	/**
	 * 生成以root为根结点的树节点 创建日期：(2004-5-10 15:13:57)
	 * 
	 * @return javax.swing.tree.DefaultMutableTreeNode
	 * @param root
	 *            nc.vo.hi.hi_301.CtrlDeptVO
	 */
	protected DefaultMutableTreeNode getChildTree(CtrlDeptVO root) {
		// 封装根
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
		// 添加孩子
		Vector children = root.getChildren();
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				CtrlDeptVO vo = (CtrlDeptVO) children.elementAt(i);
				if (vo.isControlled()) {
					DefaultMutableTreeNode childNode = getChildTree((CtrlDeptVO) children
							.elementAt(i));
					rootNode.add(childNode);
				}
			}
		}
		return rootNode;
	}

	/**
	 * 返回 CmbJobType 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* 警告：此方法将重新生成。 */
	protected nc.ui.pub.beans.UIComboBox getCmbJobType() {
		if (ivjCmbJobType == null) {
			try {
				ivjCmbJobType = new nc.ui.pub.beans.UIComboBox();
				ivjCmbJobType.setName("CmbJobType");
				ivjCmbJobType.setFont(new java.awt.Font("dialog", 0, 12));
				ivjCmbJobType.setPreferredSize(new java.awt.Dimension(60, 20));
				ivjCmbJobType.setTranslate(true);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCmbJobType;
	}

	/**
	 * 返回 CmbPsncl 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	protected nc.ui.pub.beans.UIComboBox getCmbPsncl() {
		if (ivjCmbPsncl == null) {
			try {
				ivjCmbPsncl = new nc.ui.pub.beans.UIComboBox();
				ivjCmbPsncl.setName("CmbPsncl");
				ivjCmbPsncl.setFont(new java.awt.Font("dialog", 0, 12));
				ivjCmbPsncl.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjCmbPsncl.setTranslate(true);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCmbPsncl;
	}

	/**
	 * 刷新排序窗口的字段列表
	 */
	private void refreshSortDialogField() {
		if (configDialog == null)
			return;
		Pair[] items = getListItems();
		Vector vecPair = new Vector();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				// 任职类型、人员类别不加排序了
				if ("jobtypename".equals(items[i].getCode())/*
															 * ||
															 * "psnclassname".equals(items[i].getCode())
															 */)
					continue;
				String fieldcode = null;
				if ("pk_psncl".equals(items[i].getCode())) {
					fieldcode = "bd_psncl.psnclassname";
				} else {
					if (items[i].getTablecode() == null) {
						fieldcode = items[i].getCode();
					} else {
						fieldcode = items[i].getTablecode() + "."
								+ items[i].getCode();
					}
				}
				nc.ui.hr.comp.sort.Pair fieldPair = new nc.ui.hr.comp.sort.Pair(
						fieldcode, items[i].getName());
				vecPair.addElement(fieldPair);
			}
		}
		// 除了这些，还要增加公司顺序号等。
		String[] fieldcodes = new String[] { "bd_corp.showorder",
				"bd_corp.innercode", "bd_deptdoc.showorder",
				"bd_deptdoc.innercode" };
		String[] fieldnames = new String[] {
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000229")/* "公司顺序号" */,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000231")/* "公司级次号" */,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000230")/* "部门顺序号" */,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000232") /* "部门级次号" */};
		for (int j = 0; j < fieldcodes.length; j++) {
			vecPair.addElement(new nc.ui.hr.comp.sort.Pair(fieldcodes[j],
					fieldnames[j]));
		}
		nc.ui.hr.comp.sort.Pair[] listSortItems = new nc.ui.hr.comp.sort.Pair[vecPair
				.size()];
		vecPair.copyInto(listSortItems);
		configDialog.setFields(listSortItems);
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-8-16 9:25:33)
	 * 
	 * @return nc.ui.hi.hi_301.SortConfigDialog
	 */
	public SortConfigDialog getConfigDialog() {
		if (configDialog != null)
			return configDialog;

		Pair[] items = getListItems();
		Vector vecPair = new Vector();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				// 任职类型、人员类别不加排序了
				if ("jobtypename".equals(items[i].getCode())/*
															 * ||
															 * "psnclassname".equals(items[i].getCode())
															 */)
					continue;
				String fieldcode = null;
				if ("pk_psncl".equals(items[i].getCode())) {
					fieldcode = "bd_psncl.psnclassname";
				} else {
					if (items[i].getTablecode() == null) {
						fieldcode = items[i].getCode();
					} else {
						fieldcode = items[i].getTablecode() + "."
								+ items[i].getCode();
					}
				}
				nc.ui.hr.comp.sort.Pair fieldPair = new nc.ui.hr.comp.sort.Pair(
						fieldcode, items[i].getName());
				vecPair.addElement(fieldPair);
			}
		}
		// 除了这些，还要增加公司顺序号等。
		String[] fieldcodes = new String[] { "bd_corp.showorder",
				"bd_corp.innercode", "bd_deptdoc.showorder",
				"bd_deptdoc.innercode" };
		String[] fieldnames = new String[] {
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000229")/* "公司顺序号" */
				,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000231")/* "公司级次号" */,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000230")/* "部门顺序号" */,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
						"UPT6007-000232") /* "部门级次号" */};
		for (int j = 0; j < fieldcodes.length; j++) {
			vecPair.addElement(new nc.ui.hr.comp.sort.Pair(fieldcodes[j],
					fieldnames[j]));
		}
		nc.ui.hr.comp.sort.Pair[] listSortItems = new nc.ui.hr.comp.sort.Pair[vecPair
				.size()];
		vecPair.copyInto(listSortItems);
		configDialog = new SortConfigDialog(Util.getTopFrame(this), true);
		configDialog.setAllowNullSort(true);
		configDialog.setFields(listSortItems);
		configDialog.setModuleCode(getModuleName());

		configDialog.setLocationRelativeTo(this);
		configDialog.btnLoad_ActionPerformed(null);
		if (configDialog.getSortingFields() == null
				|| configDialog.getSortingFields().size() < 1) {
			Vector<Attribute> defaultSortingFields = new Vector<Attribute>();
			defaultSortingFields.add(new Attribute(
					new nc.ui.hr.comp.sort.Pair("bd_corp.showorder",
							nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
									"UPT6007-000229")/* "公司顺序号" */), true));
			defaultSortingFields.add(new Attribute(
					new nc.ui.hr.comp.sort.Pair("bd_deptdoc.showorder",
							nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
									"UPT6007-000230")/* "部门顺序号" */), true));
			defaultSortingFields
					.add(new Attribute(new nc.ui.hr.comp.sort.Pair(
							"bd_psndoc.showorder", nc.ui.ml.NCLangRes
									.getInstance().getStrByID("600704",
											"UPT600704-000325")/* "人员顺序号" */),
							true));
			configDialog.setSortingFields(defaultSortingFields);
			configDialog.btnLoad_ActionPerformed(null);
		}
		return configDialog;
	}

	/**
	 * 获取当前部门树的根结点。 创建日期：(2004-5-10 8:41:00)
	 * 
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 */
	protected nc.vo.hi.hi_301.CtrlDeptVO getCtrlDeptRoot() {
		return ctrlDeptRoot;
	}

	/**
	 * 得到当前查询相关的查询条件
	 * 
	 * @return
	 */
	protected ConditionVO[] getCurConditions() {
		ConditionVO[] cvos = null;
		// 没有选中树节点
		if (getSelectedNode() == null) {
			cvos = conditions;
		}
		// 如果选中的节点是部门节点需要添加部门条件
		CtrlDeptVO deptvo = (CtrlDeptVO) (getSelectedNode().getUserObject());
		if (deptvo.getNodeType() == CtrlDeptVO.DEPT) {
			ConditionVO vo = new ConditionVO();
			vo.setDataType(BillItem.UFREF);
			vo.setTableCode("bd_psndoc");
			vo.setFieldCode("bd_psndoc.pk_deptdoc");
			vo.setOperaCode("=");
			vo.setValue(deptvo.getPk_dept());
			if (conditions == null) {
				cvos = new ConditionVO[1];
				cvos[0] = vo;
			} else {
				cvos = new ConditionVO[conditions.length + 1];
				System.arraycopy(conditions, 0, cvos, 0, conditions.length);
				cvos[conditions.length] = vo;
			}
		} else {// 选中了根节点
			cvos = conditions;
		}
		Vector vBookTemp = new Vector();
		ConditionVO[] cvosAdd = null;
		ConditionVO[] cvosNew = null;
		if (vBookConds.size() > 0) {
			for (int i = 0; i < vBookConds.size(); i++) {
				ConditionVO vo = new ConditionVO();
				vo.setLogic(false);
				vo.setDataType(BillItem.UFREF);
				vo.setTableCode("bd_psndoc");
				vo.setFieldCode("bd_psndoc.pk_psndoc");
				vo.setOperaCode("=");
				vo.setValue((String) vBookConds.elementAt(i));
				vBookTemp.addElement(vo);
			}
			cvosAdd = new ConditionVO[vBookTemp.size()];
			vBookTemp.copyInto(cvosAdd);
			if (cvos == null) {
				cvos = cvosAdd;
			} else {
				cvosNew = new ConditionVO[cvos.length + cvosAdd.length];
				System.arraycopy(cvos, 0, cvosNew, 0, cvos.length);
				for (int c = 0; c < cvosAdd.length; c++) {
					cvosNew[cvos.length + c] = cvosAdd[c];
				}
				cvos = cvosNew;
			}
		}
		// wangkf add 把性别 的英文/繁体 改成 简体中文
		if (cvos != null) {
			for (int c = 0; c < cvos.length; c++) {
				if (cvos[c] != null
						&& "bd_psnbasdoc.sex".equals(cvos[c].getFieldCode())
						&& "male".equals(cvos[c].getValue())) {
					cvos[c].setValue("男");
				} else if (cvos[c] != null
						&& "bd_psnbasdoc.sex".equals(cvos[c].getFieldCode())
						&& "female".equals(cvos[c].getValue())) {
					cvos[c].setValue("女");
				} else if (cvos[c] != null
						&& "hi_psndoc_ctrt.iconttype".equals(cvos[c]
								.getFieldCode())) {
					int index = cvos[c].getComboIndex();
					if (index > 1) {
						index++;
					}
					cvos[c].setValue(Integer.toString(index));
				}
			}
		}
		return cvos;
	}

	/**
	 * 返回依赖项的名称数组，该数据项长度只能为 1 或者 2 返回 null : 没有依赖 长度 1 : 单项依赖 长度 2 : 双向依赖
	 */
	public java.lang.String[] getDependentItemExpressByExpress(
			java.lang.String itemName) {
		return null;
	}

	/**
	 * 得到各个公司是否启用了部分档案权限。 创建日期：(2004-10-22 15:36:49)
	 * 
	 * @return java.lang.String
	 * @param pk_corp
	 *            java.lang.String
	 */
	public String getDeptPowerByCorp(String corppk) throws Exception {
		String power = (String) deptPowerlist.get(corppk);
		if (power != null) {
			return power;
		}
		// 根据新需求：如果是下级公司，则不限制部门权限。dusx 2008.11.24
		if (!corppk.equals(Global.getCorpPK())) {
			deptPowerlist.put(corppk, "0=0");
			return "0=0";
		}
		if (!"0001".equals(Global.getCorpPK())) {
			// boolean useDeptPower = GlobalTool.isUsedDataPower("bd_deptdoc",
			// "部门档案", corppk);
			// if (useDeptPower /* && !"600704".equals(getModuleName()) */) {
			// power = GlobalTool.getPowerSql("bd_deptdoc",
			// Global.getUserID(), corppk);
			// if(power==null){
			// power = "0=0";
			// }
			// deptPowerlist.put(corppk, power);
			// return power;
			// } else {
			// deptPowerlist.put(corppk, "0=0");
			// return "0=0";
			// }
			// 连接数优化
			power = GlobalTool.getPowerSql("bd_deptdoc", Global.getUserID(),
					corppk);
			if (power == null) {
				power = "0=0";
			}
			deptPowerlist.put(corppk, power);
			return power;
		} else {
			deptPowerlist.put(corppk, "0=0");
			return "0=0";
		}
	}

	/**
	 * 返回 DeptTree 特性值。
	 * 
	 * @return nc.ui.pub.beans.UITree
	 */
	protected nc.ui.pub.beans.UITree getDeptTree() {
		if (ivjDeptTree == null) {
			try {
				ivjDeptTree = new nc.ui.pub.beans.UITree();
				ivjDeptTree.setName("DeptTree");
				ivjDeptTree.setBounds(0, 0, 160, 120);
				ivjDeptTree.getSelectionModel().setSelectionMode(
						TreeSelectionModel.SINGLE_TREE_SELECTION);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDeptTree;
	}

	/**
	 * 根据ctrlDeptRoot构造树model。 创建日期：(2004-5-10 15:08:02)
	 * 
	 * @return javax.swing.tree.DefaultTreeModel
	 */
	protected DefaultTreeModel getDeptTreeModel() {
		if (treemodel == null) {
			// 如果当前部门树为空，返回空
			if (ctrlDeptRoot == null) {
				return null;
			}
			// 调用getChildTree组织树
			DefaultMutableTreeNode root = getChildTree(ctrlDeptRoot);
			// 封装成树的model返回
			treemodel = new DefaultTreeModel(root);
		}
		return treemodel;
	}

	protected void setDeptTreeModel(DefaultTreeModel newtreemodel) {
		this.treemodel = newtreemodel;
	}

	/**
	 * 返回 DismissCard 特性值。
	 * 
	 * @return nc.ui.hi.hi_301.HIBillCardPanel
	 */
	protected HIBillCardPanel getDismissCard() {
		if (ivjDismissCard == null) {
			try {
				ivjDismissCard = new nc.ui.hi.hi_301.HIBillCardPanel(
						allRelatedPara);
				ivjDismissCard.setName("DismissCard");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDismissCard;
	}

	/**
	 * 获取当前正在编辑的物理表。 创建日期：(2004-5-17 11:35:26)
	 * 
	 * @return java.lang.String[]
	 */
	private String[] getEditingTables() throws Exception {
		// 如果正在编辑表头，返回所有表头的物理表
		// 否则只保存当前正在编辑的
		if (getEditType() == EDIT_MAIN || getEditType() == EDIT_RETURN) {
			return getCard().getBillData().getHeadTableCodes();
		} else {
			return new String[] { getBodyTableCode() };
		}
	}

	/**
	 * 获取当前表体中正在编辑的行号。 创建日期：(2004-5-12 14:51:25)
	 * 
	 * @return int
	 */
	private int getEditLineNo() {
		return editLineNo;
	}

	/**
	 * 获取当前正在编辑类型：编辑排序、编辑主表、编辑子表。 创建日期：(2004-5-12 11:40:28)
	 * 
	 * @return int
	 */
	protected int getEditType() {
		return editType;
	}

	private nc.ui.pub.beans.UIFileChooser ivjFileChooser = null;

	/**
	 * 此处插入方法说明。 创建日期：(2005-4-28 14:24:14)
	 */
	public nc.ui.pub.beans.UIFileChooser getFileChooser() {
		if (ivjFileChooser == null) {
			try {
				ivjFileChooser = new nc.ui.pub.beans.UIFileChooser();
				ivjFileChooser.setName("FileChooser");
				ivjFileChooser.setBounds(17, 301, 433, 293);
				ExampleFileFilter JPGFilter = new ExampleFileFilter(
						new String[] { "jpg", "bmp" }, "Image Files");
				ivjFileChooser.setFileFilter(JPGFilter);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjFileChooser;
	}

	/**
	 * 获取排序的中的移至对话框
	 */
	protected GotoDialog getGotoDialog() {
		if (gotoDialog == null) {
			// 注意要指定父窗口，并且是模态对话框
			gotoDialog = new GotoDialog(Util.getTopFrame(this), true);
			// 将对话框的位置设置为中心
			gotoDialog.setLocationRelativeTo(this);
		}
		return gotoDialog;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-3 12:03:10) *
	 * 
	 * @return java.lang.String
	 */
	protected String getIndocflag() {
		return "N";
	}

	/**
	 * 获取当前正在编辑的表tableCodes的数据。 创建日期：(2004-5-17 11:30:33) *
	 * 
	 * @param tableCodes
	 *            String[] 当前正在编辑的表
	 * @return nc.vo.hi.hi_301.PersonEAVO 这些表的数据，封存在PersonEAVO中
	 */
	private PersonEAVO getInputData(String[] tableCodes) throws Exception {
		// 当前人员数据
		PersonEAVO eavo;
		// 如果当前正在添加人员（条件：编辑状态为编辑主表，并且是添加状态），则新生成一个PersonEAVO对象
		if (getEditType() == EDIT_MAIN && isAdding()) {
			person = null;
			eavo = new PersonEAVO();
		} else if (getEditType() == EDIT_RETURN && isAdding()) {
			person = null;
			eavo = new PersonEAVO();
		} else {
			eavo = (PersonEAVO) person.clone();
		}
		// 删除编辑器
		if (getEditType() == EDIT_SUB
				&& getCard().getBillTable().getCellEditor() != null) {
			getCard().getBillTable().getCellEditor().stopCellEditing();
			getCard().getBillTable().removeEditor();
		}
		// 获取数据
		getCard().getBillValueVOExtended(eavo);
		if (getEditType() == EDIT_MAIN || getEditType() == EDIT_RETURN
				|| getEditType() == EDIT_REF) {
			// 如果当前编辑状态为编辑主表
			if (isAdding()) {
				// 如果正在添加人员
				// 设置各种标示：公司(pk_corp)，入档标识(indocflag，该字段缺省为Y，所以要设置），和辅助表中的indocflag（同步主表)
				eavo.getPsndocVO().setAttributeValue("pk_corp",
						Global.getCorpPK());
				eavo.getPsndocVO().setAttributeValue("indocflag",
						new nc.vo.pub.lang.UFBoolean('N'));
				eavo.getPsndocVO().setAttributeValue("pk_psnbasdoc",
						getRehirePsnbaspk());
				eavo.setPk_psnbasdoc(getRehirePsnbaspk());
			}

			// 此处根据人员类别设置归属范围。
			String pk_psncl = (String) eavo.getPsndocVO().getAttributeValue(
					"pk_psncl");
			if (pk_psncl != null && !"".equals(pk_psncl)) {
				eavo.getPsndocVO().setAttributeValue("psnclscope",
						getPsnclscopeAccordingPsncl(pk_psncl));
			}

		} else {
			// 维护子表
			// 将人员的主键设置到各个子表中的记录。
			String[] keys = getTempPsnPk();
			String pk_psndoc = keys[1];
			for (int i = 0; i < tableCodes.length; i++) {
				// 获取被编辑子表的所有记录
				CircularlyAccessibleValueObject[] records = eavo
						.getTableVO(tableCodes[i]);
				// 增加判断，如果是输入的行中没有此项，则将复制的记录中的此项清空
				if ("hi_psndoc_deptchg".equalsIgnoreCase(tableCodes[i])) {
					int row = getEditLineNo();
					GeneralVO vo = (GeneralVO) records[row];
					BillItem[] items = getCard().getBillModel().getBodyItems();
					//
					for (int k = 0; k < items.length; k++) {
						if (items[k].isShow()) {
							if (vo.getAttributeValue(items[k].getKey()) == null) {
								vo.removeAttributeName(Util.PK_PREFIX
										+ items[k].getKey());
							}
						}
					}
				}

				// v50 add 处理采集节点中默认带出的当前公司
				records = setCorp(records, tableCodes[i]);
				// 当前表是否是同期记录类型
				boolean isCurrent = AbstractValidator
						.isCurrentTable(tableCodes[i]);
				if (records != null) {
					int len = records.length;
					for (int j = 0; j < len; j++) {
						// change by zhyan 2006-03-24
						// 子集增加顺序变化，增加的行在最大，recordnum:0,1,2,3,4数字越大记录越老，record=最大时lastflag设置Y
						// recordnum:0,1,2,3,4数字越大记录越老，record=0时lastflag设置Y
						records[j].setAttributeValue("recordnum", new Integer(
								len - j - 1));
						// 同期记录 lastflag = 'Y'
						records[j].setAttributeValue("lastflag", new UFBoolean(
								j == len - 1 || isCurrent));
						// 设置子表记录的人员主键和表名
						records[j].setAttributeValue("pk_psndoc", pk_psndoc);
						records[j].setAttributeValue("pk_psnbasdoc", keys[0]);
						//
						GeneralVO vo = (GeneralVO) records[j];
						BillItem[] items = getCard().getBillModel()
								.getBodyItems();
						// 循环判断后，如果逻辑值字段没有编辑，则置为‘N’
						for (int k = 0; k < items.length; k++) {
							if (items[k].isShow()
									&& items[k].getDataType() == BillItem.BOOLEAN) {
								if (vo.getAttributeValue(items[k].getKey()) == null) {
									vo.setAttributeValue(items[k].getKey(),
											new UFBoolean(false));
								}
							}
						}
					}
				}
			}
		}
		return eavo;
	}

	private int getPsnclscopeAccordingPsncl(String pk_psncl) throws Exception {

		IPsncl ipsncl = (IPsncl) NCLocator.getInstance().lookup(
				IPsncl.class.getName());
		nc.vo.bd.b05.PsnclVO psncl = ipsncl.findPsnclVOByPk(pk_psncl);
		if (psncl == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("600704", "upt600704-000042")/*
																			 * @res
																			 * "人员类别"
																			 */
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"600704", "UPP600704-000003")/*
															 * @res
															 * "该记录已经被删除,请刷新后再试！"
															 */);
		}
		// 
		int psnclscope = 0;
		if (psncl != null) {
			psnclscope = psncl.getPsnclscope();

		}
		return psnclscope;
	}

	/**
	 * v50 add 处理各子集中公司参照name->pk,前提是采集能编辑业务子集,维护不能编辑
	 * 
	 * @param curtablename
	 * @return
	 */
	private CircularlyAccessibleValueObject[] setCorp(
			CircularlyAccessibleValueObject[] records, String curtablename) {
		if ("hi_psndoc_dimission".equalsIgnoreCase(curtablename)
				|| "hi_psndoc_ctrt".equalsIgnoreCase(curtablename)
				|| "hi_psndoc_retire".equalsIgnoreCase(curtablename)
				|| "hi_psndoc_training".equalsIgnoreCase(curtablename)
				|| "hi_psndoc_orgpsn".equalsIgnoreCase(curtablename)
				|| "hi_psndoc_psnchg".equalsIgnoreCase(curtablename)) {
			int row = getEditLineNo();
			if (row == -1)
				return records;// add by lianglj 2010-11-04
			// 合并56补丁，当没有安装培训模块的时候，会报错

			GeneralVO vo = (GeneralVO) records[row];
			if (vo.getAttributeValue("pk_corp") != null) {
				vo.setAttributeValue("pk_corp", Global.getCorpPK());
			}
			if ("hi_psndoc_ctrt".equalsIgnoreCase(curtablename)) {
				if (vo.getAttributeValue("pk_majorcorp") != null) {
					vo.setAttributeValue("pk_majorcorp", Global.getCorpPK());
				}
			}
		} else if ("hi_psndoc_ass".equalsIgnoreCase(curtablename)) {
			int row = getEditLineNo();
			GeneralVO vo = (GeneralVO) records[row];
			if (vo.getAttributeValue("pk_corpperson") != null) {
				vo.setAttributeValue("pk_corpperson", Global.getCorpPK());
			} else if (vo.getAttributeValue("pk_corpassess") != null) {
				vo.setAttributeValue("pk_corpassess", Global.getCorpPK());
			}
		}
		return records;
	}

	/**
	 * 加载各个列名称，进行处理
	 */
	public java.lang.String[] getItemValuesByExpress(
			java.lang.String itemExpress) {
		try {
			if (psnList == null)
				return null;
			if (psnList.length == 0)
				return new String[0];
			String[] values = new String[psnList.length];
			for (int i = 0; i < values.length; i++) {
				String printtable = null;
				String printfield = null;
				// 所有以"__" 开头的进行转换
				String fieldprefix = "__";// UFAGE
				if (itemExpress.indexOf(fieldprefix) > 0) {
					printtable = itemExpress.substring(0, itemExpress
							.indexOf(fieldprefix));
					printfield = itemExpress.substring(itemExpress
							.indexOf(fieldprefix) + 2);
				}
				// 人员归属范围 单独处理
				if ("psnclscope".equals(itemExpress)) {
					Integer intPsnclscope = (Integer) psnList[i]
							.getAttributeValue(itemExpress);
					Object v = getPsnclscopeName(psnScopeItems, intPsnclscope);
					values[i] = (v == null ? null : v.toString());
				} else if ("sex".equals(itemExpress)
						|| "bloodtype".equals(itemExpress)) {
					Object obj = psnList[i].getAttributeValue(itemExpress);
					BillItem billItem = getBillItem("bd_psnbasdoc."
							+ itemExpress);
					String reftype = billItem.getRefType();// IS,:,男:male,女:female
					String dispname = null;
					if (reftype != null && obj != null) {
						String[] first = reftype.split(",");
						for (int ii = 0; ii < first.length; ii++) {
							if (first[ii] != null
									&& first[ii].startsWith(obj.toString())) {
								String[] second = first[ii].split(":");
								dispname = (second == null ? null
										: second[second.length - 1]);
							}
						}
					}
					values[i] = (dispname == null ? " " : dispname);
				} else {
					if (printfield == null) {
						printfield = itemExpress;
					}
					Object v = psnList[i].getAttributeValue(printfield);
					values[i] = (v == null ? " " : v.toString());
					if (printtable != null && printfield != null && v != null) {
						BillItem billItem = getBillItem(printtable + "."
								+ printfield);
						if (billItem != null) {
							int itemDataType = billItem.getDataType();
							String pkName = null;
							if (itemDataType == BillItem.UFREF
									|| itemDataType == BillItem.USERDEF) {
								UIRefPane refPane = (UIRefPane) billItem
										.getComponent();
								refPane.setPK(v);
								pkName = refPane.getRefName();
								values[i] = (pkName == null ? " " : pkName);
							} else if (itemDataType == BillItem.COMBO) {
								UIComboBox combox = (UIComboBox) billItem
										.getComponent();
								String dispname = (combox == null ? null
										: combox.getName());
								values[i] = (dispname == null ? " " : dispname);
							}
						}
					}
				}
			}
			return values;
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
			return null;
		}
	}

	/**
	 * 返回 LeaveCard 特性值。
	 * 
	 * @return nc.ui.hi.hi_301.HIBillCardPanel
	 */
	protected HIBillCardPanel getLeaveCard() {
		if (ivjLeaveCard == null) {
			try {
				ivjLeaveCard = new nc.ui.hi.hi_301.HIBillCardPanel(
						allRelatedPara);
				ivjLeaveCard.setName("LeaveCard");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLeaveCard;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-5-18 10:55:48)
	 * 
	 * @return java.lang.String[]
	 */
	private Pair[] getListItems() {
		return listItems;
	}

	protected HashMap hmField = new HashMap();

	/**
	 * 默认显示的列
	 */
	protected void initHmField() {
		hmField.put("psncode", "bd_psndoc.psncode");
		hmField.put("psnname", "bd_psnbasdoc.psnname");
		hmField.put("unitname", "bd_corp.unitname");
		hmField.put("deptcode", "bd_deptdoc.deptcode");
		hmField.put("deptname", "bd_deptdoc.deptname");
		hmField.put("psnclassname", "bd_psncl.psnclassname");
		hmField.put("jobname", "om_job.jobname");
		hmField.put("jobtypename", "0 as jobtypeflag");
		hmField.put("showorder", "bd_psndoc.showorder");
		if (intodocdirect == 1 && getModuleName().equalsIgnoreCase("600704")) {
			hmField.put("approveflagname", "bd_psnbasdoc.approveflag");
		}
	}

	/**
	 * V35 add 得到列表的字段
	 * 
	 * @return
	 */
	protected String getListField() {
		Pair[] listItemsTmp = listItems;
		if (listItems == null) {
			listItemsTmp = getListItemsDefault();
		}
		String listfield = "";
		for (int i = 0; i < listItemsTmp.length; i++) {
			String fullfield = listItemsTmp[i].getTablecode() == null ? listItemsTmp[i]
					.getCode()
					: listItemsTmp[i].getTablecode() + "."
							+ listItemsTmp[i].getCode();
			if ("belong_pk_corp".equals(listItemsTmp[i].getCode())) {
				continue;
			}
			fullfield = (hmField.get(fullfield) == null ? fullfield
					: (String) hmField.get(fullfield));
			if ("bd_psnbasdoc.approveflagname".equalsIgnoreCase(fullfield)) {
				fullfield = "bd_psnbasdoc.approveflag as approveflagname";
			}
			listfield += fullfield;
			if (i < listItemsTmp.length - 1) {
				listfield += ",";
			}
		}
		// dusx修改2009.6.4
		if (listfield.endsWith(","))
			listfield = listfield.substring(0, listfield.length() - 1);
		return listfield;
	}

	/**
	 * 通过人员主键查询一个人员的信息时获得列表信息
	 * 
	 * @return
	 */
	protected String getListFieldForOne() {
		Pair[] listItemsTmp = listItems;
		if (listItems == null) {
			listItemsTmp = getListItemsDefault();
		}

		String listfield = "";
		for (int i = 0; i < listItemsTmp.length; i++) {
			String fullfield = listItemsTmp[i].getTablecode() == null ? listItemsTmp[i]
					.getCode()
					: listItemsTmp[i].getTablecode() + "."
							+ listItemsTmp[i].getCode();
			if ("bd_psnbasdoc.approveflagname".equalsIgnoreCase(fullfield)) {
				fullfield = "bd_psnbasdoc.approveflag as approveflagname";
			}
			if ("jobtypename".equalsIgnoreCase(fullfield)) {
				fullfield = "0 as jobtypeflag";
			}
			listfield += fullfield;
			if (i < listItemsTmp.length - 1) {
				listfield += ",";
			}
		}
		// dusx修改2009.6.4
		if (listfield.endsWith(","))
			listfield = listfield.substring(0, listfield.length() - 1);

		return listfield;
	}

	/**
	 * 用于在开发环境获得节点编号。 创建日期：(2003-2-27 12:28:41)
	 * 
	 * @return java.lang.String
	 */
	public String getModuleCodeWithDebug() {
		return "600704";
	}

	/**
	 * 返回该数据源对应的节点编码
	 */
	public java.lang.String getModuleName() {
		return "600704";
	}

	// 包含已撤消部门
	private UICheckBox includeCancleDept = null;

	protected UICheckBox getincludeCancleDept() {
		if (includeCancleDept == null) {
			try {
				includeCancleDept = new UICheckBox();
				includeCancleDept.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600704", "UPT600704-000279"));// 包含已撤消部门
				includeCancleDept.setFont(new java.awt.Font("dialog", 0, 12));
				includeCancleDept.setPreferredSize(new java.awt.Dimension(93,
						20));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return includeCancleDept;
	}

	// 包含历史人员组
	private UICheckBox includeHisPersonGroup = null;

	protected UICheckBox getIncludeHisPersonGroup() {
		if (includeHisPersonGroup == null) {
			try {
				includeHisPersonGroup = new UICheckBox();
				includeHisPersonGroup.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600704", "UPT600704-000295"));// "包含历史人员组"
				includeHisPersonGroup.setPreferredSize(new java.awt.Dimension(
						120, 20));
				includeHisPersonGroup
						.setFont(new java.awt.Font("dialog", 0, 12));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return includeHisPersonGroup;
	}

	// 包含历史人员
	private UICheckBox includeHisPerson = null;

	protected UICheckBox getIncludeHisPerson() {
		if (includeHisPerson == null) {
			try {
				includeHisPerson = new UICheckBox();
				includeHisPerson.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600704", "UPT600704-000296"));// 包含历史人员
				includeHisPerson.setFont(new java.awt.Font("dialog", 0, 12));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return includeHisPerson;
	}

	// 是否显示子集
	private UICheckBox ivjIsSelSet = null;

	protected UICheckBox getIsSelSet() {
		if (ivjIsSelSet == null) {
			try {
				ivjIsSelSet = new UICheckBox();
				ivjIsSelSet.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600704", "UPP600704-000195"));// 是否显示子集
				ivjIsSelSet.setPreferredSize(new java.awt.Dimension(69, 20));
				ivjIsSelSet.setFont(new java.awt.Font("dialog", 0, 12));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjIsSelSet;
	}

	private UIPanel quickQueryPanel = null;

	protected UIPanel getQuickQueryPanel() {
		if (quickQueryPanel == null) {
			try {
				quickQueryPanel = new UIPanel();
				quickQueryPanel.setName("queryEastPanel");
				quickQueryPanel
						.setPreferredSize(new java.awt.Dimension(130, 30));// WestPanel.setPreferredSize(new
				// java.awt.Dimension(750,
				// 30));
				quickQueryPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				// getQuickQueryPanel().add(getUILabelName(),
				// getUILabelName().getName());
				getQuickQueryPanel()
						.add(getUItfName(), getUItfName().getName());
				// getQuickQueryPanel().add(getUILabelJobRank(),
				// getUILabelJobRank().getName());
				// getQuickQueryPanel().add(getUIRefDutyRank(),
				// getUIRefDutyRank().getName());
				getQuickQueryPanel().add(getQuickQueryButton(),
						getQuickQueryButton().getName());
				getQuickQueryPanel().add(getQuickMenuBar());

			} catch (Throwable e) {
				handleException(e);
			}
		}
		return quickQueryPanel;
	}

	private nc.ui.pub.beans.UILabel ivjUILabelName = null;

	protected nc.ui.pub.beans.UILabel getUILabelName() {
		if (ivjUILabelName == null) {
			try {
				ivjUILabelName = new nc.ui.pub.beans.UILabel();
				ivjUILabelName.setName("UILabelName");
				ivjUILabelName.setText("姓名");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabelName;
	}

	// 快速查询：人员姓名TextField
	private nc.ui.pub.beans.UITextField ivjUItfName = null;

	protected nc.ui.pub.beans.UITextField getUItfName() {
		if (ivjUItfName == null) {
			try {
				ivjUItfName = new nc.ui.pub.beans.UITextField();
				ivjUItfName.setName("UItfName");
				ivjUItfName.setMaxLength(50);
				ivjUItfName.addKeyListener(new KeyAdapter() {
					public void keyTyped(KeyEvent keyevent) {
						if (keyevent.getKeyChar() == '\n') {
							getQuickQueryButton().doClick();
							ivjUItfName.requestFocusInWindow();
						}
					}
				});

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUItfName;
	}

	private nc.ui.pub.beans.UILabel ivjUILabelJobRank = null;

	protected nc.ui.pub.beans.UILabel getUILabelJobRank() {
		if (ivjUILabelJobRank == null) {
			try {
				ivjUILabelJobRank = new nc.ui.pub.beans.UILabel();
				ivjUILabelJobRank.setName("UILabelJobRank");
				ivjUILabelJobRank.setText("职级");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabelJobRank;
	}

	private nc.ui.pub.beans.UIRefPane ivjUIRefDutyRank = null; // 职务级别参照

	private nc.ui.pub.beans.UIButton quickQuery = null;

	protected nc.ui.pub.beans.UIButton getQuickQueryButton() {
		if (quickQuery == null) {
			try {
				quickQuery = new nc.ui.pub.beans.UIButton();
				quickQuery.setName("quickQuery");
				quickQuery.setText(NCLangRes.getInstance().getStrByID("600704",
						"UPP600704-100186")/* @res "快查" */);
				quickQuery.setPreferredSize(new java.awt.Dimension(30, 20));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return quickQuery;
	}

	// protected ButtonObject bnUpdatePsnnamePinyin = new
	// ButtonObject("更新所有人员拼音码(升级用)","更新所有人员拼音码(升级用)", 0, "更新所有人员拼音码(升级用)");
	protected ButtonObject[] grpQuery = new ButtonObject[] { bnCard, bnBook,
			bnDisplay, bnExportPic, bnUpload, bnQuery, bnFresh, bnPrint };

	protected ButtonObject[] grpCardDisplayQuery = new ButtonObject[] {
			bnUpload,/* bnCard2, */bnToFirst, bnUpRecord, bnDownRecord,
			bnToLast, bnList };

	/**
	 * 批量导出事件处理函数。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onBatchExport() throws java.lang.Exception {
		GeneralVO[] intoDocData = getSelectPsnListData();
		if (intoDocData == null || intoDocData.length < 1) {
			showWarningMessage(NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-100187")/* @res "请选择批量导出的人员！" */);
			return;
		}

		UIFileChooserForOutport digFile = getDigOutport();
		int returnVal = digFile.showDialog(this, NCLangRes.getInstance()
				.getStrByID("10241201", "UPP10241201-000107")/* @res "导出" */);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			new Thread(new Runnable() {
				public void run() {
					BannerDialog dialog = new BannerDialog(PsnInfCollectUI.this);
					doOutput(dialog);
				}
			}).start();

		}
	}

	private UIFileChooserForOutport digOutport = null;

	private UIFileChooserForOutport getDigOutport() {
		if (digOutport == null) {
			digOutport = new UIFileChooserForOutport();
		}
		return digOutport;
	}

	private void doOutput(BannerDialog waitDlg) {

		String strFileName = getDigOutport().getSelectedFile().getPath();
		try {
			waitDlg.start();
			// String startText = "登记表正在输出，请稍候......";
			// String title = "提示";

			GeneralVO[] intoDocData = getSelectPsnListData();

			// 取得登记表模板
			RptDefVO curTemplate = null;
			RptAuthVO auVO = getDigOutport().getSelectedRpt();
			if (auVO != null) {

				RptDefVO[] vos = HIDelegator
						.getIHrReport()
						.queryDefVOByCondition(
								" d.pk_rpt_def='" + auVO.getPk_rpt_def() + "' ");
				if (vos == null || vos.length <= 0) {
					throw new BusinessException(NCLangRes.getInstance()
							.getStrByID("600704", "UPP600704-100188")/*
																		 * @res
																		 * "查询卡片模板发生异常"
																		 */);
				}
				curTemplate = vos[0];
			} else {
				MessageDialog.showHintDlg(this, null, NCLangRes.getInstance()
						.getStrByID("600704", "UPP600704-100189")/*
																	 * @res
																	 * "请选择卡片表模板！"
																	 */);
				return;
			}

			if (auVO.getRpt_type() == nc.vo.hr.pub.carddef.CommonValue.RPT_TYPE_CARD) {
				//
				ContextVO context = new ContextVO();
				for (int i = 0; i < intoDocData.length; i++) {
					String pk_psndoc = (String) intoDocData[i]
							.getAttributeValue("pk_psndoc");
					String psnname = (String) intoDocData[i]
							.getAttributeValue("psnname");
					String pk_psndoc_sub = (String) intoDocData[i]
							.getAttributeValue("pk_psndoc_sub");
					int jobType = (Integer) intoDocData[i]
							.getAttributeValue("jobTypeFlag");
					RptDefVO defVO = HrCardReportViewPanel
							.queryRptDefVoByPk(auVO.getPk_rpt_def());
					CellsModel cm = (CellsModel) defVO.getObj_rpt_def();
					if (cm == null) {
						continue;
					}
					HrReportDataVO rptData = HrCardReportViewPanel
							.queryReportData(pk_psndoc, pk_psndoc_sub,
									jobType != 0, auVO.getPk_rpt_def(), cm);
					// cmcopy.setCellsAuth(auth);
					CardBrowseMainPanel mp = new CardBrowseMainPanel(null);
					mp.getPreviewPanel().processPreviewCellsModel(cm);
					mp.getPreviewPanel().putReportVOIntoCellsModel(rptData, cm);

					// context.setName("导出卡片");
					context.setName(NCLangRes.getInstance().getStrByID(
							"600700", "UPP600700-000364")/* @res "导出卡片" */);
					HSSFWorkbook workBook = ExcelExpUtil.createWorkBook(
							context, cm, null);
					saveWorkBook2Local(strFileName, psnname + ".xls", workBook);
				}// end for
			} else if (auVO.getRpt_type() == nc.vo.hr.pub.carddef.CommonValue.RPT_TYPE_WORD) {

				// 插件license校验
				// if(!isCheckLicense_wordcard) {
				// MessageDialog.showErrorDlg(ClientAssistant.getApplet(), null,
				// "该插件没有申请license或license已经过期。");
				// return;
				// }

				//
				WordVO wordVO = (WordVO) curTemplate.getObj_rpt_def();
				Map refDataTypeMap = RefDataTypeHelper.getRefDataTypeMap(Global
						.getCorpPK());
				if (wordVO == null) {
					MessageDialog.showErrorDlg(this, null, NCLangRes
							.getInstance().getStrByID("600704",
									"UPP600704-100190")/* @res "此模板为空模板" */);
					return;
				}
				if (!getDigOutport().isOne()) {
					for (int i = 0; i < intoDocData.length; i++) {
						String pk_psndoc = (String) intoDocData[i]
								.getAttributeValue("pk_psndoc");
						String psnname = (String) intoDocData[i]
								.getAttributeValue("psnname");
						String deptname = (String) intoDocData[i]
								.getAttributeValue("deptname");

						Calendar calendar = Calendar.getInstance();
						int year = calendar.get(Calendar.YEAR);
						int month = calendar.get(Calendar.MONTH) + 1;
						int day = calendar.get(Calendar.DATE);
						int hour = calendar.get(Calendar.HOUR_OF_DAY);
						int minute = calendar.get(Calendar.MINUTE);
						int second = calendar.get(Calendar.SECOND);
						String date = year
								+ (month >= 10 ? Integer.toString(month) : "0"
										+ month)
								+ (day >= 10 ? Integer.toString(day) : "0"
										+ day)
								+ (hour >= 10 ? Integer.toString(hour) : "0"
										+ hour)
								+ (minute >= 10 ? Integer.toString(minute)
										: "0" + minute)
								+ (second >= 10 ? Integer.toString(second)
										: "0" + second);

						String fileName = date + "_"
								+ curTemplate.getRpt_name() + "_" + deptname
								+ "_" + psnname;
						WordUtil.output(refDataTypeMap, pk_psndoc, wordVO
								.getConditionMap(), wordVO.getWordDocument(),
								strFileName, fileName, false);
					}
				} else {
					strFileName = System.getProperty("user.home") + "/"
							+ "TEMPLETE_REGISTER" + "/";
					String[] fileNames = new String[intoDocData.length];
					for (int i = 0; i < intoDocData.length; i++) {
						String pk_psndoc = (String) intoDocData[i]
								.getAttributeValue("pk_psndoc");
						fileNames[i] = pk_psndoc;
						WordUtil.output(refDataTypeMap, pk_psndoc, wordVO
								.getConditionMap(), wordVO.getWordDocument(),
								strFileName, pk_psndoc, false);
					}
					strFileName = getDigOutport().getSelectedFile().getPath();
					WordUtil.output(fileNames, strFileName);
				}
			}
			MessageDialog.showHintDlg(this, null, NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-100191")/*
																 * @res
																 * "卡片输出成功!"
																 */);
			waitDlg.end();
		} catch (BusinessException e) {
			MessageDialog.showErrorDlg(this, null, e.getMessage());
		} catch (Exception e) {
			MessageDialog.showErrorDlg(this, null, NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-100192")/*
																 * @res
																 * "登记表输出发生错误!"
																 */);
			e.printStackTrace();
		} finally {
			waitDlg.end();
		}
	}

	/**
	 * 保存生成的Excel文件对象到用户本地
	 * 
	 * @param parent
	 * @param workBook
	 * @param strFilePostfix
	 */
	private static void saveWorkBook2Local(String dir, String fileName,
			HSSFWorkbook workBook) {
		if (!StringUtils.hasText(dir) || !StringUtils.hasText(fileName)
				|| workBook == null) {
			return;
		}

		// 处理filename中的非法字符
		fileName = StringUtils.replace(fileName, "/", "-");
		fileName = StringUtils.replace(fileName, "\\", "-");
		fileName = StringUtils.replace(fileName, ":", "-");
		fileName = StringUtils.replace(fileName, "*", "-");
		fileName = StringUtils.replace(fileName, "?", "-");
		fileName = StringUtils.replace(fileName, "\"", "-");
		fileName = StringUtils.replace(fileName, "<", "-");
		fileName = StringUtils.replace(fileName, ">", "-");
		fileName = StringUtils.replace(fileName, "|", "-");

		File file = new File(dir + "/" + fileName);
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
			workBook.write(stream);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 子集显示历史
	private UICheckBox ivjIsShowSetHistory = null;// v50 add

	protected UICheckBox getIsShowSetHistory() {
		if (ivjIsShowSetHistory == null) {
			try {
				ivjIsShowSetHistory = new UICheckBox();
				ivjIsShowSetHistory.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600704", "UPT600704-000277"));// 子集显示历史
				ivjIsShowSetHistory.setPreferredSize(new java.awt.Dimension(93,
						20));
				ivjIsShowSetHistory.setFont(new java.awt.Font("dialog", 0, 12));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjIsShowSetHistory;
	}

	protected boolean IsLookHistory() {
		if (getIsShowSetHistory().isSelected()) {
			return true;
		} else {
			return false;
		}
	}

	private UIPanel WestPanel = null;

	protected UIPanel getWestPanel() {
		if (WestPanel == null) {
			try {
				WestPanel = new UIPanel();
				WestPanel.setName("WestPanel");
				WestPanel.setPreferredSize(new java.awt.Dimension(800, 30));
				WestPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				getWestPanel().add(getLbScope(), getLbScope().getName());
				getWestPanel().add(getCmbPsncl(), getCmbPsncl().getName());
				getWestPanel()
						.add(getUILabelType(), getUILabelType().getName());
				getWestPanel().add(getCmbJobType(), getCmbJobType().getName());
				getWestPanel().add(getincludeChildDeptPsn(),
						getincludeChildDeptPsn().getName());// v5.3 add
				getWestPanel().add(getincludeCancleDept(),
						getincludeCancleDept().getName());// v5.3 add
				getWestPanel().add(getIncludeHisPersonGroup(),
						getIncludeHisPersonGroup().getName());
				getWestPanel().add(getIncludeHisPerson(),
						getIncludeHisPerson().getName());
				getWestPanel().add(getIsSelSet(), getIsSelSet().getName());
				getWestPanel().add(getIsShowSetHistory(),
						getIsShowSetHistory().getName());

			} catch (Throwable e) {
				handleException(e);
			}
		}
		return WestPanel;
	}

	private UIPanel EastPanel = null;

	protected UIPanel getEestPanel() {
		if (EastPanel == null) {
			try {
				EastPanel = new UIPanel();
				EastPanel.setName("EastPanel");
				// EastPanel.setPreferredSize(new java.awt.Dimension(400, 30));
				EastPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				// getEestPanel().add(getQuickQueryPanel(), "West");
				getEestPanel().add(getUILabelRecords(), "East");
			} catch (Throwable e) {
				handleException(e);
			}
		}
		return EastPanel;
	}

	/**
	 * 返回 NorthPanel 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	protected nc.ui.pub.beans.UIPanel getNorthPanel() {
		if (ivjNorthPanel == null) {
			try {
				ivjNorthPanel = new nc.ui.pub.beans.UIPanel();
				ivjNorthPanel.setName("NorthPanel");
				ivjNorthPanel
						.setPreferredSize(new java.awt.Dimension(1000, 30));
				ivjNorthPanel.setLayout(new BorderLayout());
				getNorthPanel().add(getWestPanel(), "West");
				getNorthPanel().add(getEestPanel(), "East");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjNorthPanel;
	}

	/**
	 * 人员采集自动编码 :获得单据编号VO。 创建日期：(2004-5-19 16:48:20)
	 */
	public nc.vo.pub.billcodemanage.BillCodeObjValueVO getObjVO() {
		if (objBillCodeVO == null) {
			objBillCodeVO = new nc.vo.pub.billcodemanage.BillCodeObjValueVO();
			objBillCodeVO.setAttributeValue("公司", Global.getCorpPK());
			objBillCodeVO.setAttributeValue("操作员", Global.getUserID());
		}
		return objBillCodeVO;
	}

	/**
	 * 返回 OtherCard 特性值。
	 * 
	 * @return nc.ui.hi.hi_301.HIBillCardPanel
	 */
	protected HIBillCardPanel getOtherCard() {
		if (ivjOtherCard == null) {
			try {
				ivjOtherCard = new nc.ui.hi.hi_301.HIBillCardPanel(
						allRelatedPara);
				ivjOtherCard.setName("OtherCard");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjOtherCard;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-5-31 14:26:07)
	 * 
	 * @return nc.ui.pub.print.PrintEntry
	 */
	private PrintEntry getPrintEntry() throws Exception {
		if (printEntry == null) {
			printEntry = new PrintEntry(this, this);
			printEntry.setTemplateID(Global.getCorpPK(), getModuleName(),
					Global.getUserID(), null, null);
		}
		return printEntry;
	}

	/**
	 * 返回 PsnCard 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	protected nc.ui.pub.beans.UIPanel getPsnCard() {
		if (ivjPsnCard == null) {
			try {
				ivjPsnCard = new nc.ui.pub.beans.UIPanel();
				ivjPsnCard.setName("PsnCard");
				ivjPsnCard.setPreferredSize(new java.awt.Dimension(100, 21));
				ivjPsnCard.setLayout(new java.awt.CardLayout());
				getPsnCard().add(getWorkCard(), getWorkCard().getName());
				getPsnCard().add(getDismissCard(), getDismissCard().getName());
				getPsnCard().add(getRetireCard(), getRetireCard().getName());
				getPsnCard().add(getLeaveCard(), getLeaveCard().getName());
				getPsnCard().add(getOtherCard(), getOtherCard().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjPsnCard;
	}

	/**
	 * 得到人员归属范围的名称
	 * 
	 * @param psnScopes
	 * @param psnclscope
	 * @return
	 */
	private String getPsnclscopeName(final Item[] psnScopes, Integer psnclscope) {
		if (psnScopes == null || psnclscope == null) {
			return "0";
		}
		String name = null;
		for (int i = 0; i < psnScopes.length; i++) {
			if (psnScopes[i].getValue() == psnclscope.intValue()) {
				name = psnScopes[i].getName();
				break;
			}
		}
		return name;
	}

	/**
	 * 返回 PsnList 特性值。
	 * 
	 * @return nc.ui.hi.hi_301.SortableBillScrollPane
	 */
	public SortableBillScrollPane getPsnList() {
		if (ivjPsnList == null) {
			try {
				ivjPsnList = new nc.ui.hi.hi_301.SortableBillScrollPane();
				ivjPsnList.setName("PsnList");
				ivjPsnList.setBorder(javax.swing.border.LineBorder
						.createGrayLineBorder());
				ivjPsnList.getTable().setTranslate(true);
				ivjPsnList.getTable().setSelectionMode(
						javax.swing.ListSelectionModel.SINGLE_SELECTION);
				ivjPsnList.getTable().getSelectionModel()
						.addListSelectionListener(this);
				// 表体右键按钮不可见
				ivjPsnList.setBBodyMenuShow(false);
				ivjPsnList.setModuleName(getModuleName());
				ivjPsnList.removeTableSortListener();
				
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjPsnList;
	}

	/**
	 * 获取人员列表的BillModel 创建日期：(2003-6-6 16:52:56)
	 * 
	 * @return nc.ui.pub.bill.BillModel
	 */
	public BillModel getPsnListModel() {
		// 获取当前显示项
		Pair[] items = getListItems();
		// 生成表头
		BillItem[] biaBody = new BillItem[items.length];
		Vector vExtension = new Vector();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getDataType() == CommonValue.DATATYPE_UFREF) {//
				// 显示
				biaBody[i] = new BillItem();
				biaBody[i].setName(items[i].getName());
				biaBody[i].setKey(items[i].getCode() + "_name");
				biaBody[i].setWidth(100);
				biaBody[i].setEnabled(true);
				biaBody[i].setEdit(false);
				biaBody[i].setShow(items[i].isVisible());
				biaBody[i].setNull(false);
				String[] loadFormula = getLoadFormula(items[i]);
				biaBody[i].setLoadFormula(loadFormula);
				vExtension.addElement(biaBody[i]);
				// 下面是主键
				BillItem billItem = new BillItem();
				billItem.setName(items[i].getName() + "主键");
				billItem.setKey(items[i].getCode());
				billItem.setWidth(100);
				billItem.setEnabled(true);
				billItem.setEdit(false);
				billItem.setShow(false);// debug false
				billItem.setNull(false);
				vExtension.addElement(billItem);
			} else {
				biaBody[i] = new BillItem();
				biaBody[i].setName(items[i].getName());
				if (items[i].getDataType() == CommonValue.DATATYPE_BOOLEAN) {
					biaBody[i].setDataType(BillItem.BOOLEAN);
				} else {
					biaBody[i].setDataType(BillItem.STRING);
				}
				// 设置序号数值类型
				if (items[i].getCode().equalsIgnoreCase("showorder")) {
					biaBody[i].setDataType(BillItem.INTEGER);
				}
				biaBody[i].setKey(items[i].getCode());
				biaBody[i].setWidth(100);
				biaBody[i].setEnabled(true);
				biaBody[i].setEdit(false);
				biaBody[i].setShow(items[i].isVisible());
				biaBody[i].setNull(false);
				vExtension.addElement(biaBody[i]);
			}
		}
		biaBody = new BillItem[vExtension.size()];
		vExtension.copyInto(biaBody);
		// 设置
		BillModel billModel = new EditBillModel();
		billModel.setBodyItems(biaBody);
		return billModel;
	}

	private class EditBillModel extends BillModel {
		@Override
		public boolean isCellEditable(int row, int col) {
			boolean edit = false;
			if (getBodyItems()[col].getKey().equals("showorder")
					&& Global.getCorpPK().equals(
							(String) psnList[row]
									.getAttributeValue("belong_pk_corp"))) {
				/*
				 * BillCellEditor cellEditor = (BillCellEditor)
				 * getPsnList().getTable().getCellEditor(); UIRefPane ref =
				 * (UIRefPane) cellEditor.getComponent(); ref.setEditable(true);
				 * ref.setEnabled(true);
				 */
				getPsnList().getTableModel().getItemByKey("showorder").setEdit(
						true);
				getPsnList().getTableModel().getItemByKey("showorder")
						.setEnabled(true);
				edit = true;
			}
			if (getBodyItems()[col].getKey().equals("#SEL_COL#")) {
				getPsnList().getTableModel().getItemByKey("#SEL_COL#").setEdit(
						true);
				getPsnList().getTableModel().getItemByKey("#SEL_COL#")
						.setEnabled(true);
				edit = true;
			}
			return edit;
		}
	}

	private HashMap hmRefType = null;

	protected HashMap getRefTypeHash() {
		if (hmRefType == null) {
			try {
				hmRefType = HIDelegator.getIInfoSet().getRefTypeHashAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return hmRefType;
	}

	/**
	 * 加载公式定义
	 * 
	 * @param pair
	 * @return
	 */
	private String[] getLoadFormula(Pair pair) {
		String[] loadFormula = null;
		if (pair.getDataType() == CommonValue.DATATYPE_UFREF) {
			final UIRefPane ref = new UIRefPane();
			ref.setReturnCode(false);
			ref.setButtonFireEvent(true);
			String RefType = pair.getRefType();
			if (Util.isDefdocPk(RefType)) {// 如果当前是自定义档案参照
				loadFormula = new String[] { pair.getCode() + "_name"
						+ "->getColValue(bd_defdoc,docname,pk_defdoc,"
						+ pair.getCode() + ")" };
			} else if (RefType.equals("归属公司")) {// fengwei 2010-11-29 给归属公司增加公式
				loadFormula = new String[] { pair.getCode() + "_name"
						+ "->getColValue(bd_corp,unitname,pk_corp,"
						+ pair.getCode() + ")" };
			} else {
				HashMap refTypeHash = getRefTypeHash();
				GeneralVO refvo = (GeneralVO) refTypeHash.get(RefType);
				if (refvo != null) {
					String tablename = (String) refvo
							.getAttributeValue("tablename");
					String namefield = (String) refvo
							.getAttributeValue("namefield");
					String pkfield = (String) refvo
							.getAttributeValue("pkfield");
					loadFormula = new String[] { pair.getCode() + "_name"
							+ "->getColValue(" + tablename + "," + namefield
							+ "," + pkfield + "," + pair.getCode() + ")" };
				}
			}
		}
		return loadFormula;
	}

	/**
	 * 获取当前人员列表的主键（除去引用人员）。 创建日期：(2004-5-31 10:32:16)
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getselectPsnPksExceptRef(String tableCode) {
		if (psnList == null)
			return null;
		int[] rows = getPsnList().getHeadSelectedRows();
		if (rows.length < 1)
			return null;

		Vector v = new Vector();
		try {
			for (int i = 0; i < rows.length; i++) {
				if (((Integer) psnList[rows[i]]
						.getAttributeValue("jobtypeflag")).intValue() == 0
						&& Global.getCorpPK().equals(
								(String) psnList[rows[i]]
										.getAttributeValue("belong_pk_corp"))
						&& Global.getCorpPK().equals(
								(String) psnList[rows[i]]
										.getAttributeValue("pk_corp"))) {
					if (tableCode.equalsIgnoreCase("hi_psndoc_deptchg")
							|| tableCode.equalsIgnoreCase("hi_psndoc_ctrt")
							|| tableCode.equalsIgnoreCase("hi_psndoc_part")
							|| tableCode.equalsIgnoreCase("hi_psndoc_training")
							|| tableCode.equalsIgnoreCase("hi_psndoc_ass")
							|| tableCode.equalsIgnoreCase("hi_psndoc_retire")
							|| tableCode.equalsIgnoreCase("hi_psndoc_orgpsn")
							|| tableCode.equalsIgnoreCase("hi_psndoc_psnchg")
							|| tableCode
									.equalsIgnoreCase("hi_psndoc_dimission")
							|| tableCode.equalsIgnoreCase("bd_psndoc"))
						v.addElement((String) psnList[rows[i]]
								.getAttributeValue("pk_psndoc"));
					else
						v.addElement((String) psnList[rows[i]]
								.getAttributeValue("pk_psnbasdoc"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] pks = new String[v.size()];
		v.copyInto(pks);
		return pks;
	}

	/**
	 * 加载查询模板，初始化查询条件内部参照
	 * 
	 * @return
	 * @throws Exception
	 */
	protected QueryConditionClient getQueryDlg() throws Exception {
		if (queryDlg == null) {
			queryDlg = new QueryConditionClient(this);
			// 加载查询模板
			queryDlg.setTempletID(Global.getCorpPK(), getModuleName(), Global
					.getUserID(), null);

			queryDlg.setMultiTableFlag(true);
			queryDlg.setLocationRelativeTo(this);
			// 常用查询
			queryDlg.getUIPanelNormal().add(new NormalQueryPanel(),
					BorderLayout.CENTER);
			queryDlg.getUIPanelNormal().updateUI();
			queryDlg.hideUnitButton();
			queryDlg.setNormalShow(true);
			// 设置自定义档案参照
			nc.vo.pub.query.QueryConditionVO[] conditionvos = queryDlg
					.getConditionDatas();
			if (conditionvos != null && conditionvos.length > 0) {
				for (int i = 0; i < conditionvos.length; i++) {
					if (conditionvos[i].getDataType().intValue() == 5) {// 参照类型
						if (Util.isDefdocPk(conditionvos[i].getConsultCode())) {
							nc.ui.hi.pub.PsnInfFldRefPane ref = new nc.ui.hi.pub.PsnInfFldRefPane(
									conditionvos[i].getConsultCode());
							queryDlg.setValueRef(
									conditionvos[i].getTableCode(),
									conditionvos[i].getFieldCode(), ref);
						} else if ("人员档案".equals(conditionvos[i]
								.getConsultCode())
								|| "人员档案HR".equals(conditionvos[i]
										.getConsultCode())) {
							nc.ui.pub.beans.UIRefPane psndocref = new nc.ui.pub.beans.UIRefPane();
							psndocref.setRefNodeName(conditionvos[i]
									.getConsultCode());
							psndocref.getRefModel().setUseDataPower(false);
							if (getModuleName().equals("600704")) {
								setWhereToModel(
										psndocref.getRefModel(),
										"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or "
												+ "( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
							} else {
								setWhereToModel(psndocref.getRefModel(),
										"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
							}
							// 人员类别权限
							// 连接数优化
							String psnwhere = psndocref.getRefModel()
									.getWherePart();
							// if (GlobalTool.isUsedDataPower("bd_psncl",
							// Global.getCorpPK())) {
							String powerPsnclSql = nc.ui.hr.global.GlobalTool
									.getPowerSql("bd_psncl",
											Global.getUserID(), Global
													.getCorpPK());
							if (powerPsnclSql != null
									&& powerPsnclSql.length() > 0) {
								psnwhere += " and bd_psndoc.pk_psncl in ("
										+ powerPsnclSql + ") ";
							}
							// }

							psndocref.getRefModel().setWherePart(psnwhere);
							String tablecode = conditionvos[i].getTableCode();
							String fieldcode = conditionvos[i].getFieldCode();
							if (tablecode != null && fieldcode != null) {
								queryDlg.setValueRef(tablecode, fieldcode,
										psndocref);
							}
						} else if ("部门档案".equals(conditionvos[i]
								.getConsultCode())
								|| "部门档案HR".equals(conditionvos[i]
										.getConsultCode())
								|| "<nc.ui.hi.ref.DeptRefModel>"
										.equals(conditionvos[i]
												.getConsultCode())) {
							nc.ui.pub.beans.UIRefPane deptref = new nc.ui.pub.beans.UIRefPane();
							deptref.setRefType(1);
							deptref.setRefInputType(1);
							if ("<nc.ui.hi.ref.DeptRefModel>"
									.equals(conditionvos[i].getConsultCode())) {
								nc.ui.hi.ref.DeptRefModel deptModel = new nc.ui.hi.ref.DeptRefModel();
								deptref.setRefModel(deptModel);
							} else {
								deptref.setRefNodeName(conditionvos[i]
										.getConsultCode());
							}
							/*
							 * if (getModuleName().equals("600704")) {
							 * deptref.getRefModel().setUseDataPower(false);
							 * setWhereToModel( deptref.getRefModel(),
							 * "((bd_deptdoc.canceled = 'N' and
							 * bd_deptdoc.hrcanceled = 'N') or " + "(
							 * bd_deptdoc.canceled is null and
							 * bd_deptdoc.hrcanceled is null))"); } else {
							 */
							setWhereToModel(deptref.getRefModel(),
									"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
							// }
							String tablecode = conditionvos[i].getTableCode();
							String fieldcode = conditionvos[i].getFieldCode();
							if (tablecode != null && fieldcode != null) {
								queryDlg.setValueRef(tablecode, fieldcode,
										deptref);
							}
						}
					}
				}
			}
			SpaDeptdocRefGridTreeModel psnrefmodel = null;
			String wheresql = " bd_deptdoc.pk_deptdoc = V_HR_PSNDOC_MULTI_JOB.pk_deptdoc and bd_psndoc.dr = 0 "
					+ "and bd_psndoc.indocflag = '" + getIndocflag() + "' ";
			if (getModuleName().equals("600707")
					|| getModuleName().equals("600704")
					|| getModuleName().equals("600708")) {
				psnrefmodel = new SpaDeptdocRefGridTreeModel();
				psnrefmodel.setIsLimit(0);
				String deptsql = "0=0";
				if ((powerSql.length() > 0 && powerSql.trim().startsWith(
						"select"))
						&& !getModuleName().equals("600704")) {
					deptsql = " bd_deptdoc.pk_deptdoc in (" + powerSql + ")";
				}
				psnrefmodel.setClassWherePart(deptsql);
			}
			psnrefmodel.setCacheEnabled(false);
			if (getModuleName().equals("600707")
					|| getModuleName().equals("600708")) {
				wheresql += "and bd_psndoc.psnclscope = " + getSelPsnclscope();
			}
			psnrefmodel.setWherePart(wheresql);
			nc.ui.pub.beans.UIRefPane psnref = new nc.ui.pub.beans.UIRefPane();
			psnref.setRefType(1);
			psnref.setRefInputType(1);
			psnrefmodel.setUseDataPower(false);
			if (getModuleName().equalsIgnoreCase("600704")) {
				setWhereToModel(
						psnrefmodel,
						"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or "
								+ "( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
			} else {
				setWhereToModel(psnrefmodel,
						"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
				if ((powerSql.length() > 0 && powerSql.trim().startsWith(
						"select"))
						&& !getModuleName().equals("600704")) {
					setWhereToModel(psnrefmodel, "( bd_deptdoc.pk_deptdoc in ("
							+ powerSql + ") )");
				}
			}
			psnref.setRefModel(psnrefmodel);
			// 人员类别权限
			// 连接数优化
			String psnwhere = psnrefmodel.getWherePart();
			// if (GlobalTool.isUsedDataPower("bd_psncl", Global.getCorpPK())) {
			String powerPsnclSql = nc.ui.hr.global.GlobalTool.getPowerSql(
					"bd_psncl", Global.getUserID(), Global.getCorpPK());
			if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
				psnwhere += " and bd_psndoc.pk_psncl in (" + powerPsnclSql
						+ ") ";
			}
			// }
			psnrefmodel.setWherePart(psnwhere);
			queryDlg.setValueRef("bd_psndoc", "bd_psndoc.pk_psndoc", psnref);

			if (getModuleName().equals("600707")
					|| getModuleName().equalsIgnoreCase("600704")
					|| getModuleName().equals("600708")) {
				nc.ui.hi.ref.JobRef jobmodel = new nc.ui.hi.ref.JobRef();
				String where = jobmodel.getWherePart();
				if (powerSql.length() > 0
						&& powerSql.trim().startsWith("select")
						&& !getModuleName().equalsIgnoreCase("600704")) {
					where += " and om_job.pk_deptdoc in (" + powerSql + ")";
				} else {
					where += " and (0=0)";
				}
				jobmodel.setWherePart(where);
				jobmodel.setRefTitle(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600704", "UPP600704-000068")/*
																	 * @res
																	 * "岗位参照"
																	 */);
				nc.ui.pub.beans.UIRefPane jobref = new nc.ui.pub.beans.UIRefPane();
				jobref.setRefType(1);
				jobref.setRefInputType(1);
				jobref.setRefModel(jobmodel);
				queryDlg
						.setValueRef("bd_psndoc", "bd_psndoc.pk_om_job", jobref);
				// 部门档案参照
				nc.ui.pub.beans.UIRefPane deptref = new nc.ui.pub.beans.UIRefPane();
				deptref.setRefType(1);
				deptref.setRefInputType(1);
				deptref.setRefNodeName("部门档案HR");
				// if (getModuleName().equalsIgnoreCase("600704")) {
				// deptref.getRefModel().setUseDataPower(false);
				// }

				// 是否包含下级
				deptref.setIncludeSubShow(true);
				// wangkf 不查询撤销或者封存的部门
				if (getModuleName().equalsIgnoreCase("600704")) {
					setWhereToModel(
							deptref.getRefModel(),
							"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or "
									+ "( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
				} else {
					setWhereToModel(deptref.getRefModel(),
							"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
				}
				queryDlg.setValueRef("bd_psndoc", "bd_psndoc.pk_deptdoc",
						deptref);
			}
			// 设置人员类别显示已封存数据
			nc.ui.pub.beans.UIRefPane psnclsref = new nc.ui.pub.beans.UIRefPane();
			psnclsref.setRefType(1);
			psnclsref.setRefInputType(1);
			psnclsref.setRefNodeName("人员类别");
			psnclsref.getRefModel().setSealedDataShow(true);
			queryDlg.setValueRef("bd_psndoc", "bd_psndoc.pk_psncl", psnclsref);
		}
		return queryDlg;
	}

	/**
	 * 查询模板
	 * 
	 * 创建日期：(2002-4-27 11:18:09)
	 * 
	 * @return nc.ui.pub.query.QueryConditionClient
	 */
	public HrQueryDialog getQueryDialog() {

		if (queryDialog == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			tempinfo.setPk_Org(ClientEnvironment.getInstance().getCorporation()
					.getPrimaryKey());
			tempinfo.setCurrentCorpPk(Global.getCorpPK());
			tempinfo.setFunNode(getModuleName());
			tempinfo.setUserid(Global.getUserID());

			queryDialog = new HrQueryDialog(this, tempinfo);

			queryDialog.setFieldValueEditor(this);
			// 采集节点审批状态只能有“未审批”和“未批准”
			if (getModuleName().equals("600704")) {
				IConstEnum[] STATUS = new DefaultConstEnum[] {
						new DefaultConstEnum(0, nc.ui.ml.NCLangRes
								.getInstance().getStrByID("600700",
										"UPP600700-000362")/* @res "未审批" */),
						new DefaultConstEnum(2, nc.ui.ml.NCLangRes
								.getInstance().getStrByID("600700",
										"UPP600700-000363")/* @res "未批准" */) };

				queryDialog.setFieldValueElementEnum(
						"bd_psnbasdoc.approveflag", STATUS);
			}
			// 合同类型
			queryDialog.setFieldValueElementEnum("hi_psndoc_ctrt.iconttype",
					new DefaultConstEnum[] {
							new DefaultConstEnum(new Integer(1),
									nc.ui.ml.NCLangRes.getInstance()
											.getStrByID("600704",
													"UPP600704-000045")/*
																		 * @res
																		 * "签订"
																		 */),
							new DefaultConstEnum(new Integer(4),
									nc.ui.ml.NCLangRes.getInstance()
											.getStrByID("600704",
													"UPP600704-000046")/*
																		 * @res
																		 * "变更"
																		 */),
							new DefaultConstEnum(new Integer(3),
									nc.ui.ml.NCLangRes.getInstance()
											.getStrByID("600704",
													"UPP600704-000047")/*
																		 * @res
																		 * "续签"
																		 */),
							new DefaultConstEnum(new Integer(6),
									nc.ui.ml.NCLangRes.getInstance()
											.getStrByID("600704",
													"UPP600704-000049")/*
																		 * @res
																		 * "解除"
																		 */),
							new DefaultConstEnum(new Integer(5),
									nc.ui.ml.NCLangRes.getInstance()
											.getStrByID("common",
													"UC001-0000032")/*
																	 * @res "终止"
																	 */) });

			queryDialog.setMultiTable(true);
			// 增加监听(人员范围改变时要更新一些参照的条件)
			queryDialog
					.registerCriteriaEditorListener(new ICriteriaChangedListener() {
						public void criteriaChanged(CriteriaChangedEvent event) {
							// if(event.getEventtype()==CriteriaChangedEvent.FILTER_CHANGED
							// ||
							// event.getEventtype()==CriteriaChangedEvent.FILTEREDITOR_INITIALIZED){
							if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
								if (event.getFieldCode().equals(
										"bd_psndoc.pk_psndoc")) {
									// 此处要换参照
									if (getModuleName().equals("600704"))
										return;
									String pk_corp = Global.getCorpPK();
									DefaultFilterEditor dfe = (DefaultFilterEditor) event
											.getFiltereditor();
									nc.ui.pub.beans.UIRefPane psnref = (UIRefPane) ((DefaultFieldValueEditor) dfe
											.getFieldValueEditor())
											.getFieldValueElemEditor()
											.getFieldValueElemEditorComponent();

									if (getSelPsnclscope() != 0
											&& (getModuleName()
													.equalsIgnoreCase("600707") || getModuleName()
													.equalsIgnoreCase("600708"))) {
										psnref
												.setRefModel(new PsndocDefaulRefModel(
														"人员档案"));
										psnref.getRefModel().setPk_corp(
												Global.getCorpPK());
										// psnref.setMultiCorpRef(true); //
										// 是否显示公司参照
										String psnwhere = " bd_psndoc.indocflag = 'Y' and bd_psndoc.psnclscope = "
												+ getSelPsnclscope();
										try {
											// 连接数优化
											// if
											// (GlobalTool.isUsedDataPower("bd_psncl",
											// pk_corp)) {
											String powerPsnclSql = nc.ui.hr.global.GlobalTool
													.getPowerSql("bd_psncl",
															Global.getUserID(),
															pk_corp);
											if (powerPsnclSql != null
													&& powerPsnclSql.length() > 0) {
												psnwhere += " and bd_psndoc.pk_psncl in ("
														+ powerPsnclSql + ") ";
											}
											// }
										} catch (Exception e) {
											e.printStackTrace();
										}
										((nc.ui.bd.ref.busi.PsndocDefaulRefModel) psnref
												.getRefModel())
												.setWherePart(psnwhere);
										return;
									}

									psnref.setRefType(1);
									psnref.setRefInputType(1);
									SpaDeptdocRefGridTreeModel psnrefmodel = new SpaDeptdocRefGridTreeModel();
									String wheresql = " bd_deptdoc.pk_deptdoc = V_HR_PSNDOC_MULTI_JOB.pk_deptdoc and bd_psndoc.dr = 0 "
											+ "and bd_psndoc.indocflag = '"
											+ getIndocflag() + "' ";

									psnrefmodel.setCacheEnabled(false);
									if (getModuleName().equalsIgnoreCase(
											"600707")
											|| getModuleName()
													.equalsIgnoreCase("600708")) {
										wheresql += "and bd_psndoc.psnclscope = "
												+ getSelPsnclscope();
									}

									psnrefmodel.setUseDataPower(false);
									if (getModuleName().equalsIgnoreCase(
											"600704")) {
										setWhereToModel(
												psnrefmodel,
												"((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or "
														+ "( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
									} else {
										setWhereToModel(psnrefmodel,
												"((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
										if ((powerSql.length() > 0 && powerSql
												.trim().startsWith("select"))
												&& !getModuleName().equals(
														"600704")) {
											setWhereToModel(psnrefmodel,
													"( bd_deptdoc.pk_deptdoc in ("
															+ powerSql + ") )");
										}
									}
									// 人员类别权限
									// 连接数优化
									try {
										// if
										// (GlobalTool.isUsedDataPower("bd_psncl",
										// Global.getCorpPK())) {
										String powerPsnclSql = nc.ui.hr.global.GlobalTool
												.getPowerSql("bd_psncl", Global
														.getUserID(), Global
														.getCorpPK());
										if (powerPsnclSql != null
												&& powerPsnclSql.length() > 0) {
											wheresql += " and bd_psndoc.pk_psncl in ("
													+ powerPsnclSql + ") ";
										}
										// }
									} catch (Exception e) {
										e.printStackTrace();
									}
									psnrefmodel.setWherePart(wheresql);
									psnref.setRefModel(psnrefmodel);
								} else if (event.getFieldCode().equals(
										"bd_psndoc.pk_psncl")
										&& !getModuleName().equalsIgnoreCase(
												"600704")) {// 更改人员类别的选择范围
									DefaultFilterEditor dfe = (DefaultFilterEditor) event
											.getFiltereditor();
									nc.ui.pub.beans.UIRefPane psnref = (UIRefPane) ((DefaultFieldValueEditor) dfe
											.getFieldValueEditor())
											.getFieldValueElemEditor()
											.getFieldValueElemEditorComponent();
									String pk_cl = psnref.getRefPK();
									String code_cl = psnref.getRefCode();
									// fengwei 2010-11-11 人员类别过滤掉已封存的项
									psnref.getRefModel().addWherePart(
											" and psnclscope ="
													+ getSelPsnclscope()
													+ " and sealflag is null ");
									psnref.setPK(pk_cl);

								} else if ((event.getFieldCode().equals(
										"bd_psndoc.pk_deptdoc") || event
										.getFieldCode().equals(
												"hi_psndoc_deptchg.pk_deptdoc"))
										&& (getModuleName().equalsIgnoreCase(
												"600707") || getModuleName()
												.equalsIgnoreCase("600704"))) {
									DefaultFilterEditor dfe = (DefaultFilterEditor) event
											.getFiltereditor();
									final nc.ui.pub.beans.UIRefPane deptref = (UIRefPane) ((DefaultFieldValueEditor) dfe
											.getFieldValueEditor())
											.getFieldValueElemEditor()
											.getFieldValueElemEditorComponent();
									deptref.setButtonFireEvent(true);
									try {
										// 支持显示下级公司部门
										if (Global.getCorpPK() != null) {
											deptref.setMultiCorpRef(true);
											ICorpQry corpQ = (ICorpQry) NCLocator
													.getInstance().lookup(
															ICorpQry.class
																	.getName());
											// 查询本下级公司
											CorpVO[] corps = null;
											if ("0001".equalsIgnoreCase(Global
													.getCorpPK())) {
												corps = corpQ.getAllChildCorp(
														Global.getCorpPK(),
														UFBoolean.TRUE);
											} else {
												corps = corpQ
														.getSelfAndAllChildCorp(
																Global
																		.getCorpPK(),
																UFBoolean.TRUE);
											}// end if
											String[] arrcorps = new String[corps.length];
											for (int i = 0; i < corps.length; i++) {
												arrcorps[i] = corps[i]
														.getPk_corp();
											}
											deptref.getRefUIConfig()
													.setMultiCorpRefPks(
															arrcorps);

											// modified by zhangdd,
											// 为弹出的参照对话框中的顶部公司对话框加入事件监听，以此来设定部门权限
											((AbstractRefDialog) deptref
													.getRef().getRefUI())
													.getButtonPanelFactory()
													.getRefCorp()
													.addValueChangedListener(
															new ValueChangedListener() {

																public void valueChanged(
																		ValueChangedEvent event) {
																	UIRefPane corpPane = (UIRefPane) event
																			.getSource();
																	String selectedCorp = corpPane
																			.getRefPK();
																	if (selectedCorp == null) {
																		return;
																	}
																	if (Global
																			.getCorpPK()
																			.equals(
																					selectedCorp)) {
																		deptref
																				.getRefModel()
																				.setUseDataPower(
																						true);
																	} else {
																		deptref
																				.getRefModel()
																				.setUseDataPower(
																						false);
																	}
																}

															});

										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									// ---------------------------------
								} else if (event.getFieldCode().equals(
										"bd_psndoc.pk_deptdoc")
										&& getModuleName().equalsIgnoreCase(
												"600710")) {
									DefaultFilterEditor dfe = (DefaultFilterEditor) event
											.getFiltereditor();
									nc.ui.pub.beans.UIRefPane deptref = (UIRefPane) ((DefaultFieldValueEditor) dfe
											.getFieldValueEditor())
											.getFieldValueElemEditor()
											.getFieldValueElemEditorComponent();
									deptref.setButtonFireEvent(true);
									try {
										// 支持显示下级公司部门
										if (Global.getCorpPK() != null) {
											deptref.setMultiCorpRef(true);
											ICorpQry corpQ = (ICorpQry) NCLocator
													.getInstance().lookup(
															ICorpQry.class
																	.getName());
											// 查询本下级公司
											CorpVO[] corps = null;
											if ("0001".equalsIgnoreCase(Global
													.getCorpPK())) {
												corps = corpQ.getAllChildCorp(
														Global.getCorpPK(),
														UFBoolean.TRUE);
											} else {
												corps = corpQ
														.getSelfAndAllChildCorp(
																Global
																		.getCorpPK(),
																UFBoolean.TRUE);
											}// end if
											String[] arrcorps = new String[corps.length];
											for (int i = 0; i < corps.length; i++) {
												arrcorps[i] = corps[i]
														.getPk_corp();
											}
											deptref.getRefUIConfig()
													.setMultiCorpRefPks(
															arrcorps);
											deptref.getRefModel()
													.setUseDataPower(false);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									// ---------------------------------
								} else if (event.getFieldCode().equals(
										"hi_psndoc_deptchg.pk_deptdoc")
										&& getModuleName().equalsIgnoreCase(
												"600710")) {
									DefaultFilterEditor dfe = (DefaultFilterEditor) event
											.getFiltereditor();
									nc.ui.pub.beans.UIRefPane deptref = (UIRefPane) ((DefaultFieldValueEditor) dfe
											.getFieldValueEditor())
											.getFieldValueElemEditor()
											.getFieldValueElemEditorComponent();
									deptref.setButtonFireEvent(true);
									try {
										// 支持显示下级公司部门
										if (Global.getCorpPK() != null) {
											deptref.setMultiCorpRef(true);
											ICorpQry corpQ = (ICorpQry) NCLocator
													.getInstance().lookup(
															ICorpQry.class
																	.getName());
											// 查询本下级公司
											CorpVO[] corps = null;
											if ("0001".equalsIgnoreCase(Global
													.getCorpPK())) {
												corps = corpQ.getAllChildCorp(
														Global.getCorpPK(),
														UFBoolean.TRUE);
											} else {
												corps = corpQ
														.getSelfAndAllChildCorp(
																Global
																		.getCorpPK(),
																UFBoolean.TRUE);
											}// end if
											String[] arrcorps = new String[corps.length];
											for (int i = 0; i < corps.length; i++) {
												arrcorps[i] = corps[i]
														.getPk_corp();
											}
											deptref.getRefUIConfig()
													.setMultiCorpRefPks(
															arrcorps);
											deptref.getRefModel()
													.setUseDataPower(false);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					});
			if (getModuleName().equalsIgnoreCase("600708")) {
				queryDialog
						.registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
							public void processQueryTempletTotalVO(
									QueryTempletTotalVO totalVO) {
								if (((Item) getCmbPsncl().getSelectedItem())
										.getValue() == 0)
									return;
								if (totalVO != null) {
									QueryConditionVO[] qryconds = totalVO
											.getConditionVOs();
									for (QueryConditionVO qcvo : qryconds) {
										if (qcvo.getFieldCode().equals(
												"hi_psndoc_deptchg.isreturn")) {
											qcvo.setIfDefault(new UFBoolean(
													false));
										}
									}
								}
							}
						});
			}
			// add by suhf1
			if(getModuleName().equalsIgnoreCase("600707")){
				queryDialog
				.registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
					public void processQueryTempletTotalVO(
							QueryTempletTotalVO totalVO) {
						//add by suhf1
						if (totalVO != null) {
							QueryConditionVO[] qryconds = totalVO
									.getConditionVOs();
							for (int i = 0 ;i <qryconds.length;i++ ) {
								if(qryconds[i].getFieldCode().trim().equalsIgnoreCase("bd_psndoc.xhhrp1")) {
									if(qryconds[i].getOperaCode().equalsIgnoreCase("like@not like@")) {
										qryconds[i].getOperaName();
										qryconds[i].setOperaName("前截取@后截取@");
										qryconds[i].getOperaName();
									}
								}
								if(qryconds[i].getFieldCode().trim().equalsIgnoreCase("bd_psndoc.xhhrp2")) {
									if(qryconds[i].getOperaCode().equalsIgnoreCase("like@not like@")) {
										qryconds[i].getOperaName();
										qryconds[i].setOperaName("前截取@后截取@");
									}
								}
							}
						}
						if (((Item) getCmbPsncl().getSelectedItem())
								.getValue() == 0)
							return;
						if (totalVO != null) {
							QueryConditionVO[] qryconds = totalVO
									.getConditionVOs();
							for (QueryConditionVO qcvo : qryconds) {
								if (qcvo.getFieldCode().equals(
										"hi_psndoc_deptchg.isreturn")) {
									qcvo.setIfDefault(new UFBoolean(
											false));
								}
							}
						}
					}
				});
		
			}
		}
		return queryDialog;

	}

	/**
	 * 获得当前表的关联字段。 创建日期：(2004-5-30 15:21:00)
	 * 
	 * @return java.util.Hashtable
	 */
	protected Hashtable getRelationMap() {
		return getCard().getRelationMap();
	}

	/**
	 * 返回 RetireCard 特性值。
	 * 
	 * @return nc.ui.hi.hi_301.HIBillCardPanel
	 */
	protected HIBillCardPanel getRetireCard() {
		if (ivjRetireCard == null) {
			try {
				ivjRetireCard = new nc.ui.hi.hi_301.HIBillCardPanel(
						allRelatedPara);
				ivjRetireCard.setName("RetireCard");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjRetireCard;
	}

	public String getSelCorpPK() {
		return corpPK;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-3 11:37:59)
	 * 
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 */
	protected CtrlDeptVO getSelectedDept() {
		// 获取当前选择的部门
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null)
			return null;
		return (CtrlDeptVO) node.getUserObject();
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-3 12:08:14)
	 * 
	 * @return int
	 */
	protected Item getSelectedItem() {
		return (Item) getCmbPsncl().getSelectedItem();
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-3 11:36:53)
	 * 
	 * @return javax.swing.tree.DefaultMutableTreeNode
	 */
	protected DefaultMutableTreeNode getSelectedNode() {
		// 获取当前选择的部门
		TreePath path = getDeptTree().getSelectionPath();
		if (path == null)
			return null;
		return (DefaultMutableTreeNode) path.getLastPathComponent();
	}

	// /**
	// * 取得包含指定子集的人的信息
	// * @author:wangkf 王开福
	// */
	// protected PersonEAVO[] getSubInfo(String tableCode, String[] pk_psndocs)
	// {
	// if (pk_psndocs == null || pk_psndocs.length < 1) {
	// return null;
	// }
	// PersonEAVO[] curPersons = new PersonEAVO[pk_psndocs.length];
	// try {
	// for (int i = 0; i < pk_psndocs.length; i++) {
	// curPersons[i] = new PersonEAVO();
	// curPersons[i].setPk_psndoc(pk_psndocs[i]);
	// GeneralVO[] infoVOs =
	// HIDelegator.getPsnInf().queryPersonInfo(pk_psndocs[i], tableCode);
	// // 添加到人员数据对象中
	// SubTable subtable = new SubTable();
	// subtable.setTableCode(tableCode);
	// if (infoVOs != null) {// wangkf add
	// transPkToName(tableCode, infoVOs);
	// subtable.setRecordArray(infoVOs);
	// }
	// curPersons[i].addSubtable(subtable);
	// }
	// } catch (Exception e) {
	// reportException(e);
	// showErrorMessage(e.getMessage());
	// }
	// return curPersons;
	// }

	/**
	 * 或全当前正在操作的人员的主键。 创建日期：(2004-5-17 11:11:38)
	 * 
	 * @return java.lang.String
	 */
	private String[] getTempPsnPk() {
		String[] keys = new String[2];
		// 如果人员数据还没有初始化
		if (person == null) {
			if (listSelectRow != -1 && listSelectRow < psnList.length) {
				keys[0] = (String) psnList[listSelectRow]
						.getAttributeValue("pk_psnbasdoc");
				keys[1] = (String) psnList[listSelectRow]
						.getAttributeValue("pk_psndoc");
				return keys;
			} else {
				// 没有选中任何人，返回null
				return null;
			}
		}
		// 返回当前人的人员主键
		keys[0] = (String) person.getPk_psnbasdoc();
		keys[1] = (String) person.getPk_psndoc();
		return keys;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-3 12:02:24)
	 * 
	 * @return int
	 */
	protected int getTempPsnScope() {
		return -1;
	}

	/**
	 * 子类实现该方法，返回业务界面的标题。
	 * 
	 * @version (00-6-6 13:33:25)
	 * @return java.lang.String
	 */
	public String getTitle() {
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
				"UPP600704-000069")/* @res "员工信息采集" */;
	}

	/**
	 * 得到当前选中的公司，如果是根结点得到树上的所有公司，第一级肯定是当前公司，其他公司都在第二级。 创建日期：(2004-10-21 8:59:02)
	 * wangkf fixed :现在子公司与当前公司并列第一级。
	 * 
	 * @return java.lang.String
	 */
	public String getTreeSelCorpPK() throws Exception {
		DefaultMutableTreeNode node = getSelectedNode();

		if (node == null || node.isRoot()) {
			// dusx 修改2008.11.26：若是根，则返回登录公司。
			// if (allCorpPks == null) {
			// CtrlDeptVO[] Corpvos =
			// HIDelegator.getPsnInf().queryAllRelatedCorps(Global.getUserID(),
			// Global.getCorpPK(), true);
			// if(Corpvos!=null){
			// allCorpPks = new String[Corpvos.length];
			// for(int i=0;i<Corpvos.length;i++){
			// allCorpPks[i]= Corpvos[i].getPk_corp();
			// }
			// }

			// }
			return Global.getCorpPK();
		} else {
			CtrlDeptVO corpvo = (CtrlDeptVO) node.getUserObject();
			return corpvo.getPk_corp();
		}
		// return allCorpPks;
		// dusx修改，2008.11.24
		// String nowcorp = "";

		// if (node == null || node.isRoot()) {
		// nowcorp = Global.getCorpPK();
		// }else{
		// CtrlDeptVO corpvo = (CtrlDeptVO) node.getUserObject();
		// nowcorp = corpvo.getPk_corp() ;
		// }
		// //如果是不包含下级，则只返回选择的单位。
		// if(!getincludeChildDeptPsn().isSelected()){
		// return new String[]{nowcorp};
		// }

		// String[] selectCorppks = null;
		// CtrlDeptVO[] Corpvos =
		// HIDelegator.getPsnInf().queryAllRelatedCorps(Global.getUserID(),
		// nowcorp, true);
		// if(Corpvos!=null){
		// selectCorppks = new String[Corpvos.length];
		// for(int i=0;i<Corpvos.length;i++){
		// selectCorppks[i]= Corpvos[i].getPk_corp();
		// }
		// }

		// return selectCorppks;
	}

	/**
	 * 查询所有公司
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] getAllCorpPks() throws Exception {

		if (allCorpPks == null) {
			CtrlDeptVO[] Corpvos = HIDelegator.getPsnInf()
					.queryAllRelatedCorps(Global.getUserID(),
							Global.getCorpPK(), true);
			if (Corpvos != null) {
				allCorpPks = new String[Corpvos.length];
				for (int i = 0; i < Corpvos.length; i++) {
					allCorpPks[i] = Corpvos[i].getPk_corp();
				}
			}
		}
		return allCorpPks;
	}

	/**
	 * 返回 归属范围。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	protected nc.ui.pub.beans.UILabel getLbScope() {
		if (ivjUILabel1 == null) {
			try {
				ivjUILabel1 = new nc.ui.pub.beans.UILabel();
				ivjUILabel1.setName("UILabel1");
				ivjUILabel1
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"600704", "upt600704-000029")/* @res "归属范围" */);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel1;
	}

	private nc.ui.pub.beans.UILabel ivjUILabelRecords = null;

	/**
	 * 
	 * @return
	 */
	protected nc.ui.pub.beans.UILabel getUILabelRecords() {
		if (ivjUILabelRecords == null) {
			try {
				ivjUILabelRecords = new nc.ui.pub.beans.UILabel();
				ivjUILabelRecords.setName("UILabelRecords");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabelRecords;
	}

	/**
	 * 返回 UILabelType 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	protected nc.ui.pub.beans.UILabel getUILabelType() {
		if (ivjUILabelType == null) {
			try {
				ivjUILabelType = new nc.ui.pub.beans.UILabel();
				ivjUILabelType.setName("UILabelType");
				ivjUILabelType
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"600704", "UPP600704-000065")/* @res "任职类型" */);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabelType;
	}

	/**
	 * 返回 UIScrollPane1 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIScrollPane
	 */
	protected nc.ui.pub.beans.UIScrollPane getUIScrollPane() {
		if (ivjUIScrollPane == null) {
			try {
				ivjUIScrollPane = new nc.ui.pub.beans.UIScrollPane();
				ivjUIScrollPane.setName("UIScrollPane");
				ivjUIScrollPane.setBorder(javax.swing.border.LineBorder
						.createGrayLineBorder());
				getUIScrollPane().setViewportView(getDeptTree());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIScrollPane;
	}

	/**
	 * 返回 UISplitPane1 特性值。
	 * 
	 * @return nc.ui.pub.beans.UISplitPane
	 */
	protected nc.ui.pub.beans.UISplitPane getUISplitPane() {
		if (ivjUISplitPane == null) {
			try {
				ivjUISplitPane = new nc.ui.pub.beans.UISplitPane(1);
				ivjUISplitPane.setName("UISplitPane");
				ivjUISplitPane.setDividerSize(4);
				// ivjUISplitPane.setBorder(new
				// javax.swing.border.EmptyBorder(0,0, 0, 0));
				ivjUISplitPane.setDividerLocation(180);
				getUISplitPane().add(getUIScrollPane(), "left");
				getUISplitPane().add(getCenterPanelRight(), "right");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUISplitPane;
	}

	/**
	 * 返回 UISplitPaneVertical 特性值
	 * 
	 * @return nc.ui.pub.beans.UISplitPane
	 */
	protected nc.ui.pub.beans.UISplitPane getUISplitPaneVertical() {
		if (ivjUISplitPaneVertical == null) {
			try {
				ivjUISplitPaneVertical = new nc.ui.pub.beans.UISplitPane(
						javax.swing.JSplitPane.VERTICAL_SPLIT);
				ivjUISplitPaneVertical.setName("UISplitPaneVertical");
				ivjUISplitPaneVertical.setDividerLocation(200);
				ivjUISplitPaneVertical.setDividerSize(4);
				getUISplitPaneVertical().add(getPsnList(), "top");
				getUISplitPaneVertical().add(getPsnCard(), "bottom");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUISplitPaneVertical;
	}

	/**
	 * 获取当前归属范围的虚拟节点，缺省为当前节点加上扩展节点号，可在子类中重载。 创建日期：(2004-6-1 9:36:41)
	 * 
	 * @return java.lang.String
	 * @param nodeKey
	 *            java.lang.String
	 */
	protected String getVirtualModuleCode(String nodeKey) {
		String moduleCode = getModuleName().substring(0,
				getModuleName().length() - 2);
		String ext;
		if (nodeKey == null)
			ext = "91";
		else if (nodeKey.equals("work"))
			ext = "91";
		else if (nodeKey.equals("dismiss"))
			ext = "93";
		else if (nodeKey.equals("retire"))
			ext = "95";
		else if (nodeKey.equals("leave"))
			ext = "97";
		else if (nodeKey.equals("other"))
			ext = "99";
		else
			ext = "91";
		return moduleCode + ext;
	}

	/**
	 * 返回 PsnCard 特性值。
	 * 
	 * @return nc.ui.hi.hi_301.HIBillCardPanel
	 */
	protected HIBillCardPanel getWorkCard() {
		if (ivjWorkCard == null) {
			try {
				ivjWorkCard = new nc.ui.hi.hi_301.HIBillCardPanel(
						allRelatedPara);
				ivjWorkCard.setName("WorkCard");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjWorkCard;
	}

	/**
	 * 每当部件抛出异常时被调用
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
		Logger.error("--------- "
				+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
						"UPP600700-000361")/* @res "未捕捉到的异常" */+ " ---------");
		exception.printStackTrace(System.out);
	}

	/**
	 * 表头页签选中时响应的事件处理。 创建日期：(2004-5-13 16:26:18)
	 * 
	 * @param event
	 *            javax.swing.event.ChangeEvent
	 */
	protected void headTabbedPane_stateChanged(ChangeEvent event) {
		try {
			// 没有选中任何人则返回
			if (person == null) {
				return;
			}
			// 当前人员的主键
			String[] keys = getTempPsnPk();
			String tablecode = getCard().getHeadTabbedPane()
					.getSelectedTableCode();
			loadPersonMain(keys[0], keys[1], tablecode);
			if (getEditType() == EDIT_MAIN) {
				getCard().transferFocusTo(0);
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 由于单据模版不支持自定义档案，需要手工处理 创建日期：(2004-5-15 10:05:14)
	 * 
	 * @param items
	 *            nc.ui.pub.bill.BillItem[]
	 */
	private void initBillItems(BillItem[] items) throws Exception {
		// 遍历items
		for (int i = 0; i < items.length; i++) {
			final BillItem item = items[i];
			if (item.getDataType() == BillItem.UFREF) {
				// 处理参照中的自定义档案
				final UIRefPane ref = (UIRefPane) item.getComponent();
				ref.setReturnCode(false);
				ref.setButtonFireEvent(true);
				String refNodeName = ref.getRefNodeName();
				if (item.getPos() == IBillItem.BODY) {
					if (item.getTableCode().equalsIgnoreCase("hi_psndoc_part")
							&& "pk_psncl".equals(item.getKey())) {
						String wherepart = ref.getRefModel().getWherePart();
						if (wherepart == null || wherepart.trim().length() == 0) {
							wherepart = " ( psnclscope = 0 or psnclscope = 5 ) ";
						} else {
							String whereScope = " (psnclscope = 0 or psnclscope = 5 ) and (";
							if (wherepart.indexOf(whereScope) < 0) {
								wherepart = whereScope + wherepart + ")";
							}
						}
						ref.getRefModel().setWherePart(wherepart);
						// modified by zhangdd, 去掉人员类别参照的权限
						ref.getRefModel().setUseDataPower(false);
					} else if (item.getTableCode().equalsIgnoreCase(
							"hi_psndoc_deptchg")) {
						if ("pk_post".equals(item.getKey()))
							ref.setEditable(false);
						// 
						else if ("pk_psncl".equals(item.getKey())) {
							String wherepart = ref.getRefModel().getWherePart();
							if (wherepart == null
									|| wherepart.trim().length() == 0) {
								wherepart = "( psnclscope = 0 or  psnclscope = 5 ) ";
							} else {
								String whereScope = " (psnclscope = 0 or  psnclscope = 5 ) and (";
								if (wherepart.indexOf(whereScope) < 0) {
									wherepart = whereScope + wherepart + ")";
								}
							}
							ref.getRefModel().setWherePart(wherepart);
						}
					}
					// wangkf add 支持教育培训中培训类别的多选
					if ("tra_type".equals(item.getKey())
							|| "tra_mode".equals(item.getKey())) {
						ref.setMultiSelectedEnabled(true);
					}
					ref.addValueChangedListener(new ValueChangedListener() {
						public void valueChanged(ValueChangedEvent e) {
							if (!isBatchAddSet()) {
								String pk = ref.getRefPK();
								String pkname = ref.getRefName();
								String itemKey = item.getKey();
								// wangkf add 支持教育培训中培训类别和培训方式的多选
								if ("tra_type".equals(itemKey)
										|| "tra_mode".equals(itemKey)) {
									String[] traTypePks = ref.getRefPKs();
									if (traTypePks != null) {
										for (int jj = 0; jj < traTypePks.length; jj++) {
											if (jj == 0) {
												pk = traTypePks[jj];
											} else {
												pk += ",";
												pk += traTypePks[jj];
											}
										}
									}
								}
								CircularlyAccessibleValueObject record = person
										.getTableVO(item.getTableCode())[getEditLineNo()];
								record.setAttributeValue(Util.PK_PREFIX
										+ itemKey, pk);
								record.setAttributeValue(itemKey, pkname);
							}
						}
					});
				}
				if (item.getPos() == IBillItem.HEAD) {
					if (item.getTableCode().equalsIgnoreCase("bd_psndoc")
							&& "pk_om_job".equals(item.getKey())) {
						if (ref != null && ref.getRefModel() != null) {
							if (ref.getRefModel().getWherePart() != null
									&& ref.getRefModel().getWherePart().trim()
											.length() > 0) {
								String wherepart = "  (om_job.isabort ='Y' or om_job.isabort= 'N') and om_job.pk_corp= '"
										+ Global.getCorpPK() + "'";
								ref.getRefModel().setWherePart(wherepart);
							} else {
								ref.getRefModel().setWherePart(
										"om_job.pk_corp= '"
												+ Global.getCorpPK() + "'");
							}
						}
					}
				}
				if ("部门档案".equals(refNodeName) || "部门档案HR".equals(refNodeName)) {

					if (ref != null) {
						setWhereToModel(ref.getRefModel(),
								"((bd_deptdoc.canceled = 'N'  or bd_deptdoc.canceled is null))");

						// 部门受部门档案权限控制
						if (item.getTableCode().equalsIgnoreCase(
								"hi_psndoc_deptchg")
								&& "pk_deptdoc".equals(item.getKey())) {
							ref.getRefModel().setUseDataPower(false);
						}
						if (item.getTableCode().equalsIgnoreCase(
								"hi_psndoc_part")
								&& "pk_deptdoc".equals(item.getKey())) {
							ref.getRefModel().setUseDataPower(false);
						}
					}
				}
				if ("人员档案HR".equals(refNodeName) || "人员档案".equals(refNodeName)) {
					if (ref != null) {
						ref.getRefModel().setUseDataPower(false);
						if (getModuleName().equals("600704")) {
							setWhereToModel(
									ref.getRefModel(),
									"((canceled = 'N' and hrcanceled = 'N') or ( canceled is null and hrcanceled is null))");
						} else {
							setWhereToModel(ref.getRefModel(),
									"((bd_deptdoc.canceled = 'N'  or bd_deptdoc.canceled is null))");
						}
					}
				}
				// 如果当前是自定义档案参照
				if (Util.isDefdocPk(refNodeName)) {
					Util.setDefdocRefModel(ref, refNodeName);
					// 参照末级档案
					ref.setNotLeafSelectedEnabled(item
							.isM_bNotLeafSelectedEnabled());
				}
			} else if (item.getDataType() == BillItem.COMBO) {
				if (item.getPos() == IBillItem.BODY) {
					final UIComboBox box = (UIComboBox) item.getComponent();
					box.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent event) {
							if (getEditLineNo() < 0) {
								return;
							}
							if (!isBatchAddSet) {
								CircularlyAccessibleValueObject record = person
										.getTableVO(item.getTableCode())[getEditLineNo()];
								String itemKey = item.getKey();
								Object obj = box.getSelectedItem();
								if (obj instanceof ComboBillItem.ComboItem) {
									ComboBillItem.ComboItem comboItem = (ComboBillItem.ComboItem) box
											.getSelectedItem();
									Object value = comboItem.getValue();
									record.setAttributeValue(Util.PK_PREFIX
											+ itemKey, value);
									record.setAttributeValue(itemKey, comboItem
											.getName());
								} else if (obj instanceof java.lang.String) {
									record.setAttributeValue(itemKey, obj
											.toString());
								}
							}
						}
					});
				}
			} else if (item.getDataType() == BillItem.BOOLEAN) {
				if (item.getPos() == IBillItem.BODY) {
					final UICheckBox check = (UICheckBox) item.getComponent();
					if (item.getTableCode().equalsIgnoreCase("hi_psndoc_edu")
							&& "lasteducation".equals(item.getKey())) {
						check.addActionListener(new CheckChangeListener());
					}
				}
			}
		}
	}

	/**
	 * v50 新增，学历信息最高学历监听
	 * 
	 * @author zhyan
	 * 
	 */
	public class CheckChangeListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (!isBatchAddSet) {
				String tablecode = getCurrentSetCode();
				CircularlyAccessibleValueObject[] oldrecords = person
						.getTableVO(tablecode);
				CircularlyAccessibleValueObject record = getCurrnentBillModel()
						.getBodyValueRowVO(getEditLineNo(),
								"nc.vo.hi.hi_301.GeneralVO");
				if (((UFBoolean) record.getAttributeValue("lasteducation"))
						.booleanValue()) {
					if (oldrecords != null && oldrecords.length > 0) {
						for (int i = 0; i < oldrecords.length; i++) {
							if (i != getEditLineNo()) {
								getCurrnentBillModel().setValueAt("N", i,
										"lasteducation");
							}
						}
					}
				} else {
					if (oldrecords != null && oldrecords.length > 0) {
						for (int i = 0; i < oldrecords.length; i++) {
							if (i != getEditLineNo()) {
								getCurrnentBillModel()
										.setValueAt(
												oldrecords[i]
														.getAttributeValue("lasteducation"),
												i, "lasteducation");
							}
						}
					}
				}
			}
		}
	}

	private boolean psncodeCanEdit = false;

	/**
	 * 初始化单据模板。 创建日期：(2004-5-9 17:05:25)
	 */
	protected void initBillTemp() throws Exception {
		// 加载单据模版
		loadTemplet();

		// 记录单据模板中bd_psndoc.psncode的可编辑属性(为了“修改”时设置其是否可以编辑)。dusx add 2009.4.29
		psncodeCanEdit = getBillItem("bd_psndoc.psncode").isEdit();
		// 设置模板的触发器
		initBillTrigger();
		// 设置模版的某些属性
		initTempAttr();
		// 初始化为不可编辑
		getCard().setEnabled(false);
		// getCard().setShowMenuBar(false);
		getCard().setVisible(false);// V35 add
		// 为单据主子集添加适当的事件监听器
		initTabbedPaneListener();
		// 为子集的表添加编辑事件处理器和行选择事件处理器
		initSubTableListener();
	}

	/**
	 * 初始化模板的触发器。 创建日期：(2004-5-17 20:06:14)
	 */
	private void initBillTrigger() {
		// 遍历触发器表
		for (int i = 0; i < triggerTable.triggers.length; i++) {
			// 触发源
			String srcField = (String) triggerTable.triggers[i][0];
			// 触发对象
			String destField = (String) triggerTable.triggers[i][1];
			// 触发器
			ITriggerListener listener = (ITriggerListener) triggerTable.triggers[i][2];
			// 获取触发源描述项
			BillItem srcItem = getBillItem(srcField);
			// 获取触发对象描述项
			BillItem destItem = getBillItem(destField);
			// 检查触发源和触发对象是否有效
			if (srcItem == null || destItem == null) {
				continue;
			}
			// 使用触发器将触发源和触发对象关联起来
			addTriggerListener(srcItem, destItem, listener);
		}
	}

	/**
	 * 初始化按钮及初始状态。 创建日期：(2004-5-9 16:27:02)
	 */
	protected void initButton() throws Exception {
		switchButtonGroup(LIST_INIT_GROUP);// V35
		setButtonsState(LIST_INIT);// V35
	}

	/**
	 * 初始化连接
	 */
	public void initConnections() throws java.lang.Exception {
		getCmbPsncl().addItemListener(ivjEventHandler);
		getCmbJobType().addItemListener(ivjEventHandler);
		getDeptTree().addTreeSelectionListener(ivjEventHandler);
		getDeptTree().addMouseListener(ivjEventHandler);
		getPsnList().addMouseListener(new BillTableMouseListener() {
			public void mouse_doubleclick(BillMouseEnent arg0) {
				if (EDIT_SORT != getEditType()) {
					PsnListMouseClicked(arg0);
				}
			}
		});

		getincludeCancleDept().addActionListener(new ActionListener() {// 包含已撤销部门事件响应v5.3
					// add
					public void actionPerformed(ActionEvent e) {
						if (getincludeCancleDept().isSelected()) {
							deptTreeChanged(true);
						} else {
							deptTreeChanged(false);
						}

					}
				});
		getincludeChildDeptPsn().addActionListener(new ActionListener() {
			// 包含下级人员事件响应v5.5 add
			public void actionPerformed(ActionEvent e) {
				connEtoC2(null);
			}
		});
		getIncludeHisPersonGroup().addActionListener(new ActionListener() {// 包含历史人员组v5.3
					// add
					// 封存人员组
					public void actionPerformed(ActionEvent e) {

						if (getIncludeHisPersonGroup().isSelected()) {
							psnGroupTreeChanged(true);
						} else {
							psnGroupTreeChanged(false);
						}

					}
				});
		getIncludeHisPerson().addActionListener(new ActionListener() {// 包含历史关键人员v5.3
					// add
					// 存在结束日期的关键人员
					public void actionPerformed(ActionEvent e) {

						if (getIncludeHisPerson().isSelected()) {
							onRefresh();
						} else {
							onRefresh();
						}

					}
				});

		getIsSelSet().addActionListener(new ActionListener() {// 是否显示子集事件响应
					public void actionPerformed(ActionEvent e) {
						if (getIsSelSet().isSelected()) {
							String tableCode = getBodyTableCode();
							person = loadPsnChildInfo(listSelectRow, tableCode);
							getUISplitPaneVertical().setDividerLocation(
									SPLIT_STANDARD);
							getUISplitPaneVertical().setEnabled(true);
							splitLocation = SPLIT_STANDARD;
						} else {
							getUISplitPaneVertical().setDividerLocation(
									SPLIT_MAX);
							getUISplitPaneVertical().setEnabled(false);
							splitLocation = SPLIT_MAX;
						}
					}
				});
		getIsShowSetHistory().addActionListener(new ActionListener() {// 是否显示历史事件响应
					public void actionPerformed(ActionEvent e) {
						int temprow = getPsnList().getTable().getSelectedRow();
						if (temprow == -1) {
							return;
						} else {
							String tableCode = getBodyTableCode();
							loadPsnChildInfo(temprow, tableCode);
						}
					}
				});
	}

	/**
	 * 
	 * 根据ID找树节点
	 * 
	 * @param node
	 * 
	 * @param id
	 * 
	 * @return
	 * 
	 */

	private DefaultMutableTreeNode findTreeNodeByID(
			DefaultMutableTreeNode node, String pk) {

		if (node == null || !StringUtils.hasText(pk)) {

			return null;

		}
		if (((CtrlDeptVO) node.getUserObject()).getNodeType() == CtrlDeptVO.CORP) {

			if (pk.equals(((CtrlDeptVO) node.getUserObject()).getPk_corp())) {
				return node;
			}
		}
		if (((CtrlDeptVO) node.getUserObject()).getNodeType() == CtrlDeptVO.DEPT) {

			if (pk.equals(((CtrlDeptVO) node.getUserObject()).getPk_dept())) {
				return node;
			}
		}

		int c = node.getChildCount();
		DefaultMutableTreeNode tmpNode = null;

		for (int i = 0; i < c; i++) {
			tmpNode = findTreeNodeByID((DefaultMutableTreeNode) node
					.getChildAt(i), pk);

			if (tmpNode != null) {

				return tmpNode;

			}

		}

		return null;

	}

	/**
	 * 
	 * 设置选中的报表
	 * 
	 * @param rptID
	 * 
	 */

	public void setSelectPath(String pk) {

		if (!StringUtils.hasText(pk)) {

			return;

		}// end if

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getDeptTree()
				.getModel().getRoot();

		int c = root.getChildCount();

		DefaultMutableTreeNode selNode = null;

		for (int i = 0; i < c; i++) {

			selNode = findTreeNodeByID((DefaultMutableTreeNode) root
					.getChildAt(i), pk);

			if (selNode != null) {

				// setSelectedNode(selNode);

				TreePath path = new TreePath(selNode.getPath());

				getDeptTree().setSelectionPath(path);

				getDeptTree().expandPath(path);

				break;

			}// end if

		}// end for

	}

	/**
	 * 是否显示撤销部门
	 * 
	 * @param select
	 */
	protected void psnGroupTreeChanged(boolean select) {

	}

	/**
	 * 是否显示撤销部门 根据是否包含撤销部门刷新树
	 * 
	 * @param select
	 */
	protected void deptTreeChanged(boolean select) {
		try {
			DefaultMutableTreeNode node = getSelectedNode();
			if (node == null) {
				return;
			}
			// 采集状态或者维护状态
			setCtrlDeptRoot(HIDelegator.getIHRhiQBS().queryRelatCorps(
					Global.getUserID(), Global.getCorpPK(), true));

			// 调用getChildTree组织树
			DefaultMutableTreeNode root = getChildTree(ctrlDeptRoot);
			// 封装成树的model返回
			treemodel = new DefaultTreeModel(root);
			DefaultTreeModel treeModel = getDeptTreeModel();
			if (treeModel != null) {
				getDeptTree().setModel(treeModel);
				getDeptTree().setCellRenderer(new DeptTreeCellRender());
			}

			CtrlDeptVO corpvo = (CtrlDeptVO) node.getUserObject();
			// boolean expand = false;
			if (corpvo.nodeType == CtrlDeptVO.CORP
					&& corpvo.getPk_corp().equalsIgnoreCase(Global.getCorpPK())
					&& corpvo.isLoadDept()) {
				corpvo.removeAllDeptChild();
				String strDeptPower = getDeptPowerByCorp(corpvo.getPk_corp());
				boolean isUsedPower = strDeptPower.length() > 3;

				corpvo.setModuleCode(getModuleName());
				// boolean includeHrCanceld =
				// getincludeCancleDept().isSelected();//v53
				corpvo = HIDelegator.getPsnInf().queryCorpCtrlDepts(
						Global.getUserID(), corpvo, isUsedPower, select);
				node.setUserObject(corpvo);
				corpvo.setLoadDept(true);
				addDeptNodeToTree(node, corpvo);

				getDeptTreeModel().reload(node);
				getDeptTree().setCellRenderer(new DeptTreeCellRender());
				// getDeptTree().expandPath(path);
			}
			queryResult = new GeneralVO[0];
			psnList = queryResult;
			getPsnList().setBodyData(psnList);
			recordcount = 0;

			String recordCountDecrip = nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-000192")/* @res "共有" */;
			String tiao = nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000240")/* @res "条" */;

			Integer maxline = PubDelegator.getIParValue().getParaInt("0001",
					"HI_MAXLINE");

			String max = ","
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000239")/* @res "显示前" */
					+ (maxline == null ? 1000 : maxline.intValue())
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000240")/* @res "条" */;
			if (recordcount <= (maxline == null ? 1000 : maxline.intValue())) {
				max = "";
			}
			getUILabelRecords().setText(
					recordCountDecrip + recordcount + tiao + max + "   ");
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 初始化数据， 包括人员部门树和卡片模板。 创建日期：(2004-5-9 16:27:54)
	 */
	private void initData() throws Exception {
		initHmField();// V35 add
		// 初始化人员归属范围
		initPsnScope();
		// 初始化人员部门树
		initDeptTree();
		// V35
		initListHeadItems();
		// 初始化人员列表
		initPsnList();
		// // 计算装载时间
		// long now = System.currentTimeMillis();
		// 初始化卡片模板
		initBillTemp();
		if ("600704".equalsIgnoreCase(getModuleName())) {
			// 初始化人员信息跟踪映射表
			initTraceTableMap();
		}
	}

	/**
	 * 初始化人员部门树。 创建日期：(2004-5-9 17:08:41)
	 */
	protected void initDeptTree() throws Exception {
		// 获取当前用户的ID，当前登录公司，是否启用部门权限,起用部门权限时的权限条件
		String userID = Global.getUserID();
		String pk_corp = Global.getCorpPK();
		// 连接数优化

		// if (isUsedDeptPower) {//
		// powerSql = GlobalTool.getPowerSql("bd_deptdoc", userID, pk_corp);
		// if (powerSql == null) {
		// powerSql = "0=0";
		// }
		// deptPowerlist.put(pk_corp, powerSql);
		// } else {
		// deptPowerlist.put(pk_corp, "0=0");
		// }

		powerSql = GlobalTool.getPowerSql("bd_deptdoc", userID, pk_corp);
		if (powerSql == null) {
			powerSql = "0=0";
		}
		deptPowerlist.put(pk_corp, powerSql);
		// 采集状态或者维护状态
		setCtrlDeptRoot(HIDelegator.getIHRhiQBS().queryRelatCorps(
				Global.getUserID(), Global.getCorpPK(), true));
		// 非员工自助,设置树model
		DefaultTreeModel treeModel = getDeptTreeModel();
		if (treeModel != null) {
			getDeptTree().setModel(treeModel);
			getDeptTree().setCellRenderer(new DeptTreeCellRender());
		}
	}

	/**
	 * 初始化类。
	 */
	// protected boolean isCheckLicense_wordcard = false;
	protected void initialize() {

		// 插件license校验
		// isCheckLicense_wordcard =
		// PluginsUtil.checkLicense("a87e8795-f6d2-4ef3-ae93-a858c2da3941");

		isHRCPInstalled = isHRCPInstalled();
		if (!isHRCPInstalled) {

			// 员工信息维护
			// 武燕军 2011-3-18 添加导入导出按钮

			grpSortMaintain = new ButtonObject[] { bnEdit, bnDel, bnBatch,
					bnBatchAddSet, bnCard, bnBook, bnEmployeeReference,
					bnDisplay, bnAssist, bnUpload, bnQuery, bnFresh, bnPrint,bnImport };

			// 人员引用的列表组

			// 维护节点修改子按钮
			/*
			 * 卡片组 【编辑】【删除】[子集编辑] [保存] [取消]【上一条】【下一条】【至头条】【至末条】[列表]【刷新】
			 * [增加行][插入行][编辑行] [删除行]
			 */
			grpCardDisplayMaintain = new ButtonObject[] { bnEdit, bnDel,
					bnChildEdit, bnSave, bnCancel, bnUpload, bnCard, bnToFirst,
					bnUpRecord, bnDownRecord, bnToLast, bnSubsetMove, bnList };
		}

		// 初始化参数
		initPara();
		try {

			bnSave.setEnabled(false);
			if (!checkEntrance()) {
				return;
			}
			setName("PsnInfCollectUI");
			setSize(864, 541);
			add(getNorthPanel(), "North");
			add(getCenterPanel(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		try {
			if (getModuleName().equalsIgnoreCase("600704")) {
				intodocdirect = getIndocType();
				if (intodocdirect == 1) {
					ButtonObject[] grpCollectIndoc = { bnAdd, bnEdit, bnDel,
							bnBatch, bnBatchAddSet, bnCard, bnBook, bnIndocApp,
							bnDisplay, bnUpload, bnQuery, bnFresh, bnPrint };
					grpCollect = grpCollectIndoc;
				}
			}
			if (getModuleName().equalsIgnoreCase("600707")
					|| getModuleName().equalsIgnoreCase("600708")) {
				caneditkeypsn = getCaneditKeypsn();
			} // 初始化按钮
			initButtonGroup();
			initButton();
			// 初始化数据
			initData();
			// 初始化界面属性
			initUI();
			// 初始化连接
			initConnections();
			// 获取合同试用期限和合同期限参数 v502 add by zhyan 放到HiBillCardPanel中的filter方法查询
			setTreeSelectButtonState((DefaultMutableTreeNode) (getDeptTree()
					.getModel().getRoot()));

			getUISplitPaneVertical().setEnabled(false);

			if (!isHRCPInstalled) {
				Component[] components = getCard().getBodyTabbedPane()
						.getComponents();

				if (!ArrayUtils.isEmpty(components)) {
					for (Component component : components) {
						if (component instanceof BillScrollPane
								&& "hi_psndoc_capa"
										.equals(((BillScrollPane) component)
												.getTableCode())) {
							getCard().getBodyTabbedPane().remove(component);
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			reportException(e);
			showHintMessage(e.getMessage());
		}
	}

	/**
	 * 判断素质指标模块是否安装<br>
	 * 
	 * @author zhangdd
	 * @return
	 */
	protected boolean isHRCPInstalled() {
		ICreateCorpQueryService service = NCLocator.getInstance().lookup(
				ICreateCorpQueryService.class);
		try {
			Hashtable result = service.queryProductEnabled(Global.getCorpPK(),
					new String[] { "HRCP" });
			return UFBoolean.TRUE.equals(result.get("HRCP"));
		} catch (BusinessException e) {
			return false;
		}
	}

	/**
	 * 获取采集方式参数
	 * 
	 */
	protected int getIndocType() {
		int indoc = 0;
		try {
			// 效率优化start
			// indoc =
			// PubDelegator.getIParValue().getParaInt(Global.getCorpPK(),
			// "HI_INDOC").intValue();
			indoc = Integer.parseInt(getPara("HI_INDOC"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indoc;
	}

	/**
	 * 获取编辑关键人员参数
	 * 
	 */
	protected boolean getCaneditKeypsn() {
		boolean canedit = false;
		try {
			// 效率优化
			// canedit =
			// PubDelegator.getIParValue().getParaBoolean(Global.getCorpPK(),
			// "HI_KEYPERSON").booleanValue();
			canedit = UFBoolean.valueOf((getPara("HI_KEYPERSON")))
					.booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return canedit;
	}

	boolean isListDefault = true;

	/**
	 * 
	 * 
	 */
	protected void initListHeadItems() {
		try {
			GeneralVO[] itemVOs = HIDelegator.getPsnInf().queryListItem(
					Global.getCorpPK(), getModuleName(), "seted");
			Pair[] pairs = convertToPairs(itemVOs);
			if (pairs == null || pairs.length == 0) {
				setListItems(getListItemsDefault());
				setListDefault(true);
			} else {
				setListItems(pairs);
				setListDefault(false);
			}
		} catch (Exception e) {
			reportException(e);
			showHintMessage(e.getMessage());
		}
	}

	/**
	 * 初始化人员列表。 创建日期：(2004-5-11 9:28:35)
	 */
	protected void initPsnList() {
		// 设置人员列表的tableModel
		getPsnList().setTableModel(getPsnListModel());
		getPsnList().getTable().getSelectionModel().addListSelectionListener(
				this);
		// 设置选择状态为单行选择
		getPsnList().getTable().setSelectionMode(
				ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// 设置显示行号
		getPsnList().setRowNOShow(true);
		getPsnList().removeTableSortListener();
		getPsnList().getTable().getModel().addTableModelListener(this);

		getPsnList().setHeadMultiSelected(true);
	}

	/**
	 * 查询人员列表的人员数据。 创建日期：(2004-5-19 11:55:58)
	 */
	protected void initPsnListData() throws Exception {
		performQuery();
	}

	/**
	 * 初始化人员归属范围选择列表的初始数据。 创建日期：(2004-5-11 10:07:14)
	 */
	protected void initPsnScope() {
		getLbScope().setVisible(false);
		getCmbPsncl().setVisible(false);
		getUILabelType().setVisible(false);
		getCmbJobType().setVisible(false);
		getIsShowSetHistory().setVisible(false);// v50 add
	}

	/**
	 * 为子集的表添加编辑事件处理器和行选择事件处理器 创建日期：(2004-5-26 15:10:22)
	 */
	private void initSubTableListener() {
		// 为子集的表添加编辑事件处理器和行选择事件处理器
		String[] tableCodes = getCard().getBillData().getBodyTableCodes();
		for (int i = 0; i < tableCodes.length; i++) {
			// 添加编辑处理器
			BillScrollPane bsp = getCard().getBodyPanel(tableCodes[i]);
			// add by zhyan 2006-12-23 去掉单据模板表体的双击自动排序
			bsp.removeTableSortListener();
			UITable table = bsp.getTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// 添加行选择事件
			table.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent event) {
							subTable_valueChanged(event);
						}
					});
		}
	}

	/**
	 * 为单据主子集添加适当的事件监听器 创建日期：(2004-5-26 15:06:31)
	 */
	private void initTabbedPaneListener() {
		// 表头
		getCard().getHeadTabbedPane().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				headTabbedPane_stateChanged(event);
			}
		});
		// 表体
		getCard().getBodyTabbedPane().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				bodyTabbedPane_stateChanged(event);
			}
		});
		//
		getCard().addEditListener(this);
	}

	/**
	 * 设置模版的某些属性 创建日期：(2004-5-15 10:02:02)
	 */
	private void initTempAttr() throws Exception {
		// 获取表体模板数据
		BillData billData = getCard().getBillData();
		// 设置表头
		BillItem[] items = billData.getHeadItems();
		initBillItems(items);
		// 设置表体
		String[] tableCodes = billData.getBodyTableCodes();
		for (int i = 0; i < tableCodes.length; i++) {
			items = billData.getBodyItemsForTable(tableCodes[i]);
			if (items != null)
				initBillItems(items);
		}
		// 记录转正日期的初始属性
		boolean regularedit = getBillItem("bd_psndoc.regulardata").isEdit();
		if (htbRegularEdit.get("bd_psndoc.regulardata") == null) {
			htbRegularEdit.put("bd_psndoc.regulardata",
					new Boolean(regularedit));
		}
		// test

	}

	/**
	 * 初始化界面，设置某些属性，如字体。 创建日期：(2004-5-11 10:21:07)
	 */
	private void initUI() {

		Util.initComponent(this);
		// 初始化单据模板表体追踪信息集页签的前后背景色彩
		if (Util.TR_BGCOLOR == null && Util.TR_FGCOLOR == null) {
			return;
		}

		 if (getModuleName().equalsIgnoreCase("600707")) {
		 initPCIAuth(0);
		 }
		 if (getModuleName().equalsIgnoreCase("600708")) {
		 initPCIAuth(1);
		 }

		BillTabbedPane btp = getCard().getBodyTabbedPane();
		int count = btp.getTabCount();
		for (int i = 0; i < count; i++) {
			BillScrollPane bsp = (BillScrollPane) btp.getComponentAt(i);
			String tableCode = getCard().getBodyTableCode(bsp);
			if (getCard().getTraceTables() != null
					&& getCard().getTraceTables().get(tableCode) != null) {
				if (Util.TR_BGCOLOR != null)
					btp.setBackgroundAt(i, Util.TR_BGCOLOR);
				if (Util.TR_FGCOLOR != null)
					btp.setForegroundAt(i, Util.TR_FGCOLOR);
			}
		}
	}

	// 刘鹏飞 新增 2010-4-2处理没有权限的人员子集
	public void initPCIAuth(int flag) {

		// 获取没有权限的标签页
		String userId = this.getClientEnvironment().getUser().getPrimaryKey();
		String pkcorp = this.getCorpPrimaryKey();

		// 查询
		IUserChildInfoAuth irewrite = (IUserChildInfoAuth) NCLocator
				.getInstance().lookup(IUserChildInfoAuth.class);
		nc.vo.hr.bd.setdict.SetdictVO[] bodyvos = irewrite.queryHavePCIAuth(
				userId, pkcorp, flag);

		// 删除没有权限的tab页
		BillTabbedPane btp = getCard().getBodyTabbedPane();
		int count = btp.getTabCount();
		if (bodyvos != null && bodyvos.length > 0) {
			for (int i = count - 1; i >= 0; i--) {
				BillScrollPane bsp = (BillScrollPane) btp.getComponentAt(i);
				String tableCode = getCard().getBodyTableCode(bsp);
				int temp = 0;

				for (int j = 0; j < bodyvos.length; j++) {
					String tempcode = bodyvos[j].getSetcode();// bodyvos[j].getAttributeValue("tabcode").toString();
					if (tempcode.equals(tableCode)) {
						temp = 1;
					}
				}
				if (temp == 0) {
					btp.remove(i);
					// count--;
				}

			}
		} else {
			for (int i = count - 1; i >= 0; i--) {
				btp.remove(i);
			}

		}

	}

	/**
	 * 当前是否正在添加人员。
	 * 
	 * @return boolean
	 */
	public boolean isAdding() {
		return adding;
	}

	/**
	 * 当前选中的子表是否是追踪信息集。 采集节点一同对待，维护节点重写，需要区别处理
	 * 
	 * @return boolean
	 */
	protected boolean isBodyTraceTable() {
		// 追踪信息集的显示名称以<开始
		return getCard().getTraceTables().get(getBodyTableCode()) != null;
	}

	/**
	 * 当前选中的子表是否是追踪信息集。 采集节点"调整子集顺序"用,维护节点区别对待
	 * 
	 * @return boolean
	 */
	protected boolean Canmoved() {
		return traceTableMap.get(getBodyTableCode()) != null;
	}

	/**
	 * 物理表tablecode的field字段是否可编辑。
	 * 
	 * @param tableCode
	 * @param field
	 * @return
	 */
	private boolean isEditable(String tableCode, String field) {
		// 获取模板映射的高速缓存
		Hashtable table = (Hashtable) getBillBodyMap().get(tableCode);
		// 没有该表
		if (table == null) {
			return false;
		}
		// 从该表获取获取该字段的描述
		BillTempletBodyVO fieldVO = (BillTempletBodyVO) table.get(field);
		if (fieldVO == null) {
			// 没有该字段
			return false;
		}
		// 是否可编辑标识
		Boolean edit = (Boolean) fieldVO.getEditflag();
		// 为空认为不可编辑
		if (edit == null) {
			return false;
		}
		// 返回是否可编辑
		return edit.booleanValue();
	}

	/**
	 * 当前是否正在编辑子表的某一行，主要区分编辑行和添加、插入行
	 * 
	 * @return boolean
	 */
	private boolean isEditLine() {
		return editLine;
	}

	/**
	 * 判断是否为空
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isNULL(Object obj) {
		if (obj == null || obj.equals(""))
			return true;
		return false;
	}

	/**
	 * 返回该数据项是否为数字项 数字项可参与运算；非数字项只作为字符串常量 如“数量”为数字项、“存货编码”为非数字项
	 * IDataSource接口方法，必须实现
	 */
	public boolean isNumber(java.lang.String itemExpress) {
		return false;
	}

	/**
	 * 判断是否与辅助情况关联
	 * 
	 * @param record
	 * @param tableCode
	 * @return boolean
	 */
	protected boolean isUpdateAccRel(String[] fields, String tableCode) {
		// 获得关联映射表
		Hashtable map = getRelationMap();
		// 是否修改了
		boolean updated = false;
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				if (tableCode.equalsIgnoreCase("hi_psndoc_deptchg")
						|| tableCode.equalsIgnoreCase("hi_psndoc_dimission")) {// 采集节点不同步任职记录和离职记录关联项
					continue;
				}
				// 映射表存取主键为"table.field"
				String key = tableCode + "." + fields[i];
				String acc_fldcode = (String) map.get(key);
				if (acc_fldcode != null) {
					updated = true;
				}
			}
		}
		return updated;
	}

	/**
	 * 加载人员pk_psndoc的主表的信息
	 * 
	 * @param pk_psnbasdoc
	 * @param pk_psndoc
	 * @param tableCode
	 * @throws Exception
	 */
	protected void loadPersonMain(String pk_psnbasdoc, String pk_psndoc,
			String tableCode) throws Exception {
		if (isEmployeeRef()) {// 如果是引用人员特殊处理
			CircularlyAccessibleValueObject record = null;
			if (tableCode.equals("bd_psndoc")) {
				GeneralVO temprecord = person.getPsndocVO();
				if (temprecord == null) {
					if (!isEditing() && pk_psndoc != null) {
						GeneralVO[] personVOs = HIDelegator.getPsnInf()
								.queryPersonInfo(pk_psndoc, "hi_psndoc_ref");// 这是基本信息表的信息
						temprecord = (GeneralVO) personVOs[0].clone();
					}
				}

				// V35 add
				if (!isEditing() && temprecord != null) {
					if (!"E0020603".equals(getModuleName())
							&& !"E0020303".equals(getModuleName())
							&& !"E0041203".equals(getModuleName())) {// 当查看其他公司人员数据时，公司自定义项置为空--begin---
						String psncorp = (String) temprecord
								.getAttributeValue("pk_corp");
						GeneralVO[] tempVOs = new GeneralVO[] { temprecord };
						if (!Global.getCorpPK().equals(psncorp)) {
							tempVOs = setDefItem(tempVOs, "");
						}
						temprecord = tempVOs[0];
					}
				}
				person.setPsndocVO(temprecord);

				if (person != null
						&& person.getPsndocVO() != null
						&& person.getPsndocVO().getAttributeValue("dutyname") != null) {
					BillItem dutyItem = getBillItem("bd_psndoc.dutyname");
					UIRefPane dutyref = (UIRefPane) dutyItem.getComponent();
					nc.ui.hi.ref.DutyRef duty = null;// DutySimpleRef--〉DutyRef
					duty = new nc.ui.hi.ref.DutyRef();
					if (CommonValue.GROUPCODE != Global.getCorpPK()) {
						duty.setWherePart("(duty.pk_corp = '"
								+ nc.vo.hi.pub.CommonValue.GROUPCODE
								+ "' or duty.pk_corp = '"
								+ person.getPsndocVO().getAttributeValue(
										"pk_corp").toString()
								+ "')  and sery.pk_defdoc=duty.series");
					}
					dutyref.setRefType(1);
					dutyref.setRefInputType(1);
					dutyref.setRefModel(duty);
				}

				if (!isEditing()) {// wangkf add 编辑状态时，不更新界面数据 !isAdding()
					// !m_bnSave.isEnabled()
					record = person.getPsndocVO();
					// 设置值
					if (!isLoadedBasicData) {// wangkf add
						getCard().setBillValueVO(person);
						isLoadedBasicData = true;
					}
					if(getBillItem("bd_psndoc.clerkcode") != null) {
						getBillItem("bd_psndoc.clerkcode").setEdit(false);
					}
					
				} else {
					record = null;
				}
			} else if (tableCode.equals("bd_psnbasdoc")) {// V35待改代码--－
				GeneralVO tempaccpsndoc = person.getAccpsndocVO();
				if (tempaccpsndoc == null) {
					GeneralVO[] personVOs = HIDelegator.getPsnInf()
							.queryMainPersonInfo(pk_psnbasdoc,
									Global.getCorpPK(), "bd_psnbasdoc",
									GlobalTool.getFuncParserWithoutWa());
					tempaccpsndoc = (GeneralVO) personVOs[0].clone();
				}
				person.setAccpsndocVO(tempaccpsndoc);
				record = person.getAccpsndocVO();
				// 设置值
				if (!isLoadedAccData) {
					getCard().setBillValueVO(person);
					isLoadedAccData = true;
				}
				if(getBillItem("bd_psndoc.clerkcode") != null) {
					getBillItem("bd_psndoc.clerkcode").setEdit(false);
				}
				
			}
			// 触发参照事件
			if (record != null) {
				sequenceFire(tableCode, record);
			}
			if (tableCode.equals("bd_psndoc") && record != null
					&& Util.isDutyDependJobSeries) {
				BillItem item = getBillItem("bd_psndoc.jobseries");
				((UIRefPane) item.getComponent()).setPK((String) record
						.getAttributeValue("jobseries"));
			}
		} else {
			int jobtype = 0;
			if (isMaintain && listSelectRow >= 0) {
				jobtype = ((Integer) psnList[listSelectRow]
						.getAttributeValue("jobtypeflag")).intValue();
			}
			CircularlyAccessibleValueObject record = null;
			if (tableCode.equals("bd_psndoc")) {
				GeneralVO temprecord = person.getPsndocVO();
				if (temprecord == null) {
					GeneralVO[] personVOs = HIDelegator.getPsnInf()
							.queryMainPersonInfo(pk_psndoc, Global.getCorpPK(),
									"bd_psndoc",
									GlobalTool.getFuncParserWithoutWa());// 这是基本信息表的信息
					temprecord = (GeneralVO) personVOs[0].clone();
				}
				if (jobtype > 0) {
					String jianzhipk_corp = (String) psnList[listSelectRow]
							.getAttributeValue("man_pk_corp");
					if (jianzhipk_corp != null
							&& !jianzhipk_corp.equals(Global.getCorpPK())) {
						String[] names = temprecord.getAttributeNames();
						if (names != null && names.length > 0) {
							for (int i = 0; i < names.length; i++) {
								temprecord.setAttributeValue(names[i], null);
							}
						}
					}
					temprecord
							.setAttributeValue("psncode",
									psnList[listSelectRow]
											.getAttributeValue("psncode"));
					temprecord
							.setAttributeValue("psnname",
									psnList[listSelectRow]
											.getAttributeValue("psnname"));
					temprecord.setAttributeValue("pk_deptdoc",
							psnList[listSelectRow]
									.getAttributeValue("pk_deptdoc"));
					temprecord.setAttributeValue("pk_psncl",
							psnList[listSelectRow]
									.getAttributeValue("pk_psncl"));
					temprecord.setAttributeValue("pk_om_job",
							psnList[listSelectRow]
									.getAttributeValue("pk_om_job"));
					// v35 add
					temprecord.setAttributeValue("dutyname",
							psnList[listSelectRow]
									.getAttributeValue("pk_om_duty"));// pk_om_duty
					temprecord.setAttributeValue("series",
							psnList[listSelectRow].getAttributeValue("series"));
					temprecord
							.setAttributeValue("jobrank",
									psnList[listSelectRow]
											.getAttributeValue("jobrank"));
					temprecord.setAttributeValue("jobseries",
							psnList[listSelectRow]
									.getAttributeValue("jobseries"));
				}
				// else{
				// if (!"E0020603".equals(getModuleName())
				// && !"E0020303".equals(getModuleName())
				// && !"E0041203".equals(getModuleName())) {
				// temprecord.setAttributeValue("pk_deptdoc",psnList[listSelectRow].getAttributeValue("pk_deptdoc"));
				// temprecord.setAttributeValue("pk_psncl",psnList[listSelectRow].getAttributeValue("pk_psncl"));
				// temprecord.setAttributeValue("pk_om_job",psnList[listSelectRow].getAttributeValue("pk_om_job"));
				// // v35 add
				// temprecord.setAttributeValue("dutyname",psnList[listSelectRow].getAttributeValue("pk_om_duty"));//
				// pk_om_duty
				// temprecord.setAttributeValue("series",psnList[listSelectRow].getAttributeValue("series"));
				// temprecord.setAttributeValue("jobrank",psnList[listSelectRow].getAttributeValue("jobrank"));
				// temprecord.setAttributeValue("jobseries",psnList[listSelectRow].getAttributeValue("jobseries"));
				// }
				// }
				if (!"E0020603".equals(getModuleName())
						&& !"E0020303".equals(getModuleName())
						&& !"E0041203".equals(getModuleName())) {// 当查看其他公司人员数据时，公司自定义项置为空--begin---
					String psncorp = (String) temprecord
							.getAttributeValue("pk_corp");
					GeneralVO[] tempVOs = new GeneralVO[] { temprecord };
					if (!Global.getCorpPK().equals(psncorp)) {
						tempVOs = setDefItem(tempVOs, "");
					}
					temprecord = tempVOs[0];
				}
				person.setPsndocVO(temprecord);

				if (person != null
						&& person.getPsndocVO() != null
						&& person.getPsndocVO().getAttributeValue("dutyname") != null) {
					BillItem dutyItem = getBillItem("bd_psndoc.dutyname");
					UIRefPane dutyref = (UIRefPane) dutyItem.getComponent();
					nc.ui.hi.ref.DutyRef duty = null;// DutySimpleRef--〉DutyRef
					duty = new nc.ui.hi.ref.DutyRef();
					if (CommonValue.GROUPCODE != Global.getCorpPK()) {
						duty.setWherePart("(duty.pk_corp = '"
								+ nc.vo.hi.pub.CommonValue.GROUPCODE
								+ "' or duty.pk_corp = '"
								+ person.getPsndocVO().getAttributeValue(
										"pk_corp").toString()
								+ "')  and sery.pk_defdoc=duty.series");
					}
					dutyref.setRefType(1);
					dutyref.setRefInputType(1);
					dutyref.setRefModel(duty);
				}

				if (!isEditing()) {// wangkf add 编辑状态时，不更新界面数据 !isAdding()
					// !m_bnSave.isEnabled()
					record = person.getPsndocVO();
					// 设置值
					if (!isLoadedBasicData) {// wangkf add 需要仔细考虑修改
						getCard().setBillValueVO(person);
						isLoadedBasicData = true;
					}
					if(getBillItem("bd_psndoc.clerkcode") !=null) {
						getBillItem("bd_psndoc.clerkcode").setEdit(false);
					}
					
					getBillItem("bd_psndoc.regulardata").setEdit(false);
				} else {
					record = null;
				}
			} else if (tableCode.equals("bd_psnbasdoc")) {// V35待改代码--－
				GeneralVO tempaccpsndoc = person.getAccpsndocVO();
				if (tempaccpsndoc == null) {
					GeneralVO[] personVOs = HIDelegator.getPsnInf()
							.queryMainPersonInfo(pk_psnbasdoc,
									Global.getCorpPK(), "bd_psnbasdoc",
									GlobalTool.getFuncParserWithoutWa());
					tempaccpsndoc = personVOs[0] == null ? null
							: (GeneralVO) personVOs[0].clone();
				}
				if (!"E0020603".equals(getModuleName())
						&& !"E0020303".equals(getModuleName())
						&& !"E0041203".equals(getModuleName())) {
					// 当查看其他公司人员数据时，公司自定义项置为空
					GeneralVO bdpsnrecord = person.getPsndocVO();
					String psncorp = null;
					if (bdpsnrecord != null) {
						psncorp = (String) bdpsnrecord
								.getAttributeValue("pk_corp");
					}
					GeneralVO[] tempVOs = new GeneralVO[] { tempaccpsndoc };
					if (!Global.getCorpPK().equals(psncorp)) {
						tempVOs = setDefItem(tempVOs, "");
					}
					tempaccpsndoc = tempVOs[0];
				}
				person.setAccpsndocVO(tempaccpsndoc);
				if (!isEditing()) {// wangkf add 编辑状态时，不更新界面数据
					// !m_bnSave.isEnabled()
					record = person.getAccpsndocVO();
					// 设置值
					if (!isLoadedAccData) {// wangkf add
						getCard().setBillValueVO(person);
						isLoadedAccData = true;
					}
					if(getBillItem("bd_psndoc.clerkcode") != null) {
						getBillItem("bd_psndoc.clerkcode").setEdit(false);
					}
					
				} else {
					record = null;
				}

			}
			// 触发参照事件
			if (record != null) {
				sequenceFire(tableCode, record);
			}
			if (tableCode.equals("bd_psndoc") && record != null
					&& Util.isDutyDependJobSeries) {// bd_accpsndoc
				BillItem item = getBillItem("bd_psndoc.jobseries");
				((UIRefPane) item.getComponent()).setPK((String) record
						.getAttributeValue("jobseries"));
			}
		}
	}

	/**
	 * 装载单据模板。 创建日期：(2004-5-12 15:06:12)
	 */
	protected void loadTemplet() {
		// 采集和员工自助按照正常方法获取单据模版
		loadTempletByModuleCode(getModuleName());
	}

	/**
	 * 加载模板 v50 改变方式
	 * 
	 * @param moduleCode
	 */
	protected void loadTempletByModuleCode(String moduleCode) {
		// 加载模板
		String nodekey = null;
		if (moduleCode.startsWith("60079")) {
			nodekey = moduleCode;
		}
		try {
			getCard().loadTemplet(getModuleName(), null, Global.getUserID(),
					Global.getCorpPK(), nodekey);
			// V35 add
			getCard().setPosMaximized(BillItem.BODY);
			getCard().getBodyTabbedPane().setTabLayoutPolicy(
					ExtTabbedPane.LIST_SCROLL_TAB_LAYOUT);
			getCard().setShowMenuBar(false);
		} catch (Exception e) {
			e.printStackTrace();
			// 没有，则使用缺省模版
			getCard().loadTemplet(getModuleName(), null, Global.getUserID(),
					Global.getCorpPK(), nodekey);
			getCard().setPosMaximized(BillItem.BODY);
			getCard().getBodyTabbedPane().setTabLayoutPolicy(
					JTabbedPane.WRAP_TAB_LAYOUT);
			getCard().setShowMenuBar(false);
		}
	}

	/**
	 * 锁记录
	 * 
	 * @param psnpk
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean lockPsn(String psnpk) throws java.lang.Exception {
		try {
			return PKLock.getInstance().acquireLock(psnpk,
					nc.ui.hr.global.Global.getUserID(), null);
		} catch (Exception e) {
			reportException(e);
			throw e;
		}
	}

	/**
	 * 返聘人员修改人员类别参照,只为在职或者其他人员
	 * 
	 * add by lq  人员范围为3的因为返聘的要能选到离退的人员类别---新华医院
	 * 
	 */
	private void changePsncl(Integer psnclscope) {
		UIRefPane psncl = (UIRefPane) getBillItem("bd_psndoc.pk_psncl")
				.getComponent();
		nc.ui.bd.ref.AbstractRefModel refmodel = psncl.getRefModel();
		if(psnclscope!=null && psnclscope== 3){
			refmodel.setWherePart(" psnclscope in (3) and pk_corp in('"
					+ Global.getCorpPK() + "','0001')");
		}else if(psnclscope!=null && psnclscope== 2){
			refmodel.setWherePart(" psnclscope in ( 0 ) and pk_corp in('"
					+ Global.getCorpPK() + "','0001')");
		}else{
			refmodel.setWherePart(" psnclscope in ( 0,5 ) and pk_corp in('"
					+ Global.getCorpPK() + "','0001')");
		}
		
		psncl.setRefModel(refmodel);
	}

	/**
	 * 添加人员事件处理。
	 * 
	 * @throws java.lang.Exception
	 */
	private void onAdd() throws java.lang.Exception {
		setAdding(true);// 设置为添加状态

		if (!loadedToTempPkCorp.equalsIgnoreCase(Global.getCorpPK())) {// 只改变一次参照
			changeBillTempRef(Global.getCorpPK());
			loadedToTempPkCorp = Global.getCorpPK();
		}

		ResetJobRef();

		// 将其移至设置到addnew之前，为的是此两个字段默认值可以设置上。
		UIRefPane psncl = (UIRefPane) getBillItem("bd_psndoc.pk_psncl")
				.getComponent();
		psncl.setRefModel(new PsnClsRef());
		UIRefPane jobRef = (UIRefPane) getBillItem("bd_psndoc.pk_om_job")
				.getComponent();
		
		if (jobRef != null) {
			jobRef.getRefModel().setWherePart(
					" om_job.pk_corp ='" + Global.getCorpPK() + "'");
		}

		getCard().addNew();
		person = null;
		isLoadedAccData = true;
		isLoadedBasicData = true;
		onEdit();
		// 重新初始化数据
		if (checkAddDlg != null) {
			checkAddDlg.initDate();
		}
		// 弹出输入唯一性条件对话框
		getAddCheckDlg().onReset();
		if (getAddCheckDlg().showModal() == UIDialog.ID_OK) {
			BillItem[] items = checkAddDlg.getAllBillItems();
			GeneralVO vo = checkAddDlg.getDatavo();
			isrehire = checkAddDlg.isRehire();
			isbackrehire = checkAddDlg.isBackrehire();
			//add by lq 查询人员范围 -----------
			Integer psnclscope = qryPsnScope(vo);
			//add by lq ---------------------
			if (isrehire) {
				setEditType(EDIT_RETURN);
				String rehirepsncorp = checkAddDlg.getRehirePersonCreatcorp();
				setRehirePsnBelonpk(rehirepsncorp);
				String pk_psnbasdoc = checkAddDlg.getRehirePersonpk();
				setRehirePsnbaspk(pk_psnbasdoc);
				String pk_psndoc = checkAddDlg.getRehirePersonworkpk();
				setRehirePsnpk(pk_psndoc);
				loadRehirePerson(pk_psnbasdoc, rehirepsncorp, pk_psndoc);
				changePsncl(psnclscope);

				BillItem jobItem = getBillItem("bd_psndoc.pk_om_job");
				if (jobItem != null && jobItem.getValueObject() != null) {
					BillItem jobSeries = getBillItem("bd_psndoc.jobrank");
					if (jobSeries != null) {
						jobSeries.setEdit(false);
					}
				}

			} else if (isbackrehire) {
				setEditType(EDIT_RETURN);
				String rehirepsncorp = checkAddDlg.getRehirePersonCreatcorp();
				setRehirePsnBelonpk(rehirepsncorp);
				String pk_psnbasdoc = checkAddDlg.getRehirePersonpk();
				setRehirePsnbaspk(pk_psnbasdoc);
				String pk_psndoc = checkAddDlg.getRehirePersonworkpk();
				setRehirePsnpk(pk_psndoc);
				loadRehirePerson(pk_psnbasdoc, rehirepsncorp, pk_psndoc);
				changePsncl(psnclscope);

				BillItem jobItem = getBillItem("bd_psndoc.pk_om_job");
				if (jobItem != null && jobItem.getValueObject() != null) {
					BillItem jobSeries = getBillItem("bd_psndoc.jobrank");
					if (jobSeries != null) {
						jobSeries.setEdit(false);
					}
				}

			} else {
				for (int i = 0; i < items.length; i++) {
					BillItem item = getCard().getHeadItem(items[i].getKey());
					item.setValue(vo.getAttributeValue(items[i].getKey()));
					if (htbEdit.get(items[i].getKey()) == null) {
						htbEdit.put(items[i].getKey(), new Boolean(item
								.isEdit()));
					}
					item.setEdit(false);
					if ("id".equalsIgnoreCase(items[i].getKey())) {// 设置出生日期，年龄,保障号
						getCard().getHeadItem("birthdate").setValue(
								vo.getAttributeValue("birthdate"));
						getCard().getHeadItem("sex").setValue(
								vo.getAttributeValue("sex"));
						getCard().getHeadItem("ssnum").setValue(
								vo.getAttributeValue("ssnum"));

					}
				}

			}
			getCard().getBodyTabbedPane().setEnabled(false);
			getCard().execHeadEditFormulas();
			getCard().transferFocusTo(0);// 表头获得焦点
		} else {
			onCancelOp();
		}
	}

	private Integer qryPsnScope(GeneralVO vo) throws BusinessException {
		Object obj = HIDelegator.getPsnInf().getValue("bd_psnbasdoc", "id","pk_psnbasdoc",  vo.getAttributeValue("id"));
		if(obj==null){
			return null;
		}
		PsndocVO[] psnVOs = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " pk_psnbasdoc='"+obj.toString()+"' and pk_corp='"+Global.getCorpPK()+"'");
		//GeneralVO[] personVOs = HIDelegator.getPsnInf()
		//.queryPersonInfo(obj.toString(), "bd_psndoc",
		//		Global.getCorpPK());
		String dVal = "2001-01-02";
		Integer psnclscope = 0;
		for(int i=0;i<psnVOs.length;i++){
			UFDateTime createtime = (UFDateTime)psnVOs[i].getAttributeValue("createtime");
			String strTime = null;
			if(createtime == null ){
				strTime = null;
			}else{
				strTime = createtime.toString();
			}
			if(strTime!=null && dVal .compareTo(strTime)<0){
				dVal = strTime;
				psnclscope =(Integer)psnVOs[i].getAttributeValue("psnclscope");
			}
		}
		return psnclscope;
	}

	private void ResetJobRef() {
		BillItem jobItem = getBillItem("bd_psndoc.pk_om_job");
		UIRefPane jobref = (UIRefPane) jobItem.getComponent();
		nc.ui.hi.ref.JobRef job = null;
		job = new nc.ui.hi.ref.JobRef(Global.getCorpPK(), false);
		jobref.setRefType(1);
		jobref.setRefInputType(1);
		jobref.setRefModel(job);
	}

	/**
	 * v53 加载返聘人员信息
	 * 
	 */
	public void loadRehirePerson(String pk_psnbasdoc, String rehirepsncorp,
			String pk_psndoc) throws Exception {
		GeneralVO tempaccpsndoc = null;
		if (tempaccpsndoc == null) {
			GeneralVO[] personVOs = HIDelegator
					.getPsnInf()
					.queryMainPersonInfo(pk_psnbasdoc, Global.getCorpPK(),
							"bd_psnbasdoc", GlobalTool.getFuncParserWithoutWa());
			tempaccpsndoc = personVOs[0] == null ? null
					: (GeneralVO) personVOs[0].clone();
		}
		PersonEAVO rehireperson = new PersonEAVO();
		tempaccpsndoc.removeAttributeName("indocflag");
		tempaccpsndoc.setAttributeValue("pk_corp", Global.getCorpPK());
		tempaccpsndoc.setAttributeValue("belong_pk_corp", Global.getCorpPK());
		rehireperson.setAccpsndocVO(tempaccpsndoc);
		// 不论是否本公司返聘,都要查询原公司信息,取人员编码

		if (rehirepsncorp.equalsIgnoreCase(Global.getCorpPK())) {// 本公司返聘,工作信息带入
			GeneralVO temppsndoc = null;
			if (temppsndoc == null) {
				// 
				GeneralVO[] personVOs = HIDelegator.getPsnInf()
						.queryMainPersonInfo(pk_psndoc, Global.getCorpPK(),
								"bd_psndoc",
								GlobalTool.getFuncParserWithoutWa());
				temppsndoc = personVOs[0] == null ? null
						: (GeneralVO) personVOs[0].clone();
			}
			if (temppsndoc != null) {
				temppsndoc.removeAttributeName("pk_psncl");
				temppsndoc.removeAttributeName("outdutydate");
				// fengwei 2010-11-4 员工返聘/再聘后，到职日期不变，最新到岗日期初始化为登陆日期
				// temppsndoc.setAttributeValue("indutydate",
				// Global.getLogDate());
				temppsndoc.setAttributeValue("onpostdate", Global.getLogDate());
			}
			getBillItem("bd_psndoc.outdutydate").setEnabled(true);
			rehireperson.setPsndocVO(temppsndoc);
			getCard().setBillValueVO(rehireperson);
		} else {// 跨公司返聘,如果有工作信息则带入
			GeneralVO temppsndoc = null;
			if (temppsndoc == null) {
				GeneralVO[] personVOs = HIDelegator.getPsnInf()
						.queryPersonInfo(pk_psnbasdoc, "bd_psndoc",
								Global.getCorpPK());
				if (personVOs != null && personVOs.length > 0) {
					temppsndoc = (GeneralVO) personVOs[0].clone();
				}
			}
			GeneralVO[] personVOss = HIDelegator.getPsnInf().queryPersonInfo(
					pk_psnbasdoc, "bd_psndoc", rehirepsncorp);
			if (temppsndoc != null) {
				temppsndoc.removeAttributeName("pk_psncl");
				temppsndoc.removeAttributeName("outdutydate");
				// fengwei 2010-11-4 员工返聘/再聘后，到职日期不变，最新到岗日期初始化为登陆日期
				// temppsndoc.setAttributeValue("indutydate",
				// Global.getLogDate());
				temppsndoc.setAttributeValue("onpostdate", Global.getLogDate());
			}
			rehireperson.setPsndocVO(temppsndoc);
			getCard().setBillValueVO(rehireperson);
			// 如果是跨公司返聘，并且本公司无以前的工作信息
			if (personVOss != null && temppsndoc == null) {
				// 集团编码唯一的情况下，需要带入原人员编码
				if ("Y".equalsIgnoreCase(isUniquePsncodeInGroup)) {
					Object psncode = personVOss[0].getAttributeValue("psncode");
					if (psncode != null) {
						getBillItem("bd_psndoc.psncode").setValue(psncode);
					}
					// 公司编码唯一的情况下
				} else {
					// 自动编码
					if ((getPsncodeAutoGenerateParam() != null)
							&& getPsncodeAutoGenerateParam().trim().equals("1")) {
						// 自动生成编码，直接转人员档案,则把编辑时产生的编码拿过来
						if (intodocdirect == 0) {// 直接转人员档案,则把编辑时产生的编码拿过来
							getBillItem("bd_psndoc.psncode").setValue(
									autopsncode);
						}
						// 走入职审批，则将编码项置空，在审批通过后再产生编码，但是会产生跳码的情况,防止跳码，则回退一次
						else if (intodocdirect == 1) {
							resetBillcode();
							getBillItem("bd_psndoc.psncode").setValue("");
						}
						// 手工编码，带入原人员编码
					} else {
						Object psncode = personVOss[0]
								.getAttributeValue("psncode");
						if (psncode != null) {
							getBillItem("bd_psndoc.psncode").setValue(psncode);
						}
					}
				}
			}
		}
		if (rehireperson.getPsndocVO() != null
				&& rehireperson.getPsndocVO().getAttributeValue("pk_deptdoc") != null) {
			String pk_deptdoc = (String) rehireperson.getPsndocVO()
					.getAttributeValue("pk_deptdoc");
			Object pk_om_job = rehireperson.getPsndocVO().getAttributeValue(
					"pk_om_job");
			UIRefPane jobRef = (UIRefPane) getBillItem("bd_psndoc.pk_om_job")
					.getComponent();
			if (jobRef != null) {
				jobRef.getRefModel().setWherePart(
						" om_job.pk_deptdoc ='" + pk_deptdoc + "'");
				jobRef.getRefModel().clearData();
				// //dusx修改，上一句不起作用，使用下面两句对参照进行刷新
				// ((nc.ui.hi.hi_301.ref.JobRef)jobRef.getRefModel()).setPk_deptdoc(pk_deptdoc);
				// jobRef.getRefModel().reloadData();
				// //dusx修改结束2009.6.25
			}
			if (pk_om_job != null) {
				jobRef.setPK((String) pk_om_job);
			}
		}
		// String psncode = null;
		// if ("Y".equalsIgnoreCase(isUniquePsncodeInGroup)) {//
		// 如果是集团内唯一，则按集团产生人员编码
		// if ((getPsncodeAutoGenerateParam() != null) &&
		// getPsncodeAutoGenerateParam().trim().equals("1")){//自动生成
		// if(intodocdirect == 0){//直接转人员档案
		// psncode = HIDelegator.getBillcodeRule().getBillCode_RequiresNew("BS",
		// "0001", null, getObjVO());
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// }else if(intodocdirect == 1){//走入职审批
		// psncode = "";
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// }
		// }else{
		// psncode = "";
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// }

		// } else {//公司唯一
		// if ((getPsncodeAutoGenerateParam() != null) &&
		// getPsncodeAutoGenerateParam().trim().equals("1")) {//自动生成
		// if(rehirepsncorp.equalsIgnoreCase(Global.getCorpPK())){//本公司返聘,工作信息带入
		// if (intodocdirect == 0) {// 直接转人员档案
		// psncode = HIDelegator.getBillcodeRule().getBillCode_RequiresNew("BS",
		// Global.getCorpPK(), null, getObjVO());
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// } else if (intodocdirect == 1) {// 走入职审批
		// psncode = "";
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// }
		// } else {//跨公司
		// if (intodocdirect == 0) {// 直接转人员档案
		// psncode = HIDelegator.getBillcodeRule().getBillCode_RequiresNew("BS",
		// Global.getCorpPK(), null, getObjVO());
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// } else if (intodocdirect == 1) {// 走入职审批
		// psncode = "";
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// }
		// }
		// }else{
		// psncode = "";
		// getBillItem("bd_psndoc.psncode").setValue(psncode);
		// }
		// }
		isLoadedAccData = true;

	}

	/**
	 * v53 加载返聘人员子集信息
	 * 
	 */
	public void loadRehirePersonChildInfo(String pk_psnbasdoc,
			String rehirepsncorp, String pk_psndoc) throws Exception {

		try {
			if (person == null) {
				person = new PersonEAVO();
				person.setPk_psndoc(pk_psndoc);
				person.setPk_psnbasdoc(pk_psnbasdoc);
			}
			String tablecode = getBodyTableCode();
			// 是否是业务子集
			boolean isTraceTable = getCard().getTraceTables().get(tablecode) != null;

			// v50 add
			if (isTraceTable) {// 如果是业务子集
				// 查询该业务子集是否允许查看历史
				boolean look = HIDelegator.getPsnInf().isTraceTableLookHistory(
						tablecode);
				// 得到界面中用户是否选择查看历史数据
				boolean islookhistory = IsLookHistory();
				// 是否是归属公司人员用belong_pk_corp,而不是pk_corp
				boolean isbelongcorp = rehirepsncorp.equalsIgnoreCase(Global
						.getCorpPK());

				// 该业务子集允许查看历史且用户选中查看历史并且该人员是本公司人员，则重新查询子集数据，因为是否显示历史可能变化
				if (look && islookhistory && isbelongcorp) {
					// 如果已经查询过，则清除该业务子集数据
					if (person.getSubtables().get(tablecode) != null) {
						person.removeSubtable(tablecode);
					}
					// 查询该表的记录，业务子集允许查看历史的数据
					loadPsnChildSub(person.getPk_psndoc(), person
							.getPk_psnbasdoc(), tablecode, isTraceTable,
							islookhistory, person.getPk_psndoc());
				} else {
					// 如果已经查询过，则清除该业务子集数据
					if (person.getSubtables().get(tablecode) != null) {
						person.removeSubtable(tablecode);
					}
					// 加载当前人员的该表的信息记录，业务子不查看历史
					loadPsnChildSub(person.getPk_psndoc(), person
							.getPk_psnbasdoc(), tablecode, isTraceTable, false,
							person.getPk_psndoc());
				}
			} else {// 如果非业务子集
				if (person.getSubtables().get(tablecode) == null) {
					// 加载当前人员的该表的信息记录，非业务子集默认查看历史
					loadPsnChildSub(person.getPk_psndoc(), person
							.getPk_psnbasdoc(), tablecode, isTraceTable, true,
							person.getPk_psndoc());
				}
			}
			CircularlyAccessibleValueObject[] records = person
					.getTableVO(tablecode);// 当前信息集的所有记录
			getCard().getBillModel().setBodyDataVO(records);// 设置表格内容

		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * V35 add 把选择的行的数据设置到新增的行上
	 * 
	 * @param selectrow
	 * @param newRow
	 * @return
	 */
	private GeneralVO setSelRowToNewRow(int selectrow, int newRow) {
		GeneralVO vo = new GeneralVO();
		if (selectrow >= 0) {
			if (person != null) {
				CircularlyAccessibleValueObject[] records = person
						.getTableVO(getCurrentSetCode());
				if (records != null && records.length > 0) {
					vo = (GeneralVO) records[selectrow].clone();
					if ("hi_psndoc_edu".equalsIgnoreCase(getCurrentSetCode())) {
						vo.setAttributeValue("lasteducation", null);
					}
					getCard().getBillModel(getCurrentSetCode()).setBodyRowVO(
							vo, newRow);
					// 触发控件
				}
			}
		}
		// 设置最后一行的“是否在岗”为true，设置其余的为false fengwei 2009-09-21
		// -----------------------------------------------------
		if (person != null) {
			CircularlyAccessibleValueObject[] records = person
					.getTableVO(getCurrentSetCode());
			if (records != null && records.length > 0) {
				for (CircularlyAccessibleValueObject rec : records) {
					if ("hi_psndoc_deptchg"
							.equalsIgnoreCase(getCurrentSetCode())) {
						rec.setAttributeValue("poststat", UFBoolean.FALSE);
					}
				}
			}
		}
		// -----------------------------------------------------

		if ("hi_psndoc_ctrt".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("icontstate", nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-000050")/* @res "正常" */);
			vo.setAttributeValue(Util.PK_PREFIX + "icontstate", new Integer(2));
			vo.setAttributeValue("iconttype", nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("600704", "UPP600704-000072")/* @res "签订" */);
			vo.setAttributeValue(Util.PK_PREFIX + "iconttype", new Integer(1));
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue("dsigndate", Global.getLogDate());
			vo.setAttributeValue("pk_majorcorp", getClientEnvironment()
					.getCorporation().getUnitname());
		} else if ("hi_psndoc_deptchg".equalsIgnoreCase(getCurrentSetCode())) {
			vo
					.setAttributeValue(Util.PK_PREFIX + "pk_corp", Global
							.getCorpPK());
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue("poststat", UFBoolean.TRUE);
		} else if ("hi_psndoc_dimission".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_retire".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_orgpsn".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_psnchg".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_keypsn".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
		} else if ("hi_psndoc_training".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue("pk_corptraining", getClientEnvironment()
					.getCorporation().getUnitname());
		} else if ("hi_psndoc_ass".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corpperson", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue("pk_corpassess", getClientEnvironment()
					.getCorporation().getUnitname());
		} else // 履历增加时无对应任职记录
		if ("hi_psndoc_work".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_deptchg", null);
		}
		vo.setAttributeValue("pk_psndoc_sub", null);
		return vo;
	}

	/**
	 * 新增子表记录
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onAddChild() throws java.lang.Exception {
		if ("600707".equalsIgnoreCase(getModuleName())) {
			String sql = " select 1 from hi_psndoc_keypsn where pk_psndoc ='"
					+ person.getPk_psndoc() + "'and enddate is null";
			boolean iskeypsn = HIDelegator.getPsnInf().isRecordExist(sql);
			if (iskeypsn && !caneditkeypsn) {
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600707", "UPT600707-000239")/*
														 * @res
														 * "该人员为关键人员,请到关键人员管理节点维护其信息！"
														 */);
				return;
			}
		}
		// V56增加，校验是否存在未审核的（人员变动）单据。
		HIDelegator.getPsnInf().checkIfExistsUnAuditBillofPSN(
				new String[] { person.getPk_psndoc() }, getBodyTableCode());

		// 
		if (!isEditSub()) {
			if (!lockPsn(person.getPk_psndoc() + getBodyTableCode())) {

				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600704", "UPP600704-000070")/*
														 * @res "该人员的"
														 */
						+ getBodyTableName()
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000071")/*
													 * @res "子集正在有其他用户操作，请稍后再试！"
													 */);
				return;
			}
		}
		// 设置单据模板表体页签不能切换
		getCard().getBodyTabbedPane().setEnabled(false);
		// 正在编辑子表
		setEditType(EDIT_SUB);
		// 不是在修改行
		setEditLine(false);
		// 设置正在编辑行号
		int rowcount = getCard().getBillTable().getRowCount();
		setEditLineNo(rowcount);
		int selectrow = getCard().getBillTable(getCurrentSetCode())
				.getSelectedRow();
		// 添加一空行
		getCard().removeEditListener(getBodyTableCode());

		// 不失去焦点得不到数据
		TableCellEditor editor = getCard().getBillTable().getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}

		getCard().getBodyPanel().addLine();
		getCard().addEditListener(this);
		GeneralVO copyVO = setSelRowToNewRow(selectrow, rowcount);
		// 设置单据模板为可编辑状态
		setBillTempletState(true);
		// 为当前子表添加一行空记录
		SubTable subtable = person.getSubTable(getBodyTableCode());
		subtable.getRecords().addElement(copyVO);

		// setSelRowRefValueNull(getBodyTableCode());
		// 任职情况设置默认值 wangkf add
		if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_deptchg")) {
			if (nc.ui.hi.hi_301.Util.isDutyDependJobSeries) {
				UIRefPane dutyRef = null;
				UIRefPane jobRef = null;
				BillItem item = getBillItem("hi_psndoc_deptchg.pk_om_duty");
				if (item != null) {
					dutyRef = (UIRefPane) (item.getComponent());
				}
				jobRef = (UIRefPane) getBillItem("hi_psndoc_deptchg.pk_postdoc")
						.getComponent();
				String oldWhere = dutyRef.getRefModel().getWherePart();
				if (jobRef != null) {
					String pk_jobseries = (String) jobRef
							.getRefValue("om_job.jobseries");
					if (oldWhere.indexOf("and duty.series ='") >= 0) {
						oldWhere = oldWhere.substring(0, oldWhere
								.indexOf("and duty.series ='"));
					}
					if (pk_jobseries != null) {
						String newSql = oldWhere + " and duty.series ='"
								+ pk_jobseries.trim() + "'";
						dutyRef.getRefModel().setWherePart(newSql);
					} else {
						dutyRef.getRefModel().setWherePart(oldWhere);
					}
				}
			}
			getCard().getBodyPanel().getTableModel().setBodyDataVO(
					(CircularlyAccessibleValueObject[]) subtable.getRecords()
							.toArray(new CircularlyAccessibleValueObject[0]));
		} else if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_ctrt")
				|| getBodyTableCode().equalsIgnoreCase("hi_psndoc_dimission")
				|| "hi_psndoc_retire".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_training".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_orgpsn".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_ass".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_psnchg".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_keypsn".equalsIgnoreCase(getCurrentSetCode())) {
			getCard().getBodyPanel().getTableModel().setBodyDataVO(
					(CircularlyAccessibleValueObject[]) subtable.getRecords()
							.toArray(new CircularlyAccessibleValueObject[0]));
		}
		if (isNotMultiEdit()) {
			// 设置按钮状态
			setAddChildButtonState();
			// 设置当前行可编辑
			enableTableLine(getEditLineNo());
		} else {
			if (!isEditSub()) {
				setEditSub(true);
				getCard().getBodyPanel().getTableModel().updateValue();
			}
			// 设置按钮状态
			setMultEditChildButtonState();
		}
		setUnEditable();// v50 add
		// 防止表头最大化时编辑子集，看不到子集
		getCard().setPosMaximized(-1);

	}

	/**
	 * v50 add 设置需要默认的字段,业务子集的pk_corp等
	 * 
	 */
	private void setUnEditable() {
		if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_ctrt")) {
			// v50 add
			UIRefPane corpref = null;
			BillItem item = getBillItem("hi_psndoc_ctrt.pk_corp");
			if (item != null) {
				corpref = (UIRefPane) (item.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}
			BillItem itemMajor = getBillItem("hi_psndoc_ctrt.pk_majorcorp");
			if (itemMajor != null) {
				corpref = (UIRefPane) (itemMajor.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}

		} else if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_dimission")) {
			// v50 add
			UIRefPane corpref = null;
			BillItem item = getBillItem("hi_psndoc_dimission.pk_corp");
			if (item != null) {
				corpref = (UIRefPane) (item.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}
		} else if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_retire")) {
			// v50 add
			UIRefPane corpref = null;
			BillItem item = getBillItem("hi_psndoc_retire.pk_corp");
			if (item != null) {
				corpref = (UIRefPane) (item.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}
		} else if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_training")) {
			// v50 add
			UIRefPane corpref = null;
			BillItem item = getBillItem("hi_psndoc_training.pk_corp");
			if (item != null) {
				corpref = (UIRefPane) (item.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}

			UIRefPane corptrainingref = null;
			BillItem item2 = getBillItem("hi_psndoc_training.pk_corptraining");
			if (item2 != null) {
				corptrainingref = (UIRefPane) item.getComponent();
				corptrainingref.setPK(Global.getCorpPK());
				corptrainingref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corptrainingref.setEnabled(false);
				corptrainingref.setEditable(false);
			}

			String pk = corpref.getRefPK();
			String pkname = corpref.getRefName();
			String itemKey = item.getKey();
			CircularlyAccessibleValueObject record = person.getTableVO(item
					.getTableCode())[getEditLineNo()];
			record.setAttributeValue(Util.PK_PREFIX + itemKey, pk);
			record.setAttributeValue(itemKey, pkname);

			pk = corptrainingref.getRefPK();
			pkname = corptrainingref.getRefName();
			itemKey = item2.getKey();
			record.setAttributeValue(Util.PK_PREFIX + itemKey, pk);
			record.setAttributeValue(itemKey, pkname);

		} else if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_orgpsn")) {
			// v50 add
			UIRefPane corpref = null;
			BillItem item = getBillItem("hi_psndoc_orgpsn.pk_corp");
			if (item != null) {
				corpref = (UIRefPane) (item.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}
		} else if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_ass")) {
			// v50 add
			UIRefPane corpref = null;
			BillItem item = getBillItem("hi_psndoc_ass.pk_corpperson");
			if (item != null) {
				corpref = (UIRefPane) (item.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}
			// v50 add
			UIRefPane assessref = null;
			BillItem assessitem = getBillItem("hi_psndoc_ass.pk_corpassess");
			if (item != null) {
				assessref = (UIRefPane) (assessitem.getComponent());
				assessref.setPK(Global.getCorpPK());
				assessref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				assessref.setEnabled(false);
				assessref.setEditable(false);
			}
		} else if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_psnchg")) {
			// v50 add
			UIRefPane corpref = null;
			BillItem item = getBillItem("hi_psndoc_psnchg.pk_corp");
			if (item != null) {
				corpref = (UIRefPane) (item.getComponent());
				corpref.setPK(Global.getCorpPK());
				corpref.setText(getClientEnvironment().getCorporation()
						.getUnitname());
				corpref.setEnabled(false);
				corpref.setEditable(false);
			}
		}

	}

	/**
	 * 批量修改按钮响应事件处理函数
	 * 
	 * @throws java.lang.Exception
	 */
	public void onBatch() throws java.lang.Exception {
		if (getPsnList().getHeadSelectedRows().length < 1) {
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600710", "UPT600710-000047")/* @res 请在复选框中勾选要修改的人员!" */);
			return;
		}
		// 设置批量修改的使用的单据模板
		getBatchDlg().setHIBillCardPanel(getCard());
		getBatchDlg().showModal();
		// 获得批量修改结果
		BatchUpdateDlg.BatchResult batchResult = getBatchDlg().getBatchResult();
		if (batchResult != null) {
			// 确定之后
			GeneralVO value = batchResult.getValue();
			String tableCode = batchResult.getTableCode();
			String fieldCode = batchResult.getFieldCode();
			// V56增加，校验是否存在未审核的（人员变动）单据。
			HIDelegator.getPsnInf().checkIfExistsUnAuditBillofPSN(
					getselectPsnPksExceptRef("bd_psndoc"), tableCode);

			// 批量更新
			HIDelegator.getPsnInf().batchUpdate(
					getselectPsnPksExceptRef(tableCode), tableCode, fieldCode,
					value.getAttributeValue(fieldCode), getModuleName(),
					Global.getCorpPK());
			// wangkf add 更新辅助表中的关联字段
			String key = tableCode + "." + fieldCode;
			String acc_fldcode = (String) getRelationMap().get(key);
			if (acc_fldcode != null) {
				String relateTable = acc_fldcode.substring(0, acc_fldcode
						.indexOf("."));
				// 辅助表因当前保存发生变动，更新数据库
				HIDelegator.getPsnInf().batchUpdate(
						getselectPsnPksExceptRef(tableCode), relateTable,
						acc_fldcode, value.getAttributeValue(fieldCode),
						getModuleName(), Global.getCorpPK());
			}
			// 清缓存
			persons.clear();
			// 刷新界面
			onRefresh();
		}
	}

	/**
	 * 花名册按钮响应事件处理函数
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onBook() throws java.lang.Exception {
		// 获得当前人员列表的数据
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null)
			return;
		CtrlDeptVO dept = getSelectedDept();
		Vector conds = new Vector();
		if (conditions != null) {
			// 添加当前查询条件
			for (int i = 0; i < conditions.length; i++) {
				conds.addElement(conditions[i]);
			}
		}
		if (!node.isRoot()) {
			// update by sunxj 2010-02-03 快速查询插件 start
			// if (dept != null && isQuery) {
			if (dept != null && (isQuery || isQuickSearch)) {
				// update by sunxj 2010-02-03 快速查询插件 end
				if (dept.getNodeType() == CtrlDeptVO.DEPT) {
					// 不加此部门条件vo了，部门条件已经在wheresql中了，并且考虑了包含下级的情况。
					// ConditionVO deptCond = new ConditionVO();
					// deptCond.setTableCode("bd_psndoc");
					// deptCond.setFieldCode("bd_psndoc.pk_deptdoc");
					// deptCond.setOperaCode("=");
					// deptCond.setValue(dept.getPk_dept());
					// conds.addElement(deptCond);
				} else {
					// 不加此公司条件vo了，部门条件已经在wheresql中了，并且考虑了包含下级的情况。
					// ConditionVO deptCond = new ConditionVO();
					// deptCond.setTableCode("bd_psndoc");
					// deptCond.setFieldCode("bd_psndoc.pk_corp");
					// deptCond.setOperaCode("=");
					// deptCond.setValue(dept.getPk_corp());
					// conds.addElement(deptCond);
				}
			}
			if (vBookConds.size() > 0) {
				for (int i = 0; i < vBookConds.size(); i++) {
					ConditionVO vo = new ConditionVO();
					// update by sunxj 2010-02-03 快速查询插件 start
					// if (!isQuery) {
					if (!isQuery && !isQuickSearch) {
						// update by sunxj 2010-02-03 快速查询插件 end
						if (i == 0) {
							vo.setLogic(true);
						} else {
							vo.setLogic(false);
						}
					} else {
						vo.setLogic(false);
					}
					vo.setDataType(5);
					vo.setTableCode("bd_psndoc");
					vo.setFieldCode("bd_psndoc.pk_psndoc");
					vo.setOperaCode("=");
					vo.setValue((String) vBookConds.elementAt(i));
					conds.addElement(vo);
				}
			}
		}
		PsnInfReportDlg dlg = new PsnInfReportDlg(this);
		// 注意下面的order条件与人员采集/维护查询时的order条件一致
		// String order = null;
		// if (getModuleName().equals("600704")){
		// order = " order by bd_psndoc.showorder asc,bd_corp.unitcode
		// asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode
		// asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
		// }else if (getModuleName().equals("600707")){
		// Item item = (Item) getCmbPsncl().getSelectedItem();
		// if (item != null){
		// if (item.getValue()>0){
		// order = " order by bd_psndoc.showorder asc,bd_corp.unitcode
		// asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode
		// asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
		// }else {
		// order = " order by bd_psndoc.showorder asc,bd_corp.unitcode
		// asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode
		// asc,hi_psndoc_deptchg.jobtype asc,bd_psncl.psnclasscode
		// asc,om_duty.dutycode asc ";
		// }
		// }
		// }
		String order = getConfigDialog().getOrderStr();
		dlg.setOrder(order);
		if (getModuleName().equalsIgnoreCase("600707")
				|| getModuleName().equalsIgnoreCase("600708")) {
			Item itemcl = (Item) getCmbPsncl().getSelectedItem();
			Item itemjob = (Item) getCmbJobType().getSelectedItem();
			if (itemcl != null && itemcl.getValue() == 0 && itemjob != null
					&& itemjob.getValue() != -1) {
				// 加入任职类型条件
				ConditionVO cond = new ConditionVO();
				cond.setTableCode("bd_psndoc");
				cond.setFieldCode("bd_psndoc.jobtypeflag");
				cond.setOperaCode("=");
				cond.setDataType(1);
				cond.setValue("" + itemjob.getValue() + "");
				conds.addElement(cond);
			}
		}
		dlg.setCorpWhereStr(getTreeSelCorpPK());
		dlg.setQueryConditionVOs((ConditionVO[]) conds
				.toArray(new ConditionVO[0]));
		dlg.setIPsnScope(getTempPsnScope());
		dlg.setIndocflag(getIndocflag());
		// 传入是否要包含下级公司（或部门）
		dlg.setIncludesubcorp(getincludeChildDeptPsn().isSelected());
		String dlg_wheresql = getQueryDialog().getWhereSQL();

		if (isQuickQuery) {
			dlg_wheresql = quickDLGsql;
		}
		if (isQuickSearch) {
			dlg_wheresql = " bd_psndoc.pk_psndoc in(" + quickWherePart + ") ";
		}
		// dlg.setNormalsql(wheresql);
		if (dlg_wheresql != null)
			dlg.setNormalsql(wheresql + " and " + dlg_wheresql);
		else
			dlg.setNormalsql(wheresql);
		// 采集查看花名册的条件要和采集的查询条件保持一致：默认查询未在入职申请单的人员。
		if (getModuleName().equals("600704")) {
			if (dlg.getNormalsql().indexOf("bd_psnbasdoc.approveflag") < 0) {
				dlg
						.setNormalsql(dlg.getNormalsql()
								+ " and (bd_psnbasdoc.approveflag in (0,1) or bd_psnbasdoc.approveflag is null) ");
			}
		}

		String power = (String) deptPowerlist.get(getTreeSelCorpPK());
		if (power != null && power.length() > 4) {
			dlg.setDeptStr(" bd_psndoc.pk_deptdoc in (" + power + ") ");
		} else {
			dlg.setDeptStr(" 1 = 1 ");
		}
		dlg.showDetail();
	}

	/**
	 * 浏览按钮事件处理函数。
	 */
	private void onBrowse() throws Exception {
		// 获得当前选中行，并检查是否有效
		int row = getCard().getBillTable().getSelectedRow();
		int rowCount = getCard().getBillTable().getRowCount();
		if (row < 0 || rowCount == 0 || row >= rowCount) {
			return;
		}
		// 获得当前子表的所有记录
		CircularlyAccessibleValueObject[] records = person
				.getTableVO(getBodyTableCode());
		// 获得当前选中行的中文档位置
		String remotePath = (String) records[row].getAttributeValue("vdocloc");
		// 如果文档不为空，则浏览
		if (remotePath != null)
			FileTransClient.browseServerFile(remotePath);
	}

	/**
	 * 响应按钮事件。
	 */
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {
		try {
			if (bo == bnAdd) {
				onAdd();
			} else if (bo == bnSave) {
				onSave();
			} else if (bo == bnCancel) {
				onCancelOp();
			} else if (bo == bnSort) {
				onSort();
			} else if (bo == bnReturn) {
				onReturn();
			} else if (bo == bnEdit) {
				onEdit();
			} else if (bo == bnDel) {
				onDel();
			} else if (bo == bnAddChild) {
				onAddChild();
			} else if (bo == bnInsertChild) {
				onInsertChild();
			} else if (bo == bnUpdateChild) {
				onUpdateChild();
			} else if (bo == bnDelChild) {
				onDelChild();
			} else if (bo == bnIntoDoc) {
				onIntoDoc();
			} else if (bo == bnUpload) {
				onUpload();
			} else if (bo == bnFresh) {
				onRefresh();
			} else if (bo == bnSmUser) {
				onSmUser();
			} else if (bo == bnBatch) {
				onBatch();
			} else if (bo == bnQuery) {
				onQuery();
			} else if (bo == bnPrint) {
				onPrint();

				// 武燕军 2011-3-18 添加
			} else if(bo == bnImport){
				onImport();
			}
			/** *********************** */
			else if (bo == bnCard) {
				onCard();
			} else if (bo == bnBook) {
				onBook();
			} else if (bo == bnExportPic) {
				exportPic();
			} else if (bo == bnList) {
				onList();
			} else if (bo == bnUpRecord) {
				onUpRecord();
			} else if (bo == bnDownRecord) {
				onDownRecord();
			} else if (bo == bnToFirst) {
				onToFirst();
			} else if (bo == bnToLast) {
				onToLast();
			} else if (bo == bnListItemSet) {
				onListItemSet();
			} else if (bo == bnBatchAddSet) {
				onBatchAddSet();
			} else if (bo == bnApplicate) {
				onApplicate();
			} else if (bo == bnBatchApplicate) {
				onBatchApplicate();
			} else if (bo == bnAffirm) {
				onAffirm();
			} else if (bo == bnQueryAffirm) {
				onQueryAffirm();
			} else if (bo == bnCancelAffirm) {
				onCancelAffirm();
			} else if (bo == bnSubsetMove) {
				onSubsetMove();
			} else if (bo == bnIndocApp) {
				onOpenIndocApp();
			} else if (bo == bnSetSort) {// 排序
				onSetOrderAndSort();
			} else if (bo == bnView) {
				onCard();
			} else if (bo == bnBatchExport) {
				onBatchExport();
			} else if (bo == bnAbilityMatch) {
				onAbilityMatch();
			}
		} catch (Exception e) {
			reportException(e);
			String erromsg = e.getMessage();
			// 20100927
			if (erromsg.equalsIgnoreCase("ValidateFormulasValue"))
				return;
			// end

			// 已经提示过的异常，不再提示，直接退出。dusx add
			if (erromsg.startsWith("HAVEPOPMESSAGE"))
				return;
			if (e.getMessage().startsWith("psncode")) {
				erromsg = e.getMessage().substring(7);
				codeflag = false;
			}
			showErrorMessage(erromsg);
		}
	}

	protected boolean isEmployeeRef = false;

	public static final int TYPE_USERID = 0;

	public static final int TYPE_PK_CORP = 1;

	/**
	 * 
	 * 打开入职申请节点
	 */
	protected void onOpenIndocApp() {
		DocLinkAddData linkdata = new DocLinkAddData();
		if (psnList == null || psnList.length < 1) {
			SFClientUtil.showNode("600705", 2);
		}
		// 找到本公司的人员
		Vector<GeneralVO> volist = new Vector<GeneralVO>();
		int[] rows = getPsnList().getHeadSelectedRows();
		if (rows.length < 1)
			SFClientUtil.showNode("600705", 2);
		for (int i = 0; i < rows.length; i++) {
			GeneralVO vo = psnList[rows[i]];
			vo.setAttributeValue("pk_psndoc_showname", vo
					.getAttributeValue("psnname"));
			if (Global.getCorpPK().equals(vo.getAttributeValue("pk_corp")))
				volist.add(vo);
		}
		GeneralVO[] vos = new GeneralVO[volist.size()];
		volist.copyInto(vos);
		if (vos == null || vos.length < 1) {
			SFClientUtil.showNode("600705", 2);
		}
		// 如果采集节点查询出人员，则直接打开申请节点后，将列表中的人员加入新的申请单

		linkdata.setPsnList(vos);
		boolean succ = false;
		IFuncWindow window = SFClientUtil.findOpenedFuncWindow("600705");
		if (window != null) {
			succ = window.closeWindow();
		} else {
			succ = true;
		}
		//
		if (succ) {
			SFClientUtil.openNodeLinkedADD("600705", linkdata);// 打开节点
		} else {
			// showWarningMessage("窗口已经打开,没有正确关闭!");
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600700", "UPP600700-000114")/* @res "窗口已经打开,没有正确关闭!" */);
		}

	}

	/**
	 * 查看申请按钮响应事件
	 * 
	 * @throws java.lang.Exception
	 */
	public void onQueryAffirm() throws java.lang.Exception {
		// 查看申请的人员
		nc.ui.hi.hi_301.RefAffirmDlg queryAffirm = new nc.ui.hi.hi_301.RefAffirmDlg(
				this, true);
		queryAffirm.setTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"600704", "UPP600704-000204")/* @res "查看申请" */);
		queryAffirm.setCancelBtnVisible(false);
		queryAffirm.showModal();
	}

	/**
	 * 调整子集顺序按钮相应事件 v50 add
	 * 
	 * @throws java.lang.Exception
	 */
	public void onSubsetMove() throws java.lang.Exception {
		String currentSetCode = getCurrentSetCode();
		CircularlyAccessibleValueObject[] records = person
				.getTableVO(currentSetCode);// 当前信息集的所有记录
		if (records != null && records.length > 1) {
			// 调整子集顺序
			MoveSubsetDlg movesubset = new MoveSubsetDlg(this);
			movesubset.setTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPT600704-000273")/* @res "调整子集顺序" */);
			movesubset.showModal();
			if (movesubset.getResult() == UIDialog.ID_OK) {
				CircularlyAccessibleValueObject[] returnrecords = movesubset
						.getBodyData();
				SubTable subtable = new SubTable();
				subtable.setTableCode(currentSetCode);
				if (returnrecords != null) {
					subtable.setRecordArray(returnrecords);
				}
				if (person.getSubTable(currentSetCode) != null) {
					person.getSubtables().put(currentSetCode, returnrecords);
				}
				person.addSubtable(subtable);
			}
			loadPsnChildInfo(listSelectRow, currentSetCode);

		} else {
			// showErrorMessage("当前子集不需要调整顺序");
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600700", "UPP600700-000115")/* @res "当前子集不需要调整顺序" */);
			return;
		}
	}

	String app_pk_corp = null;// 申请公司

	/**
	 * 设置编辑属性
	 */
	protected void setHeadItemEnableInRef() {
		setHeadBillItemEditable("bd_psnbasdoc", false);
		setHeadBillItemEditable("bd_psndoc", false);
		getBillItem("bd_psndoc.psncode").setEdit(true);
		getBillItem("bd_psndoc.psncode").setEnabled(true);
		// 人员类别
		BillItem clItem = getBillItem("bd_psndoc.pk_psncl");
		UIRefPane psnclsref = (UIRefPane) clItem.getComponent();
		String wherepart = psnclsref.getRefModel().getWherePart();
		if (wherepart == null || wherepart.trim().length() == 0) {
			wherepart = " psnclscope = 5 ";
		} else if (wherepart.indexOf("psnclscope = 5") < 0) {
			String whereScope = " (psnclscope = 5) and (";
			wherepart = whereScope + wherepart + ")";
		}
		psnclsref.getRefModel().setWherePart(wherepart);
		// 部门参照
		BillItem deptItem = getBillItem("bd_psndoc.pk_deptdoc");
		UIRefPane deptref = (UIRefPane) deptItem.getComponent();
		setWhereToModel(
				deptref.getRefModel(),
				"((canceled = 'N' and hrcanceled = 'N') or ( canceled is null and hrcanceled is null))");
		getBillItem("bd_psndoc.pk_psncl").setEdit(true);
		getBillItem("bd_psndoc.pk_psncl").setEnabled(true);
		getBillItem("bd_psndoc.pk_deptdoc").setEdit(true);
		getBillItem("bd_psndoc.pk_deptdoc").setEnabled(true);
		getBillItem("bd_psndoc.pk_om_job").setEdit(true);
		getBillItem("bd_psndoc.pk_om_job").setEnabled(true);
		getBillItem("bd_psndoc.jobrank").setEdit(true);
		getBillItem("bd_psndoc.jobrank").setEnabled(true);
		getBillItem("bd_psndoc.series").setEdit(true);
		getBillItem("bd_psndoc.series").setEnabled(true);
		getBillItem("bd_psndoc.dutyname").setEdit(true);
		getBillItem("bd_psndoc.dutyname").setEnabled(true);
		getBillItem("bd_psndoc.jobseries").setEdit(true);
		getBillItem("bd_psndoc.jobseries").setEnabled(true);

		// 处理业务逻辑控制得可编辑性
		getBillItem("bd_psndoc.pk_psncl").setEdit(true);
		getBillItem("bd_psndoc.pk_deptdoc").setEdit(true);
		getBillItem("bd_psndoc.pk_om_job").setEdit(true);

		// 岗位序列，岗位等级，职务
		UIRefPane job = (UIRefPane) (getBillItem("bd_psndoc.pk_om_job")
				.getComponent());
		String pk_job = job.getRefPK();
		if (pk_job == null || pk_job.length() != 20) {

			getBillItem("bd_psndoc.jobrank").setEnabled(
					getBillItem("bd_psndoc.jobrank").isEdit());// true
			getBillItem("bd_psndoc.jobseries").setEdit(false);
			getBillItem("bd_psndoc.dutyname").setEnabled(
					getBillItem("bd_psndoc.dutyname").isEdit());// true
			getBillItem("bd_psndoc.series").setEnabled(
					getBillItem("bd_psndoc.series").isEdit());// true

			UIRefPane dutynameRef = (UIRefPane) (getBillItem("bd_psndoc.dutyname")
					.getComponent());
			String pkduty = dutynameRef.getRefPK();
			if (nc.ui.hi.hi_301.Util.isDutyDependJobSeries) {
				if (pkduty != null && pkduty.trim().length() == 20) {
					getBillItem("bd_psndoc.jobseries").setEnabled(false);
					getBillItem("bd_psndoc.jobseries").setEdit(false);
				} else {
					getBillItem("bd_psndoc.jobseries").setEnabled(
							getBillItem("bd_psndoc.jobseries").isEdit());// true
					getBillItem("bd_psndoc.jobseries").setEdit(
							getBillItem("bd_psndoc.jobseries").isEdit());
				}
			}
		} else {
			getBillItem("bd_psndoc.jobrank").setEnabled(false);
			getBillItem("bd_psndoc.jobrank").setEdit(false);
			getBillItem("bd_psndoc.jobseries").setEdit(false);
			String pk_duty = (String) job.getRefValue("om_job.pk_om_duty");
			if (pk_duty != null && pk_duty.trim().length() == 20) {
				getBillItem("bd_psndoc.dutyname").setEnabled(false);
				getBillItem("bd_psndoc.series").setEnabled(false);
				getBillItem("bd_psndoc.dutyname").setEdit(false);
				getBillItem("bd_psndoc.series").setEdit(false);
			} else {
				getBillItem("bd_psndoc.dutyname").setEnabled(
						getBillItem("bd_psndoc.dutyname").isEdit());
				getBillItem("bd_psndoc.dutyname").setEdit(
						getBillItem("bd_psndoc.dutyname").isEdit());
				UIRefPane dutynameRef = (UIRefPane) (getBillItem("bd_psndoc.dutyname")
						.getComponent());
				String pkduty = dutynameRef.getRefPK();
				if (pkduty != null && pkduty.trim().length() == 20) {
					getBillItem("bd_psndoc.series").setEnabled(false);
					getBillItem("bd_psndoc.series").setEdit(false);
				} else {
					getBillItem("bd_psndoc.series").setEnabled(
							getBillItem("bd_psndoc.series").isEdit());// true
				}
			}
		}
		if (!curTempCorpPk.equalsIgnoreCase(Global.getCorpPK())
				|| isPartPsnCurCorp) {
			// wangkf fixed isPartPsnCurCorp ->!isPartPsnCurCorp
			getBillItem("bd_psndoc.jobrank").setEnabled(false);
			getBillItem("bd_psndoc.jobseries").setEnabled(false);
			getBillItem("bd_psndoc.dutyname").setEnabled(false);
			getBillItem("bd_psndoc.series").setEnabled(false);
			setBillItemEnabled("bd_psndoc.timecardid", false);
		}

		// fengwei 2010-0903 引用人员增加和修改时，重置工作信息自定义项可以编辑 start
		BillItem[] items = getCard().getHeadShowItems("bd_psndoc");
		for (BillItem billItem : items) {
			String strKey = billItem.getKey();
			if (strKey.startsWith("groupdef")) {
				getBillItem("bd_psndoc." + strKey).setEdit(true);
				getBillItem("bd_psndoc." + strKey).setEnabled(true);
			}
		}
		// fengwei 2010-0903 引用人员增加和修改时，重置工作信息自定义项可以编辑 end

	}

	protected void setHeadItemEnable() {
		setHeadBillItemEditable("bd_psnbasdoc", true);
		setHeadBillItemEditable("bd_psndoc", true);
		getBillItem("bd_psndoc.psncode").setEdit(false);
		getBillItem("bd_psndoc.psncode").setEnabled(false);
		getBillItem("bd_psndoc.pk_psncl").setEdit(false);
		getBillItem("bd_psndoc.pk_psncl").setEnabled(false);
		getBillItem("bd_psndoc.pk_deptdoc").setEdit(false);
		getBillItem("bd_psndoc.pk_deptdoc").setEnabled(false);
		getBillItem("bd_psndoc.pk_om_job").setEdit(false);
		getBillItem("bd_psndoc.pk_om_job").setEnabled(false);
	}

	String oldpsncode = null;

	/**
	 * V35 新增 --人员引用 引用申请按钮响应事件
	 */
	public void onApplicate() throws java.lang.Exception {
		// 调用ApplicatePsnRefTreeModel类，得到一个特定的UIRefPane
		UIRefPane psndocref = new ApplicatePsnRefTreeModel().getRefPane(this);
		psndocref.getRef().showModal();
		String pk_psnbasdoc = (String) psndocref
				.getRefValue("bd_psnbasdoc.pk_psnbasdoc");
		app_pk_corp = (String) psndocref.getRefValue("bd_psnbasdoc.pk_corp");
		oldpsncode = (String) psndocref.getRefValue("bd_psndoc.psncode");
		String pk_psndoc = (String) psndocref
				.getRefValue("bd_psndoc.pk_psndoc");
		// 弹出人员参照对话框，选择维护人员
		// 根据主键查询出人员的信息，切换至卡片状态 －－人员信息状态控制：是引用还是非引用
		if (pk_psnbasdoc != null) {
			getCard().resumeValue();
			setEmployeeRef(true);
			setEditState(true);
			setEditType(EDIT_REF);
			person = new PersonEAVO();
			person.setPk_psnbasdoc(pk_psnbasdoc);
			person.setPk_psndoc(pk_psndoc);
			// del by zhyan 2006-05-17 引用人员时只能看到姓名，其他信息不能见false－》ture
			isLoadedBasicData = true;// wangkf add 20060515
			isLoadedAccData = true;// wangkf add 20060515
			loadPersonMain(pk_psnbasdoc, null, "bd_psnbasdoc");
			switchButtonGroup(EMPLOYEE_REF_CARD);
			setButtonsState(EMPLOYEE_REF_EDIT);
			setBillTempletState(true);
			setHeadItemEnableInRef();
			getBillItem("bd_psnbasdoc.psnname").setValue(
					person.getAccpsndocVO().getAttributeValue("psnname"));
			getBillItem("bd_psndoc.psncode").setValue(oldpsncode);
		}
		psndocref.getRefModel().clearData();
		// 根据人员信息状态，把信息保存到不同表中，注意在保存之后，把人员信息状态修改为非引用状态
	}

	/**
	 * 人员引用的批量申请按钮事件。v55增加
	 * 
	 * @throws Exception
	 */
	public void onBatchApplicate() throws Exception {

	}

	/**
	 * V35 新增 --人员引用 确认引用按钮响应事件
	 */
	public void onAffirm() throws java.lang.Exception {
		getRefAffirmDlg().setCancelBtnVisible(true);
		getRefAffirmDlg().showModal();
	}

	/**
	 * V35 新增 批量增加子集按钮响应事件
	 */
	public void onBatchAddSet() throws java.lang.Exception {
		if (getPsnList().getHeadSelectedRows().length < 1) {
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600710", "UPT600710-000046")/* @res "请在复选框中勾选要增加子集的人员!" */);
			return;
		}
		BatchAddSetDialog batchAddsetDlg = new BatchAddSetDialog(this, false,
				getModuleName(), true);
		batchAddsetDlg.setLocationRelativeTo(this);
		batchAddsetDlg.showModal();
	}

	/**
	 * V35 新增 首条按钮响应事件
	 */
	public void onToFirst() throws java.lang.Exception {
		listSelectRow = 0;
		person = null;
		person = loadPsnInfo(listSelectRow);
		setButtonsState(CARD_MAIN_BROWSE);
	}

	/**
	 * V35 新增 末条按钮响应事件
	 */
	public void onToLast() throws java.lang.Exception {
		listSelectRow = psnList.length - 1;
		person = null;
		person = loadPsnInfo(listSelectRow);
		setButtonsState(CARD_MAIN_BROWSE);

	}

	/**
	 * V35 新增 上一条按钮响应事件
	 */
	public void onUpRecord() throws java.lang.Exception {
		listSelectRow -= 1;
		person = null;
		person = loadPsnInfo(listSelectRow);
		setButtonsState(CARD_MAIN_BROWSE);
	}

	/**
	 * V35 新增 下一条按钮响应事件
	 */
	public void onDownRecord() throws java.lang.Exception {
		listSelectRow += 1;
		person = null;
		person = loadPsnInfo(listSelectRow);
		setButtonsState(CARD_MAIN_BROWSE);
	}

	/**
	 * 
	 * @param itemVOs
	 * @return
	 */
	protected Pair[] convertToPairs(GeneralVO[] itemVOs) {
		Vector vv = new Vector();
		Pair[] pairs = null;
		boolean includeshoworder = false;
		if (itemVOs != null && itemVOs.length > 0) {
			// fengwei。2010-09-21 将“公司名称”和“任职类型”放到项目设置中，不用特殊处理。
			// Pair unitpair = new
			// Pair("unitname",nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
			// "upt600704-000047")/* @res "公司名称" */, 0, "","bd_corp", true);
			// vv.addElement(unitpair);

			for (int i = 0; i < itemVOs.length; i++) {
				String tablecode = (String) itemVOs[i]
						.getAttributeValue("setcode");
				String code = (String) itemVOs[i].getAttributeValue("fldcode");
				if ("showorder".equals(code)) {
					includeshoworder = true;
					if (getModuleName().equals("600710"))
						tablecode = "hi_psndoc_keypsn";
				}
				if ("jobtype".equalsIgnoreCase(code)) {
					if (getModuleName().equalsIgnoreCase("600704")
							|| getModuleName().equals("600710")) {
						tablecode = null;
					}
					code = "jobtypename";
				}
				String name = (String) itemVOs[i].getAttributeValue("fldname");
				Integer datatype = (Integer) itemVOs[i]
						.getAttributeValue("datatype");
				int intDatatype = (datatype == null ? 0 : datatype.intValue());
				String reftype = (String) itemVOs[i]
						.getAttributeValue("fldreftype");
				Pair pair = new Pair(code, name, intDatatype, reftype,
						tablecode, true);
				vv.addElement(pair);
			}
			if (intodocdirect == 1
					&& getModuleName().equalsIgnoreCase("600704")) {

				Pair approveflagnamepair = new Pair("approveflagname",
						nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
								"UPP600700-000360")/* @res "人员状态" */, 0, "",
						"bd_psnbasdoc", true);
				vv.addElement(approveflagnamepair);
			}

			// 为何用keypsngroupname而不是groupname？
			if (getModuleName().equals("600710")) {
				Pair gourppair = new Pair("keypsngroupname",
						nc.ui.ml.NCLangRes.getInstance().getStrByID("600710",
								"UPP600710-000026")/* @res"人员组" */, 0, "",
						"hi_keypsn_group", true);
				vv.addElement(gourppair);

				if (!includeshoworder) {
					Pair showorderpair = new Pair("showorder",
							nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600704", "UPT600704-000271")/* "序号" */,
							BillItem.INTEGER, "", "hi_psndoc_keypsn", false);
					vv.addElement(showorderpair);
				}
			} else {
				// fengwei。2010-09-21 将“公司名称”和“任职类型”放到项目设置中，不用特殊处理。
				// if (getModuleName().equalsIgnoreCase("600707") ||
				// getModuleName().equalsIgnoreCase("600708")) {
				// Pair jobtypepair = new
				// Pair("jobtypename",nc.ui.ml.NCLangRes.getInstance().getStrByID("600704","UPP600704-000065")/*
				// @res "任职类型" */, 0, "",
				// "hi_psndoc_deptchg", true);
				// vv.addElement(jobtypepair);
				// }
				if (!includeshoworder) {
					Pair showorderpair = new Pair("showorder",
							nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600704", "UPT600704-000271")/* "序号" */,
							BillItem.INTEGER, "", "bd_psndoc", false);
					vv.addElement(showorderpair);
				}
			}

			if (vv.size() > 0) {
				pairs = new Pair[vv.size()];
				vv.copyInto(pairs);
			}
		}
		return pairs;
	}

	boolean itemchange = false;

	/**
	 * 项目设置对话框是否点击了“全消”按钮
	 */
	private boolean isSelectCancelAll = false;

	public boolean isSelectCancelAll() {
		return isSelectCancelAll;
	}

	public void setSelectCancelAll(boolean isSelectCancelAll) {
		this.isSelectCancelAll = isSelectCancelAll;
	}

	/**
	 * V35 新增
	 * 
	 * @throws java.lang.Exception
	 */
	public void onListItemSet() throws java.lang.Exception {
		setSelectCancelAll(false);
		// 构造Pair
		if (getListItemSetDlg().showModal() != UIDialog.ID_OK) {
			return;
		}
		if (isItemchange()) {
			if (isSelectCancelAll()) {// 如果选择了“全消”，则显示默认项目
				setListItems(getListItemsDefault());
			} else {
				GeneralVO[] itemVOs = getSelectItemVos();
				Pair[] pairs = convertToPairs(itemVOs);
				if (pairs != null && pairs.length > 0) {
					setListItems(pairs);
				} else {
					setListItems(getListItemsDefault());
				}
			}
			// 刷新树的表头
			// 重新设置表头数据：注意修改查询数据的方法
			initPsnList();
			deptTreeValueChanged(null);
		}
	}

	/**
	 * V35 新增返回列表
	 * 
	 * @throws java.lang.Exception
	 */
	public void onList() throws java.lang.Exception {
		if (isUpdated) {// 如果编辑过，则刷新psnList wangkf add
			if (person != null) {
				String curpk = person.getPk_psndoc();
				// onRefresh();
				// 设置人员条件
				ConditionVO cond = new ConditionVO();
				cond.setFieldCode("bd_psndoc.pk_psndoc");
				cond.setOperaCode("=");
				cond.setValue(curpk);

				// 更新改人员信息
				GeneralVO[] gvos = HIDelegator.getPsnInf().queryByCondition(
						Global.getCorpPK(), new ConditionVO[] { cond },
						powerSql, getListField(), wheresql);
				if (gvos != null && gvos.length > 0) {
					listSelectRow = findPersonPos(curpk, "pk_psndoc", psnList);// getPsnList().getTable().getSelectedRow();
					if (listSelectRow >= 0
							&& listSelectRow < queryResult.length) {
						queryResult[listSelectRow] = gvos[0];
						getPsnList().setBodyData(queryResult);
						getPsnList().getTableModel().execLoadFormula();
					}
				}
			}
		}

		isUpdated = false;
		setEditState(false);
		setEmployeeRef(false);
		switchButtonGroup(LIST_GROUP);
		setButtonGroup(LIST_GROUP);
		setButtonsState(LIST_BROWSE);
		int selectRow = listSelectRow;
		if (listSelectRow == -1) {
			selectRow = getPsnList().getTable().getRowCount() - 1;// 定位在最后一行
			listSelectRow = selectRow;
		}
		if (selectRow >= 0) {
			getPsnList().getTable().addRowSelectionInterval(selectRow,
					selectRow);
			getPsnList().getTable().addColumnSelectionInterval(0, 0);
			getPsnList().repaint();
		}
		isShowBillTemp = false;
	}

	/**
	 * 当选中取消按钮的响应事件。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onCancelOp() throws java.lang.Exception {

		boolean isFromREF = false;
		if (getEditType() == EDIT_SORT) {
			cancelSort();
		} else if (getEditType() == EDIT_MAIN) {
			try {
				if (isAdding()) {
					// 恢复唯一字段的编辑状态
					BillItem[] items = checkAddDlg.getAllBillItems();
					for (int i = 0; i < items.length; i++) {
						BillItem item = getCard()
								.getHeadItem(items[i].getKey());
						Object obj = htbEdit.get(items[i].getKey());
						if (obj != null) {
							item.setEdit(((Boolean) obj).booleanValue());
						} else {
							item.setEdit(false);
						}
					}

				}
				// 如果是引用人员，则要恢复表头的编辑状态
				// added on 2009-12-18 for 引用人员在引用公司不应该能够修改个人信息 by fengwei
				if (isRefPerson) {
					resumeEditState();
				}
				cancelEditMain();// 如果当前正在编辑主信息集
				if (!isAdding()) { // 修改取消解锁
					freeLockPsn(person.getPk_psndoc());
				} else if (paramIsValid()) {
					if (codeflag) {
						resetBillcode();
					}
					codeflag = true;
				}
				if (isAdding() && listSelectRow >= 0) {
					loadPsnInfo(listSelectRow);
				}
			} catch (Exception e) {
			} finally {
				if (!isAdding()) {
					freeLockPsn(person.getPk_psndoc());
				}
			}
			setButtonsState(CARD_MAIN_BROWSE);
			setAdding(false);
		} else if (getEditType() == EDIT_REF) {
			// 恢复表头字段的编辑状态
			// added on 2009-12-18 for 引用人员在引用公司不应该能够修改个人信息 by fengwei
			resumeEditState();

			getCard().resumeValue();// 否则清空编辑界面
			isFromREF = true;
			setBillTempletState(false);
			if (isEditing() && paramIsValid()) {
				if (codeflag) {
					resetBillcode();
				}
				codeflag = true;
			}
			setButtonsState(EMPLOYEE_REF_CARD_BROWSE);
			setEditState(false);
			setEmployeeRef(false);
		} else if (getEditType() == EDIT_RETURN) {
			// 恢复唯一字段的编辑状态
			BillItem[] items = checkAddDlg.getAllBillItems();
			for (int i = 0; i < items.length; i++) {
				BillItem item = getCard().getHeadItem(items[i].getKey());
				Object obj = htbEdit.get(items[i].getKey());
				if (obj != null) {
					item.setEdit(((Boolean) obj).booleanValue());
				} else {
					item.setEdit(false);
				}
			}
			cancelEditMain();// 如果当前正在编辑主信息集
			if (paramIsValid()) {
				if (codeflag) {
					resetBillcode();
				}
				codeflag = true;
			}
			setButtonsState(CARD_MAIN_BROWSE);
			setAdding(false);
			isrehire = false;
			setBillTempletState(false);
			setEditState(false);
		} else {
			try {
				cancelEditSub();// 如果当前正在编辑的是子表
				freeLockPsn(person.getPk_psndoc() + getBodyTableCode());// 子表编辑解锁
			} catch (Exception e) {
			} finally {
				freeLockPsn(person.getPk_psndoc() + getBodyTableCode());
				vDelPkPsndocSub.clear();
				// 
				vDelSubVOs.clear();
			}
			setButtonsState(CARD_MAIN_BROWSE);
			setButtonsState(CARD_CHILD_BROWSE);
			// 备份
			subBackupVOs = null;
		}

		getCard().getBodyTabbedPane().setEnabled(true);
		if (listSelectRow >= 0 && !isFromREF) {
			loadPsnInfo(listSelectRow);
		}
		// 设置职员编码控件不可编辑
		if(getBillItem("bd_psndoc.clerkcode") != null) {
			getBillItem("bd_psndoc.clerkcode").setEdit(false);
		}
		
		//
	}

	/**
	 * 恢复表头字段的编辑状态
	 * 
	 * @author fengwei on 2009-12-18
	 * @return
	 */
	private void resumeEditState() {
		// 恢复表头字段的编辑状态
		resumeHeadBillItemEditable("bd_psnbasdoc");
		resumeHeadBillItemEditable("bd_psndoc");
	}

	/**
	 * 恢复字段的编辑状态
	 * 
	 * @author fengwei on 2009-12-18
	 * @return
	 */
	protected void resumeHeadBillItemEditable(String table) {
		BillItem[] items = getCard().getHeadShowItems(table);
		if (items == null || items.length < 1)
			return;

		// 设置是否可以编辑
		for (int i = 0; i < items.length; i++) {
			BillItem item = getCard().getHeadItem(items[i].getKey());
			Object obj = htbEdit.get(items[i].getKey());
			if (obj != null) {
				item.setEdit(((Boolean) obj).booleanValue());
			} else {
				item.setEdit(false);
			}
		}
	}

	/**
	 * 单据参数是否正确
	 * 
	 * @return
	 */
	private boolean isPsncodeAutoGenerate() {
		return getPsncodeAutoGenerateParam().equalsIgnoreCase(
				PSNCODE_AUTO_GENERATE);
	}

	/**
	 * 单据号是否已经被填充并且不为空
	 * 
	 * @return
	 */
	private boolean autopsnbillcodeIsValid() {
		return (autopsncode != null && autopsncode.trim().length() > 0);
	}

	public void tableChanged2(TableModelEvent e) {
		// 
		if (getEditType() == EDIT_SORT) {
			isBeginEdit = true;
			int row = e.getFirstRow();
			GeneralVO[] sortPsnList = (GeneralVO[]) getPsnList()
					.getTableModel().getBodyValueVOs(
							"nc.vo.hi.hi_301.GeneralVO");
			GeneralVO[] oldPsnList = (GeneralVO[]) getPsnList().getBodyData();
			if (sortPsnList == null || sortPsnList.length < 1) {
				sortPsnList = oldPsnList;
			}
			Integer currentshoworder = ((Integer) sortPsnList[row]
					.getAttributeValue("showorder"));// .intValue();
			String pk_psndoc = (String) oldPsnList[row]
					.getAttributeValue("pk_psndoc");
			for (int i = 0; i < oldPsnList.length; i++) {
				if (oldPsnList[i].getAttributeValue("pk_psndoc").equals(
						pk_psndoc)
						&& i != row) {
					getPsnList().getTableModel().setValueAt(currentshoworder,
							i, "showorder");
				}
			}

			isBeginEdit = false;
		}

	}

	public void tableChanged(TableModelEvent e) {
		if (!isBeginEdit)
			tableChanged2(e);

	}

	/**
	 * 参数是否正确，用于回退单据号之前的条件判定
	 * 
	 * @return
	 */
	private boolean paramIsValid() {
		return (isPsncodeAutoGenerate() && autopsnbillcodeIsValid());
	}

	/**
	 * 回滚billcode并重设autopsnbillcode
	 * 
	 * @throws Exception
	 */
	private void resetBillcode() throws Exception {
		// 如果是集团内唯一，则按集团产生人员编码
		String pkcorp = ("Y".equalsIgnoreCase(isUniquePsncodeInGroup)) ? "0001"
				: Global.getCorpPK();
		HIDelegator.getBillcodeRule().returnBillCodeOnDelete(pkcorp, "BS",
				autopsncode, getObjVO());
		autopsncode = null;
	}

	/**
	 * 卡片按钮响应事件处理函数。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onCard() throws java.lang.Exception {
		// 获取当前人员pk
		String[] keys = getTempPsnPk();
		String pk_psndoc = keys[1];
		String pk_psnbasdoc = "";
		String pk_psndoc_sub = "";
		int jobtype = 0;
		if (pk_psndoc != null) {
			// 显示卡片
			GeneralVO vo = null;
			int row = getPsnList().getTable().getSelectedRow();
			// vo = psnList[row];// queryResult
			// pk_psnbasdoc = (String) vo.getAttributeValue("pk_psnbasdoc");
			// pk_psndoc_sub = (String) vo.getAttributeValue("pk_psndoc_sub");
			// Object o = vo.getAttributeValue("jobtypeflag");
			// if (o != null) {
			// jobtype = ((Integer) o).intValue();
			// }
			CardReportViewFrame dlg = new CardReportViewFrame(getTopFrame(this));
			dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			dlg.resetDlg(getClientEnvironment().getUser().getPrimaryKey(),
					corpPK, psnList, row);

			int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit()
					.getScreenSize().getWidth();
			int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit()
					.getScreenSize().getHeight();
			if (screenWidth <= 1280) {
				dlg.setSize(screenWidth, screenHeight - 50);
			} else {
				dlg.setSize(1024, 768);
			}

			// int x = (int) (screenWidth - dlg.getWidth()) / 2;
			// int y = (int) (screenHeight - dlg.getHeight()) / 2;
			// if (x < 0)x = 0;
			// if (y < 0)y = 0;
			dlg.setLocation(0, 0);

			dlg.showModal();
		}
	}

	/**
	 * 获得当前顶级父窗口，主要用于对话框的父窗口。 创建日期：(2004-5-18 11:55:22)
	 * 
	 * @return java.awt.Frame
	 */
	public static Frame getTopFrame(Component component) {

		// 逐级获取组件的父组件，直到父窗口
		Container parent = component.getParent();
		while (!(parent == null || parent instanceof Frame))
			parent = parent.getParent();

		return (Frame) parent;
	}

	/**
	 * 覆盖父类的onClosing方法：数据编辑窗口的关闭，只要涉及到是否保存退出 进行提示
	 */
	public boolean onClosing() {
		if (bnSave.isEnabled()) {// 
			String title = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000020")/* @res "提示" */;
			String question = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPT600704-000244")/* @res "是否保存" */;
			int operatestate = MessageDialog.showYesNoCancelDlg(this, title,
					question);
			if (operatestate == MessageDialog.ID_YES
					|| operatestate == MessageDialog.ID_OK) {
				try {
					onSave();
					return true;
				} catch (Exception ex) {
					reportException(ex);
					showErrorMessage(ex.getMessage());
					return false;
				}
			} else if (operatestate == MessageDialog.ID_NO
					|| operatestate == MessageDialog.ID_NOTOALL) {
				try {
					if (getEditType() == EDIT_MAIN) {
						// 如果参数有效，回滚billcode并重设autopsnbillcode
						if (paramIsValid()) {
							if (codeflag) {
								resetBillcode();
							}
							codeflag = true;
						}
					} else if (getEditType() == EDIT_SUB) {
					} else if (getEditType() == EDIT_SORT) {
						return true;
					}
				} catch (Exception e) {
					reportException(e);
					showErrorMessage(e.getMessage());
				}
				if (person != null) {
					try {
						// 释放人员锁。
						String pk_psndoc = null;
						if (getEditType() == EDIT_MAIN) {
							pk_psndoc = person.getPk_psndoc();
						} else if (getEditType() == EDIT_SUB) {
							pk_psndoc = person.getPk_psndoc()
									+ getBodyTableCode();
						} else if (getEditType() == EDIT_REF) {
							return true;
						}
						freeLockPsn(pk_psndoc);
					} catch (Exception e) {
						reportException(e);
						showErrorMessage(e.getMessage());
						return true;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * 删除按钮选中时响应事件。维护节点已重写。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onDel() throws java.lang.Exception {
		String pkpsndoc = "";
		try {
			if (listSelectRow < 0) {
				return;
			}
			int oriselectrow = listSelectRow;// 记录删除前该人员在列表中的位置
			// 确认删除
			GeneralVO psn = psnList[listSelectRow];
			pkpsndoc = (String) psn.getAttributeValue("pk_psndoc");
			String pk_psnbasdoc = (String) psn
					.getAttributeValue("pk_psnbasdoc");
			String isreturn = (String) psn.getAttributeValue("isreturn");

			String sql1 = " select 1 from bd_psndoc where psnclscope in(2) and indocflag = 'Y'and pk_psnbasdoc ='"
					+ pk_psnbasdoc + "'";// and pk_corp
			// ='"+Global.getCorpPK()+"'
			String sql2 = " select 1 from hi_docapply_b where pk_psndoc ='"
					+ pkpsndoc + "'";
			boolean[] isexists = HIDelegator.getPsnInf().isRecordExists(
					new String[] { sql1, sql2 });

			boolean existrehire = isexists[0];// 再聘人员
			boolean existdoc = isexists[1];
			if (existdoc) {
				// showWarningMessage("该人员存在入职申请单中，不能删除！");
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600700", "UPP600700-000117")/*
														 * @res
														 * "该人员存在入职申请单中，不能删除！"
														 */);
				return;
			}
			if (!lockPsn(pkpsndoc)) {
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600704", "UPP600704-000073")/*
														 * @res*
														 * "该人员正在被其他用户操作，请稍后再试！"
														 */);
				return;
			}
			int useroperate = MessageDialog.showYesNoDlg(this,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000020")/* @res "提示" */,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000074")/*
												 * @res "是否删除"
												 */
							+ psn.getAttributeValue("psnname")
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600704", "UPP600704-000075")/*
																	 * @res "？"
																	 */);
			if (useroperate == MessageDialog.ID_OK
					|| useroperate == MessageDialog.ID_YES) {
				// 获取要删除的人员，当前操作员及当前公司
				String pk_corp = Global.getCorpPK();
				// 先将删除标志改为1
				HIDelegator.getPsnInf().updateDeleteFlag(pkpsndoc, true);
				if ("Y".equalsIgnoreCase(isreturn)) {// 如果是返聘人员或者再聘人员，只删除工作信息
					HIDelegator.getPsnInf().deletePersonRehire(psn, pk_corp, 0);
				} else if (existrehire) {
					HIDelegator.getPsnInf().deletePersonRehire(psn, pk_corp, 1);
				} else {
					// 删除
					HIDelegator.getPsnInf().deletePersonnotinDoc(psn, pk_corp);
				}
				// 删除记录的附件信息
				IAttachment attachment = (IAttachment) NCLocator.getInstance()
						.lookup(IAttachment.class.getName());
				attachment.deleteAttachment((String) psn
						.getFieldValue("pk_psndoc"), "bd_psndoc");

				// 补号
				String pkcorp = ("Y".equalsIgnoreCase(isUniquePsncodeInGroup)) ? "0001"
						: Global.getCorpPK();
				HIDelegator.getBillcodeRule().returnBillCodeOnDelete(pkcorp,
						"BS", (String) psn.getAttributeValue("psncode"),
						getObjVO());

				// 从查询结果中删除此人
				queryResult = deletePsnFromArray(queryResult, pkpsndoc);
				// 从当前显示结果中删除此人
				psnList = deletePsnFromArray(psnList, pkpsndoc);
				// 刷新当前人员列表
				getPsnList().setBodyData(psnList);// 此操作将listSelectRow置为-1
				getPsnList().getTableModel().execLoadFormula();
				// 刷新当前缓冲中关于该部门的人员信息,Fixed Bug
				psnDeptCache.clear(); // put(getSelectedDept(),psnList);
				psnCorpCache.clear();// wangkf add

				freeLockPsn(pkpsndoc);
				if (psnList != null && psnList.length > 0) {
					listSelectRow = oriselectrow-1;// 恢复删除前该人员在列表中的位置
				} else {
					listSelectRow = -1;
				}
				if (getButtonGroup() == CARD_GROUP) {
					if (psnList != null && psnList.length > 0) {
						if (listSelectRow == 0) {
							listSelectRow--;
							onDownRecord();
						} else if (listSelectRow > 0) {
							onUpRecord();
						}
					} else {
						listSelectRow = -1;
						getCard().resumeValue();
						bnChildEdit.setEnabled(false);
						bnEdit.setEnabled(false);
						bnDel.setEnabled(false);
						updateButtons();
					}
				} else {
					onRefresh();
				}
			} else {
				freeLockPsn(pkpsndoc);
				return;
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
			// 如果删除失败,则将标志改为0
			HIDelegator.getPsnInf().updateDeleteFlag(pkpsndoc, false);
			freeLockPsn(pkpsndoc);
		}
	}

	/**
	 * (多条删除)删除按钮选中时响应事件。维护节点已重写。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onDelmul() throws java.lang.Exception {
		int[] rows = getPsnList().getHeadSelectedRows();
		if (rows.length < 1) {
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600710", "UPT600710-000051")/* @res "请选择要删除的人员!" */);
		}
		int oriselectrow = rows[0];// 记录删除位置
		GeneralVO[] selectPsns = new GeneralVO[rows.length];
		String[] pkpsndocs = new String[rows.length];
		String[] psnnames = new String[rows.length];
		String[] isreturns = new String[rows.length];
		boolean[] existrehires = new boolean[rows.length];
		try {
			for (int i = 0; i < rows.length; i++) {
				// 确认删除
				GeneralVO psn = psnList[rows[i]];
				String pkpsndoc = (String) psn.getAttributeValue("pk_psndoc");
				String pk_psnbasdoc = (String) psn
						.getAttributeValue("pk_psnbasdoc");
				String psnname = (String) psn.getAttributeValue("psnname");
				String isreturn = (String) psn.getAttributeValue("isreturn");
				String pk_corp = (String) psn.getAttributeValue("pk_corp");
				selectPsns[i] = psn;
				pkpsndocs[i] = pkpsndoc;
				psnnames[i] = psnname;
				isreturns[i] = isreturn;
				if (!Global.getCorpPK().equals(pk_corp)) {
					freeLockPsns(pkpsndocs);
					// showWarningMessage(psnname+":"+"不是当前公司人员，不能删除！");
					showWarningMessage(psnname
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000359")/*
																	 * @res
																	 * "不是当前公司人员，不能删除！"
																	 */);
					return;
				}
				String sql1 = " select 1 from bd_psndoc where psnclscope in(2) and indocflag = 'Y'and pk_psnbasdoc ='"
						+ pk_psnbasdoc + "'";// and pk_corp
				// ='"+Global.getCorpPK()+"'
				boolean existrehire = HIDelegator.getPsnInf().isRecordExist(
						sql1);// 再聘人员
				existrehires[i] = existrehire;
				String sql2 = " select 1 from hi_docapply_b where pk_psndoc ='"
						+ pkpsndoc + "'";
				boolean existdoc = HIDelegator.getPsnInf().isRecordExist(sql2);
				if (existdoc) {
					freeLockPsns(pkpsndocs);
					// showWarningMessage(psnname+":"+"该人员存在入职申请单中，不能删除！");
					showWarningMessage(psnname
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000117")/*
																	 * @res
																	 * "该人员存在入职申请单中，不能删除！"
																	 */);
					return;
				}
				if (!lockPsn(pkpsndoc)) {
					freeLockPsns(pkpsndocs);
					showWarningMessage(psnname
							+ ":"
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600704", "UPP600704-000073")/*
																	 * @res*
																	 * "该人员正在被其他用户操作，请稍后再试！"
																	 */);
					return;
				}
			}
			int useroperate = MessageDialog.showYesNoDlg(this,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000020")/* @res "提示" */,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000074")/*
												 * @res "是否删除"
												 */
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600704", "UPP600704-000075")/*
																	 * @res "？"
																	 */);
			if (useroperate != MessageDialog.ID_OK
					&& useroperate != MessageDialog.ID_YES) {
				freeLockPsns(pkpsndocs);
				return;
			}
		} catch (Exception e) {
			freeLockPsns(pkpsndocs);
			reportException(e);
			showErrorMessage(e.getMessage());
			return;
		}

		String pk_corp = Global.getCorpPK();
		for (int i = 0; i < pkpsndocs.length; i++) {
			try {
				// 先将删除标志改为1
				HIDelegator.getPsnInf().updateDeleteFlag(pkpsndocs[i], true);
				if ("Y".equalsIgnoreCase(isreturns[i])) {// 如果是返聘人员或者再聘人员，只删除工作信息
					HIDelegator.getPsnInf().deletePersonRehire(selectPsns[i],
							pk_corp, 0);
				} else if (existrehires[i]) {
					HIDelegator.getPsnInf().deletePersonRehire(selectPsns[i],
							pk_corp, 1);
				} else {
					// 删除
					HIDelegator.getPsnInf().deletePersonnotinDoc(selectPsns[i],
							pk_corp);
				}
				// 删除记录的附件信息
				IAttachment attachment = (IAttachment) NCLocator.getInstance()
						.lookup(IAttachment.class.getName());
				attachment.deleteAttachment((String) selectPsns[i]
						.getFieldValue("pk_psndoc"), "bd_psndoc");

				// 补号
				String pkcorp = ("Y".equalsIgnoreCase(isUniquePsncodeInGroup)) ? "0001"
						: Global.getCorpPK();
				HIDelegator.getBillcodeRule().returnBillCodeOnDelete(pkcorp,
						"BS",
						(String) selectPsns[i].getAttributeValue("psncode"),
						getObjVO());
			} catch (Exception e) {
				freeLockPsn(pkpsndocs[i]);
				reportException(e);
				showErrorMessage(e.getMessage());
				// 如果删除失败,则将标志改为0
				HIDelegator.getPsnInf().updateDeleteFlag(pkpsndocs[i], false);
				return;
			}
			// 从查询结果中删除此人
			queryResult = deletePsnFromArray(queryResult, pkpsndocs[i]);
			// 从当前显示结果中删除此人
			psnList = deletePsnFromArray(psnList, pkpsndocs[i]);
			freeLockPsn(pkpsndocs[i]);
		}
		// 刷新当前人员列表
		getPsnList().setBodyData(psnList);// 此操作将listSelectRow置为-1
		getPsnList().getTableModel().execLoadFormula();
		// 刷新当前缓冲中关于该部门的人员信息,Fixed Bug
		psnDeptCache.clear(); // put(getSelectedDept(),psnList);
		psnCorpCache.clear();// wangkf add

		if (psnList != null && psnList.length > 0) {
			listSelectRow = oriselectrow;// 恢复删除前该人员在列表中的位置
		} else {
			listSelectRow = -1;
		}
		onRefresh();
	}

	private void freeLockPsns(String[] pkpsndocs) throws Exception {
		for (String pkpsndoc : pkpsndocs) {
			if (pkpsndoc != null)
				freeLockPsn(pkpsndoc);
		}
	}

	/**
	 * 删除行操作响应事件函数。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onDelChild() throws java.lang.Exception {
		try {
			if ("600707".equalsIgnoreCase(getModuleName())) {
				String sql = " select 1 from hi_psndoc_keypsn where pk_psndoc ='"
						+ person.getPk_psndoc() + "'and enddate is null";
				boolean iskeypsn = HIDelegator.getPsnInf().isRecordExist(sql);
				if (iskeypsn && !caneditkeypsn) {
					showWarningMessage(nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("600707", "UPT600707-000239")/*
																		 * @res
																		 * "该人员为关键人员,请到关键人员管理节点维护其信息！"
																		 */);
					return;
				}
			}
			// V56增加，校验是否存在未审核的（人员变动）单据。
			HIDelegator.getPsnInf().checkIfExistsUnAuditBillofPSN(
					new String[] { person.getPk_psndoc() }, getBodyTableCode());

			if (!isEditSub()) {
				if (!lockPsn(person.getPk_psndoc() + getBodyTableCode())) {

					showWarningMessage(nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("600704", "UPP600704-000070")/*
																		 * @res
																		 * "该人员的"
																		 */
							+ getBodyTableName()
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600704", "UPP600704-000071")/*
																	 * @res
																	 * "子集正在有其他用户操作，请稍后再试！"
																	 */);
					return;
				}
			}

			// 检查选中行的有效性
			int row = getCard().getBillTable().getSelectedRow();
			if (row == -1 || getCard().getBillTable().getRowCount() == 0) {
				freeLockPsn(person.getPk_psndoc() + getBodyTableCode());
				return;
			}
			// 确认删除
			int user_operate = MessageDialog.showYesNoDlg(this,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000020")/* @res "提示" */,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000077")/* @res "是否要删除该项记录？" */);
			if (user_operate == MessageDialog.ID_OK
					|| user_operate == MessageDialog.ID_YES) {
				// 从子表记录中删除该行
				SubTable subtable = person.getSubTable(getBodyTableCode());
				GeneralVO vo = (GeneralVO) subtable.getRecords().elementAt(row);
				subtable.getRecords().removeElementAt(row);
				if (isNotMultiEdit()) {
					// 从数据库中删除该行
					HIDelegator.getPsnInf().deleteChild(getBodyTableCode(),
							person.getPk_psndoc(), vo);
					// 同步辅助信息---
					boolean updated = isUpdateAccRel(vo.getAttributeNames(),
							getBodyTableCode());// wangkf
					// add
					if (updated) {
						GeneralVO voTemp = new GeneralVO();
						voTemp.setAttributeValue("pk_psnbasdoc", person
								.getPk_psnbasdoc());
						voTemp.setAttributeValue("pk_psndoc", person
								.getPk_psndoc());
						GeneralVO relVO = HIDelegator.getPsnInf().updateAccRel(
								vo.getAttributeNames(), getRelationMap(),
								getBodyTableCode(), voTemp);
						// V35 modify
						HashMap hmRelVO = new HashMap();
						String[] fields = relVO.getAttributeNames();
						GeneralVO psnvo = new GeneralVO();
						GeneralVO baspsnvo = new GeneralVO();
						String mainpk = person.getPk_psndoc();
						psnvo.setAttributeValue("pk_psnbasdoc", person
								.getPk_psnbasdoc());
						psnvo.setAttributeValue("pk_psndoc", person
								.getPk_psndoc());
						baspsnvo.setAttributeValue("pk_psnbasdoc", person
								.getPk_psnbasdoc());
						baspsnvo.setAttributeValue("pk_psndoc", person
								.getPk_psndoc());
						boolean isRelPsndoc = false;
						boolean isRelBasPsndoc = false;
						for (int i = 0; i < fields.length; i++) {
							if (fields[i].indexOf(".") < 0) {
								continue;
							}
							String table = fields[i].substring(0, fields[i]
									.indexOf("."));
							if ("bd_psndoc".equalsIgnoreCase(table)) {
								String field = fields[i].substring(fields[i]
										.indexOf(".") + 1);
								psnvo.setAttributeValue(field, relVO
										.getAttributeValue(fields[i]));
								person.getPsndocVO().setAttributeValue(field,
										relVO.getAttributeValue(fields[i]));
								isRelPsndoc = true;
							} else if ("bd_psnbasdoc".equalsIgnoreCase(table)) {
								String field = fields[i].substring(fields[i]
										.indexOf(".") + 1);
								baspsnvo.setAttributeValue(field, relVO
										.getAttributeValue(fields[i]));
								person.getAccpsndocVO().setAttributeValue(
										field,
										relVO.getAttributeValue(fields[i]));
								isRelBasPsndoc = true;
							}

						}
						if (isRelPsndoc)
							hmRelVO.put("bd_psndoc", psnvo);
						if (isRelBasPsndoc)
							hmRelVO.put("bd_psnbasdoc", baspsnvo);
						HIDelegator.getPsnInf().updateRelTable(hmRelVO);
						// 更新界面数据
						if (isRelPsndoc) {
							String[] field = psnvo.getAttributeNames();
							for (int i = 0; i < field.length; i++) {
								if ("pk_psnbasdoc".equals(field[i])
										|| "pk_psndoc".equals(field[i])) {
									continue;
								}
								BillItem item = getCard().getHeadItem(field[i]);
								if (item != null) {
									Object value = psnvo
											.getFieldValue(field[i]);
									if (value == null
											|| "#null#"
													.equals(value.toString())) {
										item.setValue(null);
										value = null;
									} else {
										item.setValue(value);
									}
									psnList[listSelectRow].setAttributeValue(
											field[i], value);
								}
							}
						}
						if (isRelBasPsndoc) {
							String[] field = baspsnvo.getAttributeNames();
							for (int i = 0; i < field.length; i++) {
								if ("pk_psnbasdoc".equals(field[i])
										|| "pk_psndoc".equals(field[i])) {
									continue;
								}
								BillItem item = getCard().getHeadItem(field[i]);
								if (item != null) {
									Object value = baspsnvo
											.getFieldValue(field[i]);
									if (value == null
											|| "#null#"
													.equals(value.toString())) {
										item.setValue(null);
										value = null;
									} else {
										item.setValue(value);
									}
									psnList[listSelectRow].setAttributeValue(
											field[i], value);
								}
							}
						}
					}
					isRelateToAcc = updated;
					// 设置按钮状态
					setChildButtonState();
					updateButtons();
					setEditLineNo(-1);
					// 删除选中行
					getCard().delLine();
				} else {
					if (!isEditSub()) {
						setEditSub(true);
						// 设置单据模板表体页签不能切换
						getCard().getBodyTabbedPane().setEnabled(false);
						getCard().getBodyPanel().getTableModel().updateValue();
					}
					String pk_psndoc_sub = (String) vo
							.getAttributeValue("pk_psndoc_sub");
					if (pk_psndoc_sub != null
							&& pk_psndoc_sub.trim().length() > 0) {
						// 保存删除子记录主键
						vDelPkPsndocSub.addElement(pk_psndoc_sub);
						//
						vDelSubVOs.addElement(vo);
					}
					setEditLineNo(-1);
					// 删除选中行
					getCard().delLine();
					// 设置按钮状态
					setMultEditChildButtonState();
				}
			} else {
				freeLockPsn(person.getPk_psndoc() + getBodyTableCode());
				return;
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000078")/*
													 * @res "删除失败！"
													 */
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
							"UPP600700-000358")/*
												 * @res "\n原因如下：\n"
												 */
					+ e.getMessage());
			freeLockPsn(person.getPk_psndoc() + getBodyTableCode());
		} finally {
			freeLockPsn(person.getPk_psndoc() + getBodyTableCode());
		}
	}

	/**
	 * 下载按钮响应事件处理函数。人员信息记录档案子集使用
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onDownload() throws java.lang.Exception {
		// 获得当前子表（附加文档）的所有记录
		CircularlyAccessibleValueObject[] records = person
				.getTableVO(getBodyTableCode());
		int selRow = getCard().getBillTable().getSelectedRow();
		if (selRow < 0 || selRow >= records.length) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000079")/*
													 * @res "请选择一条记录！"
													 */);
			return;
		}
		// 获得当前记录的文档
		String remotePath = (String) records[selRow]
				.getAttributeValue("vdocloc");
		// 检查是否有文档
		if (remotePath != null) {
			// 显示保存文件对话框
			UIFileChooser fileChooser = new UIFileChooser();
			fileChooser.showSaveDialog(this);
			// 取得保存文件
			File file = fileChooser.getSelectedFile();
			// 取消
			if (file == null) {
				return;
			}
			// 下载文件到本地文件中
			FileTransClient.download(remotePath, file.getCanonicalPath());
		}
	}

	/**
	 * 
	 * @param isEnabled
	 */
	private void setBillTempletState(boolean isEnabled) {
		if (getEditType() == EDIT_MAIN) {
			if (isEnabled) {
				setBillItemEditable();
			}
			getCard().setEnabled(isEnabled);
			getCard().getBillModel(getCurrentSetCode()).setEnabled(false);
		} else if (getEditType() == EDIT_SUB) {// 子集操作
			getCard().setEnabled(false);
			getCard().getBillModel(getCurrentSetCode()).setEnabled(isEnabled);
		} else if (getEditType() == EDIT_REF) {
			getCard().setEnabled(isEnabled);
		} else if (getEditType() == EDIT_RETURN) {
			getCard().setEnabled(isEnabled);
		}

	}

	/**
	 * 编辑操作按钮响应事件处理函数。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onEdit() throws java.lang.Exception {
		// V56增加，校验是否存在未审核的（人员变动）单据。
		if (listSelectRow != -1) {
			String pk_psndoc = (String) psnList[listSelectRow]
					.getAttributeValue("pk_psndoc");
			HIDelegator.getPsnInf().checkIfExistsUnAuditBillofPSN(
					new String[] { pk_psndoc }, "maintable");
		}
		// 
		if (getBillItem("bd_psndoc.amcode") != null)
			getBillItem("bd_psndoc.amcode").setLength(10);// 防止超过数据库中的长度
		if (!isAdding()) { // 编辑,加锁
			if (listSelectRow != -1) {
				loadPsnInfo(listSelectRow);// 装载 当前行人员数据
			}
			if ("600707".equalsIgnoreCase(getModuleName())) {
				String sql = " select 1 from hi_psndoc_keypsn where pk_psndoc ='"
						+ person.getPk_psndoc() + "'and enddate is null";
				boolean iskeypsn = HIDelegator.getPsnInf().isRecordExist(sql);
				if (iskeypsn && !caneditkeypsn) {
					showWarningMessage(nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("600707", "UPT600707-000239")/*
																		 * @res
																		 * "该人员为关键人员,请到关键人员管理节点维护其信息！"
																		 */);
					return;
				}
			}
			if (!lockPsn(person.getPk_psndoc())) {
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600704", "UPP600704-000073")/*
														 * @res
														 * "该人员正在被其他用户操作，请稍后再试！"
														 */);
				return;
			}
			if ("600704".equalsIgnoreCase(getModuleName())) {
				String isreturn = (String) psnList[listSelectRow]
						.getAttributeValue("isreturn");
				if ("Y".equalsIgnoreCase(isreturn)) {// &&
					// "600704".equalsIgnoreCase(getModuleName()
					changePsncl(null);
				}
			}
			setEditState(true);
			isUpdated = true;
		}
		if (isEmployeeRef()) {// 人员引用
			setEditType(EDIT_REF);
			switchButtonGroup(EMPLOYEE_REF_CARD);
			setButtonGroup(EMPLOYEE_REF_CARD);
			setButtonsState(EMPLOYEE_REF_EDIT);
			setEditState(true);
			setHeadBillItemEditable("bd_psnbasdoc", false);
		} else {
			// 如果当前不是卡片界面，则进入卡片界面后表头表体默认显示
			if (getButtonGroup() != CARD_GROUP) {
				getCard().setPosMaximized(-1);
			}
			switchButtonGroup(CARD_GROUP);
			setButtonGroup(CARD_GROUP);
			setButtonsState(CARD_EDIT);
			setEditType(EDIT_MAIN);// 设置编辑类型为编辑主信息集
		}
		// 设置某些项目的可编辑性
		if (nc.ui.hi.hi_301.Util.isDutyDependJobSeries) {
			UIRefPane dutyRef = null;
			UIRefPane jobRef = null;
			BillItem item = getBillItem("bd_psndoc.dutyname");
			if (item != null) {
				dutyRef = (UIRefPane) (item.getComponent());
			}
			jobRef = (UIRefPane) getBillItem("bd_psndoc.pk_om_job")
					.getComponent();
			String oldWhere = dutyRef.getRefModel().getWherePart();
			if (jobRef != null) {
				String pk_jobseries = (String) jobRef
						.getRefValue("om_job.jobseries");
				if (oldWhere.indexOf("and duty.series ='") >= 0) {
					oldWhere = oldWhere.substring(0, oldWhere
							.indexOf("and duty.series ='"));
				}
				if (pk_jobseries != null) {
					String newSql = oldWhere + " and duty.series ='"
							+ pk_jobseries.trim() + "'";
					dutyRef.getRefModel().setWherePart(newSql);
				} else {
					dutyRef.getRefModel().setWherePart(oldWhere);
				}
			}
		}
		// ----shien 2010-11-29----
		// 根据V57档案管理需求需在人员个人信息集中增加下面两个字段，修改相关的系统预置模板，
		// 安装了档案管理模块后，下面两个字段不可以修改。
		if (nc.ui.hr.utils.Util.isModuleInstalled(Global.getCorpPK(), "HRPF")) {
			// 管档机构
			BillItem item_pk_fileorg = getBillItem("bd_psnbasdoc.pk_fileorg");
			// 档案号
			BillItem item_file_code = getBillItem("bd_psnbasdoc.file_code");
			// 加载不能修改
			if (item_pk_fileorg != null) {
				item_pk_fileorg.setEdit(false);
			}
			if (item_file_code != null) {
				item_file_code.setEdit(false);
			}
		}
		// ----end shien 2010-11-29----

		if (isAdding()) {
			// 当前选中的节点
			DefaultMutableTreeNode node = getSelectedNode();
			if (node == null) {
				return;
			}
			CtrlDeptVO dept = getSelectedDept();// 当前选中的部门
			// 人员采集自动编码 取人员编码
			retrivePsncodeInGroup();

			if ("Y".equalsIgnoreCase(isUniquePsncodeInGroup)) {// 如果是集团内唯一，则按集团产生人员编码
				// 根据人员采集方式设置编码
				if ((getPsncodeAutoGenerateParam() != null)
						&& getPsncodeAutoGenerateParam().trim().equals("1")) {
					if (intodocdirect == 0) {
						autopsncode = HIDelegator.getBillcodeRule()
								.getBillCode_RequiresNew("BS", "0001", null,
										getObjVO());
						getBillItem("bd_psndoc.psncode").setValue(autopsncode);
					} else if (intodocdirect == 1) {
						autopsncode = "";
						getBillItem("bd_psndoc.psncode").setValue(autopsncode);
					}
				}
			} else {
				if ((getPsncodeAutoGenerateParam() != null)
						&& getPsncodeAutoGenerateParam().trim().equals("1")) {
					if (intodocdirect == 0) {
						autopsncode = HIDelegator.getBillcodeRule()
								.getBillCode_RequiresNew("BS",
										Global.getCorpPK(), null, getObjVO());
						getBillItem("bd_psndoc.psncode").setValue(autopsncode);
					} else if (intodocdirect == 1) {
						autopsncode = "";
						getBillItem("bd_psndoc.psncode").setValue(autopsncode);
					}
				}
			}
			// 如果选中的是根结点（公司），则将要显示的列表人员是所有查询结果
			if (!node.isRoot() && isAdding()
					&& dept.getNodeType() == CtrlDeptVO.DEPT) {
				if (dept.isControlled()) {
					BillItem item = getBillItem("bd_psndoc.pk_deptdoc");
					UIRefPane deptRef = (UIRefPane) item.getComponent();
					deptRef.setAutoCheck(true);
					deptRef.setPK(dept.getPk_dept());
					// 设置岗位参照
					UIRefPane job = (UIRefPane) getBillItem(
							"bd_psndoc.pk_om_job").getComponent();
					nc.ui.bd.ref.AbstractRefModel refModel = job.getRefModel();
					nc.ui.hi.hi_301.ref.JobRef jobRef;
					if (refModel instanceof nc.ui.hi.hi_301.ref.JobRef) {
						jobRef = new nc.ui.hi.hi_301.ref.JobRef();
						nc.ui.hi.hi_301.ref.JobRef oldRef = (nc.ui.hi.hi_301.ref.JobRef) refModel;
						jobRef.setPk_deptdoc(oldRef.getPk_deptdoc());
					} else {
						jobRef = new nc.ui.hi.hi_301.ref.JobRef();
					}
					jobRef.setPk_deptdoc(dept.getPk_dept());
					job.setRefModel(jobRef);
					job.setPK("");
					job.setText("");
				}
			}
		} else {// wangkf add --修改时，更新岗位的参照。
			String pk_deptdoc = (String) person.getPsndocVO()
					.getAttributeValue("pk_deptdoc");
			String pk_om_job = (String) person.getPsndocVO().getAttributeValue(
					"pk_om_job");
			UIRefPane job = (UIRefPane) getBillItem("bd_psndoc.pk_om_job")
					.getComponent();
			nc.ui.bd.ref.AbstractRefModel refModel = job.getRefModel();
			nc.ui.hi.hi_301.ref.JobRef jobRef;
			if (refModel instanceof nc.ui.hi.hi_301.ref.JobRef) {
				jobRef = new nc.ui.hi.hi_301.ref.JobRef();
				nc.ui.hi.hi_301.ref.JobRef oldRef = (nc.ui.hi.hi_301.ref.JobRef) refModel;
				jobRef.setPk_deptdoc(oldRef.getPk_deptdoc());
			} else {
				jobRef = new nc.ui.hi.hi_301.ref.JobRef();
			}
			jobRef.setPk_deptdoc(pk_deptdoc);
			job.setRefModel(jobRef);
			job.setPK(pk_om_job);
		}
		setBillTempletState(true);

		getCard().transferFocusTo(0);
	}

	/**
	 * 
	 * @throws BusinessException
	 */
	private void retrivePsncodeInGroup() throws BusinessException {
		// 效率优化
		// isUniquePsncodeInGroup =
		// PubDelegator.getIParValue().getParaString("0001", "HI_CODEUNIQUE");
		isUniquePsncodeInGroup = getPara("HI_CODEUNIQUE");
		if (isUniquePsncodeInGroup == null) {
			isUniquePsncodeInGroup = "Y";
		}
	}

	/**
	 * 插入行按钮响应事件处理函数。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onInsertChild() throws java.lang.Exception {
		// V56增加，校验是否存在未审核的（人员变动）单据。
		HIDelegator.getPsnInf().checkIfExistsUnAuditBillofPSN(
				new String[] { person.getPk_psndoc() }, getBodyTableCode());

		if (!isEditSub()) {
			if (!lockPsn(person.getPk_psndoc() + getBodyTableCode())) {
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600704", "UPP600704-000070")/*
														 * @res "该人员的"
														 */
						+ getBodyTableName()
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000071")/*
													 * @res "子集正在有其他用户操作，请稍后再试！"
													 */);
				return;
			}
		}

		// 设置单据模板表体页签不能切换
		getCard().getBodyTabbedPane().setEnabled(false);
		// 正在编辑子表
		setEditType(EDIT_SUB);
		// 不是在修改行
		setEditLine(false);
		// 设置正在编辑行号
		int row = getCard().getBillTable().getSelectedRow();
		getCard().removeEditListener(getBodyTableCode());
		getCard().getBodyPanel().insertLine(row, 1);
		setEditLineNo(row);
		GeneralVO copyVO = setSelRowToNewRow(row, row);
		// 设置单据模板为可编辑状态
		setBillTempletState(true);
		// 为当前子表插入一行空记录
		SubTable subtable = person.getSubTable(getCard().getBodyTableCode(
				getCard().getBodyPanel()));
		subtable.getRecords().insertElementAt(copyVO, row);// wangkf
		getCard().removeEditListener(getBodyTableCode());
		// setSelRowRefValueNull(getBodyTableCode());
		// 劳动合同设置默认值
		if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_ctrt")) {
			Vector gvos = subtable.getRecords();
			GeneralVO infoVO = (GeneralVO) gvos.elementAt(row);
			infoVO
					.setAttributeValue("icontstate", nc.ui.ml.NCLangRes
							.getInstance().getStrByID("600704",
									"UPP600704-000050")/* @res "正常" */);
			infoVO.setAttributeValue(Util.PK_PREFIX + "icontstate",
					new Integer(2));
			infoVO
					.setAttributeValue("iconttype", nc.ui.ml.NCLangRes
							.getInstance().getStrByID("600704",
									"UPP600704-000072")/* @res "签订" */);
			infoVO.setAttributeValue(Util.PK_PREFIX + "iconttype", new Integer(
					1));
			getCard().getBodyPanel().getTableModel().setBodyDataVO(
					(CircularlyAccessibleValueObject[]) gvos
							.toArray(new CircularlyAccessibleValueObject[0]));
		}
		if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_dimission")
				|| "hi_psndoc_retire".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_training".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_orgpsn".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_ass".equalsIgnoreCase(getCurrentSetCode())) {
			getCard().getBodyPanel().getTableModel().setBodyDataVO(
					(CircularlyAccessibleValueObject[]) subtable.getRecords()
							.toArray(new CircularlyAccessibleValueObject[0]));

		}
		// 任职情况设置默认值 wangkf add
		if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_deptchg")) {
			Vector gvos = subtable.getRecords();
			GeneralVO infoVO = (GeneralVO) gvos.elementAt(row);
			infoVO.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			infoVO.setAttributeValue(Util.PK_PREFIX + "pk_corp", corpPK);
			getCard().getBodyPanel().getTableModel().setBodyDataVO(
					(CircularlyAccessibleValueObject[]) gvos
							.toArray(new CircularlyAccessibleValueObject[0]));
			if (nc.ui.hi.hi_301.Util.isDutyDependJobSeries) {
				UIRefPane dutyRef = null;
				UIRefPane jobRef = null;
				BillItem item = getBillItem("hi_psndoc_deptchg.pk_om_duty");
				if (item != null) {
					dutyRef = (UIRefPane) (item.getComponent());
				}
				jobRef = (UIRefPane) getBillItem("hi_psndoc_deptchg.pk_postdoc")
						.getComponent();
				String oldWhere = dutyRef.getRefModel().getWherePart();
				if (jobRef != null) {
					String pk_jobseries = (String) jobRef
							.getRefValue("om_job.jobseries");
					if (oldWhere.indexOf("and duty.series ='") >= 0) {
						oldWhere = oldWhere.substring(0, oldWhere
								.indexOf("and duty.series ='"));
					}
					if (pk_jobseries != null) {
						String newSql = oldWhere + " and duty.series ='"
								+ pk_jobseries.trim() + "'";
						dutyRef.getRefModel().setWherePart(newSql);
					} else {
						dutyRef.getRefModel().setWherePart(oldWhere);
					}
				}
			}
		}

		// modified by zhangdd 处理子表的参照问题
		setUnEditable();

		// 
		if (isNotMultiEdit()) {
			// 设置按钮状态
			setInsertChildButtonState();
			// 设置当前行可编辑
			enableTableLine(getEditLineNo());
		} else {
			if (!isEditSub()) {
				setEditSub(true);
				getCard().getBodyPanel().getTableModel().updateValue();
			}
			// 设置按钮状态
			setMultEditChildButtonState();
		}
	}

	/**
	 * 是否超编
	 * 
	 * @param intoDocData
	 * @return
	 * @throws Exception
	 */
	protected boolean isExceedWorkout(GeneralVO[] intoDocData) throws Exception {
		boolean isExceedWorkout = true;

		if (!nc.ui.hr.utils.Util.isModuleInstalled(Global.getCorpPK(), "LB"))
			return true;
		// 单位编制和子类编制
		String corpMsg = "";
		String pk_corp = getClientEnvironment().getCorporation()
				.getPrimaryKey();
		Vector v = new Vector();
		for (int i = 0; i < intoDocData.length; i++) {
			String pk = (String) intoDocData[i].getFieldValue("pk_psndoc");
			v.addElement(pk);
		}
		String[] pk_psndocs = null;
		if (v.size() > 0) {
			pk_psndocs = new String[v.size()];
			v.copyInto(pk_psndocs);
		}
		ICorpWorkout corpwork = (ICorpWorkout) NCLocator.getInstance().lookup(
				ICorpWorkout.class.getName());
		WorkoutResultVO result = corpwork.checkWorkout(pk_corp, Global
				.getUserID(), Global.getLogYear(), pk_psndocs);
		if (result != null) {
			corpMsg = result.getMessage();
			boolean lock = result.isSyncLock();
			boolean pass = result.isPassTest();
			boolean iscorpoverout = result.isCorpOverWorkout();
			boolean isdeptoverout = result.isDeptOverWorkout();
			boolean isjoboverout = result.isJobOverWorkout();
			if (!lock) {
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600705", "UPP600705-000018")/*
														 * @res
														 * "编制管理正有其他用户操作，请稍后再试！"
														 */);
				isExceedWorkout = false;
				return isExceedWorkout;
			}
			if (!pass) {
				// showWarningMessage(corpMsg+"\n 不允许新进人员!");
				showWarningMessage(corpMsg
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
								"UPP600700-000118")/* @res "\n 不允许新进人员!" */);
				isExceedWorkout = false;
				return isExceedWorkout;
			}

			String msg = "";
			if (corpMsg != null && !"".equalsIgnoreCase(corpMsg)) {
				msg += corpMsg;
			}
			if (!"".equalsIgnoreCase(msg)
					&& (iscorpoverout || isdeptoverout || isjoboverout)) {
				String title = nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600722", "UPP600722-000036")/* @res "提示" */;
				String question = msg
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000199")/* @res "是否转入人员档案" */;
				int operatestate = MessageDialog.showYesNoDlg(this, title,
						question);
				if (operatestate == MessageDialog.ID_YES
						|| operatestate == MessageDialog.ID_OK) {
					// isExceedWorkout = true;
					if (!"".equalsIgnoreCase(corpMsg) && iscorpoverout) {// 只有公司超编才发消息
						sendMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"600704", "UPP600704-000236")/*
																 * @res "超编提示"
																 */, corpMsg);
					}
				} else {
					isExceedWorkout = false;
				}
			}
		}
		return isExceedWorkout;
	}

	/**
	 * 
	 * @param title
	 * @param msg
	 */
	protected void sendMsg(String title, String msg) {
		nc.vo.pub.msg.UserNameObject[] userRecievers = null;
		String[] keys = null;
		try {
			// 效率优化
			// String recieverPks =
			// PubDelegator.getIParValue().getParaString(Global.getCorpPK(),
			// "HI_MESSAGE");
			String recieverPks = getPara("HI_MESSAGE");
			if (recieverPks != null) {
				keys = getReceivePk(recieverPks);
				userRecievers = HIDelegator.getPsnInf().getUserObj(keys);
				nc.vo.pub.msg.CommonMessageVO msgVo = new nc.vo.pub.msg.CommonMessageVO();
				msgVo.setActionType("MSN");
				msgVo.setTitle(title);
				msgVo.setMessageContent(msg);
				msgVo.setReceiver(userRecievers);
				msgVo.setSendDataTime(Global.getServerTime());
				msgVo.setSender(Global.getUserID());
				HIDelegator.getPFMessage().insertCommonMsg(msgVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			reportException(e);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param title
	 * @param msg
	 * @param userRecievers
	 */
	public void sendMsg(String title, String msg,
			nc.vo.pub.msg.UserNameObject[] userRecievers) {
		try {
			nc.vo.pub.msg.CommonMessageVO msgVo = new nc.vo.pub.msg.CommonMessageVO();
			msgVo.setTitle(title);
			msgVo.setActionType("MSN");
			msgVo.setMessageContent(msg);
			msgVo.setReceiver(userRecievers);
			msgVo.setSendDataTime(Global.getServerTime());
			msgVo.setSender(Global.getUserID());
			HIDelegator.getPFMessage().insertCommonMsg(msgVo);
		} catch (Exception e) {
			e.printStackTrace();
			reportException(e);
		}
	}

	/**
	 * v50 add
	 * 
	 * @param pks
	 * @return
	 */
	private String[] getReceivePk(String pks) {
		String[] recievepks = null;
		recievepks = pks.split(",");
		return recievepks;
	}

	/**
	 * 
	 * @param intoDocData
	 * @throws java.lang.Exception
	 */
	private void intoDoc(GeneralVO[] intoDocData) throws java.lang.Exception {
		if (intoDocData == null || intoDocData.length < 1)
			return;
		Vector vPkPsn = new Vector();

		if ((getPsncodeAutoGenerateParam() != null)
				&& getPsncodeAutoGenerateParam().trim().equals("0")) {
			checkPsnListPsnCode(intoDocData);
		} else {
			autoSetInDocPsnCode(intoDocData);
		}
		checkPsnListDept(intoDocData);
		if (isExceedWorkout(intoDocData) && intoDocData != null) {// V35 add
			// 效率优化start 将循环加锁移到服务端
			for (int i = 0; i < intoDocData.length; i++) {
				String pk = (String) intoDocData[i].getFieldValue("pk_psndoc");
				// if (!lockPsn(pk)) {
				// showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
				// "UPP600704-000082")/*
				// * @res
				// * "当前列表中的人员:"
				// */
				// + intoDocData[i].getAttributeValue("psnname").toString()
				// + nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
				// "UPP600704-000083")/*
				// * @res
				// * "正在被其他用户操作，操作失败"
				// */);
				// for (int k = 0; k < v.size(); k++) {
				// freeLockPsn((String) v.elementAt(k));
				// }
				// return;
				// }
				// String sql = " select 1 from bd_psndoc where pk_psndoc
				// ='"+pk+"'and indocflag ='Y'";
				// boolean isinto = HIDelegator.getPsnInf().isRecordExist(sql);
				// if(isinto){
				// showWarningMessage(intoDocData[i].getAttributeValue("psnname").toString()+"已经转入人员档案，不能再次入职!");
				// return;
				// }
				// v.addElement(pk);
				if (!vPkPsn.contains(pk)) {// wangkf add
					vPkPsn.addElement(pk);
				}
			}
			// wangkf add------
			String[] pk_psndocs = null;
			if (vPkPsn.size() > 0) {
				pk_psndocs = new String[vPkPsn.size()];
				vPkPsn.copyInto(pk_psndocs);
			}
			HIDelegator.getPsnInf().intoDoc(intoDocData, pk_psndocs,
					Global.getUserID());
			// 效率优化end
			// 将列表中的人从查询结果中删除（已经被转入档案中了)
			deleteListFromResult(intoDocData);
			// 清除部门的人员列表缓冲
			psnDeptCache.clear();
			psnCorpCache.clear();
			// 重新选中当前部门
			deptTreeValueChanged(null);
			listSelectRow = -1;
		}

	}

	/**
	 * 
	 * @param srcData
	 * @param groupPk
	 * @return
	 */
	private HashMap groupByPk(GeneralVO[] srcData, String groupPk) {
		HashMap hm = new HashMap();
		if (srcData != null) {
			Vector vPool = new Vector();
			for (int i = 0; i < srcData.length; i++) {
				vPool.addElement(srcData[i]);
			}
			Vector vSameGroup = new Vector();
			while (vPool.size() > 0) {
				GeneralVO firstVO = (GeneralVO) vPool.elementAt(0);
				String firstKey = (String) firstVO.getAttributeValue(groupPk);
				vSameGroup.addElement(firstVO);
				for (int i = 1; i < vPool.size(); i++) {
					GeneralVO nextVO = (GeneralVO) vPool.elementAt(i);
					String nextKey = (String) nextVO.getAttributeValue(groupPk);
					if (firstKey != null && nextKey != null
							&& firstKey.equalsIgnoreCase(nextKey)) {
						vSameGroup.addElement(nextVO);
						vPool.remove(nextVO);
						i--;
					}
				}
				vPool.remove(firstVO);
				if (vSameGroup.size() > 0) {
					GeneralVO[] vos = new GeneralVO[vSameGroup.size()];
					vSameGroup.copyInto(vos);
					hm.put(firstKey, vos);
					vSameGroup.removeAllElements();
				}
			}
		}
		return hm;
	}

	/**
	 * 得到需要编制判断的数据 V35 add
	 * 
	 * @param intoDocData
	 * @return
	 */
	private GeneralVO[] getWorkoutData(GeneralVO[] intoDocData) {
		GeneralVO[] vos = null;
		Vector v = new Vector();
		if (intoDocData != null) {
			for (int i = 0; i < intoDocData.length; i++) {
				Integer intPsnclscope = (Integer) intoDocData[i]
						.getAttributeValue("psnclscope");// 只判断人员归属为在职
				if (intPsnclscope != null && intPsnclscope.intValue() == 0) {
					v.addElement(intoDocData[i]);
				}
			}
		}
		if (v.size() > 0) {
			vos = new GeneralVO[v.size()];
			v.copyInto(vos);
		}
		return vos;
	}

	/**
	 * 将当前列表中的人员转入档案
	 * 
	 * @throws java.lang.Exception
	 */
	private void onIntoDoc() throws java.lang.Exception {
		GeneralVO[] intoDocData = getSelectPsnListData();
		if (intoDocData == null || intoDocData.length < 1) {
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600710", "UPT600710-000050")/*
													 * @res
													 * "请在复选框中勾选要转入人员档案的人员!"
													 */);
			return;
		}
		intoDoc(getSelectPsnListDataOfMyCorp());
	}

	/**
	 * 查询操作。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void onQuery() {// throws java.lang.Exception

		try {
			vBookConds.removeAllElements();
			// 常见查询条件
			getQueryDlg().getUIPanelNormal().setLayout(new BorderLayout());
			// //
			// if (getModuleName().equalsIgnoreCase("600707")
			// && ((Item) getCmbPsncl().getSelectedItem()).getValue() == 0) {
			// ((NormalQueryPanel)getQueryDlg().getUIPanelNormal().getComponent(0)).getcheckIncludeReturn().setVisible(true);
			// }else{
			// ((NormalQueryPanel)getQueryDlg().getUIPanelNormal().getComponent(0)).getcheckIncludeReturn().setVisible(false);
			// }
			getQueryDialog().showModal();
			if (getQueryDialog().getResult() == UIDialog.ID_OK) {
				isQuery = true;
				// update by sunxj 2010-02-03 快速查询插件 start
				isQuickSearch = false;
				// update by sunxj 2010-02-03 快速查询插件 end
				hmSortCondition.clear();
				onRefresh();
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 刷新界面响应事件处理函数。
	 * 
	 * @throws java.lang.Exception
	 */
	public void onRefresh() {

		try {
			// update by sunxj 2010-02-03 快速查询插件 start
			// if (queryDlg == null) {
			if (queryDlg == null && !isQuickSearch) {
				return;
			}
			// update by sunxj 2010-02-03 快速查询插件 end
			// 刷新所有缓冲
			psnList = null;
			queryResult = null;
			recordcount = 0;
			person = null;
			psnDeptCache.clear();
			psnCorpCache.clear();
			persons.clear();
			// 强制回收垃圾
			System.gc();
			hmSortCondition.clear();
			listSelectRow = -1;
			// 重新查询，刷新人员列表
			performQuery();
		} catch (Exception ex) {
			reportException(ex);
			showErrorMessage(ex.getMessage());
		}
	}

	/**
	 * 编辑界面的返回按钮。
	 * 
	 * @throws java.lang.Exception
	 */
	private void onReturn() throws java.lang.Exception {
		// 直接调用返回方法
		returnToMain();
	}

	private String userpks = null;

	private UserNameObject[] userRecievers = null;

	/**
	 * 保存按钮响应事件处理函数。
	 * 
	 * @throws java.lang.Exception
	 */
	public void onSave() throws java.lang.Exception {
		String pkpsn = "";
		try {
			if (isEmployeeRef()) {// 如果是员工引用
				String[] tableCodes = new String[] { "bd_psndoc" };// 获取当前正在编辑的表
				PersonEAVO eavo = getInputData(tableCodes);// 获取界面输入
				eavo.setJobtype(person.getJobType());
				eavo.getPsndocVO().setAttributeValue("pk_corp",
						Global.getCorpPK());
				eavo.getPsndocVO().setAttributeValue("indocflag",
						new nc.vo.pub.lang.UFBoolean('Y'));
				dataNotNullValidateforRef(-1);
				validateInput(eavo, tableCodes);
				// dataUniqueValidate("bd_psnbasdoc",eavo);//主集唯一性校验 主要进行姓名的校验
				eavo.setPk_psnbasdoc(person.getPk_psnbasdoc());
				eavo.setPk_psndoc(person.getPk_psndoc());
				saveHiRef(eavo);
				person = eavo;
				// String msgContent = "引用申请 人员姓名:"+
				// eavo.getPsndocVO().getAttributeValue("psnname").toString()
				// + ",人员编码:" + oldpsncode;
				String msgContent = nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600700", "UPP600700-000120")/*
																	 * @res
																	 * "引用申请
																	 * 人员姓名:"
																	 */
						+ eavo.getPsndocVO().getAttributeValue("psnname")
								.toString()
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
								"UPP600700-000121")/* @res ",人员编码:" */
						+ oldpsncode;
				// add by zhyan 2006-09-20 增加消息配置对话框
				getUserSelectDlg().setApp_pk_corp(app_pk_corp);
				getUserSelectDlg().refreshContent();
				getUserSelectDlg().setLocationRelativeTo(this);
				getUserSelectDlg().showModal();
				if (userSelectDlg.getResult() == UIDialog.ID_OK) {
					HIDelegator.getPsnInf().insertRecievers(app_pk_corp,
							getUserpks(), 1);// 引用人员消息接收人
				}
				// sendMsg("引用申请", msgContent, getUserRecievers());
				sendMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
						"UPP600700-000122")/* @res "引用申请" */
				, msgContent, getUserRecievers());
				oldpsncode = null;
				setEmployeeRef(false);
			} else {
				save();
			}
			setEditState(false);
			// 根据情况切换界面和设置按钮状态
			if (getEditType() == EDIT_SORT) {
				setSaveSortBottonState();
				// 如果正在排序，现返回主界面，在重新选择当前部门节点
				returnToMain();
				deptTreeValueChanged(null);
				if (getIsSelSet().isSelected()) {
					getUISplitPaneVertical().setDividerLocation(SPLIT_STANDARD);
					getUISplitPaneVertical().setEnabled(true);
					splitLocation = SPLIT_STANDARD;
				} else {
					getUISplitPaneVertical().setDividerLocation(SPLIT_MAX);
					getUISplitPaneVertical().setEnabled(true);
					splitLocation = SPLIT_MAX;
				}
			} else if (getEditType() == EDIT_MAIN) {
				if (isAdding()) {// 设置增加保存后控件编辑属性
					BillItem[] items = checkAddDlg.getAllBillItems();
					for (int i = 0; i < items.length; i++) {
						BillItem item = getCard()
								.getHeadItem(items[i].getKey());
						Object obj = htbEdit.get(items[i].getKey());
						if (obj != null) {
							item.setEdit(((Boolean) obj).booleanValue());
						} else {
							item.setEdit(false);
						}
					}
					setAdding(false);
				}
				getCard().setEnabled(false);// 设置当前单据编辑状态为不可编辑
				// 效率优化start
				if (!isAdding()) { // 修改保存解锁
					pkpsn = person.getPk_psndoc();
					// freeLockPsn(person.getPk_psndoc());
				}
				// 效率优化end
				setButtonsState(CARD_MAIN_BROWSE);
			} else if (getEditType() == EDIT_REF) {//
				getCard().setEnabled(false);// 设置当前单据编辑状态为不可编辑
				setButtonsState(EMPLOYEE_REF_CARD_BROWSE);
			} else if (getEditType() == EDIT_RETURN) {//
				setAdding(false);
				setButtonsState(CARD_MAIN_BROWSE);
			} else {
				getCard().setEnabled(false);// 设置当前单据编辑状态为不可编辑
				getCard().getBodyTabbedPane().setEnabled(true);// 恢复表体页签为可选择
				if (getEditType() == EDIT_SUB) {
					if (isEditLine()) // 子表行编辑解锁
						freeLockPsn(person.getPk_psndoc() + getBodyTableCode());
				}
				setButtonsState(CARD_MAIN_BROWSE);
				setButtonsState(CARD_CHILD_BROWSE);
			}
			if(getBillItem("bd_psndoc.clerkcode") != null) {
				getBillItem("bd_psndoc.clerkcode").setEdit(false);// 设置职员编码控件不可编辑

			}
			autopsncode = null;// wangkf add 人员编码自动生成，点取消，则编码不取号。
			isrehire = false;
			isbackrehire = false;
			getCard().getBodyTabbedPane().setEnabled(true);
		} finally {
			if (getEditType() == EDIT_MAIN) {
				if (!isAdding()) {
					freeLockPsn(pkpsn);
				}
			} else if (getEditType() == EDIT_SUB) {
				if (!isEditLine()) {
					freeLockPsn(person.getPk_psndoc() + getBodyTableCode());
				}
			}
		}
	}

	/**
	 * 添加自助用户按钮响应事件处理函数。 创建日期：(2004-5-30 13:10:36)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void onSmUser() throws java.lang.Exception {

		// 检查选中行的有效性
		int rowCount = getPsnList().getTable().getRowCount();
		if (rowCount == 0)
			return;
		// 显示添加自助用户对话框
		SmUserAddDlg smUserDlg = new SmUserAddDlg(this);
		smUserDlg.setLocationRelativeTo(this);
		smUserDlg.showModal();
	}

	/**
	 * 排序按钮操作响应事件处理函数。 创建日期：(2004-5-11 8:33:56)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected void onSort() throws java.lang.Exception {
		// 装载排序数据v50 update zhyan
		EditShowOrder();
		// 保存选中状态
		bnHistory.push(getButtons());
		// 设置按钮及状态
		setButtons(grpShoworder);
		bnSave.setEnabled(true);
		bnCancel.setEnabled(true);
		updateButtons();
		// 最大化分隔条，并不可操作
		getUISplitPane().setDividerLocation(0);
		getUISplitPane().setDividerSize(0);
		getUISplitPane().setEnabled(false);
		getUISplitPaneVertical().setDividerLocation(SPLIT_MAX);
		getUISplitPaneVertical().setEnabled(true);
		// 隐藏北部panel
		getNorthPanel().setVisible(false);
		// 设置编辑状态为排序
		setEditType(EDIT_SORT);
	}

	/**
	 * 修改行操作按钮响应事件处理函数。 创建日期：(2004-5-21 15:16:22)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected void onUpdateChild() throws java.lang.Exception {
		if ("600707".equalsIgnoreCase(getModuleName())) {
			String sql = " select 1 from hi_psndoc_keypsn where pk_psndoc ='"
					+ person.getPk_psndoc() + "'and enddate is null";
			boolean iskeypsn = HIDelegator.getPsnInf().isRecordExist(sql);
			if (iskeypsn && !caneditkeypsn) {
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600707", "UPT600707-000239")/*
														 * @res
														 * "该人员为关键人员,请到关键人员管理节点维护其信息！"
														 */);
				return;
			}
		}
		// V56增加，校验是否存在未审核的（人员变动）单据。
		HIDelegator.getPsnInf().checkIfExistsUnAuditBillofPSN(
				new String[] { person.getPk_psndoc() }, getBodyTableCode());

		if (!isEditSub()) {
			if (!lockPsn(person.getPk_psndoc() + getBodyTableCode())) {
				showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600704", "UPP600704-000070")/*
														 * @res "该人员的"
														 */
						+ getBodyTableName()
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000071")/*
													 * @res "子集正在有其他用户操作，请稍后再试！"
													 */);
				return;
			}
		}

		// wangkf add
		if (getCard().getBillTable().getSelectedRow() < 0) {
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000085")/* @res "请选择行" */);
			return;
		}
		// 设置子表页签为不可选，防止切换页签
		getCard().getBodyTabbedPane().setEnabled(false);
		// 设置编辑类型为编辑子表
		setEditType(EDIT_SUB);
		// 正在编辑行
		setEditLine(true);
		// 设置正在编辑的行号
		int row = getCard().getBillTable().getSelectedRow();
		setEditLineNo(row);
		// 设置当前单据模板可编辑
		setBillTempletState(true);
		// 获取当前子集数据
		CircularlyAccessibleValueObject[] records = person
				.getTableVO(getBodyTableCode());
		GeneralVO vo = (GeneralVO) records[row];
		if (records != null) {
			if (subBackupVOs == null) {
				subBackupVOs = new GeneralVO[records.length];
				for (int i = 0; i < subBackupVOs.length; i++) {
					subBackupVOs[i] = (GeneralVO) records[i].clone();
				}
			} else {
				vo = subBackupVOs[row];
			}
		}
		// 目前的参照已经能够设置行数据
		getCard().getBillModel().setBodyRowVO(vo, row);
		// 
		if (isNotMultiEdit()) {
			// 设置按钮状态
			setUpdateChildButtonState();
			// 设置当前正在编辑的行
			enableTableLineModify(getEditLineNo());
		} else {
			if (!isEditSub()) {
				setEditSub(true);
				getCard().getBodyPanel().getTableModel().updateValue();
			}
			// 设置按钮状态
			setMultEditChildButtonState();
		}

		// 设置某些项目的编辑性
		String tableCode = getBodyTableCode();
		if ("hi_psndoc_deptchg".equalsIgnoreCase(tableCode)) {
			setHiDeptchgEditable(vo);
			if (nc.ui.hi.hi_301.Util.isDutyDependJobSeries) {
				UIRefPane dutyRef = null;
				UIRefPane jobRef = null;
				BillItem item = getBillItem("hi_psndoc_deptchg.pk_om_duty");
				if (item != null) {
					dutyRef = (UIRefPane) (item.getComponent());
				}
				jobRef = (UIRefPane) getBillItem("hi_psndoc_deptchg.pk_postdoc")
						.getComponent();
				String oldWhere = dutyRef.getRefModel().getWherePart();
				if (jobRef != null) {
					String pk_jobseries = (String) jobRef
							.getRefValue("om_job.jobseries");
					if (oldWhere.indexOf("and duty.series ='") >= 0) {
						oldWhere = oldWhere.substring(0, oldWhere
								.indexOf("and duty.series ='"));
					}
					if (pk_jobseries != null) {
						String newSql = oldWhere + " and duty.series ='"
								+ pk_jobseries.trim() + "'";
						dutyRef.getRefModel().setWherePart(newSql);
					} else {
						dutyRef.getRefModel().setWherePart(oldWhere);
					}
				}
			}
		} else if ("hi_psndoc_keypsn".equalsIgnoreCase(tableCode)) {
			setHiKeyPsnEditable(vo);
		}
	}

	/**
	 * 上传人员的附加文档按钮响应处理事件函数。 创建日期：(2004-5-25 16:30:05)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected void onUpload() throws java.lang.Exception {
		if (listSelectRow < 0) {
			return;
		}
		// 当前选择人员主键
		String pk_psnbasdoc = (String) psnList[listSelectRow]
				.getAttributeValue("pk_psnbasdoc");

		// 检查是不是关键人员
		boolean caneditattachment = true;
		if ("600707".equalsIgnoreCase(getModuleName())) {
			String sql = " select 1 from hi_psndoc_keypsn where pk_psndoc ='"
					+ (String) psnList[listSelectRow]
							.getAttributeValue("pk_psndoc")
					+ "'and enddate is null";
			boolean iskeypsn = HIDelegator.getPsnInf().isRecordExist(sql);
			if (iskeypsn && !caneditkeypsn) {
				caneditattachment = false;
			}
		}

		// 选择本地文件
		AttachmentDialog attdlg = new AttachmentDialog(this, pk_psnbasdoc,
				"bd_psndoc");
		// 当前“修改”按钮若是可用，则附件可修改，否则只能查看。
		if (!bnEdit.isEnabled() || !caneditattachment)
			attdlg.setFileBtnUsable(7);
		attdlg.showModal();
	}

	/**
	 * 根据查询对话框的条件、部门权限、人员范围查询人员列表。 创建日期：(2004-5-13 11:42:59)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void performQuery() throws java.lang.Exception {
		// 设置鼠标忙
		Frame thisFrame = Util.getTopFrame(this);
		try {
			if (thisFrame != null)
				thisFrame.setCursor(Cursor
						.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// 选中根节点
			TreePath path = getDeptTree().getSelectionPath();
			if (path == null) {// !isRoot(path) ||
				// 如果当前没有选中或者选中节点不是根结点则选择根结点
				Object root = getDeptTree().getModel().getRoot();
				getDeptTree().setSelectionPath(
						new TreePath(new Object[] { root }));
			} else {
				getDeptTree().setSelectionPath(path);
				// 刷新根结点的选中事件
				deptTreeValueChanged(null);
			}
		} finally { // 设置鼠标为缺省
			if (thisFrame != null)
				thisFrame.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	protected int listSelectRow = -1;// 列表中选中的当前行

	/**
	 * 人员列表选中事件。 创建日期：(2004-5-13 15:53:49)
	 * 
	 * @param event
	 *            javax.swing.event.ListSelectionEvent
	 */
	private void psnListValueChange(ListSelectionEvent event) {
		try {
			// V35 加载人员的子集信息
			listSelectRow = getPsnList().getTable().getSelectedRow();
			String tableCode = getBodyTableCode();
			getCard().setBodyMenuShow(tableCode, false);
			person = null;
			if (getIsSelSet().isSelected()) {
				person = loadPsnChildInfo(listSelectRow, tableCode);

			}
			// V35 界面状态变化：1、按钮
			setButtonsState(LIST_BROWSE);
			// 将当前选中的人的姓名放在状态栏上
			setPsnNameOnBottom();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询人员列表。 创建日期：(2004-6-1 11:02:41)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected void queryResult() throws java.lang.Exception {
		vBookConds.clear();

		// 当前登录公司
		String selectcorp = getTreeSelCorpPK();
		ConditionVO[] cvos = conditions;
		// Integer recordcounts = PubDelegator.getIParValue().getParaInt("0001",
		// "HI_MAXLINE");//效率优化
		Integer recordcounts = Integer.parseInt(getPara("HI_MAXLINE"));

		recordcounts = (recordcounts == null ? new Integer(1000) : recordcounts);
		// 按照人员采集条件查询
		// wangkf add 把性别 的英文/繁体 改成 简体中文
		if (cvos != null) {
			for (int c = 0; c < cvos.length; c++) {
				if (cvos[c] != null
						&& "bd_psnbasdoc.sex".equals(cvos[c].getFieldCode())
						&& "male".equals(cvos[c].getValue())) {
					cvos[c].setValue("男");
				} else if (cvos[c] != null
						&& "bd_psnbasdoc.sex".equals(cvos[c].getFieldCode())
						&& "female".equals(cvos[c].getValue())) {
					cvos[c].setValue("女");
				} else if (cvos[c] != null
						&& "hi_psndoc_ctrt.iconttype".equals(cvos[c]
								.getFieldCode())) {
					int index = cvos[c].getComboIndex();
					if (index > 1) {
						index++;
					}
					cvos[c].setValue(Integer.toString(index));
				}
			}
		}
		wheresql = "";
		// 根据树选择节点以及是否包含下级人员添加查询条件
		if (getSelectedNode() != null) {

			CtrlDeptVO deptvo = (CtrlDeptVO) (getSelectedNode().getUserObject());
			// 如果选中的节点是部门节点需要添加部门条件
			if (deptvo.getNodeType() == CtrlDeptVO.DEPT) {
				// 若是包含下级部门
				if (getincludeChildDeptPsn().isSelected()) {
					// 
					wheresql += " and bd_psndoc.pk_deptdoc in ";
					wheresql += " ( select pk_deptdoc from bd_deptdoc where pk_corp = '";
					wheresql += selectcorp;
					wheresql += "' and innercode like '"
							+ deptvo.getInnercode().trim() + "%') ";
				} else {
					wheresql += " and bd_psndoc.pk_deptdoc = '"
							+ deptvo.getPk_dept() + "' ";
				}
			}
		}
		if (!getincludeCancleDept().isSelected()) {
			wheresql += " and (bd_deptdoc.hrcanceled = 'N' or bd_psndoc.pk_deptdoc is null)";
		}
		recordcount = 0;

		String DLGwheresql = getQueryDialog().getWhereSQL();
		HashSet<String> hstables = getQueryDialog().getUsedTableName();
		String[] DLGtables = hstables.toArray(new String[0]);
		tablesFromQueryDLG = DLGtables;
		// 获取排序字段。
		// 目前使用后台排序。需将order by传入，并且要保证其中的字段is selected.
		String orderbyclause = getConfigDialog().getOrderStr();
		orderbyclause = orderbyclause.replaceAll(
				"bd_psnbasdoc.approveflagname", "bd_psnbasdoc.approveflag");
		Vector<Attribute> orderfields = getConfigDialog().getSortingFields();
		// 下一版本建议全部用别名：tablename__fieldname，就不会出现重复

		String selectfieldclause = getListField();
		for (int i = 0; i < orderfields.size(); i++) {
			if (selectfieldclause.indexOf(orderfields.get(i).toString()) < 0) {
				// 加systemsort别名是为了防止字段层面的重名。
				// 如bd_corp.showorder，bd_deptdoc.showorder。在按行查询时，在外层会包一select *
				// ....都是showorder，sql会报错。
				selectfieldclause += (","
						+ orderfields.get(i).getAttribute().getCode()
						+ " as systemsort" + i);
			}
		}
		selectfieldclause = selectfieldclause.replaceAll(
				"bd_psnbasdoc.approveflagname", "bd_psnbasdoc.approveflag");

		// v50 update 不用公司循环查询,支持集团内人员排序
		if (selectcorp != null && selectcorp.length() > 0) {
			recordcount += HIDelegator.getPsnInf()
					.queryRecordCountByCondition_Collect(false, selectcorp,
							DLGwheresql, DLGtables, Global.getCorpPK(),
							Global.getUserID(), wheresql,
							GlobalTool.getFuncParserWithoutWa(),
							getincludeChildDeptPsn().isSelected());
			GeneralVO[] result = HIDelegator.getPsnInf()
					.queryByCondition_Collect(false, selectcorp, DLGwheresql,
							DLGtables, Global.getCorpPK(), Global.getUserID(),
							selectfieldclause, wheresql,
							recordcounts.intValue(),
							GlobalTool.getFuncParserWithoutWa(), orderbyclause,
							getincludeChildDeptPsn().isSelected());
			queryResult = result;
		} else {
			queryResult = new GeneralVO[0];
		}
	}

	/**
	 * 返回主界面。 创建日期：(2004-5-10 19:59:56)
	 */
	protected void returnToMain() {
		// 如果“排序”字段本来在主界面种没有，则要隐藏。
		if (!showordervisible)
			getPsnList().hideTableCol("showorder");
		// 获取历史按钮并恢复
		ButtonObject[] buttons = (ButtonObject[]) bnHistory.pop();
		setButtons(buttons);
		updateButtons();
		if (getEditType() != EDIT_SORT) {
			// 返回主界面
			showPanel(getUISplitPane().getName());
		} else {
			// 最大化分隔条，并不可操作
			getUISplitPane().setDividerLocation(150);
			getUISplitPane().setDividerSize(4);
			getUISplitPane().setEnabled(true);
			getNorthPanel().setVisible(true);
		}
		// 不处于编辑状态
		setEditType(EDIT_NONE);
		// 设置当前界面显示状态为显示主界面
		isShowBillTemp = false;
	}

	/**
	 * 按类型保存数据。 创建日期：(2004-5-14 16:03:12)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void save() throws java.lang.Exception {
		// 排序保存
		if (getEditType() == EDIT_SORT) {
			saveShowOrder();// v50 add
			return;
		}
		// 操作界面之后的保存
		String[] tableCodes = getEditingTables();// 获取当前正在编辑的表
		PersonEAVO eavo = getInputData(tableCodes);// 获取界面输入
		if (getEditType() == EDIT_MAIN || getEditType() == EDIT_RETURN) {
			if (isAdding()) {
				eavo.setJobtype(0);
			} else {
				eavo.setJobtype(person.getJobType());
			}
			// 非空校验
			dataNotNullValidate(tableCodes[0], null, -1);
			// 
			dataUniqueValidate("bd_psnbasdoc", eavo);// 主集唯一性校验

		} else {
			if (isEditSub()) {
				// 
				CircularlyAccessibleValueObject[] records = eavo
						.getTableVO(tableCodes[0]);
				GeneralVO vo = null;
				if (records != null) {
					for (int i = 0; i < records.length; i++) {
						vo = (GeneralVO) records[i];
						dataNotNullValidate(tableCodes[0],
								new GeneralVO[] { vo }, i);
						recordNotNullValidate(tableCodes[0], vo, i);
					}
				}
			} else {
				int row = getEditLineNo();
				CircularlyAccessibleValueObject[] records = eavo
						.getTableVO(tableCodes[0]);
				GeneralVO vo = (GeneralVO) records[row];
				dataNotNullValidate(tableCodes[0], new GeneralVO[] { vo }, row);
				recordNotNullValidate(tableCodes[0], vo, row);
			}
		}
		// 20100927
		if (!getCard().getBillData().execValidateFormulas())
			throw new BusinessException("ValidateFormulasValue");
		// end

		BillItem ref = getBillItem("bd_psnbasdoc.ssum");
		if (ref != null) {
			ref.getComponent().requestFocus();
		}
		eavo.getPsndocVO().setAttributeValue("psnname",
				eavo.getAccpsndocVO().getFieldValue("psnname"));
		validateInput(eavo, tableCodes);// 检查输入有效性
		if (getEditType() == EDIT_MAIN || getEditType() == EDIT_RETURN) {
			saveMain(eavo);
		} else {
			if (isEditSub()) {
				saveChilds(eavo, tableCodes[0]);
				setEditSub(false);
				vDelPkPsndocSub.clear();
				//
				vDelSubVOs.clear();
				// 重新加载数据
				loadPsnChildInfo(listSelectRow, tableCodes[0]);
			} else {
				saveChild(eavo, tableCodes[0]);
			}
			setRefBillItemDefaultValue(tableCodes[0]);
		}
		persons.clear();
	}

	/**
	 * 按类型保存数据。 创建日期：(2004-5-14 16:03:12)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void importandsave() throws java.lang.Exception {
		// 排序保存
		if (getEditType() == EDIT_SORT) {
			saveShowOrder();// v50 add
			return;
		}
		// 操作界面之后的保存
		String[] tableCodes = getEditingTables();// 获取当前正在编辑的表
		PersonEAVO eavo = getInputData(tableCodes);// 获取界面输入
		if (getEditType() == EDIT_MAIN || getEditType() == EDIT_RETURN) {
			if (isAdding()) {
				eavo.setJobtype(0);
			} else {
				eavo.setJobtype(person.getJobType());
			}
			// 非空校验
			dataNotNullValidate(tableCodes[0], null, -1);
			// 
			dataUniqueValidate("bd_psnbasdoc", eavo);// 主集唯一性校验

		} else {
			if (isEditSub()) {
				// 
				CircularlyAccessibleValueObject[] records = eavo
						.getTableVO(tableCodes[0]);
				GeneralVO vo = null;
				if (records != null) {
					for (int i = 0; i < records.length; i++) {
						vo = (GeneralVO) records[i];
						dataNotNullValidate(tableCodes[0],
								new GeneralVO[] { vo }, i);
						recordNotNullValidate(tableCodes[0], vo, i);
					}
				}
			} else {
				int row = getEditLineNo();
				CircularlyAccessibleValueObject[] records = eavo
						.getTableVO(tableCodes[0]);
				GeneralVO vo = (GeneralVO) records[row];
				dataNotNullValidate(tableCodes[0], new GeneralVO[] { vo }, row);
				recordNotNullValidate(tableCodes[0], vo, row);
			}
		}
		// 20100927
		if (!getCard().getBillData().execValidateFormulas())
			throw new BusinessException("ValidateFormulasValue");
		// end

		BillItem ref = getBillItem("bd_psnbasdoc.ssum");
		if (ref != null) {
			ref.getComponent().requestFocus();
		}
		eavo.getPsndocVO().setAttributeValue("psnname",
				eavo.getAccpsndocVO().getFieldValue("psnname"));
		validateInput(eavo, tableCodes);// 检查输入有效性
		if (getEditType() == EDIT_MAIN || getEditType() == EDIT_RETURN) {
			saveMain(eavo);
		} else {
			if (isEditSub()) {
				saveChilds(eavo, tableCodes[0]);
				setEditSub(false);
				vDelPkPsndocSub.clear();
				//
				vDelSubVOs.clear();
				// 重新加载数据
				loadPsnChildInfo(listSelectRow, tableCodes[0]);
			} else {
				saveChild(eavo, tableCodes[0]);
			}
			setRefBillItemDefaultValue(tableCodes[0]);
		}
		persons.clear();
	}

	/**
	 * 设置下拉框控件的默认选择，保证能够执行itemstatchange事件。 创建日期：(2004-5-21 8:43:21)
	 * 
	 * @param tableCode
	 *            java.lang.String 信息集表名
	 */
	private void setRefBillItemDefaultValue(String tableCode) {
		int ieditline = getEditLineNo();
		setEditLineNo(-1);
		BillItem[] items = getCard().getBillData().getBodyItemsForTable(
				tableCode);
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getDataType() == IBillItem.COMBO) {
					UIComboBox box = (UIComboBox) items[i].getComponent();
					box.setSelectedIndex(-1);
				}
			}
		}
		setEditLineNo(ieditline);
	}

	/**
	 * 保存子集操作。 创建日期：(2004-5-21 8:43:21)
	 * 
	 * @param eavo
	 *            nc.vo.hi.hi_301.PersonEAVO
	 * @param tableCode
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void saveChild(PersonEAVO eavo, String tableCode)
			throws java.lang.Exception {
		// 获取当前子集数据
		CircularlyAccessibleValueObject[] records = eavo.getTableVO(tableCode);
		String pk_psnbasdoc = eavo.getPk_psnbasdoc();
		int row = getEditLineNo();
		GeneralVO vo = (GeneralVO) records[row];
		vo.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		// 劳动合同模块加入公司主键值
		if (tableCode.equalsIgnoreCase("hi_psndoc_ctrt")) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
					nc.ui.hr.global.Global.getCorpPK());
			vo.setAttributeValue("isrefer", new UFBoolean("Y"));
			vo.setAttributeValue("pk_locusedcorp", nc.ui.hr.global.Global
					.getCorpPK());
			vo.setAttributeValue("pk_majorcorp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_majorcorp",
					nc.ui.hr.global.Global.getCorpPK());
			vo.setAttributeValue("pk_user", Global.getUserID());
			vo.setAttributeValue("operatedate", Global.getLogDate());
		}
		// v50 add
		if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_dimission")
				|| "hi_psndoc_retire".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_training".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_orgpsn".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_psnchg".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_keypsn".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
					nc.ui.hr.global.Global.getCorpPK());
		}
		// modified by zhangdd, 修改培训子集培训发起公司和培训类型保存名称的问题
		if ("hi_psndoc_training".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corptraining", Global.getCorpPK());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corptraining", Global
					.getCorpPK());
		}
		if ("hi_psndoc_ass".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corpperson", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corpperson",
					nc.ui.hr.global.Global.getCorpPK());
			Object o = vo.getAttributeValue(Util.PK_PREFIX + "pk_corpassess");
			if (o == null) {
				vo.setAttributeValue("pk_corpassess", getClientEnvironment()
						.getCorporation().getUnitname());
				vo.setAttributeValue(Util.PK_PREFIX + "pk_corpassess",
						nc.ui.hr.global.Global.getCorpPK());
				vo.setAttributeValue("pk_corp", nc.ui.hr.global.Global
						.getCorpPK());
			} else {
				vo.setAttributeValue("pk_corp", (String) o);
			}
		}
		if (tableCode.equalsIgnoreCase("hi_psndoc_part")) {
			Object objJobtype = vo
					.getAttributeValue(Util.PK_PREFIX + "jobtype");
			Integer jobtype = null;
			if (jobtype instanceof Integer) {
				jobtype = (Integer) objJobtype;
			}
			if (jobtype != null) {
				vo.setJobTypeFlag(jobtype);
			}
		}
		// 任职情况子集写入公司主键
		if (tableCode.equalsIgnoreCase("hi_psndoc_deptchg")) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
					nc.ui.hr.global.Global.getCorpPK());
			if (((Integer) vo.getAttributeValue("recordnum")).intValue() == 0) {
				for (int i = 0; i < records.length; i++) {
					GeneralVO vo2 = (GeneralVO) records[i];
					if (((Integer) vo2.getAttributeValue("recordnum"))
							.intValue() == 1) {
						if (vo2.getAttributeValue("enddate") == null
								|| "".equals(vo2.getAttributeValue("enddate")
										.toString().trim())) {
							nc.vo.pub.lang.UFDate enddate = ((nc.vo.pub.lang.UFDate) vo
									.getAttributeValue("begindate"))
									.getDateBefore(1);
							vo2.setAttributeValue("enddate", enddate);
							getCard().getBodyPanel().getTableModel()
									.setValueAt(enddate, i, "enddate");
						}
						break;
					}
				}
			} else if (((Integer) vo.getAttributeValue("recordnum")).intValue() > 0
					&& (vo.getAttributeValue("enddate") == null || "".equals(vo
							.getAttributeValue("enddate").toString().trim()))) {// 当插入行时，把当前行的上一条记录的开始日期的前一天作为当前行的结束日期
				for (int i = 0; i < records.length; i++) {
					GeneralVO vo1 = (GeneralVO) records[i];
					if (((Integer) vo1.getAttributeValue("recordnum"))
							.intValue() + 1 == ((Integer) vo
							.getAttributeValue("recordnum")).intValue()) {
						nc.vo.pub.lang.UFDate enddate = ((nc.vo.pub.lang.UFDate) vo1
								.getAttributeValue("begindate"))
								.getDateBefore(1);
						vo.setAttributeValue("enddate", enddate);
						getCard().getBodyPanel().getTableModel().setValueAt(
								enddate, row, "enddate");
						break;
					}
				}
			}
		}
		if (vo.getAttributeValue("pk_psndoc_sub") == null) {
			// V35 add 校验字段值的重复
			if (!PubDelegator.getISetdict().checkRecordUnique(tableCode,
					(String) vo.getAttributeValue("pk_psnbasdoc"),
					getUniqueFld(tableCode), vo, null, "pk_psndoc_sub", 0)) {
				String pk_psndoc_sub = HIDelegator.getPsnInf().insertChild(
						tableCode, person.getPk_psndoc(), vo);
				// String pk_psndoc_sub =
				// HIDelegator.getPsnInf().insertChild(tableCode,
				// vo.getAttributeValue("pk_psndoc").toString(), vo);
				records[row].setAttributeValue("pk_psndoc_sub", pk_psndoc_sub);
			} else {
				FlddictVO[] fldvos = getUniqueFld(tableCode);
				String msg = getCard().getBillData().getBodyTableName(
						getCurrentSetCode())
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000197")/* @res "子集" */;
				for (int i = 0; i < fldvos.length; i++) {
					msg += fldvos[i].getFldname();
					if (i < fldvos.length - 1) {
						msg += ",";
					}
				}
				msg += ","
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000196")/* @res "组合信息不允许重复" */;
				throw new Exception(msg);
			}
		} else {
			// V35 add 校验字段值的重复
			if (!PubDelegator.getISetdict().checkRecordUnique(tableCode,
					(String) vo.getAttributeValue("pk_psnbasdoc"),
					getUniqueFld(tableCode), vo,
					(String) vo.getAttributeValue("pk_psndoc_sub"),
					"pk_psndoc_sub", 1)) {
				HIDelegator.getPsnInf().updateChild(tableCode,
						person.getPk_psndoc(), vo);
			} else {
				FlddictVO[] fldvos = getUniqueFld(tableCode);
				String msg = getCard().getBillData().getBodyTableName(
						getCurrentSetCode())
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000197")/* @res "子集" */;
				for (int i = 0; i < fldvos.length; i++) {
					msg += fldvos[i].getFldname();
					if (i < fldvos.length - 1) {
						msg += ",";
					}
				}
				msg += ","
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
								"UPP600704-000196")/* @res "组合信息不允许重复" */;
				throw new Exception(msg);
			}
		}
		person = eavo;// wangkf add
		if (records.length > 0) {
			boolean updated = isUpdateAccRel(vo.getAttributeNames(), tableCode);
			if (updated) {
				GeneralVO voTemp = new GeneralVO();
				voTemp.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				voTemp.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				GeneralVO relVO = HIDelegator.getPsnInf().updateAccRel(
						vo.getAttributeNames(), getRelationMap(), tableCode,
						voTemp);
				HashMap hmRelVO = new HashMap();
				String[] fields = relVO.getAttributeNames();
				GeneralVO psnvo = new GeneralVO();
				GeneralVO baspsnvo = new GeneralVO();
				psnvo.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				psnvo.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				baspsnvo.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				baspsnvo.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				boolean isRelPsndoc = false;
				boolean isRelBasPsndoc = false;
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].indexOf(".") < 0) {
						continue;
					}
					String table = fields[i].substring(0, fields[i]
							.indexOf("."));
					if ("bd_psndoc".equalsIgnoreCase(table)) {
						String field = fields[i].substring(fields[i]
								.indexOf(".") + 1);
						psnvo.setAttributeValue(field, relVO
								.getAttributeValue(fields[i]));
						person.getPsndocVO().setAttributeValue(field,
								relVO.getAttributeValue(fields[i]));
						isRelPsndoc = true;
					} else if ("bd_psnbasdoc".equalsIgnoreCase(table)) {
						String field = fields[i].substring(fields[i]
								.indexOf(".") + 1);
						baspsnvo.setAttributeValue(field, relVO
								.getAttributeValue(fields[i]));
						person.getAccpsndocVO().setAttributeValue(field,
								relVO.getAttributeValue(fields[i]));
						isRelBasPsndoc = true;
					}
				}
				if (isRelPsndoc)
					hmRelVO.put("bd_psndoc", psnvo);
				if (isRelBasPsndoc)
					hmRelVO.put("bd_psnbasdoc", baspsnvo);
				HIDelegator.getPsnInf().updateRelTable(hmRelVO);
				// 更新界面数据
				if (isRelPsndoc) {
					String[] field = psnvo.getAttributeNames();
					for (int i = 0; i < field.length; i++) {
						if ("pk_psnbasdoc".equals(field[i])
								|| "pk_psndoc".equals(field[i])) {
							continue;
						}
						BillItem item = getCard().getHeadItem(field[i]);
						if (item != null) {
							Object value = psnvo.getFieldValue(field[i]);
							if (value == null
									|| "#null#".equals(value.toString())) {
								item.setValue(null);
								value = null;
							} else {
								item.setValue(value);
							}
							psnList[listSelectRow].setAttributeValue(field[i],
									value);
						}
					}
				}
				if (isRelBasPsndoc) {
					String[] field = baspsnvo.getAttributeNames();
					for (int i = 0; i < field.length; i++) {
						if ("pk_psnbasdoc".equals(field[i])
								|| "pk_psndoc".equals(field[i])) {
							continue;
						}
						BillItem item = getCard().getHeadItem(field[i]);
						if (item != null) {
							Object value = baspsnvo.getFieldValue(field[i]);
							if (value == null
									|| "#null#".equals(value.toString())) {
								item.setValue(null);
								value = null;
							} else {
								item.setValue(value);
							}
							psnList[listSelectRow].setAttributeValue(field[i],
									value);
						}
					}
				}
			}
			isRelateToAcc = updated;
		}
		subBackupVOs = null;
	}

	/**
	 * 校验字段输入重复 by dusx
	 * 
	 * @param tableCode
	 * @param records
	 * @throws java.lang.Exception
	 */
	private void checkReduplicateField(String tableCode,
			CircularlyAccessibleValueObject[] records)
			throws java.lang.Exception {
		FlddictVO[] flddictVOs = getUniqueFld(tableCode);
		if (flddictVOs == null || flddictVOs.length < 1 || records == null
				|| records.length < 1)
			return;
		for (int i = 0; i < flddictVOs.length; i++) {
			String fieldcode = flddictVOs[i].getFldcode();
			String fieldname = flddictVOs[i].getFldname();
			HashMap<Object, Integer> values = new HashMap<Object, Integer>();
			values.clear();
			for (int row = 0; row < records.length; row++) {
				Object value = records[row].getAttributeValue(fieldcode);
				if ("".equals(value) || value == null)
					continue;
				if (flddictVOs[i].getDatatype() == 5) {// 允许多个false
					if (value instanceof Boolean) {
						if (value.equals(new Boolean(false)))
							continue;
					}
					if (value instanceof UFBoolean) {
						if (value.equals(new UFBoolean(false)))
							continue;
					}
				}
				if (values.containsKey(value)) {
					int row1 = values.get(value);
					// throw new
					// BusinessException("字段“"+fieldname+"”在第"+(row1+1)+"行和第"+(row+1)+"行出现重复，请修改！");
					throw new BusinessException(nc.ui.ml.NCLangRes
							.getInstance().getStrByID("600700",
									"UPP600700-000123")/* @res "字段“" */
							+ fieldname
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000124")/*
																	 * @res
																	 * "”在第"
																	 */
							+ (row1 + 1)
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000125")/*
																	 * @res
																	 * "行和第"
																	 */
							+ (row + 1)
							+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
									"600700", "UPP600700-000126")/*
																	 * @res
																	 * "行出现重复，请修改！"
																	 */);
				}
				values.put(value, row);
			}
		}
	}

	/**
	 * 保存多行子集操作。 创建日期：(2004-5-21 8:43:21)
	 * 
	 * @param eavo
	 *            nc.vo.hi.hi_301.PersonEAVO
	 * @param tableCode
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void saveChilds(PersonEAVO eavo, String tableCode)
			throws java.lang.Exception {
		// 获取当前子集数据
		CircularlyAccessibleValueObject[] records = eavo.getTableVO(tableCode);
		GeneralVO[] vos = new GeneralVO[records.length];
		String pk_psnbasdoc = eavo.getPk_psnbasdoc();

		// 校验子集中不可重复字段是否有重复
		checkReduplicateField(tableCode, records);

		for (int i = 0; i < records.length; i++) {
			GeneralVO vo = (GeneralVO) records[i];
			vo.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
			// 劳动合同模块加入公司主键值
			if (tableCode.equalsIgnoreCase("hi_psndoc_ctrt")) {
				vo.setAttributeValue("pk_corp", getClientEnvironment()
						.getCorporation().getUnitname());
				vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
						nc.ui.hr.global.Global.getCorpPK());
				vo.setAttributeValue("isrefer", new UFBoolean("Y"));
				vo.setAttributeValue("pk_locusedcorp", nc.ui.hr.global.Global
						.getCorpPK());
				vo.setAttributeValue("pk_majorcorp", getClientEnvironment()
						.getCorporation().getUnitname());
				vo.setAttributeValue(Util.PK_PREFIX + "pk_majorcorp",
						nc.ui.hr.global.Global.getCorpPK());
				vo.setAttributeValue("pk_majorcorp", nc.ui.hr.global.Global
						.getCorpPK());
				vo.setAttributeValue("pk_user", Global.getUserID());
				vo.setAttributeValue("operatedate", Global.getLogDate());
			}
			// v50 add
			if ("hi_psndoc_retire".equalsIgnoreCase(getCurrentSetCode())
					|| "hi_psndoc_training"
							.equalsIgnoreCase(getCurrentSetCode())
					|| "hi_psndoc_orgpsn".equalsIgnoreCase(getCurrentSetCode())
					|| "hi_psndoc_keypsn".equalsIgnoreCase(getCurrentSetCode())) {
				vo.setAttributeValue("pk_corp", getClientEnvironment()
						.getCorporation().getUnitname());
				vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
						nc.ui.hr.global.Global.getCorpPK());
			}
			if ("hi_psndoc_ass".equalsIgnoreCase(getCurrentSetCode())) {
				vo.setAttributeValue("pk_corpperson", getClientEnvironment()
						.getCorporation().getUnitname());
				vo.setAttributeValue(Util.PK_PREFIX + "pk_corpperson",
						nc.ui.hr.global.Global.getCorpPK());
				Object o = vo.getAttributeValue(Util.PK_PREFIX
						+ "pk_corpassess");
				if (o == null) {
					vo.setAttributeValue("pk_corpassess",
							nc.ui.hr.global.Global.getCorpPK());
					vo.setAttributeValue("pk_corp", nc.ui.hr.global.Global
							.getCorpPK());
				} else {
					vo.setAttributeValue("pk_corp", (String) o);
				}
			}
			if (vo.getAttributeValue("pk_psndoc_sub") == null) {
				vo.setStatus(nc.vo.pub.VOStatus.NEW);
			} else {
				vo.setStatus(nc.vo.pub.VOStatus.UPDATED);
			}
			vos[i] = vo;
		}
		// 取得删除子集记录信息
		String[] delPkPsndocSubs = null;
		if (vDelPkPsndocSub.size() > 0) {
			delPkPsndocSubs = new String[vDelPkPsndocSub.size()];
			vDelPkPsndocSub.copyInto(delPkPsndocSubs);
		}
		// 保存所有子集记录信息
		HIDelegator.getPsnInf().saveSubSetInfos(tableCode,
				person.getPk_psndoc(), vos, delPkPsndocSubs);
		person = eavo;

		// 要从单据模板上取得字段数组。2009-4-24 dusx
		BillItem[] items = getCard().getBillModel().getBodyItems();
		String[] fieldnames = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			fieldnames[i] = items[i].getKey();
		}

		if (records.length > 0 || vDelSubVOs.size() > 0) {
			boolean updated = isUpdateAccRel(fieldnames, tableCode);
			if (updated) {
				GeneralVO voTemp = new GeneralVO();
				voTemp.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				voTemp.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				GeneralVO relVO = HIDelegator.getPsnInf().updateAccRel(
						fieldnames, getRelationMap(), tableCode, voTemp);
				HashMap hmRelVO = new HashMap();
				String[] fields = relVO.getAttributeNames();
				GeneralVO psnvo = new GeneralVO();
				GeneralVO baspsnvo = new GeneralVO();
				psnvo.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				psnvo.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				baspsnvo.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				baspsnvo.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				boolean isRelPsndoc = false;
				boolean isRelBasPsndoc = false;
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].indexOf(".") < 0) {
						continue;
					}
					String table = fields[i].substring(0, fields[i]
							.indexOf("."));
					if ("bd_psndoc".equalsIgnoreCase(table)) {
						String field = fields[i].substring(fields[i]
								.indexOf(".") + 1);
						psnvo.setAttributeValue(field, relVO
								.getAttributeValue(fields[i]));
						person.getPsndocVO().setAttributeValue(field,
								relVO.getAttributeValue(fields[i]));
						isRelPsndoc = true;
					} else if ("bd_psnbasdoc".equalsIgnoreCase(table)) {
						String field = fields[i].substring(fields[i]
								.indexOf(".") + 1);
						baspsnvo.setAttributeValue(field, relVO
								.getAttributeValue(fields[i]));
						person.getAccpsndocVO().setAttributeValue(field,
								relVO.getAttributeValue(fields[i]));
						isRelBasPsndoc = true;
					}
				}
				if (isRelPsndoc)
					hmRelVO.put("bd_psndoc", psnvo);
				if (isRelBasPsndoc)
					hmRelVO.put("bd_psnbasdoc", baspsnvo);
				HIDelegator.getPsnInf().updateRelTable(hmRelVO);
				// 更新界面数据
				if (isRelPsndoc) {
					String[] field = psnvo.getAttributeNames();
					for (int i = 0; i < field.length; i++) {
						if ("pk_psnbasdoc".equals(field[i])
								|| "pk_psndoc".equals(field[i])) {
							continue;
						}
						BillItem item = getCard().getHeadItem(field[i]);
						if (item != null) {
							Object value = psnvo.getFieldValue(field[i]);
							if (value == null
									|| "#null#".equals(value.toString())) {
								item.setValue(null);
								value = null;
							} else {
								item.setValue(value);
							}
							psnList[listSelectRow].setAttributeValue(field[i],
									value);
						}
					}
				}
				if (isRelBasPsndoc) {
					String[] field = baspsnvo.getAttributeNames();
					for (int i = 0; i < field.length; i++) {
						if ("pk_psnbasdoc".equals(field[i])
								|| "pk_psndoc".equals(field[i])) {
							continue;
						}
						BillItem item = getCard().getHeadItem(field[i]);
						if (item != null) {
							Object value = baspsnvo.getFieldValue(field[i]);
							if (value == null
									|| "#null#".equals(value.toString())) {
								item.setValue(null);
								value = null;
							} else {
								item.setValue(value);
							}
							psnList[listSelectRow].setAttributeValue(field[i],
									value);
						}
					}
				}
			}
			isRelateToAcc = updated;
		}
		subBackupVOs = null;
	}

	/**
	 * 保存引用人员信息。 创建日期：(2004-5-21 8:41:14)
	 * 
	 * @param eavo
	 *            nc.vo.hi.hi_301.PersonEAVO
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void saveHiRef(PersonEAVO eavo) throws java.lang.Exception {
		if (getIsNeedAFirm()) {// 保存到hi_psndoc_ref
			eavo.getPsndocVO().setAttributeValue("pk_corp", Global.getCorpPK());
			eavo.getPsndocVO().setAttributeValue("indocflag",
					new nc.vo.pub.lang.UFBoolean('Y'));
			eavo.getPsndocVO().setAttributeValue("userid", Global.getUserID());
			eavo.getPsndocVO()
					.setAttributeValue("appdate", Global.getLogDate());
			eavo.getPsndocVO().setAttributeValue("psnclscope", new Integer(5));
			eavo.getPsndocVO().setAttributeValue("tbm_prop", new Integer(2));
			eavo.getPsndocVO().setAttributeValue("pk_psnbasdoc",
					eavo.getPk_psnbasdoc());
			eavo.getPsndocVO().setAttributeValue("oripk_psndoc",
					eavo.getPk_psndoc());
			// fengwei 2010-11-4 增加引用人员保存自定义信息项 start
			// String[] fieldNames = eavo.getPsndocVO().getAttributeNames();
			// for (int i = 0; i < fieldNames.length; i++) {
			// if (fieldNames[i].indexOf("def") > 0 ||
			// fieldNames[i].startsWith("$")) {
			// eavo.getPsndocVO().removeAttributeName(fieldNames[i]);
			// }
			// }
			// ----------------------------------------end
			eavo.getPsndocVO().removeAttributeName("isreferenced");

		} else {// 保存到bd_psndoc

			eavo.getPsndocVO().setAttributeValue("pk_corp", Global.getCorpPK());
			eavo.getPsndocVO().setAttributeValue("indocflag",
					new nc.vo.pub.lang.UFBoolean('Y'));
			eavo.getPsndocVO().setAttributeValue("psnclscope", new Integer(5));
			eavo.getPsndocVO().setAttributeValue("tbm_prop", new Integer(2));
			eavo.getPsndocVO().setAttributeValue("isreferenced", "Y");
			eavo.getPsndocVO().setAttributeValue("pk_psnbasdoc",
					eavo.getPk_psnbasdoc());
			String[] fieldNames = eavo.getPsndocVO().getAttributeNames();
			for (int i = 0; i < fieldNames.length; i++) {
				if (fieldNames[i].indexOf("def") > 0
						|| fieldNames[i].startsWith("$")
						|| fieldNames[i].startsWith("ori")) {
					eavo.getPsndocVO().removeAttributeName(fieldNames[i]);
				}
			}

		}
		String pk_psndoc = HIDelegator.getPsnInf().insertHiRef(
				eavo.getPsndocVO(), getIsNeedAFirm());
		eavo.getPsndocVO().setAttributeValue("pk_psndoc", pk_psndoc);

	}

	/**
	 * 保存主集。 创建日期：(2004-5-21 8:41:14)
	 * 
	 * @param eavo
	 *            nc.vo.hi.hi_301.PersonEAVO
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void saveMain(PersonEAVO eavo) throws java.lang.Exception {
		// 编辑主集状态
		UFDate currdate = ClientEnvironment.getInstance().getDate();
		eavo.getPsndocVO().setAttributeValue("groupdef47", currdate);
		if (isAdding()) {
			addPerson(eavo);
			BillItem item = getBillItem("bd_psnbasdoc.belong_pk_corp");
			if (item != null) {
				item.setValue(Global.getCorpPK());
			}
		} else {
			eavo.getPsndocVO().setAttributeValue("jobtypeflag",
					new Integer(eavo.getJobType()));
			// 更新当前人员的信息
			String pk_psnbasdoc = eavo.getPk_psnbasdoc();
			eavo.getAccpsndocVO().setAttributeValue("pk_psnbasdoc",
					pk_psnbasdoc);
			// 归属公司
			String belong_pk_corp = (String) psnList[listSelectRow]
					.getAttributeValue("belong_pk_corp");
			Item item = (Item) getCmbPsncl().getSelectedItem();
			if (!Global.getCorpPK().equalsIgnoreCase(belong_pk_corp)
					&& item != null && item.getValue() == 5) {// 引用人员修改需要调用薪资接口
				// v50
				GeneralVO accpsndocVO = eavo.getAccpsndocVO();
				accpsndocVO.setAttributeValue("curret_pk_corp", Global
						.getCorpPK());
				HIDelegator.getPsnInf().updateRefPersonMain(eavo.getPsndocVO(),
						accpsndocVO, null, Global.getLogDate());
			} else {
				HIDelegator.getPsnInf().updateMain(eavo.getPsndocVO(),
						eavo.getAccpsndocVO(), null);
			}
			// 更新当前人员的主表数据信息
			person.setPsndocVO(eavo.getPsndocVO());
			person.setAccpsndocVO(eavo.getAccpsndocVO());
			// 获得当前被编辑人员的主键
			String pk_psndoc = (String) eavo.getPsndocVO().getAttributeValue(
					"pk_psndoc");
			Vector v = new Vector();
			// 刷新当前人员信息
			ConditionVO cond = new ConditionVO();
			cond.setFieldCode("bd_psndoc.pk_psndoc");
			cond.setOperaCode("=");
			cond.setValue(pk_psndoc);
			v.addElement(cond);
			if ("600707".equalsIgnoreCase(getModuleCode())) {
				ConditionVO cond1 = new ConditionVO();
				cond1.setFieldCode("bd_psndoc.indocflag");
				cond1.setOperaCode("=");
				cond1.setValue("Y");
				v.addElement(cond1);
				//
				ConditionVO cond2 = new ConditionVO();
				cond2.setFieldCode("hi_psndoc_deptchg.jobtype");
				cond2.setOperaCode("=");
				cond2.setDataType(CommonValue.DATATYPE_INTEGER);
				cond2.setValue("" + person.getJobType() + "");
				v.addElement(cond2);
			} else {
				ConditionVO cond1 = new ConditionVO();
				cond1.setFieldCode("bd_psndoc.indocflag");
				cond1.setOperaCode("=");
				cond1.setValue("N");
				v.addElement(cond1);
			}

			ConditionVO[] conds = new ConditionVO[v.size()];
			v.copyInto(conds);
			// GeneralVO[] gvos = HIDelegator.getPsnInf().queryByCondition(
			// Global.getCorpPK(), conds, powerSql, getListField(),wheresql);
			GeneralVO[] gvos = HIDelegator.getPsnInf().queryByCondition(
					Global.getCorpPK(), conds, powerSql, getListField(),
					" and (1=1) ");
			final String[] keys = { "deptcode", "deptname", "jobtypeflag",
					"psnclassname", "pk_om_job", "jobname", "pk_deptdoc",
					"pk_psncl", "pk_om_duty", "jobrank", "jobseries", "series" };
			int i = findPersonInQueryResult(pk_psndoc);
			if (i != -1 && (gvos != null && gvos.length > 0)) {
				if (eavo.getJobType() > 0) { // 非正式人员，替换为原来的
					for (int j = 0; j < keys.length; j++) {
						Object value = queryResult[j]
								.getAttributeValue(keys[j]);
						gvos[0].setAttributeValue(keys[j], value);
					}
				}
				queryResult[i] = gvos[0];
				// 清除缓冲，重新选择
				psnDeptCache.clear();
				psnCorpCache.clear();
				person = eavo;// wangkf add
				listSelectRow = findPersonPos(pk_psndoc, "pk_psndoc", psnList);
				if (person != null
						|| person.getSubtables().get("hi_psndoc_deptchg") != null) {
					person.getSubtables().remove("hi_psndoc_deptchg");
					bodyTabbedPane_stateChanged(null);
				}
			}
		}
	}

	/**
	 * 全集团保存排序的结果。
	 * 
	 * @throws java.lang.Exception
	 */
	protected void saveShowOrder() throws java.lang.Exception {
		// 不失去焦点得不到数据
		TableCellEditor editor = getPsnList().getTable().getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
		// 获取当前部门排序的结果
		GeneralVO[] sortPsnList = (GeneralVO[]) getPsnList().getTableModel()
				.getBodyValueVOs("nc.vo.hi.hi_301.GeneralVO");
		GeneralVO[] oldPsnList = (GeneralVO[]) getPsnList().getBodyData();
		HashMap psnhash = new HashMap();
		// 获取排序后的员工主键
		String[] pk_psndocs = new String[sortPsnList.length];
		for (int i = 0; i < pk_psndocs.length; i++) {
			pk_psndocs[i] = (String) oldPsnList[i]
					.getAttributeValue("pk_psndoc");
			if (sortPsnList[i].getAttributeValue("showorder") != null
					&& sortPsnList[i].getAttributeValue("showorder") != "") {
				if (((Integer) sortPsnList[i].getAttributeValue("showorder"))
						.intValue() < 0) {
					throw new BusinessException(nc.ui.ml.NCLangRes
							.getInstance().getStrByID("600704",
									"UPT600704-000274")/* "序号不能小于0" */);
				}
				if (!sortPsnList[i].getAttributeValue("showorder").equals(
						oldPsnList[i].getAttributeValue("showorder"))) {
					psnhash.put(pk_psndocs[i], sortPsnList[i]
							.getAttributeValue("showorder"));
				}

				oldPsnList[i].setAttributeValue("showorder", sortPsnList[i]
						.getAttributeValue("showorder"));
			} else {
				psnhash.put(pk_psndocs[i], new Integer(999999));// 如果为空则设置成默认值(界面显示空)
				oldPsnList[i].setAttributeValue("showorder", "");
			}
		}
		HIDelegator.getPsnInf().updateShoworder(pk_psndocs, psnhash);

	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-8-16 16:22:15)
	 */
	private void sequenceFire(String tableCode,
			CircularlyAccessibleValueObject record) {
		/** 效率优化 BATCH START */
		/* 发现使用批量取参照，页面逻辑会有问题，只好改回原来的 */
		// BatchMatchContext.getShareInstance().setInBatchMatch(true);
		// BatchMatchContext.getShareInstance().clear();
		// try {
		String[] seqFields = (String[]) triggerTable.fieldAOE.get(tableCode);
		if (seqFields == null)
			return;
		for (int i = 0; i < seqFields.length; i++) {
			BillItem item = getBillItem(tableCode + "." + seqFields[i]);
			if (item == null)
				continue;
			if (item.getDataType() == IBillItem.UFREF) {
				// 如果是参照类型，则需要触发
				// 其他组件，在设置数值时就已经触发，这是由于UIRefPane设置值时不能自动触发事件造成的
				Object v = null;
				String key = null;
				if (item.getPos() == BillItem.BODY) {
					key = Util.PK_PREFIX + item.getKey();
					v = record
							.getAttributeValue(Util.PK_PREFIX + item.getKey());
				} else {
					key = item.getKey();
					v = record.getAttributeValue(item.getKey());
				}
				if (v == null)
					continue;

				((UIRefPane) item.getComponent()).setPK(v);
				// ((UIRefPane) item.getComponent()).setValue(v.toString());
				((UIRefPane) item.getComponent()).repaint();
				record.setAttributeValue(key, v);
			}
		}
		// ///***BATCH END***/
		// BatchMatchContext.getShareInstance().executeBatch();
		// }finally{
		// BatchMatchContext.getShareInstance().setInBatchMatch(false);
		//
		// }
	}

	/**
	 * 设置点击添加行按钮时设置按钮状态 创建日期：(2004-5-26 21:26:36)
	 */
	protected void setAddChildButtonState() {
		// 当前选中页签是否是追踪信息集
		if (isNotMultiEdit()) {
			bnAddChild.setEnabled(false);
			bnUpdateChild.setEnabled(false);
			bnInsertChild.setEnabled(false);
			bnDelChild.setEnabled(false);
		} else {
			bnAddChild.setEnabled(true);
			bnUpdateChild.setEnabled(true);
			bnInsertChild.setEnabled(true);
			bnDelChild.setEnabled(true);
		}
		// 设置按钮状态
		bnAdd.setEnabled(false);
		bnEdit.setEnabled(false);
		bnDel.setEnabled(false);
		bnList.setEnabled(false);
		bnSave.setEnabled(true);
		bnCancel.setEnabled(true);
		bnUpload.setEnabled(false);
		bnUpRecord.setEnabled(false);
		bnDownRecord.setEnabled(false);
		bnToFirst.setEnabled(false);
		bnToLast.setEnabled(false);
		bnSubsetMove.setEnabled(false);
		// 更新按钮状态
		updateButtons();
	}

	/**
	 * 设置编辑状态为新增。 创建日期：(2004-5-14 15:52:50)
	 * 
	 * @param newAdding
	 *            boolean
	 */
	private void setAdding(boolean newAdding) {
		adding = newAdding;
	}

	protected void setHeadBillItemEditable(String table, boolean isEnabled) {
		BillItem[] items = getCard().getHeadShowItems(table);
		if (items == null || items.length < 1)
			return;
		// 缓存表头编辑状态
		for (int i = 0; i < items.length; i++) {
			if (htbEdit.get(items[i].getKey()) == null) {
				htbEdit.put(items[i].getKey(), new Boolean(items[i].isEdit()));
			}
		}
		// 设置是否可以编辑
		if (getEditType() == EDIT_REF || isRefPerson) {
			for (int i = 0; i < items.length; i++) {
				items[i].setEdit(isEnabled);
				items[i].setEnabled(isEnabled);
			}
		} else {
			for (int i = 0; i < items.length; i++) {
				// 日期型信息项不可编辑
				if (items[i].getKey().indexOf(CommonValue.UFFORMULA_DATA) >= 0) {
					items[i].setEdit(false);
					items[i].setEnabled(false);
				} else {
					Object obj = htbEdit.get(items[i].getKey());
					if (obj != null) {
						items[i].setEdit(((Boolean) obj).booleanValue());
						items[i].setEnabled(((Boolean) obj).booleanValue());
					} else {
						items[i].setEdit(false);
						items[i].setEnabled(false);
					}
				}
			}
		}
	}

	protected boolean isRefPerson = false;// 是否引用的人员

	/**
	 * 根据其他项目的不同状态设置项目的可编辑性。 创建日期：(2004-9-30 10:27:18)
	 */
	protected void setBillItemEditable() {
		Item psnclitem = (Item) getCmbPsncl().getSelectedItem();
		if (psnclitem != null && psnclitem.getValue() == 5) {
			// 引用标记不可编辑
			setBillItemEnabled("bd_psndoc.isreferenced", false);
			setBillItemEdit("bd_psndoc.isreferenced", false);
			// 封存日期不可编辑
			// 引用标记不可编辑
			setBillItemEnabled("bd_psndoc.sealdate", false);
			setBillItemEdit("bd_psndoc.sealdate", false);
		}
		// 只有其他人员引用标志为true时才特殊处理
		if (isRefPerson) {
			setHeadItemEnableInRef();
			return;
		} else {
			setHeadItemEnable();
		}
		// -人员采集自动编码--- begin
		if (isPsncodeAutoGenerate()) {
			getBillItem("bd_psndoc.psncode").setEdit(false);
		} else {
			// dusx 修改，此字段同时应该按照单据模板上的可编辑属性处理 20009-4-29
			// getBillItem("bd_psndoc.psncode").setEdit(true);// true
			getBillItem("bd_psndoc.psncode").setEdit(psncodeCanEdit);
		}
		// -人员采集自动编码----end
		// 除采集节点外考勤卡号不能编辑
		if (!getModuleName().equals("600704")) {
			setBillItemEdit("bd_psndoc.timecardid", false);
		}
		if (!getModuleName().equals("600704")) {
			getBillItem("bd_psndoc.pk_psncl").setEdit(false);
			getBillItem("bd_psndoc.pk_deptdoc").setEdit(false);
			getBillItem("bd_psndoc.pk_om_job").setEdit(false);
		} else {
			getBillItem("bd_psndoc.pk_psncl").setEdit(true);
			getBillItem("bd_psndoc.pk_deptdoc").setEdit(true);
			getBillItem("bd_psndoc.pk_om_job").setEdit(true);
		}
		// 转正日期控件
		getBillItem("bd_psndoc.regular").setEdit(
				getBillItem("bd_psndoc.regular").isEdit());
		UICheckBox checkBoxregular = (UICheckBox) getBillItem(
				"bd_psndoc.regular").getComponent();
		// 设置转正日期控件不可编辑
		if (checkBoxregular.isSelected()) {
			Object obj = htbRegularEdit.get("bd_psndoc.regulardata");
			if (obj != null) {
				getBillItem("bd_psndoc.regulardata").setEnabled(
						((Boolean) obj).booleanValue());
				getBillItem("bd_psndoc.regulardata").setEdit(
						((Boolean) obj).booleanValue());
			} else {
				getBillItem("bd_psndoc.regulardata").setEnabled(false);
				getBillItem("bd_psndoc.regulardata").setEdit(false);
			}
		} else {
			getBillItem("bd_psndoc.regulardata").setEnabled(false);
			getBillItem("bd_psndoc.regulardata").setEdit(false);
		}
		UICheckBox checkBox = null;
		if(  getBillItem("bd_psndoc.clerkflag") != null){
			 checkBox = (UICheckBox) getBillItem("bd_psndoc.clerkflag")
					.getComponent();
		}
	
		// 设置职员编码控件不可编辑
		if (checkBox == null ? false :checkBox.isSelected() &&  getBillItem("bd_psndoc.clerkcode") != null) {
			getBillItem("bd_psndoc.clerkcode").setEnabled(true);
			getBillItem("bd_psndoc.clerkcode").setEdit(true);
		} else if(getBillItem("bd_psndoc.clerkcode" )!= null) {
			getBillItem("bd_psndoc.clerkcode").setEnabled(false);
			getBillItem("bd_psndoc.clerkcode").setEdit(false);
		}
		// 岗位序列，岗位等级，职务
		UIRefPane job = (UIRefPane) (getBillItem("bd_psndoc.pk_om_job")
				.getComponent());
		String pk_job = job.getRefPK();
		if (pk_job == null || pk_job.length() != 20) {
			if (getModuleName().equals("600704")) {
				getBillItem("bd_psndoc.jobrank").setEnabled(
						getBillItem("bd_psndoc.jobrank").isEdit());// true
				getBillItem("bd_psndoc.jobseries").setEdit(false);
				getBillItem("bd_psndoc.dutyname").setEnabled(
						getBillItem("bd_psndoc.dutyname").isEdit());// true
				getBillItem("bd_psndoc.series").setEnabled(
						getBillItem("bd_psndoc.series").isEdit());// true
			} else {
				getBillItem("bd_psndoc.jobrank").setEdit(false);// true
				getBillItem("bd_psndoc.jobseries").setEdit(false);
				getBillItem("bd_psndoc.dutyname").setEdit(false);// true
				getBillItem("bd_psndoc.series").setEdit(false);// true
			}
			UIRefPane dutynameRef = (UIRefPane) (getBillItem("bd_psndoc.dutyname")
					.getComponent());
			String pkduty = dutynameRef.getRefPK();
			if (nc.ui.hi.hi_301.Util.isDutyDependJobSeries) {
				if (pkduty != null && pkduty.trim().length() == 20) {
					getBillItem("bd_psndoc.jobseries").setEnabled(false);
					getBillItem("bd_psndoc.jobseries").setEdit(false);
				} else {
					if (getModuleName().equals("600704")) {
						getBillItem("bd_psndoc.jobseries").setEnabled(
								getBillItem("bd_psndoc.jobseries").isEdit());// true
						getBillItem("bd_psndoc.jobseries").setEdit(
								getBillItem("bd_psndoc.jobseries").isEdit());
					} else {
						getBillItem("bd_psndoc.jobseries").setEnabled(false);// true
						getBillItem("bd_psndoc.jobseries").setEdit(false);
					}
				}
			}
		} else {
			getBillItem("bd_psndoc.jobrank").setEnabled(false);
			getBillItem("bd_psndoc.jobrank").setEdit(false);
			getBillItem("bd_psndoc.jobseries").setEdit(false);
			String pk_duty = (String) job.getRefValue("om_job.pk_om_duty");
			if (pk_duty != null && pk_duty.trim().length() == 20) {
				getBillItem("bd_psndoc.dutyname").setEnabled(false);
				getBillItem("bd_psndoc.series").setEnabled(false);
				getBillItem("bd_psndoc.dutyname").setEdit(false);
				getBillItem("bd_psndoc.series").setEdit(false);
			} else {
				if (getModuleName().equals("600704")) {
					getBillItem("bd_psndoc.dutyname").setEnabled(
							getBillItem("bd_psndoc.dutyname").isEdit());
					getBillItem("bd_psndoc.dutyname").setEdit(
							getBillItem("bd_psndoc.dutyname").isEdit());
					UIRefPane dutynameRef = (UIRefPane) (getBillItem("bd_psndoc.dutyname")
							.getComponent());
					String pkduty = dutynameRef.getRefPK();
					if (pkduty != null && pkduty.trim().length() == 20) {
						getBillItem("bd_psndoc.series").setEnabled(false);
						getBillItem("bd_psndoc.series").setEdit(false);
					} else {
						getBillItem("bd_psndoc.series").setEnabled(
								getBillItem("bd_psndoc.series").isEdit());// true
					}
				} else {
					getBillItem("bd_psndoc.dutyname").setEnabled(false);
					getBillItem("bd_psndoc.series").setEnabled(false);
					getBillItem("bd_psndoc.dutyname").setEdit(false);
					getBillItem("bd_psndoc.series").setEdit(false);
				}
			}
		}
		if (!curTempCorpPk.equalsIgnoreCase(Global.getCorpPK())
				|| isPartPsnCurCorp) {
			// wangkf fixed isPartPsnCurCorp ->!isPartPsnCurCorp
			getBillItem("bd_psndoc.jobrank").setEnabled(false);
			getBillItem("bd_psndoc.jobseries").setEnabled(false);
			getBillItem("bd_psndoc.dutyname").setEnabled(false);
			getBillItem("bd_psndoc.series").setEnabled(false);
			setBillItemEnabled("bd_psndoc.timecardid", false);
		}
	}

	/**
	 */
	public void setBillItemEnabled(String itemkey, boolean isEnabled) {
		if (getBillItem(itemkey) != null)
			getBillItem(itemkey).setEnabled(isEnabled);
	}

	/**
	 */
	public void setBillItemEdit(String itemkey, boolean isEdit) {
		if (getBillItem(itemkey) != null)
			getBillItem(itemkey).setEdit(isEdit);
	}

	/**
	 * 根据界面状态设定按钮组状态。 当同处某种界面状态下，有类似不同的树节点需要有不同的按钮状态时，
	 * 可以考虑用setBtnStatWithBuss的方法再补充一次 创建日期：(2003-3-27 20:09:48)
	 * 
	 * @param stat
	 *            int
	 */
	public void setBtnsStatWithUIStat(int stat) {
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-1 8:46:01)
	 */
	protected void setButtons() {
		// 采集状态
		setButtons(grpCollect);
	}

	/**
	 * 设置取消编辑主表时按钮的状态。 创建日期：(2004-5-26 21:53:32)
	 */
	protected void setCancelEditMainButtonState() {
		// 设置取消编辑主表时的状态
		bnAdd.setEnabled(true);
		bnEdit.setEnabled(true);
		bnDel.setEnabled(true);
		bnChildEdit.setEnabled(true);
		bnList.setEnabled(true);
		bnSave.setEnabled(false);
		bnCancel.setEnabled(false);
		bnUpload.setEnabled(true);
		bnChildOp.setEnabled(!isAdding());
		bnReturn.setEnabled(true);
		// 更新按钮状态
		updateButtons();
	}

	/**
	 * 决定编辑行、插入行和删除行是否可用，判断规则是: 如果是编辑状态，则他们都可用， 否则，只有当前行数得大于1才可用， 因为取消后要删除新增的行。
	 * 创建日期：(2004-5-26 21:48:07)
	 */
	private void setCancelEditSubButtonState() {

		// 决定编辑行、插入行和删除行是否可用，判断规则是:
		// 如果是编辑状态，则他们都可用，
		// 否则，只有当前行数得大于1才可用，因为取消后要删除新增的行。
		boolean enabled = isEditLine() ? true : (getCard().getBillTable()
				.getRowCount() > 1);
		// 设置按钮状态
		bnAddChild.setEnabled(true);
		bnUpdateChild.setEnabled(enabled);
		bnInsertChild.setEnabled(enabled);
		bnDelChild.setEnabled(enabled);
		bnSave.setEnabled(false);
		bnCancel.setEnabled(false);
		bnReturn.setEnabled(true);
		bnUpload.setEnabled(true);
		// 更新按钮状态
		updateButtons();
	}

	/**
	 * 设置取消排序操作后的按钮状态。 创建日期：(2004-5-27 9:21:58)
	 */
	protected void setCancelSortButtonState() {
		// 设置按钮状态
		bnSave.setEnabled(false);
		bnCancel.setEnabled(false);
		// 更新按钮
		updateButtons();
	}

	/**
	 * 如果是档案子集，则加入文件操作的按钮 v50 add
	 */
	protected void expendChildEditButons() {
		bnChildEdit.addChileButtons(grpChild);
	}

	/**
	 * 设置子操作按钮的状态。 创建日期：(2004-5-25 15:15:08)
	 */
	protected void setChildButtonState() {
		// 当前选中行和行数
		int row = getCard().getBillTable().getSelectedRow();
		int count = getCard().getBillTable().getRowCount();
		// 当前选中页签是否是追踪信息集
		boolean traceable = isBodyTraceTable();
		// 根据是否是追踪信息集设置新增行按钮状态
		String indoc = "N";
		boolean bemoved = false;
		boolean isreturn = false;
		if (person != null) {
			if (listSelectRow >= 0) {
				Object o = psnList[listSelectRow]
						.getAttributeValue("indocflag");
				if (o != null) {
					isreturn = indoc.equals(o) ? false : true;
				}
			}
			bemoved = true;
		}
		// v50 add
		boolean move = Canmoved();
		// 如果选中人员归属公司不是当前登录公司或者是关键人员，则调整子集顺序按钮不可用
		boolean belong = false;
		if (listSelectRow >= 0) {
			if (Global.getCorpPK().equalsIgnoreCase(
					(String) psnList[listSelectRow]
							.getAttributeValue("belong_pk_corp"))) {
				belong = true;
			}
		}
		bnSubsetMove.setEnabled(!move && bemoved && belong);
		if (isreturn) {
			bnAddChild.setEnabled(!traceable);
			if (row != -1 && count > 0 && row < count) {
				// 选中了一行
				bnUpdateChild.setEnabled(!traceable);
				bnDelChild.setEnabled(!traceable);
				bnInsertChild.setEnabled(!traceable);
			} else {
				// 没有选中
				bnUpdateChild.setEnabled(false);
				bnDelChild.setEnabled(false);
				bnInsertChild.setEnabled(false);
			}
		} else {
			bnAddChild.setEnabled(true);
			if (row != -1 && count > 0 && row < count) {
				// 选中了一行
				bnUpdateChild.setEnabled(true);
				bnDelChild.setEnabled(true);
				bnInsertChild.setEnabled(true);
			} else {
				// 没有选中
				bnUpdateChild.setEnabled(false);
				bnDelChild.setEnabled(false);
				bnInsertChild.setEnabled(false);
			}
		}
		if ("hi_psndoc_capa".equals(getBodyTableCode())) {
			bnAddChild.setEnabled(false);
			bnUpdateChild.setEnabled(false);
			bnDelChild.setEnabled(false);
			bnInsertChild.setEnabled(false);
		}
		updateButtons();
	}

	/**
	 * 设置部门树的根结点。 创建日期：(2004-5-10 8:41:00)
	 * 
	 * @param newCtrlDeptRoot
	 *            nc.vo.hi.hi_301.CtrlDeptVO
	 */
	protected void setCtrlDeptRoot(nc.vo.hi.hi_301.CtrlDeptVO newCtrlDeptRoot) {
		ctrlDeptRoot = newCtrlDeptRoot;
	}

	/**
	 * wangkf add 设置 公司自定义项为 空
	 * 
	 * @param infoVOs
	 * @param value
	 */
	protected GeneralVO[] setDefItem(GeneralVO[] infoVOs, String value) {
		if (infoVOs == null || infoVOs.length < 1) {
			return null;
		}
		String prefix = "corpdef";
		for (int i = 0; i < infoVOs.length; i++) {
			String[] fields = infoVOs[i].getAttributeNames();
			if (fields == null || fields.length < 1)
				break;

			for (int j = 0; j < fields.length; j++) {
				if (fields[j] == null)
					continue;
				String field = null;
				String fieldprefix = null;
				if (fields[j].indexOf(".") >= 0) {
					field = fields[j].substring(fields[j].indexOf(".") + 1);
				} else {
					field = fields[j];
				}
				if (field.length() > prefix.length()) {
					fieldprefix = field.substring(0, prefix.length());
				}
				if (prefix.equals(fieldprefix)) {
					infoVOs[i].setAttributeValue(field, value);
				}
			}
		}
		return infoVOs;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-10-25 21:03:27)
	 * 
	 * @param power
	 *            java.lang.String
	 */
	public void setDeptPowerInit(String power) {
		deptPowerInit = power;
	}

	/**
	 * 设置是否正在编辑行。 创建日期：(2004-5-12 14:51:25)
	 * 
	 * @param newEditLine
	 *            boolean
	 */
	private void setEditLine(boolean newEditLine) {
		editLine = newEditLine;
	}

	/**
	 * 设置正在编辑的行号。 创建日期：(2004-5-12 14:51:25)
	 * 
	 * @param newEditLineNo
	 *            int
	 */
	private void setEditLineNo(int newEditLineNo) {
		editLineNo = newEditLineNo;
	}

	/**
	 * 设置编辑类型。 创建日期：(2004-5-12 11:40:28)
	 * 
	 * @param newEditType
	 *            int
	 */
	protected void setEditType(int newEditType) {
		editType = newEditType;
	}

	/**
	 * 设置人员任职信息子表的某些项的可编辑性。
	 */
	private void setHiDeptchgEditable(GeneralVO vo) {
		UIRefPane job = (UIRefPane) (getBillItem("hi_psndoc_deptchg.pk_postdoc")
				.getComponent());
		String pk_job = (String) vo.getFieldValue("pk_postdoc");// Util.PK_PREFIX
		job.setPK(pk_job);
		if (pk_job == null || pk_job.length() != 20) {
			getBillItem("hi_psndoc_deptchg.pk_jobrank").getComponent()
					.setEnabled(true);
			getBillItem("hi_psndoc_deptchg.pk_om_duty").getComponent()
					.setEnabled(true);
			getBillItem("hi_psndoc_deptchg.pk_detytype").getComponent()
					.setEnabled(true);
			UIRefPane dutynameRef = (UIRefPane) (getBillItem("hi_psndoc_deptchg.pk_om_duty")
					.getComponent());
			String pkduty = dutynameRef.getRefPK();
			if (nc.ui.hi.hi_301.Util.isDutyDependJobSeries) {
				if (pkduty != null && pkduty.trim().length() == 20) {
					getBillItem("hi_psndoc_deptchg.pk_jobserial")
							.getComponent().setEnabled(false);
				} else {
					getBillItem("hi_psndoc_deptchg.pk_jobserial")
							.getComponent().setEnabled(true);
				}
			} else {
				getBillItem("hi_psndoc_deptchg.pk_jobserial").getComponent()
						.setEnabled(true);
			}
		} else {
			getBillItem("hi_psndoc_deptchg.pk_jobrank").getComponent()
					.setEnabled(false);
			getBillItem("hi_psndoc_deptchg.pk_jobserial").getComponent()
					.setEnabled(false);
			String pk_duty = (String) job.getRefValue("om_job.pk_om_duty");
			if (pk_duty != null && pk_duty.trim().length() == 20) {
				getBillItem("hi_psndoc_deptchg.pk_om_duty").getComponent()
						.setEnabled(false);
				getBillItem("hi_psndoc_deptchg.pk_detytype").getComponent()
						.setEnabled(false);
			} else {
				getBillItem("hi_psndoc_deptchg.pk_om_duty").getComponent()
						.setEnabled(true);
				UIRefPane dutynameRef = (UIRefPane) (getBillItem("hi_psndoc_deptchg.pk_om_duty")
						.getComponent());
				String pkduty = dutynameRef.getRefPK();
				if (pkduty != null && pkduty.trim().length() == 20) {
					getBillItem("hi_psndoc_deptchg.pk_detytype").getComponent()
							.setEnabled(false);
				} else {
					getBillItem("hi_psndoc_deptchg.pk_detytype").getComponent()
							.setEnabled(true);
				}
			}
		}
		getCard().updateUI();
	}

	/**
	 * 设置人员关键人员信息子表的某些项的可编辑性。
	 */
	protected void setHiKeyPsnEditable(GeneralVO vo) {
		Object enddate = vo.getAttributeValue("enddate");
		if (enddate != null) {
			getBillItem("hi_psndoc_keypsn.begindate").getComponent()
					.setEnabled(false);
			getBillItem("hi_psndoc_keypsn.enddate").getComponent().setEnabled(
					false);
			getBillItem("hi_psndoc_keypsn.pk_keypsn_group").getComponent()
					.setEnabled(false);
			getBillItem("hi_psndoc_keypsn.pk_corp").getComponent().setEnabled(
					false);
		} else {
			getBillItem("hi_psndoc_keypsn.begindate").getComponent()
					.setEnabled(true);
			getBillItem("hi_psndoc_keypsn.enddate").getComponent().setEnabled(
					true);
			getBillItem("hi_psndoc_keypsn.pk_keypsn_group").getComponent()
					.setEnabled(true);
			getBillItem("hi_psndoc_keypsn.pk_corp").getComponent().setEnabled(
					true);
		}
		getCard().updateUI();
	}

	/**
	 * 设置选中插入行后的按钮状态。 创建日期：(2004-5-27 9:09:29)
	 */
	protected void setInsertChildButtonState() {

		// 设置选中插入行后的按钮状态。
		if (isNotMultiEdit()) {
			bnAddChild.setEnabled(false);
			bnUpdateChild.setEnabled(false);
			bnInsertChild.setEnabled(false);
			bnDelChild.setEnabled(false);
		} else {
			bnAddChild.setEnabled(true);
			bnUpdateChild.setEnabled(true);
			bnInsertChild.setEnabled(true);
			bnDelChild.setEnabled(true);
		}
		bnAdd.setEnabled(false);
		bnEdit.setEnabled(false);
		bnDel.setEnabled(false);
		bnList.setEnabled(false);
		bnSave.setEnabled(true);
		bnCancel.setEnabled(true);
		bnReturn.setEnabled(false);
		bnUpload.setEnabled(false);
		bnUpRecord.setEnabled(false);
		bnDownRecord.setEnabled(false);
		bnToFirst.setEnabled(false);
		bnToLast.setEnabled(false);
		bnSubsetMove.setEnabled(false);// v50
		// 更新按钮状态
		updateButtons();
	}

	/**
	 * 设置多行编辑子集按钮状态 创建日期：(2004-5-27 9:09:29)
	 */
	protected void setMultEditChildButtonState() {
		// 当前选中行和行数
		int row = getCard().getBillTable().getSelectedRow();
		int count = getCard().getBillTable().getRowCount();
		// 设置选中插入行后的按钮状态。
		bnAddChild.setEnabled(true);
		if (row != -1 && count > 0 && row < count) {
			bnUpdateChild.setEnabled(true);
			bnInsertChild.setEnabled(true);
			bnDelChild.setEnabled(true);
		} else {
			bnUpdateChild.setEnabled(true);
			bnInsertChild.setEnabled(false);
			bnDelChild.setEnabled(true);
		}
		//
		bnAdd.setEnabled(false);
		bnEdit.setEnabled(false);
		bnDel.setEnabled(false);
		bnUpload.setEnabled(false);
		bnList.setEnabled(false);
		bnSave.setEnabled(true);
		bnCancel.setEnabled(true);
		bnReturn.setEnabled(false);
		bnUpRecord.setEnabled(false);
		bnDownRecord.setEnabled(false);
		bnToFirst.setEnabled(false);
		bnToLast.setEnabled(false);
		bnSubsetMove.setEnabled(false);// v50
		// 更新按钮状态
		updateButtons();
	}

	/**
	 * 设置人员信息显示列表的信息项。 创建日期：(2004-5-18 10:55:48)
	 * 
	 * @param newListItems
	 *            java.lang.String[]
	 */
	private void setListItems(Pair[] newListItems) {
		listItems = newListItems;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-1 10:28:30)
	 */
	protected void setMaintainMarkBeforeValidation(PersonEAVO eavo) {
	}

	/**
	 * 根据当前界面状态设定界面空间的编辑状态。 创建日期：(2003-3-27 20:08:38)
	 * 
	 * @param stat
	 *            int
	 */
	public void setPanelStat(int stat) {
	}

	/**
	 * 将当前选中人员的姓名放在窗口标题上。 创建日期：(2004-6-5 10:06:35)
	 */
	private void setPsnNameOnBottom() {
		if (psnList == null)
			return;
		int row = getPsnList().getTable().getSelectedRow();
		int rowCount = getPsnList().getTable().getRowCount();
		if (rowCount == 0)
			return;
		if (row < 0 || row >= rowCount || row >= psnList.length)
			return;

		// 确认删除
		GeneralVO psn = ((GeneralVO[]) getPsnList().getBodyData())[row];
		String psnname = nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
				"UPP600704-000090")/* @res "当前选中人员是：" */
				+ psn.getAttributeValue("psnname");
		showHintMessage(psnname);
	}

	/**
	 * 设置第row＋1行是否可以编辑：editable=true:可以编辑；editable=false不可以编辑 wangkf add
	 * 
	 * @param row
	 * @param editable
	 */
	private void setRowState(BillModel model, int row, boolean editable) {
		// 循环遍历
		int count = model.getColumnCount();
		// 当前表的编码
		String tableCode = getBodyTableCode();
		// 当前表的编辑项
		BillItem[] billItems = model.getBodyItems();
		for (int i = 0; i < count; i++) {
			// 当前字段编码
			String key = billItems[i].getKey();
			// 是否可以编辑
			boolean isEdit = isEditable(tableCode, key);
			if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_ctrt")) {
				if (row == 0 && key.equalsIgnoreCase("iconttype")) {
					// 增加顺序变后，第一行合同类型不能编辑
					isEdit = false;
				}
			} else if (getBodyTableCode()
					.equalsIgnoreCase("hi_psndoc_training")) {
				if (key.equalsIgnoreCase("tra_mode_name")) {
					isEdit = false;
				}
			}
			// 设置编辑状态
			boolean newEditable = (editable ? isEdit : false);
			getCard().setCellEditable(row, key, newEditable);
			getCard().getBodyPanel().repaint();
			getCard().updateUI();
		}

	}

	/**
	 * 设置保存主集操作后的按钮状态。 创建日期：(2004-5-27 9:21:58)
	 */
	protected void setSaveMainButtonState() {
		// 设置按钮状态
		bnAdd.setEnabled(true);
		bnEdit.setEnabled(true);
		bnSave.setEnabled(false);
		bnCancel.setEnabled(false);
		bnChildOp.setEnabled(true);
		bnReturn.setEnabled(true);
		// 更新按钮
		updateButtons();
	}

	/**
	 * 设置保存排序操作后的按钮状态。 创建日期：(2004-5-27 9:21:58)
	 */
	protected void setSaveSortBottonState() {
		// 设置按钮状态
		bnSave.setEnabled(false);
		bnCancel.setEnabled(false);
		// 更新按钮
		updateButtons();
		// 设置序号列不可编辑
		getPsnList().getTableModel().getItemByKey("showorder").setEdit(false);
		getPsnList().getTableModel().getItemByKey("showorder")
				.setEnabled(false);
	}

	/**
	 * 设置参照值为空。 创建日期：(2004-8-2 15:49:40)
	 * 
	 * @param tableCode
	 *            java.lang.String
	 */
	private void setSelRowRefValueNull(String tableCode) {
		BillItem[] items = getCard().getBillModel().getBodyItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getDataType() == 5) { // 参照类新
				UIRefPane ref = (UIRefPane) items[i].getComponent();
				ref.setPK("");
				ref.setName("");
			}
		}
	}

	/**
	 * 设置row行编辑状态，如果row是-1，表示设置所有的行的编辑状态。 创建日期：(2004-5-20 20:53:39)
	 * 
	 * @param model
	 *            nc.ui.pub.bill.BillModel 要设置的表的billModel
	 * @param row
	 *            int 要设置的行，如果为-1，表示设置所有的行
	 * @param editable
	 *            boolean 是否可编辑的状态
	 */
	private void setTableLineEditable(BillModel model, int row, boolean editable) {
		if (row == -1) {
			// 为-1时递归调用，设置所有行的状态
			int rowCount = getCard().getBillTable().getRowCount();
			for (int i = 0; i < rowCount; i++) {
				setRowState(model, i, editable);
			}
		} else {
			setRowState(model, row, editable);
		}
	}

	/**
	 * 设置树节点选中时按钮状态。 创建日期：(2004-5-14 9:49:28)
	 */
	protected void setTreeSelectButtonState(DefaultMutableTreeNode node) {
		// 当前选中的部门
		CtrlDeptVO dept = (CtrlDeptVO) node.getUserObject();
		// 修改、删除、卡片及添加自助用户
		bnEdit.setEnabled(false);
		bnDel.setEnabled(false);
		bnCard.setEnabled(false);
		bnUpload.setEnabled(false);
		bnAbilityMatch.setEnabled(false);
		bnSmUser.setEnabled(false);
		bnQuery.setEnabled(true);
		// 如果是根结点
		if (node.isRoot()) {
			bnEdit.setEnabled(false);
			bnAdd.setEnabled(false);
			bnSort.setEnabled(false);
		} else if (dept.isControlled()) {
			if (Global.getCorpPK().equals(dept.getPk_corp())) {
				// 如果是可控制部门节点
				if (listSelectRow != -1)
					bnEdit.setEnabled(true);
				bnAdd.setEnabled(true);
			} else {
				bnEdit.setEnabled(false);
				bnAdd.setEnabled(false);
				bnSort.setEnabled(false);
			}
		} else {
			// 不可控制部门节点
			bnEdit.setEnabled(false);
			bnAdd.setEnabled(false);
			bnSort.setEnabled(false);
		}
		if (node.isRoot() || dept.isControlled()) {
			// 根结点或者可控部门按钮
			if (psnList != null && psnList.length > 0) {
				if (Global.getCorpPK().equals(dept.getPk_corp())) {
					if (listSelectRow != -1)
						bnEdit.setEnabled(true);
					bnBatch.setEnabled(true);
					bnIntoDoc.setEnabled(true);
					bnBatchAddSet.setEnabled(getBatchAddSetButtonState());
				} else {
					bnEdit.setEnabled(false);
					bnBatch.setEnabled(false);
					bnIntoDoc.setEnabled(false);
					bnBatchAddSet.setEnabled(false);
					bnSort.setEnabled(false);
				}
				// 人员数大于0时
				bnBook.setEnabled(true);
				bnPrint.setEnabled(true);

			} else {
				bnEdit.setEnabled(false);
				// 人员数等于0时
				bnBatch.setEnabled(false);
				bnBatchAddSet.setEnabled(false);
				bnBook.setEnabled(false);
				bnPrint.setEnabled(false);
				bnIntoDoc.setEnabled(false);
				bnSort.setEnabled(false);
			}
		}
		if (node.isRoot()) {
			bnBook.setEnabled(false);
		}
		if ("600707".equals(getModuleName())
				|| "600710".equals(getModuleName())
				|| "600708".equals(getModuleName())) {
			if (listSelectRow == -1) {// 如果没有选中人员，则不能选择照片导出
				bnExportPic.setEnabled(false);
			} else {
				bnExportPic.setEnabled(true);
			}
		}
		// 更新按钮状态
		updateButtons();
	}

	protected boolean getBatchAddSetButtonState() {
		String bodytablecode = getWorkCard().getCurrentBodyTableCode();
		boolean isEnabled = false;
		if (psnList != null && psnList.length > 0) {
			if (getCard().getTraceTables() != null) {
				if (getCard().getTraceTables().get(bodytablecode) != null) {// getBodyTableCode()
					isEnabled = false;
				} else {
					isEnabled = true;
				}
			}
		}
		if (bodytablecode.equalsIgnoreCase("hi_psndoc_keypsn")) {
			isEnabled = false;
		}
		return isEnabled;
	}

	protected boolean recordNotNullValidate(String tableCode, GeneralVO vo,
			int curRow) throws Exception {
		BillItem[] items = getCard().getBillModel().getBodyItems();
		String key = "";
		for (int i = 0; i < items.length; i++) {
			if (items[i].isShow()) {
				if (vo.getAttributeValue(items[i].getKey()) == null) {
					vo.removeAttributeName(Util.PK_PREFIX + items[i].getKey());
				}
				if (vo.getFieldValue(items[i].getKey()) != null) {
					return true;
				} else if (key == null) {
					key = items[i].getKey();
				}
			}
		}
		// String message = "不能保存空行！";
		String message = nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
				"UPP600700-000127")/* @res "不能保存空行！" */;
		ValidException vException = new ValidException(message);
		vException.setTableCode(tableCode);
		vException.setFieldCode(key);
		vException.setLineNo(curRow);// -1
		traceErrorPoint(vException);
		return false;

	}

	/**
	 * 设置修改行操作按钮按下后的按钮状态。 创建日期：(2004-5-27 9:33:15)
	 */
	protected void setUpdateChildButtonState() {
		// 设置按钮状态
		bnAddChild.setEnabled(false);
		bnAdd.setEnabled(false);
		bnEdit.setEnabled(false);
		bnDel.setEnabled(false);
		bnList.setEnabled(false);
		bnAddChild.setEnabled(false);
		bnUpdateChild.setEnabled(false);
		bnInsertChild.setEnabled(false);
		bnDelChild.setEnabled(false);
		bnSave.setEnabled(true);
		bnCancel.setEnabled(true);
		bnUpload.setEnabled(false);
		bnUpRecord.setEnabled(false);
		bnDownRecord.setEnabled(false);
		bnToFirst.setEnabled(false);
		bnToLast.setEnabled(false);
		bnSubsetMove.setEnabled(false);// v50
		// 更新按钮状态
		updateButtons();
	}

	/**
	 * 为 参照添加条件 --wangkf add
	 * 
	 * @param refmodel
	 * @param whereSql
	 * @return
	 */
	public String setWhereToModel(nc.ui.bd.ref.AbstractRefModel refmodel,
			String whereSql) {

		String whereSqlTemp = refmodel.getWherePart();
		if (whereSqlTemp != null && whereSqlTemp.length() > 0
				&& whereSql != null && whereSql.length() > 0) {
			if (whereSqlTemp.indexOf(whereSql) < 0) {
				whereSqlTemp += " and " + whereSql;
			}
		} else {
			whereSqlTemp = whereSql;
		}
		refmodel.setWherePart(whereSqlTemp);

		if (refmodel instanceof SpaDeptdocRefTreeModel) {
			SpaDeptdocRefTreeModel refTreemodel = (SpaDeptdocRefTreeModel) refmodel;
			refTreemodel.setClassWherePart(whereSql);
		} else if (refmodel instanceof SpaDeptdocRefGridTreeModel) {
			SpaDeptdocRefGridTreeModel refTreemodel = (SpaDeptdocRefGridTreeModel) refmodel;
			refTreemodel.setClassWherePart(whereSql);
		}
		return whereSqlTemp;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-2 17:12:11)
	 * 
	 * @param name
	 *            java.lang.String
	 */
	protected void showPanel(String name) {
		// CardLayout layout = (CardLayout) getCardPanelDown().getLayout();
		// layout.show(getCardPanelDown(), name);
	}

	/**
	 * v50 add 全集团内进行排序相应事件。
	 * 
	 * @throws java.lang.Exception
	 */
	private void EditShowOrder() throws java.lang.Exception {
		getPsnList().setBodyData(psnList);
		getPsnList().removeTableSortListener();
		getPsnList().getTableModel().execLoadFormula();
		showordervisible = getPsnList().getTableModel().getItemByKey(
				"showorder").isShow();
		if (!showordervisible)
			getPsnList().showTableCol("showorder");// hideTableCol(strKey).getTableModel().getItemByKey("showorder").setShow(true);
		// getPsnList().getTableModel().getItemByKey("showorder").setEdit(true);
		// getPsnList().getTableModel().getItemByKey("showorder").setEnabled(true);
	}

	/**
	 * 人员信息子表行选中事件处理函数 Called whenever the value of the selection changes.
	 * 
	 * @param e
	 *            the event that characterizes the change.
	 */
	private void subTable_valueChanged(javax.swing.event.ListSelectionEvent e) {
		if (getCard().getBodyTabbedPane().isEnabled()) {
			setChildButtonState();// 不是编辑状态，设置按钮状态
			updateButtons();
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-5 10:57:58)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void traceErrorPoint(ValidException ve) throws java.lang.Exception {
		// 具体化报错信息位置
		String tableCode = ve.getTableCode();
		String tableName = null;
		// 将界面定位到报错点
		if (getEditType() == EDIT_MAIN || getEditType() == EDIT_REF
				|| getEditType() == EDIT_RETURN) {
			tableName = getCard().getBillData().getHeadTableName(tableCode);
			getCard().getHeadTabbedPane().setSelectedIndex(
					tableCode.equals("bd_psndoc") ? 1 : 0);
		} else {
			tableName = getCard().getBillData().getBodyTableName(tableCode);
		}
		// 将焦点定位到具体报错组件
		String fieldCode = ve.getFieldCode();
		if (fieldCode != null && !"id".equalsIgnoreCase(fieldCode)) {
			// 报错的BillItem
			BillItem errItem = getBillItem(tableCode + "." + fieldCode);
			if (errItem != null && errItem.getPos() == BillItem.HEAD) {

				// 表头的组件进行定位，首先要获得焦点
				Component errComponent = errItem.getComponent();

				// 区分字符串类型的输入框，防止发生错误时的提示框引发重复的trigger
				errComponent.requestFocus();
				if (errComponent instanceof UIRefPane)
					// 是Ref的要全选
					((UIRefPane) errComponent).getUITextField().selectAll();
			}
		}

		// 重新组织错误信息，以便统一显示
		String message = tableName;
		if (ve.getLineNo() != -1)
			message += nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
					"UPP600704-000091")/* @res "第" */
					+ (ve.getLineNo() + 1)
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000092")/* @res "行" */;
		message += ":" + ve.getMessage();

		if (ve.getLevel() == ValidException.LEVEL_CANACCEPT) {
			// 若是可以容忍的异常可以继续保存。仅提示而已。
			showWarningMessage(message);
		} else if (ve.getLevel() == ValidException.LEVEL_DECIDEBYUSER) {
			if (showOkCancelMessage(ve.getMessage()
					+ "\n"
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("6007",
							"UPT6007-000237")/* 是否继续？ */) == UIDialog.ID_CANCEL) {
				throw new Exception("HAVEPOPMESSAGE" + message);// 继续抛出异常是为了中止方法中后面的操作。但不再提示。
			}
		} else {
			// 重新抛出含有错误提示的异常
			throw new Exception(message);
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-2 11:41:11)
	 * 
	 * @param infoVOs
	 *            nc.vo.hi.hi_301.GeneralVO[]
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected void transPkToName(String tableCode, GeneralVO[] infoVOs)
			throws java.lang.Exception {
		if (infoVOs == null || infoVOs.length < 1)
			return;

		for (int i = 0; i < infoVOs.length; i++) {
			transPkToName(tableCode, infoVOs[i]);
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-2 11:42:26)
	 * 
	 * @param infoVOs
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void transPkToName(String tableCode, GeneralVO infoVO)
			throws java.lang.Exception {
		if (infoVO == null) {
			return;
		}
		String[] fieldCodes = infoVO.getAttributeNames();
		for (int i = 0; i < fieldCodes.length; i++) {
			BillItem item = getBillItem(tableCode + "." + fieldCodes[i]);
			if (item == null)
				continue;
			switch (item.getDataType()) {
			case BillItem.UFREF: {
				Object value = infoVO.getAttributeValue(fieldCodes[i]);
				UIRefPane ref = (UIRefPane) item.getComponent();

				String pk_corp = (String) infoVO.getFieldValue("pk_corp");
				if (ref != null
						&& (pk_corp != null || "".equals(pk_corp))
						&& !tableCode.equalsIgnoreCase("hi_psndoc_ctrt") // v5.0
						// 子集中显示pk_corp，不需再次设置
						&& !tableCode.equalsIgnoreCase("hi_psndoc_dimission")
						&& !tableCode.equalsIgnoreCase("hi_psndoc_retire")
						&& !tableCode.equalsIgnoreCase("hi_psndoc_training")
						&& !tableCode.equalsIgnoreCase("hi_psndoc_orgpsn")
						&& !tableCode.equalsIgnoreCase("hi_psndoc_deptchg")
						&& !tableCode.equalsIgnoreCase("hi_psndoc_psnchg")
						&& !tableCode.equalsIgnoreCase("hi_psndoc_keypsn")) {
					ref.setPk_corp(pk_corp);
					ref.getRefModel().setPk_corp(pk_corp);
				}
				// 取得参照的可编辑性
				boolean isEnable = ref.getRefModel().isRefEnable();
				// 设置参照不可编辑，这样参照不启用数据权限控制;包含封存数据
				ref.getRefModel().setisRefEnable(false);
				ref.setPK(value);
				String name = ref.getRefName();
				if (name == null) {
					name = Util.retrieveNameOf(tableCode + "." + fieldCodes[i],
							(String) value);
				}
				// 重新设置参照的可编辑性
				ref.getRefModel().setisRefEnable(isEnable);
				infoVO.setAttributeValue(fieldCodes[i], name);
				infoVO.setAttributeValue(Util.PK_PREFIX + fieldCodes[i], value);
			}
				break;
			case BillItem.COMBO: {
				String refType = item.getRefType();
				if (refType == null)
					continue;
				Object value = infoVO.getAttributeValue(fieldCodes[i]);
				if (value == null) {
					continue;
				}

				java.util.StringTokenizer token = new StringTokenizer(refType,
						",");
				while (token.hasMoreTokens()) {
					String temp = (String) token.nextElement();
					int index = temp.indexOf(":");
					if (index > 0) {
						if (value.toString().equals(temp.substring(0, index))) {
							infoVO.setAttributeValue(fieldCodes[i], temp
									.substring(index + 1));
							infoVO.setAttributeValue(Util.PK_PREFIX
									+ fieldCodes[i], value);
							break;
						}
					}
				}
			}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * V35 增加 子类重载此方法
	 * 
	 * @param btnGroupType
	 */
	protected void switchButtonGroup(int btnGroupType) {
		try {
			if (btnGroupType == CARD_GROUP) {// 卡片界面
				getIsSelSet().setVisible(false);
				getincludeCancleDept().setVisible(false);
				getincludeChildDeptPsn().setVisible(false);
				getDeptTree().setEnabled(false);
				getIncludeHisPersonGroup().setVisible(false);
				getIncludeHisPerson().setVisible(false);
				getUISplitPane().setDividerLocation(0);
				getUISplitPane().setEnabled(false);

				getUISplitPaneVertical().setDividerLocation(0);
				getUISplitPaneVertical().setEnabled(false);

				BillCardLayout layout = (BillCardLayout) getCard().getLayout();

				// 参数值为-1时,取消最大化,还原为默认显示方式
				// if(getEditType()==EDIT_MAIN){
				// getCard().setPosMaximized(BillItem.HEAD);
				// }else if(getEditType()==EDIT_SUB){
				// getCard().setPosMaximized(-1);
				// }else{
				// getCard().setPosMaximized(-1);
				// }
				getCard().setShowMenuBar(true);
				expendChildEditButons();
				setButtons(grpCardDisplayCollect);

				bnCard.removeChildButton(bnBatchExport);
			} else if (btnGroupType == LIST_GROUP) {// 列表界面
				getIsSelSet().setVisible(true);
				getincludeCancleDept().setVisible(true);
				getincludeChildDeptPsn().setVisible(true);
				getDeptTree().setEnabled(true);
				getIncludeHisPersonGroup().setVisible(false);
				getIncludeHisPerson().setVisible(false);

				getUISplitPane().setDividerLocation(180);
				getUISplitPane().setDividerSize(4);
				getUISplitPane().setEnabled(true);

				getUISplitPaneVertical().setDividerLocation(splitLocation);
				getUISplitPaneVertical().setEnabled(true);
				getCard().setShowMenuBar(false);
				getCard().setPosMaximized(BillItem.BODY);
				setButtons(grpCollect);// 列表组
				bnCard.addChildButton(bnBatchExport);

			} else if (btnGroupType == LIST_INIT_GROUP) {// 列表界面初始状态
				bnEmployeeReference.addChileButtons(grpEmployeeReference);
				setButtons(grpCollect);// 列表组
				getIncludeHisPersonGroup().setVisible(false);
				getIncludeHisPerson().setVisible(false);

				getUISplitPaneVertical().setDividerLocation(SPLIT_MAX);
				getUISplitPaneVertical().setEnabled(true);
				boolean show = getCard().isShowMenuBar();
				getCard().setShowMenuBar(!show);
				getCard().setShowMenuBar(false);
				getCard().setPosMaximized(BillItem.BODY);
				setButtons(grpCollect);// 列表组
			}
			updateButtons();
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}

	}

	/**
	 * V35 增加
	 * 
	 * @param btnState
	 */
	protected void setButtonsState(int btnState) {
		if (btnState == CARD_EDIT) {
			for (int i = 0; i < grpCardDisplayCollect.length; i++) {
				grpCardDisplayCollect[i].setEnabled(false);
			}
			bnSave.setEnabled(true);
			bnCancel.setEnabled(true);
			bnUpload.setEnabled(false);
			//
			isShowBillTemp = true;
		} else if (btnState == CARD_MAIN_BROWSE) {
			for (int i = 0; i < grpCardDisplayCollect.length; i++) {
				grpCardDisplayCollect[i].setEnabled(true);
			}
			bnSave.setEnabled(false);
			bnCancel.setEnabled(false);
			setUpDownState();
			if (getEditType() == EDIT_MAIN || getEditType() == EDIT_RETURN) {
				if (isAdding()) {
					bnChildEdit.setEnabled(false);
					bnEdit.setEnabled(false);
					bnDel.setEnabled(false);
					bnUpload.setEnabled(false);
				} else {
					bnChildEdit.setEnabled(true);
					bnEdit.setEnabled(true);
					bnDel.setEnabled(true);
					bnUpload.setEnabled(true);
				}
			}
			setChildButtonState();
			if (person != null) {
				String belong_pk_corp = null;
				if (listSelectRow >= 0) {
					belong_pk_corp = (String) psnList[listSelectRow]
							.getAttributeValue("belong_pk_corp");// 归属公司
				}
				if (!Global.getCorpPK().equalsIgnoreCase(belong_pk_corp)) {
					bnEdit.setEnabled(false);
					bnDel.setEnabled(false);
					bnChildEdit.setEnabled(false);
					bnSubsetMove.setEnabled(false);
					bnUpload.setEnabled(false);
				} else {
					bnEdit.setEnabled(true);
					bnDel.setEnabled(true);
					bnChildEdit.setEnabled(true);
					bnUpload.setEnabled(true);
				}
			}
			//
			isShowBillTemp = true;
		} else if (btnState == LIST_INIT) {
			for (int i = 0; i < grpCollect.length; i++) {
				grpCollect[i].setEnabled(true);
			}
			//
			isShowBillTemp = false;
		} else if (btnState == LIST_BROWSE) {
			if (listSelectRow != -1) {// 针对某个人时对按钮状态的控制---begin--
				GeneralVO psn = ((GeneralVO[]) getPsnList().getBodyData())[listSelectRow];
				String belong_pk_corp = (String) psn
						.getAttributeValue("belong_pk_corp");// 归属公司
				bnCard.setEnabled(true);
				// 人员列表大于1才能调整顺序
				int count = getPsnList().getTable().getRowCount();
				if (count > 0) {
					bnSort.setEnabled(true);
				} else {
					bnSort.setEnabled(false);
				}
				if (!Global.getCorpPK().equalsIgnoreCase(belong_pk_corp)) {
					bnEdit.setEnabled(false);
					bnDel.setEnabled(false);
					bnChildEdit.setEnabled(false);
					bnBatchAddSet.setEnabled(false);
					bnSort.setEnabled(false);
					// bnUpload.setEnabled(false);
					// 即使非登录公司，也可以查看附件。dusx
					bnUpload.setEnabled(true);
				} else {
					bnEdit.setEnabled(true);
					bnDel.setEnabled(true);
					bnBatchAddSet.setEnabled(getBatchAddSetButtonState());// 批量增加子集判断
					bnUpload.setEnabled(true);
				}
			}
			//
			isShowBillTemp = false;

		} else if (btnState == CARD_CHILD_BROWSE) {
			bnSave.setEnabled(false);
			bnCancel.setEnabled(false);
			setChildButtonState();
			if (person != null) {
				String belong_pk_corp = null;
				if (listSelectRow >= 0) {
					belong_pk_corp = (String) psnList[listSelectRow]
							.getAttributeValue("belong_pk_corp");// 归属公司
				}
				if (!Global.getCorpPK().equalsIgnoreCase(belong_pk_corp)) {
					bnSubsetMove.setEnabled(false);
				}
			}
			//
			isShowBillTemp = true;
		}
		updateButtons();
	}

	/**
	 * V35 new Add
	 * 
	 */
	protected void setUpDownState() {
		// 比较当前人（person）变量在psnList数组中的位置：下列方法需要修改，直接按当前行修改
		if (getPosInList(person, psnList) == 1) {
			bnUpRecord.setEnabled(false);
			bnToFirst.setEnabled(false);
		} else {
			bnUpRecord.setEnabled(true);
			bnToFirst.setEnabled(true);
		}
		if ((psnList == null || psnList.length == 0)
				|| getPosInList(person, psnList) == psnList.length) {
			bnDownRecord.setEnabled(false);
			bnToLast.setEnabled(false);
		} else {
			bnDownRecord.setEnabled(true);
			bnToLast.setEnabled(true);
		}
		if (listSelectRow < 0) {
			bnUpRecord.setEnabled(false);
			bnToFirst.setEnabled(false);
			bnDownRecord.setEnabled(false);
			bnToLast.setEnabled(false);
		}
	}

	/**
	 * V35 new Add 人员引用的按钮状态
	 * 
	 */
	protected void setRefButtoState() {
		bnEmployeeReference.setEnabled(true);
		bnApplicate.setEnabled(true);
		bnAffirm.setEnabled(true);
		bnBatchApplicate.setEnabled(true);
		bnQueryAffirm.setEnabled(true);
		if (getCmbPsncl().getSelectedItem() != null
				&& ((Item) getCmbPsncl().getSelectedItem()).getValue() == 5) {
			bnCancelAffirm.setEnabled(true);
		} else {
			bnCancelAffirm.setEnabled(false);
		}
	}

	/**
	 * V35 增加
	 * 
	 * @param person
	 * @param psnList
	 * @return
	 */
	protected int getPosInList(PersonEAVO person, GeneralVO[] psnList) {
		if (psnList == null || psnList.length == 0) {
			return 1;
		}
		int curpsnpos = 1;
		if (isAdding()) {// 新增加人时的处理 listSelectRow == -1
			if (person != null) {
				String curpsnpk = person.getPk_psndoc();
				for (int i = 0; i < psnList.length; i++) {
					if (curpsnpk.equalsIgnoreCase(psnList[i].getAttributeValue(
							"pk_psndoc").toString())) {
						curpsnpos = i + 1;
					}
				}
			} else {
				curpsnpos = listSelectRow + 1;// psnList.length;
			}
		} else {// 在列表中选中某人时的处理
			curpsnpos = listSelectRow + 1;
		}

		return curpsnpos;
	}

	/**
	 * 加载各子集信息
	 * 
	 * @param selectRow
	 * @param tablecode
	 * @return
	 */
	protected PersonEAVO loadPsnChildInfo(int selectRow, String tablecode) {
		try {
			if (selectRow == -1) {
				getCard().getBillModel().setBodyDataVO(null);
				return null;
			}
			if (person == null) {
				String selectPkPsn = (String) psnList[selectRow]
						.getAttributeValue("pk_psndoc");
				String selectpkpsnbasdoc = (String) psnList[selectRow]
						.getAttributeValue("pk_psnbasdoc");
				person = new PersonEAVO();
				person.setPk_psndoc(selectPkPsn);
				person.setPk_psnbasdoc(selectpkpsnbasdoc);
			}
			// 是否是业务子集
			boolean isTraceTable = getCard().getTraceTables().get(tablecode) != null;
			String pk_corp = (String) psnList[selectRow]
					.getAttributeValue("man_pk_corp");
			String currentcorp = (String) psnList[selectRow]
					.getAttributeValue("pk_corp");
			if (pk_corp == null) {
				pk_corp = Global.getCorpPK();
			}
			// 判断是否是返聘或者再聘
			String indoc = "N";
			boolean isreturn = false;
			if (person != null) {
				if (listSelectRow >= 0) {
					Object o = psnList[listSelectRow]
							.getAttributeValue("indocflag");
					if (o != null) {
						isreturn = indoc.equals(o) ? false : true;
					}
				}
			}
			// v50 add
			if (isTraceTable) {// 如果是业务子集
				// 查询该业务子集是否允许查看历史
				boolean look = HIDelegator.getPsnInf().isTraceTableLookHistory(
						tablecode);
				// 得到界面中用户是否选择查看历史数据
				boolean islookhistory = IsLookHistory();

				// 是否是归属公司人员用belong_pk_corp,而不是pk_corp
				boolean isbelongcorp = psnList[selectRow].getAttributeValue(
						"belong_pk_corp").equals(Global.getCorpPK());
				// 是否兼职人员
				boolean isPart = psnList[selectRow].getJobTypeFlag().intValue() == 0 ? false
						: true;
				// 返聘再聘人员在采集节点不能查看业务子集
				if (isreturn && getModuleName().equalsIgnoreCase("600704")) {
					SubTable subtable = new SubTable();
					subtable.setTableCode(tablecode);
					GeneralVO[] infoVOs = new GeneralVO[0];
					subtable.setRecordArray(infoVOs);
					person.addSubtable(subtable);
				} else {
					if (isPart) {// 兼职人员
						// 该业务子集允许查看历史且用户选中查看历史并且该人员是本公司人员，则重新查询子集数据，因为是否显示历史可能变化
						if (look && islookhistory && isbelongcorp) {// 本公司兼职人员
							// 如果已经查询过，则清除该业务子集数据
							if (person.getSubtables().get(tablecode) != null) {
								person.removeSubtable(tablecode);
							}
							// 查询该表的记录，业务子集允许查看历史的数据
							loadPsnChildSub(pk_corp, person.getPk_psnbasdoc(),
									tablecode, isTraceTable, islookhistory,
									person.getPk_psndoc());
						} else {// 跨公司兼职人员
							if (tablecode
									.equalsIgnoreCase("hi_psndoc_dimission")
									|| tablecode
											.equalsIgnoreCase("hi_psndoc_deptchg")
									|| tablecode
											.equalsIgnoreCase("hi_psndoc_retire")
									|| tablecode
											.equalsIgnoreCase("hi_psndoc_psnchg")) {// 对于跨单位兼职，在兼职公司中，不能查看兼职人员的任职信息、离职信息、离退休情况、流动情况
								// 如果已经查询过，则清除该业务子集数据
								if (person.getSubtables().get(tablecode) != null) {
									person.removeSubtable(tablecode);
								}
								SubTable subtable = new SubTable();
								subtable.setTableCode(tablecode);
								person.addSubtable(subtable);
							} else {
								// 如果已经查询过，则清除该业务子集数据
								if (person.getSubtables().get(tablecode) != null) {
									person.removeSubtable(tablecode);
								}
								// 加载当前人员的该表的信息记录，业务子不查看历史
								// loadPartPsnChildSub(person.getPk_psndoc(),Global.getCorpPK(),tablecode);
								loadPartPsnChildSub(person.getPk_psndoc(),
										currentcorp, tablecode);
							}
						}
					} else {// 非兼职人员
						// 该业务子集允许查看历史且用户选中查看历史并且该人员是本公司人员，则重新查询子集数据，因为是否显示历史可能变化
						if (look && islookhistory && isbelongcorp) {
							// 如果已经查询过，则清除该业务子集数据
							if (person.getSubtables().get(tablecode) != null) {
								person.removeSubtable(tablecode);
							}
							// 查询该表的记录，业务子集允许查看历史的数据
							loadPsnChildSub(pk_corp, person.getPk_psnbasdoc(),
									tablecode, isTraceTable, islookhistory,
									person.getPk_psndoc());
						} else {
							// 如果已经查询过，则清除该业务子集数据
							if (person.getSubtables().get(tablecode) != null) {
								person.removeSubtable(tablecode);
							}
							// 加载当前人员的该表的信息记录，业务子不查看历史
							loadPsnChildSub(pk_corp, person.getPk_psnbasdoc(),
									tablecode, isTraceTable, false, person
											.getPk_psndoc());
						}
					}
				}
			} else {// 如果非业务子集
				if (person.getSubtables().get(tablecode) != null) {
					person.removeSubtable(tablecode);
				}
				// 加载当前人员的该表的信息记录，非业务子集默认查看历史
				loadPsnChildSub(pk_corp, person.getPk_psnbasdoc(), tablecode,
						isTraceTable, true, person.getPk_psndoc());
			}
			CircularlyAccessibleValueObject[] records = person
					.getTableVO(tablecode);// 当前信息集的所有记录
			getCard().getBillModel().setBodyDataVO(records);// 设置表格内容

		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
		return person;
	}

	/**
	 * 加载人员子集信息
	 * 
	 * @param pk_psndoc
	 * @param pk_psnbasdoc
	 * @param tablecode
	 * @param isTraceTable
	 * @param lookhistory
	 */
	protected void loadPsnChildSub(String pk_corp, String pk_psnbasdoc,
			String tablecode, boolean isTraceTable, boolean lookhistory,
			String pk_psndoc) {
		try {
			GeneralVO[] infoVOs = null;
			boolean look = lookhistory;
			// 加载当前人员的该表的信息记录，非业务子集默认查看历史
			if ("600704".equalsIgnoreCase(getModuleName())) {
				look = true;
			}
			infoVOs = HIDelegator.getPsnInf().queryPersonInfo(pk_corp,
					pk_psnbasdoc, tablecode, isTraceTable, look, pk_psndoc,
					Global.getCorpPK());
			if (!"E0020603".equals(getModuleName())
					&& !"E0020303".equals(getModuleName())
					&& !"E0041203".equals(getModuleName())) {
				// 当查看其他公司人员数据时，公司自定义项置为空
				if (person.getPsndocVO() != null) {
					String psncorp = (String) person.getPsndocVO()
							.getAttributeValue("pk_corp");
					if (!Global.getCorpPK().equals(psncorp)) {
						infoVOs = setDefItem(infoVOs, "");
					}
				}
			}
			SubTable subtable = new SubTable();
			subtable.setTableCode(tablecode);
			if (infoVOs != null) {
				transPkToName(tablecode, infoVOs);
				subtable.setRecordArray(infoVOs);
			}
			person.addSubtable(subtable);
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * 加载人员子集信息
	 * 
	 * @param pk_psndoc
	 * @param pk_psnbasdoc
	 * @param tablecode
	 * @param isTraceTable
	 * @param lookhistory
	 */
	protected void loadPartPsnChildSub(String pk_psndoc, String pk_corp,
			String tablecode) {
		try {
			GeneralVO[] infoVOs = null;
			// 加载当前人员的该表的信息记录，非业务子集默认查看历史
			infoVOs = HIDelegator.getPsnInf().queryPartPersonChildInfo(
					pk_psndoc, pk_corp, tablecode);
			if (!"E0020603".equals(getModuleName())
					&& !"E0020303".equals(getModuleName())
					&& !"E0041203".equals(getModuleName())) {
				// 当查看其他公司人员数据时，公司自定义项置为空
				if (person.getPsndocVO() != null) {
					String psncorp = (String) person.getPsndocVO()
							.getAttributeValue("pk_corp");
					if (!Global.getCorpPK().equals(psncorp)) {
						infoVOs = setDefItem(infoVOs, "");
					}
				}
			}
			SubTable subtable = new SubTable();
			subtable.setTableCode(tablecode);
			if (infoVOs != null) {
				transPkToName(tablecode, infoVOs);
				subtable.setRecordArray(infoVOs);
			}
			person.addSubtable(subtable);
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
	}

	private String loadedToTempPkCorp = Global.getCorpPK();// 用于记录切换单据模板中参照中的公司

	/**
	 * 
	 * @param selectRow
	 * @param tablecode
	 * @return
	 */
	protected PersonEAVO loadPsnMainInfo(int selectRow, String tablecode) {
		try {

			if (psnList == null || psnList.length == 0 || selectRow == -1) {
				getCard().getBillModel().setBodyDataVO(null);
				return null;
			}
			String selectPkPsn = (String) psnList[selectRow]
					.getAttributeValue("pk_psndoc");
			String selectPkPsnBas = (String) psnList[selectRow]
					.getAttributeValue("pk_psnbasdoc");
			String key = getPersonsKey(psnList[selectRow]);
			// 此处需要修改，以能用一个合适的key区分同一个人的多个信息：如果是归属范围为在职用任职记录的主键
			person = (PersonEAVO) persons.get(key);
			if (person == null) {
				person = new PersonEAVO();
				person.setPk_psndoc(selectPkPsn);
				person.setPk_psnbasdoc(selectPkPsnBas);
				persons.put(key, person);
			}
			int jobtype = 0;
			if (isMaintain && listSelectRow >= 0 && !isEmployeeRef()) {
				jobtype = ((Integer) psnList[listSelectRow]
						.getAttributeValue("jobtypeflag")).intValue();
			}
			String psncorppk = (String) psnList[listSelectRow]
					.getAttributeValue("pk_corp");
			String pk_psnbasdoc = (String) psnList[listSelectRow]
					.getAttributeValue("pk_psnbasdoc");
			String pk_psndoc = (String) psnList[listSelectRow]
					.getAttributeValue("pk_psndoc");// getTempPsnPk();
			String psncorp = psncorppk;
			if (jobtype > 0) {// 兼职或借调人员
				GeneralVO[] vos = HIDelegator.getPsnInf().queryMainPersonInfo(
						pk_psndoc, Global.getCorpPK(), "bd_psndoc",
						GlobalTool.getFuncParserWithoutWa());
				psncorp = (String) vos[0].getAttributeValue("pk_corp");// 人员实际公司
				isPartPsnCurCorp = !(psncorppk.equals(psncorp));// 判断兼职人员是否为其他公司在当前查询公司兼职的
			} else {
				isPartPsnCurCorp = false;//
			}
			// 不替换参照了：如果当前公司非人员所在公司，则部门参照显示不出来数据。
			// if (isPartPsnCurCorp) {
			// changeBillTempRef();
			// loadedToTempPkCorp = psncorppk;
			// }
			String[] tableCodes = getCard().getBillData().getHeadTableCodes();
			for (int i = 0; i < tableCodes.length; i++) {
				loadPersonMain(pk_psnbasdoc, pk_psndoc, tableCodes[i]);
			}
			getCard().execHeadLoadFormulas();
			getCard().setEnabled(false);
			String psnname = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000090")/* @res "当前选中人员是：" */
					+ (String) psnList[listSelectRow]
							.getAttributeValue("psnname");
			showHintMessage(psnname);

		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
		return person;
	}

	/**
	 * V35 add
	 * 
	 * @param curselectRow
	 * @return
	 * @throws Exception
	 */
	protected PersonEAVO loadPsnInfo(int curselectRow) throws Exception {
		try {
			isLoadedBasicData = false;
			isLoadedAccData = false;
			BillItem item = getBillItem("bd_psndoc.pk_om_job");
			if (item != null) {
				UIRefPane ref = (UIRefPane) item.getComponent();
				if (ref != null) {
					nc.ui.bd.ref.AbstractRefModel m = ref.getRefModel();
					if (m instanceof nc.ui.hi.hi_301.ref.JobRef) {
						nc.ui.hi.hi_301.ref.JobRef refm = new nc.ui.hi.hi_301.ref.JobRef();
						refm.setPk_deptdoc((String) null);
						refm.setCorpPK(Global.getCorpPK());
						ref.setRefModel(refm);
					}
				}
			}
			setAdding(false);// 设置编辑状态
			loadPsnMainInfo(curselectRow, null);
			if (!isEmployeeRef()) {
				String tableCode = getBodyTableCode();
				getCard().setBodyMenuShow(tableCode, false);
				loadPsnChildInfo(curselectRow, tableCode);
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
		return person;
	}

	/**
	 * 
	 * @param eavo
	 * @param psnLists
	 * @return
	 */
	private int getRowInPsnList(PersonEAVO eavo, GeneralVO[] psnLists) {
		if (psnLists == null || psnLists.length < 1) {
			return -1;
		}
		String key = getPersonsKey(eavo.getPsndocVO());
		int pos = -1;
		for (int i = 0; i < psnLists.length; i++) {
			String comparedKey = getPersonsKey(psnLists[i]);
			if (key.equalsIgnoreCase(comparedKey)) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	private String getPersonsKey(GeneralVO oneperson) {
		String key = (String) oneperson.getAttributeValue("pk_psndoc");
		// V55 add
		if (oneperson.getAttributeValue("pk_deptdoc") != null) {
			key += (String) oneperson.getAttributeValue("pk_deptdoc");
		}
		return key;
	}

	private boolean isEdit = false;

	protected void setEditState(boolean isEdit) {
		this.isEdit = isEdit;
	}

	private boolean isEditing() {
		return isEdit;
	}
	
	//zhf add
	protected boolean isEditStatu(){
		return isEdit || adding;
	}

	private int intButtonGroup = LIST_GROUP;

	public int getButtonGroup() {
		return intButtonGroup;
	}

	public void setButtonGroup(int buttonGroup) {
		intButtonGroup = buttonGroup;
	}

	public BillModel getPsnListModelDlg() {
		// 获取当前显示项
		Pair[] items = getListItems();
		Vector vExtension = new Vector();
		// 生成表头
		BillItem[] biaBody = new BillItem[items.length + 1];
		biaBody[0] = new BillItem();
		biaBody[0].setName(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"600704", "UPP600704-000033"));// "选择"
		biaBody[0].setWidth(50);
		biaBody[0].setEnabled(true);
		biaBody[0].setEdit(true);
		biaBody[0].setShow(true);
		biaBody[0].setNull(false);
		biaBody[0].setDataType(BillItem.BOOLEAN);
		((UICheckBox) biaBody[0].getComponent())
				.setHorizontalAlignment(UICheckBox.CENTER);
		vExtension.addElement(biaBody[0]);
		for (int i = 0; i < items.length; i++) {
			if (items[i].getDataType() == CommonValue.DATATYPE_UFREF) {//
				// 显示
				biaBody[i + 1] = new BillItem();
				biaBody[i + 1].setName(items[i].getName());
				biaBody[i + 1].setKey(items[i].getCode() + "_name");
				biaBody[i + 1].setWidth(100);
				biaBody[i + 1].setEnabled(true);
				biaBody[i + 1].setEdit(false);
				biaBody[i + 1].setShow(items[i].isVisible());
				biaBody[i + 1].setNull(false);
				String[] loadFormula = getLoadFormula(items[i]);
				biaBody[i + 1].setLoadFormula(loadFormula);
				vExtension.addElement(biaBody[i + 1]);
				// 下面是主键
				BillItem billItem = new BillItem();
				billItem.setName(items[i].getName() + "主键");
				billItem.setKey(items[i].getCode());
				billItem.setWidth(100);
				billItem.setEnabled(true);
				billItem.setEdit(false);
				billItem.setShow(false);// debug false
				billItem.setNull(false);
				vExtension.addElement(billItem);
			} else {
				biaBody[i + 1] = new BillItem();
				biaBody[i + 1].setName(items[i].getName());
				if (items[i].getDataType() == CommonValue.DATATYPE_BOOLEAN) {
					biaBody[i + 1].setDataType(BillItem.BOOLEAN);
				} else {
					biaBody[i + 1].setDataType(BillItem.STRING);
				}
				biaBody[i + 1].setKey(items[i].getCode());
				biaBody[i + 1].setWidth(100);
				biaBody[i + 1].setEnabled(true);
				biaBody[i + 1].setEdit(false);
				biaBody[i + 1].setShow(items[i].isVisible());
				biaBody[i + 1].setNull(false);
				vExtension.addElement(biaBody[i + 1]);
			}
		}
		biaBody = new BillItem[vExtension.size()];
		vExtension.copyInto(biaBody);

		// 设置
		BillModel billModel = new BillModel();
		billModel.setBodyItems(biaBody);

		return billModel;
	}

	private HashMap hmIntoDoc = null;// 存放经过转入人员档案的数据

	public final String INTO_DOC = "into_doc";// 当前列表中转入人员档案的数据

	public final String NOT_INTO_DOC = "not_into_doc";// 当前列表中不转入人员档案的数据

	public HashMap getHmIntoDoc() {
		return hmIntoDoc;
	}

	public void setHmIntoDoc(HashMap hmIntoDoc) {
		this.hmIntoDoc = hmIntoDoc;
	}

	public GeneralVO[] getPsnListData() {
		return psnList;
	}

	/**
	 * 得到选中的数据列表
	 * 
	 * @return
	 */
	public GeneralVO[] getSelectPsnListData() {
		int[] rows = getPsnList().getHeadSelectedRows();
		if (rows.length < 1)
			return null;
		Vector v = new Vector();
		for (int i = 0; i < rows.length; i++) {
			v.add(psnList[rows[i]]);
		}
		GeneralVO[] selectpsnList = new GeneralVO[v.size()];
		v.copyInto(selectpsnList);

		return selectpsnList;
	}

	/**
	 * 得到本公司选中的数据列表
	 * 
	 * @return
	 */
	public GeneralVO[] getSelectPsnListDataOfMyCorp() {
		int[] rows = getPsnList().getHeadSelectedRows();
		if (rows.length < 1)
			return null;
		Vector v = new Vector();
		for (int i = 0; i < rows.length; i++) {
			if (Global.getCorpPK().equals(
					(String) psnList[rows[i]]
							.getAttributeValue("belong_pk_corp"))
					&& Global.getCorpPK().equals(
							(String) psnList[rows[i]]
									.getAttributeValue("pk_corp"))) {
				v.add(psnList[rows[i]]);
			}
		}
		GeneralVO[] selectpsnList = new GeneralVO[v.size()];
		v.copyInto(selectpsnList);

		return selectpsnList;
	}

	private nc.ui.hi.hi_301.IntoDocDlg ivjIntoDocDlg = null;

	protected nc.ui.hi.hi_301.IntoDocDlg getIntoDocDlg() {
		try {
			ivjIntoDocDlg = new nc.ui.hi.hi_301.IntoDocDlg(this);
			ivjIntoDocDlg.setName("IntoDocDlg");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		return ivjIntoDocDlg;
	}

	private nc.ui.hi.hi_301.ListItemSetDlg ivjListItemSetDlg = null;

	protected nc.ui.hi.hi_301.ListItemSetDlg getListItemSetDlg() {
		try {
			ivjListItemSetDlg = new nc.ui.hi.hi_301.ListItemSetDlg(this);
			ivjListItemSetDlg.setName("ListItemSetDlg");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		return ivjListItemSetDlg;
	}

	private nc.ui.hi.hi_301.RefAffirmDlg ivjRefAffirmDlg = null;

	protected nc.ui.hi.hi_301.RefAffirmDlg getRefAffirmDlg() {
		try {
			ivjRefAffirmDlg = new nc.ui.hi.hi_301.RefAffirmDlg(this, false);
			ivjRefAffirmDlg.setName("RefAffirmDlg");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		return ivjRefAffirmDlg;
	}

	private GeneralVO[] selectItemVos;

	/**
	 * @return 返回 selectItemVos。
	 */
	public GeneralVO[] getSelectItemVos() {
		return selectItemVos;
	}

	/**
	 * @param selectItemVos
	 *            要设置的 selectItemVos。
	 */
	public void setSelectItemVos(GeneralVO[] selectItemVos) {
		this.selectItemVos = selectItemVos;
	}

	private Pair[] listItemsDefault = new Pair[] {// 维护和不走入职的采集
			new Pair("psncode", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000025")/* @res "人员编码" */,
					"bd_psndoc", true),
			new Pair("psnname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000064")/* @res "人员姓名" */,
					"bd_psnbasdoc", true),
			new Pair("unitname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000047")/* @res "公司名称" */, "bd_corp",
					true),
			new Pair("deptcode", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000046")/* @res "部门编码" */,
					"bd_deptdoc", true),
			new Pair("deptname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000001")/* @res "部门名称" */,
					"bd_deptdoc", true),
			new Pair("psnclassname",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"upt600704-000042")/* @res "人员类别" */, true),
			new Pair("jobname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000002")/* @res "岗位名称" */, true),
			new Pair("jobtypename",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000065")/* @res "任职类型" */, true),
			new Pair("showorder", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPT600704-000325")/* "人员顺序号" */, "bd_psndoc",
					true), };

	private Pair[] listItemsDefaultGet = new Pair[] {// 走入职的采集
			new Pair("psncode", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000025")/* @res "人员编码" */,
					"bd_psndoc", true),
			new Pair("psnname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000064")/* @res "人员姓名" */,
					"bd_psnbasdoc", true),
			new Pair("unitname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000047")/* @res "公司名称" */, "bd_corp",
					true),
			new Pair("deptcode", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000046")/* @res "部门编码" */,
					"bd_deptdoc", true),
			new Pair("deptname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000001")/* @res "部门名称" */,
					"bd_deptdoc", true),
			new Pair("psnclassname",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"upt600704-000042")/* @res "人员类别" */, true),
			new Pair("jobname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000002")/* @res "岗位名称" */, true),
			new Pair("jobtypename",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"UPP600704-000065")/* @res "任职类型" */, true),
			// new Pair("approveflagname","人员状态", true),
			new Pair("approveflagname",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
							"UPP600700-000360")/* @res "人员状态" */, true),
			new Pair("showorder", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPT600704-000325")/* "人员顺序号" */, "bd_psndoc",
					true), };

	protected Pair[] listItemsKeyPsnDefault = new Pair[] {// 关键人员管理
			new Pair("psncode", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000025")/* @res "人员编码" */,
					"bd_psndoc", true),
			new Pair("psnname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPP600704-000064")/* @res "人员姓名" */,
					"bd_psnbasdoc", true),
			new Pair("unitname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000047")/* @res "公司名称" */, "bd_corp",
					true),
			new Pair("keypsngroupname", nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("600710", "UPP600710-000026")/* @res"人员组" */,
					true),
			new Pair("deptcode", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000046")/* @res "部门编码" */,
					"bd_deptdoc", true),
			new Pair("deptname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000001")/* @res "部门名称" */,
					"bd_deptdoc", true),
			new Pair("psnclassname",
					nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
							"upt600704-000042")/* @res "人员类别" */, true),
			new Pair("jobname", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "upt600704-000002")/* @res "岗位名称" */, true),

			new Pair("showorder", nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"600704", "UPT600704-000325")/* "人员顺序号" */, "bd_psndoc",
					true), };

	/**
	 * 根据不同节点设置默认列表
	 * 
	 * @return
	 */
	public Pair[] getListItemsDefault() {
		if (intodocdirect == 1 && getModuleName().equalsIgnoreCase("600704")) {
			return (Pair[]) Collections.unmodifiableList(
					Arrays.asList(listItemsDefaultGet)).toArray().clone();
		} else {
			return (Pair[]) Collections.unmodifiableList(
					Arrays.asList(listItemsDefault)).toArray().clone();
		}
	}

	public BillItem[] getCurrentSetItems() {
		BillData billData = getCard().getBillData();
		BillItem[] returnItems = billData.getBodyItemsForTable(getCard()
				.getCurrentBodyTableCode());
		return returnItems;
	}

	public BillModel getCurrnentBillModel() {
		return getCard().getBillModel(getCard().getCurrentBodyTableCode());
	}

	public String getCurrentSetCode() {
		return getCard().getCurrentBodyTableCode();
	}

	private boolean isBatchAddSet = false;

	/**
	 * @return 返回 isBatchAddSet。
	 */
	public boolean isBatchAddSet() {
		return isBatchAddSet;
	}

	/**
	 * @param isBatchAddSet
	 *            要设置的 isBatchAddSet。
	 */
	public void setBatchAddSet(boolean isBatchAddSet) {
		this.isBatchAddSet = isBatchAddSet;
	}

	HashMap hmUniqueFld = new HashMap();

	public FlddictVO[] getUniqueFld(String tableCode) {
		FlddictVO[] flds = (FlddictVO[]) hmUniqueFld.get(tableCode);
		if (flds == null) {// || flds.length == 0
			try {
				flds = PubDelegator.getISetdict().querySingleFld(tableCode,
						Global.getCorpPK());
				if (flds != null && flds.length > 1
						&& tableCode.equalsIgnoreCase("hi_psndoc_edu")) {
					FlddictVO[] tempvo = null;
					Vector v = new Vector();
					for (int i = 0; i < flds.length; i++) {
						if (flds[i].getFldcode().equalsIgnoreCase(
								"lasteducation")) {
							continue;
						} else {
							v.addElement(flds[i]);
						}
					}
					tempvo = new FlddictVO[v.size()];
					v.copyInto(tempvo);
					flds = tempvo;
				}
				hmUniqueFld.put(tableCode, flds);
			} catch (Exception e) {
				e.printStackTrace();
				reportException(e);
			}
		}
		return flds;
	}

	/**
	 * @return 返回 isEmployeeRef。
	 */
	public boolean isEmployeeRef() {
		return isEmployeeRef;
	}

	/**
	 * @param isEmployeeRef
	 *            要设置的 isEmployeeRef。
	 */
	public void setEmployeeRef(boolean isEmployeeRef) {
		this.isEmployeeRef = isEmployeeRef;
	}

	/**
	 * @return Returns the isListDefault.
	 */
	public boolean isListDefault() {
		return isListDefault;
	}

	/**
	 * @param isListDefault
	 *            The isListDefault to set.
	 */
	public void setListDefault(boolean isListDefault) {
		this.isListDefault = isListDefault;
	}

	/**
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		psnListValueChange(e);
	}

	/**
	 * @return 返回 itemchange。
	 */
	public boolean isItemchange() {
		return itemchange;
	}

	/**
	 * @param itemchange
	 *            要设置的 itemchange。
	 */
	public void setItemchange(boolean itemchange) {
		this.itemchange = itemchange;
	}

	private CheckUniqueDlg getAddCheckDlg() {
		if (checkAddDlg == null) {
			checkAddDlg = new CheckUniqueDlg(this);
		}
		return checkAddDlg;
	}

	public void setUserpks(String userpks) {
		this.userpks = userpks;
	}

	public UserNameObject[] getUserRecievers() {
		return userRecievers;
	}

	public void setUserRecievers(UserNameObject[] userRecievers) {
		this.userRecievers = userRecievers;
	}

	public String getUserpks() {
		return userpks;
	}

	public String getRehirePsnbaspk() {
		return rehirePsnbaspk;
	}

	public void setRehirePsnbaspk(String rehirePsnbaspk) {
		this.rehirePsnbaspk = rehirePsnbaspk;
	}

	public String getRehirePsnBelonpk() {
		return rehirePsnBelonpk;
	}

	public void setRehirePsnBelonpk(String rehirePsnBelonpk) {
		this.rehirePsnBelonpk = rehirePsnBelonpk;
	}

	public String getRehirePsnpk() {
		return rehirePsnpk;
	}

	public void setRehirePsnpk(String rehirePsnpk) {
		this.rehirePsnpk = rehirePsnpk;
	}

	// 包含包含下级人员
	private UICheckBox includeChildDeptPsn = null;

	protected UICheckBox getincludeChildDeptPsn() {
		if (includeChildDeptPsn == null) {
			try {
				includeChildDeptPsn = new UICheckBox();
				includeChildDeptPsn.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("600704", "UPT600704-000302"));// 包含下级人员
				includeChildDeptPsn.setFont(new java.awt.Font("dialog", 0, 12));
				includeChildDeptPsn.setPreferredSize(new java.awt.Dimension(93,
						20));
				// 默认包含下级人员
				// includeChildDeptPsn.setSelected(true);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return includeChildDeptPsn;
	}

	/**
	 * 取消引用按钮响应事件
	 * 
	 * @throws java.lang.Exception
	 */
	public void onCancelAffirm() throws java.lang.Exception {
		// if (listSelectRow < 0) {
		// return;
		// }
		// // 查看申请的人员
		// CancelAffirmDlg cancelAffirm = new CancelAffirmDlg(this);
		// // 默认为当前登陆日期
		// cancelAffirm.setInputDate(nc.ui.hr.global.Global.getLogDate());
		// cancelAffirm.showModal();
		// if (cancelAffirm.getResult() == UIDialog.ID_CANCEL)
		// return;

		// //得到用户输入的日期，默认为系统登录日期
		// UFDate userDate = cancelAffirm.getInputDate() != null ? cancelAffirm
		// .getInputDate() : new UFDate(nc.ui.hr.global.Global
		// .getLogDate().toString());
		// HashMap hmRelVO = new HashMap();

		// GeneralVO psnvo = new GeneralVO();
		// psnvo.setAttributeValue("pk_psndoc",
		// psnList[listSelectRow].getAttributeValue("pk_psndoc"));
		// psnvo.setAttributeValue("sealdate", userDate);
		// psnvo.setAttributeValue("pk_corp", Global.getCorpPK());
		// hmRelVO.put("bd_psndoc", psnvo);
		// // 更新记录信息
		// HIDelegator.getPsnInf().updateRelTable(hmRelVO);
		// // 刷新当前界面
		// onRefresh();
		BatchCancelAffirmDlg cancelAffirm = new BatchCancelAffirmDlg(this,
				this, null);
		// 刷新当前界面
		if (cancelAffirm.showModal() == UIDialog.ID_OK) {
			onRefresh();
		}
	}

	/**
	 * 编辑后事件. 创建日期:(2001-3-23 2:02:27)
	 * 
	 * @param e
	 *            ufbill.BillEditEvent
	 */
	public void afterEdit(BillEditEvent e) {
		String key = e.getKey();
		if (e.getPos() == IBillItem.HEAD) {
			BillItem item = getCard().getHeadItem(key);
			getCard().execHeadTailEditFormulas(item);
			if (key.equalsIgnoreCase("pk_om_job")) {
				String pk_deptdoc = (String) ((UIRefPane) getCard()
						.getHeadItem(key).getComponent())
						.getRefValue("om_job.pk_deptdoc");
				if (pk_deptdoc != null)
					((UIRefPane) getCard().getHeadItem("pk_deptdoc")
							.getComponent()).setPK(pk_deptdoc);

			}
		}
	};

	/**
	 * 行改变事件。 创建日期：(2001-3-23 2:02:27)
	 * 
	 * @param e
	 *            ufbill.BillEditEvent
	 */
	public void bodyRowChange(nc.ui.pub.bill.BillEditEvent e) {
		//
		BillModel mymodel = getCurBillModel();
		int row = e.getRow();
		if (row == -1)
			return;
		BillItem[] bodyItem = (BillItem[]) mymodel.getBodyItems();
		PersonEAVO eavo = getCurData();// 获取界面输入
		CircularlyAccessibleValueObject[] records = eavo
				.getTableVO(getCurTableName());
		String tablecode = getCard().getCurrentBodyTableCode();
		for (int i = 0; i < bodyItem.length; i++) {
			if (bodyItem[i].getDataType() == BillItem.UFREF) {
				String pk = (String) records[row]
						.getAttributeValue(Util.PK_PREFIX
								+ bodyItem[i].getKey());
				((UIRefPane) getCard().getBodyItem(bodyItem[i].getKey())
						.getComponent()).setPK(pk);
				// 列数据之间存在依赖关系时，需要改变参照
				if ("hi_psndoc_part".equals(tablecode)
						|| "hi_psndoc_deptchg".equals(tablecode)) {
					refreshRef(tablecode, bodyItem[i].getKey(), pk);
				}
			}
		}
		// 设置当前编辑行
		if (!isNotMultiEdit())
			setEditLineNo(row);
	}

	/**
	 * 更改表体参照
	 * 
	 * @param tablecode
	 * @param fieldcode
	 * @param pk
	 */
	public void refreshRef(String tablecode, String fieldcode, String pk) {
		// 任职、兼职记录
		if ("hi_psndoc_part".equals(tablecode)
				|| "hi_psndoc_deptchg".equals(tablecode)) {
			// 公司改变影响参照
			if ("pk_corp".equals(fieldcode)) {
				// 部门档案参照
				UIRefPane deptRef = (UIRefPane) getCard().getBodyItem(
						"pk_deptdoc").getComponent();
				deptRef.getRefModel().clearCacheData();
				deptRef.setPk_corp(pk);
				AbstractRefModel refmodel = deptRef.getRefModel();
				if (refmodel instanceof SpaDeptdocRefTreeModel) {
					((SpaDeptdocRefTreeModel) refmodel).setCorppk(pk);

				} else if (refmodel instanceof nc.ui.bd.ref.DefaultRefTreeModel) {
					((DefaultRefTreeModel) refmodel).setPk_corp(pk);
				} else {
					refmodel.setPk_corp(pk);
				}
			} else if ("pk_deptdoc".equals(fieldcode)) { // 部门改变影响参照
				// 岗位档案参照
				UIRefPane jobRef = (UIRefPane) getCard().getBodyItem(
						"pk_postdoc").getComponent();
				nc.ui.hi.hi_301.ref.JobRef jobRefModel = new nc.ui.hi.hi_301.ref.JobRef();
				jobRef.setRefModel(jobRefModel);
				jobRefModel.setPk_deptdoc(pk);
				jobRef.getRefModel().reloadData();
			}
		}

	}

	/**
	 * 当前选择单据Model。 创建日期：(2003-7-24 10:55:28)
	 */
	private BillModel getCurBillModel() {
		try {
			return (BillModel) (getCurBillPanel().getTableModel());
		} catch (Exception e) {
			reportException(e);
		}
		return null;
	}

	/**
	 * 返回当前选择子页面。 创建日期：(2003-6-17 9:50:49)
	 * 
	 * @return nc.ui.pub.bill.BillScrollPane
	 */
	private BillScrollPane getCurBillPanel() {
		return getCard().getBodyPanel(getCurTableName());
	}

	/**
	 * 返回当前操作子表表名。 创建日期：(2003-6-17 9:50:49)
	 * 
	 * @return nc.ui.pub.bill.BillScrollPane
	 */
	public String getCurTableName() {
		return getCard().getCurrentBodyTableCode();
	}

	/**
	 * 获取当前正在编辑的表tableCodes的数据。 创建日期：(2004-5-17 11:30:33) *
	 * 
	 * @param tableCodes
	 *            String[] 当前正在编辑的表
	 * @return nc.vo.hi.hi_301.PersonEAVO 这些表的数据，封存在PersonEAVO中
	 */
	private PersonEAVO getCurData() {
		// 当前人员数据
		PersonEAVO eavo;
		// 如果当前正在添加人员（条件：编辑状态为编辑主表，并且是添加状态），则新生成一个PersonEAVO对象
		if (getEditType() == EDIT_MAIN && isAdding()) {
			person = null;
			eavo = new PersonEAVO();
		} else if (getEditType() == EDIT_RETURN && isAdding()) {
			person = null;
			eavo = new PersonEAVO();
		} else {
			eavo = (PersonEAVO) person.clone();
		}
		// 获取数据
		getCard().getBillValueVOExtended(eavo);
		return eavo;
	}

	private boolean isPsnclPower(String corppk) throws Exception {
		return nc.ui.hr.global.GlobalTool.isUsedDataPower("bd_psncl", corppk);
	}

	/**
	 * 得到各个公司是否启用了人员类别权限。 创建日期：(2004-10-22 15:36:49)
	 * 
	 * @return java.lang.String
	 * @param pk_corp
	 *            java.lang.String
	 */
	public String getPsnclPowerByCorp(String corppk) throws Exception {
		String power = (String) psnclPowerlist.get(corppk);
		if (power != null) {
			return power;
		}
		if (!"0001".equals(Global.getCorpPK())) {
			// 连接数优化
			// boolean usePsnclPower = GlobalTool.isUsedDataPower("bd_psncl",
			// corppk);
			// if (usePsnclPower) {
			power = GlobalTool.getPowerSql("bd_psncl", Global.getUserID(),
					corppk);
			if (power == null) {
				psnclPowerlist.put(corppk, " 0 = 0 ");
			} else {
				psnclPowerlist.put(corppk, power);
			}
			return power;
			// } else {
			// psnclPowerlist.put(corppk, " 0 = 0 ");
			// return " 0 = 0 ";
			// }
		} else {
			psnclPowerlist.put(corppk, " 0 = 0 ");
			return " 0 = 0 ";
		}
	}

	/**
	 * 是否可以多行子集记录同时编辑
	 * 
	 * @return boolean
	 */
	protected boolean isNotMultiEdit() {
		// 追踪信息集的显示名称以<开始
		return isBodyTraceTable()
				|| getBodyTableCode().equalsIgnoreCase("hi_psndoc_ctrt");
	}

	public boolean isEditSub() {
		return isEditSub;
	}

	public void setEditSub(boolean isEditSub) {
		this.isEditSub = isEditSub;
	}

	/**
	 * 取得系统参数：引用是否需要确认。
	 * 
	 * @return
	 */
	public boolean getIsNeedAFirm() {
		if (isNeedAFirm == null) {
			try {
				// 效率优化
				// isNeedAFirm =
				// PubDelegator.getIParValue().getParaBoolean(Global.getCorpPK(),
				// "HI_NEEDAFIRM");
				isNeedAFirm = UFBoolean.valueOf(getPara("HI_NEEDAFIRM"));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		return isNeedAFirm.booleanValue();
	}

	public JComponent createFieldValueEditor(FilterMeta filterMeta) {
		// 初始情况判断
		if (filterMeta == null
				|| StringUtils.isEmpty(filterMeta.getFieldCode())) {
			return null;
		}// end if
		// 登录公司主键
		String pk_corp = Global.getCorpPK();

		// 登录用户ID
		String userID = Global.getUserID();
		// String selcorppk = getUIRefCorpPnl().getRefPK();
		String selcorppk = null;
		if (selcorppk == null) {
			selcorppk = Global.getCorpPK();
		}// end if

		// 分情况处理
		if ("bd_psndoc.pk_psncl".equals(filterMeta.getFieldCode())) {
			// 人员类别参照
			UIRefPane psnclsref = new UIRefPane();
			psnclsref.setRefType(1);
			psnclsref.setName("psnclsref");
			psnclsref.setRefInputType(1);
			psnclsref.setRefNodeName("人员类别");
			psnclsref.getRefModel().setSealedDataShow(true);
			return psnclsref;
		} else if ("bd_accpsndoc.dutyname".equals(filterMeta.getFieldCode())) {
			// 职务参照
			UIRefPane dutyref = new UIRefPane();
			dutyref.setName("dutyref");
			DutyRef dutyModel = new DutyRef(selcorppk);
			dutyref.setRefType(1);
			dutyref.setRefInputType(1);
			dutyref.setRefModel(dutyModel);
			return dutyref;
		} else if ("hi_psndoc_deptchg.isreturn".equals(filterMeta
				.getFieldCode())) {

			// filterMeta.setDataType(IQueryConstants.BOOLEAN);
		} else if ("hi_psndoc_deptchg.pk_psncl".equals(filterMeta
				.getFieldCode())) {
			// 人员类别参照
			UIRefPane psnclsref = new UIRefPane();
			psnclsref.setRefType(1);
			psnclsref.setName("psnclsref");
			psnclsref.setRefInputType(1);
			psnclsref.setRefNodeName("人员类别");
			psnclsref.getRefModel().setSealedDataShow(true);
			String wherepart = psnclsref.getRefModel().getWherePart();
			if (wherepart == null || wherepart.trim().length() == 0) {
				wherepart = "( psnclscope = 0 or  psnclscope = 5 ) ";
			} else {
				String whereScope = " (psnclscope = 0 or  psnclscope = 5 ) and (";
				if (wherepart.indexOf(whereScope) < 0) {
					wherepart = whereScope + wherepart + ")";
				}
			}
			psnclsref.getRefModel().setWherePart(wherepart);
			return psnclsref;
		}

		// 分登录类别
		if (Global.getCorpPK().equals(CommonValue.GROUPCODE)) {
			// 如果登录为集团
			if ("bd_psndoc.pk_psndoc".equals(filterMeta.getFieldCode())) {

				// 人员参照 wangkf add v31Sp1
				SpaDeptdocRefGridTreeModel psnrefmodel = new SpaDeptdocRefGridTreeModel();
				psnrefmodel.setIsLimit(0);
				String deptsql = " bd_deptdoc.pk_corp = '" + selcorppk + "'";
				psnrefmodel.setCorpPK(selcorppk);
				psnrefmodel.setClassWherePart(deptsql);
				psnrefmodel.setCacheEnabled(false);
				UIRefPane psnref = new UIRefPane();
				psnref.setRefType(1);
				psnref.setRefInputType(1);
				String psnwhere = psnrefmodel.getWherePart();
				psnwhere = psnwhere.substring(0, psnwhere
						.indexOf("and bd_psndoc.psnclscope"));
				psnwhere += ("and bd_psndoc.psnclscope =" + getCurPsnScope());
				psnrefmodel.setWherePart(psnwhere);
				psnref.setRefModel(psnrefmodel);

				return psnref;

			} else if ("bd_psndoc.pk_deptdoc".equals(filterMeta.getFieldCode())) {
				// 部门参照
				DeptRefModel deptrefmodel = new DeptRefModel();
				UIRefPane deptref = new UIRefPane();
				deptref.setName("deptref");
				deptref.setRefType(1);
				deptref.setRefInputType(1);
				deptref.setRefModel(deptrefmodel);
				setWhereToModel(deptref.getRefModel(),
						"((bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.hrcanceled is null) )");
				deptref.setIncludeSubShow(true);
				return deptref;
			} else if ("bd_psndoc.pk_om_job".equals(filterMeta.getFieldCode())) {
				// 岗位参照
				JobRef jobrefmodel = new JobRef();
				jobrefmodel.setRefTitle("岗位参照");
				UIRefPane jobref = new UIRefPane();
				jobref.setName("jobref");
				jobref.setRefType(1);
				jobref.setRefInputType(1);
				jobref.setRefModel(jobrefmodel);
				return jobref;
			}// end if
		} else {
			// 如果登录为公司

			if ("bd_psndoc.pk_psndoc".equals(filterMeta.getFieldCode())) {
				if (getModuleName().equals("600704")) {
					try {
						// 增加部门权限
						String deptpower = "";
						// 选择的公司为当前登录公司,并且登录公司不是集团，受部门权限控制
						boolean isUserDeptPower = GlobalTool.isUsedDataPower(
								"bd_deptdoc", selcorppk);
						if (isUserDeptPower) {
							String powerSql = GlobalTool.getPowerSql(
									"bd_deptdoc", userID, selcorppk);
							if (powerSql != null && powerSql.length() > 0) {
								deptpower = " " + powerSql + " ";
							}
						} else {
							deptpower = " 0=0 ";
						}
						String deptsql = " 0=0 ";
						if (deptpower.length() > 0
								&& deptpower.trim().startsWith("select")) {
							deptsql = " and bd_deptdoc.pk_deptdoc in ("
									+ deptpower + ")";
						} else {
							deptsql = " and 0 = 0 ";
						}
						String psnclsql = "";
						boolean isUserPsnClPower = GlobalTool.isUsedDataPower(
								"bd_psncl", selcorppk);
						if (isUserPsnClPower) {
							String powerPsnclSql = GlobalTool.getPowerSql(
									"bd_psncl", userID, selcorppk);
							if (powerPsnclSql != null
									&& powerPsnclSql.length() > 0) {
								psnclsql += " and bd_psndoc.pk_psncl in ("
										+ powerPsnclSql + ") ";
							}
						}

						PsndocDefaulRefModel model = new PsndocDefaulRefModel(
								"人员档案");
						model.setPk_corp(pk_corp);
						model
								.setClassWherePart(" (pk_corp='"
										+ pk_corp
										+ "' or pk_corp= '"
										+ "0001"
										+ "' or pk_corp is null) and canceled <>'Y' and hrcanceled <> 'Y' "
										+ deptsql);
						model
								.setWherePart(" bd_psndoc.pk_corp = '"
										+ pk_corp
										+ "' and bd_psndoc.pk_corp = bd_psnbasdoc.pk_corp and  bd_psndoc.indocflag = 'N' "
										+ psnclsql);
						// 设置不受人员档案权限控制
						model.setUseDataPower(false);
						model.clearCacheData();
						UIRefPane psnref = new UIRefPane();
						psnref.setRefModel(model);
						return psnref;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}

					// PsndocDefaulRefModel model = new
					// PsndocDefaulRefModel("人员档案");
					// model.setPk_corp(pk_corp);
					// model.setClassWherePart(" (pk_corp='" + pk_corp
					// + "' or pk_corp= '" + "0001"
					// + "' or pk_corp is null) and canceled <>'Y' and
					// hrcanceled <> 'Y'");
					// model.setWherePart(" bd_psndoc.pk_corp = '"
					// + pk_corp
					// + "' and bd_psndoc.pk_corp = bd_psnbasdoc.pk_corp and
					// bd_psndoc.indocflag = 'N' ");
					//
					// //设置查询树不受部门权限控制
					// model.setClassDataPower(false);
					// //设置不受人员档案权限控制
					// model.setUseDataPower(false);
					// model.clearCacheData();
					// UIRefPane psnref = new UIRefPane();
					// psnref.setRefModel(model);
					// return psnref;
				} else {
					try {
						String deptpower = "";
						// 选择的公司为当前登录公司,并且登录公司不是集团，受部门权限控制
						// 连接数优化
						// boolean isUserDeptPower =
						// GlobalTool.isUsedDataPower("bd_deptdoc",selcorppk);
						// if (isUserDeptPower) {
						String powerSql = GlobalTool.getPowerSql("bd_deptdoc",
								userID, selcorppk);
						if (powerSql != null && powerSql.length() > 0) {
							deptpower = " " + powerSql + " ";
						} else {
							deptpower = " 0=0 ";
						}
						// } else {
						// deptpower = " 0=0 ";
						// }
						// 人员参照
						SpaDeptdocRefGridTreeModel psnrefmodel = new SpaDeptdocRefGridTreeModel();
						psnrefmodel.setIsLimit(0);
						String deptsql = " 0=0 ";
						if (deptpower.length() > 0
								&& deptpower.trim().startsWith("select")) {
							deptsql = " bd_deptdoc.pk_deptdoc in (" + deptpower
									+ ")";
						} else {
							deptsql = " 0 = 0 ";
						}
						psnrefmodel.setCorpPK(selcorppk);
						psnrefmodel.setClassWherePart(deptsql);

						psnrefmodel.setCacheEnabled(false);
						UIRefPane psnref = new UIRefPane();
						psnref.setName("psnref");
						psnref.setRefType(0);
						psnref.setRefInputType(1);
						String psnwhere = psnrefmodel.getWherePart();
						if (psnwhere == null)
							psnwhere = "";
						psnwhere = psnwhere.substring(0, psnwhere
								.indexOf("and bd_psndoc.psnclscope"));
						psnwhere += ("and bd_psndoc.psnclscope =" + getSelPsnclscope());
						// 连接数优化
						// boolean isUserPsnClPower =
						// GlobalTool.isUsedDataPower("bd_psncl", selcorppk);
						// if (isUserPsnClPower) {
						String powerPsnclSql = GlobalTool.getPowerSql(
								"bd_psncl", userID, selcorppk);
						if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
							psnwhere += " and bd_psndoc.pk_psncl in ("
									+ powerPsnclSql + ") ";
						}
						// }
						psnrefmodel.setWherePart(psnwhere);
						psnref.setRefModel(psnrefmodel);
						return psnref;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}// end try
				}
			} else if ("bd_psndoc.pk_deptdoc".equals(filterMeta.getFieldCode())) {
				// 部门参照
				UIRefPane deptref = new UIRefPane();
				deptref.setName("deptref");
				deptref.setRefType(1);
				deptref.setRefInputType(1);
				deptref.setRefNodeName("部门档案HR");
				// 2009-10-15
				deptref.setIncludeSubShow(true);
				boolean aa = deptref.isMultiSelectedEnabled();

				deptref.setMultiSelectedEnabled(false);
				deptref.getRefModel().setPk_corp(selcorppk);
				deptref.getRefModel().setUseDataPower(true);
				setWhereToModel(deptref.getRefModel(),
						"((bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.hrcanceled is null) )");

				return deptref;
			} else if ("hi_psndoc_deptchg.pk_deptdoc".equals(filterMeta
					.getFieldCode())) {
				// 部门参照
				UIRefPane deptref2 = new UIRefPane();
				deptref2.setRefType(1);
				deptref2.setName("deptref2");
				deptref2.setRefInputType(1);
				deptref2.setRefNodeName("部门档案HR");
				deptref2.setIncludeSubShow(true);
				deptref2.getRefModel().setPk_corp(selcorppk);
				deptref2.getRefModel().setUseDataPower(true);
				setWhereToModel(deptref2.getRefModel(),
						"((bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.hrcanceled is null) )");
				return deptref2;

			} else if ("bd_psndoc.pk_om_job".equals(filterMeta.getFieldCode())) {
				// 岗位参照
				JobRef jobmodel = new JobRef(selcorppk, null);

				String where = jobmodel.getWherePart();
				jobmodel.setWherePart(where);
				jobmodel.setRefTitle("岗位参照");
				UIRefPane jobref = new UIRefPane();
				jobref.setName("jobref");
				jobref.setRefType(1);
				jobref.setRefInputType(1);
				jobref.setRefModel(jobmodel);
				return jobref;
			}// end if
		}// end if

		// 正常返回
		return null;
	}

	public String getRefPanelWherePart(FilterMeta filterMeta) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 得到选中的人员类别归属范围
	 * 
	 * @return
	 */
	private int getCurPsnScope() {
		int index = getCmbPsncl().getSelectedIndex();
		if (index < 0 || index > PSNCLSCOPE_GROUP.length - 1)
			return -1;// 未定义的某个指
		return PSNCLSCOPE_GROUP[index];
	}

	/**
	 * 设置排序条件并查询
	 * 
	 * @since NCHRV5.5
	 */
	protected void onSetOrderAndSort() {
		refreshSortDialogField();
		if (getConfigDialog().showModal() != UIDialog.ID_OK) {
			return;
		}
		// 刷新
		onRefresh();

	}

	/**
	 * 获得排序Dialog
	 * 
	 * @return SortCnfigDialog 可以保存排序条件的Dialog
	 * @since NCHRV5.5
	 */
	// public SortConfigDialog getSortDialog() {
	// BillModel billModel = getPsnList().getTableModel();
	// BillItem[] items = billModel.getBodyItems();
	// Vector vvv = new Vector();
	// for (int i = 0; i < items.length; i++){
	// if(items[i].isShow()){
	// nc.ui.hr.comp.sort.Pair fieldPair
	// = new nc.ui.hr.comp.sort.Pair(items[i].getKey(), items[i].getName());
	// vvv.addElement(fieldPair);
	// }
	// }
	// nc.ui.hr.comp.sort.Pair[] listItems = new
	// nc.ui.hr.comp.sort.Pair[vvv.size()];
	// vvv.copyInto(listItems);
	// configDialog = new
	// nc.ui.hr.comp.combinesort.SortConfigDialog(Util.getTopFrame(this), true);
	// configDialog.setFields(listItems);
	// configDialog.setLocationRelativeTo(this);
	// configDialog.setModuleCode(getModuleName());
	// configDialog.btnLoad_ActionPerformed(null);
	// nc.ui.hr.comp.sort.Pair[] listSortItems = null;
	// Vector<nc.ui.hr.comp.sort.Pair> vecPair = new
	// Vector<nc.ui.hr.comp.sort.Pair>();
	// nc.ui.hr.comp.sort.Pair condVo = new
	// nc.ui.hr.comp.sort.Pair("bd_psndoc.psncode",NCLangRes.getInstance().getStrByID("common","UC000-0000147")/*@res
	// "人员编码"*/);
	// vecPair.addElement(condVo);
	// condVo = new
	// nc.ui.hr.comp.sort.Pair("bd_psndoc.psnname",NCLangRes.getInstance().getStrByID("common","UC000-0000135")/*@res
	// "人员姓名"*/);
	// vecPair.addElement(condVo);
	// listSortItems = new nc.ui.hr.comp.sort.Pair[vecPair.size()];
	// vecPair.copyInto(listSortItems);
	// if (sortDialog == null) {
	// sortDialog = new SortConfigDialog(this, true);
	// }
	// String newClassPk = "";//getUIRefWaClass().getRefPK();
	// sortDialog.setFields(listSortItems);
	// sortDialog.setTableCode(newClassPk);
	// sortDialog.setModuleCode(getModuleCode());
	// sortDialog.setLocationRelativeTo(this);
	// //切换类别，且类别发生变化才清掉排序条件列表，否则保留上次排序条件，如果切换类别后则不保存
	// if(!newClassPk.equalsIgnoreCase(oldClassPk)) {
	// sortDialog.setSortingFields(new Vector());//先清空默认条件
	// }
	// sortDialog.btnLoad_ActionPerformed(null);
	// //设置默认排序条件
	// setDefaultOrderFields(sortDialog, listSortItems);
	// oldClassPk = newClassPk;
	// return sortDialog;
	// }
	/**
	 * 设置默认排序条件
	 * 
	 * @param configDialog
	 *            SortConfigDialog 排序Dialog
	 * @param listSortItems
	 *            Vector 可供选择的排序字段
	 * @since NCHRV5.5
	 */
	private void setDefaultOrderFields(SortConfigDialog configDialog,
			nc.ui.hr.comp.sort.Pair[] listSortItems) {
		Vector<Attribute> sortingFields = configDialog.getSortingFields();
		// 第一次使用没有默认排序字段，则设置默认为"部门编码" asc 、"人员编码" asc
		if (sortingFields == null || sortingFields.isEmpty()) {
			Vector<Attribute> defaultSortFields = new Vector<Attribute>();
			nc.ui.hr.comp.combinesort.Attribute deptcode = new nc.ui.hr.comp.combinesort.Attribute(
					new nc.ui.hr.comp.sort.Pair("bd_deptdoc.deptcode",
							NCLangRes.getInstance().getStrByID("common",
									"UC000-0004073")/* @res "部门编码" */), true);
			nc.ui.hr.comp.combinesort.Attribute psncode = new nc.ui.hr.comp.combinesort.Attribute(
					new nc.ui.hr.comp.sort.Pair("bd_psndoc.psncode", NCLangRes
							.getInstance()
							.getStrByID("common", "UC000-0000147")/*
																	 * @res
																	 * "人员编码"
																	 */), true);
			defaultSortFields.addElement(deptcode);
			defaultSortFields.addElement(psncode);
			configDialog.setSortingFields(defaultSortFields);
		}
		// 如果已经有默认排序字段，检查排序字段是否在可供选择的排序字段类别中，如果不再则需要删除
		else {
			for (int i = 0; i < sortingFields.size(); i++) {
				boolean isExistInList = false;
				nc.ui.hr.comp.sort.Pair pair = (sortingFields.elementAt(i))
						.getAttribute();
				for (int j = 0; j < listSortItems.length; j++) {
					if (listSortItems[j].getCode().equals(pair.getCode())) {
						isExistInList = true;
						break;
					}
				}
				if (!isExistInList) {
					sortingFields.removeElementAt(i);
				}
			}
		}
	}

	protected String oldClassPk = null;

	public void doMaintainAction(ILinkMaintainData maintaindata) {
		try {
			String pk_psndoc = maintaindata.getBillID();
			// GeneralVO[] personVOs =
			// HIDelegator.getPsnInf().queryMainPersonInfo(pk_psndoc,
			// Global.getCorpPK(),"bd_psndoc", GlobalTool.getFuncParser());//
			// 这是基本信息表的信息
			ConditionVO[] conds = new ConditionVO[2];
			conds[0] = new ConditionVO();
			conds[0].setFieldCode("bd_psndoc.pk_psndoc");
			conds[0].setOperaCode("=");
			conds[0].setValue(pk_psndoc);

			conds[1] = new ConditionVO();
			conds[1].setFieldCode("bd_psndoc.indocflag");
			conds[1].setOperaCode("=");
			conds[1].setValue("Y");

			// 不再关联任职表，调离人员没有任职记录，若关联则不能查出此类人员。dusx 2009-3-13
			// conds[2] = new ConditionVO();
			// conds[2].setFieldCode("hi_psndoc_deptchg.jobtype");
			// conds[2].setOperaCode("=");
			// conds[2].setDataType(CommonValue.DATATYPE_INTEGER);
			// conds[2].setValue("0");

			// amend on 09.12.2
			// 可能是集团节点，因此不按照公司过滤
			GeneralVO[] personVOs = HIDelegator.getPsnInf().queryByCondition(
					null, conds, "", getListFieldForOne(), " and (1=1) ");

			if (personVOs == null || personVOs.length == 0) {
				return;
			}
			psnList = personVOs;
			listSelectRow = 0;
			switchButtonGroup(CARD_GROUP);
			setButtonGroup(CARD_GROUP);
			loadPsnInfo(listSelectRow);// 装载 当前行人员数据
			setButtonsState(CARD_MAIN_BROWSE);
			setButtonsState(CARD_CHILD_BROWSE);
			getCard().setPosMaximized(-1);
			bnList.setVisible(false);// 不允许返回
			updateButton(bnList);
			bnDel.setEnabled(false);
			updateButtons();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// 效率优化start
	private HashMap<String, String> allRelatedPara = null;

	public String getPara(String para_name) {
		if (allRelatedPara == null || allRelatedPara.size() < 0) {
			try {
				allRelatedPara = PubDelegator.getHrPara().getStringValues(
						Global.getCorpPK(),
						null,
						null,
						new String[] { "HI_INDOC", "PROBUNIT", "TERMUNIT",
								"HI_MAXLINE", "HI_CODECRTTYPE",
								"HI_CODEUNIQUE", "HI_MESSAGE", "HI_KEYPERSON",
								"HI_NEEDAFIRM" });
				String hi_indoc = "0";
				if (!allRelatedPara.get("HI_INDOC").equals("0")) {
					hi_indoc = "1";
				}
				allRelatedPara.put("HI_INDOC", hi_indoc);
			} catch (BusinessException e) {
				// Logger.error("取参数报错！", e);
				Logger.error(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600700", "UPP600700-000357")/* @res "取参数报错！" */, e);

				e.printStackTrace();
			}
		}
		return allRelatedPara.get(para_name);
	}

	// 效率优化end
	private void initPara() {
		try {
			allRelatedPara = PubDelegator.getHrPara().getStringValues(
					Global.getCorpPK(),
					null,
					null,
					new String[] { "HI_INDOC", "PROBUNIT", "TERMUNIT",
							"HI_MAXLINE", "HI_CODECRTTYPE", "HI_CODEUNIQUE",
							"HI_MESSAGE", "HI_KEYPERSON", "HI_NEEDAFIRM" });
			String hi_indoc = "0";
			if (!allRelatedPara.get("HI_INDOC").equals("0")) {
				hi_indoc = "1";
			}
			allRelatedPara.put("HI_INDOC", hi_indoc);
		} catch (BusinessException e) {
			// Logger.error("取参数报错！", e);
			Logger.error(nc.ui.ml.NCLangRes.getInstance().getStrByID("600700",
					"UPP600700-000357")/* @res "取参数报错！" */, e);
			e.printStackTrace();
		}
	}

	public boolean isNeedPrompt() throws BusinessException {
		return false;
	}

	public String quickSearch(String psnWherePart, SearchType searchType)
			throws BusinessException {
		try {
			// 判断是否可以执行查询
			String powerStr = QsbUtil.checkSearchPower(this, bnQuery, true);
			if (powerStr != null) {
				return powerStr;
			}
			isQuery = false;
			isQuickSearch = true;
			quickWherePart = psnWherePart;
			// 刷新所有缓冲
			psnList = null;
			queryResult = null;
			recordcount = 0;
			person = null;
			psnDeptCache.clear();
			psnCorpCache.clear();
			persons.clear();
			// 强制回收垃圾
			System.gc();
			hmSortCondition.clear();
			listSelectRow = -1;
			unloadPsn = null;
			// 重新查询，刷新人员列表
			performQuery();
			if (unloadPsn != null) {
				return unloadPsn;
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());
		}
		return null;
	}

	/**
	 * 查询人员列表。 人员信息采集用 创建日期：(2010-02-03 11:02:41)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected void queryResult(String psnWherePart) throws java.lang.Exception {
		vBookConds.clear();
		// 当前登录公司
		String selectcorp = getTreeSelCorpPK();
		ConditionVO[] cvos = conditions;
		// Integer recordcounts = PubDelegator.getIParValue().getParaInt("0001",
		// "HI_MAXLINE");//效率优化
		Integer recordcounts = Integer.parseInt(getPara("HI_MAXLINE"));

		recordcounts = (recordcounts == null ? new Integer(1000) : recordcounts);
		// 按照人员采集条件查询
		// wangkf add 把性别 的英文/繁体 改成 简体中文
		if (cvos != null) {
			for (int c = 0; c < cvos.length; c++) {
				if (cvos[c] != null
						&& "bd_psnbasdoc.sex".equals(cvos[c].getFieldCode())
						&& "male".equals(cvos[c].getValue())) {
					cvos[c].setValue("男");
				} else if (cvos[c] != null
						&& "bd_psnbasdoc.sex".equals(cvos[c].getFieldCode())
						&& "female".equals(cvos[c].getValue())) {
					cvos[c].setValue("女");
				} else if (cvos[c] != null
						&& "hi_psndoc_ctrt.iconttype".equals(cvos[c]
								.getFieldCode())) {
					int index = cvos[c].getComboIndex();
					if (index > 1) {
						index++;
					}
					cvos[c].setValue(Integer.toString(index));
				}
			}
		}
		wheresql = "";
		// 根据树选择节点以及是否包含下级人员添加查询条件
		if (getSelectedNode() != null) {

			CtrlDeptVO deptvo = (CtrlDeptVO) (getSelectedNode().getUserObject());
			// 如果选中的节点是部门节点需要添加部门条件
			if (deptvo.getNodeType() == CtrlDeptVO.DEPT) {
				// 若是包含下级部门
				if (getincludeChildDeptPsn().isSelected()) {
					// 
					wheresql += " and bd_psndoc.pk_deptdoc in ";
					wheresql += " ( select pk_deptdoc from bd_deptdoc where pk_corp = '";
					wheresql += selectcorp;
					wheresql += "' and innercode like '"
							+ deptvo.getInnercode().trim() + "%') ";
				} else {
					wheresql += " and bd_psndoc.pk_deptdoc = '"
							+ deptvo.getPk_dept() + "' ";
				}
			}
		}
		if (!getincludeCancleDept().isSelected()) {
			wheresql += " and (bd_deptdoc.hrcanceled = 'N' or bd_psndoc.pk_deptdoc is null)";
		}
		recordcount = 0;
		/* 2010-08-24 update by sunxj 快速查询插件开发 start */
		// String DLGwheresql = getQueryDialog().getWhereSQL();
		String DLGwheresql = " bd_psndoc.pk_psndoc in(" + psnWherePart + ") ";
		/* 2010-08-24 update by sunxj 快速查询插件开发 end */
		HashSet<String> hstables = getQueryDialog().getUsedTableName();
		String[] DLGtables = hstables.toArray(new String[0]);
		tablesFromQueryDLG = DLGtables;
		// 获取排序字段。

		String selectfieldclause = getListField();

		// v50 update 不用公司循环查询,支持集团内人员排序
		if (selectcorp != null && selectcorp.length() > 0) {
			ArrayList reList = new ArrayList();
			recordcount += HIDelegator.getPsnInf()
					.queryRecordCountByCondition_Collect(false, selectcorp,
							DLGwheresql, DLGtables, Global.getCorpPK(),
							Global.getUserID(), wheresql,
							GlobalTool.getFuncParserWithoutWa(),
							getincludeChildDeptPsn().isSelected());
			GeneralVO[] result = HIDelegator.getPsnInf()
					.queryByCondition_Collect(false, selectcorp, DLGwheresql,
							DLGtables, Global.getCorpPK(), Global.getUserID(),
							selectfieldclause, wheresql,
							recordcounts.intValue(),
							GlobalTool.getFuncParserWithoutWa(), null,
							getincludeChildDeptPsn().isSelected());
			queryResult = (GeneralVO[]) QsbUtil.orderbyResult(psnWherePart,
					GeneralVO.class, result, "pk_psndoc", reList);
			// 返回未成功加载人数
			if (reList.size() > 0) {
				unloadPsn = (reList.get(0)).toString();
			}
		} else {
			queryResult = new GeneralVO[0];
		}
	}

	protected void onAbilityMatch() {
		if (listSelectRow != -1) {
			GeneralVO psndocVO = psnList[listSelectRow];
			Object pk_om_job = psndocVO.getAttributeValue("pk_om_job");
			if (pk_om_job == null) {
				MessageDialog.showErrorDlg(this, null, NCLangRes.getInstance()
						.getStrByID("600704", "UPP600704-100193")/*
																	 * @res
																	 * "此用户没有岗位信息，无法进行人岗能力匹配"
																	 */);
				return;
			}
			AbilityMatchDialog dialog = new AbilityMatchDialog(this, NCLangRes
					.getInstance().getStrByID("600704", "UPP600704-100194")/*
																			 * @res
																			 * "员工能力分析"
																			 */);
			dialog.initData(psndocVO);
			dialog.showModal();
		}
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-5-31 14:22:07)
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	protected void onPrint() throws java.lang.Exception {

		if (getPsnList().getTableModel().getRowCount() == 0) {
			showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6007", "UPT600704-000295")/* @res "当前没有需要打印的数据。" */);
			return;
		}
		UITable uitable = getPsnList().getTable();

		nc.ui.pub.print.PrintDirectEntry print = nc.ui.report.base.PrintManager
				.getDirectPrinter(uitable, null);
		java.awt.Font font = new java.awt.Font("dialog", java.awt.Font.BOLD, 30);

		print.setTitle(getTitle());
		print.setTitleFont(font);
		print.preview();

	}
	
	public void onImport() throws Exception{
		ImportExcelData data = new ImportExcelData();
		 if (data.executeImport() == null) {
			//MessageDialog.showErrorDlg(clientUI, "Error", "Excel表中无数据！导入不成功！");
			 MessageDialog.showHintDlg(null, "提示", "取消或Excel表中无数据！导入不成功！");
			return;
		 }
			
		 Object[][] importData = data.executeImport();
		 int importDatalen = importData.length;
		 StringBuffer errorMsg = new StringBuffer();   //错误提示信息
		 GeneralVO[] vos = new GeneralVO[importDatalen - 1];
		 
		 
		 PsndocGrpDef15[] psndocgrpdef15vos = new PsndocGrpDef15[importDatalen - 1];
		 String pk_psndoc = null;
		 
		 for(int i =1 ;i<importDatalen;i++){
			 
			 String psncode = null;
			 String psnname = null;
			 String[] psndoc = null;
			 String khjg = null;
			 String pk_defdoc = null;
			 
			 if(importData[i][1]!=null && !"".equals(importData[i][1])){
				khjg =  (String) importData[i][1];
				pk_defdoc = getDefdoc(khjg);
				
				if(pk_defdoc == null){
					errorMsg.append("第 "+ i +"行,"+khjg+"不存在！\n");
					continue;
				}
			 }
			 			 
			 if(importData[i][2]==null && "".equals(importData[i][2]) ){
				  psncode = (String) importData[i][2];
				  errorMsg.append("第 "+ i +"行,"+psncode+"不能为空！\n");
				  continue;
			 }else{
				  psncode = (String) importData[i][2];
				 
			 }
			 
			 if(importData[i][3]==null && "".equals(importData[i][3]) ){
				 psnname = (String) importData[i][3];
				 errorMsg.append("第 "+ i +"行,"+psnname+"不能为空！\n");
				 continue;
			 }else{
				 psnname = (String) importData[i][3];
				 
			 }
			 
			 if(psncode != null && psnname != null){
				 psndoc = getPsndoc(psncode,psnname);
				 if(psndoc == null){
					 errorMsg.append("第 "+ i +"行,"+psnname+"不存在！\n");
					 continue;
				 }
			 }
			
			
			 PsndocGrpDef15 psndocgrpdef15 = new PsndocGrpDef15();
			 psndocgrpdef15.setGroupdef1((String)importData[i][0]);
			 psndocgrpdef15.setGroupdef2(pk_defdoc);
			 psndocgrpdef15.setPk_psnbasdoc(psndoc[0]);
			 psndocgrpdef15.setPk_psndoc(psndoc[1]);
			 psndocgrpdef15.setLastflag(new UFBoolean("Y"));
			 psndocgrpdef15.setDr(0);
			 
			 pk_psndoc = psndoc[1];
			 
			 String pk_psndoc_sub = getPkPsndocSub(psndoc[0],psndoc[1],(String)importData[i][0]);
			 
			 GeneralVO vo = new GeneralVO();
			 vo.setAttributeValue("groupdef1", (String)importData[i][0]);
			 vo.setAttributeValue("groupdef2", pk_defdoc);
			 vo.setAttributeValue("pk_psnbasdoc", psndoc[0]);
			 vo.setAttributeValue("pk_psndoc", psndoc[1]);
			 vo.setAttributeValue("lastflag", new UFBoolean("Y"));
			 vo.setAttributeValue("dr", 0);
			 
			 if(pk_psndoc_sub == null){
				 vo.setStatus(2);// 增加
				 
			 }else{
				 vo.setStatus(1);
				 vo.setAttributeValue("pk_psndoc_sub", pk_psndoc_sub);// 修改
			 }
			 
			 
			 
			 psndocgrpdef15vos[i - 1] = psndocgrpdef15;
			 vos[i-1] = vo;
			 
			
			 
		 }
		 
		 if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
			 MessageDialog.showErrorDlg(null, "提示","数据导入失败："+errorMsg);
		 }else{
			 // 保存数据
			 
//			 IVOPersistence service = (IVOPersistence)NCLocator.getInstance().lookup(IVOPersistence.class.getName());
//			 service.insertVOArray(psndocgrpdef15vos);
			 
			 for(int i =0 ;i<vos.length;i++){
				 GeneralVO vo = vos[i];
				 GeneralVO[] vos1 = new GeneralVO[1];
				 vos1[0] = vo;
				 HIDelegator.getPsnInf().saveSubSetInfos("hi_psndoc_grpdef15", (String) vo.getAttributeValue("pk_psndoc"), vos1, null);
			 }
			
			
			
			 MessageDialog.showHintDlg(null,"提示", "导入成功。");
		 }
	}
	
	public String getDefdoc(String docname) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select  pk_defdoc"
			+"  from bd_defdoc"
			+" where (pk_defdoclist = '00018L1000000010L9E1' and"
			+"       (pk_corp = '0001' or pk_corp = '0001'))"
			+"   and (sealflag is null or sealflag <> 'Y') "
			+"   and docname = '"+docname+"'"
			+" order by doccode";
		String pk_defdoc = null;
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				 pk_defdoc = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
			}
		}
		
		return pk_defdoc;
	}
	
	public String getPkPsndocSub(String pk_psnbasdoc,String pk_psndoc,String year) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		
		String strSQL = "select pk_psndoc_sub from hi_psndoc_grpdef15 where pk_psnbasdoc ='"+pk_psnbasdoc+"' and pk_psndoc ='"+pk_psndoc+"' and groupdef1='"+year+"'";
		String pk_psndoc_sub = null;
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				pk_psndoc_sub = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
			}
		}
		
		return pk_psndoc_sub;
	}
	
	public String[] getPsndoc(String psncode,String psnname) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select pk_psnbasdoc,pk_psndoc from bd_psndoc where psncode='"+psncode+"' and psnname='"+psnname+"' and dr =0";
		String[] psndocs = null;
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				String pk_psnbasdoc = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
				String pk_psndoc = new String(((Vector) o1.elementAt(i)).elementAt(1) != null ? ((Vector) o1.elementAt(i)).elementAt(1).toString() : null);
				 
				 psndocs = new String[2];
				 psndocs[0] = pk_psnbasdoc;
				 psndocs[1] = pk_psndoc;
				 
			}
		}
		
		return psndocs;
	}

	/**
	 * 武燕军 2011-03-18 导入
	 * 
	 * @throws java.lang.Exception
	 */

	public void onExlImport() throws java.lang.Exception {
		
		/**数据准备*/
		
		// 调用远程接口
		IUserChildInfoAuth service = (IUserChildInfoAuth) NCLocator
				.getInstance().lookup(IUserChildInfoAuth.class.getName());

		HashMap<String, PersonEAVO> hasmapVO = new HashMap<String, PersonEAVO>();

		String corp = ClientEnvironment.getInstance().getCorporation()
				.getPrimaryKey();

		// 主表查询sql

		String sql = " select distinct "
				+ " bd_psndoc.pk_psndoc, "
				+ " bd_psndoc.psncode, "
				+ /* 人员管理档案 */
				" bd_psnbasdoc.pk_psnbasdoc, "
				+ /* 人员基本档案 */
				" bd_deptdoc.pk_deptdoc,  "
				+ /* 部门 */
				" bd_psncl.pk_psncl,  "
				+ /* 人员类别主键 */
				" om_job.pk_om_job ,  "
				+ /* 岗位 */
				" om_duty.pk_om_duty  ,"
				+ /* 职务 */
				" hi_psndoc_deptchg.pk_psndoc_sub"
				+ /* 任职信息PK */
				" from hi_psndoc_deptchg "
				+ " inner join bd_psndoc       on hi_psndoc_deptchg.pk_psndoc    = bd_psndoc.pk_psndoc "
				+ " inner join bd_psnbasdoc    on hi_psndoc_deptchg.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc "
				+ " inner join bd_corp         on hi_psndoc_deptchg.pk_corp      = bd_corp.pk_corp "
				+ " left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc   = bd_deptdoc.pk_deptdoc "
				+ " inner join bd_psncl        on hi_psndoc_deptchg.pk_psncl     = bd_psncl.pk_psncl "
				+ " left outer join om_job     on hi_psndoc_deptchg.pk_postdoc   = om_job.pk_om_job "
				+ " left outer join om_duty    on hi_psndoc_deptchg.pk_om_duty   = om_duty.pk_om_duty "
				+ " where hi_psndoc_deptchg.lastflag = 'Y' "
				//+ " and hi_psndoc_deptchg.bendflag = 'N' "
				+ " and bd_psndoc.sealdate is null "
				+ " and bd_deptdoc.hrcanceled = 'N' "
				+ " and hi_psndoc_deptchg.jobtype = 0 "
				+
				// " and (hi_psndoc_deptchg.isreturn = 'N') "+ /*是否返聘*/
				" and bd_psndoc.indocflag = 'Y' "
				//+ " and (bd_psndoc.psnclscope = 0 or bd_psndoc.psnclscope = 5) "
				+ " and bd_psncl.psnclscope = 0 "
//				+ " and (hi_psndoc_deptchg.pk_corp = '" + corp + "') "
				+ " and (bd_corp.ishasaccount = 'Y') "
				+ " and (bd_corp.isseal is null or bd_corp.isseal <> 'Y') "
				+ " and (bd_corp.pk_corp in " + "    (select pk_corp "
				+ "        from sm_createcorp "
				+ "       where sm_createcorp.pk_corp = bd_corp.pk_corp "
				+ "        and funccode = '6007')         "
				+ "  )";

		IUAPQueryBS servicequery = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		List listPsn=(List)servicequery.executeQuery(sql, new BeanListProcessor(PsndocVO.class));
		
		
		//人员信息
		HashMap<String, SuperVO> mapPsn = HRPPubTool.getKeyToVO("psncode",(SuperVO[]) listPsn.toArray(new SuperVO[0]));
		StringBuffer errorMsg = new StringBuffer();   //错误提示信息
		
		//按照页签读取全部数据
		ImportExcelData data = new ImportExcelData();
		Map<String, List<Map<String, Object>>> mapData = data.executeImportSheetNameToMap();
		
		
		ArrayList<String> listSetName = new ArrayList<String>();//要查询数据的Name
		
		for (String setName : mapData.keySet()) {
			
			HashMap<String, String> mapInfoSheetName = getInfoMap();
			if(mapInfoSheetName.containsKey(setName)){
				
				setName = mapInfoSheetName.get(setName);
			}
			listSetName.add(setName);
		}
		
		//<子集名称，子集表名>
		HashMap<String,String> mapNameToCode = new HashMap<String, String>();
		SetdictVO[] setdictVOs = PubDelegator.getISetdict().querySetdictByCondition(" pk_hr_defdoctype = '00000000000000000004' "+
						HRPPubTool.formInSQL("setname", listSetName));
		//生成子集信息
		for (SetdictVO setVO : setdictVOs) {  
			
			String setName = setVO.getSetname();
			HashMap<String, String> mapInfoSheetName = getInfoMap2();
			if(mapInfoSheetName.containsKey(setName)){
				setName = mapInfoSheetName.get(setName);
			}
			
			mapNameToCode.put(setName, setVO.getSetcode());
		}
		
		for (String sheetName : mapData.keySet()) {
			
			if(mapNameToCode.containsKey(sheetName)==false){
				
				errorMsg.append("信息集 "+sheetName+"不存在！\n");
				continue;
			}
			
			ArrayList<String> listItemIsNull = new ArrayList<String>(); //不能为空的item，保存的是名称
			ArrayList<String> listItemIsRef = new ArrayList<String>();  //类型为参照的
			HashMap<String,BillItem> mapItem = new HashMap<String,BillItem>();  //name,item
			
			//获取表体信息
			String tableCode = mapNameToCode.get(sheetName);
			if(getCard().getBillModel(tableCode)==null){
				errorMsg.append("当前模板不存在信息集 "+sheetName+"！\n");
				continue;
			}
			BillItem[] bodyItems = getCard().getBillModel(tableCode).getBodyItems();
			
			for (BillItem billItem : bodyItems) {
				if(billItem.isNull()){  //不为空
					listItemIsNull.add(billItem.getName());
				}else if(billItem.getDataType()==BillItem.UFREF){   //参照
					listItemIsRef.add(billItem.getName());
				}
				mapItem.put(billItem.getName(),billItem);
			}
			
			listItemIsNull.add("人员编码");
			
			//某个页签的数据
			List<Map<String, Object>> listMap = mapData.get(sheetName);
			
			int lineNum = 0 ;   //行号
			for(Map<String,Object> mapLine : listMap){
				lineNum++;
				
				// 主表数据设置
				PersonEAVO eavo = new PersonEAVO();
				// 子表vo数据
				GeneralVO tablevo = new GeneralVO();
				
				for (String colName : mapLine.keySet()) {
					
					Object colData = mapLine.get(colName); //单元格数据
					
					//验证必输项
					if(listItemIsNull.contains(colName)){
						if(colData==null){
							errorMsg.append("页签"+sheetName+" 第"+lineNum+"行 不能为空！");
							continue;
						}
					}
					
					if(listItemIsRef.contains(colName)){
						if(colData!=null){
							//根据参照转换
							UIRefPane refPane = (UIRefPane)mapItem.get(colName).getComponent();
							
							String codeField = refPane.getRefModel().getRefCodeField();
							String nameField = refPane.getRefModel().getRefNameField();
							String tableName = refPane.getRefModel().getTableName();
							String pkField = refPane.getRefModel().getPkFieldCode();
							
							String refSQL = " select "+pkField+" from "+tableName+" " +
									" where "+codeField+"='"+colData+"' or "+nameField+"='"+colData+"' ";
							
							Object[] obj = (Object[]) servicequery.executeQuery(refSQL, new ArrayProcessor());
							if(obj==null || obj[0]==null)  continue;
							else{
								colData = obj[0];
							}
						}
					}
					//验证人员
					if( colName.equals("人员编码")){
						if(mapPsn.containsKey(colData)==false){
							errorMsg.append("页签"+sheetName+" 第"+lineNum+"行 人员编码"+colData+"系统中没有找到！");
							continue;
						} else{
							String pk_psndoc = mapPsn.get(colData).getPrimaryKey();
							String pk_psnbasdoc = mapPsn.get(colData).getAttributeValue("pk_psnbasdoc").toString();
//							String pk_psndoc_sub = mapPsn.get(colData).getAttributeValue("pk_psndoc_sub").toString();
							
							eavo.setPk_psnbasdoc(pk_psnbasdoc);
							eavo.setPk_psndoc(pk_psndoc);
							
							//关联主子表
							tablevo.setAttributeValue("pk_psndoc",
									pk_psndoc == null ? "" : pk_psndoc);
							tablevo.setAttributeValue("pk_psnbasdoc",
									pk_psnbasdoc == null ? "" : pk_psnbasdoc);
//							tablevo.setAttributeValue("pk_psndoc_sub",
//									pk_psndoc_sub == null ? "" : pk_psndoc_sub);
							tablevo.setAttributeValue("recordnum", 0);
							
						}
					}
					//验证部门
					else if( colName.equals("部门编码") ){
//						errorMsg.append("页签"+sheetName+" 第"+lineNum+"行 编码"+colData+"系统中没有找到！");
//						continue;
					}else if( colName.equals("人员姓名") || colName.equals("部门名称")){
						
					}else{    
						if(mapItem.get(colName)!=null){
							//导入数据
							tablevo.setAttributeValue(mapItem.get(colName).getKey(), colData);
						}
					}

				}
				
				String str = "hi_psndoc_techrank,hi_psndoc_title,hi_psndoc_pun,hi_psndoc_enc,hi_psndoc_family,hi_psndoc_edu,hi_psndoc_work";
				if (!str.contains(tableCode)) {
					tablevo.setAttributeValue("pk_corp", corp);
				}

				// 任职信息
				if ("hi_psndoc_deptchg".equals(tableCode)) {
//					tablevo.setAttributeValue("pk_deptdoc",
//							QueryData[2] == null ? "" : QueryData[2]);
//					tablevo.setAttributeValue("pk_psncl",
//							QueryData[3] == null ? "" : QueryData[3]);
//					tablevo.setAttributeValue("pk_postdoc",
//							QueryData[4] == null ? "" : QueryData[4]);
//					tablevo.setAttributeValue("pk_om_duty",
//							QueryData[5] == null ? "" : QueryData[5]);
					tablevo.setAttributeValue("jobtype", 0);
					tablevo.setAttributeValue("lastflag", "Y");
					tablevo.setAttributeValue("bendflag", "N");
					tablevo.setAttributeValue("isreturn", "N");
				}
				// 兼职借用
				if ("hi_psndoc_part".equals(tableCode)) {
					tablevo.setAttributeValue("jobtype", 1);
				}
				
				eavo.setTableVO(tableCode,new CircularlyAccessibleValueObject[] { tablevo });
				hasmapVO.put(tableCode+","+lineNum, eavo);
			}
		}

		if (errorMsg.length() != 0 || !"".equals(errorMsg.toString())) {
			MessageDialog.showErrorDlg(null, "提示","数据导入失败："+errorMsg);
		}
		else {
//			 数据保存
			Set key = hasmapVO.keySet();
			for (Object obkey : key) {
				String code = obkey.toString().split(",")[0];
				// 是否如许数据导入
				ExlsaveChild(hasmapVO.get(obkey), code);
			}
			MessageDialog.showHintDlg(null, "提示", "数据导入成功！");
		}
		
	}
	
	
//
//	/**
//	 * 武燕军 2011-03-18 导入
//	 * 
//	 * @throws java.lang.Exception
//	 */
//
//	public void onExlImport() throws java.lang.Exception {
//		
//
//		// 调用远程接口
//		IUserChildInfoAuth service = (IUserChildInfoAuth) NCLocator
//				.getInstance().lookup(IUserChildInfoAuth.class.getName());
//
//		List listname = new ArrayList();
//		String[] sheetname = new String[] {};
//		HashMap<String, PersonEAVO> hasmapVO = new HashMap<String, PersonEAVO>();
//		String psncode = ""; /* 人员代码 */
//		String msg = ""; /* 错误信息 */
//		String msgcode = "";
//		boolean bool = true; /* 退出循环标记 */
//
//		String corp = ClientEnvironment.getInstance().getCorporation()
//				.getPrimaryKey();
//
//		// DefaultMutableTreeNode node = getSelectedNode();
//		// String corp=((CtrlDeptVO)node.getUserObject()).getPk_corp() ;
//		// if ("0001".equals(corp)){
//		// corp=ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
//		// }
//
//		// 主表查询sql
//
//		String sql = " select distinct "
//				+ " bd_psndoc.pk_psndoc, "
//				+ /* 人员管理档案 */
//				" bd_psnbasdoc.pk_psnbasdoc, "
//				+ /* 人员基本档案 */
//				" bd_deptdoc.pk_deptdoc,  "
//				+ /* 部门 */
//				" bd_psncl.pk_psncl,  "
//				+ /* 人员类别主键 */
//				" om_job.pk_om_job ,  "
//				+ /* 岗位 */
//				" om_duty.pk_om_duty  ,"
//				+ /* 职务 */
//				" hi_psndoc_deptchg.pk_psndoc_sub"
//				+ /* 任职信息PK */
//				" from hi_psndoc_deptchg "
//				+ " inner join bd_psndoc       on hi_psndoc_deptchg.pk_psndoc    = bd_psndoc.pk_psndoc "
//				+ " inner join bd_psnbasdoc    on hi_psndoc_deptchg.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc "
//				+ " inner join bd_corp         on hi_psndoc_deptchg.pk_corp      = bd_corp.pk_corp "
//				+ " left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc   = bd_deptdoc.pk_deptdoc "
//				+ " inner join bd_psncl        on hi_psndoc_deptchg.pk_psncl     = bd_psncl.pk_psncl "
//				+ " left outer join om_job     on hi_psndoc_deptchg.pk_postdoc   = om_job.pk_om_job "
//				+ " left outer join om_duty    on hi_psndoc_deptchg.pk_om_duty   = om_duty.pk_om_duty "
//				+ " where hi_psndoc_deptchg.lastflag = 'Y' "
//				//+ " and hi_psndoc_deptchg.bendflag = 'N' "
//				+ " and bd_psndoc.sealdate is null "
//				+ " and bd_deptdoc.hrcanceled = 'N' "
//				+ " and hi_psndoc_deptchg.jobtype = 0 "
//				+
//				// " and (hi_psndoc_deptchg.isreturn = 'N') "+ /*是否返聘*/
//				" and bd_psndoc.indocflag = 'Y' "
//				//+ " and (bd_psndoc.psnclscope = 0 or bd_psndoc.psnclscope = 5) "
//				+ " and bd_psncl.psnclscope = 0 "
////				+ " and (hi_psndoc_deptchg.pk_corp = '" + corp + "') "
//				+ " and (bd_corp.ishasaccount = 'Y') "
//				+ " and (bd_corp.isseal is null or bd_corp.isseal <> 'Y') "
//				+ " and (bd_corp.pk_corp in " + "    (select pk_corp "
//				+ "        from sm_createcorp "
//				+ "       where sm_createcorp.pk_corp = bd_corp.pk_corp "
//				+ "        and funccode = '6007')         "
//				+ " and bd_psndoc.psncode='Mypsncode' )";
//
//		SetdictVO[] setdictVO = PubDelegator.getISetdict()
//				.queryAllSetdictForClass(corp, nc.vo.hi.pub.CommonValue.PERSON);
//		BillTabVO[] btvos = getBillTabVOs(setdictVO);
//
//		int count = btvos.length;
//		for (int i = 0; i < count; i++) {
//			BillTabVO tabco = btvos[i];
//			// 名称
//			listname.add(tabco.getTabname());
//		}
//
//		// 动态取页签
//		// BillTabbedPane btp = getCard().getBodyTabbedPane();
//		// int count = btp.getTabCount();
//		// for (int i = 0; i < count; i++) {
//		// BillScrollPane bsp = (BillScrollPane) btp.getComponentAt(i);
//		// //名称
//		// listname.add(bsp.getName());
//		// }
//
//		// 页签数组
//		sheetname = (String[]) listname.toArray(new String[count]);
//		ImportExcelData data = new ImportExcelData();
//		HashMap<String, Object[][]> hm = data.executeImport(sheetname);
//
//		for (int i = 0; i < count; i++) {
//			bool = true;
//			BillTabVO bsp = (BillTabVO) btvos[i];
//			// 页签编码
//			String tableCode = bsp.getTabcode();
//			// 页签名称
//			String tableName = bsp.getTabname();
//			// 页签信息
//			List items = NewListValue("select datatype,fldcode,fldname,pk_fldreftype from HI_FLDDICT WHERE PK_SETDICT = '"
//					+ bsp.getPrimaryKey() + "'");
//			// 通过页签取导入数据
//			Object[][] tabDate = hm.get(tableName);
//			// 无数据导入
//			if (tabDate == null || tabDate.length == 1)
//				continue;
//			// 加空行的一个处理
//
//			for (int x = 1; x < tabDate.length; x++) {
//				if (!bool)
//					break;
//				// 子表vo数据
//				GeneralVO tablevo = new GeneralVO();
//				// 主表数据设置
//				PersonEAVO eavo = new PersonEAVO();
//
//				// 数据项匹配
//				for (int y = 0; y < tabDate[x].length; y++) {
//					String colName = tabDate[0][y].toString().trim();
//					if ("人员编码".equals(colName)) {
//						// 人员代码不能为空
//						if (tabDate[x][y] == null) {
//							msg += tableName + ",";
//							msgcode += tableCode + ",";
//							bool = false;
//							break;
//						}
//						psncode = tabDate[x][y].toString().trim();
//
//						Object[] QueryData = NewsValue(sql.replace("Mypsncode",
//								psncode));
//						if (QueryData == null || QueryData.length == 0) {
//							msg += tableName + ","+"导入人员编码"+psncode+"不存在";
//							msgcode += tableCode + ",";
//							bool = false;
//							break;
//						}
//
//						tablevo.setAttributeValue("pk_psndoc",
//								QueryData[0] == null ? "" : QueryData[0]);
//						tablevo.setAttributeValue("pk_psnbasdoc",
//								QueryData[1] == null ? "" : QueryData[1]);
//						tablevo.setAttributeValue("pk_psndoc_sub",
//								QueryData[6] == null ? "" : QueryData[6]);
//						tablevo.setAttributeValue("recordnum", 0);
//
//						String str = "hi_psndoc_techrank,hi_psndoc_title,hi_psndoc_pun,hi_psndoc_enc,hi_psndoc_family,hi_psndoc_edu,hi_psndoc_work";
//						if (!str.contains(tableCode)) {
//							tablevo.setAttributeValue("pk_corp", corp);
//						}
//
//						// 任职信息
//						if ("hi_psndoc_deptchg".equals(tableCode)) {
//							tablevo.setAttributeValue("pk_deptdoc",
//									QueryData[2] == null ? "" : QueryData[2]);
//							tablevo.setAttributeValue("pk_psncl",
//									QueryData[3] == null ? "" : QueryData[3]);
//							tablevo.setAttributeValue("pk_postdoc",
//									QueryData[4] == null ? "" : QueryData[4]);
//							tablevo.setAttributeValue("pk_om_duty",
//									QueryData[5] == null ? "" : QueryData[5]);
//							tablevo.setAttributeValue("jobtype", 0);
//							tablevo.setAttributeValue("lastflag", "Y");
//							tablevo.setAttributeValue("bendflag", "N");
//							tablevo.setAttributeValue("isreturn", "N");
//						}
//						// 兼职借用
//						if ("hi_psndoc_part".equals(tableCode)) {
//							tablevo.setAttributeValue("jobtype", 1);
//						}
//						eavo.setPk_psnbasdoc(QueryData[1].toString());
//						eavo.setPk_psndoc(QueryData[0].toString());
//					}
//
//					for (int k = 0; k < items.size(); k++) {
//						Object[] ob = (Object[]) items.get(k);
//						// 取页签对应的字段及属性
//						if (colName.equals(ob[2].toString().trim())) {
//							if (tabDate[x][y] != null) {
//								if (tablevo.getAttributeValue(ob[1].toString()) == null) {
//									// 数据类型匹配
//									switch (Integer.parseInt(ob[0].toString())) {
//
//									case 4:
//										UFDate vlaue = new UFDate(tabDate[x][y]
//												.toString());
//										tablevo.setAttributeValue(ob[1]
//												.toString(), vlaue);
//										break;
//									case 6:
//										// 参照转换
//										Object[] obxml = service.XmlRefValue(
//												tableCode, ob[1].toString());
//										// 表名，此表PK，name,exlvalue
//										String pk_value = ClientDbCache
//												.creator(obxml[0].toString())
//												.getPkByName(
//														obxml[0].toString(),
//														obxml[1].toString(),
//														obxml[2].toString(),
//														tabDate[x][y]
//																.toString(),
//														obxml[3].toString());
//										tablevo.setAttributeValue(ob[1]
//												.toString(), pk_value);
//										break;
//									default:
//										tablevo.setAttributeValue(ob[1]
//												.toString(), tabDate[x][y]);
//										break;
//									}
//								}
//							}
//						}
//
//					}
//					
//					//参照字段转换及数据设置（修改可扩展为对于导入所有字段的导入数据的设置，一旦按照配置文件导入就不需要上面的代码了） modify by zsh 
//					Object[] obxmldef =service.getTableRef(tableCode,colName);
//					
//					if(obxmldef!=null)
//					{
//							if (tabDate[x][y] != null) {
//								if (tablevo.getAttributeValue(obxmldef[4].toString()) == null) {
//										// 表名，此表PK，name,exlvalue
//										String pk_value = ClientDbCache
//												.creator(obxmldef[0].toString())
//												.getPkByName(
//														obxmldef[0].toString(),
//														obxmldef[1].toString(),
//														obxmldef[2].toString(),
//														tabDate[x][y]
//																.toString(),
//																obxmldef[3].toString());
//										tablevo.setAttributeValue(obxmldef[4]
//												.toString(), pk_value);
//				    				}
//								}
//							
//						}
//
//				}
//
//				// 重复校验
//				if (service.XmlResolving(tableCode, tablevo)) {
//					eavo.setTableVO(tableCode,
//							new CircularlyAccessibleValueObject[] { tablevo });
//					hasmapVO.put(tableCode + "," + x, eavo);
//				} else {
//					msg += tableName + ",";
//					msgcode += tableCode + ",";
//					bool = false;
//					break;
//				}
//			}
//		}
//
//		// 数据保存
//		Set key = hasmapVO.keySet();
//		for (Object obkey : key) {
//			String code = obkey.toString().split(",")[0];
//			// 是否如许数据导入
//			if (!msgcode.contains(code)) {
//				ExlsaveChild(hasmapVO.get(obkey), code);
//			}
//		}
//
//		if (msg.length() != 0 || !"".equals(msg)) {
//			MessageDialog.showErrorDlg(null, "提示","页签数据导入失败："+msg);
//		}
//		else {
//			MessageDialog.showErrorDlg(null, "提示", "数据导入成功！");
//		}
//
//	
//	}

	/**
	 * 武燕军 2011-03-18 导出
	 * 
	 * @throws java.lang.Exception
	 */
	public void onExlExport(PSNDOCEXPORTVO[] valuevo)
			throws java.lang.Exception {
		final Container co=this;
		final HashMap<String, Object[][]> map = new HashMap();
		final PSNDOCEXPORTVO[] p_vos = valuevo;
		//弹出数据等待框
		 Runnable checkRun = new Runnable(){
	            public void run()
	            {
	             //线程对话框：系统运行提示框
	                BannerDialog dialog = new BannerDialog(co);
	                dialog.start();
	                try{
		// 获取导出配置
		
		if (p_vos == null || p_vos.length == 0) {
			MessageDialog.showErrorDlg(null, "错误", "无导出方案，请设置!");
			return;
		}

		// 无主表数据
		if (getPsnList().getTableModel().getRowCount() == 0) {
			MessageDialog.showErrorDlg(null, "错误", "无导出数据!");
			return;
		}

		UITable table = getPsnList().getTable();
		Vector info = diStillHeaderInfo(table);
		// 表头名称
		Object colname[][] = parseInfo(info);
		int len = colname[0].length + p_vos.length;
		// 表头数据
		Object data[][] = getTableData(table);
		BillTabbedPane body = getCard().getBodyTabbedPane();
		// 页签个数

		for (int i = 0; i < body.getTabCount(); i++) {
			boolean bool = false;
			BillScrollPane tab = (BillScrollPane) body.getComponentAt(i);
			// 页签名称
			String tablename = tab.getName();
			// 页签代码
			String tableCode = getCard().getBodyTableCode(tab);
			Object[][] tabData = new Object[6556][len];

			// excel列
			int col = 0;
			for (col = 0; col < colname[0].length; col++) {
				// 添加主表title
				tabData[0][col] = colname[0][col];
			}
			// 导出配置项目
			for (int x = 0; x < p_vos.length; x++) {
				PSNDOCEXPORTVO p_vo = (PSNDOCEXPORTVO) p_vos[x];
				if (p_vo.getPk_parent().equals(tablename)) {
					bool = true;
					// 添加子表title
					tabData[0][col] = p_vo.getName();
					// excel表行
					int row = 1;
					// 主表行数
					for (int j = 0; j < data.length; j++) {
						// 取表体数据
						person = new PersonEAVO();
						person.setPk_psndoc((String) psnList[j]
								.getAttributeValue("pk_psndoc"));
						person.setPk_psnbasdoc((String) psnList[j]
								.getAttributeValue("pk_psnbasdoc"));
						person = loadPsnChildInfo(j, tableCode);
						// 表体vo
						CircularlyAccessibleValueObject[] vo = person
								.getTableVO(tableCode);
						if (vo == null)
							continue;
						// 子表行数
						for (int y = 0; y < vo.length; y++) {
							GeneralVO bodydata = (GeneralVO) vo[y];
							if (bodydata == null)
								continue;
							// 添加主表数据
							for (int k = 0; k < data[j].length; k++) {
								tabData[row][k] = data[j][k];
							}
							// 添加子表数据
							tabData[row][col] = bodydata.getAttributeValue(p_vo
									.getCode());
							row++;
						}
					}
					col++;
				}
			}
			//beg 2012.05.09  sqt  修改 之前的 固话 多余的行，将空行删除
			int newcol=tabData[0].length;
			int newrow=0;
			ArrayList<Integer> listvalue=new  ArrayList<Integer> ();
			for(int m=0;m<tabData.length;m++){
				boolean iscan=true;
					if(tabData[m][1]==null&&tabData[m][2]==null&&tabData[m][3]==null){
						continue;
				}
				newrow++;
			}
			Object[][] newtabData = new Object[newrow][newcol];
			for(int u=0;u<newtabData.length;u++){
				for(int t=0;t<newtabData[u].length;t++){
					newtabData[u][t]=tabData[u][t];
				}
			}
			//end
			if (bool) {
				map.put(tablename, newtabData);
			}
		}
	              
		// 导出
		ExcelOut out = new ExcelOut();
		out.createExcelFile(map);
		 MessageDialog.showHintDlg(dialog,"提示", "导出成功。");
	                } catch(Exception e) {
	                    e.printStackTrace();
//	                    this.showErrorMessage(e.getMessage());
	                } finally {
	                 //销毁系统运行提示框
	                    dialog.end();
	                }
	            }
	        };
	        //启用线程
	        new Thread(checkRun).start();
	}

	/**
	 * 武燕军 2010 03-24 模板下载
	 * 
	 * @throws java.lang.Exception
	 */

	public void onExlDownload() throws java.lang.Exception {

		String pk = "exldoc";
		UIDialog dlg = new UIDialog(this, "模板下载");
		dlg.getContentPane().setLayout(new BorderLayout());
		FileManageUI fm = new FileManageUI(pk);
		// fm.setDownloadFileEnable(false);
		dlg.getContentPane().add(fm, BorderLayout.CENTER);
		dlg.setResizable(true);
		dlg.setSize(600, 400);
		dlg.showModal();

	}

	/**
	 * 武燕军 2010 03-24 导出模板设置
	 * 
	 * @throws java.lang.Exception
	 */
	public void onExlExportUi() throws java.lang.Exception {

		// 删除配置数据
		HYPubBO_Client.deleteByWhereClause(PSNDOCEXPORTVO.class, "");

		// 动态创建导出设置数据
		List listvo = new ArrayList();
		List listvos = new ArrayList();
		List listcode = new ArrayList();

		BillItem[] items = null;

		String sql = "";
		UFBoolean bool = new UFBoolean(true);

		String corp = ClientEnvironment.getInstance().getCorporation()
				.getPrimaryKey(); /* 公司 */

		BillTabbedPane btp = getCard().getBodyTabbedPane();
		int count = btp.getTabCount();
		for (int i = 0; i < count; i++) {
			BillScrollPane bsp = (BillScrollPane) btp.getComponentAt(i);
			// 代码
			String tableCode = getCard().getBodyTableCode(bsp);
			listcode.add(getCard().getBodyPanel(tableCode));

			PSNDOCEXPORTVO exprot = new PSNDOCEXPORTVO();
			exprot.setCode(tableCode);
			exprot.setName(bsp.getName());
			exprot.setPk_corp(corp);
			exprot.setVbillstatus(8);
			exprot.setDr(0);
			listvos.add(exprot);
//			exprot.getPKFieldName();
		}

		// 页签添加
		PSNDOCEXPORTVO[] vos = (PSNDOCEXPORTVO[]) listvos
				.toArray(new PSNDOCEXPORTVO[count]);
		IPsnInf iif= NCLocator.getInstance().lookup(IPsnInf.class);
		iif.saveexptvo(listvos);
//		HYPubBO_Client.insertAry(vos);

		BillScrollPane[] tabpanes = (BillScrollPane[]) listcode
				.toArray(new BillScrollPane[count]);

		for (int j = 0; j < tabpanes.length; j++) {
			BillScrollPane tabpane = tabpanes[j];
			items = tabpane.getTableModel().getBodyItems();
			sql = "select pk_psndoc_export from  hi_psndoc_export where dr=0 and pk_corp='"
					+ corp + "' and code='" + tabpane.getTableCode() + "'";
			String pk_parent = NewValue(sql);

			for (BillItem item : items) {

				if (item.getName().toString().contains("&")) {
					continue;
				}
				PSNDOCEXPORTVO exprot = new PSNDOCEXPORTVO();
				exprot.setCode(item.getKey());
				exprot.setName(item.getName());
				exprot.setPk_parent(pk_parent);
				exprot.setDc_status(bool);
				exprot.setPk_corp(corp);
				exprot.setVbillstatus(8);
				exprot.setDr(0);
				listvo.add(exprot);
			}
		}

		// 字段添加
		PSNDOCEXPORTVO[] vo = (PSNDOCEXPORTVO[]) listvo
				.toArray(new PSNDOCEXPORTVO[count]);
		HYPubBO_Client.insertAry(vo);

		PfLinkData date = new PfLinkData();
		date.setUserObject(this);

		openNodeUI("HY020202", date, ILinkType.LINK_TYPE_MAINTAIN, 2, true);

	};

	public List NewListValue(String sql) {

		IUAPQueryBS service = (IUAPQueryBS) NCLocator.getInstance().lookup(
				IUAPQueryBS.class.getName());
		List list = new ArrayList();
		try {
			list = (List) service.executeQuery(sql, new ArrayListProcessor());
			if (list == null || list.size() == 0) {
				return null;
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 武燕军 2011-03-21
	 * 
	 * @param sql
	 * @return 根据编码获取pk
	 */

	public Object[] NewsValue(String sql) {

		Object[] object = new Object[1];
		IUAPQueryBS service = (IUAPQueryBS) NCLocator.getInstance().lookup(
				IUAPQueryBS.class.getName());
		try {
			List list = (List) service.executeQuery(sql,
					new ArrayListProcessor());
			if (list == null || list.size() == 0) {
				return null;
			} else {
				object = (Object[]) list.get(0);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return object;
	}

	public String NewValue(String sql) {

		String value = "";
		IUAPQueryBS service = (IUAPQueryBS) NCLocator.getInstance().lookup(
				IUAPQueryBS.class.getName());
		try {
			List list = (List) service.executeQuery(sql,
					new ArrayListProcessor());
			if (list == null || list.size() == 0) {
				return "";
			} else {
				Object[] object = (Object[]) list.get(0);
				value = object[0].toString();
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 武燕军 2010-03-24 导入保存
	 * 
	 * @param eavo
	 * @param tableCode
	 * @throws java.lang.Exception
	 */
	private void ExlsaveChild(PersonEAVO eavo, String tableCode)
			throws java.lang.Exception {
		// 获取当前子集数据
		CircularlyAccessibleValueObject[] records = eavo.getTableVO(tableCode);
		String pk_psnbasdoc = eavo.getPk_psnbasdoc();
		// int row = getEditLineNo();
		GeneralVO vo = (GeneralVO) records[0];
		vo.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		// 劳动合同模块加入公司主键值
		if (tableCode.equalsIgnoreCase("hi_psndoc_ctrt")) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
					nc.ui.hr.global.Global.getCorpPK());
			vo.setAttributeValue("isrefer", new UFBoolean("Y"));
			vo.setAttributeValue("pk_locusedcorp", nc.ui.hr.global.Global
					.getCorpPK());
			vo.setAttributeValue("pk_majorcorp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_majorcorp",
					nc.ui.hr.global.Global.getCorpPK());
			vo.setAttributeValue("pk_user", Global.getUserID());
			vo.setAttributeValue("operatedate", Global.getLogDate());
		}
		// v50 add
		if (getBodyTableCode().equalsIgnoreCase("hi_psndoc_dimission")
				|| "hi_psndoc_retire".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_training".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_orgpsn".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_psnchg".equalsIgnoreCase(getCurrentSetCode())
				|| "hi_psndoc_keypsn".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
					nc.ui.hr.global.Global.getCorpPK());
		}
		// modified by zhangdd, 修改培训子集培训发起公司和培训类型保存名称的问题
		if ("hi_psndoc_training".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corptraining", Global.getCorpPK());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corptraining", Global
					.getCorpPK());
		}
		if ("hi_psndoc_ass".equalsIgnoreCase(getCurrentSetCode())) {
			vo.setAttributeValue("pk_corpperson", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corpperson",
					nc.ui.hr.global.Global.getCorpPK());
			Object o = vo.getAttributeValue(Util.PK_PREFIX + "pk_corpassess");
			if (o == null) {
				vo.setAttributeValue("pk_corpassess", getClientEnvironment()
						.getCorporation().getUnitname());
				vo.setAttributeValue(Util.PK_PREFIX + "pk_corpassess",
						nc.ui.hr.global.Global.getCorpPK());
				vo.setAttributeValue("pk_corp", nc.ui.hr.global.Global
						.getCorpPK());
			} else {
				vo.setAttributeValue("pk_corp", (String) o);
			}
		}
		if (tableCode.equalsIgnoreCase("hi_psndoc_part")) {
			Object objJobtype = vo
					.getAttributeValue(Util.PK_PREFIX + "jobtype");
			Integer jobtype = null;
			if (jobtype instanceof Integer) {
				jobtype = (Integer) objJobtype;
			}
			if (jobtype != null) {
				vo.setJobTypeFlag(jobtype);
			}
		}
		// 任职情况子集写入公司主键
		if (tableCode.equalsIgnoreCase("hi_psndoc_deptchg")) {
			vo.setAttributeValue("pk_corp", getClientEnvironment()
					.getCorporation().getUnitname());
			vo.setAttributeValue(Util.PK_PREFIX + "pk_corp",
					nc.ui.hr.global.Global.getCorpPK());
			if (((Integer) vo.getAttributeValue("recordnum")).intValue() == 0) {
				for (int i = 0; i < records.length; i++) {
					GeneralVO vo2 = (GeneralVO) records[i];
					if (((Integer) vo2.getAttributeValue("recordnum"))
							.intValue() == 1) {
						if (vo2.getAttributeValue("enddate") == null
								|| "".equals(vo2.getAttributeValue("enddate")
										.toString().trim())) {
							nc.vo.pub.lang.UFDate enddate = ((nc.vo.pub.lang.UFDate) vo
									.getAttributeValue("begindate"))
									.getDateBefore(1);
							vo2.setAttributeValue("enddate", enddate);
							getCard().getBodyPanel().getTableModel()
									.setValueAt(enddate, i, "enddate");
						}
						break;
					}
				}
			} else if (((Integer) vo.getAttributeValue("recordnum")).intValue() > 0
					&& (vo.getAttributeValue("enddate") == null || "".equals(vo
							.getAttributeValue("enddate").toString().trim()))) {// 当插入行时，把当前行的上一条记录的开始日期的前一天作为当前行的结束日期
				for (int i = 0; i < records.length; i++) {
					GeneralVO vo1 = (GeneralVO) records[i];
					if (((Integer) vo1.getAttributeValue("recordnum"))
							.intValue() + 1 == ((Integer) vo
							.getAttributeValue("recordnum")).intValue()) {
						nc.vo.pub.lang.UFDate enddate = ((nc.vo.pub.lang.UFDate) vo1
								.getAttributeValue("begindate"))
								.getDateBefore(1);
						vo.setAttributeValue("enddate", enddate);
						getCard().getBodyPanel().getTableModel().setValueAt(
								enddate, 0, "enddate");
						break;
					}
				}
			}
		}

		if (vo.getAttributeValue("pk_psndoc") != null) {
			HIDelegator.getPsnInf().insertChild(tableCode,
					vo.getAttributeValue("pk_psndoc").toString(), vo);
		} 

		// if (vo.getAttributeValue("pk_psndoc_sub") == null) {
		// // V35 add 校验字段值的重复
		// if (!PubDelegator.getISetdict().checkRecordUnique(tableCode,
		// (String) vo.getAttributeValue("pk_psnbasdoc"),
		// getUniqueFld(tableCode), vo, null, "pk_psndoc_sub", 0)) {
		// //武燕军
		// //String pk_psndoc_sub =
		// HIDelegator.getPsnInf().insertChild(tableCode, person.getPk_psndoc(),
		// vo);
		// String pk_psndoc_sub = HIDelegator.getPsnInf().insertChild(tableCode,
		// vo.getAttributeValue("pk_psndoc").toString(), vo);
		// records[row].setAttributeValue("pk_psndoc_sub", pk_psndoc_sub);
		// } else {
		// FlddictVO[] fldvos = getUniqueFld(tableCode);
		// String msg =
		// getCard().getBillData().getBodyTableName(getCurrentSetCode())
		// +
		// nc.ui.ml.NCLangRes.getInstance().getStrByID("600704","UPP600704-000197")/*
		// @res "子集" */;
		// for (int i = 0; i < fldvos.length; i++) {
		// msg += fldvos[i].getFldname();
		// if (i < fldvos.length - 1) {
		// msg += ",";
		// }
		// }
		// msg += ","+
		// nc.ui.ml.NCLangRes.getInstance().getStrByID("600704","UPP600704-000196")/*
		// @res "组合信息不允许重复" */;
		// throw new Exception(msg);
		// }
		// } else {
		// // V35 add 校验字段值的重复
		// if (!PubDelegator.getISetdict().checkRecordUnique(tableCode,
		// (String) vo.getAttributeValue("pk_psnbasdoc"),
		// getUniqueFld(tableCode), vo,
		// (String) vo.getAttributeValue("pk_psndoc_sub"),
		// "pk_psndoc_sub", 1)) {
		// HIDelegator.getPsnInf().updateChild(tableCode,person.getPk_psndoc(),
		// vo);
		// } else {
		// FlddictVO[] fldvos = getUniqueFld(tableCode);
		// String msg =
		// getCard().getBillData().getBodyTableName(getCurrentSetCode())
		// +
		// nc.ui.ml.NCLangRes.getInstance().getStrByID("600704","UPP600704-000197")/*
		// @res "子集" */;
		// for (int i = 0; i < fldvos.length; i++) {
		// msg += fldvos[i].getFldname();
		// if (i < fldvos.length - 1) {
		// msg += ",";
		// }
		// }
		// msg += ","+ nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
		// "UPP600704-000196")/* @res "组合信息不允许重复" */;
		// throw new Exception(msg);
		// }
		// }

		person = eavo;// wangkf add

		// 更新
		if (records.length > 0) {

			boolean updated = isUpdateAccRel(vo.getAttributeNames(), tableCode);
			// boolean updated = true ;

			if (updated) {
				GeneralVO voTemp = new GeneralVO();
				voTemp.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				voTemp.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				GeneralVO relVO = HIDelegator.getPsnInf().updateAccRel(
						vo.getAttributeNames(), getRelationMap(), tableCode,
						voTemp);
				HashMap hmRelVO = new HashMap();
				String[] fields = relVO.getAttributeNames();
				GeneralVO psnvo = new GeneralVO();
				GeneralVO baspsnvo = new GeneralVO();
				psnvo.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				psnvo.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				baspsnvo.setAttributeValue("pk_psnbasdoc", person
						.getPk_psnbasdoc());
				baspsnvo.setAttributeValue("pk_psndoc", person.getPk_psndoc());
				boolean isRelPsndoc = false;
				boolean isRelBasPsndoc = false;
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].indexOf(".") < 0) {
						continue;
					}
					String table = fields[i].substring(0, fields[i]
							.indexOf("."));
					if ("bd_psndoc".equalsIgnoreCase(table)) {
						String field = fields[i].substring(fields[i]
								.indexOf(".") + 1);
						psnvo.setAttributeValue(field, relVO
								.getAttributeValue(fields[i]));
						person.getPsndocVO().setAttributeValue(field,
								relVO.getAttributeValue(fields[i]));
						isRelPsndoc = true;
					} else if ("bd_psnbasdoc".equalsIgnoreCase(table)) {
						String field = fields[i].substring(fields[i]
								.indexOf(".") + 1);
						baspsnvo.setAttributeValue(field, relVO
								.getAttributeValue(fields[i]));
						person.getAccpsndocVO().setAttributeValue(field,
								relVO.getAttributeValue(fields[i]));
						isRelBasPsndoc = true;
					}
				}
				if (isRelPsndoc)
					hmRelVO.put("bd_psndoc", psnvo);
				if (isRelBasPsndoc)
					hmRelVO.put("bd_psnbasdoc", baspsnvo);
				HIDelegator.getPsnInf().updateRelTable(hmRelVO);

				// 更新界面数据
				if (isRelPsndoc) {
					String[] field = psnvo.getAttributeNames();
					for (int i = 0; i < field.length; i++) {
						if ("pk_psnbasdoc".equals(field[i])
								|| "pk_psndoc".equals(field[i])) {
							continue;
						}
						BillItem item = getCard().getHeadItem(field[i]);
						if (item != null) {
							Object value = psnvo.getFieldValue(field[i]);
							if (value == null
									|| "#null#".equals(value.toString())) {
								item.setValue(null);
								value = null;
							} else {
								item.setValue(value);
							}
							psnList[listSelectRow].setAttributeValue(field[i],
									value);
						}
					}
				}
				if (isRelBasPsndoc) {
					String[] field = baspsnvo.getAttributeNames();
					for (int i = 0; i < field.length; i++) {
						if ("pk_psnbasdoc".equals(field[i])
								|| "pk_psndoc".equals(field[i])) {
							continue;
						}
						BillItem item = getCard().getHeadItem(field[i]);
						if (item != null) {
							Object value = baspsnvo.getFieldValue(field[i]);
							if (value == null
									|| "#null#".equals(value.toString())) {
								item.setValue(null);
								value = null;
							} else {
								item.setValue(value);
							}
							psnList[listSelectRow].setAttributeValue(field[i],
									value);
						}
					}
				}
			}
			isRelateToAcc = updated;
		}
		subBackupVOs = null;
	}

	/**
	 * 武燕军 取表头
	 * 
	 * @param table
	 * @return
	 */

	private static Vector diStillHeaderInfo(UITable table) {
		Vector info = new Vector();
		GroupableTableHeader header = (GroupableTableHeader) table
				.getTableHeader();
		Vector colInfo;
		for (Enumeration enumeration = header.getColumnModel().getColumns(); enumeration
				.hasMoreElements(); info.addElement(colInfo)) {
			colInfo = new Vector();
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			Enumeration cGroups = header.getColumnGroups(aColumn);
			if (cGroups != null) {
				ColumnGroup cGroup;
				for (; cGroups.hasMoreElements(); colInfo.addElement(cGroup
						.getHeaderValue()))
					cGroup = (ColumnGroup) cGroups.nextElement();

			}
		}

		int len = info.size();
		int mid = 0;
		int max = 0;
		for (int i = 0; i < len; i++) {
			Vector v = (Vector) info.elementAt(i);
			mid = v.size();
			if (mid > max)
				max = mid;
		}

		for (int i = 0; i < len; i++) {
			((Vector) info.elementAt(i)).setSize(max);
			String colname = table.getColumnName(i);
			((Vector) info.elementAt(i)).addElement(colname);
		}

		return info;
	}

	private static String[][] parseInfo(Vector v) {
		int width = v.size();
		int len = ((Vector) v.elementAt(0)).size();
		String col[][] = new String[len][width];
		for (int i = 0; i < len; i++) {
			for (int k = 0; k < width; k++) {
				Vector cv = (Vector) v.elementAt(k);
				col[i][k] = (String) cv.elementAt(i);
			}

		}

		int mid = width;
		width = len;
		len = mid;
		for (int i = 0; i < len; i++)
			if (col[0][i] == null) {
				col[0][i] = col[width - 1][i];
				col[width - 1][i] = null;
			}

		for (int i = len - 1; i > 0; i--) {
			for (int k = 0; k < width; k++)
				if (col[k][i] == col[k][i - 1])
					col[k][i] = null;

		}

		return col;
	}

	/**
	 * 武燕军 取表头数据
	 * 
	 * @param table
	 * @return
	 */

	private static Object[][] getTableData(UITable table) {
		int width = table.getRowCount();
		int length = table.getColumnCount();
		Object obj[][] = new Object[width != 0 ? width : 0][length];
		Object value = null;
		for (int i = 0; i < width; i++) {
			for (int k = 0; k < length; k++) {
				value = table.getValueAt(i, k);
				if ((value instanceof UFBoolean) || (value instanceof Boolean)) {
					if (value.toString().equals("true")
							|| value.toString().equals("Y"))
						value = nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"uifactory_report",
								"UPPuifactory_report-000023")/* @res "√" */;
					else
						value = "";
				} else if ((value instanceof UFDouble)
						|| (value instanceof Integer))
					if (value instanceof UFDouble) {
						if (((UFDouble) value).doubleValue() == 0.0D)
							value = "";
					} else if ((value instanceof Integer)
							&& ((Integer) value).intValue() == 0)
						value = "";
				obj[i][k] = value;
			}

		}

		return obj;
	}

	/**
	 * 模板
	 * 
	 * @param funCode
	 * @param pflink
	 * @param iLinkType
	 * @param iOpenMode
	 * @param bSyncOpen
	 * @return
	 */

	private boolean openNodeUI(String funCode, PfLinkData pflink,
			int iLinkType, int iOpenMode, boolean bSyncOpen) {
		boolean isOpenSucess = true;

		FuncRegisterVO frVO = SFClientUtil.findFRVOFromMenuTree(funCode);

		if (frVO == null)
			// 如果没有权限，且不检查权限，则从功能注册表中获取节点信息
			frVO = FuncRegister.getFuncRegisterVOByCode(funCode);

		if (frVO != null) {
			switch (iOpenMode) {
			// 三种不同的打开模式，都以同步方式打开
			case IFuncWindow.WINDOW_TYPE_FRAME:
				FuncNodeStarter.openLinkedFrame(frVO, iLinkType, pflink, null,
						bSyncOpen, null);
				break;
			case IFuncWindow.WINDOW_TYPE_DLG:
				FuncNodeStarter.openLinkedDialog(frVO, iLinkType, pflink, this,
						bSyncOpen, true, null);
				break;
			case IFuncWindow.WINDOW_TYPE_TAB:
				FuncNodeStarter.openLinkedTabbedPane(frVO, iLinkType, pflink,
						this, bSyncOpen, null);
				break;
			default:
				break;
			}
		} else {
			isOpenSucess = false;
		}
		return isOpenSucess;
	}

	private BillTabVO[] getBillTabVOs(SetdictVO[] setdictVO) {
		Vector vector = null;
		List list = new ArrayList();
		try {
			// 生成树
			if (setdictVO != null && setdictVO.length > 0) {
				// 转换成Pair数组

				for (int i = 0; i < HRResHiDictHelper
						.SetdictVOConvertToPair(setdictVO).length; i++) {
					SetdictVO tempvo = (SetdictVO) HRResHiDictHelper
							.SetdictVOConvertToPair(setdictVO)[i].getValue();
					if (tempvo.getIsmainset().booleanValue())
						continue;
					BillTabVO btvo = new BillTabVO();
					btvo.setTabcode(tempvo.getSetcode());
					btvo.setTabname(HRResHiDictHelper
							.SetdictVOConvertToPair(setdictVO)[i].getName());
					btvo.setPrimaryKey(tempvo.getPk_setdict());
					list.add(btvo);

				}
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"10220109", "UPP10220109-000065")/* @res "生成树错误！" */);
			return null;
		}
		return (BillTabVO[]) list.toArray(new BillTabVO[0]);
	}

	private HashMap getInfoMap(){
		
		HashMap<String,String> map = new HashMap<String, String>();
		
		map.put("工作信息", "人员工作信息");
		map.put("个人信息", "人员个人信息");
		map.put("任职信息", "人员任职信息");
		map.put("合同信息", "人员合同信息");
		map.put("工人技术等级职务情况", "工人技术等级及职务情况");
		
		map.put("考核信息", "人员考核信息");
//		map.put("学历及学位", "人员学历及学位");
//		map.put("学历信息", "人员学历及学位");
		map.put("履历记录", "人员履历记录");
		map.put("家庭信息", "人员家庭信息");
		
		map.put("离职信息", "人员离职信息");
		
		return map;
	}
	
	
	
	private HashMap getInfoMap2(){
		
		HashMap<String,String> map = new HashMap<String, String>();
		
		map.put("人员工作信息","工作信息");
		map.put("人员个人信息","个人信息");
		map.put("人员任职信息","任职信息");
		map.put("人员合同信息","合同信息");
		map.put("工人技术等级及职务情况","工人技术等级职务情况");
		
		map.put("人员考核信息","考核信息");
//		map.put("人员学历及学位","学历及学位");
//		map.put("人员学历及学位","学历信息");
		map.put("人员履历记录","履历记录");
		map.put("人员家庭信息","家庭信息");
		map.put("人员学历及学位","学历信息");
		map.put("人员离职信息","离职信息");
		
		return map;
	}
	
	
}
