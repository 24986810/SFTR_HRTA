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
 * н�ʵ����Ӽ�����Ϊ����ģ���д��������ӱ�ṹ����˵���ȡ��һ���ڵ㡣
 * �������ڣ�(2004-6-3 14:49:40)
 * @author��Administrator
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
	// ��Ŧ����o

	private final ButtonObject m_bnQuery = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000143")/*
			 * @res
			 * "��ѯ"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000143")/*
			  * @res "��ѯ"
			  */, 0, "��ѯ"); /*-=notranslate=-*/
	private final ButtonObject m_bnRefresh = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			"UPPT60130715-000144")/*
			 * @res "ˢ��"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000144")/*
			  * @res "ˢ��"
			  */, 0, "ˢ��"); /*-=notranslate=-*/
	private final ButtonObject m_boPrintGroup = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			"UPPT60130715-000145")/*
			 * @res "��ӡ"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000145")/*
			  * @res "��ӡ"
			  */, 0, "��ӡ"); /*-=notranslate=-*/

//	private final ButtonObject m_boPrint = new ButtonObject(NCLangRes
//			.getInstance().getStrByID("60131600", "UPT60131600-000052")/*
//			 * @res
//			 * "ģ���ӡ"
//			 */, NCLangRes.getInstance().getStrByID("60131600", "UPT60131600-000052")/*
//			 * @res
//			 * "ģ���ӡ"
//			 */, 0, "ģ���ӡ"); /*-=notranslate=-*/
//	private final ButtonObject m_boDirectPrint = new ButtonObject(NCLangRes
//			.getInstance().getStrByID("60131600", "UPT60131600-000053")/*
//			 * @res
//			 * "ֱ�Ӵ�ӡ"
//			 */, NCLangRes.getInstance().getStrByID("60131600", "UPT60131600-000053")/*
//			 * @res
//			 * "ֱ�Ӵ�ӡ"
//			 */, 0, "ֱ�Ӵ�ӡ"); /*-=notranslate=-*/
	
	private final ButtonObject m_boNomalPrint = new ButtonObject("��������", "��������", 0, "��������");
	private final ButtonObject m_boGwbdPrint = new ButtonObject("��λ�䶯", "��λ�䶯", 0, "��λ�䶯");
	private final ButtonObject m_boNewLyPrint = new ButtonObject("��¼��", "��¼��", 0, "��¼��");
	private final ButtonObject m_boOtherPrint = new ButtonObject("����", "����", 0, "����");
	
	private final ButtonObject m_boRemark = new ButtonObject("��ע", "��ע", 0, "��ע");// zhanghua
	//private final ButtonObject m_boTempSave = new ButtonObject("�ݴ�", "�ݴ�", 0, "�ݴ�");// zhanghua
	
	private final ButtonObject m_btnWXYJ = new ButtonObject("����һ��", "����һ��", 0, "����һ��");// lizhuchao

	private final ButtonObject m_bnSave = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000147")/*
			 * @res
			 * "����"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000147")/*
			  * @res "����"
			  */, 0, "����"); /*-=notranslate=-*/
	private final ButtonObject m_bnCancel = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000148")/*
			 * @res
			 * "ȡ��"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000148")/*
			  * @res "ȡ��"
			  */, 0, "ȡ��"); /*-=notranslate=-*/
	private final ButtonObject m_bnInsert = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000149")/*
			 * @res
			 * "����"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000149")/*
			  * @res "����"
			  */, 0, "����"); /*-=notranslate=-*/
	private final ButtonObject m_bnModify = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000150")/*
			 * @res
			 * "�޸�"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000150")/*
			  * @res "�޸�"
			  */, 0, "�޸�"); /*-=notranslate=-*/
	private final ButtonObject m_bnAdd = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000151")/*
			 * @res
			 * "����"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000151")/*
			  * @res "����"
			  */, 0, "����"); /*-=notranslate=-*/
	
	private final ButtonObject m_bnTempAdd = new ButtonObject("�����ݴ�"
			, "�����ݴ�"
			, 0, "�����ݴ�"); /*-=notranslate=-*/
	
	private final ButtonObject m_bnDelete = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPPT60130715-000152")/*
			 * @res
			 * "ɾ��"
			 */, nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
			 "UPPT60130715-000152")/*
			  * @res "ɾ��"
			  */, 0, "ɾ��"); /*-=notranslate=-*/

	private final ButtonObject m_bnImport = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000011")/*"����"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000011")/*"����"*/, 0,
	"����"); /*-=notranslate=-*/
	private final ButtonObject m_bnExport = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000056")/*"����"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000056")/*"����"*/, 0,
	"����"); /*-=notranslate=-*/
	private final ButtonObject m_bnImportData = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000012")/*"��������"*/,
			nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000012")/*"��������"*/, 0, "��������"); /*-=notranslate=-*/
	private final ButtonObject m_bnExportData = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000013")/*"��������"*/,
			nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000013")/*"��������"*/, 0, "��������"); /*-=notranslate=-*/
	// private final ButtonObject m_bnDispaly = new
	// ButtonObject("��ʾ","��ʾ",0,"��ʾ"); /*-=notranslate=-*/
	private final ButtonObject m_bnReturn = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000038")/*"����"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("common", "UC001-0000038")/*"����"*/, 0,
	"����"); /*-=notranslate=-*/

	private final ButtonObject m_bnAdjust = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000010")/*"н���յ�"*/, nc.ui.ml.NCLangRes
			.getInstance().getStrByID("60130715", "UPT60130715-000010")/*"н���յ�"*/, 0,
	"н���յ�"); /*-=notranslate=-*/

	//add by xhhrp suhf1 begin ������ά����¼��ѯ Maintenance record 
	private final ButtonObject m_bnQuery_MaintenRecord =  new ButtonObject("ά����¼","ά����¼", 0, "ά����¼");
