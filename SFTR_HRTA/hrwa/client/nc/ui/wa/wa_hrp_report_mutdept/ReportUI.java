package nc.ui.wa.wa_hrp_report_mutdept;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IClassitem;
import nc.itf.hr.wa.IItem;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.vo.pub.btn.ExcelOutBtnVO;
import nc.vo.trade.button.ButtonVO;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_024.ItemVO;

/**
 * @author szh
 *
 */
public class ReportUI extends BillCardUI {

	/**
	 * 
	 */
	public ReportUI() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ReportUI(String pk_corp, String pk_billType, String pk_busitype,
			String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.card.BillCardUI#createController()
	 */
	@Override
	protected ICardController createController() {
		// TODO Auto-generated method stub
		return new ReportCtrl();
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#getRefBillType()
	 */
	@Override
	public String getRefBillType() {
		// TODO Auto-generated method stub
		return null;
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

	}
	
    @Override
    protected void initPrivateButton() {
    	// TODO Auto-generated method stub
    	ButtonVO outbtn = new ExcelOutBtnVO().getButtonVO();
		addPrivateButton(outbtn);
    }
    @Override
    protected CardEventHandler createEventHandler() {
    	// TODO Auto-generated method stub
    	return new ReportHandler(this,this.createController());
    }
}
