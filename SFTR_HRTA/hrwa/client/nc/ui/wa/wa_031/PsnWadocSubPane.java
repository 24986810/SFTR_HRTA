package nc.ui.wa.wa_031;

import java.util.HashMap;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IPrmlv;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.bd.def.IDefdoc;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.ui.hi.pub.PsnInfFldRefPane;
import nc.ui.hr.base.HRToftPanel;
import nc.ui.hr.global.Global;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.ui.pub.bill.itemconverters.UFDoubleConverter;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintDirectEntry;
import nc.ui.pub.print.PrintEntry;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.wa.pub.CommonDataSourceForWA;
import nc.ui.wa.pub.PrintManagerForWA;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wabm.print.TableColResize;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.bd.def.DefdocVO;
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.wa_026.GradeVO;
import nc.vo.wa.wa_026.PrmlvVO;
import nc.vo.wa.wa_027.CriterionVO;
import nc.vo.wa.wa_031.PsnappaproveBVO;

/**
 * �˴��������������� �������ڣ�(2004-6-4 11:35:45)
 * 
 * @author��Administrator
 */
public class PsnWadocSubPane extends nc.ui.pub.beans.UIPanel implements
BillEditListener, ValueChangedListener, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1691612280204595966L;
	private nc.ui.pub.bill.BillScrollPane ivjBillPane = null;
	private nc.vo.hi.wadoc.PsndocWadocVO[] subVOs = null;
	private java.lang.String psnname = "";
	private java.lang.String deptname = "";
	private java.lang.String psnCode = "";
	private java.lang.String deptCode = "";
	public static final String ADD_STATE = "ADD_STATE";
	public static final String INSERT_STATE = "INSERT_STATE";
	public static final String MODIFY_STATE = "MODIFY_STATE";
	public static final String SAVE_SATE = "SAVE_SATE";
	public static final String UNKNOWN_STATE = "UNKNOWN_STATE";
	public static final String DELETE_STATE = "DELETE_STATE";
	private String state = UNKNOWN_STATE;

	private PsnWadocUI fatherUI = null;
	protected int last_select_row_number = 0;//zhf modify
	private String pk_psndoc = null;
	boolean negotiation_wage = false;
	private final HashMap<String, Boolean> negotiation_wageADD_STATE = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> negotiation_wageINSERT_STATE = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> negotiation_wageMODIFY_STATE = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> wageADD_STATE = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> wageINSERT_STATE = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> wageMODIFY_STATE = new HashMap<String, Boolean>();

	private PsndocWadocMainVO mainVO;

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 */
	public PsnWadocSubPane() {
		super();
		initialize();
	}

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 * 
	 * @param p0
	 *            java.awt.LayoutManager
	 */
	public PsnWadocSubPane(PsnWadocUI fatherUI) {
		super();
		this.fatherUI = fatherUI;
		initialize();
	}

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 * 
	 * @param p0
	 *            java.awt.LayoutManager
	 * @param p1
	 *            boolean
	 */
	public PsnWadocSubPane(java.awt.LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 * 
	 * @param p0
	 *            boolean
	 */
	public PsnWadocSubPane(boolean p0) {
		super(p0);
	}

	/**
	 * 
	 * �õ����е���������ʽ���� Ҳ���Ƿ������ж����������ı��ʽ
	 * 
	 */

	/**
	 * ���� BillPane ����ֵ��
	 * 
	 * @return nc.ui.pub.bill.BillScrollPane
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.bill.BillScrollPane getBillPane() {
		if (ivjBillPane == null) {
			try {
				ivjBillPane = new nc.ui.pub.bill.BillScrollPane();
				ivjBillPane.setName("BillPane");
				ivjBillPane.addEditListener(this);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjBillPane;
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-6-9 14:49:05)
	 */
	public nc.ui.pub.bill.BillScrollPane getBillScrollPane() {
		return getBillPane();
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-8-7 15:03:19)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDeptname() {
		return deptname;
	}

	/*
	 * ���ظ�����Դ��Ӧ�Ľڵ����
	 */
	public java.lang.String getModuleName() {
		return "60130715";
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-8-7 15:02:49)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPsnname() {
		return psnname;
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-6-9 16:29:41)
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocMainVO[]
	 */
	public nc.vo.hi.wadoc.PsndocWadocVO[] getWadocData() {
		return subVOs;
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
			// user code end
			setName("PsnWadocMainPane");
			setLayout(new java.awt.BorderLayout());
			setSize(424, 202);
			add(getBillPane(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		initTable();
		initSate();
		// user code end
	}

	/**
	 * ��ʼ������ �������ڣ�(2004-6-4 19:37:09)
	 * 
	 * @param item
	 *            java.lang.String[][]
	 */
	public void initTable() {
		String[] itemname = {
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000003"),// н����Ŀ
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000007"),// ���ʵȼ����
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000001"),// н����ʼ����
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000002"),// н�ʽ�ֹ����
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000009"),// н�ʵ�������
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704",
				"UPP60130704-000320"),// "̸�й���",
				// //
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000004"),// н�ʼ���
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000005"),// н�ʵ���
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704",
				"UPP60130704-000039"),// "������",
				// //
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000006"),// ���
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000008"),// ���ű�־
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000012"),// ���±�־
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000010"),// �䶯ԭ��
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000011"),// �����ļ�
				"pk_wa_item",//
				"iflddecimal",
				"recordnum",//
				"pk_psndoc_sub",//
				"workflowflag",//
				"pk_wa_pralv",//
				"pk_wa_seclv",//
				"pk_wa_grd",
				"pk_changecause",
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000200"),// "��������",
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000201"),// "��Ա����",
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000202") // "��Ա����"
		};
		String[] itemcode = { "vname", // 1
				"wagradename", // 2
				"begindate", // 3
				"enddate", // 4
				"changedate", // 5
				"negotiation_wage", // 6
				"wa_prmlv_levelname", // 7
				"wa_seclv_levelname", // 8
				"criterionvalue", // 9
				"nmoney", // 10
				"waflag", // 11
				"lastflag", // 12
				"docname", // 13
				"vbasefile", // 14
				"pk_wa_item", // 15
				"iflddecimal", "recordnum", // 16
				"pk_psndoc_sub", // 17
				"workflowflag", // 18
				"pk_wa_pralv", // 19
				"pk_wa_seclv", // 20
				"pk_wa_grd",// 21
				"pk_changecause",// 22
				"deptName",// ��������23
				"psnCode",// ��Ա����24
				"psnName"// ��Ա����25
		};

		int[] itemtype = { BillItem.UFREF, // 1
				BillItem.UFREF, // 2
				BillItem.DATE, // 3
				BillItem.DATE, // 4
				BillItem.DATE, // 5
				BillItem.BOOLEAN, // 6
				BillItem.UFREF, // 7
				BillItem.UFREF, // 8
				BillItem.DECIMAL, // 9
				BillItem.DECIMAL, // 0
				BillItem.BOOLEAN, // 1
				BillItem.BOOLEAN, // 2
				BillItem.UFREF, // 3
				BillItem.STRING, // 4
				BillItem.STRING, // 5
				BillItem.INTEGER, // 6
				BillItem.INTEGER, BillItem.STRING, //
				BillItem.BOOLEAN, //
				BillItem.STRING, //
				BillItem.STRING, //
				BillItem.STRING, BillItem.STRING, BillItem.STRING, //
				BillItem.STRING, BillItem.STRING };

		BillItem[] billItems = new BillItem[itemname.length];
		for (int i = 0; i < itemname.length; i++) {
			billItems[i] = new BillItem();
			billItems[i].setName(itemname[i]);
			billItems[i].setKey(itemcode[i]);
			billItems[i].setDataType(itemtype[i]);
			billItems[i].setWidth(70);
			if (i == 0) {
				billItems[0].setRefType("<nc.ui.wa.wa_031.WaItemRefModel>");
			} else if (i == 12) {
				PsnInfFldRefPane psnRef = new PsnInfFldRefPane(
				"0001AA10000000003Z5S");
				billItems[12].setComponent(psnRef);
			}

			if (i > 13) {
				billItems[i].setShow(false);
			} else {
				billItems[i].setShow(true);
			}

			billItems[i].setEdit(true);
			billItems[i].setEnabled(true);

			if (itemcode[i].equals("vname")
					|| itemcode[i].equals("wagradename")
					|| itemcode[i].equals("begindate")
					|| itemcode[i].equals("nmoney")) {

				billItems[i].setNull(true);

			} else {
				billItems[i].setNull(false);

			}
			if (itemtype[i] == BillItem.BOOLEAN) {// ���ű�־�޸�ʱ�����������������޸�
				((UICheckBox) billItems[i].getComponent())
				.setHorizontalAlignment(UICheckBox.CENTER);
			}

			if (itemcode[i].equals("criterionvalue")
					|| itemcode[i].equals("nmoney")) {
				IBillModelDecimalListener2 bmd = new CellDecimalListener(
				"iflddecimal");
				billItems[i].addDecimalListener(bmd);
			}
			if (itemcode[i].equals("nmoney")) {
				billItems[i].setLength(12);
			}

			if (itemcode[i].equals("vname")
					|| itemcode[i].equals("wagradename")
					|| itemcode[i].equals("wa_prmlv_levelname")
					|| itemcode[i].equals("wa_seclv_levelname")) {
				billItems[i].setLength(512);
			}
			if (itemcode[i].equals("vbasefile")) {
				billItems[i].setLength(100);
			}
		}
		((UIRefPane) billItems[0].getComponent()).addValueChangedListener(this);
		((UIRefPane) billItems[2].getComponent()).addValueChangedListener(this);

		BillModel billModel = new BillModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				if (getBillPane().isLockCol()) {
					col = col - getBillPane().getLockCol();
				}
				return isEditable(row, col);
			}

		};

		//
		billItems[22].setShow(false);
		billItems[23].setShow(false);
		billItems[24].setShow(false);
		//

		billModel.setBodyItems(billItems);

		getBillPane().setTableModel(billModel);
		getBillPane().getTable().setSelectionMode(
				javax.swing.ListSelectionModel.SINGLE_SELECTION);
		getBillPane().getTable().getSelectionModel().addListSelectionListener(
				this);
		getBillPane().getTable().setSortEnabled(false);

		getBillPane().getTable().setColumnSelectionAllowed(false);
		// getBillPane().getTable().setCellSelectionEnabled(false);
		getBillPane().getTable().setRowSelectionAllowed(true);
		getBillPane().setRowNOShow(true);
	}

	public boolean isEditable(int row, int col) {
		boolean isEditable = false;
		// ����������У� ����ѡ����������һ�У� �����пɱ༭
		String keyName = getBillPane().getBodyKeyByCol(col);

		// String keyName = getBillPane().getBodyKeyByCol(col);
		String WAFLAG = "waflag";
		Boolean workflowFlag = (Boolean) getBillPane().getTableModel()
		.getValueAt(row, PsndocWadocVO.WORKFLOWFLAG);
		if (workflowFlag == null) {
			workflowFlag = false;
		}
		if (row == last_select_row_number) {
			if (getState().equals(MODIFY_STATE) && workflowFlag) {
				// ֻ���޸����¼�¼�ķ��ű�־
				int index = getBillPane().getTableModel().getBodyColByKey(
						WAFLAG);
				nc.bs.logging.Logger.error("index=" + index + "col=" + col);
				if (col == index) {
					isEditable = true;
				}
			}

			else if (!getState().equals(UNKNOWN_STATE)) {
				Boolean boolean1 = getStateHash(getState(), negotiation_wage)
				.get(keyName);
				isEditable = boolean1 == null ? false : boolean1.booleanValue();
			}
		}
		int index = getBillPane().getTableModel().getBodyColByKey(
				WAFLAG);
		nc.bs.logging.Logger.error("index=" + index + "col=" + col);
		if (getState().equals(MODIFY_STATE)&&col == index) {
			isEditable = true;
		}
		nc.bs.logging.Logger.error("index=" + keyName + "col=" + col);
		return isEditable;
	}

	private void initSate() {
		negotiation_wageADD_STATE.put("vname", new Boolean(true));//
		negotiation_wageADD_STATE.put("wagradename", new Boolean(true));//
		negotiation_wageADD_STATE.put("begindate", new Boolean(true));//
		negotiation_wageADD_STATE.put("enddate", new Boolean(true));//
		negotiation_wageADD_STATE.put("changedate", new Boolean(true));//
		negotiation_wageADD_STATE.put("negotiation_wage", new Boolean(true));//
		negotiation_wageADD_STATE.put("wa_prmlv_levelname", new Boolean(false));//
		negotiation_wageADD_STATE.put("wa_seclv_levelname", new Boolean(false));//
		negotiation_wageADD_STATE.put("criterionvalue", new Boolean(false));//
		negotiation_wageADD_STATE.put("nmoney", new Boolean(true));//
		negotiation_wageADD_STATE.put("waflag", new Boolean(true));//
		negotiation_wageADD_STATE.put("lastflag", new Boolean(false));//
		negotiation_wageADD_STATE.put("docname", new Boolean(true));//
		negotiation_wageADD_STATE.put("vbasefile", new Boolean(true));//

		negotiation_wageINSERT_STATE.put("vname", new Boolean(false));//
		negotiation_wageINSERT_STATE.put("wagradename", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("begindate", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("enddate", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("changedate", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("negotiation_wage", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("wa_prmlv_levelname", new Boolean(
				false));//
		negotiation_wageINSERT_STATE.put("wa_seclv_levelname", new Boolean(
				false));//
		negotiation_wageINSERT_STATE.put("criterionvalue", new Boolean(false));//
		negotiation_wageINSERT_STATE.put("nmoney", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("waflag", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("lastflag", new Boolean(false));//
		negotiation_wageINSERT_STATE.put("docname", new Boolean(true));//
		negotiation_wageINSERT_STATE.put("vbasefile", new Boolean(true));//

		negotiation_wageMODIFY_STATE.put("vname", new Boolean(false));//
		negotiation_wageMODIFY_STATE.put("wagradename", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("begindate", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("enddate", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("changedate", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("negotiation_wage", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("wa_prmlv_levelname", new Boolean(
				false));//
		negotiation_wageMODIFY_STATE.put("wa_seclv_levelname", new Boolean(
				false));//
		negotiation_wageMODIFY_STATE.put("criterionvalue", new Boolean(false));//
		negotiation_wageMODIFY_STATE.put("nmoney", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("waflag", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("lastflag", new Boolean(false));//
		negotiation_wageMODIFY_STATE.put("docname", new Boolean(true));//
		negotiation_wageMODIFY_STATE.put("vbasefile", new Boolean(true));//

		wageADD_STATE.put("vname", new Boolean(true));//
		wageADD_STATE.put("wagradename", new Boolean(true));//
		wageADD_STATE.put("begindate", new Boolean(true));//
		wageADD_STATE.put("enddate", new Boolean(true));//
		wageADD_STATE.put("changedate", new Boolean(true));//
		wageADD_STATE.put("negotiation_wage", new Boolean(true));//
		wageADD_STATE.put("wa_prmlv_levelname", new Boolean(false));//
		wageADD_STATE.put("wa_seclv_levelname", new Boolean(false));//
		wageADD_STATE.put("criterionvalue", new Boolean(true));//
		wageADD_STATE.put("nmoney", new Boolean(true));//
		wageADD_STATE.put("waflag", new Boolean(true));//
		wageADD_STATE.put("lastflag", new Boolean(false));//
		wageADD_STATE.put("docname", new Boolean(true));//
		wageADD_STATE.put("vbasefile", new Boolean(true));//

		wageINSERT_STATE.put("vname", new Boolean(false));//
		wageINSERT_STATE.put("wagradename", new Boolean(true));//
		wageINSERT_STATE.put("begindate", new Boolean(true));//
		wageINSERT_STATE.put("enddate", new Boolean(true));//
		wageINSERT_STATE.put("changedate", new Boolean(true));//
		wageINSERT_STATE.put("negotiation_wage", new Boolean(true));//
		wageINSERT_STATE.put("wa_prmlv_levelname", new Boolean(false));//
		wageINSERT_STATE.put("wa_seclv_levelname", new Boolean(false));//
		wageINSERT_STATE.put("criterionvalue", new Boolean(true));//
		wageINSERT_STATE.put("nmoney", new Boolean(true));//
		wageINSERT_STATE.put("waflag", new Boolean(true));//
		wageINSERT_STATE.put("lastflag", new Boolean(false));//
		wageINSERT_STATE.put("docname", new Boolean(true));//
		wageINSERT_STATE.put("vbasefile", new Boolean(true));//

		wageMODIFY_STATE.put("vname", new Boolean(false));//
		wageMODIFY_STATE.put("wagradename", new Boolean(true));//
		wageMODIFY_STATE.put("begindate", new Boolean(true));//
		wageMODIFY_STATE.put("enddate", new Boolean(true));//
		wageMODIFY_STATE.put("changedate", new Boolean(true));//
		wageMODIFY_STATE.put("negotiation_wage", new Boolean(true));//
		wageMODIFY_STATE.put("wa_prmlv_levelname", new Boolean(false));//
		wageMODIFY_STATE.put("wa_seclv_levelname", new Boolean(false));//
		wageMODIFY_STATE.put("criterionvalue", new Boolean(true));//
		wageMODIFY_STATE.put("nmoney", new Boolean(true));//
		wageMODIFY_STATE.put("waflag", new Boolean(true));//
		wageMODIFY_STATE.put("lastflag", new Boolean(false));//
		wageMODIFY_STATE.put("docname", new Boolean(true));//
		wageMODIFY_STATE.put("vbasefile", new Boolean(true));//
	}

	private HashMap<String, Boolean> getStateHash(String state,
			boolean negotiation_wage) {

		if (negotiation_wage) {
			if (state.equals(ADD_STATE)) {
				return negotiation_wageADD_STATE;

			} else if (state.equals(INSERT_STATE)) {
				return negotiation_wageINSERT_STATE;

			} else if (state.equals(MODIFY_STATE)) {
				return negotiation_wageMODIFY_STATE;
			}
		} else {
			if (state.equals(ADD_STATE)) {
				return wageADD_STATE;

			} else if (state.equals(INSERT_STATE)) {
				return wageINSERT_STATE;

			} else if (state.equals(MODIFY_STATE)) {
				return wageMODIFY_STATE;
			}
		}
		return null;
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-6-15 16:47:48)
	 */
	public void onPrintBytemplate() throws Exception {
		if (subVOs == null || subVOs.length < 1) {
			throw new Exception(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60130715", "UPP60130715-000001")/*
					 * @res "û�пɴ�ӡ�����ݣ�"
					 */);
		}
		IDataSource dataSource = null;
		dataSource = new CommonDataSourceForWA((BillModel) getBillPane()
				.getTable().getModel());
		PrintEntry print = new PrintEntry(fatherUI, dataSource);

		// �趨����ģ��
		print.setTemplateID(Global.getCorpPK(), fatherUI.getModuleCode(),
				Global.getUserID(), null);

		if (print.selectTemplate() >= 0) {
			print.preview();
		}
	}

	public void onPrintDetailDirect() throws Exception {
		if (subVOs == null || subVOs.length < 1) {
			throw new Exception(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60130715", "UPP60130715-000001")/*
					 * @res "û�пɴ�ӡ�����ݣ�"
					 */);
		}

		PrintDirectEntry print = PrintManagerForWA.getDirectPrinter(
				getBillPane().getTable(), getBillPane().getTableModel()
				.getBodyItems());

		String topstr[][] = new String[1][2];
		topstr[0][0] = nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704",
		"UPP60130704-000324")/*
		 * @res "��Ա���룺"
		 */
		+ this.getPsnCode();
		topstr[0][1] = nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704",
		"UPP60130704-000325")/*
		 * @res "��Ա����:"
		 */
		+ this.getPsnname();
		int colnum = getBillPane().getTable().getColumnCount() - 1;
		print.setTopStr(topstr);
		print.setTopStrAlign(new int[] { 0, 0, 2 });
		print.setTopStrColRange(new int[] { 1, 3, colnum });

		String title = nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
		"UPP60130715-000002");

		java.awt.Font font = new java.awt.Font("dialog", java.awt.Font.BOLD, 30);

		print.setTitle(title);
		print.setTitleFont(font);

		print.preview();
	}
	
	public void onTempAdd(){

		getBillPane().getTableModel().addLine();
		last_select_row_number = getBillPane().getTableModel().getRowCount() - 1;
		// ���ż�¼�� ���±�־ȫ��ѡ��

		getBillPane().getTableModel().setValueAt(new UFBoolean(true),
				last_select_row_number, "waflag");
		getBillPane().getTableModel().setValueAt(new UFBoolean(true),
				last_select_row_number, "lastflag");
		getBillPane().getTableModel().setValueAt("0", last_select_row_number,
		"recordnum");
		getBillPane().getTableModel().setValueAt(0, last_select_row_number,
		"workflowFlag");

		getBillPane().getTableModel().setValueAt(getDeptname(),
				last_select_row_number, "");
		getBillPane().getTableModel().setValueAt(getPsnname(),
				last_select_row_number, "");
		// getBillPane().getTableModel().setValueAt(, last_select_row_number,
		// "");
		UFDate date = ClientEnvironment.getInstance().getDate();
		try {
			String month = WADelegator.getWaPeriod().getYWPeriod(IHRPWABtn.PK_GONG, false);
			date = month!=null?new UFDate(month.substring(0,4)+"-"+month.substring(4,6)+"-01"):date;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getBillPane().getTableModel().setValueAt(date,last_select_row_number,"changedate");
		getBillPane().getTableModel().setValueAt(new UFBoolean(true),last_select_row_number,"negotiation_wage");
		setState(ADD_STATE);

		getBillPane().getTable().setRowSelectionInterval(
				last_select_row_number, last_select_row_number);
	
	}

	public void onAdd() {
		getBillPane().getTableModel().addLine();
		last_select_row_number = getBillPane().getTableModel().getRowCount() - 1;
		// ���ż�¼�� ���±�־ȫ��ѡ��

		getBillPane().getTableModel().setValueAt(new UFBoolean(true),
				last_select_row_number, "waflag");
		getBillPane().getTableModel().setValueAt(new UFBoolean(true),
				last_select_row_number, "lastflag");
		getBillPane().getTableModel().setValueAt("0", last_select_row_number,
		"recordnum");
		getBillPane().getTableModel().setValueAt(0, last_select_row_number,
		"workflowFlag");

		getBillPane().getTableModel().setValueAt(getDeptname(),
				last_select_row_number, "");
		getBillPane().getTableModel().setValueAt(getPsnname(),
				last_select_row_number, "");
		// getBillPane().getTableModel().setValueAt(, last_select_row_number,
		// "");
		UFDate date = ClientEnvironment.getInstance().getDate();
		try {
			String month = WADelegator.getWaPeriod().getYWPeriod(IHRPWABtn.PK_GONG, false);
			date = month!=null?new UFDate(month.substring(0,4)+"-"+month.substring(4,6)+"-01"):date;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getBillPane().getTableModel().setValueAt(date,last_select_row_number,"changedate");
		getBillPane().getTableModel().setValueAt(new UFBoolean(true),last_select_row_number,"negotiation_wage");
		setState(ADD_STATE);

		getBillPane().getTable().setRowSelectionInterval(
				last_select_row_number, last_select_row_number);
	}

	public void onInsert() {
		int selectRow = getBillPane().getTable().getSelectedRow();
		String name = getBillPane().getTableModel().getValueAt(selectRow,
		"vname").toString();
		String pk_wa_item = getBillPane().getTableModel().getValueAt(selectRow,
		"pk_wa_item").toString();
		String recordnum = getBillPane().getTableModel().getValueAt(selectRow,
		"recordnum").toString();

		// getBillPane().getTableModel().getbody
		getBillPane().getTableModel().insertRow(selectRow);
		getBillPane().getTableModel().setValueAt(name, selectRow, "vname");
		getBillPane().getTableModel().setValueAt(pk_wa_item, selectRow,
		"pk_wa_item");
		int num = Integer.parseInt(recordnum) + 1;
		getBillPane().getTableModel().setValueAt(num, selectRow, "recordnum");
		getBillPane().getTableModel().setValueAt(new UFBoolean(true),
				selectRow, "waflag");
		getBillPane().getTableModel().setValueAt(0, selectRow, "workflowFlag");
		getBillPane().getTableModel().setValueAt(new UFBoolean(false),
				selectRow, "lastflag");

		((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
		                                                          .getComponent()).setRefModel(new GradeRefModel(pk_wa_item));
		((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
		                                                          .getComponent()).setText(null);
		((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
		                                                          .getComponent()).setReturnCode(true);
		((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
		                                                          .getComponent()).setRefInputType(0);

		// getBillPane().getTable().getCellEditor()

		last_select_row_number = selectRow;

		setState(INSERT_STATE);
		getBillPane().getTable().setRowSelectionInterval(
				last_select_row_number, last_select_row_number);
	}

	public boolean onSave() throws Exception {

		PsndocWadocVO selectVO = getSelectVO();
		validate(selectVO);

		if (WADelegator.getWaPsndoc().existEarlyDate(selectVO)) {
			String msg = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60130715", "UPP60130715-000108")/*
					 * @res
					 * "������н��¼����Ч�������ڵ�ǰ���¼�¼����Ч���ڣ��Ƿ񱣴�?"
					 */;
			if (MessageDialog.showYesNoDlg(this, null, msg) == UIDialog.ID_NO) {
				return false;
			}
		}

		selectVO.setIadjustmatter(new Integer(1));
		if (getState().equals(ADD_STATE) || getState().equals(INSERT_STATE)) {
			saveInsertSelectVO(selectVO);
		} else if (getState().endsWith(MODIFY_STATE)) {
			if (selectVO.getWorkflowflag() == null) {
				selectVO.setWorkflowflag(new UFBoolean(false));
			}
			saveModifySelectVO(selectVO);
		}
		String psnid = selectVO.getPk_psndoc();
		String iitemid = selectVO.getPk_wa_item();
		String wa_prmlv_levelname = selectVO.getPk_wa_pralv();
		UFBoolean  lastflag = selectVO.getLastflag();
		UFBoolean  waflag = selectVO.getWaflag();
		if(iitemid.equals("0001691000000000BYWP")&&lastflag!=null&&lastflag.booleanValue()){
			IPrmlv prmlv = NCLocator.getInstance().lookup(IPrmlv.class);
			PrmlvVO pvo = prmlv.findPrmlvByPk(wa_prmlv_levelname);
			String docname = pvo.getLevelname();
			if(pvo.getLevelname().startsWith("ר��δƸ")||pvo.getLevelname().startsWith("����δƸ")){
				docname = "ר��-��ϰ��";
			}else if(pvo.getLevelname().startsWith("����δƸ")){
				docname = "����-��ϰ��";
			}else if(pvo.getLevelname().startsWith("����δƸ")){
				docname = "����-��ϰ��";
			}else if(pvo.getLevelname().startsWith("�̻�")){
				docname = docname.replace("�̻�", "ר��");
			}else if(pvo.getLevelname().startsWith("������")){
				docname = docname.replace("������", "����");
			}else if(pvo.getLevelname().startsWith("��ͨ��")){
				docname = "����-��ͨ��";
			}
			IDefdoc doc = NCLocator.getInstance().lookup(IDefdoc.class);
			DefdocVO[] docvos = (DefdocVO[])doc.queryByWhere(" docname='"+docname+"' and pk_defdoclist='00016910000000009GR4' ");
			if(docvos!=null&&docvos.length>0){
				PsndocVO psnvo = (PsndocVO)HYPubBO_Client.queryByPrimaryKey(PsndocVO.class, psnid);
				if(waflag!=null&&waflag.booleanValue()){
					psnvo.setGroupdef33(docvos[0].getPrimaryKey());
				}else{
					psnvo.setGroupdef33(null);
				}
				HYPubBO_Client.update(psnvo);
			}
		}

		setState(UNKNOWN_STATE);
		return true;
	}

	public void onDelte() throws Exception {

		PsndocWadocVO selectVO = getSelectVO();

		if (selectVO.getWorkflowflag() != null
				&& selectVO.getWorkflowflag().booleanValue()) {
			throw new Exception(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"60130704", "UPP60130704-000326")/*
					 * @res "������Դ���ݲ���ɾ��"
					 */);
		}
		WADelegator.getWaPsndoc().deleteByPsndocWadocVO(selectVO);

		setState(UNKNOWN_STATE);
	}

	public PsndocWadocVO getSelectVO() {
		// getBillPane().getTable().getCellEditor().stopCellEditing();
		PsndocWadocVO selectVO = new PsndocWadocVO();
		getBillPane().getTable().getSelectedRows();
		int selectRow = getBillPane().getTable().getSelectedRow();

		BillItem[] billItems = getBillPane().getTableModel().getBodyItems();
		for (BillItem item : billItems) {
			Object value = getBillPane().getTableModel().getValueAt(selectRow,
					item.getKey());
			if (value != null) {
				if (value instanceof Boolean) {
					value = new UFBoolean((Boolean) value);
				}
			}
			if (value != null) {
				selectVO.setAttributeValue(item.getKey(), value);
			}
		}
		// selectVO = (PsndocWadocVO)
		// getBillPane().getTableModel().getBodyValueRowVO(selectRow,
		// PsndocWadocVO.class.getName());
		selectVO.setPk_psndoc(getMainVO().getPk_psndoc());
		return selectVO;
	}

	private void saveInsertSelectVO(PsndocWadocVO selectVO)
	throws BusinessException {

		WADelegator.getWaPsndoc().insertPsndocWadocVO(selectVO);

	}

	private void saveModifySelectVO(PsndocWadocVO selectVO)
	throws BusinessException {

		WADelegator.getWaPsndoc().updatePsndocWadoc(selectVO);

	}

	public void onModify() throws Exception {
		int selectRow = getBillPane().getTable().getSelectedRow();
		last_select_row_number = selectRow;

		String pk_wa_item = getBillPane().getTableModel().getValueAt(selectRow,
				"pk_wa_item").toString();

		((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
		                                                          .getComponent()).setRefModel(new GradeRefModel(pk_wa_item));

		setState(MODIFY_STATE);

		getBillPane().getTable().setRowSelectionInterval(
				last_select_row_number, last_select_row_number);
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-8-7 15:03:19)
	 * 
	 * @param newDeptname
	 *            java.lang.String
	 */
	public void setDeptname(java.lang.String newDeptname) {
		deptname = newDeptname;
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-8-7 15:02:49)
	 * 
	 * @param newPsnname
	 *            java.lang.String
	 */
	public void setPsnname(java.lang.String newPsnname) {
		psnname = newPsnname;
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-6-8 19:49:13)
	 * 
	 * @param mainVOs
	 *            nc.vo.hi.wadoc.PsndocWadocMainVO[]
	 * @exception java.lang.Exception
	 *                �쳣˵����
	 */
	public void setWadocData(nc.vo.hi.wadoc.PsndocWadocVO[] subVOs)
	throws java.lang.Exception {
		this.subVOs = subVOs;
		getBillPane().getTable().getSelectionModel()
		.removeListSelectionListener(this);
		getBillPane().getTableModel().setBodyDataVO(subVOs);
		getBillPane().getTable().getSelectionModel().addListSelectionListener(
				this);

		TableColResize.reSizeTable(getBillPane());

	}

	public void refreshCriterion(final String pk_wa_grd) {
		String colKey = "criterionvalue";

		int colKeyIndex = findColIndex(colKey);

		UIRefPane pane = new UIRefPane() {
			private static final long serialVersionUID = -4261446741080782942L;

			@Override
			public void onButtonClicked() {
				if (pk_wa_grd != null) {
					// TODO Auto-generated method stub
					CriterionDlg dlg = new CriterionDlg(fatherUI, pk_wa_grd);
					dlg.showModal();
					if (dlg.getResult() == UIDialog.ID_OK) {
						onViewValueByGrd(dlg.getSelectedCriterionVO());
						setText(dlg.getSelectedValue());

					}
				}
			}
		};
		pane.setEditable(false);
		pane.setEnabled(true);

		colKeyIndex = getBillPane().getTable().convertColumnIndexToView(
				colKeyIndex);
		TableColumn tableColumn = getBillPane().getTable().getColumnModel()
		.getColumn(colKeyIndex);

		tableColumn.setCellEditor(new BillCellEditor(pane));

	}

	/**
	 * ������Ա�����Զ�����Ĭ�ϵĽ�� Created on 2008-11-28
	 * 
	 * @author zhangg
	 * @throws BusinessException
	 */
	public void setDefaultData(String pk_grd) {
		try {
			String pk_psndoc = getPk_psndoc();
			int rowIndex = getBillPane().getTable().getSelectedRow();
			String pk_wa_item = getBillPane().getTableModel().getValueAt(
					rowIndex, "pk_wa_item").toString();
			getBillPane().getTableModel().setValueAt(pk_grd, rowIndex,
			"pk_wa_grd");
			getBillPane().getTableModel().setValueAt(null, rowIndex,
					findColIndex("wa_prmlv_levelname"));
			getBillPane().getTableModel().setValueAt(null, rowIndex,
					findColIndex("wa_seclv_levelname"));
			getBillPane().getTableModel().setValueAt(null, rowIndex,
			"pk_wa_pralv");
			getBillPane().getTableModel().setValueAt(null, rowIndex,
			"pk_wa_seclv");
			if (pk_wa_item != null && pk_psndoc != null && pk_grd != null) {
				PsnappaproveBVO psnappaproveBVO = AdjustUtil.getExtraInfo(
						pk_psndoc, pk_wa_item, pk_grd, true);
				if (psnappaproveBVO != null) {
					getBillPane().getTableModel().setValueAt(
							psnappaproveBVO.getPk_wa_prmlv_apply(), rowIndex,
					"pk_wa_pralv");
					getBillPane().getTableModel().setValueAt(
							psnappaproveBVO.getPk_wa_seclv_apply(), rowIndex,
					"pk_wa_seclv");
					getBillPane().getTableModel().setValueAt(
							psnappaproveBVO.getWa_apply_money(), rowIndex,
					"criterionvalue");
					getBillPane().getTableModel().setValueAt(
							psnappaproveBVO.getWa_apply_money(), rowIndex,
					"nmoney");
					getBillPane().getTableModel().setValueAt(
							psnappaproveBVO.getWa_prmlv_apply(), rowIndex,
					"wa_prmlv_levelname");
					getBillPane().getTableModel().setValueAt(
							psnappaproveBVO.getWa_seclv_apply(), rowIndex,
					"wa_seclv_levelname");
				}
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void afterEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int nRow = getBillPane().getTable().getSelectedRow();
		if (e.getKey() == "vname") {
			UIRefPane itemPane = ((UIRefPane) getBillPane().getTableModel()
					.getBodyItems()[0].getComponent());
			String pk_wa_item = itemPane.getRefPK();
			// liangxr
			int iflddecimal = (Integer) itemPane.getRefValue("iflddecimal");
			getBillPane().getTableModel().setValueAt(iflddecimal, nRow,
			"iflddecimal");

			// ����С��λ����һ��ʱ������С��λ����������converter��
			int col = getBillPane().getTableModel().getBodyColByKey(
			"criterionvalue");
			getBillPane().getTableModel().getBodyItems()[col]
			                                             .setConverter(new UFDoubleConverter(iflddecimal));
			col = getBillPane().getTableModel().getBodyColByKey("nmoney");
			getBillPane().getTableModel().getBodyItems()[col]
			                                             .setConverter(new UFDoubleConverter(iflddecimal));

			((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
			                                                          .getComponent()).setRefModel(new GradeRefModel(pk_wa_item));
			((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
			                                                          .getComponent()).setText(null);
			((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
			                                                          .getComponent()).setReturnCode(true);
			((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
			                                                          .getComponent()).setRefInputType(0);
			getBillPane().getTableModel().setValueAt(null, nRow, "wagradename");
			getBillPane().getTableModel().setValueAt(null, nRow, "pk_wa_grd");
			getBillPane().getTableModel().setValueAt(pk_wa_item, nRow,
			"pk_wa_item");
		} else if (e.getKey() == "wagradename") {
			UIRefPane gradePane = ((UIRefPane) getBillPane().getTableModel()
					.getBodyItems()[1].getComponent());
			String pk_grd = gradePane.getRefPK();
			refreshCriterion(pk_grd);

			setDefaultData(pk_grd);

			String pk_wa_item = (String)getBillScrollPane().getTableModel().getValueAt( e.getRow(),"pk_wa_item");

			if(pk_wa_item!=null&&pk_wa_item.equals("0001691000000000BYY5")){//��λ���������籣��������
				UFDouble nmny = new UFDouble(0.00);
				int rowcount = getBillScrollPane().getTableModel().getRowCount();
				for(int i=0;i<rowcount;i++){
					String iitemid = (String)getBillScrollPane().getTableModel().getValueAt(i, "pk_wa_item");
					UFBoolean last = getBillScrollPane().getTableModel().getValueAt(i, "lastflag")!=null?new UFBoolean(getBillScrollPane().getTableModel().getValueAt(i, "lastflag").toString()):new UFBoolean(false);
					if(last!=null&&last.booleanValue()&&iitemid!=null&&iitemid.equals("0001691000000000T5E2")){
						nmny = getBillScrollPane().getTableModel().getValueAt(i, "nmoney")!=null?new UFDouble(getBillScrollPane().getTableModel().getValueAt(i, "nmoney").toString()):new UFDouble(0.00);
						break;
					}
				}
				getBillScrollPane().getTableModel().setValueAt(nmny.multiply(0.05).setScale(0, 2), e.getRow(),"nmoney");
			}
		} else if (e.getKey() == "negotiation_wage") {
			Object object = getBillPane().getTableModel().getValueAt(nRow,
			"negotiation_wage");
			if (object != null) {
				negotiation_wage = (Boolean) object;
			}

			getBillPane().getTableModel().setValueAt(null, nRow, "pk_wa_pralv");
			getBillPane().getTableModel().setValueAt(null, nRow, "pk_wa_seclv");
			getBillPane().getTableModel().setValueAt(null, nRow,
			"criterionvalue");

			getBillPane().getTableModel().setValueAt(null, nRow,
			"wa_prmlv_levelname");
			getBillPane().getTableModel().setValueAt(null, nRow,
			"wa_seclv_levelname");

		} else if (e.getKey() == "docname") {
			String pk_changecause = ((UIRefPane) getBillPane().getTableModel()
					.getBodyItems()[findColIndex("docname")].getComponent())
					.getRefPK();
			getBillPane().getTableModel().setValueAt(pk_changecause, nRow,
			"pk_changecause");

		}
	}

	public void onViewValueByGrd(CriterionVO vo) {

		int nRow = getBillPane().getTable().getSelectedRow();

		getBillPane().getTableModel().setValueAt(vo.getPk_wa_prmlv(), nRow,
		"pk_wa_pralv");
		getBillPane().getTableModel().setValueAt(vo.getPk_wa_seclv(), nRow,
		"pk_wa_seclv");
		getBillPane().getTableModel().setValueAt(
				vo.getCriterionvalue().toString(), nRow, "criterionvalue");
		getBillPane().getTableModel().setValueAt(
				vo.getCriterionvalue().toString(), nRow, "nmoney");
		getBillPane().getTableModel().setValueAt(vo.getClsName(), nRow,
		"wa_prmlv_levelname");
		getBillPane().getTableModel().setValueAt(vo.getLevName(), nRow,
		"wa_seclv_levelname");
	}

	private int findColIndex(String keyName) {
		return getBillPane().getTableModel().getBodyColByKey(keyName);
	}

	public CriterionVO findByPKeys(String key1, String key2, String key3) {
		CriterionVO vo = null;
		try {
			vo = WADelegator.getWaCriterion().findCriterionByKeys(key1, key2,
					key3);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vo;
	}

	public void bodyRowChange(BillEditEvent e) {
		// TODO Auto-generated method stub
		int row = getBillPane().getTable().getSelectedRow();

		if (row > -1) {
			if (getState().equals(UNKNOWN_STATE)
					|| getState().equals(DELETE_STATE)) {
				fatherUI.setBtnsStatWithUIStat(HRToftPanel.UI_STAT_BROWSE);
			} else if (getState().equals(ADD_STATE)
					|| getState().equals(INSERT_STATE)
					|| getState().equals(MODIFY_STATE)) {
				fatherUI.setBtnsStatWithUIStat(HRToftPanel.UI_STAT_UPDATE);
			}

			Boolean object = (Boolean) getBillPane().getTableModel()
			.getValueAt(row, "negotiation_wage");
			if (object != null && object.booleanValue()) {
				negotiation_wage = true;
			} else {
				negotiation_wage = false;
			}

			Object pk_wa_grd = getBillPane().getTableModel().getValueAt(row,
			"pk_wa_grd");

			if (pk_wa_grd != null) {
				refreshCriterion(pk_wa_grd.toString());
			} else {
				refreshCriterion(null);
			}

		}
	}

	public void valueChanged(ValueChangedEvent event) {
		// TODO Auto-generated method stub

	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void validate(PsndocWadocVO psVo) throws BusinessException {
		String error = null;
		if (psVo.getPk_wa_item() == null || psVo.getPk_wa_item().length() <= 0) {
			error = ResHelper.getString("60130715", "UPPT60130715-000153"); // "н����Ŀ����Ϊ��.";
			throw new BusinessException(error);
		}
		if (psVo.getPk_wa_grd() == null
				|| psVo.getPk_wa_grd().toString().length() <= 0) {
			error = ResHelper.getString("60130704", "UPP60130704-000322");// "н�ʵȼ������Ϊ�ա�";
			throw new BusinessException(error);
		}

		if (psVo.getBegindate() == null
				|| psVo.getBegindate().toString().length() <= 0) {
			error = ResHelper.getString("60130715", "UPPT60130715-000155");// "������н�ʵ���ʼʱ��.";
			throw new BusinessException(error);
		}
		if (psVo.getEnddate() != null
				&& psVo.getEnddate().toString().length() > 0
				&& psVo.getEnddate().before(psVo.getBegindate())) {
			error = ResHelper.getString("60130715", "UPPT60130715-000156"); // "����ʱ�䲻Ӧ������ʼʱ��.";
			throw new BusinessException(error);

		}
		if (!negotiation_wage) {
			if (psVo.getPk_wa_grd() == null || psVo.getPk_wa_pralv() == null) {
				error = ResHelper.getString("60130704", "UPP60130704-000321");// "��̸�й���,
				// �������뼶��.";
				throw new BusinessException(error);
			}

			GradeVO grdvo = null;
			grdvo = WADelegator.getWaGrade().findGradeByPk(psVo.getPk_wa_grd());

			if (grdvo != null && grdvo.getIsmultsec().booleanValue()) {
				if (psVo.getPk_wa_seclv() == null
						|| psVo.getPk_wa_seclv().length() <= 0) {
					error = ResHelper.getString("60130704",
					"UPP60130704-000321");// "��̸�й���,
					// //
					// �������뼶��.";
					throw new BusinessException(error);
				}
			}
		}
		if (psVo.getNmoney() == null) {
			error = ResHelper.getString("60130715", "UPPT60130715-000154");// "����Ϊ��.";
			throw new BusinessException(error);
		}
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		if (getState() == ADD_STATE || getState() == INSERT_STATE
				|| getState() == MODIFY_STATE) {
			getBillPane().getTable().setRowSelectionInterval(
					last_select_row_number, last_select_row_number);
		}
	}

	public java.lang.String getPsnCode() {
		return psnCode;
	}

	public void setPsnCode(java.lang.String psnCode) {
		this.psnCode = psnCode;
	}

	public java.lang.String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(java.lang.String deptCode) {
		this.deptCode = deptCode;
	}

	public PsndocWadocMainVO getMainVO() {
		return mainVO;
	}

	public void setMainVO(PsndocWadocMainVO mainVO) {
		this.mainVO = mainVO;
	}
}