//	zhf н��ά��
	private final ButtonObject m_btnBfbk =  new ButtonObject("н��ά��","н��ά��", 0, "н��ά��");

//	zhf ��Ա��Ϣ�鿴
	private final ButtonObject m_btnPsn =  new ButtonObject("��Ա��Ϣ�鿴","��Ա��Ϣ�鿴", 0, "��Ա��Ϣ�鿴");

//	zhf ��Ա��Ϣ�鿴
	private final ButtonObject m_btnUpdateGjj =  new ButtonObject("�����𼰷���ͬ��","�����𼰷���ͬ��", 0, "�����𼰷���ͬ��");
	
	private final ButtonObject m_btnUpdateFF =  new ButtonObject("���ű�־����","���ű�־����", 0, "���ű�־����");

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
	// add by sunxj 2010-02-23 ���ٲ�ѯ��� start
	protected boolean isQuery = false;// ����Ƿ��ѯ��
	protected boolean isQuickSearch = false;// ����Ƿ���ٲ�ѯ��
	protected String quickWherePart = null;
	// add by sunxj 2010-02-23 ���ٲ�ѯ��� end

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

	// ����״̬
	public final static int UI_STAT_EXPOTRT = 10;

	public final static int UI_STAT_IMPOTRT = 11;

	private List<String> insertlist;

	private int currentstat = -1;

	private String querywhere = "";// ��ѯ�Ի�������
	/**
	 * PsnWadocUI ������ע�⡣
	 */
	public PsnWadocUI() {
		super();
		initialize();
	}

	/**
	 * ��Ӽ����ӿڡ� �������ڣ�(2004-6-4 16:49:27)
	 */
	public void addListener() {

	}

	/**
	 * �����ڿ���������ýڵ��š� �������ڣ�(2003-2-27 12:28:41)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getModuleCodeWithDebug() {
		return "60130715";
	}

	/**
	 * ����ʵ�ָ÷���������ҵ�����ı��⡣
	 * 
	 * @version (00-6-6 13:33:25)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getTitle() {
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
		"UPP60130715-000002")/*
		 * @res "Ա��н�ʱ䶯���"
		 */;
	}

	/**
	 * ������ά����¼��ѯ dialog
	 * @author suhf1
	 */
	private UIDialog getUIdialog(){
		if(dialog_wa == null){
			dialog_wa = new UIDialog(this,"������ά����¼����");
			dialog_wa.setContentPane(getMaintenRecord());
			dialog_wa.setSize(400, 260);
		}
		return dialog_wa;
	}
	/**
	 * ������ά����¼��ѯ panel
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
	 * ȷ����ť
	 * @return
	 * @author suhf1
	 */
	private UIButton getUIButtonConfrim(){
		if(confiromB == null){
			confiromB = new UIButton("ȷ��");
			confiromB.setVisible(true);
			confiromB.addActionListener(this);
		}
		return confiromB;
	}
	/**
	 * ȡ����ť
	 * @return
	 * @author suhf1
	 */
	private UIButton getUIButtonCancle(){
		if(cancleB == null){
			cancleB = new UIButton("ȡ��");
			cancleB.setVisible(true);
			cancleB.addActionListener(this);
		}
		return cancleB;
	}
	/**
	 * Ĭ��״̬___���ж�н��Ŀ���м�¼
	 * @author suhf1
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioAllRecordsRB() {
		if (allRecordsRB == null) {
			try {
				allRecordsRB = new nc.ui.pub.beans.UIRadioButton();
				allRecordsRB.setName("UIRadioAllRecordsRB");
				allRecordsRB.setText("Ĭ��״̬");
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
	 * ����н����Ŀ_���ж�н��Ŀ���¼�¼
	 * @author suhf1
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioLatestRecordsRB() {
		if (latestRecordsRB == null) {
			try {
				latestRecordsRB = new nc.ui.pub.beans.UIRadioButton();
				latestRecordsRB.setName("UIRadioAllRecordsRB");
				latestRecordsRB.setText("����н����Ŀ");
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
	 *ĳһ��н����Ŀ_һ��н����Ŀ���м�¼
	 * @author suhf1
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioCertainRecordRB() {
		if (certainRecordRB == null) {
			try {
				certainRecordRB = new nc.ui.pub.beans.UIRadioButton();
				certainRecordRB.setName("UIRadioCertainRecordRB");
				certainRecordRB.setText("ĳһ��н����Ŀ");
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
	 * н����Ŀ����
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
	 * ���� UISplitPane1 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UISplitPane
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ���� UIRightPnl ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ÿ�������׳��쳣ʱ������
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* ��ȥ���и��е�ע�ͣ��Խ�δ��׽�����쳣��ӡ�� stdout�� */
		exception.printStackTrace(System.out);
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			// user code begin {1}
			m_boPrintGroup.removeAllChildren();
