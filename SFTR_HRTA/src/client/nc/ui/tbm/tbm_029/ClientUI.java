package nc.ui.tbm.tbm_029;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.pub.PubDelegator;
import nc.itf.hr.ta.TADelegator;
import nc.itf.hr.ta.algorithm.ITimeScope;
import nc.itf.hr.ta.algorithm.TimeScopeUtils;
import nc.itf.hr.ta.algorithm.impl.DefaultTimeScope;
import nc.itf.hr.ta.util.PubUISet;
import nc.itf.uap.bd.def.IDefdoc;
import nc.ui.bd.def.DefdocRefModel;
import nc.ui.hr.comp.wizard.WizardDialog;
import nc.ui.hr.comp.wizard.WizardStep;
import nc.ui.hr.global.Global;
import nc.ui.hrp.pub.excel.ImportExcelData;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UILabelLayout;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITablePane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.beans.textfield.UITextType;
import nc.ui.pub.print.PrintEntry;
import nc.ui.tbm.pub.ButtonTipMessage;
import nc.ui.trade.bsdelegate.BusinessDelegator;
import nc.ui.trade.report.query.QueryDLG;
import nc.ui.util.TAClientUtil;
import nc.vo.bd.def.DefdocVO;
import nc.vo.hr.comp.formulaset.FuncParser;
import nc.vo.hr.para2.ParDefVO;
import nc.vo.hr.para2.ParValueVO;
import nc.vo.hrcp.indi.DefDocVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.pub.query.ConditionVO;
import nc.vo.tbm.tbm_005.TimeruleVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;
import nc.vo.tbm.tbm_029.BclbItemVO;
import nc.vo.tbm.tbm_029.BclbVO;
import nc.vo.uap.rbac.RoleVO;

import org.apache.commons.lang.StringUtils;

/**
 * 班别定义(HR经理) 创建日期：(2001-06-03 11:21:04)
 *
 * @author：
 *
 *
 * * 修改：马雅林 2005-6-28
 * 原因：
 * 涉及代码：
 * nc.ui.tbm.tbm_029.ClientUI.initialize()
 * nc.ui.tbm.tbm_029.ClientUI.onCancel()
 * nc.ui.tbm.tbm_029.ClientUI.onConfirm()
 * nc.ui.tbm.tbm_029.ClientUI.setState(int)
 * nc.ui.tbm.tbm_029.ClientUI.valueChanged(ListSelectionEvent)
 * nc.ui.tbm.tbm_029.ClientUI.checkHeader()
 * nc.ui.tbm.tbm_029.ClientUI.actionPerformed(ActionEvent)
 * nc.vo.tbm.tbm_029.BclbHeaderVO
 * nc.bs.tbm.tbm_029.BclbDMO.updateHeader(BclbHeaderVO)
 * nc.bs.tbm.tbm_029.BclbDMO.findHeaderByPrimaryKey(String)
 * nc.bs.tbm.tbm_029.BclbDMO.insertHeader(BclbHeaderVO)
 * nc.bs.tbm.tbm_029.BclbDMO.queryAllBclbHeader(String)
 * nc.bs.tbm.tbm_029.BclbDMO.queryAllBclbHeader(String, String)
 * nc.ui.tbm.tbm_029.ClientUI.clearBclb()
 * *修改、刘鹏飞 2011-4-14
 * 
 */
public class ClientUI extends nc.ui.bd.mmpub.MMToftPanel implements
		java.awt.event.ActionListener, ListSelectionListener,
		ValueChangedListener, java.awt.event.ItemListener, TreeSelectionListener {
	private UITablePane ivjBclbTablePane = null;

	private UITablePane ivjWtTablePane = null;

	private BclbTableModel ivjBclbModel = null;

	private WtTableModel ivjWtModel = null;

	// 按钮组
	private ButtonObject boAdd = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000002")/* @res "增加" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000044")/* @res "增加一个班次类别" */, 3,"增加"); /*-=notranslate=-*/
	private ButtonObject boDel = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000039")/* @res "删除" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000045")/* @res "删除一个班次类别" */, 3,"删除"); /*-=notranslate=-*/
	private ButtonObject boModify = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000045")/* @res "修改" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000046")/* @res "修改一个班次类别" */, 3,"修改"); /*-=notranslate=-*/
	private ButtonObject boConfirm = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000044")/* @res "确定" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000047")/* @res "保存变化" */, 3,"确定"); /*-=notranslate=-*/
	private ButtonObject boCancel = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000008")/* @res "取消" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000048")/* @res "取消变化" */, 0,"取消"); /*-=notranslate=-*/
	private ButtonObject boFlash = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000009")/* @res "刷新" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000049")/* @res "刷新数据" */, 0,"刷新"); /*-=notranslate=-*/
	private ButtonObject boPrint = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000007")/* @res "打印" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC001-0000007")/* @res "打印" */, 0,"打印"); /*-=notranslate=-*/
	private ButtonObject boCopy = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017","UPP6017-001296")/* @res "班别复制" */,nc.ui.ml.NCLangRes.getInstance().getStrByID("6017","UPP6017-001296")/* @res "班别复制" */, 0,"班别复制"); /*-=notranslate=-*/
	//新增查询按钮
	private ButtonObject boQuery = new ButtonObject("查询"/* @res "打印" */,"查询", 0,"查询"); /*-=notranslate=-*/
	private ButtonObject boImport = new ButtonObject("导入"/* @res "打印" */,"导入", 0,"导入"); /*-=notranslate=-*/
	private ButtonObject[] aryButtonGroup = { boAdd, boModify, boDel,
			boConfirm, boCancel, boCopy, boFlash, boPrint,boQuery,boImport };

	//	private java.util.Hashtable h_bcvo=new java.util.Hashtable();

	private BclbItemVO[] btvos = new BclbItemVO[WtTableModel.WTNUM];

	private int state = 0;//0：浏览 1：增加 2：修改

	private UIPanel ivjEditPanel = null;

	private UITextField ivjTfGzsj = null;

	private UITextField ivjTfLbbm = null;

	private UITextField ivjTfLbmc = null;
	
	private UITextField ivjTfMemo = null;//新增，班次说明
	
	private UILabel ivjUILabel1 = null;

	private UILabel ivjUILabel2 = null;

	//是否默认班别
	private UILabel ivjUILabelSfmrbb = null;

//	是否作废
	private UILabel ivjUILabelSfzf = null;
	
	private UILabel ivjUILabel3 = null;

	private UIPanel ivjUIPanel1 = null;

	private int mrow = -1;

	private UICheckBox ivjcbxSfkq = null;

	private UILabel ivjUILabel4 = null;

	private UIPanel ivjContentPanel = null;

	private java.awt.GridLayout ivjContentPanelGridLayout = null;

	private UIPanel ivjHeadPanel = null;

	private UIRefPane ivjRfpFactory = null;

	private UILabel ivjUILabel5 = null;

	private DataSource m_dataSource = null;

	//
	private UILabelLayout m_headLabelLayout = null;

	// 打印变量
	private PrintEntry m_print = null;


	/**
	 *  Modified by Young 2005-09-22 Start
	 */
	private UICheckBox ivjUIChkbontmrule = null;
	private UICheckBox ivjUIChkbovertmrule = null;
	private UILabel ivjUILbontm21 = null;
	private UILabel ivjUILbovertm1 = null;
	private UILabel ivjUILbovertm11 = null;
	private UILabel ivjUILbovertm21 = null;
	private UIRefPane ivjUIRefPane1ontype = null;
	private UIRefPane ivjUIRefPane1overtype = null;
	private UITextField ivjUITFontmrule = null;
	private UITextField ivjUITFovertmrule = null;
	/**
	 *  Modified by Young 2005-09-22 End
	 */


	/**
	 *   Added by Young 2005-07-19 Start
	 */
	private TimeruleVO ruleVO = null;
	/**
	 *   Added by Young 2005-07-19 End
	 */

	private FuncParser funcParser = null;

	//add by caizl
	private UILabel bbfl = null;
	private UIRefPane bbflRef = null;
	
	private UILabel ddfl = null;
	private UIRefPane ddflRef = null;
	private UILabel bbzfl = null;
	private UIRefPane bbzflRef = null;
	private UITree bclb = null;
	private UIScrollPane bclbPane = null;
	private UIDialog copyDialog = null;
	
	private UILabel bbmb = null;
	private UIRefPane bbmbRef = null;

	
	private UITextField ivjCkbBbjc = null;
	/**
	 * ClientUI 构造子注解。
	 */
	public ClientUI() {
		super();
		initialize();
	}

	/**
	 * 校验数据是否合法
	 * @throws BusinessException 
	 */
	private int checkData() throws BusinessException {
		int result = checkHeader();
		if (result != 0) {
			return result;
		}
		return checkItems();
	}

	/**
	 * 清除右侧班次类别设置。 创建日期：(2001-06-13 13:59:45)
	 */
	private void clearBclb() {

		//boDel.setEnabled(false);

		getTfGzsj().setText("");
		getTfLbbm().setText("");
		getTfLbmc().setText("");
		getTfMemo().setText("");
		getcbxSfkq().setSelected(false);
		getCkbSFFC().setSelected(false);

		getTfKqkssj().setText("");

		//add5.5
		getChbKqkssj().setSelectedIndex(0);
		getChbKqjssj().setSelectedIndex(0);
		getChbYbkssj().setSelectedIndex(0);
		getChbYbjssj().setSelectedIndex(0);
		getCkbsfyxwc().setSelected(true);

		getTfKqjssj().setText("");
		getTfYbkssj().setText("");
		getTfYbjssj().setText("");
		getTfallowearly().setText("");
		getTfallowlate().setText("");
		getTflargeearly().setText("");
		getTflargelate().setText("");
		getCkbSfmrbb().setSelected(false);
		getCkbSfzf().setSelected(false);
		getCkbSfyb().setSelected(false);
		getTfYbkssj().setEnabled(false);
		getChbYbkssj().setEnabled(false);
		getChbYbjssj().setEnabled(false);
		getTfYbjssj().setEnabled(false);
		getTfkgsc().setText("");
		getWtModel().clearTable();
		getrbButton_Auto().setSelected(true);

		/**
		 *  Modified by Young 2005-09-22 Start
		 */
		//add by myl
		getUIChkbovertmrule().setSelected(false);
		getUITFovertmrule().setText("");
		getUITFovertmrule2().setText("");
		getUIRefPane1overtype().setPK(null);
		getUIChkbontmrule().setSelected(false);
		getUITFontmrule().setText("");
		getUITFontmrule2().setText("");
		getUIRefPane1ontype().setPK(null);
		/**
		 *  Modified by Young 2005-09-22 End
		 */

		//add by caizl
		getBbflRef().setPK(null);
		getCkbbbjc().setText("");
         getddflRef().setPK(null);
         getbbmbRef().setPK(null);
         getbbzflRef().setPK(null);
		for (int i = 0; i < WtTableModel.WTNUM; i++) {
			BclbItemVO btvo = new BclbItemVO();
			btvo.setPk_corp(getUnitCode());
			btvo.setGcbm(getFactoryCode());
			btvo.setWtbeginday(new Integer(0));
			btvo.setWtendday(new Integer(0));
			btvos[i] = btvo;
		}
		getWtModel().addVO(btvos);


		/**
		 *  Added by Young 2005-07-20  Start
		 */
		getWtModel().setAddRow(0);
		/**
		 *  Added by Young 2005-07-20  End
		 */

	}

	/**
	 * 返回 BclbModel 特性值。
	 *
	 * @return nc.ui.pd.pd1020.BclbTableModel
	 */
	/* 警告：此方法将重新生成。 */
	private BclbTableModel getBclbModel() {
		// user code begin {1}
		if (ivjBclbModel == null) {
			ivjBclbModel = new BclbTableModel(
					nc.vo.tbm.tbm_029.BclbHeaderVO.class);

		}
		// user code end
		return ivjBclbModel;
	}

	/**
	 * 返回 BclbTablePane 特性值。
	 *
	 * @return nc.ui.pub.beans.UITablePane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITablePane getBclbTablePane() {
		if (ivjBclbTablePane == null) {
			try {
				ivjBclbTablePane = new nc.ui.pub.beans.UITablePane();
				ivjBclbTablePane.setName("BclbTablePane");
				ivjBclbTablePane.setBounds(0, 0, 264, 495);
				// user code begin {1}
				ivjBclbTablePane
						.setBorder(new nc.ui.pub.beans.border.UITitledBorder(
								nc.ui.ml.NCLangRes.getInstance().getStrByID(
										"6017010113", "UPP6017010113-000050")/*
																		  * @res
																		  * "班次类别列表"
																		  */));
				ivjBclbTablePane.getTable().setModel(getBclbModel());
				ivjBclbTablePane.getTable().getTableHeader().setPreferredSize(
						new Dimension(0, 20));
				ivjBclbTablePane.getTable().setSelectionMode(
						javax.swing.ListSelectionModel.SINGLE_SELECTION);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjBclbTablePane;
	}

	/**
	 * 返回 UICheckBox2 特性值。
	 *
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UICheckBox getcbxSfkq() {
		if (ivjcbxSfkq == null) {
			try {
				ivjcbxSfkq = new nc.ui.pub.beans.UICheckBox();
				ivjcbxSfkq.setName("cbxSfkq");
				ivjcbxSfkq.setPreferredSize(new java.awt.Dimension(20, 22));
				ivjcbxSfkq.setText("");
				ivjcbxSfkq.setBounds(383, 42, 20, 22);

				ivjcbxSfkq.setVisible(false);
				// user code begin {1}
				/**
				 *  Modified by Young 2005-10-17 Start
				 */
				ivjcbxSfkq.setVisible(false);
				/**
				 *  Modified by Young 2005-10-17 End
				 */
				//liupf add begin 2011-4-1
				//ivjcbxSfkq.setVisible(true);
