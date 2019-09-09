package nc.ui.wa.wa_031;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;

public class ParamDialog extends UIDialog implements ActionListener,BillEditListener {
	
	private UISplitPane mainPanel; // 主面板
	private BillCardPanel cardPanel;
	private UIPanel btnPanel; // 放按钮
	private UIButton saveCardBtn; // 保存
	private BillItem[] headItems;
	
	private HashMap<String,String> paramMap;

	public ParamDialog(java.awt.Container parent, String title, HashMap<String,String> paramMap) {
        super(parent, title);
        this.paramMap = paramMap;
        initialize();
    }
	
	private void initialize() {
		setName("打印参数设置");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setContentPane(getMainPanel());
        addListenerEvent();
    }

	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new UISplitPane();
			mainPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
			mainPanel.setTopComponent(getCardPanel());
			mainPanel.setBottomComponent(getBtnPanel());
			mainPanel.setDividerLocation(210);
			mainPanel.setOneTouchExpandable(true);
			mainPanel.setDividerSize(8);
		}
		return mainPanel;
	}

	private void addListenerEvent() {
		getSaveCardBtn().addActionListener(this);
		getCardPanel().addEditListener(this);
	}

	public BillCardPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new BillCardPanel();
			cardPanel.setName("cardPanel");
			BillData data = new BillData();
			data.setHeadItems(getHeadItems());
			cardPanel.setBillData(data);
			cardPanel.setBodyMenuShow(false);
			//cardPanel.setEnabled(false);
			cardPanel.addLine();
		}
		return cardPanel;
	}

	public void setCardPanel(BillCardPanel cardPanel) {
		this.cardPanel = cardPanel;
	}

	public BillItem[] getHeadItems() {
		if (headItems == null) {
			headItems = new BillItem[8];
			headItems[0] = new BillItem();
			headItems[0].setName("填表日期");
			headItems[0].setKey("tbrq");
			headItems[0].setShow(true);
			headItems[0].setRefType("");
			headItems[0].setEdit(true);
			headItems[0].setLoadFormula(null);
			headItems[0].setDataType(BillItem.DATE);
			headItems[0].setWidth(159);
			
			headItems[1] = new BillItem();
			headItems[1].setName("单位部门盖章日期");
			headItems[1].setKey("dwbmgzrq");
			headItems[1].setShow(true);
			headItems[1].setRefType("");
			headItems[1].setEdit(true);
			headItems[1].setLoadFormula(null);
			headItems[1].setDataType(BillItem.DATE);
			headItems[1].setWidth(159);
			
			headItems[2] = new BillItem();
			headItems[2].setName("批准单位盖章日期");
			headItems[2].setKey("pzdwgzrq");
			headItems[2].setShow(true);
			headItems[2].setRefType("");
			headItems[2].setEdit(true);
			headItems[2].setLoadFormula(null);
			headItems[2].setDataType(BillItem.DATE);
			headItems[2].setWidth(159);
			
			headItems[3] = new BillItem();
			headItems[3].setName("执行日期");
			headItems[3].setKey("zxrq");
			headItems[3].setShow(true);
			headItems[3].setRefType("");
			headItems[3].setEdit(true);
			headItems[3].setLoadFormula(null);
			headItems[3].setDataType(BillItem.DATE);
			headItems[3].setWidth(159);
			
			headItems[4] = new BillItem();
			headItems[4].setName("起聘或进编日期");
			headItems[4].setKey("qpjbrq");
			headItems[4].setShow(true);
			headItems[4].setRefType("");
			headItems[4].setEdit(true);
			headItems[4].setLoadFormula(null);
			headItems[4].setDataType(BillItem.DATE);
			headItems[4].setWidth(159);
			
			headItems[5] = new BillItem();
			headItems[5].setName("单位部门意见自定义头");
			headItems[5].setKey("zdyt");
			headItems[5].setShow(true);
			headItems[5].setRefType("");
			headItems[5].setEdit(true);
			headItems[5].setLoadFormula(null);
			headItems[5].setWidth(159);
			
			headItems[6] = new BillItem();
			headItems[6].setName("备注");
			headItems[6].setKey("bz");
			headItems[6].setShow(true);
			headItems[6].setRefType("");
			headItems[6].setEdit(true);
			headItems[6].setLoadFormula(null);
			headItems[6].setWidth(159);
			
			headItems[7] = new BillItem();
			headItems[7].setName("自定义99号文");
			headItems[7].setKey("dwbmyj");
			headItems[7].setShow(true);
			headItems[7].setRefType("");
			headItems[7].setEdit(true);
			headItems[7].setLoadFormula(null);
			headItems[7].setWidth(159);
		}
		return headItems;
	}

	public UIPanel getBtnPanel() {
		if (btnPanel == null) {
			btnPanel = new UIPanel();
			btnPanel.setName("btnPanel");
			btnPanel.add(getSaveCardBtn());
		}
		return btnPanel;
	}

	private UIButton getSaveCardBtn() {
		if (saveCardBtn == null) {
			saveCardBtn = new UIButton();
			saveCardBtn.setBounds(520, 210, 60, 20);
			saveCardBtn.setName("saveCardBtn");
			saveCardBtn.setText("确认");
		}
		return saveCardBtn;
	}

	public void setBtnPanel(UIPanel btnPanel) {
		this.btnPanel = btnPanel;
	}

	public void afterEdit(BillEditEvent var1) {
		// TODO Auto-generated method stub
		
	}

	public void bodyRowChange(BillEditEvent var1) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getSaveCardBtn()) {
			// 填报日期
			String tbrq = getCardPanel().getBillData().getHeadItem("tbrq").getValue();
			paramMap.put("tbrq", tbrq);
			// 单位部门盖章日期
			String dwbmgzrq = getCardPanel().getBillData().getHeadItem("dwbmgzrq").getValue();
			paramMap.put("dwbmgzrq", dwbmgzrq);
			// 批准单位盖章日期
			String pzdwgzrq = getCardPanel().getBillData().getHeadItem("pzdwgzrq").getValue();
			paramMap.put("pzdwgzrq", pzdwgzrq);
			// 执行日期
			String zxrq = getCardPanel().getBillData().getHeadItem("zxrq").getValue();
			paramMap.put("zxrq", zxrq);	
			// 起聘或进编日期
			String qpjbrq = getCardPanel().getBillData().getHeadItem("qpjbrq").getValue();
			paramMap.put("qpjbrq", qpjbrq);
			// 单位部门意见自定义头
			String zdyt = getCardPanel().getBillData().getHeadItem("zdyt").getValue();
			paramMap.put("zdyt", zdyt);
			// 备注
			String bz = getCardPanel().getBillData().getHeadItem("bz").getValue();
			paramMap.put("bz", bz);
			
			String dwbmyj = getCardPanel().getBillData().getHeadItem("dwbmyj").getValue();
			paramMap.put("dwbmyj", dwbmyj);
			this.closeOK();
		}
	}

	@Override
	protected void close() {
		// TODO Auto-generated method stub
		 if (!isShowing())
	            return;
	        this.setVisible(false);
	        destroy();
	}

	@Override
	public void closeOK() {
		// TODO Auto-generated method stub
		super.closeOK();
	}

}