//			m_boPrintGroup.addChildButton(m_boPrint);
//			m_boPrintGroup.addChildButton(m_boDirectPrint);
			
			m_boPrintGroup.addChildButton(m_boNomalPrint); //��������
			m_boPrintGroup.addChildButton(m_boGwbdPrint);  //��λ�䶯
			m_boPrintGroup.addChildButton(m_boNewLyPrint); //��¼��
			m_boPrintGroup.addChildButton(m_boOtherPrint); //����

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
	 * �õ�����Panel
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
	 * �õ�Label
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
				"UPP60130715-000081")// @res "�����ļ�·��������"
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
	 * �õ��ļ�����
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
	 * �����ť
	 * 
	 * @return
	 */
	protected nc.ui.pub.beans.UIButton getUIBnBrowser() {
		if (ivjUIBnBrowser == null) {
			try {
				ivjUIBnBrowser = new nc.ui.pub.beans.UIButton();
				ivjUIBnBrowser.setName("UIBnBrowser");
				ivjUIBnBrowser.setIButtonType(2/** ���հ�ť */
				);
				ivjUIBnBrowser.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("common", "UC001-0000021")/*
						 * @res "���"
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
							// "Excel�ļ�(*.xls)"
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
	 * �õ�����Panel
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
	 * �õ�Label
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
				"UPP60130715-000107")// @res "�����ļ�·��������"
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
	 * �õ��ļ�����
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
	 * �����ť
	 * 
	 * @return
	 */
	protected nc.ui.pub.beans.UIButton getUIBnBrowserim() {
		if (ivjUIBnBrowserim == null) {
			try {
				ivjUIBnBrowserim = new nc.ui.pub.beans.UIButton();
				ivjUIBnBrowserim.setName("UIBnBrowserim");
				ivjUIBnBrowserim.setIButtonType(2/** ���հ�ť */
				);
				ivjUIBnBrowserim.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("common", "UC001-0000021")/*
						 * @res "���"
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
							// "Excel�ļ�(*.xls)"
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
	 * ����ʵ�ָ÷�������Ӧ��ť�¼��� ��ת��controller��ȥ
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
//			} else if (bo == m_boPrint) {// ģ���ӡ
//				onPrintBytemplate();
//			} else if (bo == m_boDirectPrint) {// ֱ�Ӵ�ӡ
//				onPrintDirect();
			} else if (bo == m_boNomalPrint) { // ��������
				onPrintNomal();
				//MessageDialog.showHintDlg(this, "��ʾ", "����������ӡ");
			} else if (bo == m_boGwbdPrint) {// ��λ�䶯
				onPrintGwbd();
				//MessageDialog.showHintDlg(this, "��ʾ", "��λ�䶯��ӡ");
			} else if (bo == m_boNewLyPrint) {// ��¼��
				onPrintNewLy();
				//MessageDialog.showHintDlg(this, "��ʾ", "��¼�ô�ӡ");
			} else if (bo == m_boOtherPrint) {// ����
				onPrintOther();
				//MessageDialog.showHintDlg(this, "��ʾ", "������ӡ");
			} else if (bo == m_bnSave) {
				onSave();
				suffix = ButtonTipMessage.SUCCESSED;
			} else if (bo == m_bnCancel) {
				onCancel();
				suffix = ButtonTipMessage.SUCCESSED;
			} else if (bo == m_bnImport) { // ����
				onImport();
			} else if (bo == m_bnExport) { // ����
				onExport();
			} else if (bo == m_bnReturn) {// ����
				onReturn();
			} else if (bo == m_bnExportData) {// ��������
				onExportData();
			} else if (bo == m_bnImportData) {// ��������
				onImportData();
			}
			// н���յ�
			else if (bo == m_bnAdjust) {
				onBatchAdjust();
			} 
			//add by xhhrp suhf1 ������ά����¼
			else if(bo == m_bnQuery_MaintenRecord) {
				getUIdialog().showModal();
			}else if(bo == m_btnBfbk){
				//н��ά��
				onBfbkSet();
			}else if(bo == m_btnPsn){
				//��Ա��Ϣ�鿴
				onPsnView();
			}else if(bo == m_btnUpdateGjj){
				onUpdateGjj();
			}else if(bo == m_btnUpdateFF){
				onUpdateff();
			}else if(bo == m_boRemark){
				//��ע
				onRemarkDlg();
			}else if(bo == m_bnTempAdd){
				ontTempAdd();
			}else if(bo== m_btnWXYJ){
				//����һ��鿴
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
		showHintMessage("���ķ��ű�־");
		int rows[] = getPsnWadocSubPane().getBillScrollPane().getTable().getSelectedRows();
		if(rows==null||rows.length<=0) return;
		if(MessageDialog.showOkCancelDlg(this, "��ʾ", "ȷ������ѡ���еķ��ű�־")!=UIDialog.ID_OK) return;
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
		showHintMessage("���ķ������");
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
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		if(currentSelectRow < 0){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
				currentSelectRow);
		if(vo == null){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		if(vo.getPk_psndoc() == null){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		InfoDLG dlg =  getPsnInfoDlg(vo.getPk_psndoc());
		if(dlg.showModal() == UIDialog.ID_OK){
			onRefresh();
		}
		
		
	}
	
	protected void onWXYJDlg() throws Exception{
		if(getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount() < 1){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		if(currentSelectRow < 0){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
				currentSelectRow);
		if(vo == null){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		if(vo.getPk_psndoc() == null){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		
		if(vo.getPk_psndoc() != null){
			PsndocWadocVO psndocWadocVO = getPsndocWadocVO(vo.getPk_psndoc());
			if(psndocWadocVO == null){
				nc.ui.pub.beans.MessageDialog.showErrorDlg(this, "����", "δά���籣����������");
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
		showHintMessage("���¹��������������");
		String dwft = "0001691000000000BYY5";
		String gjjjs = "0001691000000000T5E2";
		String sbjs = "0001691000000000EIOF";
		if(MessageDialog.showOkCancelDlg(this, "��ʾ", "ȷ����������")!=UIDialog.ID_OK) return;
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
				showHintMessage("�������,����������0������");
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
				showHintMessage("�������,������"+list.size()+"������");
				MessageDialog.showHintDlg(this, "��ʾ", "�������,������"+list.size()+"������");
			}else{
				MessageDialog.showHintDlg(this, "��ʾ", "�������,������0������");
			}
		}
//		PsndocWadocMainVO[] mainVOs = getWadocMain(Global.getCorpPK(),getQueryDlg());
//		WADelegator.getWaPsndoc().queryWadocMainDataMorePsnclscope(Global.getCorpPK(), "",
//		null, null, null);
		onRefresh();
	}

	/**
	 * ��ʾ����
	 */
	public void onShowInfo() {
		try {
			String key = "";
			if(insertlist.size()<1){
				MessageDialog.showErrorDlg(this, null, ResHelper.getString("60130715", "UPP60130715-000109")/*"û�п��Ե��������"*/);
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
			"UPP60130715-000054")// @res "��ʾ"
			, ResHelper.getString("60130715", "UPP60130715-000084")// @res
			// "�ļ�����ɹ�"
			);
			// this.insertlist = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������밴ť
	 */
	public void onImport() {
		this.remove(this.getExportTopPanel());
		this.add(this.getImportTopPanel(), "North");
		this.getImportTopPanel().setVisible(true);
		this.currentstat = UI_STAT_IMPOTRT;
		this.setBtnsStatWithUIStat(UI_STAT_IMPOTRT);
	}

	/**
	 * ��������
	 */
	public void onImportData() {
		try {
			// �ж�����ļ����Ƿ�Ϊ��
			String imfilename = getOutFileNameim();
			if (imfilename == null || imfilename.trim().length() < 1) {
				return;
			}
			boolean fileHasExisted = false;
			try {
				// �ж�Ҫ�������ļ��Ƿ��Ѵ���
				fileHasExisted = fileExist(imfilename);
				if (!fileHasExisted) {
					int choice = MessageDialog.showYesNoDlg(this, ResHelper
							.getString("60130715", "UPP60130715-000054")// @res
							// "��ʾ"
							, ResHelper.getString("60130715",
							"UPP60130715-000085")// @res
							// "Ҫ������ļ������ڻ򵼳��ļ���·�������ƷǷ�������ִ����?"
					);
					if (choice != MessageDialog.ID_YES) {
						return;
					}
				}
			} catch (Exception e) {
				int choice = MessageDialog.showYesNoDlg(this, ResHelper
						.getString("60130715", "UPP60130715-000054")// @res "��ʾ"
						, ResHelper.getString("60130715", "UPP60130715-000086")// @res
						// "�ж�Ҫ������ļ��Ƿ��Ѵ���ʱ��������ִ�н��滻ԭ���ļ���Ҫ������?"
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
					MessageDialog.showHintDlg(this,"��ʾ","��"+(i+1)+"�м��������ƥ��");
					return;
				}
			}
			filein.closeFile();
			// �õ����еĵ�����Ϣ
			// List<String> keylist =
			// WADelegator.getWaPsndoc().importExcelData(wadocvos);
			List<String> keylist = null;
			Map mapinfo = WADelegator.getWaPsndoc().compareDataInfo(wadocvos);
			if (mapinfo == null) {
				keylist = WADelegator.getWaPsndoc().insertInfo(wadocvos);
				this.insertlist = keylist;
				// ���ݸ���
				onShowInfo();
			} else {
				boolean bool = (Boolean) mapinfo.get("flag");
				//=====================================beg=========================================================
				//����ʱ���Ӹ��ǹ��ܣ�֮ǰ��û�и��ǹ��� ֻ�в���ȡ�� sqt
				if (!bool) {
					int answer = MessageDialog031.showYesNoCancelDlg(this,"ѯ��",(String) mapinfo.get("info"));
					if (answer == UIDialog.ID_YES) {
						keylist = WADelegator.getWaPsndoc()
						.insertInfo(wadocvos);
						this.insertlist = keylist;
						// ���ݸ���
						onShowInfo();
					}else if(answer == UIDialog.ID_NO){
						wadocvos = (PsndocWadocVO[]) mapinfo.get("rvo");
						for(int i =0;i<wadocvos.length;i++){
							WADelegator.getWaPsndoc()
							.updatePsndocWadoc(wadocvos[i]);
						}
						this.insertlist = (List<String>) mapinfo.get("keylist");
						// ���ݸ���
						onShowInfo();
					}
					//=====================================beg=========================================================
				} else {
					keylist = WADelegator.getWaPsndoc().insertInfo(wadocvos);
					this.insertlist = keylist;
					// ���ݸ���
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
			// "�ļ�����ʧ�ܣ����鵼���ļ�·���������Ƿ���ȷ�������ļ��Ѵ��ڲ��Ҵ��ڴ�״̬��"
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
	 * ִ�����ݵ�������
	 */
	public void onExport() {
		this.remove(this.getImportTopPanel());
		this.add(this.getExportTopPanel(), "North");
		this.getExportTopPanel().setVisible(true);
		this.currentstat = UI_STAT_EXPOTRT;
		this.setBtnsStatWithUIStat(UI_STAT_EXPOTRT);
	}

	/**
	 * ִ�����ݵ�������
	 */
	public void onExportData() {
		try {
			// �ж�����ļ����Ƿ�Ϊ��
			String outfilename = getOutFileName();
			if (outfilename == null || outfilename.trim().length() < 1) {
				// int choice =
				// MessageDialog.showYesNoDlg(this,"��ʾ","����ѡ��Ҫ����������!");
				return;
			}
			boolean fileHasExisted = false;
			try {
				// �ж�Ҫ�������ļ��Ƿ��Ѵ���
				fileHasExisted = fileExist(outfilename);
				if (fileHasExisted) {
					int choice = MessageDialog.showYesNoDlg(this, ResHelper
							.getString("60130715", "UPP60130715-000054")// @res
							// "��ʾ"
							, ResHelper.getString("60130715",
							"UPP60130715-000088")// @res
							// "Ҫ�������ļ��Ѵ��ڻ򵼳��ļ���·�������ƷǷ�������ִ����?"
					);
					if (choice != MessageDialog.ID_YES) {
						return;
					}
				}
			} catch (Exception e) {
				int choice = MessageDialog.showYesNoDlg(this, ResHelper
						.getString("60130715", "UPP60130715-000054")// @res "��ʾ"
						, ResHelper.getString("60130715", "UPP60130715-000089")// @res
						// "�ж�Ҫ�������ļ��Ƿ��Ѵ���ʱ��������ִ�н��滻ԭ���ļ���Ҫ������?"
				);
				if (choice != MessageDialog.ID_YES) {
					return;
				}
			}
			// �õ�����
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
						&& !colname.equalsIgnoreCase("pk_changecause")/** &&!colname.equalsIgnoreCase("���±�־") */
				) {
					l_straryColNames[i] = colname;
				}
			}
			l_straryColNames[14] = ResHelper.getString("common",
			"UC000-0004069")// @res "��������"
			;
			l_straryColNames[15] = ResHelper.getString("common",
			"UC000-0000147")// @res "��Ա����"
			;
			l_straryColNames[16] = ResHelper.getString("common",
			"UC000-0000134")// @res "��Ա����"
			;
			l_straryColNames[17] = ResHelper.getString("common",
			"UC000-0000787")// @res "��λ����"
			;
			l_straryColNames[18] = ResHelper.getString("common",
			"UC000-0000783")// @res "��λ����"
			;
			// ��֯�������
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
				// �������
				DataExpImp fileout = new DataExpImp(outfilename,
						l_straryColNames, l_outdata);
				// ���
				int result = fileout.exportChkAttdDataExcelFile();
				if (result == 0) {
					MessageDialog.showHintDlg(this, ResHelper.getString(
							"60130715", "UPP60130715-000054")// @res "��ʾ"
							, ResHelper.getString("60130715",
							"UPP60130715-000106")// @res "�ļ������ɹ�"
					);
				}
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			showErrorMessage(ResHelper.getString("60130715",
			"UPP60130715-000090")// @res
			// "�ļ�����ʧ�ܣ����鵼���ļ�·���������Ƿ���ȷ�������ļ��Ѵ��ڲ��Ҵ��ڴ�״̬��"
			);
		}
	}

	/**
	 * ���������������ָ���ļ��� add by ddw at 2003-11-10
	 */
	public String getOutFileName() {
		String outfilename = gettfFileName().getText();

		if (outfilename == null || outfilename.trim().length() < 1) {
			showErrorMessage(ResHelper.getString("60130715",
			"UPP60130715-000091")// @res "����·�����ļ�������Ϊ��!"
			);

		}
		return outfilename;
	}

	/**
	 * ���������ݵ��뵽ָ���ļ��� add by ddw at 2003-11-10
	 */
	public String getOutFileNameim() {
		String imfilename = gettfFileNameim().getText();

		if (imfilename == null || imfilename.trim().length() < 1) {
			showErrorMessage(ResHelper.getString("60130715",
			"UPP60130715-000092")// @res "����·�����ļ�������Ϊ��!"
			);

		}
		return imfilename;
	}

	/**
	 * �˴����뷽��˵���� add by ddw at 2003-11-11
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
	 * ���ӣ���һ����¼�������ű�־���������±�־��ȫ��ѡ�У����ӵڶ�����¼�����һ����¼�ġ����±�־��ѡ��ȥ��
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
	 * ���룺ѡ�з����µļ�¼���룬�����±�־��recordnumΪѡ�м�¼��ֵ-1��ѡ�����¼�¼���룬�����±�־��recordnumΪ���ֵ��1��
	 * 
	 */
	public void onInsert() {
		setTopPanelProperty(false);
		getPsnWadocSubPane().onInsert();
	}

	/**
	 * ȡ����ȡ����ǰ���������ݡ�
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
				 * @res "ȷʵҪɾ����"
				 */) == UIDialog.ID_YES) {
			getPsnWadocSubPane().onDelte();

			setBtnsStatWithUIStat(-1);
			onRefresh();

		}

	}

	/**
	 * 
	 * ���棺��������/�޸ĵ����ݡ�
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
	 * �޸ģ��޸�ֻ���޸����¼�¼�ġ����ű�־������ʷ�ļ�¼�����ű�־�������޸�
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
	 * ���ݽ���״̬�趨��ť��״̬�� ��ͬ��ĳ�ֽ���״̬�£������Ʋ�ͬ�����ڵ���Ҫ�в�ͬ�İ�ť״̬ʱ��
	 * ���Կ�����setBtnStatWithBuss�ķ����ٲ���һ�� �������ڣ�(2003-3-27 20:09:48)
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
	 * ���ݵ�ǰ����״̬�趨����ռ�ı༭״̬�� �������ڣ�(2003-3-27 20:08:38)
	 * 
	 * @param stat
	 *            int
	 */
	@Override
	public void setPanelStat(int stat) {
	}

	/**
	 * �༭���¼��� �������ڣ�(2001-3-23 2:02:27)
	 * 
	 * @param e
	 *            ufbill.BillEditEvent
	 */
	public void afterEdit(nc.ui.pub.bill.BillEditEvent e) {
		e.getKey();
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-6-9 13:53:26)
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
	 * �иı��¼��� �������ڣ�(2001-3-23 2:02:27)
	 * 
	 * @param e
	 *            ufbill.BillEditEvent
	 */
	public void bodyRowChange(nc.ui.pub.bill.BillEditEvent e) {

		int rowCount = getPsnWadocMainPane().getBillScrollPane().getTable()
		.getRowCount();
		if (rowCount <= 0) { // ������С�ڵ���һ�������ƶ�
		} else if (rowCount > 0) { // ������������
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
	 * �˴����뷽�������� �������ڣ�(2004-5-13 19:52:04)
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
	 * �������е��������Ӧ������ ������ ����������� ���أ� �������Ӧ�����ݣ�ֻ��Ϊ String[]�� ��� itemName ӵ���������
	 * 1 ���������ӡϵͳ����������������ݵ�˳�����ж� String[] �еĴ�ŵ����� 2 ���������ӡϵͳ�������������������������������
	 * 
	 * ģ�� 2 �����: [��Ŀ] ==> [100 200 300 --> 400 500 600]
	 * 
	 * ģ�� 3 �����: ��� getDependItemNamesByName("���") ==
	 * 
	 * [��Ŀ ����] ==> [100 200 300 400 500 600] ���к��� [���� ��Ŀ] ==> [100 400 200 500
	 * 300 600] ���к���
	 * 
	 */

	/**
	 * �˴����뷽�������� �������ڣ�(2004-6-21 16:32:35)
	 * 
	 * @return int
	 */
	public int getNodeFlag() {
		return UI_MAIN;
	}

	/**
	 * ���� PsnWadocMainPane1 ����ֵ��
	 * 
	 * @return nc.ui.hi.hi_307.PsnWadocMainPane
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ���� PsnWadocSubPane1 ����ֵ��
	 * 
	 * @return nc.ui.hi.hi_307.PsnWadocSubPane
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ��ò�ѯ�Ի���
	 * 
	 * �������ڣ�(2004-6-5 16:38:40)
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
							"bd_psndoc.psnname")) {// 5.5���ڣ�
								// ����ReturnType���ԡ���������ݿ��ˡ�
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
			 * @res "��ѯģ���ʼ������"
			 */);
		}
		return queryDlg;
	}

	/**
	 * ��ʼ����� �������ڣ�(2004-6-5 11:49:40)
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
			 * @res "����ʼ��ʧ�ܣ�"
			 */);
		}
	}

	/**
	 * ��ӡ�� �������ڣ�(2004-6-5 14:10:14)
	 */
	public void onPrintDirect() {
		try {
			getPsnWadocSubPane().onPrintDetailDirect();
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * ��ӡ�� �������ڣ�(2004-6-5 14:10:14)
	 */
	public void onPrintBytemplate() {
		try {
			getPsnWadocSubPane().onPrintBytemplate();
		} catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}

	/**
	 * ������ӡ
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
	 * ��Ƭ��ʽ������ӡ
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
			ParamDialog pd = new ParamDialog(this, "��ӡ��������", paramMap);
			
			if(pd.showModal() == UIDialog.ID_OK ){
				IDataSource dataSource = null;
				PrintEntry print = new PrintEntry(this, null);
				for (PsndocWadocMainVO psndocWadocMainVO : mainVOs) {
					setSubVOs(psndocWadocMainVO);
					dataSource = new MyListPRTS(new PsndocWadocMainVO[]{psndocWadocMainVO}, paramMap, type);
					print.setDataSource(dataSource);
				}
//				 �趨����ģ��
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
//			ParamDialog pd = new ParamDialog(this, "��ӡ��������", paramMap);
//			pd.showModal();
//			IDataSource dataSource = null;
//			dataSource = new MyListPRTS(mainVOs, paramMap, type);
//			PrintEntry print = new PrintEntry(this, dataSource);
//			// �趨����ģ��
//			print.setTemplateID(Global.getCorpPK(), this.getModuleCode(), Global.getUserID(), null);
//			print.getTemplate().setPagination(4);
//			if (print.selectTemplate() >= 0) {
//				print.preview();
//			}
		} else {
			showErrorMessage("���Ȳ�ѯ��ӡ����");
		}
	}
	
	/**
	 * ��λ�䶯��ӡ
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
	 * ��¼�ô�ӡ
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
	 * ������ӡ
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
	 * ��ѯ�� �������ڣ�(2004-6-5 14:09:58)
	 */
	public void onQuery() {
		try {
			getQueryDlg().showModal();
			if (getQueryDlg().getResult() == UIDialog.ID_OK) {
				// update by sunxj 2010-02-23 ���ٲ�ѯ��� start
				isQuery = true;
				isQuickSearch = false;
				// update by sunxj 2010-02-23 ���ٲ�ѯ��� end
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
			 * @res "��ѯ����!"
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
	 * ˢ�¡� �������ڣ�(2004-6-5 14:10:06)
	 */
	public void onRefresh() {
		try {

			getPsnWadocSubPane().getBillScrollPane().getTable()
			.clearSelection();

			getPsnWadocMainPane().setWadocData(null);
			getPsnWadocSubPane().setWadocData(null);
			getPsnWadocMainPane().getBillScrollPane().removeEditListener();
			initTable();
			// delete by sunxj 2010-02-23 ���ٲ�ѯ��� start
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
			// delete by sunxj 2010-02-23 ���ٲ�ѯ��� end
			// add by sunxj 2010-02-23 ���ٲ�ѯ��� start
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
			// add by sunxj 2010-02-23 ���ٲ�ѯ��� end
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʾѡ����Ա������н�ʱ䶯����� �������ڣ�(2004-6-9 19:31:55)
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
					 * "��ʾ��Աн�ʱ䶯�����ϸ��Ϣ����"
					 */);
		}
	}

	public JComponent createFieldValueEditor(FilterMeta filterMeta) {
		// TODO Auto-generated method stub
		if (filterMeta.getFieldCode().equalsIgnoreCase("bd_psndoc.pk_psndoc")) {
			UIRefPane psndocref = new UIRefPane();
			psndocref.setRefNodeName("��Ա����");
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
			 * @res "��λ����"
			 */);
			UIRefPane jobref = new UIRefPane();
			jobref.setRefType(1);
			jobref.setRefInputType(1);
			jobref.setRefModel(jobmodel);
			return jobref;
		}

		if (filterMeta.getFieldCode().equalsIgnoreCase("bd_psndoc.pk_psncl")) {
			// ������Ա�����ʾ�ѷ������
			UIRefPane psnclsref = new UIRefPane();
			psnclsref.setRefType(1);
			psnclsref.setRefInputType(1);
			psnclsref.setRefNodeName("��Ա���");
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
			// �ж��Ƿ����ִ�в�ѯ
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
			// ����δ�ɹ���������
			if (reList.size() > 0) {
				return (reList.get(0)).toString();
			}
		} catch (Exception e) {
			reportException(e);
			showErrorMessage(NCLangRes.getInstance().getStrByID("60130715",
			"UPP60130715-000007")/*
			 * @res "��ѯ����!"
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
	 * ά����¼ ȷ��
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
//	ά����¼ ��״̬ 0��Ĭ��״̬��1������н����Ŀ��2��ĳһ��н����Ŀ
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
						"UPP60130715-000066")// @res "Excel�ļ�(*.xls)"
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
						"UPP60130715-000066")// @res "Excel�ļ�(*.xls)"
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
	 * н���յ���ѯ<BR>
	 * н���յ�����н����Ϣά���Ĳ�ѯ�����Ͻ��в������������������Ϣά���Ĳ�ѯ�����ٴβ�ѯ���൱��ˢ�²���<BR>
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
	 * н���յ�
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
	 * ���ö���Panel��ֻ������
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
			showWarningMessage("���ȱ�������");
			return;
		}

		int rows = getPsnWadocMainPane().getBillScrollPane().getTable().getRowCount();
		if(rows == 0){
			showWarningMessage("δѡ����Ա��Ϣ");
			return;

		}
		if(currentSelectRow < 0){
			showWarningMessage("δѡ����Ա��Ϣ");
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
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		if(currentSelectRow < 0){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		PsndocWadocMainVO vo = getPsnWadocMainPane().getBodySelectedVO(
				currentSelectRow);
		if(vo == null){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		if(vo.getPk_psndoc() == null){
			showWarningMessage("��ѡ��һ����Ա");
			return;
		}
		PsnViewDialog dlg = getPsnViewDlg();
		dlg.initData(vo.getPk_psndoc());
		dlg.showModal();
	}

	
}