//				liupf add begin 2011-4-1
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjcbxSfkq;
	}

	/**
	 * 返回 EditPanel 特性值。
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getEditPanel() {
		if (ivjEditPanel == null) {
			try {
				ivjEditPanel = new nc.ui.pub.beans.UIPanel();
				ivjEditPanel.setName("EditPanel");
				ivjEditPanel.setPreferredSize(new java.awt.Dimension(10, 328));//355
				ivjEditPanel.setLayout(null);
				getEditPanel().add(getUILabel1(), getUILabel1().getName());

				//add start 原来是否默认班别 ，现在注掉 换成 是否作废
				/*
				getEditPanel().add(getUILabelSfmrbb(), getUILabelSfmrbb().getName());
				getEditPanel().add(getCkbSfmrbb(), getCkbSfmrbb().getName());
				*/
				getEditPanel().add(getUILabelSfzf(), getUILabelSfzf().getName());
				getEditPanel().add(getCkbSfzf(), getCkbSfzf().getName());
				//add end
				
				getEditPanel().add(getUILabel2(), getUILabel2().getName());
				getEditPanel().add(getUILabel3(), getUILabel3().getName());//工作时长
				getEditPanel().add(getTfLbbm(), getTfLbbm().getName());
				getEditPanel().add(getTfLbmc(), getTfLbmc().getName());
				getEditPanel().add(getTfGzsj(), getTfGzsj().getName());//工作时间 输入框
				getEditPanel().add(getLbHour(), getLbHour().getName());//工作时间 小时
				getEditPanel().add(getUILabel5(), getUILabel5().getName());
				getEditPanel().add(getcbxSfkq(), getcbxSfkq().getName());
				getEditPanel().add(getLbKqkssj(), getLbKqkssj().getName());
				getEditPanel().add(getChbKqkssj(), getChbKqkssj().getName());
				getEditPanel().add(getTfKqkssj(), getTfKqkssj().getName());
				getEditPanel().add(getLbKqjssj(), getLbKqjssj().getName());
				getEditPanel().add(getChbKqjssj(), getChbKqjssj().getName());
				getEditPanel().add(getTfKqjssj(), getTfKqjssj().getName());
				getEditPanel().add(getLbSfyb(), getLbSfyb().getName());
				getEditPanel().add(getLbBbjc(), getLbBbjc().getName());
				
				//新增，说明
				getEditPanel().add(getLbMemo(), getLbMemo().getName());
				getEditPanel().add(getTfMemo(), getTfMemo().getName());//刘鹏飞新增班次说明
				
				/*注掉三行
				getEditPanel().add(getLbYbkssj(), getLbYbkssj().getName());
				getEditPanel().add(getChbYbkssj(), getChbYbkssj().getName());
				getEditPanel().add(getTfYbkssj(), getTfYbkssj().getName());
				*/
				getEditPanel().add(getCkbSfyb(), getCkbSfyb().getName());
				getEditPanel().add(getCkbbbjc(), getCkbbbjc().getName());
				/*注掉到注释处*//*
				getEditPanel().add(getLbsfyxwc(), getLbsfyxwc().getName());//是否允许中途外出
				getEditPanel().add(getCkbsfyxwc(), getCkbsfyxwc().getName());
				getEditPanel().add(getLbYbjssj(), getLbYbjssj().getName());
				getEditPanel().add(getChbYbjssj(), getChbYbjssj().getName());
				getEditPanel().add(getTfYbjssj(), getTfYbjssj().getName());
				getEditPanel().add(getLbYbkssj1(), getLbYbkssj1().getName());
				getEditPanel().add(getLbYbkssj2(), getLbYbkssj2().getName());
				getEditPanel().add(getTfallowlate(), getTfallowlate().getName());
				getEditPanel().add(getLbMin(), getLbMin().getName());
				getEditPanel().add(getTfallowearly(),getTfallowearly().getName());
				getEditPanel().add(getLbMin1(),getLbMin1().getName());
				getEditPanel().add(getLbYbkssj11(), getLbYbkssj11().getName());
				getEditPanel().add(getTflargelate(), getTflargelate().getName());
				getEditPanel().add(getLbMin2(), getLbMin2().getName());
				getEditPanel().add(getLbYbkssj21(), getLbYbkssj21().getName());
				getEditPanel().add(getTflargeearly(),getTflargeearly().getName());
				getEditPanel().add(getLbMin3(),getLbMin3().getName());
				getEditPanel().add(getLbKqsfkt1(), getLbKqsfkt1().getName());
				getEditPanel().add(getUIpnlScore(), getUIpnlScore().getName());

				getEditPanel().add(getLbSFFC(), getLbSFFC().getName());
				getEditPanel().add(getCkbSFFC(), getCkbSFFC().getName());
*/
				/**
				 *  Modified by Young 2005-09-22 Start
				 */
				/*一会注掉*//*
				getEditPanel().add(getUIChkbovertmrule(), getUIChkbovertmrule().getName());
				getEditPanel().add(getUILbovertm1(), getUILbovertm1().getName());
				getEditPanel().add(getUITFovertmrule(), getUITFovertmrule().getName());
				getEditPanel().add(getUILbovertm21(), getUILbovertm21().getName());
				getEditPanel().add(getUITFovertmrule2(), getUITFovertmrule2().getName());
				getEditPanel().add(getUILbovertm212(), getUILbovertm212().getName());

				getEditPanel().add(getUIRefPane1overtype(), getUIRefPane1overtype().getName());
				getEditPanel().add(getUIChkbontmrule(), getUIChkbontmrule().getName());
				getEditPanel().add(getUILbovertm11(), getUILbovertm11().getName());
				getEditPanel().add(getUITFontmrule(), getUITFontmrule().getName());
				getEditPanel().add(getUILbontm21(), getUILbontm21().getName());
				getEditPanel().add(getUITFontmrule2(), getUITFontmrule2().getName());
				getEditPanel().add(getUILbontm212(), getUILbontm212().getName());
				getEditPanel().add(getUIRefPane1ontype(), getUIRefPane1ontype().getName());
				getEditPanel().add(getLbKqsfkt1(), getLbKqsfkt1().getName());*/
				/*一会注掉*/
				/**
				 *  Modified by Young 2005-09-22 End
				 */

				//add by caizl
				getEditPanel().add(getBbfl(), getBbfl().getName());
				getEditPanel().add(getBbflRef(), getBbflRef().getName());
				getEditPanel().add(getddfl(), getddfl().getName());
				getEditPanel().add(getddflRef(), getddflRef().getName());
				getEditPanel().add(getbbzfl(), getbbzfl().getName());
				getEditPanel().add(getbbzflRef(), getbbzflRef().getName());
				
				// add by zhanghua
				getEditPanel().add(getbbmb(), getbbmb().getName());
				getEditPanel().add(getbbmbRef(), getbbmbRef().getName());
				
				// user code begin {1}
				getEditPanel().setBorder(new nc.ui.pub.beans.border.UITitledBorder(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000001")/* @res "班别属性"*/));
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjEditPanel;
	}
	
	private UILabel getddfl(){
		if (ddfl == null) {
			ddfl = new UILabel();
			ddfl.setName("ddfl");
			ddfl.setText("地点");
			ddfl.setPreferredSize(new Dimension(80, 22));
			ddfl.setBounds(38, 134, 80, 22);
			ddfl.setILabelType(UILabel.STYLE_BLACKBLUE);
		}
		return ddfl;
	}
	
	private UILabel getbbmb(){
		if (bbmb == null) {
			bbmb = new UILabel();
			bbmb.setName("bbmb");
			bbmb.setText("报表模板");
			bbmb.setPreferredSize(new Dimension(80, 22));
			bbmb.setBounds(38, 160, 80, 22);
			bbmb.setILabelType(UILabel.STYLE_BLACKBLUE);
		}
		return bbmb;
	}
	
	private UIRefPane getbbmbRef(){
		if (bbmbRef == null) {
			bbmbRef = new UIRefPane();
			bbmbRef.setName("bbmbRef");
			bbmbRef.setPreferredSize(new Dimension(120, 22));
			bbmbRef.setBounds(118, 160, 120, 20);
			DefdocRefModel model = new DefdocRefModel();
			String pkCorp = nc.ui.hr.global.Global.getCorpPK();
			String sql;
			if ("0001".equals(pkCorp)) {
				//0001AA1000000010QM5W
				sql = "where bd_defdoc.pk_defdoclist = '00018L1000000010MZ10' and pk_crop = '0001'";
			} else {
				sql = "where bd_defdoc.pk_defdoclist = '00018L1000000010MZ10' and pk_corp in ('" + pkCorp + "','0001') ";
			}
			model.setWherePart(sql);
			bbmbRef.setRefModel(model);
		}
		return bbmbRef;
	}

	private UIRefPane getddflRef(){
		if (ddflRef == null) {
			ddflRef = new UIRefPane();
			ddflRef.setName("ddflRef");
			ddflRef.setPreferredSize(new Dimension(120, 22));
			ddflRef.setBounds(118, 134, 120, 20);
			DefdocRefModel model = new DefdocRefModel();
			String pkCorp = nc.ui.hr.global.Global.getCorpPK();
			String sql;
			if ("0001".equals(pkCorp)) {
				sql = "where bd_defdoc.pk_defdoclist = '000154100000001119NG' and pk_crop = '0001'";
			} else {
				sql = "where bd_defdoc.pk_defdoclist = '000154100000001119NG' and pk_corp in ('" + pkCorp + "','0001') ";
			}
			model.setWherePart(sql);
			ddflRef.setRefModel(model);
		}
		return ddflRef;
	}

	private UILabel getbbzfl(){
		if (bbzfl == null) {
			bbzfl = new UILabel();
			bbzfl.setName("bbzfl");
			bbzfl.setText("班种");
			bbzfl.setPreferredSize(new Dimension(80, 22));
			bbzfl.setBounds(280, 134, 80, 22);
			bbzfl.setILabelType(UILabel.STYLE_BLACKBLUE);
		}
		return bbzfl;
	}

	private UIRefPane getbbzflRef(){
		if (bbzflRef == null) {
			bbzflRef = new UIRefPane();
			bbzflRef.setName("bbzflRef");
			bbzflRef.setPreferredSize(new Dimension(120, 22));
			bbzflRef.setBounds(360, 134, 120, 20);
			DefdocRefModel model = new DefdocRefModel();
			String pkCorp = nc.ui.hr.global.Global.getCorpPK();
			String sql;
			if ("0001".equals(pkCorp)) {
				sql = "where bd_defdoc.pk_defdoclist = '000154100000001119NR' and pk_crop = '0001'";
			} else {
				sql = "where bd_defdoc.pk_defdoclist = '000154100000001119NR' and pk_corp in ('" + pkCorp + "','0001') ";
			}
			model.setWherePart(sql);
			bbzflRef.setRefModel(model);
		}
		return bbzflRef;
	}
	
	private UILabel getBbfl(){
		if (bbfl == null) {
			bbfl = new UILabel();
			bbfl.setName("bbfl");
			bbfl.setText(ResHelper.getString("6017","UPP6017-001297")//@res "班别分类"
);
			bbfl.setPreferredSize(new Dimension(80, 22));
			bbfl.setBounds(280, 16, 80, 22);
		}
		return bbfl;
	}

	private UIRefPane getBbflRef(){
		if (bbflRef == null) {
			bbflRef = new UIRefPane();
			bbflRef.setName("bbflRef");
			bbflRef.setPreferredSize(new Dimension(120, 22));
			bbflRef.setBounds(360, 16, 120, 20);
			DefdocRefModel model = new DefdocRefModel();
			String pkCorp = nc.ui.hr.global.Global.getCorpPK();
			String sql;
			if ("0001".equals(pkCorp)) {
				sql = "where bd_defdoc.pk_defdoclist = '0001Z71000000000IA6M' and pk_crop = '0001'";
			} else {
				sql = "where bd_defdoc.pk_defdoclist = '0001Z71000000000IA6M' and pk_corp = '" + pkCorp + "'";
			}
			model.setWherePart(sql);
			bbflRef.setRefModel(model);
		}
		return bbflRef;
	}

	private UITree getBclb() {
		if (bclb == null) {
			bclb = new UITree();
			bclb.setName("bclb");
			bclb.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			bclb.setModel(getTreeModel());
			bclb.setCellRenderer(new TreeCellRender());
			bclb.addTreeSelectionListener(this);
		}
		return bclb;
	}
    public void getChild(int x,HashMap<Integer, ArrayList<String>> map_list,ArrayList<DefdocVO> list_cvo){
    	if(list_cvo==null||list_cvo.size()<=0) return;
    	ArrayList<String> last_key = map_list.get(x);
    	x++;
    	ArrayList<String> new_key = new ArrayList<String>();
    	for(int i=0;i<list_cvo.size();i++){
    		DefdocVO vo = list_cvo.get(i);
    		if(last_key.contains(vo.getPk_defdoc1())){
    			new_key.add(vo.getPrimaryKey());
    			list_cvo.remove(vo);
    		}
    	}
    	map_list.put(x, new_key);
    	getChild(x, map_list, list_cvo);
    	
    }
	private void genChild(DefaultMutableTreeNode son,HashMap<String,ArrayList<DefdocVO>> map_list_b,String key,BclbHeaderVO[] bhvos){
		ArrayList<DefdocVO> list_b = map_list_b.get(key);
		if(list_b==null||list_b.size()<=0) return;
		for(int j=0;j<list_b.size();j++){
			DefdocVO vo = (DefdocVO)list_b.get(j).clone();
			DefaultMutableTreeNode grandson = new DefaultMutableTreeNode(vo);
			son.add(grandson);
			if(bhvos!=null&&bhvos.length>0){
			for (BclbHeaderVO bclbHeaderVO : bhvos) {
				if (vo.getPk_defdoc().equals(bclbHeaderVO.getBclbfl())) {
					DefaultMutableTreeNode ggrandson = new DefaultMutableTreeNode(bclbHeaderVO);
					grandson.add(ggrandson);
				}
			}
			}
			genChild(grandson, map_list_b, vo.getPrimaryKey(), bhvos);
		}
	}
    
    
	private BclbHeaderVO defaultBclbHeaderVO;
	private TreeModel getTreeModel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ResHelper.getString("6017010417","UPT6017010417-000001")//@res "班别"
);
		IDefdoc iIDefdoc = (IDefdoc) NCLocator.getInstance().lookup(IDefdoc.class.getName());
		DefdocVO[] defdocVOs = null;
		BclbHeaderVO[] bhvos = null;
		try {
			//sealflag is null or sealflag <>'Y'解决班别分类无法显示的问题---BD_DEFDOC 查询班别分类
			defdocVOs = iIDefdoc.queryByWhere(" pk_defdoclist='0001Z71000000000IA6M' and pk_corp='"+Global.getCorpPK()+"' and (sealflag is null or sealflag <>'Y') ");
			bhvos = TADelegator.getBclb029().queryBclb029AllBclbHeader(getUnitCode(), getFactoryCode());
			
		} catch (BusinessException e) {
			handleException(e);
		}
		if (defdocVOs != null) {
			//班别分类下的树
			HashMap<Integer, ArrayList<String>> map_list = new HashMap<Integer, ArrayList<String>>();
			HashMap<String,ArrayList<DefdocVO>> map_list_b = new HashMap<String, ArrayList<DefdocVO>>();
			HashMap<String, DefdocVO> map_vo = new HashMap<String, DefdocVO>();
			int x = 1;
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<DefdocVO> list_cvo = new ArrayList<DefdocVO>();
			for (DefdocVO defdocVO : defdocVOs){
				map_vo.put(defdocVO.getPrimaryKey(), defdocVO);
				if(defdocVO.getPk_defdoc1()==null||defdocVO.getPk_defdoc1().trim().length()<=0){
					list.add(defdocVO.getPrimaryKey());
				}else{
					ArrayList<DefdocVO> list_b = map_list_b.get(defdocVO.getPk_defdoc1())!=null?map_list_b.get(defdocVO.getPk_defdoc1()):new ArrayList<DefdocVO>();
					list_b.add(defdocVO);
					map_list_b.put(defdocVO.getPk_defdoc1(), list_b);
					list_cvo.add(defdocVO);
				}
			}
			map_list.put(x, list);
//			getChild(x, map_list, list_cvo);
			ArrayList<String> list_1 = map_list.get(1);
			for(int j=0;j<list_1.size();j++){
				DefdocVO vo = 	map_vo.get(list_1.get(j));
				DefaultMutableTreeNode son = new DefaultMutableTreeNode(vo);
				root.add(son);
				for (BclbHeaderVO bclbHeaderVO : bhvos) {
					if (vo.getPk_defdoc().equals(bclbHeaderVO.getBclbfl())) {
						DefaultMutableTreeNode grandson = new DefaultMutableTreeNode(bclbHeaderVO);
						son.add(grandson);
					}
				}
				genChild(son, map_list_b, vo.getPrimaryKey(), bhvos);
			}
			
			
			
//			for (DefdocVO defdocVO : defdocVOs) {
//				DefaultMutableTreeNode son = new DefaultMutableTreeNode(defdocVO);
//				root.add(son);
//				for (BclbHeaderVO bclbHeaderVO : bhvos) {
//					if (defdocVO.getPk_defdoc().equals(bclbHeaderVO.getBclbfl())) {
//						DefaultMutableTreeNode grandson = new DefaultMutableTreeNode(bclbHeaderVO);
//						son.add(grandson);
//					}
//				}
//			}
		}
		
		//根节点下的树
		for (BclbHeaderVO bclbHeaderVO : bhvos) {
			if (bclbHeaderVO.getBclbfl() == null) {
				DefaultMutableTreeNode son = new DefaultMutableTreeNode(bclbHeaderVO);
				root.add(son);
			}
			
			if (bclbHeaderVO.getDefaultFlag()!=null && bclbHeaderVO.getDefaultFlag().booleanValue()){
				defaultBclbHeaderVO = bclbHeaderVO;
			}
		}
		return new DefaultTreeModel(root);
	}
	
	//同上个方法，只更改参数sql
	private TreeModel getTreeModel(String sql) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ResHelper.getString("6017010417","UPT6017010417-000001")//@res "班别"
);
		IDefdoc iIDefdoc = (IDefdoc) NCLocator.getInstance().lookup(IDefdoc.class.getName());
		DefdocVO[] defdocVOs = null;
		BclbHeaderVO[] bhvos = null;
		try {
			//sealflag is null or sealflag <>'Y'解决班别分类无法显示的问题---BD_DEFDOC 查询班别分类
			defdocVOs = iIDefdoc.queryByWhere(" pk_defdoclist='0001Z71000000000IA6M' and pk_corp='"+Global.getCorpPK()+"' and (sealflag is null or sealflag <>'Y') ");
			bhvos = TADelegator.getBclb029().queryBclb029AllBclbHeader(getUnitCode(), getFactoryCode(),sql);
			
		} catch (BusinessException e) {
			handleException(e);
		}
		if (defdocVOs != null) {
			//班别分类下的树
			for (DefdocVO defdocVO : defdocVOs) {
				DefaultMutableTreeNode son = new DefaultMutableTreeNode(defdocVO);
				root.add(son);
				for (BclbHeaderVO bclbHeaderVO : bhvos) {
					if (defdocVO.getPk_defdoc().equals(bclbHeaderVO.getBclbfl())) {
						DefaultMutableTreeNode grandson = new DefaultMutableTreeNode(bclbHeaderVO);
						son.add(grandson);
					}
				}
			}
		}
		//根节点下的树
		for (BclbHeaderVO bclbHeaderVO : bhvos) {
			if (bclbHeaderVO.getBclbfl() == null) {
				DefaultMutableTreeNode son = new DefaultMutableTreeNode(bclbHeaderVO);
				root.add(son);
			}
			
			if (bclbHeaderVO.getDefaultFlag()!=null && bclbHeaderVO.getDefaultFlag().booleanValue()){
				defaultBclbHeaderVO = bclbHeaderVO;
			}
		}
		return new DefaultTreeModel(root);
	}

	private UIScrollPane getBclbPane() throws BusinessException{
		if (bclbPane == null) {
			bclbPane = new UIScrollPane();
			bclbPane.setName("bclbPane");
			bclbPane.setBounds(0, 0, 264, 495);
			bclbPane.setBorder(LineBorder.createGrayLineBorder());
			bclbPane.setViewportView(getBclb());
			bclbPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return bclbPane;
	}

	private UIDialog getCopyDialog() {
		if (copyDialog == null) {
			//copyDialog = new CopyDialog(this,"班别复制");
		}
		return copyDialog;
	}

	/**
	 * 返回 EditPanelUILabelLayout 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabelLayout
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabelLayout getEditPanelUILabelLayout() {
		nc.ui.pub.beans.UILabelLayout ivjEditPanelUILabelLayout = null;
		try {
			/* 创建部件 */
			ivjEditPanelUILabelLayout = new nc.ui.pub.beans.UILabelLayout();
			ivjEditPanelUILabelLayout.setRows(4);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		;
		return ivjEditPanelUILabelLayout;
	}

	/**
	 * 返回 TfGzsj 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfGzsj() {
		if (ivjTfGzsj == null) {
			try {
				ivjTfGzsj = new nc.ui.pub.beans.UITextField();
				ivjTfGzsj.setName("TfGzsj");
				ivjTfGzsj.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfGzsj.setBounds(360, 90, 100, 22);
				//ivjTfGzsj.setBounds(360, 190, 90, 22);
				ivjTfGzsj.setTextType(UITextType.TextDbl);
				// user code begin {1}
				ivjTfGzsj.setNumPoint(2);
				ivjTfGzsj.setMinValue(0);
				//ivjTfGzsj.setMaxValue(24);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfGzsj;
	}

	/**
	 * 返回 TfLbbm 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfLbbm() {
		if (ivjTfLbbm == null) {
			try {
				ivjTfLbbm = new nc.ui.pub.beans.UITextField();
				ivjTfLbbm.setName("TfLbbm");
				ivjTfLbbm.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfLbbm.setBounds(118, 16, 120, 20);
				// user code begin {1}
				ivjTfLbbm.setMaxLength(10);
				ivjTfLbbm.setDelStr(nc.vo.bd.mmpub.MMConstant.RESTICT_STRING);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfLbbm;
	}

	/**
	 * 返回 TfLbmc 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfLbmc() {
		if (ivjTfLbmc == null) {
			try {
				ivjTfLbmc = new nc.ui.pub.beans.UITextField();
				ivjTfLbmc.setName("TfLbmc");
				ivjTfLbmc.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfLbmc.setBounds(118, 43, 120, 20);
				ivjTfLbmc.setMinimumSize(new java.awt.Dimension(4, 20));
				// user code begin {1}
				ivjTfLbmc.setMaxLength(50);
				ivjTfLbmc.setDelStr(nc.vo.bd.mmpub.MMConstant.RESTICT_STRING);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfLbmc;
	}
	
	/**
	 * 刘鹏飞新增，班次说明ui
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbMemo() {
		if (ivjLbMemo == null) {
			try {
				ivjLbMemo = new nc.ui.pub.beans.UILabel();
				ivjLbMemo.setName("LbMemo");
				ivjLbMemo.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbMemo.setText("班次说明"/* @res "夜班开始时间" */);
				ivjLbMemo.setBounds(38, 114, 74, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbMemo;
	}

	
	/**刘鹏飞新增，班次说明
	 * 返回 TfMemo 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfMemo() {
		if (ivjTfMemo == null) {
			try {
				ivjTfMemo = new nc.ui.pub.beans.UITextField();
				ivjTfMemo.setName("TfMemo");
				ivjTfMemo.setPreferredSize(new java.awt.Dimension(80, 20));
				//ivjTfMemo.setBounds(118, 43, 120, 20);
				ivjTfMemo.setBounds(118, 114, 240, 20);
				ivjTfMemo.setMinimumSize(new java.awt.Dimension(4, 20));
				// user code begin {1}
				ivjTfMemo.setMaxLength(512);
				ivjTfMemo.setDelStr(nc.vo.bd.mmpub.MMConstant.RESTICT_STRING);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfMemo;
	}

	/**
	 * 子类实现该方法，返回业务界面的标题。
	 *
	 * @version (00-6-6 13:33:25)
	 *
	 * @return java.lang.String
	 */
	public String getTitle() {
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113",
				"UPP6017010113-000051")/* @res "班别定义" */;
	}

	/**
	 * 返回 UILabel1 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel1() {
		if (ivjUILabel1 == null) {
			try {
				ivjUILabel1 = new nc.ui.pub.beans.UILabel();
				ivjUILabel1.setName("UILabel1");
				ivjUILabel1.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjUILabel1.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("6017010113","UPP6017010113-000004")/* @res"班别编码"*/);//("common", "UC000-0003134")/* @res "类别编码" */);
				ivjUILabel1.setBounds(38, 16, 80, 22);
				// user code begin {1}
				ivjUILabel1.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel1;
	}

	/**
	 * 返回 UILabel2 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel2() {
		if (ivjUILabel2 == null) {
			try {
				ivjUILabel2 = new nc.ui.pub.beans.UILabel();
				ivjUILabel2.setName("UILabel2");
				ivjUILabel2.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjUILabel2.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("6017010113","UPP6017010113-000003")/* @res"班别名称"*/);//("common", "UC000-0003131")/* @res "类别名称" */);
				ivjUILabel2.setBounds(38, 43, 80, 22);
				// user code begin {1}
				ivjUILabel2.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel2;
	}

	//新增刘鹏飞 是否作废
	private nc.ui.pub.beans.UILabel getUILabelSfzf() {
		if (ivjUILabelSfzf == null) {
			try {
				ivjUILabelSfzf = new nc.ui.pub.beans.UILabel();
				ivjUILabelSfzf.setName("ivjUILabelSfzf");
				ivjUILabelSfzf.setPreferredSize(new java.awt.Dimension(80, 22));
				
				//ivjUILabelSfzf.setText(nc.ui.ml.NCLangRes.getInstance()
					//	.getStrByID("6017010113", "UPP6017010113-000134") /*@res "是否默认班别"*/ );
				
				ivjUILabelSfzf.setText("是否作废");
				ivjUILabelSfzf.setBounds(280, 43, 80, 22);
				// user code begin {1}
				ivjUILabelSfzf.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabelSfzf;
	}
	
	private nc.ui.pub.beans.UILabel getUILabelSfmrbb() {
		if (ivjUILabelSfmrbb == null) {
			try {
				ivjUILabelSfmrbb = new nc.ui.pub.beans.UILabel();
				ivjUILabelSfmrbb.setName("ivjUILabelSfmrbb");
				ivjUILabelSfmrbb.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjUILabelSfmrbb.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("6017010113", "UPP6017010113-000134") /*@res "是否默认班别"*/ );
				ivjUILabelSfmrbb.setBounds(280, 43, 80, 22);
				// user code begin {1}
				ivjUILabelSfmrbb.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabelSfmrbb;
	}
	//刘鹏飞新增，是否作废
	private nc.ui.pub.beans.UICheckBox getCkbSfzf() {
		if (ivjCkbSfzf == null) {
			try {
				ivjCkbSfzf = new nc.ui.pub.beans.UICheckBox();
				ivjCkbSfzf.setName("ivjCkbSfzf");
				ivjCkbSfzf.setPreferredSize(new java.awt.Dimension(20, 22));
				ivjCkbSfzf.setText("");
				ivjCkbSfzf.setBounds(360, 43, 20, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCkbSfzf;
	}
	private nc.ui.pub.beans.UICheckBox getCkbSfmrbb() {
		if (ivjCkbSfmrbb == null) {
			try {
				ivjCkbSfmrbb = new nc.ui.pub.beans.UICheckBox();
				ivjCkbSfmrbb.setName("ivjCkbSfmrbb");
				ivjCkbSfmrbb.setPreferredSize(new java.awt.Dimension(20, 22));
				ivjCkbSfmrbb.setText("");
				ivjCkbSfmrbb.setBounds(360, 43, 20, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCkbSfmrbb;
	}

	/**
	 * 返回 UILabel3 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel3() {
		if (ivjUILabel3 == null) {
			try {
				ivjUILabel3 = new nc.ui.pub.beans.UILabel();
				ivjUILabel3.setName("UILabel3");
				ivjUILabel3.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjUILabel3
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000043")/* @res "工作时长" */);
				//ivjUILabel3.setBounds(280, 190, 80, 22);
				ivjUILabel3.setBounds(280, 90, 100, 22);
				
				// user code begin {1}
				ivjUILabel3.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel3;
	}

	/**
	 * 返回 UILabel3 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel4() {
		if (ivjUILabel4 == null) {
			try {
				ivjUILabel4 = new nc.ui.pub.beans.UILabel();
				ivjUILabel4.setName("UILabel4");
				ivjUILabel4.setText(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("common", "UC000-0001687")/* @res "工厂编码" */);
				ivjUILabel4.setLocation(4, 5);
				ivjUILabel4.setVisible(true);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel4;
	}

	/**
	 * 返回 UIPanel1 特性值。
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIPanel1() {
		if (ivjUIPanel1 == null) {
			try {
				ivjUIPanel1 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel1.setName("UIPanel1");
				ivjUIPanel1.setLayout(new java.awt.BorderLayout());
				ivjUIPanel1.setBounds(264, 0, 510, 495);
				//chexz20070109
				//JScrollPane scrollpane = new JScrollPane(getWtTablePane());
				//scrollpane.setBorder(new nc.ui.pub.beans.border.UITitledBorder(""));
				//chexz_end
				getUIPanel1().add(getWtTablePane(), "Center");
				getUIPanel1().add(getEditPanel(), "North");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIPanel1;
	}

	/**
	 * 返回 WtModel 特性值。
	 *
	 * @return nc.ui.pd.pd1020.WtTableModel
	 */
	/* 警告：此方法将重新生成。 */
	private WtTableModel getWtModel() {
		// user code begin {1}
		if (ivjWtModel == null) {
			for (int i = 0; i < WtTableModel.WTNUM; i++) {
				BclbItemVO btvo = new BclbItemVO();
				btvo.setPk_corp(getUnitCode());
				btvo.setGcbm(getFactoryCode());
				btvo.setWtbeginday(new Integer(0));
				btvo.setWtendday(new Integer(0));
				
				btvos[i] = btvo;
			}
			ivjWtModel = new WtTableModel(btvos);

		}
		// user code end
		return ivjWtModel;
	}

	/**
	 * 返回 WtTablePane 特性值。
	 *
	 * @return nc.ui.pub.beans.UITablePane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITablePane getWtTablePane() {
		if (ivjWtTablePane == null) {
			try {
				ivjWtTablePane = new nc.ui.pub.beans.UITablePane();
				ivjWtTablePane.setName("WtTablePane");
				// user code begin {1}
				ivjWtTablePane.setBorder(new nc.ui.pub.beans.border.UITitledBorder(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000006")/* @res "作息时间列表"*/));
				getWtModel().setTable(ivjWtTablePane.getTable());
				ivjWtTablePane.getTable().setModel(getWtModel());
				getWtModel().setTable();

				javax.swing.table.TableColumnModel tcm = getWtTablePane().getTable().getColumnModel();
				GroupableTableHeader header = (nc.ui.pub.beans.table.GroupableTableHeader) getWtTablePane().getTable().getTableHeader();

				// 设置输入格式只能是时间格式
				nc.ui.pub.beans.UITextField tfTime = new nc.ui.pub.beans.UITextField();
				tfTime.setTextType("TextTime");
				nc.ui.bd.manage.UIRefCellEditor ure = new nc.ui.bd.manage.UIRefCellEditor(tfTime);
				tcm.getColumn(2).setCellEditor(ure);
				tcm.getColumn(4).setCellEditor(ure);

				//nc.ui.pub.beans.UITextField wtresttime = new nc.ui.pub.beans.UITextField();
				//wtresttime.setTextType(UITextType.TextInt);
				//wtresttime.setMinValue(0);
				//wtresttime.setMaxValue(1440);
				//wtresttime.setText("0");
				//nc.ui.bd.manage.UIRefCellEditor ure2 = new nc.ui.bd.manage.UIRefCellEditor(wtresttime);
				//tcm.getColumn(7).setCellEditor(ure2);


				ColumnGroup cg1 =new ColumnGroup(ResHelper.getString("6017","UPP6017-000336")//@res "开始"
);;
				ColumnGroup cg2 =new ColumnGroup(ResHelper.getString("6017","UPP6017-000337")//@res "结束"
);
				cg1.add(tcm.getColumn(1));
				cg1.add(tcm.getColumn(2));
				cg2.add(tcm.getColumn(3));
				cg2.add(tcm.getColumn(4));
				header.addColumnGroup(cg1);
				header.addColumnGroup(cg2);

				getWtTablePane().getTable().getColumnModel().getColumn(0).setPreferredWidth(40);

//				nc.ui.pub.beans.UITextField tfTime1 = new nc.ui.pub.beans.UITextField();
//				tfTime1.setHorizontalAlignment(0);
//				nc.ui.bd.manage.UIRefCellEditor ure1= new nc.ui.bd.manage.UIRefCellEditor(
//						tfTime1);
//				getWtTablePane().getTable().getColumnModel().getColumn(0)
//				.setCellEditor(ure1);
// getWtTablePane().getTable().getColumnModel().getColumn(3)
// .setCellEditor(ure);
//				getWtTablePane().getTable().getColumnModel().getColumn(4)
//				.setCellEditor(ure);

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjWtTablePane;
	}

	/**
	 * 得到作息时间表VO[]。 创建日期：(2001-06-12 19:31:06)
	 *
	 * @return nc.vo.pd.pd1020.BclbItemVO[]
	 */
	private BclbItemVO[] getWtVos() {
		nc.vo.pub.ValueObject[] vos = getWtModel().getVOArray();
		int vlen = vos.length;
		BclbItemVO[] bvos = new BclbItemVO[vlen];
		int idx = 0;
		for (int i = 0; i < vlen; i++) {
			BclbItemVO bvo = (BclbItemVO) vos[i];
			bvo.setTimeid(new Integer(i + 1));
			bvo.setWtbeginday(bvo.getWtbeginday()==null?0:bvo.getWtbeginday());
			bvo.setWtendday(bvo.getWtendday()==null?0:bvo.getWtendday());
			bvos[i] = bvo;
		}
		return bvos;
	}

	/**
	 * 每当部件抛出异常时被调用
	 *
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* 除去下列各行的注释，以将未捕捉到的异常打印至 stdout。 */
		Debug.error(ResHelper.getString("6017","UPP6017-001041")//@res "--------- 未捕捉到的异常 ---------"
);
		exception.printStackTrace(System.out);
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-05-28 10:10:04)
	 */
	private void initConnection() {

		getBclbTablePane().getTable().getSelectionModel()
				.addListSelectionListener(this);

		m_dataSource = new DataSource(this);
		m_print = new PrintEntry(null, null);
	}

	/**
	 * 初始化数据。 创建日期：(2001-05-27 13:47:55)
	 */
	private void initData() {
		try {
			//tzj getRfpFactory().setPK(getFactoryCode());

			//BclbHeaderVO[] bhvos = TADelegator.getBclb029().queryBclb029AllBclbHeader(
			//		getUnitCode(), getFactoryCode());

			//getBclbModel().clearTable();

			//getBclbModel().addVO(bhvos);
			getBclb().removeTreeSelectionListener(this);
			//DefaultTreeModel treeModel = (DefaultTreeModel) getBclb().getModel();
			//treeModel.reload();
			getBclb().setModel(getTreeModel());
			getBclb().addTreeSelectionListener(this);
			clearBclb();
			setState(0);

		} catch (Exception e) {
			reportException(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000052")/* @res "读取数据库出错：" */+ e.getMessage());
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000053")/* @res "读取数据库失败" */);
		}
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("ClientUI");
			setLayout(new java.awt.BorderLayout());
			setSize(774, 419);
			add(getContentPanel(), "Center");
			add(getHeadPanel(), "North");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		initConnection();
		setButtons(aryButtonGroup);
		initData();
		boDel.setEnabled(false);
		boModify.setEnabled(false);

		setState(0);
		getCkbSfyb().addActionListener(this);
		getCkbSFFC().addActionListener(this);

		//刘鹏飞新增 2005-09-22 Start
		
		getTfKqkssj().addFocusListener(
				new java.awt.event.FocusAdapter(){
					public   void   focusLost(FocusEvent   e)   {
						jTextField1_focusLost(e);
					}
				});

		getTfKqjssj().addFocusListener(
				new java.awt.event.FocusAdapter(){
					public   void   focusLost(FocusEvent   e)   {
						jTextField1_focusLost(e);
					}
				});
		
		//刘鹏飞新增 2005-09-22 Start
	
		/**
		 *  Modified by Young 2005-09-22 Start
		 */
		getUIChkbovertmrule().addActionListener(this);
		getUIChkbontmrule().addActionListener(this);

		/**
		 *  Modified by Young 2005-09-22 End
		 */


		/**
		 *  Added by Young 2005-07-19  Start
		 */
		initTimeruleVO();

		getWtModel().setTimeruleVO(this.ruleVO);
		/**
		 *  Adde by Young 2005-07-19  End
		 */

		// user code end
	}
	
	//刘鹏飞新增方法 ：自动复制用
	void   jTextField1_focusLost(FocusEvent   e)   {
		
		BclbItemVO[] bivos = getWtVos();
		if (e.getSource() == getTfKqkssj() ) {//开始时间
		
			if(bivos[0].getKssj() == null ||bivos[0].getKssj().equals("")){
				bivos[0].setWtbeginday((Integer)getChbKqkssj().getSelectdItemValue());
				UFTime uftiem = new UFTime(getTfKqkssj().getText());
				bivos[0].setKssj(uftiem.toString());
				this.getWtModel().refreshTable();
			}
		}	
		if( e.getSource() == getTfKqjssj()){	
			if(bivos[0].getJssj() == null ||bivos[0].getJssj().equals("")){
				bivos[0].setWtendday((Integer)getChbKqjssj().getSelectdItemValue());
				UFTime uftiem = new UFTime(getTfKqjssj().getText());
				bivos[0].setJssj(uftiem.toString());
				this.getWtModel().refreshTable();
			}
		}
	} 

	/**
	 * 主入口点 - 当部件作为应用程序运行时，启动这个部件。
	 *
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			ClientUI aClientUI;
			aClientUI = new ClientUI();
			frame.setContentPane(aClientUI);
			frame.setSize(aClientUI.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.show();
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame
					.getHeight()
					+ insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("nc.ui.mm.pub.MMToftPanel 的 main() 中发生异常");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * 增加按钮响应事件
	 */
	public void onAdd() {

		//清除右侧班次类别设置
		clearBclb();
		getTfallowearly().setText("0");
		getTfallowlate().setText("0");
		getTflargeearly().setText("60");
		getTflargelate().setText("60");
		// 修改状态
		setState(1);

		/**
		 *   Added by Young 2005-07-22  Start
		 */
		getWtModel().setAddRow(0);
		getWtModel().setDataVOs(null);
		/**
		 *   Added by Young 2005-07-22  End
		 */

		getTfLbbm().requestFocus();
		DefdocVO defdocVO = new DefdocVO();
		if(getBclb().getSelectionPath()!=null&&!(((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof String)) {
			if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
				defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
			}else if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
				defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject();
			}
		}
		getBbflRef().setPK(defdocVO.getPk_defdoc());

	}

	/**
	 * 子类实现该方法，响应按钮事件。
	 *
	 * @version (00-6-1 10:32:59)
	 *
	 * @param bo
	 *            ButtonObject
	 */
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {
		try {
			String suffix = null;
			if (bo == boAdd) {
				onAdd();
				suffix = ButtonTipMessage.ING;

			}
			if (bo == boDel) {
				onDel();
				suffix = ButtonTipMessage.SUCCESSED;

			}
			if (bo == boConfirm) {
				onConfirm();
				suffix = ButtonTipMessage.SUCCESSED;
			}
			if (bo == boCancel) {
				onCancel();

			}
			if (bo == boModify) {
				onModify();
				suffix = ButtonTipMessage.ING;
			}
			if (bo == boFlash) {
				onRefresh();

			}
			if (bo == boPrint) {
				onPrint();
				suffix = ButtonTipMessage.SUCCESSED;
			}
			if (bo == boCopy) {
				onCopy();
			}
			if (bo == boQuery){
				onQuery();
			}
			if (bo == boImport){
				onImport();
			}
			if (suffix != null) {
				showHintMessage(bo.getName() + suffix);
			}else{
				showHintMessage("");
			}
		} catch (nc.vo.pub.BusinessRuntimeException e) {
			nc.vo.logging.Debug.error(e.getMessage(), e);
			nc.ui.pub.beans.MessageDialog.showErrorDlg(this, null, e.getMessage());
		} catch (Exception e) {
			nc.vo.logging.Debug.error(e.getMessage(), e);
			nc.ui.pub.beans.MessageDialog.showUnknownErrorDlg(this, e);
		}
	}

	private void onCopy() {
		List<WizardStep> list = new ArrayList<WizardStep>();
		list.add(new PreCopyWizardStep());
		list.add(new CopyWizardStep());
		WizardDialog wizardDialog = new WizardDialog(this,new CopyWizardModel(),list,null);
		wizardDialog.setSize(650, 500);
		wizardDialog.setWizardDialogListener(new CopyWizardDialogListener());
		wizardDialog.setWizardExceptionHandler(new WizardExceptionHandler(wizardDialog));
		if (wizardDialog.showModal() == UIDialog.ID_OK) {
			onRefresh();
			showHintMessage(ResHelper.getString("6017","UPP6017-001298")//@res "复制成功！"
);
		}
	}

	/**
	 * 取消按钮响应事件
	 */
	public void onCancel() {

		if (state == 0)
			return;

		setState(0); // 浏览状态

		try {
			//getBclbTablePane().getTable().getSelectionModel()
			//		.setSelectionInterval(mrow, mrow);
			if (getBclb().getSelectionPath()==null) {
				return;
			}
			if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof BclbHeaderVO) {

				BclbHeaderVO bhvo = (BclbHeaderVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
				getTfLbbm().setText(bhvo.getLbbm());
				getTfLbmc().setText(bhvo.getLbmc());
				getCkbbbjc().setText(bhvo.getLbjc());
		         getddflRef().setPK(bhvo.getPk_dd());
		         getbbmbRef().setPK(bhvo.getPk_bbmb());
		         getbbzflRef().setPK(bhvo.getPk_bbz());
				getTfMemo().setText(bhvo.getMemo());
				getTfGzsj().setText(bhvo.getGzsj().toString());
				if (bhvo.getAllowearly() != null)
					getTfallowearly().setText(
							bhvo.getAllowearly().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTfallowearly().setText("");

				if (bhvo.getAllowlate() != null)
					getTfallowlate().setText(
							bhvo.getAllowlate().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTfallowlate().setText("");

				if (bhvo.getLargelate() != null)
					getTflargelate().setText(
							bhvo.getLargelate().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTflargelate().setText("");

				if (bhvo.getLargeearly() != null)
					getTflargeearly().setText(
							bhvo.getLargeearly().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTflargeearly().setText("");

				if (bhvo.getIsautokg() == null
						|| bhvo.getIsautokg().trim().equals("Y")) {
					getrbButton_Auto().setSelected(true);
					getTfkgsc().setText(
							bhvo.getKghours() == null ? "" : bhvo.getKghours()
									.toString());
				} else {
					getrbButton_clz().setSelected(false);
					getTfkgsc().setText(
							bhvo.getKghours() == null ? "" : bhvo.getKghours()
									.toString());
				}

				getTfKqkssj().setText(bhvo.getTimebegintime());
				getTfKqjssj().setText(bhvo.getTimeendtime());
				getTfYbkssj().setText(bhvo.getNightbegintime());
				getTfYbjssj().setText(bhvo.getNightendtime());
				//5.5getCkbKqsfkt().setSelected(bhvo.getTimenextdayend() == null ? false : bhvo.getTimenextdayend().booleanValue());

				//add5.5
				getChbKqkssj().setSelectedItem(bhvo.getTimebeginday());
				getChbKqjssj().setSelectedItem(bhvo.getTimeendday());
				getChbYbkssj().setSelectedItem(bhvo.getNightbeginday());
				getChbYbjssj().setSelectedItem(bhvo.getNightendday());
				getCkbsfyxwc().setSelected(bhvo.getIsallowout().booleanValue());


				getCkbSfmrbb().setSelected(
						bhvo.getDefaultFlag() == null ? false : bhvo
								.getDefaultFlag().booleanValue());
				
				getCkbSfzf().setSelected(
						bhvo.getIscancel() == null ? false : bhvo
								.getIscancel().booleanValue());
				
				//5.5getCkbYbsfkt().setSelected(bhvo.getNightnextdayend() == null ? false : bhvo.getNightnextdayend().booleanValue());
				getCkbSfyb().setSelected(
						bhvo.getIncludenightshift() == null ? false : bhvo
								.getIncludenightshift().booleanValue());


				/**
				 *  Modified by Young 2005-09-22 Start
				 */
				if (bhvo.getUseovertmrule() != null && bhvo.getUseovertmrule().booleanValue() ){
					getUIChkbovertmrule().setSelected(true);
					getUITFovertmrule().setText(bhvo.getOvertmeffectbeyond().toString());
					getUITFovertmrule2().setText(bhvo.getOvertmbeyond().toString());
					getUIRefPane1overtype().setPK(bhvo.getOvertmruletype());
				} else
				{
					getUIChkbovertmrule().setSelected(false);
					getUITFovertmrule().setText("");
					getUITFovertmrule2().setText("");
					getUIRefPane1overtype().setPK(null);
				}
				//add by myl
				if (bhvo.getUseontmrule() != null && bhvo.getUseontmrule().booleanValue() ){
					getUIChkbontmrule().setSelected(true);
					getUITFontmrule().setText(bhvo.getOntmeffectbeyond().toString());
					getUITFontmrule2().setText(bhvo.getOntmbeyond().toString());
					getUIRefPane1ontype().setPK(bhvo.getOntmruletype());
				} else
				{
					getUIChkbontmrule().setSelected(false);
					getUITFontmrule().setText("");
					getUITFontmrule2().setText("");
					getUIRefPane1ontype().setPK(null);
				}
				/**
				 *  Modified by Young 2005-09-22 End
				 */
				//add by caizl
				getBbflRef().setPK(bhvo.getBclbfl());

				// 查询处作息时间列表数据
				BclbItemVO[] tmpbtvs = TADelegator.getBclb029().queryAllBclbItemBclb029(bhvo);

				getWtModel().setDataVOs(tmpbtvs);

				sort3(tmpbtvs);

				BclbItemVO[] btvs = new BclbItemVO[4];
				for (int i = 0; i < 4; i++) {
					if (i > tmpbtvs.length - 1) {
						BclbItemVO btvo = new BclbItemVO();
						btvo.setPk_corp(getUnitCode());
						btvo.setGcbm(getFactoryCode());
						btvs[i] = btvo;
					} else {
						btvs[i] = tmpbtvs[i];

					}
				}
				getWtTablePane().getTable().editingStopped(null);
				getWtModel().clearTable();
				getWtModel().addVO(btvs);
				boModify.setEnabled(true);
				boDel.setEnabled(true);
				updateButton(boModify);
				updateButton(boDel);
			} else {
				clearBclb();
			}

			/**
			 *  Added by Young 2005-07-20  Start
			 */
			getWtModel().setAddRow(0);
			/**
			 *  Added by Young 2005-07-20  End
			 */

		} catch (Exception e) {
			reportException(e);
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000053")/* @res "读取数据库失败" */);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000052")/* @res "读取数据库出错：" */
					+ e.getMessage());
		}
		//	getBclbTablePane().getTable().grabFocus();
		//getBclbTablePane().getTable().paintImmediately(0, 0,
		//		getBclbTablePane().getTable().getSize().width,
		//		getBclbTablePane().getTable().getSize().height);
	}

	/**
	 * 保存按钮响应事件
	 */

	public boolean onConfirm() {

		showHintMessage("");

		getWtTablePane().getTable().editingStopped(null);

		try {
			if (state == 0) //0：浏览 1：增加 2：修改
				return false;
			if (checkData() < 0) {
				showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000054")/* @res "数据不合法！" */);
				return false;
			}
			String lbbm = getTfLbbm().getText();
			String lbmc = getTfLbmc().getText();
			String gzsj = getTfGzsj().getText();
			String memo = getTfMemo().getText();
			int sfkq = 1;

			if (state == 1) { //0：浏览 1：增加 2：修改

				BclbHeaderVO newvo = new BclbHeaderVO();
				newvo.setPk_corp(getUnitCode());
				newvo.setGcbm(getFactoryCode());
				newvo.setLbbm(lbbm);
				newvo.setLbmc(lbmc);
			
				newvo.setLbjc(getCkbbbjc().getText());
				newvo.setPk_bbz(getbbzflRef().getRefPK());
				newvo.setPk_dd(getddflRef().getRefPK());
				newvo.setPk_bbmb(getbbmbRef().getRefPK());
				newvo.setMemo(memo);
				newvo.setGzsj(new nc.vo.pub.lang.UFDouble(gzsj));
				newvo.setSfkq(Integer.valueOf(sfkq));
				newvo.setTimebegintime(getTfKqkssj().getText());
				newvo.setTimeendtime(getTfKqjssj().getText());
				newvo.setTimebeginday((Integer)getChbKqkssj().getSelectdItemValue());
				newvo.setTimeendday((Integer)getChbKqjssj().getSelectdItemValue());
				//是否夜班
				newvo.setIncludenightshift(UFBoolean.valueOf(getCkbSfyb().isSelected()));
				//夜班开始时间
				newvo.setNightbegintime(getTfYbkssj().getText());
				//夜班结束时间
				newvo.setNightendtime(getTfYbjssj().getText());
				newvo.setNightbeginday((Integer)getChbYbkssj().getSelectdItemValue());
				newvo.setNightendday((Integer)getChbYbjssj().getSelectdItemValue());
				newvo.setIsallowout(UFBoolean.valueOf(getCkbsfyxwc().isSelected()));
				newvo.setDefaultFlag(UFBoolean.valueOf(getCkbSfmrbb().isSelected()));
				
				newvo.setIscancel(UFBoolean.valueOf(getCkbSfzf().isSelected()));
				newvo.setAllowearly(new UFDouble(getTfallowearly().getText()));
				newvo.setAllowlate(new UFDouble(getTfallowlate().getText()));
				newvo.setLargelate(new UFDouble(getTflargelate().getText()));
				newvo.setLargeearly(new UFDouble(getTflargeearly().getText()));
				newvo.setKghours(new UFDouble(getTfkgsc().getText()));
				newvo.setIsblocked(UFBoolean.valueOf(getCkbSFFC().isSelected()));

				//校验默认班别不能封存
				if ((newvo.getDefaultFlag()==null?false:newvo.getDefaultFlag().booleanValue()) && (newvo.getIsblocked()==null?false:newvo.getIsblocked().booleanValue())){
					showErrorMessage(ResHelper.getString("6017","UPP6017-000920")/*"默认班别不能封存!"*/);
					return false;
				}

				if (getrbButton_Auto().isSelected())
					newvo.setIsautokg("Y");
				else
					newvo.setIsautokg("N");

				/**
				 *  Modified by Young 2005-10-12 Start
				 */
				//  下班加班
	   			if (getUIChkbovertmrule().isSelected()) {
					newvo.setUseovertmrule(UFBoolean.TRUE);
					if (getUITFovertmrule().getText() != null && getUITFovertmrule().getText().trim().length() != 0) {
		 				newvo.setOvertmeffectbeyond(new UFDouble(getUITFovertmrule().getText().trim()));
					}
					if (getUITFovertmrule2().getText() != null && getUITFovertmrule2().getText().trim().length() != 0) {
						newvo.setOvertmbeyond(new UFDouble(getUITFovertmrule2().getText().trim()));
					}
					if (getUIRefPane1overtype().getRefPK() != null && getUIRefPane1overtype().getRefPK().trim().length() != 0) {
		 				newvo.setOvertmruletype(getUIRefPane1overtype().getRefPK().trim());
					}
	   			}

	   			// 提前加班
	   			if (getUIChkbontmrule().isSelected()) {
					newvo.setUseontmrule(UFBoolean.TRUE);
					if (getUITFontmrule().getText() != null && getUITFontmrule().getText().trim().length() != 0) {
		 				newvo.setOntmeffectbeyond(new UFDouble(getUITFontmrule().getText().trim()));
					}
					if (getUITFontmrule2().getText() != null && getUITFontmrule2().getText().trim().length() != 0) {
						newvo.setOntmbeyond(new UFDouble(getUITFontmrule2().getText().trim()));
					}
					if (getUIRefPane1ontype().getRefPK() != null && getUIRefPane1ontype().getRefPK().trim().length() != 0) {
		 				newvo.setOntmruletype(getUIRefPane1ontype().getRefPK().trim());
					}
	   			}
	   			//add by caizl
	   			newvo.setBclbfl(getBbflRef().getRefPK());
	   			if (StringUtils.isNotEmpty(newvo.getBclbfl())) {
	   				nc.vo.bd.defref.DefdocVO[] defdocVOs = (nc.vo.bd.defref.DefdocVO[]) PubDelegator.getIPersistenceRetrieve().retrieveByClause(null, nc.vo.bd.defref.DefdocVO.class, "pk_defdoc='"+newvo.getBclbfl()+"'");
	   				if (defdocVOs == null || defdocVOs.length <= 0) {
	   					showErrorMessage(ResHelper.getString("6017", "UPP6017-001428"));//"班别分类已不存在，请刷新班别分类参照后再操作！"
	   					return false;
	   				}
	   			}
	   			/*DefdocVO defdocVO;
	   			if(!(((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof String)) {
		   			if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
		   				defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
		   				newvo.setBclbfl(defdocVO.getPk_defdoc());
					}else if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
						defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject();
		   				newvo.setBclbfl(defdocVO.getPk_defdoc());
					}
	   			}*/
	   			BclbVO bvo = new BclbVO();
				bvo.setParentVO(newvo);

				BclbItemVO[] items = getWtVos();

				Vector<BclbItemVO> dataVOs = new Vector<BclbItemVO>();
				for (int i=0; i<items.length; i++){
					if ((items[i].getKssj()!=null) && (items[i].getKssj().trim().length()>0)){
						dataVOs.add(items[i]);
					}
				}
				
				//新增 设置上班刷卡，下班不刷卡，最后一班上下班都要刷卡o
				for(int q = 0; q < dataVOs.size(); q++) {
					dataVOs.get(q).setCheckInFlag(new UFBoolean("Y"));
					dataVOs.get(q).setCheckoutFlag(new UFBoolean("N"));
					if(q == dataVOs.size() -1)
						dataVOs.get(q).setCheckoutFlag(new UFBoolean("Y"));
				}
				
				sort2(dataVOs);

				//设置中间时间点
				PubUISet.setMidTime(dataVOs, newvo.getTimeendtime(),newvo.getTimeendday());

				BclbItemVO[] items2 = new BclbItemVO[dataVOs.size()];
				dataVOs.copyInto(items2);

				bvo.setChildrenVO(items2);
				//计算工作时长
				if (!computeGzsj(newvo,items2)){
					return false;
				}

				getWtModel().setAddRow(0);
				String pk = TADelegator.getBclb029().insertBclb029(bvo);
				
				if (newvo.getDefaultFlag()!=null && newvo.getDefaultFlag().booleanValue()){
					defaultBclbHeaderVO.setDefaultFlag(UFBoolean.FALSE);
					defaultBclbHeaderVO = newvo;
				}

				showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000055")/* @res "增加完成" */);

				// 在左侧班次类别列表中加入新的VO
				newvo.setPk_bclbid(pk);

				//getBclb().removeTreeSelectionListener(this);
				getTfGzsj().setText(newvo.getGzsj().toString());
				//add by caizl start
				DefaultTreeModel treeModel = (DefaultTreeModel) getBclb().getModel();
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(newvo);
				DefaultMutableTreeNode parent;
				if (getBclb().getSelectionPath()==null) {
					//树不选中，默认为根节点
					parent = (DefaultMutableTreeNode) treeModel.getRoot();
					//如果班别分类有值，则找到相应树节点添加进去
					if (getBbflRef().getRefPK()!=null) {
						for (int i = 0; i < treeModel.getChildCount(treeModel.getRoot()); i++) {
							DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)treeModel.getChild(treeModel.getRoot(), i);
							if (getBbflRef().getRefPK().equals(((DefdocVO)childNode.getUserObject()).getPk_defdoc())) {
								parent = childNode;
								break;
							}
						}
					}
				} else {
					//树被选中，默认为选中的班别分类节点
					parent = (DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent());
					if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof BclbHeaderVO) {
						parent = (DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent());
					}
					DefdocVO defdocVO = new DefdocVO();
		   			if(!(((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof String)) {
			   			if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
			   				defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
						}else if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
							defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject();
						}
		   			}
		   			//如果当前选中树的班别分类和右侧班别分类的值不同，则查找相应节点
		   			if (getBbflRef().getRefPK()==null) {
		   				parent = (DefaultMutableTreeNode) treeModel.getRoot();
					}else if (getBbflRef().getRefPK()!=null&&!getBbflRef().getRefPK().equals(defdocVO.getPk_defdoc())) {
						for (int i = 0; i < treeModel.getChildCount(treeModel.getRoot()); i++) {
							DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)treeModel.getChild(treeModel.getRoot(), i);
							if (getBbflRef().getRefPK().equals(((DefdocVO)childNode.getUserObject()).getPk_defdoc())) {
								parent = childNode;
								break;
							}
						}
					}
				}
				treeModel.insertNodeInto(node, parent, parent.getChildCount());
				getBclb().setSelectionPath(new TreePath(treeModel.getPathToRoot(node)));
				getBclb().scrollPathToVisible(getBclb().getSelectionPath());
				//add by caizl end
				//getBclbModel().addVO(newvo);
				//onRefresh();
				//因为insert后表格排序可能会发生变化,因此需要重新定位选择的行
				//ValueObject[] vos = getBclbModel().getVOArray();
				//for(int i = 0;i < vos.length;i++){
				//    BclbHeaderVO vo = (BclbHeaderVO)vos[i];
				//    if(pk.equals(vo.getPrimaryKey())){
				//        mrow = i;
				//       break;
				//    }
				//}
				setState(0);
				//getBclbTablePane().getTable().getSelectionModel().setSelectionInterval(mrow, mrow);
				boModify.setEnabled(true);
				boDel.setEnabled(true);
				updateButton(boModify);
				updateButton(boDel);
				//刘鹏飞新增，解决新增后不能修改第三条的问题
				getWtModel().setDataVOs(items2);
				return true;
			}

			if (state == 2) { //0：浏览 1：增加 2：修改

				BclbHeaderVO newvo = (BclbHeaderVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();


				//int isInUse = TADelegator.getBclb029().isInUseBclb029(bhvo);
				boolean isPsncalendarClear = false;
				UFDate[] maxMinDates = TADelegator.getPsncalendar().queryDateByBclb(newvo.getPk_bclbid());
				if (maxMinDates != null){
					//被工作日历引用，查出工作日历引用此班别的最大最小日期
					UFDate maxDate = maxMinDates[0];
					UFDate minDate = maxMinDates[1];

					//判断所对应考勤期间是否封存
					if (TADelegator.getPeriod().queryCheckState(Global.getCorpPK(),minDate,maxDate) == 1) {
						MessageDialog.showHintDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010421","UPP6017010421-000021")/* @res "提示"*/, ResHelper.getString("6017","UPP6017-000582")//@res "引用此班别的工作日历数据涉及的日期所在的考勤期间已封存，不允许修改！"
						);
						return false;
					}
					//没有封存
					//added by zengcheng 2009.03.20,逻辑修改为：班别没有被引用，则可以任意修改；如果被引用了，则要判断考勤开始时间结束时间的修改状况：
					//考勤开始时间结束时间能改小，不能改大
					long formerBeginTimeMillis = newvo.getTimebeginday().intValue()*24*3600*1000+new UFTime(newvo.getTimebegintime()).getMillis();
					long formerEndTimeMillis = newvo.getTimeendday().intValue()*24*3600*1000+new UFTime(newvo.getTimeendtime()).getMillis();
					long nowBeginTimeMillis = ((Integer)getChbKqkssj().getSelectdItemValue()).intValue()*24*3600*1000+new UFTime(getTfKqkssj().getText()).getMillis();
					long nowEndTimeMillis = ((Integer)getChbKqjssj().getSelectdItemValue()).intValue()*24*3600*1000+new UFTime(getTfKqjssj().getText()).getMillis();
					if(nowBeginTimeMillis<formerBeginTimeMillis||nowEndTimeMillis>formerEndTimeMillis){
						showErrorMessage(ResHelper.getString("6017","UPP6017-000583")/*@res "此班别已被引用,将班别开始时间提前或者将班别结束时间延后可能会造成班别时间冲突，不能修改!"*/);
						return false;
					}
//					if (newvo.getTimebegintime().equals(getTfKqkssj().getText())
//							&& newvo.getTimeendtime().equals(getTfKqjssj().getText())
//							&& newvo.getTimebeginday().intValue()==(Integer)getChbKqkssj().getSelectdItemValue()
//							&& newvo.getTimeendday().intValue() == (Integer)getChbKqjssj().getSelectdItemValue()){
//						//不涉及“班别开始时间”或“班别结束时间”，允许修改
//					}
//					else {
//						int act = showOkCancelMessage(ResHelper.getString("6017","UPP6017-000583")//@res "此班别已被引用，修改班别开始结束时间可能导致排班冲突错误，如确实需要修改，将清除引用此班别的全部工作日历数据，您确认吗？"
//						);
//						if (act != nc.ui.pub.beans.MessageDialog.ID_OK){
//							return false;
//						}
//						isPsncalendarClear = true;
//					}
				}

				//newvo.setPk_bclbid(getBclbModel().getVO(mrow).getPrimaryKey());
				newvo.setPk_corp(getUnitCode());
				newvo.setGcbm(getFactoryCode());
				newvo.setLbbm(lbbm);
				newvo.setLbmc(lbmc);
				newvo.setLbjc(getCkbbbjc().getText());
				newvo.setPk_bbz(getbbzflRef().getRefPK());
				newvo.setPk_dd(getddflRef().getRefPK());
				newvo.setPk_bbmb(getbbmbRef().getRefPK());
				newvo.setMemo(memo);
				newvo.setSfkq(new Integer(sfkq));
				newvo.setTimebegintime(getTfKqkssj().getText());
				newvo.setTimeendtime(getTfKqjssj().getText());
				newvo.setTimebeginday((Integer)getChbKqkssj().getSelectdItemValue());
				newvo.setTimeendday((Integer)getChbKqjssj().getSelectdItemValue());
				newvo.setNightbeginday((Integer)getChbYbkssj().getSelectdItemValue());
				newvo.setNightendday((Integer)getChbYbjssj().getSelectdItemValue());
				newvo.setIsallowout(UFBoolean.valueOf(getCkbsfyxwc().isSelected()));
				newvo.setDefaultFlag(UFBoolean.valueOf(getCkbSfmrbb().isSelected()));
				newvo.setIscancel(UFBoolean.valueOf(getCkbSfzf().isSelected()));
				
				// 下班加班
	   			if (getUIChkbovertmrule().isSelected()) {
					newvo.setUseovertmrule(UFBoolean.TRUE);
					if (getUITFovertmrule().getText() != null && getUITFovertmrule().getText().trim().length() != 0) {
		 				newvo.setOvertmeffectbeyond(new UFDouble(getUITFovertmrule().getText().trim()));
					}
					if (getUITFovertmrule2().getText() != null && getUITFovertmrule2().getText().trim().length() != 0) {
						newvo.setOvertmbeyond(new UFDouble(getUITFovertmrule2().getText().trim()));
					}
					if (getUIRefPane1overtype().getRefPK() != null && getUIRefPane1overtype().getRefPK().trim().length() != 0) {
		 				newvo.setOvertmruletype(getUIRefPane1overtype().getRefPK().trim());
					}
	   			}else{
	   				newvo.setUseovertmrule(UFBoolean.FALSE);
	   				newvo.setOvertmeffectbeyond(new UFDouble(0));
	   				newvo.setOvertmbeyond(new UFDouble(0));
	   				newvo.setOvertmruletype(null);
	   			}

	   			// 提前加班
	   			if (getUIChkbontmrule().isSelected()) {
					newvo.setUseontmrule(UFBoolean.TRUE);
					if (getUITFontmrule().getText() != null && getUITFontmrule().getText().trim().length() != 0) {
		 				newvo.setOntmeffectbeyond(new UFDouble(getUITFontmrule().getText().trim()));
					}
					if (getUITFontmrule2().getText() != null && getUITFontmrule2().getText().trim().length() != 0) {
						newvo.setOntmbeyond(new UFDouble(getUITFontmrule2().getText().trim()));
					}
					if (getUIRefPane1ontype().getRefPK() != null && getUIRefPane1ontype().getRefPK().trim().length() != 0) {
		 				newvo.setOntmruletype(getUIRefPane1ontype().getRefPK().trim());
					}
	   			}else{
	   				newvo.setUseontmrule(UFBoolean.FALSE);
	   				newvo.setOntmeffectbeyond(new UFDouble(0));
	   				newvo.setOntmbeyond(new UFDouble(0));
	   				newvo.setOntmruletype(null);
	   			}
				newvo.setIncludenightshift(UFBoolean.valueOf(getCkbSfyb().isSelected()));
				newvo.setNightbegintime(getTfYbkssj().getText());
				newvo.setNightendtime(getTfYbjssj().getText());
				newvo.setAllowearly(new UFDouble(getTfallowearly().getText()).setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()));
				newvo.setAllowlate(new UFDouble(getTfallowlate().getText()).setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()));
				newvo.setLargelate(new UFDouble(getTflargelate().getText()));
				newvo.setLargeearly(new UFDouble(getTflargeearly().getText()));
				newvo.setKghours(new UFDouble(getTfkgsc().getText()));

				newvo.setIsblocked(UFBoolean.valueOf(getCkbSFFC().isSelected()));
				//校验默认班别不能封存
				if ((newvo.getDefaultFlag()==null?false:newvo.getDefaultFlag().booleanValue()) && (newvo.getIsblocked()==null?false:newvo.getIsblocked().booleanValue())){
					showErrorMessage(ResHelper.getString("6017","UPP6017-000920")/*"默认班别不能封存!"*/);
					return false;
				}

				if (getrbButton_Auto().isSelected())
					newvo.setIsautokg("Y");
				else
					newvo.setIsautokg("N");

				//add by caizl
	   			newvo.setBclbfl(getBbflRef().getRefPK());
	   			if (StringUtils.isNotEmpty(newvo.getBclbfl())) {
					nc.vo.bd.defref.DefdocVO[] defdocVOs = (nc.vo.bd.defref.DefdocVO[]) PubDelegator.getIPersistenceRetrieve().retrieveByClause(null,nc.vo.bd.defref.DefdocVO.class,"pk_defdoc='" + newvo.getBclbfl() + "'");
					if (defdocVOs == null || defdocVOs.length <= 0) {
						showErrorMessage(ResHelper.getString("6017",
								"UPP6017-001428"));//"班别分类已不存在，请刷新班别分类参照后再操作！"
						return false;
					}
				}
				BclbVO bvo = new BclbVO();
				bvo.setParentVO(newvo);
				BclbItemVO[] items = getWtVos();
				Vector<BclbItemVO> dataVOs = new Vector<BclbItemVO>();
				for (int i=0; i<items.length; i++){
					if ((items[i].getKssj()!=null) && (items[i].getKssj().trim().length()>0)){
						dataVOs.add(items[i]);
					}
				}

				sort2(dataVOs);//将有效数据的时间段排序
				
				//新增 设置上班刷卡，下班不刷卡，最后一班上下班都要刷卡
				for(int q = 0; q < dataVOs.size(); q++) {
					dataVOs.get(q).setCheckInFlag(new UFBoolean("Y"));
					dataVOs.get(q).setCheckoutFlag(new UFBoolean("N"));
					if(q == dataVOs.size() -1)
						dataVOs.get(q).setCheckoutFlag(new UFBoolean("Y"));
				}
				
				//设置中间时间点
				PubUISet.setMidTime(dataVOs, newvo.getTimeendtime(),newvo.getTimeendday());

				BclbItemVO[] items2 = new BclbItemVO[dataVOs.size()];
				dataVOs.copyInto(items2);
				bvo.setChildrenVO(items2);

				//计算工作时长
				if (!computeGzsj(newvo,items2)){
					return false;
				}

				getWtModel().setAddRow(0);

				FuncParser fpFuncParser = getFuncParser();

				//取得保留位数
				int l_intRuleScale = 2;
				if (ruleVO != null) {
					l_intRuleScale = ruleVO.getTimedecimal().intValue();
				}

				TADelegator.getBclb029().updateBclb029(bvo,isPsncalendarClear,fpFuncParser,l_intRuleScale);
				getBclbModel().updateVO(newvo);
				
				if ((defaultBclbHeaderVO==null||newvo.getPk_bclbid()!= defaultBclbHeaderVO.getPk_bclbid()) && newvo.getDefaultFlag()!=null && newvo.getDefaultFlag().booleanValue()){
					defaultBclbHeaderVO.setDefaultFlag(UFBoolean.FALSE);
					defaultBclbHeaderVO = newvo;
				}

				showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000056")/* @res "修改完成" */);
				getTfGzsj().setText(newvo.getGzsj().toString());
				setState(0);
				//onRefresh();
				//getBclbTablePane().getTable().getSelectionModel().setSelectionInterval(mrow, mrow);
				//add by caizl start
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(newvo);
				DefaultMutableTreeNode parent = null;
				DefaultTreeModel treeModel = (DefaultTreeModel) getBclb().getModel();
				DefdocVO defdocVO = null;
	   			if(!(((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof String)) {
		   			if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
		   				defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
					}else if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject() instanceof DefdocVO) {
						defdocVO = (DefdocVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getParentPath().getLastPathComponent())).getUserObject();
					}
	   			}
	   			//如果当前选中树的班别分类和右侧班别分类的值不同，则查找相应节点
				if (getBbflRef().getRefPK()==null && defdocVO!=null) {
					parent = (DefaultMutableTreeNode) treeModel.getRoot();
				}
				if (getBbflRef().getRefPK()!=null && (defdocVO==null || !getBbflRef().getRefPK().equals(defdocVO.getPk_defdoc()))) {
					for (int i = 0; i < treeModel.getChildCount(treeModel.getRoot()); i++) {
						DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)treeModel.getChild(treeModel.getRoot(), i);
						if (getBbflRef().getRefPK().equals(((DefdocVO)childNode.getUserObject()).getPk_defdoc())) {
							parent = childNode;
							break;
						}
					}
				}
				if (parent != null) {
					//如果需要切换树的分支，先删除修改的节点再在需要增加的节点添加子节点
					getBclb().removeTreeSelectionListener(this);
					treeModel.removeNodeFromParent((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent()));
					getBclb().addTreeSelectionListener(this);
					treeModel.insertNodeInto(node, parent, parent.getChildCount());
					getBclb().setSelectionPath(new TreePath(treeModel.getPathToRoot(node)));
				}
				getBclb().scrollPathToVisible(getBclb().getSelectionPath());
				//add by caizl end
				boModify.setEnabled(true);
				boDel.setEnabled(true);
				updateButton(boModify);
				updateButton(boDel);
				return true;
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			showHintMessage(e.getMessage());
			showErrorMessage(e.getMessage());

		}catch (Throwable aThrowable) {
			if (aThrowable instanceof java.lang.OutOfMemoryError) {
				showErrorMessage(ResHelper.getString("6017","UPP6017-000921")/*"引用此班别用来排班的数据量过大，请先清理用此班别排班的工作日历！"*/);
			}
		}
		return false;
	}

	/**
	 * 删除按钮响应事件
	 */
	public void onDel() {

//		int idx = getBclbTablePane().getTable().getSelectedRow();
//		if (idx < 0) {
//			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000059")/* @res "请先在班次类别表中选一行！" */);
//			return;
//		}

//		int act = showOkCancelMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000060")/* @res "将把选中班次类别的信息全部删除，确定吗？"*/);
//
//		if (act != nc.ui.pub.beans.MessageDialog.ID_OK)
//			return;


//		int rowcount = getBclbTablePane().getTable().getRowCount();

		try {
			if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof BclbHeaderVO) {
				BclbHeaderVO bhvo = (BclbHeaderVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();

				if (bhvo.getDefaultFlag().booleanValue()){
					showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000061")/* @res "删除失败" */);
					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000135") /*@res "默认班别不能删除！"*/);
					return;
				}

				//int isInUse = TADelegator.getBclb029().isInUseBclb029(bhvo);
				UFDate[] maxMinDates = TADelegator.getPsncalendar().queryDateByBclb(bhvo.getPk_bclbid());
				if (maxMinDates != null){
					//被工作日历引用，查出工作日历引用此班别的最大最小日期
					UFDate maxDate = maxMinDates[0];
					UFDate minDate = maxMinDates[1];

					//判断所对应考勤期间是否封存
					if (TADelegator.getPeriod().queryCheckState(Global.getCorpPK(),minDate,maxDate) == 1) {
						MessageDialog.showHintDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010421","UPP6017010421-000021")/* @res "提示"*/, ResHelper.getString("6017","UPP6017-000584")//@res "引用此班别的工作日历数据涉及的日期所在的考勤期间已封存，不允许删除！"
	);
						return;
					}
						//没有封存,“引用此班别的工作日历将同时清空”
						int act = showOkCancelMessage(ResHelper.getString("6017","UPP6017-000585")//@res "引用此班别的工作日历将同时清空，确定吗？"
	);
						if (act != nc.ui.pub.beans.MessageDialog.ID_OK){
							return;
						}
						//删除
						TADelegator.getBclb029().deleteBclb(bhvo);
				}else {
					int act = showOkCancelMessage(ResHelper.getString("6017","UPP6017-000922")/*"您确定删除此班别吗？"*/);
					if (act != nc.ui.pub.beans.MessageDialog.ID_OK){
						return;
					}
					//删除
					TADelegator.getBclb029().deleteBclb(bhvo);
				}
				//onRefresh();
				getBclb().removeTreeSelectionListener(this);
				int selectedRow = getBclb().getMaxSelectionRow();
				DefaultTreeModel treeModel = (DefaultTreeModel) getBclb().getModel();
				treeModel.removeNodeFromParent((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent()));
				getBclb().addTreeSelectionListener(this);
				int rowCount = getBclb().getRowCount();
				getBclb().setSelectionRow(rowCount < selectedRow ? rowCount - 1 : selectedRow - 1);
//				getBclbModel().removeVO(idx);
//
//				if (rowcount == idx + 1) {
//					clearBclb();
//					boModify.setEnabled(false);
//					boDel.setEnabled(false);
//					updateButton(boModify);
//					updateButton(boDel);
//					mrow = -1;
//				} else {
//					getBclbTablePane().getTable().getSelectionModel().setSelectionInterval(idx, idx);
//				}

				showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000063")/* @res "删除一行" */);
			}
		} catch (Exception e) {
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000061")/* @res "删除失败" */);
			//UPP60290113-000064已删除,zengcheng 2005.04.18
			//showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000064")/*@res
			// "删除失败："*/+e.getMessage());
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000061")/* @res "删除失败" */+ ":" + e.getMessage());
			reportException(e);
		}

	}

	/**
	 * 此处插入方法说明。 创建日期：(01-3-17 3:00:08)
	 */
	public void onHelp() {
	}

	/**
	 * 修改按钮响应事件
	 */
	public void onModify() {
		try {
//			int idx = getBclbTablePane().getTable().getSelectedRow();
//
//			if (idx < 0) {
//				showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000059")/* @res "请先在班次类别表中选一行！" */);
//				return;
//			}
			if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof BclbHeaderVO) {
				boolean isupdate = true;
				BclbHeaderVO bhvo = (BclbHeaderVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
				// 删除记录，当要删除的记录被引用时，不能删除，返回值为1
				int nResult = TADelegator.getBclb029().isInUseBclb029(bhvo);
				if (nResult == 1) {
					String str = nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000065")/* @res "该班次已经被使用,如果修改,数据可能会出错,是否修改?"*/;
					if (showYesNoMessage(str) != UIDialog.ID_YES) {
						isupdate = false;
					}
				}
				if (isupdate) {
					setState(2); // 修改状态
				}

				if (getCkbSFFC().isSelected()){
					sffcClick();
				}
				getTfLbmc().requestFocus();
			}

		} catch (Exception e) {
			reportException(e);
			showErrorMessage(e.getMessage());

		}
	}

	/**
	 * 打印。 创建日期：(01-3-17 3:00:46)
	 */
	public void onPrint() {

		BclbVO vo = new BclbVO();
		/*int row = getBclbTablePane().getTable().getSelectedRow();
		int count = getBclbModel().getRowCount();
		if (row < 0 || row > count - 1) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000059") @res "请先在班次类别表中选一行！" );
			return;
		}*/
		if (getBclb().getSelectionPath()==null || !(((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof BclbHeaderVO)) {
			return;
		}
		// 得到班次类别设置VO
		BclbHeaderVO hvo = (BclbHeaderVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
		//hvo.setGcbm(nc.ui.hr.global.Global.getPsnProp().getUnitname());
		// 得到作息时间列表设置VO
		Vector temp = getWtModel().getVOs();
		Vector v = new Vector();
		for (int i = 0; i < temp.size(); i++) {
			// 如果没有设置开始时间
			if (((BclbItemVO) temp.elementAt(i)).getKssj() == null
					|| ((BclbItemVO) temp.elementAt(i)).getKssj().toString()
							.trim().equals(""))
				continue;
			v.addElement(temp.elementAt(i));
		}
		BclbItemVO[] bvos = null;
		if (v.size() > 0) {
			bvos = new BclbItemVO[v.size()];
			v.copyInto(bvos);
		}

		vo.setParentVO(hvo);

		if (bvos == null) {
			bvos = new BclbItemVO[1];
			bvos[0] = new BclbItemVO();
		}

		/**
		 *  Modified by Young 2005-10-18 Start
		 */
		for (int i=0; i<bvos.length; i++){
			if (bvos[i].getKsto()==null){
				bvos[i].setKsto(" ");
			}
			if (bvos[i].getJsto()==null){
				bvos[i].setJsto(" ");
			}
		}
		/**
		 *  Modified by Young 2005-10-18 End
		 */

		vo.setChildrenVO(bvos);

		m_dataSource.setVO(vo);
		m_dataSource.setModuleName("6017010113");
		m_print.setDataSource(m_dataSource);
		
		
		m_print.setTemplateID(getUnitCode(), "6017010113", getUser()
				.getPrimaryKey(), null);
		
		if (m_print.selectTemplate() < 0)
			return;
		m_print.preview();
	}

	/**
	 * 按刷新按钮后的处理方法。 创建日期：(01-3-17 3:00:46)
	 */
	public boolean onRefresh() {
		initData();
		return true;
	}

	/**
	 * 按导入按钮后的处理方法。 创建日期：(11-4-17 3:00:46) 刘鹏飞
	 */
	public boolean onImport() {
		
		ImportExcelData exl = new ImportExcelData();
		Object[][] ob = null;
		try{
			ob = exl.executeImport();
		}catch(Exception e){
			Logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		if(ob ==null || ob.length <= 1) return false;
		
//		获取业务数据
		DefDocVO[] ddvos = null;
		BusinessDelegator business = new BusinessDelegator();
		try {
			ddvos = (DefDocVO[])business.queryByCondition(DefDocVO.class," pk_corp='"+this.getCorpPrimaryKey()+"' and pk_defdoclist = (SELECT pk_defdoclist FROM bd_defdoclist WHERE ( nvl ( dr, 0 ) = 0 ) and doclistcode = 'HRB04')" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List listHead = new ArrayList();
		List listItem = new ArrayList();
		for(int i = 1 ; i < ob.length ; i++){
			BclbHeaderVO bhvo = new BclbHeaderVO();
			for(int j = 0 ; j < ob[i].length;j++){
				if(getUILabel1().getText().equals(ob[0][j])){//班次编码
					if(ob[i][j] == null || ob[i][j].toString().equals("")){//空判断
						showErrorMessage(getUILabel1().getText() + "不能为空");
						return false;
					}
					bhvo.setLbbm(ob[i][j].toString());
				}
				if(getUILabel2().getText().equals(ob[0][j])){//班次名称
					if(ob[i][j] == null || ob[i][j].toString().equals("")){//空判断
						showErrorMessage(getUILabel2().getText() + "不能为空");
						return false;
					}
					bhvo.setLbmc(ob[i][j].toString());
				}
				
				if(getBbfl().getText().equals(ob[0][j])){//班次类别
					if(ob[i][j] == null || ob[i][j].toString().equals("")){//空判断
						bhvo.setBclbfl("");
					}
					else{
						for(int m = 0 ; m < ddvos.length ;m++){
							if(ddvos[m].getDocname().equals(ob[i][j])){
								//bhvo.setS_bclbfl(ddvos[m].getPk_defdoc());
								bhvo.setBclbfl(ddvos[m].getPk_defdoc());
							}
						}
					}
				}
				if(getUILabelSfzf().getText().equals(ob[0][j])){//是否作废
					if(ob[i][j].toString().equals("是"))
						bhvo.setIscancel(new UFBoolean("Y"));
					else
						bhvo.setIscancel(new UFBoolean("N"));
					
				}
				if(getLbSfyb().getText().equals(ob[0][j])){//是否夜班
					if(ob[i][j].toString().equals("是"))
						bhvo.setIncludenightshift(new UFBoolean("Y"));
					else
						bhvo.setIncludenightshift(new UFBoolean("N"));
				}
				if(getUILabel3().getText().equals(ob[0][j])){//工作时长
					
					bhvo.setGzsj((UFDouble)ob[i][j]);
				}
				if("班别开始日期".equals(ob[0][j])){//开始时间
					if(ob[i][j] == null){
						showErrorMessage( "班别开始日期不能为空");
						return false;
					}
					if(ob[i][j].toString().equals("前一天"))
						bhvo.setTimebeginday(-1);
					else if(ob[i][j].toString().equals("后一天"))
						bhvo.setTimebeginday(1);
					else
						bhvo.setTimebeginday(0);
					
						
				}
				if("班别结束日期".equals(ob[0][j])){//结束时间
					if(ob[i][j] == null){
						showErrorMessage( "班别结束日期不能为空");
						return false;
					}
					if(ob[i][j].toString().equals("前一天"))
						bhvo.setTimeendday(-1);
					else if(ob[i][j].toString().equals("后一天"))
						bhvo.setTimeendday(1);
					else
						bhvo.setTimeendday(0);
					
				}
				if(getLbKqkssj().getText().equals(ob[0][j])){//开始day
					if(ob[i][j] == null){
						showErrorMessage( getLbKqkssj().getText() + "不能为空");
						return false;
					}
					if(ob[i][j] instanceof  UFDouble){
						showErrorMessage("请设置"+getLbKqkssj().getText() +  "列为文本类型！");
						return false;
					}
					bhvo.setTimebegintime(ob[i][j].toString());
				}
				if(getLbKqjssj().getText().equals(ob[0][j])){//结束day
					if(ob[i][j] == null){
						showErrorMessage(getLbKqjssj().getText() +  "不能为空");
						return false;
					}
					if(ob[i][j] instanceof  UFDouble){
						showErrorMessage("请设置"+getLbKqjssj().getText() +  "列为文本类型！");
						return false;
					}
					bhvo.setTimeendtime(ob[i][j].toString());
				}
				if(getLbMemo().getText().equals(ob[0][j])){//说明
					if(ob[i][j] != null)
						bhvo.setMemo(ob[i][j].toString());
				}
			}
			bhvo.setDefaultFlag(new UFBoolean("N"));
			int result = 0;
			try{
	//			开始验证
				result = import_checkHeader(bhvo);
				if(result != 0)	return false; 
			}
			catch(Exception e){
				
			}
			
			UFBoolean uftrue = new UFBoolean(true);
			UFBoolean uffalse = new UFBoolean(false);
			bhvo.setIsallowout(uftrue);
			bhvo.setIsblocked(uffalse);
			bhvo.setNightbeginday(0);
			bhvo.setNightendday(0);
			bhvo.setPk_corp(this.getCorpPrimaryKey());
			bhvo.setSfkq(1);
			List list = new ArrayList();
			BclbItemVO[] bvos = new BclbItemVO[4];
			bvos[0] = new BclbItemVO();
			bvos[0].setTimeid(1);
			bvos[0].setPk_corp(this.getCorpPrimaryKey());
			if(ob[i][11].toString().equals("前一天"))
				bvos[0].setWtbeginday(-1);
			else if(ob[i][11].toString().equals("后一天"))
				bvos[0].setWtbeginday(1);
			else if(ob[i][11].toString().equals("当日"))
				bvos[0].setWtbeginday(0);
			else{
				showErrorMessage("第12列应为“前一天”、“后一天”和“当日”等值！");
				return false;
			}
				
			
			
			if(ob[i][12] instanceof  UFDouble){
				showErrorMessage("请设置时间列为文本类型！");
				return false;
			}
			bvos[0].setKssj(ob[i][12]==null?"":(new UFTime(ob[i][12].toString())).toString());
			
			
			if(ob[i][13].toString().equals("前一天"))
				bvos[0].setWtendday(-1);
			else if(ob[i][13].toString().equals("后一天"))
				bvos[0].setWtendday(1);
			else 
				bvos[0].setWtendday(0);
			if(ob[i][14] instanceof  UFDouble){
				showErrorMessage("请设置时间列为文本类型！");
				return false;
			}
			bvos[0].setJssj(ob[i][14]==null?"":(new UFTime(ob[i][14].toString())).toString());
			list.add(bvos[0]);
			
			if(ob[i][16] != null){
				bvos[1] = new BclbItemVO();
				bvos[1].setTimeid(2);
				bvos[1].setPk_corp(this.getCorpPrimaryKey());
				//bvos[1].setWtbeginday(ob[i][15]==null?0:((UFDouble)ob[i][15]).intValue());
				
				if(ob[i][15] != null){
					if(ob[i][15].toString().equals("前一天"))
						bvos[1].setWtbeginday(-1);
					else if(ob[i][15].toString().equals("后一天"))
						bvos[1].setWtbeginday(1);
					else 
						bvos[1].setWtbeginday(0);
				}
				
				if(ob[i][16] instanceof  UFDouble){
					showErrorMessage("请设置时间列为文本类型！");
					return false;
				}
				bvos[1].setKssj(ob[i][16]==null?"":(new UFTime(ob[i][16].toString())).toString());
				
				//bvos[1].setWtendday(ob[i][17]==null?0:((UFDouble)ob[i][17]).intValue());
				if(ob[i][17] != null){
					if(ob[i][17].toString().equals("前一天"))
						bvos[1].setWtendday(-1);
					else if(ob[i][17].toString().equals("后一天"))
						bvos[1].setWtendday(1);
					else 
						bvos[1].setWtendday(0);
				}
				if(ob[i][18] instanceof  UFDouble){
					showErrorMessage("请设置时间列为文本类型！");
					return false;
				}
				bvos[1].setJssj(ob[i][18]==null?"":(new UFTime(ob[i][18].toString())).toString());
				list.add(bvos[1]);
				
				if(ob[i][20] != null){
					bvos[2] = new BclbItemVO();
					bvos[2].setTimeid(3);
					bvos[2].setPk_corp(this.getCorpPrimaryKey());
					//bvos[2].setWtbeginday(ob[i][19]==null?0:((UFDouble)ob[i][19]).intValue());
					if(ob[i][19] != null){
						if(ob[i][19].toString().equals("前一天"))
							bvos[2].setWtbeginday(-1);
						else if(ob[i][19].toString().equals("后一天"))
							bvos[2].setWtbeginday(1);
						else 
							bvos[2].setWtbeginday(0);
					}
					
					if(ob[i][20] instanceof  UFDouble){
						showErrorMessage("请设置时间列为文本类型！");
						return false;
					}
					bvos[2].setKssj(ob[i][20]==null?"":(new UFTime(ob[i][20].toString())).toString());
					//bvos[2].setWtendday(ob[i][21]==null?0:((UFDouble)ob[i][21]).intValue());
					if(ob[i][21] != null){
						if(ob[i][21].toString().equals("前一天"))
							bvos[2].setWtendday(-1);
						else if(ob[i][21].toString().equals("后一天"))
							bvos[2].setWtendday(1);
						else 
							bvos[2].setWtendday(0);
					}	
					if(ob[i][22] instanceof  UFDouble){
						showErrorMessage("请设置时间列为文本类型！");
						return false;
					}
					bvos[2].setJssj(ob[i][22]==null?"":(new UFTime(ob[i][22].toString())).toString());
					list.add(bvos[2]);
					if(ob[i][24] != null){
						bvos[3] = new BclbItemVO();
						bvos[3].setTimeid(4);
						bvos[3].setPk_corp(this.getCorpPrimaryKey());
						//bvos[3].setWtbeginday(ob[i][23]==null?0:((UFDouble)ob[i][23]).intValue());
						if(ob[i][23] != null){
							if(ob[i][23].toString().equals("前一天"))
								bvos[3].setWtbeginday(-1);
							else if(ob[i][23].toString().equals("后一天"))
								bvos[3].setWtbeginday(1);
							else 
								bvos[3].setWtbeginday(0);
						}
						if(ob[i][24] instanceof  UFDouble){
							showErrorMessage("请设置时间列为文本类型！");
							return false;
						}
						bvos[3].setKssj(ob[i][24]==null?"":(new UFTime(ob[i][24].toString())).toString());
						//bvos[3].setWtendday(ob[i][25]==null?0:((UFDouble)ob[i][25]).intValue());
						if(ob[i][25] != null){
							if(ob[i][25].toString().equals("前一天"))
								bvos[3].setWtendday(-1);
							else if(ob[i][25].toString().equals("后一天"))
								bvos[3].setWtendday(1);
							else 
								bvos[3].setWtendday(0);
						}
						
						if(ob[i][26] instanceof  UFDouble){
							showErrorMessage("请设置时间列为文本类型！");
							return false;
						}
						bvos[3].setJssj(ob[i][26]==null?"":(new UFTime(ob[i][26].toString())).toString());
						list.add(bvos[3]);
					}	
				}	
			}
			
			BclbVO bvo = new BclbVO();
			bvo.setParentVO(bhvo);
			//bvo.setChildrenVO(bvos);
			bvo.setChildrenVO((BclbItemVO[])list.toArray(new BclbItemVO[0]));
			
			/*
			if (result != 0) {
				return result;
			}
			return checkItems();
			*/
			
			try{
				
				result = import_checkItems(bhvo,(BclbItemVO[])list.toArray(new BclbItemVO[0]));
				if(result != 0) return false;
				String pk = TADelegator.getBclb029().insertBclb029(bvo);
			}catch(Exception e){
				showErrorMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		MessageDialog.showHintDlg(null, "提示", "导入成功！");
		return true;
	}
	
	
	/**
	 * 按查询按钮后的处理方法。 创建日期：(11-4-17 3:00:46) 刘鹏飞
	 */
	public boolean onQuery() {
		
		//6017010113
		QueryDLG dlg = new QueryDLG();
		
		dlg.setTempletID(nc.ui.hr.global.Global.getCorpPK(), "6017010113",
				this.getUser().getPrimaryKey(), null);
		dlg.setNormalShow(false);
		
		dlg.showModal();
		
        ConditionVO[] queryvos = dlg.getConditionVO();
        
        for(int i = 0 ; i< queryvos.length ; i++){
        	if(queryvos[i].getDataType() == 8){

        		String temp = new UFTime(queryvos[i].getValue()).toString();
        		if(temp == null || temp.equals("")){
        			showErrorMessage("["+queryvos[i].getFieldName()+"]输入时间格式不正确，请调整！");
        			return false;
        			
        		}
        		else{
        			queryvos[i].setValue(temp);
        		}
        	}
        }
    
        //刘鹏飞 2011-4-12 原  initData()内容 ，仅更改 getTreeModel()成getTreeModel(String sql)
        try{
			getBclb().removeTreeSelectionListener(this);
			if(dlg.getWhereSQL() != null)
				getBclb().setModel(getTreeModel(" and  " + dlg.getWhereSQL()));
			else
				getBclb().setModel(getTreeModel());
			getBclb().addTreeSelectionListener(this);
			clearBclb();
			setState(0);

		} catch (Exception e) {
			reportException(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000052")/* @res "读取数据库出错：" */+ e.getMessage());
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000053")/* @res "读取数据库失败" */);
		}

		
		return true;
	}
	
	/**
	 * 将 BclbModel 设置为新值
	 *
	 * @param newValue
	 *            nc.ui.pd.pd1020.BclbTableModel
	 */
	/* 警告：此方法将重新生成。 */
	private void setBclbModel(BclbTableModel newValue) {
		if (ivjBclbModel != newValue) {
			try {
				ivjBclbModel = newValue;
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		;
		// user code begin {3}
		// user code end
	}

	/**
	 * 状态控制。 创建日期：(2001-06-12 19:04:23)
	 *
	 * @param st
	 *            int
	 */
	public void setState(int st) {
		state = st;
		getWtModel().setState(st);
		getTfGzsj().setEnabled(false);
		if (st == 0) { //0：浏览
			getTfLbbm().setEnabled(false);
			getTfLbmc().setEnabled(false);
			getCkbbbjc().setEnabled(false);
			getbbzflRef().setEnabled(false);
			getddflRef().setEnabled(false);
			getbbmbRef().setEnabled(false);
			//getTfGzsj().setEnabled(false);
			getcbxSfkq().setEnabled(false);
			getCkbSFFC().setEnabled(false);

			//5.5getCkbYbsfkt().setEnabled(false);
			getCkbSfmrbb().setEnabled(false);
			getCkbSfzf().setEnabled(false);
			//5.5getCkbKqsfkt().setEnabled(false);
			getTfKqkssj().setEnabled(false);
			getTfKqjssj().setEnabled(false);
			
			//刘鹏飞 新增，说明
			getTfMemo().setEnabled(false);
			
			getCkbSfyb().setEnabled(false);
			//5.5getCkbYbsfkt().setEnabled(false);

			//add5.5
			getChbKqkssj().setEnabled(false);
			getChbKqjssj().setEnabled(false);
			getChbYbkssj().setEnabled(false);
			getChbYbjssj().setEnabled(false);
			getCkbsfyxwc().setEnabled(false);

			getTfYbkssj().setEnabled(false);
			getTfYbjssj().setEnabled(false);
			getChbYbkssj().setEnabled(false);
			getChbYbjssj().setEnabled(false);
			getRfpFactory().setEnabled(true);
			getTfallowearly().setEnabled(false);
			getTfallowlate().setEnabled(false);
			getTflargelate().setEnabled(false);
			getTflargeearly().setEnabled(false);
			getTfkgsc().setEnabled(false);
			getrbButton_Auto().setEnabled(false);
			getrbButton_clz().setEnabled(false);

			boAdd.setEnabled(true);
			boFlash.setEnabled(true);
			//boModify.setEnabled(true);
			//boDel.setEnabled(true);
			boModify.setEnabled(false);
			boDel.setEnabled(false);
			boPrint.setEnabled(true);
			boConfirm.setEnabled(false);
			boCancel.setEnabled(false);
			//getBclbTablePane().getTable().setColumnSelectionAllowed(false);
			//getBclbTablePane().getTable().setCellSelectionEnabled(false);
			//getBclbTablePane().getTable().setRowSelectionAllowed(true);

			/**
			 *  Modified by Young 2005-09-22 Start
			 */
	   		//add by liuhongjie 2005-04-19
	   		getUIRefPane1overtype().setEnabled(false);
	   		getUITFovertmrule().setEnabled(false);
	   		getUITFovertmrule2().setEnabled(false);
	   		getUIChkbovertmrule().setEnabled(false);
	   		//add by myl 2005-6-28
	   		getUIRefPane1ontype().setEnabled(false);
	   		getUITFontmrule().setEnabled(false);
	   		getUITFontmrule2().setEnabled(false);
	   		getUIChkbontmrule().setEnabled(false);
			/**
			 *  Modified by Young 2005-09-22 End
			 */
	   		//add by caizl
	   		//getBbfl().setEnabled(false);
	   		getBbflRef().setEnabled(false);
	   		getBclb().setEnabled(true);
	   		boCopy.setEnabled(true);
		}
		if (st == 1) { //1：增加
			getTfLbbm().setEnabled(true);
			getTfLbmc().setEnabled(true);
			getCkbbbjc().setEnabled(true);
			getbbzflRef().setEnabled(true);
			getddflRef().setEnabled(true);
			getbbmbRef().setEditable(true);
			//getTfGzsj().setEnabled(true);
			getcbxSfkq().setEnabled(true);
			getCkbSFFC().setEnabled(true);

			//5.5getCkbYbsfkt().setEnabled(true);
			getCkbSfmrbb().setEnabled(true);
			getCkbSfzf().setEnabled(true);
			//5.5getCkbKqsfkt().setEnabled(true);
			getTfKqkssj().setEnabled(true);
			getTfKqjssj().setEnabled(true);
			
			//刘鹏飞新增
			getTfMemo().setEnabled(true);
			
			getCkbSfyb().setEnabled(true);
			//5.5getCkbYbsfkt().setEnabled(true);

			//add5.5
			getChbKqkssj().setEnabled(true);
			getChbKqjssj().setEnabled(true);
			getChbYbkssj().setEnabled(true);
			getChbYbjssj().setEnabled(true);
			getCkbsfyxwc().setEnabled(true);

			getTfYbkssj().setEnabled(false);
			getTfYbjssj().setEnabled(false);
			getChbYbkssj().setEnabled(false);
			getChbYbjssj().setEnabled(false);
			getRfpFactory().setEnabled(false);
			getTfallowearly().setEnabled(true);
			getTfallowlate().setEnabled(true);
			getTflargelate().setEnabled(true);
			getTflargeearly().setEnabled(true);
			getTfkgsc().setEnabled(false);
			getrbButton_Auto().setEnabled(true);
			getrbButton_clz().setEnabled(true);

			boAdd.setEnabled(false);
			boModify.setEnabled(false);
			boDel.setEnabled(false);
			boFlash.setEnabled(false);
			boPrint.setEnabled(false);
			boConfirm.setEnabled(true);
			boCancel.setEnabled(true);
			//getBclbTablePane().getTable().setRowSelectionAllowed(false);
			//getBclbTablePane().getTable().setColumnSelectionAllowed(false);
			//getBclbTablePane().getTable().setCellSelectionEnabled(false);

			/**
			 *  Modified by Young 2005-09-22 Start
			 */
			//add by liuhongjie 2005-04-19
	   		getUIRefPane1overtype().setEnabled(false);
	   		getUITFovertmrule().setEnabled(false);
	   		getUITFovertmrule2().setEnabled(false);
	   		getUIChkbovertmrule().setEnabled(true);
	   		//add by myl 2005-06-28
	   		getUIRefPane1ontype().setEnabled(false);
	   		getUITFontmrule().setEnabled(false);
	   		getUITFontmrule2().setEnabled(false);
	   		getUIChkbontmrule().setEnabled(true);
			/**
			 *  Modified by Young 2005-09-22 End
			 */
	   		//add by caizl
	   		//getBbfl().setEnabled(true);
	   		getBbflRef().setEnabled(false);
	   		getBclb().setEnabled(false);
	   		boCopy.setEnabled(false);
		}
		if (st == 2) { //2：修改
			getTfLbbm().setEnabled(false);
			getTfLbmc().setEnabled(true);
			getCkbbbjc().setEnabled(true);
			getbbzflRef().setEnabled(true);
			getddflRef().setEnabled(true);
			getbbmbRef().setEnabled(true);
			//getTfGzsj().setEnabled(true);
			getcbxSfkq().setEnabled(true);
			getCkbSFFC().setEnabled(true);

			//5.5getCkbYbsfkt().setEnabled(true);
			if (getCkbSfmrbb().isSelected()){
				getCkbSfmrbb().setEnabled(false);
			}else{
				getCkbSfmrbb().setEnabled(true);
			}

			getCkbSfzf().setEnabled(true);
			//5.5getCkbKqsfkt().setEnabled(true);
			getTfKqkssj().setEnabled(true);
			getTfKqjssj().setEnabled(true);
			
			//刘鹏飞新增
			getTfMemo().setEnabled(true);
			
			getCkbSfyb().setEnabled(true);
			//5.5getCkbYbsfkt().setEnabled(true);

			//add5.5
			getChbKqkssj().setEnabled(true);
			getChbKqjssj().setEnabled(true);
			getChbYbkssj().setEnabled(true);
			getChbYbjssj().setEnabled(true);
			getCkbsfyxwc().setEnabled(true);

			if (getCkbSfyb().isSelected()) {
				getTfYbjssj().setEnabled(true);
				getTfYbkssj().setEnabled(true);
				getChbYbkssj().setEnabled(true);
				getChbYbjssj().setEnabled(true);
			} else {
				getTfYbjssj().setEnabled(false);
				getTfYbkssj().setEnabled(false);
				getChbYbkssj().setEnabled(false);
				getChbYbjssj().setEnabled(false);
			}
			//getTfYbkssj().setEnabled(true);
			//getTfYbjssj().setEnabled(true);
			getRfpFactory().setEnabled(true);
			getTfallowearly().setEnabled(true);
			getTfallowlate().setEnabled(true);
			getTflargelate().setEnabled(true);
			getTflargeearly().setEnabled(true);
			getrbButton_Auto().setEnabled(true);
			getrbButton_clz().setEnabled(true);
			if (getrbButton_clz().isSelected())
				getTfkgsc().setEnabled(true);
			else
				getTfkgsc().setEnabled(false);
			boAdd.setEnabled(false);
			boModify.setEnabled(false);
			boDel.setEnabled(false);
			boFlash.setEnabled(false);
			boPrint.setEnabled(false);
			boConfirm.setEnabled(true);
			boCancel.setEnabled(true);
			//getBclbTablePane().getTable().setRowSelectionAllowed(false);
			//getBclbTablePane().getTable().setColumnSelectionAllowed(false);
			//getBclbTablePane().getTable().setCellSelectionEnabled(false);

			/**
			 *  Modified by Young 2005-09-22 Start
			 */

			//add by liuhongjie 2005-04-19
	   		getUIRefPane1overtype().setEnabled(false);
	   		getUITFovertmrule().setEnabled(false);
	   		getUITFovertmrule2().setEnabled(false);
	   		getUIChkbovertmrule().setEnabled(true);
	   		//add by myl 2005-6-28
	   		getUIRefPane1ontype().setEnabled(false);
	   		getUITFontmrule().setEnabled(false);
	   		getUITFontmrule2().setEnabled(false);
	   		getUIChkbontmrule().setEnabled(true);

	   		if (getUIChkbovertmrule().isSelected()){
	   			getUITFovertmrule().setEnabled(true);
	   			getUITFovertmrule2().setEnabled(true);
	   			getUIRefPane1overtype().setEnabled(true);
	   		}else{
	   			getUITFovertmrule().setEnabled(false);
	   			getUITFovertmrule2().setEnabled(false);
	   			getUIRefPane1overtype().setEnabled(false);
	   		}
	   		if (getUIChkbontmrule().isSelected()){
	   			getUITFontmrule().setEnabled(true);
	   			getUITFontmrule2().setEnabled(true);
	   			getUIRefPane1ontype().setEnabled(true);
	   		}else{
	   			getUITFontmrule().setEnabled(false);
	   			getUITFontmrule2().setEnabled(false);
	   			getUIRefPane1ontype().setEnabled(false);
	   		}
			/**
			 *  Modified by Young 2005-09-22 End
			 */
	   		//add by caizl
	   		//getBbfl().setEnabled(true);
	   		getBbflRef().setEnabled(false);
	   		getBclb().setEnabled(false);
	   		boCopy.setEnabled(false);
		}
		updateButtons();
	}

	/**
	 * 将 WtModel 设置为新值
	 *
	 * @param newValue
	 *            nc.ui.pd.pd1020.WtTableModel
	 */
	/* 警告：此方法将重新生成。 */
	private void setWtModel(WtTableModel newValue) {
		if (ivjWtModel != newValue) {
			try {
				ivjWtModel = newValue;
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		;
		// user code begin {3}
		// user code end
	}

	/**
	 * 表行变化事件监听者必须实现的接口方法
	 *
	 */
	public void valueChanged(ListSelectionEvent e) {

		if (state != 0) // 如果是浏览状态，返回
			return;

		showHintMessage("");

		try {
			if (e.getValueIsAdjusting()) {
				boModify.setEnabled(false);
				boDel.setEnabled(false);
			} else {
				int selectRow = getBclbTablePane().getTable().getSelectedRow();

				mrow = selectRow; // 记录下当前选择行号，保存时用到

				BclbHeaderVO bhvo = (BclbHeaderVO) (getBclbModel()
						.getVO(selectRow));
				if (bhvo == null) {
					return;
				}
				// 设置右上侧显示数值
				getTfLbbm().setText(bhvo.getLbbm());
				getTfLbmc().setText(bhvo.getLbmc());
				getTfMemo().setText(bhvo.getMemo());
				getTfGzsj().setText(bhvo.getGzsj().toString());
				//getcbxSfkq().setSelected(
				//((Boolean) (getBclbModel().getValueAt(selectRow,
				// 3))).booleanValue());

				//Add tzj
				getTfKqkssj().setText(bhvo.getTimebegintime());
				getTfKqjssj().setText(bhvo.getTimeendtime());
				getTfYbkssj().setText(bhvo.getNightbegintime());
				getTfYbjssj().setText(bhvo.getNightendtime());
				if (bhvo.getAllowearly() != null)
					getTfallowearly().setText(
							bhvo.getAllowearly().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTfallowearly().setText("");
				if (bhvo.getAllowlate() != null)
					getTfallowlate().setText(
							bhvo.getAllowlate().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTfallowlate().setText("");
				if (bhvo.getLargeearly() != null)
					getTflargeearly().setText(
							bhvo.getLargeearly().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTflargeearly().setText("");
				if (bhvo.getLargelate() != null)
					getTflargelate().setText(
							bhvo.getLargelate().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
				else
					getTflargelate().setText("");

				/**
				 *  Modified by Young 2005-09-22 Start
				 */
				////////////////////////////////////////
				if (bhvo.getUseovertmrule() != null && bhvo.getUseovertmrule().booleanValue() ){
					getUIChkbovertmrule().setSelected(true);
					getUITFovertmrule().setText(bhvo.getOvertmeffectbeyond().toString());
					getUITFovertmrule2().setText(bhvo.getOvertmbeyond().toString());
					getUIRefPane1overtype().setPK(bhvo.getOvertmruletype());
				} else
				{
					getUIChkbovertmrule().setSelected(false);
					getUITFovertmrule().setText("");
					getUITFovertmrule2().setText("");
					getUIRefPane1overtype().setPK(null);
				}
				//add by myl
				if (bhvo.getUseontmrule() != null && bhvo.getUseontmrule().booleanValue() ){
					getUIChkbontmrule().setSelected(true);
					getUITFontmrule().setText(bhvo.getOntmeffectbeyond().toString());
					getUITFontmrule2().setText(bhvo.getOntmbeyond().toString());
					getUIRefPane1ontype().setPK(bhvo.getOntmruletype());
				} else
				{
					getUIChkbontmrule().setSelected(false);
					getUITFontmrule().setText("");
					getUITFontmrule2().setText("");
					getUIRefPane1ontype().setPK(null);
				}
				////////////////////////////////////////////
				/**
				 *  Modified by Young 2005-09-22 End
				 */

				//5.5getCkbKqsfkt().setSelected(bhvo.getTimenextdayend() == null ? false : bhvo.getTimenextdayend().booleanValue());
				getCkbSfmrbb().setSelected(
						bhvo.getDefaultFlag() == null ? false : bhvo
								.getDefaultFlag().booleanValue());
				getCkbSfzf().setSelected(
						bhvo.getIscancel() == null ? false : bhvo
								.getIscancel().booleanValue());
				//5.5getCkbYbsfkt().setSelected(bhvo.getNightnextdayend() == null ? false : bhvo.getNightnextdayend().booleanValue());

				//add5.5
				getChbKqkssj().setSelectedItem(bhvo.getTimebeginday());
				getChbKqjssj().setSelectedItem(bhvo.getTimeendday());
				getChbYbkssj().setSelectedItem(bhvo.getNightbeginday());
				getChbYbjssj().setSelectedItem(bhvo.getNightendday());
				getCkbsfyxwc().setSelected(bhvo.getIsallowout().booleanValue());
				getCkbSFFC().setSelected(bhvo.getIsblocked().booleanValue());

				getCkbSfyb().setSelected(
						bhvo.getIncludenightshift() == null ? false : bhvo
								.getIncludenightshift().booleanValue());

				getTfkgsc().setText(
						bhvo.getKghours() == null ? "" : bhvo.getKghours()
								.toString());

				if (bhvo.getIsautokg() == null
						|| bhvo.getIsautokg().trim().equals("Y")) {
					getrbButton_Auto().setSelected(true);
				} else {
					getrbButton_clz().setSelected(true);
				}

				//add by caizl
				getCkbbbjc().setText(bhvo.getLbjc());
				getbbzflRef().setPK(bhvo.getPk_bbz());
				getddflRef().setPK(bhvo.getPk_dd());
				getbbmbRef().setPK(bhvo.getPk_bbmb());
				getBbflRef().setPK(bhvo.getBclbfl());

				//类别编码lbbm
				if (bhvo.getLbbm() != null && !bhvo.getLbbm().equals("")) {

					// 查询出作息时间数据
					BclbItemVO[] tmpbtvs = TADelegator.getBclb029().queryAllBclbItemBclb029(bhvo);

					/**
					 *  Added by Young 2005-07-19 Start
					 */
					getWtModel().setDataVOs(tmpbtvs);
					/**
					 *  Added by Young 2005-07-19  End
					 */

					// 按时间排序
					//sort(tmpbtvs);

					sort3(tmpbtvs);

					BclbItemVO[] btvs = new BclbItemVO[4];
					for (int i = 0; i < 4; i++) {
						if (i > tmpbtvs.length - 1) { // 处理没有取出数据
							BclbItemVO btvo = new BclbItemVO();
							btvo.setPk_corp(getUnitCode());
							btvo.setGcbm(getFactoryCode());
							btvs[i] = btvo;
						} else {
							btvs[i] = tmpbtvs[i];

						}
					}
					// 在作息时间表中显示关于作息时间数据
					getWtTablePane().getTable().editingStopped(null);
					getWtModel().clearTable();
					getWtModel().addVO(btvs);
					boModify.setEnabled(true);
					boDel.setEnabled(true);
				} else {
					getWtModel().clearTable();
					getWtModel().addVO(btvos);
					boModify.setEnabled(true);
					boDel.setEnabled(true);
				}
			}
			updateButton(boDel);
			updateButton(boModify);
		} catch (Exception exe) {
			reportException(exe);
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000053")/* @res "读取数据库失败" */);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000052")/* @res "读取数据库出错：" */
					+ exe.getMessage());
		}
	}

	/**
	 * 检查班次类别设置。 创建日期：(2001-11-22 16:31:13)
	 *
	 * @return int
	 * @throws BusinessException 
	 */
	private int checkHeader() throws BusinessException {

		String lbbm = getTfLbbm().getText();//班次类别编码
		String lbmc = getTfLbmc().getText();//班次类别名称
		String bbjc = getCkbbbjc().getText();//班次类别简称
        String pk_dd = getddflRef().getRefPK();
        String pk_bbz = getbbzflRef().getRefPK();
        String pk_bbbm = getbbmbRef().getRefPK();
		
		
		if (lbbm == null || lbbm.trim().length() == 0) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000066")/* @res "班次类别编码不能为空！" */);
			return -1;
		}
		if (lbmc == null || lbmc.trim().length() == 0) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000067")/* @res "班次类别名称不能为空！" */);
			return -1;
		}
		if (pk_dd == null || pk_dd.trim().length() == 0) {
			showErrorMessage("地点不能为空" );
			return -1;
		}
		if (pk_bbz == null || pk_bbz.trim().length() == 0) {
			showErrorMessage("班种不能为空");
			return -1;
		}
		if (bbjc == null || bbjc.trim().length() == 0) {
			showErrorMessage("班别简称不能为空");
			return -1;
		}
		if (isNull(getTfKqkssj().getText()) || isNull(getTfKqjssj().getText())/*3.4开始结束时间*/
				|| getTfKqkssj().getText().trim().equals("")
				|| getTfKqjssj().getText().trim().equals("")) {
			//showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000070")/* @res "考勤开始、结束时间均不可为空,请检查！"*/);
			showErrorMessage("班别开始、结束时间均不可为空,请检查！");
			return -1;
		}

		//modify5.5
		int timebegindaytype = (Integer)(getChbKqkssj().getSelectdItemValue());
		UFTime kqks = new UFTime(getTfKqkssj().getText());
		int timeenddaytype = (Integer)getChbKqjssj().getSelectdItemValue();
		UFTime kqjs = new UFTime(getTfKqjssj().getText());

		int kqcheck = checkTime(timebegindaytype,kqks, timeenddaytype,kqjs);
		if (kqcheck == 1) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000071")/* @res "考勤开始时间不能小于结束时间" */);
			return -1;
		}

		//刘鹏飞 2011-4-1根据需求原因，注掉一下代码，不进行相关判读
		if (getCkbSfyb().isSelected()) {
			
			/*夜班时，是将夜班参数时间赋给夜班还是考勤时间*/
			/*
			//夜班参数赋给夜班
			getChbYbkssj().setSelectedIndex(Integer.parseInt(this.getParaYbParDefVOs()[0].getPar_value()));
			getTfYbkssj().setText(this.getParaYbParDefVOs()[1].getPar_value());
			getChbYbjssj().setSelectedIndex(Integer.parseInt(this.getParaYbParDefVOs()[2].getPar_value()));
			getTfYbjssj().setText(this.getParaYbParDefVOs()[3].getPar_value());
			*/
			//考勤时间赋值给夜班
			
			getChbYbkssj().setSelectedItem(getChbKqkssj().getSelectedItem());
			getTfYbkssj().setText(getTfKqkssj().getText());
			getChbYbjssj().setSelectedItem(getChbKqjssj().getSelectedItem());
			getTfYbjssj().setText(getTfKqjssj().getText());
			
			if (isNull(getTfYbkssj().getText())
					|| isNull(getTfYbjssj().getText())) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"6017010113", "UPP6017010113-000073")/* @res "夜班开始结束时间不可为空" */);
				return -1;
			}
			
			int ybbegindaytype = (Integer)getChbYbkssj().getSelectdItemValue();
			//int ybbegindaytype = Integer.parseInt(this.getParaYbParDefVOs()[0].getPar_value());
			UFTime ybks = new UFTime(getTfYbkssj().getText());
			int ybenddaytype = (Integer)getChbYbjssj().getSelectdItemValue();
			//int ybenddaytype = Integer.parseInt(this.getParaYbParDefVOs()[2].getPar_value());
			UFTime ybjs = new UFTime(getTfYbjssj().getText());

			int ybcheck = checkTime(ybbegindaytype,ybks, ybenddaytype,ybjs);
			if (ybcheck == 1) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"6017010113", "UPP6017010113-000074")/* @res "夜班开始时间不能小于结束时间"*/);
				return -1;
			}

		} else {
			getTfYbkssj().setText("");
			getTfYbjssj().setText("");
		}

		getTfallowlate().setText( "0");//"允许迟到时限不允许为空！"
//		if (getTfallowlate().getText() == null //0
//				|| getTfallowlate().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000076")/* @res "允许迟到时限不允许为空！" */);
//			return -1;
//		}
		getTfallowearly().setText("0");//允许早退时限不允许为空！
//		if (getTfallowearly().getText() == null
//				|| getTfallowearly().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000077")/* @res "允许早退时限不允许为空！" */);
//			return -1;
//		}
		getTflargelate().setText("0");//最长迟到时限不允许为空！
//		if (getTflargelate().getText() == null
//				|| getTflargelate().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000078")/* @res "最长迟到时限不允许为空！" */);
//			return -1;
//		}
		getTflargeearly().setText("0");//最长早退时限不允许为空
//		if (getTflargeearly().getText() == null
//				|| getTflargeearly().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000079")/* @res "最长早退时限不允许为空！" */);
//			return -1;
//		}
//
//		if(Integer.parseInt(getTfallowlate().getText()) > Integer.parseInt(getTflargelate().getText())){//去掉
//		    showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000028")/* @res "允许迟到时限不能大于最长迟到时限!" */);
//		    return -1;
//		}
//		if(Integer.parseInt(getTfallowearly().getText()) > Integer.parseInt(getTflargeearly().getText())){
//		    showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000036")/* @res "允许早退时限不能大于最长早退时限!" */);
//		    return -1;
//		}
		
		if (getrbButton_clz().isSelected())//0
			if (getTfkgsc().getText() == null
					|| getTfkgsc().getText().trim().length() == 0) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"6017010113", "UPP6017010113-000080")/* @res "迟到或早退时长超过其最长时限时计为旷工的时长不允许为空，请输入！"*/);
				return -1;
			}

		if(getUIChkbovertmrule().isSelected())
		{
			if (getUITFovertmrule().getText().trim().equals("")){//给最大值
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001299")//@res "下班刷卡超过下班时间多少分钟计为加班的值不能为空"
);
			    return -1;
			}
			if (getUITFovertmrule2().getText().trim().equals("")){//不加班
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001300")//@res "加班从下班时间后多少分钟开始计算的值不能为空"
);
			    return -1;
			}

			int overtmeffectbeyond = Integer.valueOf(getUITFovertmrule().getText().trim());
			int overtmbeyond = Integer.valueOf(getUITFovertmrule2().getText().trim());

			if (overtmbeyond>overtmeffectbeyond){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001301")//@res "加班从下班时间后多少分钟开始计算的值不能大于下班刷卡超过下班时间多少分钟计为加班的值"
);
			    return -1;
			}
		}
		if(getUIChkbontmrule().isSelected())
		{
			if (getUITFontmrule().getText().trim().equals("")){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001302")//@res "上班刷卡早于上班时间多少分钟计为加班的值不能为空"
);
			    return -1;
			}
			if (getUITFontmrule2().getText().trim().equals("")){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001303")//@res "加班计算到上班时间前多少分钟截止的值不能为空"
);
			    return -1;
			}

			int ontmeffectbeyond = Integer.valueOf(getUITFontmrule().getText().trim());
			int ontmbeyond = Integer.valueOf(getUITFontmrule2().getText().trim());

			if (ontmbeyond>ontmeffectbeyond){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001304")//@res "加班计算到上班时间前多少分钟截止的值不能大于上班刷卡早于上班时间多少分钟计为加班的值"
);
			    return -1;
			}
		}

		//如果是在增加或修改记录，判断新增纪录中的类别编码、类别名称是否合法
		BclbHeaderVO[] bclbHeaderVOs = null;
		bclbHeaderVOs = TADelegator.getBclb029().queryBclb029AllBclbHeader(Global.getCorpPK(), null);
		if (state == 1) { //0：浏览 1：增加 2：修改
			for (BclbHeaderVO bclbHeaderVO : bclbHeaderVOs) {
				if (bclbHeaderVO.getLbbm().equals(getTfLbbm().getText())) {
					showErrorMessage(ResHelper.getString("6017","UPP6017-001429")/*"班次类别编码已存在，请修改！"*/);
					return -1;
				}
				if (bclbHeaderVO.getLbmc().equals(getTfLbmc().getText())) {
//					showErrorMessage(ResHelper.getString("6017","UPP6017-001430")/*"班次类别名称已存在，请修改！"*/);
//					return -1;
				}
			}
		}
		if (state == 2) { //0：浏览 1：增加 2：修改
			BclbHeaderVO curVO = (BclbHeaderVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
			for (BclbHeaderVO bclbHeaderVO : bclbHeaderVOs) {
				if (!bclbHeaderVO.getPk_bclbid().equals(curVO.getPk_bclbid())&&bclbHeaderVO.getLbmc().equals(getTfLbmc().getText())) {
//					showErrorMessage(ResHelper.getString("6017","UPP6017-001430")/*"班次类别名称已存在，请修改！"*/);
//					return -1;
				}
			}
		}
		//校验如果设置成了默认班别，是否自身互斥
		BclbHeaderVO newvo = new BclbHeaderVO();
		newvo.setTimebeginday((Integer)getChbKqkssj().getSelectdItemValue());
		newvo.setTimeendday((Integer)getChbKqjssj().getSelectdItemValue());
		newvo.setTimebegintime(getTfKqkssj().getText());
		newvo.setTimeendtime(getTfKqjssj().getText());
		if (getCkbSfmrbb().isSelected() && !checkDefaultClassSelfMutex(newvo)){
			showErrorMessage(ResHelper.getString("6017","UPP6017-000581")//@res "默认班别不能够被设置为邻日互斥状态！"
);
			return -1;
		}
		return 0;
	}

	/**
	 * 检查作息时间列表数据设置。 创建日期：(2001-11-22 16:31:32)
	 *
	 * @return int
	 */
	private int checkItems() {

		BclbItemVO[] bivos = getWtVos();

		Vector<BclbItemVO> dataVOs = new Vector<BclbItemVO>();
		for (int i=0; i<bivos.length; i++){
			
			if ((bivos[i].getKssj()!=null) && (bivos[i].getKssj().trim().length()>0)){
				dataVOs.add(bivos[i]);
			}
		}

		boolean sfYb = getCkbSfyb().isSelected();

		//modify5.5 增加夜班设置范围应在考勤范围内的校验
		int kqbegindaytype = (Integer)getChbKqkssj().getSelectdItemValue();
		UFTime kqks = new UFTime(getTfKqkssj().getText());
		int kqenddaytype = (Integer)getChbKqjssj().getSelectdItemValue();
		UFTime kqjs = new UFTime(getTfKqjssj().getText());
//		if (sfYb){
//			int ybbegindaytype = (Integer)getChbYbkssj().getSelectdItemValue();
//			UFTime ybks = new UFTime(getTfYbkssj().getText());
//			int ybenddaytype = (Integer)getChbYbjssj().getSelectdItemValue();
//			UFTime ybjs = new UFTime(getTfYbjssj().getText());
//
//			if (checkTime(kqbegindaytype,kqks,ybbegindaytype,ybks)==1 || checkTime(ybenddaytype,ybjs,kqenddaytype,kqjs)==1){
//				showErrorMessage(ResHelper.getString("6017","UPP6017-000586")//@res "夜班时间范围应在考勤时间范围内!"
//				);
//				return -1;
//			}
//			
//		}

		java.util.Vector<UFDateTime[]> dres = new java.util.Vector<UFDateTime[]>();

		boolean isKsNull = false;
		boolean isJsNull = false;
		//boolean isKt = false;



		for (int i = 0; i < bivos.length; i++) {
			isKsNull = false;
			isJsNull = false;
			//isKt = false;

			String row = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000088"/* @res "{0}行" */, null,
					new String[] { nc.vo.format.Format.indexFormat(i + 1) });
			String ks = bivos[i].getKssj();
			String js = bivos[i].getJssj();

			//modify5.5 增加作息时段设置范围应在考勤范围内的校验
			int wtbegindaytype = bivos[i].getWtbeginday();
			UFTime wtks = new UFTime(ks);
			int wtenddaytype = bivos[i].getWtendday();
			UFTime wtjs = new UFTime(js);


			if (ks == null || ks.trim().equals(""))
				isKsNull = true;
			if (js == null || js.trim().equals(""))
				isJsNull = true;
			if (isKsNull && isJsNull)
				continue;
			if ( (isKsNull && (!isJsNull)) || (!isKsNull && isJsNull)) {
				showErrorMessage(row
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000091")/*
																  * @res
																  * "信息输入不完整！"
																  */);
				return -1;
			}
			if (ks.trim().length() != 8) {
				showErrorMessage(row
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000092")/*
																  * @res
																  * "开始时间格式不正确！"
																  */);
				return -1;
			}
			if (js.trim().length() != 8) {
				showErrorMessage(row
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000093")/*
																  * @res
																  * "结束时间格式不正确！"
																  */);
				return -1;
			}

			if (checkTime(wtbegindaytype,wtks,wtenddaytype,wtjs)==1){
				showErrorMessage(ResHelper.getString("6017","UPP6017-000587")//@res "作息时间段的开始时间应在结束时间之前!"
);
				return -1;
			}

/*			
			if (checkTime(kqbegindaytype,kqks,wtbegindaytype,wtks)==1 || checkTime(wtenddaytype,wtjs,kqenddaytype,kqjs)==1){
				showErrorMessage(ResHelper.getString("6017","UPP6017-000588")//@res "作息时间范围应在考勤时间范围内!"
);
				return -1;
			}
*///上边反过来
			if (checkTime(wtbegindaytype,wtks,kqbegindaytype,kqks)==0 || checkTime(kqenddaytype,kqjs,wtenddaytype,wtjs)==0){
				showErrorMessage(ResHelper.getString("6017","UPP6017-000588")//@res "作息时间范围应在考勤时间范围内!"
);
				return -1;
			}

			//int[] se = { 0, 0 };
			//se[0] = nKsTime;
			//se[1] = nJsTime;

			UFDateTime[] se = new UFDateTime[2];
			se[0] = new UFDateTime(new UFDate("2000-01-02").getDateAfter(wtbegindaytype),wtks);
			se[1] = new UFDateTime(new UFDate("2000-01-02").getDateAfter(wtenddaytype),wtjs);
			dres.addElement(se);
		}

		//检查时间段的顺序start
		for (int i=0; i<dataVOs.size(); i++){
			for (int m = 0 ;m < dataVOs.size() - i - 1 ;m++ ){

				int wtbegindaytype = dataVOs.get(m).getWtbeginday();
				UFTime wtks = new UFTime(dataVOs.get(m).getKssj());
				UFDateTime wtksDateTime = new UFDateTime((wtbegindaytype==1?new UFDate("2000-01-03"):(wtbegindaytype==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks);

				int wtbegindaytype2 = dataVOs.get(m+1).getWtbeginday();
				UFTime wtks2 = new UFTime(dataVOs.get(m+1).getKssj());
				UFDateTime wtksDateTime2 = new UFDateTime((wtbegindaytype2==1?new UFDate("2000-01-03"):(wtbegindaytype2==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks2);

				if (wtksDateTime.compareTo(wtksDateTime2)>0){
					//showErrorMessage("第" + (m+1) + "时间段与" + (m+2) + "时间段顺序不正确!");
					showErrorMessage(nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("6017010113","UPP6017010113-000138",null,new String[] {nc.vo.format.Format.indexFormat(m + 1),nc.vo.format.Format.indexFormat(m + 2) }));
					return -1;
				}
			}
		}
		//检查时间段的顺序end

//		if (sfYb &&dataVOs!=null && dataVOs.size()>0){
//			//是夜班
//			int ybbegindaytype = (Integer)getChbYbkssj().getSelectdItemValue();
//			UFTime ybks = new UFTime(getTfYbkssj().getText());
//			int ybenddaytype = (Integer)getChbYbjssj().getSelectdItemValue();
//			UFTime ybjs = new UFTime(getTfYbjssj().getText());
//
//			if (checkTime(ybbegindaytype,ybks,ybenddaytype,ybjs)==1){
//				showErrorMessage(ResHelper.getString("6017","UPP6017-000589")//@res "夜班开始时间应在夜班结束时间之前!"
//);
//				return -1;
//			}
//
//			//modify5.5 夜班时段应处于第一作息时段的开始与最后作息时段的结束时间范围内！
//
//
//			int wtbegindaytype = dataVOs.get(0).getWtbeginday();
//			UFTime wtks = new UFTime(dataVOs.get(0).getKssj());
//
//			int wtenddaytype2 = dataVOs.get(dataVOs.size()-1).getWtendday();
//			UFTime wtjs2 = new UFTime(dataVOs.get(dataVOs.size()-1).getJssj());
//
//			if (checkTime2(wtbegindaytype,wtks,ybbegindaytype,ybks)==1 || checkTime2(ybenddaytype,ybjs,wtenddaytype2,wtjs2)==1){
//				showErrorMessage(ResHelper.getString("6017","UPP6017-000923")/*"夜班时段应处于第一作息时段的开始与最后作息时段的结束时间范围内！"*/);
//				return -1;
//			}
//		}
		
		
//刘鹏飞 2011-4-1 注掉 关于上下班刷卡的处理
//		//add start
//		for (int q = 0; q < dataVOs.size(); q++) {
//			if (q==0){
//				//如果是有效数据的第一行,上班刷卡没有选
//				if (!((BclbItemVO)dataVOs.get(q)).getCheckInFlag().booleanValue()){//false
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//									"6017010113", "UPP6017010113-000139")
//																	  /* @res
//																	  * "第一时间段上班刷卡必须设置"
//																	  */);
//					return -1;
//				}
//				if (dataVOs.size()==1 && !((BclbItemVO)dataVOs.get(q)).getCheckoutFlag().booleanValue()){
//					//如果是有效数据的第一行,且只有一行有效数据,且下班刷卡没有选
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//									"6017010113", "UPP6017010113-000140")
//																	  /* @res
//																	  * "只有一个时间段上、下班刷卡都必须设置"
//																	  */);
//					return -1;
//				}
//			}else{
//				if (!((BclbItemVO)dataVOs.get(q)).getCheckInFlag().booleanValue()&&((BclbItemVO)dataVOs.get(q-1)).getCheckoutFlag().booleanValue()){
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113",	"UPP6017010113-000141", null, new String[]{Integer.toString(q+1)})
//																	  /* @res
//																	  * "第"+(q+1)+"时间段上班没有刷卡，则上一时间段下班也不应刷卡"
//																	  */);
//					return -1;
//				}
//				if (((BclbItemVO)dataVOs.get(q)).getCheckInFlag().booleanValue()&&!((BclbItemVO)dataVOs.get(q-1)).getCheckoutFlag().booleanValue()){
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113",	"UPP6017010113-000142", null, new String[]{Integer.toString(q+1)})
//																	  /* @res
//																	  * "第"+(q+1)+"时间段上班刷卡，则上一时间段下班也应刷卡"
//																	  */);
//					return -1;
//				}
//				if (q==dataVOs.size()-1){
//					if (!((BclbItemVO)dataVOs.get(q)).getCheckoutFlag().booleanValue()){
//						showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//										"6017010113", "UPP6017010113-000143")
//																		  /* @res
//																		  * "最后时间段下班刷卡必须设置"
//																		  */);
//						return -1;
//					}
//
//				}
//			}
//		}

		//add end

		//int nTotalTime = 0;
		for (int i = 0; i < dres.size(); i++) {
			UFDateTime iks = ((UFDateTime[]) (dres.elementAt(i)))[0];
			UFDateTime ijs = ((UFDateTime[]) (dres.elementAt(i)))[1];
			for (int k = 0; k < i; k++) {
				UFDateTime jks = ((UFDateTime[]) (dres.elementAt(k)))[0];
				UFDateTime jjs = ((UFDateTime[]) (dres.elementAt(k)))[1];
				//if ((jks >= iks && jks < ijs) || (iks >= jks && iks < jjs)) {
				if (iks.compareTo(jjs) < 0 && ijs.compareTo(jks) > 0) {
					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000105",
									null,new String[] {nc.vo.format.Format.indexFormat(k + 1),nc.vo.format.Format.indexFormat(i + 1) })/*
																		   * @res
																		   * "{0}行与{1}行的作息时间段出现了交叉！"
																		   */);
					return -1;
				}
			}
			//nTotalTime += (ijs - iks);
		}

		//Add tzj
		//如果考勤，作息时间不能超过4个
		if (dres.size() > 4) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000107")/* @res "考勤班次作息时段数不能大于4" */);
			return -1;
		}
		return 0;
	}

	/**
	 * 返回 ContentPanel 特性值。
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getContentPanel() {
		if (ivjContentPanel == null) {
			try {
				ivjContentPanel = new nc.ui.pub.beans.UIPanel();
				ivjContentPanel.setName("ContentPanel");
				ivjContentPanel.setLayout(null);
				//getContentPanel().add(getBclbTablePane(),
				//		getBclbTablePane().getName());
				getContentPanel().add(getBclbPane(), getBclbPane().getName());
				getContentPanel().add(getUIPanel1(), getUIPanel1().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjContentPanel;
	}



	/**
	 * 此处插入方法说明。 创建日期：(2001-11-3 9:02:26)
	 *
	 * @return nc.ui.pub.beans.UILabelLayout
	 */
	private UILabelLayout getHeadLabelLayout() {
		if (m_headLabelLayout == null) {
			m_headLabelLayout = new UILabelLayout(1, 1);
			m_headLabelLayout.setTop(6);
		}
		return m_headLabelLayout;
	}

	/**
	 * 返回 HeadPanel 特性值。
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getHeadPanel() {
		if (ivjHeadPanel == null) {
			try {
				ivjHeadPanel = new nc.ui.pub.beans.UIPanel();
				ivjHeadPanel.setName("HeadPanel");
				ivjHeadPanel.setPreferredSize(new java.awt.Dimension(0, 35));
				ivjHeadPanel.setLayout(null);
				ivjHeadPanel.setVisible(false);
				getHeadPanel().add(getUILabel4(), getUILabel4().getName());
				getHeadPanel().add(getRfpFactory(), getRfpFactory().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjHeadPanel;
	}

	/**
	 * 返回 RfpFactory 特性值，DataSource类中要用到。
	 *
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	public nc.ui.pub.beans.UIRefPane getRfpFactory() {
		if (ivjRfpFactory == null) {
			try {
				ivjRfpFactory = new nc.ui.pub.beans.UIRefPane();
				ivjRfpFactory.setName("RfpFactory");
				ivjRfpFactory.setLocation(60, 6);
				//ivjRfpFactory.setRefNodeName("部门档案");
				//ivjRfpFactory.setRefNodeName("部门档案HR");
				ivjRfpFactory.setRefNodeName(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010405", "UPP6017010405-000059")/* @res "部门档案" */);

				ivjRfpFactory.setEditable(false);
				ivjRfpFactory.setEnabled(false);
				ivjRfpFactory.setVisible(true);
				// user code begin {1}
				//ivjRfpFactory.setRefNodeName("库存组织");
				ivjRfpFactory.setRefNodeName(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001825")/* @res "库存组织" */);
				ivjRfpFactory.setEditable(true);
				ivjRfpFactory.setEnabled(true);
				ivjRfpFactory.setButtonFireEvent(true);
				ivjRfpFactory.addValueChangedListener(this);

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRfpFactory;
	}

	/**
	 * 返回 UILabel5 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel5() {
		if (ivjUILabel5 == null) {
			try {
				ivjUILabel5 = new nc.ui.pub.beans.UILabel();
				ivjUILabel5.setName("UILabel5");
				ivjUILabel5
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000109")/* @res "是否考勤" */);
				ivjUILabel5.setBounds(259, 42, 52, 22);
				// user code begin {1}

				ivjUILabel5.setVisible(false);

				//liupf add begin 
				//ivjUILabel5.setVisible(true);
