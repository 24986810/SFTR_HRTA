package nc.ui.wa.wa_020;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.ui.hr.frame.util.IconUtils;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.vo.pub.BusinessException;
import nc.vo.wa.wa_001.WaGlobalVO;
import nc.vo.wa.wa_024.ItemVO;

public abstract class ItemInfoTRVOSelAbstractDlg extends nc.ui.pub.beans.UIDialog implements java.awt.event.ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 3623741235217336425L;
	private nc.ui.pub.beans.UIButton ivjbnCancel = null;
	private nc.ui.pub.beans.UIButton ivjbnOK = null;
	private nc.ui.pub.beans.UIListToList ivjlstTolst = null;
	private nc.ui.pub.beans.UIListToList ivjexplstTolst = null;
	private nc.ui.pub.beans.UILabel ivjUILabelSelect = null;
	private nc.ui.pub.beans.UILabel ivjUILabelSelected = null;
	private nc.ui.pub.beans.UILabel ivjexpUILabelSelect = null;
	private nc.ui.pub.beans.UILabel ivjexpUILabelSelected = null;
	private javax.swing.JPanel ivjUIDialogContentPane = null;

	private int type = 0;

	private nc.ui.pub.beans.UIButton ivjUIBoSave = null;

	private UIButton ivjbnTop = null;
	private UIButton ivjbnUp = null;
	private UIButton ivjbnDown = null;
	private UIButton ivjbnBottom = null;
	
	private UIButton ivjexpbnTop = null;
	private UIButton ivjexpbnUp = null;
	private UIButton ivjexpbnDown = null;
	private UIButton ivjexpbnBottom = null;
	
	private WaGlobalVO[] list = null;
	

	/**
	 * DeptRefModel 构造子注解。
	 *
	 * @param parent
	 *            java.awt.Container
	 */
	public ItemInfoTRVOSelAbstractDlg(java.awt.Container parent, int type, WaGlobalVO[] list) {
		super(parent);
		this.type = type;
		this.list = list;
		
		initialize();
	}
	
	public ItemVO[] getExpItemVOS(){
		String[] expItemNames = new String[]{"职称",
				"岗位工资","岗位工资薪资标准","岗位工资薪资级别","岗位工资档别","岗位工资金额",
				"岗位级别","岗位级别薪资标准","岗位级别薪资级别","岗位级别档别","岗位级别金额",
				"薪级工资","薪级工资薪资标准","薪级工资薪资级别","薪级工资档别","薪级工资金额",
				//"薪级级别","薪级级别薪资标准","薪级级别薪资级别","薪级级别档别","薪级级别金额",
				"岗位津贴","岗位津贴薪资标准","岗位津贴薪资级别","岗位津贴档别","岗位津贴金额",
				"工作年限","工作年限薪资标准","工作年限薪资级别","工作年限档别","工作年限金额",
				"聘入时间","户籍","派遣类型"
				,"社保号","身份证","职务津贴"
				};
		String[] expItemKeys = new String[]{"dutyname",
				"gwgz","gwgzbz","gwgzjb","gwgzdb","gwgzje",
				"gwjb","gwjbbz","gwjbjb","gwjbdb","gwjbje",
				"xjgz","xjgzbz","xjgzjb","xjgzdb","xjgzje",
				//"xjjb","xjjbbz","xjjbjb","xjjbdb","xjjbje",
				"gwjt","gwjtbz","gwjtjb","gwjtdb","gwjtje",
				"gznx","gznxbz","gznxjb","gznxdb","gznxje",
				"indutydate","docname","psnclassname"
				,"sbh","id","zwjt"
				};
		String[] expItemCodes = new String[]{"dutyname",
				"v_hi_gwgz.gwgz","v_hi_gwgz.gwgzbz","v_hi_gwgz.gwgzjb","v_hi_gwgz.gwgzdb","v_hi_gwgz.gwgzje",
				"v_hi_gwjb.gwjb","v_hi_gwjb.gwjbbz","v_hi_gwjb.gwjbjb","v_hi_gwjb.gwjbdb","v_hi_gwjb.gwjbje",
				"v_hi_xjgz.xjgz","v_hi_xjgz.xjgzbz","v_hi_xjgz.xjgzjb","v_hi_xjgz.xjgzdb","v_hi_xjgz.xjgzje",
				//"xjjb","xjjbbz","xjjbjb","xjjbdb","xjjbje",
				"v_hi_gwjt.gwjt","v_hi_gwjt.gwjtbz","v_hi_gwjt.gwjtjb","v_hi_gwjt.gwjtdb","v_hi_gwjt.gwjtje",
				"v_hi_gznx.gznx","v_hi_gznx.gznxbz","v_hi_gznx.gznxjb","v_hi_gznx.gznxdb","v_hi_gznx.gznxje",
				"indutydate","docname","psnclassname"
				,"v_hi_sbh.sbh","id","v_hi_zwjt.zwjt"
				};
		
		ItemVO[] expitemVOs = new ItemVO[expItemNames.length];
		
		for(int i=0;i<expItemNames.length;i++){
			ItemVO itemvo1 = new ItemVO();
			itemvo1.setVname(expItemNames[i]);
			itemvo1.setPk_wa_item(expItemKeys[i]);
			itemvo1.setDocname(expItemCodes[i]);
			
			expitemVOs[i]=itemvo1;
		}
		
		return expitemVOs;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (e.getSource().equals(getbnOK())) {
			if (getSelectedVo() == null || getSelectedVo().length == 0) {
				MessageDialog.showErrorDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000173")/*@res "错误"*/, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000174")/*@res "请选择项目！"*/);
				return;
			}
			onOk();
		}
		if (e.getSource().equals(getbnCancel())) {
			onCancel();
		}
		if (e.getSource().equals(getUIBoSave())) {
			try {
				onSave();
			} catch (BusinessException ex) {
				MessageDialog.showErrorDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000173")/*@res "错误"*/, ex.getMessage());
				ex.printStackTrace();
			}
		}
		if (e.getSource().equals(getIvjbnTop())) {
			onTop();
		}
		if (e.getSource().equals(getIvjbnBottom())) {
			onButtom();
		}
		if (e.getSource().equals(getIvjbnUp())) {
			onUp();
		}
		if (e.getSource().equals(getIvjbnDown())) {
			onDown();
		}
		
		if (e.getSource().equals(getIvjExpbnTop())) {
			onExpTop();
		}
		if (e.getSource().equals(getIvjExpbnBottom())) {
			onExpButtom();
		}
		if (e.getSource().equals(getIvjExpbnUp())) {
			onExpUp();
		}
		if (e.getSource().equals(getIvjExpbnDown())) {
			onExpDown();
		}
	}

	private void setSuqenBtnEnabled() {
		int index = getlstTolst().getLstRight().getSelectedIndex();
		int count = getlstTolst().getRightData() != null ? getlstTolst().getRightData().length : 0;

		if (index == -1) {
			getIvjbnTop().setEnabled(false);
			getIvjbnUp().setEnabled(false);
			getIvjbnDown().setEnabled(false);
			getIvjbnBottom().setEnabled(false);
		} else if (index == 0) {
			getIvjbnTop().setEnabled(false);
			getIvjbnUp().setEnabled(false);
			getIvjbnDown().setEnabled(true);
			getIvjbnBottom().setEnabled(true);
		} else if (index == count - 1 && index > 1) {
			getIvjbnTop().setEnabled(true);
			getIvjbnUp().setEnabled(true);
			getIvjbnDown().setEnabled(false);
			getIvjbnBottom().setEnabled(false);
		} else {
			getIvjbnTop().setEnabled(true);
			getIvjbnUp().setEnabled(true);
			getIvjbnDown().setEnabled(true);
			getIvjbnBottom().setEnabled(true);
		}
		repaint();
	}
	
	private void setExpSuqenBtnEnabled() {
		int index = getexplstTolst().getLstRight().getSelectedIndex();
		int count = getexplstTolst().getRightData() != null ? getexplstTolst().getRightData().length : 0;

		if (index == -1) {
			getIvjExpbnTop().setEnabled(false);
			getIvjExpbnUp().setEnabled(false);
			getIvjExpbnDown().setEnabled(false);
			getIvjExpbnBottom().setEnabled(false);
		} else if (index == 0) {
			getIvjExpbnTop().setEnabled(false);
			getIvjExpbnUp().setEnabled(false);
			getIvjExpbnDown().setEnabled(true);
			getIvjExpbnBottom().setEnabled(true);
		} else if (index == count - 1 && index > 1) {
			getIvjExpbnTop().setEnabled(true);
			getIvjExpbnUp().setEnabled(true);
			getIvjExpbnDown().setEnabled(false);
			getIvjExpbnBottom().setEnabled(false);
		} else {
			getIvjExpbnTop().setEnabled(true);
			getIvjExpbnUp().setEnabled(true);
			getIvjExpbnDown().setEnabled(true);
			getIvjExpbnBottom().setEnabled(true);
		}
		repaint();
	}

	private void onTop() {
		Object obj = getlstTolst().getLstRight().getSelectedValue();
		if (obj == null) {
			return;
		}

		Object[] objs = getlstTolst().getRightData();

		Vector<Object> listData = new Vector<Object>();
		listData.addElement(obj);

		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (!temp.equals(obj)) {
				listData.addElement(temp);
			}
		}

		objs = listData.toArray(objs);
		DefaultListModel defaultListModel = (DefaultListModel) getlstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getlstTolst().getLstRight().setSelectedIndex(0);
	}
	
	private void onExpTop() {
		Object obj = getexplstTolst().getLstRight().getSelectedValue();
		if (obj == null) {
			return;
		}

		Object[] objs = getexplstTolst().getRightData();

		Vector<Object> listData = new Vector<Object>();
		listData.addElement(obj);

		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (!temp.equals(obj)) {
				listData.addElement(temp);
			}
		}

		objs = listData.toArray(objs);
		DefaultListModel defaultListModel = (DefaultListModel) getexplstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getexplstTolst().getLstRight().setSelectedIndex(0);
	}


	private void onButtom() {
		Object obj = getlstTolst().getLstRight().getSelectedValue();
		if (obj == null) {
			return;
		}

		Object[] objs = getlstTolst().getRightData();

		Vector<Object> listData = new Vector<Object>();
		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (!temp.equals(obj)) {
				listData.addElement(temp);
			}
		}

		listData.addElement(obj);

		objs = listData.toArray(objs);

		DefaultListModel defaultListModel = (DefaultListModel) getlstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getlstTolst().getLstRight().setSelectedIndex(objs.length - 1);

	}
	
	private void onExpButtom() {
		Object obj = getexplstTolst().getLstRight().getSelectedValue();
		if (obj == null) {
			return;
		}

		Object[] objs = getexplstTolst().getRightData();

		Vector<Object> listData = new Vector<Object>();
		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (!temp.equals(obj)) {
				listData.addElement(temp);
			}
		}

		listData.addElement(obj);

		objs = listData.toArray(objs);

		DefaultListModel defaultListModel = (DefaultListModel) getexplstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getexplstTolst().getLstRight().setSelectedIndex(objs.length - 1);

	}

	private void onUp() {
		Object[] objs = getlstTolst().getRightData();
		if (objs == null || objs.length <= 1) {
			return;
		}

		Object obj = getlstTolst().getLstRight().getSelectedValue();
		if (obj == null || getlstTolst().getLstRight().getSelectedIndex() == 0) {
			return;
		}

		int index = -1;
		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (temp.equals(obj)) {
				index = i - 1;
				Object temp2 = objs[i - 1];
				objs[i - 1] = obj;
				objs[i] = temp2;
				break;
			}
		}

		DefaultListModel defaultListModel = (DefaultListModel) getlstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getlstTolst().getLstRight().setSelectedIndex(index);
	}
	
	private void onExpUp() {
		Object[] objs = getexplstTolst().getRightData();
		if (objs == null || objs.length <= 1) {
			return;
		}

		Object obj = getexplstTolst().getLstRight().getSelectedValue();
		if (obj == null || getexplstTolst().getLstRight().getSelectedIndex() == 0) {
			return;
		}

		int index = -1;
		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (temp.equals(obj)) {
				index = i - 1;
				Object temp2 = objs[i - 1];
				objs[i - 1] = obj;
				objs[i] = temp2;
				break;
			}
		}

		DefaultListModel defaultListModel = (DefaultListModel) getexplstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getexplstTolst().getLstRight().setSelectedIndex(index);
	}

	private void onDown() {
		Object[] objs = getlstTolst().getRightData();
		if (objs == null || objs.length <= 1) {
			return;
		}

		Object obj = getlstTolst().getLstRight().getSelectedValue();
		if (obj == null || getlstTolst().getLstRight().getSelectedIndex() == objs.length) {
			return;
		}

		int index = -1;
		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (temp.equals(obj)) {
				index = i + 1;
				Object temp2 = objs[i + 1];
				objs[i + 1] = obj;
				objs[i] = temp2;
				break;
			}
		}

		DefaultListModel defaultListModel = (DefaultListModel) getlstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getlstTolst().getLstRight().setSelectedIndex(index);

	}
	
	
	private void onExpDown() {
		Object[] objs = getexplstTolst().getRightData();
		if (objs == null || objs.length <= 1) {
			return;
		}

		Object obj = getexplstTolst().getLstRight().getSelectedValue();
		if (obj == null || getexplstTolst().getLstRight().getSelectedIndex() == objs.length) {
			return;
		}

		int index = -1;
		for (int i = 0; i < objs.length; i++) {
			Object temp = objs[i];
			if (temp.equals(obj)) {
				index = i + 1;
				Object temp2 = objs[i + 1];
				objs[i + 1] = obj;
				objs[i] = temp2;
				break;
			}
		}

		DefaultListModel defaultListModel = (DefaultListModel) getexplstTolst().getLstRight().getModel();
		defaultListModel.removeAllElements();
		for (Object object : objs) {
			defaultListModel.addElement(object);
		}
		getexplstTolst().getLstRight().setSelectedIndex(index);

	}

	/**
	 * 返回 bnCancel 特性值。
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIButton getbnCancel() {
		if (ivjbnCancel == null) {
			try {
				ivjbnCancel = new nc.ui.pub.beans.UIButton();
				ivjbnCancel.setName("bnCancel");
				ivjbnCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000008")/* "取消" */);
				ivjbnCancel.setBounds(230, 546, 78, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjbnCancel;
	}

	/**
	 * 返回 bnOK 特性值。
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIButton getbnOK() {
		if (ivjbnOK == null) {
			try {
				ivjbnOK = new nc.ui.pub.beans.UIButton();
				ivjbnOK.setName("bnOK");
				ivjbnOK.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000044")/* "确定" */);
				ivjbnOK.setBounds(140, 546, 78, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjbnOK;
	}

	/**
	 * 返回 lstTolst 特性值。
	 *
	 * @return nc.ui.pub.beans.UIListToList
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIListToList getlstTolst() {
		if (ivjlstTolst == null) {
			try {
				ivjlstTolst = new nc.ui.pub.beans.UIListToList();
				ivjlstTolst.setName("lstTolst");
				ivjlstTolst.getbnL().setText("");
				ivjlstTolst.getbnLL().setText("");
				ivjlstTolst.getbnR().setText("");
				ivjlstTolst.getbnRR().setText("");
				ivjlstTolst.getbnL().setSize(44, 22);
				ivjlstTolst.getbnLL().setSize(44, 22);
				ivjlstTolst.getbnR().setSize(44, 22);
				ivjlstTolst.getbnRR().setSize(44, 22);
				ivjlstTolst.getbnR().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TO_RIGHT));
				ivjlstTolst.getbnRR().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_ALL_TO_RIGHT));
				ivjlstTolst.getbnL().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TO_LEFT));
				ivjlstTolst.getbnLL().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_ALL_TO_LEFT));
				ivjlstTolst.setBounds(49, 49, 390, 185);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjlstTolst;
	}
	
	private nc.ui.pub.beans.UIListToList getexplstTolst() {
		if (ivjexplstTolst == null) {
			try {
				ivjexplstTolst = new nc.ui.pub.beans.UIListToList();
				ivjexplstTolst.setName("explstTolst");
				ivjexplstTolst.getbnL().setText("");
				ivjexplstTolst.getbnLL().setText("");
				ivjexplstTolst.getbnR().setText("");
				ivjexplstTolst.getbnRR().setText("");
				ivjexplstTolst.getbnL().setSize(44, 22);
				ivjexplstTolst.getbnLL().setSize(44, 22);
				ivjexplstTolst.getbnR().setSize(44, 22);
				ivjexplstTolst.getbnRR().setSize(44, 22);
				ivjexplstTolst.getbnR().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TO_RIGHT));
				ivjexplstTolst.getbnRR().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_ALL_TO_RIGHT));
				ivjexplstTolst.getbnL().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TO_LEFT));
				ivjexplstTolst.getbnLL().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_ALL_TO_LEFT));
				ivjexplstTolst.setBounds(49, 300, 390, 185);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjexplstTolst;
	}


	public void setRigtData(ItemVO[] itemVOs){
		getlstTolst().setRightData(itemVOs);
	}

	public void setLeftData(ItemVO[] itemVOs){
		getlstTolst().setLeftData(itemVOs);
		getexplstTolst().setLeftData(getExpItemVOS());
	}
	/**
	 * 返回选择的数据
	 *
	 * 创建日期：(2001-7-18 8:36:59)
	 *
	 * @return java.util.Hashtable
	 */
	private Object[] getSelectedData() {
		return getlstTolst().getRightData();
	}
	
	private Object[] getSelectedExpData() {
		return getexplstTolst().getRightData();
	}

	/**
	 * 返回选中的VO
	 *
	 * 创建日期：(2001-8-13 19:54:51)
	 */
	public ItemVO[] getSelectedVo() {
		ItemVO[] itemSelectedVOs = null;
		if (getSelectedData() != null && getSelectedData().length != 0) {
			itemSelectedVOs = new ItemVO[getSelectedData().length];
			for (int i = 0; i < getSelectedData().length; i++) {

				itemSelectedVOs[i] = (ItemVO) getSelectedData()[i];
			}
		}
		return itemSelectedVOs;
	}

	public ItemVO[] getSelectedExpVo() {
		ItemVO[] itemSelectedVOs = null;
		if (getSelectedExpData() != null && getSelectedExpData().length != 0) {
			itemSelectedVOs = new ItemVO[getSelectedExpData().length];
			for (int i = 0; i < getSelectedExpData().length; i++) {

				itemSelectedVOs[i] = (ItemVO) getSelectedExpData()[i];
			}
		}
		return itemSelectedVOs;
	}

	/**
	 * 返回 UIBoSave 特性值。
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIButton getUIBoSave() {
		if (ivjUIBoSave == null) {
			try {
				ivjUIBoSave = new nc.ui.pub.beans.UIButton();
				ivjUIBoSave.setName("UIBoSave");
				ivjUIBoSave.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604", "UPP60131604-000032")/* "保存显示顺序" */);
				ivjUIBoSave.setBounds(320, 246, 118, 22);
				ivjUIBoSave.setVisible(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIBoSave;
	}

	/**
	 * 返回 UIDialogContentPane 特性值。
	 *
	 * @return javax.swing.JPanel
	 */
	/* 警告：此方法将重新生成。 */
	private javax.swing.JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(null);
				getUIDialogContentPane().add(getlstTolst(), getlstTolst().getName());
				getUIDialogContentPane().add(getexplstTolst(), getexplstTolst().getName());
				
				getUIDialogContentPane().add(getbnOK(), getbnOK().getName());
				getUIDialogContentPane().add(getbnCancel(), getbnCancel().getName());
				getUIDialogContentPane().add(getUILabelSelect(), getUILabelSelect().getName());
				getUIDialogContentPane().add(getExpUILabelSelect(), getExpUILabelSelect().getName());
				getUIDialogContentPane().add(getUILabelSelected(), getUILabelSelected().getName());
				getUIDialogContentPane().add(getExpUILabelSelected(), getExpUILabelSelected().getName());
				getUIDialogContentPane().add(getUIBoSave(), getUIBoSave().getName());
				getUIDialogContentPane().add(getIvjbnTop(), getIvjbnTop().getName());
				getUIDialogContentPane().add(getIvjbnUp(), getIvjbnUp().getName());
				getUIDialogContentPane().add(getIvjbnDown(), getIvjbnDown().getName());
				getUIDialogContentPane().add(getIvjbnBottom(), getIvjbnBottom().getName());
				
				getUIDialogContentPane().add(getIvjExpbnTop(), getIvjExpbnTop().getName());
				getUIDialogContentPane().add(getIvjExpbnUp(), getIvjExpbnUp().getName());
				getUIDialogContentPane().add(getIvjExpbnDown(), getIvjExpbnDown().getName());
				getUIDialogContentPane().add(getIvjExpbnBottom(), getIvjExpbnBottom().getName());
				
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	/**
	 * 返回 UILabelSelect 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabelSelect() {
		if (ivjUILabelSelect == null) {
			try {
				ivjUILabelSelect = new nc.ui.pub.beans.UILabel();
				ivjUILabelSelect.setName("UILabelSelect");
				ivjUILabelSelect.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604", "UPP60131604-000033")/* "待选工资项目" */);
				ivjUILabelSelect.setBounds(50, 25, 131, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabelSelect;
	}
	
	private nc.ui.pub.beans.UILabel getExpUILabelSelect() {
		if (ivjexpUILabelSelect == null) {
			try {
				ivjexpUILabelSelect = new nc.ui.pub.beans.UILabel();
				ivjexpUILabelSelect.setName("ExpUILabelSelect");
				ivjexpUILabelSelect.setText("待选导出项目");
				ivjexpUILabelSelect.setBounds(50, 275, 131, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjexpUILabelSelect;
	}

	/**
	 * 返回 UILabelSelected 特性值。
	 *
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getExpUILabelSelected() {
		if (ivjexpUILabelSelected == null) {
			try {
				ivjexpUILabelSelected = new nc.ui.pub.beans.UILabel();
				ivjexpUILabelSelected.setName("UILabelSelected");
				ivjexpUILabelSelected.setText("已选导出项目");
				ivjexpUILabelSelected.setBounds(280, 275, 131, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjexpUILabelSelected;
	}

	private nc.ui.pub.beans.UILabel getUILabelSelected() {
		if (ivjUILabelSelected == null) {
			try {
				ivjUILabelSelected = new nc.ui.pub.beans.UILabel();
				ivjUILabelSelected.setName("UILabelSelected");
				ivjUILabelSelected.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604", "UPP60131604-000034")/* "已选工资项目" */);
				ivjUILabelSelected.setBounds(280, 26, 131, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabelSelected;
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
			setTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604", "UPP60131604-000035")/* "薪资项目多选参照" */);
			// user code end
			setName("DeptRefModel");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(552, 620);
			setContentPane(getUIDialogContentPane());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		getbnCancel().addActionListener(this);
		getbnOK().addActionListener(this);
		getUIBoSave().addActionListener(this);

		getIvjbnBottom().addActionListener(this);
		getIvjbnDown().addActionListener(this);
		getIvjbnTop().addActionListener(this);
		getIvjbnUp().addActionListener(this);
		getIvjbnBottom().setEnabled(false);
		getIvjbnTop().setEnabled(false);
		getIvjbnUp().setEnabled(false);
		getIvjbnDown().setEnabled(false);
		getlstTolst().getLstRight().addListSelectionListener(this);
		
		getIvjExpbnBottom().addActionListener(this);
		getIvjExpbnDown().addActionListener(this);
		getIvjExpbnTop().addActionListener(this);
		getIvjExpbnUp().addActionListener(this);
		getIvjExpbnBottom().setEnabled(false);
		getIvjExpbnTop().setEnabled(false);
		getIvjExpbnUp().setEnabled(false);
		getIvjExpbnDown().setEnabled(false);
		getexplstTolst().getLstRight().addListSelectionListener(this);
		

		try {
			refreshData();
		} catch (BusinessException e) {
			MessageDialog.showErrorDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131604","UPP60131604-000173")/*@res "错误"*/, e.getMessage());
			e.printStackTrace();
		}
		// user code end
	}

	/**
	 * 取消动作
	 */
	public void onCancel() {
		closeCancel();
		dispose();
	}

	/**
	 * 确定动作
	 */
	public void onOk() {
		closeOK();
		dispose();
	}

	public abstract void onSave() throws BusinessException;

	/**
	 * 初始化数据(汇总表只初始化数字型的薪资项目)
	 *
	 * 创建日期：(2001-7-18 8:36:59)
	 * @throws BusinessException
	 */

	public abstract void refreshData() throws BusinessException;

	/**
	 * This method initializes ivjbnTop
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getIvjbnTop() {
		if (ivjbnTop == null) {
			ivjbnTop = new UIButton();
			ivjbnTop.setBounds(new Rectangle(449, 72, 44, 22));
			ivjbnTop.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TOP));
			ivjbnTop.setText("");
			ivjbnTop.setName("bnTop");
			ivjbnTop.setPreferredSize(new Dimension(44, 22));
			ivjbnTop.setSize(44, 22);
		}
		return ivjbnTop;
	}
	
	private UIButton getIvjExpbnTop() {
		if (ivjexpbnTop == null) {
			ivjexpbnTop = new UIButton();
			ivjexpbnTop.setBounds(new Rectangle(449, 320, 44, 22));
			ivjexpbnTop.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TOP));
			ivjexpbnTop.setText("");
			ivjexpbnTop.setName("expbnTop");
			ivjexpbnTop.setPreferredSize(new Dimension(44, 22));
			ivjexpbnTop.setSize(44, 22);
		}
		return ivjexpbnTop;
	}

	/**
	 * This method initializes ivjbnUp
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getIvjbnUp() {
		if (ivjbnUp == null) {
			ivjbnUp = new UIButton();
			ivjbnUp.setBounds(new Rectangle(449, 111, 44, 22));
			ivjbnUp.setName("bnUp");
			ivjbnUp.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_UP));
			ivjbnUp.setMargin(new Insets(2, 0, 2, 0));
			ivjbnUp.setText("");
			ivjbnUp.setFont(new Font("dialog", 0, 12));
			ivjbnUp.setPreferredSize(new Dimension(44, 22));
			ivjbnUp.setSize(44, 22);
		}
		return ivjbnUp;
	}
	
	private UIButton getIvjExpbnUp() {
		if (ivjexpbnUp == null) {
			ivjexpbnUp = new UIButton();
			ivjexpbnUp.setBounds(new Rectangle(449, 355, 44, 22));
			ivjexpbnUp.setName("expbnUp");
			ivjexpbnUp.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_UP));
			ivjexpbnUp.setMargin(new Insets(2, 0, 2, 0));
			ivjexpbnUp.setText("");
			ivjexpbnUp.setFont(new Font("dialog", 0, 12));
			ivjexpbnUp.setPreferredSize(new Dimension(44, 22));
			ivjexpbnUp.setSize(44, 22);
		}
		return ivjexpbnUp;
	}

	/**
	 * This method initializes ivjbnDown
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getIvjbnDown() {
		if (ivjbnDown == null) {
			ivjbnDown = new UIButton();
			ivjbnDown.setBounds(new Rectangle(449, 150, 44, 22));
			ivjbnDown.setName("bnDown");
			ivjbnDown.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_DOWN));
			ivjbnDown.setMargin(new Insets(2, 0, 2, 0));
			ivjbnDown.setText("");
			ivjbnDown.setFont(new Font("dialog", 0, 12));
			ivjbnDown.setPreferredSize(new Dimension(44, 22));
			ivjbnDown.setSize(44, 22);
		}
		return ivjbnDown;
	}

	
	private UIButton getIvjExpbnDown() {
		if (ivjexpbnDown == null) {
			ivjexpbnDown = new UIButton();
			ivjexpbnDown.setBounds(new Rectangle(449, 395, 44, 22));
			ivjexpbnDown.setName("expbnDown");
			ivjexpbnDown.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_DOWN));
			ivjexpbnDown.setMargin(new Insets(2, 0, 2, 0));
			ivjexpbnDown.setText("");
			ivjexpbnDown.setFont(new Font("dialog", 0, 12));
			ivjexpbnDown.setPreferredSize(new Dimension(44, 22));
			ivjexpbnDown.setSize(44, 22);
		}
		return ivjexpbnDown;
	}

	
	/**
	 * This method initializes ivjbnBottom
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getIvjbnBottom() {
		if (ivjbnBottom == null) {
			ivjbnBottom = new UIButton();
			ivjbnBottom.setBounds(new Rectangle(449, 189, 44, 22));
			ivjbnBottom.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_BOTTOM));
			ivjbnBottom.setText("");
			ivjbnBottom.setName("bnBottom");
			ivjbnBottom.setPreferredSize(new Dimension(44, 22));
			ivjbnBottom.setSize(44, 22);
		}
		return ivjbnBottom;
	}
	
	private UIButton getIvjExpbnBottom() {
		if (ivjexpbnBottom == null) {
			ivjexpbnBottom = new UIButton();
			ivjexpbnBottom.setBounds(new Rectangle(449, 435, 44, 22));
			ivjexpbnBottom.setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_BOTTOM));
			ivjexpbnBottom.setText("");
			ivjexpbnBottom.setName("expbnBottom");
			ivjexpbnBottom.setPreferredSize(new Dimension(44, 22));
			ivjexpbnBottom.setSize(44, 22);
		}
		return ivjexpbnBottom;
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this.getlstTolst().getLstRight()) {
			setSuqenBtnEnabled();
		}else if(e.getSource() == this.getexplstTolst().getLstRight()){
			setExpSuqenBtnEnabled();
		}
	}

	public int getType() {
		return type;
	}


	public WaGlobalVO[] getWaclassVOs() {
		return list;
	}

}
