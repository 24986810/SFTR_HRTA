/**
 * 
 */
package nc.ui.tam.tongren023;

import java.awt.Event;

import nc.ui.pub.FramePanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.manage.ManageEventHandler;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.tam.tongren010.GxHVO;
import nc.vo.trade.button.ButtonVO;

/**
 * 学术假维护
 * @author 28729
 *
 */
public class ClientUI extends BillManageUI {

	/**
	 * 
	 */
	public ClientUI() {
		// TODO Auto-generated constructor stub
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(false);
		updateButtons();
	}

	/**
	 * @param fp
	 */
	public ClientUI(FramePanel fp) {
		super(fp);
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(false);
		updateButtons();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param useBillSource
	 */
	public ClientUI(Boolean useBillSource) {
		super(useBillSource);
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(false);
		updateButtons();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientUI(String pk_corp, String pk_billType, String pk_busitype,
			String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(false);
		updateButtons();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#createController()
	 */
	@Override
	protected AbstractManageController createController() {
		// TODO Auto-generated method stub
		return new ClientCtrl();
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#setBodySpecialData(nc.vo.pub.CircularlyAccessibleValueObject[])
	 */
	@Override
	public void setBodySpecialData(CircularlyAccessibleValueObject[] vos)
			throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#setHeadSpecialData(nc.vo.pub.CircularlyAccessibleValueObject, int)
	 */
	@Override
	protected void setHeadSpecialData(CircularlyAccessibleValueObject vo,
			int intRow) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#setTotalHeadSpecialData(nc.vo.pub.CircularlyAccessibleValueObject[])
	 */
	@Override
	protected void setTotalHeadSpecialData(CircularlyAccessibleValueObject[] vos)
			throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#initSelfData()
	 */
	@Override
	protected void initSelfData() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#setDefaultData()
	 */
	@Override
	public void setDefaultData() throws Exception {
		// TODO Auto-generated method stub
       getBillCardPanel().getHeadItem("vyear").setValue(_getDate().toString().substring(0,4));
       getBillCardPanel().getHeadItem("pk_corp").setValue(_getCorp().getPrimaryKey());
	} 
	@Override
	protected ManageEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new EventHandler(this,this.createController());
	}

	@Override
	public void afterUpdate() {
		// TODO Auto-generated method stub
		if(getBufferData()!=null&&getBufferData().getCurrentVO()!=null){
			GxHVO hvo = (GxHVO)getBufferData().getCurrentVO().getParentVO();
			if(hvo.getVyear().equals(_getDate().toString().substring(0,4)) || hvo.getVyear().equals(_getDate().getDateAfter(365).toString().substring(0,4))){
				getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(true);
			}else{
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			}
		}
	}

	@Override
	protected void initPrivateButton() {
		super.initPrivateButton();
		addPrivateButton(createWhButton());
	}

	private ButtonVO createWhButton() {
		
		ButtonVO btn = new ButtonVO();
		btn.setBtnNo(1111);
		btn.setBtnName("维护");
		btn.setHintStr("维护");
		btn.setBtnCode("维护");
		btn.setBtnChinaName("维护");
		btn.setOperateStatus(new int[] { IBillOperate.OP_NOTEDIT });
		btn.setHotKey("W");
		btn.setDisplayHotKey("(Ctrl+Alt+W)");
		btn.setModifiers(Event.CTRL_MASK + Event.ALT_MASK);
		return btn;
	}

	@Override
	public void afterEdit(BillEditEvent arg0) {
		// TODO Auto-generated method stub
		super.afterEdit(arg0);
		
		
		
	}
	
	
}