//				liupf add end
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel5;
	}

	/**
	 * 将有效的作息时间段排序，用于计算中间点5.5已改
	 * @param dataVOs
	 */
	private void sort2(Vector dataVOs){

		//将有效的作息时间段排序，用于计算中间点 start
		for (int i=0; i<dataVOs.size(); i++){
			for (int j = 0 ;j < dataVOs.size() - i - 1 ;j++ ){
				int wtbegindaytype = ((BclbItemVO)dataVOs.get(j)).getWtbeginday();
				UFTime wtks = new UFTime(((BclbItemVO)dataVOs.get(j)).getKssj());
				UFDateTime wtksDateTime = new UFDateTime((wtbegindaytype==1?new UFDate("2000-01-03"):(wtbegindaytype==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks);

				int wtbegindaytype2 = ((BclbItemVO)dataVOs.get(j+1)).getWtbeginday();
				UFTime wtks2 = new UFTime(((BclbItemVO)dataVOs.get(j+1)).getKssj());
				UFDateTime wtksDateTime2 = new UFDateTime((wtbegindaytype2==1?new UFDate("2000-01-03"):(wtbegindaytype2==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks2);

				if (wtksDateTime.after(wtksDateTime2)){
					BclbItemVO tmp = (BclbItemVO)dataVOs.get(j);
					dataVOs.set(j,(BclbItemVO)dataVOs.get(j+1));
					dataVOs.set(j+1,tmp);
				}
			}
		}
		for (int i=0; i<dataVOs.size(); i++){
			((BclbItemVO)dataVOs.get(i)).setTimeid(new Integer(i + 1));
		}
		//将有效的作息时间段排序，用于计算中间点 end
	}

	/**
	 * 检查时间段的顺序是否正确
	 * @param dataVOs
	 */
	private void checkSort(Vector dataVOs){}


	/**
	 * 将有效的作息时间段排序，用于计算中间点5.5已改
	 * @param tmpbtvs
	 * @param bhvo
	 */
	private void sort3(BclbItemVO[] tmpbtvs){

		//将有效的作息时间段排序，用于计算中间点 start
		for (int i=0; i<tmpbtvs.length; i++){
			for (int j = 0 ;j < tmpbtvs.length - i - 1 ;j++ ){
				int wtbegindaytype = tmpbtvs[j].getWtbeginday();
				UFTime wtks = new UFTime(tmpbtvs[j].getKssj());
				UFDateTime wtksDateTime = new UFDateTime((wtbegindaytype==1?new UFDate("2000-01-03"):(wtbegindaytype==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks);

				int wtbegindaytype2 = tmpbtvs[j+1].getWtbeginday();
				UFTime wtks2 = new UFTime(tmpbtvs[j+1].getKssj());
				UFDateTime wtksDateTime2 = new UFDateTime((wtbegindaytype2==1?new UFDate("2000-01-03"):(wtbegindaytype2==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks2);

				if (wtksDateTime.after(wtksDateTime2)){
					BclbItemVO tmp = tmpbtvs[j];
					tmpbtvs[j] = tmpbtvs[j+1];
					tmpbtvs[j+1] = tmp;
				}
			}
		}
		//将有效的作息时间段排序，用于计算中间点 end
	}

	// 国际化用到的变量
	private final String BEGIN = nc.vo.pub.CommonConstant.BEGIN_MARK;

	private final String END = nc.vo.pub.CommonConstant.END_MARK;

	private UICheckBox ivjCkbKqsfkt = null;

	//add start
	private UICheckBox ivjCkbSfmrbb = null;
	private UICheckBox ivjCkbSfzf = null;
	//add end

	private UICheckBox ivjCkbSfyb = null;

	private UICheckBox ivjCkbSFFC = null;

	private UICheckBox ivjCkbsfyxwc = null;

	private UILabel ivjLbKqjssj = null;

	private UILabel ivjLbKqkssj = null;

	private UILabel ivjLbKqsfkt1 = null;

	private UILabel ivjLbSfyb = null;
	
	private UILabel ivjLbbbjc = null;

	private UILabel ivjLbYbjssj = null;

	private UILabel ivjLbYbkssj = null;

	private UILabel ivjLbMemo = null;//班次说明
	
	private UILabel ivjLbYbkssj1 = null;

	private UILabel ivjLbMin = null;

	private UILabel ivjLbMin1 = null;

	private UILabel ivjLbMin2 = null;

	private UILabel ivjLbMin3 = null;

	private UILabel ivjLbHour = null;

	private UILabel ivjLbYbkssj11 = null;

	private UILabel ivjLbSFFC = null;

	private UILabel ivjLbYbkssj111 = null;

	private UILabel ivjLbYbkssj2 = null;

	private UILabel ivjLbYbkssj21 = null;

	private UILabel ivjLbsfyxwc = null;

	private UIRadioButton ivjrbButton_clz = null;

	private UIRadioButton ivjrdButton_Auto = null;

	private UITextField ivjTfallowearly = null;

	private UITextField ivjTfallowlate = null;

	private UITextField ivjTfkgsc = null;

	private UITextField ivjTfKqjssj = null;

	private UITextField ivjTfKqkssj = null;

	private nc.ui.pub.beans.UIComboBox ivjUIComboBoxKqkssj = null;

	private nc.ui.pub.beans.UIComboBox ivjUIComboBoxYbkssj = null;

	private nc.ui.pub.beans.UIComboBox ivjUIComboBoxYbjssj = null;

	private nc.ui.pub.beans.UIComboBox ivjUIComboBoxKqjssj = null;

	private UITextField ivjTflargeearly = null;

	private UITextField ivjTflargelate = null;

	private UITextField ivjTfYbjssj = null;

	private UITextField ivjTfYbkssj = null;

	private UIPanel ivjUIpnlScore = null;

	private UIRefPane ivjUIrefPsn = null;

	private nc.ui.pub.beans.border.UITitledBorder m_tbGenerateScore = null; //

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (e.getSource() == getCkbSfyb()) {
			if (getCkbSfyb().isSelected()) {
				getTfYbkssj().setEnabled(true);
				getTfYbjssj().setEnabled(true);
				getChbYbkssj().setEnabled(true);
				getChbYbjssj().setEnabled(true);
				
				//刘鹏飞新增，判断考勤开始和结束时间是否在夜班范围之内
				// 夜班开始时间《考勤结束时间 《 夜班结束时间 || 夜班开始时间《考勤开始时间 《 夜班结束时间 正常 不提示 ，否则提示
				if( 	//考勤结束 < 夜班结束
					(	checkTime(Integer.parseInt(getChbKqjssj().getSelectdItemValue().toString()),new UFTime(getTfKqjssj().getText()),
						Integer.parseInt(getParaYbParDefVOs()[2].getPar_value()),new UFTime(getParaYbParDefVOs()[3].getPar_value())) == 0
					&& //考勤结束 > 夜班开始
						checkTime(Integer.parseInt(getChbKqjssj().getSelectdItemValue().toString()),new UFTime(getTfKqjssj().getText()),
							Integer.parseInt(getParaYbParDefVOs()[0].getPar_value()),new UFTime(getParaYbParDefVOs()[1].getPar_value())) == 1
					)
					||

					(	checkTime(Integer.parseInt(getChbKqkssj().getSelectdItemValue().toString()),new UFTime(getTfKqkssj().getText()),
						Integer.parseInt(getParaYbParDefVOs()[2].getPar_value()),new UFTime(getParaYbParDefVOs()[3].getPar_value())) == 0
					&& 
						checkTime(Integer.parseInt(getChbKqkssj().getSelectdItemValue().toString()),new UFTime(getTfKqkssj().getText()),
							Integer.parseInt(getParaYbParDefVOs()[0].getPar_value()),new UFTime(getParaYbParDefVOs()[1].getPar_value())) == 1
					)
					||
					//考勤开始 < 夜班开始  考勤结束 》 夜班结束  
					(	checkTime(Integer.parseInt(getChbKqkssj().getSelectdItemValue().toString()),new UFTime(getTfKqkssj().getText()),
						Integer.parseInt(getParaYbParDefVOs()[0].getPar_value()),new UFTime(getParaYbParDefVOs()[1].getPar_value())) == 0
					&& 
						checkTime(Integer.parseInt(getChbKqjssj().getSelectdItemValue().toString()),new UFTime(getTfKqjssj().getText()),
							Integer.parseInt(getParaYbParDefVOs()[2].getPar_value()),new UFTime(getParaYbParDefVOs()[3].getPar_value())) == 1
					)
					
				){}
				else	
				{
					
					showErrorMessage("考勤时间范围应在夜班时间范围内!");
					getCkbSfyb().setSelected(false);
					/*
					showErrorMessage(ResHelper.getString("6017","UPP6017-000586")//@res "考勤时间范围应在夜班时间范围内!"
							);*/
				}
				
			} else {
				getTfYbkssj().setEnabled(false);
				getTfYbjssj().setEnabled(false);
				getChbYbkssj().setEnabled(false);
				getChbYbjssj().setEnabled(false);
				getChbYbkssj().setSelectedIndex(0);
				getChbYbjssj().setSelectedIndex(0);
				getTfYbkssj().setText(null);
				getTfYbjssj().setText(null);
			}
		} else if (e.getSource() == getCkbSFFC()) {
			sffcClick();
		}else if (e.getSource() == getrbButton_clz()
				|| e.getSource() == getrbButton_Auto()) {
			if (getrbButton_clz().isSelected())
				getTfkgsc().setEnabled(true);
			else
				getTfkgsc().setEnabled(false);
		}

		/**
		 *  Modified by Young 2005-09-22 Start
		 */
		else{
			 //added by liuhongjie 2005-04-19
		 	if (e.getSource() == getUIChkbovertmrule()){
		  		if (getUIChkbovertmrule().isSelected()){
		   			getUIRefPane1overtype().setEnabled(true);
		   			getUITFovertmrule().setEnabled(true);
		   			getUITFovertmrule2().setEnabled(true);
		  		}
		  		else{
		   			getUIRefPane1overtype().setEnabled(false);
		   			getUITFovertmrule().setEnabled(false);
		   			getUITFovertmrule2().setEnabled(false);
		  		}
		 	}
			//added by myl 2005-6-28
		 	if (e.getSource() == getUIChkbontmrule()){
		  		if (getUIChkbontmrule().isSelected()){
		   			getUIRefPane1ontype().setEnabled(true);
		   			getUITFontmrule().setEnabled(true);
		   			getUITFontmrule2().setEnabled(true);
		  		}
		  		else{
		   			getUIRefPane1ontype().setEnabled(false);
		   			getUITFontmrule().setEnabled(false);
		   			getUITFontmrule2().setEnabled(false);
		  		}
		 	}
		}
		/**
		 *  Modified by Young 2005-09-22 End
		 */


	}

	private void sffcClick(){
		if (getCkbSFFC().isSelected()) {
			getTfLbbm().setEnabled(false);
			getTfLbmc().setEnabled(false);
			getCkbbbjc().setEnabled(false);
			getbbzflRef().setEnabled(false);
			getddflRef().setEnabled(false);
			getbbmbRef().setEnabled(false);
			getCkbSfmrbb().setEnabled(false);
			getCkbSfzf().setEnabled(false);
			getChbKqkssj().setEnabled(false);
			getTfKqkssj().setEnabled(false);
			getChbKqjssj().setEnabled(false);
			getTfKqjssj().setEnabled(false);
			
			getTfMemo().setEnabled(false);
			
			getCkbSfyb().setEnabled(false);

			getChbYbkssj().setEnabled(false);
			getTfYbkssj().setEnabled(false);
			getChbYbjssj().setEnabled(false);
			getTfYbjssj().setEnabled(false);

			getCkbsfyxwc().setEnabled(false);
			getTfallowlate().setEnabled(false);
			getTfallowearly().setEnabled(false);
			getTflargelate().setEnabled(false);
			getTflargeearly().setEnabled(false);
			getrbButton_clz().setEnabled(false);
			getrbButton_Auto().setEnabled(false);
			getTfkgsc().setEnabled(false);
			getUIChkbovertmrule().setEnabled(false);
			getUIChkbontmrule().setEnabled(false);

			getUITFovertmrule().setEnabled(false);
			getUITFovertmrule2().setEnabled(false);
			getUITFontmrule().setEnabled(false);
			getUITFontmrule2().setEnabled(false);

			getWtModel().setState(0);

		} else {
			if (state == 2){
				getTfLbbm().setEnabled(false);
			}else {
				getTfLbbm().setEnabled(true);
			}
			getTfLbmc().setEnabled(true);
			getCkbbbjc().setEnabled(true);
			getbbzflRef().setEnabled(true);
			getddflRef().setEnabled(true);
			getbbmbRef().setEnabled(true);
			getCkbSfmrbb().setEnabled(true);
			getCkbSfzf().setEnabled(true);
			getChbKqkssj().setEnabled(true);
			getTfKqkssj().setEnabled(true);
			getChbKqjssj().setEnabled(true);
			getTfKqjssj().setEnabled(true);
			
			getTfMemo().setEnabled(true);
			
			getCkbSfyb().setEnabled(true);
			if (getCkbSfyb().isSelected()){
				getChbYbkssj().setEnabled(true);
				getTfYbkssj().setEnabled(true);
				getChbYbjssj().setEnabled(true);
				getTfYbjssj().setEnabled(true);
			}else {
				getChbYbkssj().setEnabled(false);
				getTfYbkssj().setEnabled(false);
				getChbYbjssj().setEnabled(false);
				getTfYbjssj().setEnabled(false);
			}


			getCkbsfyxwc().setEnabled(true);
			getTfallowlate().setEnabled(true);
			getTfallowearly().setEnabled(true);
			getTflargelate().setEnabled(true);
			getTflargeearly().setEnabled(true);
			getrbButton_clz().setEnabled(true);
			getrbButton_Auto().setEnabled(true);

			if (getrbButton_clz().isSelected()){
				getTfkgsc().setEnabled(true);
			}else {
				getTfkgsc().setEnabled(false);
			}

			getUIChkbovertmrule().setEnabled(true);
			if (getUIChkbovertmrule().isSelected()){
				getUITFovertmrule().setEnabled(true);
				getUITFovertmrule2().setEnabled(true);
			}else {
				getUITFovertmrule().setEnabled(false);
				getUITFovertmrule2().setEnabled(false);
			}

			getUIChkbontmrule().setEnabled(true);
			if (getUIChkbontmrule().isSelected()){
				getUITFontmrule().setEnabled(true);
				getUITFontmrule2().setEnabled(true);
			}else {
				getUITFontmrule().setEnabled(false);
				getUITFontmrule2().setEnabled(false);
			}

			getWtModel().setState(2);
		}
	}

	/**
	 * 0-正常 1-开始时间点在结束时间点之后或相同
	 */
	private int checkTime(int timebegindaytype,UFTime timebegintime, int timeenddaytype,UFTime timeendtime) {

		//新插入(或修改)的班别的开始时间点和结束时间点，假设为一固定时间点
		UFDate timebeginday = new UFDate("2000-01-02").getDateAfter(timebegindaytype);//timebegindaytype
		UFDateTime beginDayTime = new UFDateTime(timebeginday,timebegintime);//开始时间点

		UFDate timeendday = new UFDate("2000-01-02").getDateAfter(timeenddaytype);//timeenddaytype
		UFDateTime endDayTime = new UFDateTime(timeendday,timeendtime);//结束时间点

		if (beginDayTime.compareTo(endDayTime)>=0){
			return 1;
		}
		return 0;

	}

	/**
	 * 0-正常 1-开始时间点在结束时间点之后
	 */
	private int checkTime2(int timebegindaytype,UFTime timebegintime, int timeenddaytype,UFTime timeendtime) {

		//新插入(或修改)的班别的开始时间点和结束时间点，假设为一固定时间点
		UFDate timebeginday = new UFDate("2000-01-02").getDateAfter(timebegindaytype);//timebegindaytype
		UFDateTime beginDayTime = new UFDateTime(timebeginday,timebegintime);//开始时间点

		UFDate timeendday = new UFDate("2000-01-02").getDateAfter(timeenddaytype);//timeenddaytype
		UFDateTime endDayTime = new UFDateTime(timeendday,timeendtime);//结束时间点

		if (beginDayTime.compareTo(endDayTime)>0){
			return 1;
		}
		return 0;

	}

	/**
	 * 返回 CkbSfyb 特性值。
	 *
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UICheckBox getCkbSfyb() {
		if (ivjCkbSfyb == null) {
			try {
				ivjCkbSfyb = new nc.ui.pub.beans.UICheckBox();
				ivjCkbSfyb.setName("CkbSfyb");
				ivjCkbSfyb.setPreferredSize(new java.awt.Dimension(20, 22));
				ivjCkbSfyb.setText("");
				ivjCkbSfyb.setBounds(118, 89, 20, 22);
				ivjCkbSfyb.setVisible(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCkbSfyb;
	}

	/**
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	private nc.ui.pub.beans.UICheckBox getCkbsfyxwc() {
		if (ivjCkbsfyxwc == null) {
			try {
				ivjCkbsfyxwc = new nc.ui.pub.beans.UICheckBox();
				ivjCkbsfyxwc.setName("Ckbsfyxwc");
				ivjCkbsfyxwc.setPreferredSize(new java.awt.Dimension(20, 22));
				ivjCkbsfyxwc.setText("");
				ivjCkbsfyxwc.setSelected(true);
				ivjCkbsfyxwc.setBounds(402, 88, 20, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCkbsfyxwc;
	}
	/**
	 * 返回 CkbSfyb 特性值。
	 *
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getCkbbbjc() {
		if (ivjCkbBbjc == null) {
			try {
				ivjCkbBbjc = new nc.ui.pub.beans.UITextField();
				ivjCkbBbjc.setName("CkbBbjc");
				ivjCkbBbjc.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjCkbBbjc.setText("");
				ivjCkbBbjc.setMinimumSize(new java.awt.Dimension(4, 20));
				// user code begin {1}
				ivjCkbBbjc.setMaxLength(50);
				ivjCkbBbjc.setBounds(118, 89, 120, 22);
				ivjCkbBbjc.setDelStr(nc.vo.bd.mmpub.MMConstant.RESTICT_STRING);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCkbBbjc;
	}

	/**
	 * 取得用户登录时的工厂编码 创建日期：(2001-05-29 18:33:37)
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getFactoryCode() {
		return getRfpFactory().getRefPK();
	}

	/**
	 * 返回 LbKqjssj 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbKqjssj() {
		if (ivjLbKqjssj == null) {
			try {
				ivjLbKqjssj = new nc.ui.pub.beans.UILabel();
				ivjLbKqjssj.setName("LbKqjssj");
				ivjLbKqjssj.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbKqjssj
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113","UPP6017010113-000153")/* @res"班别结束时间"*/);//"common", "UC000-0003290")/* @res "考勤结束时间" */);
				ivjLbKqjssj.setBounds(280, 68, 80, 22);
				// user code begin {1}
				ivjLbKqjssj.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbKqjssj;
	}

	/**
	 * 返回 LbKqkssj 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbKqkssj() {
		if (ivjLbKqkssj == null) {
			try {
				ivjLbKqkssj = new nc.ui.pub.beans.UILabel();
				ivjLbKqkssj.setName("LbKqkssj");
				ivjLbKqkssj.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbKqkssj
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113","UPP6017010113-000152")/* @res"班别开始时间"*/);//"common", "UC000-0003286")/* @res "考勤开始时间" */);
				ivjLbKqkssj.setBounds(38, 68, 80, 22);
				// user code begin {1}
				ivjLbKqkssj.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbKqkssj;
	}

	/**
	 * 返回 LbKqsfkt1 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbKqsfkt1() {
		if (ivjLbKqsfkt1 == null) {
			try {
				ivjLbKqsfkt1 = new nc.ui.pub.beans.UILabel();
				ivjLbKqsfkt1.setName("LbKqsfkt1");
				ivjLbKqsfkt1
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000110")/*
																  * @res
																  * "该班别是否半日班"
																  */);
				ivjLbKqsfkt1.setBounds(253, 286, 97, 18);
				// user code begin {1}
				ivjLbKqsfkt1.setVisible(false);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbKqsfkt1;
	}

	/**
	 * 返回 LbSfyb 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbSfyb() {
		if (ivjLbSfyb == null) {
			try {
				ivjLbSfyb = new nc.ui.pub.beans.UILabel();
				ivjLbSfyb.setName("LbSfyb");
				ivjLbSfyb.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"common", "UC000-0002370")/* @res "是否夜班" */);
				ivjLbSfyb.setBounds(38, 93, 52, 22);
				ivjLbSfyb.setVisible(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbSfyb;
	}
	/**
	 * 返回 LbSfyb 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbBbjc() {
		if (ivjLbbbjc == null) {
			try {
				ivjLbbbjc = new nc.ui.pub.beans.UILabel();
				ivjLbbbjc.setName("LbSfyb");
				ivjLbbbjc.setText("班别简称");
				ivjLbbbjc.setBounds(38, 93, 52, 22);
				ivjLbbbjc.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbbbjc;
	}

	/**
	 * 返回 LbYbjssj 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbYbjssj() {
		if (ivjLbYbjssj == null) {
			try {
				ivjLbYbjssj = new nc.ui.pub.beans.UILabel();
				ivjLbYbjssj.setName("LbYbjssj");
				ivjLbYbjssj.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbYbjssj
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"common", "UC000-0001398")/* @res "夜班结束时间" */);
				ivjLbYbjssj.setBounds(280, 114, 76, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbYbjssj;
	}

	/**
	 * 返回 LbYbkssj 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbYbkssj() {
		if (ivjLbYbkssj == null) {
			try {
				ivjLbYbkssj = new nc.ui.pub.beans.UILabel();
				ivjLbYbkssj.setName("LbYbkssj");
				ivjLbYbkssj.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbYbkssj
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"common", "UC000-0001396")/* @res "夜班开始时间" */);
				ivjLbYbkssj.setBounds(38, 114, 74, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbYbkssj;
	}

	/**
	 * 返回 LbYbkssj1 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbYbkssj1() {
		if (ivjLbYbkssj1 == null) {
			try {
				ivjLbYbkssj1 = new nc.ui.pub.beans.UILabel();
				ivjLbYbkssj1.setName("LbYbkssj1");
				ivjLbYbkssj1.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbYbkssj1
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000111")/*
																  * @res
																  * "允许迟到时限"
																  */);
				ivjLbYbkssj1.setBounds(38, 140, 80, 22);
				// user code begin {1}
				ivjLbYbkssj1.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbYbkssj1;
	}

	/**
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getLbMin() {
		if (ivjLbMin == null) {
			try {
				ivjLbMin = new nc.ui.pub.beans.UILabel();
				ivjLbMin.setName("LbMin");
				ivjLbMin.setPreferredSize(new java.awt.Dimension(30, 22));
				ivjLbMin.setText(ResHelper.getString("6017","UPP6017-000149")//@res "分钟"
);
				ivjLbMin.setBounds(210, 142, 30, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLbMin;
	}

	/**
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getLbMin1() {
		if (ivjLbMin1 == null) {
			try {
				ivjLbMin1 = new nc.ui.pub.beans.UILabel();
				ivjLbMin1.setName("LbMin1");
				ivjLbMin1.setPreferredSize(new java.awt.Dimension(30, 22));
				ivjLbMin1.setText(ResHelper.getString("6017","UPP6017-000149")//@res "分钟"
);
				ivjLbMin1.setBounds(452, 140, 30, 22);//360, 165, 80, 20
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLbMin1;
	}

	/**
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getLbMin2() {
		if (ivjLbMin2 == null) {
			try {
				ivjLbMin2 = new nc.ui.pub.beans.UILabel();
				ivjLbMin2.setName("LbMin2");
				ivjLbMin2.setPreferredSize(new java.awt.Dimension(30, 22));
				ivjLbMin2.setText(ResHelper.getString("6017","UPP6017-000149")//@res "分钟"
);
				ivjLbMin2.setBounds(210, 168, 30, 22);//118, 193, 80, 20
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLbMin2;
	}

	/**
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getLbMin3() {
		if (ivjLbMin3 == null) {
			try {
				ivjLbMin3 = new nc.ui.pub.beans.UILabel();
				ivjLbMin3.setName("LbMin3");
				ivjLbMin3.setPreferredSize(new java.awt.Dimension(30, 22));
				ivjLbMin3.setText(ResHelper.getString("6017","UPP6017-000149")//@res "分钟"
);
				ivjLbMin3.setBounds(452, 166, 30, 22);//360, 191, 80, 20
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLbMin3;
	}

	/**
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getLbHour() {
		if (ivjLbHour == null) {
			try {
				ivjLbHour = new nc.ui.pub.beans.UILabel();
				ivjLbHour.setName("LbHour");
				ivjLbHour.setPreferredSize(new java.awt.Dimension(30, 22));
				ivjLbHour.setText(ResHelper.getString("6017","UPP6017-000061")//@res "小时"
);
				//ivjLbHour.setBounds(452, 190, 30, 22);
				ivjLbHour.setBounds(472, 90, 110, 22);//360, 18, 120, 20
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLbHour;
	}

	/**
	 * 返回 LbYbkssj11 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbYbkssj11() {
		if (ivjLbYbkssj11 == null) {
			try {
				ivjLbYbkssj11 = new nc.ui.pub.beans.UILabel();
				ivjLbYbkssj11.setName("LbYbkssj11");
				ivjLbYbkssj11.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbYbkssj11
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000112")/*
																  * @res
																  * "最长迟到时限"
																  */);
				ivjLbYbkssj11.setBounds(38, 167, 80, 22);
				// user code begin {1}
				ivjLbYbkssj11.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbYbkssj11;
	}

	private nc.ui.pub.beans.UILabel getLbSFFC() {
		if (ivjLbSFFC == null) {
			try {
				ivjLbSFFC = new nc.ui.pub.beans.UILabel();
				ivjLbSFFC.setName("LbSFFC");
				ivjLbSFFC.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbSFFC.setText(ResHelper.getString("6017","UPP6017-000924")/*"是否封存"*/);
				ivjLbSFFC.setBounds(38, 190, 80, 22);
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbSFFC;
	}

	private nc.ui.pub.beans.UICheckBox getCkbSFFC() {
		if (ivjCkbSFFC == null) {
			try {
				ivjCkbSFFC = new nc.ui.pub.beans.UICheckBox();
				ivjCkbSFFC.setName("CkbSFFC");
				ivjCkbSFFC.setPreferredSize(new java.awt.Dimension(20, 22));
				ivjCkbSFFC.setText("");
				ivjCkbSFFC.setBounds(118, 190, 120, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCkbSFFC;
	}

	/**
	 * 返回 LbYbkssj111 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbYbkssj111() {
		if (ivjLbYbkssj111 == null) {
			try {
				ivjLbYbkssj111 = new nc.ui.pub.beans.UILabel();
				ivjLbYbkssj111.setName("LbYbkssj111");
				ivjLbYbkssj111.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbYbkssj111
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000113")/* @res "小时" */);
				ivjLbYbkssj111.setBounds(371, 23, 27, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbYbkssj111;
	}

	/**
	 * 返回 LbYbkssj2 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbYbkssj2() {
		if (ivjLbYbkssj2 == null) {
			try {
				ivjLbYbkssj2 = new nc.ui.pub.beans.UILabel();
				ivjLbYbkssj2.setName("LbYbkssj2");
				ivjLbYbkssj2.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbYbkssj2
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000114")/*
																  * @res
																  * "允许早退时限"
																  */);
				ivjLbYbkssj2.setBounds(280, 141, 80, 22);
				// user code begin {1}
				ivjLbYbkssj2.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbYbkssj2;
	}

	/**
	 * 返回 LbYbkssj21 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getLbYbkssj21() {
		if (ivjLbYbkssj21 == null) {
			try {
				ivjLbYbkssj21 = new nc.ui.pub.beans.UILabel();
				ivjLbYbkssj21.setName("LbYbkssj21");
				ivjLbYbkssj21.setPreferredSize(new java.awt.Dimension(80, 22));
				ivjLbYbkssj21
						.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000115")/*
																  * @res
																  * "最长早退时限"
																  */);
				ivjLbYbkssj21.setBounds(280, 166, 80, 22);
				// user code begin {1}
				ivjLbYbkssj21.setILabelType(UILabel.STYLE_BLACKBLUE);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLbYbkssj21;
	}

	/**
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getLbsfyxwc() {
		if (ivjLbsfyxwc == null) {
			try {
				ivjLbsfyxwc = new nc.ui.pub.beans.UILabel();
				ivjLbsfyxwc.setName("Lbsfyxwc");
				ivjLbsfyxwc.setText(ResHelper.getString("6017010113","UPT6017010113-000009")//@res "中途是否允许外出"
);
				ivjLbsfyxwc.setBounds(280, 90, 100, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLbsfyxwc;
	}

	/**
	 * 返回 UIRadioButton_clz1 特性值。
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIRadioButton getrbButton_Auto() {
		if (ivjrdButton_Auto == null) {
			try {
				ivjrdButton_Auto = new nc.ui.pub.beans.UIRadioButton();
				ivjrdButton_Auto.setName("rdButton_Auto");
				ivjrdButton_Auto.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000116")/* @res "由系统自动计算旷工工时"*/);
				ivjrdButton_Auto.setBounds(50, 26, 158, 19);
				ivjrdButton_Auto.setActionCommand(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000117")/* @res "系统自动统计旷工工时" */);
				// user code begin {1}
				ivjrdButton_Auto.addActionListener(this);
				ivjrdButton_Auto.setSelected(true);
				javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup();
				bg.add(ivjrdButton_Auto);
				bg.add(getrbButton_clz());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjrdButton_Auto;
	}

	/**
	 * 返回 UIRadioButton_clz 特性值。
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	/* 警告：此方法将重新生成。 */
	nc.ui.pub.beans.UIRadioButton getrbButton_clz() {
		if (ivjrbButton_clz == null) {
			try {
				ivjrbButton_clz = new nc.ui.pub.beans.UIRadioButton();
				ivjrbButton_clz.setName("rbButton_clz");
				ivjrbButton_clz.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000118")/* @res "计为旷工" */);
				ivjrbButton_clz.setBounds(233, 24, 74, 18);
				// user code begin {1}
				ivjrbButton_clz.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjrbButton_clz;
	}

	/**
	 * 返回 TfYbkssj2 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	nc.ui.pub.beans.UITextField getTfallowearly() {
		if (ivjTfallowearly == null) {
			try {
				ivjTfallowearly = new nc.ui.pub.beans.UITextField();
				ivjTfallowearly.setName("Tfallowearly");
				ivjTfallowearly
						.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfallowearly.setBounds(360, 140, 90, 20);
				ivjTfallowearly.setTextType(UITextType.TextInt);
				// user code begin {1}
				ivjTfallowearly.setMinValue(0);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfallowearly;
	}

	/**
	 * 返回 TfYbkssj1 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	nc.ui.pub.beans.UITextField getTfallowlate() {
		if (ivjTfallowlate == null) {
			try {
				ivjTfallowlate = new nc.ui.pub.beans.UITextField();
				ivjTfallowlate.setName("Tfallowlate");
				ivjTfallowlate.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfallowlate.setBounds(118, 142, 90, 20);
				ivjTfallowlate.setTextType(UITextType.TextInt);
				// user code begin {1}
				ivjTfallowlate.setMinValue(0);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfallowlate;
	}

	/**
	 * 返回 Tflargeearly1 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	nc.ui.pub.beans.UITextField getTfkgsc() {
		if (ivjTfkgsc == null) {
			try {
				ivjTfkgsc = new nc.ui.pub.beans.UITextField();
				ivjTfkgsc.setName("Tfkgsc");
				//ivjTfkgsc.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfkgsc.setBounds(309, 24, 56, 20);
				ivjTfkgsc.setTextType(UITextType.TextDbl);
				// user code begin {1}
				ivjTfkgsc.setNumPoint(2);
				ivjTfkgsc.setMinValue(new UFDouble(0).doubleValue());
				ivjTfkgsc.setMaxValue(new UFDouble(1000).doubleValue());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfkgsc;
	}

	/**
	 * 返回 TfKqjssj 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfKqjssj() {
		if (ivjTfKqjssj == null) {
			try {
				ivjTfKqjssj = new nc.ui.pub.beans.UITextField();
				ivjTfKqjssj.setName("TfKqjssj");
				ivjTfKqjssj.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfKqjssj.setBounds(420, 67, 60, 20);
				ivjTfKqjssj.setTextType(UITextType.TextDbl);
				// user code begin {1}
				ivjTfKqjssj.setTextType(UITextType.TextTime);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfKqjssj;
	}

	/**
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	private nc.ui.pub.beans.UIComboBox getChbKqkssj() {
		if (ivjUIComboBoxKqkssj== null) {
			try {
				ivjUIComboBoxKqkssj = new nc.ui.pub.beans.UIComboBox();
				ivjUIComboBoxKqkssj.setName("UIComboBoxKqkssj");
				ivjUIComboBoxKqkssj.setBounds(118, 68, 60, 22);//138, 93, 80, 20
				ivjUIComboBoxKqkssj.setVisible(true);
				ivjUIComboBoxKqkssj.addItem(new DefaultConstEnum(new Integer(0),ResHelper.getString("6017","UPP6017-000329")//@res "当日"
));
				ivjUIComboBoxKqkssj.addItem(new DefaultConstEnum(new Integer(-1),ResHelper.getString("6017","UPP6017-000330")//@res "前一日"
));
				ivjUIComboBoxKqkssj.addItem(new DefaultConstEnum(new Integer(1),ResHelper.getString("6017","UPP6017-000331")//@res "后一日"
));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIComboBoxKqkssj;
	}

	/**
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	private nc.ui.pub.beans.UIComboBox getChbYbkssj() {
		if (ivjUIComboBoxYbkssj== null) {
			try {
				ivjUIComboBoxYbkssj = new nc.ui.pub.beans.UIComboBox();
				ivjUIComboBoxYbkssj.setName("UIComboBoxYbkssj");
				ivjUIComboBoxYbkssj.setBounds(118, 114, 60, 22);//138, 93, 80, 20
				ivjUIComboBoxYbkssj.setVisible(true);

				ivjUIComboBoxYbkssj.addItem(new DefaultConstEnum(new Integer(0),ResHelper.getString("6017","UPP6017-000329")//@res "当日"
));
				ivjUIComboBoxYbkssj.addItem(new DefaultConstEnum(new Integer(-1),ResHelper.getString("6017","UPP6017-000330")//@res "前一日"
));
				ivjUIComboBoxYbkssj.addItem(new DefaultConstEnum(new Integer(1),ResHelper.getString("6017","UPP6017-000331")//@res "后一日"
));

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIComboBoxYbkssj;
	}

	/**
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	private nc.ui.pub.beans.UIComboBox getChbYbjssj() {
		if (ivjUIComboBoxYbjssj== null) {
			try {
				ivjUIComboBoxYbjssj = new nc.ui.pub.beans.UIComboBox();
				ivjUIComboBoxYbjssj.setName("UIComboBoxYbjssj");
				ivjUIComboBoxYbjssj.setBounds(360, 114, 60, 22);//360, 92, 60, 22
				ivjUIComboBoxYbjssj.setVisible(true);

				ivjUIComboBoxYbjssj.addItem(new DefaultConstEnum(new Integer(0),ResHelper.getString("6017","UPP6017-000329")//@res "当日"
));
				ivjUIComboBoxYbjssj.addItem(new DefaultConstEnum(new Integer(-1),ResHelper.getString("6017","UPP6017-000330")//@res "前一日"
));
				ivjUIComboBoxYbjssj.addItem(new DefaultConstEnum(new Integer(1),ResHelper.getString("6017","UPP6017-000331")//@res "后一日"
));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIComboBoxYbjssj;
	}

	/**
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	private nc.ui.pub.beans.UIComboBox getChbKqjssj() {
		if (ivjUIComboBoxKqjssj== null) {
			try {
				ivjUIComboBoxKqjssj = new nc.ui.pub.beans.UIComboBox();
				ivjUIComboBoxKqjssj.setName("UIComboBoxKqjssj");
				ivjUIComboBoxKqjssj.setBounds(360, 67, 60, 22);//387, 92, 80, 20
				ivjUIComboBoxKqjssj.setVisible(true);

				ivjUIComboBoxKqjssj.addItem(new DefaultConstEnum(new Integer(0),ResHelper.getString("6017","UPP6017-000329")//@res "当日"
));
				ivjUIComboBoxKqjssj.addItem(new DefaultConstEnum(new Integer(-1),ResHelper.getString("6017","UPP6017-000330")//@res "前一日"
));
				ivjUIComboBoxKqjssj.addItem(new DefaultConstEnum(new Integer(1),ResHelper.getString("6017","UPP6017-000331")//@res "后一日"
));

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIComboBoxKqjssj;
	}

	/**
	 * 返回 TfKqkssj 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfKqkssj() {
		if (ivjTfKqkssj == null) {
			try {
				ivjTfKqkssj = new nc.ui.pub.beans.UITextField();
				ivjTfKqkssj.setName("TfKqkssj");
				ivjTfKqkssj.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfKqkssj.setBounds(178, 68, 60, 20);
				ivjTfKqkssj.setTextType(UITextType.TextDbl);
				// user code begin {1}
				ivjTfKqkssj.setTextType(UITextType.TextTime);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfKqkssj;
	}

	/**
	 * 返回 Tflargeearly 特性值。
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	nc.ui.pub.beans.UITextField getTflargeearly() {
		if (ivjTflargeearly == null) {
			try {
				ivjTflargeearly = new nc.ui.pub.beans.UITextField();
				ivjTflargeearly.setName("Tflargeearly");
				ivjTflargeearly
						.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTflargeearly.setBounds(360, 166, 90, 20);
				ivjTflargeearly.setTextType(UITextType.TextInt);
				// user code begin {1}
				ivjTflargeearly.setMinValue(0);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTflargeearly;
	}

	/**
	 * 返回 Tflargelate 特性值。
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	nc.ui.pub.beans.UITextField getTflargelate() {
		if (ivjTflargelate == null) {
			try {
				ivjTflargelate = new nc.ui.pub.beans.UITextField();
				ivjTflargelate.setName("Tflargelate");
				ivjTflargelate.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTflargelate.setBounds(118, 168, 90, 20);
				ivjTflargelate.setTextType(UITextType.TextInt);
				// user code begin {1}
				ivjTflargelate.setMinValue(0);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTflargelate;
	}

	/**
	 * 返回 TfYbjssj 特性值。
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfYbjssj() {
		if (ivjTfYbjssj == null) {
			try {
				ivjTfYbjssj = new nc.ui.pub.beans.UITextField();
				ivjTfYbjssj.setName("TfYbjssj");
				ivjTfYbjssj.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfYbjssj.setBounds(420, 114, 60, 20);
				ivjTfYbjssj.setTextType(UITextType.TextDbl);
				// user code begin {1}
				ivjTfYbjssj.setTextType(UITextType.TextTime);
				
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfYbjssj;
	}

	/**
	 * 返回 TfYbkssj 特性值。
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getTfYbkssj() {
		if (ivjTfYbkssj == null) {
			try {
				ivjTfYbkssj = new nc.ui.pub.beans.UITextField();
				ivjTfYbkssj.setName("TfYbkssj");
				ivjTfYbkssj.setPreferredSize(new java.awt.Dimension(80, 20));
				ivjTfYbkssj.setBounds(178, 114, 60, 20);
				ivjTfYbkssj.setTextType(UITextType.TextDbl);
				// user code begin {1}
				ivjTfYbkssj.setTextType(UITextType.TextTime);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTfYbkssj;
	}

	/**
	 * 返回 UIpnlScore 特性值。
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIpnlScore() {
		if (ivjUIpnlScore == null) {
			try {
				ivjUIpnlScore = new nc.ui.pub.beans.UIPanel();
				ivjUIpnlScore.setName("UIpnlScore");
				ivjUIpnlScore.setPreferredSize(new java.awt.Dimension(0, 100));
				ivjUIpnlScore.setLayout(null);
				ivjUIpnlScore.setBounds(38, 218, 445, 53);
				ivjUIpnlScore.add(getUIrefPsn(), getUIrefPsn().getName());
				ivjUIpnlScore.add(getrbButton_clz(),getrbButton_clz().getName());
				ivjUIpnlScore.add(getrbButton_Auto(),getrbButton_Auto().getName());
				ivjUIpnlScore.add(getTfkgsc(), getTfkgsc().getName());
				ivjUIpnlScore.add(getLbYbkssj111(),getLbYbkssj111().getName());
				// user code begin {1}
				ivjUIpnlScore.setBorder(getUItitledBorder());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIpnlScore;
	}

	/**
	 * 返回 UIrefPsn 特性值。
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIRefPane getUIrefPsn() {
		if (ivjUIrefPsn == null) {
			try {
				ivjUIrefPsn = new nc.ui.pub.beans.UIRefPane();
				ivjUIrefPsn.setName("UIrefPsn");
				ivjUIrefPsn.setBounds(673, 66, 37, 22);
				//ivjUIrefPsn.setRefNodeName("人员档案");
				ivjUIrefPsn.setRefNodeName(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000144")/* @res "人员档案" */);
				ivjUIrefPsn.setVisible(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIrefPsn;
	}

	/**
	 "请选择生成范围"的UITitledBorder
	 */
	private nc.ui.pub.beans.border.UITitledBorder getUItitledBorder() {
		if (m_tbGenerateScore == null) {
			try {
				m_tbGenerateScore = new nc.ui.pub.beans.border.UITitledBorder(
						nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113",
								"UPP6017010113-000119")/*@res "当迟到或早退时长超过其允许最长时限时:"*/);
				//m_tbGenerateScore.setTitleFont(new java.awt.Font("dialog", 1, 12));

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return m_tbGenerateScore;
	}

	/**
	 * 此处插入方法说明。
	 * 创建日期：(02-7-6 20:19:22)
	 */
	private boolean isNull(String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * Invoked when an item has been selected or deselected.
	 * The code written for this method performs the operations
	 * that need to occur when an item is selected (or deselected).
	 */
	public void itemStateChanged(java.awt.event.ItemEvent e) {
		
	}

	/**
	 * 参照内容变化事件监听者必须实现的接口方法
	 * @param event valueChangedEvent 参照内容变化事件
	 */
	public void valueChanged(nc.ui.pub.beans.ValueChangedEvent event) {
		getBclbTablePane().getTable().getSelectionModel()
				.removeListSelectionListener(this);
		initData();
		mrow = -1;
		getBclbTablePane().getTable().getSelectionModel()
				.addListSelectionListener(this);
	}
	public boolean onClosing() {
		if (boConfirm.isEnabled()) {
			int ret = MessageDialog.showYesNoCancelDlg(this,
					nc.ui.hr.ml.HRPubRes.getMsgTip(), nc.ui.hr.ml.HRPubRes
							.getMsgSaveOrNot());
			if (ret == MessageDialog.ID_YES) {
				if (onConfirm())
					return true;
				return false;
			} else if (ret == MessageDialog.ID_NO) {
				return true;

			} else if (ret == MessageDialog.ID_CANCEL) {
				return false;
			}

		}
		return true;
	}

	/**
	 *  Modified by Young 2005-09-22 Start
	 */
	/**
	 * 返回 rdButton_Auto 特性值。
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	/* 警告：此方法将重新生成。 */
	public nc.ui.pub.beans.UIRadioButton getrdButton_Auto() {
		if (ivjrdButton_Auto == null) {
			try {
				ivjrdButton_Auto = new nc.ui.pub.beans.UIRadioButton();
				ivjrdButton_Auto.setName("rdButton_Auto");
//				ivjrdButton_Auto.setToolTipText("由系统自动统计旷工工时");
				ivjrdButton_Auto.setToolTipText(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000109")/* @res "由系统自动统计旷工工时" */);
				ivjrdButton_Auto.setText(NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000145")/* @res "由系统自动计算旷工工时" */);
				ivjrdButton_Auto.setBounds(5, 7, 158, 19);
				//ivjrdButton_Auto.setActionCommand("系统自动统计旷工工时");
				ivjrdButton_Auto.setActionCommand(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000146")/* @res "系统自动统计旷工工时" */);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjrdButton_Auto;
	}

	/**
	 * 返回 UIChkbontmrule 特性值。
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UICheckBox getUIChkbontmrule() {
		if (ivjUIChkbontmrule == null) {
			try {
				ivjUIChkbontmrule = new nc.ui.pub.beans.UICheckBox();
				ivjUIChkbontmrule.setName("UIChkbontmrule");
				ivjUIChkbontmrule.setBounds(38, 299, 20, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIChkbontmrule;
	}

	/**
	 * 返回 UIChkbovertmrule 特性值。
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UICheckBox getUIChkbovertmrule() {
		if (ivjUIChkbovertmrule == null) {
			try {
				ivjUIChkbovertmrule = new nc.ui.pub.beans.UICheckBox();
				ivjUIChkbovertmrule.setName("UIChkbovertmrule");
				ivjUIChkbovertmrule.setBounds(38, 277, 20, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIChkbovertmrule;
	}

	/**
	 * 返回 UILbontm21 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILbontm21() {
		if (ivjUILbontm21 == null) {
			try {
				ivjUILbontm21 = new nc.ui.pub.beans.UILabel();
				ivjUILbontm21.setName("UILbontm21");
				ivjUILbontm21.setText(ResHelper.getString("6017","UPP6017-001305")//@res "分钟计为加班，加班计算到上班时间前"
);
				ivjUILbontm21.setBounds(208, 302, 205, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILbontm21;
	}

	private UILabel ivjUILbontm212 = null;
	private nc.ui.pub.beans.UILabel getUILbontm212() {
		if (ivjUILbontm212 == null) {
			try {
				ivjUILbontm212 = new nc.ui.pub.beans.UILabel();
				ivjUILbontm212.setName("UILbontm212");
				ivjUILbontm212.setText(ResHelper.getString("6017","UPP6017-001306")//@res "分钟截止"
);
				ivjUILbontm212.setBounds(443, 302, 50, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILbontm212;
	}

	/**
	 * 返回 UILbovertm1 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILbovertm1() {
		if (ivjUILbovertm1 == null) {
			try {
				ivjUILbovertm1 = new nc.ui.pub.beans.UILabel();
				ivjUILbovertm1.setName("UILbovertm1");
				ivjUILbovertm1.setText(ResHelper.getString("6017","UPP6017-001307")//@res "下班刷卡超过下班时间"
 );
				ivjUILbovertm1.setBounds(57, 278, 120, 19);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILbovertm1;
	}

	/**
	 * 返回 UILbovertm11 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILbovertm11() {
		if (ivjUILbovertm11 == null) {
			try {
				ivjUILbovertm11 = new nc.ui.pub.beans.UILabel();
				ivjUILbovertm11.setName("UILbovertm11");
				ivjUILbovertm11.setText(ResHelper.getString("6017","UPP6017-001308")//@res "上班刷卡早于上班时间"
);
				ivjUILbovertm11.setBounds(57, 301, 120, 19);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILbovertm11;
	}

	/**
	 * 返回 UILbovertm21 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILbovertm21() {
		if (ivjUILbovertm21 == null) {
			try {
				ivjUILbovertm21 = new nc.ui.pub.beans.UILabel();
				ivjUILbovertm21.setName("UILbovertm21");
				ivjUILbovertm21.setText(ResHelper.getString("6017","UPP6017-001309")//@res "分钟计为加班，加班从下班时间后"
);
				ivjUILbovertm21.setBounds(208, 275, 180, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILbovertm21;
	}

	private UILabel ivjUILbovertm212 = null;
	private nc.ui.pub.beans.UILabel getUILbovertm212() {
		if (ivjUILbovertm212 == null) {
			try {
				ivjUILbovertm212 = new nc.ui.pub.beans.UILabel();
				ivjUILbovertm212.setName("UILbovertm212");
				ivjUILbovertm212.setText(ResHelper.getString("6017","UPP6017-001310")//@res "分钟开始计算"
);
				ivjUILbovertm212.setBounds(419, 275, 80, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILbovertm212;
	}



	/**
	 * 返回 UIRefPane1 特性值。
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIRefPane getUIRefPane1ontype() {
		if (ivjUIRefPane1ontype == null) {
			try {
				ivjUIRefPane1ontype = new nc.ui.pub.beans.UIRefPane();
				ivjUIRefPane1ontype.setName("UIRefPane1ontype");
				ivjUIRefPane1ontype.setBounds(13, 304, 123, 20);
				// user code begin {1}
				ivjUIRefPane1ontype.setIsCustomDefined(true);

	   			ivjUIRefPane1ontype.setButtonFireEvent(true);
	   			ivjUIRefPane1ontype.setRefInputType(0/** 名称*/);

	   			ivjUIRefPane1ontype.setReturnCode(true);
	   			ivjUIRefPane1ontype.setRefModel(new nc.ui.hr.comp.ref_tbm.OtTypeRefModel());
	   			ivjUIRefPane1ontype.setWhereString(" dr=0 and pk_corp = '" + nc.ui.hr.global.Global.getCorpPK()+"'"+" and fathertimeitem = 'overtimeclass ' ");
				//chexz
	   			ivjUIRefPane1ontype.setVisible(false);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIRefPane1ontype;
	}

	/**
	 * 返回 UIRefPane1overtype 特性值。
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIRefPane getUIRefPane1overtype() {
		if (ivjUIRefPane1overtype == null) {
			try {
				ivjUIRefPane1overtype = new nc.ui.pub.beans.UIRefPane();
				ivjUIRefPane1overtype.setName("UIRefPane1overtype");
				ivjUIRefPane1overtype.setBounds(373, 306, 123, 20);
				// user code begin {1}
	   			ivjUIRefPane1overtype.setIsCustomDefined(true);

	   			ivjUIRefPane1overtype.setButtonFireEvent(true);
	   			ivjUIRefPane1overtype.setRefInputType(0/** 名称*/);

	   			ivjUIRefPane1overtype.setReturnCode(true);
	   			ivjUIRefPane1overtype.setRefModel(new nc.ui.hr.comp.ref_tbm.OtTypeRefModel());
	   			ivjUIRefPane1overtype.setWhereString(" dr=0 and pk_corp = '" + nc.ui.hr.global.Global.getCorpPK()+"'"+" and fathertimeitem = 'overtimeclass ' ");
	   			//chexz
	   			ivjUIRefPane1overtype.setVisible(false);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIRefPane1overtype;
	}

	/**
	 * 返回 UITFontmrule 特性值。
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getUITFontmrule() {
		if (ivjUITFontmrule == null) {
			try {
				ivjUITFontmrule = new nc.ui.pub.beans.UITextField();
				ivjUITFontmrule.setName("UITFontmrule");
				ivjUITFontmrule.setBounds(178, 302, 28, 20);
				// user code begin {1}
				ivjUITFontmrule.setTextType(UITextType.TextDbl);
				ivjUITFontmrule.setNumPoint(0);
				ivjUITFontmrule.setText("0");
				ivjUITFontmrule.setMaxLength(3);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUITFontmrule;
	}

	private UITextField ivjUITFontmrule2 = null;
	private nc.ui.pub.beans.UITextField getUITFontmrule2() {
		if (ivjUITFontmrule2 == null) {
			try {
				ivjUITFontmrule2 = new nc.ui.pub.beans.UITextField();
				ivjUITFontmrule2.setName("UITFontmrule2");
				ivjUITFontmrule2.setBounds(413, 302, 28, 20);
				// user code begin {1}
				ivjUITFontmrule2.setTextType(UITextType.TextDbl);
				ivjUITFontmrule2.setNumPoint(0);
				ivjUITFontmrule2.setText("0");
				ivjUITFontmrule2.setMaxLength(3);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUITFontmrule2;
	}

	/**
	 * 返回 UITFovertmrule 特性值。
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField getUITFovertmrule() {
		if (ivjUITFovertmrule == null) {
			try {
				ivjUITFovertmrule = new nc.ui.pub.beans.UITextField();
				ivjUITFovertmrule.setName("UITFovertmrule");
				ivjUITFovertmrule.setBounds(178, 277, 28, 20);
				// user code begin {1}
	  		 	ivjUITFovertmrule.setTextType(UITextType.TextDbl);
	   			ivjUITFovertmrule.setNumPoint(0);
	   			ivjUITFovertmrule.setText("0");
	   			ivjUITFovertmrule.setMaxLength(3);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUITFovertmrule;
	}

	private UITextField ivjUITFovertmrule2 = null;
	private nc.ui.pub.beans.UITextField getUITFovertmrule2() {
		if (ivjUITFovertmrule2 == null) {
			try {
				ivjUITFovertmrule2 = new nc.ui.pub.beans.UITextField();
				ivjUITFovertmrule2.setName("UITFovertmrule2");
				ivjUITFovertmrule2.setBounds(389, 277, 28, 20);
				// user code begin {1}
				ivjUITFovertmrule2.setTextType(UITextType.TextDbl);
				ivjUITFovertmrule2.setNumPoint(0);
				ivjUITFovertmrule2.setText("0");
				ivjUITFovertmrule2.setMaxLength(3);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUITFovertmrule2;
	}


	/**
	 *  Modified by Young 2005-09-22 End
	 */

	/**
	 *  Modified by Young 2005-09-26 Start
	 */
	/**
	 * 得到考勤规则
	 * 创建日期：(2005-7-19 16:15:00)
	 * @return nc.vo.tbm.tbm_005.TimeruleVO
	 */
	private void initTimeruleVO() {
		try{
			String pk_corp = nc.ui.hr.global.Global.getCorpPK();
			this.ruleVO = TADelegator.getTbmCall().queryTimeRule(pk_corp);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private boolean computeGzsj(BclbHeaderVO newvo,BclbItemVO[] items2){
		int totleSec = 0;
		List<ITimeScope> timeScopesList = new ArrayList<ITimeScope>();
		if(items2.length == 0 ){
			showErrorMessage("请输入作息时间!");
			return false;
		}
		for (int i =0 ;i<items2.length ;i++){
			int wtbegindaytype = items2[i].getWtbeginday();
			UFTime startTime = new UFTime(items2[i].getKssj());
			UFDateTime wtksDateTime = new UFDateTime(new UFDate("2000-01-02").getDateAfter(wtbegindaytype),startTime);//wtbegindaytype

			int wtenddaytype = items2[i].getWtendday();
			UFTime endTime = new UFTime(items2[i].getJssj());
			UFDateTime wtjsDateTime = new UFDateTime(new UFDate("2000-01-02").getDateAfter(wtenddaytype),endTime);//wtenddaytype

			ITimeScope aTimeScope = new DefaultTimeScope(wtksDateTime,wtjsDateTime);
			timeScopesList.add(aTimeScope);

			int restSec = items2[i].getWtresttime()==null?0:items2[i].getWtresttime().intValue()*60;
			int midSec = UFDateTime.getSecondsBetween(wtksDateTime, wtjsDateTime);

			if (midSec<=restSec){
				showErrorMessage(ResHelper.getString("6017","UPP6017-000591")//@res "作息时段的休息时长大于工作时长！"
);
				return false;
			}
			totleSec += (midSec-restSec);
		}
		double totleHour = totleSec/3600.0;

		//int li_decbits = PubUISet.getTimeRuleDsl(nc.ui.hr.global.Global.getCorpPK()) * (-1);
		//nc.ui.tbm.tbm_021.TBMFormat.formatStr(new UFDouble(totleHour).toString(), li_decbits);

		//这里不取考勤规则处设置的考勤小数位数，固定为２，四舍五入
		int li_decbits = 2;
		newvo.setGzsj(new UFDouble(totleHour).setScale(li_decbits, nc.itf.hr.ta.util.PubUISet.roundPoint()));

		if (newvo.getGzsj().doubleValue() <= 0) {//作息时间需大于17秒-刘鹏飞-2011-4-6
			//showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000054")/* @res "数据不合法！" */);
			showErrorMessage("作息时间应大于17秒!");
			return false;
		}

		//计算夜班工作时间
		if (newvo.getIncludenightshift().booleanValue()){
			int nightbegindaytype = newvo.getNightbeginday();
			UFTime nightStartTime = new UFTime(newvo.getNightbegintime());
			UFDateTime nighntksDateTime = new UFDateTime(new UFDate("2000-01-02").getDateAfter(nightbegindaytype),nightStartTime);

			int nightenddaytype = newvo.getNightendday();
			UFTime nightEndTime = new UFTime(newvo.getNightendtime());
			UFDateTime nightjsDateTime = new UFDateTime(new UFDate("2000-01-02").getDateAfter(nightenddaytype),nightEndTime);

			ITimeScope[] timeScopes = TimeScopeUtils.intersectionTimeScopes(new ITimeScope[]{new DefaultTimeScope(nighntksDateTime,nightjsDateTime)},timeScopesList.toArray(new ITimeScope[0]));
			long nightSec = TimeScopeUtils.getLength(timeScopes);

			newvo.setNightgzsj(new UFDouble(nightSec/3600.0).setScale(li_decbits, nc.itf.hr.ta.util.PubUISet.roundPoint()));
		}
		//计算夜班工作时间

		return true;
	}

	private boolean checkDefaultClassSelfMutex(BclbHeaderVO newvo){

		int timebegindaytypeUpdate = newvo.getTimebeginday().intValue();
		UFDate timebegindayUpdate = new UFDate("2000-01-02").getDateAfter(timebegindaytypeUpdate);//timebegindaytypeUpdate
		UFTime timebegintimeUpdate = new UFTime(newvo.getTimebegintime());
		UFDateTime beginDayTimeUpdate = new UFDateTime(timebegindayUpdate,timebegintimeUpdate);//开始时间点

		int timeenddaytypeUpdate = newvo.getTimeendday().intValue();
		UFDate timeenddayUpdate = new UFDate("2000-01-02").getDateAfter(timeenddaytypeUpdate);//timeenddaytypeUpdate
		UFTime timeendtimeUpdate = new UFTime(newvo.getTimeendtime());
		UFDateTime endDayTimeUpdate = new UFDateTime(timeenddayUpdate,timeendtimeUpdate);//结束时间点

		UFDateTime endDayTimeUpdateBefore = new UFDateTime(endDayTimeUpdate.getDate().getDateBefore(1),endDayTimeUpdate.getUFTime());
		if (endDayTimeUpdateBefore.compareTo(beginDayTimeUpdate)>0){
			return false;
		}
		return true;
	}

	private FuncParser getFuncParser() {
		if (funcParser == null) {
			// 得到公式解析器，用于假日增加或修改后工作日历随之修改用
			String[] stFuncSql = new String[10];
			stFuncSql[0] = "'" + Global.getLogDate().toString() + "'";
			stFuncSql[1] = "'" + Global.getCurYear() + "'";
			stFuncSql[2] = "'" + Global.getCurMonth() + "'";
			stFuncSql[3] = "'" + Global.getWaYear() + "'";
			stFuncSql[4] = "'" + Global.getWAPeriod() + "'";
			stFuncSql[5] = "'" + Global.getCorpname() + "'";
			stFuncSql[6] = "'" + Global.getCorpPK() + "'";
			stFuncSql[7] = "'" + Global.getUserName() + "'";
			stFuncSql[8] = "'" + Global.getUserID() + "'";
			stFuncSql[9] = "'" + TAClientUtil.getCurSYSDate() + "'";
			funcParser = new FuncParser(true, stFuncSql);
			// 得到公式解析器
		}
		return funcParser;
	}

	public void valueChanged(TreeSelectionEvent treeselectionevent) {
		if (state != 0) // 如果是浏览状态，返回
			return;
		showHintMessage("");

		try {
			if (getBclb().getSelectionPath() != null) {
				if (((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject() instanceof BclbHeaderVO) {
					BclbHeaderVO bhvo = (BclbHeaderVO) ((DefaultMutableTreeNode)(getBclb().getSelectionPath().getLastPathComponent())).getUserObject();
					if (bhvo == null) {
						return;
					}
					// 设置右上侧显示数值
					getTfLbbm().setText(bhvo.getLbbm());
					getTfLbmc().setText(bhvo.getLbmc());
					getTfMemo().setText(bhvo.getMemo());
					getTfGzsj().setText(bhvo.getGzsj().toString());
					//getcbxSfkq().setSelected(
					//((Boolean) (getBclbModel().getValueAt(selectRow,
					// 3))).booleanValue());

					//Add tzj
					getTfKqkssj().setText(bhvo.getTimebegintime());
					getTfKqjssj().setText(bhvo.getTimeendtime());
					getTfYbkssj().setText(bhvo.getNightbegintime());
					getTfYbjssj().setText(bhvo.getNightendtime());
					if (bhvo.getAllowearly() != null)
						getTfallowearly().setText(
								bhvo.getAllowearly().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
					else
						getTfallowearly().setText("");
					if (bhvo.getAllowlate() != null)
						getTfallowlate().setText(
								bhvo.getAllowlate().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
					else
						getTfallowlate().setText("");
					if (bhvo.getLargeearly() != null)
						getTflargeearly().setText(
								bhvo.getLargeearly().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
					else
						getTflargeearly().setText("");
					if (bhvo.getLargelate() != null)
						getTflargelate().setText(
								bhvo.getLargelate().setScale(0, nc.itf.hr.ta.util.PubUISet.roundPoint()).toString());
					else
						getTflargelate().setText("");

					/**
					 *  Modified by Young 2005-09-22 Start
					 */
					////////////////////////////////////////
					if (bhvo.getUseovertmrule() != null && bhvo.getUseovertmrule().booleanValue() ){
						getUIChkbovertmrule().setSelected(true);
						getUITFovertmrule().setText(bhvo.getOvertmeffectbeyond().toString());
						getUITFovertmrule2().setText(bhvo.getOvertmbeyond().toString());
						getUIRefPane1overtype().setPK(bhvo.getOvertmruletype());
					} else
					{
						getUIChkbovertmrule().setSelected(false);
						getUITFovertmrule().setText("");
						getUITFovertmrule2().setText("");
						getUIRefPane1overtype().setPK(null);
					}
					//add by myl
					if (bhvo.getUseontmrule() != null && bhvo.getUseontmrule().booleanValue() ){
						getUIChkbontmrule().setSelected(true);
						getUITFontmrule().setText(bhvo.getOntmeffectbeyond().toString());
						getUITFontmrule2().setText(bhvo.getOntmbeyond().toString());
						getUIRefPane1ontype().setPK(bhvo.getOntmruletype());
					} else
					{
						getUIChkbontmrule().setSelected(false);
						getUITFontmrule().setText("");
						getUITFontmrule2().setText("");
						getUIRefPane1ontype().setPK(null);
					}
					////////////////////////////////////////////
					/**
					 *  Modified by Young 2005-09-22 End
					 */

					//5.5getCkbKqsfkt().setSelected(bhvo.getTimenextdayend() == null ? false : bhvo.getTimenextdayend().booleanValue());
					getCkbSfmrbb().setSelected(
							bhvo.getDefaultFlag() == null ? false : bhvo
									.getDefaultFlag().booleanValue());
					getCkbSfzf().setSelected(
							bhvo.getIscancel() == null ? false : bhvo
									.getIscancel().booleanValue());
					//5.5getCkbYbsfkt().setSelected(bhvo.getNightnextdayend() == null ? false : bhvo.getNightnextdayend().booleanValue());

					//add5.5
					getChbKqkssj().setSelectedItem(bhvo.getTimebeginday());
					getChbKqjssj().setSelectedItem(bhvo.getTimeendday());
					getChbYbkssj().setSelectedItem(bhvo.getNightbeginday());
					getChbYbjssj().setSelectedItem(bhvo.getNightendday());
					getCkbsfyxwc().setSelected(bhvo.getIsallowout().booleanValue());
					getCkbSFFC().setSelected(bhvo.getIsblocked().booleanValue());

					getCkbSfyb().setSelected(
							bhvo.getIncludenightshift() == null ? false : bhvo
									.getIncludenightshift().booleanValue());

					getTfkgsc().setText(
							bhvo.getKghours() == null ? "" : bhvo.getKghours()
									.toString());

					if (bhvo.getIsautokg() == null
							|| bhvo.getIsautokg().trim().equals("Y")) {
						getrbButton_Auto().setSelected(true);
					} else {
						getrbButton_clz().setSelected(true);
					}

					//add by caizl
					
					getCkbbbjc().setText(bhvo.getLbjc());
					getbbzflRef().setPK(bhvo.getPk_bbz());
					getddflRef().setPK(bhvo.getPk_dd());
					getbbmbRef().setPK(bhvo.getPk_bbmb());
					getBbflRef().setPK(bhvo.getBclbfl());

					//类别编码lbbm
					if (bhvo.getLbbm() != null && !bhvo.getLbbm().equals("")) {

						// 查询出作息时间数据
						BclbItemVO[] tmpbtvs = TADelegator.getBclb029().queryAllBclbItemBclb029(bhvo);

						/**
						 *  Added by Young 2005-07-19 Start
						 */
						getWtModel().setDataVOs(tmpbtvs);
						/**
						 *  Added by Young 2005-07-19  End
						 */

						// 按时间排序
						//sort(tmpbtvs);

						sort3(tmpbtvs);

						BclbItemVO[] btvs = new BclbItemVO[4];
						for (int i = 0; i < 4; i++) {
							if (i > tmpbtvs.length - 1) { // 处理没有取出数据
								BclbItemVO btvo = new BclbItemVO();
								btvo.setPk_corp(getUnitCode());
								btvo.setGcbm(getFactoryCode());
								btvs[i] = btvo;
							} else {
								btvs[i] = tmpbtvs[i];

							}
						}
						// 在作息时间表中显示关于作息时间数据
						getWtTablePane().getTable().editingStopped(null);
						getWtModel().clearTable();
						getWtModel().addVO(btvs);
						boModify.setEnabled(true);
						boDel.setEnabled(true);
					} else {
						getWtModel().clearTable();
						getWtModel().addVO(btvos);
						boModify.setEnabled(true);
						boDel.setEnabled(true);
					}
				}else {
					boModify.setEnabled(false);
					boDel.setEnabled(false);
					clearBclb();
				}
			}
			updateButton(boDel);
			updateButton(boModify);
		} catch (Exception exe) {
			reportException(exe);
			showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000053")/* @res "读取数据库失败" */);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000052")/* @res "读取数据库出错：" */
					+ exe.getMessage());
		}


	}
	
	
	private ParDefVO[] parYbDefVOs = null;
	
	private ParDefVO[] getParaYbParDefVOs(){
	
		ParValueVO[] pvvos = null;
		if(parYbDefVOs == null){
			BusinessDelegator business = new BusinessDelegator();
			try {
				parYbDefVOs = (ParDefVO[])business.queryByCondition(ParDefVO.class, " PAR_GROUP = 'HRTBM'  and par_code in ('TBT','TET','TBD','TED') order by par_code ");//"  and  "
				pvvos     = (ParValueVO[])business.queryByCondition(ParValueVO.class, "   par_code in ('TBT','TET','TBD','TED') and pk_corp='"+this.getCorpPrimaryKey()+"' order by par_code ");//"  and  "
				
				if(parYbDefVOs.length != 4){
					showErrorMessage("夜班参数个数有问题，请调整！");
					return null;
				}
				
				for(int i = 0 ; i<pvvos.length; i++){
					if(pvvos[i].getPar_code().equals("TBD")){
						parYbDefVOs[0].setPar_value(pvvos[i].getPar_value());
					}
					if(pvvos[i].getPar_code().equals("TBT")){
						parYbDefVOs[1].setPar_value(pvvos[i].getPar_value());
					}
					if(pvvos[i].getPar_code().equals("TED")){
						parYbDefVOs[2].setPar_value(pvvos[i].getPar_value());
					}
					if(pvvos[i].getPar_code().equals("TET")){
						parYbDefVOs[3].setPar_value(pvvos[i].getPar_value());
					}
				}
				if(parYbDefVOs[0].getPar_value() == null ){
					showErrorMessage("请设置夜班开始日期！");
				}
				if(parYbDefVOs[1].getPar_value() == null ){
					showErrorMessage("请设置夜班开始时间！");
				}
				if(parYbDefVOs[2].getPar_value() == null ){
					showErrorMessage("请设置夜班结束日期！");
				}
				if(parYbDefVOs[3].getPar_value() == null ){
					showErrorMessage("请设置夜班结束时间！");
				}			
			} catch (Exception e) {
				showErrorMessage("夜班参数有问题，请修改！");
				e.printStackTrace();
			}
		}
		return parYbDefVOs;
	}
	
	/**
	 * 检查班次类别设置。 创建日期：(2001-11-22 16:31:13)
	 *刘鹏飞新增，导入时的验证
	 * @return int
	 * @throws BusinessException 
	 */
	private int import_checkHeader(BclbHeaderVO bclbvo) throws BusinessException {

		String lbbm = bclbvo.getLbbm();//getTfLbbm().getText();//班次类别编码
		String lbmc = bclbvo.getLbmc();//getTfLbmc().getText();//班次类别名称

		if (lbbm == null || lbbm.trim().length() == 0) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000066")/* @res "班次类别编码不能为空！" */);
			return -1;
		}
		if (lbmc == null || lbmc.trim().length() == 0) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000067")/* @res "班次类别名称不能为空！" */);
			return -1;
		}
		if (isNull(bclbvo.getTimebegintime()) || isNull(bclbvo.getTimeendtime())/*3.4开始结束时间*/
				|| bclbvo.getTimebegintime().trim().equals("")
				|| bclbvo.getTimeendtime().trim().equals("")) {
			//showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000070")/* @res "考勤开始、结束时间均不可为空,请检查！"*/);
			showErrorMessage("班别开始、结束时间均不可为空,请检查！");
			return -1;
		}

		//modify5.5
		int timebegindaytype = bclbvo.getTimebeginday();//(Integer)(getChbKqkssj().getSelectdItemValue());
		UFTime kqks = new UFTime(bclbvo.getTimebegintime());//new UFTime(getTfKqkssj().getText());
		int timeenddaytype = bclbvo.getTimeendday();//(Integer)getChbKqjssj().getSelectdItemValue();
		UFTime kqjs = new UFTime(bclbvo.getTimeendtime());//new UFTime(getTfKqjssj().getText());

		int kqcheck = checkTime(timebegindaytype,kqks, timeenddaytype,kqjs);
		if (kqcheck == 1) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000071")/* @res "考勤开始时间不能小于结束时间" */);
			return -1;
		}

		//刘鹏飞 2011-4-1根据需求原因，注掉一下代码，不进行相关判读 
		
		//if (getCkbSfyb().isSelected()) {
		if(bclbvo.getIncludenightshift().booleanValue())	{
			/*夜班时，是将夜班参数时间赋给夜班还是考勤时间*/
		
		
			bclbvo.setNightbeginday(bclbvo.getTimebeginday());
			bclbvo.setNightbegintime(bclbvo.getTimebegintime());
			bclbvo.setNightendday(bclbvo.getTimeendday());
			bclbvo.setNightendtime(bclbvo.getTimeendtime());
			
			if (isNull(bclbvo.getNightbegintime())//isNull(getTfYbkssj().getText())
					|| isNull(bclbvo.getNightendtime()/*getTfYbjssj().getText()*/)) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"6017010113", "UPP6017010113-000073")/* @res "夜班开始结束时间不可为空" */);
				return -1;
			}
			
			int ybbegindaytype = bclbvo.getNightbeginday();//(Integer)getChbYbkssj().getSelectdItemValue();
			//int ybbegindaytype = Integer.parseInt(this.getParaYbParDefVOs()[0].getPar_value());
			UFTime ybks = new UFTime(bclbvo.getNightbegintime()/*getTfYbkssj().getText()*/);
			int ybenddaytype = bclbvo.getNightendday();//(Integer)getChbYbjssj().getSelectdItemValue();
			//int ybenddaytype = Integer.parseInt(this.getParaYbParDefVOs()[2].getPar_value());
			UFTime ybjs = new UFTime(bclbvo.getNightendtime()/*getTfYbjssj().getText()*/);

			int ybcheck = checkTime(ybbegindaytype,ybks, ybenddaytype,ybjs);
			if (ybcheck == 1) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"6017010113", "UPP6017010113-000074")/* @res "夜班开始时间不能小于结束时间"*/);
				return -1;
			}

		} else {
			
			bclbvo.setNightbegintime("");//getTfYbkssj().setText("");
			bclbvo.setNightbegintime("");//getTfYbjssj().setText("");
		}
		bclbvo.setAllowlate(new UFDouble(0));
		//getTfallowlate().setText( "0");//"允许迟到时限不允许为空！"
//		if (getTfallowlate().getText() == null //0
//				|| getTfallowlate().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000076")/* @res "允许迟到时限不允许为空！" */);
//			return -1;
//		}
		bclbvo.setAllowearly(new UFDouble(0));
		//getTfallowearly().setText("0");//允许早退时限不允许为空！
//		if (getTfallowearly().getText() == null
//				|| getTfallowearly().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000077")/* @res "允许早退时限不允许为空！" */);
//			return -1;
//		}
		bclbvo.setLargelate(new UFDouble(0));
		bclbvo.setLargeearly(new UFDouble(0));
		//getTflargelate().setText("0");//最长迟到时限不允许为空！
//		if (getTflargelate().getText() == null
//				|| getTflargelate().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000078")/* @res "最长迟到时限不允许为空！" */);
//			return -1;
//		}
		//getTflargeearly().setText("0");//最长早退时限不允许为空
//		if (getTflargeearly().getText() == null
//				|| getTflargeearly().getText().trim().length() == 0) {
//			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//					"6017010113", "UPP6017010113-000079")/* @res "最长早退时限不允许为空！" */);
//			return -1;
//		}
//
//		if(Integer.parseInt(getTfallowlate().getText()) > Integer.parseInt(getTflargelate().getText())){//去掉
//		    showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000028")/* @res "允许迟到时限不能大于最长迟到时限!" */);
//		    return -1;
//		}
//		if(Integer.parseInt(getTfallowearly().getText()) > Integer.parseInt(getTflargeearly().getText())){
//		    showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113", "UPP6017010113-000036")/* @res "允许早退时限不能大于最长早退时限!" */);
//		    return -1;
//		}
		
		if (getrbButton_clz().isSelected())//0
			if (getTfkgsc().getText() == null
					|| getTfkgsc().getText().trim().length() == 0) {
				showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"6017010113", "UPP6017010113-000080")/* @res "迟到或早退时长超过其最长时限时计为旷工的时长不允许为空，请输入！"*/);
				return -1;
			}

		if(getUIChkbovertmrule().isSelected())
		{
			if (getUITFovertmrule().getText().trim().equals("")){//给最大值
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001299")//@res "下班刷卡超过下班时间多少分钟计为加班的值不能为空"
);
			    return -1;
			}
			if (getUITFovertmrule2().getText().trim().equals("")){//不加班
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001300")//@res "加班从下班时间后多少分钟开始计算的值不能为空"
);
			    return -1;
			}

			int overtmeffectbeyond = Integer.valueOf(getUITFovertmrule().getText().trim());
			int overtmbeyond = Integer.valueOf(getUITFovertmrule2().getText().trim());

			if (overtmbeyond>overtmeffectbeyond){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001301")//@res "加班从下班时间后多少分钟开始计算的值不能大于下班刷卡超过下班时间多少分钟计为加班的值"
);
			    return -1;
			}
		}
		if(getUIChkbontmrule().isSelected())
		{
			if (getUITFontmrule().getText().trim().equals("")){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001302")//@res "上班刷卡早于上班时间多少分钟计为加班的值不能为空"
);
			    return -1;
			}
			if (getUITFontmrule2().getText().trim().equals("")){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001303")//@res "加班计算到上班时间前多少分钟截止的值不能为空"
);
			    return -1;
			}

			int ontmeffectbeyond = Integer.valueOf(getUITFontmrule().getText().trim());
			int ontmbeyond = Integer.valueOf(getUITFontmrule2().getText().trim());

			if (ontmbeyond>ontmeffectbeyond){
			    showErrorMessage(ResHelper.getString("6017","UPP6017-001304")//@res "加班计算到上班时间前多少分钟截止的值不能大于上班刷卡早于上班时间多少分钟计为加班的值"
);
			    return -1;
			}
		}

		//如果是在增加或修改记录，判断新增纪录中的类别编码、类别名称是否合法
		BclbHeaderVO[] bclbHeaderVOs = null;
		bclbHeaderVOs = TADelegator.getBclb029().queryBclb029AllBclbHeader(Global.getCorpPK(), null);
		
			for (BclbHeaderVO bclbHeaderVO : bclbHeaderVOs) {
				if (bclbHeaderVO.getLbbm().equals(lbbm/*getTfLbbm().getText()*/)) {
					showErrorMessage(ResHelper.getString("6017","UPP6017-001429")/*"班次类别编码已存在，请修改！"*/);
					return -1;
				}
				if (bclbHeaderVO.getLbmc().equals(/*getTfLbmc().getText()*/lbmc)) {
					showErrorMessage(ResHelper.getString("6017","UPP6017-001430")/*"班次类别名称已存在，请修改！"*/);
					return -1;
				}
			}
		
		//校验如果设置成了默认班别，是否自身互斥
			/*
		BclbHeaderVO newvo = new BclbHeaderVO();
		newvo.setTimebeginday((Integer)getChbKqkssj().getSelectdItemValue());
		newvo.setTimeendday((Integer)getChbKqjssj().getSelectdItemValue());
		newvo.setTimebegintime(getTfKqkssj().getText());
		newvo.setTimeendtime(getTfKqjssj().getText());
		if (getCkbSfmrbb().isSelected() && !checkDefaultClassSelfMutex(bclbvo)){
			showErrorMessage(ResHelper.getString("6017","UPP6017-000581")//@res "默认班别不能够被设置为邻日互斥状态！"
);
			return -1;
		}*/
		return 0;
	}

	/**
	 * 检查作息时间列表数据设置。 创建日期：(2001-11-22 16:31:32)
	 *
	 * @return int
	 */
	private int import_checkItems(BclbHeaderVO bclbvo,BclbItemVO[] btvos) {

		BclbItemVO[] bivos = btvos;//getWtVos();

		Vector<BclbItemVO> dataVOs = new Vector<BclbItemVO>();
		for (int i=0; i<bivos.length; i++){
			
			if ((bivos[i].getKssj()!=null) && (bivos[i].getKssj().trim().length()>0)){
				dataVOs.add(bivos[i]);
			}
		}

		boolean sfYb = bclbvo.getIncludenightshift().booleanValue();//getCkbSfyb().isSelected();

		//modify5.5 增加夜班设置范围应在考勤范围内的校验
		int kqbegindaytype = bclbvo.getTimebeginday();//(Integer)getChbKqkssj().getSelectdItemValue();
		UFTime kqks = new UFTime(/*getTfKqkssj().getText()*/bclbvo.getTimebegintime());
		int kqenddaytype = bclbvo.getTimeendday();//(Integer)getChbKqjssj().getSelectdItemValue();
		UFTime kqjs = new UFTime(/*getTfKqjssj().getText()*/bclbvo.getTimeendtime());
//		if (sfYb){
//			int ybbegindaytype = (Integer)getChbYbkssj().getSelectdItemValue();
//			UFTime ybks = new UFTime(getTfYbkssj().getText());
//			int ybenddaytype = (Integer)getChbYbjssj().getSelectdItemValue();
//			UFTime ybjs = new UFTime(getTfYbjssj().getText());
//
//			if (checkTime(kqbegindaytype,kqks,ybbegindaytype,ybks)==1 || checkTime(ybenddaytype,ybjs,kqenddaytype,kqjs)==1){
//				showErrorMessage(ResHelper.getString("6017","UPP6017-000586")//@res "夜班时间范围应在考勤时间范围内!"
//				);
//				return -1;
//			}
//			
//		}

		java.util.Vector<UFDateTime[]> dres = new java.util.Vector<UFDateTime[]>();

		boolean isKsNull = false;
		boolean isJsNull = false;
		//boolean isKt = false;



		for (int i = 0; i < bivos.length; i++) {
			isKsNull = false;
			isJsNull = false;
			//isKt = false;

			String row = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000088"/* @res "{0}行" */, null,
					new String[] { nc.vo.format.Format.indexFormat(i + 1) });
			String ks = bivos[i].getKssj();
			String js = bivos[i].getJssj();

			//modify5.5 增加作息时段设置范围应在考勤范围内的校验
			int wtbegindaytype = bivos[i].getWtbeginday();
			UFTime wtks = new UFTime(ks);
			int wtenddaytype = bivos[i].getWtendday();
			UFTime wtjs = new UFTime(js);


			if (ks == null || ks.trim().equals(""))
				isKsNull = true;
			if (js == null || js.trim().equals(""))
				isJsNull = true;
			if (isKsNull && isJsNull)
				continue;
			if ( (isKsNull && (!isJsNull)) || (!isKsNull && isJsNull)) {
				showErrorMessage(row
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000091")/*
																  * @res
																  * "信息输入不完整！"
																  */);
				return -1;
			}
			if (ks.trim().length() != 8) {
				showErrorMessage(row
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000092")/*
																  * @res
																  * "开始时间格式不正确！"
																  */);
				return -1;
			}
			if (js.trim().length() != 8) {
				showErrorMessage(row
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"6017010113", "UPP6017010113-000093")/*
																  * @res
																  * "结束时间格式不正确！"
																  */);
				return -1;
			}

			if (checkTime(wtbegindaytype,wtks,wtenddaytype,wtjs)==1){
				showErrorMessage(ResHelper.getString("6017","UPP6017-000587")//@res "作息时间段的开始时间应在结束时间之前!"
);
				return -1;
			}

			/*
			if (checkTime(kqbegindaytype,kqks,wtbegindaytype,wtks)==1 || checkTime(wtenddaytype,wtjs,kqenddaytype,kqjs)==1){
				showErrorMessage(ResHelper.getString("6017","UPP6017-000588")//@res "作息时间范围应在考勤时间范围内!"
);
				return -1;
			}
*/
			if (checkTime(wtbegindaytype,wtks,kqbegindaytype,kqks)==0 || checkTime(kqenddaytype,kqjs,wtenddaytype,wtjs)==0){
				showErrorMessage(ResHelper.getString("6017","UPP6017-000588")//@res "作息时间范围应在考勤时间范围内!"
);
				return -1;
			}


			//int[] se = { 0, 0 };
			//se[0] = nKsTime;
			//se[1] = nJsTime;

			UFDateTime[] se = new UFDateTime[2];
			se[0] = new UFDateTime(new UFDate("2000-01-02").getDateAfter(wtbegindaytype),wtks);
			se[1] = new UFDateTime(new UFDate("2000-01-02").getDateAfter(wtenddaytype),wtjs);
			dres.addElement(se);
		}

		//检查时间段的顺序start
		for (int i=0; i<dataVOs.size(); i++){
			for (int m = 0 ;m < dataVOs.size() - i - 1 ;m++ ){

				int wtbegindaytype = dataVOs.get(m).getWtbeginday();
				UFTime wtks = new UFTime(dataVOs.get(m).getKssj());
				UFDateTime wtksDateTime = new UFDateTime((wtbegindaytype==1?new UFDate("2000-01-03"):(wtbegindaytype==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks);

				int wtbegindaytype2 = dataVOs.get(m+1).getWtbeginday();
				UFTime wtks2 = new UFTime(dataVOs.get(m+1).getKssj());
				UFDateTime wtksDateTime2 = new UFDateTime((wtbegindaytype2==1?new UFDate("2000-01-03"):(wtbegindaytype2==-1?new UFDate("2000-01-01"):new UFDate("2000-01-02"))),wtks2);

				if (wtksDateTime.compareTo(wtksDateTime2)>0){
					//showErrorMessage("第" + (m+1) + "时间段与" + (m+2) + "时间段顺序不正确!");
					showErrorMessage(nc.ui.ml.NCLangRes.getInstance()
							.getStrByID("6017010113","UPP6017010113-000138",null,new String[] {nc.vo.format.Format.indexFormat(m + 1),nc.vo.format.Format.indexFormat(m + 2) }));
					return -1;
				}
			}
		}
		//检查时间段的顺序end

//		if (sfYb &&dataVOs!=null && dataVOs.size()>0){
//			//是夜班
//			int ybbegindaytype = (Integer)getChbYbkssj().getSelectdItemValue();
//			UFTime ybks = new UFTime(getTfYbkssj().getText());
//			int ybenddaytype = (Integer)getChbYbjssj().getSelectdItemValue();
//			UFTime ybjs = new UFTime(getTfYbjssj().getText());
//
//			if (checkTime(ybbegindaytype,ybks,ybenddaytype,ybjs)==1){
//				showErrorMessage(ResHelper.getString("6017","UPP6017-000589")//@res "夜班开始时间应在夜班结束时间之前!"
//);
//				return -1;
//			}
//
//			//modify5.5 夜班时段应处于第一作息时段的开始与最后作息时段的结束时间范围内！
//
//
//			int wtbegindaytype = dataVOs.get(0).getWtbeginday();
//			UFTime wtks = new UFTime(dataVOs.get(0).getKssj());
//
//			int wtenddaytype2 = dataVOs.get(dataVOs.size()-1).getWtendday();
//			UFTime wtjs2 = new UFTime(dataVOs.get(dataVOs.size()-1).getJssj());
//
//			if (checkTime2(wtbegindaytype,wtks,ybbegindaytype,ybks)==1 || checkTime2(ybenddaytype,ybjs,wtenddaytype2,wtjs2)==1){
//				showErrorMessage(ResHelper.getString("6017","UPP6017-000923")/*"夜班时段应处于第一作息时段的开始与最后作息时段的结束时间范围内！"*/);
//				return -1;
//			}
//		}
//		新增 设置上班刷卡，下班不刷卡，最后一班上下班都要刷卡
		for(int q = 0; q < dataVOs.size(); q++) {
			dataVOs.get(q).setCheckInFlag(new UFBoolean("Y"));
			dataVOs.get(q).setCheckoutFlag(new UFBoolean("N"));
			if(q == dataVOs.size() -1)
				dataVOs.get(q).setCheckoutFlag(new UFBoolean("Y"));
		}
	
		
//刘鹏飞 2011-4-1 注掉 关于上下班刷卡的处理
//		//add start
//		for (int q = 0; q < dataVOs.size(); q++) {
//			if (q==0){
//				//如果是有效数据的第一行,上班刷卡没有选
//				if (!((BclbItemVO)dataVOs.get(q)).getCheckInFlag().booleanValue()){//false
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//									"6017010113", "UPP6017010113-000139")
//																	  /* @res
//																	  * "第一时间段上班刷卡必须设置"
//																	  */);
//					return -1;
//				}
//				if (dataVOs.size()==1 && !((BclbItemVO)dataVOs.get(q)).getCheckoutFlag().booleanValue()){
//					//如果是有效数据的第一行,且只有一行有效数据,且下班刷卡没有选
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//									"6017010113", "UPP6017010113-000140")
//																	  /* @res
//																	  * "只有一个时间段上、下班刷卡都必须设置"
//																	  */);
//					return -1;
//				}
//			}else{
//				if (!((BclbItemVO)dataVOs.get(q)).getCheckInFlag().booleanValue()&&((BclbItemVO)dataVOs.get(q-1)).getCheckoutFlag().booleanValue()){
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113",	"UPP6017010113-000141", null, new String[]{Integer.toString(q+1)})
//																	  /* @res
//																	  * "第"+(q+1)+"时间段上班没有刷卡，则上一时间段下班也不应刷卡"
//																	  */);
//					return -1;
//				}
//				if (((BclbItemVO)dataVOs.get(q)).getCheckInFlag().booleanValue()&&!((BclbItemVO)dataVOs.get(q-1)).getCheckoutFlag().booleanValue()){
//					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113",	"UPP6017010113-000142", null, new String[]{Integer.toString(q+1)})
//																	  /* @res
//																	  * "第"+(q+1)+"时间段上班刷卡，则上一时间段下班也应刷卡"
//																	  */);
//					return -1;
//				}
//				if (q==dataVOs.size()-1){
//					if (!((BclbItemVO)dataVOs.get(q)).getCheckoutFlag().booleanValue()){
//						showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//										"6017010113", "UPP6017010113-000143")
//																		  /* @res
//																		  * "最后时间段下班刷卡必须设置"
//																		  */);
//						return -1;
//					}
//
//				}
//			}
//		}

		//add end

		//int nTotalTime = 0;
		for (int i = 0; i < dres.size(); i++) {
			UFDateTime iks = ((UFDateTime[]) (dres.elementAt(i)))[0];
			UFDateTime ijs = ((UFDateTime[]) (dres.elementAt(i)))[1];
			for (int k = 0; k < i; k++) {
				UFDateTime jks = ((UFDateTime[]) (dres.elementAt(k)))[0];
				UFDateTime jjs = ((UFDateTime[]) (dres.elementAt(k)))[1];
				//if ((jks >= iks && jks < ijs) || (iks >= jks && iks < jjs)) {
				if (iks.compareTo(jjs) < 0 && ijs.compareTo(jks) > 0) {
					showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("6017010113","UPP6017010113-000105",
									null,new String[] {nc.vo.format.Format.indexFormat(k + 1),nc.vo.format.Format.indexFormat(i + 1) })/*
																		   * @res
																		   * "{0}行与{1}行的作息时间段出现了交叉！"
																		   */);
					return -1;
				}
			}
			//nTotalTime += (ijs - iks);
		}

		//Add tzj
		//如果考勤，作息时间不能超过4个
		if (dres.size() > 4) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"6017010113", "UPP6017010113-000107")/* @res "考勤班次作息时段数不能大于4" */);
			return -1;
		}
		return 0;
	}
}